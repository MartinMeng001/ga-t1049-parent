package com.traffic.gat1049.protocol.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息体
 * 包含一个或多个操作命令
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作命令列表 - 可包含一个或多个操作命令
     */
    @XmlElement(name = "Operation")
    @JsonProperty("Operation")
    private List<Operation> operations = new ArrayList<>();

    public MessageBody() {}

    public List<Operation> getOperations() { return operations; }
    public void setOperations(List<Operation> operations) { this.operations = operations; }

    public void addOperation(Operation operation) {
        if (operation != null) {
            this.operations.add(operation);
        }
    }

    @Override
    public String toString() {
        return "MessageBody{" +
                "operations=" + operations +
                '}';
    }
}
