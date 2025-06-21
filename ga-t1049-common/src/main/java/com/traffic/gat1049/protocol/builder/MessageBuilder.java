package com.traffic.gat1049.protocol.builder;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.core.Address;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.core.MessageBody;
import com.traffic.gat1049.protocol.model.core.Operation;
import com.traffic.gat1049.protocol.model.sdo.SdoError;
import com.traffic.gat1049.protocol.model.sdo.SdoHeartBeat;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.model.sdo.SdoUser;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.util.ResultHandlingUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * GA/T 1049.1消息构建器
 * 符合标准的消息构建工具
 */
public class MessageBuilder {

    private Message message;

    private MessageBuilder() {
        this.message = new Message();
        this.message.setVersion(GatConstants.PROTOCOL_VERSION);
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
     * 创建登录请求消息
     */
    public static Message createLoginRequest(String userName, String password) {
        SdoUser user =
                new SdoUser(userName, password);

        return MessageBuilder.create()
                .request()
                .fromUtcs()
                .toTicp()
                .login(user)
                .build();
    }
    public static Message createLogoutRequest(String userName, String password, String token) {
        SdoUser user =
                new SdoUser(userName, password);

        return MessageBuilder.create()
                .request()
                .fromUtcs()
                .toTicp()
                .token(token)
                .logout(user)
                .build();
    }
    /**
     * 创建登录成功响应
     */
    public static Message createLoginSuccessResponse(Message request, String token, String userName) {
        SdoUser user =
                new SdoUser(userName, "");

        return MessageBuilder.create()
                .response()
                .reverseAddress(request)
                .seq(request.getSeq())
                .token(token)
                .login(user)
                .build();
    }

    /**
     * 创建登录失败响应
     */
    public static Message createLoginErrorResponse(Message request, String errorType, String errorDesc) {
        SdoError error =
                new SdoError(
                        GatConstants.SystemObject.SDO_USER, errorType, errorDesc);

        return MessageBuilder.create()
                .error()
                .reverseAddress(request)
                .seq(request.getSeq())
                .operation("Login", error)
                .build();
    }

    /**
     * 创建心跳消息
     */
    public static Message createHeartbeatMessage(String token) {
        SdoHeartBeat heartbeat =
                new SdoHeartBeat();

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
        SdoMsgEntity subscription =
                new SdoMsgEntity(msgType, operName, objName);

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
                .fromTicp()
                .toUtcs()
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
                .reverseAddress(request)
                .seq(request.getSeq())
                .token(request.getToken())
                .operation(ProtocolUtils.getOperationName(request), data)
                .build();
    }
    /**
     * 创建错误响应消息
     */
    public static Message createErrorResponse(String seq, String token, String errorCode, String errorMessage) {
        SdoError error =
                new SdoError("", errorCode, errorMessage);

        return MessageBuilder.create()
                .error()
                .fromTicp()
                .toUtcs()
                .seq(seq)
                .token(token)
                .operation("Error", error)
                .build();
    }


    /**
     * 响应构建器 - 专门用于构建响应消息
     */
    public static ResponseBuilder responseFor(Message request) {
        return new ResponseBuilder(request);
    }
    /**
     * 响应构建器内部类
     * 在ResponseBuilder中添加对MultiDataWrapper的支持
     */
    public static class ResponseBuilder {
        private final Message request;
        private final MessageBuilder builder;

        public ResponseBuilder(Message request) {
            this.request = request;
            this.builder = MessageBuilder.create()
                    .seq(request.getSeq())
                    .token(request.getToken())
                    .reverseAddress(request); // 使用reverseAddress，最合理的方式
        }

        /**
         * 原有的成功响应方法
         */
        public ResponseBuilder success(Object data) {
            return success(ProtocolUtils.getOperationName(request), data);
        }

        /**
         * 增强的成功响应方法 - 支持MultiDataWrapper
         */
        public ResponseBuilder success(String operation, Object data) {
            // 检查是否为MultiDataWrapper，需要特殊处理
            if (ResultHandlingUtils.isMultiDataWrapper(data)) {
                return successWithMultiData(operation, (ResultHandlingUtils.MultiDataWrapper) data);
            } else {
                // 原有的处理方式
                builder.response().operation(operation, data);
                return this;
            }
        }

        /**
         * 处理多数据对象的成功响应
         */
        private ResponseBuilder successWithMultiData(String operation, ResultHandlingUtils.MultiDataWrapper wrapper) {
            builder.response();

            // 检查Operation是否支持多数据对象
            if (ProtocolUtils.supportsMultipleData()) {
                // 创建多数据对象Operation
                List<Object> dataList = wrapper.getDataList();
                Operation multiDataOperation = ProtocolUtils.createOperationWithMultipleData(
                        1, operation, dataList);
                builder.message.getBody().addOperation(multiDataOperation);
            } else {
                // 单数据对象版本：取第一个元素或保持为列表
                List<Object> dataList = wrapper.getDataList();
                Object singleData;
                if (dataList.isEmpty()) {
                    singleData = null;
                } else if (dataList.size() == 1) {
                    singleData = dataList.get(0);
                } else {
                    singleData = new ArrayList<>(dataList);
                }
                builder.operation(operation, singleData);
            }

            return this;
        }

        /**
         * 原有的错误响应方法
         */
        public ResponseBuilder error(String errorCode, String errorMessage) {
            SdoError error = new SdoError("", errorCode, errorMessage);
            builder.error().operation("Error", error);
            return this;
        }

        /**
         * 便捷的批量数据响应方法
         */
        public ResponseBuilder batch(Collection<?> dataCollection) {
            ResultHandlingUtils.MultiDataWrapper wrapper =
                    new ResultHandlingUtils.MultiDataWrapper(dataCollection);
            return success(wrapper);
        }

        /**
         * 便捷的批量数据响应方法（指定操作名）
         */
        public ResponseBuilder batch(String operation, Collection<?> dataCollection) {
            ResultHandlingUtils.MultiDataWrapper wrapper =
                    new ResultHandlingUtils.MultiDataWrapper(dataCollection);
            return success(operation, wrapper);
        }

        /**
         * 构建最终消息
         */
        public Message build() {
            return builder.build();
        }
    }
    private MessageBuilder reverseAddress(Message request) {
        // 自动处理响应的地址方向，避免手动错误
        this.message.setFrom(request.getTo());
        this.message.setTo(request.getFrom());
        return this;
    }
}
