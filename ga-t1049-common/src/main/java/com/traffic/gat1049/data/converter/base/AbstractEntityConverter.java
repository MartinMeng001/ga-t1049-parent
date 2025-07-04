package com.traffic.gat1049.data.converter.base;

import com.traffic.gat1049.data.converter.interfaces.EntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象实体转换器基类
 * 提供通用的转换逻辑和工具方法
 */
public abstract class AbstractEntityConverter<E, P> implements EntityConverter<E, P> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ObjectMapper objectMapper;

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

    /**
     * 安全的JSON序列化
     */
    protected String serializeToJson(Object obj) {
        if (obj == null) return null;

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("JSON序列化失败", e);
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
     * 设置实体的通用字段
     */
    protected void setEntityCommonFields(E entity, boolean isNew) {
        // 由于泛型擦除，这里需要在具体实现中处理
        // 可以通过反射或接口方式实现
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