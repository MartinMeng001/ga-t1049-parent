package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.CtrlVarLaneLogEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 可变导向车道控制记录Repository
 * 提供可变导向车道控制记录相关的数据访问功能
 */
@Repository
public interface CtrlVarLaneLogRepository extends BaseMapper<CtrlVarLaneLogEntity> {

    /**
     * 根据路口ID查询控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和车道号查询控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND lane_no = #{laneNo} " +
            "ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findByCrossIdAndLaneNo(@Param("crossId") String crossId,
                                                      @Param("laneNo") Integer laneNo);

    /**
     * 根据状态查询控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND status = #{status} " +
            "ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findByCrossIdAndStatus(@Param("crossId") String crossId,
                                                      @Param("status") String status);

    /**
     * 查询活跃的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND status = 'ACTIVE' " +
            "ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findActiveByCrossId(@Param("crossId") String crossId);

    /**
     * 查询指定车道的当前活跃控制
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND lane_no = #{laneNo} " +
            "AND status = 'ACTIVE' AND start_time <= NOW() " +
            "AND (end_time IS NULL OR end_time > NOW()) " +
            "ORDER BY start_time DESC LIMIT 1")
    CtrlVarLaneLogEntity findCurrentActiveByCrossIdAndLaneNo(@Param("crossId") String crossId,
                                                             @Param("laneNo") Integer laneNo);

    /**
     * 查询所有当前活跃的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND status = 'ACTIVE' " +
            "AND start_time <= NOW() AND (end_time IS NULL OR end_time > NOW()) " +
            "ORDER BY lane_no, start_time DESC")
    List<CtrlVarLaneLogEntity> findAllCurrentActiveByCrossId(@Param("crossId") String crossId);

    /**
     * 根据操作员ID查询控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE operator_id = #{operatorId} ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findByOperatorId(@Param("operatorId") String operatorId);

    /**
     * 根据转向功能查询控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND movement = #{movement} " +
            "ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findByCrossIdAndMovement(@Param("crossId") String crossId,
                                                        @Param("movement") String movement);

    /**
     * 根据控制模式查询控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND ctrl_mode = #{ctrlMode} " +
            "ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findByCrossIdAndCtrlMode(@Param("crossId") String crossId,
                                                        @Param("ctrlMode") String ctrlMode);

    /**
     * 查询指定时间范围内的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "AND start_time >= #{startTime} AND start_time <= #{endTime} " +
            "ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findByCrossIdAndTimeRange(@Param("crossId") String crossId,
                                                         @Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最近的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "ORDER BY start_time DESC LIMIT #{limit}")
    List<CtrlVarLaneLogEntity> findRecentByCrossId(@Param("crossId") String crossId,
                                                   @Param("limit") Integer limit);

    /**
     * 查询指定车道的最近控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND lane_no = #{laneNo} " +
            "ORDER BY start_time DESC LIMIT 1")
    CtrlVarLaneLogEntity findLastByCrossIdAndLaneNo(@Param("crossId") String crossId,
                                                    @Param("laneNo") Integer laneNo);

    /**
     * 查询过期但状态未更新的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE status = 'ACTIVE' " +
            "AND end_time IS NOT NULL AND end_time < NOW()")
    List<CtrlVarLaneLogEntity> findExpiredActiveRecords();

    /**
     * 查询今天的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "AND DATE(start_time) = CURDATE() ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findTodayByCrossId(@Param("crossId") String crossId);

    /**
     * 查询本周的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "AND start_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findThisWeekByCrossId(@Param("crossId") String crossId);

    /**
     * 查询本月的控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "AND YEAR(start_time) = YEAR(NOW()) AND MONTH(start_time) = MONTH(NOW()) " +
            "ORDER BY start_time DESC")
    List<CtrlVarLaneLogEntity> findThisMonthByCrossId(@Param("crossId") String crossId);

    /**
     * 批量更新过期状态
     */
    @Update("UPDATE ctrl_var_lane_log SET status = 'EXPIRED' " +
            "WHERE status = 'ACTIVE' AND end_time IS NOT NULL AND end_time < NOW()")
    int updateExpiredStatus();

    /**
     * 停止指定车道的所有活跃控制
     */
    @Update("UPDATE ctrl_var_lane_log SET status = 'STOPPED', end_time = NOW() " +
            "WHERE cross_id = #{crossId} AND lane_no = #{laneNo} AND status = 'ACTIVE'")
    int stopActiveByCrossIdAndLaneNo(@Param("crossId") String crossId, @Param("laneNo") Integer laneNo);

    /**
     * 停止路口的所有活跃控制
     */
    @Update("UPDATE ctrl_var_lane_log SET status = 'STOPPED', end_time = NOW() " +
            "WHERE cross_id = #{crossId} AND status = 'ACTIVE'")
    int stopAllActiveByCrossId(@Param("crossId") String crossId);

    /**
     * 统计控制记录
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count, " +
            "SUM(CASE WHEN status = 'EXPIRED' THEN 1 ELSE 0 END) as expired_count, " +
            "SUM(CASE WHEN status = 'STOPPED' THEN 1 ELSE 0 END) as stopped_count " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId}")
    @MapKey("cross_id")
    java.util.Map<String, Object> getStatisticsByCrossId(@Param("crossId") String crossId);

    /**
     * 按车道统计控制记录
     */
    @Select("SELECT " +
            "lane_no, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count, " +
            "SUM(CASE WHEN status = 'EXPIRED' THEN 1 ELSE 0 END) as expired_count, " +
            "SUM(CASE WHEN status = 'STOPPED' THEN 1 ELSE 0 END) as stopped_count " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "GROUP BY lane_no ORDER BY lane_no")
    List<java.util.Map<String, Object>> getStatisticsByLane(@Param("crossId") String crossId);

    /**
     * 按转向功能统计控制记录
     */
    @Select("SELECT " +
            "movement, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "GROUP BY movement ORDER BY total_count DESC")
    List<java.util.Map<String, Object>> getStatisticsByMovement(@Param("crossId") String crossId);

    /**
     * 按控制模式统计控制记录
     */
    @Select("SELECT " +
            "ctrl_mode, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "GROUP BY ctrl_mode ORDER BY total_count DESC")
    List<java.util.Map<String, Object>> getStatisticsByCtrlMode(@Param("crossId") String crossId);

    /**
     * 按操作员统计控制记录
     */
    @Select("SELECT " +
            "operator_id, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "GROUP BY operator_id ORDER BY total_count DESC")
    List<java.util.Map<String, Object>> getStatisticsByOperator(@Param("crossId") String crossId);

    /**
     * 按日期统计控制记录（最近30天）
     */
    @Select("SELECT " +
            "DATE(start_time) as control_date, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "AND start_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(start_time) ORDER BY control_date DESC")
    List<java.util.Map<String, Object>> getDailyStatistics(@Param("crossId") String crossId);

    /**
     * 按小时统计控制记录（今天）
     */
    @Select("SELECT " +
            "HOUR(start_time) as control_hour, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "AND DATE(start_time) = CURDATE() " +
            "GROUP BY HOUR(start_time) ORDER BY control_hour")
    List<java.util.Map<String, Object>> getHourlyStatistics(@Param("crossId") String crossId);

    /**
     * 查询使用频率最高的车道
     */
    @Select("SELECT lane_no, COUNT(*) as count FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "GROUP BY lane_no ORDER BY count DESC LIMIT 1")
    @MapKey("lane_no")
    java.util.Map<String, Object> getMostUsedLane(@Param("crossId") String crossId);

    /**
     * 查询使用频率最高的转向功能
     */
    @Select("SELECT movement, COUNT(*) as count FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "GROUP BY movement ORDER BY count DESC LIMIT 1")
    @MapKey("movement")
    java.util.Map<String, Object> getMostUsedMovement(@Param("crossId") String crossId);

    /**
     * 查询平均控制时长
     */
    @Select("SELECT " +
            "AVG(TIMESTAMPDIFF(MINUTE, start_time, IFNULL(end_time, NOW()))) as avg_duration_minutes " +
            "FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND start_time IS NOT NULL")
    Double getAverageControlDuration(@Param("crossId") String crossId);

    /**
     * 删除指定时间之前的历史记录
     */
    @Delete("DELETE FROM ctrl_var_lane_log WHERE created_time < #{beforeTime} AND status != 'ACTIVE'")
    int deleteHistoryRecords(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 分页查询控制记录
     */
    @Select("SELECT * FROM ctrl_var_lane_log WHERE cross_id = #{crossId} " +
            "ORDER BY start_time DESC LIMIT #{offset}, #{limit}")
    List<CtrlVarLaneLogEntity> findByCrossIdWithPagination(@Param("crossId") String crossId,
                                                           @Param("offset") Integer offset,
                                                           @Param("limit") Integer limit);

    /**
     * 查询控制记录总数
     */
    @Select("SELECT COUNT(*) FROM ctrl_var_lane_log WHERE cross_id = #{crossId}")
    int countByCrossId(@Param("crossId") String crossId);

    /**
     * 查询控制记录详情及路口信息
     */
    @Select("SELECT cvl.*, c.cross_name " +
            "FROM ctrl_var_lane_log cvl " +
            "LEFT JOIN cross_param c ON cvl.cross_id = c.cross_id " +
            "WHERE cvl.id = #{id}")
    @Results(id = "ctrlVarLaneLogWithCrossMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "laneNo", column = "lane_no"),
            @Result(property = "movement", column = "movement"),
            @Result(property = "ctrlMode", column = "ctrl_mode"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "status", column = "status"),
            @Result(property = "operatorId", column = "operator_id"),
            @Result(property = "createdTime", column = "created_time")
    })
    CtrlVarLaneLogEntity findWithCrossInfo(@Param("id") Integer id);

    /**
     * 检查车道是否有冲突控制
     */
    @Select("SELECT COUNT(*) FROM ctrl_var_lane_log WHERE cross_id = #{crossId} AND lane_no = #{laneNo} " +
            "AND status = 'ACTIVE' AND start_time <= NOW() " +
            "AND (end_time IS NULL OR end_time > NOW())")
    int countActiveControlsByLane(@Param("crossId") String crossId, @Param("laneNo") Integer laneNo);
}