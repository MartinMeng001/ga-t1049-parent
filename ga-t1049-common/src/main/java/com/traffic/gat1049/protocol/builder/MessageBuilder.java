package com.traffic.gat1049.protocol.builder;

import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.protocol.model.*;

import java.util.UUID;

/**
 * GA/T 1049.2 消息构建器
 */
public class MessageBuilder {

    private Message message;

    private MessageBuilder() {
        this.message = new Message();
        this.message.setVersion(GatConstants.PROTOCOL_VERSION);
        this.message.setSeq(generateSequence());
        this.message.setBody(new MessageBody());
    }

    public static MessageBuilder create() {
        return new MessageBuilder();
    }

    /**
     * 设置消息类型
     */
    public MessageBuilder type(String type) {
        this.message.setType(type);
        return this;
    }

    /**
     * 设置请求类型
     */
    public MessageBuilder request() {
        return type(GatConstants.MessageType.REQUEST);
    }

    /**
     * 设置响应类型
     */
    public MessageBuilder response() {
        return type(GatConstants.MessageType.RESPONSE);
    }

    /**
     * 设置推送类型
     */
    public MessageBuilder push() {
        return type(GatConstants.MessageType.PUSH);
    }

    /**
     * 设置错误类型
     */
    public MessageBuilder error() {
        return type(GatConstants.MessageType.ERROR);
    }

    /**
     * 设置令牌
     */
    public MessageBuilder token(String token) {
        this.message.setToken(token);
        return this;
    }

    /**
     * 设置序列号
     */
    public MessageBuilder seq(String seq) {
        this.message.setSeq(seq);
        return this;
    }

    /**
     * 设置源地址
     */
    public MessageBuilder from(String sys) {
        this.message.setFrom(new Address(sys));
        return this;
    }

    /**
     * 设置源地址
     */
    public MessageBuilder from(String sys, String subSys, String instance) {
        this.message.setFrom(new Address(sys, subSys, instance));
        return this;
    }

    /**
     * 设置目标地址
     */
    public MessageBuilder to(String sys) {
        this.message.setTo(new Address(sys));
        return this;
    }

    /**
     * 设置目标地址
     */
    public MessageBuilder to(String sys, String subSys, String instance) {
        this.message.setTo(new Address(sys, subSys, instance));
        return this;
    }

    /**
     * 设置TICP源地址
     */
    public MessageBuilder fromTicp() {
        return from(GatConstants.SystemAddress.TICP);
    }

    /**
     * 设置TSC源地址
     */
    public MessageBuilder fromTsc() {
        return from(GatConstants.SystemAddress.TSC);
    }

    /**
     * 设置TICP目标地址
     */
    public MessageBuilder toTicp() {
        return to(GatConstants.SystemAddress.TICP);
    }

    /**
     * 设置TSC目标地址
     */
    public MessageBuilder toTsc() {
        return to(GatConstants.SystemAddress.TSC);
    }

    /**
     * 添加操作
     */
    public MessageBuilder operation(String name, Object data) {
        Operation operation = new Operation(1, name, data);
        this.message.getBody().addOperation(operation);
        return this;
    }

    /**
     * 添加Get操作
     */
    public MessageBuilder get(Object data) {
        return operation(GatConstants.Operation.GET, data);
    }

    /**
     * 添加Set操作
     */
    public MessageBuilder set(Object data) {
        return operation(GatConstants.Operation.SET, data);
    }

    /**
     * 添加Notify操作
     */
    public MessageBuilder notify(Object data) {
        return operation(GatConstants.Operation.NOTIFY, data);
    }

    /**
     * 构建消息
     */
    public Message build() {
        return this.message;
    }

    /**
     * 生成序列号
     */
    private String generateSequence() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 创建查询请求消息
     */
    public static Message createQueryRequest(String objName, String id, Integer no) {
        return MessageBuilder.create()
                .request()
                .fromTicp()
                .toTsc()
                .get(createTscCmd(objName, id, no))
                .build();
    }

    /**
     * 创建设置请求消息
     */
    public static Message createSetRequest(Object data) {
        return MessageBuilder.create()
                .request()
                .fromTicp()
                .toTsc()
                .set(data)
                .build();
    }

    /**
     * 创建推送通知消息
     */
    public static Message createPushNotification(Object data) {
        return MessageBuilder.create()
                .push()
                .fromTsc()
                .toTicp()
                .notify(data)
                .build();
    }

    /**
     * 创建响应消息
     */
    public static Message createResponse(String seq, Object data) {
        return MessageBuilder.create()
                .response()
                .fromTsc()
                .toTicp()
                .seq(seq)
                .get(data)
                .build();
    }

    /**
     * 创建错误响应消息
     */
    public static Message createErrorResponse(String seq, String errorCode, String errorMessage) {
        // 这里可以创建错误对象，暂时简化处理
        return MessageBuilder.create()
                .error()
                .fromTsc()
                .toTicp()
                .seq(seq)
                .operation("Error", createErrorObject(errorCode, errorMessage))
                .build();
    }

    /**
     * 创建TSCCmd对象
     */
    private static Object createTscCmd(String objName, String id, Integer no) {
        // 这里应该返回TSCCmd对象，暂时用简单对象代替
        return new Object() {
            public String getObjName() { return objName; }
            public String getId() { return id; }
            public Integer getNo() { return no; }
        };
    }

    /**
     * 创建错误对象
     */
    private static Object createErrorObject(String code, String message) {
        return new Object() {
            public String getCode() { return code; }
            public String getMessage() { return message; }
        };
    }
}
