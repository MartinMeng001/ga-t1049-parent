// ================================================================
// 6. 转换服务（业务门面）
// ================================================================
package com.traffic.gat1049.data.converter.service;

import com.traffic.gat1049.data.converter.factory.ConverterFactory;
import com.traffic.gat1049.data.converter.interfaces.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据转换服务
 * 提供统一的转换服务接口
 */
@Service
public class DataConverterService {

    @Autowired
    private ConverterFactory converterFactory;

    /**
     * 实体转协议对象
     */
    public <E, P> P toProtocol(E entity, Class<P> protocolClass) {
        if (entity == null) {
            return null;
        }

        EntityConverter<E, P> converter = getConverter(entity, protocolClass);
        return converter.toProtocol(entity);
    }

    /**
     * 协议对象转实体
     */
    public <E, P> E toEntity(P protocol, Class<E> entityClass) {
        if (protocol == null) {
            return null;
        }

        EntityConverter<E, P> converter = getConverter(entityClass, protocol);
        return converter.toEntity(protocol);
    }

    /**
     * 批量实体转协议对象
     */
    public <E, P> List<P> toProtocolList(List<E> entities, Class<P> protocolClass) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        EntityConverter<E, P> converter = getConverter(entities.get(0), protocolClass);
        return converter.toProtocolList(entities);
    }

    /**
     * 批量协议对象转实体
     */
    public <E, P> List<E> toEntityList(List<P> protocols, Class<E> entityClass) {
        if (protocols == null || protocols.isEmpty()) {
            return List.of();
        }

        EntityConverter<E, P> converter = getConverter(entityClass, protocols.get(0));
        return converter.toEntityList(protocols);
    }

    /**
     * 更新实体
     */
    public <E, P> void updateEntity(P protocol, E entity) {
        if (protocol == null || entity == null) {
            return;
        }

        EntityConverter<E, P> converter = getConverter(entity, protocol);
        converter.updateEntity(protocol, entity);
    }

    /**
     * 获取转换器
     */
    @SuppressWarnings("unchecked")
    private <E, P> EntityConverter<E, P> getConverter(Object entity, Object protocol) {
        Class<E> entityClass = (Class<E>) entity.getClass();
        Class<P> protocolClass = (Class<P>) protocol.getClass();
        return converterFactory.getConverter(entityClass, protocolClass);
    }

    @SuppressWarnings("unchecked")
    private <E, P> EntityConverter<E, P> getConverter(Class<E> entityClass, Object protocol) {
        Class<P> protocolClass = (Class<P>) protocol.getClass();
        return converterFactory.getConverter(entityClass, protocolClass);
    }

    @SuppressWarnings("unchecked")
    private <E, P> EntityConverter<E, P> getConverter(Object entity, Class<P> protocolClass) {
        Class<E> entityClass = (Class<E>) entity.getClass();
        return converterFactory.getConverter(entityClass, protocolClass);
    }
}