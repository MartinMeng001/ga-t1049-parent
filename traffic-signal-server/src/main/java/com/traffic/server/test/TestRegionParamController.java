package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.RegionParamEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域参数测试控制器
 * 放置位置：traffic-signal-server/src/main/java/com/traffic/server/controller/TestRegionParamController.java
 */
@RestController
@RequestMapping("/api/test/region")
@Slf4j
public class TestRegionParamController {

    @Autowired
    private TestRegionParamService testRegionParamService;

    /**
     * 测试创建区域参数
     * GET /api/test/region/create?regionId=110101&regionName=东城区&description=北京市东城区
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "110101") String regionId,
            @RequestParam(defaultValue = "测试区域") String regionName,
            @RequestParam(defaultValue = "测试创建的区域") String description) {

        Map<String, Object> response = new HashMap<>();

        try {
            RegionParamEntity created = testRegionParamService.createRegionParam(regionId, regionName, description);

            response.put("success", true);
            response.put("message", "区域参数创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建成功: {}", created);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试更新区域参数
     * GET /api/test/region/update?regionId=110101&regionName=更新的东城区&description=更新后的描述
     */
    @GetMapping("/update")
    public ResponseEntity<Map<String, Object>> testUpdate(
            @RequestParam String regionId,
            @RequestParam String regionName,
            @RequestParam String description) {

        Map<String, Object> response = new HashMap<>();

        try {
            RegionParamEntity updated = testRegionParamService.updateRegionParam(regionId, regionName, description);

            response.put("success", true);
            response.put("message", "区域参数更新成功");
            response.put("data", updated);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试更新失败", e);

            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询单个区域参数
     * GET /api/test/region/get?regionId=110101
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGet(@RequestParam String regionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            RegionParamEntity entity = testRegionParamService.getRegionParam(regionId);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entity);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据名称模糊查询
     * GET /api/test/region/search?regionName=城区
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> testSearchByName(@RequestParam String regionName) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RegionParamEntity> entities = testRegionParamService.searchRegionsByName(regionName);

            response.put("success", true);
            response.put("message", "模糊查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("searchKeyword", regionName);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("模糊查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据ID前缀查询
     * GET /api/test/region/by-prefix?prefix=1101
     */
    @GetMapping("/by-prefix")
    public ResponseEntity<Map<String, Object>> testGetByPrefix(@RequestParam String prefix) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RegionParamEntity> entities = testRegionParamService.getRegionsByPrefix(prefix);

            response.put("success", true);
            response.put("message", "前缀查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("prefix", prefix);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("前缀查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询完整信息
     * GET /api/test/region/complete-info
     */
    @GetMapping("/complete-info")
    public ResponseEntity<Map<String, Object>> testGetCompleteInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RegionParamEntity> entities = testRegionParamService.getAllRegionsWithCompleteInfo();

            response.put("success", true);
            response.put("message", "完整信息查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("完整信息查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试批量创建区域
     * GET /api/test/region/batch-create?cityPrefix=1101&count=5
     */
    @GetMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "1101") String cityPrefix,
            @RequestParam(defaultValue = "5") int count) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (count > 10) {
                count = 10; // 限制最大批量数量
            }

            List<RegionParamEntity> created = testRegionParamService.createBatchTestRegions(cityPrefix, count);

            response.put("success", true);
            response.put("message", "批量创建成功");
            response.put("data", created);
            response.put("requestCount", count);
            response.put("successCount", created.size());
            response.put("cityPrefix", cityPrefix);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("批量创建失败", e);

            response.put("success", false);
            response.put("message", "批量创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试创建区域层级结构
     * GET /api/test/region/create-hierarchy?cityCode=1101&cityName=北京市
     */
//    @GetMapping("/create-hierarchy")
//    public ResponseEntity<Map<String, Object>> testCreateHierarchy(
//            @RequestParam(defaultValue = "1101") String cityCode,
//            @RequestParam(defaultValue = "测试市") String cityName) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Map<String, Object> result = testRegionParamService.createRegionHierarchy(cityCode, cityName);
//
//            response.putAll(result);
//            response.put("timestamp", LocalDateTime.now());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("创建层级结构失败", e);
//
//            response.put("success", false);
//            response.put("message", "创建失败: " + e.getMessage());
//            response.put("timestamp", LocalDateTime.now());
//
//            return ResponseEntity.ok(response);
//        }
//    }

    /**
     * 完整测试流程
     * GET /api/test/region/full-test
     */
    @GetMapping("/full-test")
    public ResponseEntity<Map<String, Object>> fullTest() {
        Map<String, Object> response = new HashMap<>();
        List<String> steps = new ArrayList<>();

        try {
            String testRegionId = "TEST_" + System.currentTimeMillis();

            // 1. 创建测试
            steps.add("步骤1: 创建区域参数");
            RegionParamEntity created = testRegionParamService.createRegionParam(
                    testRegionId,
                    "全流程测试区域",
                    "自动化测试创建的区域"
            );
            steps.add("创建成功: " + created.getRegionId());

            // 2. 查询测试
            steps.add("步骤2: 查询区域参数");
            RegionParamEntity queried = testRegionParamService.getRegionParam(testRegionId);
            steps.add("查询成功: " + queried.getRegionName());

            // 3. 更新测试
            steps.add("步骤3: 更新区域参数");
            RegionParamEntity updated = testRegionParamService.updateRegionParam(
                    testRegionId,
                    "更新后的测试区域",
                    "更新后的描述信息"
            );
            steps.add("更新成功: " + updated.getRegionName());

            // 4. 模糊查询测试
            steps.add("步骤4: 模糊查询测试");
            List<RegionParamEntity> searchResult = testRegionParamService.searchRegionsByName("测试");
            steps.add("模糊查询成功，找到 " + searchResult.size() + " 条记录");

            // 5. 前缀查询测试
            steps.add("步骤5: 前缀查询测试");
            List<RegionParamEntity> prefixResult = testRegionParamService.getRegionsByPrefix("TEST");
            steps.add("前缀查询成功，找到 " + prefixResult.size() + " 条记录");

            // 6. 完整信息查询测试
            steps.add("步骤6: 完整信息查询");
            List<RegionParamEntity> completeInfo = testRegionParamService.getAllRegionsWithCompleteInfo();
            steps.add("完整信息查询成功，共 " + completeInfo.size() + " 条记录");

            // 7. 删除测试
            steps.add("步骤7: 删除区域参数");
            testRegionParamService.deleteRegionParam(testRegionId);
            steps.add("删除成功");

            response.put("success", true);
            response.put("message", "区域参数完整测试流程执行成功");
            response.put("steps", steps);
            response.put("testRegionId", testRegionId);
            response.put("repositoryType", "MyBatis Plus + @Select");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("完整测试失败", e);
            steps.add("测试失败: " + e.getMessage());

            response.put("success", false);
            response.put("message", "测试失败: " + e.getMessage());
            response.put("steps", steps);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
}
