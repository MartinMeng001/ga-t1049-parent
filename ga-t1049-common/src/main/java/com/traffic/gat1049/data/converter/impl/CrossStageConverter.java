// ================================================================
// CrossStage 转换器实现
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.runtime.CrossStage;
import com.traffic.gat1049.repository.entity.CrossStageEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 路口阶段转换器实现
 * 参考 SysInfo converter 的实现模式
 */
@Component
public class CrossStageConverter extends AbstractEntityConverter<CrossStageEntity, CrossStage> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CrossStage toProtocol(CrossStageEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossStage protocol = new CrossStage();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());

            // 根据数据库字段映射到协议字段
            // 注意：数据库结构与协议结构的字段对应关系

            // 从实体的字段映射（需要根据实际数据库表结构调整）
            // 假设数据库表结构包含这些字段，如果没有则需要从其他相关查询获取

            // 上个阶段信息 - 可能需要从历史记录或其他表获取
            // 这里先设置为null，实际应用中可能需要额外查询
            protocol.setLastStageNo(null);
            protocol.setLastStageLen(null);

            // 当前阶段信息
            protocol.setCurStageNo(entity.getStageNo());

            // 当前阶段开始时间转换
            if (entity.getStartTime() != null) {
                protocol.setCurStageStartTime(entity.getStartTime().format(DATE_TIME_FORMATTER));
            }

            // 当前阶段已执行时长
            protocol.setCurStageLen(entity.getStageLen());

            validateConversion(entity, protocol);

            logger.debug("路口阶段实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("路口阶段转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口阶段转换失败", e);
        }
    }

    @Override
    public CrossStageEntity toEntity(CrossStage protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CrossStageEntity entity = new CrossStageEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());

            // 当前阶段号
            entity.setStageNo(protocol.getCurStageNo());

            // 当前阶段开始时间转换
            if (StringUtils.hasText(protocol.getCurStageStartTime())) {
                try {
                    entity.setStartTime(LocalDateTime.parse(protocol.getCurStageStartTime(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    logger.warn("阶段开始时间格式转换失败，使用当前时间: {}", protocol.getCurStageStartTime());
                    entity.setStartTime(LocalDateTime.now());
                }
            } else {
                entity.setStartTime(LocalDateTime.now());
            }

            // 当前阶段已执行时长
            entity.setStageLen(protocol.getCurStageLen());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("路口阶段协议转实体成功: {}", protocol.getCrossId());
            return entity;

        } catch (Exception e) {
            logger.error("路口阶段转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("路口阶段转换失败", e);
        }
    }

    @Override
    public void updateEntity(CrossStage protocol, CrossStageEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }

            if (protocol.getCurStageNo() != null) {
                entity.setStageNo(protocol.getCurStageNo());
            }

            if (StringUtils.hasText(protocol.getCurStageStartTime())) {
                try {
                    entity.setStartTime(LocalDateTime.parse(protocol.getCurStageStartTime(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    logger.warn("阶段开始时间格式转换失败，保持原值: {}", protocol.getCurStageStartTime());
                }
            }

            if (protocol.getCurStageLen() != null) {
                entity.setStageLen(protocol.getCurStageLen());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("路口阶段实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("路口阶段更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口阶段更新失败", e);
        }
    }

    /**
     * 重写父类的验证方法
     */
    @Override
    protected void validateConversion(CrossStageEntity entity, CrossStage protocol) {
        super.validateConversion(entity, protocol);

        // 添加特定的业务验证逻辑
        if (protocol != null) {
            if (!StringUtils.hasText(protocol.getCrossId())) {
                throw new DataConversionException("路口编号不能为空");
            }
            if (protocol.getCurStageLen() != null && protocol.getCurStageLen() < 0) {
                throw new DataConversionException("阶段时长不能为负数");
            }
        }

        if (entity != null) {
            if (!StringUtils.hasText(entity.getCrossId())) {
                throw new DataConversionException("路口编号不能为空");
            }
            if (entity.getStageLen() != null && entity.getStageLen() < 0) {
                throw new DataConversionException("阶段时长不能为负数");
            }
        }
    }

    /**
     * 从 LocalDateTime 创建 CrossStage
     */
    public CrossStage createWithLocalDateTime(String crossId, Integer stageNo, LocalDateTime startTime, Integer stageLen) {
        CrossStage stage = new CrossStage();
        stage.setCrossId(crossId);
        stage.setCurStageNo(stageNo);
        if (startTime != null) {
            stage.setCurStageStartTime(startTime.format(DATE_TIME_FORMATTER));
        }
        stage.setCurStageLen(stageLen);
        return stage;
    }

    /**
     * 创建包含上个阶段信息的 CrossStage
     */
    public CrossStage createWithLastStage(String crossId,
                                          Integer lastStageNo, Integer lastStageLen,
                                          Integer curStageNo, LocalDateTime curStageStartTime, Integer curStageLen) {
        CrossStage stage = new CrossStage();
        stage.setCrossId(crossId);
        stage.setLastStageNo(lastStageNo);
        stage.setLastStageLen(lastStageLen);
        stage.setCurStageNo(curStageNo);
        if (curStageStartTime != null) {
            stage.setCurStageStartTime(curStageStartTime.format(DATE_TIME_FORMATTER));
        }
        stage.setCurStageLen(curStageLen);
        return stage;
    }

    /**
     * 从数据库查询结果创建完整的 CrossStage
     * 这个方法可能需要查询历史记录来获取上个阶段信息
     */
    public CrossStage createCompleteStage(CrossStageEntity currentEntity, CrossStageEntity lastEntity) {
        if (currentEntity == null) {
            return null;
        }

        CrossStage stage = toProtocol(currentEntity);

        // 如果有上个阶段信息，则设置
        if (lastEntity != null) {
            stage.setLastStageNo(lastEntity.getStageNo());
            stage.setLastStageLen(lastEntity.getStageLen());
        }

        return stage;
    }
}