package com.traffic.gat1049.model.entity.sdo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 心跳对象 (SDO_HeartBeat)
 * 对应文档中的 SDO_HeartBeat
 * 心跳信息对象为空元素对象，元素中不包含文本和子元素
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SDO_HeartBeat")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdoHeartBeat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 时间戳（可选）
     */
    @XmlElement(name = "Timestamp")
    @JsonProperty("Timestamp")
    private Long timestamp;

    public SdoHeartBeat() {
        this.timestamp = System.currentTimeMillis();
    }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "SdoHeartBeat{" +
                "timestamp=" + timestamp +
                '}';
    }
}
