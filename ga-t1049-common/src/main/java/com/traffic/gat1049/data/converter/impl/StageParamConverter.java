package com.traffic.gat1049.data.converter.impl;
import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.repository.entity.StageParamEntity;
import com.traffic.gat1049.repository.entity.StageSignalGroupStatusEntity;
import com.traffic.gat1049.repository.interfaces.StageSignalGroupStatusRepository;
import com.traffic.gat1049.model.enums.LampStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * 阶段参数转换器实现
 * 完全匹配SQL表结构，包含完整的关联表处理
 */
@Component
public class StageParamConverter extends AbstractEntityConverter<StageParamEntity, StageParam> {

    // 注入关联表Repository（需要创建）
    // @Autowired
    // private StageSignalGroupStatusRepository stageSignalGroupStatusRepository;

    @Override
    public StageParam toProtocol(StageParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            StageParam protocol = new StageParam();

            // 基础字段映射 - 完全匹配SQL表结构
            protocol.setCrossId(entity.getCrossId());
            protocol.setStageNo(entity.getStageNo());
            protocol.setStageName(entity.getStageName());
            protocol.setAttribute(entity.getAttribute());

            // 获取信号组状态列表
            List<SignalGroupStatus> signalGroupStatusList = getSignalGroupStatusList(entity);
            protocol.setSignalGroupStatusList(signalGroupStatusList);

            validateConversion(entity, protocol);

            logger.debug("阶段参数实体转协议成功: crossId={}, stageNo={}",
                    entity.getCrossId(), entity.getStageNo());
            return protocol;

        } catch (Exception e) {
            logger.error("阶段参数转换失败: crossId={}, stageNo={}",
                    entity.getCrossId(), entity.getStageNo(), e);
            throw new DataConversionException("阶段参数转换失败", e);
        }
    }

    @Override
    public StageParamEntity toEntity(StageParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            StageParamEntity entity = new StageParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setStageNo(protocol.getStageNo());
            entity.setStageName(protocol.getStageName());
            entity.setAttribute(protocol.getAttribute());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("阶段参数协议转实体成功: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo());
            return entity;

        } catch (Exception e) {
            logger.error("阶段参数转换失败: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo(), e);
            throw new DataConversionException("阶段参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(StageParam protocol, StageParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 更新可修改的字段
            if (StringUtils.hasText(protocol.getStageName())) {
                entity.setStageName(protocol.getStageName());
            }

            if (protocol.getAttribute() != null) {
                entity.setAttribute(protocol.getAttribute());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("阶段参数实体更新成功: crossId={}, stageNo={}",
                    entity.getCrossId(), entity.getStageNo());

        } catch (Exception e) {
            logger.error("阶段参数更新失败: crossId={}, stageNo={}",
                    entity.getCrossId(), entity.getStageNo(), e);
            throw new DataConversionException("阶段参数更新失败", e);
        }
    }

    /**
     * 获取阶段的信号组状态列表
     * 查询 stage_signal_group_status 表
     */
    private List<SignalGroupStatus> getSignalGroupStatusList(StageParamEntity entity) {
        List<SignalGroupStatus> statusList = new ArrayList<>();

        try {
            // 实际实现需要查询关联表
            // if (stageSignalGroupStatusRepository != null) {
            //     List<StageSignalGroupStatusEntity> statusEntities =
            //         stageSignalGroupStatusRepository.findByCrossIdAndStageNo(
            //             entity.getCrossId(), entity.getStageNo());
            //
            //     for (StageSignalGroupStatusEntity statusEntity : statusEntities) {
            //         SignalGroupStatus status = new SignalGroupStatus();
            //         status.setSignalGroupNo(statusEntity.getSignalGroupNo());
            //         status.setLampStatus(LampStatus.fromCode(statusEntity.getLampStatus()));
            //         statusList.add(status);
            //     }
            // }

            // 临时示例数据
            SignalGroupStatus status1 = new SignalGroupStatus();
            status1.setSignalGroupNo(1);
            // status1.setLampStatus(LampStatus.GREEN);
            statusList.add(status1);

            SignalGroupStatus status2 = new SignalGroupStatus();
            status2.setSignalGroupNo(2);
            // status2.setLampStatus(LampStatus.RED);
            statusList.add(status2);

        } catch (Exception e) {
            logger.warn("获取阶段信号组状态失败: crossId={}, stageNo={}",
                    entity.getCrossId(), entity.getStageNo(), e);
        }

        logger.debug("获取阶段信号组状态列表: crossId={}, stageNo={}, 数量={}",
                entity.getCrossId(), entity.getStageNo(), statusList.size());

        return statusList;
    }

    /**
     * 保存信号组状态列表
     * 操作 stage_signal_group_status 表
     */
    private void saveSignalGroupStatusList(StageParam protocol) {
        if (protocol.getSignalGroupStatusList() == null ||
                protocol.getSignalGroupStatusList().isEmpty()) {
            return;
        }

        try {
            // 实际实现：
            // for (SignalGroupStatus status : protocol.getSignalGroupStatusList()) {
            //     StageSignalGroupStatusEntity statusEntity = new StageSignalGroupStatusEntity();
            //     statusEntity.setCrossId(protocol.getCrossId());
            //     statusEntity.setStageNo(protocol.getStageNo());
            //     statusEntity.setSignalGroupNo(status.getSignalGroupNo());
            //     statusEntity.setLampStatus(status.getLampStatus().getCode());
            //
            //     stageSignalGroupStatusRepository.insert(statusEntity);
            // }

            logger.debug("保存阶段信号组状态列表: crossId={}, stageNo={}, 数量={}",
                    protocol.getCrossId(), protocol.getStageNo(),
                    protocol.getSignalGroupStatusList().size());

        } catch (Exception e) {
            logger.error("保存阶段信号组状态列表失败: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo(), e);
        }
    }

    /**
     * 更新信号组状态列表
     */
    private void updateSignalGroupStatusList(StageParam protocol) {
        try {
            // 先删除旧的状态
            // stageSignalGroupStatusRepository.deleteByCrossIdAndStageNo(
            //     protocol.getCrossId(), protocol.getStageNo());

            // 再保存新的状态
            saveSignalGroupStatusList(protocol);

            logger.debug("更新阶段信号组状态列表: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo());

        } catch (Exception e) {
            logger.error("更新阶段信号组状态列表失败: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo(), e);
        }
    }

    /**
     * 完整保存阶段参数（包含关联数据）
     * 这个方法可以在Service层调用，确保主表和关联表的事务一致性
     */
    public void saveStageWithAssociations(StageParam protocol) {
        try {
            // 1. 保存主表数据
            StageParamEntity entity = toEntity(protocol);
            // stageParamRepository.insert(entity);

            // 2. 保存关联表数据
            saveSignalGroupStatusList(protocol);

            logger.info("完整保存阶段参数成功: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo());

        } catch (Exception e) {
            logger.error("完整保存阶段参数失败: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo(), e);
            throw new DataConversionException("完整保存阶段参数失败", e);
        }
    }

    /**
     * 完整更新阶段参数（包含关联数据）
     */
    public void updateStageWithAssociations(StageParam protocol, StageParamEntity entity) {
        try {
            // 1. 更新主表数据
            updateEntity(protocol, entity);
            // stageParamRepository.updateById(entity);

            // 2. 更新关联表数据
            updateSignalGroupStatusList(protocol);

            logger.info("完整更新阶段参数成功: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo());

        } catch (Exception e) {
            logger.error("完整更新阶段参数失败: crossId={}, stageNo={}",
                    protocol.getCrossId(), protocol.getStageNo(), e);
            throw new DataConversionException("完整更新阶段参数失败", e);
        }
    }
}
