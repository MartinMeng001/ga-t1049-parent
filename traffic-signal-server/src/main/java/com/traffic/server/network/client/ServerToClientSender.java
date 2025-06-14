package com.traffic.server.network.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端到客户端消息发送器
 * 管理客户端连接，并向指定客户端发送消息
 */
@Component
public class ServerToClientSender {

    private static final Logger logger = LoggerFactory.getLogger(ServerToClientSender.class);

    // 客户端连接管理 clientId -> Channel
    private final ConcurrentHashMap<String, Channel> clientChannels = new ConcurrentHashMap<>();

    // 客户端连接状态 clientId -> 连接状态
    private final ConcurrentHashMap<String, Boolean> clientStatus = new ConcurrentHashMap<>();

    /**
     * 注册客户端连接
     * @param clientId 客户端ID
     * @param channel 客户端连接通道
     */
    public void registerClient(String clientId, Channel channel) {
        clientChannels.put(clientId, channel);
        clientStatus.put(clientId, true);

        // 监听连接关闭事件
        channel.closeFuture().addListener(future -> {
            logger.info("客户端连接关闭: {}", clientId);
            clientChannels.remove(clientId);
            clientStatus.remove(clientId);
        });

        logger.info("注册客户端连接: {} -> {}", clientId, channel.remoteAddress());
    }

    /**
     * 注册客户端连接（通过ChannelHandlerContext）
     * @param clientId 客户端ID
     * @param ctx 通道上下文
     */
    public void registerClient(String clientId, ChannelHandlerContext ctx) {
        registerClient(clientId, ctx.channel());
    }

    /**
     * 移除客户端连接
     * @param clientId 客户端ID
     */
    public void unregisterClient(String clientId) {
        Channel channel = clientChannels.remove(clientId);
        clientStatus.remove(clientId);

        if (channel != null && channel.isActive()) {
            channel.close();
        }

        logger.info("移除客户端连接: {}", clientId);
    }

    /**
     * 发送消息到指定客户端
     * @param clientId 客户端ID
     * @param message 消息内容
     * @return 发送结果的Future
     */
    public CompletableFuture<Boolean> sendToClient(String clientId, String message) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Channel channel = clientChannels.get(clientId);
        if (channel == null) {
            logger.warn("客户端连接不存在: {}", clientId);
            future.complete(false);
            return future;
        }

        if (!channel.isActive()) {
            logger.warn("客户端连接不活跃: {}", clientId);
            clientChannels.remove(clientId);
            clientStatus.remove(clientId);
            future.complete(false);
            return future;
        }

        try {
            channel.writeAndFlush(message).addListener(channelFuture -> {
                if (channelFuture.isSuccess()) {
                    logger.debug("消息发送成功到客户端: {}, messageLength={}", clientId, message.length());
                    future.complete(true);
                } else {
                    logger.error("消息发送失败到客户端: {}", clientId, channelFuture.cause());
                    future.complete(false);
                }
            });
        } catch (Exception e) {
            logger.error("发送消息异常: clientId={}", clientId, e);
            future.complete(false);
        }

        return future;
    }

    /**
     * 广播消息到所有客户端
     * @param message 消息内容
     * @return 发送结果统计
     */
    public CompletableFuture<BroadcastResult> broadcastToAllClients(String message) {
        CompletableFuture<BroadcastResult> future = new CompletableFuture<>();
        BroadcastResult result = new BroadcastResult();

        if (clientChannels.isEmpty()) {
            logger.warn("没有客户端连接，无法广播消息");
            result.setTotalClients(0);
            result.setSuccessCount(0);
            result.setFailedCount(0);
            future.complete(result);
            return future;
        }

        result.setTotalClients(clientChannels.size());

        // 异步发送到所有客户端
        CompletableFuture<Void> allSends = CompletableFuture.allOf(
                clientChannels.entrySet().stream()
                        .map(entry -> {
                            String clientId = entry.getKey();
                            return sendToClient(clientId, message)
                                    .thenAccept(success -> {
                                        if (success) {
                                            result.incrementSuccess();
                                            result.addSuccessClient(clientId);
                                        } else {
                                            result.incrementFailed();
                                            result.addFailedClient(clientId);
                                        }
                                    });
                        })
                        .toArray(CompletableFuture[]::new)
        );

        allSends.whenComplete((unused, throwable) -> {
            if (throwable != null) {
                logger.error("广播消息时发生异常", throwable);
            }

            logger.info("广播消息完成: 总数={}, 成功={}, 失败={}",
                    result.getTotalClients(), result.getSuccessCount(), result.getFailedCount());

            future.complete(result);
        });

        return future;
    }

    /**
     * 检查客户端是否在线
     * @param clientId 客户端ID
     * @return 是否在线
     */
    public boolean isClientOnline(String clientId) {
        Channel channel = clientChannels.get(clientId);
        return channel != null && channel.isActive();
    }

    /**
     * 获取在线客户端数量
     * @return 在线客户端数量
     */
    public int getOnlineClientCount() {
        return (int) clientChannels.values().stream()
                .filter(Channel::isActive)
                .count();
    }

    /**
     * 获取所有在线客户端ID
     * @return 在线客户端ID列表
     */
    public java.util.Set<String> getOnlineClientIds() {
        return clientChannels.entrySet().stream()
                .filter(entry -> entry.getValue().isActive())
                .map(java.util.Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 获取客户端连接信息
     * @param clientId 客户端ID
     * @return 连接信息
     */
    public ClientConnectionInfo getClientInfo(String clientId) {
        Channel channel = clientChannels.get(clientId);
        if (channel == null) {
            return null;
        }

        ClientConnectionInfo info = new ClientConnectionInfo();
        info.setClientId(clientId);
        info.setRemoteAddress(channel.remoteAddress().toString());
        info.setActive(channel.isActive());
        info.setConnectTime(System.currentTimeMillis()); // 简化实现

        return info;
    }

    /**
     * 获取所有客户端连接信息
     * @return 所有客户端连接信息
     */
    public java.util.List<ClientConnectionInfo> getAllClientInfo() {
        return clientChannels.entrySet().stream()
                .map(entry -> {
                    String clientId = entry.getKey();
                    Channel channel = entry.getValue();

                    ClientConnectionInfo info = new ClientConnectionInfo();
                    info.setClientId(clientId);
                    info.setRemoteAddress(channel.remoteAddress().toString());
                    info.setActive(channel.isActive());
                    info.setConnectTime(System.currentTimeMillis());

                    return info;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 清理所有连接
     */
    public void cleanupAllConnections() {
        logger.info("清理所有客户端连接...");

        clientChannels.values().forEach(channel -> {
            if (channel.isActive()) {
                channel.close();
            }
        });

        clientChannels.clear();
        clientStatus.clear();

        logger.info("所有客户端连接已清理");
    }

    /**
     * 广播结果类
     */
    public static class BroadcastResult {
        private int totalClients;
        private int successCount;
        private int failedCount;
        private final java.util.List<String> successClients = new java.util.concurrent.CopyOnWriteArrayList<>();
        private final java.util.List<String> failedClients = new java.util.concurrent.CopyOnWriteArrayList<>();

        public int getTotalClients() { return totalClients; }
        public void setTotalClients(int totalClients) { this.totalClients = totalClients; }

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getFailedCount() { return failedCount; }
        public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

        public java.util.List<String> getSuccessClients() { return successClients; }
        public java.util.List<String> getFailedClients() { return failedClients; }

        public void incrementSuccess() { this.successCount++; }
        public void incrementFailed() { this.failedCount++; }

        public void addSuccessClient(String clientId) { successClients.add(clientId); }
        public void addFailedClient(String clientId) { failedClients.add(clientId); }

        public double getSuccessRate() {
            return totalClients > 0 ? (double) successCount / totalClients : 0.0;
        }
    }

    /**
     * 客户端连接信息类
     */
    public static class ClientConnectionInfo {
        private String clientId;
        private String remoteAddress;
        private boolean active;
        private long connectTime;

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }

        public String getRemoteAddress() { return remoteAddress; }
        public void setRemoteAddress(String remoteAddress) { this.remoteAddress = remoteAddress; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public long getConnectTime() { return connectTime; }
        public void setConnectTime(long connectTime) { this.connectTime = connectTime; }

        @Override
        public String toString() {
            return String.format("ClientConnectionInfo{clientId='%s', remoteAddress='%s', active=%s, connectTime=%d}",
                    clientId, remoteAddress, active, connectTime);
        }
    }
}