package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.model.enums.OperationType;

import java.util.List;

/**
 * 日计划服务接口
 */
public interface DayPlanService {

    /**
     * 根据路口编号获取日计划列表
     *
     * @param crossId 路口编号
     * @return 日计划列表
     * @throws BusinessException 业务异常
     */
    List<DayPlanParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和日计划号获取日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @return 日计划参数
     * @throws BusinessException 业务异常
     */
    DayPlanParam findByCrossIdAndDayPlanNo(String crossId, Integer dayPlanNo) throws BusinessException;

    /**
     * 获取日计划参数 (兼容原PlanService接口)
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
     * 获取所有日计划
     *
     * @return 所有日计划列表
     * @throws BusinessException 业务异常
     */
    List<DayPlanParam> findAllDayPlans() throws BusinessException;

    /**
     * 删除日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @throws BusinessException 业务异常
     */
    void deleteDayPlan(String crossId, Integer dayPlanNo) throws BusinessException;

    /**
     * 添加日计划
     *
     * @param dayPlanParam 日计划参数
     * @return 添加后的日计划参数
     * @throws BusinessException 业务异常
     */
    DayPlanParam addDayPlan(DayPlanParam dayPlanParam) throws BusinessException;

    /**
     * 修改日计划
     *
     * @param dayPlanParam 日计划参数
     * @return 修改后的日计划参数
     * @throws BusinessException 业务异常
     */
    DayPlanParam modifyDayPlan(DayPlanParam dayPlanParam) throws BusinessException;

    /**
     * 验证日计划参数
     *
     * @param dayPlanParam 日计划参数
     * @throws BusinessException 验证异常
     */
    void validateDayPlan(DayPlanParam dayPlanParam) throws BusinessException;
}