package com.traffic.gat1049.protocol.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.constants.GatConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GA/T 1049.1 消息结构
 * 符合标准文档要求的完整消息格式
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final AtomicLong sequenceCounter = new AtomicLong(0);

    /**
     * 版本号 - 格式为"主版本号.副版本号"，主版本号和副版本号的取值范围为数字0～9
     */
    @NotBlank(message = "版本号不能为空")
    @Pattern(regexp = "\\d\\.\\d", message = "版本号格式错误，应为主版本号.副版本号")
    @XmlElement(name = "Version", required = true)
    @JsonProperty("Version")
    private String version = GatConstants.PROTOCOL_VERSION;

    /**
     * 令牌 - 由各基础应用系统成功登录后获得，登录成功前可为空字符串
     */
    @XmlElement(name = "Token")
    @JsonProperty("Token")
    private String token = "";

    /**
     * 源地址 - 数据包发送方的地址
     */
    @XmlElement(name = "From", required = true)
    @JsonProperty("From")
    private Address from;

    /**
     * 目的地址 - 数据包接收的地址
     */
    @XmlElement(name = "To", required = true)
    @JsonProperty("To")
    private Address to;

    /**
     * 数据包类型 - REQUEST/RESPONSE/PUSH/ERROR
     */
    @NotBlank(message = "数据包类型不能为空")
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private String type;

    /**
     * 序列号 - 最大长度20位，采用14位日期（YYYYMMDDHHMMSS）+6位顺序号
     */
    @NotBlank(message = "序列号不能为空")
    @Size(max = 20, message = "序列号长度不能超过20位")
    @XmlElement(name = "Seq", required = true)
    @JsonProperty("Seq")
    private String seq;

    /**
     * 消息体 - 包含一个或多个操作命令
     */
    @XmlElement(name = "Body", required = true)
    @JsonProperty("Body")
    private MessageBody body;

    // 构造函数
    public Message() {
        this.seq = generateSequence();
        this.body = new MessageBody();
    }

    public Message(String type) {
        this();
        this.type = type;
    }

    public Message(String type, String seq) {
        this();
        this.type = type;
        this.seq = seq;
    }

    /**
     * 生成符合标准的序列号
     * 格式：14位日期（YYYYMMDDHHMMSS）+6位顺序号（不足6位前补0）
     */
    public static String generateSequence() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long counter = sequenceCounter.incrementAndGet() % 1000000; // 确保6位
        return String.format("%s%06d", dateTime, counter);
    }

    // Getters and Setters
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Address getFrom() { return from; }
    public void setFrom(Address from) { this.from = from; }

    public Address getTo() { return to; }
    public void setTo(Address to) { this.to = to; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSeq() { return seq; }
    public void setSeq(String seq) { this.seq = seq; }

    public MessageBody getBody() { return body; }
    public void setBody(MessageBody body) { this.body = body; }

    @Override
    public String toString() {
        return "Message{" +
                "version='" + version + '\'' +
                ", token='" + (token != null && !token.isEmpty() ? "[HIDDEN]" : "empty") + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", type='" + type + '\'' +
                ", seq='" + seq + '\'' +
                ", body=" + body +
                '}';
    }
}
