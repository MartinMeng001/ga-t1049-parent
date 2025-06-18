package com.traffic.gat1049.service.abstracts;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.service.interfaces.*;

/**
 * 默认服务工厂实现
 * 注意：这个实现不使用Spring注解，需要在使用的模块中手动配置
 */
public class DefaultServiceFactory implements ServiceFactory {

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
    private DayPlanService dayPlanService;
    private ScheduleService scheduleService;
    private TrafficDataService trafficDataService;
    private ControlService controlService;
    private RouteControlService routeControlService;
    private LampGroupServiceImpl lampGroupService;
    private PedestrianServiceImpl pedestrianService;
    private StageServiceImpl stageService;
    private ControlModeService controlModeService;

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
            DayPlanService dayPlanService,
            ScheduleService scheduleService,
            TrafficDataService trafficDataService,
            ControlService controlService,
            RouteControlService routeControlService,
            LampGroupServiceImpl lampGroupService,
            PedestrianServiceImpl pedestrianService,
            ControlModeService controlModeService,
            StageServiceImpl stageService) {

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
        this.dayPlanService = dayPlanService;
        this.scheduleService = scheduleService;
        this.trafficDataService = trafficDataService;
        this.controlService = controlService;
        this.routeControlService = routeControlService;
        this.lampGroupService = lampGroupService;
        this.pedestrianService = pedestrianService;
        this.controlModeService = controlModeService;
        this.stageService = stageService;
    }

    // 默认构造函数，创建默认实现
    public DefaultServiceFactory() throws BusinessException {
        this.systemService = new SystemServiceImpl();
        this.crossService = new CrossServiceImpl();
        this.planService = new PlanServiceImpl();
        this.dayPlanService = new DayPlanServiceImpl();
        this.scheduleService = new ScheduleServiceImpl();
        this.controlService = new ControlServiceImpl();
        this.regionService = new RegionServiceImpl();
        this.subRegionService = new SubRegionServiceImpl();
        this.routeService = new RouteServiceImpl();
        this.signalControllerService = new SignalControllerServiceImpl();
        this.laneService = new LaneServiceImpl();
        this.detectorService = new DetectorServiceImpl();
        this.signalGroupService = new SignalGroupServiceImpl();
        this.trafficDataService = new TrafficDataServiceImpl();
        this.routeControlService = new RouteControlServiceImpl();
        this.lampGroupService = new LampGroupServiceImpl();
        this.pedestrianService = new PedestrianServiceImpl();
        this.controlModeService = new ControlModeServiceImpl();
        this.stageService = new StageServiceImpl();
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
    public PedestrianService getPedestrianService() {
        return pedestrianService;
    }

    @Override
    public DetectorService getDetectorService() {
        return detectorService;
    }

    @Override
    public LampGroupService getLampGroupService() {
        return lampGroupService;
    }

    @Override
    public SignalGroupService getSignalGroupService() {
        return signalGroupService;
    }

    @Override
    public StageService getStageService() {
        return stageService;
    }

    @Override
    public PlanService getPlanService() {
        return planService;
    }

    @Override
    public DayPlanService getDayPlanService() {
        if (dayPlanService == null) {
            try {
                dayPlanService = new DayPlanServiceImpl();
            } catch (BusinessException e) {
                throw new RuntimeException("Failed to create DayPlanService", e);
            }
        }
        return dayPlanService;
    }

    @Override
    public ScheduleService getScheduleService() {
        if (scheduleService == null) {
            try {
                scheduleService = new ScheduleServiceImpl();
            } catch (BusinessException e) {
                throw new RuntimeException("Failed to create ScheduleService", e);
            }
        }
        return scheduleService;
    }

    @Override
    public ControlModeService getControlModeService() {
        if (controlModeService == null) {
            try {
                // 创建ControlModeService时注入PlanService依赖
                ControlModeServiceImpl impl = new ControlModeServiceImpl();
                impl.setPlanService(getPlanService());
                controlModeService = impl;
            } catch (BusinessException e) {
                throw new RuntimeException("Failed to create ControlModeService", e);
            }
        }
        return controlModeService;
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
