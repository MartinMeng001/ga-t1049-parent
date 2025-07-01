package com.traffic.gat1049.application.subscription.interfaces;

import com.traffic.gat1049.application.subscription.SubscriptionResult;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;

/**
 * 订阅处理服务接口
 * 不同工程可以实现此接口来提供自己的订阅处理逻辑
 */
public interface SubscriptionService {

    /**
     * 处理订阅请求
     * @param token 用户令牌
     * @param subscription 订阅实体
     * @param originalMessage 原始请求消息
     * @return 处理结果
     */
    SubscriptionResult handleSubscribe(String token, SdoMsgEntity subscription, Message originalMessage);

    /**
     * 处理取消订阅请求
     * @param token 用户令牌
     * @param subscription 订阅实体
     * @param originalMessage 原始请求消息
     * @return 处理结果
     */
    SubscriptionResult handleUnsubscribe(String token, SdoMsgEntity subscription, Message originalMessage);

    /**
     * 检查是否支持指定的订阅对象
     * @param objName 对象名称
     * @return 是否支持
     */
    boolean supportsObject(String objName);

    /**
     * 获取支持的对象名称列表
     * @return 支持的对象名称列表
     */
    java.util.List<String> getSupportedObjects();
}
