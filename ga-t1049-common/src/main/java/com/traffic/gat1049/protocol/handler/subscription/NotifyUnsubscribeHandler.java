package com.traffic.gat1049.protocol.handler.subscription;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.application.session.SessionManager;

/**
 * Notify取消订阅处理器
 * 专门处理msgType为PUSH、operName为Notify的取消订阅请求
 */
public class NotifyUnsubscribeHandler extends AbstractProtocolHandler {

    private final SubscriptionManager subscriptionManager;
    private final SessionManager sessionManager;

    public NotifyUnsubscribeHandler(SubscriptionManager subscriptionManager, SessionManager sessionManager) {
        this.subscriptionManager = subscriptionManager;
        this.sessionManager = sessionManager;
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
    protected Message doHandle(Message message) throws GatProtocolException {
        try {
            // 验证token
            String token = message.getToken();
            if (token == null || !sessionManager.validateToken(token)) {
                return createErrorResponse(message,
                        GatConstants.ErrorCode.SDE_TOKEN,
                        "无效令牌或会话已过期");
            }

            // 获取取消订阅数据
            SdoMsgEntity subscription = (SdoMsgEntity) ProtocolUtils.getOperationData(message);

            // 验证取消订阅参数
            validateUnsubscription(subscription);

            // 执行取消订阅
            boolean success = subscriptionManager.unsubscribe(token, subscription);

            if (success) {
                logger.info("成功取消Notify订阅: token={}, objName={}", token, subscription.getObjName());
                return createSuccessResponse(message, subscription);
            } else {
                logger.warn("取消订阅失败（可能不存在）: token={}, subscription={}", token, subscription);
                // 即使订阅不存在，也返回成功，这是幂等操作
                return createSuccessResponse(message, subscription);
            }

        } catch (ValidationException e) {
            logger.error("取消订阅参数验证失败: {}", e.getMessage());
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_OPER_NAME,
                    e.getMessage());
        } catch (Exception e) {
            logger.error("处理Notify取消订阅请求失败", e);
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_FAILURE,
                    "取消订阅处理失败: " + e.getMessage());
        }
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
            if (!NotifySubscribeHandler.getSupportedObjectNames().contains(subscription.getObjName())
                    && !"*".equals(subscription.getObjName())) {
                logger.warn("尝试取消订阅不支持的对象: {}", subscription.getObjName());
                // 对于取消订阅，我们允许取消任何对象名称，即使它不在支持列表中
            }
        }
    }

    /**
     * 创建取消订阅成功响应
     */
    private Message createSuccessResponse(Message request, SdoMsgEntity subscription) {
        return super.createSuccessResponse(request, subscription);
    }
    @Override
    public String getHandlerName() {
        return "NotifyUnsubscribeHandler";
    }
}