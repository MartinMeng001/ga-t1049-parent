package com.traffic.gat1049.data.converter.factory;

import com.traffic.gat1049.data.converter.interfaces.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 改进的转换器工厂
 * 支持精确的泛型类型匹配
 */
@Component
public class ConverterFactory {

    @Autowired
    private ApplicationContext applicationContext;

    // 缓存转换器，避免重复查找
    private final Map<String, EntityConverter<?, ?>> converterCache = new ConcurrentHashMap<>();

    // 缓存类型信息，提高性能
    private final Map<Class<?>, ConverterTypeInfo> typeInfoCache = new ConcurrentHashMap<>();

    /**
     * 获取指定类型的转换器（改进版）
     */
    @SuppressWarnings("unchecked")
    public <E, P> EntityConverter<E, P> getConverter(Class<E> entityClass, Class<P> protocolClass) {
        String cacheKey = entityClass.getName() + "_" + protocolClass.getName();

        return (EntityConverter<E, P>) converterCache.computeIfAbsent(cacheKey,
                k -> findConverterByGenericType(entityClass, protocolClass));
    }

    /**
     * 通过泛型类型精确查找转换器
     */
    private <E, P> EntityConverter<E, P> findConverterByGenericType(Class<E> entityClass, Class<P> protocolClass) {
        Map<String, EntityConverter> converters = applicationContext.getBeansOfType(EntityConverter.class);

        for (EntityConverter<?, ?> converter : converters.values()) {
            ConverterTypeInfo typeInfo = getConverterTypeInfo(converter.getClass());

            if (typeInfo != null &&
                    typeInfo.entityClass.equals(entityClass) &&
                    typeInfo.protocolClass.equals(protocolClass)) {
                return (EntityConverter<E, P>) converter;
            }
        }

        throw new IllegalArgumentException(
                String.format("未找到匹配的转换器: %s -> %s", entityClass.getSimpleName(), protocolClass.getSimpleName()));
    }

    /**
     * 获取转换器的泛型类型信息
     */
    private ConverterTypeInfo getConverterTypeInfo(Class<?> converterClass) {
        return typeInfoCache.computeIfAbsent(converterClass, this::extractTypeInfo);
    }

    /**
     * 提取转换器的泛型类型信息
     */
    private ConverterTypeInfo extractTypeInfo(Class<?> converterClass) {
        try {
            // 获取直接实现的接口
            Type[] genericInterfaces = converterClass.getGenericInterfaces();

            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) genericInterface;

                    if (EntityConverter.class.equals(paramType.getRawType())) {
                        Type[] typeArgs = paramType.getActualTypeArguments();
                        if (typeArgs.length == 2) {
                            Class<?> entityClass = (Class<?>) typeArgs[0];
                            Class<?> protocolClass = (Class<?>) typeArgs[1];
                            return new ConverterTypeInfo(entityClass, protocolClass);
                        }
                    }
                }
            }

            // 检查父类的泛型信息
            Type genericSuperclass = converterClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) genericSuperclass;
                Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length == 2) {
                    Class<?> entityClass = (Class<?>) typeArgs[0];
                    Class<?> protocolClass = (Class<?>) typeArgs[1];
                    return new ConverterTypeInfo(entityClass, protocolClass);
                }
            }

        } catch (Exception e) {
            // 记录日志但不抛出异常
            System.err.println("无法提取转换器类型信息: " + converterClass.getName() + ", 错误: " + e.getMessage());
        }

        return null;
    }

    /**
     * 注册自定义转换器
     */
    public <E, P> void registerConverter(Class<E> entityClass, Class<P> protocolClass,
                                         EntityConverter<E, P> converter) {
        String cacheKey = entityClass.getName() + "_" + protocolClass.getName();
        converterCache.put(cacheKey, converter);
    }

    /**
     * 获取所有已注册的转换器信息
     */
    public Map<String, String> getConverterInfo() {
        Map<String, String> info = new HashMap<>();

        Map<String, EntityConverter> converters = applicationContext.getBeansOfType(EntityConverter.class);

        for (Map.Entry<String, EntityConverter> entry : converters.entrySet()) {
            ConverterTypeInfo typeInfo = getConverterTypeInfo(entry.getValue().getClass());
            if (typeInfo != null) {
                String key = typeInfo.entityClass.getSimpleName() + " -> " + typeInfo.protocolClass.getSimpleName();
                info.put(key, entry.getKey());
            }
        }

        return info;
    }

    /**
     * 转换器类型信息内部类
     */
    private static class ConverterTypeInfo {
        final Class<?> entityClass;
        final Class<?> protocolClass;

        ConverterTypeInfo(Class<?> entityClass, Class<?> protocolClass) {
            this.entityClass = entityClass;
            this.protocolClass = protocolClass;
        }
    }
}