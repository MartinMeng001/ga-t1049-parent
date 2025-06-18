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
     * 获取人行服务
     */
    PedestrianService getPedestrianService();

    /**
     * 获取检测器服务
     */
    DetectorService getDetectorService();

    /**
     * 获取灯组服务
     */
    LampGroupService getLampGroupService();

    /**
     * 获取信号组服务
     */
    SignalGroupService getSignalGroupService();

    /**
     * 获取阶段服务
     */
    StageService getStageService();

    /**
     * 获取配时方案服务
     */
    PlanService getPlanService();

    /**
     * 获取日计划服务
     */
    DayPlanService getDayPlanService();

    /**
     * 获取调度服务
     */
    ScheduleService getScheduleService();

    /**
     * 获取交通数据服务
     */
    TrafficDataService getTrafficDataService();

    /**
     * 获取控制服务
     */
    ControlService getControlService();

    /**
     * 获取控制模式服务
     */
    ControlModeService getControlModeService();

    /**
     * 获取干线控制服务
     */
    RouteControlService getRouteControlService();
}