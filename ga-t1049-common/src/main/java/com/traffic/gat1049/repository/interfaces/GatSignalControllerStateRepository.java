package com.traffic.gat1049.repository.interfaces;
import com.traffic.gat1049.repository.entity.GatSignalControllerStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 信号机控制器状态表Repository
 */
@Repository
public interface GatSignalControllerStateRepository extends JpaRepository<GatSignalControllerStateEntity, Long> {

    /**
     * 根据控制器ID查找最新状态
     */
    @Query("SELECT s FROM GatSignalControllerStateEntity s WHERE s.controllerId = :controllerId ORDER BY s.createdAt DESC")
    Optional<GatSignalControllerStateEntity> findLatestByControllerId(@Param("controllerId") String controllerId);

    /**
     * 根据控制器ID查找所有状态记录
     */
    List<GatSignalControllerStateEntity> findByControllerIdOrderByCreatedAtDesc(String controllerId);

    /**
     * 根据运行状态查找
     */
    List<GatSignalControllerStateEntity> findByOperationState(Integer operationState);

    /**
     * 根据控制模式查找
     */
    List<GatSignalControllerStateEntity> findByControlMode(Integer controlMode);

    /**
     * 根据当前方案ID查找
     */
    List<GatSignalControllerStateEntity> findByCurrentPlanId(String currentPlanId);

    /**
     * 根据故障代码查找
     */
    List<GatSignalControllerStateEntity> findByFaultCode(String faultCode);

    /**
     * 查找指定时间范围内的状态记录
     */
    @Query("SELECT s FROM GatSignalControllerStateEntity s WHERE s.controllerId = :controllerId AND s.createdAt BETWEEN :startTime AND :endTime ORDER BY s.createdAt DESC")
    List<GatSignalControllerStateEntity> findByControllerIdAndTimeRange(@Param("controllerId") String controllerId,
                                                                        @Param("startTime") LocalDateTime startTime,
                                                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 查找心跳超时的控制器
     */
    @Query("SELECT s FROM GatSignalControllerStateEntity s WHERE s.lastHeartbeat < :threshold")
    List<GatSignalControllerStateEntity> findByLastHeartbeatBefore(@Param("threshold") LocalDateTime threshold);

    /**
     * 统计各运行状态的控制器数量
     */
    @Query("SELECT s.operationState, COUNT(s) FROM GatSignalControllerStateEntity s GROUP BY s.operationState")
    List<Object[]> countByOperationState();

    /**
     * 删除指定控制器的历史状态记录
     */
    void deleteByControllerIdAndCreatedAtBefore(String controllerId, LocalDateTime beforeTime);
}
