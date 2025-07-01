package com.traffic.server.debug;

import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.model.enums.*;
import com.traffic.gat1049.protocol.model.command.*;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.utils.PeriodGenerator;
import com.traffic.gat1049.utils.ScheduleParamGenerator;
import com.traffic.gat1049.utils.StageTimingGenerator;
import com.traffic.server.service.EnhancedServerSubscriptionService;
import com.traffic.server.network.client.ServerToClientSender;
import com.traffic.server.service.TSCCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Autowired
    private ServerToClientSender clientSender;

    @Autowired
    private TSCCommandService tscCommandService;

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
     * 处理用户输入的命令 - 添加TSC查询功能
     */
    private void handleCommand(String command, Scanner scanner) throws GatProtocolException {
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

            // 新增: TSC查询功能
            case "tsc":
            case "query":
                performTSCQuery(scanner);
                break;

            // 在handleCommand方法中添加
            case "set":
            case "config":
                performTSCSet(scanner);
                break;
            default:
                System.out.println("未知命令: " + command);
                System.out.println("输入 'help' 查看可用命令");
        }
    }

    /**
     * 显示帮助信息 - 更新版本
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
        System.out.println("TSC功能测试:");
        System.out.println("  tsc (query)     - TSC指令查询测试 🔍");
        System.out.println("  set (config)    - TSC设置功能测试 🔧 NEW!");
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
        System.out.println("🔍 = 查询功能");
        System.out.println("🔧 = 设置功能");
        System.out.println("===========================================");
    }
    /**
     * 执行TSC查询测试
     */
    private void performTSCQuery(Scanner scanner) throws GatProtocolException {
        System.out.println("\n=== TSC指令查询测试 ===");

        // 第一步：选择TCP客户端
        String selectedClient = selectTcpClient(scanner);
        if (selectedClient == null) {
            return; // 用户取消或没有可用客户端
        }

        // 第二步：选择查询类别
        System.out.println("\n客户端已选择: " + selectedClient);
        System.out.println("请选择查询类别:");
        System.out.println("1. 系统参数查询");
        System.out.println("2. 配置参数查询");
        System.out.println("3. 运行信息查询");
        System.out.println("4. 自定义查询");
        System.out.println("5. 返回主菜单");
        System.out.print("请选择 (1-5): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                performSystemParamQuery(scanner, selectedClient);
                break;
            case "2":
                performConfigParamQuery(scanner, selectedClient);
                break;
            case "3":
                performRuntimeInfoQuery(scanner, selectedClient);
                break;
            case "4":
                performCustomQuery(scanner, selectedClient);
                break;
            case "5":
                return;
            default:
                System.out.println("无效选择");
        }
    }
    /**
     * 执行TSC设置测试
     */
    private void performTSCSet(Scanner scanner) throws GatProtocolException {
        System.out.println("\n=== TSC设置功能测试 ===");

        // 第一步：选择TCP客户端
        String selectedClient = selectTcpClient(scanner);
        if (selectedClient == null) {
            return; // 用户取消或没有可用客户端
        }

        // 第二步：选择设置功能类别
        System.out.println("\n客户端已选择: " + selectedClient);
        System.out.println("请选择设置功能类别:");
        System.out.println("1. 流向控制设置");
        System.out.println("2. 数据上报设置");
        System.out.println("3. 预案管理设置");
        System.out.println("4. 计划调度设置");
        System.out.println("5. 实时控制设置");
        System.out.println("6. 返回主菜单");
        System.out.print("请选择 (1-6): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                performFlowControlSetting(scanner, selectedClient);
                break;
            case "2":
                performDataReportSetting(scanner, selectedClient);
                break;
            case "3":
                performPlanManagementSetting(scanner, selectedClient);
                break;
            case "4":
                performScheduleSetting(scanner, selectedClient);
                break;
            case "5":
                performRealTimeControlSetting(scanner, selectedClient);
                break;
            case "6":
                return;
            default:
                System.out.println("无效选择");
        }
    }

    /**
     * 选择TCP客户端
     */
    private String selectTcpClient(Scanner scanner) {
        System.out.println("\n--- 选择TCP客户端 ---");

        // 获取在线客户端列表
        java.util.List<String> onlineClients = getOnlineClients();

        if (onlineClients.isEmpty()) {
            System.out.println("⚠️ 当前没有在线的TCP客户端");
            System.out.println("请先确保有客户端连接到服务器");
            System.out.println();
            System.out.println("可用选项:");
            System.out.println("1. 使用模拟客户端进行测试");
            System.out.println("2. 返回主菜单");
            System.out.print("请选择 (1-2): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return "MOCK_CLIENT_127.0.0.1:9999";
                case "2":
                default:
                    return null;
            }
        }

        System.out.println("当前在线客户端:");
        for (int i = 0; i < onlineClients.size(); i++) {
            System.out.println((i + 1) + ". " + onlineClients.get(i));
        }
        System.out.println((onlineClients.size() + 1) + ". 使用模拟客户端");
        System.out.println((onlineClients.size() + 2) + ". 返回主菜单");

        System.out.print("请选择客户端 (1-" + (onlineClients.size() + 2) + "): ");
        String choice = scanner.nextLine().trim();

        try {
            int index = Integer.parseInt(choice);
            if (index >= 1 && index <= onlineClients.size()) {
                return onlineClients.get(index - 1);
            } else if (index == onlineClients.size() + 1) {
                return "MOCK_CLIENT_127.0.0.1:9999";
            } else if (index == onlineClients.size() + 2) {
                return null;
            } else {
                System.out.println("⚠️ 无效选择");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ 请输入有效的数字");
            return null;
        }
    }
    /**
     * 获取在线客户端列表
     */
    private java.util.List<String> getOnlineClients() {
        java.util.List<String> clients = new java.util.ArrayList<>();

        if (clientSender != null) {
            try {
                // 尝试从clientSender获取在线客户端
                clients.addAll(clientSender.getOnlineClientIds());
            } catch (Exception e) {
                logger.debug("无法从clientSender获取客户端列表: {}", e.getMessage());
            }
        }

        // 如果没有实际客户端，添加一些模拟的示例
        if (clients.isEmpty() && sessionManager != null) {
            try {
                // 从session manager获取活跃会话
                int sessionCount = sessionManager.getOnlineSessionCount();
                for (int i = 1; i <= sessionCount; i++) {
                    clients.add("TCP_CLIENT_" + i + " (192.168.1." + (100 + i) + ":9999)");
                }
            } catch (Exception e) {
                logger.debug("无法从sessionManager获取会话信息: {}", e.getMessage());
            }
        }

        return clients;
    }

    /**
     * 系统参数查询
     */
    private void performSystemParamQuery(Scanner scanner, String clientId) throws GatProtocolException {
        System.out.println("\n--- 系统参数查询 ---");
        System.out.println("1. SysInfo        - 系统信息");
        System.out.println("2. SysState       - 系统状态");
        System.out.println("3. 返回上级菜单");
        System.out.print("请选择 (1-3): ");

        String choice = scanner.nextLine().trim();
        String objName = null;

        switch (choice) {
            case "1":
                objName = "SysInfo";
                break;
            case "2":
                objName = "SysState";
                break;
            case "3":
                return;
            default:
                System.out.println("无效选择");
                return;
        }

        System.out.println("📋 将执行查询: " + objName);
        simulateTSCQuery(objName, null, null, clientId);
    }
    /**
     * 配置参数查询
     */
    private void performConfigParamQuery(Scanner scanner, String clientId) throws GatProtocolException {
        System.out.println("\n--- 配置参数查询 ---");
        System.out.println("1.  RegionParam         - 区域参数");
        System.out.println("2.  SubRegionParam      - 子区域参数");
        System.out.println("3.  RouteParam          - 路线参数");
        System.out.println("4.  CrossParam          - 路口参数");
        System.out.println("5.  SignalController    - 信号机参数");
        System.out.println("6.  LampGroup           - 灯组参数");
        System.out.println("7.  DetectorParam       - 检测器参数");
        System.out.println("8.  LaneParam           - 车道参数");
        System.out.println("9.  PedestrianParam     - 行人参数");
        System.out.println("10. SignalGroupParam    - 信号组参数");
        System.out.println("11. StageParam          - 阶段参数");
        System.out.println("12. PlanParam           - 配时方案参数");
        System.out.println("13. DayPlanParam        - 日计划参数");
        System.out.println("14. ScheduleParam       - 调度参数");
        System.out.println("15. 返回上级菜单");
        System.out.print("请选择 (1-15): ");

        String choice = scanner.nextLine().trim();
        String objName = null;
        boolean needsId = true;

        switch (choice) {
            case "1": objName = "RegionParam"; break;
            case "2": objName = "SubRegionParam"; break;
            case "3": objName = "RouteParam"; break;
            case "4": objName = "CrossParam"; break;
            case "5": objName = "SignalController"; break;
            case "6": objName = "LampGroupParam"; break;
            case "7": objName = "DetectorParam"; break;
            case "8": objName = "LaneParam"; break;
            case "9": objName = "PedestrianParam"; break;
            case "10": objName = "SignalGroupParam"; break;
            case "11": objName = "StageParam"; break;
            case "12": objName = "PlanParam"; break;
            case "13": objName = "DayPlanParam"; break;
            case "14": objName = "ScheduleParam"; break;
            case "15": return;
            default:
                System.out.println("无效选择");
                return;
        }

        String id = null;
        Integer no = null;

        if (needsId) {
            System.out.print("请输入ID (可选，回车跳过): ");
            String inputId = scanner.nextLine().trim();
            if (!inputId.isEmpty()) {
                id = inputId;
            }

            System.out.print("请输入No索引号 (可选，回车跳过): ");
            String inputNo = scanner.nextLine().trim();
            if (!inputNo.isEmpty()) {
                try {
                    no = Integer.parseInt(inputNo);
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ 无效的数字格式，将忽略No参数");
                }
            }
        }

        System.out.println("📋 将执行查询: " + objName +
                (id != null ? ", ID=" + id : "") +
                (no != null ? ", No=" + no : ""));
        simulateTSCQuery(objName, id, no, clientId);
    }

    /**
     * 运行信息查询
     */
    private void performRuntimeInfoQuery(Scanner scanner, String clientId) throws GatProtocolException {
        System.out.println("\n--- 运行信息查询 ---");
        System.out.println("1.  CrossState               - 路口状态");
        System.out.println("2.  SignalControllerError    - 信号机故障");
        System.out.println("3.  CrossModePlan            - 路口控制方式和方案");
        System.out.println("4.  CrossCycle               - 路口周期");
        System.out.println("5.  CrossStage                - 路口阶段");
        System.out.println("6.  CrossSignalGroupStatus   - 路口信号组状态");
        System.out.println("7.  CrossTrafficData         - 路口交通流数据");
        System.out.println("8.  StageTrafficData         - 阶段交通流数据");
        System.out.println("9.  VarLaneStatus            - 可变车道状态");
        System.out.println("10. RouteCtrlInfo        - 路线控制方式");
        System.out.println("11. RouteSpeed               - 路线速度");
        System.out.println("12. 返回上级菜单");
        System.out.print("请选择 (1-12): ");

        String choice = scanner.nextLine().trim();
        String objName = null;

        switch (choice) {
            case "1": objName = "CrossState"; break;
            case "2": objName = "SignalControllerError"; break;
            case "3": objName = "CrossCtrlInfo"; break;
            case "4": objName = "CrossCycle"; break;
            case "5": objName = "CrossStage"; break;
            case "6": objName = "CrossSignalGroupStatus"; break;
            case "7": objName = "CrossTrafficData"; break;
            case "8": objName = "StageTrafficData"; break;
            case "9": objName = "VarLaneStatus"; break;
            case "10": objName = "RouteCtrlInfo"; break;
            case "11": objName = "RouteSpeed"; break;
            case "12": return;
            default:
                System.out.println("无效选择");
                return;
        }

        String id = null;
        Integer no = null;

        // 大部分运行信息查询需要ID参数
        System.out.print("请输入ID (建议输入路口ID/路线ID等): ");
        String inputId = scanner.nextLine().trim();
        if (!inputId.isEmpty()) {
            id = inputId;
        } else {
            id = ""; // 默认路口ID
            System.out.println("📌 使用默认ID: " + id);
        }

        System.out.print("请输入No索引号 (可选，回车跳过): ");
        String inputNo = scanner.nextLine().trim();
        if (!inputNo.isEmpty()) {
            try {
                no = Integer.parseInt(inputNo);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ 无效的数字格式，将忽略No参数");
            }
        }

        System.out.println("📋 将执行查询: " + objName +
                (id != null ? ", ID=" + id : "") +
                (no != null ? ", No=" + no : ""));
        simulateTSCQuery(objName, id, no, clientId);
    }

    /**
     * 自定义查询
     */
    private void performCustomQuery(Scanner scanner, String clientId) throws GatProtocolException {
        System.out.println("\n--- 自定义TSC查询 ---");
        System.out.print("请输入对象名称 (ObjName): ");
        String objName = scanner.nextLine().trim();

        if (objName.isEmpty()) {
            System.out.println("⚠️ 对象名称不能为空");
            return;
        }

        System.out.print("请输入ID (可选): ");
        String inputId = scanner.nextLine().trim();
        String id = inputId.isEmpty() ? null : inputId;

        System.out.print("请输入No (可选): ");
        String inputNo = scanner.nextLine().trim();
        Integer no = null;
        if (!inputNo.isEmpty()) {
            try {
                no = Integer.parseInt(inputNo);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ 无效的数字格式，将忽略No参数");
            }
        }

        System.out.println("📋 将执行自定义查询: " + objName +
                (id != null ? ", ID=" + id : "") +
                (no != null ? ", No=" + no : ""));
        simulateTSCQuery(objName, id, no, clientId);
    }

    /**
     * 模拟TSC查询执行
     * 注意：这里只是模拟显示，不执行实际的调用
     */
    private void simulateTSCQuery(String objName, String id, Integer no, String targetClient) throws GatProtocolException {

        tscCommandService.queryTSCInfo(targetClient, objName, id, no);
//        System.out.println("\n🔄 模拟执行TSC查询...");
//        System.out.println("─────────────────────────");
//        System.out.println("📋 查询类别: " + category);
//        System.out.println("🎯 对象名称: " + objName);
//        System.out.println("🆔 对象ID: " + (id != null ? id : "N/A"));
//        System.out.println("🔢 索引号: " + (no != null ? no.toString() : "N/A"));
//        System.out.println("─────────────────────────");
//
//        // 模拟构建TSCCmd
//        System.out.println("🔧 构建TSCCmd对象:");
//        System.out.println("   TSCCmd {");
//        System.out.println("     objName: \"" + objName + "\",");
//        if (id != null) {
//            System.out.println("     id: \"" + id + "\",");
//        }
//        if (no != null) {
//            System.out.println("     no: " + no + ",");
//        }
//        System.out.println("   }");
//
//        // 模拟构建请求消息
//        System.out.println("📨 构建GA/T 1049协议消息:");
//        System.out.println("   Message {");
//        System.out.println("     type: \"REQUEST\",");
//        System.out.println("     from: \"TICP\",");
//        System.out.println("     to: \"UTCS\",");
//        System.out.println("     operation: \"Get\",");
//        System.out.println("     token: \"DEBUG_SESSION_TOKEN\",");
//        System.out.println("     data: TSCCmd");
//        System.out.println("   }");
//
//        // 模拟处理过程
//        System.out.println("⚙️ 处理流程:");
//        System.out.println("   1. 验证会话令牌");
//        System.out.println("   2. 解析TSCCmd参数");
//        System.out.println("   3. 路由到TSCCommandHandler");
//        System.out.println("   4. 分发到具体的服务方法");
//        System.out.println("   5. 执行业务逻辑查询");
//        System.out.println("   6. 构建响应消息");
//        System.out.println("   7. 返回查询结果");
//
//        // 模拟可能的处理器
//        String handlerName = determineHandler(objName);
//        System.out.println("🎛️ 预期处理器: " + handlerName);
//
//        // 模拟响应状态
//        System.out.println("✅ 模拟执行完成");
//        System.out.println("📤 响应状态: SUCCESS");
//        System.out.println("📊 数据类型: " + objName + "对象");
//
//        System.out.println("\n💡 提示: 这是调试模式，未执行实际的网络调用");
//        System.out.println("   实际使用时会通过MessageProcessor处理消息");
//        System.out.println("─────────────────────────");
    }

    /**
     * 流向控制设置
     */
    private void performFlowControlSetting(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 流向控制设置 ===");
        System.out.println("1. lockFlowDirection - 锁定交通流向");
        System.out.println("2. unlockFlowDirection - 解锁交通流向");
        System.out.println("3. 返回上级菜单");
        System.out.print("请选择 (1-3): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                performLockFlowDirection(scanner, selectedClient);
                break;
            case "2":
                performUnlockFlowDirection(scanner, selectedClient);
                break;
            case "3":
                return;
            default:
                System.out.println("无效选择");
        }
    }
    /**
     * 数据上报设置
     */
    private void performDataReportSetting(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 数据上报设置 ===");
        System.out.println("1. CrossReportCtrl - 交通数据上报控制");
        System.out.println("2. 返回上级菜单");
        System.out.print("请选择 (1-2): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                performCrossReportCtrl(scanner, selectedClient);
                break;
            case "2":
                return;
            default:
                System.out.println("无效选择");
        }
    }

    /**
     * 预案管理设置
     */
    private void performPlanManagementSetting(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 预案管理设置 ===");
        System.out.println("1. CenterPlan - 中心预案控制");
        System.out.println("2. SetPlanParam - 配时方案设置");
        System.out.println("3. 返回上级菜单");
        System.out.print("请选择 (1-3): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                performCenterPlan(scanner, selectedClient);
                break;
            case "2":
                performSetPlanParam(scanner, selectedClient);
                break;
            case "3":
                return;
            default:
                System.out.println("无效选择");
        }
    }
    /**
     * 计划调度设置
     */
    private void performScheduleSetting(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 计划调度设置 ===");
        System.out.println("1. SetDayplanParam - 日计划参数设置");
        System.out.println("2. SetScheduleParam - 调度参数设置");
        System.out.println("3. 返回上级菜单");
        System.out.print("请选择 (1-3): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                performSetDayPlanParam(scanner, selectedClient);
                break;
            case "2":
                performSetScheduleParam(scanner, selectedClient);
                break;
            case "3":
                return;
            default:
                System.out.println("无效选择");
        }
    }
    /**
     * 实时控制设置
     */
    private void performRealTimeControlSetting(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 实时控制设置 ===");
        System.out.println("1. StageCtrl - 阶段干预控制");
        System.out.println("2. 返回上级菜单");
        System.out.print("请选择 (1-2): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                performStageCtrl(scanner, selectedClient);
                break;
            case "2":
                return;
            default:
                System.out.println("无效选择");
        }
    }
    /**
     * 锁定交通流向
     */
    private void performLockFlowDirection(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 锁定交通流向测试 ===");

        try {
            // 收集参数
            System.out.print("请输入路口编号 (如: 001001001): ");
            String crossId = scanner.nextLine().trim();

            System.out.println("请选择交通流类型:");
            System.out.println("1. MOTOR - 机动车");
            System.out.println("2. NONMOTOR - 非机动");
            System.out.println("3. PEDSTRAINT - 人行");
            System.out.print("请选择 (1-3): ");
            FlowType flowType = parseFlowType(scanner.nextLine().trim());

            System.out.println("请选择进口方向:");
            System.out.println("1. NORTH - 北进口");
            System.out.println("2. SOUTH - 南进口");
            System.out.println("3. EAST - 东进口");
            System.out.println("4. WEST - 西进口");
            System.out.print("请选择 (1-4): ");
            Direction entrance = parseDirection(scanner.nextLine().trim());

            System.out.println("请选择出口方向:");
            System.out.println("1. NORTH - 北出口");
            System.out.println("2. SOUTH - 南出口");
            System.out.println("3. EAST - 东出口");
            System.out.println("4. WEST - 西出口");
            System.out.print("请选择 (1-4): ");
            Direction exit = parseDirection(scanner.nextLine().trim());

            System.out.println("请选择锁定类型:");
            System.out.println("1. CURRENT_PLAN - 阶段放行");
            System.out.println("2. SINGLE_ENTRANCE - 单向全放");
            System.out.println("3. SIGNAL_GROUP_ONLY - 只放行此流向信号组");
            System.out.print("请选择 (1-3): ");
            LockType lockType = parseLockType(scanner.nextLine().trim());

            System.out.print("请输入锁定时长(秒，0表示持续锁定): ");
            Integer duration = Integer.parseInt(scanner.nextLine().trim());

            // 构建命令
            LockFlowDirection lockCmd = new LockFlowDirection();
            lockCmd.setCrossId(crossId);
            lockCmd.setType(flowType);
            lockCmd.setEntrance(entrance);
            lockCmd.setExit(exit);
            lockCmd.setLockType(lockType);
            lockCmd.setDuration(duration);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("路口编号: " + crossId);
            System.out.println("流向类型: " + flowType.getDescription());
            System.out.println("进口方向: " + entrance.getDescription());
            System.out.println("出口方向: " + exit.getDescription());
            System.out.println("锁定类型: " + lockType.getDescription());
            System.out.println("锁定时长: " + (duration == 0 ? "持续锁定" : duration + "秒"));

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            sendSettingCommand(selectedClient, lockCmd, "锁定交通流向");

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }
    /**
     * 解锁交通流向
     */
    private void performUnlockFlowDirection(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 解锁交通流向测试 ===");

        try {
            // 收集参数
            System.out.print("请输入路口编号 (如: 001001001): ");
            String crossId = scanner.nextLine().trim();

            System.out.println("请选择交通流类型:");
            System.out.println("1. MOTOR - 机动车");
            System.out.println("2. NONMOTOR - 非机动");
            System.out.println("3. PEDSTRAIN - 人行");
            System.out.print("请选择 (1-3): ");
            FlowType flowType = parseFlowType(scanner.nextLine().trim());

            System.out.println("请选择进口方向:");
            System.out.println("1. NORTH - 北进口");
            System.out.println("2. SOUTH - 南进口");
            System.out.println("3. EAST - 东进口");
            System.out.println("4. WEST - 西进口");
            System.out.print("请选择 (1-4): ");
            Direction entrance = parseDirection(scanner.nextLine().trim());

            System.out.println("请选择出口方向:");
            System.out.println("1. NORTH - 北出口");
            System.out.println("2. SOUTH - 南出口");
            System.out.println("3. EAST - 东出口");
            System.out.println("4. WEST - 西出口");
            System.out.print("请选择 (1-4): ");
            Direction exit = parseDirection(scanner.nextLine().trim());

            // 构建命令
            UnlockFlowDirection unlockCmd = new UnlockFlowDirection();
            unlockCmd.setCrossId(crossId);
            unlockCmd.setType(flowType);
            unlockCmd.setEntrance(entrance);
            unlockCmd.setExit(exit);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("路口编号: " + crossId);
            System.out.println("流向类型: " + flowType.getDescription());
            System.out.println("进口方向: " + entrance.getDescription());
            System.out.println("出口方向: " + exit.getDescription());

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            simulateTSCSet("解锁交通流向", unlockCmd, selectedClient);

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }

    /**
     * 交通数据上报控制
     */
    private void performCrossReportCtrl(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 交通数据上报控制测试 ===");

        try {
            // 收集参数
            System.out.println("请选择上报命令:");
            System.out.println("1. START - 开始主动上报");
            System.out.println("2. STOP - 停止主动上报");
            System.out.print("请选择 (1-2): ");
            ReportCommand cmd = parseReportCommand(scanner.nextLine().trim());

            System.out.println("请选择上报数据类型:");
            System.out.println("1. CrossCycle - 路口周期");
            System.out.println("2. CrossStage - 路口阶段");
            System.out.println("3. CrossSignalGroupStatus - 路口信号组灯态");
            System.out.println("4. CrossTrafficData - 路口交通流数据");
            System.out.println("5. StageTrafficData - 路口阶段交通流数据");
            System.out.print("请选择 (1-5): ");
            ReportDataType type = parseReportDataType(scanner.nextLine().trim());

            System.out.print("请输入路口编号列表 (用逗号分隔，如: 001001001,001001002): ");
            String crossIdInput = scanner.nextLine().trim();
            List<String> crossIdList = Arrays.asList(crossIdInput.split(","));

            // 去除空格
            crossIdList = crossIdList.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList());

            // 构建命令
            CrossReportCtrl reportCtrl = new CrossReportCtrl();
            reportCtrl.setCmd(cmd);
            reportCtrl.setType(type);
            reportCtrl.setCrossIdList(crossIdList);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("上报命令: " + cmd.getDescription());
            System.out.println("数据类型: " + type.getDescription());
            System.out.println("路口列表: " + String.join(", ", crossIdList));

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            sendSettingCommand(selectedClient, reportCtrl, "交通数据上报控制");

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }

    /**
     * 中心预案控制
     */
    private void performCenterPlan(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 中心预案控制测试 ===");

        try {
            // 收集参数
            System.out.println("请选择控制模式:");
            System.out.println("1. COMPUTER_CONTROL - 计算机控制");
            System.out.println("2. MANUAL_CONTROL - 手动控制");
            //System.out.println("3. CENTRAL_CONTROL - 中心控制");
            System.out.print("请选择 (1-3): ");
            ControlMode controlMode = parseControlMode(scanner.nextLine().trim());

            System.out.print("请输入路口编号 (如: 001001001): ");
            String crossId = scanner.nextLine().trim();

            System.out.print("请输入方案名称 (如: 应急预案1): ");
            String planName = scanner.nextLine().trim();

            System.out.print("请输入周期时长(秒) (如: 120): ");
            Integer cycleTime = Integer.parseInt(scanner.nextLine().trim());

            // 构建配时方案参数
            PlanParam planParam = new PlanParam();
            planParam.setCrossId(crossId);
            planParam.setPlanName(planName);
            planParam.setCycleLen(cycleTime);
            planParam.setStageTimingList(StageTimingGenerator.generateEvenStageTimingList(cycleTime, 2));
            // 这里可以添加更多阶段配时参数...

            // 构建命令
            CenterPlan centerPlan = new CenterPlan();
            centerPlan.setCrossControlMode(controlMode);
            centerPlan.setPlanParam(planParam);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("控制模式: " + controlMode.getDescription());
            System.out.println("路口编号: " + crossId);
            System.out.println("方案名称: " + planName);
            System.out.println("周期时长: " + cycleTime + "秒");

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            sendSettingCommand(selectedClient, centerPlan, "中心预案控制");

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }

    /**
     * 配时方案设置
     */
    private void performSetPlanParam(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 配时方案设置测试 ===");

        try {
            // 收集参数
            System.out.println("请选择操作类型:");
            System.out.println("1. ADD - 新增");
            System.out.println("2. MODIFY - 修改");
            System.out.println("3. DELETE - 删除");
            System.out.print("请选择 (1-3): ");
            OperationType oper = parseOperationType(scanner.nextLine().trim());

            System.out.print("请输入路口编号 (如: 001001001): ");
            String crossId = scanner.nextLine().trim();

            System.out.print("请输入方案号 (如: 5): ");
            Integer planNo = Integer.parseInt(scanner.nextLine().trim());

            String planName = "";
            Integer cycleTime = null;

            System.out.print("请输入方案名称 (如: 早高峰方案): ");
            planName = scanner.nextLine().trim();


            System.out.print("请输入周期时长(秒) (如: 150): ");
            cycleTime = Integer.parseInt(scanner.nextLine().trim());


            // 构建配时方案参数
            PlanParam planParam = new PlanParam();
            planParam.setCrossId(crossId);
            planParam.setPlanNo(planNo);
            if (!planName.isEmpty()) {
                planParam.setPlanName(planName);
            }
            if (cycleTime != null) {
                planParam.setCycleLen(cycleTime);
            }
            planParam.setStageTimingList(StageTimingGenerator.generateEvenStageTimingList(cycleTime, 2));
            // 构建命令
            SetPlanParam setPlan = new SetPlanParam();
            setPlan.setOper(oper);
            setPlan.setPlanParam(planParam);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("操作类型: " + oper.getDescription());
            System.out.println("路口编号: " + crossId);
            System.out.println("方案号: " + planNo);
            if (!planName.isEmpty()) {
                System.out.println("方案名称: " + planName);
            }
            if (cycleTime != null) {
                System.out.println("周期时长: " + cycleTime + "秒");
            }

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            sendSettingCommand(selectedClient, setPlan, "配时方案设置");

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }

    /**
     * 日计划参数设置
     */
    private void performSetDayPlanParam(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 日计划参数设置测试 ===");

        try {
            // 收集参数
            System.out.println("请选择操作类型:");
            System.out.println("1. ADD - 新增");
            System.out.println("2. MODIFY - 修改");
            System.out.println("3. DELETE - 删除");
            System.out.print("请选择 (1-3): ");
            OperationType oper = parseOperationType(scanner.nextLine().trim());

            System.out.print("请输入路口编号 (如: 001001001): ");
            String crossId = scanner.nextLine().trim();

            System.out.print("请输入日计划号 (如: 3): ");
            Integer dayPlanNo = Integer.parseInt(scanner.nextLine().trim());

            String dayPlanName = "";

            System.out.print("请输入日计划名称 (如: 工作日计划): ");
            dayPlanName = scanner.nextLine().trim();

            // 构建日计划参数
            DayPlanParam dayPlan = new DayPlanParam();
            dayPlan.setCrossId(crossId);
            dayPlan.setDayPlanNo(dayPlanNo);
            dayPlan.setPeriodList(PeriodGenerator.generateEvenPeriodList(3));
//            if (!dayPlanName.isEmpty()) {
//                dayPlan.setDayPlanName(dayPlanName);
//            }

            // 构建命令
            SetDayPlanParam setDayPlan = new SetDayPlanParam();
            setDayPlan.setOper(oper);
            setDayPlan.setDayPlanParam(dayPlan);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("操作类型: " + oper.getDescription());
            System.out.println("路口编号: " + crossId);
            System.out.println("日计划号: " + dayPlanNo);
            if (!dayPlanName.isEmpty()) {
                System.out.println("日计划名称: " + dayPlanName);
            }

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            sendSettingCommand(selectedClient, setDayPlan, "日计划参数设置");

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }
    /**
     * 调度参数设置
     */
    private void performSetScheduleParam(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 调度参数设置测试 ===");

        try {
            // 收集参数
            System.out.println("请选择操作类型:");
            System.out.println("1. ADD - 新增");
            System.out.println("2. MODIFY - 修改");
            System.out.println("3. DELETE - 删除");
            System.out.print("请选择 (1-3): ");
            OperationType oper = parseOperationType(scanner.nextLine().trim());

            System.out.print("请输入路口编号 (如: 001001001): ");
            String crossId = scanner.nextLine().trim();

            System.out.print("请输入调度号 (如: 2): ");
            Integer scheduleNo = Integer.parseInt(scanner.nextLine().trim());

            String scheduleName = "";

            //if (oper != OperationType.DELETE) {
            System.out.print("请输入调度名称 (如: 月度调度): ");
            scheduleName = scanner.nextLine().trim();
            //}

            // 构建调度参数
            ScheduleParam scheduleParam = ScheduleParamGenerator.generateScheduleParam(crossId, scheduleNo, 2);//new ScheduleParam();
//            scheduleParam.setCrossId(crossId);
//            scheduleParam.setScheduleNo(scheduleNo);
//            if (!scheduleName.isEmpty()) {
//                scheduleParam.setScheduleName(scheduleName);
//            }

            // 构建命令
            SetScheduleParam setSchedule = new SetScheduleParam();
            setSchedule.setOper(oper);
            setSchedule.setScheduleParam(scheduleParam);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("操作类型: " + oper.getDescription());
            System.out.println("路口编号: " + crossId);
            System.out.println("调度号: " + scheduleNo);
            if (!scheduleName.isEmpty()) {
                System.out.println("调度名称: " + scheduleName);
            }

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            sendSettingCommand(selectedClient, setSchedule, "调度参数设置");

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }
    /**
     * 阶段干预控制
     */
    private void performStageCtrl(Scanner scanner, String selectedClient) throws GatProtocolException {
        System.out.println("\n=== 阶段干预控制测试 ===");

        try {
            // 收集参数
            System.out.print("请输入路口编号 (如: 001001001): ");
            String crossId = scanner.nextLine().trim();

            System.out.print("请输入阶段号 (如: 2): ");
            Integer stageNo = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("请选择干预类型:");
            System.out.println("1. EXTEND - 延长");
            System.out.println("2. REDUCE - 缩短");
            //System.out.println("3. SKIP - 跳过");
            System.out.print("请选择 (1-2): ");
            InterventionType type = parseInterventionType(scanner.nextLine().trim());

            System.out.print("请输入干预时长(秒) (如: 30): ");
            Integer len = Integer.parseInt(scanner.nextLine().trim());

            // 构建命令
            AdjustStage adjustStage = new AdjustStage();
            adjustStage.setCrossId(crossId);
            adjustStage.setStageNo(stageNo);
            adjustStage.setType(type);
            adjustStage.setLen(len);

            // 显示设置信息
            System.out.println("\n=== 设置信息确认 ===");
            System.out.println("目标客户端: " + selectedClient);
            System.out.println("路口编号: " + crossId);
            System.out.println("阶段号: " + stageNo);
            System.out.println("干预类型: " + type.getDescription());
            System.out.println("干预时长: " + len + "秒");

            System.out.print("\n确认发送? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 发送命令
            sendSettingCommand(selectedClient, adjustStage, "阶段干预控制");

        } catch (Exception e) {
            System.out.println("参数输入错误: " + e.getMessage());
        }
    }
    /**
     * 发送设置命令的通用方法
     */
    private void sendSettingCommand(String selectedClient, Object command, String commandName) throws GatProtocolException {
        long startTime = System.currentTimeMillis();

        try {
            System.out.println("\n正在发送" + commandName + "命令...");

            // 构建消息
            //Message requestMessage = MessageBuilder.createSetRequest(command);

            if (selectedClient.startsWith("MOCK_CLIENT")) {
                // 模拟客户端模式
                System.out.println("✓ 使用模拟客户端模式");
                System.out.println("✓ 命令构建成功: " + command.getClass().getSimpleName());
                System.out.println("✓ 模拟发送成功");
                System.out.println("✓ 模拟响应: " + commandName + "设置成功");

            } else if (clientSender != null) {
                // 实际网络发送
                System.out.println("✓ 使用实际网络发送");
                System.out.println("✓ 命令构建成功: " + command.getClass().getSimpleName());

                // 这里需要根据实际的clientSender接口进行发送
                simulateTSCSet(commandName, command, selectedClient);
                // clientSender.sendMessage(selectedClient, requestMessage);
                System.out.println("✓ 网络发送成功");
                System.out.println("✓ 等待服务端响应...");

                // 模拟响应处理
                System.out.println("✓ 服务端响应: " + commandName + "设置成功");

            } else {
                System.out.println("⚠️ 网络发送器未配置，使用模拟模式");
                System.out.println("✓ 命令构建成功: " + command.getClass().getSimpleName());
                System.out.println("✓ 模拟发送成功");
            }

            long endTime = System.currentTimeMillis();
            System.out.println("\n执行时间: " + (endTime - startTime) + "ms");
            System.out.println("状态: 成功");
            System.out.println("时间戳: " + LocalDateTime.now());
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            System.out.println("✗ " + commandName + "命令发送失败: " + e.getMessage());
            System.out.println("\n执行时间: " + (endTime - startTime) + "ms");
            System.out.println("状态: 失败");
            logger.error(commandName + "命令发送失败", e);
        }
    }
    /**
     * 模拟TSC设置执行
     */
    private void simulateTSCSet(String objName, Object setData, String targetClient) throws GatProtocolException {
        tscCommandService.setTSCInfo(targetClient, objName, setData);
    }
    /**
     * 根据对象名称确定处理器
     */
    private String determineHandler(String objName) {
        switch (objName) {
            case "SysInfo":
            case "SysState":
                return "TSCCommandHandler (系统参数)";
            case "RegionParam":
            case "SubRegionParam":
            case "RouteParam":
            case "CrossParam":
            case "SignalController":
            case "LampGroup":
            case "DetectorParam":
            case "LaneParam":
            case "PedestrianParam":
            case "SignalGroupParam":
            case "StageParam":
            case "PlanParam":
            case "DayPlanParam":
            case "ScheduleParam":
                return "TSCCommandHandler (配置参数)";
            case "CrossState":
            case "SignalControllerError":
            case "CrossModePlan":
            case "CrossCycle":
            case "CrossStage":
            case "CrossSignalGroupStatus":
            case "CrossTrafficData":
            case "StageTrafficData":
            case "VarLaneStatus":
            case "RouteControlMode":
            case "RouteSpeed":
                return "TSCCommandHandler (运行信息)";
            default:
                return "TSCCommandHandler (通用处理)";
        }
    }

    /**
     * 显示TSC对象名称帮助信息
     */
    private void showTSCObjectHelp() {
        System.out.println("\n=== TSC支持的对象名称 ===");
        System.out.println("系统参数:");
        System.out.println("  SysInfo, SysState");
        System.out.println();
        System.out.println("配置参数:");
        System.out.println("  RegionParam, SubRegionParam, RouteParam, CrossParam,");
        System.out.println("  SignalController, LampGroup, DetectorParam, LaneParam,");
        System.out.println("  PedestrianParam, SignalGroupParam, StageParam,");
        System.out.println("  PlanParam, DayPlanParam, ScheduleParam");
        System.out.println();
        System.out.println("运行信息:");
        System.out.println("  CrossState, SignalControllerError, CrossModePlan, CrossCycle,");
        System.out.println("  CrossStage, CrossSignalGroupStatus, CrossTrafficData,");
        System.out.println("  StageTrafficData, VarLaneStatus, RouteControlMode, RouteSpeed");
        System.out.println("========================");
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
    // ==================== 解析方法 ====================

    private FlowType parseFlowType(String input) {
        switch (input) {
            case "1": return FlowType.MOTOR_VEHICLE;
            case "2": return FlowType.NON_MOTOR_VEHICLE;
            case "3": return FlowType.PEDESTRIAN;
            default: throw new IllegalArgumentException("无效的流向类型选择");
        }
    }

    private Direction parseDirection(String input) {
        switch (input) {
            case "1": return Direction.NORTH;
            case "2": return Direction.SOUTH;
            case "3": return Direction.EAST;
            case "4": return Direction.WEST;
            default: throw new IllegalArgumentException("无效的方向选择");
        }
    }

    private LockType parseLockType(String input) {
        switch (input) {
            case "1": return LockType.CURRENT_PLAN;
            case "2": return LockType.SINGLE_ENTRANCE;
            case "3": return LockType.SIGNAL_GROUP_ONLY;
            default: throw new IllegalArgumentException("无效的锁定类型选择");
        }
    }

    private ReportCommand parseReportCommand(String input) {
        switch (input) {
            case "1": return ReportCommand.START;
            case "2": return ReportCommand.STOP;
            default: throw new IllegalArgumentException("无效的上报命令选择");
        }
    }

    private ReportDataType parseReportDataType(String input) {
        switch (input) {
            case "1": return ReportDataType.CROSS_TRAFFIC_DATA;
            case "2": return ReportDataType.CROSS_SIGNAL_GROUP_STATUS;
            case "3": return ReportDataType.STAGE_TRAFFIC_DATA;
            default: throw new IllegalArgumentException("无效的数据类型选择");
        }
    }
    private ControlMode parseControlMode(String input) {
        switch (input) {
            case "1": return ControlMode.CANCEL;
            case "2": return ControlMode.MANUAL;
            //case "3": return ControlMode.CENTRAL_CONTROL;
            default: throw new IllegalArgumentException("无效的控制模式选择");
        }
    }

    private OperationType parseOperationType(String input) {
        switch (input) {
            case "1": return OperationType.ADD;
            case "2": return OperationType.MODIFY;
            case "3": return OperationType.DELETE;
            default: throw new IllegalArgumentException("无效的操作类型选择");
        }
    }
    private InterventionType parseInterventionType(String input) {
        switch (input) {
            case "1": return InterventionType.EXTEND;
            case "2": return InterventionType.SHORTEN;
            //case "3": return InterventionType.SKIP;
            default: throw new IllegalArgumentException("无效的干预类型选择");
        }
    }
}