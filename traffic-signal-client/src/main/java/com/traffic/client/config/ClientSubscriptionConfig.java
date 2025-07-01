package com.traffic.client.config;

import com.traffic.client.service.ClientSubscriptionService;
import com.traffic.gat1049.application.HandlerRegistry;
import com.traffic.gat1049.protocol.handler.subscription.NotifySubscribeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * 客户端订阅配置 - 通过PostConstruct注入
 */
@Configuration
public class ClientSubscriptionConfig {

    private static final Logger logger = LoggerFactory.getLogger(ClientSubscriptionConfig.class);

    @Autowired
    private HandlerRegistry handlerRegistry;

    @Autowired
    private ClientSubscriptionService clientSubscriptionService;

    @PostConstruct
    public void configureSubscriptionService() {
        // 将客户端订阅服务注入到NotifySubscribeHandler
        handlerRegistry.injectSubscriptionService(clientSubscriptionService);
        logger.info("客户端订阅服务注入完成");
    }
}