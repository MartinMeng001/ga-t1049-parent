package com.traffic.gat1049.protocol.processor;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.protocol.handler.base.ProtocolHandler;
import com.traffic.gat1049.protocol.model.core.Message;

/**
 * 消息处理器接口
 */
public interface MessageProcessor {

    /**
     * 处理接收到的消息
     *
     * @param xmlContent XML消息内容
     * @return 处理结果的XML内容，如果不需要响应则返回null
     * @throws GatProtocolException 处理异常
     */
    String processMessage(String xmlContent) throws GatProtocolException;

    /**
     * 处理消息对象
     *
     * @param message 消息对象
     * @return 响应消息对象，如果不需要响应则返回null
     * @throws GatProtocolException 处理异常
     */
    Message processMessage(Message message) throws GatProtocolException;

    /**
     * 注册协议处理器
     *
     * @param handler 协议处理器
     */
    void registerHandler(ProtocolHandler handler);

    /**
     * 移除协议处理器
     *
     * @param handlerName 处理器名称
     */
    void removeHandler(String handlerName);
}
