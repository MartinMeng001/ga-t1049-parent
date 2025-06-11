package com.traffic.gat1049.handler.common;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.model.dto.response.LogoutResponse;
import com.traffic.gat1049.protocol.handler.AbstractProtocolHandler;
import com.traffic.gat1049.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutHandler extends AbstractProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogoutHandler.class);
    private final SessionManager sessionManager;

    public LogoutHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected com.traffic.gat1049.protocol.model.Message doHandle(com.traffic.gat1049.protocol.model.Message message)
            throws GatProtocolException {

        String token = message.getToken();
        logger.info("处理Logout请求: token={}", token != null ? token.substring(0, 8) + "..." : "null");

        if (token == null || token.trim().isEmpty()) {
            return createErrorResponse(message, "MISSING_TOKEN", "访问令牌不能为空");
        }

        // 获取会话信息（用于日志）
        SessionManager.SessionInfo sessionInfo = sessionManager.getSession(token);
        String userName = sessionInfo != null ? sessionInfo.getUserName() : "unknown";

        // 执行登出
        boolean success = sessionManager.logout(token);

        if (success) {
            logger.info("用户登出成功: {}", userName);
            return createSuccessResponse(message, new LogoutResponse(true, "登出成功"));
        } else {
            logger.warn("用户登出失败，token可能已失效: {}", userName);
            return createErrorResponse(message, "LOGOUT_FAILED", "登出失败，令牌可能已失效");
        }
    }

    @Override
    public boolean supports(com.traffic.gat1049.protocol.model.Message message) {
        return com.traffic.gat1049.protocol.util.ProtocolUtils.isRequest(message) &&
                "Logout".equals(com.traffic.gat1049.protocol.util.ProtocolUtils.getOperationName(message));
    }

    @Override
    public String getHandlerName() {
        return "LogoutHandler";
    }
}
