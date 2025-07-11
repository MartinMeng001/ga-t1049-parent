package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.SysInfoEntity;
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

@RestController
@RequestMapping("/api/test/sysinfo")
@Slf4j
public class TestSysInfoController {

    @Autowired
    private TestSysInfoService testSysInfoService;

    /**
     * 测试创建系统信息
     * GET /api/test/sysinfo/create?systemId=SYS001&sysName=测试系统&sysVersion=1.0.0&supplier=测试供应商
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "SYS001") String systemId,
            @RequestParam(defaultValue = "交通信号控制测试系统") String sysName,
            @RequestParam(defaultValue = "1.0.0") String sysVersion,
            @RequestParam(defaultValue = "测试供应商") String supplier) {

        Map<String, Object> response = new HashMap<>();

        try {
            SysInfoEntity created = testSysInfoService.createSysInfo(systemId, sysName, sysVersion, supplier);

            response.put("success", true);
            response.put("message", "系统信息创建成功");
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
     * 测试查询单个系统信息
     * GET /api/test/sysinfo/get?systemId=SYS001
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testGet(@RequestParam String systemId) {
        Map<String, Object> response = new HashMap<>();

        try {
            SysInfoEntity entity = testSysInfoService.getSysInfo(systemId);

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
     * 测试查询所有活跃系统
     * GET /api/test/sysinfo/list
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> testList() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SysInfoEntity> entities = testSysInfoService.getAllActiveSysInfo();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
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
     * 测试根据供应商查询
     * GET /api/test/sysinfo/by-supplier?supplier=测试供应商
     */
    @GetMapping("/by-supplier")
    public ResponseEntity<Map<String, Object>> testGetBySupplier(@RequestParam String supplier) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SysInfoEntity> entities = testSysInfoService.getSysInfoBySupplier(supplier);

            response.put("success", true);
            response.put("message", "根据供应商查询成功");
            response.put("data", entities);
            response.put("count", entities.size());
            response.put("supplier", supplier);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("根据供应商查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试分页查询
     * GET /api/test/sysinfo/page?keyword=测试&pageNum=1&pageSize=5
     */
//    @GetMapping("/page")
//    public ResponseEntity<Map<String, Object>> testPageQuery(
//            @RequestParam(defaultValue = "") String keyword,
//            @RequestParam(defaultValue = "1") int pageNum,
//            @RequestParam(defaultValue = "10") int pageSize) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            IPage<SysInfoEntity> pageResult = testSysInfoService.testPageQuery(keyword, pageNum, pageSize);
//
//            response.put("success", true);
//            response.put("message", "分页查询成功");
//            response.put("data", pageResult.getRecords());
//            response.put("total", pageResult.getTotal());
//            response.put("pageNum", pageResult.getCurrent());
//            response.put("pageSize", pageResult.getSize());
//            response.put("totalPages", pageResult.getPages());
//            response.put("keyword", keyword);
//            response.put("timestamp", LocalDateTime.now());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("分页查询失败", e);
//
//            response.put("success", false);
//            response.put("message", "查询失败: " + e.getMessage());
//            response.put("timestamp", LocalDateTime.now());
//
//            return ResponseEntity.ok(response);
//        }
//    }

    /**
     * 测试批量创建数据
     * GET /api/test/sysinfo/batch-create?count=5
     */
    @GetMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "5") int count) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (count > 20) {
                count = 20; // 限制最大批量数量
            }

            List<SysInfoEntity> created = testSysInfoService.createBatchTestData(count);

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
     * 完整测试流程（基于现有Repository）
     * GET /api/test/sysinfo/full-test
     */
//    @GetMapping("/full-test")
//    public ResponseEntity<Map<String, Object>> fullTest() {
//        Map<String, Object> response = new HashMap<>();
//        List<String> steps = new ArrayList<>();
//
//        try {
//            String testSystemId = "FULL_TEST_" + System.currentTimeMillis();
//
//            // 1. 创建测试
//            steps.add("步骤1: 创建系统信息");
//            SysInfoEntity created = testSysInfoService.createSysInfo(
//                    testSystemId,
//                    "全流程测试系统",
//                    "1.0.0",
//                    "自动化测试供应商"
//            );
//            steps.add("创建成功: " + created.getSystemId());
//
//            // 2. 查询测试
//            steps.add("步骤2: 查询系统信息");
//            SysInfoEntity queried = testSysInfoService.getSysInfo(testSystemId);
//            steps.add("查询成功: " + queried.getSysName());
//
//            // 3. 更新测试
//            steps.add("步骤3: 更新系统信息");
//            SysInfoEntity updated = testSysInfoService.updateSysInfo(
//                    testSystemId,
//                    "更新后的测试系统",
//                    "2.0.0",
//                    "更新后的供应商"
//            );
//            steps.add("更新成功: " + updated.getSysVersion());
//
//            // 4. 供应商查询测试
//            steps.add("步骤4: 根据供应商查询");
//            List<SysInfoEntity> bySupplier = testSysInfoService.getSysInfoBySupplier("更新后的供应商");
//            steps.add("供应商查询成功，找到 " + bySupplier.size() + " 条记录");
//
//            // 5. 列表查询测试
//            steps.add("步骤5: 查询所有活跃系统");
//            List<SysInfoEntity> allActive = testSysInfoService.getAllActiveSysInfo();
//            steps.add("列表查询成功，共 " + allActive.size() + " 条活跃记录");
//
//            // 6. 分页查询测试
//            steps.add("步骤6: 测试分页查询");
//            IPage<SysInfoEntity> pageResult = testSysInfoService.testPageQuery("测试", 1, 5);
//            steps.add("分页查询成功，总计 " + pageResult.getTotal() + " 条记录");
//
//            // 7. 删除测试
//            steps.add("步骤7: 删除系统信息");
//            testSysInfoService.deleteSysInfo(testSystemId);
//            steps.add("删除成功");
//
//            response.put("success", true);
//            response.put("message", "完整测试流程执行成功");
//            response.put("steps", steps);
//            response.put("testSystemId", testSystemId);
//            response.put("repositoryType", "MyBatis Plus + @Select");
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
}
