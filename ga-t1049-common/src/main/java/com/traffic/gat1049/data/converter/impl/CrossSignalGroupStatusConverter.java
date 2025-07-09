// ================================================================
// CrossSignalGroupStatus转换器实现
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.runtime.CrossSignalGroupStatus;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.repository.entity.CrossSignalGroupStatusEntity;
import com.traffic.gat1049.repository.interfaces.CrossSignalGroupStatusRepository;
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
 * 路口信号组状态转换器实现
 * 演示一对多关系的数据转换模式
 */
@Component
public class CrossSignalGroupStatusConverter extends AbstractEntityConverter<CrossSignalGroupStatusEntity, CrossSignalGroupStatus> {

    @Autowired
    private CrossSignalGroupStatusRepository repository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public CrossSignalGroupStatus toProtocol(CrossSignalGroupStatusEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossSignalGroupStatus protocol = new CrossSignalGroupStatus();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());

            // 时间格式转换
            if (entity.getLampStatusTime() != null) {
                protocol.setLampStatusTime(entity.getLampStatusTime().format(DATETIME_FORMATTER));
            }

            // 查询该路口在同一时间点的所有信号组状态
            List<CrossSignalGroupStatusEntity> allStatusEntities =
                    repository.findLatestStatusByCrossId(entity.getCrossId());

            // 转换为信号组状态列表
            List<SignalGroupStatus> statusList = allStatusEntities.stream()
                    .map(this::convertToSignalGroupStatus)
                    .collect(Collectors.toList());

            protocol.setSignalGroupStatusList(statusList);

            validateConversion(entity, protocol);

            logger.debug("路口信号组状态实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("路口信号组状态转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口信号组状态转换失败", e);
        }
    }

    @Override
    public CrossSignalGroupStatusEntity toEntity(CrossSignalGroupStatus protocol) {
        // 注意：由于CrossSignalGroupStatus包含多个信号组状态，这里只返回第一个信号组的实体
        // 完整的转换应该使用toEntityList方法
        if (protocol == null || protocol.getSignalGroupStatusList() == null
                || protocol.getSignalGroupStatusList().isEmpty()) {
            return null;
        }

        try {
            SignalGroupStatus firstStatus = protocol.getSignalGroupStatusList().get(0);
            return createEntityFromProtocol(protocol, firstStatus);

        } catch (Exception e) {
            logger.error("路口信号组状态转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("路口信号组状态转换失败", e);
        }
    }

    @Override
    public List<CrossSignalGroupStatusEntity> toEntityList(List<CrossSignalGroupStatus> protocols) {
        if (protocols == null || protocols.isEmpty()) {
            return new ArrayList<>();
        }

        List<CrossSignalGroupStatusEntity> entities = new ArrayList<>();

        for (CrossSignalGroupStatus protocol : protocols) {
            if (protocol.getSignalGroupStatusList() != null) {
                for (SignalGroupStatus signalGroupStatus : protocol.getSignalGroupStatusList()) {
                    CrossSignalGroupStatusEntity entity = createEntityFromProtocol(protocol, signalGroupStatus);
                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    @Override
    public void updateEntity(CrossSignalGroupStatus protocol, CrossSignalGroupStatusEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 更新路口ID
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }

            // 更新时间
            if (StringUtils.hasText(protocol.getLampStatusTime())) {
                LocalDateTime lampStatusTime = LocalDateTime.parse(
                        protocol.getLampStatusTime(), DATETIME_FORMATTER);
                entity.setLampStatusTime(lampStatusTime);
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("路口信号组状态实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("路口信号组状态更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口信号组状态更新失败", e);
        }
    }

    /**
     * 将实体转换为信号组状态
     */
    private SignalGroupStatus convertToSignalGroupStatus(CrossSignalGroupStatusEntity entity) {
        SignalGroupStatus status = new SignalGroupStatus();
        status.setSignalGroupNo(entity.getSignalGroupNo());
        status.setLampStatus(entity.getLampStatus());
        // 注意：原实体中没有RemainTime字段，这里设为0或从其他地方获取
        status.setRemainTime(0);
        return status;
    }

    /**
     * 从协议对象和信号组状态创建实体
     */
    private CrossSignalGroupStatusEntity createEntityFromProtocol(
            CrossSignalGroupStatus protocol, SignalGroupStatus signalGroupStatus) {

        CrossSignalGroupStatusEntity entity = new CrossSignalGroupStatusEntity();

        // 基础字段映射
        entity.setCrossId(protocol.getCrossId());
        entity.setSignalGroupNo(signalGroupStatus.getSignalGroupNo());
        entity.setLampStatus(signalGroupStatus.getLampStatus());

        // 时间转换
        if (StringUtils.hasText(protocol.getLampStatusTime())) {
            LocalDateTime lampStatusTime = LocalDateTime.parse(
                    protocol.getLampStatusTime(), DATETIME_FORMATTER);
            entity.setLampStatusTime(lampStatusTime);
        }

        // 设置审计字段
        setEntityAuditFields(entity, true);

        return entity;
    }

    /**
     * 批量保存信号组状态
     */
    public void saveBatch(CrossSignalGroupStatus protocol) {
        if (protocol == null || protocol.getSignalGroupStatusList() == null) {
            return;
        }

        List<CrossSignalGroupStatusEntity> entities = new ArrayList<>();
        for (SignalGroupStatus signalGroupStatus : protocol.getSignalGroupStatusList()) {
            CrossSignalGroupStatusEntity entity = createEntityFromProtocol(protocol, signalGroupStatus);
            entities.add(entity);
        }

        repository.batchInsert(entities);
        logger.info("批量保存路口信号组状态成功: crossId={}, count={}",
                protocol.getCrossId(), entities.size());
    }

    /**
     * 根据路口ID获取最新状态
     */
    public CrossSignalGroupStatus getLatestStatusByCrossId(String crossId) {
        if (!StringUtils.hasText(crossId)) {
            throw new DataConversionException("路口ID不能为空");
        }

        List<CrossSignalGroupStatusEntity> entities = repository.findLatestStatusByCrossId(crossId);
        if (entities.isEmpty()) {
            return null;
        }

        // 使用第一个实体创建协议对象框架
        CrossSignalGroupStatusEntity firstEntity = entities.get(0);
        CrossSignalGroupStatus protocol = new CrossSignalGroupStatus();
        protocol.setCrossId(crossId);

        if (firstEntity.getLampStatusTime() != null) {
            protocol.setLampStatusTime(firstEntity.getLampStatusTime().format(DATETIME_FORMATTER));
        }

        // 转换所有信号组状态
        List<SignalGroupStatus> statusList = entities.stream()
                .map(this::convertToSignalGroupStatus)
                .collect(Collectors.toList());

        protocol.setSignalGroupStatusList(statusList);

        return protocol;
    }
}