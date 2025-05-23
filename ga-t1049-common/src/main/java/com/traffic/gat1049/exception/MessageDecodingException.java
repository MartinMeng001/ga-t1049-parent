package com.traffic.gat1049.exception;

/**
 * 消息解码异常
 */
public class MessageDecodingException extends GatProtocolException {

    private static final long serialVersionUID = 1L;

    public MessageDecodingException(String message) {
        super(message);
    }

    public MessageDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
