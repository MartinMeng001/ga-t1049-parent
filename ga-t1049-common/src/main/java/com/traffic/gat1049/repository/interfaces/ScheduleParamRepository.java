package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.ScheduleParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 调度参数Repository
 */
@Repository
public interface ScheduleParamRepository extends BaseMapper<ScheduleParam> {

    /**
     * 根据路口ID查询所有调度
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} ORDER BY schedule_no")
    List<ScheduleParam> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和调度号查询
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND schedule_no = #{scheduleNo}")
    ScheduleParam findByCrossIdAndScheduleNo(@Param("crossId") String crossId, @Param("scheduleNo") Integer scheduleNo);

    /**
     * 根据调度类型查询
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND type = #{type} ORDER BY schedule_no")
    List<ScheduleParam> findByCrossIdAndType(@Param("crossId") String crossId, @Param("type") Integer type);

    /**
     * 查询特殊日调度
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND type = 1 " +
            "AND start_day <= #{currentDate} AND end_day >= #{currentDate} ORDER BY schedule_no")
    List<ScheduleParam> findSpecialDaySchedules(@Param("crossId") String crossId, @Param("currentDate") String currentDate);

    /**
     * 查询周调度
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} AND type IN (2, 3) " +
            "AND (week_day = #{weekDay} OR week_day IS NULL) ORDER BY schedule_no")
    List<ScheduleParam> findWeeklySchedules(@Param("crossId") String crossId, @Param("weekDay") Integer weekDay);

    /**
     * 查询当前有效的调度
     */
    @Select("SELECT * FROM schedule_param WHERE cross_id = #{crossId} " +
            "AND ((type = 1 AND start_day <= #{currentDate} AND end_day >= #{currentDate}) " +
            "OR (type IN (2, 3) AND (week_day = #{weekDay} OR week_day IS NULL))) " +
            "ORDER BY type, schedule_no")
    List<ScheduleParam> findCurrentActiveSchedules(@Param("crossId") String crossId,
                                                   @Param("currentDate") String currentDate,
                                                   @Param("weekDay") Integer weekDay);
}
