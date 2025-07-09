package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.model.dto.StageSignalGroupCountDto;
import com.traffic.gat1049.repository.entity.StageSignalGroupStatusEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 阶段信号组状态表Repository
 */
@Repository
public interface StageSignalGroupStatusRepository extends BaseMapper<StageSignalGroupStatusEntity> {

    /**
     * 根据路口ID和阶段号查询信号组状态
     */
    @Select("SELECT * FROM stage_signal_group_status WHERE cross_id = #{crossId} AND stage_no = #{stageNo} ORDER BY signal_group_no")
    List<StageSignalGroupStatusEntity> findByCrossIdAndStageNo(@Param("crossId") String crossId, @Param("stageNo") Integer stageNo);

    /**
     * 根据路口ID查询所有阶段的信号组状态
     */
    @Select("SELECT * FROM stage_signal_group_status WHERE cross_id = #{crossId} ORDER BY stage_no, signal_group_no")
    List<StageSignalGroupStatusEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和信号组编号查询该信号组在各阶段的状态
     */
    @Select("SELECT * FROM stage_signal_group_status WHERE cross_id = #{crossId} AND signal_group_no = #{signalGroupNo} ORDER BY stage_no")
    List<StageSignalGroupStatusEntity> findByCrossIdAndSignalGroupNo(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);

    /**
     * 删除指定阶段的所有信号组状态
     */
    @Delete("DELETE FROM stage_signal_group_status WHERE cross_id = #{crossId} AND stage_no = #{stageNo}")
    int deleteByCrossIdAndStageNo(@Param("crossId") String crossId, @Param("stageNo") Integer stageNo);

    /**
     * 删除指定路口的所有阶段信号组状态
     */
    @Delete("DELETE FROM stage_signal_group_status WHERE cross_id = #{crossId}")
    int deleteByCrossId(@Param("crossId") String crossId);

    /**
     * 批量插入阶段信号组状态
     */
    @Insert("<script>" +
            "INSERT INTO stage_signal_group_status (cross_id, stage_no, signal_group_no, lamp_status) VALUES " +
            "<foreach collection='entities' item='entity' separator=','>" +
            "(#{entity.crossId}, #{entity.stageNo}, #{entity.signalGroupNo}, #{entity.lampStatus})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("entities") List<StageSignalGroupStatusEntity> entities);

    /**
     * 更新特定阶段信号组的灯态
     */
    @Update("UPDATE stage_signal_group_status SET lamp_status = #{lampStatus} WHERE cross_id = #{crossId} AND stage_no = #{stageNo} AND signal_group_no = #{signalGroupNo}")
    int updateLampStatus(@Param("crossId") String crossId, @Param("stageNo") Integer stageNo, @Param("signalGroupNo") Integer signalGroupNo, @Param("lampStatus") String lampStatus);

    /**
     * 查询指定灯态的阶段信号组
     */
    @Select("SELECT * FROM stage_signal_group_status WHERE cross_id = #{crossId} AND lamp_status = #{lampStatus} ORDER BY stage_no, signal_group_no")
    List<StageSignalGroupStatusEntity> findByCrossIdAndLampStatus(@Param("crossId") String crossId, @Param("lampStatus") String lampStatus);

    /**
     * 统计各阶段的信号组数量
     */
    @Select("SELECT stage_no, COUNT(*) as signal_group_count FROM stage_signal_group_status WHERE cross_id = #{crossId} GROUP BY stage_no ORDER BY stage_no")
    @Results({
            @Result(property = "stageNo", column = "stage_no"),
            @Result(property = "signalGroupCount", column = "signal_group_count")
    })
    List<StageSignalGroupCountDto> countSignalGroupsByStage(@Param("crossId") String crossId);
}
