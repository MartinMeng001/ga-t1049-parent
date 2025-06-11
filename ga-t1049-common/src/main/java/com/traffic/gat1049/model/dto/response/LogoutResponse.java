package com.traffic.gat1049.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDateTime;

@XmlRootElement(name = "LogoutResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogoutResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "success", required = true)
    @JsonProperty("success")
    private boolean success;

    @XmlElement(name = "message")
    @JsonProperty("message")
    private String message;

    @XmlElement(name = "timestamp")
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public LogoutResponse() {}

    public LogoutResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
