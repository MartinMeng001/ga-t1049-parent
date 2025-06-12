package com.traffic.gat1049.protocol.model.sdo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * 系统错误对象 (SDO_Error)
 * 对应文档中的 SDO_Error
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SDO_Error")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdoError implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 错误对象名
     */
    @NotBlank(message = "错误对象名不能为空")
    @XmlElement(name = "ErrObj", required = false)
    @JsonProperty("ErrObj")
    private String errObj;

    /**
     * 错误类型
     */
    @NotBlank(message = "错误类型不能为空")
    @XmlElement(name = "ErrType", required = true)
    @JsonProperty("ErrType")
    private String errType;

    /**
     * 错误描述
     */
    @XmlElement(name = "ErrDesc")
    @JsonProperty("ErrDesc")
    private String errDesc;

    // 构造函数
    public SdoError() {}

    public SdoError(String errObj, String errType, String errDesc) {
        this.errObj = errObj;
        this.errType = errType;
        this.errDesc = errDesc;
    }

    // Getters and Setters
    public String getErrObj() { return errObj; }
    public void setErrObj(String errObj) { this.errObj = errObj; }

    public String getErrType() { return errType; }
    public void setErrType(String errType) { this.errType = errType; }

    public String getErrDesc() { return errDesc; }
    public void setErrDesc(String errDesc) { this.errDesc = errDesc; }

    @Override
    public String toString() {
        return "SdoError{" +
                "errObj='" + errObj + '\'' +
                ", errType='" + errType + '\'' +
                ", errDesc='" + errDesc + '\'' +
                '}';
    }
}
