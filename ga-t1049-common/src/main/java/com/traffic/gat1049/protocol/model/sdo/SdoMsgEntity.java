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
 * 订阅对象 (SDO_MsgEntity)
 * 对应文档中的 SDO_MsgEntity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SDO_MsgEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdoMsgEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据包类型
     */
    @NotBlank(message = "数据包类型不能为空")
    @XmlElement(name = "MsgType", required = true)
    @JsonProperty("MsgType")
    private String msgType;

    /**
     * 操作命令
     */
    @NotBlank(message = "操作命令不能为空")
    @XmlElement(name = "OperName", required = true)
    @JsonProperty("OperName")
    private String operName;

    /**
     * 操作的数据对象名称
     */
    @NotBlank(message = "数据对象名称不能为空")
    @XmlElement(name = "ObjName", required = true)
    @JsonProperty("ObjName")
    private String objName;

    // 构造函数
    public SdoMsgEntity() {}

    public SdoMsgEntity(String msgType, String operName, String objName) {
        this.msgType = msgType;
        this.operName = operName;
        this.objName = objName;
    }

    // Getters and Setters
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }

    public String getOperName() { return operName; }
    public void setOperName(String operName) { this.operName = operName; }

    public String getObjName() { return objName; }
    public void setObjName(String objName) { this.objName = objName; }

    @Override
    public String toString() {
        return "SdoMsgEntity{" +
                "msgType='" + msgType + '\'' +
                ", operName='" + operName + '\'' +
                ", objName='" + objName + '\'' +
                '}';
    }
}
