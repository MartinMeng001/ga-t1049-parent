package com.traffic.gat1049.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * 消息地址
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Sys")
    @JsonProperty("Sys")
    private String sys;

    @XmlElement(name = "SubSys")
    @JsonProperty("SubSys")
    private String subSys;

    @XmlElement(name = "Instance")
    @JsonProperty("Instance")
    private String instance;

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
