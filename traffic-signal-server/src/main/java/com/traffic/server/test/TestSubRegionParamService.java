package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.SubRegionParamEntity;
import com.traffic.gat1049.repository.interfaces.SubRegionParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 子区参数测试服务
 */
@Service
@Slf4j
public class TestSubRegionParamService {

    @Autowired
    private SubRegionParamRepository subRegionParamRepository;

    /**
     * 创建子区参数
     */
    public SubRegionParamEntity createSubRegionParam(String subRegionId, String subRegionName) {
        // 检查是否已存在
        SubRegionParamEntity existing = subRegionParamRepository.findBySubRegionId(subRegionId);
        if (existing != null) {
            throw new RuntimeException("子区参数已存在: " + subRegionId);
        }

        SubRegionParamEntity entity = new SubRegionParamEntity();
        entity.setSubRegionId(subRegionId);
        entity.setSubRegionName(subRegionName);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        subRegionParamRepository.insert(entity);
        log.info("创建子区参数: subRegionId={}, subRegionName={}", subRegionId, subRegionName);

        return entity;
    }

    /**
     * 根据ID查询子区参数
     */
    public SubRegionParamEntity getSubRegionParam(String subRegionId) {
        SubRegionParamEntity entity = subRegionParamRepository.findBySubRegionId(subRegionId);

        if (entity == null) {
            throw new RuntimeException("未找到子区参数: " + subRegionId);
        }

        log.info("查询子区参数: subRegionId={}", subRegionId);
        return entity;
    }

    /**
     * 查询所有子区参数
     */
    public List<SubRegionParamEntity> getAllSubRegions() {
        List<SubRegionParamEntity> entities = subRegionParamRepository.selectList(null);
        log.info("查询所有子区参数，共 {} 条", entities.size());
        return entities;
    }

    /**
     * 根据区域ID查询子区
     */
    public List<SubRegionParamEntity> getSubRegionsByRegion(String regionId) {
        List<SubRegionParamEntity> entities = subRegionParamRepository.findByRegionId(regionId);
        log.info("根据区域查询子区: regionId={}, 共 {} 条", regionId, entities.size());
        return entities;
    }

    /**
     * 根据子区名称模糊查询
     */
    public List<SubRegionParamEntity> searchSubRegionsByName(String subRegionName) {
        List<SubRegionParamEntity> entities = subRegionParamRepository.findBySubRegionNameLike(subRegionName);
        log.info("模糊查询子区: subRegionName={}, 共 {} 条", subRegionName, entities.size());
        return entities;
    }

    /**
     * 查询完整信息视图
     */
    public List<SubRegionParamEntity> getAllWithCompleteInfo() {
        List<SubRegionParamEntity> entities = subRegionParamRepository.findAllWithCompleteInfo();
        log.info("查询完整信息视图，共 {} 条", entities.size());
        return entities;
    }

    /**
     * 更新子区参数
     */
    public SubRegionParamEntity updateSubRegionParam(String subRegionId, String subRegionName) {
        SubRegionParamEntity existing = getSubRegionParam(subRegionId);

        if (subRegionName != null && !subRegionName.trim().isEmpty()) {
            existing.setSubRegionName(subRegionName);
        }
        existing.setUpdatedTime(LocalDateTime.now());

        subRegionParamRepository.updateById(existing);
        log.info("更新子区参数: subRegionId={}", subRegionId);

        return existing;
    }

    /**
     * 删除子区参数
     */
    public void deleteSubRegionParam(String subRegionId) {
        SubRegionParamEntity existing = getSubRegionParam(subRegionId);
        subRegionParamRepository.deleteById(subRegionId);
        log.info("删除子区参数: subRegionId={}", subRegionId);
    }

    /**
     * 批量创建测试子区数据
     */
    public List<SubRegionParamEntity> createBatchSubRegions(String regionPrefix, int count) {
        log.info("批量创建测试子区数据: regionPrefix={}, count={}", regionPrefix, count);

        List<SubRegionParamEntity> results = new ArrayList<>();
        String[] subRegionNames = {"中心子区", "商务子区", "住宅子区", "工业子区", "文教子区", "商业子区", "开发子区", "科技子区", "生态子区", "历史子区"};

        for (int i = 1; i <= count && i <= subRegionNames.length; i++) {
            String subRegionId = regionPrefix + String.format("%04d", i);
            String subRegionName = subRegionNames[i - 1];

            try {
                SubRegionParamEntity entity = createSubRegionParam(subRegionId, subRegionName);
                results.add(entity);
            } catch (Exception e) {
                log.warn("批量创建第{}个子区失败: {}", i, e.getMessage());
            }
        }

        log.info("批量创建完成: 成功={}, 总数={}", results.size(), count);
        return results;
    }

    /**
     * 创建子区层级结构测试数据
     */
    public Map<String, Object> createSubRegionHierarchy(String regionId, String regionName) {
        log.info("创建子区层级结构测试数据: regionId={}, regionName={}", regionId, regionName);

        Map<String, Object> result = new HashMap<>();
        List<SubRegionParamEntity> createdSubRegions = new ArrayList<>();

        try {
            // 1. 创建核心功能子区
            String[] coreTypes = {"01", "02", "03", "04"};
            String[] coreNames = {"行政中心子区", "商务中心子区", "商业中心子区", "交通枢纽子区"};

            for (int i = 0; i < coreTypes.length; i++) {
                String subRegionId = regionId + coreTypes[i] + "001";
                String subRegionName = regionName + coreNames[i];

                try {
                    SubRegionParamEntity subRegion = createSubRegionParam(subRegionId, subRegionName);
                    createdSubRegions.add(subRegion);
                } catch (Exception e) {
                    log.warn("创建核心子区失败: {}", e.getMessage());
                }
            }

            // 2. 创建功能性子区
            String[] functionalTypes = {"05", "06", "07"};
            String[] functionalNames = {"居住子区", "工业子区", "生态子区"};

            for (int i = 0; i < functionalTypes.length; i++) {
                String subRegionId = regionId + functionalTypes[i] + "001";
                String subRegionName = regionName + functionalNames[i];

                try {
                    SubRegionParamEntity subRegion = createSubRegionParam(subRegionId, subRegionName);
                    createdSubRegions.add(subRegion);
                } catch (Exception e) {
                    log.warn("创建功能子区失败: {}", e.getMessage());
                }
            }

            result.put("success", true);
            result.put("message", "子区层级结构创建成功");
            result.put("regionId", regionId);
            result.put("regionName", regionName);
            result.put("coreSubRegions", createdSubRegions.subList(0, Math.min(4, createdSubRegions.size())));
            result.put("functionalSubRegions", createdSubRegions.subList(Math.min(4, createdSubRegions.size()), createdSubRegions.size()));
            result.put("allSubRegions", createdSubRegions);
            result.put("totalCount", createdSubRegions.size());

        } catch (Exception e) {
            log.error("创建子区层级结构失败", e);
            result.put("success", false);
            result.put("message", "创建失败: " + e.getMessage());
            result.put("createdSubRegions", createdSubRegions);
            throw e; // 触发事务回滚
        }

        return result;
    }
}
