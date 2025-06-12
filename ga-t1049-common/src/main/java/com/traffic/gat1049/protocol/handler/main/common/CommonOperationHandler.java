package com.traffic.gat1049.protocol.handler.main.common;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.model.enums.OperationName;
import com.traffic.gat1049.model.enums.SystemErrorType;
import com.traffic.gat1049.model.enums.SystemType;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.*;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用操作处理器
 * 处理登录、登出、订阅、心跳等通用操作
 */
public class CommonOperationHandler extends AbstractProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommonOperationHandler.class);

    private final SessionManager sessionManager;
    private final SubscriptionManager subscriptionManager;

    public CommonOperationHandler(SessionManager sessionManager, SubscriptionManager subscriptionManager) {
        this.sessionManager = sessionManager;
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        String operationName = ProtocolUtils.getOperationName(message);

        switch (operationName) {
            case "Login":
                return handleLogin(message);
            case "Logout":
                return handleLogout(message);
            case "Subscribe":
                return handleSubscribe(message);
            case "Unsubscribe":
                return handleUnsubscribe(message);
            case "Notify":
                return handleNotify(message);
            case "Get":
                return handleGet(message);
            case "Set":
                return handleSet(message);
            default:
                throw new GatProtocolException("UNSUPPORTED_OPERATION", "不支持的操作: " + operationName);
        }
    }

    @Override
    public boolean supports(Message message) {
        if (!ProtocolUtils.isRequest(message)) {
            return false;
        }

        String operationName = ProtocolUtils.getOperationName(message);
        return OperationName.LOGIN.getCode().equals(operationName) ||
                OperationName.LOGOUT.getCode().equals(operationName) ||
                OperationName.SUBSCRIBE.getCode().equals(operationName) ||
                OperationName.UNSUBSCRIBE.getCode().equals(operationName) ||
                OperationName.NOTIFY.getCode().equals(operationName) ||
                (OperationName.GET.getCode().equals(operationName) && isCommonGetOperation(message)) ||
                (OperationName.SET.getCode().equals(operationName) && isCommonSetOperation(message));
    }

    @Override
    public String getHandlerName() {
        return "CommonOperationHandler";
    }

    /**
     * 处理登录操作
     */
    private Message handleLogin(Message request) {
        try {
            Object data = ProtocolUtils.getOperationData(request);
            if (!(data instanceof SdoUser)) {
                return createErrorResponse(request, SystemErrorType.SDE_OPER_NAME.getCode(), "登录数据格式错误");
            }

            SdoUser user = (SdoUser) data;
            String clientAddress = getClientAddress(request);
            SystemType systemType = getSystemType(request);

            SessionManager.LoginResult result = sessionManager.login(clientAddress, user, systemType);

            if (result.isSuccess()) {
                // 登录成功，返回包含token的响应
                SdoUser responseUser = new SdoUser(user.getUserName(), ""); // 密码不返回
                Message response = createSuccessResponse(request, responseUser);
                response.setToken(result.getToken());
                return response;
            } else {
                return createErrorResponse(request, SystemErrorType.SDE_USER_NAME.getCode(), result.getErrorMessage());
            }

        } catch (Exception e) {
            logger.error("处理登录操作失败", e);
            return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "登录操作失败");
        }
    }

    /**
     * 处理登出操作
     */
    private Message handleLogout(Message request) {
        try {
            String token = request.getToken();
            if (token == null || !sessionManager.validateToken(token)) {
                return createErrorResponse(request, SystemErrorType.SDE_TOKEN.getCode(), "无效令牌");
            }

            boolean success = sessionManager.logout(token);
            if (success) {
                // 清理订阅
                subscriptionManager.clearSubscriptions(token);

                Object data = ProtocolUtils.getOperationData(request);
                return createSuccessResponse(request, data);
            } else {
                return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "登出失败");
            }

        } catch (Exception e) {
            logger.error("处理登出操作失败", e);
            return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "登出操作失败");
        }
    }

    /**
     * 处理订阅操作
     */
    private Message handleSubscribe(Message request) {
        try {
            String token = request.getToken();
            if (token == null || !sessionManager.validateToken(token)) {
                return createErrorResponse(request, SystemErrorType.SDE_TOKEN.getCode(), "无效令牌");
            }

            Object data = ProtocolUtils.getOperationData(request);
            if (!(data instanceof SdoMsgEntity)) {
                return createErrorResponse(request, SystemErrorType.SDE_OPER_NAME.getCode(), "订阅数据格式错误");
            }

            SdoMsgEntity subscription = (SdoMsgEntity) data;
            boolean success = subscriptionManager.subscribe(token, subscription);

            if (success) {
                return createSuccessResponse(request, subscription);
            } else {
                return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "订阅失败");
            }

        } catch (Exception e) {
            logger.error("处理订阅操作失败", e);
            return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "订阅操作失败");
        }
    }

    /**
     * 处理取消订阅操作
     */
    private Message handleUnsubscribe(Message request) {
        try {
            String token = request.getToken();
            if (token == null || !sessionManager.validateToken(token)) {
                return createErrorResponse(request, SystemErrorType.SDE_TOKEN.getCode(), "无效令牌");
            }

            Object data = ProtocolUtils.getOperationData(request);
            if (!(data instanceof SdoMsgEntity)) {
                return createErrorResponse(request, SystemErrorType.SDE_OPER_NAME.getCode(), "取消订阅数据格式错误");
            }

            SdoMsgEntity subscription = (SdoMsgEntity) data;
            boolean success = subscriptionManager.unsubscribe(token, subscription);

            if (success) {
                return createSuccessResponse(request, subscription);
            } else {
                return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "取消订阅失败");
            }

        } catch (Exception e) {
            logger.error("处理取消订阅操作失败", e);
            return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "取消订阅操作失败");
        }
    }

    /**
     * 处理通知操作（主要是心跳）
     */
    private Message handleNotify(Message request) {
        try {
            String token = request.getToken();
            if (token != null && sessionManager.validateToken(token)) {
                sessionManager.heartbeat(token);
            }

            Object data = ProtocolUtils.getOperationData(request);
            if (data instanceof SdoHeartBeat) {
                logger.debug("收到心跳消息");
            }

            // 通知类型消息通常不需要响应
            return null;

        } catch (Exception e) {
            logger.error("处理通知操作失败", e);
            return null;
        }
    }

    /**
     * 处理查询操作（超时设置、对时服务器等）
     */
    private Message handleGet(Message request) {
        try {
            String token = request.getToken();
            if (token == null || !sessionManager.validateToken(token)) {
                return createErrorResponse(request, SystemErrorType.SDE_TOKEN.getCode(), "无效令牌");
            }

            Object data = ProtocolUtils.getOperationData(request);

            if (data instanceof SdoTimeServer) {
                // 查询对时服务器
                SdoTimeServer timeServer = new SdoTimeServer("ntp.ntsc.ac.cn", "NTP", 123);
                return createSuccessResponse(request, timeServer);
            }

            return createErrorResponse(request, SystemErrorType.SDE_OPER_NAME.getCode(), "不支持的查询操作");

        } catch (Exception e) {
            logger.error("处理查询操作失败", e);
            return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "查询操作失败");
        }
    }

    /**
     * 处理设置操作（超时设置等）
     */
    private Message handleSet(Message request) {
        try {
            String token = request.getToken();
            if (token == null || !sessionManager.validateToken(token)) {
                return createErrorResponse(request, SystemErrorType.SDE_TOKEN.getCode(), "无效令牌");
            }

            Object data = ProtocolUtils.getOperationData(request);

            if (data instanceof SdoTimeOut) {
                // 设置超时时间
                SdoTimeOut timeout = (SdoTimeOut) data;
                logger.info("设置超时时间: {} 秒", timeout.getTimeoutSeconds());
                return createSuccessResponse(request, timeout);
            }

            return createErrorResponse(request, SystemErrorType.SDE_OPER_NAME.getCode(), "不支持的设置操作");

        } catch (Exception e) {
            logger.error("处理设置操作失败", e);
            return createErrorResponse(request, SystemErrorType.SDE_FAILURE.getCode(), "设置操作失败");
        }
    }

    private String getClientAddress(Message message) {
        if (message.getFrom() != null) {
            return message.getFrom().getSys() + ":" + message.getFrom().getInstance();
        }
        return "unknown";
    }

    private SystemType getSystemType(Message message) {
        if (message.getFrom() != null && message.getFrom().getSys() != null) {
            try {
                return SystemType.fromCode(message.getFrom().getSys());
            } catch (Exception e) {
                return SystemType.UTCS; // 默认为交通信号控制系统
            }
        }
        return SystemType.UTCS;
    }

    private boolean isCommonGetOperation(Message message) {
        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof SdoTimeServer;
    }

    private boolean isCommonSetOperation(Message message) {
        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof SdoTimeOut;
    }

    protected Message createErrorResponse(Message request, String errorCode, String errorMessage) {
        SdoError error = new SdoError("", errorCode, errorMessage);
        return MessageBuilder.create()
                .error()
                .fromTicp()
                .toUtcs()
                .seq(request.getSeq())
                .token(request.getToken())
                .operation("Error", error)
                .build();
    }
}
