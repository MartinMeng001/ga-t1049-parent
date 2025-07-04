package com.traffic.gat1049.data.converter.factory;

import com.traffic.gat1049.data.converter.interfaces.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 转换器工厂
 * 提供统一的转换器获取和管理
 */
@Component
public class ConverterFactory {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<Class<?>, EntityConverter<?, ?>> converterCache = new HashMap<>();

    /**
     * 获取指定类型的转换器
     */
    @SuppressWarnings("unchecked")
    public <E, P> EntityConverter<E, P> getConverter(Class<E> entityClass, Class<P> protocolClass) {
        String key = entityClass.getName() + "_" + protocolClass.getName();

        return (EntityConverter<E, P>) converterCache.computeIfAbsent(entityClass,
                k -> findConverter(entityClass, protocolClass));
    }

    /**
     * 查找转换器实现
     */
    private <E, P> EntityConverter<E, P> findConverter(Class<E> entityClass, Class<P> protocolClass) {
        Map<String, EntityConverter> converters = applicationContext.getBeansOfType(EntityConverter.class);

        for (EntityConverter<?, ?> converter : converters.values()) {
            if (isCompatibleConverter(converter, entityClass, protocolClass)) {
                return (EntityConverter<E, P>) converter;
            }
        }

        throw new IllegalArgumentException("未找到匹配的转换器: " + entityClass + " -> " + protocolClass);
    }

    /**
     * 检查转换器是否兼容
     */
    private boolean isCompatibleConverter(EntityConverter<?, ?> converter,
                                          Class<?> entityClass, Class<?> protocolClass) {
        // 这里需要通过泛型信息或其他方式判断转换器是否匹配
        // 简化实现，可以通过命名约定或注解来判断
        return converter.getClass().getSimpleName().toLowerCase()
                .contains(entityClass.getSimpleName().toLowerCase().replace("entity", ""));
    }
}
