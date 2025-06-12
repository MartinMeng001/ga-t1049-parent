package com.traffic.gat1049.protocol.model.sdo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 对时服务器对象 (SDO_TimeServer)
 * 对应文档中的 SDO_TimeServer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SDO_TimeServer")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdoTimeServer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务器的IP地址或域名
     */
    @NotBlank(message = "服务器地址不能为空")
    @XmlElement(name = "Host", required = true)
    @JsonProperty("Host")
    private String host;

    /**
     * 对时协议
     */
    @NotBlank(message = "对时协议不能为空")
    @XmlElement(name = "Protocol", required = true)
    @JsonProperty("Protocol")
    private String protocol;

    /**
     * 使用端口号
     */
    @XmlElement(name = "Port", required = true)
    @JsonProperty("Port")
    private Integer port;

    // 构造函数
    public SdoTimeServer() {}

    public SdoTimeServer(String host, String protocol, Integer port) {
        this.host = host;
        this.protocol = protocol;
        this.port = port;
    }

    // Getters and Setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    @Override
    public String toString() {
        return "SdoTimeServer{" +
                "host='" + host + '\'' +
                ", protocol='" + protocol + '\'' +
                ", port=" + port +
                '}';
    }
}
