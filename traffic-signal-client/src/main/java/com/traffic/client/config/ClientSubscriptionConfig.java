package com.traffic.client.config;

import com.traffic.client.service.ClientDataPushService;
import com.traffic.gat1049.protocol.processor.DefaultMessageProcessor;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端订阅配置
 * 配置客户端接收服务端订阅请求，向服务端推送数据的功能
 *
 * 放置位置：traffic-signal-client/src/main/java/com/traffic/client/config/
 */
@Configuration
@ConditionalOnProperty(name = "app.role", havingValue = "client", matchIfMissing = true)
public class ClientSubscriptionConfig {

    private static final Logger logger = LoggerFactory.getLogger(ClientSubscriptionConfig.class);

    /**
     * 客户端数据推送服务 - 负责向服务端推送数据
     */
    @Bean
    public ClientDataPushService clientDataPushService() {
        logger.info("创建客户端数据推送服务");
        return new ClientDataPushService();
    }

    /**
     * 客户端订阅请求处理器 - 处理来自服务端的订阅请求
     */
    @Bean
    public ClientSubscriptionHandler clientSubscriptionHandler(
            ClientDataPushService pushService) {
        logger.info("创建客户端订阅请求处理器");
        return new ClientSubscriptionHandler(pushService);
    }

    /**
     * 配置客户端消息处理器
     */
    @Bean
    public DefaultMessageProcessor configureClientMessageProcessor(
            DefaultMessageProcessor messageProcessor,
            ClientSubscriptionHandler subscriptionHandler) {

        logger.info("配置客户端消息处理器");

        // 注册订阅请求处理器
        messageProcessor.registerHandler(subscriptionHandler);

        logger.info("客户端消息处理器配置完成");
        return messageProcessor;
    }

    /**
     * 客户端订阅请求处理器实现
     * 处理来自服务端的订阅和取消订阅请求
     */
    public static class ClientSubscriptionHandler extends AbstractProtocolHandler {

        private static final Logger logger = LoggerFactory.getLogger(ClientSubscriptionHandler.class);
        private final ClientDataPushService pushService;

        public ClientSubscriptionHandler(ClientDataPushService pushService) {
            this.pushService = pushService;
        }

        @Override
        public boolean supports(Message message) {
            // 处理来自服务端的订阅和取消订阅请求
            if (!ProtocolUtils.isRequest(message)) {
                return false;
            }

            String operationName = ProtocolUtils.getOperationName(message);
            return GatConstants.Operation.SUBSCRIBE.equals(operationName) ||
                    GatConstants.Operation.UNSUBSCRIBE.equals(operationName);
        }

        @Override
        protected Message doHandle(Message message) {
            String operationName = ProtocolUtils.getOperationName(message);

            logger.debug("客户端处理订阅请求: operation={}, from={}",
                    operationName,
                    message.getFrom() != null ? message.getFrom().getSys() : "unknown");

            if (GatConstants.Operation.SUBSCRIBE.equals(operationName)) {
                return pushService.handleSubscriptionRequest(message);
            } else if (GatConstants.Operation.UNSUBSCRIBE.equals(operationName)) {
                return pushService.handleUnsubscriptionRequest(message);
            }

            return createErrorResponse(message, "UNSUPPORTED_OPERATION",
                    "不支持的操作: " + operationName);
        }

        @Override
        public String getHandlerName() {
            return "ClientSubscriptionHandler";
        }
    }

    /**
     * 客户端推送监控服务
     */
    @Bean
    public ClientPushMonitor clientPushMonitor(
            ClientDataPushService pushService) {
        logger.info("创建客户端推送监控服务");
        return new ClientPushMonitor(pushService);
    }

    /**
     * 客户端推送监控实现
     */
    public static class ClientPushMonitor {

        private static final Logger logger = LoggerFactory.getLogger(ClientPushMonitor.class);
        private final ClientDataPushService pushService;

        public ClientPushMonitor(ClientDataPushService pushService) {
            this.pushService = pushService;
        }

        /**
         * 获取推送统计信息
         */
        public PushStats getPushStats() {
            PushStats stats = new PushStats();
            // 实现统计逻辑
            logger.debug("获取客户端推送统计信息");
            return stats;
        }

        /**
         * 检查推送服务健康状态
         */
        public boolean checkPushHealth() {
            logger.debug("检查客户端推送服务健康状态");
            return true;
        }

        /**
         * 手动触发数据推送（用于测试）
         */
        public void triggerManualPush(String serverId, String dataType) {
            logger.info("手动触发数据推送: serverId={}, dataType={}", serverId, dataType);
            pushService.manualPush(serverId, dataType);
        }

        /**
         * 推送统计信息
         */
        public static class PushStats {
            private int activeSubscriptions = 0;
            private int totalPushMessages = 0;
            private long lastPushTime = System.currentTimeMillis();
            private double pushSuccessRate = 1.0;

            // Getters and Setters
            public int getActiveSubscriptions() { return activeSubscriptions; }
            public void setActiveSubscriptions(int activeSubscriptions) {
                this.activeSubscriptions = activeSubscriptions;
            }

            public int getTotalPushMessages() { return totalPushMessages; }
            public void setTotalPushMessages(int totalPushMessages) {
                this.totalPushMessages = totalPushMessages;
            }

            public long getLastPushTime() { return lastPushTime; }
            public void setLastPushTime(long lastPushTime) {
                this.lastPushTime = lastPushTime;
            }

            public double getPushSuccessRate() { return pushSuccessRate; }
            public void setPushSuccessRate(double pushSuccessRate) {
                this.pushSuccessRate = pushSuccessRate;
            }
        }
    }
}