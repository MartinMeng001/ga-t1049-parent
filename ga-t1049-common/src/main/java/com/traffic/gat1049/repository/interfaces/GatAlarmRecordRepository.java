package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatAlarmRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 告警记录表Repository
 */
@Repository
public interface GatAlarmRecordRepository extends JpaRepository<GatAlarmRecordEntity, Long> {

    /**
     * 根据告警ID查找
     */
    Optional<GatAlarmRecordEntity> findByAlarmId(String alarmId);

    /**
     * 根据规则ID查找告警记录
     */
    List<GatAlarmRecordEntity> findByRuleId(String ruleId);

    /**
     * 根据控制器ID查找告警记录
     */
    List<GatAlarmRecordEntity> findByControllerId(String controllerId);

    /**
     * 根据告警类型查找
     */
    List<GatAlarmRecordEntity> findByAlarmType(Integer alarmType);

    /**
     * 根据告警等级查找
     */
    List<GatAlarmRecordEntity> findByAlarmLevel(Integer alarmLevel);

    /**
     * 根据告警状态查找
     */
    List<GatAlarmRecordEntity> findByStatus(Integer status);

    /**
     * 查找未处理的告警
     */
    @Query("SELECT a FROM GatAlarmRecordEntity a WHERE a.status = 0 ORDER BY a.alarmTime DESC")
    List<GatAlarmRecordEntity> findUnprocessedAlarms();

    /**
     * 查找处理中的告警
     */
    @Query("SELECT a FROM GatAlarmRecordEntity a WHERE a.status = 1 ORDER BY a.alarmTime DESC")
    List<GatAlarmRecordEntity> findProcessingAlarms();

    /**
     * 查找已处理的告警
     */
    @Query("SELECT a FROM GatAlarmRecordEntity a WHERE a.status = 2 ORDER BY a.resolvedTime DESC")
    List<GatAlarmRecordEntity> findProcessedAlarms();

    /**
     * 根据确认人查找告警
     */
    List<GatAlarmRecordEntity> findByAcknowledgedBy(String acknowledgedBy);

    /**
     * 根据解决人查找告警
     */
    List<GatAlarmRecordEntity> findByResolvedBy(String resolvedBy);

    /**
     * 根据告警时间范围查找
     */
    @Query("SELECT a FROM GatAlarmRecordEntity a WHERE a.alarmTime BETWEEN :startTime AND :endTime ORDER BY a.alarmTime DESC")
    List<GatAlarmRecordEntity> findByAlarmTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 根据控制器ID和时间范围查找告警
     */
    @Query("SELECT a FROM GatAlarmRecordEntity a WHERE a.controllerId = :controllerId AND a.alarmTime BETWEEN :startTime AND :endTime ORDER BY a.alarmTime DESC")
    List<GatAlarmRecordEntity> findByControllerIdAndAlarmTimeBetween(@Param("controllerId") String controllerId,
                                                                     @Param("startTime") LocalDateTime startTime,
                                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 根据通知发送状态查找
     */
    List<GatAlarmRecordEntity> findByNotificationSent(Integer notificationSent);

    /**
     * 统计各告警类型数量
     */
    @Query("SELECT a.alarmType, COUNT(a) FROM GatAlarmRecordEntity a GROUP BY a.alarmType")
    List<Object[]> countByAlarmType();

    /**
     * 统计各告警等级数量
     */
    @Query("SELECT a.alarmLevel, COUNT(a) FROM GatAlarmRecordEntity a GROUP BY a.alarmLevel")
    List<Object[]> countByAlarmLevel();

    /**
     * 统计各告警状态数量
     */
    @Query("SELECT a.status, COUNT(a) FROM GatAlarmRecordEntity a GROUP BY a.status")
    List<Object[]> countByStatus();

    /**
     * 检查告警ID是否存在
     */
    boolean existsByAlarmId(String alarmId);

    /**
     * 删除已处理的过期告警记录
     */
    void deleteByStatusAndResolvedTimeBefore(Integer status, LocalDateTime beforeTime);
}
