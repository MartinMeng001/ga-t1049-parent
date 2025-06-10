package com.traffic.gat1049.model.enums;

/**
 * 消息类型枚举
 * 对应文档表1
 */
public enum MessageType {
    REQUEST("REQUEST", "请求，要求通信对方应答"),
    RESPONSE("RESPONSE", "应答，针对请求数据包的应答数据包"),
    PUSH("PUSH", "主动推送，单向数据包不需要通信对方应答"),
    ERROR("ERROR", "出错应答，REQUEST数据包发生错误时向通信对方应答该数据包");

    private final String code;
    private final String description;

    MessageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static MessageType fromCode(String code) {
        for (MessageType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type code: " + code);
    }
}
