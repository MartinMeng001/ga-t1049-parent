package com.traffic.gat1049.model.entity.system;
/**
 * 区域参数
 * 对应文档中的 RegionParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "RegionParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegionParam extends BaseParam {

    /**
     * 区域编号 - 全局唯一，取值6位行政区划代码+3位数字
     */
    @NotBlank(message = "区域编号不能为空")
    @Pattern(regexp = "\\d{9}", message = "区域编号格式错误，应为9位数字")
    @XmlElement(name = "RegionID", required = true)
    @JsonProperty("RegionID")
    private String regionId;

    /**
     * 区域名称
     */
    @NotBlank(message = "区域名称不能为空")
    @XmlElement(name = "RegionName", required = true)
    @JsonProperty("RegionName")
    private String regionName;

    /**
     * 子区编号列表
     */
    @XmlElementWrapper(name = "SubRegionIDList")
    @XmlElement(name = "SubRegionID")
    @JsonProperty("SubRegionIDList")
    private List<String> subRegionIdList = new ArrayList<>();

    /**
     * 路口编号列表
     */
    @NotEmpty(message = "路口编号列表不能为空")
    @XmlElementWrapper(name = "CrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("CrossIDList")
    private List<String> crossIdList = new ArrayList<>();

    // 构造函数
    public RegionParam() {
        super();
    }

    public RegionParam(String regionId, String regionName) {
        super();
        this.regionId = regionId;
        this.regionName = regionName;
    }

    // Getters and Setters
    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<String> getSubRegionIdList() {
        return subRegionIdList;
    }

    public void setSubRegionIdList(List<String> subRegionIdList) {
        this.subRegionIdList = subRegionIdList;
    }

    public List<String> getCrossIdList() {
        return crossIdList;
    }

    public void setCrossIdList(List<String> crossIdList) {
        this.crossIdList = crossIdList;
    }

    @Override
    public String toString() {
        return "RegionParam{" +
                "regionId='" + regionId + '\'' +
                ", regionName='" + regionName + '\'' +
                ", subRegionIdList=" + subRegionIdList +
                ", crossIdList=" + crossIdList +
                "} " + super.toString();
    }
}