package com.traffic.gat1049.protocol.handler;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.protocol.model.Message;

/**
 * 协议处理器接口
 */
public interface ProtocolHandler {

    /**
     * 处理消息
     *
     * @param message 输入消息
     * @return 处理结果消息，如果不需要响应则返回null
     * @throws GatProtocolException 处理异常
     */
    Message handleMessage(Message message) throws GatProtocolException;

    /**
     * 检查是否支持处理指定类型的消息
     *
     * @param message 消息
     * @return 是否支持
     */
    boolean supports(Message message);

    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    String getHandlerName();
}
