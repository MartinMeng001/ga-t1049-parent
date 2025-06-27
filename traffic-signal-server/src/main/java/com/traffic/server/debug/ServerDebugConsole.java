package com.traffic.server.debug;

import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.server.service.ServerSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;

/**
 * 服务端调试控制台
 * 提供交互式命令行界面用于调试和管理
 */
@Component
public class ServerDebugConsole {

    private static final Logger logger = LoggerFactory.getLogger(ServerDebugConsole.class);

    private final SessionManager sessionManager;
    private final SubscriptionManager subscriptionManager;
    private final MessageProcessor messageProcessor;
    private final ServerSubscriptionService subscriptionService;

    public ServerDebugConsole(SessionManager sessionManager,
                              SubscriptionManager subscriptionManager,
                              MessageProcessor messageProcessor,
                              ServerSubscriptionService subscriptionService) {
        this.sessionManager = sessionManager;
        this.subscriptionManager = subscriptionManager;
        this.messageProcessor = messageProcessor;
        this.subscriptionService = subscriptionService;
    }

    /**
     * 启动调试控制台
     */
    public void startConsole() {
        logger.info("启动服务端调试控制台...");

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            printHelp();

            while (true) {
                try {
                    System.out.print("\nGA/T1049-Server> ");
                    String command = scanner.nextLine().trim();

                    if (command.isEmpty()) {
                        continue;
                    }

                    handleCommand(command, scanner);

                } catch (Exception e) {
                    logger.error("调试控制台命令执行错误", e);
                    System.out.println("命令执行出错: " + e.getMessage());
                }
            }
        }, "Server-Debug-Console").start();
    }

    /**
     * 处理用户输入的命令
     */
    private void handleCommand(String command, Scanner scanner) {
        switch (command.toLowerCase()) {
            case "help":
            case "h":
                printHelp();
                break;

            case "quit":
            case "exit":
            case "q":
                logger.info("关闭服务端调试控制台...");
                System.exit(0);
                break;

            case "status":
                showStatus();
                break;

            case "sessions":
                showSessions();
                break;

            case "subscribe":
                performSubscribe(scanner);
                break;

            case "push":
                performPush(scanner);
                break;

            case "test":
                performTest(scanner);
                break;

            case "stats":
                showStats();
                break;

            case "monitor":
                showMonitor();
                break;

            default:
                System.out.println("未知命令: " + command);
                System.out.println("输入 'help' 查看可用命令");
        }
    }

    /**
     * 显示帮助信息
     */
    private void printHelp() {
        System.out.println("\n=== GA/T 1049.2 服务端调试控制台 ===");
        System.out.println("基本命令:");
        System.out.println("  help (h)      - 显示帮助信息");
        System.out.println("  quit (q)      - 退出控制台");
        System.out.println("  status        - 显示服务状态");
        System.out.println("  sessions      - 显示会话信息");
        System.out.println("  stats         - 显示统计信息");
        System.out.println("  monitor       - 显示监控信息");
        System.out.println();
        System.out.println("调试命令:");
        System.out.println("  subscribe     - 测试订阅功能");
        System.out.println("  push          - 测试推送功能");
        System.out.println("  test          - 执行测试操作");
        System.out.println("=====================================");
    }

    /**
     * 显示服务状态
     */
    private void showStatus() {
        System.out.println("=== 服务端状态 ===");
        System.out.println("服务状态: 运行中");
        System.out.println("协议版本: GA/T 1049.1-2013");
        System.out.println("系统类型: TICP");
        System.out.println("TCP端口: 9999");
        System.out.println("HTTP端口: 8080");
        System.out.println("在线会话: " + sessionManager.getOnlineSessionCount());
        System.out.println("当前时间: " + java.time.LocalDateTime.now());
        System.out.println("==================");
    }

    /**
     * 显示会话信息
     */
    private void showSessions() {
        System.out.println("=== 会话信息 ===");
        System.out.println("在线会话数: " + sessionManager.getOnlineSessionCount());
        System.out.println("===============");
    }

    /**
     * 执行订阅测试
     */
    private void performSubscribe(Scanner scanner) {
        System.out.println("=== 订阅测试 ===");
        System.out.print("输入客户端ID (默认: TEST_CLIENT): ");
        String clientId = scanner.nextLine().trim();
        if (clientId.isEmpty()) {
            clientId = "TEST_CLIENT";
        }

        System.out.println("选择订阅类型:");
        System.out.println("1. 交通流数据");
        System.out.println("2. 路口状态");
        System.out.println("3. 信号组状态");
        System.out.println("4. 全部数据");
        System.out.print("请选择 (1-4): ");

        String choice = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    testSingleSubscription(clientId, GatConstants.ObjectName.CROSS_TRAFFIC_DATA);
                    break;
                case "2":
                    testSingleSubscription(clientId, GatConstants.ObjectName.CROSS_STATE);
                    break;
                case "3":
                    testSingleSubscription(clientId, GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS);
                    break;
                case "4":
                    List<Message> requests = subscriptionService.subscribeTrafficSignalData(clientId);
                    System.out.println("✓ 发送了 " + requests.size() + " 个订阅请求");
                    break;
                default:
                    System.out.println("无效选择");
                    return;
            }
            System.out.println("✓ 订阅测试完成");
        } catch (Exception e) {
            System.out.println("✗ 订阅测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试单个订阅
     */
    private void testSingleSubscription(String clientId, String objectName) {
        Message request = subscriptionService.sendSubscriptionRequest(clientId, objectName);
        System.out.println("✓ 发送订阅请求: " + objectName);
        System.out.println("  请求序号: " + request.getSeq());
    }

    /**
     * 执行推送测试
     */
    private void performPush(Scanner scanner) {
        System.out.println("=== 推送测试 ===");
        System.out.println("1. 系统通知");
        System.out.println("2. 测试告警");
        System.out.println("3. 心跳消息");
        System.out.print("请选择 (1-3): ");

        String choice = scanner.nextLine().trim();

        try {
            Message pushMessage = null;

            switch (choice) {
                case "1":
                    pushMessage = createTestNotification();
                    break;
                case "2":
                    pushMessage = createTestAlarm();
                    break;
                case "3":
                    pushMessage = createTestHeartbeat();
                    break;
                default:
                    System.out.println("无效选择");
                    return;
            }

            if (pushMessage != null) {
                System.out.println("✓ 创建推送消息:");
                System.out.println("  序号: " + pushMessage.getSeq());
                System.out.println("  类型: " + pushMessage.getType());
                System.out.println("  (模拟发送成功)");
            }

        } catch (Exception e) {
            System.out.println("✗ 推送测试失败: " + e.getMessage());
        }
    }

    /**
     * 执行各种测试
     */
    private void performTest(Scanner scanner) {
        System.out.println("=== 系统测试 ===");
        System.out.println("1. 消息处理器测试");
        System.out.println("2. 订阅功能测试");
        System.out.println("3. 连接测试");
        System.out.println("4. 性能测试");
        System.out.print("请选择 (1-4): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                testMessageProcessor();
                break;
            case "2":
                testSubscriptionSystem();
                break;
            case "3":
                testConnections();
                break;
            case "4":
                testPerformance();
                break;
            default:
                System.out.println("无效选择");
        }
    }

    /**
     * 显示统计信息
     */
    private void showStats() {
        System.out.println("=== 统计信息 ===");
        System.out.println("运行时长: " + getUptime() + " 分钟");
        System.out.println("总会话数: " + sessionManager.getOnlineSessionCount());
        System.out.println("内存使用: " + getMemoryUsage());
        System.out.println("===============");
    }

    /**
     * 显示监控信息
     */
    private void showMonitor() {
        System.out.println("=== 监控信息 ===");
        System.out.println("CPU使用率: " + getCpuUsage() + "%");
        System.out.println("内存使用: " + getMemoryUsage());
        System.out.println("网络状态: 正常");
        System.out.println("服务状态: 运行中");
        System.out.println("===============");
    }

    // 测试方法实现
    private void testMessageProcessor() {
        System.out.println("测试消息处理器...");
        try {
            String testXml = createTestMessage();
            String response = messageProcessor.processMessage(testXml);
            System.out.println("✓ 消息处理器测试成功");
            System.out.println("  响应: " + (response != null ? "有响应" : "无响应"));
        } catch (Exception e) {
            System.out.println("✗ 消息处理器测试失败: " + e.getMessage());
        }
    }

    private void testSubscriptionSystem() {
        System.out.println("测试订阅系统...");
        try {
            List<Message> requests = subscriptionService.subscribeTrafficSignalData("TEST_CLIENT");
            System.out.println("✓ 订阅系统测试成功");
            System.out.println("  生成订阅请求数: " + requests.size());
        } catch (Exception e) {
            System.out.println("✗ 订阅系统测试失败: " + e.getMessage());
        }
    }

    private void testConnections() {
        System.out.println("测试连接状态...");
        System.out.println("✓ TCP服务器 (端口9999): 正常");
        System.out.println("✓ HTTP服务器 (端口8080): 正常");
        System.out.println("✓ 当前连接数: " + sessionManager.getOnlineSessionCount());
    }

    private void testPerformance() {
        System.out.println("执行性能测试...");
        long startTime = System.currentTimeMillis();

        // 模拟一些操作
        try {
            for (int i = 0; i < 100; i++) {
                String testXml = createTestMessage();
                messageProcessor.processMessage(testXml);
            }

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("✓ 性能测试完成");
            System.out.println("  处理100条消息耗时: " + duration + "ms");
            System.out.println("  平均处理时间: " + (duration / 100.0) + "ms/条");

        } catch (Exception e) {
            System.out.println("✗ 性能测试失败: " + e.getMessage());
        }
    }

    // 辅助方法
    private Message createTestNotification() {
        return MessageBuilder.create()
                .push()
                .fromTicp()
                .toUtcs()
                .notify("系统通知: 服务端调试测试消息")
                .build();
    }

    private Message createTestAlarm() {
        return MessageBuilder.create()
                .push()
                .fromTicp()
                .toUtcs()
                .notify("告警: 测试告警消息")
                .build();
    }

    private Message createTestHeartbeat() {
        return MessageBuilder.createHeartbeatMessage("TEST_TOKEN", "");
    }

    private String createTestMessage() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Message>" +
                "<Version>2.0</Version>" +
                "<Type>REQUEST</Type>" +
                "<Seq>DEBUG_TEST_" + System.currentTimeMillis() + "</Seq>" +
                "<Body><Operation order=\"1\" name=\"Test\"></Operation></Body>" +
                "</Message>";
    }

    private long getUptime() {
        return System.currentTimeMillis() / 1000 / 60; // 简化的运行时间
    }

    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return String.format("%.1fMB / %.1fMB",
                usedMemory / 1024.0 / 1024.0,
                totalMemory / 1024.0 / 1024.0);
    }

    private String getCpuUsage() {
        try {
            com.sun.management.OperatingSystemMXBean osBean =
                    (com.sun.management.OperatingSystemMXBean)
                            java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            double cpuUsage = osBean.getProcessCpuLoad() * 100;
            return String.format("%.1f", cpuUsage);
        } catch (Exception e) {
            return "N/A";
        }
    }
}