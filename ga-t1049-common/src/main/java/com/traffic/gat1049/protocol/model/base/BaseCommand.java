package com.traffic.gat1049.protocol.model.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

/**
 * 控制命令基类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseCommand extends BaseEntity {

    /**
     * 命令ID
     */
    @XmlElement(name = "CommandId")
    private String commandId;

    /**
     * 命令优先级
     */
    @XmlElement(name = "Priority")
    private Integer priority = 0;

    /**
     * 命令执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "ExecuteTime")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime executeTime;

    /**
     * 命令超时时间（秒）
     */
    @XmlElement(name = "Timeout")
    private Integer timeout;

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "BaseCommand{" +
                "commandId='" + commandId + '\'' +
                ", priority=" + priority +
                ", executeTime=" + executeTime +
                ", timeout=" + timeout +
                "} " + super.toString();
    }
}
