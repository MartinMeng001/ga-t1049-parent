package com.traffic.server.service;

import com.traffic.gat1049.application.subscription.interfaces.SubscriptionService;
import com.traffic.gat1049.application.subscription.SubscriptionResult;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 服务端订阅服务实现
 * 服务端特定的订阅处理逻辑
 */
@Service
public class ServerSubscriptionServiceImpl implements SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(ServerSubscriptionServiceImpl.class);

    private final SubscriptionManager subscriptionManager;

    // 服务端支持的对象列表
    private static final List<String> SERVER_SUPPORTED_OBJECTS = Arrays.asList(
            "CrossCycle", "CrossModePlan", "SignalControllerError",
            "CrossState", "SysState", "CrossStage", "CrossSignalGroupStatus",
            "CrossTrafficData", "StageTrafficData", "VarLaneStatus"
    );

    public ServerSubscriptionServiceImpl(SubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public SubscriptionResult handleSubscribe(String token, SdoMsgEntity subscription, Message originalMessage) {
        try {
            logger.info("服务端处理订阅请求: token={}, objName={}", token, subscription.getObjName());

            // 使用现有的SubscriptionManager逻辑
            boolean success = subscriptionManager.subscribe(token, subscription);

            if (success) {
                logger.info("服务端订阅成功: token={}, objName={}", token, subscription.getObjName());
                return SubscriptionResult.success(subscription);
            } else {
                logger.warn("服务端订阅失败: token={}, objName={}", token, subscription.getObjName());
                return SubscriptionResult.error("SUBSCRIPTION_FAILED", "订阅失败");
            }

        } catch (Exception e) {
            logger.error("服务端订阅处理异常: token=" + token + ", objName=" + subscription.getObjName(), e);
            return SubscriptionResult.error("PROCESSING_ERROR", e.getMessage());
        }
    }

    @Override
    public SubscriptionResult handleUnsubscribe(String token, SdoMsgEntity subscription, Message originalMessage) {
        try {
            logger.info("服务端处理取消订阅请求: token={}, objName={}", token, subscription.getObjName());

            // 使用现有的SubscriptionManager逻辑
            boolean success = subscriptionManager.unsubscribe(token, subscription);

            if (success) {
                logger.info("服务端取消订阅成功: token={}, objName={}", token, subscription.getObjName());
                return SubscriptionResult.success(subscription);
            } else {
                logger.info("服务端取消订阅完成（订阅可能不存在）: token={}, objName={}", token, subscription.getObjName());
                // 取消订阅是幂等操作，即使订阅不存在也返回成功
                return SubscriptionResult.success(subscription);
            }

        } catch (Exception e) {
            logger.error("服务端取消订阅处理异常: token=" + token + ", objName=" + subscription.getObjName(), e);
            return SubscriptionResult.error("PROCESSING_ERROR", e.getMessage());
        }
    }

    @Override
    public boolean supportsObject(String objName) {
        return "*".equals(objName) || SERVER_SUPPORTED_OBJECTS.contains(objName);
    }

    @Override
    public List<String> getSupportedObjects() {
        return SERVER_SUPPORTED_OBJECTS;
    }
}
