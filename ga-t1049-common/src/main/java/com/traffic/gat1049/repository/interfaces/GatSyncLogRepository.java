package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatSyncLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 同步日志表Repository
 */
@Repository
public interface GatSyncLogRepository extends JpaRepository<GatSyncLogEntity, Long> {

    /**
     * 根据日志ID查找
     */
    Optional<GatSyncLogEntity> findByLogId(String logId);

    /**
     * 根据任务ID查找
     */
    List<GatSyncLogEntity> findByTaskId(String taskId);

    /**
     * 根据控制器ID查找
     */
    List<GatSyncLogEntity> findByControllerId(String controllerId);

    /**
     * 根据同步类型查找
     */
    List<GatSyncLogEntity> findBySyncType(Integer syncType);

    /**
     * 根据同步结果查找
     */
    List<GatSyncLogEntity> findByResult(Integer result);

    /**
     * 根据同步方向查找
     */
    List<GatSyncLogEntity> findBySyncDirection(Integer syncDirection);

    /**
     * 根据时间范围查找
     */
    @Query("SELECT l FROM GatSyncLogEntity l WHERE l.startTime BETWEEN :startTime AND :endTime ORDER BY l.startTime DESC")
    List<GatSyncLogEntity> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 查找同步失败的日志
     */
    @Query("SELECT l FROM GatSyncLogEntity l WHERE l.result = 0 ORDER BY l.startTime DESC")
    List<GatSyncLogEntity> findFailedLogs();

    /**
     * 查找同步成功的日志
     */
    @Query("SELECT l FROM GatSyncLogEntity l WHERE l.result = 1 ORDER BY l.startTime DESC")
    List<GatSyncLogEntity> findSuccessLogs();

    /**
     * 根据控制器ID和时间范围查找
     */
    @Query("SELECT l FROM GatSyncLogEntity l WHERE l.controllerId = :controllerId AND l.startTime BETWEEN :startTime AND :endTime ORDER BY l.startTime DESC")
    List<GatSyncLogEntity> findByControllerIdAndTimeRange(@Param("controllerId") String controllerId,
                                                          @Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 根据耗时查找
     */
    @Query("SELECT l FROM GatSyncLogEntity l WHERE l.durationMs > :threshold ORDER BY l.durationMs DESC")
    List<GatSyncLogEntity> findByDurationGreaterThan(@Param("threshold") Integer threshold);

    /**
     * 根据数据类型查找
     */
    List<GatSyncLogEntity> findByDataType(String dataType);

    /**
     * 统计各结果类型数量
     */
    @Query("SELECT l.result, COUNT(l) FROM GatSyncLogEntity l GROUP BY l.result")
    List<Object[]> countByResult();

    /**
     * 统计各同步类型数量
     */
    @Query("SELECT l.syncType, COUNT(l) FROM GatSyncLogEntity l GROUP BY l.syncType")
    List<Object[]> countBySyncType();

    /**
     * 计算平均同步耗时
     */
    @Query("SELECT AVG(l.durationMs) FROM GatSyncLogEntity l WHERE l.result = 1")
    Double calculateAverageDuration();

    /**
     * 根据控制器ID统计成功率
     */
    @Query("SELECT l.controllerId, " +
            "SUM(CASE WHEN l.result = 1 THEN 1 ELSE 0 END) as successCount, " +
            "COUNT(l) as totalCount " +
            "FROM GatSyncLogEntity l " +
            "GROUP BY l.controllerId")
    List<Object[]> calculateSuccessRateByController();

    /**
     * 删除指定时间之前的日志
     */
    void deleteByCreatedAtBefore(LocalDateTime beforeTime);

    /**
     * 删除成功的日志（保留指定天数）
     */
    void deleteByResultAndCreatedAtBefore(Integer result, LocalDateTime beforeTime);
}
