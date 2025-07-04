package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatDeviceAdapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 设备适配器表Repository
 */
@Repository
public interface GatDeviceAdapterRepository extends JpaRepository<GatDeviceAdapterEntity, Long> {

    /**
     * 根据适配器ID查找
     */
    Optional<GatDeviceAdapterEntity> findByAdapterId(String adapterId);

    /**
     * 根据适配器名称查找
     */
    Optional<GatDeviceAdapterEntity> findByAdapterName(String adapterName);

    /**
     * 根据厂商查找
     */
    List<GatDeviceAdapterEntity> findByVendor(String vendor);

    /**
     * 根据适配器类型查找
     */
    List<GatDeviceAdapterEntity> findByAdapterType(String adapterType);

    /**
     * 根据协议类型查找
     */
    List<GatDeviceAdapterEntity> findByProtocolType(String protocolType);

    /**
     * 根据状态查找
     */
    List<GatDeviceAdapterEntity> findByStatus(Integer status);

    /**
     * 查找启用的适配器
     */
    @Query("SELECT a FROM GatDeviceAdapterEntity a WHERE a.status = 1")
    List<GatDeviceAdapterEntity> findEnabledAdapters();

    /**
     * 根据厂商和类型查找
     */
    List<GatDeviceAdapterEntity> findByVendorAndAdapterType(String vendor, String adapterType);

    /**
     * 根据版本查找
     */
    List<GatDeviceAdapterEntity> findByAdapterVersion(String adapterVersion);

    /**
     * 查找支持特定协议的适配器
     */
    @Query("SELECT a FROM GatDeviceAdapterEntity a WHERE a.protocolType = :protocolType AND a.status = 1")
    List<GatDeviceAdapterEntity> findByProtocolTypeAndEnabled(@Param("protocolType") String protocolType);

    /**
     * 统计各厂商适配器数量
     */
    @Query("SELECT a.vendor, COUNT(a) FROM GatDeviceAdapterEntity a GROUP BY a.vendor")
    List<Object[]> countByVendor();

    /**
     * 统计各类型适配器数量
     */
    @Query("SELECT a.adapterType, COUNT(a) FROM GatDeviceAdapterEntity a GROUP BY a.adapterType")
    List<Object[]> countByAdapterType();

    /**
     * 统计各状态适配器数量
     */
    @Query("SELECT a.status, COUNT(a) FROM GatDeviceAdapterEntity a GROUP BY a.status")
    List<Object[]> countByStatus();

    /**
     * 检查适配器ID是否存在
     */
    boolean existsByAdapterId(String adapterId);

    /**
     * 检查适配器名称是否存在
     */
    boolean existsByAdapterName(String adapterName);

    /**
     * 根据厂商和协议类型查找唯一适配器
     */
    Optional<GatDeviceAdapterEntity> findByVendorAndProtocolTypeAndStatus(String vendor, String protocolType, Integer status);
}
