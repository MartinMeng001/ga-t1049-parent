package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.CrossParamEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路口参数测试控制器
 * 按照 com.traffic.server.test 模式实现
 */
@RestController
@RequestMapping("/api/test/cross")
@Slf4j
public class TestCrossParamController {

    @Autowired
    private TestCrossParamService testCrossParamService;

    /**
     * 测试创建路口参数
     * GET /api/test/cross/create?crossId=11010180001&crossName=长安街与王府井大街交叉口&feature=24&grade=11&longitude=116.407526&latitude=39.909264&altitude=43
     /**
     * 根据位置范围查询路口
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "11010180001") String crossId,
            @RequestParam(defaultValue = "测试路口") String crossName,
            @RequestParam(defaultValue = "24") Integer feature,
            @RequestParam(defaultValue = "11") String grade,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Integer altitude,
            @RequestParam(required = false) String greenConflictMatrix) {

        Map<String, Object> response = new HashMap<>();

        try {
            CrossParamEntity created = testCrossParamService.createCrossParam(
                    crossId, crossName, feature, grade, longitude, latitude, altitude, greenConflictMatrix);

            response.put("success", true);
            response.put("message", "路口参数创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建路口成功: {}", created);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建路口失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取路口形状特征描述
     */
    private String getFeatureDescription(Integer feature) {
        switch (feature) {
            case 10: return "行人过街";
            case 12: return "2次行人过街";
            case 23: return "T形、Y形";
            case 24: return "十字形";
            case 35: return "五岔路口";
            case 36: return "六岔路口";
            case 39: return "多岔路口";
            case 40: return "环形交叉口（环岛）";
            case 50: return "匝道";
            case 51: return "匝道-入口";
            case 52: return "匝道-出口";
            case 61: return "快速路主路路段（交汇区）";
            case 90: return "其他";
            default: return "未知类型";
        }
    }

    /**
     * 获取路口等级描述
     */
    private String getGradeDescription(String grade) {
        switch (grade) {
            case "11": return "一级（主干路与主干路相交）";
            case "12": return "二级（主干路与次干路相交）";
            case "13": return "三级（主干路与支路相交）";
            case "21": return "四级（次干路与次干路相交）";
            case "22": return "五级（次干路与支路相交）";
            case "31": return "六级（支路与支路相交）";
            case "99": return "其他";
            default: return "未知等级";
        }
    }

    /**
     * 测试查询单个路口参数
     * GET /api/test/cross/get?crossId=11010180001
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGet(@RequestParam String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            CrossParamEntity entity = testCrossParamService.getCrossParam(crossId);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entity);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询路口失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询路口列表
     * GET /api/test/cross/list
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> testList() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getAllCrosses();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询路口列表失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据区域ID查询路口
     * GET /api/test/cross/by-region?regionId=110101
     */
    @GetMapping("/by-region")
    public ResponseEntity<Map<String, Object>> testFindByRegion(@RequestParam String regionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getCrossesByRegion(regionId);

            response.put("success", true);
            response.put("message", "根据区域查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("regionId", regionId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据区域查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据子区ID查询路口
     * GET /api/test/cross/by-subregion?subRegionId=11010100001
     */
    @GetMapping("/by-subregion")
    public ResponseEntity<Map<String, Object>> testFindBySubRegion(@RequestParam String subRegionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getCrossesBySubRegion(subRegionId);

            response.put("success", true);
            response.put("message", "根据子区查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("subRegionId", subRegionId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据子区查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据线路ID查询路口
     * GET /api/test/cross/by-route?routeId=110101001
     */
    @GetMapping("/by-route")
    public ResponseEntity<Map<String, Object>> testFindByRoute(@RequestParam String routeId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getCrossesByRoute(routeId);

            response.put("success", true);
            response.put("message", "根据线路查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("routeId", routeId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据线路查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据系统ID查询路口
     * GET /api/test/cross/by-system?systemId=SYS001
     */
    @GetMapping("/by-system")
    public ResponseEntity<Map<String, Object>> testFindBySystem(@RequestParam String systemId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getCrossesBySystem(systemId);

            response.put("success", true);
            response.put("message", "根据系统查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("systemId", systemId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据系统查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据路口形状特征查询
     * GET /api/test/cross/by-feature?feature=24
     */
    @GetMapping("/by-feature")
    public ResponseEntity<Map<String, Object>> testFindByFeature(@RequestParam Integer feature) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getCrossesByFeature(feature);

            response.put("success", true);
            response.put("message", "根据路口形状查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("feature", feature);
            response.put("featureDesc", getFeatureDescription(feature));
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据路口形状查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据路口等级查询
     * GET /api/test/cross/by-grade?grade=11
     */
    @GetMapping("/by-grade")
    public ResponseEntity<Map<String, Object>> testFindByGrade(@RequestParam String grade) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getCrossesByGrade(grade);

            response.put("success", true);
            response.put("message", "根据路口等级查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("grade", grade);
            response.put("gradeDesc", getGradeDescription(grade));
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据路口等级查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
    /**
     * 测试根据位置范围查询路口
     * GET /api/test/cross/by-location?minLng=116.3&maxLng=116.5&minLat=39.8&maxLat=40.0
     */
    @GetMapping("/by-location")
    public ResponseEntity<Map<String, Object>> testFindByLocation(
            @RequestParam Double minLng,
            @RequestParam Double maxLng,
            @RequestParam Double minLat,
            @RequestParam Double maxLat) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getCrossesByLocation(
                    minLng, maxLng, minLat, maxLat);

            response.put("success", true);
            response.put("message", "根据位置范围查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("locationRange", Map.of(
                    "minLng", minLng, "maxLng", maxLng,
                    "minLat", minLat, "maxLat", maxLat
            ));
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据位置范围查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询完整信息视图
     * GET /api/test/cross/complete
     */
    @GetMapping("/complete")
    public ResponseEntity<Map<String, Object>> testCompleteInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> entities = testCrossParamService.getAllWithCompleteInfo();

            response.put("success", true);
            response.put("message", "查询完整信息成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询完整信息失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试关键字分页查询
     * GET /api/test/cross/search?keyword=长安街&page=1&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> testSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> result = testCrossParamService.searchCrossesPaged(keyword, page, size);

            response.put("success", true);
            response.put("message", "分页查询成功");
            response.putAll(result);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试分页查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试更新路口参数
     * POST /api/test/cross/update?crossId=11010180001&crossName=新路口名称&feature=35&grade=12&longitude=116.408&latitude=39.910&altitude=45
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> testUpdate(
            @RequestParam String crossId,
            @RequestParam(required = false) String crossName,
            @RequestParam(required = false) Integer feature,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Integer altitude,
            @RequestParam(required = false) String greenConflictMatrix) {

        Map<String, Object> response = new HashMap<>();

        try {
            CrossParamEntity updated = testCrossParamService.updateCrossParam(
                    crossId, crossName, feature, grade, longitude, latitude, altitude, greenConflictMatrix);

            response.put("success", true);
            response.put("message", "路口参数更新成功");
            response.put("data", updated);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试更新路口成功: {}", updated);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试更新路口失败", e);

            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试删除路口参数
     * DELETE /api/test/cross/delete?crossId=11010180001
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> testDelete(@RequestParam String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            testCrossParamService.deleteCrossParam(crossId);

            response.put("success", true);
            response.put("message", "路口参数删除成功");
            response.put("crossId", crossId);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试删除路口成功: {}", crossId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试删除路口失败", e);

            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试批量创建路口
     * POST /api/test/cross/batch-create?regionPrefix=110101&count=5
     */
    @PostMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "110101") String regionPrefix,
            @RequestParam(defaultValue = "3") Integer count) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<CrossParamEntity> created = testCrossParamService.createBatchCrosses(regionPrefix, count);

            response.put("success", true);
            response.put("message", "批量创建路口成功");
            response.put("data", created);
            response.put("count", created.size());
            response.put("regionPrefix", regionPrefix);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试批量创建路口成功: count={}", created.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试批量创建路口失败", e);

            response.put("success", false);
            response.put("message", "批量创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试创建路网层级结构
     * POST /api/test/cross/network?regionId=110101&regionName=东城区
     */
    @PostMapping("/network")
    public ResponseEntity<Map<String, Object>> testCreateNetwork(
            @RequestParam String regionId,
            @RequestParam String regionName) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> result = testCrossParamService.createCrossNetwork(regionId, regionName);

            response.put("success", true);
            response.put("message", "创建路网结构成功");
            response.putAll(result);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建路网结构成功: regionId={}", regionId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建路网结构失败", e);

            response.put("success", false);
            response.put("message", "创建路网结构失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
}
