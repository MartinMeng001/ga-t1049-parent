package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.system.RouteParam;
import com.traffic.gat1049.protocol.model.system.RouteCross;
import com.traffic.gat1049.model.enums.RouteType;

import java.util.List;

/**
 * 线路服务接口
 */
public interface RouteService extends BaseService<RouteParam, String> {

    /**
     * 根据线路名称查询
     *
     * @param routeName 线路名称
     * @return 线路参数列表
     * @throws BusinessException 业务异常
     */
    List<RouteParam> findByName(String routeName) throws BusinessException;

    /**
     * 根据线路类型查询
     *
     * @param routeType 线路类型
     * @return 线路参数列表
     * @throws BusinessException 业务异常
     */
    List<RouteParam> findByType(RouteType routeType) throws BusinessException;

    /**
     * 获取线路包含的路口列表
     *
     * @param routeId 线路编号
     * @return 路口列表
     * @throws BusinessException 业务异常
     */
    List<RouteCross> getRouteCrosses(String routeId) throws BusinessException;

    /**
     * 添加路口到线路
     *
     * @param routeId 线路编号
     * @param crossId 路口编号
     * @param distance 距离
     * @throws BusinessException 业务异常
     */
    void addCross(String routeId, String crossId, Integer distance) throws BusinessException;

    /**
     * 从线路移除路口
     *
     * @param routeId 线路编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void removeCross(String routeId, String crossId) throws BusinessException;

    /**
     * 更新路口在线路中的距离
     *
     * @param routeId 线路编号
     * @param crossId 路口编号
     * @param distance 新距离
     * @throws BusinessException 业务异常
     */
    void updateCrossDistance(String routeId, String crossId, Integer distance) throws BusinessException;
}
