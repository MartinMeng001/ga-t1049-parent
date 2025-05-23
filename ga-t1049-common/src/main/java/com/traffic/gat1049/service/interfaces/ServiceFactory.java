package com.traffic.gat1049.service.interfaces;

/**
 * 服务工厂接口
 */
public interface ServiceFactory {

    /**
     * 获取系统服务
     */
    SystemService getSystemService();

    /**
     * 获取区域服务
     */
    RegionService getRegionService();

    /**
     * 获取子区服务
     */
    SubRegionService getSubRegionService();

    /**
     * 获取线路服务
     */
    RouteService getRouteService();

    /**
     * 获取路口服务
     */
    CrossService getCrossService();

    /**
     * 获取信号机服务
     */
    SignalControllerService getSignalControllerService();

    /**
     * 获取车道服务
     */
    LaneService getLaneService();

    /**
     * 获取检测器服务
     */
    DetectorService getDetectorService();

    /**
     * 获取信号组服务
     */
    SignalGroupService getSignalGroupService();

    /**
     * 获取配时方案服务
     */
    PlanService getPlanService();

    /**
     * 获取交通数据服务
     */
    TrafficDataService getTrafficDataService();

    /**
     * 获取控制服务
     */
    ControlService getControlService();

    /**
     * 获取干线控制服务
     */
    RouteControlService getRouteControlService();
}
