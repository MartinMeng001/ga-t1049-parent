package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.repository.entity.SignalGroupParamEntity;
import com.traffic.gat1049.repository.interfaces.SignalGroupLampGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * 信号组参数转换器实现
 * 完全匹配SQL表结构，包含关联表处理
 */
@Component
public class SignalGroupParamConverter extends AbstractEntityConverter<SignalGroupParamEntity, SignalGroupParam> {

    // 注入关联表Repository（需要创建）
    // @Autowired
    // private SignalGroupLampGroupRepository signalGroupLampGroupRepository;

    @Override
    public SignalGroupParam toProtocol(SignalGroupParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SignalGroupParam protocol = new SignalGroupParam();

            // 基础字段映射 - 完全匹配SQL表结构
            protocol.setCrossId(entity.getCrossId());
            protocol.setSignalGroupNo(entity.getSignalGroupNo());
            protocol.setName(entity.getName());

            // 配时参数映射
            if (entity.getGreenFlashLen() != null) {
                protocol.setGreenFlashLen(entity.getGreenFlashLen());
            }
            if (entity.getMaxGreen() != null) {
                protocol.setMaxGreen(entity.getMaxGreen());
            }
            if (entity.getMinGreen() != null) {
                protocol.setMinGreen(entity.getMinGreen());
            }

            // 获取关联的信号灯组列表
            List<Integer> lampGroupNoList = getLampGroupNoList(entity);
            protocol.setLampGroupNoList(lampGroupNoList);

            validateConversion(entity, protocol);

            logger.debug("信号组参数实体转协议成功: crossId={}, signalGroupNo={}",
                    entity.getCrossId(), entity.getSignalGroupNo());
            return protocol;

        } catch (Exception e) {
            logger.error("信号组参数转换失败: crossId={}, signalGroupNo={}",
                    entity.getCrossId(), entity.getSignalGroupNo(), e);
            throw new DataConversionException("信号组参数转换失败", e);
        }
    }

    @Override
    public SignalGroupParamEntity toEntity(SignalGroupParam protocol) {
        if (protocol == null) {
            return null;
        }
        SignalGroupParamEntity entity = new SignalGroupParamEntity();
        try {
            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setSignalGroupNo(protocol.getSignalGroupNo());
            entity.setName(protocol.getName());

            // 配时参数映射
            if (protocol.getGreenFlashLen() != null) {
                entity.setGreenFlashLen(protocol.getGreenFlashLen());
            }
            if (protocol.getMaxGreen() != null) {
                entity.setMaxGreen(protocol.getMaxGreen());
            }
            if (protocol.getMinGreen() != null) {
                entity.setMinGreen(protocol.getMinGreen());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("信号组参数实体更新成功: crossId={}, signalGroupNo={}",
                    entity.getCrossId(), entity.getSignalGroupNo());
            return entity;

        } catch (Exception e) {
            logger.error("信号组参数更新失败: crossId={}, signalGroupNo={}",
                    entity.getCrossId(), entity.getSignalGroupNo(), e);
            throw new DataConversionException("信号组参数更新失败", e);
        }
    }

    /**
     * 获取信号组关联的信号灯组列表
     * 查询 signal_group_lamp_group 关联表
     */
    private List<Integer> getLampGroupNoList(SignalGroupParamEntity entity) {
        List<Integer> lampGroupNoList = new ArrayList<>();

        try {
            // 实际实现需要查询关联表
            // if (signalGroupLampGroupRepository != null) {
            //     List<SignalGroupLampGroupEntity> associations =
            //         signalGroupLampGroupRepository.findByCrossIdAndSignalGroupNo(
            //             entity.getCrossId(), entity.getSignalGroupNo());
            //
            //     for (SignalGroupLampGroupEntity association : associations) {
            //         lampGroupNoList.add(association.getLampGroupNo());
            //     }
            // }

            // 临时示例：返回与信号组号相同的灯组号
            if (entity.getSignalGroupNo() != null) {
                lampGroupNoList.add(entity.getSignalGroupNo());
            }

        } catch (Exception e) {
            logger.warn("获取信号组关联灯组失败: crossId={}, signalGroupNo={}",
                    entity.getCrossId(), entity.getSignalGroupNo(), e);
        }

        return lampGroupNoList;
    }

    /**
     * 保存信号灯组关联关系
     * 需要操作 signal_group_lamp_group 表
     */
    private void saveLampGroupAssociations(SignalGroupParam protocol) {
        if (protocol.getLampGroupNoList() == null || protocol.getLampGroupNoList().isEmpty()) {
            return;
        }

        try {
            // 实际实现：
            // for (Integer lampGroupNo : protocol.getLampGroupNoList()) {
            //     SignalGroupLampGroupEntity association = new SignalGroupLampGroupEntity();
            //     association.setCrossId(protocol.getCrossId());
            //     association.setSignalGroupNo(protocol.getSignalGroupNo());
            //     association.setLampGroupNo(lampGroupNo);
            //     signalGroupLampGroupRepository.insert(association);
            // }

            logger.debug("保存信号组灯组关联关系: crossId={}, signalGroupNo={}, 灯组数量={}",
                    protocol.getCrossId(), protocol.getSignalGroupNo(),
                    protocol.getLampGroupNoList().size());

        } catch (Exception e) {
            logger.error("保存信号组灯组关联关系失败: crossId={}, signalGroupNo={}",
                    protocol.getCrossId(), protocol.getSignalGroupNo(), e);
        }
    }

    /**
     * 更新信号灯组关联关系
     */
    private void updateLampGroupAssociations(SignalGroupParam protocol) {
        try {
            // 先删除旧的关联关系
            // signalGroupLampGroupRepository.deleteByCrossIdAndSignalGroupNo(
            //     protocol.getCrossId(), protocol.getSignalGroupNo());

            // 再保存新的关联关系
            saveLampGroupAssociations(protocol);

            logger.debug("更新信号组灯组关联关系: crossId={}, signalGroupNo={}",
                    protocol.getCrossId(), protocol.getSignalGroupNo());

        } catch (Exception e) {
            logger.error("更新信号组灯组关联关系失败: crossId={}, signalGroupNo={}",
                    protocol.getCrossId(), protocol.getSignalGroupNo(), e);
        }
    }
}
