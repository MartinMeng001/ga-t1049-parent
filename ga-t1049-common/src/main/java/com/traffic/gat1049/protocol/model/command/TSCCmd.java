package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseCommand;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;

/**
 * 配置参数及运行信息通知、查询命令
 * 对应文档中的 TSCCmd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "TSCCmd")
@XmlAccessorType(XmlAccessType.FIELD)
public class TSCCmd {//extends BaseCommand

    /**
     * 查询对象的名称
     */
    @NotBlank(message = "对象名称不能为空")
    @XmlElement(name = "ObjName", required = true)
    @JsonProperty("ObjName")
    private String objName;

    /**
     * 查询对象的编号，仅查询"系统参数"对象时可为空
     */
    @XmlElement(name = "ID")
    @JsonProperty("ID")
    private String id;

    /**
     * 索引号，具体对象的属性所对应的序号，为空时指全部对象
     */
    @XmlElement(name = "No")
    @JsonProperty("No")
    private Integer no;

    // 构造函数
    public TSCCmd() {
        super();
    }

    public TSCCmd(String objName) {
        super();
        this.objName = objName;
    }

    public TSCCmd(String objName, String id) {
        super();
        this.objName = objName;
        this.id = id;
    }

    // Getters and Setters
    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = (id == null) ? "" : id; // 或者 "0"
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = (no == null) ? 0 : no;
    }

    @Override
    public String toString() {
        return "TSCCmd{" +
                "objName='" + objName + '\'' +
                ", id='" + id + '\'' +
                ", no=" + no +
                "} " + super.toString();
    }
}
