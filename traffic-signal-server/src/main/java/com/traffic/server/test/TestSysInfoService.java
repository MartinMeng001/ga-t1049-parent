package com.traffic.server.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.traffic.gat1049.repository.entity.SysInfoEntity;
import com.traffic.gat1049.repository.interfaces.SysInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class TestSysInfoService {

    @Autowired
    private SysInfoRepository sysInfoRepository;

    /**
     * 创建系统信息
     */
    public SysInfoEntity createSysInfo(String systemId, String sysName, String sysVersion, String supplier) {
        log.info("开始创建系统信息: systemId={}, sysName={}", systemId, sysName);

        // 检查是否已存在（使用现有的方法）
        SysInfoEntity existing = sysInfoRepository.findBySystemId(systemId);
        if (existing != null && existing.getIsActive()) {
            throw new IllegalArgumentException("系统ID已存在: " + systemId);
        }

        // 创建新的实体
        SysInfoEntity entity = new SysInfoEntity();
        entity.setSystemId(systemId);
        entity.setSysName(sysName);
        entity.setSysVersion(sysVersion);
        entity.setSupplier(supplier);
        entity.setDescription("测试创建的系统信息");
        entity.setIsActive(true);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        // 使用MyBatis Plus的insert方法
        int result = sysInfoRepository.insert(entity);
        if (result <= 0) {
            throw new RuntimeException("插入系统信息失败");
        }

        log.info("系统信息创建成功: systemId={}", entity.getSystemId());
        return entity;
    }

    /**
     * 更新系统信息
     */
    public SysInfoEntity updateSysInfo(String systemId, String sysName, String sysVersion, String supplier) {
        log.info("开始更新系统信息: systemId={}", systemId);

        // 查找现有记录
        SysInfoEntity entity = sysInfoRepository.findBySystemId(systemId);
        if (entity == null || !entity.getIsActive()) {
            throw new IllegalArgumentException("系统信息不存在或已停用: " + systemId);
        }

        // 更新字段
        entity.setSysName(sysName);
        entity.setSysVersion(sysVersion);
        entity.setSupplier(supplier);
        entity.setDescription("测试更新的系统信息 - " + LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        // 使用MyBatis Plus的updateById方法
        int result = sysInfoRepository.updateById(entity);
        if (result <= 0) {
            throw new RuntimeException("更新系统信息失败");
        }

        log.info("系统信息更新成功: systemId={}", entity.getSystemId());
        return entity;
    }

    /**
     * 查询系统信息
     */
    public SysInfoEntity getSysInfo(String systemId) {
        log.info("查询系统信息: systemId={}", systemId);

        SysInfoEntity entity = sysInfoRepository.findBySystemId(systemId);
        if (entity == null || !entity.getIsActive()) {
            throw new IllegalArgumentException("系统信息不存在或已停用: " + systemId);
        }

        return entity;
    }

    /**
     * 查询所有活跃系统信息
     */
    public List<SysInfoEntity> getAllActiveSysInfo() {
        log.info("查询所有活跃系统信息");
        return sysInfoRepository.findAllActive();
    }

    /**
     * 根据供应商查询系统
     */
    public List<SysInfoEntity> getSysInfoBySupplier(String supplier) {
        log.info("根据供应商查询系统: supplier={}", supplier);
        return sysInfoRepository.findBySupplier(supplier);
    }

    /**
     * 删除系统信息（逻辑删除）
     */
    public void deleteSysInfo(String systemId) {
        log.info("删除系统信息: systemId={}", systemId);

        SysInfoEntity entity = sysInfoRepository.findBySystemId(systemId);
        if (entity == null) {
            throw new IllegalArgumentException("系统信息不存在: " + systemId);
        }

        // 使用现有的updateSystemStatus方法进行逻辑删除
        int result = sysInfoRepository.updateSystemStatus(systemId, false, LocalDateTime.now());
        if (result <= 0) {
            throw new RuntimeException("删除系统信息失败");
        }

        log.info("系统信息删除成功: systemId={}", systemId);
    }

    /**
     * 批量创建测试数据
     */
    public List<SysInfoEntity> createBatchTestData(int count) {
        log.info("批量创建测试数据: count={}", count);

        List<SysInfoEntity> results = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String systemId = "TEST_BATCH_" + String.format("%03d", i);
            String sysName = "批量测试系统 " + i;
            String sysVersion = "1.0." + i;
            String supplier = "测试供应商 " + (i % 3 + 1); // 轮换3个供应商

            try {
                SysInfoEntity entity = createSysInfo(systemId, sysName, sysVersion, supplier);
                results.add(entity);
            } catch (Exception e) {
                log.warn("批量创建第{}条数据失败: {}", i, e.getMessage());
            }
        }

        log.info("批量创建完成: 成功={}, 总数={}", results.size(), count);
        return results;
    }

    /**
     * 测试分页查询
     */
//    public IPage<SysInfoEntity> testPageQuery(String keyword, int pageNum, int pageSize) {
//        log.info("测试分页查询: keyword={}, pageNum={}, pageSize={}", keyword, pageNum, pageSize);
//
//        Page<SysInfoEntity> page = new Page<>(pageNum, pageSize);
//        if (StringUtils.isEmpty(keyword)) {
//            keyword = ""; // 空关键词查询所有
//        }
//
//        return sysInfoRepository.findByKeywordPaged(page, keyword);
//    }
}
