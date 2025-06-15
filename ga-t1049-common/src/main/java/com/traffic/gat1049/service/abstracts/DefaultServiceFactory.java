package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认服务工厂实现
 * 注意：这个实现不使用Spring注解，需要在使用的模块中手动配置
 */
public class DefaultServiceFactory implements ServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceFactory.class);

    private SystemService systemService;
    private RegionService regionService;
    private SubRegionService subRegionService;
    private RouteService routeService;
    private CrossService crossService;
    private SignalControllerService signalControllerService;
    private LaneService laneService;
    private DetectorService detectorService;
    private SignalGroupService signalGroupService;
    private PlanService planService;
    private TrafficDataService trafficDataService;
    private ControlService controlService;
    private RouteControlService routeControlService;

    // 构造函数注入
    public DefaultServiceFactory(
            SystemService systemService,
            RegionService regionService,
            SubRegionService subRegionService,
            RouteService routeService,
            CrossService crossService,
            SignalControllerService signalControllerService,
            LaneService laneService,
            DetectorService detectorService,
            SignalGroupService signalGroupService,
            PlanService planService,
            TrafficDataService trafficDataService,
            ControlService controlService,
            RouteControlService routeControlService) {

        this.systemService = systemService;
        this.regionService = regionService;
        this.subRegionService = subRegionService;
        this.routeService = routeService;
        this.crossService = crossService;
        this.signalControllerService = signalControllerService;
        this.laneService = laneService;
        this.detectorService = detectorService;
        this.signalGroupService = signalGroupService;
        this.planService = planService;
        this.trafficDataService = trafficDataService;
        this.controlService = controlService;
        this.routeControlService = routeControlService;
    }

    // 默认构造函数，创建默认实现
    public DefaultServiceFactory() throws BusinessException {
        // 已实现的服务
        this.systemService = new SystemServiceImpl();
        this.crossService = new CrossServiceImpl();
        this.planService = new PlanServiceImpl();
        this.controlService = new ControlServiceImpl();

        // 新增实现的服务 - 参照SystemService模式，提供查询数据的获取
        this.regionService = new RegionServiceImpl();
        this.subRegionService = new SubRegionServiceImpl();
        this.routeService = new RouteServiceImpl();
        this.signalControllerService = new SignalControllerServiceImpl();
        this.laneService = new LaneServiceImpl();
        this.detectorService = new DetectorServiceImpl();
        this.signalGroupService = new SignalGroupServiceImpl();
        this.trafficDataService = new TrafficDataServiceImpl();
        this.routeControlService = new RouteControlServiceImpl();

        logger.info("DefaultServiceFactory 初始化完成，所有服务已实现");
    }

    @Override
    public SystemService getSystemService() {
        return systemService;
    }

    @Override
    public RegionService getRegionService() {
        return regionService;
    }

    @Override
    public SubRegionService getSubRegionService() {
        return subRegionService;
    }

    @Override
    public RouteService getRouteService() {
        return routeService;
    }

    @Override
    public CrossService getCrossService() {
        return crossService;
    }

    @Override
    public SignalControllerService getSignalControllerService() {
        return signalControllerService;
    }

    @Override
    public LaneService getLaneService() {
        return laneService;
    }

    @Override
    public DetectorService getDetectorService() {
        return detectorService;
    }

    @Override
    public SignalGroupService getSignalGroupService() {
        return signalGroupService;
    }

    @Override
    public PlanService getPlanService() {
        return planService;
    }

    @Override
    public TrafficDataService getTrafficDataService() {
        return trafficDataService;
    }

    @Override
    public ControlService getControlService() {
        return controlService;
    }

    @Override
    public RouteControlService getRouteControlService() {
        return routeControlService;
    }
}