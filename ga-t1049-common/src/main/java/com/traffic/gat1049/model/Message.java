package com.traffic.gat1049.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * GA/T 1049.2 协议消息结构
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Version", required = true)
    @JsonProperty("Version")
    private String version = "2.0";

    @XmlElement(name = "Token")
    @JsonProperty("Token")
    private String token;

    @XmlElement(name = "From", required = true)
    @JsonProperty("From")
    private Address from;

    @XmlElement(name = "To", required = true)
    @JsonProperty("To")
    private Address to;

    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private String type;

    @XmlElement(name = "Seq", required = true)
    @JsonProperty("Seq")
    private String seq;

    @XmlElement(name = "Body", required = true)
    @JsonProperty("Body")
    private MessageBody body;

    // Constructors
    public Message() {}

    public Message(String type, String seq) {
        this.type = type;
        this.seq = seq;
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
                ", token='" + token + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", type='" + type + '\'' +
                ", seq='" + seq + '\'' +
                ", body=" + body +
                '}';
    }
}
