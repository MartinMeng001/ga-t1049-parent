package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traffic.gat1049.repository.entity.SysInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统信息Repository
 */
@Repository
public interface SysInfoRepository extends BaseMapper<SysInfo> {

    /**
     * 根据系统ID查询系统信息
     */
    @Select("SELECT * FROM sys_info WHERE system_id = #{systemId}")
    SysInfo findBySystemId(@Param("systemId") String systemId);

    /**
     * 查询所有激活的系统
     */
    @Select("SELECT * FROM sys_info WHERE is_active = 1 ORDER BY created_time DESC")
    List<SysInfo> findAllActive();

    /**
     * 根据供应商查询系统
     */
    @Select("SELECT * FROM sys_info WHERE supplier = #{supplier} AND is_active = 1")
    List<SysInfo> findBySupplier(@Param("supplier") String supplier);

    /**
     * 更新系统状态
     */
    @Update("UPDATE sys_info SET is_active = #{isActive}, updated_time = #{updateTime} WHERE system_id = #{systemId}")
    int updateSystemStatus(@Param("systemId") String systemId, @Param("isActive") Boolean isActive, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 分页查询系统信息
     */
    @Select("SELECT * FROM sys_info WHERE sys_name LIKE CONCAT('%', #{keyword}, '%') OR supplier LIKE CONCAT('%', #{keyword}, '%') ORDER BY created_time DESC")
    IPage<SysInfo> findByKeywordPaged(Page<SysInfo> page, @Param("keyword") String keyword);
}