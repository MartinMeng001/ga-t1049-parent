package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.enums.DoorStatus;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.DoorStatusAdapter;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * 信号机柜门状态
 * 对应文档中的 SCDoorStatus (B.2.13)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SCDoorStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class SCDoorStatus {

    /**
     * 信号机设备编号
     * 取值同表B.7中信号机设备编号
     */
    @NotBlank(message = "信号机设备编号不能为空")
    @XmlElement(name = "SignalControllerID", required = true)
    @JsonProperty("SignalControllerID")
    private String signalControllerId;

    /**
     * 时间
     * 符合GA/T 543.6的DE0554
     */
    @NotBlank(message = "时间不能为空")
    @XmlElement(name = "Time", required = true)
    @JsonProperty("Time")
    private String time;

    /**
     * 机柜门状态列表
     * 包含至少1个机柜门状态DoorStatus
     */
    @NotNull(message = "机柜门状态列表不能为空")
    @Size(min = 1, message = "至少包含1个机柜门状态")
    @XmlElement(name = "DoorStatusList", required = true)
    @JsonProperty("DoorStatusList")
    private DoorStatusList doorStatusList;

    /**
     * 机柜门状态列表内部类
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DoorStatusList {
        @XmlElement(name = "DoorStatus")
        @JsonProperty("DoorStatus")
        private List<DoorStatusItem> doorStatus;

        public DoorStatusList() {}

        public DoorStatusList(List<DoorStatusItem> doorStatus) {
            this.doorStatus = doorStatus;
        }

        public List<DoorStatusItem> getDoorStatus() {
            return doorStatus;
        }

        public void setDoorStatus(List<DoorStatusItem> doorStatus) {
            this.doorStatus = doorStatus;
        }
    }

    /**
     * 机柜门状态项
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DoorStatusItem {
        /**
         * 机柜门序号
         * 取值1-20
         */
        @Min(value = 1, message = "机柜门序号最小值为1")
        @Max(value = 20, message = "机柜门序号最大值为20")
        @XmlElement(name = "DoorNo", required = true)
        @JsonProperty("DoorNo")
        private Integer doorNo;

        /**
         * 机柜门名称
         * 最大长度50
         */
        @Size(max = 50, message = "机柜门名称最大长度为50")
        @XmlElement(name = "DoorName", required = true)
        @JsonProperty("DoorName")
        private String doorName;

        /**
         * 机柜门当前状态
         * 取值：0-关闭，1-打开，9-未知
         */
        @NotNull(message = "机柜门状态不能为空")
        @XmlElement(name = "Status", required = true)
        @XmlJavaTypeAdapter(DoorStatusAdapter.class)
        @JsonProperty("Status")
        private DoorStatus status;

        // 构造函数
        public DoorStatusItem() {}

        public DoorStatusItem(Integer doorNo, String doorName, DoorStatus status) {
            this.doorNo = doorNo;
            this.doorName = doorName;
            this.status = status;
        }

        // Getters and Setters
        public Integer getDoorNo() {
            return doorNo;
        }

        public void setDoorNo(Integer doorNo) {
            this.doorNo = doorNo;
        }

        public String getDoorName() {
            return doorName;
        }

        public void setDoorName(String doorName) {
            this.doorName = doorName;
        }

        public DoorStatus getStatus() {
            return status;
        }

        public void setStatus(DoorStatus status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "DoorStatusItem{" +
                    "doorNo=" + doorNo +
                    ", doorName='" + doorName + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

    // 构造函数
    public SCDoorStatus() {}

    public SCDoorStatus(String signalControllerId, String time, DoorStatusList doorStatusList) {
        this.signalControllerId = signalControllerId;
        this.time = time;
        this.doorStatusList = doorStatusList;
    }

    // Getters and Setters
    public String getSignalControllerId() {
        return signalControllerId;
    }

    public void setSignalControllerId(String signalControllerId) {
        this.signalControllerId = signalControllerId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DoorStatusList getDoorStatusList() {
        return doorStatusList;
    }

    public void setDoorStatusList(DoorStatusList doorStatusList) {
        this.doorStatusList = doorStatusList;
    }

    @Override
    public String toString() {
        return "SCDoorStatus{" +
                "signalControllerId='" + signalControllerId + '\'' +
                ", time='" + time + '\'' +
                ", doorStatusList=" + doorStatusList +
                '}';
    }
}