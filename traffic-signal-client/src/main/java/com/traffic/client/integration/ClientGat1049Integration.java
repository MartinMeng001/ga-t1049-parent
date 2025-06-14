package com.traffic.client.integration;

import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.model.enums.SystemType;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.codec.MessageCodec;
import com.traffic.gat1049.protocol.model.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端GA/T 1049.1集成示例
 */
@Component
public class ClientGat1049Integration {
    private static final Logger logger = LoggerFactory.getLogger(ClientGat1049Integration.class);
    private MessageCodec messageCodec;
    private ScheduledExecutorService heartbeatExecutor;
    private String currentToken;
    private boolean connected = false;

    @Autowired
    private SessionManager sessionManager;

    @PostConstruct
    public void initialize() throws Exception {
        messageCodec = MessageCodec.getInstance();
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

        // 自动登录
        login("tsc_client", "tsc123");

        // 启动心跳
        startHeartbeat();

        logger.info("GA/T 1049.1客户端集成初始化完成");
    }

    /**
     * 登录到服务端
     */
    public boolean login(String username, String password) {
        try {
            Message loginRequest = MessageBuilder.createLoginRequest(username, password);
            String requestXml = messageCodec.encode(loginRequest);

            // 发送登录请求到服务端（这里需要实际的网络通信实现）
            String responseXml = sendToServer(requestXml);

            if (responseXml != null) {
                Message loginResponse = messageCodec.decode(responseXml);
                if ("RESPONSE".equals(loginResponse.getType())) {
                    currentToken = loginResponse.getToken();
                    connected = true;
                    logger.info("登录成功，获得Token");
                    sessionManager.registerLogin(username, SystemType.UTCS, "127.0.0.1", currentToken);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("登录失败", e);
        }
        return false;
    }

    /**
     * 订阅消息
     */
    public boolean subscribe(String msgType, String operName, String objName) {
        if (!connected || currentToken == null) {
            logger.warn("未连接或未登录，无法订阅");
            return false;
        }

        try {
            Message subscribeRequest = MessageBuilder.createSubscribeRequest(currentToken, msgType, operName, objName);
            String requestXml = messageCodec.encode(subscribeRequest);
            String responseXml = sendToServer(requestXml);

            if (responseXml != null) {
                Message subscribeResponse = messageCodec.decode(responseXml);
                return "RESPONSE".equals(subscribeResponse.getType());
            }
        } catch (Exception e) {
            logger.error("订阅失败", e);
        }
        return false;
    }

    /**
     * 发送心跳
     */
    private void sendHeartbeat() {
        if (!connected || currentToken == null) {
            return;
        }

        try {
            Message heartbeat = MessageBuilder.createHeartbeatMessage(currentToken);
            String heartbeatXml = messageCodec.encode(heartbeat);
            sendToServer(heartbeatXml);
            logger.debug("发送心跳");
        } catch (Exception e) {
            logger.error("发送心跳失败", e);
        }
    }

    /**
     * 启动心跳定时器
     */
    private void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(this::sendHeartbeat, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 发送消息到服务端（需要实际实现）
     */
    private String sendToServer(String xmlMessage) {
        // 这里应该实现实际的网络通信
        // 例如通过TCP连接发送到服务端
        logger.debug("发送消息到服务端: {}", xmlMessage);
        return null; // 模拟返回
    }
}
