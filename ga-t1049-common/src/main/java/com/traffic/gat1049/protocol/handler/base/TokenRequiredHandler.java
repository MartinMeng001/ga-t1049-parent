package com.traffic.gat1049.protocol.handler.base;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.MessageValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.validator.TokenValidator;
import com.traffic.gat1049.application.session.SessionManager;

/**
 * 需要Token验证的Handler基类
 * 使用组合而不是继承的方式，更加灵活
 */
public abstract class TokenRequiredHandler extends AbstractProtocolHandler {

    protected TokenRequiredHandler(SessionManager sessionManager) {
        // 自动设置TokenValidator
        this.setTokenValidator(new TokenValidator(sessionManager));
    }

    @Override
    protected final Message doHandle(Message message) throws GatProtocolException {
        try {
            // 获取当前会话信息
            SessionManager.SessionInfo sessionInfo = getCurrentSession(message);

            if (sessionInfo != null) {
                logger.debug("Processing request from user: {}", sessionInfo.getUserName());
                return doHandleWithSession(message, sessionInfo);
            } else {
                // 不需要token验证的请求（如Login）
                return doHandleWithoutSession(message);
            }

        } catch (MessageValidationException e) {
            throw new GatProtocolException("TOKEN_VALIDATION_FAILED", e.getMessage());
        }
    }

    /**
     * 需要token验证的请求处理逻辑
     */
    protected abstract Message doHandleWithSession(Message message, SessionManager.SessionInfo sessionInfo)
            throws GatProtocolException;

    /**
     * 不需要token验证的请求处理逻辑（默认不支持）
     */
    protected Message doHandleWithoutSession(Message message) throws GatProtocolException {
        throw new GatProtocolException("TOKEN_REQUIRED",
                "此处理器需要有效的访问令牌: " + getHandlerName());
    }
}
