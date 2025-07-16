package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.LaneParamEntity;
import com.traffic.gat1049.repository.interfaces.LaneParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 车道参数测试控制器
 * 演示LaneParamRepository的各种查询方法
 *
 * 放置位置：traffic-signal-server/src/main/java/com/traffic/server/test/TestLaneParamController.java
 */
@RestController
@RequestMapping("/api/test/lane")
@Slf4j
public class TestLaneParamController {

    @Autowired
    private LaneParamRepository laneParamRepository;

    @Autowired
    private TestLaneParamService testLaneParamService;

    /**
     * 测试创建车道参数
     * GET /api/test/lane/create?crossId=1234567890&laneNo=1&direction=E&movement=12&feature=1
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> testCreate(
            @RequestParam(defaultValue = "1234567890") String crossId,
            @RequestParam(defaultValue = "1") Integer laneNo,
            @RequestParam(defaultValue = "E") String direction,
            @RequestParam(defaultValue = "12") String movement,
            @RequestParam(defaultValue = "1") Integer feature) {

        Map<String, Object> response = new HashMap<>();

        try {
            LaneParamEntity created = testLaneParamService.createLaneParam(
                    crossId, laneNo, direction, movement, feature);

            response.put("success", true);
            response.put("message", "车道参数创建成功");
            response.put("data", created);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建车道参数成功: crossId={}, laneNo={}", crossId, laneNo);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建车道参数失败", e);

            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据路口ID查询所有车道
     * GET /api/test/lane/list?crossId=1234567890
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> testFindByCrossId(
            @RequestParam(defaultValue = "1234567890") String crossId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> lanes = laneParamRepository.findByCrossId(crossId);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", lanes);
            response.put("count", lanes.size());
            response.put("timestamp", LocalDateTime.now());

            log.info("测试查询路口车道成功: crossId={}, count={}", crossId, lanes.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询路口车道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据路口ID和车道号查询单个车道
     * GET /api/test/lane/get?crossId=1234567890&laneNo=1
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> testFindByCrossIdAndLaneNo(
            @RequestParam(defaultValue = "1234567890") String crossId,
            @RequestParam(defaultValue = "1") Integer laneNo) {

        Map<String, Object> response = new HashMap<>();

        try {
            LaneParamEntity lane = laneParamRepository.findByCrossIdAndLaneNo(crossId, laneNo);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", lane);
            response.put("exists", lane != null);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试查询单个车道成功: crossId={}, laneNo={}, exists={}",
                    crossId, laneNo, lane != null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询单个车道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据进口方向查询车道
     * GET /api/test/lane/by-entrance?crossId=1234567890&entrance=E
     */
    @GetMapping("/by-entrance")
    public ResponseEntity<Map<String, Object>> testFindByEntrance(
            @RequestParam(defaultValue = "1234567890") String crossId,
            @RequestParam(defaultValue = "E") String entrance) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> lanes = laneParamRepository.findByEntrance(crossId, entrance);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", lanes);
            response.put("count", lanes.size());
            response.put("entrance", entrance);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试按进口方向查询车道成功: crossId={}, entrance={}, count={}",
                    crossId, entrance, lanes.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试按进口方向查询车道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据转向属性查询车道
     * GET /api/test/lane/by-movement?crossId=1234567890&movement=12
     */
    @GetMapping("/by-movement")
    public ResponseEntity<Map<String, Object>> testFindByMovement(
            @RequestParam(defaultValue = "1234567890") String crossId,
            @RequestParam(defaultValue = "12") Integer movement) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> lanes = laneParamRepository.findByMovement(crossId, movement);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", lanes);
            response.put("count", lanes.size());
            response.put("movement", movement);
            response.put("movementDesc", getMovementDescription(movement));
            response.put("timestamp", LocalDateTime.now());

            log.info("测试按转向查询车道成功: crossId={}, movement={}, count={}",
                    crossId, movement, lanes.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试按转向查询车道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询机动车道
     * GET /api/test/lane/vehicle?crossId=1234567890
     */
    @GetMapping("/vehicle")
    public ResponseEntity<Map<String, Object>> testFindVehicleLanes(
            @RequestParam(defaultValue = "1234567890") String crossId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> lanes = laneParamRepository.findVehicleLanes(crossId);

            response.put("success", true);
            response.put("message", "查询机动车道成功");
            response.put("data", lanes);
            response.put("count", lanes.size());
            response.put("laneType", "机动车道");
            response.put("timestamp", LocalDateTime.now());

            log.info("测试查询机动车道成功: crossId={}, count={}", crossId, lanes.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询机动车道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试查询非机动车道
     * GET /api/test/lane/non-vehicle?crossId=1234567890
     */
    @GetMapping("/non-vehicle")
    public ResponseEntity<Map<String, Object>> testFindNonVehicleLanes(
            @RequestParam(defaultValue = "1234567890") String crossId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> lanes = laneParamRepository.findNonVehicleLanes(crossId);

            response.put("success", true);
            response.put("message", "查询非机动车道成功");
            response.put("data", lanes);
            response.put("count", lanes.size());
            response.put("laneType", "非机动车道");
            response.put("timestamp", LocalDateTime.now());

            log.info("测试查询非机动车道成功: crossId={}, count={}", crossId, lanes.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询非机动车道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试根据信号组查询关联车道
     * GET /api/test/lane/by-signal-group?crossId=1234567890&signalGroupNo=1
     */
    @GetMapping("/by-signal-group")
    public ResponseEntity<Map<String, Object>> testFindBySignalGroup(
            @RequestParam(defaultValue = "1234567890") String crossId,
            @RequestParam(defaultValue = "1") Integer signalGroupNo) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> lanes = laneParamRepository.findBySignalGroup(crossId, signalGroupNo);

            response.put("success", true);
            response.put("message", "查询信号组关联车道成功");
            response.put("data", lanes);
            response.put("count", lanes.size());
            response.put("signalGroupNo", signalGroupNo);
            response.put("timestamp", LocalDateTime.now());

            log.info("测试查询信号组关联车道成功: crossId={}, signalGroupNo={}, count={}",
                    crossId, signalGroupNo, lanes.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试查询信号组关联车道失败", e);

            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试批量创建车道参数
     * GET /api/test/lane/batch-create?crossId=1234567890&count=4
     */
    @GetMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> testBatchCreate(
            @RequestParam(defaultValue = "1234567890") String crossId,
            @RequestParam(defaultValue = "4") Integer count) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> created = testLaneParamService.batchCreateLanes(crossId, count);

            response.put("success", true);
            response.put("message", "批量创建车道成功");
            response.put("data", created);
            response.put("count", created.size());
            response.put("timestamp", LocalDateTime.now());

            log.info("测试批量创建车道成功: crossId={}, count={}", crossId, created.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试批量创建车道失败", e);

            response.put("success", false);
            response.put("message", "批量创建失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试数据库连接和基本操作
     * GET /api/test/lane/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> testHealth() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 测试基本查询
            List<LaneParamEntity> testQuery = laneParamRepository.selectList(null);

            response.put("success", true);
            response.put("message", "车道参数Repository健康检查通过");
            response.put("totalRecords", testQuery.size());
            response.put("repositoryStatus", "正常");
            response.put("timestamp", LocalDateTime.now());

            log.info("车道参数Repository健康检查通过，总记录数: {}", testQuery.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("车道参数Repository健康检查失败", e);

            response.put("success", false);
            response.put("message", "健康检查失败: " + e.getMessage());
            response.put("repositoryStatus", "异常");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 测试创建标准十字路口车道布局
     * GET /api/test/lane/create-standard-cross?crossId=STANDARD_001
     */
    @GetMapping("/create-standard-cross")
    public ResponseEntity<Map<String, Object>> testCreateStandardCross(
            @RequestParam(defaultValue = "STANDARD_001") String crossId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<LaneParamEntity> created = testLaneParamService.createStandardCrossLanes(crossId);

            response.put("success", true);
            response.put("message", "标准十字路口车道布局创建成功");
            response.put("data", created);
            response.put("count", created.size());
            response.put("layout", "4个方向，每个方向3车道（左转、直行、右转）");
            response.put("directions", getDirectionSummary(created));
            response.put("timestamp", LocalDateTime.now());

            log.info("测试创建标准十字路口成功: crossId={}, 总车道数={}", crossId, created.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("测试创建标准十字路口失败", e);

            response.put("success", false);
            response.put("message", "创建标准十字路口失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取车道布局方向摘要
     */
    private Map<String, Object> getDirectionSummary(List<LaneParamEntity> lanes) {
        Map<String, Object> summary = new HashMap<>();

        // 按方向分组统计
        Map<String, Long> directionCount = lanes.stream()
                .collect(Collectors.groupingBy(
                        LaneParamEntity::getDirection,
                        Collectors.counting()));

        // 添加方向名称说明
        Map<String, Object> directionDetails = new HashMap<>();
        for (Map.Entry<String, Long> entry : directionCount.entrySet()) {
            String direction = entry.getKey();
            Long count = entry.getValue();

            Map<String, Object> detail = new HashMap<>();
            detail.put("name", getDirectionName(direction));
            detail.put("count", count);
            detail.put("code", direction);

            directionDetails.put(direction, detail);
        }

        summary.put("details", directionDetails);
        summary.put("totalDirections", directionCount.size());

        return summary;
    }
    /**
     * 获取方向名称
     */
    private String getDirectionName(String direction) {
        if (direction == null) return "未知";

        switch (direction) {
            case "1": return "北";
            case "2": return "东北";
            case "3": return "东";
            case "4": return "东南";
            case "5": return "南";
            case "6": return "西南";
            case "7": return "西";
            case "8": return "西北";
            case "9": return "其它";
            default: return "未知(" + direction + ")";
        }
    }
    /**
     * 获取转向描述
     */
    private String getMovementDescription(Integer movement) {
        if (movement == null) return "未知";

        switch (movement) {
            case 11: return "左转";
            case 12: return "直行";
            case 13: return "右转";
            case 14: return "直左";
            case 15: return "左右";
            case 16: return "直右";
            case 21: return "直左右";
            case 22: return "左直右";
            case 23: return "右直左";
            case 24: return "直左右混行";
            case 31: return "掉头";
            case 32: return "掉头加左转";
            case 33: return "掉头加直行";
            case 34: return "掉头加右转";
            default: return "其他(" + movement + ")";
        }
    }
}
