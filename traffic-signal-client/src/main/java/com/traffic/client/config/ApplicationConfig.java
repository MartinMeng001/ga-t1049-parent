package com.traffic.client.config;

import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 客户端核心Bean配置
 */
@Configuration
public class ApplicationConfig {

//    @Bean
//    @Primary
//    public DefaultServiceFactory defaultServiceFactory() throws BusinessException {
//        return new DefaultServiceFactory();
//    }

    @Bean
    public SessionManager sessionManager() {
        return new SessionManager();
    }

    @Bean
    public SubscriptionManager subscriptionManager() {
        return new SubscriptionManager();
    }
}
