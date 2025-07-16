package com.traffic.gat1049.device.adapter.exception;

/**
 * 数据转换异常
 */
public class DataConversionException extends AdapterException {

    private static final long serialVersionUID = 1L;

    public DataConversionException(String message) {
        super("DATA_CONVERSION_ERROR", message);
    }

    public DataConversionException(String message, Throwable cause) {
        super("DATA_CONVERSION_ERROR", message, cause);
    }

    public DataConversionException(String deviceId, String message) {
        super(deviceId, "DATA_CONVERSION_ERROR", message);
    }

    public DataConversionException(String deviceId, String message, Throwable cause) {
        super(deviceId, "DATA_CONVERSION_ERROR", message, cause);
    }
}
