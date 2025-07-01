package com.traffic.server.config;

import com.traffic.server.service.ServerSubscriptionService;
import com.traffic.gat1049.application.HandlerRegistry;
import com.traffic.gat1049.application.subscription.interfaces.SubscriptionService;
import com.traffic.gat1049.application.subscription.SubscriptionResult;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 服务端订阅配置
 */
@Configuration
public class ServerSubscriptionConfig implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ServerSubscriptionConfig.class);

    @Autowired
    private HandlerRegistry handlerRegistry;

    private SubscriptionService subscriptionServiceInstance;

    /**
     * 服务端订阅服务实现
     */
    @Bean
    public SubscriptionService serverSubscriptionService() {
        if (subscriptionServiceInstance == null) {
            subscriptionServiceInstance = new ServerSubscriptionServiceImpl();
        }
        return subscriptionServiceInstance;
    }

    /**
     * ServerSubscriptionService Bean定义
     * 供其他组件注入使用
     */
    @Bean
    public ServerSubscriptionService trafficServerSubscriptionService() {
        return new ServerSubscriptionService();
    }

    /**
     * 在Spring容器完全初始化后执行注入操作
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 确保只在根容器初始化完成后执行一次
        if (event.getApplicationContext().getParent() == null) {
            configureSubscriptionService();
        }
    }

    private void configureSubscriptionService() {
        try {
            // 获取已创建的Bean实例
            if (subscriptionServiceInstance == null) {
                subscriptionServiceInstance = serverSubscriptionService();
            }

            // 注入到HandlerRegistry
            handlerRegistry.injectSubscriptionService(subscriptionServiceInstance);
            logger.info("服务端订阅服务注入完成");
        } catch (Exception e) {
            logger.error("订阅服务注入失败", e);
        }
    }

    /**
     * 服务端订阅服务实现类
     */
    private static class ServerSubscriptionServiceImpl implements SubscriptionService {

        private static final Logger logger = LoggerFactory.getLogger(ServerSubscriptionServiceImpl.class);

        private static final List<String> SERVER_SUPPORTED_OBJECTS = Arrays.asList(
                "CrossCycle", "CrossModePlan", "SignalControllerError",
                "CrossState", "SysState", "CrossStage"
        );

        @Override
        public SubscriptionResult handleSubscribe(String token, SdoMsgEntity subscription, Message originalMessage) {
            logger.info("服务端处理订阅请求: token={}, objName={}", token, subscription.getObjName());

            // 服务端的订阅处理逻辑
            // 这里可以集成现有的SubscriptionManager或其他逻辑

            return SubscriptionResult.success();
        }

        @Override
        public SubscriptionResult handleUnsubscribe(String token, SdoMsgEntity subscription, Message originalMessage) {
            logger.info("服务端处理取消订阅请求: token={}, objName={}", token, subscription.getObjName());

            // 服务端的取消订阅处理逻辑

            return SubscriptionResult.success();
        }

        @Override
        public boolean supportsObject(String objName) {
            return "*".equals(objName) || SERVER_SUPPORTED_OBJECTS.contains(objName);
        }

        @Override
        public List<String> getSupportedObjects() {
            return SERVER_SUPPORTED_OBJECTS;
        }
    }
}