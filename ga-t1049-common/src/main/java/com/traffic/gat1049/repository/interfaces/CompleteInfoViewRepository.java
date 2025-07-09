package com.traffic.gat1049.repository.interfaces;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 完整信息视图Repository
 * 专门用于查询各种完整信息视图
 */
@Repository
public interface CompleteInfoViewRepository {

    /**
     * 查询系统完整信息视图
     */
    @Select("SELECT * FROM v_protocol_sys_info_complete WHERE system_id = #{systemId}")
    Map<String, Object> findSystemCompleteInfo(@Param("systemId") String systemId);

    /**
     * 查询所有系统完整信息
     */
    @Select("SELECT * FROM v_protocol_sys_info_complete ORDER BY system_id")
    List<Map<String, Object>> findAllSystemCompleteInfo();

    /**
     * 查询区域完整信息视图
     */
    @Select("SELECT * FROM v_region_param_complete WHERE region_id = #{regionId}")
    Map<String, Object> findRegionCompleteInfo(@Param("regionId") String regionId);

    /**
     * 查询子区完整信息视图
     */
    @Select("SELECT * FROM v_sub_region_param_complete WHERE sub_region_id = #{subRegionId}")
    Map<String, Object> findSubRegionCompleteInfo(@Param("subRegionId") String subRegionId);

    /**
     * 查询线路完整信息视图
     */
    @Select("SELECT * FROM v_route_param_complete WHERE route_id = #{routeId}")
    Map<String, Object> findRouteCompleteInfo(@Param("routeId") String routeId);

    /**
     * 查询路口完整信息视图
     */
    @Select("SELECT * FROM v_cross_param_complete WHERE cross_id = #{crossId}")
    Map<String, Object> findCrossCompleteInfo(@Param("crossId") String crossId);

    /**
     * 查询信号机完整信息视图
     */
    @Select("SELECT * FROM v_signal_controller_complete WHERE signal_controller_id = #{signalControllerId}")
    Map<String, Object> findSignalControllerCompleteInfo(@Param("signalControllerId") String signalControllerId);

    /**
     * 查询系统概览视图
     */
    @Select("SELECT * FROM v_system_overview ORDER BY system_id")
    List<Map<String, Object>> findSystemOverview();

    /**
     * 查询指定系统概览
     */
    @Select("SELECT * FROM v_system_overview WHERE system_id = #{systemId}")
    Map<String, Object> findSystemOverviewById(@Param("systemId") String systemId);

    /**
     * 查询路口系统映射视图
     */
    @Select("SELECT * FROM v_cross_system_mapping ORDER BY cross_id")
    List<Map<String, Object>> findCrossSystemMapping();

    /**
     * 查询指定路口系统映射
     */
    @Select("SELECT * FROM v_cross_system_mapping WHERE cross_id = #{crossId}")
    Map<String, Object> findCrossSystemMappingById(@Param("crossId") String crossId);

    /**
     * 按系统查询路口映射
     */
    @Select("SELECT * FROM v_cross_system_mapping WHERE primary_system_id = #{systemId} OR FIND_IN_SET(#{systemId}, all_system_ids) ORDER BY cross_id")
    List<Map<String, Object>> findCrossSystemMappingBySystemId(@Param("systemId") String systemId);

    /**
     * 查询在线路口统计
     */
    @Select("SELECT " +
            "  COUNT(*) as total_crosses, " +
            "  SUM(CASE WHEN cs.value = 'Online' THEN 1 ELSE 0 END) as online_crosses, " +
            "  SUM(CASE WHEN cs.value = 'Offline' THEN 1 ELSE 0 END) as offline_crosses, " +
            "  SUM(CASE WHEN cs.value = 'Error' THEN 1 ELSE 0 END) as error_crosses " +
            "FROM v_cross_param_complete vpc " +
            "LEFT JOIN (SELECT cross_id, value FROM cross_state cs1 WHERE cs1.created_time = (SELECT MAX(cs2.created_time) FROM cross_state cs2 WHERE cs2.cross_id = cs1.cross_id)) cs ON vpc.cross_id = cs.cross_id")
    Map<String, Object> getCrossStatusStatistics();

    /**
     * 按区域查询路口状态统计
     */
    @Select("SELECT " +
            "  vpc.region_id, " +
            "  COUNT(*) as total_crosses, " +
            "  SUM(CASE WHEN cs.value = 'Online' THEN 1 ELSE 0 END) as online_crosses, " +
            "  SUM(CASE WHEN cs.value = 'Offline' THEN 1 ELSE 0 END) as offline_crosses, " +
            "  SUM(CASE WHEN cs.value = 'Error' THEN 1 ELSE 0 END) as error_crosses " +
            "FROM v_cross_param_complete vpc " +
            "LEFT JOIN (SELECT cross_id, value FROM cross_state cs1 WHERE cs1.created_time = (SELECT MAX(cs2.created_time) FROM cross_state cs2 WHERE cs2.cross_id = cs1.cross_id)) cs ON vpc.cross_id = cs.cross_id " +
            "WHERE vpc.region_id = #{regionId} " +
            "GROUP BY vpc.region_id")
    Map<String, Object> getCrossStatusStatisticsByRegion(@Param("regionId") String regionId);

    // ==================== 路口相关视图方法 ====================

    /**
     * 查询所有路口概览信息（使用现有视图或简化查询）
     */
    @Select("SELECT * FROM v_cross_param_complete ORDER BY cross_id")
    List<Map<String, Object>> findAllCrossOverview();

    /**
     * 查询指定路口概览信息（使用现有视图）
     */
    @Select("SELECT * FROM v_cross_param_complete WHERE cross_id = #{crossId}")
    Map<String, Object> findCrossOverviewById(@Param("crossId") String crossId);

    /**
     * 根据条件查询路口概览信息
     * 支持的条件：cross_state, signal_controller_id 等
     */
    @Select("<script>" +
            "SELECT " +
            "cp.cross_id, cp.cross_name, cp.longitude, cp.latitude, " +
            "cs.value as cross_state, cs.created_time as last_update_time, " +
            "sgc.signal_controller_id, " +
            "COUNT(DISTINCT lp.lane_no) as lane_count, " +
            "COUNT(DISTINCT lgp.lamp_group_no) as signal_group_count " +
            "FROM cross_param cp " +
            "LEFT JOIN cross_state cs ON cp.cross_id = cs.cross_id " +
            "LEFT JOIN signal_controller_cross sgc ON cp.cross_id = sgc.cross_id AND sgc.is_main = 1 " +
            "LEFT JOIN lane_param lp ON cp.cross_id = lp.cross_id " +
            "LEFT JOIN lamp_group_param lgp ON cp.cross_id = lgp.cross_id " +
            "<where>" +
            "<if test='cross_state != null and cross_state != \"\"'>" +
            "AND cs.value = #{cross_state}" +
            "</if>" +
            "<if test='signal_controller_id != null and signal_controller_id != \"\"'>" +
            "AND sgc.signal_controller_id = #{signal_controller_id}" +
            "</if>" +
            "<if test='cross_name != null and cross_name != \"\"'>" +
            "AND cp.cross_name LIKE CONCAT('%', #{cross_name}, '%')" +
            "</if>" +
            "</where>" +
            "GROUP BY cp.cross_id, cs.value, cs.created_time, sgc.signal_controller_id " +
            "ORDER BY cp.cross_id" +
            "</script>")
    List<Map<String, Object>> findCrossOverviewByCondition(Map<String, Object> conditions);

    // ==================== 统计查询方法 ====================

    /**
     * 查询在线路口数量
     */
    @Select("SELECT COUNT(DISTINCT cp.cross_id) " +
            "FROM cross_param cp " +
            "LEFT JOIN cross_state cs ON cp.cross_id = cs.cross_id " +
            "WHERE cs.value = 'Online'")
    Integer countOnlineCrosses();

    /**
     * 查询离线路口数量
     */
    @Select("SELECT COUNT(DISTINCT cp.cross_id) " +
            "FROM cross_param cp " +
            "LEFT JOIN cross_state cs ON cp.cross_id = cs.cross_id " +
            "WHERE cs.value = 'Offline' OR cs.value IS NULL")
    Integer countOfflineCrosses();

    /**
     * 查询路口统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total_crosses, " +
            "COUNT(CASE WHEN cs.value = 'Online' THEN 1 END) as online_crosses, " +
            "COUNT(CASE WHEN cs.value = 'Offline' THEN 1 END) as offline_crosses, " +
            "COUNT(CASE WHEN cs.value = 'Error' THEN 1 END) as error_crosses, " +
            "AVG(lane_counts.lane_count) as avg_lane_count, " +
            "AVG(signal_counts.signal_group_count) as avg_signal_group_count " +
            "FROM cross_param cp " +
            "LEFT JOIN cross_state cs ON cp.cross_id = cs.cross_id " +
            "LEFT JOIN (" +
            "  SELECT cross_id, COUNT(*) as lane_count " +
            "  FROM lane_param GROUP BY cross_id" +
            ") lane_counts ON cp.cross_id = lane_counts.cross_id " +
            "LEFT JOIN (" +
            "  SELECT cross_id, COUNT(*) as signal_group_count " +
            "  FROM lamp_group_param GROUP BY cross_id" +
            ") signal_counts ON cp.cross_id = signal_counts.cross_id")
    Map<String, Object> getCrossStatistics();
}
