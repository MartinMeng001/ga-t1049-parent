package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.CrossSignalGroupStatusEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 路口信号组状态Repository
 */
@Repository
public interface CrossSignalGroupStatusRepository extends BaseMapper<CrossSignalGroupStatusEntity> {

    /**
     * 查询路口当前信号状态
     */
    @Select("SELECT * FROM cross_signal_group_status WHERE cross_id = #{crossId} " +
            "ORDER BY lamp_status_time DESC, signal_group_no LIMIT #{signalGroupCount}")
    List<CrossSignalGroupStatusEntity> findCurrentStatus(@Param("crossId") String crossId, @Param("signalGroupCount") Integer signalGroupCount);

    /**
     * 查询信号组状态历史
     */
    @Select("SELECT * FROM cross_signal_group_status WHERE cross_id = #{crossId} " +
            "AND signal_group_no = #{signalGroupNo} " +
            "AND lamp_status_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY lamp_status_time")
    List<CrossSignalGroupStatusEntity> findSignalGroupHistory(@Param("crossId") String crossId,
                                                              @Param("signalGroupNo") Integer signalGroupNo,
                                                              @Param("startTime") LocalDateTime startTime,
                                                              @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最新信号状态
     */
    @Select("SELECT * FROM cross_signal_group_status WHERE cross_id = #{crossId} " +
            "AND lamp_status_time = (SELECT MAX(lamp_status_time) FROM cross_signal_group_status WHERE cross_id = #{crossId}) " +
            "ORDER BY signal_group_no")
    List<CrossSignalGroupStatusEntity> findLatestStatusByCrossId(@Param("crossId") String crossId);

    /**
     * 统计信号状态持续时间
     */
    @Select("SELECT signal_group_no, lamp_status, " +
            "       AVG(TIMESTAMPDIFF(SECOND, LAG(lamp_status_time) OVER (PARTITION BY signal_group_no ORDER BY lamp_status_time), lamp_status_time)) as avg_duration " +
            "FROM cross_signal_group_status WHERE cross_id = #{crossId} " +
            "AND lamp_status_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY signal_group_no, lamp_status")
    List<Object> getSignalDurationStats(@Param("crossId") String crossId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 批量插入信号状态
     */
    @Insert("<script>" +
            "INSERT INTO cross_signal_group_status (cross_id, signal_group_no, lamp_status, lamp_status_time) VALUES " +
            "<foreach collection='statusList' item='status' separator=','>" +
            "(#{status.crossId}, #{status.signalGroupNo}, #{status.lampStatus}, #{status.lampStatusTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("statusList") List<CrossSignalGroupStatusEntity> statusList);
}