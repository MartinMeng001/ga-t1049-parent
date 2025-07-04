package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.model.dto.SyncTaskDto;
import com.traffic.gat1049.repository.entity.GatSyncTaskEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 同步任务转换器实现
 */
@Component
public class SyncTaskConverter extends AbstractEntityConverter<GatSyncTaskEntity, SyncTaskDto> {

    @Override
    public SyncTaskDto toProtocol(GatSyncTaskEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SyncTaskDto dto = new SyncTaskDto();

            dto.setTaskId(entity.getTaskId());
            dto.setControllerId(entity.getControllerId());
            dto.setTaskType(entity.getTaskType());
            dto.setDataType(entity.getDataType());
            dto.setSyncDirection(entity.getSyncDirection());
            dto.setStatus(entity.getStatus());
            dto.setPriorityLevel(entity.getPriorityLevel());
            dto.setMaxRetryCount(entity.getMaxRetryCount());
            dto.setRetryCount(entity.getRetryCount());
            dto.setNextExecuteTime(entity.getNextExecuteTime());
            dto.setLastExecuteTime(entity.getLastExecuteTime());
            dto.setErrorMessage(entity.getErrorMessage());
            dto.setCreatedBy(entity.getCreatedBy());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());

            // 反序列化JSON字段
            dto.setTaskData(deserializeFromJson(entity.getTaskData(), Object.class));
            dto.setResultData(deserializeFromJson(entity.getResultData(), Object.class));

            validateConversion(entity, dto);

            logger.debug("同步任务实体转DTO成功: {}", entity.getTaskId());
            return dto;

        } catch (Exception e) {
            logger.error("同步任务实体转DTO失败: {}", entity.getTaskId(), e);
            throw new DataConversionException("同步任务转换失败", e);
        }
    }

    @Override
    public GatSyncTaskEntity toEntity(SyncTaskDto dto) {
        if (dto == null) {
            return null;
        }

        try {
            GatSyncTaskEntity entity = new GatSyncTaskEntity();

            entity.setTaskId(dto.getTaskId());
            entity.setControllerId(dto.getControllerId());
            entity.setTaskType(dto.getTaskType());
            entity.setDataType(dto.getDataType());
            entity.setSyncDirection(dto.getSyncDirection());
            entity.setStatus(dto.getStatus());
            entity.setPriorityLevel(dto.getPriorityLevel());
            entity.setMaxRetryCount(dto.getMaxRetryCount());
            entity.setRetryCount(dto.getRetryCount());
            entity.setNextExecuteTime(dto.getNextExecuteTime());
            entity.setLastExecuteTime(dto.getLastExecuteTime());
            entity.setErrorMessage(dto.getErrorMessage());
            entity.setCreatedBy(dto.getCreatedBy());

            // 序列化JSON字段
            entity.setTaskData(serializeToJson(dto.getTaskData()));
            entity.setResultData(serializeToJson(dto.getResultData()));

            // 设置时间戳
            LocalDateTime now = LocalDateTime.now();
            if (entity.getId() == null) {
                entity.setCreatedAt(now);
            }
            entity.setUpdatedAt(now);

            validateConversion(entity, dto);

            logger.debug("同步任务DTO转实体成功: {}", dto.getTaskId());
            return entity;

        } catch (Exception e) {
            logger.error("同步任务DTO转实体失败: {}", dto.getTaskId(), e);
            throw new DataConversionException("同步任务转换失败", e);
        }
    }
}