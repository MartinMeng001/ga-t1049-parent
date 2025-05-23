package com.traffic.gat1049.model.dto;

/**
 * 控制命令DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControlCommandDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 命令类型
     */
    @NotBlank(message = "命令类型不能为空")
    @JsonProperty("commandType")
    private String commandType;

    /**
     * 目标路口编号
     */
    @NotBlank(message = "目标路口编号不能为空")
    @JsonProperty("targetCrossId")
    private String targetCrossId;

    /**
     * 命令参数
     */
    @JsonProperty("parameters")
    private Object parameters;

    /**
     * 执行时间
     */
    @JsonProperty("executeTime")
    private LocalDateTime executeTime;

    /**
     * 优先级
     */
    @JsonProperty("priority")
    private Integer priority = 0;

    /**
     * 超时时间（秒）
     */
    @JsonProperty("timeout")
    private Integer timeout = 30;

    // Getters and Setters
    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getTargetCrossId() {
        return targetCrossId;
    }

    public void setTargetCrossId(String targetCrossId) {
        this.targetCrossId = targetCrossId;
    }

    public Object getParameters() {
        return parameters;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "ControlCommandDto{" +
                "commandType='" + commandType + '\'' +
                ", targetCrossId='" + targetCrossId + '\'' +
                ", parameters=" + parameters +
                ", executeTime=" + executeTime +
                ", priority=" + priority +
                ", timeout=" + timeout +
                '}';
    }
}
