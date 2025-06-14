package com.traffic.gat1049.protocol.handler.main.common;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.protocol.model.sdo.SdoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutHandler extends AbstractProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogoutHandler.class);
    private final SessionManager sessionManager;

    public LogoutHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Message doHandle(Message message)
            throws GatProtocolException {
        String objName = GatConstants.Operation.LOGOUT;
        String token = message.getToken();
        logger.info("处理Logout请求: token={}", token != null ? token.substring(0, 8) + "..." : "null");

        if (token == null || token.trim().isEmpty()) {
            return createErrorResponse(message, "MISSING_TOKEN", "访问令牌不能为空", objName);
        }

        // 获取会话信息（用于日志）
        SessionManager.SessionInfo sessionInfo = sessionManager.getSession(token);
        String userName = sessionInfo != null ? sessionInfo.getUserName() : "unknown";

        // 执行登出
        boolean success = sessionManager.logout(token);

        if (success) {
            logger.info("用户登出成功: {}", userName);
            return createSuccessResponse(message, new SdoUser(userName, ""));
        } else {
            logger.warn("用户登出失败，token可能已失效: {}", userName);
            return createErrorResponse(message, "LOGOUT_FAILED", "登出失败，令牌可能已失效", objName);
        }
    }

    @Override
    public boolean supports(Message message) {
        return com.traffic.gat1049.protocol.util.ProtocolUtils.isRequest(message) &&
                "Logout".equals(com.traffic.gat1049.protocol.util.ProtocolUtils.getOperationName(message));
    }

    @Override
    public String getHandlerName() {
        return "LogoutHandler";
    }
}
