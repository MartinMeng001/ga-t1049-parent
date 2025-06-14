package com.traffic.client.config;

import com.traffic.gat1049.application.HandlerRegistry;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.exception.MessageEncodingException;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.service.abstracts.DefaultServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 客户端消息处理相关Bean配置
 */
@Configuration
@DependsOn("applicationConfig")
public class MessageProcessingConfig {

    @Bean
    public HandlerRegistry handlerRegistry(
            DefaultServiceFactory serviceFactory,
            SessionManager sessionManager,
            SubscriptionManager subscriptionManager) throws MessageEncodingException {
        return new HandlerRegistry(serviceFactory, sessionManager, subscriptionManager);
    }

    @Bean
    public MessageProcessor messageProcessor(HandlerRegistry handlerRegistry) {
        return handlerRegistry.getMessageProcessor();
    }
}
