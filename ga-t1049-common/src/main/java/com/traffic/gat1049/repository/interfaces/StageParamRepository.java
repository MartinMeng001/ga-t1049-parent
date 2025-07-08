package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.StageParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 阶段参数Repository
 */
@Repository
public interface StageParamRepository extends BaseMapper<StageParamEntity> {

    /**
     * 根据路口ID查询所有阶段
     */
    @Select("SELECT * FROM stage_param WHERE cross_id = #{crossId} ORDER BY stage_no")
    List<StageParamEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和阶段号查询
     */
    @Select("SELECT * FROM stage_param WHERE cross_id = #{crossId} AND stage_no = #{stageNo}")
    StageParamEntity findByCrossIdAndStageNo(@Param("crossId") String crossId, @Param("stageNo") Integer stageNo);

    /**
     * 查询感应阶段
     */
    @Select("SELECT * FROM stage_param WHERE cross_id = #{crossId} AND attribute = 1 ORDER BY stage_no")
    List<StageParamEntity> findInductiveStages(@Param("crossId") String crossId);

    /**
     * 根据配时方案查询阶段（方案内唯一编号）
     */
    @Select("SELECT * FROM stage_param WHERE cross_id = #{crossId} " +
            "AND stage_no BETWEEN #{planNo} * 100 + 1 AND #{planNo} * 100 + 99 ORDER BY stage_no")
    List<StageParamEntity> findByPlanNo(@Param("crossId") String crossId, @Param("planNo") Integer planNo);

    /**
     * 查询阶段及其信号组状态
     */
    @Select("SELECT sp.*, ssgs.signal_group_no, ssgs.lamp_status " +
            "FROM stage_param sp " +
            "LEFT JOIN stage_signal_group_status ssgs ON sp.cross_id = ssgs.cross_id AND sp.stage_no = ssgs.stage_no " +
            "WHERE sp.cross_id = #{crossId} ORDER BY sp.stage_no, ssgs.signal_group_no")
    @Results(id = "stageWithSignalGroupMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "stageNo", column = "stage_no"),
            @Result(property = "stageName", column = "stage_name"),
            @Result(property = "attribute", column = "attribute"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<StageParamEntity> findWithSignalGroupStatus(@Param("crossId") String crossId);
}
