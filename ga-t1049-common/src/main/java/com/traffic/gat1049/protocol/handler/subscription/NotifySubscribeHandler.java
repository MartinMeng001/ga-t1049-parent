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

import java.util.Arrays;
import java.util.List;

/**
 * 改进的Notify订阅处理器 - 使用接口委托模式
 * 处理器负责消息路由和基础验证，具体订阅逻辑委托给SubscriptionService实现
 */
public class NotifySubscribeHandler extends TokenRequiredHandler {

    // 可插拔的订阅服务
    private SubscriptionService subscriptionService;

    // 默认支持的对象名称（保持向后兼容）
    private static final List<String> DEFAULT_SUPPORTED_OBJECTS = Arrays.asList(
            GatConstants.ObjectName.CROSS_CYCLE,
            GatConstants.ObjectName.CROSS_MODE_PLAN,
            GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR,
            GatConstants.ObjectName.CROSS_STATE,
            GatConstants.ObjectName.SYS_STATE,
            GatConstants.ObjectName.CROSS_STAGE,
            GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS,
            GatConstants.ObjectName.CROSS_TRAFFIC_DATA,
            GatConstants.ObjectName.STAGE_TRAFFIC_DATA,
            GatConstants.ObjectName.VAR_LANE_STATUS,
            GatConstants.ObjectName.ROUTE_CONTROL_MODE,
            GatConstants.ObjectName.ROUTE_SPEED
    );

    public NotifySubscribeHandler(SessionManager sessionManager) {
        super(sessionManager);
        // 可以设置默认的订阅服务，或者为null等待注入
        this.subscriptionService = null;
    }

    /**
     * 设置订阅服务实现（由Spring或工程代码注入）
     */
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
        logger.info("订阅服务已注入: {}", subscriptionService.getClass().getSimpleName());
    }

    @Override
    public boolean supports(Message message) {
        // 只处理订阅请求
        if (!ProtocolUtils.isRequest(message)) {
            return false;
        }

        String operationName = ProtocolUtils.getOperationName(message);
        if (!GatConstants.Operation.SUBSCRIBE.equals(operationName)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        if (!(data instanceof SdoMsgEntity)) {
            return false;
        }

        SdoMsgEntity subscription = (SdoMsgEntity) data;

        // 检查是否是PUSH/Notify类型的订阅
        return GatConstants.MessageType.PUSH.equals(subscription.getMsgType()) &&
                GatConstants.Operation.NOTIFY.equals(subscription.getOperName()) &&
                isSupportedObjectName(subscription.getObjName());
    }

    @Override
    protected Message doHandleWithSession(Message message, SessionManager.SessionInfo sessionInfo) {
        String objName = "Unknown";
        try {
            if (!supports(message)) {
                logger.info("{}消息无需处理", message.getType());
                return null;
            }

            // 获取订阅数据
            SdoMsgEntity subscription = (SdoMsgEntity) ProtocolUtils.getOperationData(message);
            String token = message.getToken();
            objName = subscription.getObjName();

            // 基础验证
            validateSubscription(subscription);

            // 委托给订阅服务处理
            SubscriptionResult result = delegateToSubscriptionService(token, subscription, message);

            if (result.isSuccess()) {
                logger.info("成功添加Notify订阅: token={}, objName={}", token, objName);
                Object responseData = result.getResponseData() != null ? result.getResponseData() : subscription;
                return createSuccessResponse(message, responseData);
            } else {
                logger.warn("订阅失败: token={}, objName={}, error={}", token, objName, result.getErrorMessage());
                return createErrorResponse(message,
                        result.getErrorCode() != null ? result.getErrorCode() : GatConstants.ErrorCode.SDE_FAILURE,
                        result.getErrorMessage(), objName);
            }

        } catch (ValidationException e) {
            logger.error("订阅参数验证失败: {}", e.getMessage());
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_OPER_NAME,
                    e.getMessage(), objName);
        } catch (Exception e) {
            logger.error("处理Notify订阅请求失败", e);
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_FAILURE,
                    "订阅处理失败: " + e.getMessage(), objName);
        }
    }

    /**
     * 委托给订阅服务处理
     */
    private SubscriptionResult delegateToSubscriptionService(String token, SdoMsgEntity subscription, Message message) {
        if (subscriptionService != null) {
            // 使用注入的订阅服务
            return subscriptionService.handleSubscribe(token, subscription, message);
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
        // 这里可以保留原有的简单逻辑，或者返回未实现错误
        logger.info("使用默认订阅处理逻辑: token={}, objName={}", token, subscription.getObjName());
        return SubscriptionResult.success();
    }

    /**
     * 检查是否是支持的对象名称
     */
    private boolean isSupportedObjectName(String objName) {
        if (objName == null) {
            return false;
        }

        // 通配符总是支持
        if ("*".equals(objName)) {
            return true;
        }

        // 如果有注入的订阅服务，使用其支持列表
        if (subscriptionService != null) {
            return subscriptionService.supportsObject(objName);
        }

        // 否则使用默认支持列表
        return DEFAULT_SUPPORTED_OBJECTS.contains(objName);
    }

    /**
     * 验证订阅参数
     */
    private void validateSubscription(SdoMsgEntity subscription) throws ValidationException {
        if (subscription == null) {
            throw new ValidationException("subscription", "订阅对象不能为空");
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

        // 验证对象名称
        if (subscription.getObjName() == null || subscription.getObjName().trim().isEmpty()) {
            throw new ValidationException("objName", "对象名称不能为空");
        }

        if (!isSupportedObjectName(subscription.getObjName())) {
            List<String> supportedObjects = subscriptionService != null ?
                    subscriptionService.getSupportedObjects() : DEFAULT_SUPPORTED_OBJECTS;
            throw new ValidationException("objName",
                    "不支持的对象名称: " + subscription.getObjName() +
                            "。支持的对象名称: " + supportedObjects);
        }
    }

    @Override
    public String getHandlerName() {
        return "NotifySubscribeHandler";
    }

    /**
     * 获取支持的对象名称列表（静态方法保持向后兼容）
     */
    public static List<String> getSupportedObjectNames() {
        return DEFAULT_SUPPORTED_OBJECTS;
    }
}