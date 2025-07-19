package com.traffic.gat1049.service.abstracts;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 默认服务工厂实现
 * 注意：这个实现不使用Spring注解，需要在使用的模块中手动配置
 */
@Component
public class DefaultServiceFactory implements ServiceFactory {

    // 使用@Autowired自动注入所有服务
    @Autowired private SystemService systemService;
    @Autowired private RegionService regionService;
    @Autowired private SubRegionService subRegionService;
    @Autowired
    private RouteService routeService;
    @Autowired private CrossService crossService;
    @Autowired private SignalControllerService signalControllerService;
    @Autowired private LaneService laneService;
    @Autowired private DetectorService detectorService;
    @Autowired private SignalGroupService signalGroupService;
    @Autowired private PlanService planService;
    @Autowired private DayPlanService dayPlanService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private TrafficDataService trafficDataService;
    @Autowired private ControlService controlService;
    @Autowired private RouteControlService routeControlService;
    @Autowired private LampGroupServiceImpl lampGroupService;
    @Autowired private PedestrianServiceImpl pedestrianService;
    @Autowired private StageServiceImpl stageService;
    @Autowired private ControlModeService controlModeService;
    @Autowired private RunInfoRetransService runInfoRetransService;
    @Autowired private DoorStatusService doorStatusService;

    @PostConstruct
    public void init() {
        System.out.println("✅ SpringServiceFactory initialized with all dependencies injected");
    }

    // 实现所有getter方法
    @Override public SystemService getSystemService() { return systemService; }
    @Override public RegionService getRegionService() { return regionService; }
    @Override public SubRegionService getSubRegionService() { return subRegionService; }
    @Override public RouteService getRouteService() { return routeService; }
    @Override public CrossService getCrossService() { return crossService; }
    @Override public SignalControllerService getSignalControllerService() { return signalControllerService; }
    @Override public LaneService getLaneService() { return laneService; }
    @Override public DetectorService getDetectorService() { return detectorService; }
    @Override public SignalGroupService getSignalGroupService() { return signalGroupService; }
    @Override public PlanService getPlanService() { return planService; }
    @Override public DayPlanService getDayPlanService() { return dayPlanService; }
    @Override public ScheduleService getScheduleService() { return scheduleService; }
    @Override public TrafficDataService getTrafficDataService() { return trafficDataService; }
    @Override public ControlService getControlService() { return controlService; }
    @Override public RouteControlService getRouteControlService() { return routeControlService; }
    @Override public LampGroupServiceImpl getLampGroupService() { return lampGroupService; }
    @Override public PedestrianServiceImpl getPedestrianService() { return pedestrianService; }
    @Override public StageServiceImpl getStageService() { return stageService; }
    @Override public ControlModeService getControlModeService() { return controlModeService; }
    @Override public RunInfoRetransService getRunInfoRetransService() { return runInfoRetransService; }
    @Override public DoorStatusService getDoorStatusService() { return doorStatusService; }
}
