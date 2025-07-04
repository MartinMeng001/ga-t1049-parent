package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 事件表Repository
 */
@Repository
public interface GatEventRepository extends JpaRepository<GatEventEntity, Long> {

    /**
     * 根据事件ID查找
     */
    Optional<GatEventEntity> findByEventId(String eventId);

    /**
     * 根据控制器ID查找事件
     */
    List<GatEventEntity> findByControllerId(String controllerId);

    /**
     * 根据事件类型查找
     */
    List<GatEventEntity> findByEventType(Integer eventType);

    /**
     * 根据事件等级查找
     */
    List<GatEventEntity> findByEventLevel(Integer eventLevel);

    /**
     * 根据事件源查找
     */
    List<GatEventEntity> findByEventSource(String eventSource);

    /**
     * 根据处理状态查找
     */
    List<GatEventEntity> findByIsHandled(Integer isHandled);

    /**
     * 查找未处理的事件
     */
    @Query("SELECT e FROM GatEventEntity e WHERE e.isHandled = 0 ORDER BY e.eventTime DESC")
    List<GatEventEntity> findUnhandledEvents();

    /**
     * 查找已处理的事件
     */
    @Query("SELECT e FROM GatEventEntity e WHERE e.isHandled = 1 ORDER BY e.handledTime DESC")
    List<GatEventEntity> findHandledEvents();

    /**
     * 根据处理人查找事件
     */
    List<GatEventEntity> findByHandledBy(String handledBy);

    /**
     * 根据时间范围查找事件
     */
    @Query("SELECT e FROM GatEventEntity e WHERE e.eventTime BETWEEN :startTime AND :endTime ORDER BY e.eventTime DESC")
    List<GatEventEntity> findByEventTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 根据控制器ID和时间范围查找事件
     */
    @Query("SELECT e FROM GatEventEntity e WHERE e.controllerId = :controllerId AND e.eventTime BETWEEN :startTime AND :endTime ORDER BY e.eventTime DESC")
    List<GatEventEntity> findByControllerIdAndEventTimeBetween(@Param("controllerId") String controllerId,
                                                               @Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 根据事件类型和等级查找
     */
    List<GatEventEntity> findByEventTypeAndEventLevel(Integer eventType, Integer eventLevel);

    /**
     * 统计各事件类型数量
     */
    @Query("SELECT e.eventType, COUNT(e) FROM GatEventEntity e GROUP BY e.eventType")
    List<Object[]> countByEventType();

    /**
     * 统计各事件等级数量
     */
    @Query("SELECT e.eventLevel, COUNT(e) FROM GatEventEntity e GROUP BY e.eventLevel")
    List<Object[]> countByEventLevel();

    /**
     * 检查事件ID是否存在
     */
    boolean existsByEventId(String eventId);
}
