package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.SysState;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统状态Repository
 */
@Repository
public interface SysStateRepository extends BaseMapper<SysState> {

    /**
     * 获取系统最新状态
     */
    @Select("SELECT * FROM sys_state WHERE system_id = #{systemId} ORDER BY time DESC LIMIT 1")
    SysState findLatestBySystemId(@Param("systemId") String systemId);

    /**
     * 插入系统状态（使用存储过程）
     */
    @Select("CALL UpsertSystemState(#{systemId}, #{value}, #{time})")
    void upsertSystemState(@Param("systemId") String systemId,
                           @Param("value") String value,
                           @Param("time") LocalDateTime time);

    /**
     * 查询时间范围内的状态变化
     */
    @Select("SELECT * FROM sys_state WHERE system_id = #{systemId} " +
            "AND time BETWEEN #{startTime} AND #{endTime} ORDER BY time")
    List<SysState> findByTimeRange(@Param("systemId") String systemId,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 统计系统在线时长
     */
    @Select("SELECT COUNT(*) as online_count, " +
            "       SUM(CASE WHEN value = 'Online' THEN 1 ELSE 0 END) as online_time " +
            "FROM sys_state WHERE system_id = #{systemId} " +
            "AND time BETWEEN #{startTime} AND #{endTime}")
    Object getSystemUptime(@Param("systemId") String systemId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
}
