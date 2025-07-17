package com.traffic.server;

import com.traffic.server.config.ApplicationConfig;
import com.traffic.server.config.MessageProcessingConfig;
import com.traffic.server.config.ServerConfig;
import com.traffic.server.config.ServerSubscriptionConfig;
import com.traffic.server.service.EnhancedServerSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

/**
 * GA/T 1049.2交通信号控制系统服务端应用
 * 集成GA/T 1049.1通用通信协议
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.traffic.gat1049.repository")
@EntityScan(basePackages = "com.traffic.gat1049.repository.entity")
@ComponentScan(basePackages = {
        "com.traffic.server",
        "com.traffic.gat1049", // 扫描common模块的组件
        "com.traffic.device.adapter"    // 🔥 新增：适配器包扫描
})
@Import({
        ApplicationConfig.class,
        MessageProcessingConfig.class,
        ServerConfig.class,
        ServerSubscriptionConfig.class
})
public class TrafficSignalServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(TrafficSignalServerApplication.class);

    @Autowired
    private EnhancedServerSubscriptionService subscriptionService;

    @PostConstruct
    public void initSubscriptions() {
        try {
            subscriptionService.subscribeTrafficSignalData("CLIENT001");

            // 注册自定义数据处理器
            subscriptionService.registerDataHandler((data, message) -> {
                logger.info("接收到客户端数据: {}", data.getClass().getSimpleName());
            });
        } catch (Exception e) {
            logger.warn("初始化订阅服务时出错: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TrafficSignalServerApplication.class, args);
    }
}