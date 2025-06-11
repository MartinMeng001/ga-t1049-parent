package com.traffic.server;
import com.traffic.gat1049.connection.ConnectionManager;
import com.traffic.gat1049.exception.MessageEncodingException;
import com.traffic.gat1049.handler.HandlerRegistry;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.service.abstracts.DefaultServiceFactory;
import com.traffic.gat1049.session.SessionManager;
import com.traffic.gat1049.subscription.SubscriptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * GA/T 1049.2交通信号控制系统服务端应用
 * 集成GA/T 1049.1通用通信协议
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.traffic.server",
        "com.traffic.gat1049"
})
public class TrafficSignalServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(TrafficSignalServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TrafficSignalServerApplication.class, args);
    }

    @Bean
    public DefaultServiceFactory defaultServiceFactory() {
        return new DefaultServiceFactory();
    }

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

    @Bean
    public HandlerRegistry handlerRegistry(DefaultServiceFactory serviceFactory, SessionManager sessionManager) throws MessageEncodingException {
        return new HandlerRegistry(serviceFactory, sessionManager);
    }

    @Bean
    public MessageProcessor messageProcessor(HandlerRegistry handlerRegistry) {
        return handlerRegistry.getMessageProcessor();
    }

    @Bean
    public CommandLineRunner serverRunner(
            SessionManager sessionManager,
            SubscriptionManager subscriptionManager,
            ConnectionManager connectionManager,
            MessageProcessor messageProcessor) {

        return args -> {
            logger.info("启动GA/T 1049.2交通信号控制系统服务端...");

            // 设置订阅推送回调
            subscriptionManager.setPushCallback((token, message) -> {
                logger.info("推送消息给订阅者: token={}, seq={}", token, message.getSeq());
                // 这里可以实现实际的消息推送逻辑
            });

            // 启动TCP服务器
            startTcpServer(messageProcessor);

            logger.info("GA/T 1049.2交通信号控制系统服务端启动完成");
            logger.info("TCP端口: 9999");
            logger.info("HTTP端口: 8080");
            logger.info("支持的协议版本: GA/T 1049.1-2013, GA/T 1049.2");
        };
    }

    private void startTcpServer(MessageProcessor messageProcessor) {
        // 这里启动TCP服务器来处理客户端连接
        // 实际实现需要使用Netty或其他网络框架
        logger.info("TCP服务器启动在端口 9999");
    }
}