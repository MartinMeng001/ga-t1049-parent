package com.traffic.gat1049.protocol.model.intersection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.CrossFeatureAdapter;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.CrossGradeAdapter;
import com.traffic.gat1049.model.enums.*;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 路口参数
 * 对应文档中的 CrossParam - 根据5.1.7路口参数最新定义更新
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossParam {

    /**
     * 路口编号
     * 唯一编号，取值：交通管理部门机构代码（按GA/T 380 2012前6位）+ 80 + 4位路口代码
     * 路口代码符合GA/T 543.10的DE00827
     */
    @NotBlank(message = "路口编号不能为空")
    @Size(max = 50, message = "路口编号长度不能超过50")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 路口名称
     * 符合GA/T 543.10的DE00904
     */
    @NotBlank(message = "路口名称不能为空")
    @Size(max = 50, message = "路口名称长度不能超过50")
    @XmlElement(name = "CrossName", required = true)
    @JsonProperty("CrossName")
    private String crossName;

    /**
     * 路口形状
     * 取值：
     * 10：行人过街；12：2次行人过街；23：T形、Y形；24：十字形；
     * 35：五岔路口；36：六岔路口；39：多岔路口；40：环形交叉口（环岛）；
     * 50：匝道；51：匝道-入口；52：匝道-出口；61：快速路主路路段（交汇区）；
     * 90：其他
     */
    @NotNull(message = "路口形状不能为空")
    @XmlElement(name = "Feature", required = true)
    @XmlJavaTypeAdapter(CrossFeatureAdapter.class)
    @JsonProperty("Feature")
    private CrossFeature feature;

    /**
     * 路口等级
     * 取值：
     * 11：一级，主干路与主干路相交交叉口；12：二级，主干路与次干路相交交叉口；
     * 13：三级，主干路与支路相交交叉口；21：四级，次干路与次干路相交交叉口；
     * 22：五级，次干路与支路相交交叉口；31：六级，支路与支路相交交叉口；
     * 99：其他
     */
    @NotNull(message = "路口等级不能为空")
    @XmlElement(name = "Grade", required = true)
    @XmlJavaTypeAdapter(CrossGradeAdapter.class)
    @JsonProperty("Grade")
    private CrossGrade grade;

    /**
     * 检测器序号列表（对应检测器参数中的检测器序号）
     * 无检测器时可空
     */
    @XmlElementWrapper(name = "DetNoList")
    @XmlElement(name = "DetNo")
    @JsonProperty("DetNoList")
    private List<Integer> detNoList = new ArrayList<>();

    /**
     * 车道序号列表（对应车道参数中的车道序号）
     * 包含至少1个车道序号
     */
    @NotEmpty(message = "车道序号列表不能为空，至少包含1个车道序号")
    @XmlElementWrapper(name = "LaneNoList")
    @XmlElement(name = "LaneNo")
    @JsonProperty("LaneNoList")
    private List<Integer> laneNoList = new ArrayList<>();

    /**
     * 人行横道序号列表（对应人行横道参数中的人行横道序号）
     * 可空
     */
    @XmlElementWrapper(name = "PedestrianNoList")
    @XmlElement(name = "PedestrianNo")
    @JsonProperty("PedestrianNoList")
    private List<Integer> pedestrianNoList = new ArrayList<>();

    /**
     * 信号灯组序号列表
     * 包含至少1个灯组序号
     */
    @NotEmpty(message = "信号灯组序号列表不能为空，至少包含1个灯组序号")
    @XmlElementWrapper(name = "LampGroupNoList")
    @XmlElement(name = "LampGroupNo")
    @JsonProperty("LampGroupNoList")
    private List<Integer> lampGroupNoList = new ArrayList<>();

    /**
     * 信号组序号列表（对应信号组参数中的序号）
     * 包含至少1个信号组序号
     */
    @NotEmpty(message = "信号组序号列表不能为空，至少包含1个信号组序号")
    @XmlElementWrapper(name = "SignalGroupNoList")
    @XmlElement(name = "SignalGroupNo")
    @JsonProperty("SignalGroupNoList")
    private List<String> signalGroupNoList = new ArrayList<>();

    /**
     * 绿冲突矩阵
     * 由0、1组成的字符串，长度为信号组数量的平方，
     * 按照矩阵自左向右、自上而下排列。每位字符取值：
     * 0：不冲突；1：冲突
     */
    @NotNull(message = "绿冲突矩阵不能为空")
    @XmlElement(name = "GreenConflictMatrix", required = true)
    @JsonProperty("GreenConflictMatrix")
    private String greenConflictMatrix;

    /**
     * 阶段号列表
     * 包含至少1个阶段号
     */
    @NotEmpty(message = "阶段号列表不能为空，至少包含1个阶段号")
    @XmlElementWrapper(name = "StageNoList")
    @XmlElement(name = "StageNo")
    @JsonProperty("StageNoList")
    private List<Integer> stageNoList = new ArrayList<>();

    /**
     * 配时方案序号列表
     * 包含至少1个配时方案序号
     */
    @NotEmpty(message = "配时方案序号列表不能为空，至少包含1个配时方案序号")
    @XmlElementWrapper(name = "PlanNoList")
    @XmlElement(name = "PlanNo")
    @JsonProperty("PlanNoList")
    private List<Integer> planNoList = new ArrayList<>();

    /**
     * 日计划号列表
     * 包含至少1个日计划号
     */
    @NotEmpty(message = "日计划号列表不能为空，至少包含1个日计划号")
    @XmlElementWrapper(name = "DayPlanNoList")
    @XmlElement(name = "DayPlanNo")
    @JsonProperty("DayPlanNoList")
    private List<Integer> dayPlanNoList = new ArrayList<>();

    /**
     * 调度号列表
     * 包含至少1个调度号
     */
    @NotEmpty(message = "调度号列表不能为空，至少包含1个调度号")
    @XmlElementWrapper(name = "ScheduleNoList")
    @XmlElement(name = "ScheduleNo")
    @JsonProperty("ScheduleNoList")
    private List<Integer> scheduleNoList = new ArrayList<>();

    /**
     * 路口中心位置经度
     * 使用WGS84坐标系，符合GA/T 543.9的DE011919
     */
    @NotNull(message = "路口中心位置经度不能为空")
    @XmlElement(name = "Longitude", required = true)
    @JsonProperty("Longitude")
    private Double longitude;

    /**
     * 路口中心位置纬度
     * 使用WGS84坐标系，符合GA/T 543.9的DE011919
     */
    @NotNull(message = "路口中心位置纬度不能为空")
    @XmlElement(name = "Latitude", required = true)
    @JsonProperty("Latitude")
    private Double latitude;

    /**
     * 路口位置海拔高度（米）
     */
    @NotNull(message = "路口位置海拔高度不能为空")
    @XmlElement(name = "Altitude", required = true)
    @JsonProperty("Altitude")
    private Integer altitude;

    // 构造函数
    public CrossParam() {
    }

    public CrossParam(String crossId, String crossName, CrossFeature feature, CrossGrade grade) {
        this.crossId = crossId;
        this.crossName = crossName;
        this.feature = feature;
        this.grade = grade;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public String getCrossName() {
        return crossName;
    }

    public void setCrossName(String crossName) {
        this.crossName = crossName;
    }

    public CrossFeature getFeature() {
        return feature;
    }

    public void setFeature(CrossFeature feature) {
        this.feature = feature;
    }

    public CrossGrade getGrade() {
        return grade;
    }

    public void setGrade(CrossGrade grade) {
        this.grade = grade;
    }

    public List<Integer> getDetNoList() {
        return detNoList;
    }

    public void setDetNoList(List<Integer> detNoList) {
        this.detNoList = detNoList;
    }

    public List<Integer> getLaneNoList() {
        return laneNoList;
    }

    public void setLaneNoList(List<Integer> laneNoList) {
        this.laneNoList = laneNoList;
    }

    public List<Integer> getPedestrianNoList() {
        return pedestrianNoList;
    }

    public void setPedestrianNoList(List<Integer> pedestrianNoList) {
        this.pedestrianNoList = pedestrianNoList;
    }

    public List<Integer> getLampGroupNoList() {
        return lampGroupNoList;
    }

    public void setLampGroupNoList(List<Integer> lampGroupNoList) {
        this.lampGroupNoList = lampGroupNoList;
    }

    public List<String> getSignalGroupNoList() {
        return signalGroupNoList;
    }

    public void setSignalGroupNoList(List<String> signalGroupNoList) {
        this.signalGroupNoList = signalGroupNoList;
    }

    public String getGreenConflictMatrix() {
        return greenConflictMatrix;
    }

    public void setGreenConflictMatrix(String greenConflictMatrix) {
        this.greenConflictMatrix = greenConflictMatrix;
    }

    public List<Integer> getStageNoList() {
        return stageNoList;
    }

    public void setStageNoList(List<Integer> stageNoList) {
        this.stageNoList = stageNoList;
    }

    public List<Integer> getPlanNoList() {
        return planNoList;
    }

    public void setPlanNoList(List<Integer> planNoList) {
        this.planNoList = planNoList;
    }

    public List<Integer> getDayPlanNoList() {
        return dayPlanNoList;
    }

    public void setDayPlanNoList(List<Integer> dayPlanNoList) {
        this.dayPlanNoList = dayPlanNoList;
    }

    public List<Integer> getScheduleNoList() {
        return scheduleNoList;
    }

    public void setScheduleNoList(List<Integer> scheduleNoList) {
        this.scheduleNoList = scheduleNoList;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "CrossParam{" +
                "crossId='" + crossId + '\'' +
                ", crossName='" + crossName + '\'' +
                ", feature=" + feature +
                ", grade=" + grade +
                ", detNoList=" + detNoList +
                ", laneNoList=" + laneNoList +
                ", pedestrianNoList=" + pedestrianNoList +
                ", lampGroupNoList=" + lampGroupNoList +
                ", signalGroupNoList=" + signalGroupNoList +
                ", greenConflictMatrix='" + greenConflictMatrix + '\'' +
                ", stageNoList=" + stageNoList +
                ", planNoList=" + planNoList +
                ", dayPlanNoList=" + dayPlanNoList +
                ", scheduleNoList=" + scheduleNoList +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                '}';
    }

    /**
     * 验证绿冲突矩阵的有效性
     * 矩阵长度应等于信号组数量的平方，且只包含0和1
     */
    public boolean validateGreenConflictMatrix() {
        if (greenConflictMatrix == null || signalGroupNoList == null) {
            return false;
        }

        int expectedLength = signalGroupNoList.size() * signalGroupNoList.size();
        if (greenConflictMatrix.length() != expectedLength) {
            return false;
        }

        return greenConflictMatrix.matches("[01]+");
    }

    /**
     * 生成默认的绿冲突矩阵（所有信号组互不冲突）
     */
    public void generateDefaultGreenConflictMatrix() {
        if (signalGroupNoList != null) {
            int size = signalGroupNoList.size();
            StringBuilder matrix = new StringBuilder();
            for (int i = 0; i < size * size; i++) {
                matrix.append("0");
            }
            this.greenConflictMatrix = matrix.toString();
        }
    }

    /**
     * 设置指定信号组之间的冲突关系
     * @param groupIndex1 信号组1的索引
     * @param groupIndex2 信号组2的索引
     * @param conflict 是否冲突（true为冲突，false为不冲突）
     */
    public void setConflict(int groupIndex1, int groupIndex2, boolean conflict) {
        if (greenConflictMatrix == null || signalGroupNoList == null) {
            return;
        }

        int size = signalGroupNoList.size();
        if (groupIndex1 >= size || groupIndex2 >= size || groupIndex1 < 0 || groupIndex2 < 0) {
            return;
        }

        char[] matrix = greenConflictMatrix.toCharArray();
        int index = groupIndex1 * size + groupIndex2;
        matrix[index] = conflict ? '1' : '0';

        // 设置对称位置
        int symmetricIndex = groupIndex2 * size + groupIndex1;
        matrix[symmetricIndex] = conflict ? '1' : '0';

        this.greenConflictMatrix = new String(matrix);
    }

    /**
     * 检查两个信号组是否冲突
     * @param groupIndex1 信号组1的索引
     * @param groupIndex2 信号组2的索引
     * @return 是否冲突
     */
    public boolean isConflict(int groupIndex1, int groupIndex2) {
        if (greenConflictMatrix == null || signalGroupNoList == null) {
            return false;
        }

        int size = signalGroupNoList.size();
        if (groupIndex1 >= size || groupIndex2 >= size || groupIndex1 < 0 || groupIndex2 < 0) {
            return false;
        }

        int index = groupIndex1 * size + groupIndex2;
        return greenConflictMatrix.charAt(index) == '1';
    }
}