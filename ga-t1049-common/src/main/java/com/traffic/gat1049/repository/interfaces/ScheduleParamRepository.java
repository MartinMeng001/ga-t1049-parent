package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.ScheduleParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 调度参数Repository
 * 基于修复后的schedule_param表结构
 */
@Repository
public interface ScheduleParamRepository extends BaseMapper<ScheduleParamEntity> {

    /**
     * 根据路口ID查询所有调度
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} ORDER BY schedule_no")
    List<ScheduleParamEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和调度号查询
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND schedule_no = #{scheduleNo}")
    ScheduleParamEntity findByCrossIdAndScheduleNo(@Param("crossId") String crossId, @Param("scheduleNo") Integer scheduleNo);

    /**
     * 根据调度类型查询
     * @param crossId 路口ID
     * @param type 调度类型：1-特殊日调度；2-时间段周调度；3-周调度
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND type = #{type} ORDER BY schedule_no")
    List<ScheduleParamEntity> findByCrossIdAndType(@Param("crossId") String crossId, @Param("type") Integer type);

    /**
     * 查询特殊日调度
     * @param crossId 路口ID
     * @param currentDate 当前日期(MM-DD格式)
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND type = 1 " +
            "AND start_day <= #{currentDate} AND end_day >= #{currentDate} ORDER BY schedule_no")
    List<ScheduleParamEntity> findSpecialDaySchedules(@Param("crossId") String crossId, @Param("currentDate") String currentDate);

    /**
     * 查询周调度
     * @param crossId 路口ID
     * @param weekDay 周几(1-7分别代表周一至周日)
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND type IN (2, 3) " +
            "AND (week_day = #{weekDay} OR week_day IS NULL) ORDER BY schedule_no")
    List<ScheduleParamEntity> findWeeklySchedules(@Param("crossId") String crossId, @Param("weekDay") Integer weekDay);

    /**
     * 查询当前有效的调度
     * @param crossId 路口ID
     * @param currentDate 当前日期(MM-DD格式)
     * @param weekDay 周几(1-7)
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} " +
            "AND ((type = 1 AND start_day <= #{currentDate} AND end_day >= #{currentDate}) " +
            "OR (type IN (2, 3) AND (week_day = #{weekDay} OR week_day IS NULL))) " +
            "ORDER BY type, schedule_no")
    List<ScheduleParamEntity> findCurrentActiveSchedules(@Param("crossId") String crossId,
                                                         @Param("currentDate") String currentDate,
                                                         @Param("weekDay") Integer weekDay);

    /**
     * 查询指定日期范围的特殊日调度
     * @param crossId 路口ID
     * @param startDate 开始日期(MM-DD)
     * @param endDate 结束日期(MM-DD)
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND type = 1 " +
            "AND ((start_day >= #{startDate} AND start_day <= #{endDate}) " +
            "OR (end_day >= #{startDate} AND end_day <= #{endDate}) " +
            "OR (start_day <= #{startDate} AND end_day >= #{endDate})) " +
            "ORDER BY start_day, schedule_no")
    List<ScheduleParamEntity> findSpecialDaySchedulesInRange(@Param("crossId") String crossId,
                                                             @Param("startDate") String startDate,
                                                             @Param("endDate") String endDate);

    /**
     * 查询使用指定日计划的调度
     * @param crossId 路口ID
     * @param dayPlanNo 日计划号
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND day_plan_no = #{dayPlanNo} ORDER BY schedule_no")
    List<ScheduleParamEntity> findByDayPlanNo(@Param("crossId") String crossId, @Param("dayPlanNo") Integer dayPlanNo);

    /**
     * 批量插入调度参数
     */
    @Insert("<script>" +
            "INSERT INTO schedule_param (cross_id, schedule_no, schedule_name, type, start_day, end_day, week_day, day_plan_no) VALUES " +
            "<foreach collection='schedules' item='schedule' separator=','>" +
            "(#{schedule.crossId}, #{schedule.scheduleNo}, #{schedule.scheduleName}, #{schedule.type}, " +
            "#{schedule.startDay}, #{schedule.endDay}, #{schedule.weekDay}, #{schedule.dayPlanNo})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("schedules") List<ScheduleParamEntity> schedules);

    /**
     * 删除路口的所有调度
     */
    @Delete("DELETE FROM schedule_param WHERE cross_id = #{crossId}")
    int deleteByCrossId(@Param("crossId") String crossId);

    /**
     * 删除指定类型的调度
     */
    @Delete("DELETE FROM schedule_param WHERE cross_id = #{crossId} AND type = #{type}")
    int deleteByCrossIdAndType(@Param("crossId") String crossId, @Param("type") Integer type);

    /**
     * 统计路口调度数量
     */
    @Select("SELECT COUNT(*) FROM schedule_param WHERE cross_id = #{crossId}")
    int countByCrossId(@Param("crossId") String crossId);

    /**
     * 统计各类型调度数量
     */
    @Select("SELECT type, COUNT(*) as count FROM schedule_param WHERE cross_id = #{crossId} GROUP BY type")
    @Results({
            @Result(property = "type", column = "type"),
            @Result(property = "count", column = "count")
    })
    List<Map<String, Object>> countByType(@Param("crossId") String crossId);

    /**
     * 检查调度号是否已存在
     */
    @Select("SELECT COUNT(*) FROM schedule_param WHERE cross_id = #{crossId} AND schedule_no = #{scheduleNo}")
    int existsByScheduleNo(@Param("crossId") String crossId, @Param("scheduleNo") Integer scheduleNo);
}