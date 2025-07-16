package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.LaneParamEntity;
import com.traffic.gat1049.repository.interfaces.LaneParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 车道参数测试服务类
 * 提供车道参数的测试数据创建和管理功能
 *
 * 放置位置：traffic-signal-server/src/main/java/com/traffic/server/test/TestLaneParamService.java
 */
@Service
@Slf4j
public class TestLaneParamService {

    @Autowired
    private LaneParamRepository laneParamRepository;

    /**
     * 创建单个车道参数（使用参数）
     */
    public LaneParamEntity createLaneParam(String crossId, Integer laneNo,
                                           String direction, String movement, Integer feature) {

        LaneParamEntity entity = new LaneParamEntity();

        // 设置基础信息
        entity.setCrossId(crossId);
        entity.setLaneNo(laneNo);
        entity.setDirection(direction);
        entity.setMovement(movement);
        entity.setFeature(feature);

        // 设置默认值
        entity.setAttribute(0); // 路口进口
        entity.setAzimuth(getDefaultAzimuth(direction)); // 根据方向设置方位角
        entity.setWaitingArea(0); // 无待行区

        // 设置可变转向列表（示例）
        if ("11".equals(movement)) { // 直行车道可能支持左转和右转
            entity.setVarMovementList("[\"11\", \"12\", \"13\"]"); // 支持直行、左转、右转
        } else {
            entity.setVarMovementList("[]"); // 无可变转向
        }

        return createLaneParam(entity);
    }

    /**
     * 创建单个车道参数（使用Entity）
     */
    public LaneParamEntity createLaneParam(LaneParamEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("车道参数实体不能为空");
        }

        // 验证必填字段
        validateRequiredFields(entity);

        // 自动设置默认值（如果未设置）
        setDefaultValues(entity);

        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        if (entity.getCreatedTime() == null) {
            entity.setCreatedTime(now);
        }
        entity.setUpdatedTime(now);

        // 保存到数据库
        laneParamRepository.insert(entity);

        log.info("创建车道参数: crossId={}, laneNo={}, direction={}, movement={}",
                entity.getCrossId(), entity.getLaneNo(), entity.getDirection(), entity.getMovement());

        return entity;
    }

    /**
     * 批量创建车道参数（使用Entity列表）
     */
    public List<LaneParamEntity> batchCreateLaneParams(List<LaneParamEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("车道参数实体列表不能为空");
        }

        List<LaneParamEntity> createdEntities = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (LaneParamEntity entity : entities) {
            // 验证必填字段
            validateRequiredFields(entity);

            // 自动设置默认值
            setDefaultValues(entity);

            // 设置时间戳
            if (entity.getCreatedTime() == null) {
                entity.setCreatedTime(now);
            }
            entity.setUpdatedTime(now);

            // 保存到数据库
            laneParamRepository.insert(entity);
            createdEntities.add(entity);

            log.debug("批量创建车道参数: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo());
        }

        log.info("批量创建车道参数完成: 总数={}", createdEntities.size());

        return createdEntities;
    }

    /**
     * 批量创建车道参数
     */
    public List<LaneParamEntity> batchCreateLanes(String crossId, Integer count) {
        List<LaneParamEntity> lanes = new ArrayList<>();

        // 定义四个主要方向的基础信息 [按实际编码]
        String[] directions = {"1", "3", "5", "7"}; // 北、东、南、西
        String[] movements = {"12", "11", "13"}; // 左转、直行、右转

        int laneNo = 1;

        for (int dirIndex = 0; dirIndex < directions.length && laneNo <= count; dirIndex++) {
            String direction = directions[dirIndex];

            for (int moveIndex = 0; moveIndex < movements.length && laneNo <= count; moveIndex++) {
                String movement = movements[moveIndex];

                LaneParamEntity lane = new LaneParamEntity();

                // 基础设置
                lane.setCrossId(crossId);
                lane.setLaneNo(laneNo);
                lane.setDirection(direction);
                lane.setMovement(movement);
                lane.setFeature(1); // 机动车道
                lane.setAttribute(0); // 路口进口
                lane.setAzimuth(getDefaultAzimuth(direction));
                lane.setWaitingArea(laneNo % 3 == 0 ? 1 : 0); // 每三个车道设置一个待行区

                // 可变转向设置
                if ("11".equals(movement) && laneNo % 2 == 0) { // 偶数直行车道设为可变
                    lane.setVarMovementList("[\"11\", \"12\", \"13\"]"); // 支持直行、左转、右转
                } else {
                    lane.setVarMovementList("[]");
                }

                // 时间戳
                LocalDateTime now = LocalDateTime.now();
                lane.setCreatedTime(now);
                lane.setUpdatedTime(now);

                lanes.add(lane);
                laneNo++;
            }
        }

        // 批量插入
        for (LaneParamEntity lane : lanes) {
            laneParamRepository.insert(lane);
        }

        log.info("批量创建车道参数完成: crossId={}, 总数={}", crossId, lanes.size());

        return lanes;
    }

    /**
     * 创建标准十字路口车道布局（4个方向，每个方向3车道）
     */
    public List<LaneParamEntity> createStandardCrossLanes(String crossId) {
        List<LaneParamEntity> lanes = new ArrayList<>();

        // 方向定义：北、东、南、西 [按实际编码和方位角]
        Object[][] directions = {
                {"1", 0},    // 北，方位角0度
                {"3", 90},   // 东，方位角90度
                {"5", 180},  // 南，方位角180度
                {"7", 270}   // 西，方位角270度
        };

        // 每个方向的车道配置：左转、直行、右转 [按实际编码]
        Object[][] laneConfigs = {
                {"12", "左转车道"},
                {"11", "直行车道"},
                {"13", "右转车道"}
        };

        int laneNo = 1;

        for (Object[] dir : directions) {
            String direction = (String) dir[0];
            Integer azimuth = (Integer) dir[1];

            for (Object[] config : laneConfigs) {
                String movement = (String) config[0];
                String desc = (String) config[1];

                LaneParamEntity lane = new LaneParamEntity();

                lane.setCrossId(crossId);
                lane.setLaneNo(laneNo);
                lane.setDirection(direction);
                lane.setMovement(movement);
                lane.setFeature(1); // 机动车道
                lane.setAttribute(0); // 路口进口
                lane.setAzimuth(azimuth);

                // 直行车道设置待行区
                if ("11".equals(movement)) {
                    lane.setWaitingArea(1);
                    // 直行车道支持可变转向
                    lane.setVarMovementList("[\"11\", \"12\", \"13\"]");
                } else {
                    lane.setWaitingArea(0);
                    lane.setVarMovementList("[]");
                }

                LocalDateTime now = LocalDateTime.now();
                lane.setCreatedTime(now);
                lane.setUpdatedTime(now);

                lanes.add(lane);
                laneParamRepository.insert(lane);

                log.debug("创建{}方向{}: laneNo={}", getDirectionName(direction), desc, laneNo);
                laneNo++;
            }
        }

        log.info("创建标准十字路口车道布局完成: crossId={}, 总车道数={}", crossId, lanes.size());

        return lanes;
    }

    /**
     * 清理测试数据
     */
    public void cleanupTestData(String crossId) {
        try {
            List<LaneParamEntity> existingLanes = laneParamRepository.findByCrossId(crossId);

            for (LaneParamEntity lane : existingLanes) {
                laneParamRepository.deleteById(lane.getId());
            }

            log.info("清理测试数据完成: crossId={}, 清理车道数={}", crossId, existingLanes.size());

        } catch (Exception e) {
            log.error("清理测试数据失败: crossId={}", crossId, e);
            throw new RuntimeException("清理测试数据失败", e);
        }
    }

    /**
     * 获取车道统计信息
     */
    public Map<String, Object> getLaneStatistics(String crossId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            List<LaneParamEntity> allLanes = laneParamRepository.findByCrossId(crossId);

            // 基础统计
            stats.put("totalLanes", allLanes.size());

            // 按方向统计
            Map<String, Long> directionCount = allLanes.stream()
                    .collect(Collectors.groupingBy(
                            LaneParamEntity::getDirection,
                            Collectors.counting()));
            stats.put("byDirection", directionCount);

            // 按转向统计
            Map<String, Long> movementCount = allLanes.stream()
                    .collect(Collectors.groupingBy(
                            LaneParamEntity::getMovement,
                            Collectors.counting()));
            stats.put("byMovement", movementCount);

            // 按特性统计
            Map<Integer, Long> featureCount = allLanes.stream()
                    .collect(Collectors.groupingBy(
                            LaneParamEntity::getFeature,
                            Collectors.counting()));
            stats.put("byFeature", featureCount);

            // 可变车道统计
            long varLaneCount = allLanes.stream()
                    .mapToLong(lane -> {
                        String varList = lane.getVarMovementList();
                        return (varList != null && !varList.equals("[]")) ? 1 : 0;
                    })
                    .sum();
            stats.put("variableLanes", varLaneCount);

            // 待行区统计
            long waitingAreaCount = allLanes.stream()
                    .mapToLong(lane -> lane.getWaitingArea() != null && lane.getWaitingArea() == 1 ? 1 : 0)
                    .sum();
            stats.put("waitingAreaLanes", waitingAreaCount);

            log.info("车道统计完成: crossId={}, 总车道数={}", crossId, allLanes.size());

        } catch (Exception e) {
            log.error("获取车道统计失败: crossId={}", crossId, e);
            stats.put("error", e.getMessage());
        }

        return stats;
    }

    /**
     * 根据方向编码获取默认方位角
     */
    private Integer getDefaultAzimuth(String direction) {
        if (direction == null) return 0;

        switch (direction) {
            case "1": return 0;    // 北
            case "2": return 45;   // 东北
            case "3": return 90;   // 东
            case "4": return 135;  // 东南
            case "5": return 180;  // 南
            case "6": return 225;  // 西南
            case "7": return 270;  // 西
            case "8": return 315;  // 西北
            case "9": return 0;    // 其它
            default: return 0;
        }
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
     * 验证必填字段
     */
    private void validateRequiredFields(LaneParamEntity entity) {
        if (entity.getCrossId() == null || entity.getCrossId().trim().isEmpty()) {
            throw new IllegalArgumentException("路口编号不能为空");
        }

        if (entity.getLaneNo() == null) {
            throw new IllegalArgumentException("车道序号不能为空");
        }

        if (entity.getDirection() == null || entity.getDirection().trim().isEmpty()) {
            throw new IllegalArgumentException("车道方向不能为空");
        }

        if (entity.getMovement() == null || entity.getMovement().trim().isEmpty()) {
            throw new IllegalArgumentException("车道转向不能为空");
        }

        // 验证方向值 [按实际编码]
        String direction = entity.getDirection();
        if (!Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9").contains(direction)) {
            throw new IllegalArgumentException("车道方向必须是 1-9 之一 (1-北,2-东北,3-东,4-东南,5-南,6-西南,7-西,8-西北,9-其它)");
        }

        // 验证车道序号范围
        if (entity.getLaneNo() < 1 || entity.getLaneNo() > 99) {
            throw new IllegalArgumentException("车道序号必须在 1-99 之间");
        }

        // 验证转向编码 [按实际编码]
        String movement = entity.getMovement();
        List<String> validMovements = Arrays.asList(
                "11", "12", "13",        // 直行、左转、右转
                "21", "22", "23", "24",  // 混行
                "31", "32", "33", "34",  // 调头
                "99"                     // 其它
        );
        if (!validMovements.contains(movement)) {
            throw new IllegalArgumentException("车道转向编码无效: " + movement);
        }

        // 验证特性编码 [按实际编码]
        if (entity.getFeature() != null) {
            List<Integer> validFeatures = Arrays.asList(1, 2, 3, 4, 9);
            if (!validFeatures.contains(entity.getFeature())) {
                throw new IllegalArgumentException("车道特性编码无效: " + entity.getFeature() +
                        " (1-机动车,2-非机动车,3-机非混合,4-行人便道,9-其它)");
            }
        }

        // 验证属性编码 [按实际编码]
        if (entity.getAttribute() != null) {
            List<Integer> validAttributes = Arrays.asList(0, 1, 2, 3, 9);
            if (!validAttributes.contains(entity.getAttribute())) {
                throw new IllegalArgumentException("车道属性编码无效: " + entity.getAttribute() +
                        " (0-路口进口,1-路口出口,2-匝道,3-路段车道,9-其它)");
            }
        }
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues(LaneParamEntity entity) {
        // 设置默认属性
        if (entity.getAttribute() == null) {
            entity.setAttribute(0); // 默认为路口进口
        }

        // 设置默认特性
        if (entity.getFeature() == null) {
            entity.setFeature(1); // 默认为机动车道
        }

        // 设置默认方位角
        if (entity.getAzimuth() == null && entity.getDirection() != null) {
            entity.setAzimuth(getDefaultAzimuth(entity.getDirection()));
        }

        // 设置默认待行区
        if (entity.getWaitingArea() == null) {
            entity.setWaitingArea(0); // 默认无待行区
        }

        // 设置默认可变转向列表
        if (entity.getVarMovementList() == null || entity.getVarMovementList().trim().isEmpty()) {
            // 根据转向设置默认可变转向 [按实际编码]
            if ("11".equals(entity.getMovement())) { // 直行车道
                entity.setVarMovementList("[\"11\", \"12\", \"13\"]"); // 支持直行、左转、右转
            } else if ("24".equals(entity.getMovement())) { // 左直右混行车道
                entity.setVarMovementList("[\"11\", \"12\", \"13\"]"); // 已经是混行
            } else {
                entity.setVarMovementList("[]"); // 其他车道默认无可变转向
            }
        }
    }

    /**
     * 更新车道参数（使用Entity）
     */
    public LaneParamEntity updateLaneParam(LaneParamEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("车道参数实体不能为空");
        }

        if (entity.getId() == null) {
            throw new IllegalArgumentException("更新操作需要实体ID");
        }

        // 验证必填字段
        validateRequiredFields(entity);

        // 设置更新时间
        entity.setUpdatedTime(LocalDateTime.now());

        // 更新到数据库
        laneParamRepository.updateById(entity);

        log.info("更新车道参数: id={}, crossId={}, laneNo={}",
                entity.getId(), entity.getCrossId(), entity.getLaneNo());

        return entity;
    }

    /**
     * 保存或更新车道参数（智能判断）
     */
    public LaneParamEntity saveOrUpdateLaneParam(LaneParamEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("车道参数实体不能为空");
        }

        if (entity.getId() != null) {
            // 有ID，执行更新
            return updateLaneParam(entity);
        } else {
            // 无ID，检查是否已存在
            LaneParamEntity existing = laneParamRepository.findByCrossIdAndLaneNo(
                    entity.getCrossId(), entity.getLaneNo());

            if (existing != null) {
                // 已存在，复制ID后更新
                entity.setId(existing.getId());
                entity.setCreatedTime(existing.getCreatedTime()); // 保持原创建时间
                return updateLaneParam(entity);
            } else {
                // 不存在，创建新记录
                return createLaneParam(entity);
            }
        }
    }
}
