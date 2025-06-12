package com.traffic.gat1049.protocol.model.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 子区参数
 * 对应文档中的 SubRegionParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SubRegionParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubRegionParam extends BaseParam {

    /**
     * 子区编号 - 全局唯一，取值6位行政区划代码+5位数字
     */
    @NotBlank(message = "子区编号不能为空")
    @Pattern(regexp = "\\d{11}", message = "子区编号格式错误，应为11位数字")
    @XmlElement(name = "SubRegionID", required = true)
    @JsonProperty("SubRegionID")
    private String subRegionId;

    /**
     * 子区名称
     */
    @NotBlank(message = "子区名称不能为空")
    @XmlElement(name = "SubRegionName", required = true)
    @JsonProperty("SubRegionName")
    private String subRegionName;

    /**
     * 路口编号列表
     */
    @NotEmpty(message = "路口编号列表不能为空")
    @XmlElementWrapper(name = "CrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("CrossIDList")
    private List<String> crossIdList = new ArrayList<>();

    /**
     * 关键路口编号列表
     */
    @XmlElementWrapper(name = "KeyCrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("KeyCrossIDList")
    private List<String> keyCrossIdList = new ArrayList<>();

    // 构造函数
    public SubRegionParam() {
        super();
    }

    public SubRegionParam(String subRegionId, String subRegionName) {
        super();
        this.subRegionId = subRegionId;
        this.subRegionName = subRegionName;
    }

    // Getters and Setters
    public String getSubRegionId() {
        return subRegionId;
    }

    public void setSubRegionId(String subRegionId) {
        this.subRegionId = subRegionId;
    }

    public String getSubRegionName() {
        return subRegionName;
    }

    public void setSubRegionName(String subRegionName) {
        this.subRegionName = subRegionName;
    }

    public List<String> getCrossIdList() {
        return crossIdList;
    }

    public void setCrossIdList(List<String> crossIdList) {
        this.crossIdList = crossIdList;
    }

    public List<String> getKeyCrossIdList() {
        return keyCrossIdList;
    }

    public void setKeyCrossIdList(List<String> keyCrossIdList) {
        this.keyCrossIdList = keyCrossIdList;
    }

    @Override
    public String toString() {
        return "SubRegionParam{" +
                "subRegionId='" + subRegionId + '\'' +
                ", subRegionName='" + subRegionName + '\'' +
                ", crossIdList=" + crossIdList +
                ", keyCrossIdList=" + keyCrossIdList +
                "} " + super.toString();
    }
}
