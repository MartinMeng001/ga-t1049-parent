package com.traffic.gat1049.protocol.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

/**
 * 操作命令
 * 符合GA/T 1049.1标准的操作结构
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作命令的顺序编号，从1开始
     */
    @XmlAttribute(name = "order", required = true)
    @JsonProperty("order")
    private Integer order;

    /**
     * 操作命令的名称
     * 取值见GA/T 1049.1表A.3：Login/Logout/Subscribe/Unsubscribe/Get/Set/Notify/Other
     */
    @NotBlank(message = "操作命令名称不能为空")
    @XmlAttribute(name = "name", required = true)
    @JsonProperty("name")
    private String name;

    /**
     * 数据对象 - 包含了描述对象具体信息的内容
     */
    @XmlAnyElement(lax = true)
    @JsonProperty("data")
    private Object data;

    // 构造函数
    public Operation() {}

    public Operation(Integer order, String name, Object data) {
        this.order = order;
        this.name = name;
        this.data = data;
    }

    // Getters and Setters
    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    @Override
    public String toString() {
        return "Operation{" +
                "order=" + order +
                ", name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
