package com.traffic.gat1049.data.converter.interfaces;

import java.util.List;

/**
 * 实体转换器接口
 * 定义JPA实体与GA/T 1049协议对象之间的转换规范
 */
public interface EntityConverter<E, P> {

    /**
     * 将JPA实体转换为GA/T 1049协议对象
     * @param entity JPA实体
     * @return GA/T 1049协议对象
     */
    P toProtocol(E entity);

    /**
     * 将GA/T 1049协议对象转换为JPA实体
     * @param protocol GA/T 1049协议对象
     * @return JPA实体
     */
    E toEntity(P protocol);

    /**
     * 批量转换：实体列表转协议对象列表
     * @param entities JPA实体列表
     * @return 协议对象列表
     */
    List<P> toProtocolList(List<E> entities);

    /**
     * 批量转换：协议对象列表转实体列表
     * @param protocols 协议对象列表
     * @return JPA实体列表
     */
    List<E> toEntityList(List<P> protocols);

    /**
     * 部分更新：将协议对象的非空字段更新到实体
     * @param protocol 协议对象
     * @param entity 目标实体
     */
    default void updateEntity(P protocol, E entity) {
        throw new UnsupportedOperationException("部分更新功能未实现");
    }
}
