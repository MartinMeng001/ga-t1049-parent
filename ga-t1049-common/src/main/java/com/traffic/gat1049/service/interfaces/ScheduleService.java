package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.model.enums.OperationType;

import java.util.List;

/**
 * 调度服务接口
 */
public interface ScheduleService {

    /**
     * 根据路口编号获取调度列表
     *
     * @param crossId 路口编号
     * @return 调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和调度号获取调度
     *
     * @param crossId 路口编号
     * @param scheduleNo 调度号
     * @return 调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam findByCrossIdAndScheduleNo(String crossId, Integer scheduleNo) throws BusinessException;

    /**
     * 获取调度参数 (兼容原PlanService接口)
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
     * 获取所有调度
     *
     * @return 所有调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> findAllSchedules() throws BusinessException;

    /**
     * 删除调度
     *
     * @param crossId 路口编号
     * @param scheduleNo 调度号
     * @throws BusinessException 业务异常
     */
    void deleteSchedule(String crossId, Integer scheduleNo) throws BusinessException;

    /**
     * 添加调度
     *
     * @param scheduleParam 调度参数
     * @return 添加后的调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam addSchedule(ScheduleParam scheduleParam) throws BusinessException;

    /**
     * 修改调度
     *
     * @param scheduleParam 调度参数
     * @return 修改后的调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam modifySchedule(ScheduleParam scheduleParam) throws BusinessException;

    /**
     * 验证调度参数
     *
     * @param scheduleParam 调度参数
     * @throws BusinessException 验证异常
     */
    void validateSchedule(ScheduleParam scheduleParam) throws BusinessException;

    /**
     * 根据日计划号查找调度
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @return 调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> findByDayPlanNo(String crossId, Integer dayPlanNo) throws BusinessException;
}