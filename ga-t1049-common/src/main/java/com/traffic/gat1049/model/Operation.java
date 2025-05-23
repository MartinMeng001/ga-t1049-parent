package com.traffic.gat1049.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * 操作对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "order")
    @JsonProperty("order")
    private Integer order;

    @XmlAttribute(name = "name")
    @JsonProperty("name")
    private String name;

    @XmlAnyElement(lax = true)
    @JsonProperty("data")
    private Object data;

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
