// ================================================================
// 1. 核心转换器接口
// ================================================================
package com.traffic.gat1049.data.converter.interfaces;

import java.util.List;

/**
 * 实体转换器核心接口
 * 定义数据库实体与GA/T 1049协议对象之间的双向转换规范
 *
 * @param <E> 数据库实体类型
 * @param <P> GA/T 1049协议对象类型
 */
public interface EntityConverter<E, P> {

    /**
     * 实体转协议对象
     */
    P toProtocol(E entity);

    /**
     * 协议对象转实体
     */
    E toEntity(P protocol);

    /**
     * 批量转换：实体列表转协议对象列表
     */
    List<P> toProtocolList(List<E> entities);

    /**
     * 批量转换：协议对象列表转实体列表
     */
    List<E> toEntityList(List<P> protocols);

    /**
     * 部分更新：将协议对象的非空字段更新到现有实体
     */
    void updateEntity(P protocol, E entity);

    /**
     * 获取支持的实体类型
     */
    Class<E> getEntityType();

    /**
     * 获取支持的协议类型
     */
    Class<P> getProtocolType();
}