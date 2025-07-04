package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatSyncTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 同步任务表Repository
 */
@Repository
public interface GatSyncTaskRepository extends JpaRepository<GatSyncTaskEntity, Long> {

    /**
     * 根据任务ID查找
     */
    Optional<GatSyncTaskEntity> findByTaskId(String taskId);

    /**
     * 根据控制器ID查找
     */
    List<GatSyncTaskEntity> findByControllerId(String controllerId);

    /**
     * 根据任务类型查找
     */
    List<GatSyncTaskEntity> findByTaskType(Integer taskType);

    /**
     * 根据任务状态查找
     */
    List<GatSyncTaskEntity> findByStatus(Integer status);

    /**
     * 根据同步方向查找
     */
    List<GatSyncTaskEntity> findBySyncDirection(Integer syncDirection);

    /**
     * 查找待执行的任务
     */
    @Query("SELECT t FROM GatSyncTaskEntity t WHERE t.status = 0 AND t.nextExecuteTime <= :now ORDER BY t.priorityLevel DESC, t.nextExecuteTime ASC")
    List<GatSyncTaskEntity> findPendingTasks(@Param("now") LocalDateTime now);

    /**
     * 查找正在执行的任务
     */
    @Query("SELECT t FROM GatSyncTaskEntity t WHERE t.status = 1")
    List<GatSyncTaskEntity> findRunningTasks();

    /**
     * 查找失败的任务
     */
    @Query("SELECT t FROM GatSyncTaskEntity t WHERE t.status = 3")
    List<GatSyncTaskEntity> findFailedTasks();

    /**
     * 根据优先级查找任务
     */
    List<GatSyncTaskEntity> findByPriorityLevelGreaterThanEqualOrderByPriorityLevelDesc(Integer priorityLevel);

    /**
     * 根据创建人查找任务
     */
    List<GatSyncTaskEntity> findByCreatedBy(String createdBy);

    /**
     * 根据数据类型查找任务
     */
    List<GatSyncTaskEntity> findByDataType(String dataType);

    /**
     * 查找需要重试的任务
     */
    @Query("SELECT t FROM GatSyncTaskEntity t WHERE t.status = 3 AND t.maxRetryCount > 0")
    List<GatSyncTaskEntity> findRetryableTasks();

    /**
     * 根据控制器ID和任务类型查找
     */
    List<GatSyncTaskEntity> findByControllerIdAndTaskType(String controllerId, Integer taskType);

    /**
     * 统计各状态任务数量
     */
    @Query("SELECT t.status, COUNT(t) FROM GatSyncTaskEntity t GROUP BY t.status")
    List<Object[]> countByStatus();

    /**
     * 统计各类型任务数量
     */
    @Query("SELECT t.taskType, COUNT(t) FROM GatSyncTaskEntity t GROUP BY t.taskType")
    List<Object[]> countByTaskType();

    /**
     * 检查任务ID是否存在
     */
    boolean existsByTaskId(String taskId);

    /**
     * 删除过期的已完成任务
     */
    void deleteByStatusAndLastExecuteTimeBefore(Integer status, LocalDateTime beforeTime);
}
