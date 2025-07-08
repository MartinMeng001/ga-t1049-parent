// ================================================================
// 2. 抽象转换器基类
// ================================================================
package com.traffic.gat1049.data.converter.base;

import com.traffic.gat1049.data.converter.interfaces.EntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象实体转换器基类
 * 提供通用转换逻辑和工具方法
 */
public abstract class AbstractEntityConverter<E, P> implements EntityConverter<E, P> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ObjectMapper objectMapper;

    private final Class<E> entityType;
    private final Class<P> protocolType;

    @SuppressWarnings("unchecked")
    public AbstractEntityConverter() {
        // 通过反射获取泛型类型
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityType = (Class<E>) superClass.getActualTypeArguments()[0];
        this.protocolType = (Class<P>) superClass.getActualTypeArguments()[1];
    }

    @Override
    public List<P> toProtocolList(List<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(this::toProtocol)
                .collect(Collectors.toList());
    }

    @Override
    public List<E> toEntityList(List<P> protocols) {
        if (protocols == null || protocols.isEmpty()) {
            return List.of();
        }

        return protocols.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Class<E> getEntityType() {
        return entityType;
    }

    @Override
    public Class<P> getProtocolType() {
        return protocolType;
    }

    /**
     * 安全的JSON序列化
     */
    protected String serializeToJson(Object obj) {
        if (obj == null) return null;

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("JSON序列化失败: {}", obj, e);
            throw new DataConversionException("JSON序列化失败", e);
        }
    }

    /**
     * 安全的JSON反序列化
     */
    protected <T> T deserializeFromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("JSON反序列化失败: {}", json, e);
            throw new DataConversionException("JSON反序列化失败", e);
        }
    }

    /**
     * 设置实体通用字段
     */
    protected void setEntityAuditFields(E entity, boolean isNew) {
        LocalDateTime now = LocalDateTime.now();

        try {
            if (isNew) {
                // 新建时设置创建时间
                setFieldValue(entity, "createdTime", now);
                setFieldValue(entity, "createdAt", now);
            }
            // 总是更新修改时间
            setFieldValue(entity, "updatedTime", now);
            setFieldValue(entity, "updatedAt", now);
        } catch (Exception e) {
            logger.debug("设置审计字段失败，实体可能没有这些字段: {}", e.getMessage());
        }
    }

    /**
     * 通过反射设置字段值
     */
    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException e) {
            // 字段不存在，忽略
        } catch (Exception e) {
            logger.debug("设置字段{}失败: {}", fieldName, e.getMessage());
        }
    }

    /**
     * 部分更新：将协议对象的非空字段更新到现有实体
     * 默认实现抛出异常，子类需要重写此方法
     */
    @Override
    public void updateEntity(P protocol, E entity) {
        throw new UnsupportedOperationException("部分更新功能需要在具体转换器中实现");
    }

    /**
     * 验证转换结果
     */
    protected void validateConversion(E entity, P protocol) {
        if (entity == null || protocol == null) {
            throw new DataConversionException("转换结果不能为null");
        }
    }
}