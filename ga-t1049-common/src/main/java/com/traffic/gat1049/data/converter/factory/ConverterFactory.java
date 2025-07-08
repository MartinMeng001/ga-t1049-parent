// ================================================================
// 5. 转换器工厂
// ================================================================
package com.traffic.gat1049.data.converter.factory;

import com.traffic.gat1049.data.converter.interfaces.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 转换器工厂
 * 负责管理和获取转换器实例
 */
@Component
public class ConverterFactory {

    @Autowired
    private ApplicationContext applicationContext;

    // 转换器缓存
    private final Map<String, EntityConverter<?, ?>> converterCache = new ConcurrentHashMap<>();

    /**
     * 获取指定类型的转换器
     */
    @SuppressWarnings("unchecked")
    public <E, P> EntityConverter<E, P> getConverter(Class<E> entityClass, Class<P> protocolClass) {
        String cacheKey = entityClass.getName() + "_" + protocolClass.getName();

        return (EntityConverter<E, P>) converterCache.computeIfAbsent(cacheKey,
                k -> findConverterByType(entityClass, protocolClass));
    }

    /**
     * 通过类型查找转换器
     */
    @SuppressWarnings("unchecked")
    private <E, P> EntityConverter<E, P> findConverterByType(Class<E> entityClass, Class<P> protocolClass) {
        Map<String, EntityConverter> converters = applicationContext.getBeansOfType(EntityConverter.class);

        for (EntityConverter<?, ?> converter : converters.values()) {
            if (converter.getEntityType().equals(entityClass) &&
                    converter.getProtocolType().equals(protocolClass)) {
                return (EntityConverter<E, P>) converter;
            }
        }

        throw new IllegalArgumentException(
                String.format("没有找到匹配的转换器: %s -> %s", entityClass.getName(), protocolClass.getName()));
    }

    /**
     * 获取所有已注册的转换器信息
     */
    public Map<String, String> getConverterInfo() {
        Map<String, String> info = new ConcurrentHashMap<>();

        Map<String, EntityConverter> converters = applicationContext.getBeansOfType(EntityConverter.class);

        for (Map.Entry<String, EntityConverter> entry : converters.entrySet()) {
            EntityConverter<?, ?> converter = entry.getValue();
            String key = converter.getEntityType().getSimpleName() + " -> " +
                    converter.getProtocolType().getSimpleName();
            info.put(key, entry.getKey());
        }

        return info;
    }
}