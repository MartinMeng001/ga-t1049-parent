package com.traffic.gat1049.application.connection;

import com.traffic.gat1049.application.session.SessionManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接状态管理器
 * 负责管理客户端连接状态、心跳检测和断线重连
 */
public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private final SessionManager sessionManager;
    private final ScheduledExecutorService heartbeatExecutor;

    // 连接状态存储
    private final ConcurrentHashMap<String, ConnectionStatus> connections = new ConcurrentHashMap<>();

    // 心跳间隔（秒）
    private static final int HEARTBEAT_INTERVAL = 30;

    // 连接超时时间（秒）
    private static final int CONNECTION_TIMEOUT = 90;

    public ConnectionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.heartbeatExecutor = Executors.newScheduledThreadPool(2);

        // 启动心跳检测任务
        startHeartbeatChecker();
    }

    /**
     * 注册连接
     */
    public void registerConnection(String clientId, String token) {
        if(connections.containsKey(clientId)) return;
        ConnectionStatus status = new ConnectionStatus(clientId, token);
        connections.put(clientId, status);
        logger.info("注册连接: clientId={}, token={}", clientId, token);
    }

    /**
     * 注销连接
     */
    public void unregisterConnection(String clientId) {
        ConnectionStatus removed = connections.remove(clientId);
        if (removed != null) {
            logger.info("注销连接: clientId={}", clientId);
        }
    }

    /**
     * 更新连接心跳
     */
    public void updateHeartbeat(String clientId) {
        ConnectionStatus status = connections.get(clientId);
        if (status != null) {
            status.updateLastHeartbeat();
            logger.debug("更新心跳: clientId={}", clientId);
        }
    }

    /*
     * 根据clientId获取Token
     */
    public String getToken(String clientId) {
        String token = connections.get(clientId).getToken();
        return token;
    }
    /**
     * 检查连接是否在线
     */
    public boolean isOnline(String clientId) {
        ConnectionStatus status = connections.get(clientId);
        return status != null && !status.isTimeout();
    }

    /**
     * 获取在线连接数
     */
    public int getOnlineConnectionCount() {
        return (int) connections.values().stream()
                .filter(status -> !status.isTimeout())
                .count();
    }

    /**
     * 关闭连接管理器
     */
    public void shutdown() {
        heartbeatExecutor.shutdown();
        connections.clear();
        logger.info("连接管理器已关闭");
    }

    private void startHeartbeatChecker() {
        // 定期检查超时连接
        heartbeatExecutor.scheduleAtFixedRate(this::checkTimeoutConnections,
                HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    private void checkTimeoutConnections() {
        for (String clientId : connections.keySet()) {
            ConnectionStatus status = connections.get(clientId);
            if (status != null && status.isTimeout()) {
                logger.warn("连接超时: clientId={}", clientId);

                // 清理会话
                if (status.getToken() != null) {
                    sessionManager.logout(status.getToken());
                }

                // 移除连接
                connections.remove(clientId);
            }
        }
    }

    /**
     * 连接状态
     */
    private static class ConnectionStatus {
        private final String clientId;
        private final String token;
        private final long connectTime;
        private volatile long lastHeartbeat;

        public ConnectionStatus(String clientId, String token) {
            this.clientId = clientId;
            this.token = token;
            this.connectTime = System.currentTimeMillis();
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public void updateLastHeartbeat() {
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public boolean isTimeout() {
            return (System.currentTimeMillis() - lastHeartbeat) > (CONNECTION_TIMEOUT * 1000L);
        }

        public String getClientId() { return clientId; }
        public String getToken() { return token; }
        public long getConnectTime() { return connectTime; }
        public long getLastHeartbeat() { return lastHeartbeat; }
    }
}
