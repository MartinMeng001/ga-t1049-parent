package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 事件表实体
 */
@Entity
@Table(name = "gat_event", indexes = {
        @Index(name = "idx_event_id", columnList = "eventId"),
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_event_type", columnList = "eventType"),
        @Index(name = "idx_event_level", columnList = "eventLevel"),
        @Index(name = "idx_event_time", columnList = "eventTime"),
        @Index(name = "idx_is_handled", columnList = "isHandled")
})
public class GatEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", length = 32, nullable = false, unique = true)
    private String eventId;

    @Column(name = "controller_id", length = 12)
    private String controllerId;

    @Column(name = "event_type", columnDefinition = "TINYINT", nullable = false)
    private Integer eventType; // 1-设备故障，2-通信异常，3-配置变更，4-系统告警

    @Column(name = "event_level", columnDefinition = "TINYINT", nullable = false)
    private Integer eventLevel; // 1-信息，2-警告，3-错误，4-严重

    @Column(name = "event_source", length = 32)
    private String eventSource;

    @Column(name = "event_title", length = 128, nullable = false)
    private String eventTitle;

    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "event_data", columnDefinition = "JSON")
    private String eventData;

    @Column(name = "is_handled", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isHandled; // 0-未处理，1-已处理

    @Column(name = "handled_by", length = 64)
    private String handledBy;

    @Column(name = "handled_time")
    private LocalDateTime handledTime;

    @Column(name = "handle_result", columnDefinition = "TEXT")
    private String handleResult;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 外键关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id", referencedColumnName = "controllerId", insertable = false, updatable = false)
    private GatSignalControllerEntity signalController;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public GatEventEntity() {}

    public GatEventEntity(String eventId, Integer eventType, Integer eventLevel, String eventTitle, LocalDateTime eventTime) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventLevel = eventLevel;
        this.eventTitle = eventTitle;
        this.eventTime = eventTime;
        this.isHandled = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public Integer getEventType() { return eventType; }
    public void setEventType(Integer eventType) { this.eventType = eventType; }

    public Integer getEventLevel() { return eventLevel; }
    public void setEventLevel(Integer eventLevel) { this.eventLevel = eventLevel; }

    public String getEventSource() { return eventSource; }
    public void setEventSource(String eventSource) { this.eventSource = eventSource; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }

    public Integer getIsHandled() { return isHandled; }
    public void setIsHandled(Integer isHandled) { this.isHandled = isHandled; }

    public String getHandledBy() { return handledBy; }
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }

    public LocalDateTime getHandledTime() { return handledTime; }
    public void setHandledTime(LocalDateTime handledTime) { this.handledTime = handledTime; }

    public String getHandleResult() { return handleResult; }
    public void setHandleResult(String handleResult) { this.handleResult = handleResult; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public GatSignalControllerEntity getSignalController() { return signalController; }
    public void setSignalController(GatSignalControllerEntity signalController) { this.signalController = signalController; }

    @Override
    public String toString() {
        return "GatEventEntity{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", eventType=" + eventType +
                ", eventLevel=" + eventLevel +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventTime=" + eventTime +
                ", isHandled=" + isHandled +
                '}';
    }
}
