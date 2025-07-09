// ================================================================
// CrossCycle 转换器实现
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.runtime.CrossCycle;
import com.traffic.gat1049.repository.entity.CrossCycleEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 路口周期转换器实现
 * 参考 SysInfo converter 的实现模式
 */
@Component
public class CrossCycleConverter extends AbstractEntityConverter<CrossCycleEntity, CrossCycle> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CrossCycle toProtocol(CrossCycleEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossCycle protocol = new CrossCycle();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());

            // 开始时间转换（从 LocalDateTime 转为字符串）
            if (entity.getStartTime() != null) {
                protocol.setStartTime(entity.getStartTime().format(DATE_TIME_FORMATTER));
            }

            // 注意：实体中是 cycleLen，协议中是 lastCycleLen
            protocol.setLastCycleLen(entity.getCycleLen());

            // 从数据库字段映射到协议字段
            // 根据数据库表结构，可能需要从其他相关表获取 adjustFlag
            // 这里设置默认值，实际应用中可能需要查询相关表或计算得出
            protocol.setAdjustFlag(CrossCycle.ADJUST_FLAG_NO); // 默认不处于过渡调整

            validateConversion(entity, protocol);

            logger.debug("路口周期实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("路口周期转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口周期转换失败", e);
        }
    }

    @Override
    public CrossCycleEntity toEntity(CrossCycle protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CrossCycleEntity entity = new CrossCycleEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());

            // 开始时间转换（从字符串转为 LocalDateTime）
            if (StringUtils.hasText(protocol.getStartTime())) {
                try {
                    entity.setStartTime(LocalDateTime.parse(protocol.getStartTime(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    logger.warn("开始时间格式转换失败，使用当前时间: {}", protocol.getStartTime());
                    entity.setStartTime(LocalDateTime.now());
                }
            } else {
                entity.setStartTime(LocalDateTime.now());
            }

            // 注意：协议中是 lastCycleLen，实体中是 cycleLen
            entity.setCycleLen(protocol.getLastCycleLen());

            // 设置其他字段的默认值
            // 根据实际业务需求设置
            entity.setPlanNo(0); // 默认方案号
            entity.setSplitNum(0); // 默认绿信比数量

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("路口周期协议转实体成功: {}", protocol.getCrossId());
            return entity;

        } catch (Exception e) {
            logger.error("路口周期转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("路口周期转换失败", e);
        }
    }

    @Override
    public void updateEntity(CrossCycle protocol, CrossCycleEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }

            if (StringUtils.hasText(protocol.getStartTime())) {
                try {
                    entity.setStartTime(LocalDateTime.parse(protocol.getStartTime(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    logger.warn("开始时间格式转换失败，保持原值: {}", protocol.getStartTime());
                }
            }

            if (protocol.getLastCycleLen() != null) {
                entity.setCycleLen(protocol.getLastCycleLen());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("路口周期实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("路口周期更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口周期更新失败", e);
        }
    }

    /**
     * 重写父类的验证方法
     */
    @Override
    protected void validateConversion(CrossCycleEntity entity, CrossCycle protocol) {
        super.validateConversion(entity, protocol);

        // 添加特定的业务验证逻辑
        if (protocol != null) {
            if (!StringUtils.hasText(protocol.getCrossId())) {
                throw new DataConversionException("路口编号不能为空");
            }
            if (protocol.getLastCycleLen() != null && protocol.getLastCycleLen() < 0) {
                throw new DataConversionException("周期长度不能为负数");
            }
        }

        if (entity != null) {
            if (!StringUtils.hasText(entity.getCrossId())) {
                throw new DataConversionException("路口编号不能为空");
            }
            if (entity.getCycleLen() != null && entity.getCycleLen() < 0) {
                throw new DataConversionException("周期长度不能为负数");
            }
        }
    }

    /**
     * 从 LocalDateTime 创建 CrossCycle
     */
    public CrossCycle createWithLocalDateTime(String crossId, LocalDateTime startTime, Integer cycleLen) {
        CrossCycle cycle = new CrossCycle();
        cycle.setCrossId(crossId);
        if (startTime != null) {
            cycle.setStartTime(startTime.format(DATE_TIME_FORMATTER));
        }
        cycle.setLastCycleLen(cycleLen);
        cycle.setAdjustFlag(CrossCycle.ADJUST_FLAG_NO);
        return cycle;
    }

    /**
     * 创建过渡调整中的周期
     */
    public CrossCycle createTransitionCycle(String crossId, LocalDateTime startTime, Integer cycleLen) {
        CrossCycle cycle = createWithLocalDateTime(crossId, startTime, cycleLen);
        cycle.setAdjustFlag(CrossCycle.ADJUST_FLAG_YES);
        return cycle;
    }
}