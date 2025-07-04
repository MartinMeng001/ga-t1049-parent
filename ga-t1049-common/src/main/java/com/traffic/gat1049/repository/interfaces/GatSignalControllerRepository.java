package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatSignalControllerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 信号机控制器表Repository
 */
@Repository
public interface GatSignalControllerRepository extends JpaRepository<GatSignalControllerEntity, Long> {

    /**
     * 根据控制器ID查找
     */
    Optional<GatSignalControllerEntity> findByControllerId(String controllerId);

    /**
     * 根据控制器名称查找
     */
    Optional<GatSignalControllerEntity> findByControllerName(String controllerName);

    /**
     * 根据厂商查找
     */
    List<GatSignalControllerEntity> findByVendor(String vendor);

    /**
     * 根据设备状态查找
     */
    List<GatSignalControllerEntity> findByDeviceStatus(Integer deviceStatus);

    /**
     * 根据通信方式查找
     */
    List<GatSignalControllerEntity> findByCommunicationMode(Integer communicationMode);

    /**
     * 根据IP地址查找
     */
    Optional<GatSignalControllerEntity> findByIpAddress(String ipAddress);

    /**
     * 查找在线设备
     */
    @Query("SELECT s FROM GatSignalControllerEntity s WHERE s.deviceStatus = 1")
    List<GatSignalControllerEntity> findOnlineControllers();

    /**
     * 查找离线设备
     */
    @Query("SELECT s FROM GatSignalControllerEntity s WHERE s.deviceStatus = 0")
    List<GatSignalControllerEntity> findOfflineControllers();

    /**
     * 查找故障设备
     */
    @Query("SELECT s FROM GatSignalControllerEntity s WHERE s.deviceStatus = 2")
    List<GatSignalControllerEntity> findFaultControllers();

    /**
     * 根据地理位置范围查找
     */
    @Query("SELECT s FROM GatSignalControllerEntity s WHERE s.longitude BETWEEN :minLng AND :maxLng AND s.latitude BETWEEN :minLat AND :maxLat")
    List<GatSignalControllerEntity> findByLocationRange(@Param("minLng") BigDecimal minLng,
                                                        @Param("maxLng") BigDecimal maxLng,
                                                        @Param("minLat") BigDecimal minLat,
                                                        @Param("maxLat") BigDecimal maxLat);

    /**
     * 根据最后在线时间查找
     */
    @Query("SELECT s FROM GatSignalControllerEntity s WHERE s.lastOnlineTime < :beforeTime")
    List<GatSignalControllerEntity> findByLastOnlineTimeBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 统计各厂商设备数量
     */
    @Query("SELECT s.vendor, COUNT(s) FROM GatSignalControllerEntity s GROUP BY s.vendor")
    List<Object[]> countByVendor();

    /**
     * 统计各状态设备数量
     */
    @Query("SELECT s.deviceStatus, COUNT(s) FROM GatSignalControllerEntity s GROUP BY s.deviceStatus")
    List<Object[]> countByDeviceStatus();

    /**
     * 检查控制器ID是否存在
     */
    boolean existsByControllerId(String controllerId);

    /**
     * 检查IP地址是否存在
     */
    boolean existsByIpAddress(String ipAddress);
}
