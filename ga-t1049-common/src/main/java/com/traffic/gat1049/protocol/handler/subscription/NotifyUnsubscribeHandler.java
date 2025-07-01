package com.traffic.gat1049.protocol.handler.subscription;

import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.handler.base.TokenRequiredHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.application.subscription.interfaces.SubscriptionService;
import com.traffic.gat1049.application.subscription.SubscriptionResult;
import com.traffic.gat1049.application.session.SessionManager;

/**
 * 改进的Notify取消订阅处理器 - 使用接口委托模式
 * 处理器负责消息路由和基础验证，具体取消订阅逻辑委托给SubscriptionService实现
 */
public class NotifyUnsubscribeHandler extends TokenRequiredHandler {

    // 可插拔的订阅服务
    private SubscriptionService subscriptionService;

    public NotifyUnsubscribeHandler(SessionManager sessionManager) {
        super(sessionManager);
        this.subscriptionService = null;
    }

    /**
     * 设置订阅服务实现（由Spring或工程代码注入）
     */
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
        logger.info("取消订阅服务已注入: {}", subscriptionService.getClass().getSimpleName());
    }

    @Override
    public boolean supports(Message message) {
        // 只处理取消订阅请求
        if (!ProtocolUtils.isRequest(message)) {
            return false;
        }

        String operationName = ProtocolUtils.getOperationName(message);
        if (!GatConstants.Operation.UNSUBSCRIBE.equals(operationName)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        if (!(data instanceof SdoMsgEntity)) {
            return false;
        }

        SdoMsgEntity subscription = (SdoMsgEntity) data;

        // 检查是否是PUSH/Notify类型的取消订阅
        return GatConstants.MessageType.PUSH.equals(subscription.getMsgType()) &&
                GatConstants.Operation.NOTIFY.equals(subscription.getOperName());
    }

    @Override
    protected Message doHandleWithSession(Message message, SessionManager.SessionInfo sessionInfo) {
        String objName = "Unknown";
        try {
            if (!supports(message)) {
                logger.info("{}消息无需处理", message.getType());
                return null;
            }

            // 获取取消订阅数据
            SdoMsgEntity subscription = (SdoMsgEntity) ProtocolUtils.getOperationData(message);
            String token = message.getToken();
            objName = subscription.getObjName();

            // 基础验证
            validateUnsubscription(subscription);

            // 委托给订阅服务处理
            SubscriptionResult result = delegateToSubscriptionService(token, subscription, message);

            if (result.isSuccess()) {
                logger.info("成功取消Notify订阅: token={}, objName={}", token, objName);
                Object responseData = result.getResponseData() != null ? result.getResponseData() : subscription;
                return createSuccessResponse(message, responseData);
            } else {
                logger.warn("取消订阅失败: token={}, objName={}, error={}", token, objName, result.getErrorMessage());
                return createErrorResponse(message,
                        result.getErrorCode() != null ? result.getErrorCode() : GatConstants.ErrorCode.SDE_FAILURE,
                        result.getErrorMessage(), objName);
            }

        } catch (ValidationException e) {
            logger.error("取消订阅参数验证失败: {}", e.getMessage());
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_OPER_NAME,
                    e.getMessage(), objName);
        } catch (Exception e) {
            logger.error("处理Notify取消订阅请求失败", e);
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_FAILURE,
                    "取消订阅处理失败: " + e.getMessage(), objName);
        }
    }

    /**
     * 委托给订阅服务处理
     */
    private SubscriptionResult delegateToSubscriptionService(String token, SdoMsgEntity subscription, Message message) {
        if (subscriptionService != null) {
            // 使用注入的订阅服务
            return subscriptionService.handleUnsubscribe(token, subscription, message);
        } else {
            // 使用默认处理逻辑（保持向后兼容）
            logger.warn("未注入订阅服务，使用默认处理逻辑");
            return handleWithDefaultLogic(token, subscription);
        }
    }

    /**
     * 默认处理逻辑（保持向后兼容）
     */
    private SubscriptionResult handleWithDefaultLogic(String token, SdoMsgEntity subscription) {
        // 保留原有的简单逻辑，或者返回未实现错误
        logger.info("使用默认取消订阅处理逻辑: token={}, objName={}", token, subscription.getObjName());
        return SubscriptionResult.success();
    }

    /**
     * 验证取消订阅参数
     */
    private void validateUnsubscription(SdoMsgEntity subscription) throws ValidationException {
        if (subscription == null) {
            throw new ValidationException("subscription", "取消订阅对象不能为空");
        }

        // 验证消息类型必须是PUSH
        if (!GatConstants.MessageType.PUSH.equals(subscription.getMsgType())) {
            throw new ValidationException("msgType",
                    "消息类型必须为PUSH，当前值: " + subscription.getMsgType());
        }

        // 验证操作名称必须是Notify
        if (!GatConstants.Operation.NOTIFY.equals(subscription.getOperName())) {
            throw new ValidationException("operName",
                    "操作名称必须为Notify，当前值: " + subscription.getOperName());
        }

        // 对象名称可以为空（表示取消所有相关订阅）或通配符
        if (subscription.getObjName() != null && !subscription.getObjName().trim().isEmpty()) {
            // 如果指定了对象名称，验证是否为支持的名称
            boolean isSupported = "*".equals(subscription.getObjName()) ||
                    (subscriptionService != null && subscriptionService.supportsObject(subscription.getObjName()));

            if (!isSupported) {
                logger.warn("尝试取消订阅不支持的对象: {}", subscription.getObjName());
                // 对于取消订阅，我们允许取消任何对象名称，即使它不在支持列表中
            }
        }
    }

    @Override
    public String getHandlerName() {
        return "NotifyUnsubscribeHandler";
    }
}