package com.traffic.gat1049.exception;

/**
 * 业务异常基类
 */
public class BusinessException extends GatProtocolException {

    private static final long serialVersionUID = 1L;

    private String businessCode;
    private Object[] args;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String businessCode, String message) {
        super(businessCode, message);
        this.businessCode = businessCode;
    }

    public BusinessException(String businessCode, String message, Object... args) {
        super(businessCode, message);
        this.businessCode = businessCode;
        this.args = args;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String businessCode, String message, Throwable cause) {
        super(businessCode, message, cause);
        this.businessCode = businessCode;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
