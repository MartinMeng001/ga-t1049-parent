package com.traffic.server.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traffic.gat1049.repository.entity.CrossParamEntity;
import com.traffic.gat1049.repository.interfaces.CrossParamRepository;
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
 * 路口参数测试服务
 */
@Service
@Transactional
@Slf4j
public class TestCrossParamService {

    @Autowired
    private CrossParamRepository crossParamRepository;

    /**
     * 创建路口参数
     */
    public CrossParamEntity createCrossParam(String crossId, String crossName, Integer feature, String grade,
                                             Double longitude, Double latitude, Integer altitude, String greenConflictMatrix) {
        // 检查是否已存在
        CrossParamEntity existing = crossParamRepository.findByCrossId(crossId);
        if (existing != null) {
            throw new RuntimeException("路口参数已存在: " + crossId);
        }

        // 验证必填字段
        if (feature == null) {
            throw new IllegalArgumentException("路口形状特征不能为空");
        }
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("路口等级不能为空");
        }

        CrossParamEntity entity = new CrossParamEntity();
        entity.setCrossId(crossId);
        entity.setCrossName(crossName);
        entity.setFeature(feature);
        entity.setGrade(grade);
        entity.setLongitude(longitude);
        entity.setLatitude(latitude);
        entity.setAltitude(altitude);
        entity.setGreenConflictMatrix(greenConflictMatrix);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        crossParamRepository.insert(entity);
        log.info("创建路口参数: crossId={}, crossName={}, feature={}, grade={}",
                crossId, crossName, feature, grade);

        return entity;
    }

    /**
     * 根据ID查询路口参数
     */
    public CrossParamEntity getCrossParam(String crossId) {
        CrossParamEntity entity = crossParamRepository.findByCrossId(crossId);

        if (entity == null) {
            throw new RuntimeException("未找到路口参数: " + crossId);
        }

        log.info("查询路口参数: crossId={}", crossId);
        return entity;
    }

    /**
     * 查询所有路口参数
     */
    public List<CrossParamEntity> getAllCrosses() {
        List<CrossParamEntity> entities = crossParamRepository.selectList(null);
        log.info("查询所有路口参数，共 {} 条", entities.size());
        return entities;
    }

    /**
     * 根据区域ID查询路口
     */
    public List<CrossParamEntity> getCrossesByRegion(String regionId) {
        List<CrossParamEntity> entities = crossParamRepository.findByRegionId(regionId);
        log.info("根据区域查询路口: regionId={}, 共 {} 条", regionId, entities.size());
        return entities;
    }

    /**
     * 根据子区ID查询路口
     */
    public List<CrossParamEntity> getCrossesBySubRegion(String subRegionId) {
        List<CrossParamEntity> entities = crossParamRepository.findBySubRegionId(subRegionId);
        log.info("根据子区查询路口: subRegionId={}, 共 {} 条", subRegionId, entities.size());
        return entities;
    }

    /**
     * 根据线路ID查询路口
     */
    public List<CrossParamEntity> getCrossesByRoute(String routeId) {
        List<CrossParamEntity> entities = crossParamRepository.findByRouteIdOrderBySeq(routeId);
        log.info("根据线路查询路口: routeId={}, 共 {} 条", routeId, entities.size());
        return entities;
    }

    /**
     * 根据系统ID查询路口
     */
    public List<CrossParamEntity> getCrossesBySystem(String systemId) {
        List<CrossParamEntity> entities = crossParamRepository.findBySystemId(systemId);
        log.info("根据系统查询路口: systemId={}, 共 {} 条", systemId, entities.size());
        return entities;
    }

    /**
     * 根据路口形状特征查询
     */
    public List<CrossParamEntity> getCrossesByFeature(Integer feature) {
        List<CrossParamEntity> entities = crossParamRepository.findByFeature(feature);
        log.info("根据路口形状查询: feature={}, 共 {} 条", feature, entities.size());
        return entities;
    }

    /**
     * 根据路口等级查询
     */
    public List<CrossParamEntity> getCrossesByGrade(String grade) {
        List<CrossParamEntity> entities = crossParamRepository.findByGrade(grade);
        log.info("根据路口等级查询: grade={}, 共 {} 条", grade, entities.size());
        return entities;
    }

    public List<CrossParamEntity> getCrossesByLocation(Double minLng, Double maxLng,
                                                       Double minLat, Double maxLat) {
        List<CrossParamEntity> entities = crossParamRepository.findByLocation(minLng, maxLng, minLat, maxLat);
        log.info("根据位置范围查询路口: 经度[{}-{}], 纬度[{}-{}], 共 {} 条",
                minLng, maxLng, minLat, maxLat, entities.size());
        return entities;
    }

    /**
     * 查询完整信息视图
     */
    public List<CrossParamEntity> getAllWithCompleteInfo() {
        List<CrossParamEntity> entities = crossParamRepository.findAllWithCompleteInfo();
        log.info("查询完整信息视图，共 {} 条", entities.size());
        return entities;
    }

    /**
     * 关键字分页查询
     */
    public Map<String, Object> searchCrossesPaged(String keyword, Integer page, Integer size) {
        Page<CrossParamEntity> pageObj = new Page<>(page, size);
        IPage<CrossParamEntity> result = crossParamRepository.findByKeywordPaged(pageObj, keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("data", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("size", result.getSize());
        response.put("pages", result.getPages());
        response.put("keyword", keyword);

        log.info("关键字分页查询: keyword={}, page={}, size={}, total={}",
                keyword, page, size, result.getTotal());
        return response;
    }

    /**
     * 更新路口参数
     */
    public CrossParamEntity updateCrossParam(String crossId, String crossName, Integer feature, String grade,
                                             Double longitude, Double latitude, Integer altitude, String greenConflictMatrix) {
        CrossParamEntity existing = getCrossParam(crossId);

        if (crossName != null && !crossName.trim().isEmpty()) {
            existing.setCrossName(crossName);
        }
        if (feature != null) {
            existing.setFeature(feature);
        }
        if (grade != null && !grade.trim().isEmpty()) {
            existing.setGrade(grade);
        }
        if (longitude != null) {
            existing.setLongitude(longitude);
        }
        if (latitude != null) {
            existing.setLatitude(latitude);
        }
        if (altitude != null) {
            existing.setAltitude(altitude);
        }
        if (greenConflictMatrix != null) {
            existing.setGreenConflictMatrix(greenConflictMatrix);
        }
        existing.setUpdatedTime(LocalDateTime.now());

        crossParamRepository.updateById(existing);
        log.info("更新路口参数: crossId={}", crossId);

        return existing;
    }

    /**
     * 删除路口参数
     */
    public void deleteCrossParam(String crossId) {
        CrossParamEntity existing = getCrossParam(crossId);
        crossParamRepository.deleteById(crossId);
        log.info("删除路口参数: crossId={}", crossId);
    }

    /**
     * 批量创建测试路口数据
     */
    public List<CrossParamEntity> createBatchCrosses(String regionPrefix, int count) {
        log.info("批量创建测试路口数据: regionPrefix={}, count={}", regionPrefix, count);

        List<CrossParamEntity> results = new ArrayList<>();
        String[] crossNames = {
                "主干道交叉口", "商业中心路口", "住宅区路口", "学校路口", "医院路口",
                "公园路口", "工业区路口", "科技园路口", "政务中心路口", "交通枢纽路口"
        };
        Integer[] features = {24, 24, 23, 24, 23, 24, 35, 24, 40, 24}; // 对应不同路口形状
        String[] grades = {"11", "12", "22", "22", "12", "22", "13", "11", "11", "11"}; // 对应不同等级

        // 北京市中心区域的经纬度范围
        double baseLng = 116.407526; // 天安门经度
        double baseLat = 39.909264;  // 天安门纬度

        for (int i = 1; i <= count && i <= crossNames.length; i++) {
            String crossId = regionPrefix + "80" + String.format("%04d", i);
            String crossName = crossNames[i - 1];
            Integer feature = features[i - 1];
            String grade = grades[i - 1];
            Double longitude = baseLng + (i - 1) * 0.01; // 模拟不同位置
            Double latitude = baseLat + (i - 1) * 0.005;
            Integer altitude = 43 + i; // 北京海拔约43米
            String greenMatrix = generateSimpleGreenMatrix(feature);

            try {
                CrossParamEntity entity = createCrossParam(crossId, crossName, feature, grade,
                        longitude, latitude, altitude, greenMatrix);
                results.add(entity);
            } catch (Exception e) {
                log.warn("批量创建第{}个路口失败: {}", i, e.getMessage());
            }
        }

        log.info("批量创建完成: 成功={}, 总数={}", results.size(), count);
        return results;
    }

    /**
     * 创建路网结构测试数据
     */
    public Map<String, Object> createCrossNetwork(String regionId, String regionName) {
        log.info("创建路网结构测试数据: regionId={}, regionName={}", regionId, regionName);

        Map<String, Object> result = new HashMap<>();
        List<CrossParamEntity> createdCrosses = new ArrayList<>();

        try {
            // 1. 创建主干道路口
            String[] mainRoads = {"001", "002", "003"};
            String[] mainRoadNames = {"主干道东西向路口", "主干道南北向路口", "主干道环线路口"};
            double[] mainLngs = {116.407526, 116.417526, 116.427526};
            double[] mainLats = {39.909264, 39.919264, 39.929264};
            Integer[] mainFeatures = {24, 24, 40}; // 十字形、十字形、环形
            String[] mainGrades = {"11", "11", "11"}; // 一级路口

            for (int i = 0; i < mainRoads.length; i++) {
                String crossId = regionId + "80" + mainRoads[i];
                String crossName = regionName + mainRoadNames[i];

                try {
                    CrossParamEntity cross = createCrossParam(
                            crossId, crossName, mainFeatures[i], mainGrades[i],
                            mainLngs[i], mainLats[i], 43 + i,
                            generateSimpleGreenMatrix(mainFeatures[i]));
                    createdCrosses.add(cross);
                } catch (Exception e) {
                    log.warn("创建主干道路口失败: {}", e.getMessage());
                }
            }

            // 2. 创建次干道路口
            String[] secondaryRoads = {"011", "012", "013", "014"};
            String[] secondaryNames = {"次干道商业路口", "次干道住宅路口", "次干道学校路口", "次干道医院路口"};
            Integer[] secondaryFeatures = {24, 23, 24, 23}; // 十字形、T形、十字形、T形
            String[] secondaryGrades = {"12", "22", "22", "12"}; // 二级、五级、五级、二级

            for (int i = 0; i < secondaryRoads.length; i++) {
                String crossId = regionId + "80" + secondaryRoads[i];
                String crossName = regionName + secondaryNames[i];
                Double longitude = 116.407526 + (i + 1) * 0.008;
                Double latitude = 39.909264 + (i + 1) * 0.004;

                try {
                    CrossParamEntity cross = createCrossParam(
                            crossId, crossName, secondaryFeatures[i], secondaryGrades[i],
                            longitude, latitude, 45 + i,
                            generateSimpleGreenMatrix(secondaryFeatures[i]));
                    createdCrosses.add(cross);
                } catch (Exception e) {
                    log.warn("创建次干道路口失败: {}", e.getMessage());
                }
            }

            // 3. 创建支路路口
            String[] branchRoads = {"021", "022", "023"};
            String[] branchNames = {"支路社区路口", "支路停车场路口", "支路出入口"};
            Integer[] branchFeatures = {23, 23, 50}; // T形、T形、匝道
            String[] branchGrades = {"31", "31", "99"}; // 六级、六级、其他

            for (int i = 0; i < branchRoads.length; i++) {
                String crossId = regionId + "80" + branchRoads[i];
                String crossName = regionName + branchNames[i];
                Double longitude = 116.407526 + (i + 1) * 0.005;
                Double latitude = 39.909264 - (i + 1) * 0.003;

                try {
                    CrossParamEntity cross = createCrossParam(
                            crossId, crossName, branchFeatures[i], branchGrades[i],
                            longitude, latitude, 42 + i,
                            generateSimpleGreenMatrix(branchFeatures[i]));
                    createdCrosses.add(cross);
                } catch (Exception e) {
                    log.warn("创建支路路口失败: {}", e.getMessage());
                }
            }

            result.put("success", true);
            result.put("message", "路网结构创建成功");
            result.put("regionId", regionId);
            result.put("regionName", regionName);
            result.put("mainRoadCrosses", createdCrosses.subList(0, Math.min(3, createdCrosses.size())));
            result.put("secondaryRoadCrosses", createdCrosses.subList(Math.min(3, createdCrosses.size()),
                    Math.min(7, createdCrosses.size())));
            result.put("branchRoadCrosses", createdCrosses.subList(Math.min(7, createdCrosses.size()),
                    createdCrosses.size()));
            result.put("allCrosses", createdCrosses);
            result.put("totalCount", createdCrosses.size());

        } catch (Exception e) {
            log.error("创建路网结构失败", e);
            result.put("success", false);
            result.put("message", "创建失败: " + e.getMessage());
            result.put("createdCrosses", createdCrosses);
            throw e; // 触发事务回滚
        }

        return result;
    }

    /**
     * 生成简单的绿冲突矩阵
     */
    private String generateSimpleGreenMatrix(Integer feature) {
        switch (feature) {
            case 24: // 十字形路口
                return "[[0,1,0,1],[1,0,1,0],[0,1,0,1],[1,0,1,0]]";
            case 23: // T形路口
                return "[[0,1,0],[1,0,1],[0,1,0]]";
            case 40: // 环形路口
                return "[[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0]]";
            case 35: // 五岔路口
                return "[[0,1,0,1,0],[1,0,1,0,1],[0,1,0,1,0],[1,0,1,0,1],[0,1,0,1,0]]";
            default:
                return "[[0,1],[1,0]]"; // 简单冲突矩阵
        }
    }
}
