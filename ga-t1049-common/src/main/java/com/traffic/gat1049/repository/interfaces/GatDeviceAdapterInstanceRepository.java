package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatDeviceAdapterInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 设备适配器实例表Repository
 */
@Repository
public interface GatDeviceAdapterInstanceRepository extends JpaRepository<GatDeviceAdapterInstanceEntity, Long> {

    /**
     * 根据实例ID查找
     */
    Optional<GatDeviceAdapterInstanceEntity> findByInstanceId(String instanceId);

    /**
     * 根据适配器ID查找所有实例
     */
    List<GatDeviceAdapterInstanceEntity> findByAdapterId(String adapterId);

    /**
     * 根据控制器ID查找实例
     */
    List<GatDeviceAdapterInstanceEntity> findByControllerId(String controllerId);

    /**
     * 根据适配器ID和控制器ID查找实例
     */
    Optional<GatDeviceAdapterInstanceEntity> findByAdapterIdAndControllerId(String adapterId, String controllerId);

    /**
     * 根据同步状态查找实例
     */
    List<GatDeviceAdapterInstanceEntity> findBySyncStatus(Integer syncStatus);

    /**
     * 根据状态查找实例
     */
    List<GatDeviceAdapterInstanceEntity> findByStatus(Integer status);

    /**
     * 查找启用的实例
     */
    @Query("SELECT i FROM GatDeviceAdapterInstanceEntity i WHERE i.status = 1")
    List<GatDeviceAdapterInstanceEntity> findEnabledInstances();

    /**
     * 查找同步中的实例
     */
    @Query("SELECT i FROM GatDeviceAdapterInstanceEntity i WHERE i.syncStatus = 1")
    List<GatDeviceAdapterInstanceEntity> findSyncingInstances();

    /**
     * 查找同步成功的实例
     */
    @Query("SELECT i FROM GatDeviceAdapterInstanceEntity i WHERE i.syncStatus = 2")
    List<GatDeviceAdapterInstanceEntity> findSyncSuccessInstances();

    /**
     * 查找同步失败的实例
     */
    @Query("SELECT i FROM GatDeviceAdapterInstanceEntity i WHERE i.syncStatus = 3")
    List<GatDeviceAdapterInstanceEntity> findSyncFailedInstances();

    /**
     * 根据同步间隔查找实例
     */
    List<GatDeviceAdapterInstanceEntity> findBySyncInterval(Integer syncInterval);

    /**
     * 根据最后同步时间查找需要同步的实例
     */
    @Query("SELECT i FROM GatDeviceAdapterInstanceEntity i WHERE i.status = 1 AND (i.lastSyncTime IS NULL OR i.lastSyncTime < :threshold)")
    List<GatDeviceAdapterInstanceEntity> findInstancesNeedingSync(@Param("threshold") LocalDateTime threshold);

    /**
     * 根据超时时间查找实例
     */
    List<GatDeviceAdapterInstanceEntity> findByTimeoutSeconds(Integer timeoutSeconds);

    /**
     * 根据重试次数查找实例
     */
    List<GatDeviceAdapterInstanceEntity> findByRetryCount(Integer retryCount);

    /**
     * 统计各同步状态实例数量
     */
    @Query("SELECT i.syncStatus, COUNT(i) FROM GatDeviceAdapterInstanceEntity i GROUP BY i.syncStatus")
    List<Object[]> countBySyncStatus();

    /**
     * 统计各状态实例数量
     */
    @Query("SELECT i.status, COUNT(i) FROM GatDeviceAdapterInstanceEntity i GROUP BY i.status")
    List<Object[]> countByStatus();

    /**
     * 检查实例ID是否存在
     */
    boolean existsByInstanceId(String instanceId);

    /**
     * 检查适配器ID和控制器ID的组合是否存在
     */
    boolean existsByAdapterIdAndControllerId(String adapterId, String controllerId);

    /**
     * 删除指定适配器的所有实例
     */
    void deleteByAdapterId(String adapterId);

    /**
     * 删除指定控制器的所有实例
     */
    void deleteByControllerId(String controllerId);
}
