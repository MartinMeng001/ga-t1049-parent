package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.OperationTypeAdapter;
import com.traffic.gat1049.protocol.model.base.BaseCommand;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.model.enums.OperationType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置配时方案参数命令
 * 对应文档中的 SetPlanParam
 *
 * 更新说明：
 * - 添加了阶段参数列表 StageParamList 字段
 * - 支持新增配时方案时同步新增阶段参数
 * - 当无需改变方案现有阶段参数时，StageParamList 可为空
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SetPlanParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SetPlanParam {//extends BaseCommand

    /**
     * 设置类型（新增，修改，删除）
     * 取值：1-新增, 2-修改, 3-删除
     */
    @XmlElement(name = "Oper", required = true)
    @XmlJavaTypeAdapter(OperationTypeAdapter.class)
    @JsonProperty("Oper")
    private OperationType oper;

    /**
     * 阶段参数列表
     * 包含 0 到多个阶段参数
     *
     * 使用说明：
     * - 无需改变方案现有阶段参数时（仅修改配时方案参数），StageParamList 为空
     * - 新增配时方案并同步新增阶段参数时，填入新增 StageParam 对象信息
     * - 新增时阶段号 StageNo 取值 0，在新增配时方案成功后，从成功应答数据对象中获取信控系统确定的阶段号
     */
    @XmlElementWrapper(name = "StageParamList")
    @XmlElement(name = "StageParam")
    @JsonProperty("StageParamList")
    private List<StageParam> stageParamList = new ArrayList<>();

    /**
     * 配时方案参数
     * 在新增配时方案时，方案序号取值 0，从成功应答返回 PlanParam 对象中获取信控系统分配的配时方案序号
     */
    @XmlElement(name = "PlanParam", required = true)
    @JsonProperty("PlanParam")
    private PlanParam planParam;

    // 构造函数
    public SetPlanParam() {
        //super();
    }

    public SetPlanParam(OperationType oper, PlanParam planParam) {
        //super();
        this.oper = oper;
        this.planParam = planParam;
    }

    public SetPlanParam(OperationType oper, List<StageParam> stageParamList, PlanParam planParam) {
        //super();
        this.oper = oper;
        this.stageParamList = stageParamList != null ? stageParamList : new ArrayList<>();
        this.planParam = planParam;
    }

    // Getters and Setters
    public OperationType getOper() {
        return oper;
    }

    public void setOper(OperationType oper) {
        this.oper = oper;
    }

    public List<StageParam> getStageParamList() {
        return stageParamList;
    }

    public void setStageParamList(List<StageParam> stageParamList) {
        this.stageParamList = stageParamList != null ? stageParamList : new ArrayList<>();
    }

    /**
     * 添加阶段参数
     * @param stageParam 阶段参数
     */
    public void addStageParam(StageParam stageParam) {
        if (this.stageParamList == null) {
            this.stageParamList = new ArrayList<>();
        }
        if (stageParam != null) {
            this.stageParamList.add(stageParam);
        }
    }

    /**
     * 检查是否包含阶段参数
     * @return true 如果包含阶段参数
     */
    public boolean hasStageParams() {
        return stageParamList != null && !stageParamList.isEmpty();
    }

    public PlanParam getPlanParam() {
        return planParam;
    }

    public void setPlanParam(PlanParam planParam) {
        this.planParam = planParam;
    }

    /**
     * 验证参数完整性
     * @return 验证结果
     */
    public boolean isValid() {
        if (oper == null || planParam == null) {
            return false;
        }

        // 对于新增操作，检查阶段参数的完整性
        if (oper == OperationType.ADD && hasStageParams()) {
            for (StageParam stageParam : stageParamList) {
                if (stageParam.getCrossId() == null || stageParam.getStageNo() == null) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "SetPlanParam{" +
                "oper=" + oper +
                ", stageParamList=" + (stageParamList != null ? stageParamList.size() + " items" : "null") +
                ", planParam=" + planParam +
                "} " + super.toString();
    }
}