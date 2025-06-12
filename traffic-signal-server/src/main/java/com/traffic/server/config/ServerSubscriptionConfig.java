package com.traffic.server.config;

import com.traffic.server.service.ServerSubscriptionService;
import com.traffic.gat1049.protocol.processor.DefaultMessageProcessor;
import com.traffic.gat1049.protocol.handler.subscription.NotifySubscribeHandler;
import com.traffic.gat1049.protocol.handler.subscription.NotifyUnsubscribeHandler;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
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
 * 服务端订阅配置
 * 配置服务端向客户端发送订阅请求，接收客户端推送数据的功能
 *
 * 放置位置：traffic-signal-server/src/main/java/com/traffic/server/config/
 */
@Configuration
@ConditionalOnProperty(name = "app.role", havingValue = "server", matchIfMissing = true)
public class ServerSubscriptionConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServerSubscriptionConfig.class);

    /**
     * 服务端订阅服务 - 负责向客户端发送订阅请求
     */
    @Bean
    public ServerSubscriptionService serverSubscriptionService() {
        logger.info("创建服务端订阅服务");
        return new ServerSubscriptionService();
    }

    /**
     * 服务端接收客户端订阅请求的处理器
     * 虽然主要是反向订阅，但服务端也可能接收客户端的正向订阅
     */
    @Bean
    public NotifySubscribeHandler serverNotifySubscribeHandler(
            SubscriptionManager subscriptionManager,
            SessionManager sessionManager) {
        logger.info("创建服务端Notify订阅处理器");
        return new NotifySubscribeHandler(subscriptionManager, sessionManager);
    }

    /**
     * 服务端取消订阅处理器
     */
    @Bean
    public NotifyUnsubscribeHandler serverNotifyUnsubscribeHandler(
            SubscriptionManager subscriptionManager,
            SessionManager sessionManager) {
        logger.info("创建服务端Notify取消订阅处理器");
        return new NotifyUnsubscribeHandler(subscriptionManager, sessionManager);
    }

    /**
     * 服务端推送消息处理器 - 处理客户端推送的数据
     */
    @Bean
    public ServerPushMessageHandler serverPushMessageHandler(
            ServerSubscriptionService subscriptionService) {
        logger.info("创建服务端推送消息处理器");
        return new ServerPushMessageHandler(subscriptionService);
    }

    /**
     * 配置服务端消息处理器
     */
    @Bean
    public DefaultMessageProcessor configureServerMessageProcessor(
            DefaultMessageProcessor messageProcessor,
            NotifySubscribeHandler subscribeHandler,
            NotifyUnsubscribeHandler unsubscribeHandler,
            ServerPushMessageHandler pushHandler) {

        logger.info("配置服务端消息处理器");

        // 注册订阅相关处理器
        messageProcessor.registerHandler(subscribeHandler);
        messageProcessor.registerHandler(unsubscribeHandler);

        // 注册推送消息处理器
        messageProcessor.registerHandler(pushHandler);

        logger.info("服务端消息处理器配置完成");
        return messageProcessor;
    }

    /**
     * 服务端推送消息处理器实现类
     * 专门处理从客户端接收到的推送消息
     */
    public static class ServerPushMessageHandler extends AbstractProtocolHandler {

        private static final Logger logger = LoggerFactory.getLogger(ServerPushMessageHandler.class);
        private final ServerSubscriptionService subscriptionService;

        public ServerPushMessageHandler(ServerSubscriptionService subscriptionService) {
            this.subscriptionService = subscriptionService;
        }

        @Override
        public boolean supports(Message message) {
            // 只处理推送类型的Notify消息
            return GatConstants.MessageType.PUSH.equals(message.getType()) &&
                    GatConstants.Operation.NOTIFY.equals(ProtocolUtils.getOperationName(message));
        }

        @Override
        protected Message doHandle(Message message) {
            logger.debug("服务端接收到推送消息: seq={}, from={}",
                    message.getSeq(),
                    message.getFrom() != null ? message.getFrom().getSys() : "unknown");

            // 将推送消息委托给订阅服务处理
            subscriptionService.handlePushMessage(message);

            // 推送消息不需要响应
            return null;
        }

        @Override
        public String getHandlerName() {
            return "ServerPushMessageHandler";
        }
    }

    /**
     * 服务端订阅监控服务
     */
    @Bean
    public ServerSubscriptionMonitor serverSubscriptionMonitor(
            ServerSubscriptionService subscriptionService) {
        logger.info("创建服务端订阅监控服务");
        return new ServerSubscriptionMonitor(subscriptionService);
    }

    /**
     * 服务端订阅监控实现
     */
    public static class ServerSubscriptionMonitor {

        private static final Logger logger = LoggerFactory.getLogger(ServerSubscriptionMonitor.class);
        private final ServerSubscriptionService subscriptionService;

        public ServerSubscriptionMonitor(ServerSubscriptionService subscriptionService) {
            this.subscriptionService = subscriptionService;
        }

        /**
         * 获取订阅统计信息
         */
        public SubscriptionStats getSubscriptionStats() {
            SubscriptionStats stats = new SubscriptionStats();
            // 实现统计逻辑
            logger.debug("获取服务端订阅统计信息");
            return stats;
        }

        /**
         * 检查订阅健康状态
         */
        public boolean checkSubscriptionHealth() {
            logger.debug("检查服务端订阅健康状态");
            return true;
        }

        /**
         * 订阅统计信息
         */
        public static class SubscriptionStats {
            private int totalSentSubscriptions = 0;
            private int activeSubscriptions = 0;
            private int receivedPushMessages = 0;
            private long lastPushTime = System.currentTimeMillis();

            // Getters and Setters
            public int getTotalSentSubscriptions() { return totalSentSubscriptions; }
            public void setTotalSentSubscriptions(int totalSentSubscriptions) {
                this.totalSentSubscriptions = totalSentSubscriptions;
            }

            public int getActiveSubscriptions() { return activeSubscriptions; }
            public void setActiveSubscriptions(int activeSubscriptions) {
                this.activeSubscriptions = activeSubscriptions;
            }

            public int getReceivedPushMessages() { return receivedPushMessages; }
            public void setReceivedPushMessages(int receivedPushMessages) {
                this.receivedPushMessages = receivedPushMessages;
            }

            public long getLastPushTime() { return lastPushTime; }
            public void setLastPushTime(long lastPushTime) {
                this.lastPushTime = lastPushTime;
            }
        }
    }
}