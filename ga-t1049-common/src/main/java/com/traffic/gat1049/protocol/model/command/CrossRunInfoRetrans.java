package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 重传运行信息命令
 * 用于集指平台请求信控系统重新传输指定时间段内的运行信息
 * 对应文档中的 CrossRunInfoRetrans (B.3.11)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossRunInfoRetrans")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossRunInfoRetrans {

    /**
     * 开始时间 (需重传运行信息时间段的开始时间)
     * 符合 GA/T 543.6 的 DE00554 格式: yyyyMMddHHmmss
     */
    @XmlElement(name = "StartTime", required = true)
    @JsonProperty("StartTime")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    /**
     * 结束时间 (需重传运行信息时间段的结束时间)
     * 符合 GA/T 543.6 的 DE00554 格式: yyyyMMddHHmmss
     */
    @XmlElement(name = "EndTime", required = true)
    @JsonProperty("EndTime")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    /**
     * 要重传的运行信息数据对象名称
     * 取值参考5.2中规定的数据对象名称，如：
     * - SignalControllerError: 信号机故障信息
     * - CrossModePlan: 路口控制方式方案
     * - CrossTrafficData: 路口交通流数据
     * - CrossCycle: 路口周期数据
     * - CrossStage: 路口阶段数据
     * - CrossSignalGroupStatus: 信号组灯态数据
     */
    @XmlElement(name = "ObjName", required = true)
    @JsonProperty("ObjName")
    @NotBlank(message = "数据对象名称不能为空")
    private String objName;

    /**
     * 需重传的路口编号列表
     * 包含至少1个路口编号
     */
    @NotEmpty(message = "路口编号列表不能为空")
    @XmlElementWrapper(name = "CrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("CrossIDList")
    private List<String> crossIdList = new ArrayList<>();

    // 构造函数
    public CrossRunInfoRetrans() {
        // 默认构造函数
    }

    public CrossRunInfoRetrans(String startTime, String endTime, String objName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.objName = objName;
    }

    public CrossRunInfoRetrans(String startTime, String endTime, String objName, List<String> crossIdList) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.objName = objName;
        this.crossIdList = crossIdList != null ? new ArrayList<>(crossIdList) : new ArrayList<>();
    }

    // Getters and Setters
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public List<String> getCrossIdList() {
        return crossIdList;
    }

    public void setCrossIdList(List<String> crossIdList) {
        this.crossIdList = crossIdList != null ? new ArrayList<>(crossIdList) : new ArrayList<>();
    }

    /**
     * 添加路口编号
     */
    public void addCrossId(String crossId) {
        if (crossId != null && !crossId.trim().isEmpty()) {
            this.crossIdList.add(crossId.trim());
        }
    }

    /**
     * 获取路口数量
     */
    public int getCrossCount() {
        return crossIdList != null ? crossIdList.size() : 0;
    }

    /**
     * 检查是否包含指定路口
     */
    public boolean containsCrossId(String crossId) {
        return crossIdList != null && crossIdList.contains(crossId);
    }

    @Override
    public String toString() {
        return "CrossRunInfoRetrans{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", objName='" + objName + '\'' +
                ", crossIdList=" + crossIdList +
                ", crossCount=" + getCrossCount() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossRunInfoRetrans that = (CrossRunInfoRetrans) o;

        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (objName != null ? !objName.equals(that.objName) : that.objName != null) return false;
        return crossIdList != null ? crossIdList.equals(that.crossIdList) : that.crossIdList == null;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (objName != null ? objName.hashCode() : 0);
        result = 31 * result + (crossIdList != null ? crossIdList.hashCode() : 0);
        return result;
    }
}