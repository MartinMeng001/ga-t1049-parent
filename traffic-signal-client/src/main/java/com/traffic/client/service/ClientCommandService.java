package com.traffic.client.service;

import com.traffic.client.network.client.GatTcpClient;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.model.enums.SystemType;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoTimeOut;
import com.traffic.gat1049.protocol.model.sdo.SdoTimeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 客户端交互式命令服务
 */
@Service
public class ClientCommandService {

    private static final Logger logger = LoggerFactory.getLogger(ClientCommandService.class);

    @Autowired
    private ClientDemoService demoService;

    @Autowired
    private SessionManager sessionManager;

    public void startInteractiveConsole(GatTcpClient client) {
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

                    executeCommand(command, client, scanner);

                } catch (Exception e) {
                    logger.error("命令执行错误", e);
                }
            }

            scanner.close();
        }, "Console-Thread").start();
    }

    private void executeCommand(String command, GatTcpClient client, Scanner scanner) throws Exception {
        switch (command.toLowerCase()) {
            case "help":
            case "h":
                printGat1049Help();
                break;

            case "quit":
            case "exit":
            case "q":
                logger.info("退出客户端...");
                sessionManager.logout(GatTcpClient.tocken);
                client.disconnect();
                System.exit(0);
                break;

            case "status":
                showGat1049Status(client);
                break;

            case "login":
                performGat1049Login(client, scanner);
                break;

            case "logout":
                performGat1049Logout(client, scanner);
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
    }

    private void printGat1049Help() {
        System.out.println("\n=== GA/T 1049.1/1049.2 客户端命令 ===");
        System.out.println("GA/T 1049.1 通用操作:");
        System.out.println("  help (h)       - 显示帮助信息");
        System.out.println("  quit (q)       - 退出客户端");
        System.out.println("  status         - 显示连接状态");
        System.out.println("  login          - 手动登录");
        System.out.println("  logout         - 退出登录");
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
        System.out.println("Token状态: " + (demoService.getToken() != null ? "已获取" : "未获取"));
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
                demoService.setToken(response.getToken());
                GatTcpClient.tocken = response.getToken();
                sessionManager.registerLogin(username, SystemType.UTCS, "127.0.0.1", GatTcpClient.tocken);
            } else {
                System.out.println("登录失败: " + response.getType());
            }
        } else {
            System.out.println("登录请求超时");
        }
    }

    private void performGat1049Logout(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("用户名: ");
        String username = scanner.nextLine().trim();

        Message logoutRequest = MessageBuilder.createLogoutRequest(username, "", demoService.getToken());
        Message response = client.sendRequest(logoutRequest, 10, TimeUnit.SECONDS);

        if (response != null) {
            if ("RESPONSE".equals(response.getType())) {
                System.out.println("退出登录成功");
                demoService.setToken(null);
                GatTcpClient.tocken = null;
                sessionManager.logout(GatTcpClient.tocken);
            } else {
                System.out.println("退出登录失败: " + response.getType());
            }
        } else {
            System.out.println("退出登录请求超时");
        }
    }

    private void performGat1049Subscribe(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("消息类型 (REQUEST/RESPONSE/PUSH/ERROR): ");
        String msgType = scanner.nextLine().trim();
        System.out.print("操作名称 (Get/Set/Notify/Other): ");
        String operName = scanner.nextLine().trim();
        System.out.print("对象名称: ");
        String objName = scanner.nextLine().trim();

        Message subscribeRequest = MessageBuilder.createSubscribeRequest(demoService.getToken(), msgType, operName, objName);
        Message response = client.sendRequest(subscribeRequest, 10, TimeUnit.SECONDS);

        if (response != null) {
            System.out.println("订阅响应: " + response.getType());
        } else {
            System.out.println("订阅请求超时");
        }
    }

    private void performGat1049Heartbeat(GatTcpClient client) throws Exception {
        Message heartbeat = MessageBuilder.createHeartbeatMessage(demoService.getToken());
        client.sendMessage(heartbeat);
        System.out.println("心跳已发送");
    }

    private void performGat1049Query(GatTcpClient client, Scanner scanner) throws Exception {
        SdoTimeServer timeServerQuery = new SdoTimeServer("", "", null);
        Message queryRequest = MessageBuilder.createQueryRequest(demoService.getToken(), timeServerQuery);
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

        SdoTimeOut timeout = new SdoTimeOut(timeoutSeconds);
        Message setRequest = MessageBuilder.createSetRequest(demoService.getToken(), timeout);
        Message response = client.sendRequest(setRequest, 10, TimeUnit.SECONDS);

        if (response != null) {
            System.out.println("设置响应: " + response.getType());
        } else {
            System.out.println("设置请求超时");
        }
    }

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
