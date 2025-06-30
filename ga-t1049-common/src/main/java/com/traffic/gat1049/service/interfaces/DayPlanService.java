package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.model.enums.OperationType;

import java.util.List;

/**
 * 日计划服务接口
 * 更新版本 - 符合最新协议定义
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
     * 增强版本 - 包含更严格的验证规则
     *
     * @param dayPlanParam 日计划参数
     * @throws BusinessException 验证异常
     */
    void validateDayPlan(DayPlanParam dayPlanParam) throws BusinessException;

    /**
     * 验证日计划名称是否重复
     * 新增方法 - 确保日计划名称在同一路口内唯一
     *
     * @param crossId 路口编号
     * @param dayPlanName 日计划名称
     * @param excludeDayPlanNo 排除的日计划号（用于修改时排除自身）
     * @return 是否重复
     * @throws BusinessException 业务异常
     */
    boolean isDayPlanNameDuplicate(String crossId, String dayPlanName, Integer excludeDayPlanNo) throws BusinessException;

    /**
     * 根据日计划名称查找日计划
     * 新增方法
     *
     * @param crossId 路口编号
     * @param dayPlanName 日计划名称
     * @return 日计划参数，如果不存在返回null
     * @throws BusinessException 业务异常
     */
    DayPlanParam findByName(String crossId, String dayPlanName) throws BusinessException;

    /**
     * 批量设置日计划参数
     * 新增方法 - 支持批量操作
     *
     * @param operationType 操作类型
     * @param dayPlanParams 日计划参数列表
     * @return 处理结果列表
     * @throws BusinessException 业务异常
     */
    List<DayPlanParam> batchSetDayPlanParams(OperationType operationType, List<DayPlanParam> dayPlanParams) throws BusinessException;

    /**
     * 复制日计划
     * 新增方法 - 将一个日计划复制为新的日计划
     *
     * @param sourceCrossId 源路口编号
     * @param sourceDayPlanNo 源日计划号
     * @param targetCrossId 目标路口编号
     * @param targetDayPlanNo 目标日计划号
     * @param targetDayPlanName 目标日计划名称
     * @return 复制后的日计划参数
     * @throws BusinessException 业务异常
     */
    DayPlanParam copyDayPlan(String sourceCrossId, Integer sourceDayPlanNo,
                             String targetCrossId, Integer targetDayPlanNo,
                             String targetDayPlanName) throws BusinessException;

    /**
     * 检查日计划是否被调度使用
     * 新增方法 - 删除前检查是否有调度正在使用该日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @return 是否被使用
     * @throws BusinessException 业务异常
     */
    boolean isDayPlanInUse(String crossId, Integer dayPlanNo) throws BusinessException;

    /**
     * 获取日计划的使用情况
     * 新增方法 - 获取哪些调度正在使用该日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @return 使用该日计划的调度号列表
     * @throws BusinessException 业务异常
     */
    List<Integer> getDayPlanUsage(String crossId, Integer dayPlanNo) throws BusinessException;

    /**
     * 验证时段配置的合理性
     * 新增方法 - 验证时段配置是否合理（时间不重叠、覆盖24小时等）
     *
     * @param dayPlanParam 日计划参数
     * @throws BusinessException 验证异常
     */
    void validatePeriodConfiguration(DayPlanParam dayPlanParam) throws BusinessException;

    /**
     * 自动优化时段配置
     * 新增方法 - 自动优化时段配置，消除时间间隙
     *
     * @param dayPlanParam 日计划参数
     * @return 优化后的日计划参数
     * @throws BusinessException 业务异常
     */
    DayPlanParam optimizePeriodConfiguration(DayPlanParam dayPlanParam) throws BusinessException;
}