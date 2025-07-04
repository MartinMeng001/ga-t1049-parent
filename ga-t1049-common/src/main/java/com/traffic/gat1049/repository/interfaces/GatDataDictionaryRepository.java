package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatDataDictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据字典表Repository
 */
@Repository
public interface GatDataDictionaryRepository extends JpaRepository<GatDataDictionaryEntity, Long> {

    /**
     * 根据字典编码查找
     */
    Optional<GatDataDictionaryEntity> findByDictCode(String dictCode);

    /**
     * 根据字典类型查找
     */
    List<GatDataDictionaryEntity> findByDictType(String dictType);

    /**
     * 根据字典类型和值查找
     */
    Optional<GatDataDictionaryEntity> findByDictTypeAndDictValue(String dictType, String dictValue);

    /**
     * 根据字典名称查找
     */
    List<GatDataDictionaryEntity> findByDictName(String dictName);

    /**
     * 根据父级编码查找子项
     */
    List<GatDataDictionaryEntity> findByParentCode(String parentCode);

    /**
     * 根据状态查找
     */
    List<GatDataDictionaryEntity> findByStatus(Integer status);

    /**
     * 根据默认状态查找
     */
    List<GatDataDictionaryEntity> findByIsDefault(Integer isDefault);

    /**
     * 查找启用的字典项
     */
    @Query("SELECT d FROM GatDataDictionaryEntity d WHERE d.status = 1 ORDER BY d.sortOrder ASC")
    List<GatDataDictionaryEntity> findEnabledDictionaries();

    /**
     * 根据字典类型查找启用的字典项
     */
    @Query("SELECT d FROM GatDataDictionaryEntity d WHERE d.dictType = :dictType AND d.status = 1 ORDER BY d.sortOrder ASC")
    List<GatDataDictionaryEntity> findEnabledByDictType(@Param("dictType") String dictType);

    /**
     * 根据父级编码查找启用的子项
     */
    @Query("SELECT d FROM GatDataDictionaryEntity d WHERE d.parentCode = :parentCode AND d.status = 1 ORDER BY d.sortOrder ASC")
    List<GatDataDictionaryEntity> findEnabledByParentCode(@Param("parentCode") String parentCode);

    /**
     * 查找默认字典项
     */
    @Query("SELECT d FROM GatDataDictionaryEntity d WHERE d.isDefault = 1 AND d.status = 1")
    List<GatDataDictionaryEntity> findDefaultDictionaries();

    /**
     * 根据字典类型查找默认字典项
     */
    @Query("SELECT d FROM GatDataDictionaryEntity d WHERE d.dictType = :dictType AND d.isDefault = 1 AND d.status = 1")
    Optional<GatDataDictionaryEntity> findDefaultByDictType(@Param("dictType") String dictType);

    /**
     * 统计各字典类型数量
     */
    @Query("SELECT d.dictType, COUNT(d) FROM GatDataDictionaryEntity d GROUP BY d.dictType")
    List<Object[]> countByDictType();

    /**
     * 统计各状态数量
     */
    @Query("SELECT d.status, COUNT(d) FROM GatDataDictionaryEntity d GROUP BY d.status")
    List<Object[]> countByStatus();

    /**
     * 检查字典编码是否存在
     */
    boolean existsByDictCode(String dictCode);

    /**
     * 检查字典类型和值的组合是否存在
     */
    boolean existsByDictTypeAndDictValue(String dictType, String dictValue);
}
