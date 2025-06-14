package com.traffic.client.service;

import com.traffic.gat1049.protocol.model.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 客户端消息处理服务
 */
@Service
public class ClientMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientMessageHandler.class);

    public void handleMessage(Message message) {
        logger.info("收到GA/T 1049消息: type={}, seq={}", message.getType(), message.getSeq());

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

    private void handlePushMessage(Message message) {
        logger.info("收到推送消息: seq={}", message.getSeq());
        // 处理服务端主动推送的消息
    }

    private void handleErrorMessage(Message message) {
        logger.warn("收到错误消息: seq={}", message.getSeq());
        // 处理错误响应
    }
}
