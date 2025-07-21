package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;
import com.traffic.gat1049.repository.entity.LampGroupParamEntity;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 信号灯组参数转换器实现
 * 演示枚举类型转换模式
 */
@Component
public class LampGroupParamConverter extends AbstractEntityConverter<LampGroupParamEntity, LampGroupParam> {

    @Override
    public LampGroupParam toProtocol(LampGroupParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            LampGroupParam protocol = new LampGroupParam();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setLampGroupNo(entity.getLampGroupNo());

            // 枚举类型转换 - 从数据库的字符串字段转换为枚举
            if (StringUtils.hasText(entity.getDirection())) {
                protocol.setDirection(Direction.fromCode(entity.getDirection()));
            }

            if (StringUtils.hasText(entity.getType())) {
                protocol.setType(LampGroupType.fromCode(entity.getType()));
            }

            validateConversion(entity, protocol);

            logger.debug("信号灯组参数实体转协议成功: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo());
            return protocol;

        } catch (Exception e) {
            logger.error("信号灯组参数转换失败: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo(), e);
            throw new DataConversionException("信号灯组参数转换失败", e);
        }
    }

    @Override
    public LampGroupParamEntity toEntity(LampGroupParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            LampGroupParamEntity entity = new LampGroupParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setLampGroupNo(protocol.getLampGroupNo());

            // 枚举类型转换 - 从枚举转换为数据库的字符串字段
            if (protocol.getDirection() != null) {
                entity.setDirection(protocol.getDirection().getCode());
            }

            if (protocol.getType() != null) {
                entity.setType(protocol.getType().getCode());
            }

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("信号灯组参数协议转实体成功: crossId={}, lampGroupNo={}",
                    protocol.getCrossId(), protocol.getLampGroupNo());
            return entity;

        } catch (Exception e) {
            logger.error("信号灯组参数转换失败: crossId={}, lampGroupNo={}",
                    protocol.getCrossId(), protocol.getLampGroupNo(), e);
            throw new DataConversionException("信号灯组参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(LampGroupParam protocol, LampGroupParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 灯组编号不可更新（业务主键）

            // 更新方向 - 直接使用字符串代码
            if (protocol.getDirection() != null) {
                entity.setDirection(protocol.getDirection().getCode());
            }

            // 更新类型 - 直接使用字符串代码
            if (protocol.getType() != null) {
                entity.setType(protocol.getType().getCode());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("信号灯组参数实体更新成功: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo());

        } catch (Exception e) {
            logger.error("信号灯组参数更新失败: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo(), e);
            throw new DataConversionException("信号灯组参数更新失败", e);
        }
    }

    // 新增：List批量转换方法
    // =================================================================

    /**
     * 批量转换：实体列表 → 协议对象列表
     *
     * @param entities 实体列表
     * @return 协议对象列表
     */
    public List<LampGroupParam> toProtocolList(List<LampGroupParamEntity> entities) {
        if (entities == null) {
            logger.debug("输入实体列表为null，返回null");
            return null;
        }

        if (entities.isEmpty()) {
            logger.debug("输入实体列表为空，返回空列表");
            return new ArrayList<>();
        }

        try {
            logger.debug("开始批量转换实体列表到协议列表，数量: {}", entities.size());

            List<LampGroupParam> protocols = entities.stream()
                    .map(this::toProtocol)
                    .collect(Collectors.toList());

            logger.info("批量转换实体到协议成功: 输入{}个，输出{}个", entities.size(), protocols.size());
            return protocols;

        } catch (Exception e) {
            logger.error("批量转换实体到协议失败: 输入数量={}", entities.size(), e);
            throw new DataConversionException("批量转换实体到协议失败", e);
        }
    }

    /**
     * 批量转换：协议对象列表 → 实体列表
     *
     * @param protocols 协议对象列表
     * @return 实体列表
     */
    public List<LampGroupParamEntity> toEntityList(List<LampGroupParam> protocols) {
        if (protocols == null) {
            logger.debug("输入协议列表为null，返回null");
            return null;
        }

        if (protocols.isEmpty()) {
            logger.debug("输入协议列表为空，返回空列表");
            return new ArrayList<>();
        }

        try {
            logger.debug("开始批量转换协议列表到实体列表，数量: {}", protocols.size());

            List<LampGroupParamEntity> entities = protocols.stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList());

            logger.info("批量转换协议到实体成功: 输入{}个，输出{}个", protocols.size(), entities.size());
            return entities;

        } catch (Exception e) {
            logger.error("批量转换协议到实体失败: 输入数量={}", protocols.size(), e);
            throw new DataConversionException("批量转换协议到实体失败", e);
        }
    }

    // =================================================================
    // 增强的List转换方法（带过滤和验证）
    // =================================================================

    /**
     * 安全的批量转换：实体列表 → 协议对象列表
     * 跳过转换失败的项目，而不是整体失败
     *
     * @param entities 实体列表
     * @return 转换结果对象，包含成功列表和错误信息
     */
    public ConversionResult<LampGroupParam> toProtocolListSafely(List<LampGroupParamEntity> entities) {
        ConversionResult<LampGroupParam> result = new ConversionResult<>();

        if (entities == null || entities.isEmpty()) {
            logger.debug("输入实体列表为空，返回空结果");
            result.setSuccessItems(new ArrayList<>());
            result.setErrors(new ArrayList<>());
            return result;
        }

        List<LampGroupParam> successItems = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            try {
                LampGroupParamEntity entity = entities.get(i);
                if (entity != null) {
                    LampGroupParam protocol = toProtocol(entity);
                    if (protocol != null) {
                        successItems.add(protocol);
                    }
                } else {
                    errors.add(String.format("第%d项为null", i + 1));
                }
            } catch (Exception e) {
                String error = String.format("第%d项转换失败: %s", i + 1, e.getMessage());
                errors.add(error);
                logger.warn(error, e);
            }
        }

        result.setSuccessItems(successItems);
        result.setErrors(errors);
        result.setTotalInput(entities.size());
        result.setSuccessCount(successItems.size());
        result.setFailureCount(errors.size());

        logger.info("安全批量转换实体到协议完成: 输入{}个，成功{}个，失败{}个",
                entities.size(), successItems.size(), errors.size());

        return result;
    }

    /**
     * 安全的批量转换：协议对象列表 → 实体列表
     * 跳过转换失败的项目，而不是整体失败
     *
     * @param protocols 协议对象列表
     * @return 转换结果对象，包含成功列表和错误信息
     */
    public ConversionResult<LampGroupParamEntity> toEntityListSafely(List<LampGroupParam> protocols) {
        ConversionResult<LampGroupParamEntity> result = new ConversionResult<>();

        if (protocols == null || protocols.isEmpty()) {
            logger.debug("输入协议列表为空，返回空结果");
            result.setSuccessItems(new ArrayList<>());
            result.setErrors(new ArrayList<>());
            return result;
        }

        List<LampGroupParamEntity> successItems = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < protocols.size(); i++) {
            try {
                LampGroupParam protocol = protocols.get(i);
                if (protocol != null) {
                    LampGroupParamEntity entity = toEntity(protocol);
                    if (entity != null) {
                        successItems.add(entity);
                    }
                } else {
                    errors.add(String.format("第%d项为null", i + 1));
                }
            } catch (Exception e) {
                String error = String.format("第%d项转换失败: %s", i + 1, e.getMessage());
                errors.add(error);
                logger.warn(error, e);
            }
        }

        result.setSuccessItems(successItems);
        result.setErrors(errors);
        result.setTotalInput(protocols.size());
        result.setSuccessCount(successItems.size());
        result.setFailureCount(errors.size());

        logger.info("安全批量转换协议到实体完成: 输入{}个，成功{}个，失败{}个",
                protocols.size(), successItems.size(), errors.size());

        return result;
    }

    // =================================================================
    // 便捷方法：过滤和筛选
    // =================================================================

    /**
     * 转换并过滤：只返回指定路口的信号灯组
     */
    public List<LampGroupParam> toProtocolListByCrossId(List<LampGroupParamEntity> entities, String crossId) {
        if (entities == null || entities.isEmpty() || !StringUtils.hasText(crossId)) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(entity -> crossId.equals(entity.getCrossId()))
                .map(this::toProtocol)
                .collect(Collectors.toList());
    }

    /**
     * 转换并过滤：只返回指定方向的信号灯组
     */
    public List<LampGroupParam> toProtocolListByDirection(List<LampGroupParamEntity> entities, Direction direction) {
        if (entities == null || entities.isEmpty() || direction == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(entity -> direction.getCode().equals(entity.getDirection()))
                .map(this::toProtocol)
                .collect(Collectors.toList());
    }

    /**
     * 转换并过滤：只返回指定类型的信号灯组
     */
    public List<LampGroupParam> toProtocolListByType(List<LampGroupParamEntity> entities, LampGroupType type) {
        if (entities == null || entities.isEmpty() || type == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(entity -> type.getCode().equals(entity.getType()))
                .map(this::toProtocol)
                .collect(Collectors.toList());
    }

    /**
     * 验证转换结果
     */
    protected void validateConversion(LampGroupParamEntity entity, LampGroupParam protocol) {
        if (entity == null || protocol == null) {
            throw new DataConversionException("转换结果不能为null");
        }

        // 验证关键字段是否正确转换
        if (!entity.getCrossId().equals(protocol.getCrossId())) {
            throw new DataConversionException("路口编号转换失败");
        }

        if (!entity.getLampGroupNo().equals(protocol.getLampGroupNo())) {
            throw new DataConversionException("灯组编号转换失败");
        }

        // 验证枚举转换
        if (protocol.getDirection() != null && entity.getDirection() != null) {
            if (!entity.getDirection().equals(protocol.getDirection().getCode())) {
                throw new DataConversionException("方向枚举转换失败");
            }
        }

        if (protocol.getType() != null && entity.getType() != null) {
            if (!entity.getType().equals(protocol.getType().getCode())) {
                throw new DataConversionException("类型枚举转换失败");
            }
        }
    }
    // =================================================================
    // 内部类：转换结果
    // =================================================================

    /**
     * 转换结果封装类
     */
    public static class ConversionResult<T> {
        private List<T> successItems;
        private List<String> errors;
        private int totalInput;
        private int successCount;
        private int failureCount;

        // 构造函数
        public ConversionResult() {
            this.successItems = new ArrayList<>();
            this.errors = new ArrayList<>();
        }

        // Getters and Setters
        public List<T> getSuccessItems() { return successItems; }
        public void setSuccessItems(List<T> successItems) { this.successItems = successItems; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public int getTotalInput() { return totalInput; }
        public void setTotalInput(int totalInput) { this.totalInput = totalInput; }

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

        // 便捷方法
        public boolean hasErrors() { return !errors.isEmpty(); }
        public boolean isAllSuccess() { return failureCount == 0; }
        public double getSuccessRate() {
            return totalInput == 0 ? 0.0 : (double) successCount / totalInput * 100;
        }

        @Override
        public String toString() {
            return String.format("ConversionResult{总计:%d, 成功:%d, 失败:%d, 成功率:%.1f%%}",
                    totalInput, successCount, failureCount, getSuccessRate());
        }
    }
}
