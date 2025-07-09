package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.CenterPlanEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 中心预案Repository
 * 提供中心预案相关的数据访问功能
 */
@Repository
public interface CenterPlanRepository extends BaseMapper<CenterPlanEntity> {

    /**
     * 根据路口ID查询所有预案
     */
    @Select("SELECT * FROM center_plan WHERE cross_id = #{crossId} ORDER BY created_time DESC")
    List<CenterPlanEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID查询活跃的预案
     */
    @Select("SELECT * FROM center_plan WHERE cross_id = #{crossId} AND status = 'ACTIVE' ORDER BY start_time DESC")
    List<CenterPlanEntity> findActiveByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID查询当前活跃的预案
     */
    @Select("SELECT * FROM center_plan WHERE cross_id = #{crossId} AND status = 'ACTIVE' " +
            "AND start_time <= NOW() ORDER BY start_time DESC LIMIT 1")
    CenterPlanEntity findCurrentActiveByCrossId(@Param("crossId") String crossId);

    /**
     * 根据状态查询预案
     */
    @Select("SELECT * FROM center_plan WHERE status = #{status} ORDER BY created_time DESC")
    List<CenterPlanEntity> findByStatus(@Param("status") String status);

    /**
     * 查询指定时间范围内的预案
     */
    @Select("SELECT * FROM center_plan WHERE cross_id = #{crossId} " +
            "AND start_time >= #{startTime} AND start_time <= #{endTime} " +
            "ORDER BY start_time DESC")
    List<CenterPlanEntity> findByCrossIdAndTimeRange(@Param("crossId") String crossId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 查询超时的活跃预案
     */
    @Select("SELECT * FROM center_plan WHERE status = 'ACTIVE' " +
            "AND start_time IS NOT NULL " +
            "AND DATE_ADD(start_time, INTERVAL max_run_time MINUTE) < NOW()")
    List<CenterPlanEntity> findOvertimeActivePlans();

    /**
     * 查询即将超时的活跃预案（未来5分钟内）
     */
    @Select("SELECT * FROM center_plan WHERE status = 'ACTIVE' " +
            "AND start_time IS NOT NULL " +
            "AND DATE_ADD(start_time, INTERVAL max_run_time MINUTE) BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 5 MINUTE)")
    List<CenterPlanEntity> findSoonToExpirePlans();

    /**
     * 根据控制方式查询预案
     */
    @Select("SELECT * FROM center_plan WHERE cross_id = #{crossId} AND control_mode = #{controlMode} " +
            "ORDER BY created_time DESC")
    List<CenterPlanEntity> findByCrossIdAndControlMode(@Param("crossId") String crossId,
                                                       @Param("controlMode") String controlMode);

    /**
     * 批量更新预案状态
     */
    @Update("UPDATE center_plan SET status = #{newStatus}, end_time = NOW() " +
            "WHERE cross_id = #{crossId} AND status = #{oldStatus}")
    int updateStatusByCrossId(@Param("crossId") String crossId,
                              @Param("oldStatus") String oldStatus,
                              @Param("newStatus") String newStatus);

    /**
     * 停止路口的所有活跃预案
     */
    @Update("UPDATE center_plan SET status = 'STOPPED', end_time = NOW() " +
            "WHERE cross_id = #{crossId} AND status = 'ACTIVE'")
    int stopAllActivePlansByCrossId(@Param("crossId") String crossId);

    /**
     * 将超时的活跃预案设置为过期
     */
    @Update("UPDATE center_plan SET status = 'EXPIRED', end_time = NOW() " +
            "WHERE status = 'ACTIVE' AND start_time IS NOT NULL " +
            "AND DATE_ADD(start_time, INTERVAL max_run_time MINUTE) < NOW()")
    int expireOvertimePlans();

    /**
     * 删除指定时间之前的历史预案
     */
    @Delete("DELETE FROM center_plan WHERE created_time < #{beforeTime} AND status != 'ACTIVE'")
    int deleteHistoryPlans(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查询预案统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count, " +
            "SUM(CASE WHEN status = 'EXPIRED' THEN 1 ELSE 0 END) as expired_count, " +
            "SUM(CASE WHEN status = 'STOPPED' THEN 1 ELSE 0 END) as stopped_count " +
            "FROM center_plan WHERE cross_id = #{crossId}")
    @MapKey("cross_id")
    java.util.Map<String, Object> getStatisticsByCrossId(@Param("crossId") String crossId);

    /**
     * 查询所有路口的预案统计信息
     */
    @Select("SELECT " +
            "cross_id, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_count, " +
            "SUM(CASE WHEN status = 'EXPIRED' THEN 1 ELSE 0 END) as expired_count, " +
            "SUM(CASE WHEN status = 'STOPPED' THEN 1 ELSE 0 END) as stopped_count " +
            "FROM center_plan " +
            "GROUP BY cross_id")
    List<java.util.Map<String, Object>> getAllCrossStatistics();

    /**
     * 查询预案详情及其关联信息
     */
    @Select("SELECT cp.*, c.cross_name " +
            "FROM center_plan cp " +
            "LEFT JOIN cross_param c ON cp.cross_id = c.cross_id " +
            "WHERE cp.id = #{id}")
    @Results(id = "centerPlanWithCrossMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "controlMode", column = "control_mode"),
            @Result(property = "maxRunTime", column = "max_run_time"),
            @Result(property = "planData", column = "plan_data"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdTime", column = "created_time")
    })
    CenterPlanEntity findWithCrossInfo(@Param("id") Integer id);

    /**
     * 分页查询预案列表
     */
    @Select("SELECT * FROM center_plan " +
            "WHERE cross_id = #{crossId} " +
            "ORDER BY created_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<CenterPlanEntity> findByCrossIdWithPagination(@Param("crossId") String crossId,
                                                       @Param("offset") Integer offset,
                                                       @Param("limit") Integer limit);

    /**
     * 查询预案总数
     */
    @Select("SELECT COUNT(*) FROM center_plan WHERE cross_id = #{crossId}")
    int countByCrossId(@Param("crossId") String crossId);

    /**
     * 根据预案数据内容搜索
     */
    @Select("SELECT * FROM center_plan WHERE cross_id = #{crossId} " +
            "AND JSON_EXTRACT(plan_data, '$.planNo') = #{planNo}")
    List<CenterPlanEntity> findByPlanNo(@Param("crossId") String crossId, @Param("planNo") Integer planNo);

    /**
     * 查询最近创建的预案
     */
    @Select("SELECT * FROM center_plan WHERE cross_id = #{crossId} " +
            "ORDER BY created_time DESC LIMIT #{limit}")
    List<CenterPlanEntity> findRecentByCrossId(@Param("crossId") String crossId, @Param("limit") Integer limit);
}
