package com.traffic.client.service;

import com.traffic.gat1049.application.subscription.interfaces.SubscriptionService;
import com.traffic.gat1049.application.subscription.SubscriptionResult;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 客户端订阅服务实现
 * 客户端特定的订阅处理逻辑
 */
@Service
public class ClientSubscriptionService implements SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(ClientSubscriptionService.class);

    private final ClientDataPushService pushService;

    // 客户端支持的对象列表
    private static final List<String> CLIENT_SUPPORTED_OBJECTS = Arrays.asList(
            "CrossCycle", "CrossModePlan", "SignalControllerError",
            "CrossState", "SysState", "CrossStage", "CrossSignalGroupStatus",
            "CrossTrafficData", "StageTrafficData", "VarLaneStatus"
    );

    public ClientSubscriptionService(ClientDataPushService pushService) {
        this.pushService = pushService;
    }

    @Override
    public SubscriptionResult handleSubscribe(String token, SdoMsgEntity subscription, Message originalMessage) {
        try {
            logger.info("客户端处理订阅请求: token={}, objName={}", token, subscription.getObjName());

            // 使用现有的ClientDataPushService逻辑
            Message response = pushService.handleSubscriptionRequest(originalMessage);

            if (response != null && !"ERROR".equals(response.getType())) {
                logger.info("客户端订阅成功: objName={}", subscription.getObjName());
                return SubscriptionResult.success(subscription);
            } else {
                logger.warn("客户端订阅失败: objName={}", subscription.getObjName());
                return SubscriptionResult.error("SUBSCRIPTION_FAILED", "订阅处理失败");
            }

        } catch (Exception e) {
            logger.error("客户端订阅处理异常: objName=" + subscription.getObjName(), e);
            return SubscriptionResult.error("PROCESSING_ERROR", e.getMessage());
        }
    }

    @Override
    public SubscriptionResult handleUnsubscribe(String token, SdoMsgEntity subscription, Message originalMessage) {
        try {
            logger.info("客户端处理取消订阅请求: token={}, objName={}", token, subscription.getObjName());

            // 使用现有的ClientDataPushService逻辑
            Message response = pushService.handleUnsubscriptionRequest(originalMessage);

            if (response != null && !"ERROR".equals(response.getType())) {
                logger.info("客户端取消订阅成功: objName={}", subscription.getObjName());
                return SubscriptionResult.success(subscription);
            } else {
                logger.warn("客户端取消订阅失败: objName={}", subscription.getObjName());
                return SubscriptionResult.error("UNSUBSCRIPTION_FAILED", "取消订阅处理失败");
            }

        } catch (Exception e) {
            logger.error("客户端取消订阅处理异常: objName=" + subscription.getObjName(), e);
            return SubscriptionResult.error("PROCESSING_ERROR", e.getMessage());
        }
    }

    @Override
    public boolean supportsObject(String objName) {
        return "*".equals(objName) || CLIENT_SUPPORTED_OBJECTS.contains(objName);
    }

    @Override
    public List<String> getSupportedObjects() {
        return CLIENT_SUPPORTED_OBJECTS;
    }
}
