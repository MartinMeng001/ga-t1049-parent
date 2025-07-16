package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.SubRegionParamEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 子区参数测试控制器
 * 按照 com.traffic.server.test 模式实现
 */
@RestController
@RequestMapping("/api/test/subregion")
@Slf4j
public class TestSubRegionParamController {

    @Autowired
    private TestSubRegionParamService testSubRegionParamService;

    /**
     * 测试创建子区参数
     * GET /api/test/subregion/create?subRegionId=11010100001&subRegionName=东城区中心子区
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "11010100001") String subRegionId,
            @RequestParam(defaultValue = "测试子区") String subRegionName) {

        Map<String, Object> response = new HashMap<>();

        try {
            SubRegionParamEntity created = testSubRegionParamService.createSubRegionParam(subRegionId, subRegionName);

            response.put("success", true);
            response.put("message", "子区参数创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建子区成功: {}", created);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建子区失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询单个子区参数
     * GET /api/test/subregion/get?subRegionId=11010100001
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGet(@RequestParam String subRegionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            SubRegionParamEntity entity = testSubRegionParamService.getSubRegionParam(subRegionId);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entity);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询子区失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询子区列表
     * GET /api/test/subregion/list
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> testList() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SubRegionParamEntity> entities = testSubRegionParamService.getAllSubRegions();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询子区列表失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据区域ID查询子区
     * GET /api/test/subregion/by-region?regionId=110101
     */
    @GetMapping("/by-region")
    public ResponseEntity<Map<String, Object>> testFindByRegion(@RequestParam String regionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SubRegionParamEntity> entities = testSubRegionParamService.getSubRegionsByRegion(regionId);

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
     * 测试模糊查询子区名称
     * GET /api/test/subregion/search?subRegionName=东城
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> testSearchByName(@RequestParam String subRegionName) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SubRegionParamEntity> entities = testSubRegionParamService.searchSubRegionsByName(subRegionName);

            response.put("success", true);
            response.put("message", "模糊查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("searchTerm", subRegionName);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试模糊查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询完整信息视图
     * GET /api/test/subregion/complete
     */
    @GetMapping("/complete")
    public ResponseEntity<Map<String, Object>> testCompleteInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SubRegionParamEntity> entities = testSubRegionParamService.getAllWithCompleteInfo();

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
     * 测试更新子区参数
     * POST /api/test/subregion/update?subRegionId=11010100001&subRegionName=新子区名称
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> testUpdate(
            @RequestParam String subRegionId,
            @RequestParam(required = false) String subRegionName) {

        Map<String, Object> response = new HashMap<>();

        try {
            SubRegionParamEntity updated = testSubRegionParamService.updateSubRegionParam(subRegionId, subRegionName);

            response.put("success", true);
            response.put("message", "子区参数更新成功");
            response.put("data", updated);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试更新子区成功: {}", updated);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试更新子区失败", e);

            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试删除子区参数
     * DELETE /api/test/subregion/delete?subRegionId=11010100001
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> testDelete(@RequestParam String subRegionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            testSubRegionParamService.deleteSubRegionParam(subRegionId);

            response.put("success", true);
            response.put("message", "子区参数删除成功");
            response.put("subRegionId", subRegionId);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试删除子区成功: {}", subRegionId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试删除子区失败", e);

            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试批量创建子区
     * POST /api/test/subregion/batch-create?regionPrefix=1101010&count=5
     */
    @PostMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "1101010") String regionPrefix,
            @RequestParam(defaultValue = "3") Integer count) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<SubRegionParamEntity> created = testSubRegionParamService.createBatchSubRegions(regionPrefix, count);

            response.put("success", true);
            response.put("message", "批量创建子区成功");
            response.put("data", created);
            response.put("count", created.size());
            response.put("regionPrefix", regionPrefix);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试批量创建子区成功: count={}", created.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试批量创建子区失败", e);

            response.put("success", false);
            response.put("message", "批量创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试创建区域层级子区结构
     * POST /api/test/subregion/hierarchy?regionId=110101&regionName=东城区
     */
    @PostMapping("/hierarchy")
    public ResponseEntity<Map<String, Object>> testCreateHierarchy(
            @RequestParam String regionId,
            @RequestParam String regionName) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> result = testSubRegionParamService.createSubRegionHierarchy(regionId, regionName);

            response.put("success", true);
            response.put("message", "创建子区层级结构成功");
            response.putAll(result);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建子区层级结构成功: regionId={}", regionId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建子区层级结构失败", e);

            response.put("success", false);
            response.put("message", "创建层级结构失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
}
