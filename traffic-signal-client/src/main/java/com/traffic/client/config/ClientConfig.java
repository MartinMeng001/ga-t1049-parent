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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 改进的方案一：支持连接中断恢复后业务功能自动恢复
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

            // 1. 只启动一次连接（不管成功失败）
            try {
                client.connect();
                logger.info("初始连接请求已发送");
            } catch (Exception e) {
                logger.warn("初始连接失败，等待自动重连: {}", e.getMessage());
            }

            // 2. 启动智能业务监控器（支持中断恢复）
            startSmartBusinessMonitor(client, messageHandler, demoService, commandService);
        };
    }

    private void startSmartBusinessMonitor(GatTcpClient client, ClientMessageHandler messageHandler,
                                           ClientDemoService demoService, ClientCommandService commandService) {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "SmartBusinessMonitor"));

        AtomicBoolean businessStarted = new AtomicBoolean(false);
        AtomicBoolean wasConnected = new AtomicBoolean(false); // 记录上次连接状态

        // 定时检查连接状态和业务功能
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                boolean currentlyConnected = client.isConnected();
                boolean previouslyConnected = wasConnected.get();

                if (currentlyConnected && !businessStarted.get()) {
                    // 场景1：连接成功但业务未启动（首次连接或重连后恢复）
                    logger.info("检测到连接已建立，启动业务功能...");

                    if (businessStarted.compareAndSet(false, true)) {
                        startAllBusinessFunctions(client, messageHandler, demoService, commandService);

                        // 区分首次连接和重连恢复
                        if (previouslyConnected) {
                            logger.info("🔄 连接已恢复，业务功能重新启动");
                        } else {
                            logger.info("🎉 首次连接成功，业务功能已启动");
                        }
                    }
                } else if (!currentlyConnected && businessStarted.get()) {
                    // 场景2：连接断开但业务仍标记为启动状态
                    logger.warn("检测到连接已断开，重置业务状态等待重连...");
                    businessStarted.set(false); // 重置业务状态
                    demoExecuted.set(false); // 重连后，重新登录
                }

                // 更新连接状态记录
                wasConnected.set(currentlyConnected);

            } catch (Exception e) {
                logger.error("智能业务监控发生错误", e);
            }
        }, 1, 2, TimeUnit.SECONDS); // 每2秒检查一次
    }

    private void startAllBusinessFunctions(GatTcpClient client, ClientMessageHandler messageHandler,
                                           ClientDemoService demoService, ClientCommandService commandService) {
        try {
            // 检查连接状态（防止在启动过程中连接断开）
            if (!client.isConnected()) {
                logger.warn("启动业务功能时发现连接已断开，取消启动");
                return;
            }

            // 1. 添加消息监听器（支持重复注册检查）
            registerMessageListener(client, messageHandler);

            // 2. 演示GA/T 1049.1标准操作（仅首次执行）
            startDemoOperations(client, demoService);

            // 3. 启动订阅
            // 4. 启动交互式命令行（仅首次执行）
            startInteractiveConsole(client, commandService);

            logger.info("✅ 所有业务功能启动完成");

        } catch (Exception e) {
            logger.error("启动业务功能失败", e);
        }
    }

    private void registerMessageListener(GatTcpClient client, ClientMessageHandler messageHandler) {
        try {
            // 消息监听器可以重复注册，ResponseHandler内部会处理重复问题
            client.getResponseHandler().addListener("gat1049", messageHandler::handleMessage);
            logger.debug("✅ 消息监听器已注册/确认");
        } catch (Exception e) {
            logger.error("注册消息监听器失败", e);
        }
    }

    private static AtomicBoolean demoExecuted = new AtomicBoolean(false);

    private void startDemoOperations(GatTcpClient client, ClientDemoService demoService) {
        // Demo操作只在首次连接时执行，重连时不重复执行
        if (demoExecuted.compareAndSet(false, true)) {
            try {
                demoService.demonstrateGat1049Operations(client);
                logger.info("✅ GA/T 1049.1演示操作已完成");
            } catch (Exception e) {
                logger.error("GA/T 1049.1演示操作失败", e);
                demoExecuted.set(false); // 失败时重置，允许下次重试
            }
        } else {
            logger.debug("Demo操作已执行过，跳过");
        }
    }

    private static AtomicBoolean consoleStarted = new AtomicBoolean(false);

    private void startInteractiveConsole(GatTcpClient client, ClientCommandService commandService) {
        // 交互式命令行只启动一次，重连时不重复启动
        if (consoleStarted.compareAndSet(false, true)) {
            try {
                commandService.startInteractiveConsole(client);
                logger.info("✅ 交互式命令行已启动");
            } catch (Exception e) {
                logger.error("启动交互式命令行失败", e);
                consoleStarted.set(false); // 失败时重置，允许下次重试
            }
        } else {
            logger.debug("交互式命令行已启动，跳过");
        }
    }
}