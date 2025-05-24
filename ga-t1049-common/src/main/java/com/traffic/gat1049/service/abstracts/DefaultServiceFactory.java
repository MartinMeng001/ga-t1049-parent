package com.traffic.gat1049.service.abstracts;
import com.traffic.gat1049.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 默认服务工厂实现
 */
@Component
public class DefaultServiceFactory implements ServiceFactory {

    @Autowired
    private SystemService systemService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private SubRegionService subRegionService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private CrossService crossService;
    @Autowired
    private SignalControllerService signalControllerService;
    @Autowired
    private LaneService laneService;
    @Autowired
    private DetectorService detectorService;
    @Autowired
    private SignalGroupService signalGroupService;
    @Autowired
    private PlanService planService;
    @Autowired
    private TrafficDataService trafficDataService;
    @Autowired
    private ControlService controlService;
    @Autowired
    private RouteControlService routeControlService;

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
