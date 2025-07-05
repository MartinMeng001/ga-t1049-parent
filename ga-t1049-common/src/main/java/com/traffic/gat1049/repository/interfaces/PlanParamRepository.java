package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.PlanParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配时方案Repository
 */
@Repository
public interface PlanParamRepository extends BaseMapper<PlanParam> {

    /**
     * 根据路口ID查询所有配时方案
     */
    @Select("SELECT * FROM plan_param WHERE cross_id = #{crossId} ORDER BY plan_no")
    List<PlanParam> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和方案号查询
     */
    @Select("SELECT * FROM plan_param WHERE cross_id = #{crossId} AND plan_no = #{planNo}")
    PlanParam findByCrossIdAndPlanNo(@Param("crossId") String crossId, @Param("planNo") Integer planNo);

    /**
     * 查询协调方案（有协调相位的方案）
     */
    @Select("SELECT * FROM plan_param WHERE cross_id = #{crossId} AND coord_stage_no > 0 ORDER BY plan_no")
    List<PlanParam> findCoordinatedPlans(@Param("crossId") String crossId);

    /**
     * 根据周期长度范围查询方案
     */
    @Select("SELECT * FROM plan_param WHERE cross_id = #{crossId} " +
            "AND cycle_len BETWEEN #{minCycle} AND #{maxCycle} ORDER BY plan_no")
    List<PlanParam> findByCycleLenRange(@Param("crossId") String crossId,
                                        @Param("minCycle") Integer minCycle,
                                        @Param("maxCycle") Integer maxCycle);

    /**
     * 查询方案及其阶段配时
     */
    @Select("SELECT pp.*, spp.stage_no, spp.green_time, spp.yellow_time, spp.red_time " +
            "FROM plan_param pp " +
            "LEFT JOIN stage_plan_param spp ON pp.cross_id = spp.cross_id AND pp.plan_no = spp.plan_no " +
            "WHERE pp.cross_id = #{crossId} ORDER BY pp.plan_no, spp.stage_no")
    @Results(id = "planWithStageMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "planNo", column = "plan_no"),
            @Result(property = "planName", column = "plan_name"),
            @Result(property = "cycleLen", column = "cycle_len"),
            @Result(property = "coordStageNo", column = "coord_stage_no"),
            @Result(property = "offset", column = "offset"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<PlanParam> findWithStageTiming(@Param("crossId") String crossId);
}