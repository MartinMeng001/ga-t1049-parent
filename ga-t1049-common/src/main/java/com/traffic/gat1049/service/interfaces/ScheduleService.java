package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.model.enums.OperationType;
import com.traffic.gat1049.model.enums.ScheduleType;

import java.time.LocalDate;
import java.util.List;

/**
 * 调度服务接口（更新版本）
 * 根据最新ScheduleParam定义更新
 */
public interface ScheduleService {

    // ========== 基础CRUD操作 ==========

    /**
     * 根据路口编号获取调度列表
     *
     * @param crossId 路口编号
     * @return 调度列表（按优先级排序）
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
     * 设置调度参数（通用接口）
     *
     * @param operationType 操作类型
     * @param scheduleParam 调度参数
     * @return 处理后的调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam setScheduleParam(OperationType operationType, ScheduleParam scheduleParam) throws BusinessException;

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
     * 删除调度
     *
     * @param crossId 路口编号
     * @param scheduleNo 调度号
     * @throws BusinessException 业务异常
     */
    void deleteSchedule(String crossId, Integer scheduleNo) throws BusinessException;

    // ========== 查询操作 ==========

    /**
     * 获取所有调度
     *
     * @return 所有调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> findAllSchedules() throws BusinessException;

    /**
     * 根据日计划号查找调度
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @return 调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> findByDayPlanNo(String crossId, Integer dayPlanNo) throws BusinessException;

    /**
     * 根据调度类型查找调度
     *
     * @param crossId 路口编号
     * @param scheduleType 调度类型
     * @return 调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> findByScheduleType(String crossId, ScheduleType scheduleType) throws BusinessException;

    /**
     * 根据调度名称模糊查找
     *
     * @param crossId 路口编号
     * @param scheduleName 调度名称（支持模糊匹配）
     * @return 调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> findByScheduleNameLike(String crossId, String scheduleName) throws BusinessException;

    // ========== 智能调度选择 ==========

    /**
     * 根据指定日期获取有效的调度参数
     * 按照优先级规则选择最合适的调度
     *
     * @param crossId 路口编号
     * @param targetDate 目标日期
     * @return 有效的调度参数，如果没有匹配的返回null
     * @throws BusinessException 业务异常
     */
    ScheduleParam getEffectiveSchedule(String crossId, LocalDate targetDate) throws BusinessException;

    /**
     * 获取当前时间的有效调度参数
     *
     * @param crossId 路口编号
     * @return 当前有效的调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam getCurrentEffectiveSchedule(String crossId) throws BusinessException;

    /**
     * 预测未来几天的调度安排
     *
     * @param crossId 路口编号
     * @param days 预测天数
     * @return 调度安排列表（日期 -> 调度参数）
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> predictScheduleForDays(String crossId, int days) throws BusinessException;

    // ========== 验证和工具方法 ==========

    /**
     * 验证调度参数
     *
     * @param scheduleParam 调度参数
     * @throws BusinessException 验证异常
     */
    void validateSchedule(ScheduleParam scheduleParam) throws BusinessException;

    /**
     * 检查调度号是否已存在
     *
     * @param crossId 路口编号
     * @param scheduleNo 调度号
     * @return 是否存在
     * @throws BusinessException 业务异常
     */
    boolean existsScheduleNo(String crossId, Integer scheduleNo) throws BusinessException;

    /**
     * 获取下一个可用的调度号
     *
     * @param crossId 路口编号
     * @return 下一个可用调度号
     * @throws BusinessException 业务异常
     */
    Integer getNextAvailableScheduleNo(String crossId) throws BusinessException;

    // ========== 批量操作 ==========

    /**
     * 批量添加调度
     *
     * @param scheduleParams 调度参数列表
     * @return 添加成功的调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> batchAddSchedules(List<ScheduleParam> scheduleParams) throws BusinessException;

    /**
     * 批量删除调度
     *
     * @param crossId 路口编号
     * @param scheduleNos 调度号列表
     * @throws BusinessException 业务异常
     */
    void batchDeleteSchedules(String crossId, List<Integer> scheduleNos) throws BusinessException;

    /**
     * 复制调度到其他路口
     *
     * @param sourceCrossId 源路口编号
     * @param targetCrossId 目标路口编号
     * @param scheduleNos 要复制的调度号列表，null表示复制所有
     * @return 复制后的调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> copySchedulesToCross(String sourceCrossId, String targetCrossId,
                                             List<Integer> scheduleNos) throws BusinessException;

    // ========== 模板和预设 ==========

    /**
     * 创建标准调度模板
     * 包含工作日、周末、节假日等标准调度
     *
     * @param crossId 路口编号
     * @param workdayDayPlanNo 工作日日计划号
     * @param weekendDayPlanNo 周末日计划号
     * @param holidayDayPlanNo 节假日日计划号
     * @return 创建的调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> createStandardScheduleTemplate(String crossId, Integer workdayDayPlanNo,
                                                       Integer weekendDayPlanNo, Integer holidayDayPlanNo)
            throws BusinessException;

    /**
     * 重置路口调度为默认配置
     *
     * @param crossId 路口编号
     * @param keepExisting 是否保留现有调度
     * @return 重置后的调度列表
     * @throws BusinessException 业务异常
     */
    List<ScheduleParam> resetToDefaultSchedules(String crossId, boolean keepExisting) throws BusinessException;

    // ========== 统计和报告 ==========

    /**
     * 获取调度统计信息
     *
     * @param crossId 路口编号
     * @return 统计信息
     * @throws BusinessException 业务异常
     */
    ScheduleStatistics getScheduleStatistics(String crossId) throws BusinessException;

    /**
     * 获取调度参数
     *
     * @param crossId 路口编号
     * @param scheduleNo 调度编号
     * @return 调度参数
     * @throws BusinessException 业务异常
     */
    ScheduleParam getScheduleParam(String crossId, Integer scheduleNo) throws BusinessException;
    /**
     * 调度统计信息类
     */
    class ScheduleStatistics {
        private int totalSchedules;
        private int specialDaySchedules;
        private int weekPeriodSchedules;
        private int weekSchedules;
        private List<Integer> usedDayPlanNos;
        private List<Integer> availableScheduleNos;

        // getters and setters
        public int getTotalSchedules() { return totalSchedules; }
        public void setTotalSchedules(int totalSchedules) { this.totalSchedules = totalSchedules; }

        public int getSpecialDaySchedules() { return specialDaySchedules; }
        public void setSpecialDaySchedules(int specialDaySchedules) { this.specialDaySchedules = specialDaySchedules; }

        public int getWeekPeriodSchedules() { return weekPeriodSchedules; }
        public void setWeekPeriodSchedules(int weekPeriodSchedules) { this.weekPeriodSchedules = weekPeriodSchedules; }

        public int getWeekSchedules() { return weekSchedules; }
        public void setWeekSchedules(int weekSchedules) { this.weekSchedules = weekSchedules; }

        public List<Integer> getUsedDayPlanNos() { return usedDayPlanNos; }
        public void setUsedDayPlanNos(List<Integer> usedDayPlanNos) { this.usedDayPlanNos = usedDayPlanNos; }

        public List<Integer> getAvailableScheduleNos() { return availableScheduleNos; }
        public void setAvailableScheduleNos(List<Integer> availableScheduleNos) { this.availableScheduleNos = availableScheduleNos; }

        @Override
        public String toString() {
            return "ScheduleStatistics{" +
                    "totalSchedules=" + totalSchedules +
                    ", specialDaySchedules=" + specialDaySchedules +
                    ", weekPeriodSchedules=" + weekPeriodSchedules +
                    ", weekSchedules=" + weekSchedules +
                    ", usedDayPlanNos=" + usedDayPlanNos +
                    ", availableScheduleNos=" + availableScheduleNos +
                    '}';
        }
    }
}