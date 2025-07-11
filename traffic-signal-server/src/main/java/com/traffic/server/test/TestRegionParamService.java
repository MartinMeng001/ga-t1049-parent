package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.RegionParamEntity;
import com.traffic.gat1049.repository.interfaces.RegionCrossRepository;
import com.traffic.gat1049.repository.interfaces.RegionParamRepository;
import com.traffic.gat1049.repository.interfaces.RegionSubRegionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域参数测试服务
 * 放置位置：traffic-signal-server/src/main/java/com/traffic/server/service/TestRegionParamService.java
 */
@Service
@Transactional
@Slf4j
public class TestRegionParamService {

    @Autowired
    private RegionParamRepository regionParamRepository;

    @Autowired
    private RegionSubRegionRepository regionSubRegionRepository;

    @Autowired
    private RegionCrossRepository regionCrossRepository;

    /**
     * 创建区域参数
     */
    public RegionParamEntity createRegionParam(String regionId, String regionName, String description) {
        log.info("开始创建区域参数: regionId={}, regionName={}", regionId, regionName);

        // 检查是否已存在
        RegionParamEntity existing = regionParamRepository.findByRegionId(regionId);
        if (existing != null) {
            throw new IllegalArgumentException("区域ID已存在: " + regionId);
        }

        // 创建新的实体
        RegionParamEntity entity = new RegionParamEntity();
        entity.setRegionId(regionId);
        entity.setRegionName(regionName);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        // 使用MyBatis Plus的insert方法
        int result = regionParamRepository.insert(entity);
        if (result <= 0) {
            throw new RuntimeException("插入区域参数失败");
        }

        log.info("区域参数创建成功: regionId={}", entity.getRegionId());
        return entity;
    }

    /**
     * 更新区域参数
     */
    public RegionParamEntity updateRegionParam(String regionId, String regionName, String description) {
        log.info("开始更新区域参数: regionId={}", regionId);

        // 查找现有记录
        RegionParamEntity entity = regionParamRepository.findByRegionId(regionId);
        if (entity == null) {
            throw new IllegalArgumentException("区域参数不存在或已停用: " + regionId);
        }

        // 更新字段
        entity.setRegionName(regionName);
        entity.setUpdatedTime(LocalDateTime.now());

        // 使用MyBatis Plus的updateById方法
        int result = regionParamRepository.updateById(entity);
        if (result <= 0) {
            throw new RuntimeException("更新区域参数失败");
        }

        log.info("区域参数更新成功: regionId={}", entity.getRegionId());
        return entity;
    }

    /**
     * 查询区域参数
     */
    public RegionParamEntity getRegionParam(String regionId) {
        log.info("查询区域参数: regionId={}", regionId);

        RegionParamEntity entity = regionParamRepository.findByRegionId(regionId);
        if (entity == null) {
            throw new IllegalArgumentException("区域参数不存在或已停用: " + regionId);
        }

        return entity;
    }

    /**
     * 根据名称模糊查询区域
     */
    public List<RegionParamEntity> searchRegionsByName(String regionName) {
        log.info("根据名称模糊查询区域: regionName={}", regionName);
        return regionParamRepository.findByRegionNameLike(regionName);
    }

    /**
     * 根据ID前缀查询区域（如：查询北京市下所有区域）
     */
    public List<RegionParamEntity> getRegionsByPrefix(String prefix) {
        log.info("根据ID前缀查询区域: prefix={}", prefix);
        return regionParamRepository.findByRegionIdPrefix(prefix);
    }

    /**
     * 查询所有区域（包含完整信息）
     */
    public List<RegionParamEntity> getAllRegionsWithCompleteInfo() {
        log.info("查询所有区域（包含完整信息）");
        return regionParamRepository.findAllWithCompleteInfo();
    }

    /**
     * 删除区域参数（逻辑删除）
     */
    public void deleteRegionParam(String regionId) {
        log.info("删除区域参数: regionId={}", regionId);

        RegionParamEntity entity = regionParamRepository.findByRegionId(regionId);
        if (entity == null) {
            throw new IllegalArgumentException("区域参数不存在: " + regionId);
        }

        entity.setUpdatedTime(LocalDateTime.now());

        int result = regionParamRepository.updateById(entity);
        if (result <= 0) {
            throw new RuntimeException("删除区域参数失败");
        }

        log.info("区域参数删除成功: regionId={}", regionId);
    }

    /**
     * 批量创建测试区域数据
     */
    public List<RegionParamEntity> createBatchTestRegions(String cityPrefix, int count) {
        log.info("批量创建测试区域数据: cityPrefix={}, count={}", cityPrefix, count);

        List<RegionParamEntity> results = new ArrayList<>();
        String[] regionNames = {"东城区", "西城区", "朝阳区", "海淀区", "丰台区", "石景山区", "通州区", "昌平区", "大兴区", "房山区"};

        for (int i = 1; i <= count && i <= regionNames.length; i++) {
            String regionId = cityPrefix + String.format("%02d", i);
            String regionName = regionNames[i - 1];
            String description = "测试创建的" + regionName;

            try {
                RegionParamEntity entity = createRegionParam(regionId, regionName, description);
                results.add(entity);
            } catch (Exception e) {
                log.warn("批量创建第{}个区域失败: {}", i, e.getMessage());
            }
        }

        log.info("批量创建完成: 成功={}, 总数={}", results.size(), count);
        return results;
    }

    /**
     * 创建区域层级结构测试数据
     */
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> createRegionHierarchy(String cityCode, String cityName) {
//        log.info("创建区域层级结构测试数据: cityCode={}, cityName={}", cityCode, cityName);
//
//        Map<String, Object> result = new HashMap<>();
//        List<RegionParamEntity> createdRegions = new ArrayList<>();
//
//        try {
//            // 1. 创建市级区域
//            RegionParamEntity cityRegion = createRegionParam(cityCode, cityName, cityName + "行政区域");
//            createdRegions.add(cityRegion);
//
//            // 2. 创建区级区域
//            String[] districts = {"01", "02", "03"};
//            String[] districtNames = {"中心区", "开发区", "新区"};
//
//            for (int i = 0; i < districts.length; i++) {
//                String districtId = cityCode + districts[i];
//                String districtName = cityName + districtNames[i];
//
//                RegionParamEntity districtRegion = createRegionParam(
//                        districtId,
//                        districtName,
//                        districtName + "行政区域"
//                );
//                createdRegions.add(districtRegion);
//
//                // 创建区域关联关系（如果需要）
//                // createRegionRelation(cityCode, districtId);
//            }
//
//            result.put("success", true);
//            result.put("message", "区域层级结构创建成功");
//            result.put("cityRegion", cityRegion);
//            result.put("allRegions", createdRegions);
//            result.put("totalCount", createdRegions.size());
//
//        } catch (Exception e) {
//            log.error("创建区域层级结构失败", e);
//            result.put("success", false);
//            result.put("message", "创建失败: " + e.getMessage());
//            result.put("createdRegions", createdRegions);
//            throw e; // 触发事务回滚
//        }
//
//        return result;
//    }
}
