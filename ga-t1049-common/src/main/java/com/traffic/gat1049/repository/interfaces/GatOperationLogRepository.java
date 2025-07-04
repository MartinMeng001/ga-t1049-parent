package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatOperationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 操作日志表Repository
 */
@Repository
public interface GatOperationLogRepository extends JpaRepository<GatOperationLogEntity, Long> {

    /**
     * 根据日志ID查找
     */
    Optional<GatOperationLogEntity> findByLogId(String logId);

    /**
     * 根据用户ID查找操作日志
     */
    List<GatOperationLogEntity> findByUserId(String userId);

    /**
     * 根据用户名查找操作日志
     */
    List<GatOperationLogEntity> findByUsername(String username);

    /**
     * 根据操作类型查找
     */
    List<GatOperationLogEntity> findByOperationType(String operationType);

    /**
     * 根据操作结果查找
     */
    List<GatOperationLogEntity> findByOperationResult(Integer operationResult);

    /**
     * 根据目标对象类型查找
     */
    List<GatOperationLogEntity> findByTargetType(String targetType);

    /**
     * 根据目标对象ID查找
     */
    List<GatOperationLogEntity> findByTargetId(String targetId);

    /**
     * 根据客户端IP查找
     */
    List<GatOperationLogEntity> findByClientIp(String clientIp);

    /**
     * 根据操作时间范围查找
     */
    @Query("SELECT l FROM GatOperationLogEntity l WHERE l.operationTime BETWEEN :startTime AND :endTime ORDER BY l.operationTime DESC")
    List<GatOperationLogEntity> findByOperationTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户ID和时间范围查找
     */
    @Query("SELECT l FROM GatOperationLogEntity l WHERE l.userId = :userId AND l.operationTime BETWEEN :startTime AND :endTime ORDER BY l.operationTime DESC")
    List<GatOperationLogEntity> findByUserIdAndOperationTimeBetween(@Param("userId") String userId,
                                                                    @Param("startTime") LocalDateTime startTime,
                                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 查找操作失败的日志
     */
    @Query("SELECT l FROM GatOperationLogEntity l WHERE l.operationResult = 0 ORDER BY l.operationTime DESC")
    List<GatOperationLogEntity> findFailedOperations();

    /**
     * 查找操作成功的日志
     */
    @Query("SELECT l FROM GatOperationLogEntity l WHERE l.operationResult = 1 ORDER BY l.operationTime DESC")
    List<GatOperationLogEntity> findSuccessfulOperations();

    /**
     * 根据操作耗时查找
     */
    @Query("SELECT l FROM GatOperationLogEntity l WHERE l.durationMs > :threshold ORDER BY l.durationMs DESC")
    List<GatOperationLogEntity> findByDurationMsGreaterThan(@Param("threshold") Integer threshold);

    /**
     * 统计各操作类型数量
     */
    @Query("SELECT l.operationType, COUNT(l) FROM GatOperationLogEntity l GROUP BY l.operationType")
    List<Object[]> countByOperationType();

    /**
     * 统计各操作结果数量
     */
    @Query("SELECT l.operationResult, COUNT(l) FROM GatOperationLogEntity l GROUP BY l.operationResult")
    List<Object[]> countByOperationResult();

    /**
     * 检查日志ID是否存在
     */
    boolean existsByLogId(String logId);

    /**
     * 删除指定时间之前的日志
     */
    void deleteByOperationTimeBefore(LocalDateTime beforeTime);
}
