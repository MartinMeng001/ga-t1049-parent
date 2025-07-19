package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.LampGroupParamEntity;
import com.traffic.gat1049.repository.interfaces.LampGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 信号灯组参数测试服务
 * 演示 LampGroupRepository 的使用
 */
@Service
@Transactional
@Slf4j
public class TestLampGroupService {

    @Autowired
    private LampGroupRepository lampGroupRepository;

    // 方向字典
    private static final Map<String, String> DIRECTION_DICT = Map.of(
            "E", "东向",
            "W", "西向",
            "S", "南向",
            "N", "北向"
    );

    // 类型字典
    private static final Map<String, String> TYPE_DICT = Map.of(
            "01", "机动车信号灯",
            "02", "左转信号灯",
            "03", "直行信号灯",
            "04", "右转信号灯",
            "05", "人行信号灯",
            "06", "非机动车信号灯"
    );

    /**
     * 创建信号灯组
     */
    public LampGroupParamEntity createLampGroup(String crossId, Integer lampGroupNo, String direction, String type) {
        log.info("开始创建信号灯组: crossId={}, lampGroupNo={}, direction={}, type={}",
                crossId, lampGroupNo, direction, type);

        // 检查是否已存在
        Boolean exists = lampGroupRepository.existsByCrossIdAndLampGroupNo(crossId, lampGroupNo);
        if (exists) {
            throw new IllegalArgumentException("信号灯组已存在: crossId=" + crossId + ", lampGroupNo=" + lampGroupNo);
        }

        // 验证参数
        validateParameters(crossId, lampGroupNo, direction, type);

        // 创建新的实体
        LampGroupParamEntity entity = new LampGroupParamEntity();
        entity.setCrossId(crossId);
        entity.setLampGroupNo(lampGroupNo);
        entity.setDirection(direction);
        entity.setType(type);
        entity.setValid(1);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        // 保存到数据库
        int result = lampGroupRepository.insert(entity);
        if (result <= 0) {
            throw new RuntimeException("创建信号灯组失败");
        }

        log.info("信号灯组创建成功: id={}", entity.getId());
        return entity;
    }

    /**
     * 查询单个信号灯组
     */
    public LampGroupParamEntity getLampGroup(String crossId, Integer lampGroupNo) {
        log.info("查询信号灯组: crossId={}, lampGroupNo={}", crossId, lampGroupNo);

        LampGroupParamEntity entity = lampGroupRepository.findByCrossIdAndLampGroupNo(crossId, lampGroupNo);
        if (entity == null) {
            throw new IllegalArgumentException("信号灯组不存在: crossId=" + crossId + ", lampGroupNo=" + lampGroupNo);
        }

        return entity;
    }

    /**
     * 查询路口所有信号灯组
     */
    public List<LampGroupParamEntity> getLampGroupsByCrossId(String crossId) {
        log.info("查询路口所有信号灯组: crossId={}", crossId);

        if (!StringUtils.hasText(crossId)) {
            throw new IllegalArgumentException("路口编号不能为空");
        }

        return lampGroupRepository.findByValidCrossId(crossId);
    }

    /**
     * 根据方向查询信号灯组
     */
    public List<LampGroupParamEntity> getLampGroupsByDirection(String crossId, String direction) {
        log.info("根据方向查询信号灯组: crossId={}, direction={}", crossId, direction);

        validateDirection(direction);
        return lampGroupRepository.findByCrossIdAndDirection(crossId, direction);
    }

    /**
     * 根据类型查询信号灯组
     */
    public List<LampGroupParamEntity> getLampGroupsByType(String crossId, String type) {
        log.info("根据类型查询信号灯组: crossId={}, type={}", crossId, type);

        validateType(type);
        return lampGroupRepository.findByCrossIdAndType(crossId, type);
    }

    /**
     * 批量创建信号灯组
     */
    public List<LampGroupParamEntity> createBatchLampGroups(String crossId, int count) {
        log.info("批量创建信号灯组: crossId={}, count={}", crossId, count);

        List<LampGroupParamEntity> results = new ArrayList<>();

        // 获取当前最大编号
        Integer maxLampGroupNo = lampGroupRepository.getMaxLampGroupNo(crossId);
        int startNo = maxLampGroupNo != null ? maxLampGroupNo + 1 : 1;

        // 预定义的方向和类型组合
        String[] directions = {"E", "W", "S", "N"};
        String[] types = {"01", "02"};  // 机动车和左转

        for (int i = 0; i < count; i++) {
            try {
                int lampGroupNo = startNo + i;
                String direction = directions[i % directions.length];
                String type = types[i % types.length];

                LampGroupParamEntity entity = new LampGroupParamEntity();
                entity.setCrossId(crossId);
                entity.setLampGroupNo(lampGroupNo);
                entity.setDirection(direction);
                entity.setType(type);
                entity.setCreatedTime(LocalDateTime.now());
                entity.setUpdatedTime(LocalDateTime.now());

                int result = lampGroupRepository.insert(entity);
                if (result > 0) {
                    results.add(entity);
                    log.debug("创建信号灯组成功: lampGroupNo={}, direction={}, type={}",
                            lampGroupNo, direction, type);
                }
            } catch (Exception e) {
                log.warn("批量创建第{}个信号灯组失败: {}", i + 1, e.getMessage());
            }
        }

        log.info("批量创建完成: 成功={}, 总数={}", results.size(), count);
        return results;
    }

    /**
     * 更新信号灯组
     */
    public LampGroupParamEntity updateLampGroup(String crossId, Integer lampGroupNo, String direction, String type) {
        log.info("更新信号灯组: crossId={}, lampGroupNo={}, direction={}, type={}",
                crossId, lampGroupNo, direction, type);

        // 检查是否存在
        LampGroupParamEntity existing = lampGroupRepository.findByCrossIdAndLampGroupNo(crossId, lampGroupNo);
        if (existing == null) {
            throw new IllegalArgumentException("信号灯组不存在: crossId=" + crossId + ", lampGroupNo=" + lampGroupNo);
        }

        // 验证参数
        validateDirection(direction);
        validateType(type);

        // 更新
        int result = lampGroupRepository.updateLampGroup(crossId, lampGroupNo, direction, type);
        if (result <= 0) {
            throw new RuntimeException("更新信号灯组失败");
        }

        // 返回更新后的实体
        return lampGroupRepository.findByCrossIdAndLampGroupNo(crossId, lampGroupNo);
    }

    /**
     * 删除路口所有信号灯组
     */
    public int deleteAllByCrossId(String crossId) {
        log.info("删除路口所有信号灯组: crossId={}", crossId);

        if (!StringUtils.hasText(crossId)) {
            throw new IllegalArgumentException("路口编号不能为空");
        }

        int result = lampGroupRepository.deleteByCrossId(crossId);
        log.info("删除完成: crossId={}, 删除数量={}", crossId, result);

        return result;
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics(String crossId) {
        log.info("获取统计信息: crossId={}", crossId);

        Map<String, Object> stats = new HashMap<>();

        // 总数统计
        Integer totalCount = lampGroupRepository.countByCrossId(crossId);
        stats.put("totalCount", totalCount);

        // 最大编号
        Integer maxLampGroupNo = lampGroupRepository.getMaxLampGroupNo(crossId);
        stats.put("maxLampGroupNo", maxLampGroupNo);

        // 按类型统计
        List<LampGroupRepository.LampGroupTypeCount> typeCounts = lampGroupRepository.countByLampGroupType(crossId);
        Map<String, Object> typeStatistics = new HashMap<>();
        for (LampGroupRepository.LampGroupTypeCount typeCount : typeCounts) {
            String typeName = TYPE_DICT.getOrDefault(typeCount.getType(), typeCount.getType());
            typeStatistics.put(typeCount.getType() + "(" + typeName + ")", typeCount.getCount());
        }
        stats.put("typeStatistics", typeStatistics);

        // 按方向统计
        Map<String, Integer> directionStats = new HashMap<>();
        for (String direction : DIRECTION_DICT.keySet()) {
            List<LampGroupParamEntity> entities = lampGroupRepository.findByCrossIdAndDirection(crossId, direction);
            String directionName = DIRECTION_DICT.get(direction);
            directionStats.put(direction + "(" + directionName + ")", entities.size());
        }
        stats.put("directionStatistics", directionStats);

        // 数据字典
        List<String> allDirections = lampGroupRepository.findAllDirections();
        List<String> allTypes = lampGroupRepository.findAllTypes();
        stats.put("availableDirections", allDirections);
        stats.put("availableTypes", allTypes);

        return stats;
    }

    /**
     * 获取数据字典
     */
    public Map<String, Object> getDictionary() {
        log.info("获取数据字典");

        Map<String, Object> dictionary = new HashMap<>();

        // 方向字典
        Map<String, String> directionDict = new HashMap<>();
        for (Map.Entry<String, String> entry : DIRECTION_DICT.entrySet()) {
            directionDict.put(entry.getKey(), entry.getValue());
        }
        dictionary.put("directions", directionDict);

        // 类型字典
        Map<String, String> typeDict = new HashMap<>();
        for (Map.Entry<String, String> entry : TYPE_DICT.entrySet()) {
            typeDict.put(entry.getKey(), entry.getValue());
        }
        dictionary.put("types", typeDict);

        // 数据库中已使用的值
        List<String> usedDirections = lampGroupRepository.findAllDirections();
        List<String> usedTypes = lampGroupRepository.findAllTypes();
        dictionary.put("usedDirections", usedDirections);
        dictionary.put("usedTypes", usedTypes);

        // 示例数据
        Map<String, Object> examples = new HashMap<>();
        examples.put("crossId", "11010000100001");
        examples.put("lampGroupNo", "1-99");
        examples.put("direction", "E/W/S/N");
        examples.put("type", "01/02/03/04/05/06");
        dictionary.put("examples", examples);

        return dictionary;
    }

    /**
     * 验证参数
     */
    private void validateParameters(String crossId, Integer lampGroupNo, String direction, String type) {
        if (!StringUtils.hasText(crossId)) {
            throw new IllegalArgumentException("路口编号不能为空");
        }

        if (crossId.length() != 14) {
            throw new IllegalArgumentException("路口编号长度必须为14位");
        }

        if (lampGroupNo == null || lampGroupNo < 1 || lampGroupNo > 99) {
            throw new IllegalArgumentException("信号灯组序号必须在1-99之间");
        }

        validateDirection(direction);
        validateType(type);
    }

    /**
     * 验证方向
     */
    private void validateDirection(String direction) {
        if (!StringUtils.hasText(direction)) {
            throw new IllegalArgumentException("方向不能为空");
        }

//        if (!DIRECTION_DICT.containsKey(direction)) {
//            throw new IllegalArgumentException("无效的方向代码: " + direction + ", 有效值: " + DIRECTION_DICT.keySet());
//        }
    }

    /**
     * 验证类型
     */
    private void validateType(String type) {
        if (!StringUtils.hasText(type)) {
            throw new IllegalArgumentException("类型不能为空");
        }

//        if (!TYPE_DICT.containsKey(type)) {
//            throw new IllegalArgumentException("无效的类型代码: " + type + ", 有效值: " + TYPE_DICT.keySet());
//        }
    }

    /**
     * 测试复杂查询功能
     */
    public Map<String, Object> testComplexQueries(String crossId) {
        log.info("测试复杂查询功能: crossId={}", crossId);

        Map<String, Object> results = new HashMap<>();

        try {
            // 1. 查询指定方向和类型的信号灯组
//            List<LampGroupParamEntity> eastMotorGroups = lampGroupRepository.findByCrossIdAndDirectionAndType(crossId, "E", "01");
//            results.put("eastMotorGroups", eastMotorGroups);

            // 2. 查询完整信息（包含关联的信号组）
            List<LampGroupRepository.LampGroupWithSignalGroups> completeInfo =
                    lampGroupRepository.findCompleteInfoByCrossId(crossId);
            results.put("completeInfo", completeInfo);

            // 3. 获取指定方向的灯组编号列表
            List<Integer> eastLampGroupNos = lampGroupRepository.getLampGroupNosByCrossIdAndDirection(crossId, "E");
            results.put("eastLampGroupNos", eastLampGroupNos);

            // 4. 类型统计
            List<LampGroupRepository.LampGroupTypeCount> typeStats = lampGroupRepository.countByLampGroupType(crossId);
            results.put("typeStatistics", typeStats);

            results.put("queryTime", LocalDateTime.now());
            results.put("success", true);

        } catch (Exception e) {
            log.error("复杂查询测试失败", e);
            results.put("error", e.getMessage());
            results.put("success", false);
        }

        return results;
    }

    /**
     * 批量插入测试
     */
    public List<LampGroupParamEntity> testBatchInsert(String crossId, int count) {
        log.info("测试批量插入: crossId={}, count={}", crossId, count);

        List<LampGroupParamEntity> entities = new ArrayList<>();
        Integer maxNo = lampGroupRepository.getMaxLampGroupNo(crossId);
        int startNo = maxNo != null ? maxNo + 1 : 1;

        // 构建批量插入的数据
        String[] directions = {"E", "W", "S", "N"};
        String[] types = {"01", "02", "03", "04"};

        for (int i = 0; i < count; i++) {
            LampGroupParamEntity entity = new LampGroupParamEntity();
            entity.setCrossId(crossId);
            entity.setLampGroupNo(startNo + i);
            entity.setDirection(directions[i % directions.length]);
            entity.setType(types[i % types.length]);

            entities.add(entity);
        }

        // 执行批量插入
        int result = lampGroupRepository.batchInsert(entities);
        log.info("批量插入完成: 请求={}, 成功={}", count, result);

        // 返回实际插入的数据
        return lampGroupRepository.findByCrossId(crossId);
    }

    /**
     * 测试数据一致性检查
     */
    public Map<String, Object> testDataConsistency(String crossId) {
        log.info("测试数据一致性: crossId={}", crossId);

        Map<String, Object> results = new HashMap<>();
        List<String> issues = new ArrayList<>();

        try {
            // 查询所有数据
            List<LampGroupParamEntity> allGroups = lampGroupRepository.findByCrossId(crossId);
            results.put("totalCount", allGroups.size());

            // 检查编号连续性
            Set<Integer> lampGroupNos = new HashSet<>();
            for (LampGroupParamEntity group : allGroups) {
                if (lampGroupNos.contains(group.getLampGroupNo())) {
                    issues.add("重复的灯组编号: " + group.getLampGroupNo());
                }
                lampGroupNos.add(group.getLampGroupNo());
            }

            // 检查方向有效性
            for (LampGroupParamEntity group : allGroups) {
                if (!DIRECTION_DICT.containsKey(group.getDirection())) {
                    issues.add("无效方向: lampGroupNo=" + group.getLampGroupNo() + ", direction=" + group.getDirection());
                }
            }

            // 检查类型有效性
            for (LampGroupParamEntity group : allGroups) {
                if (!TYPE_DICT.containsKey(group.getType())) {
                    issues.add("无效类型: lampGroupNo=" + group.getLampGroupNo() + ", type=" + group.getType());
                }
            }

            // 检查时间字段
            for (LampGroupParamEntity group : allGroups) {
                if (group.getCreatedTime() == null) {
                    issues.add("缺少创建时间: lampGroupNo=" + group.getLampGroupNo());
                }
                if (group.getUpdatedTime() == null) {
                    issues.add("缺少更新时间: lampGroupNo=" + group.getLampGroupNo());
                }
            }

            results.put("issues", issues);
            results.put("isConsistent", issues.isEmpty());
            results.put("checkTime", LocalDateTime.now());

        } catch (Exception e) {
            log.error("数据一致性检查失败", e);
            results.put("error", e.getMessage());
            results.put("isConsistent", false);
        }

        return results;
    }
}
