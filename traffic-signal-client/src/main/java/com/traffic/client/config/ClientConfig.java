package com.traffic.client.config;

import com.traffic.client.network.client.GatTcpClient;

import com.traffic.client.service.ClientCommandService;
import com.traffic.client.service.ClientDemoService;
import com.traffic.client.service.ClientMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 客户端启动和运行时配置
 */
@Configuration
public class ClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    public CommandLineRunner clientRunner(
            GatTcpClient client,
            ClientMessageHandler messageHandler,
            ClientDemoService demoService,
            ClientCommandService commandService) {

        return args -> {
            logger.info("启动GA/T 1049.2交通信号控制系统客户端...");
            logger.info("支持协议: GA/T 1049.1-2013, GA/T 1049.2");

            // 连接到服务器
            client.connect();

            // 添加消息监听器
            client.getResponseHandler().addListener("gat1049", messageHandler::handleMessage);

            // 演示GA/T 1049.1标准操作
            demoService.demonstrateGat1049Operations(client);

            // 启动交互式命令行
            commandService.startInteractiveConsole(client);
        };
    }
}
