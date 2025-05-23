package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.entity.runtime.RouteControlModeStatus;
import com.traffic.gat1049.model.entity.runtime.RouteSpeed;
import com.traffic.gat1049.model.entity.runtime.RoadSectionSpeed;
import com.traffic.gat1049.model.enums.RouteControlMode;

import java.util.List;

/**
 * 干线控制服务接口
 */
public interface RouteControlService {

    /**
     * 获取干线控制方式
     *
     * @param routeId 线路编号
     * @return 干线控制方式
     * @throws BusinessException 业务异常
     */
    RouteControlModeStatus getRouteControlMode(String routeId) throws BusinessException;

    /**
     * 设置干线控制方式
     *
     * @param routeId 线路编号
     * @param controlMode 控制方式
     * @throws BusinessException 业务异常
     */
    void setRouteControlMode(String routeId, RouteControlMode controlMode) throws BusinessException;

    /**
     * 获取干线路段推荐车速
     *
     * @param routeId 线路编号
     * @return 干线路段推荐车速
     * @throws BusinessException 业务异常
     */
    RouteSpeed getRouteSpeed(String routeId) throws BusinessException;

    /**
     * 设置干线路段推荐车速
     *
     * @param routeSpeed 干线路段推荐车速
     * @throws BusinessException 业务异常
     */
    void setRouteSpeed(RouteSpeed routeSpeed) throws BusinessException;

    /**
     * 设置单个路段推荐车速
     *
     * @param routeId 线路编号
     * @param upCrossId 上游路口编号
     * @param downCrossId 下游路口编号
     * @param recommendSpeed 推荐车速
     * @throws BusinessException 业务异常
     */
    void setRoadSectionSpeed(String routeId, String upCrossId, String downCrossId, Integer recommendSpeed) throws BusinessException;

    /**
     * 获取路段推荐车速
     *
     * @param routeId 线路编号
     * @param upCrossId 上游路口编号
     * @param downCrossId 下游路口编号
     * @return 路段推荐车速
     * @throws BusinessException 业务异常
     */
    RoadSectionSpeed getRoadSectionSpeed(String routeId, String upCrossId, String downCrossId) throws BusinessException;

    /**
     * 启动干线协调控制
     *
     * @param routeId 线路编号
     * @throws BusinessException 业务异常
     */
    void startCoordination(String routeId) throws BusinessException;

    /**
     * 停止干线协调控制
     *
     * @param routeId 线路编号
     * @throws BusinessException 业务异常
     */
    void stopCoordination(String routeId) throws BusinessException;

    /**
     * 检查干线是否在协调控制中
     *
     * @param routeId 线路编号
     * @return 是否在协调控制中
     * @throws BusinessException 业务异常
     */
    boolean isCoordinating(String routeId) throws BusinessException;
}
