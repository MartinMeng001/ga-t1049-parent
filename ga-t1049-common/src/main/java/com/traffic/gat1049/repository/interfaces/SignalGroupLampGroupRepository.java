package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.SignalGroupLampGroupEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信号组灯组关联表Repository
 */
@Repository
public interface SignalGroupLampGroupRepository extends BaseMapper<SignalGroupLampGroupEntity> {

    /**
     * 根据路口ID和信号组编号查询关联的灯组
     */
    @Select("SELECT * FROM signal_group_lamp_group WHERE cross_id = #{crossId} AND signal_group_no = #{signalGroupNo} ORDER BY lamp_group_no")
    List<SignalGroupLampGroupEntity> findByCrossIdAndSignalGroupNo(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);

    /**
     * 根据路口ID和灯组编号查询关联的信号组
     */
    @Select("SELECT * FROM signal_group_lamp_group WHERE cross_id = #{crossId} AND lamp_group_no = #{lampGroupNo} ORDER BY signal_group_no")
    List<SignalGroupLampGroupEntity> findByCrossIdAndLampGroupNo(@Param("crossId") String crossId, @Param("lampGroupNo") Integer lampGroupNo);

    /**
     * 删除指定信号组的所有关联关系
     */
    @Delete("DELETE FROM signal_group_lamp_group WHERE cross_id = #{crossId} AND signal_group_no = #{signalGroupNo}")
    int deleteByCrossIdAndSignalGroupNo(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);

    /**
     * 删除指定灯组的所有关联关系
     */
    @Delete("DELETE FROM signal_group_lamp_group WHERE cross_id = #{crossId} AND lamp_group_no = #{lampGroupNo}")
    int deleteByCrossIdAndLampGroupNo(@Param("crossId") String crossId, @Param("lampGroupNo") Integer lampGroupNo);

    /**
     * 批量插入关联关系
     */
    @Insert("<script>" +
            "INSERT INTO signal_group_lamp_group (cross_id, signal_group_no, lamp_group_no) VALUES " +
            "<foreach collection='entities' item='entity' separator=','>" +
            "(#{entity.crossId}, #{entity.signalGroupNo}, #{entity.lampGroupNo})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("entities") List<SignalGroupLampGroupEntity> entities);

    /**
     * 检查关联关系是否存在
     */
    @Select("SELECT COUNT(*) FROM signal_group_lamp_group WHERE cross_id = #{crossId} AND signal_group_no = #{signalGroupNo} AND lamp_group_no = #{lampGroupNo}")
    int existsAssociation(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo, @Param("lampGroupNo") Integer lampGroupNo);
}
