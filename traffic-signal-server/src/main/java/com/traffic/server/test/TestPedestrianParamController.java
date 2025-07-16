package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.PedestrianParamEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人行横道参数测试控制器
 * 按照 com.traffic.server.test 模式实现
 */
@RestController
@RequestMapping("/api/test/pedestrian")
@Slf4j
public class TestPedestrianParamController {

    @Autowired
    private TestPedestrianParamService testPedestrianParamService;

    /**
     * 测试创建人行横道参数
     * GET /api/test/pedestrian/create?crossId=110101001001&pedestrianNo=1&direction=E&attribute=1
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "110101001001") String crossId,
            @RequestParam(defaultValue = "1") Integer pedestrianNo,
            @RequestParam(defaultValue = "E") String direction,
            @RequestParam(defaultValue = "1") Integer attribute) {

        Map<String, Object> response = new HashMap<>();

        try {
            PedestrianParamEntity created = testPedestrianParamService.createPedestrianParam(
                    crossId, pedestrianNo, direction, attribute);

            response.put("success", true);
            response.put("message", "人行横道参数创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建成功: crossId={}, pedestrianNo={}", crossId, pedestrianNo);
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
     * 测试根据路口ID查询所有人行横道
     * GET /api/test/pedestrian/list-by-cross?crossId=110101001001
     */
    @GetMapping("/list-by-cross")
    public ResponseEntity<Map<String, Object>> testListByCross(
            @RequestParam String crossId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<PedestrianParamEntity> entities = testPedestrianParamService.findByCrossId(crossId);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("crossId", crossId);
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
     * 测试根据路口ID和人行横道号查询
     * GET /api/test/pedestrian/get?crossId=110101001001&pedestrianNo=1
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGet(
            @RequestParam String crossId,
            @RequestParam Integer pedestrianNo) {

        Map<String, Object> response = new HashMap<>();

        try {
            PedestrianParamEntity entity = testPedestrianParamService.findByCrossIdAndPedestrianNo(
                    crossId, pedestrianNo);

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
     * 测试根据方向查询人行横道
     * GET /api/test/pedestrian/by-direction?crossId=110101001001&direction=E
     */
    @GetMapping("/by-direction")
    public ResponseEntity<Map<String, Object>> testFindByDirection(
            @RequestParam String crossId,
            @RequestParam String direction) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<PedestrianParamEntity> entities = testPedestrianParamService.findByDirection(
                    crossId, direction);

            response.put("success", true);
            response.put("message", "按方向查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("direction", direction);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("按方向查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询有按钮的人行横道
     * GET /api/test/pedestrian/with-button?crossId=110101001001
     */
    @GetMapping("/with-button")
    public ResponseEntity<Map<String, Object>> testFindWithButton(
            @RequestParam String crossId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<PedestrianParamEntity> entities = testPedestrianParamService.findWithButton(crossId);

            response.put("success", true);
            response.put("message", "查询有按钮的人行横道成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("查询有按钮的人行横道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试批量创建数据
     * GET /api/test/pedestrian/batch-create?crossId=110101001001&count=4
     */
    @GetMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "110101001001") String crossId,
            @RequestParam(defaultValue = "4") int count) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (count > 10) {
                count = 10; // 限制最大批量数量
            }

            List<PedestrianParamEntity> created = testPedestrianParamService.createBatchTestData(
                    crossId, count);

            response.put("success", true);
            response.put("message", "批量创建成功");
            response.put("data", created);
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
     * 完整测试流程
     * GET /api/test/pedestrian/full-test
     */
    @GetMapping("/full-test")
    public ResponseEntity<Map<String, Object>> fullTest() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> testResults = new HashMap<>();

        try {
            String testCrossId = "FULL_TEST_" + System.currentTimeMillis();

            // 1. 创建测试
            testResults.put("step1_create", "开始创建人行横道参数");
            PedestrianParamEntity created = testPedestrianParamService.createPedestrianParam(
                    testCrossId, 1, "E", 1);
            testResults.put("step1_result", "创建成功: " + created.getCrossId() + "-" + created.getPedestrianNo());

            // 2. 查询测试
            testResults.put("step2_query", "查询刚创建的参数");
            PedestrianParamEntity queried = testPedestrianParamService.findByCrossIdAndPedestrianNo(
                    testCrossId, 1);
            testResults.put("step2_result", "查询成功: " + queried.getDirection());

            // 3. 批量创建测试
            testResults.put("step3_batch", "批量创建更多数据");
            List<PedestrianParamEntity> batchCreated = testPedestrianParamService.createBatchTestData(
                    testCrossId, 3);
            testResults.put("step3_result", "批量创建成功: " + batchCreated.size() + " 条记录");

            // 4. 列表查询测试
            testResults.put("step4_list", "查询所有人行横道");
            List<PedestrianParamEntity> allList = testPedestrianParamService.findByCrossId(testCrossId);
            testResults.put("step4_result", "查询成功: 总共 " + allList.size() + " 条记录");

            // 5. 删除测试
            testResults.put("step5_delete", "清理测试数据");
            int deleteCount = testPedestrianParamService.cleanupTestData(testCrossId);
            testResults.put("step5_result", "清理成功: 删除 " + deleteCount + " 条记录");

            response.put("success", true);
            response.put("message", "完整测试流程执行成功");
            response.put("testResults", testResults);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("完整测试流程失败", e);

            testResults.put("error", "测试流程中断: " + e.getMessage());

            response.put("success", false);
            response.put("message", "测试流程失败: " + e.getMessage());
            response.put("testResults", testResults);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
}
