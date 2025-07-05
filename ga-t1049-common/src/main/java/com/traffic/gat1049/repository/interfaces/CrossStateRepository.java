package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.CrossState;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 路口状态Repository
 */
@Repository
public interface CrossStateRepository extends BaseMapper<CrossState> {

    /**
     * 获取路口最新状态
     */
    @Select("SELECT * FROM cross_state WHERE cross_id = #{crossId} ORDER BY created_time DESC LIMIT 1")
    CrossState findLatestByCrossId(@Param("crossId") String crossId);

    /**
     * 查询所有在线路口
     */
    @Select("SELECT DISTINCT cs1.* FROM cross_state cs1 " +
            "WHERE cs1.value = 'Online' " +
            "AND cs1.created_time = (SELECT MAX(cs2.created_time) FROM cross_state cs2 WHERE cs2.cross_id = cs1.cross_id)")
    List<CrossState> findAllOnline();

    /**
     * 查询离线路口
     */
    @Select("SELECT DISTINCT cs1.* FROM cross_state cs1 " +
            "WHERE cs1.value IN ('Offline', 'Error') " +
            "AND cs1.created_time = (SELECT MAX(cs2.created_time) FROM cross_state cs2 WHERE cs2.cross_id = cs1.cross_id)")
    List<CrossState> findAllOffline();

    /**
     * 根据区域查询路口状态
     */
    @Select("SELECT cs.* FROM cross_state cs " +
            "JOIN region_cross rc ON cs.cross_id = rc.cross_id " +
            "WHERE rc.region_id = #{regionId} " +
            "AND cs.created_time = (SELECT MAX(cs2.created_time) FROM cross_state cs2 WHERE cs2.cross_id = cs.cross_id) " +
            "ORDER BY cs.cross_id")
    List<CrossState> findLatestByRegionId(@Param("regionId") String regionId);

    /**
     * 统计各状态路口数量
     */
    @Select("SELECT value, COUNT(*) as count FROM (" +
            "  SELECT DISTINCT cs1.cross_id, cs1.value " +
            "  FROM cross_state cs1 " +
            "  WHERE cs1.created_time = (SELECT MAX(cs2.created_time) FROM cross_state cs2 WHERE cs2.cross_id = cs1.cross_id)" +
            ") t GROUP BY value")
    List<Object> getStatusStatistics();
}
