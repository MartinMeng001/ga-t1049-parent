package com.traffic.client;

import com.traffic.client.network.client.GatTcpClient;
import com.traffic.gat1049.model.entity.sdo.*;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * GA/T 1049.2交通信号控制系统客户端应用
 * 支持GA/T 1049.1通用通信协议
 */
@SpringBootApplication
public class TrafficSignalClientApplication {

    private static final Logger logger = LoggerFactory.getLogger(TrafficSignalClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TrafficSignalClientApplication.class, args);
    }

    @Bean
    public GatTcpClient gatTcpClient(Environment env) throws Exception {
        String host = env.getProperty("tcp.client.server-host", "localhost");
        int port = env.getProperty("tcp.client.server-port", Integer.class, 9999);
        String clientId = env.getProperty("client.cross-id", "110100001");

        return new GatTcpClient(host, port, clientId);
    }

    @Bean
    public CommandLineRunner clientRunner(GatTcpClient client) {
        return args -> {
            logger.info("启动GA/T 1049.2交通信号控制系统客户端...");
            logger.info("支持协议: GA/T 1049.1-2013, GA/T 1049.2");

            // 连接到服务器
            client.connect();

            // 添加消息监听器
            client.getResponseHandler().addListener("gat1049", message -> {
                logger.info("收到GA/T 1049消息: type={}, seq={}", message.getType(), message.getSeq());
                handleReceivedMessage(message);
            });

            // 演示GA/T 1049.1标准操作
            demonstrateGat1049Operations(client);

            // 启动交互式命令行
            startInteractiveConsole(client);
        };
    }

    /**
     * 演示GA/T 1049.1标准操作
     */
    private void demonstrateGat1049Operations(GatTcpClient client) {
        logger.info("\n=== 演示GA/T 1049.1标准操作 ===");

        try {
            // 1. 登录操作
            Message loginRequest = MessageBuilder.createLoginRequest("tsc_client", "tsc123");
            Message loginResponse = client.sendRequest(loginRequest, 10, TimeUnit.SECONDS);

            if (loginResponse != null && "RESPONSE".equals(loginResponse.getType())) {
                String token = loginResponse.getToken();
                logger.info("登录成功，获得Token: {}", token);

                // 2. 订阅操作
                Message subscribeRequest = MessageBuilder.createSubscribeRequest(
                        token, "PUSH", "Notify", "CrossSignalGroupStatus");
                Message subscribeResponse = client.sendRequest(subscribeRequest, 10, TimeUnit.SECONDS);

                if (subscribeResponse != null && "RESPONSE".equals(subscribeResponse.getType())) {
                    logger.info("订阅成功");
                }

                // 3. 查询对时服务器
                SdoTimeServer timeServerQuery = new SdoTimeServer("", "", null);
                Message queryRequest = MessageBuilder.createQueryRequest(token, timeServerQuery);
                Message queryResponse = client.sendRequest(queryRequest, 10, TimeUnit.SECONDS);

                if (queryResponse != null) {
                    logger.info("查询响应: {}", queryResponse.getType());
                }

                // 4. 设置超时时间
                SdoTimeOut timeout = new SdoTimeOut(60);
                Message setRequest = MessageBuilder.createSetRequest(token, timeout);
                Message setResponse = client.sendRequest(setRequest, 10, TimeUnit.SECONDS);

                if (setResponse != null) {
                    logger.info("设置响应: {}", setResponse.getType());
                }

            } else {
                logger.error("登录失败");
            }

        } catch (Exception e) {
            logger.error("演示GA/T 1049.1操作失败", e);
        }
    }

    /**
     * 处理收到的消息
     */
    private void handleReceivedMessage(Message message) {
        String messageType = message.getType();

        switch (messageType) {
            case "PUSH":
                handlePushMessage(message);
                break;
            case "ERROR":
                handleErrorMessage(message);
                break;
            default:
                logger.debug("处理消息: type={}", messageType);
        }
    }

    /**
     * 处理推送消息
     */
    private void handlePushMessage(Message message) {
        logger.info("收到推送消息: seq={}", message.getSeq());
        // 这里处理服务端主动推送的消息
    }

    /**
     * 处理错误消息
     */
    private void handleErrorMessage(Message message) {
        logger.warn("收到错误消息: seq={}", message.getSeq());
        // 这里处理错误响应
    }

    private void startInteractiveConsole(GatTcpClient client) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            printGat1049Help();

            while (client.isConnected()) {
                try {
                    System.out.print("\nGA/T1049> ");
                    String command = scanner.nextLine().trim();

                    if (command.isEmpty()) {
                        continue;
                    }

                    switch (command.toLowerCase()) {
                        case "help":
                        case "h":
                            printGat1049Help();
                            break;

                        case "quit":
                        case "exit":
                        case "q":
                            logger.info("退出客户端...");
                            client.disconnect();
                            System.exit(0);
                            break;

                        case "status":
                            showGat1049Status(client);
                            break;

                        case "login":
                            performGat1049Login(client, scanner);
                            break;

                        case "subscribe":
                            performGat1049Subscribe(client, scanner);
                            break;

                        case "heartbeat":
                            performGat1049Heartbeat(client);
                            break;

                        case "query":
                            performGat1049Query(client, scanner);
                            break;

                        case "timeout":
                            performGat1049SetTimeout(client, scanner);
                            break;

                        // 原有的交通信号控制命令
                        case "sysinfo":
                            querySysInfo(client);
                            break;

                        case "crossstate":
                            queryCrossState(client, scanner);
                            break;

                        default:
                            System.out.println("未知命令: " + command);
                            System.out.println("输入 'help' 查看可用命令");
                    }

                } catch (Exception e) {
                    logger.error("命令执行错误", e);
                }
            }

            scanner.close();
        }, "Console-Thread").start();
    }

    private void printGat1049Help() {
        System.out.println("\n=== GA/T 1049.1/1049.2 客户端命令 ===");
        System.out.println("GA/T 1049.1 通用操作:");
        System.out.println("  help (h)       - 显示帮助信息");
        System.out.println("  quit (q)       - 退出客户端");
        System.out.println("  status         - 显示连接状态");
        System.out.println("  login          - 手动登录");
        System.out.println("  subscribe      - 订阅消息");
        System.out.println("  heartbeat      - 发送心跳");
        System.out.println("  query          - 查询对时服务器");
        System.out.println("  timeout        - 设置超时时间");
        System.out.println();
        System.out.println("GA/T 1049.2 交通信号控制:");
        System.out.println("  sysinfo        - 查询系统信息");
        System.out.println("  crossstate     - 查询路口状态");
        System.out.println("==========================================");
    }

    private void showGat1049Status(GatTcpClient client) {
        System.out.println("=== GA/T 1049 连接状态 ===");
        System.out.println("连接状态: " + (client.isConnected() ? "已连接" : "未连接"));
        System.out.println("协议版本: GA/T 1049.1-2013");
        System.out.println("系统类型: UTCS (交通信号控制系统)");
        System.out.println("当前时间: " + java.time.LocalDateTime.now());
    }

    private void performGat1049Login(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("用户名: ");
        String username = scanner.nextLine().trim();
        System.out.print("密码: ");
        String password = scanner.nextLine().trim();

        Message loginRequest = MessageBuilder.createLoginRequest(username, password);
        Message response = client.sendRequest(loginRequest, 10, TimeUnit.SECONDS);

        if (response != null) {
            if ("RESPONSE".equals(response.getType())) {
                System.out.println("登录成功，Token: " + response.getToken());
            } else {
                System.out.println("登录失败: " + response.getType());
            }
        } else {
            System.out.println("登录请求超时");
        }
    }

    private void performGat1049Subscribe(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("消息类型 (REQUEST/RESPONSE/PUSH/ERROR): ");
        String msgType = scanner.nextLine().trim();
        System.out.print("操作名称 (Get/Set/Notify/Other): ");
        String operName = scanner.nextLine().trim();
        System.out.print("对象名称: ");
        String objName = scanner.nextLine().trim();

        // 这里需要一个有效的token，实际应用中应该从登录响应中获取
        String token = "sample_token";
        Message subscribeRequest = MessageBuilder.createSubscribeRequest(token, msgType, operName, objName);
        Message response = client.sendRequest(subscribeRequest, 10, TimeUnit.SECONDS);

        if (response != null) {
            System.out.println("订阅响应: " + response.getType());
        } else {
            System.out.println("订阅请求超时");
        }
    }

    private void performGat1049Heartbeat(GatTcpClient client) throws Exception {
        String token = "sample_token";
        Message heartbeat = MessageBuilder.createHeartbeatMessage(token);
        client.sendMessage(heartbeat);
        System.out.println("心跳已发送");
    }

    private void performGat1049Query(GatTcpClient client, Scanner scanner) throws Exception {
        String token = "sample_token";
        SdoTimeServer timeServerQuery = new SdoTimeServer("", "", null);
        Message queryRequest = MessageBuilder.createQueryRequest(token, timeServerQuery);
        Message response = client.sendRequest(queryRequest, 10, TimeUnit.SECONDS);

        if (response != null) {
            System.out.println("查询响应: " + response.getType());
        } else {
            System.out.println("查询请求超时");
        }
    }

    private void performGat1049SetTimeout(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("超时时间（秒）: ");
        int timeoutSeconds = Integer.parseInt(scanner.nextLine().trim());

        String token = "sample_token";
        SdoTimeOut timeout = new SdoTimeOut(timeoutSeconds);
        Message setRequest = MessageBuilder.createSetRequest(token, timeout);
        Message response = client.sendRequest(setRequest, 10, TimeUnit.SECONDS);

        if (response != null) {
            System.out.println("设置响应: " + response.getType());
        } else {
            System.out.println("设置请求超时");
        }
    }

    // 保留原有的交通信号控制方法
    private void querySysInfo(GatTcpClient client) throws Exception {
        System.out.println("查询系统信息...");
        // 原有实现
    }

    private void queryCrossState(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("输入路口编号: ");
        String crossId = scanner.nextLine().trim();
        System.out.println("查询路口状态: " + crossId);
        // 原有实现
    }
}
