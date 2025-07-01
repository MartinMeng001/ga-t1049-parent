package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.command.CrossCtrlInfo;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.runtime.CrossModePlan;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.model.enums.OperationType;

import java.util.List;

/**
 * 配时方案服务接口
 */
public interface PlanService {

    /**
     * 根据路口编号获取配时方案列表
     *
     * @param crossId 路口编号
     * @return 配时方案列表
     * @throws BusinessException 业务异常
     */
    List<PlanParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和方案序号获取配时方案
     *
     * @param crossId 路口编号
     * @param planNo 方案序号
     * @return 配时方案
     * @throws BusinessException 业务异常
     */
    PlanParam findByCrossIdAndPlanNo(String crossId, Integer planNo) throws BusinessException;

    /**
     * 设置配时方案参数
     *
     * @param operationType 操作类型
     * @param planParam 配时方案参数
     * @return 处理后的配时方案
     * @throws BusinessException 业务异常
     */
    PlanParam setPlanParam(OperationType operationType, PlanParam planParam) throws BusinessException;

    /**
     * 获取所有配时方案
     *
     * @return 所有配时方案列表
     * @throws BusinessException 业务异常
     */
    List<PlanParam> findAllPlans() throws BusinessException;

    /**
     * 删除配时方案
     *
     * @param crossId 路口编号
     * @param planNo 方案序号
     * @throws BusinessException 业务异常
     */
    void deletePlan(String crossId, Integer planNo) throws BusinessException;

    /**
     * 设置中心预案（新版本）
     * 支持预案最大运行时长
     *
     * @param controlMode 控制模式
     * @param maxRunTime 预案最大运行时长（分钟）
     * @param planParam 配时方案参数
     * @return 分配的方案号
     * @throws BusinessException 业务异常
     */
    Integer setCenterPlan(ControlMode controlMode, Integer maxRunTime, PlanParam planParam) throws BusinessException;

    /**
     * 设置中心预案（兼容版本）
     * 为保持向后兼容性，maxRunTime默认为60分钟
     *
     * @param controlMode 控制模式
     * @param planParam 配时方案参数
     * @return 分配的方案号
     * @throws BusinessException 业务异常
     */
    default Integer setCenterPlan(ControlMode controlMode, PlanParam planParam) throws BusinessException {
        return setCenterPlan(controlMode, 60, planParam);
    }

    /**
     * 获取当前控制模式和方案
     *
     * @param crossId 路口编号
     * @return 当前控制模式和方案
     * @throws BusinessException 业务异常
     */
    CrossCtrlInfo getCurrentControlMode(String crossId) throws BusinessException;

    /**
     * 获取全部当前控制模式和方案
     *
     * @return 当前控制模式和方案
     * @throws BusinessException 业务异常
     */
    List<CrossCtrlInfo> getAllCurrentControlMode() throws BusinessException;

}