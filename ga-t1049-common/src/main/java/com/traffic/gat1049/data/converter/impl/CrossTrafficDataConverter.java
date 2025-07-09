// ================================================================
// CrossTrafficData转换器实现
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.LaneTrafficData;
import com.traffic.gat1049.repository.entity.CrossTrafficDataEntity;
import com.traffic.gat1049.repository.interfaces.CrossTrafficDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路口交通数据转换器实现
 * 演示一对多关系和时间聚合的数据转换模式
 */
@Component
public class CrossTrafficDataConverter extends AbstractEntityConverter<CrossTrafficDataEntity, CrossTrafficData> {

    @Autowired
    private CrossTrafficDataRepository repository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public CrossTrafficData toProtocol(CrossTrafficDataEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossTrafficData protocol = new CrossTrafficData();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());

            // 时间格式转换
            if (entity.getEndTime() != null) {
                protocol.setEndTime(entity.getEndTime().format(DATETIME_FORMATTER));
            }

            // 设置间隔时长（转换秒为分钟）
            protocol.setInterval(entity.getIntervalSeconds() != null ? entity.getIntervalSeconds() / 60 : 0);

            // 查询该路口在同一时间段的所有车道交通数据
            // 计算开始时间（结束时间减去间隔秒数）
            LocalDateTime startTime = entity.getEndTime().minusSeconds(entity.getIntervalSeconds());
            List<CrossTrafficDataEntity> allDataEntities = repository.findByTimeRange(
                    entity.getCrossId(),
                    startTime,
                    entity.getEndTime()
            );

            // 按车道分组并转换为车道交通数据列表
            Map<Integer, List<CrossTrafficDataEntity>> laneDataMap = allDataEntities.stream()
                    .collect(Collectors.groupingBy(CrossTrafficDataEntity::getLaneNo));

            List<LaneTrafficData> dataList = new ArrayList<>();
            for (Map.Entry<Integer, List<CrossTrafficDataEntity>> entry : laneDataMap.entrySet()) {
                LaneTrafficData laneData = convertToLaneTrafficData(entry.getValue());
                if (laneData != null) {
                    dataList.add(laneData);
                }
            }

            protocol.setDataList(dataList);

            validateConversion(entity, protocol);

            logger.debug("路口交通数据实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("路口交通数据转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口交通数据转换失败", e);
        }
    }

    @Override
    public CrossTrafficDataEntity toEntity(CrossTrafficData protocol) {
        // 注意：由于CrossTrafficData包含多个车道数据，这里只返回第一个车道的实体
        // 完整的转换应该使用toEntityList方法
        if (protocol == null || protocol.getDataList() == null
                || protocol.getDataList().isEmpty()) {
            return null;
        }

        try {
            LaneTrafficData firstLaneData = protocol.getDataList().get(0);
            return createEntityFromProtocol(protocol, firstLaneData);

        } catch (Exception e) {
            logger.error("路口交通数据转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("路口交通数据转换失败", e);
        }
    }

    @Override
    public List<CrossTrafficDataEntity> toEntityList(List<CrossTrafficData> protocols) {
        if (protocols == null || protocols.isEmpty()) {
            return new ArrayList<>();
        }

        List<CrossTrafficDataEntity> entities = new ArrayList<>();

        for (CrossTrafficData protocol : protocols) {
            if (protocol.getDataList() != null) {
                for (LaneTrafficData laneData : protocol.getDataList()) {
                    CrossTrafficDataEntity entity = createEntityFromProtocol(protocol, laneData);
                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    @Override
    public void updateEntity(CrossTrafficData protocol, CrossTrafficDataEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 更新路口ID
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }

            // 更新结束时间
            if (StringUtils.hasText(protocol.getEndTime())) {
                LocalDateTime endTime = LocalDateTime.parse(
                        protocol.getEndTime(), DATETIME_FORMATTER);
                entity.setEndTime(endTime);
            }

            // 更新间隔时长（分钟转秒）
            if (protocol.getInterval() != null) {
                entity.setIntervalSeconds(protocol.getInterval() * 60);
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("路口交通数据实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("路口交通数据更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口交通数据更新失败", e);
        }
    }

    /**
     * 将实体列表转换为车道交通数据（聚合同一车道的数据）
     */
    private LaneTrafficData convertToLaneTrafficData(List<CrossTrafficDataEntity> entities) {
        if (entities.isEmpty()) {
            return null;
        }

        // 使用第一个实体的信息作为基础
        CrossTrafficDataEntity firstEntity = entities.get(0);
        LaneTrafficData laneData = new LaneTrafficData();

        laneData.setLaneNo(firstEntity.getLaneNo());

        // 聚合统计数据（求和或平均值）
        int totalVolume = entities.stream().mapToInt(e -> e.getVolume() != null ? e.getVolume() : 0).sum();
        double avgOccupancy = entities.stream().mapToDouble(e -> e.getOccupancy() != null ? e.getOccupancy() : 0.0).average().orElse(0.0);
        double avgSpeed = entities.stream().mapToDouble(e -> e.getSpeed() != null ? e.getSpeed() : 0.0).average().orElse(0.0);
        double avgHeadway = entities.stream().mapToDouble(e -> e.getHeadTime() != null ? e.getHeadTime() / 10.0 : 0.0).average().orElse(0.0); // 转换为秒

        laneData.setVolume(totalVolume);
        laneData.setOccupancy((int)avgOccupancy); // 转换为小数形式
        laneData.setSpeed(BigDecimal.valueOf(avgSpeed));
        laneData.setHeadDistance(BigDecimal.valueOf(avgHeadway));

        return laneData;
    }

    /**
     * 从协议对象和车道数据创建实体
     */
    private CrossTrafficDataEntity createEntityFromProtocol(
            CrossTrafficData protocol, LaneTrafficData laneData) {

        CrossTrafficDataEntity entity = new CrossTrafficDataEntity();

        // 基础字段映射
        entity.setCrossId(protocol.getCrossId());
        entity.setLaneNo(laneData.getLaneNo());

        // 时间转换
        if (StringUtils.hasText(protocol.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(
                    protocol.getEndTime(), DATETIME_FORMATTER);
            entity.setEndTime(endTime);
        }

        // 间隔时长（分钟转秒）
        int intervalSeconds = protocol.getInterval() != null ? protocol.getInterval() * 60 : 300; // 默认5分钟
        entity.setIntervalSeconds(intervalSeconds);

        // 交通数据映射
        entity.setVolume(laneData.getVolume());
        entity.setOccupancy(laneData.getOccupancy() != null ? (int)(laneData.getOccupancy() * 100) : null); // 转换为百分比整数
        entity.setSpeed(laneData.getSpeed() != null ? laneData.getSpeed().floatValue() : null);
        entity.setHeadTime(laneData.getHeadTime() != null ? laneData.getHeadTime().intValue() : 0); // 转换为十分之一秒

        // 设置其他可能的默认值
        entity.setPcu(laneData.getVolume()); // 假设PCU等于车辆数
        entity.setSaturation(0); // 默认饱和度
        entity.setDensity(0); // 默认密度
        entity.setQueueLength(0); // 默认排队长度
        entity.setMaxQueueLength(0); // 默认最大排队长度
        entity.setAvgVehLen(450); // 默认车长4.5米

        // 设置审计字段
        setEntityAuditFields(entity, true);

        return entity;
    }

    /**
     * 批量保存交通数据
     */
    public void saveBatch(CrossTrafficData protocol) {
        if (protocol == null || protocol.getDataList() == null) {
            return;
        }

        List<CrossTrafficDataEntity> entities = new ArrayList<>();
        for (LaneTrafficData laneData : protocol.getDataList()) {
            CrossTrafficDataEntity entity = createEntityFromProtocol(protocol, laneData);
            entities.add(entity);
        }

        repository.batchInsert(entities);
        logger.info("批量保存路口交通数据成功: crossId={}, count={}",
                protocol.getCrossId(), entities.size());
    }

    /**
     * 根据路口ID和时间范围获取交通数据
     */
    public CrossTrafficData getTrafficDataByTimeRange(String crossId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        if (!StringUtils.hasText(crossId)) {
            throw new DataConversionException("路口ID不能为空");
        }

        List<CrossTrafficDataEntity> entities = repository.findByTimeRange(crossId, startTime, endTime);
        if (entities.isEmpty()) {
            return null;
        }

        // 使用第一个实体创建协议对象框架
        CrossTrafficDataEntity firstEntity = entities.get(0);
        CrossTrafficData protocol = new CrossTrafficData();
        protocol.setCrossId(crossId);

        if (firstEntity.getEndTime() != null) {
            protocol.setEndTime(firstEntity.getEndTime().format(DATETIME_FORMATTER));
        }

        protocol.setInterval(firstEntity.getIntervalSeconds() != null ? firstEntity.getIntervalSeconds() / 60 : 0);

        // 按车道分组并转换
        Map<Integer, List<CrossTrafficDataEntity>> laneDataMap = entities.stream()
                .collect(Collectors.groupingBy(CrossTrafficDataEntity::getLaneNo));

        List<LaneTrafficData> dataList = new ArrayList<>();
        for (Map.Entry<Integer, List<CrossTrafficDataEntity>> entry : laneDataMap.entrySet()) {
            LaneTrafficData laneData = convertToLaneTrafficData(entry.getValue());
            if (laneData != null) {
                dataList.add(laneData);
            }
        }

        protocol.setDataList(dataList);
        return protocol;
    }

    /**
     * 获取最新交通数据
     */
    public CrossTrafficData getLatestTrafficData(String crossId, int limit) {
        if (!StringUtils.hasText(crossId)) {
            throw new DataConversionException("路口ID不能为空");
        }

        List<CrossTrafficDataEntity> entities = repository.findLatestData(crossId, limit);
        if (entities.isEmpty()) {
            return null;
        }

        // 使用最新的实体创建协议对象
        return toProtocol(entities.get(0));
    }
}