package com.traffic.gat1049.protocol.builder;

import com.traffic.gat1049.protocol.model.core.Address;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.core.MessageBody;
import com.traffic.gat1049.protocol.model.core.Operation;
import com.traffic.gat1049.protocol.constants.GatConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 独立的推送消息构建器
 * 专门用于构建推送消息，与现有的MessageBuilder保持独立
 * 提供与ResponseBuilder相似的API设计
 *
 * 修复版本：将数据处理逻辑内置，避免与PushHandlingUtils的循环依赖
 */
public class PushBuilder {

    private final Message message;
    private int operationOrder = 1;

    // ==================== 构造函数和静态工厂方法 ====================

    private PushBuilder() {
        this.message = new Message();
        this.message.setVersion(GatConstants.PROTOCOL_VERSION);
        this.message.setType(GatConstants.MessageType.PUSH);
        this.message.setBody(new MessageBody());
        this.message.setSeq(Message.generateSequence());
    }

    /**
     * 创建推送构建器 - 指定源系统
     */
    public static PushBuilder from(String fromSys) {
        PushBuilder builder = new PushBuilder();
        builder.message.setFrom(new Address(fromSys));
        return builder;
    }

    /**
     * 创建推送构建器 - 完整源地址
     */
    public static PushBuilder from(String fromSys, String fromSubSys, String fromInstance) {
        PushBuilder builder = new PushBuilder();
        builder.message.setFrom(new Address(fromSys, fromSubSys, fromInstance));
        return builder;
    }

    /**
     * 客户端推送构建器 - 便捷方法
     */
    public static PushBuilder clientPush() {
        return from(GatConstants.SystemAddress.UTCS);
    }
    public static PushBuilder clientPush(String username) {
        return from(GatConstants.SystemAddress.UTCS, username, "");
    }

    /**
     * 服务端推送构建器 - 便捷方法
     */
    public static PushBuilder serverPush() {
        return from(GatConstants.SystemAddress.TICP);
    }

    // ==================== 地址设置方法 ====================

    /**
     * 设置目标地址 - 仅系统类型
     */
    public PushBuilder to(String toSys) {
        this.message.setTo(new Address(toSys));
        return this;
    }

    /**
     * 设置目标地址 - 完整地址
     */
    public PushBuilder to(String toSys, String toSubSys, String toInstance) {
        this.message.setTo(new Address(toSys, toSubSys, toInstance));
        return this;
    }

    /**
     * 设置目标为TICP
     */
    public PushBuilder toTicp() {
        return to(GatConstants.SystemAddress.TICP, null, null);
    }

    /**
     * 设置目标为TICP - 指定用户名
     */
    public PushBuilder toTicp(String username) {
        return to(GatConstants.SystemAddress.TICP, username, null);
    }

    /**
     * 设置目标为UTCS
     */
    public PushBuilder toUtcs() {
        return to(GatConstants.SystemAddress.UTCS);
    }

    /**
     * 设置目标为UTCS - 指定用户名
     */
    public PushBuilder toUtcs(String username) {
        return to(GatConstants.SystemAddress.UTCS, username, "");
    }

    // ==================== 消息属性设置方法 ====================

    /**
     * 设置Token
     */
    public PushBuilder token(String token) {
        this.message.setToken(token != null ? token : "");
        return this;
    }

    /**
     * 设置序列号
     */
    public PushBuilder seq(String seq) {
        this.message.setSeq(seq);
        return this;
    }

    // ==================== 内部数据处理方法 ====================

    /**
     * 内部数据处理方法 - 避免循环依赖
     * 将ArrayList等复杂类型转换为JAXB可序列化的格式
     */
    private static Object processDataForPush(Object data) {
        if (data == null) {
            return null;
        }

        // 处理Collection类型（包括ArrayList）
        if (data instanceof Collection) {
            Collection<?> collection = (Collection<?>) data;
            if (collection.isEmpty()) {
                return null;
            } else if (collection.size() == 1) {
                // 单个元素，返回元素本身（JAXB可以序列化）
                return collection.iterator().next();
            } else {
                // 多个元素：返回MultiElementData
                return new MultiElementData(new ArrayList<>(collection));
            }
        }

        // 处理数组类型
        else if (data.getClass().isArray()) {
            Object[] array = (Object[]) data;
            if (array.length == 0) {
                return null;
            } else if (array.length == 1) {
                return array[0];
            } else {
                List<Object> list = new ArrayList<>();
                for (Object item : array) {
                    if (item != null) {
                        list.add(item);
                    }
                }
                return new MultiElementData(list);
            }
        }

        // 单个对象，直接返回
        return data;
    }

    /**
     * 强制转换为单数据格式
     */
    private static Object processDataForSinglePush(Object data) {
        Object processedData = processDataForPush(data);

        if (processedData instanceof MultiElementData) {
            List<Object> elements = ((MultiElementData) processedData).getElements();
            return elements.isEmpty() ? null : elements.get(0);
        }

        return processedData;
    }

    /**
     * 强制转换为多数据格式
     */
    private static MultiElementData processDataForMultiplePush(Object data) {
        if (data == null) {
            return new MultiElementData(new ArrayList<>());
        }

        if (data instanceof Collection) {
            return new MultiElementData(new ArrayList<>((Collection<?>) data));
        } else if (data.getClass().isArray()) {
            List<Object> list = new ArrayList<>();
            Object[] array = (Object[]) data;
            for (Object item : array) {
                if (item != null) {
                    list.add(item);
                }
            }
            return new MultiElementData(list);
        } else {
            List<Object> list = new ArrayList<>();
            list.add(data);
            return new MultiElementData(list);
        }
    }

    /**
     * 验证推送数据是否有效
     */
    private static boolean isValidPushData(Object data) {
        if (data == null) {
            return false;
        }

        if (data instanceof Collection) {
            return !((Collection<?>) data).isEmpty();
        }

        if (data.getClass().isArray()) {
            return ((Object[]) data).length > 0;
        }

        return true;
    }

    // ==================== 数据推送方法 - 核心功能 ====================

    /**
     * 推送数据 - 使用Notify操作
     * 自动处理ArrayList等复杂数据类型
     */
    public PushBuilder notify(Object data) {
        Object processedData = processDataForPush(data);
        return operation(GatConstants.Operation.NOTIFY, processedData);
    }

    /**
     * 推送数据 - 指定操作名称
     */
    public PushBuilder notify(String operationName, Object data) {
        Object processedData = processDataForPush(data);
        return operation(operationName, processedData);
    }

    /**
     * 添加操作 - 核心方法
     * 正确处理MultiElementData，避免JAXB序列化ArrayList
     */
    public PushBuilder operation(String operationName, Object data) {
        if (data instanceof MultiElementData) {
            // 多元素数据：逐个添加到Operation的dataList，而不是设置为单个data
            MultiElementData multiData = (MultiElementData) data;
            Operation operation = new Operation(operationOrder++, operationName);

            // 将每个元素添加到Operation的dataList
            for (Object element : multiData.getElements()) {
                operation.getDataList().add(element);
            }

            this.message.getBody().addOperation(operation);
        } else {
            // 单个数据对象：使用原来的方式
            Operation operation = new Operation(operationOrder++, operationName, data);
            this.message.getBody().addOperation(operation);
        }

        return this;
    }

    // ==================== 高级数据处理方法 ====================

    /**
     * 自动处理数据并推送
     * 这是最常用的方法，会智能处理ArrayList
     */
    public PushBuilder withData(Object data) {
        return notify(data);
    }

    /**
     * 强制使用单数据模式推送
     * 即使是ArrayList也只取第一个元素
     */
    public PushBuilder withSingleData(Object data) {
        Object processedData = processDataForSinglePush(data);
        return operation(GatConstants.Operation.NOTIFY, processedData);
    }

    /**
     * 强制使用多数据模式推送
     * 单个对象也包装为ArrayList
     */
    public PushBuilder withMultiData(Object data) {
        Object processedData = processDataForMultiplePush(data);
        return operation(GatConstants.Operation.NOTIFY, processedData);
    }

    /**
     * 批量数据推送
     */
    public PushBuilder withBatch(Collection<?> dataCollection) {
        // 创建MultiElementData并传递给operation方法
        MultiElementData multiData = new MultiElementData(new ArrayList<>(dataCollection));
        return operation(GatConstants.Operation.NOTIFY, multiData);
    }

    /**
     * 批量数据推送 - 指定操作名称
     */
    public PushBuilder withBatch(String operationName, Collection<?> dataCollection) {
        MultiElementData multiData = new MultiElementData(new ArrayList<>(dataCollection));
        return operation(operationName, multiData);
    }

    // ==================== 便捷的数据类型推送方法 ====================

    /**
     * 推送字符串数据
     */
    public PushBuilder notifyString(String data) {
        return notify(data);
    }

    /**
     * 推送数字数据
     */
    public PushBuilder notifyNumber(Number data) {
        return notify(data);
    }

    /**
     * 推送布尔数据
     */
    public PushBuilder notifyBoolean(Boolean data) {
        return notify(data);
    }

    /**
     * 推送状态信息
     */
    public PushBuilder notifyStatus(String status, Object details) {
        return operation("StatusNotify", new StatusData(status, details));
    }

    /**
     * 推送错误信息
     */
    public PushBuilder notifyError(String errorCode, String errorMessage) {
        return operation("ErrorNotify", new ErrorData(errorCode, errorMessage));
    }

    // ==================== 链式调用增强方法 ====================

    /**
     * 条件推送 - 只有满足条件才推送数据
     */
    public PushBuilder notifyIf(boolean condition, Object data) {
        if (condition) {
            return notify(data);
        }
        return this;
    }

    /**
     * 非空推送 - 只有数据非空才推送
     */
    public PushBuilder notifyIfNotNull(Object data) {
        return notifyIf(data != null, data);
    }

    /**
     * 有效数据推送 - 使用内部验证
     */
    public PushBuilder notifyIfValid(Object data) {
        return notifyIf(isValidPushData(data), data);
    }

    // ==================== 构建方法 ====================

    /**
     * 构建最终的推送消息
     */
    public Message build() {
        // 验证必要字段
        validateMessage();

        // 设置默认目标地址（如果未设置）
        if (message.getTo() == null) {
            setDefaultDestination();
        }

        return message;
    }

    /**
     * 构建并返回XML字符串
     */
    public String buildXml() {
        // 这里可以集成MessageCodec进行XML编码
        // 暂时返回消息对象的字符串表示
        return build().toString();
    }

    // ==================== 内部辅助方法 ====================

    /**
     * 验证消息完整性
     */
    private void validateMessage() {
        if (message.getFrom() == null) {
            throw new IllegalStateException("推送消息必须设置源地址");
        }

        if (message.getBody().getOperations().isEmpty()) {
            throw new IllegalStateException("推送消息必须包含至少一个操作");
        }
    }

    /**
     * 设置默认目标地址
     */
    private void setDefaultDestination() {
        String fromSys = message.getFrom().getSys();

        // 客户端默认推送到服务端，服务端默认推送到客户端
        if (GatConstants.SystemAddress.UTCS.equals(fromSys)) {
            toTicp(); // 客户端推送到服务端
        } else if (GatConstants.SystemAddress.TICP.equals(fromSys)) {
            toUtcs(); // 服务端推送到客户端
        } else {
            throw new IllegalStateException("无法确定默认目标地址，请显式设置");
        }
    }

    // ==================== 内部数据类 ====================

    /**
     * 多元素数据包装器
     * 用于标识数据需要添加到Operation.dataList而不是Operation.data
     * 这样可以避免JAXB序列化ArrayList的问题
     */
    public static class MultiElementData {
        private final List<Object> elements;

        public MultiElementData(List<Object> elements) {
            this.elements = elements != null ? elements : new ArrayList<>();
        }

        public List<Object> getElements() {
            return elements;
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }

        public int size() {
            return elements.size();
        }
    }

    /**
     * 状态数据包装器
     */
    private static class StatusData {
        private final String status;
        private final Object details;

        public StatusData(String status, Object details) {
            this.status = status;
            this.details = details;
        }

        public String getStatus() {
            return status;
        }

        public Object getDetails() {
            return details;
        }
    }

    /**
     * 错误数据包装器
     */
    private static class ErrorData {
        private final String errorCode;
        private final String errorMessage;

        public ErrorData(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}