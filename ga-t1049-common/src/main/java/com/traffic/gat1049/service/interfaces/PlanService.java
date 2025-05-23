package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.entity.signal.PlanParam;
import com.traffic.gat1049.model.entity.signal.DayPlanParam;
import com.traffic.gat1049.model.entity.signal.ScheduleParam;
import com.traffic.gat1049.model.entity.runtime.CrossModePlan;
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
     * 获取日计划参数
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @return 日计划参数
     * @throws BusinessException 业务异常
     */
    DayPlanParam getDayPlanParam(String crossId, Integer dayPlanNo) throws BusinessException;

    /**
     * 设置日计划参数
     *
     * @param operationType 操作类型
     * @param dayPlanParam 日计划参数
     * @return 处理后的日计划参数
     * @throws BusinessException 业务异常
     */
    DayPlanParam setDayPlanParam(OperationType operationType, DayPlanParam dayPlanParam) throws BusinessException;

    /**
     * 获取调度参数
     *
     * @param crossId 路口编号
     * @param scheduleNo 调度号
     * @return 调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam getScheduleParam(String crossId, Integer scheduleNo) throws BusinessException;

    /**
     * 设置调度参数
     *
     * @param operationType 操作类型
     * @param scheduleParam 调度参数
     * @return 处理后的调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam setScheduleParam(OperationType operationType, ScheduleParam scheduleParam) throws BusinessException;

    /**
     * 获取路口当前控制方式方案
     *
     * @param crossId 路口编号
     * @return 路口控制方式方案
     * @throws BusinessException 业务异常
     */
    CrossModePlan getCurrentModePlan(String crossId) throws BusinessException;

    /**
     * 设置路口控制方式
     *
     * @param crossId 路口编号
     * @param controlMode 控制方式
     * @param planNo 方案号
     * @throws BusinessException 业务异常
     */
    void setControlMode(String crossId, ControlMode controlMode, Integer planNo) throws BusinessException;

    /**
     * 下发中心预案
     *
     * @param crossControlMode 控制方式
     * @param planParam 配时方案参数
     * @return 分配的方案号
     * @throws BusinessException 业务异常
     */
    Integer setCenterPlan(ControlMode crossControlMode, PlanParam planParam) throws BusinessException;

    /**
     * 删除配时方案
     *
     * @param crossId 路口编号
     * @param planNo 方案序号
     * @throws BusinessException 业务异常
     */
    void deletePlan(String crossId, Integer planNo) throws BusinessException;

    /**
     * 验证配时方案的合法性
     *
     * @param planParam 配时方案参数
     * @throws BusinessException 业务异常
     */
    void validatePlan(PlanParam planParam) throws BusinessException;
}
