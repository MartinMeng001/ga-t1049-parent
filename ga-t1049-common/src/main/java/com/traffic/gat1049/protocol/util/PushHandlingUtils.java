package com.traffic.gat1049.protocol.util;

import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.builder.PushBuilder;

import java.util.Collection;

/**
 * 简化版推送消息处理工具类
 * 只提供便捷的API入口，数据处理由PushBuilder内部完成
 * 避免与PushBuilder的循环依赖
 */
public class PushHandlingUtils {

    // ==================== 核心推送消息创建方法 ====================

    /**
     * 创建客户端推送消息 - 最常用方法
     * 数据处理由PushBuilder内部完成，避免循环依赖
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
     * 创建客户端推送消息 - 带Token, 带username
     */
    public static Message createClientPushMessage(String username, String token, Object data) {
        return PushBuilder.clientPush(username)
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

    // ==================== 智能推送构建器 ====================

    /**
     * 智能推送构建器类
     * 提供更灵活的推送配置
     */
    public static class SmartPushBuilder {
        private final String fromSys;
        private final String fromSubSys;
        private final String fromInstance;
        private String toSys;
        private String toSubSys;
        private String toInstance;
        private String token;

        public SmartPushBuilder(String fromSys, String fromSubSys, String fromInstance) {
            this.fromSys = fromSys;
            this.fromSubSys = fromSubSys;
            this.fromInstance = fromInstance;
        }

        /**
         * 设置目标地址
         */
        public SmartPushBuilder to(String toSys) {
            this.toSys = toSys;
            return this;
        }

        /**
         * 设置目标地址 - 完整地址
         */
        public SmartPushBuilder to(String toSys, String toSubSys, String toInstance) {
            this.toSys = toSys;
            this.toSubSys = toSubSys;
            this.toInstance = toInstance;
            return this;
        }

        /**
         * 设置目标为TICP
         */
        public SmartPushBuilder toTicp() {
            return to("TICP", null, null);
        }

        /**
         * 设置目标为UTCS
         */
        public SmartPushBuilder toUtcs() {
            return to("UTCS", null, null);
        }

        /**
         * 设置Token
         */
        public SmartPushBuilder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * 推送数据
         */
        public SmartPushBuilder withData(Object data) {
            return this; // 数据会在build时处理
        }

        /**
         * 构建最终消息
         */
        public Message build() {
            PushBuilder builder = PushBuilder.from(fromSys, fromSubSys, fromInstance)
                    .to(toSys, toSubSys, toInstance);

            if (token != null) {
                builder.token(token);
            }

            return builder.build();
        }

        /**
         * 构建并推送数据
         */
        public Message buildWithData(Object data) {
            PushBuilder builder = PushBuilder.from(fromSys, fromSubSys, fromInstance)
                    .to(toSys, toSubSys, toInstance);

            if (token != null) {
                builder.token(token);
            }

            return builder.withData(data).build();
        }
    }

    /**
     * 智能客户端推送构建器
     */
    public static SmartPushBuilder smartClientPush() {
        return new SmartPushBuilder("UTCS", null, null);
    }

    /**
     * 智能服务端推送构建器
     */
    public static SmartPushBuilder smartServerPush() {
        return new SmartPushBuilder("TICP", null, null);
    }

    /**
     * 智能推送构建器 - 指定源地址
     */
    public static SmartPushBuilder smartPushFrom(String fromSys) {
        return new SmartPushBuilder(fromSys, null, null);
    }

    /**
     * 智能推送构建器 - 完整源地址
     */
    public static SmartPushBuilder smartPushFrom(String fromSys, String fromSubSys, String fromInstance) {
        return new SmartPushBuilder(fromSys, fromSubSys, fromInstance);
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
        return PushBuilder.from(fromSys)
                .to(toSys)
                .withBatch(data)
                .build();
    }

    /**
     * 快速客户端批量推送
     */
    public static Message clientBatchPush(Collection<?> data) {
        return PushBuilder.clientPush()
                .toTicp()
                .withBatch(data)
                .build();
    }

    /**
     * 快速服务端批量推送
     */
    public static Message serverBatchPush(Collection<?> data) {
        return PushBuilder.serverPush()
                .toUtcs()
                .withBatch(data)
                .build();
    }

    // ==================== 条件推送便捷方法 ====================

    /**
     * 条件客户端推送
     */
    public static Message clientPushIf(boolean condition, Object data) {
        if (condition) {
            return createClientPushMessage(data);
        }
        return null;
    }

    /**
     * 非空客户端推送
     */
    public static Message clientPushIfNotNull(Object data) {
        return clientPushIf(data != null, data);
    }

    /**
     * 条件服务端推送
     */
    public static Message serverPushIf(boolean condition, Object data) {
        if (condition) {
            return createServerPushMessage(data);
        }
        return null;
    }

    /**
     * 非空服务端推送
     */
    public static Message serverPushIfNotNull(Object data) {
        return serverPushIf(data != null, data);
    }
}