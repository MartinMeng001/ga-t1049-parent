package com.traffic.server.debug.recorder;

import com.traffic.gat1049.model.enums.MessageType;
import com.traffic.gat1049.protocol.model.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消息数据记录器
 * 用于记录所有的请求、响应、推送和心跳消息的原始数据
 */
@Component
public class MessageDataRecorder {

    private static final Logger logger = LoggerFactory.getLogger(MessageDataRecorder.class);

    protected final ExecutorService recordExecutor;
    private final String dataDirectory;
    private final DateTimeFormatter timeFormatter;

    public MessageDataRecorder() {
        this.recordExecutor = Executors.newFixedThreadPool(2);
        this.dataDirectory = "./message_records";
        this.timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        initializeDataDirectory();
    }

    /**
     * 初始化数据目录
     */
    private void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(dataDirectory);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                logger.info("创建消息记录目录: {}", dataDirectory);
            }
        } catch (IOException e) {
            logger.error("创建消息记录目录失败", e);
        }
    }

    /**
     * 记录客户端请求-响应对
     * @param request 客户端发送的请求
     * @param response 服务端返回的响应
     * @param requestXml 请求的原始XML
     * @param responseXml 响应的原始XML
     */
    public void recordClientRequestResponse(Message request, Message response,
                                            String requestXml, String responseXml) {
        recordExecutor.execute(() -> {
            try {
                String objectName = extractObjectName(request);
                String fileName = String.format("client_req_resp_%s_%s.txt",
                        objectName, getCurrentTimestamp());

                StringBuilder content = new StringBuilder();
                content.append("=== CLIENT REQUEST-RESPONSE RECORD ===\n");
                content.append("时间: ").append(LocalDateTime.now().format(timeFormatter)).append("\n");
                content.append("序列号: ").append(request.getSeq()).append("\n");
                content.append("对象名: ").append(objectName).append("\n\n");

                content.append("--- REQUEST XML ---\n");
                content.append(requestXml).append("\n\n");

                content.append("--- RESPONSE XML ---\n");
                content.append(responseXml).append("\n\n");

                writeToFile(fileName, content.toString());
                logger.debug("记录客户端请求-响应: {}", fileName);

            } catch (Exception e) {
                logger.error("记录客户端请求-响应失败", e);
            }
        });
    }

    /**
     * 记录服务端请求-响应对
     * @param request 服务端发送的请求
     * @param response 客户端返回的响应
     * @param requestXml 请求的原始XML
     * @param responseXml 响应的原始XML
     */
    public void recordServerRequestResponse(Message request, Message response,
                                            String requestXml, String responseXml) {
        recordExecutor.execute(() -> {
            try {
                String objectName = extractObjectName(request);
                String fileName = String.format("server_req_resp_%s_%s.txt",
                        objectName, getCurrentTimestamp());

                StringBuilder content = new StringBuilder();
                content.append("=== SERVER REQUEST-RESPONSE RECORD ===\n");
                content.append("时间: ").append(LocalDateTime.now().format(timeFormatter)).append("\n");
                content.append("序列号: ").append(request.getSeq()).append("\n");
                content.append("对象名: ").append(objectName).append("\n\n");

                content.append("--- REQUEST XML ---\n");
                content.append(requestXml).append("\n\n");

                content.append("--- RESPONSE XML ---\n");
                content.append(responseXml).append("\n\n");

                writeToFile(fileName, content.toString());
                logger.debug("记录服务端请求-响应: {}", fileName);

            } catch (Exception e) {
                logger.error("记录服务端请求-响应失败", e);
            }
        });
    }

    /**
     * 记录推送消息
     * @param pushMessage 推送消息
     * @param pushXml 推送的原始XML
     */
    public void recordPushMessage(Message pushMessage, String pushXml) {
        recordExecutor.execute(() -> {
            try {
                String objectName = extractObjectName(pushMessage);
                String fileName = String.format("push_%s_%s.txt",
                        objectName, getCurrentTimestamp());

                StringBuilder content = new StringBuilder();
                content.append("=== PUSH MESSAGE RECORD ===\n");
                content.append("时间: ").append(LocalDateTime.now().format(timeFormatter)).append("\n");
                content.append("序列号: ").append(pushMessage.getSeq()).append("\n");
                content.append("对象名: ").append(objectName).append("\n\n");

                content.append("--- PUSH XML ---\n");
                content.append(pushXml).append("\n\n");

                writeToFile(fileName, content.toString());
                logger.debug("记录推送消息: {}", fileName);

            } catch (Exception e) {
                logger.error("记录推送消息失败", e);
            }
        });
    }

    /**
     * 记录心跳消息
     * @param heartbeatMessage 心跳消息
     * @param heartbeatXml 心跳的原始XML
     */
    public void recordHeartbeat(Message heartbeatMessage, String heartbeatXml) {
        recordExecutor.execute(() -> {
            try {
                String fileName = String.format("push_heartbeat_%s.txt", getCurrentTimestamp());

                StringBuilder content = new StringBuilder();
                content.append("=== HEARTBEAT RECORD ===\n");
                content.append("时间: ").append(LocalDateTime.now().format(timeFormatter)).append("\n");
                content.append("序列号: ").append(heartbeatMessage.getSeq()).append("\n\n");

                content.append("--- HEARTBEAT XML ---\n");
                content.append(heartbeatXml).append("\n\n");

                writeToFile(fileName, content.toString());
                logger.debug("记录心跳消息: {}", fileName);

            } catch (Exception e) {
                logger.error("记录心跳消息失败", e);
            }
        });
    }

    /**
     * 提取对象名称
     */
    private String extractObjectName(Message message) {
        if (message == null || message.getBody() == null ||
                message.getBody().getOperations() == null ||
                message.getBody().getOperations().isEmpty()) {
            return "unknown";
        }

        try {
            // 获取第一个操作的数据对象类名作为对象名
            Object dataObject = message.getBody().getOperations().get(0).getData();
            if (dataObject != null) {
                String className = dataObject.getClass().getSimpleName();
                // 移除Sdo前缀，如SdoTimeServer -> TimeServer
                if (className.startsWith("Sdo")) {
                    className = className.substring(3);
                }
                return className;
            }
        } catch (Exception e) {
            logger.warn("提取对象名失败", e);
        }

        return "unknown";
    }

    /**
     * 获取当前时间戳用于文件名
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
    }

    /**
     * 写入文件
     */
    private void writeToFile(String fileName, String content) throws IOException {
        Path filePath = Paths.get(dataDirectory, fileName);
        Files.write(filePath, content.getBytes("UTF-8"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    /**
     * 关闭记录器
     */
    public void shutdown() {
        recordExecutor.shutdown();
        logger.info("消息数据记录器已关闭");
    }
}
