package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatSysInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 系统信息表Repository
 */
@Repository
public interface GatSysInfoRepository extends JpaRepository<GatSysInfoEntity, Long> {

    /**
     * 根据系统ID查找
     */
    Optional<GatSysInfoEntity> findBySystemId(String systemId);

    /**
     * 根据系统名称查找
     */
    Optional<GatSysInfoEntity> findBySystemName(String systemName);

    /**
     * 根据状态查找
     */
    List<GatSysInfoEntity> findByStatus(Integer status);

    /**
     * 根据制造商查找
     */
    List<GatSysInfoEntity> findByManufacturer(String manufacturer);

    /**
     * 根据软件版本查找
     */
    List<GatSysInfoEntity> findBySoftwareVersion(String softwareVersion);

    /**
     * 查找启用的系统
     */
    @Query("SELECT s FROM GatSysInfoEntity s WHERE s.status = 1")
    List<GatSysInfoEntity> findEnabledSystems();

    /**
     * 根据创建时间范围查找
     */
    @Query("SELECT s FROM GatSysInfoEntity s WHERE s.createdAt BETWEEN :startTime AND :endTime")
    List<GatSysInfoEntity> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态的系统数量
     */
    @Query("SELECT s.status, COUNT(s) FROM GatSysInfoEntity s GROUP BY s.status")
    List<Object[]> countByStatus();

    /**
     * 检查系统ID是否存在
     */
    boolean existsBySystemId(String systemId);

    /**
     * 检查系统名称是否存在
     */
    boolean existsBySystemName(String systemName);
}
