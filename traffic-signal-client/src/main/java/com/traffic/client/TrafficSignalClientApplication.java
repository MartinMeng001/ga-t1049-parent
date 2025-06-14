package com.traffic.client;

import com.traffic.client.config.ApplicationConfig;
import com.traffic.client.config.ClientConfig;
import com.traffic.client.config.ClientSubscriptionConfig;
import com.traffic.client.config.MessageProcessingConfig;
import com.traffic.client.config.NetworkConfig;
import com.traffic.client.service.ClientDataPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * GA/T 1049.2交通信号控制系统客户端应用
 * 支持GA/T 1049.1通用通信协议
 */
@SpringBootApplication
@Import({
        ApplicationConfig.class,
        MessageProcessingConfig.class,
        NetworkConfig.class,
        ClientConfig.class,
        ClientSubscriptionConfig.class
})
public class TrafficSignalClientApplication {

    private static final Logger logger = LoggerFactory.getLogger(TrafficSignalClientApplication.class);

    @Autowired
    private ClientDataPushService pushService;

    @PostConstruct
    public void initPushService() {
        logger.info("客户端推送服务已就绪，等待服务端订阅请求");
        // 客户端无需主动配置，会自动处理服务端的订阅请求
    }

    public static void main(String[] args) {
        SpringApplication.run(TrafficSignalClientApplication.class, args);
    }
}