package com.traffic.gat1049.exception;

/**
 * 数据转换异常
 */
public class DataConversionException extends RuntimeException {

    public DataConversionException(String message) {
        super(message);
    }

    public DataConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
