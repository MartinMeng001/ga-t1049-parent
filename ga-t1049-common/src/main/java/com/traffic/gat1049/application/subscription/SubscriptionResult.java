package com.traffic.gat1049.application.subscription;

/**
 * 订阅处理结果
 */
public class SubscriptionResult {
    private boolean success;
    private String errorCode;
    private String errorMessage;
    private Object responseData;

    public static SubscriptionResult success() {
        SubscriptionResult result = new SubscriptionResult();
        result.success = true;
        return result;
    }

    public static SubscriptionResult success(Object responseData) {
        SubscriptionResult result = success();
        result.responseData = responseData;
        return result;
    }

    public static SubscriptionResult error(String errorCode, String errorMessage) {
        SubscriptionResult result = new SubscriptionResult();
        result.success = false;
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        return result;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Object getResponseData() { return responseData; }
    public void setResponseData(Object responseData) { this.responseData = responseData; }
}
