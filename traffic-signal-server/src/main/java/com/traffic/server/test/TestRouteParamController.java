package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.RouteParamEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线路参数测试控制器
 * 按照 com.traffic.server.test 模式实现
 */
@RestController
@RequestMapping("/api/test/route")
@Slf4j
public class TestRouteParamController {

    @Autowired
    private TestRouteParamService testRouteParamService;

    /**
     * 测试创建线路参数
     * GET /api/test/route/create?routeId=110101001&routeName=长安街干线&type=1
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "110101001") String routeId,
            @RequestParam(defaultValue = "测试干线") String routeName,
            @RequestParam(defaultValue = "1") Integer type) {

        Map<String, Object> response = new HashMap<>();

        try {
            RouteParamEntity created = testRouteParamService.createRouteParam(routeId, routeName, type);

            response.put("success", true);
            response.put("message", "线路参数创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建线路成功: {}", created);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建线路失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询单个线路参数
     * GET /api/test/route/get?routeId=110101001
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGet(@RequestParam String routeId) {
        Map<String, Object> response = new HashMap<>();

        try {
            RouteParamEntity entity = testRouteParamService.getRouteParam(routeId);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entity);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询线路失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询线路列表
     * GET /api/test/route/list
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> testList() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RouteParamEntity> entities = testRouteParamService.getAllRoutes();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询线路列表失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据类型查询线路
     * GET /api/test/route/by-type?type=1
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Object>> testFindByType(@RequestParam Integer type) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RouteParamEntity> entities = testRouteParamService.getRoutesByType(type);

            response.put("success", true);
            response.put("message", "根据类型查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("type", type);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据类型查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据子区ID查询线路
     * GET /api/test/route/by-subregion?subRegionId=11010101
     */
    @GetMapping("/by-subregion")
    public ResponseEntity<Map<String, Object>> testFindBySubRegion(@RequestParam String subRegionId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RouteParamEntity> entities = testRouteParamService.getRoutesBySubRegion(subRegionId);

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
     * 测试模糊查询线路名称
     * GET /api/test/route/search?routeName=长安街
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> testSearchByName(@RequestParam String routeName) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RouteParamEntity> entities = testRouteParamService.searchRoutesByName(routeName);

            response.put("success", true);
            response.put("message", "模糊查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("searchTerm", routeName);
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
     * GET /api/test/route/complete
     */
    @GetMapping("/complete")
    public ResponseEntity<Map<String, Object>> testCompleteInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RouteParamEntity> entities = testRouteParamService.getAllWithCompleteInfo();

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
     * 测试更新线路参数
     * POST /api/test/route/update?routeId=110101001&routeName=新线路名称&type=2
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> testUpdate(
            @RequestParam String routeId,
            @RequestParam(required = false) String routeName,
            @RequestParam(required = false) Integer type) {

        Map<String, Object> response = new HashMap<>();

        try {
            RouteParamEntity updated = testRouteParamService.updateRouteParam(routeId, routeName, type);

            response.put("success", true);
            response.put("message", "线路参数更新成功");
            response.put("data", updated);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试更新线路成功: {}", updated);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试更新线路失败", e);

            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试删除线路参数
     * DELETE /api/test/route/delete?routeId=110101001
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> testDelete(@RequestParam String routeId) {
        Map<String, Object> response = new HashMap<>();

        try {
            testRouteParamService.deleteRouteParam(routeId);

            response.put("success", true);
            response.put("message", "线路参数删除成功");
            response.put("routeId", routeId);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试删除线路成功: {}", routeId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试删除线路失败", e);

            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
}
