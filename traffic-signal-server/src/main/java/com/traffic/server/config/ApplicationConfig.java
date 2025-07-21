package com.traffic.server.config;

import com.traffic.gat1049.application.connection.ConnectionManager;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.exception.BusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 应用核心Bean配置
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

    @Bean
    public ConnectionManager connectionManager(SessionManager sessionManager) {
        return new ConnectionManager(sessionManager);
    }
}