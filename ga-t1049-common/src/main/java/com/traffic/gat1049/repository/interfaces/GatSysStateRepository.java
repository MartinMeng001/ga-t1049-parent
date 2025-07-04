package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatSysStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 系统状态表Repository
 */
@Repository
public interface GatSysStateRepository extends JpaRepository<GatSysStateEntity, Long> {

    /**
     * 根据系统ID查找最新状态
     */
    @Query("SELECT s FROM GatSysStateEntity s WHERE s.systemId = :systemId ORDER BY s.createdAt DESC")
    Optional<GatSysStateEntity> findLatestBySystemId(@Param("systemId") String systemId);

    /**
     * 根据系统ID查找所有状态记录
     */
    List<GatSysStateEntity> findBySystemIdOrderByCreatedAtDesc(String systemId);

    /**
     * 根据运行状态查找
     */
    List<GatSysStateEntity> findByRunningState(Integer runningState);

    /**
     * 根据工作模式查找
     */
    List<GatSysStateEntity> findByWorkMode(Integer workMode);

    /**
     * 根据网络状态查找
     */
    List<GatSysStateEntity> findByNetworkStatus(Integer networkStatus);

    /**
     * 查找指定时间范围内的状态记录
     */
    @Query("SELECT s FROM GatSysStateEntity s WHERE s.systemId = :systemId AND s.currentTime BETWEEN :startTime AND :endTime ORDER BY s.currentTime DESC")
    List<GatSysStateEntity> findBySystemIdAndTimeRange(@Param("systemId") String systemId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查找CPU使用率超过阈值的记录
     */
    @Query("SELECT s FROM GatSysStateEntity s WHERE s.cpuUsage > :threshold ORDER BY s.cpuUsage DESC")
    List<GatSysStateEntity> findByCpuUsageGreaterThan(@Param("threshold") java.math.BigDecimal threshold);

    /**
     * 查找内存使用率超过阈值的记录
     */
    @Query("SELECT s FROM GatSysStateEntity s WHERE s.memoryUsage > :threshold ORDER BY s.memoryUsage DESC")
    List<GatSysStateEntity> findByMemoryUsageGreaterThan(@Param("threshold") java.math.BigDecimal threshold);

    /**
     * 查找错误计数超过阈值的记录
     */
    List<GatSysStateEntity> findByErrorCountGreaterThan(Integer errorCount);

    /**
     * 删除指定系统的历史状态记录
     */
    void deleteBySystemIdAndCreatedAtBefore(String systemId, LocalDateTime beforeTime);
}
