package com.traffic.gat1049.model.entity.sdo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;

/**
 * 超时对象 (SDO_TimeOut)
 * 对应文档中的 SDO_TimeOut
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SDO_TimeOut")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdoTimeOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通信超时时间，单位（秒）
     */
    @XmlValue
    @JsonProperty("value")
    private Integer timeoutSeconds;

    // 构造函数
    public SdoTimeOut() {}

    public SdoTimeOut(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    // Getters and Setters
    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    @Override
    public String toString() {
        return "SdoTimeOut{" +
                "timeoutSeconds=" + timeoutSeconds +
                '}';
    }
}
