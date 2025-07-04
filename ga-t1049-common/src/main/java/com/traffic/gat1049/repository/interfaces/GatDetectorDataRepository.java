package com.traffic.gat1049.repository.interfaces;
import com.traffic.gat1049.repository.entity.GatDetectorDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检测器数据表Repository
 */
@Repository
public interface GatDetectorDataRepository extends JpaRepository<GatDetectorDataEntity, Long> {

    /**
     * 根据控制器ID查找数据
     */
    List<GatDetectorDataEntity> findByControllerId(String controllerId);

    /**
     * 根据检测器ID查找数据
     */
    List<GatDetectorDataEntity> findByDetectorId(String detectorId);

    /**
     * 根据控制器ID和检测器ID查找数据
     */
    List<GatDetectorDataEntity> findByControllerIdAndDetectorId(String controllerId, String detectorId);

    /**
     * 根据时间范围查找数据
     */
    @Query("SELECT d FROM GatDetectorDataEntity d WHERE d.detectionTime BETWEEN :startTime AND :endTime ORDER BY d.detectionTime DESC")
    List<GatDetectorDataEntity> findByDetectionTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 根据控制器ID和时间范围查找数据
     */
    @Query("SELECT d FROM GatDetectorDataEntity d WHERE d.controllerId = :controllerId AND d.detectionTime BETWEEN :startTime AND :endTime ORDER BY d.detectionTime DESC")
    List<GatDetectorDataEntity> findByControllerIdAndDetectionTimeBetween(@Param("controllerId") String controllerId,
                                                                          @Param("startTime") LocalDateTime startTime,
                                                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 根据检测器状态查找数据
     */
    List<GatDetectorDataEntity> findByDetectorStatus(Integer detectorStatus);

    /**
     * 查找车辆数量超过阈值的数据
     */
    List<GatDetectorDataEntity> findByVehicleCountGreaterThan(Integer vehicleCount);

    /**
     * 查找占有率超过阈值的数据
     */
    List<GatDetectorDataEntity> findByOccupancyRateGreaterThan(BigDecimal occupancyRate);

    /**
     * 查找平均速度超过阈值的数据
     */
    List<GatDetectorDataEntity> findByAverageSpeedGreaterThan(BigDecimal averageSpeed);

    /**
     * 查找最新的检测器数据
     */
    @Query("SELECT d FROM GatDetectorDataEntity d WHERE d.controllerId = :controllerId AND d.detectorId = :detectorId ORDER BY d.detectionTime DESC")
    List<GatDetectorDataEntity> findLatestByControllerIdAndDetectorId(@Param("controllerId") String controllerId,
                                                                      @Param("detectorId") String detectorId);

    /**
     * 统计指定时间范围内的车辆总数
     */
    @Query("SELECT SUM(d.vehicleCount) FROM GatDetectorDataEntity d WHERE d.controllerId = :controllerId AND d.detectionTime BETWEEN :startTime AND :endTime")
    Long sumVehicleCountByControllerIdAndTimeRange(@Param("controllerId") String controllerId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 删除指定时间之前的数据
     */
    void deleteByDetectionTimeBefore(LocalDateTime beforeTime);
}