package com.traffic.gat1049.model.entity.command;

/**
 * 路口周期、阶段、信号组灯态、交通流数据上传设置命令
 * 对应文档中的 CrossReportCtrl
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossReportCtrl")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossReportCtrl extends BaseCommand {

    /**
     * 命令 - Start:开始主动上传，Stop:停止主动上传
     */
    @XmlElement(name = "Cmd", required = true)
    @JsonProperty("Cmd")
    private ReportCommand cmd;

    /**
     * 上传数据类型
     */
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private ReportDataType type;

    /**
     * 路口编号列表
     */
    @NotEmpty(message = "路口编号列表不能为空")
    @XmlElementWrapper(name = "CrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("CrossIDList")
    private List<String> crossIdList = new ArrayList<>();

    // 构造函数
    public CrossReportCtrl() {
        super();
    }

    public CrossReportCtrl(ReportCommand cmd, ReportDataType type) {
        super();
        this.cmd = cmd;
        this.type = type;
    }

    // Getters and Setters
    public ReportCommand getCmd() {
        return cmd;
    }

    public void setCmd(ReportCommand cmd) {
        this.cmd = cmd;
    }

    public ReportDataType getType() {
        return type;
    }

    public void setType(ReportDataType type) {
        this.type = type;
    }

    public List<String> getCrossIdList() {
        return crossIdList;
    }

    public void setCrossIdList(List<String> crossIdList) {
        this.crossIdList = crossIdList;
    }

    @Override
    public String toString() {
        return "CrossReportCtrl{" +
                "cmd=" + cmd +
                ", type=" + type +
                ", crossIdList=" + crossIdList +
                "} " + super.toString();
    }
}
