package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.LampGroupParamEntity;
import com.traffic.gat1049.repository.interfaces.LampGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信号灯组参数测试控制器
 * 演示 LampGroupRepository 的各种功能
 *
 * 访问路径: /api/test/lampgroup/*
 */
@RestController
@RequestMapping("/api/test/lampgroup")
@Slf4j
public class TestLampGroupController {

    @Autowired
    private TestLampGroupService testLampGroupService;

    /**
     * 测试创建信号灯组
     * GET /api/test/lampgroup/create?crossId=11010000100001&lampGroupNo=1&direction=E&type=01
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "11010000100001") String crossId,
            @RequestParam(defaultValue = "1") Integer lampGroupNo,
            @RequestParam(defaultValue = "E") String direction,
            @RequestParam(defaultValue = "01") String type) {

        Map<String, Object> response = new HashMap<>();

        try {
            LampGroupParamEntity created = testLampGroupService.createLampGroup(crossId, lampGroupNo, direction, type);

            response.put("success", true);
            response.put("message", "信号灯组创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建成功: crossId={}, lampGroupNo={}", crossId, lampGroupNo);
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
     * 测试查询单个信号灯组
     * GET /api/test/lampgroup/get?crossId=11010000100001&lampGroupNo=1
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGet(
            @RequestParam String crossId,
            @RequestParam Integer lampGroupNo) {

        Map<String, Object> response = new HashMap<>();

        try {
            LampGroupParamEntity entity = testLampGroupService.getLampGroup(crossId, lampGroupNo);

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
     * 测试查询路口所有信号灯组
     * GET /api/test/lampgroup/list?crossId=11010000100001
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> testList(@RequestParam String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<LampGroupParamEntity> entities = testLampGroupService.getLampGroupsByCrossId(crossId);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("crossId", crossId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询列表失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据方向查询信号灯组
     * GET /api/test/lampgroup/by-direction?crossId=11010000100001&direction=E
     */
    @GetMapping("/by-direction")
    public ResponseEntity<Map<String, Object>> testByDirection(
            @RequestParam String crossId,
            @RequestParam String direction) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LampGroupParamEntity> entities = testLampGroupService.getLampGroupsByDirection(crossId, direction);

            response.put("success", true);
            response.put("message", "按方向查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("crossId", crossId);
            response.put("direction", direction);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试按方向查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据类型查询信号灯组
     * GET /api/test/lampgroup/by-type?crossId=11010000100001&type=01
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Object>> testByType(
            @RequestParam String crossId,
            @RequestParam String type) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LampGroupParamEntity> entities = testLampGroupService.getLampGroupsByType(crossId, type);

            response.put("success", true);
            response.put("message", "按类型查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("crossId", crossId);
            response.put("type", type);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试按类型查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试批量创建信号灯组
     * GET /api/test/lampgroup/batch-create?crossId=11010000100001&count=4
     */
    @GetMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "11010000100001") String crossId,
            @RequestParam(defaultValue = "4") int count) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (count > 10) {
                count = 10; // 限制最大批量数量
            }

            List<LampGroupParamEntity> created = testLampGroupService.createBatchLampGroups(crossId, count);

            response.put("success", true);
            response.put("message", "批量创建成功");
            response.put("data", created);
            response.put("crossId", crossId);
            response.put("requestCount", count);
            response.put("successCount", created.size());
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
     * 测试统计功能
     * GET /api/test/lampgroup/statistics?crossId=11010000100001
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> testStatistics(@RequestParam String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> stats = testLampGroupService.getStatistics(crossId);

            response.put("success", true);
            response.put("message", "统计查询成功");
            response.put("data", stats);
            response.put("crossId", crossId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("统计查询失败", e);

            response.put("success", false);
            response.put("message", "统计查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试更新信号灯组
     * PUT /api/test/lampgroup/update?crossId=11010000100001&lampGroupNo=1&direction=W&type=02
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> testUpdate(
            @RequestParam String crossId,
            @RequestParam Integer lampGroupNo,
            @RequestParam String direction,
            @RequestParam String type) {

        Map<String, Object> response = new HashMap<>();

        try {
            LampGroupParamEntity updated = testLampGroupService.updateLampGroup(crossId, lampGroupNo, direction, type);

            response.put("success", true);
            response.put("message", "更新成功");
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
     * 测试删除路口所有信号灯组
     * DELETE /api/test/lampgroup/delete-all?crossId=11010000100001
     */
    @DeleteMapping("/delete-all")
    public ResponseEntity<Map<String, Object>> testDeleteAll(@RequestParam String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            int deletedCount = testLampGroupService.deleteAllByCrossId(crossId);

            response.put("success", true);
            response.put("message", "删除成功");
            response.put("deletedCount", deletedCount);
            response.put("crossId", crossId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试删除失败", e);

            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 完整测试流程
     * GET /api/test/lampgroup/full-test?crossId=11010000100001
     */
//    @GetMapping("/full-test")
//    public ResponseEntity<Map<String, Object>> fullTest(
//            @RequestParam(defaultValue = "FULL_TEST_" + System.currentTimeMillis()) String crossId) {
//
//        Map<String, Object> response = new HashMap<>();
//        List<String> steps = new ArrayList<>();
//
//        try {
//            // 1. 批量创建测试
//            steps.add("步骤1: 批量创建信号灯组");
//            List<LampGroupParamEntity> created = testLampGroupService.createBatchLampGroups(crossId, 6);
//            steps.add("创建成功: 共" + created.size() + "个信号灯组");
//
//            // 2. 查询测试
//            steps.add("步骤2: 查询路口所有信号灯组");
//            List<LampGroupParamEntity> allLampGroups = testLampGroupService.getLampGroupsByCrossId(crossId);
//            steps.add("查询成功: 共" + allLampGroups.size() + "个信号灯组");
//
//            // 3. 方向查询测试
//            steps.add("步骤3: 按方向查询信号灯组");
//            List<LampGroupParamEntity> eastLampGroups = testLampGroupService.getLampGroupsByDirection(crossId, "E");
//            steps.add("东向信号灯组: " + eastLampGroups.size() + "个");
//
//            // 4. 类型查询测试
//            steps.add("步骤4: 按类型查询信号灯组");
//            List<LampGroupParamEntity> motorLampGroups = testLampGroupService.getLampGroupsByType(crossId, "01");
//            steps.add("机动车信号灯组: " + motorLampGroups.size() + "个");
//
//            // 5. 统计查询测试
//            steps.add("步骤5: 统计信息查询");
//            Map<String, Object> statistics = testLampGroupService.getStatistics(crossId);
//            steps.add("统计信息: 总数=" + statistics.get("totalCount") + ", 最大编号=" + statistics.get("maxLampGroupNo"));
//
//            // 6. 更新测试
//            if (!allLampGroups.isEmpty()) {
//                steps.add("步骤6: 更新信号灯组");
//                LampGroupParamEntity first = allLampGroups.get(0);
//                LampGroupParamEntity updated = testLampGroupService.updateLampGroup(
//                        first.getCrossId(), first.getLampGroupNo(), "S", "02");
//                steps.add("更新成功: 方向=" + updated.getDirection() + ", 类型=" + updated.getType());
//            }
//
//            // 7. 删除测试
//            steps.add("步骤7: 删除所有信号灯组");
//            int deletedCount = testLampGroupService.deleteAllByCrossId(crossId);
//            steps.add("删除成功: " + deletedCount + "个");
//
//            response.put("success", true);
//            response.put("message", "完整测试流程执行成功");
//            response.put("steps", steps);
//            response.put("testCrossId", crossId);
//            response.put("repositoryType", "MyBatis Plus + @Select/@Insert/@Update/@Delete");
//            response.put("timestamp", LocalDateTime.now());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("完整测试失败", e);
//            steps.add("测试失败: " + e.getMessage());
//
//            response.put("success", false);
//            response.put("message", "测试失败: " + e.getMessage());
//            response.put("steps", steps);
//            response.put("timestamp", LocalDateTime.now());
//
//            return ResponseEntity.ok(response);
//        }
//    }

    /**
     * 查询数据字典
     * GET /api/test/lampgroup/dictionary
     */
    @GetMapping("/dictionary")
    public ResponseEntity<Map<String, Object>> getDictionary() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> dictionary = testLampGroupService.getDictionary();

            response.put("success", true);
            response.put("message", "数据字典查询成功");
            response.put("data", dictionary);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("数据字典查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
}
