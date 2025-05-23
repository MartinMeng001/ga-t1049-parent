package com.traffic.gat1049.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息体
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageBody implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Operation")
    @JsonProperty("Operation")
    private List<Operation> operations = new ArrayList<>();

    public MessageBody() {}

    public List<Operation> getOperations() { return operations; }
    public void setOperations(List<Operation> operations) { this.operations = operations; }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }

    @Override
    public String toString() {
        return "MessageBody{" +
                "operations=" + operations +
                '}';
    }
}
