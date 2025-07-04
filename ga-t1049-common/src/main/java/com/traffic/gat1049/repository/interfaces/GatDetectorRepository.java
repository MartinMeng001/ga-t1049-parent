package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatDetectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 检测器表Repository
 */
@Repository
public interface GatDetectorRepository extends JpaRepository<GatDetectorEntity, Long> {

    /**
     * 根据控制器ID和检测器ID查找
     */
    Optional<GatDetectorEntity> findByControllerIdAndDetectorId(String controllerId, String detectorId);

    /**
     * 根据控制器ID查找所有检测器
     */
    List<GatDetectorEntity> findByControllerId(String controllerId);

    /**
     * 根据检测器类型查找
     */
    List<GatDetectorEntity> findByDetectorType(Integer detectorType);

    /**
     * 根据车道ID查找
     */
    List<GatDetectorEntity> findByLaneId(String laneId);

    /**
     * 根据检测方向查找
     */
    List<GatDetectorEntity> findByDirection(String direction);

    /**
     * 根据状态查找
     */
    List<GatDetectorEntity> findByStatus(Integer status);

    /**
     * 查找正常工作的检测器
     */
    @Query("SELECT d FROM GatDetectorEntity d WHERE d.status = 1")
    List<GatDetectorEntity> findNormalDetectors();

    /**
     * 查找故障的检测器
     */
    @Query("SELECT d FROM GatDetectorEntity d WHERE d.status = 0")
    List<GatDetectorEntity> findFaultDetectors();

    /**
     * 根据控制器ID和检测器类型查找
     */
    List<GatDetectorEntity> findByControllerIdAndDetectorType(String controllerId, Integer detectorType);

    /**
     * 根据灵敏度等级查找
     */
    List<GatDetectorEntity> findBySensitivityLevel(Integer sensitivityLevel);

    /**
     * 统计各类型检测器数量
     */
    @Query("SELECT d.detectorType, COUNT(d) FROM GatDetectorEntity d GROUP BY d.detectorType")
    List<Object[]> countByDetectorType();

    /**
     * 统计各状态检测器数量
     */
    @Query("SELECT d.status, COUNT(d) FROM GatDetectorEntity d GROUP BY d.status")
    List<Object[]> countByStatus();

    /**
     * 检查检测器是否存在
     */
    boolean existsByControllerIdAndDetectorId(String controllerId, String detectorId);
}
