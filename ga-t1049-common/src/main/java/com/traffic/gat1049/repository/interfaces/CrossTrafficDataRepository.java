package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.CrossTrafficData;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 路口交通数据Repository
 */
@Repository
public interface CrossTrafficDataRepository extends BaseMapper<CrossTrafficData> {

    /**
     * 查询路口交通数据（时间范围）
     */
    @Select("SELECT * FROM cross_traffic_data WHERE cross_id = #{crossId} " +
            "AND end_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY end_time, lane_no")
    List<CrossTrafficData> findByTimeRange(@Param("crossId") String crossId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最新交通数据
     */
    @Select("SELECT * FROM cross_traffic_data WHERE cross_id = #{crossId} " +
            "ORDER BY end_time DESC LIMIT #{limit}")
    List<CrossTrafficData> findLatestData(@Param("crossId") String crossId, @Param("limit") Integer limit);

    /**
     * 统计路口流量（使用存储过程）
     */
    @Select("CALL GetCrossTrafficSummary(#{crossId}, #{startTime}, #{endTime})")
    Object getCrossTrafficSummary(@Param("crossId") String crossId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 查询车道交通数据
     */
    @Select("SELECT * FROM cross_traffic_data WHERE cross_id = #{crossId} AND lane_no = #{laneNo} " +
            "AND end_time BETWEEN #{startTime} AND #{endTime} ORDER BY end_time")
    List<CrossTrafficData> findByLane(@Param("crossId") String crossId,
                                      @Param("laneNo") Integer laneNo,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 批量插入交通数据
     */
    @Insert("<script>" +
            "INSERT INTO cross_traffic_data (cross_id, lane_no, end_time, volume, occupancy, speed, headway, gap, queue_len) VALUES " +
            "<foreach collection='dataList' item='data' separator=','>" +
            "(#{data.crossId}, #{data.laneNo}, #{data.endTime}, #{data.volume}, #{data.occupancy}, #{data.speed}, #{data.headway}, #{data.gap}, #{data.queueLen})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("dataList") List<CrossTrafficData> dataList);
}
