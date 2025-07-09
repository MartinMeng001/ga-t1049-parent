package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.StageTrafficDataEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 阶段交通数据Repository
 * 根据gat1049.sql中的实际表结构创建
 */
@Repository
public interface StageTrafficDataRepository extends BaseMapper<StageTrafficDataEntity> {

    /**
     * 查询指定阶段的交通数据（时间范围）
     */
    @Select("SELECT * FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND stage_no = #{stageNo} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "ORDER BY start_time, lane_no")
    List<StageTrafficDataEntity> findByStage(@Param("crossId") String crossId,
                                             @Param("stageNo") Integer stageNo,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 查询路口所有阶段交通数据
     */
    @Select("SELECT * FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "ORDER BY start_time DESC, stage_no, lane_no")
    List<StageTrafficDataEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 查询最新阶段交通数据
     */
    @Select("SELECT * FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "ORDER BY start_time DESC LIMIT #{limit}")
    List<StageTrafficDataEntity> findLatestData(@Param("crossId") String crossId, @Param("limit") Integer limit);

    /**
     * 查询车道阶段交通数据
     */
    @Select("SELECT * FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND stage_no = #{stageNo} AND lane_no = #{laneNo} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "ORDER BY start_time")
    List<StageTrafficDataEntity> findByLane(@Param("crossId") String crossId,
                                            @Param("stageNo") Integer stageNo,
                                            @Param("laneNo") Integer laneNo,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计阶段交通数据（使用自定义查询）
     */
    @Select("SELECT " +
            "  stage_no, " +
            "  SUM(vehicle_num) as total_vehicles, " +
            "  SUM(pcu) as total_pcu, " +
            "  AVG(head_time) as avg_head_time, " +
            "  AVG(saturation) as avg_saturation, " +
            "  AVG(queue_length) as avg_queue_length, " +
            "  AVG(occupancy) as avg_occupancy, " +
            "  COUNT(*) as record_count " +
            "FROM stage_traffic_data " +
            "WHERE cross_id = #{crossId} AND stage_no = #{stageNo} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "GROUP BY stage_no")
    Map<String, Object> getStageTrafficSummary(@Param("crossId") String crossId,
                                               @Param("stageNo") Integer stageNo,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定时间范围内的阶段交通数据
     */
    @Select("SELECT * FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "ORDER BY start_time, stage_no, lane_no")
    List<StageTrafficDataEntity> findByTimeRange(@Param("crossId") String crossId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 按阶段查询交通数据（不限时间）
     */
    @Select("SELECT * FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND stage_no = #{stageNo} ORDER BY start_time DESC")
    List<StageTrafficDataEntity> findByStageNo(@Param("crossId") String crossId,
                                               @Param("stageNo") Integer stageNo);

    /**
     * 批量插入阶段交通数据
     */
    @Insert("<script>" +
            "INSERT INTO stage_traffic_data (cross_id, start_time, end_time, stage_no, lane_no, vehicle_num, pcu, head_time, saturation, queue_length, occupancy) VALUES " +
            "<foreach collection='dataList' item='data' separator=','>" +
            "(#{data.crossId}, #{data.startTime}, #{data.endTime}, #{data.stageNo}, #{data.laneNo}, #{data.vehicleNum}, #{data.pcu}, #{data.headTime}, #{data.saturation}, #{data.queueLength}, #{data.occupancy})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("dataList") List<StageTrafficDataEntity> dataList);

    /**
     * 删除过期数据
     */
    @Delete("DELETE FROM stage_traffic_data WHERE created_time < #{expireTime}")
    int deleteExpiredData(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 按时间段统计各阶段交通量
     */
    @Select("SELECT stage_no, " +
            "       SUM(vehicle_num) as total_vehicles, " +
            "       SUM(pcu) as total_pcu, " +
            "       AVG(head_time) as avg_head_time, " +
            "       AVG(saturation) as avg_saturation, " +
            "       AVG(queue_length) as avg_queue_length, " +
            "       AVG(occupancy) as avg_occupancy, " +
            "       COUNT(*) as record_count, " +
            "       COUNT(DISTINCT lane_no) as lane_count " +
            "FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "GROUP BY stage_no ORDER BY stage_no")
    List<Map<String, Object>> getStageStatsByTimeRange(@Param("crossId") String crossId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查询车道交通统计
     */
    @Select("SELECT lane_no, " +
            "       SUM(vehicle_num) as total_vehicles, " +
            "       SUM(pcu) as total_pcu, " +
            "       AVG(head_time) as avg_head_time, " +
            "       AVG(saturation) as avg_saturation, " +
            "       AVG(queue_length) as avg_queue_length, " +
            "       AVG(occupancy) as avg_occupancy, " +
            "       COUNT(*) as record_count " +
            "FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND stage_no = #{stageNo} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "GROUP BY lane_no ORDER BY lane_no")
    List<Map<String, Object>> getLaneStatsByStage(@Param("crossId") String crossId,
                                                  @Param("stageNo") Integer stageNo,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最繁忙的阶段
     */
    @Select("SELECT stage_no, SUM(vehicle_num) as total_vehicles " +
            "FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "GROUP BY stage_no ORDER BY total_vehicles DESC LIMIT 1")
    Map<String, Object> findBusiestStage(@Param("crossId") String crossId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 查询平均阶段时长
     */
    @Select("SELECT stage_no, " +
            "       AVG(TIMESTAMPDIFF(SECOND, start_time, end_time)) as avg_duration_seconds " +
            "FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "GROUP BY stage_no ORDER BY stage_no")
    List<Map<String, Object>> getAverageStageDuration(@Param("crossId") String crossId,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 查询阶段效率统计（通过率、饱和度等）
     */
    @Select("SELECT stage_no, " +
            "       AVG(saturation) as avg_saturation, " +
            "       AVG(occupancy) as avg_occupancy, " +
            "       AVG(queue_length) as avg_queue_length, " +
            "       SUM(vehicle_num) / COUNT(*) as avg_vehicles_per_cycle " +
            "FROM stage_traffic_data WHERE cross_id = #{crossId} " +
            "AND start_time >= #{startTime} AND end_time <= #{endTime} " +
            "GROUP BY stage_no ORDER BY stage_no")
    List<Map<String, Object>> getStageEfficiencyStats(@Param("crossId") String crossId,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);
}
