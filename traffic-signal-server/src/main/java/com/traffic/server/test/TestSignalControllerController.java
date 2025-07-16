package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.SignalControllerParamEntity;
import com.traffic.gat1049.repository.interfaces.SignalControllerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 信号机Repository测试控制器
 * 演示SignalControllerRepository的各种功能
 *
 * 放置位置：traffic-signal-server/src/main/java/com/traffic/server/test/TestSignalControllerController.java
 */
@RestController
@RequestMapping("/api/test/signal-controller")
@Slf4j
public class TestSignalControllerController {

    @Autowired
    private SignalControllerRepository signalControllerRepository;

    @Autowired
    private TestSignalControllerService testSignalControllerService;

    /**
     * 测试创建信号机（完整对象）
     * POST /api/test/signal-controller/create
     * Content-Type: application/json
     *
     * 请求体示例：
     * {
     *   "signalControllerId": "SC001",
     *   "supplier": "海信",
     *   "type": 1,
     *   "ip": "192.168.1.100",
     *   "port": 9999,
     *   "crossIdList": "[\"CROSS001\", \"CROSS002\"]",
     *   "systemIdList": "[\"SYS001\"]",
     *   "crossCount": 2,
     *   "systemCount": 1
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreateWithEntity(@RequestBody SignalControllerParamEntity entity) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 设置创建和更新时间
            entity.setCreatedTime(LocalDateTime.now());
            entity.setUpdatedTime(LocalDateTime.now());

            SignalControllerParamEntity created = testSignalControllerService.createSignalControllerWithEntity(entity);

            response.put("success", true);
            response.put("message", "信号机创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建成功: {}", created.getSignalControllerId());
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
     * 测试创建信号机（简化参数，兼容GET请求）
     * GET /api/test/signal-controller/create-simple?signalControllerId=SC001&supplier=海信&type=1&ip=192.168.1.100&port=9999
     */
    @GetMapping("/create-simple")
    public ResponseEntity<Map<String, Object>> testCreateSimple(
            @RequestParam(defaultValue = "SC001") String signalControllerId,
            @RequestParam(defaultValue = "海信") String supplier,
            @RequestParam(defaultValue = "1") String type,
            @RequestParam(defaultValue = "192.168.1.100") String ip,
            @RequestParam(defaultValue = "9999") Integer port) {

        Map<String, Object> response = new HashMap<>();

        try {
            SignalControllerParamEntity created = testSignalControllerService.createSignalController(
                    signalControllerId, supplier, type, ip, port);

            response.put("success", true);
            response.put("message", "信号机创建成功");
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
     * 测试更新信号机（完整对象）
     * PUT /api/test/signal-controller/update
     * Content-Type: application/json
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> testUpdateWithEntity(@RequestBody SignalControllerParamEntity entity) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 设置更新时间
            entity.setUpdatedTime(LocalDateTime.now());

            SignalControllerParamEntity updated = testSignalControllerService.updateSignalControllerWithEntity(entity);

            response.put("success", true);
            response.put("message", "信号机更新成功");
            response.put("data", updated);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试更新成功: {}", updated.getSignalControllerId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试更新失败", e);

            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
     /* GET /api/test/signal-controller/get?signalControllerId=SC001
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGetById(@RequestParam String signalControllerId) {
        Map<String, Object> response = new HashMap<>();

        try {
            SignalControllerParamEntity entity = signalControllerRepository.findBySignalControllerId(signalControllerId);

            if (entity != null) {
                response.put("success", true);
                response.put("message", "查询成功");
                response.put("data", entity);
            } else {
                response.put("success", false);
                response.put("message", "未找到指定的信号机");
                response.put("data", null);
            }
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
     * 测试查询所有信号机（完整信息视图）
     * GET /api/test/signal-controller/list-complete
     */
    @GetMapping("/list-complete")
    public ResponseEntity<Map<String, Object>> testListComplete() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SignalControllerParamEntity> entities = signalControllerRepository.findAllWithCompleteInfo();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询完整列表失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据供应商查询
     * GET /api/test/signal-controller/by-supplier?supplier=海信
     */
    @GetMapping("/by-supplier")
    public ResponseEntity<Map<String, Object>> testGetBySupplier(@RequestParam String supplier) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SignalControllerParamEntity> entities = signalControllerRepository.findBySupplier(supplier);

            response.put("success", true);
            response.put("message", "根据供应商查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("supplier", supplier);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据供应商查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据类型查询
     * GET /api/test/signal-controller/by-type?type=1
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Object>> testGetByType(@RequestParam String type) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SignalControllerParamEntity> entities = signalControllerRepository.findByType(type);

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
     * 测试根据路口ID查询信号机
     * GET /api/test/signal-controller/by-cross?crossId=CROSS001
     */
    @GetMapping("/by-cross")
    public ResponseEntity<Map<String, Object>> testGetByCrossId(@RequestParam String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SignalControllerParamEntity> entities = signalControllerRepository.findByCrossId(crossId);

            response.put("success", true);
            response.put("message", "根据路口ID查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("crossId", crossId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据路口ID查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据系统ID查询信号机
     * GET /api/test/signal-controller/by-system?systemId=SYS001
     */
    @GetMapping("/by-system")
    public ResponseEntity<Map<String, Object>> testGetBySystemId(@RequestParam String systemId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SignalControllerParamEntity> entities = signalControllerRepository.findBySystemId(systemId);

            response.put("success", true);
            response.put("message", "根据系统ID查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("systemId", systemId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据系统ID查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据IP地址查询
     * GET /api/test/signal-controller/by-ip?ip=192.168.1.100
     */
    @GetMapping("/by-ip")
    public ResponseEntity<Map<String, Object>> testGetByIp(@RequestParam String ip) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SignalControllerParamEntity> entities = signalControllerRepository.findByIp(ip);

            response.put("success", true);
            response.put("message", "根据IP地址查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("ip", ip);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试根据IP地址查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试批量创建信号机
     * GET /api/test/signal-controller/batch-create?count=5
     */
    @GetMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "5") int count) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (count > 10) {
                count = 10; // 限制最大批量数量
            }

            List<SignalControllerParamEntity> created = testSignalControllerService.createBatchTestData(count);

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
     * 完整测试流程（演示所有Repository功能）
     * GET /api/test/signal-controller/full-test
     */
    @GetMapping("/full-test")
    public ResponseEntity<Map<String, Object>> fullTest() {
        Map<String, Object> response = new HashMap<>();
        List<String> steps = new ArrayList<>();

        try {
            String testSignalControllerId = "FULL_TEST_" + System.currentTimeMillis();
//
//            // 1. 创建测试
//            steps.add("步骤1: 创建信号机");
//            SignalControllerParamEntity created = testSignalControllerService.createSignalController(
//                    testSignalControllerId,
//                    "全流程测试供应商",
//                    1,
//                    "192.168.100.100",
//                    8080
//            );
//            steps.add("创建成功: " + created.getSignalControllerId());
//
//            // 2. 根据ID查询测试
//            steps.add("步骤2: 根据ID查询信号机");
//            SignalControllerParamEntity queried = signalControllerRepository.findBySignalControllerId(testSignalControllerId);
//            steps.add("查询成功: " + queried.getSupplier());
//
//            // 3. 根据供应商查询测试
//            steps.add("步骤3: 根据供应商查询");
//            List<SignalControllerParamEntity> bySupplier = signalControllerRepository.findBySupplier("全流程测试供应商");
//            steps.add("供应商查询结果数量: " + bySupplier.size());
//
//            // 4. 根据类型查询测试
//            steps.add("步骤4: 根据类型查询");
//            List<SignalControllerParamEntity> byType = signalControllerRepository.findByType(1);
//            steps.add("类型查询结果数量: " + byType.size());
//
//            // 5. 根据IP查询测试
//            steps.add("步骤5: 根据IP查询");
//            List<SignalControllerParamEntity> byIp = signalControllerRepository.findByIp("192.168.100.100");
//            steps.add("IP查询结果数量: " + byIp.size());
//
//            // 6. 查询完整信息视图
//            steps.add("步骤6: 查询完整信息视图");
//            List<SignalControllerParamEntity> completeInfo = signalControllerRepository.findAllWithCompleteInfo();
//            steps.add("完整信息查询结果数量: " + completeInfo.size());
//
//            // 7. 删除测试数据
//            steps.add("步骤7: 清理测试数据");
//            signalControllerRepository.deleteById(created.getId());
//            steps.add("清理完成");
//
//            response.put("success", true);
//            response.put("message", "全流程测试完成");
//            response.put("steps", steps);
//            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("全流程测试失败", e);
            steps.add("测试失败: " + e.getMessage());

            response.put("success", false);
            response.put("message", "全流程测试失败: " + e.getMessage());
            response.put("steps", steps);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 数据库统计信息
     * GET /api/test/signal-controller/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 统计各种数据
            List<SignalControllerParamEntity> allControllers = signalControllerRepository.findAllWithCompleteInfo();

            Map<String, Long> supplierStats = new HashMap<>();
            Map<String, Long> typeStats = new HashMap<>();

            for (SignalControllerParamEntity controller : allControllers) {
                // 供应商统计
                supplierStats.put(controller.getSupplier(),
                        supplierStats.getOrDefault(controller.getSupplier(), 0L) + 1);

                // 类型统计
                typeStats.put(controller.getType(),
                        typeStats.getOrDefault(controller.getType(), 0L) + 1);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("总数量", allControllers.size());
            stats.put("供应商分布", supplierStats);
            stats.put("类型分布", typeStats);

            response.put("success", true);
            response.put("message", "统计信息获取成功");
            response.put("data", stats);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取统计信息失败", e);

            response.put("success", false);
            response.put("message", "获取统计信息失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
}
