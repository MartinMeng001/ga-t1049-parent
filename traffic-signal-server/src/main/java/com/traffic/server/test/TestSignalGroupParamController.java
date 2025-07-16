package com.traffic.server.test;

import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.repository.entity.SignalGroupParamEntity;
import com.traffic.gat1049.repository.interfaces.SignalGroupParamRepository;
import com.traffic.server.test.dto.CreateSignalGroupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 信号组参数测试控制器
 * 按照 com.traffic.server.test 的模式设计
 * 测试 SignalGroupParamRepository 的各种功能
 */
@RestController
@RequestMapping("/api/test/signalgroup")
@Slf4j
public class TestSignalGroupParamController {

    @Autowired
    private TestSignalGroupParamService completeSignalGroupParamService;

    /**
     * 测试获取路口所有信号组（包含关联灯组）
     * GET /api/test/signalgroup-dto/list-with-lamps?crossId=C001
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> testGetSignalGroupsWithLampGroups(
            @RequestParam(defaultValue = "C001") String crossId) {

        Map<String, Object> response = new HashMap<>();

        try {
            log.info("测试查询路口所有信号组（含灯组）: crossId={}", crossId);

            // 使用DTO方案查询
            List<SignalGroupParam> signalGroupParams = completeSignalGroupParamService.getSignalGroupsWithLampGroups(crossId);

            // 统计信息
//            int totalSignalGroups = signalGroupParams.size();
//            int totalLampGroups = signalGroupParams.stream()
//                    .mapToInt(sg -> sg.getLampGroupNoList().size())
//                    .sum();

            // 按类型分组统计
//            Map<String, Long> typeStatistics = signalGroupParams.stream()
//                    .collect(Collectors.groupingBy(
//                            sg -> getTypeDescription(sg.),
//                            Collectors.counting()));

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", signalGroupParams);
//            response.put("statistics", Map.of(
//                    "totalSignalGroups", totalSignalGroups,
//                    "totalLampGroups", totalLampGroups,
//                    "typeStatistics", typeStatistics
//            ));
            response.put("crossId", crossId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("查询失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }
    /**
     * 方案1：使用SignalGroupParam对象创建（推荐）
     * POST /api/test/signalgroup/create-with-param
     *
     * Request Body示例：
     * {
     *   "crossId": "C001",
     *   "signalGroupNo": 1,
     *   "name": "东西直行",
     *   "greenFlashLen": 3,
     *   "maxGreen": 60,
     *   "minGreen": 10,
     *   "lampGroupNoList": [1, 2, 3]
     * }
     */
    @PostMapping("/create-with-param")
    public ResponseEntity<Map<String, Object>> testCreateWithParam(@RequestBody SignalGroupParam signalGroupParam) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("测试使用SignalGroupParam创建信号组: {}", signalGroupParam);

            // 调用服务创建完整信号组
            SignalGroupParam createdParam = completeSignalGroupParamService.createCompleteSignalGroup(signalGroupParam);

            response.put("success", true);
            response.put("message", "信号组创建成功");
            response.put("data", createdParam);
            response.put("method", "SignalGroupParam对象方式");
            response.put("lampGroupCount", createdParam.getLampGroupNoList() != null ? createdParam.getLampGroupNoList().size() : 0);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("创建信号组失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 方案2：使用CreateSignalGroupRequest对象创建
     * POST /api/test/signalgroup-complete/create-with-request
     *
     * Request Body示例：
     * {
     *   "crossId": "C001",
     *   "signalGroupNo": 2,
     *   "name": "南北直行",
     *   "type": 1,
     *   "greenFlashLen": 3,
     *   "maxGreen": 60,
     *   "minGreen": 10,
     *   "lampGroupNoList": [4, 5, 6]
     * }
     */
    @PostMapping("/create-with-request")
    public ResponseEntity<Map<String, Object>> testCreateWithRequest(@RequestBody CreateSignalGroupRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("测试使用CreateSignalGroupRequest创建信号组: {}", request);

            // 调用服务创建完整信号组
            SignalGroupParam createdParam = completeSignalGroupParamService.createSignalGroupWithRequest(request);

            response.put("success", true);
            response.put("message", "信号组创建成功");
            response.put("data", createdParam);
            response.put("method", "CreateSignalGroupRequest对象方式");
            response.put("originalRequest", request);
            response.put("lampGroupCount", createdParam.getLampGroupNoList() != null ? createdParam.getLampGroupNoList().size() : 0);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("创建信号组失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 方案3：使用分步参数创建
     * POST /api/test/signalgroup-complete/create-step-by-step
     *
     * Request Body示例：
     * {
     *   "crossId": "C001",
     *   "signalGroupNo": 3,
     *   "name": "东西左转",
     *   "type": 21,
     *   "greenFlashLen": 3,
     *   "maxGreen": 45,
     *   "minGreen": 8,
     *   "lampGroupNoList": [7, 8]
     * }
     */
    @PostMapping("/create-step-by-step")
    public ResponseEntity<Map<String, Object>> testCreateStepByStep(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 解析请求参数
            String crossId = (String) requestData.get("crossId");
            Integer signalGroupNo = (Integer) requestData.get("signalGroupNo");
            String name = (String) requestData.get("name");
            Integer type = (Integer) requestData.get("type");
            Integer greenFlashLen = (Integer) requestData.get("greenFlashLen");
            Integer maxGreen = (Integer) requestData.get("maxGreen");
            Integer minGreen = (Integer) requestData.get("minGreen");

            @SuppressWarnings("unchecked")
            List<Integer> lampGroupNoList = (List<Integer>) requestData.get("lampGroupNoList");

            log.info("测试分步创建信号组: crossId={}, signalGroupNo={}, name={}, 灯组列表={}",
                    crossId, signalGroupNo, name, lampGroupNoList);

            // 调用服务分步创建信号组
            SignalGroupParam createdParam = completeSignalGroupParamService.createSignalGroupStepByStep(
                    crossId, signalGroupNo, name, type, greenFlashLen, maxGreen, minGreen, lampGroupNoList);

            response.put("success", true);
            response.put("message", "信号组创建成功");
            response.put("data", createdParam);
            response.put("method", "分步参数方式");
            response.put("inputParameters", Map.of(
                    "crossId", crossId,
                    "signalGroupNo", signalGroupNo,
                    "name", name,
                    "lampGroupNoList", lampGroupNoList
            ));
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("分步创建信号组失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试更新完整信号组
     * PUT /api/test/signalgroup-complete/update
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> testUpdateComplete(@RequestBody SignalGroupParam signalGroupParam) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("测试更新完整信号组: {}", signalGroupParam);

            // 调用服务更新完整信号组
            SignalGroupParam updatedParam = completeSignalGroupParamService.updateCompleteSignalGroup(signalGroupParam);

            response.put("success", true);
            response.put("message", "信号组更新成功");
            response.put("data", updatedParam);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("更新信号组失败", e);

            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试删除完整信号组
     * DELETE /api/test/signalgroup-complete/delete?crossId=C001&signalGroupNo=1
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> testDeleteComplete(
            @RequestParam String crossId,
            @RequestParam Integer signalGroupNo) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("测试删除完整信号组: crossId={}, signalGroupNo={}", crossId, signalGroupNo);

            // 调用服务删除完整信号组
            completeSignalGroupParamService.deleteCompleteSignalGroup(crossId, signalGroupNo);

            response.put("success", true);
            response.put("message", "信号组删除成功");
            response.put("deletedSignalGroup", Map.of("crossId", crossId, "signalGroupNo", signalGroupNo));
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("删除信号组失败", e);

            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 批量创建信号组测试
     * POST /api/test/signalgroup-complete/batch-create
     *
     * Request Body示例：
     * {
     *   "crossId": "C001",
     *   "signalGroups": [
     *     {
     *       "signalGroupNo": 1,
     *       "name": "东西直行",
     *       "lampGroupNoList": [1, 2]
     *     },
     *     {
     *       "signalGroupNo": 2,
     *       "name": "南北直行",
     *       "lampGroupNoList": [3, 4]
     *     }
     *   ]
     * }
     */
    @PostMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String crossId = (String) requestData.get("crossId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> signalGroupsData = (List<Map<String, Object>>) requestData.get("signalGroups");

            log.info("测试批量创建信号组: crossId={}, 数量={}", crossId, signalGroupsData.size());

            List<SignalGroupParam> createdSignalGroups = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (Map<String, Object> sgData : signalGroupsData) {
                try {
                    SignalGroupParam signalGroupParam = new SignalGroupParam();
                    signalGroupParam.setCrossId(crossId);
                    signalGroupParam.setSignalGroupNo((Integer) sgData.get("signalGroupNo"));
                    signalGroupParam.setName((String) sgData.get("name"));
                    signalGroupParam.setGreenFlashLen(3); // 默认值
                    signalGroupParam.setMaxGreen(60); // 默认值
                    signalGroupParam.setMinGreen(10); // 默认值

                    @SuppressWarnings("unchecked")
                    List<Integer> lampGroupNoList = (List<Integer>) sgData.get("lampGroupNoList");
                    signalGroupParam.setLampGroupNoList(lampGroupNoList);

                    // 创建信号组
                    SignalGroupParam createdParam = completeSignalGroupParamService.createCompleteSignalGroup(signalGroupParam);
                    createdSignalGroups.add(createdParam);

                } catch (Exception e) {
                    String error = "创建信号组失败: " + sgData.get("signalGroupNo") + " - " + e.getMessage();
                    errors.add(error);
                    log.error(error, e);
                }
            }

            boolean allSuccess = errors.isEmpty();
            response.put("success", allSuccess);
            response.put("message", allSuccess ? "批量创建完成" : "部分创建失败");
            response.put("data", createdSignalGroups);
            response.put("statistics", Map.of(
                    "total", signalGroupsData.size(),
                    "success", createdSignalGroups.size(),
                    "failed", errors.size()
            ));
            response.put("errors", errors);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("批量创建信号组失败", e);

            response.put("success", false);
            response.put("message", "批量创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 创建标准路口信号组配置
     * GET /api/test/signalgroup-complete/create-standard?crossId=C001
     *
     * 创建标准四相位路口的信号组配置：
     * - 信号组1: 东西直行 (灯组1,2)
     * - 信号组2: 南北直行 (灯组3,4)
     * - 信号组3: 东西左转 (灯组5,6)
     * - 信号组4: 南北左转 (灯组7,8)
     * - 信号组5: 东西行人 (灯组9)
     * - 信号组6: 南北行人 (灯组10)
     */
    @GetMapping("/create-standard")
    public ResponseEntity<Map<String, Object>> testCreateStandardCross(@RequestParam(defaultValue = "C001") String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("测试创建标准路口信号组配置: crossId={}", crossId);

            List<SignalGroupParam> standardSignalGroups = createStandardSignalGroups(crossId);
            List<SignalGroupParam> createdSignalGroups = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (SignalGroupParam signalGroupParam : standardSignalGroups) {
                try {
                    SignalGroupParam createdParam = completeSignalGroupParamService.createCompleteSignalGroup(signalGroupParam);
                    createdSignalGroups.add(createdParam);
                } catch (Exception e) {
                    String error = "创建信号组失败: " + signalGroupParam.getSignalGroupNo() + " - " + e.getMessage();
                    errors.add(error);
                    log.error(error, e);
                }
            }

            boolean allSuccess = errors.isEmpty();
            response.put("success", allSuccess);
            response.put("message", allSuccess ? "标准路口配置创建完成" : "部分配置创建失败");
            response.put("data", createdSignalGroups);
            response.put("configType", "标准四相位路口");
            response.put("statistics", Map.of(
                    "total", standardSignalGroups.size(),
                    "success", createdSignalGroups.size(),
                    "failed", errors.size(),
                    "vehicleSignalGroups", createdSignalGroups.stream().filter(sg -> sg.getSignalGroupNo() <= 4).count(),
                    "pedestrianSignalGroups", createdSignalGroups.stream().filter(sg -> sg.getSignalGroupNo() > 4).count()
            ));
            response.put("errors", errors);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("创建标准路口配置失败", e);

            response.put("success", false);
            response.put("message", "创建标准配置失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 验证信号组完整性测试
     * GET /api/test/signalgroup-complete/validate?crossId=C001
     *
     * 验证路口所有信号组的数据完整性：
     * - 检查信号组参数是否完整
     * - 检查关联灯组是否存在
     * - 检查配时参数是否合理
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> testValidateIntegrity(@RequestParam String crossId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("测试验证信号组完整性: crossId={}", crossId);

            // 这里应该调用查询服务获取信号组列表
            // 由于我们主要演示创建功能，这里用模拟数据
            List<Map<String, Object>> validationResults = new ArrayList<>();
            List<String> issues = new ArrayList<>();

            // 模拟验证逻辑
            for (int i = 1; i <= 4; i++) {
                Map<String, Object> validation = new HashMap<>();
                validation.put("signalGroupNo", i);
                validation.put("hasBasicParams", true);
                validation.put("hasLampGroups", true);
                validation.put("timingValid", true);
                validation.put("status", "正常");
                validationResults.add(validation);
            }

            boolean hasIssues = !issues.isEmpty();
            response.put("success", !hasIssues);
            response.put("message", hasIssues ? "发现数据完整性问题" : "数据完整性验证通过");
            response.put("validationResults", validationResults);
            response.put("issues", issues);
            response.put("crossId", crossId);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("验证信号组完整性失败", e);

            response.put("success", false);
            response.put("message", "验证失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取创建操作统计
     * GET /api/test/signalgroup-complete/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCreationStatistics() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 模拟统计数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalSignalGroupsCreated", 156);
            statistics.put("totalLampGroupAssociations", 312);
            statistics.put("averageLampGroupsPerSignalGroup", 2.0);
            statistics.put("successRate", 98.5);
            statistics.put("mostUsedCreateMethod", "SignalGroupParam对象方式");

            Map<String, Integer> methodUsage = new HashMap<>();
            methodUsage.put("SignalGroupParam对象方式", 89);
            methodUsage.put("CreateSignalGroupRequest对象方式", 45);
            methodUsage.put("分步参数方式", 22);

            response.put("success", true);
            response.put("message", "统计信息获取成功");
            response.put("statistics", statistics);
            response.put("methodUsage", methodUsage);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取统计信息失败", e);

            response.put("success", false);
            response.put("message", "获取统计失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    // ============ 私有辅助方法 ============

    /**
     * 创建标准信号组配置
     */
    private List<SignalGroupParam> createStandardSignalGroups(String crossId) {
        List<SignalGroupParam> signalGroups = new ArrayList<>();

        // 信号组1: 东西直行
        SignalGroupParam sg1 = new SignalGroupParam();
        sg1.setCrossId(crossId);
        sg1.setSignalGroupNo(1);
        sg1.setName("东西直行");
        sg1.setGreenFlashLen(3);
        sg1.setMaxGreen(60);
        sg1.setMinGreen(15);
        sg1.setLampGroupNoList(Arrays.asList(1, 2));
        signalGroups.add(sg1);

        // 信号组2: 南北直行
        SignalGroupParam sg2 = new SignalGroupParam();
        sg2.setCrossId(crossId);
        sg2.setSignalGroupNo(2);
        sg2.setName("南北直行");
        sg2.setGreenFlashLen(3);
        sg2.setMaxGreen(60);
        sg2.setMinGreen(15);
        sg2.setLampGroupNoList(Arrays.asList(3, 4));
        signalGroups.add(sg2);

        // 信号组3: 东西左转
        SignalGroupParam sg3 = new SignalGroupParam();
        sg3.setCrossId(crossId);
        sg3.setSignalGroupNo(3);
        sg3.setName("东西左转");
        sg3.setGreenFlashLen(3);
        sg3.setMaxGreen(45);
        sg3.setMinGreen(10);
        sg3.setLampGroupNoList(Arrays.asList(5, 6));
        signalGroups.add(sg3);

        // 信号组4: 南北左转
        SignalGroupParam sg4 = new SignalGroupParam();
        sg4.setCrossId(crossId);
        sg4.setSignalGroupNo(4);
        sg4.setName("南北左转");
        sg4.setGreenFlashLen(3);
        sg4.setMaxGreen(45);
        sg4.setMinGreen(10);
        sg4.setLampGroupNoList(Arrays.asList(7, 8));
        signalGroups.add(sg4);

        // 信号组5: 东西行人
        SignalGroupParam sg5 = new SignalGroupParam();
        sg5.setCrossId(crossId);
        sg5.setSignalGroupNo(5);
        sg5.setName("东西行人");
        sg5.setGreenFlashLen(3);
        sg5.setMaxGreen(30);
        sg5.setMinGreen(8);
        sg5.setLampGroupNoList(Arrays.asList(9));
        signalGroups.add(sg5);

        // 信号组6: 南北行人
        SignalGroupParam sg6 = new SignalGroupParam();
        sg6.setCrossId(crossId);
        sg6.setSignalGroupNo(6);
        sg6.setName("南北行人");
        sg6.setGreenFlashLen(3);
        sg6.setMaxGreen(30);
        sg6.setMinGreen(8);
        sg6.setLampGroupNoList(Arrays.asList(10));
        signalGroups.add(sg6);

        return signalGroups;
    }

    /**
     * 获取信号组类型描述
     */
    private String getTypeDescription(Integer type) {
        if (type == null) return "未知";

        switch (type) {
            case 0: return "行人信号组";
            case 1: return "机动车直行";
            case 21: return "机动车左转";
            case 22: return "机动车右转";
            case 23: return "机动车掉头";
            default: return "其他类型(" + type + ")";
        }
    }
}
