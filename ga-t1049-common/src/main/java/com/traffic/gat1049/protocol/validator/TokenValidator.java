package com.traffic.gat1049.protocol.validator;

import com.traffic.gat1049.exception.MessageValidationException;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Token验证器 - 协议层的token验证组件
 * 与MessageValidator并列，专门负责协议层面的token验证
 */
public class TokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(TokenValidator.class);

    private final SessionManager sessionManager;

    public TokenValidator(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * 验证消息中的token是否有效
     *
     * @param message 待验证的消息
     * @throws MessageValidationException 如果token无效
     */
    public void validateToken(Message message) throws MessageValidationException {
        String token = message.getToken();

        if (token == null || token.trim().isEmpty()) {
            throw new MessageValidationException("访问令牌不能为空");
        }

        if (!sessionManager.validateToken(token)) {
            throw new MessageValidationException("访问令牌无效或已过期");
        }

        logger.debug("Token验证通过: token={}", token.substring(0, Math.min(8, token.length())) + "...");
    }

    /**
     * 验证token并返回会话信息
     *
     * @param message 待验证的消息
     * @return 会话信息
     * @throws MessageValidationException 如果token无效
     */
    public SessionManager.SessionInfo validateAndGetSession(Message message) throws MessageValidationException {
        validateToken(message);

        String token = message.getToken();
        SessionManager.SessionInfo sessionInfo = sessionManager.getSession(token);

        if (sessionInfo == null) {
            throw new MessageValidationException("会话信息不存在");
        }

        // 更新心跳
        sessionManager.heartbeat(token);

        return sessionInfo;
    }

    /**
     * 检查消息是否需要token验证
     * 与MessageValidator的验证逻辑类似，检查协议层面的要求
     *
     * @param message 消息
     * @return 是否需要验证token
     */
    public static boolean requiresTokenValidation(Message message) {
        if (message == null) {
            return false;
        }

        // Login请求不需要token验证
        if (isLoginRequest(message)) {
            return false;
        }

        // 其他所有请求都需要token验证
        return true;
    }

    /**
     * 判断是否为登录请求
     * 这是协议层面的判断逻辑
     */
    private static boolean isLoginRequest(Message message) {
        if (message.getBody() == null || message.getBody().getOperations() == null) {
            return false;
        }

        return message.getBody().getOperations().stream()
                .anyMatch(op -> "Login".equals(op.getName()));
    }

    /**
     * 获取当前活跃会话统计信息
     * 协议层面的会话状态查询
     */
    public String getSessionStats() {
        int activeCount = sessionManager.getOnlineSessionCount();
        return String.format("当前活跃会话数: %d", activeCount);
    }

    /**
     * 注销指定token的会话
     * 协议层面的会话管理操作
     */
    public boolean logoutSession(String token) {
        return sessionManager.logout(token);
    }

    /**
     * 批量验证消息列表的token
     * 协议层面的批量验证功能
     */
    public void validateTokenBatch(java.util.List<Message> messages) throws MessageValidationException {
        for (Message message : messages) {
            if (requiresTokenValidation(message)) {
                validateToken(message);
            }
        }
    }

    /**
     * 验证token格式是否正确（不检查有效性）
     * 协议层面的格式验证
     */
    public static boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        // 检查token格式：应该以TKN_开头，长度合理
        return token.startsWith("TKN_") && token.length() >= 10 && token.length() <= 64;
    }

    /**
     * 从消息中安全提取token（处理各种异常情况）
     * 协议层面的数据提取功能
     */
    public static String extractToken(Message message) {
        if (message == null) {
            return null;
        }

        String token = message.getToken();
        if (token != null) {
            token = token.trim();
            if (token.isEmpty()) {
                return null;
            }
        }

        return token;
    }
}