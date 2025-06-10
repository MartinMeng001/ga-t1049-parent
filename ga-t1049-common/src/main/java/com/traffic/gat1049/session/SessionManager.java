package com.traffic.gat1049.session;

import com.traffic.gat1049.model.entity.sdo.SdoUser;
import com.traffic.gat1049.model.enums.SystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 会话管理器
 * 负责管理客户端连接会话和令牌
 */
public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    // 会话存储
    private final ConcurrentHashMap<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    // 令牌到会话的映射
    private final ConcurrentHashMap<String, String> tokenToSessionId = new ConcurrentHashMap<>();

    // 用户名到会话的映射
    private final ConcurrentHashMap<String, String> userToSessionId = new ConcurrentHashMap<>();

    // 定时清理器
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "SessionCleanup"));

    // 会话超时时间（分钟）
    private static final int SESSION_TIMEOUT_MINUTES = 30;

    public SessionManager() {
        // 启动定时清理任务
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredSessions,
                5, 5, TimeUnit.MINUTES);
    }

    /**
     * 用户登录，创建会话
     */
    public LoginResult login(String clientAddress, SdoUser user, SystemType systemType) {
        if (user == null || user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            return LoginResult.failure("用户名不能为空");
        }

        // 验证用户名和密码（这里简化处理）
        if (!validateUser(user)) {
            return LoginResult.failure("用户名或密码错误");
        }

        // 检查是否已经登录
        String existingSessionId = userToSessionId.get(user.getUserName());
        if (existingSessionId != null) {
            SessionInfo existingSession = sessions.get(existingSessionId);
            if (existingSession != null && !existingSession.isExpired()) {
                // 用户已登录，返回现有令牌
                logger.info("用户 {} 重复登录，返回现有会话", user.getUserName());
                return LoginResult.success(existingSession.getToken());
            } else {
                // 清理过期会话
                removeSession(existingSessionId);
            }
        }

        // 创建新会话
        String sessionId = generateSessionId();
        String token = generateToken();

        SessionInfo sessionInfo = new SessionInfo(
                sessionId, token, user.getUserName(), clientAddress, systemType);

        sessions.put(sessionId, sessionInfo);
        tokenToSessionId.put(token, sessionId);
        userToSessionId.put(user.getUserName(), sessionId);

        logger.info("用户 {} 登录成功，系统类型: {}, 客户端地址: {}",
                user.getUserName(), systemType.getDescription(), clientAddress);

        return LoginResult.success(token);
    }

    /**
     * 用户登出
     */
    public boolean logout(String token) {
        String sessionId = tokenToSessionId.get(token);
        if (sessionId == null) {
            return false;
        }

        SessionInfo session = sessions.get(sessionId);
        if (session != null) {
            logger.info("用户 {} 登出", session.getUserName());
            removeSession(sessionId);
            return true;
        }

        return false;
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        String sessionId = tokenToSessionId.get(token);
        if (sessionId == null) {
            return false;
        }

        SessionInfo session = sessions.get(sessionId);
        if (session == null || session.isExpired()) {
            if (session != null) {
                removeSession(sessionId);
            }
            return false;
        }

        // 更新最后活动时间
        session.updateLastActivity();
        return true;
    }

    /**
     * 获取会话信息
     */
    public SessionInfo getSession(String token) {
        String sessionId = tokenToSessionId.get(token);
        if (sessionId == null) {
            return null;
        }

        SessionInfo session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.updateLastActivity();
            return session;
        }

        return null;
    }

    /**
     * 更新会话心跳
     */
    public void heartbeat(String token) {
        SessionInfo session = getSession(token);
        if (session != null) {
            session.updateLastActivity();
            logger.debug("收到会话心跳: {}", session.getUserName());
        }
    }

    /**
     * 获取在线会话数量
     */
    public int getOnlineSessionCount() {
        return sessions.size();
    }

    /**
     * 关闭会话管理器
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        sessions.clear();
        tokenToSessionId.clear();
        userToSessionId.clear();
        logger.info("会话管理器已关闭");
    }

    private boolean validateUser(SdoUser user) {
        // 简化的用户验证逻辑
        // 在实际应用中，这里应该连接数据库进行用户验证
        String userName = user.getUserName();
        String password = user.getPwd();

        // 示例：允许特定的用户名和密码组合
        if ("admin".equals(userName) && "admin123".equals(password)) {
            return true;
        }
        if ("tsc_client".equals(userName) && "tsc123".equals(password)) {
            return true;
        }
        if ("test".equals(userName) && "test123".equals(password)) {
            return true;
        }

        return false;
    }

    private String generateSessionId() {
        return "SID_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateToken() {
        return "TKN_" + UUID.randomUUID().toString().replace("-", "");
    }

    private void removeSession(String sessionId) {
        SessionInfo session = sessions.remove(sessionId);
        if (session != null) {
            tokenToSessionId.remove(session.getToken());
            userToSessionId.remove(session.getUserName());
        }
    }

    private void cleanupExpiredSessions() {
        logger.debug("开始清理过期会话...");
        int cleanedCount = 0;

        for (String sessionId : sessions.keySet()) {
            SessionInfo session = sessions.get(sessionId);
            if (session != null && session.isExpired()) {
                removeSession(sessionId);
                cleanedCount++;
                logger.info("清理过期会话: {}", session.getUserName());
            }
        }

        if (cleanedCount > 0) {
            logger.info("清理了 {} 个过期会话", cleanedCount);
        }
    }

    /**
     * 会话信息
     */
    public static class SessionInfo {
        private final String sessionId;
        private final String token;
        private final String userName;
        private final String clientAddress;
        private final SystemType systemType;
        private final LocalDateTime createTime;
        private volatile LocalDateTime lastActivity;

        public SessionInfo(String sessionId, String token, String userName,
                           String clientAddress, SystemType systemType) {
            this.sessionId = sessionId;
            this.token = token;
            this.userName = userName;
            this.clientAddress = clientAddress;
            this.systemType = systemType;
            this.createTime = LocalDateTime.now();
            this.lastActivity = LocalDateTime.now();
        }

        public boolean isExpired() {
            return lastActivity.plusMinutes(SESSION_TIMEOUT_MINUTES).isBefore(LocalDateTime.now());
        }

        public void updateLastActivity() {
            this.lastActivity = LocalDateTime.now();
        }

        // Getters
        public String getSessionId() { return sessionId; }
        public String getToken() { return token; }
        public String getUserName() { return userName; }
        public String getClientAddress() { return clientAddress; }
        public SystemType getSystemType() { return systemType; }
        public LocalDateTime getCreateTime() { return createTime; }
        public LocalDateTime getLastActivity() { return lastActivity; }

        @Override
        public String toString() {
            return "SessionInfo{" +
                    "sessionId='" + sessionId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", clientAddress='" + clientAddress + '\'' +
                    ", systemType=" + systemType +
                    ", createTime=" + createTime +
                    ", lastActivity=" + lastActivity +
                    '}';
        }
    }

    /**
     * 登录结果
     */
    public static class LoginResult {
        private final boolean success;
        private final String token;
        private final String errorMessage;

        private LoginResult(boolean success, String token, String errorMessage) {
            this.success = success;
            this.token = token;
            this.errorMessage = errorMessage;
        }

        public static LoginResult success(String token) {
            return new LoginResult(true, token, null);
        }

        public static LoginResult failure(String errorMessage) {
            return new LoginResult(false, null, errorMessage);
        }

        public boolean isSuccess() { return success; }
        public String getToken() { return token; }
        public String getErrorMessage() { return errorMessage; }
    }
}
