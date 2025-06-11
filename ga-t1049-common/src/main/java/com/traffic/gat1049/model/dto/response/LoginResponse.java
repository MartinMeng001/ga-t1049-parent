package com.traffic.gat1049.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 登录响应DTO
 * 支持JAXB XML序列化和Jackson JSON序列化
 */
@XmlRootElement(name = "LoginResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "success", required = true)
    @JsonProperty("success")
    private boolean success;

    @XmlElement(name = "message")
    @JsonProperty("message")
    private String message;

    @XmlElement(name = "token")
    @JsonProperty("token")
    private String token;

    @XmlElement(name = "onlineUsers")
    @JsonProperty("onlineUsers")
    private int onlineUsers;

    @XmlElement(name = "timestamp")
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp; // 改为 String 类型

    // 无参构造函数（JAXB要求）
    public LoginResponse() {}

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getOnlineUsers() { return onlineUsers; }
    public void setOnlineUsers(int onlineUsers) { this.onlineUsers = onlineUsers; }

    public LocalDateTime getTimestampAsLocalDateTime() {
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp() { this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(8, token.length())) + "..." : "null") + '\'' +
                ", onlineUsers=" + onlineUsers +
                ", timestamp=" + timestamp +
                '}';
    }
}
