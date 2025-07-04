package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatAlarmRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 告警规则表Repository
 */
@Repository
public interface GatAlarmRuleRepository extends JpaRepository<GatAlarmRuleEntity, Long> {

    /**
     * 根据规则ID查找
     */
    Optional<GatAlarmRuleEntity> findByRuleId(String ruleId);

    /**
     * 根据规则名称查找
     */
    Optional<GatAlarmRuleEntity> findByRuleName(String ruleName);

    /**
     * 根据规则类型查找
     */
    List<GatAlarmRuleEntity> findByRuleType(Integer ruleType);

    /**
     * 根据告警等级查找
     */
    List<GatAlarmRuleEntity> findByAlarmLevel(Integer alarmLevel);

    /**
     * 根据启用状态查找
     */
    List<GatAlarmRuleEntity> findByEnabled(Integer enabled);

    /**
     * 查找启用的规则
     */
    @Query("SELECT r FROM GatAlarmRuleEntity r WHERE r.enabled = 1")
    List<GatAlarmRuleEntity> findEnabledRules();

    /**
     * 根据创建人查找规则
     */
    List<GatAlarmRuleEntity> findByCreatedBy(String createdBy);

    /**
     * 根据创建时间范围查找规则
     */
    @Query("SELECT r FROM GatAlarmRuleEntity r WHERE r.createdAt BETWEEN :startTime AND :endTime")
    List<GatAlarmRuleEntity> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 根据规则类型和告警等级查找
     */
    List<GatAlarmRuleEntity> findByRuleTypeAndAlarmLevel(Integer ruleType, Integer alarmLevel);

    /**
     * 统计各规则类型数量
     */
    @Query("SELECT r.ruleType, COUNT(r) FROM GatAlarmRuleEntity r GROUP BY r.ruleType")
    List<Object[]> countByRuleType();

    /**
     * 统计各告警等级数量
     */
    @Query("SELECT r.alarmLevel, COUNT(r) FROM GatAlarmRuleEntity r GROUP BY r.alarmLevel")
    List<Object[]> countByAlarmLevel();

    /**
     * 检查规则ID是否存在
     */
    boolean existsByRuleId(String ruleId);

    /**
     * 检查规则名称是否存在
     */
    boolean existsByRuleName(String ruleName);
}
