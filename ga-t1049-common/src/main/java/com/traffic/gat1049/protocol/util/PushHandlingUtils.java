package com.traffic.gat1049.protocol.util;

import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.builder.PushBuilder;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 修正版推送消息处理工具类
 * 解决JAXB序列化ArrayList的问题
 *
 * 关键修正：
 * 1. 不直接传递ArrayList给Operation.data
 * 2. 将ArrayList的元素逐个添加到Operation.dataList
 * 3. 保持与TSC查询相同的数据结构
 */
public class PushHandlingUtils {

    // ==================== 核心推送消息创建方法 ====================

    /**
     * 创建客户端推送消息 - 最常用方法
     * 修正：正确处理ArrayList，避免JAXB序列化错误
     */
    public static Message createClientPushMessage(Object data) {
        return PushBuilder.clientPush()
                .toTicp()
                .withData(data)
                .build();
    }

    /**
     * 创建客户端推送消息 - 带Token
     */
    public static Message createClientPushMessage(String token, Object data) {
        return PushBuilder.clientPush()
                .toTicp()
                .token(token)
                .withData(data)
                .build();
    }

    /**
     * 创建服务端推送消息
     */
    public static Message createServerPushMessage(Object data) {
        return PushBuilder.serverPush()
                .toUtcs()
                .withData(data)
                .build();
    }

    /**
     * 创建服务端推送消息 - 带Token
     */
    public static Message createServerPushMessage(String token, Object data) {
        return PushBuilder.serverPush()
                .toUtcs()
                .token(token)
                .withData(data)
                .build();
    }

    // ==================== 数据处理方法 - 核心修正 ====================

    /**
     * 处理推送数据 - 核心数据转换逻辑
     * 修正：正确处理ArrayList，避免JAXB序列化问题
     *
     * 重要：这个方法现在返回的是适合JAXB序列化的数据结构
     */
    public static Object processPushData(Object data) {
        if (data == null) {
            return null;
        }

        // 关键修正：处理Collection类型（包括ArrayList）
        if (data instanceof Collection) {
            Collection<?> collection = (Collection<?>) data;
            if (collection.isEmpty()) {
                return null;
            } else if (collection.size() == 1) {
                // 单个元素，返回元素本身（JAXB可以序列化）
                return collection.iterator().next();
            } else {
                // 多个元素：不返回ArrayList，而是返回特殊标记
                // 让PushBuilder知道需要将这些元素添加到Operation.dataList
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
                // 转换为MultiElementData
                List<Object> list = new ArrayList<>();
                for (Object item : array) {
                    if (item != null) {
                        list.add(item);
                    }
                }
                return new MultiElementData(list);
            }
        }

        // 单个对象，直接返回（JAXB可以序列化）
        return data;
    }

    /**
     * 强制转换为单数据格式
     */
    public static Object processPushDataAsSingle(Object data) {
        Object processedData = processPushData(data);

        if (processedData instanceof MultiElementData) {
            List<Object> elements = ((MultiElementData) processedData).getElements();
            return elements.isEmpty() ? null : elements.get(0);
        }

        return processedData;
    }

    /**
     * 强制转换为多数据格式
     */
    public static MultiElementData processPushDataAsMultiple(Object data) {
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

    // ==================== MultiElementData包装器 ====================

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

        public int size() {
            return elements.size();
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }

        @Override
        public String toString() {
            return String.format("MultiElementData{size=%d, types=%s}",
                    elements.size(),
                    getElementTypes());
        }

        private String getElementTypes() {
            return elements.stream()
                    .map(Object::getClass)
                    .map(Class::getSimpleName)
                    .distinct()
                    .reduce((a, b) -> a + "," + b)
                    .orElse("empty");
        }
    }

    // ==================== 数据类型检查方法 ====================

    /**
     * 检查数据是否为MultiElementData类型
     */
    public static boolean isMultiElementData(Object data) {
        return data instanceof MultiElementData;
    }

    /**
     * 检查数据是否为ArrayList类型
     */
    public static boolean isArrayList(Object data) {
        return data instanceof ArrayList;
    }

    /**
     * 检查数据是否为Collection类型
     */
    public static boolean isCollection(Object data) {
        return data instanceof Collection;
    }

    /**
     * 获取数据元素数量
     */
    public static int getDataSize(Object data) {
        if (data instanceof MultiElementData) {
            return ((MultiElementData) data).size();
        } else if (data instanceof Collection) {
            return ((Collection<?>) data).size();
        } else if (data != null && data.getClass().isArray()) {
            return ((Object[]) data).length;
        } else if (data != null) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 获取数据类型描述
     */
    public static String getDataTypeDescription(Object data) {
        if (data == null) {
            return "null";
        } else if (data instanceof MultiElementData) {
            MultiElementData multiData = (MultiElementData) data;
            return String.format("MultiElementData(size=%d)", multiData.size());
        } else if (data instanceof Collection) {
            Collection<?> collection = (Collection<?>) data;
            return String.format("%s(size=%d)", data.getClass().getSimpleName(), collection.size());
        } else if (data.getClass().isArray()) {
            Object[] array = (Object[]) data;
            return String.format("Array(length=%d)", array.length);
        } else {
            return data.getClass().getSimpleName();
        }
    }

    /**
     * 验证推送数据是否有效
     */
    public static boolean isValidPushData(Object data) {
        if (data == null) {
            return false;
        }

        if (data instanceof MultiElementData) {
            return !((MultiElementData) data).isEmpty();
        }

        if (data instanceof Collection) {
            return !((Collection<?>) data).isEmpty();
        }

        if (data.getClass().isArray()) {
            return ((Object[]) data).length > 0;
        }

        return true;
    }

    // ==================== 批量数据推送方法 ====================

    /**
     * 创建批量推送消息 - 客户端
     */
    public static Message createClientBatchPushMessage(Collection<?> dataCollection) {
        return PushBuilder.clientPush()
                .toTicp()
                .withBatch(dataCollection)
                .build();
    }

    /**
     * 创建批量推送消息 - 服务端
     */
    public static Message createServerBatchPushMessage(Collection<?> dataCollection) {
        return PushBuilder.serverPush()
                .toUtcs()
                .withBatch(dataCollection)
                .build();
    }

    // ==================== 智能推送构建器入口 ====================

    /**
     * 智能客户端推送构建器
     */
    public static SmartPushBuilder smartClientPush() {
        return new SmartPushBuilder(GatConstants.SystemAddress.UTCS, GatConstants.SystemAddress.TICP);
    }

    /**
     * 智能服务端推送构建器
     */
    public static SmartPushBuilder smartServerPush() {
        return new SmartPushBuilder(GatConstants.SystemAddress.TICP, GatConstants.SystemAddress.UTCS);
    }

    /**
     * 智能推送构建器 - 自定义源系统
     */
    public static SmartPushBuilder smartPushFrom(String fromSys) {
        return new SmartPushBuilder(fromSys, null);
    }

    // ==================== 智能推送构建器类 ====================

    /**
     * 智能推送构建器
     * 修正：正确处理MultiElementData
     */
    public static class SmartPushBuilder {
        private final String fromSys;
        private String toSys;
        private String fromSubSys;
        private String fromInstance;
        private String toSubSys;
        private String toInstance;
        private String token;
        private Object pendingData; // 缓存待推送的数据

        public SmartPushBuilder(String fromSys, String defaultToSys) {
            this.fromSys = fromSys;
            this.toSys = defaultToSys;
        }

        /**
         * 设置目标系统
         */
        public SmartPushBuilder to(String sys) {
            this.toSys = sys;
            return this;
        }

        /**
         * 设置完整目标地址
         */
        public SmartPushBuilder to(String sys, String subSys, String instance) {
            this.toSys = sys;
            this.toSubSys = subSys;
            this.toInstance = instance;
            return this;
        }

        /**
         * 设置源地址详细信息
         */
        public SmartPushBuilder fromDetails(String subSys, String instance) {
            this.fromSubSys = subSys;
            this.fromInstance = instance;
            return this;
        }

        /**
         * 设置令牌
         */
        public SmartPushBuilder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * 推送到TICP
         */
        public SmartPushBuilder toTicp() {
            return to(GatConstants.SystemAddress.TICP);
        }

        /**
         * 推送到TICP（指定用户）
         */
        public SmartPushBuilder toTicp(String username) {
            return to(GatConstants.SystemAddress.TICP, username, null);
        }

        /**
         * 推送到UTCS
         */
        public SmartPushBuilder toUtcs() {
            return to(GatConstants.SystemAddress.UTCS);
        }

        /**
         * 推送到UTCS（指定用户）
         */
        public SmartPushBuilder toUtcs(String username) {
            return to(GatConstants.SystemAddress.UTCS, username, "");
        }

        /**
         * 自动处理数据并构建推送消息
         */
        public SmartPushBuilder withData(Object data) {
            this.pendingData = processPushData(data);
            return this;
        }

        /**
         * 强制使用单数据模式
         */
        public SmartPushBuilder withSingleData(Object data) {
            this.pendingData = processPushDataAsSingle(data);
            return this;
        }

        /**
         * 强制使用多数据模式
         */
        public SmartPushBuilder withMultiData(Object data) {
            this.pendingData = processPushDataAsMultiple(data);
            return this;
        }

        /**
         * 批量数据推送
         */
        public SmartPushBuilder withBatch(Collection<?> dataCollection) {
            this.pendingData = new MultiElementData(new ArrayList<>(dataCollection));
            return this;
        }

        /**
         * 构建最终消息
         */
        public Message build() {
            if (pendingData == null) {
                throw new IllegalStateException("必须先设置推送数据，请调用withData()、withSingleData()、withMultiData()或withBatch()方法");
            }

            PushBuilder builder = PushBuilder.from(fromSys, fromSubSys, fromInstance)
                    .to(toSys, toSubSys, toInstance);

            if (token != null) {
                builder.token(token);
            }

            return builder.notify(pendingData).build();
        }
    }

    // ==================== 便捷静态方法 ====================

    /**
     * 快速客户端推送
     */
    public static Message clientPush(Object data) {
        return createClientPushMessage(data);
    }

    /**
     * 快速服务端推送
     */
    public static Message serverPush(Object data) {
        return createServerPushMessage(data);
    }

    /**
     * 快速批量推送
     */
    public static Message batchPush(String fromSys, String toSys, Collection<?> data) {
        return smartPushFrom(fromSys)
                .to(toSys)
                .withBatch(data)
                .build();
    }
}