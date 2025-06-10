package com.traffic.gat1049.model.entity.intersection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseParam;
import com.traffic.gat1049.model.enums.CommMode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 信号机参数
 * 对应文档中的 SignalController
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SignalController")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalController extends BaseParam {

    /**
     * 信号机编号 - 取值12位交通管理部门机构代码+5位数字
     */
    @NotBlank(message = "信号机编号不能为空")
    @Pattern(regexp = "\\d{17}", message = "信号机编号格式错误，应为17位数字")
    @XmlElement(name = "SignalControlerID", required = true)
    @JsonProperty("SignalControlerID")
    private String signalControllerId;

    /**
     * 供应商
     */
    @NotBlank(message = "供应商不能为空")
    @XmlElement(name = "Supplier", required = true)
    @JsonProperty("Supplier")
    private String supplier;

    /**
     * 规格型号
     */
    @NotBlank(message = "规格型号不能为空")
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private String type;

    /**
     * 通信接口
     */
    @XmlElement(name = "CommMode", required = true)
    @JsonProperty("CommMode")
    private CommMode commMode;

    /**
     * 控制路口编号列表
     */
    @NotEmpty(message = "控制路口编号列表不能为空")
    @XmlElementWrapper(name = "CrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("CrossIDList")
    private List<String> crossIdList = new ArrayList<>();

    // 构造函数
    public SignalController() {
        super();
    }

    public SignalController(String signalControllerId, String supplier, String type) {
        super();
        this.signalControllerId = signalControllerId;
        this.supplier = supplier;
        this.type = type;
    }

    // Getters and Setters
    public String getSignalControllerId() {
        return signalControllerId;
    }

    public void setSignalControllerId(String signalControllerId) {
        this.signalControllerId = signalControllerId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CommMode getCommMode() {
        return commMode;
    }

    public void setCommMode(CommMode commMode) {
        this.commMode = commMode;
    }

    public List<String> getCrossIdList() {
        return crossIdList;
    }

    public void setCrossIdList(List<String> crossIdList) {
        this.crossIdList = crossIdList;
    }

    @Override
    public String toString() {
        return "SignalController{" +
                "signalControllerId='" + signalControllerId + '\'' +
                ", supplier='" + supplier + '\'' +
                ", type='" + type + '\'' +
                ", commMode=" + commMode +
                ", crossIdList=" + crossIdList +
                "} " + super.toString();
    }
}
