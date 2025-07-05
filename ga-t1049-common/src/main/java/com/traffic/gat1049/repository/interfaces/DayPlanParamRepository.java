package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.DayPlanParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

/**
 * 日计划参数Repository
 */
@Repository
public interface DayPlanParamRepository extends BaseMapper<DayPlanParam> {

    /**
     * 根据路口ID查询所有日计划
     */
    @Select("SELECT * FROM day_plan_param WHERE cross_id = #{crossId} ORDER BY day_plan_no")
    List<DayPlanParam> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和日计划号查询
     */
    @Select("SELECT * FROM day_plan_param WHERE cross_id = #{crossId} AND day_plan_no = #{dayPlanNo}")
    DayPlanParam findByCrossIdAndDayPlanNo(@Param("crossId") String crossId, @Param("dayPlanNo") Integer dayPlanNo);

    /**
     * 查询日计划及其时段信息
     */
    @Select("SELECT dp.*, pi.start_time, pi.plan_no, pi.ctrl_mode " +
            "FROM day_plan_param dp " +
            "LEFT JOIN period_info pi ON dp.cross_id = pi.cross_id AND dp.day_plan_no = pi.day_plan_no " +
            "WHERE dp.cross_id = #{crossId} ORDER BY dp.day_plan_no, pi.start_time")
    @Results(id = "dayPlanWithPeriodMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "dayPlanNo", column = "day_plan_no"),
            @Result(property = "dayPlanName", column = "day_plan_name"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<DayPlanParam> findWithPeriods(@Param("crossId") String crossId);

    /**
     * 根据当前时间查询适用的日计划
     */
    @Select("SELECT dp.* FROM day_plan_param dp " +
            "JOIN period_info pi ON dp.cross_id = pi.cross_id AND dp.day_plan_no = pi.day_plan_no " +
            "WHERE dp.cross_id = #{crossId} AND pi.start_time <= #{currentTime} " +
            "ORDER BY pi.start_time DESC LIMIT 1")
    DayPlanParam findCurrentActivePlan(@Param("crossId") String crossId, @Param("currentTime") LocalTime currentTime);
}