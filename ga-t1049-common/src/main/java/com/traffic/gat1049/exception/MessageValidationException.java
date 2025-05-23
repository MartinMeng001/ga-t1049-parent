package com.traffic.gat1049.exception;

/**
 * 消息验证异常
 */
public class MessageValidationException extends GatProtocolException {

    private static final long serialVersionUID = 1L;

    public MessageValidationException(String message) {
        super(message);
    }

    public MessageValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
