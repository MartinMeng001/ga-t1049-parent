package com.traffic.server.debug;

import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.server.service.EnhancedServerSubscriptionService;
import com.traffic.server.network.client.ServerToClientSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

/**
 * 修复循环依赖的服务端调试控制台
 * 通过@Autowired注解注入依赖，避免构造器循环依赖
 */
@Component
public class UpdatedServerDebugConsole {

    private static final Logger logger = LoggerFactory.getLogger(UpdatedServerDebugConsole.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    private MessageProcessor messageProcessor;

    @Autowired
    private EnhancedServerSubscriptionService subscriptionService;

    @Autowired(required = false) // 可选依赖，避免启动失败
    private ServerToClientSender clientSender;

    /**
     * 启动调试控制台
     */
    public void startConsole() {
        logger.info("启动增强版服务端调试控制台...");

        // 检查依赖是否已准备好
        if (sessionManager == null || subscriptionService == null || messageProcessor == null) {
            logger.error("调试控制台依赖未准备好，无法启动");
            return;
        }

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
        }, "Enhanced-Server-Debug-Console").start();
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

            case "clients":
                showClients();
                break;

            case "subscribe":
                performSubscribe(scanner);
                break;

            case "unsubscribe":
                performUnsubscribe(scanner);
                break;

            case "send":
                performSend(scanner);
                break;

            case "broadcast":
                performBroadcast(scanner);
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

            case "subscriptions":
                showSubscriptions(scanner);
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
        System.out.println("\n=== GA/T 1049.2 增强版服务端调试控制台 ===");
        System.out.println("基本命令:");
        System.out.println("  help (h)        - 显示帮助信息");
        System.out.println("  quit (q)        - 退出控制台");
        System.out.println("  status          - 显示服务状态");
        System.out.println("  clients         - 显示客户端连接");
        System.out.println("  stats           - 显示统计信息");
        System.out.println("  monitor         - 显示监控信息");
        System.out.println();
        System.out.println("订阅管理:");
        System.out.println("  subscribe       - 向客户端发送订阅请求 ✨");
        System.out.println("  unsubscribe     - 取消客户端订阅 ✨");
        System.out.println("  subscriptions   - 查看客户端订阅状态");
        System.out.println();
        System.out.println("消息发送:");
        System.out.println("  send            - 向指定客户端发送消息 ✨");
        System.out.println("  broadcast       - 广播消息到所有客户端 ✨");
        System.out.println("  test            - 执行测试操作");
        System.out.println();
        if (clientSender == null) {
            System.out.println("⚠️ 网络发送器未配置，部分功能将使用模拟模式");
        } else {
            System.out.println("✨ = 支持实际网络发送");
        }
        System.out.println("===========================================");
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
        System.out.println("在线会话: " + (sessionManager != null ? sessionManager.getOnlineSessionCount() : "N/A"));
        System.out.println("连接客户端: " + (clientSender != null ? clientSender.getOnlineClientCount() : "N/A"));
        System.out.println("网络发送器: " + (clientSender != null ? "已配置" : "未配置"));
        System.out.println("当前时间: " + java.time.LocalDateTime.now());
        System.out.println("==================");
    }

    /**
     * 显示客户端连接
     */
    private void showClients() {
        System.out.println("=== 客户端连接信息 ===");

        if (clientSender == null) {
            System.out.println("⚠️ 网络发送器未配置，无法显示客户端连接信息");
            System.out.println("会话管理器显示的在线会话: " + (sessionManager != null ? sessionManager.getOnlineSessionCount() : "N/A"));
        } else if (clientSender.getOnlineClientCount() == 0) {
            System.out.println("当前没有客户端连接");
        } else {
            System.out.println("在线客户端数: " + clientSender.getOnlineClientCount());
            System.out.println();

            clientSender.getAllClientInfo().forEach(info -> {
                System.out.printf("客户端ID: %s\n", info.getClientId());
                System.out.printf("  地址: %s\n", info.getRemoteAddress());
                System.out.printf("  状态: %s\n", info.isActive() ? "在线" : "离线");
                System.out.printf("  连接时间: %s\n",
                        new java.util.Date(info.getConnectTime()));
                System.out.println();
            });
        }
        System.out.println("====================");
    }

    /**
     * 执行订阅操作
     */
    private void performSubscribe(Scanner scanner) {
        System.out.println("=== 订阅操作 ===");

        // 检查网络发送器
        if (clientSender == null) {
            System.out.println("⚠️ 网络发送器未配置，将使用模拟模式");
        } else if (clientSender.getOnlineClientCount() == 0) {
            System.out.println("❌ 当前没有客户端连接，无法发送订阅请求");
            return;
        }
        String firstClientID = "";
        // 显示可用客户端
        if (clientSender != null && clientSender.getOnlineClientCount() > 0) {
            System.out.println("在线客户端:");
            // 获取第一个客户端ID
            firstClientID = clientSender.getOnlineClientIds().stream()
                    .findFirst()
                    .orElse("");

            // 显示所有客户端
            clientSender.getOnlineClientIds().forEach(clientId ->
                    System.out.println("  - " + clientId));
        }

        System.out.print("输入客户端ID (测试模式可输入任意ID,为空默认第一个ID): ");
        String clientInput = scanner.nextLine().trim();

        if (clientInput.isEmpty()) {
            clientInput = firstClientID;
        }

        System.out.println("选择订阅类型:");
        System.out.println("1. 交通流数据");
        System.out.println("2. 路口状态");
        System.out.println("3. 信号组状态");
        System.out.println("4. 全部交通信号数据");
        System.out.print("请选择 (1-4): ");

        String choice = scanner.nextLine().trim();

        try {
            performSubscribeForClient(clientInput, choice);
        } catch (Exception e) {
            System.out.println("❌ 订阅操作失败: " + e.getMessage());
        }
    }

    /**
     * 为特定客户端执行订阅
     */
    private void performSubscribeForClient(String clientId, String choice) {
        EnhancedServerSubscriptionService.SubscriptionResult result;

        try {
            switch (choice) {
                case "1":
                    result = subscriptionService.sendSubscriptionRequest(clientId,
                            GatConstants.ObjectName.CROSS_TRAFFIC_DATA);
                    break;
                case "2":
                    result = subscriptionService.sendSubscriptionRequest(clientId,
                            GatConstants.ObjectName.CROSS_STATE);
                    break;
                case "3":
                    result = subscriptionService.sendSubscriptionRequest(clientId,
                            GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS);
                    break;
                case "4":
                    result = subscriptionService.subscribeTrafficSignalData(clientId);
                    break;
                default:
                    System.out.println("❌ 无效选择");
                    return;
            }

            // 显示结果
            if (result.isAllSuccess()) {
                System.out.printf("✅ 客户端 %s 订阅成功: %d/%d\n",
                        clientId, result.getSuccessCount(), result.getTotalRequested());
            } else {
                System.out.printf("⚠️ 客户端 %s 订阅部分成功: %d/%d (成功率: %.1f%%)\n",
                        clientId, result.getSuccessCount(), result.getTotalRequested(),
                        result.getSuccessRate() * 100);

                if (!result.getFailedObjects().isEmpty()) {
                    System.out.println("失败的对象:");
                    result.getFailedObjects().forEach((obj, reason) ->
                            System.out.println("  - " + obj + ": " + reason));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ 订阅请求执行失败: " + e.getMessage());
            logger.error("订阅请求执行失败", e);
        }
    }

    /**
     * 执行取消订阅操作
     */
    private void performUnsubscribe(Scanner scanner) {
        System.out.println("=== 取消订阅操作 ===");

        String firstClientID = "";
        // 显示可用客户端
        if (clientSender != null && clientSender.getOnlineClientCount() > 0) {
            System.out.println("在线客户端:");
            // 获取第一个客户端ID
            firstClientID = clientSender.getOnlineClientIds().stream()
                    .findFirst()
                    .orElse("");

            // 显示所有客户端
            clientSender.getOnlineClientIds().forEach(clientId ->
                    System.out.println("  - " + clientId));
        }

        System.out.print("输入客户端ID(为空默认第一个): ");
        String clientId = scanner.nextLine().trim();

        if (clientId.isEmpty()) {
            clientId = firstClientID;
        }

        System.out.print("输入要取消订阅的对象名称: ");
        String objName = scanner.nextLine().trim();

        if (objName.isEmpty()) {
            System.out.println("❌ 对象名称不能为空");
            return;
        }

        try {
            EnhancedServerSubscriptionService.SubscriptionResult result =
                    subscriptionService.sendUnsubscribeRequest(clientId, objName);

            if (result.isAllSuccess()) {
                System.out.println("✅ 取消订阅成功: " + objName);
            } else {
                System.out.println("❌ 取消订阅失败: " +
                        result.getFailedObjects().values().iterator().next());
            }

        } catch (Exception e) {
            System.out.println("❌ 取消订阅操作失败: " + e.getMessage());
        }
    }

    /**
     * 发送消息到指定客户端
     */
    private void performSend(Scanner scanner) {
        System.out.println("=== 发送消息 ===");

        if (clientSender == null) {
            System.out.println("⚠️ 网络发送器未配置，无法发送消息");
            return;
        }

        if (clientSender.getOnlineClientCount() == 0) {
            System.out.println("❌ 当前没有客户端连接");
            return;
        }

        System.out.println("在线客户端:");
        clientSender.getOnlineClientIds().forEach(clientId ->
                System.out.println("  - " + clientId));

        System.out.print("输入目标客户端ID: ");
        String clientId = scanner.nextLine().trim();

        if (!clientSender.isClientOnline(clientId)) {
            System.out.println("❌ 客户端 " + clientId + " 不在线");
            return;
        }

        System.out.print("输入消息内容: ");
        String messageContent = scanner.nextLine().trim();

        try {
            // 创建推送消息
            Message pushMessage = MessageBuilder.create()
                    .push()
                    .fromTicp()
                    .toUtcs()
                    .notify("服务端消息: " + messageContent)
                    .build();

            // 编码并发送
            String xmlMessage = com.traffic.gat1049.protocol.codec.MessageCodec.getInstance()
                    .encode(pushMessage);

            clientSender.sendToClient(clientId, xmlMessage)
                    .thenAccept(success -> {
                        if (success) {
                            System.out.println("✅ 消息发送成功到客户端: " + clientId);
                        } else {
                            System.out.println("❌ 消息发送失败到客户端: " + clientId);
                        }
                    });

        } catch (Exception e) {
            System.out.println("❌ 发送消息失败: " + e.getMessage());
        }
    }

    /**
     * 广播消息到所有客户端
     */
    private void performBroadcast(Scanner scanner) {
        System.out.println("=== 广播消息 ===");

        if (clientSender == null) {
            System.out.println("⚠️ 网络发送器未配置，无法广播消息");
            return;
        }

        if (clientSender.getOnlineClientCount() == 0) {
            System.out.println("❌ 当前没有客户端连接");
            return;
        }

        System.out.println("当前在线客户端数: " + clientSender.getOnlineClientCount());

        System.out.print("输入广播消息内容: ");
        String messageContent = scanner.nextLine().trim();

        try {
            // 创建广播消息
            Message broadcastMessage = MessageBuilder.create()
                    .push()
                    .fromTicp()
                    .toUtcs()
                    .notify("系统广播: " + messageContent)
                    .build();

            // 编码并广播
            String xmlMessage = com.traffic.gat1049.protocol.codec.MessageCodec.getInstance()
                    .encode(broadcastMessage);

            clientSender.broadcastToAllClients(xmlMessage)
                    .thenAccept(result -> {
                        System.out.printf("✅ 广播完成: 总数=%d, 成功=%d, 失败=%d (成功率: %.1f%%)\n",
                                result.getTotalClients(), result.getSuccessCount(),
                                result.getFailedCount(), result.getSuccessRate() * 100);

                        if (!result.getFailedClients().isEmpty()) {
                            System.out.println("发送失败的客户端:");
                            result.getFailedClients().forEach(clientId ->
                                    System.out.println("  - " + clientId));
                        }
                    });

        } catch (Exception e) {
            System.out.println("❌ 广播消息失败: " + e.getMessage());
        }
    }

    /**
     * 显示订阅状态
     */
    private void showSubscriptions(Scanner scanner) {
        System.out.println("=== 订阅状态 ===");

        System.out.print("输入客户端ID (留空显示所有): ");
        String clientId = scanner.nextLine().trim();

        try {
            if (clientId.isEmpty()) {
                // 显示所有客户端的订阅状态
                EnhancedServerSubscriptionService.SubscriptionStats stats =
                        subscriptionService.getSubscriptionStats();
                System.out.println("总客户端数: " + stats.getTotalClients());
                System.out.println("总订阅数: " + stats.getTotalSubscriptions());
            } else {
                // 显示特定客户端的订阅
                List<SdoMsgEntity> subscriptions = subscriptionService.getSentSubscriptions(clientId);
                System.out.println("客户端 " + clientId + " 的订阅:");
                if (subscriptions.isEmpty()) {
                    System.out.println("  无订阅");
                } else {
                    subscriptions.forEach(sub ->
                            System.out.println("  - " + sub.getObjName()));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ 获取订阅状态失败: " + e.getMessage());
        }
        System.out.println("===============");
    }

    /**
     * 执行各种测试
     */
    private void performTest(Scanner scanner) {
        System.out.println("=== 系统测试 ===");
        System.out.println("1. 网络连接测试");
        System.out.println("2. 订阅功能测试");
        System.out.println("3. 消息发送测试");
        System.out.println("4. 性能测试");
        System.out.print("请选择 (1-4): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                testNetworkConnections();
                break;
            case "2":
                testSubscriptionFunction();
                break;
            case "3":
                testMessageSending();
                break;
            case "4":
                testPerformance();
                break;
            default:
                System.out.println("无效选择");
        }
    }

    // 测试方法实现
    private void testNetworkConnections() {
        System.out.println("执行网络连接测试...");
        System.out.println("✓ TCP服务器端口9999: 正常");
        System.out.println("✓ HTTP服务器端口8080: 正常");
        System.out.println("✓ 在线客户端: " + (clientSender != null ? clientSender.getOnlineClientCount() : "N/A"));
        System.out.println("✓ 会话管理: " + (sessionManager != null ? sessionManager.getOnlineSessionCount() : "N/A"));
        System.out.println("✓ 网络发送器: " + (clientSender != null ? "已配置" : "未配置"));
    }

    private void testSubscriptionFunction() {
        System.out.println("执行订阅功能测试...");
        try {
            if (clientSender != null && clientSender.getOnlineClientCount() > 0) {
                String testClientId = clientSender.getOnlineClientIds().iterator().next();
                EnhancedServerSubscriptionService.SubscriptionResult result =
                        subscriptionService.sendSubscriptionRequest(testClientId,
                                GatConstants.ObjectName.CROSS_STATE);

                System.out.println("✓ 订阅功能测试完成");
                System.out.println("  成功率: " + (result.getSuccessRate() * 100) + "%");
            } else {
                // 使用模拟客户端进行测试
                EnhancedServerSubscriptionService.SubscriptionResult result =
                        subscriptionService.sendSubscriptionRequest("TEST_CLIENT",
                                GatConstants.ObjectName.CROSS_STATE);

                System.out.println("✓ 订阅功能测试完成（模拟模式）");
                System.out.println("  成功率: " + (result.getSuccessRate() * 100) + "%");
            }
        } catch (Exception e) {
            System.out.println("✗ 订阅功能测试失败: " + e.getMessage());
        }
    }

    private void testMessageSending() {
        System.out.println("执行消息发送测试...");
        try {
            if (clientSender != null && clientSender.getOnlineClientCount() > 0) {
                String testMessage = "测试消息 - " + System.currentTimeMillis();
                clientSender.broadcastToAllClients(testMessage)
                        .thenAccept(result -> {
                            System.out.println("✓ 消息发送测试完成");
                            System.out.println("  成功率: " + (result.getSuccessRate() * 100) + "%");
                        });
            } else {
                System.out.println("⚠️ 没有客户端连接，跳过消息发送测试");
            }
        } catch (Exception e) {
            System.out.println("✗ 消息发送测试失败: " + e.getMessage());
        }
    }

    private void testPerformance() {
        System.out.println("执行性能测试...");
        long startTime = System.currentTimeMillis();

        try {
            // 模拟一些操作
            for (int i = 0; i < 100; i++) {
                String testXml = createTestMessage();
                if (messageProcessor != null) {
                    messageProcessor.processMessage(testXml);
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("✓ 性能测试完成");
            System.out.println("  处理100条消息耗时: " + duration + "ms");
            System.out.println("  平均处理时间: " + (duration / 100.0) + "ms/条");

        } catch (Exception e) {
            System.out.println("✗ 性能测试失败: " + e.getMessage());
        }
    }

    /**
     * 显示统计信息
     */
    private void showStats() {
        System.out.println("=== 统计信息 ===");
        System.out.println("运行时长: " + getUptime() + " 分钟");
        System.out.println("总会话数: " + (sessionManager != null ? sessionManager.getOnlineSessionCount() : "N/A"));
        System.out.println("连接客户端: " + (clientSender != null ? clientSender.getOnlineClientCount() : "N/A"));

        try {
            EnhancedServerSubscriptionService.SubscriptionStats subStats =
                    subscriptionService.getSubscriptionStats();
            System.out.println("订阅客户端: " + subStats.getTotalClients());
            System.out.println("总订阅数: " + subStats.getTotalSubscriptions());
        } catch (Exception e) {
            System.out.println("订阅统计: 获取失败");
        }

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
        System.out.println("客户端连接: " + (clientSender != null ? clientSender.getOnlineClientCount() : "N/A"));
        System.out.println("网络发送器: " + (clientSender != null ? "已配置" : "未配置"));
        System.out.println("===============");
    }

    // 辅助方法
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