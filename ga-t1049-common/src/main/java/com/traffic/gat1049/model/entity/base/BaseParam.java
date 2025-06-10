package com.traffic.gat1049.model.entity.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 配置参数基类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseParam extends BaseEntity {

    /**
     * 参数是否启用
     */
    @XmlElement(name = "Enabled")
    private Boolean enabled = true;

    /**
     * 参数描述
     */
    @XmlElement(name = "Description")
    private String description;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BaseParam{" +
                "enabled=" + enabled +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
