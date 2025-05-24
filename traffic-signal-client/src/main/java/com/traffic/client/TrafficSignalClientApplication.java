package com.traffic.client;

import com.traffic.gat1049.model.entity.command.TSCCmd;
import com.traffic.gat1049.model.entity.runtime.CrossState;
import com.traffic.gat1049.model.entity.system.SysInfo;
import com.traffic.gat1049.network.client.GatTcpClient;
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
 * 交通信号控制系统客户端应用
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
            logger.info("Starting GA/T 1049.2 Traffic Signal Control Client...");

            // 连接到服务器
            client.connect();

            // 添加消息监听器
            client.getResponseHandler().addListener("default", message -> {
                logger.info("Received message: type={}, seq={}", message.getType(), message.getSeq());
            });

            // 启动交互式命令行
            startInteractiveConsole(client);
        };
    }

    private void startInteractiveConsole(GatTcpClient client) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            printHelp();

            while (client.isConnected()) {
                try {
                    System.out.print("\n> ");
                    String command = scanner.nextLine().trim();

                    if (command.isEmpty()) {
                        continue;
                    }

                    switch (command.toLowerCase()) {
                        case "help":
                        case "h":
                            printHelp();
                            break;

                        case "quit":
                        case "exit":
                        case "q":
                            logger.info("Exiting...");
                            client.disconnect();
                            System.exit(0);
                            break;

                        case "status":
                            System.out.println("Client connected: " + client.isConnected());
                            break;

                        case "sysinfo":
                            querySysInfo(client);
                            break;

                        case "crosslist":
                            queryCrossList(client);
                            break;

                        case "crossstate":
                            queryCrossState(client, scanner);
                            break;

                        case "planlist":
                            queryPlanList(client, scanner);
                            break;

                        case "trafficdata":
                            queryTrafficData(client, scanner);
                            break;

                        case "setmode":
                            setControlMode(client, scanner);
                            break;

                        case "lockflow":
                            lockFlowDirection(client, scanner);
                            break;

                        default:
                            System.out.println("Unknown command: " + command);
                            System.out.println("Type 'help' for available commands");
                    }

                } catch (Exception e) {
                    logger.error("Command execution error", e);
                }
            }

            scanner.close();
        }, "Console-Thread").start();
    }

    private void printHelp() {
        System.out.println("\n=== GA/T 1049.2 Client Commands ===");
        System.out.println("help (h)       - Show this help");
        System.out.println("quit (q)       - Exit the client");
        System.out.println("status         - Show connection status");
        System.out.println("sysinfo        - Query system information");
        System.out.println("crosslist      - Query cross list");
        System.out.println("crossstate     - Query cross state");
        System.out.println("planlist       - Query plan list for a cross");
        System.out.println("trafficdata    - Query traffic data for a cross");
        System.out.println("setmode        - Set control mode for a cross");
        System.out.println("lockflow       - Lock flow direction");
        System.out.println("===================================");
    }

    private void querySysInfo(GatTcpClient client) throws Exception {
        System.out.println("Querying system information...");

        TSCCmd cmd = new TSCCmd();
        cmd.setObjName("SysInfo");

        Message request = MessageBuilder.createQueryRequest("SysInfo", null, null);
        Message response = client.sendRequest(request, 10, TimeUnit.SECONDS);

        System.out.println("Response: " + response.getType());
        // TODO: Parse and display system info
    }

    private void queryCrossList(GatTcpClient client) throws Exception {
        System.out.println("Querying cross list...");

        Message request = MessageBuilder.createQueryRequest("CrossParam", null, null);
        Message response = client.sendRequest(request, 10, TimeUnit.SECONDS);

        System.out.println("Response: " + response.getType());
        // TODO: Parse and display cross list
    }

    private void queryCrossState(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter cross ID: ");
        String crossId = scanner.nextLine().trim();

        if (crossId.isEmpty()) {
            System.out.println("Cross ID cannot be empty");
            return;
        }

        System.out.println("Querying state for cross " + crossId + "...");

        Message request = MessageBuilder.createQueryRequest("CrossState", crossId, null);
        Message response = client.sendRequest(request, 10, TimeUnit.SECONDS);

        System.out.println("Response: " + response.getType());
        // TODO: Parse and display cross state
    }

    private void queryPlanList(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter cross ID: ");
        String crossId = scanner.nextLine().trim();

        if (crossId.isEmpty()) {
            System.out.println("Cross ID cannot be empty");
            return;
        }

        System.out.println("Querying plan list for cross " + crossId + "...");

        Message request = MessageBuilder.createQueryRequest("PlanParam", crossId, null);
        Message response = client.sendRequest(request, 10, TimeUnit.SECONDS);

        System.out.println("Response: " + response.getType());
        // TODO: Parse and display plan list
    }

    private void queryTrafficData(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter cross ID: ");
        String crossId = scanner.nextLine().trim();

        if (crossId.isEmpty()) {
            System.out.println("Cross ID cannot be empty");
            return;
        }

        System.out.println("Querying traffic data for cross " + crossId + "...");

        Message request = MessageBuilder.createQueryRequest("CrossTrafficData", crossId, null);
        Message response = client.sendRequest(request, 10, TimeUnit.SECONDS);

        System.out.println("Response: " + response.getType());
        // TODO: Parse and display traffic data
    }

    private void setControlMode(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter cross ID: ");
        String crossId = scanner.nextLine().trim();

        System.out.println("Control modes:");
        System.out.println("11 - Light off");
        System.out.println("12 - All red");
        System.out.println("13 - All yellow flash");
        System.out.println("21 - Single timing");
        System.out.println("22 - Single actuated");
        System.out.println("23 - Single adaptive");
        System.out.print("Enter control mode: ");
        String mode = scanner.nextLine().trim();

        Integer planNo = null;
        if (!"11".equals(mode) && !"12".equals(mode) && !"13".equals(mode)) {
            System.out.print("Enter plan number: ");
            planNo = Integer.parseInt(scanner.nextLine().trim());
        }

        System.out.println("Setting control mode...");

        // Create CrossModePlan object
        var modePlan = new com.traffic.gat1049.model.entity.runtime.CrossModePlan();
        modePlan.setCrossId(crossId);
        modePlan.setControlMode(com.traffic.gat1049.model.enums.ControlMode.fromCode(mode));
        modePlan.setPlanNo(planNo);

        Message request = MessageBuilder.createSetRequest(modePlan);
        Message response = client.sendRequest(request, 10, TimeUnit.SECONDS);

        System.out.println("Response: " + response.getType());
    }

    private void lockFlowDirection(GatTcpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter cross ID: ");
        String crossId = scanner.nextLine().trim();

        System.out.println("Flow types: 0=Pedestrian, 1=Motor vehicle, 2=Non-motor vehicle");
        System.out.print("Enter flow type: ");
        String type = scanner.nextLine().trim();

        System.out.println("Directions: 1=N, 2=NE, 3=E, 4=SE, 5=S, 6=SW, 7=W, 8=NW");
        System.out.print("Enter entrance direction: ");
        String entrance = scanner.nextLine().trim();

        System.out.print("Enter exit direction: ");
        String exit = scanner.nextLine().trim();

        System.out.print("Enter duration (seconds, 0 for permanent): ");
        int duration = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Locking flow direction...");

        // Create LockFlowDirection object
        var lockFlow = new com.traffic.gat1049.model.entity.command.LockFlowDirection();
        lockFlow.setCrossId(crossId);
        lockFlow.setType(com.traffic.gat1049.model.enums.FlowType.fromCode(type));
        lockFlow.setEntrance(com.traffic.gat1049.model.enums.Direction.fromCode(entrance));
        lockFlow.setExit(com.traffic.gat1049.model.enums.Direction.fromCode(exit));
        lockFlow.setLockType(com.traffic.gat1049.model.enums.LockType.CURRENT_PLAN);
        lockFlow.setDuration(duration);

        Message request = MessageBuilder.createSetRequest(lockFlow);
        Message response = client.sendRequest(request, 10, TimeUnit.SECONDS);

        System.out.println("Response: " + response.getType());
    }
}