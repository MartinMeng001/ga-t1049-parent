package com.traffic.gat1049.model.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.enums.SystemState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 基础响应DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应代码
     */
    @JsonProperty("code")
    private String code;

    /**
     * 响应消息
     */
    @JsonProperty("message")
    private String message;

    /**
     * 响应时间
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    /**
     * 请求序列号
     */
    @JsonProperty("requestId")
    private String requestId;

    public BaseResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public BaseResponseDto(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "BaseResponseDto{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
