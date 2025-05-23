package com.traffic.gat1049.exception;

/**
 * 消息编码异常
 */
public class MessageEncodingException extends GatProtocolException {

    private static final long serialVersionUID = 1L;

    public MessageEncodingException(String message) {
        super(message);
    }

    public MessageEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
