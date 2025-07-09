// ================================================================
// StageTrafficData转换器实现
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficFlowData;
import com.traffic.gat1049.repository.entity.StageTrafficDataEntity;
import com.traffic.gat1049.repository.interfaces.StageTrafficDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 阶段交通数据转换器实现
 * 演示阶段级交通数据聚合转换模式
 */
@Component
public class StageTrafficDataConverter extends AbstractEntityConverter<StageTrafficDataEntity, StageTrafficData> {

    @Autowired
    private StageTrafficDataRepository repository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public StageTrafficData toProtocol(StageTrafficDataEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            StageTrafficData protocol = new StageTrafficData();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setStageNo(entity.getStageNo());

            // 时间格式转换
            if (entity.getStartTime() != null) {
                protocol.setStartTime(entity.getStartTime().format(DATETIME_FORMATTER));
            }
            if (entity.getEndTime() != null) {
                protocol.setEndTime(entity.getEndTime().format(DATETIME_FORMATTER));
            }

            // 查询该路口该阶段的所有车道交通数据
            List<StageTrafficDataEntity> allStageEntities = repository.findByStage(
                    entity.getCrossId(),
                    entity.getStageNo(),
                    entity.getStartTime(),
                    entity.getEndTime()
            );

            // 按方向和车道分组并转换为阶段交通流数据列表
            Map<String, List<StageTrafficDataEntity>> groupedData = allStageEntities.stream()
                    .collect(Collectors.groupingBy(e ->  "_" + e.getLaneNo()));

            List<StageTrafficFlowData> flowDataList = new ArrayList<>();
            for (Map.Entry<String, List<StageTrafficDataEntity>> entry : groupedData.entrySet()) {
                StageTrafficFlowData flowData = convertToStageTrafficFlowData(entry.getValue());
                if (flowData != null) {
                    flowDataList.add(flowData);
                }
            }

            protocol.setDataList(flowDataList);

            validateConversion(entity, protocol);

            logger.debug("阶段交通数据实体更新成功: crossId={}, stageNo={}",
                    entity.getCrossId(), entity.getStageNo());
            return protocol;
        } catch (Exception e) {
            logger.error("阶段交通数据更新失败: crossId={}, stageNo={}",
                    entity.getCrossId(), entity.getStageNo(), e);
            throw new DataConversionException("阶段交通数据更新失败", e);
        }
    }

    @Override
    public StageTrafficDataEntity toEntity(StageTrafficData protocol) {
        if (protocol == null || protocol.getDataList() == null
                || protocol.getDataList().isEmpty()) {
            return null;
        }

        try {
            StageTrafficFlowData firstFlowData = protocol.getDataList().get(0);
            return createEntityFromProtocol(protocol, firstFlowData);

        } catch (Exception e) {
            logger.error("阶段交通数据转换失败: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo(), e);
            throw new DataConversionException("阶段交通数据转换失败", e);
        }
    }

    /**
     * 将实体列表转换为阶段交通流数据（聚合同一方向车道的数据）
     */
    private StageTrafficFlowData convertToStageTrafficFlowData(List<StageTrafficDataEntity> entities) {
        if (entities.isEmpty()) {
            return null;
        }

        // 使用第一个实体的信息作为基础
        StageTrafficDataEntity firstEntity = entities.get(0);
        StageTrafficFlowData flowData = new StageTrafficFlowData();

        // 根据车道号推算方向号（这里使用简单的算法，实际可能需要更复杂的逻辑）
        //int directionNo = (firstEntity.getLaneNo() - 1) / 3 + 1; // 假设每个方向3条车道
        //flowData.setDirectionNo(directionNo);
        flowData.setLaneNo(firstEntity.getLaneNo());

        // 聚合统计数据（求和或平均值）
        int totalVolume = entities.stream().mapToInt(e -> e.getVehicleNum() != null ? e.getVehicleNum() : 0).sum();
        double avgOccupancy = entities.stream().mapToDouble(e -> e.getOccupancy() != null ? e.getOccupancy() : 0.0).average().orElse(0.0);
        double avgHeadway = entities.stream().mapToDouble(e -> e.getHeadTime() != null ? e.getHeadTime() / 10.0 : 0.0).average().orElse(0.0); // 转换为秒

        // 阶段交通数据中没有速度字段，使用默认值或从其他地方推算
        double avgSpeed = 40.0; // 默认速度

        flowData.setVehicleNum(totalVolume);
        flowData.setOccupancy((int)avgOccupancy); // 转换为小数形式
        //flowData.setSpeed(avgSpeed);
        //flowData.setHeadway(avgHeadway);

        return flowData;
    }

    /**
     * 从协议对象和交通流数据创建实体
     */
    private StageTrafficDataEntity createEntityFromProtocol(
            StageTrafficData protocol, StageTrafficFlowData flowData) {

        StageTrafficDataEntity entity = new StageTrafficDataEntity();

        // 基础字段映射
        entity.setCrossId(protocol.getCrossId());
        entity.setStageNo(protocol.getStageNo());
        //entity.setDirectionNo(flowData.getDirectionNo());
        entity.setLaneNo(flowData.getLaneNo());

        // 时间转换
        if (StringUtils.hasText(protocol.getStartTime())) {
            LocalDateTime startTime = LocalDateTime.parse(
                    protocol.getStartTime(), DATETIME_FORMATTER);
            entity.setStartTime(startTime);
        }

        if (StringUtils.hasText(protocol.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(
                    protocol.getEndTime(), DATETIME_FORMATTER);
            entity.setEndTime(endTime);
        }

        // 交通数据
        entity.setVehicleNum(flowData.getVehicleNum());
        entity.setOccupancy(flowData.getOccupancy());
//        entity.setSpeed(flowData.getSpeed());
//        entity.setHeadway(flowData.getHeadway());

        // 设置审计字段
        setEntityAuditFields(entity, true);

        return entity;
    }

    /**
     * 批量保存阶段交通数据
     */
    public void saveBatch(StageTrafficData protocol) {
        if (protocol == null || protocol.getDataList() == null) {
            return;
        }

        List<StageTrafficDataEntity> entities = new ArrayList<>();
        for (StageTrafficFlowData flowData : protocol.getDataList()) {
            StageTrafficDataEntity entity = createEntityFromProtocol(protocol, flowData);
            entities.add(entity);
        }

        repository.batchInsert(entities);
        logger.info("批量保存阶段交通数据成功: crossId={}, stageNo={}, count={}",
                protocol.getCrossId(), protocol.getStageNo(), entities.size());
    }

    /**
     * 根据路口ID和阶段号获取交通数据
     */
    public List<StageTrafficData> getStageTrafficDataByCross(String crossId) {
        if (!StringUtils.hasText(crossId)) {
            throw new DataConversionException("路口ID不能为空");
        }

        List<StageTrafficDataEntity> entities = repository.findByCrossId(crossId);
        if (entities.isEmpty()) {
            return new ArrayList<>();
        }

        // 按阶段分组
        Map<Integer, List<StageTrafficDataEntity>> stageDataMap = entities.stream()
                .collect(Collectors.groupingBy(StageTrafficDataEntity::getStageNo));

        List<StageTrafficData> protocolList = new ArrayList<>();
        for (Map.Entry<Integer, List<StageTrafficDataEntity>> entry : stageDataMap.entrySet()) {
            StageTrafficData protocol = buildStageTrafficData(crossId, entry.getKey(), entry.getValue());
            if (protocol != null) {
                protocolList.add(protocol);
            }
        }

        return protocolList;
    }

    /**
     * 根据路口ID、阶段号和时间范围获取交通数据
     */
    public StageTrafficData getStageTrafficDataByTimeRange(String crossId,
                                                           Integer stageNo,
                                                           LocalDateTime startTime,
                                                           LocalDateTime endTime) {
        if (!StringUtils.hasText(crossId) || stageNo == null) {
            throw new DataConversionException("路口ID和阶段号不能为空");
        }

        List<StageTrafficDataEntity> entities = repository.findByStage(crossId, stageNo, startTime, endTime);
        if (entities.isEmpty()) {
            return null;
        }

        return buildStageTrafficData(crossId, stageNo, entities);
    }

    /**
     * 构建阶段交通数据协议对象
     */
    private StageTrafficData buildStageTrafficData(String crossId, Integer stageNo,
                                                   List<StageTrafficDataEntity> entities) {
        if (entities.isEmpty()) {
            return null;
        }

        StageTrafficData protocol = new StageTrafficData();
        protocol.setCrossId(crossId);
        protocol.setStageNo(stageNo);

        // 使用第一个和最后一个实体确定时间范围
        StageTrafficDataEntity firstEntity = entities.get(0);
        StageTrafficDataEntity lastEntity = entities.get(entities.size() - 1);

        if (firstEntity.getStartTime() != null) {
            protocol.setStartTime(firstEntity.getStartTime().format(DATETIME_FORMATTER));
        }
        if (lastEntity.getEndTime() != null) {
            protocol.setEndTime(lastEntity.getEndTime().format(DATETIME_FORMATTER));
        }

        // 按方向和车道分组并转换
        Map<String, List<StageTrafficDataEntity>> groupedData = entities.stream()
                .collect(Collectors.groupingBy(e -> {
                    // 根据车道号推算方向号
                    int directionNo = (e.getLaneNo() - 1) / 3 + 1;
                    return directionNo + "_" + e.getLaneNo();
                }));

        List<StageTrafficFlowData> flowDataList = new ArrayList<>();
        for (Map.Entry<String, List<StageTrafficDataEntity>> entry : groupedData.entrySet()) {
            StageTrafficFlowData flowData = convertToStageTrafficFlowData(entry.getValue());
            if (flowData != null) {
                flowDataList.add(flowData);
            }
        }

        protocol.setDataList(flowDataList);
        return protocol;
    }

    /**
     * 获取最新阶段交通数据
     */
    public List<StageTrafficData> getLatestStageTrafficData(String crossId, int limit) {
        if (!StringUtils.hasText(crossId)) {
            throw new DataConversionException("路口ID不能为空");
        }

        List<StageTrafficDataEntity> entities = repository.findLatestData(crossId, limit);
        if (entities.isEmpty()) {
            return new ArrayList<>();
        }

        // 按阶段分组并转换
        Map<Integer, List<StageTrafficDataEntity>> stageDataMap = entities.stream()
                .collect(Collectors.groupingBy(StageTrafficDataEntity::getStageNo));

        List<StageTrafficData> protocolList = new ArrayList<>();
        for (Map.Entry<Integer, List<StageTrafficDataEntity>> entry : stageDataMap.entrySet()) {
            StageTrafficData protocol = buildStageTrafficData(crossId, entry.getKey(), entry.getValue());
            if (protocol != null) {
                protocolList.add(protocol);
            }
        }

        return protocolList;
    }

    /**
     * 统计阶段交通数据
     */
    public Map<String, Object> getStageTrafficStatistics(String crossId, Integer stageNo,
                                                         LocalDateTime startTime, LocalDateTime endTime) {
        if (!StringUtils.hasText(crossId) || stageNo == null) {
            throw new DataConversionException("路口ID和阶段号不能为空");
        }

        return repository.getStageTrafficSummary(crossId, stageNo, startTime, endTime);
    }
}