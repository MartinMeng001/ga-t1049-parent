package com.traffic.gat1049.protocol.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.command.*;
import com.traffic.gat1049.protocol.model.intersection.*;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.protocol.model.sdo.*;
import com.traffic.gat1049.protocol.model.signal.*;
import com.traffic.gat1049.protocol.model.system.*;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作命令
 * 符合GA/T 1049.1标准的操作结构
 * 修改版本：支持多个对象数据
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
     * 数据对象列表 - 改为支持多个对象
     * 使用@XmlElements注解支持多种类型的对象
     */
    @XmlElements({
            // 系统预定义数据对象 (SDO)
            @XmlElement(name = "SDO_Error", type = SdoError.class),
            @XmlElement(name = "SDO_User", type = SdoUser.class),
            @XmlElement(name = "SDO_HeartBeat", type = SdoHeartBeat.class),
            @XmlElement(name = "SDO_MsgEntity", type = SdoMsgEntity.class),
            @XmlElement(name = "SDO_TimeOut", type = SdoTimeOut.class),
            @XmlElement(name = "SDO_TimeServer", type = SdoTimeServer.class),

            // 信号控制相关对象
            @XmlElement(name = "SignalControler", type = SignalController.class),
            @XmlElement(name = "LampGroup", type = LampGroup.class),
            @XmlElement(name = "LaneParam", type = LaneParam.class),
            @XmlElement(name = "SignalGroupParam", type = SignalGroupParam.class),
            @XmlElement(name = "PedestrianParam", type = PedestrianParam.class),
            @XmlElement(name = "StageParam", type = StageParam.class),
            @XmlElement(name = "PlanParam", type = PlanParam.class),
            @XmlElement(name = "DayPlanParam", type = DayPlanParam.class),
            @XmlElement(name = "ScheduleParam", type = ScheduleParam.class),
            @XmlElement(name = "DetectorParam", type = DetectorParam.class),

            // 路线和区域参数
            @XmlElement(name = "RouteParam", type = RouteParam.class),
            @XmlElement(name = "RegionParam", type = RegionParam.class),
            @XmlElement(name = "SubRegionParam", type = SubRegionParam.class),
            @XmlElement(name = "CrossParam", type = CrossParam.class),

            // 系统信息对象
            @XmlElement(name = "SysInfo", type = SysInfo.class),
            @XmlElement(name = "SysState", type = SysState.class),
            @XmlElement(name = "CrossState", type = CrossState.class),
            @XmlElement(name = "SignalControlerError", type = SignalControllerError.class),

            // 命令对象
            @XmlElement(name = "TSCCmd", type = TSCCmd.class),
            @XmlElement(name = "SetDayPlanParam", type = SetDayPlanParam.class),
            @XmlElement(name = "SetPlanParam", type = SetPlanParam.class),

            // *** 添加这些缺失的控制命令注册 ***
            @XmlElement(name = "LockFlowDirection", type = LockFlowDirection.class),
            @XmlElement(name = "UnlockFlowDirection", type = UnlockFlowDirection.class),
            @XmlElement(name = "StageCtrl", type = AdjustStage.class),
            @XmlElement(name = "CrossReportCtrl", type = CrossReportCtrl.class),
            @XmlElement(name = "CenterPlan", type = CenterPlan.class),
            @XmlElement(name = "SetDayPlanParam", type = SetDayPlanParam.class),
            @XmlElement(name = "SetScheduleParam", type = SetScheduleParam.class),

            @XmlElement(name = "CrossTrafficData", type = CrossTrafficData.class),
            @XmlElement(name = "StageTrafficData", type = StageTrafficData.class),
            @XmlElement(name = "VarLaneStatus", type = VarLaneStatus.class),
            @XmlElement(name = "CrossModePlan", type = CrossModePlan.class),
            @XmlElement(name = "CrossCycle", type = CrossCycle.class),
            @XmlElement(name = "CrossStage", type = CrossStage.class),
            @XmlElement(name = "CrossSignalGroupStatus", type = CrossSignalGroupStatus.class),
            @XmlElement(name = "RouteControlModeStatus", type = RouteControlModeStatus.class),
            @XmlElement(name = "RouteSpeed", type = RouteSpeed.class),

            // 新增：重传运行信息命令
            @XmlElement(name = "CtrlVarLane", type = CtrlVarLane.class),
            @XmlElement(name = "CrossCtrlInfo", type = CrossCtrlInfo.class),
            @XmlElement(name = "CrossRunInfoRetrans", type = CrossRunInfoRetrans.class)

            // 通用对象支持
            //@XmlElement(name = "GenericObject", type = Object.class)
            // 可以根据需要添加更多类型
    })
    @JsonProperty("dataList")
    private List<Object> dataList = new ArrayList<>();

    // 构造函数
    public Operation() {}

    public Operation(Integer order, String name) {
        this.order = order;
        this.name = name;
        this.dataList = new ArrayList<>();
    }

    public Operation(Integer order, String name, Object data) {
        this.order = order;
        this.name = name;
        this.dataList = new ArrayList<>();
        if (data != null) {
            this.dataList.add(data);
        }
    }

    public Operation(Integer order, String name, List<Object> dataList) {
        this.order = order;
        this.name = name;
        this.dataList = dataList != null ? new ArrayList<>(dataList) : new ArrayList<>();
    }

    // Getters and Setters
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList != null ? dataList : new ArrayList<>();
    }

    // 为了向后兼容，保留原来的getData/setData方法
    /**
     * 获取第一个数据对象（向后兼容）
     * @return 第一个数据对象，如果列表为空返回null
     */
    @JsonProperty("data")
    public Object getData() {
        return dataList.isEmpty() ? null : dataList.get(0);
    }

    /**
     * 设置单个数据对象（向后兼容）
     * 会清空列表并添加新对象
     * @param data 要设置的数据对象
     */
    public void setData(Object data) {
        this.dataList.clear();
        if (data != null) {
            this.dataList.add(data);
        }
    }

    // 便捷方法
    /**
     * 添加数据对象到列表
     * @param data 要添加的数据对象
     */
    public void addData(Object data) {
        if (data != null) {
            this.dataList.add(data);
        }
    }

    /**
     * 移除指定的数据对象
     * @param data 要移除的数据对象
     * @return 是否成功移除
     */
    public boolean removeData(Object data) {
        return this.dataList.remove(data);
    }

    /**
     * 获取指定索引的数据对象
     * @param index 索引
     * @return 数据对象
     */
    public Object getData(int index) {
        if (index >= 0 && index < dataList.size()) {
            return dataList.get(index);
        }
        return null;
    }

    /**
     * 获取数据对象数量
     * @return 数据对象数量
     */
    public int getDataCount() {
        return dataList.size();
    }

    /**
     * 检查是否包含数据对象
     * @return 是否包含数据对象
     */
    public boolean hasData() {
        return !dataList.isEmpty();
    }

    /**
     * 清空所有数据对象
     */
    public void clearData() {
        this.dataList.clear();
    }

    /**
     * 获取指定类型的数据对象列表
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 指定类型的对象列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getDataByType(Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (Object data : dataList) {
            if (clazz.isInstance(data)) {
                result.add((T) data);
            }
        }
        return result;
    }

    /**
     * 获取第一个指定类型的数据对象
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 第一个匹配的对象，如果没有找到返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T getFirstDataByType(Class<T> clazz) {
        for (Object data : dataList) {
            if (clazz.isInstance(data)) {
                return (T) data;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "order=" + order +
                ", name='" + name + '\'' +
                ", dataCount=" + dataList.size() +
                ", dataList=" + dataList +
                '}';
    }
}