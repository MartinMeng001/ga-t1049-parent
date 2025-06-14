package com.traffic.server.config;

import com.traffic.gat1049.application.connection.ConnectionManager;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.server.debug.UpdatedServerDebugConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务器启动和运行时配置
 */
@Configuration
public class ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    @Bean
    public CommandLineRunner serverRunner(
            SessionManager sessionManager,
            SubscriptionManager subscriptionManager,
            ConnectionManager connectionManager,
            MessageProcessor messageProcessor,
            ApplicationContext applicationContext) {

        return args -> {
            logger.info("启动GA/T 1049.2交通信号控制系统服务端...");

            // 设置订阅推送回调
            subscriptionManager.setPushCallback((token, message) -> {
                logger.info("推送消息给订阅者: token={}, seq={}", token, message.getSeq());
                // 实际的消息推送逻辑
            });

            logger.info("GA/T 1049.2交通信号控制系统服务端启动完成");
            logger.info("TCP端口: 9999");
            logger.info("HTTP端口: 8080");
            logger.info("支持的协议版本: GA/T 1049.1-2013, GA/T 1049.2");

            // 启动调试控制台
            startDebugConsole(applicationContext);
        };
    }

    private void startDebugConsole(ApplicationContext applicationContext) {
        try {
            UpdatedServerDebugConsole debugConsole = applicationContext.getBean(UpdatedServerDebugConsole.class);
            // 延迟启动调试控制台
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    debugConsole.startConsole();
                } catch (InterruptedException e) {
                    logger.error("启动调试控制台时被中断", e);
                }
            }, "Debug-Console-Starter").start();

            logger.info("调试控制台将在2秒后启动...");
        } catch (Exception e) {
            logger.warn("无法启动调试控制台: {}", e.getMessage());
            logger.info("服务端将在没有调试控制台的情况下继续运行");
        }
    }
}