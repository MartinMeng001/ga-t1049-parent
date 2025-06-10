package com.traffic.gat1049.protocol.builder;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.protocol.model.*;
import com.traffic.gat1049.protocol.util.ProtocolUtils;

import java.util.UUID;

/**
 * GA/T 1049.1消息构建器
 * 符合标准的消息构建工具
 */
public class MessageBuilder {

    private Message message;

//    private MessageBuilder() {
//        this.message = new Message();
//        this.message.setVersion(GatConstants.PROTOCOL_VERSION);
//        this.message.setBody(new MessageBody());
//    }
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
        this.message.setToken(token != null ? token : "");
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
     * 设置源地址 - 仅系统类型
     */
    public MessageBuilder from(String sys) {
        this.message.setFrom(new Address(sys));
        return this;
    }

    /**
     * 设置源地址 - 完整地址
     */
    public MessageBuilder from(String sys, String subSys, String instance) {
        this.message.setFrom(new Address(sys, subSys, instance));
        return this;
    }

    /**
     * 设置目标地址 - 仅系统类型
     */
    public MessageBuilder to(String sys) {
        this.message.setTo(new Address(sys));
        return this;
    }

    /**
     * 设置目标地址 - 完整地址
     */
    public MessageBuilder to(String sys, String subSys, String instance) {
        this.message.setTo(new Address(sys, subSys, instance));
        return this;
    }

    /**
     * 设置TICP源地址
     */
    public MessageBuilder fromTicp() {
        return from(GatConstants.SystemAddress.TICP, null, null);
    }

    /**
     * 设置UTCS源地址
     */
    public MessageBuilder fromUtcs() {
        return from(GatConstants.SystemAddress.UTCS);
    }

    /**
     * 设置TICP目标地址
     */
    public MessageBuilder toTicp() {
        return to(GatConstants.SystemAddress.TICP, null, null);
    }

    /**
     * 设置UTCS目标地址
     */
    public MessageBuilder toUtcs() {
        return to(GatConstants.SystemAddress.UTCS);
    }

    /**
     * 添加操作
     */
    public MessageBuilder operation(String name, Object data) {
        int order = this.message.getBody().getOperations().size() + 1;
        Operation operation = new Operation(order, name, data);
        this.message.getBody().addOperation(operation);
        return this;
    }

    /**
     * 添加Login操作
     */
    public MessageBuilder login(Object data) {
        return operation(GatConstants.Operation.LOGIN, data);
    }

    /**
     * 添加Logout操作
     */
    public MessageBuilder logout(Object data) {
        return operation(GatConstants.Operation.LOGOUT, data);
    }

    /**
     * 添加Subscribe操作
     */
    public MessageBuilder subscribe(Object data) {
        return operation(GatConstants.Operation.SUBSCRIBE, data);
    }

    /**
     * 添加Unsubscribe操作
     */
    public MessageBuilder unsubscribe(Object data) {
        return operation(GatConstants.Operation.UNSUBSCRIBE, data);
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
    // ========== 静态工厂方法 ==========

    /**
     * 创建登录请求消息
     */
    public static Message createLoginRequest(String userName, String password) {
        com.traffic.gat1049.model.entity.sdo.SdoUser user =
                new com.traffic.gat1049.model.entity.sdo.SdoUser(userName, password);

        return MessageBuilder.create()
                .request()
                .fromUtcs()
                .toTicp()
                .login(user)
                .build();
    }

    /**
     * 创建登录成功响应
     */
    public static Message createLoginSuccessResponse(String seq, String token, String userName) {
        com.traffic.gat1049.model.entity.sdo.SdoUser user =
                new com.traffic.gat1049.model.entity.sdo.SdoUser(userName, "");

        return MessageBuilder.create()
                .response()
                .fromTicp()
                .toUtcs()
                .seq(seq)
                .token(token)
                .login(user)
                .build();
    }

    /**
     * 创建登录失败响应
     */
    public static Message createLoginErrorResponse(String seq, String errorType, String errorDesc) {
        com.traffic.gat1049.model.entity.sdo.SdoError error =
                new com.traffic.gat1049.model.entity.sdo.SdoError(
                        GatConstants.SystemObject.SDO_USER, errorType, errorDesc);

        return MessageBuilder.create()
                .error()
                .fromTicp()
                .toUtcs()
                .seq(seq)
                .operation("Login", error)
                .build();
    }

    /**
     * 创建心跳消息
     */
    public static Message createHeartbeatMessage(String token) {
        com.traffic.gat1049.model.entity.sdo.SdoHeartBeat heartbeat =
                new com.traffic.gat1049.model.entity.sdo.SdoHeartBeat();

        return MessageBuilder.create()
                .push()
                .fromUtcs()
                .toTicp()
                .token(token)
                .notify(heartbeat)
                .build();
    }

    /**
     * 创建订阅请求消息
     */
    public static Message createSubscribeRequest(String token, String msgType, String operName, String objName) {
        com.traffic.gat1049.model.entity.sdo.SdoMsgEntity subscription =
                new com.traffic.gat1049.model.entity.sdo.SdoMsgEntity(msgType, operName, objName);

        return MessageBuilder.create()
                .request()
                .fromUtcs()
                .toTicp()
                .token(token)
                .subscribe(subscription)
                .build();
    }

    /**
     * 创建查询请求消息
     */
    public static Message createQueryRequest(String token, Object queryData) {
        return MessageBuilder.create()
                .request()
                .fromUtcs()
                .toTicp()
                .token(token)
                .get(queryData)
                .build();
    }

    /**
     * 创建设置请求消息
     */
    public static Message createSetRequest(String token, Object setData) {
        return MessageBuilder.create()
                .request()
                .fromUtcs()
                .toTicp()
                .token(token)
                .set(setData)
                .build();
    }
    /**
     * 创建通用成功响应消息
     */
    public static Message createSuccessResponse(Message request, Object data) {
        return MessageBuilder.create()
                .response()
                .fromUtcs()  // 注意：响应应该从TICP发回UTCS
                .toTicp()
                .seq(request.getSeq())
                .token(request.getToken())
                .operation(ProtocolUtils.getOperationName(request), data)
                .build();
    }
    /**
     * 创建错误响应消息
     */
    public static Message createErrorResponse(String seq, String token, String errorCode, String errorMessage) {
        com.traffic.gat1049.model.entity.sdo.SdoError error =
                new com.traffic.gat1049.model.entity.sdo.SdoError("", errorCode, errorMessage);

        return MessageBuilder.create()
                .error()
                .fromTicp()
                .toUtcs()
                .seq(seq)
                .token(token)
                .operation("Error", error)
                .build();
    }
}
