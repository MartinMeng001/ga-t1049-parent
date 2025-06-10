package com.traffic.gat1049.protocol.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * 地址对象
 * 符合GA/T 1049.1标准的地址结构
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 基础应用系统类型标识
     * 取值见GA/T 1049.1表A.2：TICP/UTCS/TVMS/TICS/TVMR/TIPS/PGPS/TDMS/TEDS/VMKS
     */
    @NotBlank(message = "系统类型标识不能为空")
    @XmlElement(name = "Sys", required = true)
    @JsonProperty("Sys")
    private String sys;

    /**
     * 子系统标识 - 不存在可为空
     * 交通集成指挥平台作为源地址或目的地址时应为空
     */
    @XmlElement(name = "SubSys")
    @JsonProperty("SubSys")
    private String subSys;

    /**
     * 具体系统标识 - 不存在可为空
     * 交通集成指挥平台作为源地址或目的地址时应为空
     */
    @XmlElement(name = "Instance")
    @JsonProperty("Instance")
    private String instance;

    // 构造函数
    public Address() {}

    public Address(String sys) {
        this.sys = sys;
    }

    public Address(String sys, String subSys, String instance) {
        this.sys = sys;
        this.subSys = subSys;
        this.instance = instance;
    }

    // Getters and Setters
    public String getSys() { return sys; }
    public void setSys(String sys) { this.sys = sys; }

    public String getSubSys() { return subSys; }
    public void setSubSys(String subSys) { this.subSys = subSys; }

    public String getInstance() { return instance; }
    public void setInstance(String instance) { this.instance = instance; }

    @Override
    public String toString() {
        return "Address{" +
                "sys='" + sys + '\'' +
                ", subSys='" + subSys + '\'' +
                ", instance='" + instance + '\'' +
                '}';
    }
}
