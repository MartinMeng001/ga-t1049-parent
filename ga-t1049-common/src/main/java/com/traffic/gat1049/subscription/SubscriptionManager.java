package com.traffic.gat1049.subscription;

import com.traffic.gat1049.model.entity.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 订阅管理器
 * 负责管理客户端的消息订阅
 */
public class SubscriptionManager {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionManager.class);

    // 订阅存储：token -> 订阅列表
    private final ConcurrentHashMap<String, List<SdoMsgEntity>> subscriptions = new ConcurrentHashMap<>();

    // 消息推送回调接口
    private MessagePushCallback pushCallback;

    public SubscriptionManager() {}

    public void setPushCallback(MessagePushCallback pushCallback) {
        this.pushCallback = pushCallback;
    }

    /**
     * 添加订阅
     */
    public boolean subscribe(String token, SdoMsgEntity subscription) {
        if (token == null || subscription == null) {
            return false;
        }

        List<SdoMsgEntity> userSubscriptions = subscriptions.computeIfAbsent(
                token, k -> new CopyOnWriteArrayList<>());

        // 检查是否已存在相同订阅
        for (SdoMsgEntity existing : userSubscriptions) {
            if (isMatchingSubscription(existing, subscription)) {
                logger.debug("订阅已存在: {}", subscription);
                return true;
            }
        }

        userSubscriptions.add(subscription);
        logger.info("添加订阅: token={}, subscription={}", token, subscription);
        return true;
    }

    /**
     * 取消订阅
     */
    public boolean unsubscribe(String token, SdoMsgEntity subscription) {
        if (token == null || subscription == null) {
            return false;
        }

        List<SdoMsgEntity> userSubscriptions = subscriptions.get(token);
        if (userSubscriptions == null) {
            return false;
        }

        boolean removed = userSubscriptions.removeIf(existing ->
                isMatchingSubscription(existing, subscription));

        if (removed) {
            logger.info("取消订阅: token={}, subscription={}", token, subscription);
        }

        return removed;
    }

    /**
     * 推送消息给订阅者
     */
    public void pushMessage(Message message) {
        if (message == null || pushCallback == null) {
            return;
        }

        String msgType = message.getType();
        String operName = getOperationName(message);
        String objName = getObjectName(message);

        for (String token : subscriptions.keySet()) {
            List<SdoMsgEntity> userSubscriptions = subscriptions.get(token);
            if (userSubscriptions == null) {
                continue;
            }

            for (SdoMsgEntity subscription : userSubscriptions) {
                if (isMatchingMessage(subscription, msgType, operName, objName)) {
                    try {
                        pushCallback.pushMessage(token, message);
                        logger.debug("推送消息给订阅者: token={}, message={}", token, message.getSeq());
                    } catch (Exception e) {
                        logger.error("推送消息失败: token=" + token, e);
                    }
                    break; // 找到匹配的订阅后跳出循环
                }
            }
        }
    }

    /**
     * 清理用户订阅
     */
    public void clearSubscriptions(String token) {
        List<SdoMsgEntity> removed = subscriptions.remove(token);
        if (removed != null && !removed.isEmpty()) {
            logger.info("清理用户订阅: token={}, count={}", token, removed.size());
        }
    }

    /**
     * 获取用户订阅列表
     */
    public List<SdoMsgEntity> getSubscriptions(String token) {
        List<SdoMsgEntity> userSubscriptions = subscriptions.get(token);
        return userSubscriptions != null ? new CopyOnWriteArrayList<>(userSubscriptions) : new CopyOnWriteArrayList<>();
    }

    private boolean isMatchingSubscription(SdoMsgEntity existing, SdoMsgEntity subscription) {
        return existing.getMsgType().equals(subscription.getMsgType()) &&
                existing.getOperName().equals(subscription.getOperName()) &&
                existing.getObjName().equals(subscription.getObjName());
    }

    private boolean isMatchingMessage(SdoMsgEntity subscription, String msgType, String operName, String objName) {
        return (subscription.getMsgType().equals("*") || subscription.getMsgType().equals(msgType)) &&
                (subscription.getOperName().equals("*") || subscription.getOperName().equals(operName)) &&
                (subscription.getObjName().equals("*") || subscription.getObjName().equals(objName));
    }

    private String getOperationName(Message message) {
        if (message.getBody() != null && message.getBody().getOperations() != null &&
                !message.getBody().getOperations().isEmpty()) {
            return message.getBody().getOperations().get(0).getName();
        }
        return "";
    }

    private String getObjectName(Message message) {
        // 这里需要从操作的数据对象中提取对象名称
        // 简化处理，返回空字符串
        return "";
    }

    /**
     * 消息推送回调接口
     */
    public interface MessagePushCallback {
        void pushMessage(String token, Message message);
    }
}
