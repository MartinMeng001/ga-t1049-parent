package com.traffic.server.debug.recorder;

import com.traffic.gat1049.model.enums.MessageType;
import com.traffic.gat1049.protocol.codec.MessageCodec;
import com.traffic.gat1049.protocol.model.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息记录拦截器
 * 在消息处理的关键节点自动记录数据
 */
@Component
public class MessageRecordingInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MessageRecordingInterceptor.class);

    @Autowired
    private MessageDataRecorder messageDataRecorder;

    private final MessageCodec messageCodec = MessageCodec.getInstance();

    /**
     * 拦截客户端发送请求和接收响应
     * 在客户端发送请求并收到响应后调用
     */
    public void interceptClientRequestResponse(Message request, Message response) {
        try {
            String requestXml = messageCodec.encode(request);
            String responseXml = messageCodec.encode(response);

            messageDataRecorder.recordClientRequestResponse(request, response, requestXml, responseXml);
        } catch (Exception e) {
            logger.error("拦截客户端请求-响应失败", e);
        }
    }

    /**
     * 拦截服务端发送请求和接收响应
     * 在服务端发送请求并收到响应后调用
     */
    public void interceptServerRequestResponse(Message request, Message response) {
        try {
            String requestXml = messageCodec.encode(request);
            String responseXml = messageCodec.encode(response);

            messageDataRecorder.recordServerRequestResponse(request, response, requestXml, responseXml);
        } catch (Exception e) {
            logger.error("拦截服务端请求-响应失败", e);
        }
    }

    /**
     * 拦截推送消息
     * 在收到推送消息后调用
     */
    public void interceptPushMessage(Message pushMessage) {
        try {
            String pushXml = messageCodec.encode(pushMessage);

            // 检查是否为心跳消息
            if (isHeartbeatMessage(pushMessage)) {
                messageDataRecorder.recordHeartbeat(pushMessage, pushXml);
            } else {
                messageDataRecorder.recordPushMessage(pushMessage, pushXml);
            }
        } catch (Exception e) {
            logger.error("拦截推送消息失败", e);
        }
    }

    /**
     * 拦截原始XML消息
     * 当无法解析为Message对象时使用
     */
    public void interceptRawMessage(String xmlMessage, MessageType messageType, String objectName) {
        try {
            if (messageType == MessageType.PUSH) {
                if ("heartbeat".equalsIgnoreCase(objectName)) {
                    recordRawHeartbeat(xmlMessage);
                } else {
                    recordRawPush(xmlMessage, objectName);
                }
            }
        } catch (Exception e) {
            logger.error("拦截原始消息失败", e);
        }
    }

    /**
     * 检查是否为心跳消息
     */
    private boolean isHeartbeatMessage(Message message) {
        if (message == null || message.getBody() == null ||
                message.getBody().getOperations() == null ||
                message.getBody().getOperations().isEmpty()) {
            return false;
        }

        try {
            String operName = message.getBody().getOperations().get(0).getName();
            return "Heartbeat".equalsIgnoreCase(operName) || "心跳".equals(operName);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 记录原始心跳消息
     */
    private void recordRawHeartbeat(String xmlMessage) {
        // 创建一个简单的心跳消息记录
        messageDataRecorder.recordExecutor.execute(() -> {
            try {
                String fileName = String.format("push_heartbeat_%s.txt", getCurrentTimestamp());
                String content = String.format("=== RAW HEARTBEAT RECORD ===\n时间: %s\n\n--- RAW XML ---\n%s\n\n",
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                        xmlMessage);

                java.nio.file.Path filePath = java.nio.file.Paths.get("./message_records", fileName);
                java.nio.file.Files.write(filePath, content.getBytes("UTF-8"),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.WRITE);

                logger.debug("记录原始心跳消息: {}", fileName);
            } catch (Exception e) {
                logger.error("记录原始心跳消息失败", e);
            }
        });
    }

    /**
     * 记录原始推送消息
     */
    private void recordRawPush(String xmlMessage, String objectName) {
        messageDataRecorder.recordExecutor.execute(() -> {
            try {
                String fileName = String.format("push_%s_%s.txt", objectName, getCurrentTimestamp());
                String content = String.format("=== RAW PUSH RECORD ===\n时间: %s\n对象名: %s\n\n--- RAW XML ---\n%s\n\n",
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                        objectName, xmlMessage);

                java.nio.file.Path filePath = java.nio.file.Paths.get("./message_records", fileName);
                java.nio.file.Files.write(filePath, content.getBytes("UTF-8"),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.WRITE);

                logger.debug("记录原始推送消息: {}", fileName);
            } catch (Exception e) {
                logger.error("记录原始推送消息失败", e);
            }
        });
    }

    /**
     * 获取当前时间戳
     */
    private String getCurrentTimestamp() {
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
    }
}
