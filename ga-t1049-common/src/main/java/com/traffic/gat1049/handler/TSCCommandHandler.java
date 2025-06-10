package com.traffic.gat1049.handler;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.model.entity.command.TSCCmd;
import com.traffic.gat1049.protocol.handler.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * TSCCmd命令处理器基类
 * 处理配置参数和运行信息的查询、通知命令
 */
public class TSCCommandHandler extends AbstractProtocolHandler {

    protected final ServiceFactory serviceFactory;

    public TSCCommandHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isQueryRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof TSCCmd;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        TSCCmd tscCmd = (TSCCmd) ProtocolUtils.getOperationData(message);

        try {
            // 验证TSCCmd
            validateTSCCmd(tscCmd);

            // 根据对象名称分发处理
            Object result = dispatchQuery(tscCmd);

            // 创建成功响应
            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("TSCCmd validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (DataNotFoundException e) {
            logger.error("Data not found: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OBJECT_NOT_FOUND, e.getMessage());
        } catch (BusinessException e) {
            logger.error("Business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage());
        }
    }

    /**
     * 验证TSCCmd参数
     */
    private void validateTSCCmd(TSCCmd tscCmd) throws ValidationException {
        if (tscCmd.getObjName() == null || tscCmd.getObjName().trim().isEmpty()) {
            throw new ValidationException("objName", "Object name cannot be null or empty");
        }
        try {
            validator.validateObjectName(tscCmd.getObjName());
            validator.validateId(tscCmd.getObjName(), tscCmd.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 根据对象名称分发查询请求
     */
    private Object dispatchQuery(TSCCmd tscCmd) throws BusinessException {
        String objName = tscCmd.getObjName();
        String id = tscCmd.getId();
        Integer no = tscCmd.getNo();

        switch (objName) {
            case GatConstants.ObjectName.SYS_INFO:
                return serviceFactory.getSystemService().getSystemInfo();

            case GatConstants.ObjectName.SYS_STATE:
                return serviceFactory.getSystemService().getSystemState();

            case GatConstants.ObjectName.REGION_PARAM:
                return handleRegionParam(id);

            case GatConstants.ObjectName.SUB_REGION_PARAM:
                return handleSubRegionParam(id);

            case GatConstants.ObjectName.ROUTE_PARAM:
                return handleRouteParam(id);

            case GatConstants.ObjectName.CROSS_PARAM:
                return handleCrossParam(id);

            case GatConstants.ObjectName.SIGNAL_CONTROLLER:
                return handleSignalController(id);

            case GatConstants.ObjectName.LAMP_GROUP:
                return handleLampGroup(id, no);

            case GatConstants.ObjectName.DETECTOR_PARAM:
                return handleDetectorParam(id, no);

            case GatConstants.ObjectName.LANE_PARAM:
                return handleLaneParam(id, no);

            case GatConstants.ObjectName.PEDESTRIAN_PARAM:
                return handlePedestrianParam(id, no);

            case GatConstants.ObjectName.SIGNAL_GROUP_PARAM:
                return handleSignalGroupParam(id, no);

            case GatConstants.ObjectName.STAGE_PARAM:
                return handleStageParam(id, no);

            case GatConstants.ObjectName.PLAN_PARAM:
                return handlePlanParam(id, no);

            case GatConstants.ObjectName.DAY_PLAN_PARAM:
                return handleDayPlanParam(id, no);

            case GatConstants.ObjectName.SCHEDULE_PARAM:
                return handleScheduleParam(id, no);

            case GatConstants.ObjectName.CROSS_STATE:
                return handleCrossState(id);

            case GatConstants.ObjectName.CROSS_MODE_PLAN:
                return handleCrossModePlan(id);

            case GatConstants.ObjectName.CROSS_CYCLE:
                return handleCrossCycle(id);

            case GatConstants.ObjectName.CROSS_STAGE:
                return handleCrossStage(id);

            case GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS:
                return handleCrossSignalGroupStatus(id);

            case GatConstants.ObjectName.CROSS_TRAFFIC_DATA:
                return handleCrossTrafficData(id);

            case GatConstants.ObjectName.STAGE_TRAFFIC_DATA:
                return handleStageTrafficData(id, no);

            case GatConstants.ObjectName.VAR_LANE_STATUS:
                return handleVarLaneStatus(id, no);

            case GatConstants.ObjectName.ROUTE_CONTROL_MODE:
                return handleRouteControlMode(id);

            case GatConstants.ObjectName.ROUTE_SPEED:
                return handleRouteSpeed(id);

            default:
                throw new ValidationException("objName", "Unsupported object name: " + objName);
        }
    }

    // ==================== 配置参数处理方法 ====================

    private Object handleRegionParam(String regionId) throws BusinessException {
        if (regionId == null) {
            return serviceFactory.getRegionService().findAll();
        }
        return serviceFactory.getRegionService().findById(regionId);
    }

    private Object handleSubRegionParam(String subRegionId) throws BusinessException {
        if (subRegionId == null) {
            return serviceFactory.getSubRegionService().findAll();
        }
        return serviceFactory.getSubRegionService().findById(subRegionId);
    }

    private Object handleRouteParam(String routeId) throws BusinessException {
        if (routeId == null) {
            return serviceFactory.getRouteService().findAll();
        }
        return serviceFactory.getRouteService().findById(routeId);
    }

    private Object handleCrossParam(String crossId) throws BusinessException {
        if (crossId == null) {
            return serviceFactory.getCrossService().findAll();
        }
        return serviceFactory.getCrossService().findById(crossId);
    }

    private Object handleSignalController(String signalControllerId) throws BusinessException {
        if (signalControllerId == null) {
            return serviceFactory.getSignalControllerService().findAll();
        }
        return serviceFactory.getSignalControllerService().findById(signalControllerId);
    }

    private Object handleLampGroup(String crossId, Integer lampGroupNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for LampGroup query");
        }
        // 这里需要实现LampGroupService，暂时返回空列表
        logger.warn("LampGroup query not fully implemented for crossId: {}, lampGroupNo: {}", crossId, lampGroupNo);
        return java.util.Collections.emptyList();
    }

    private Object handleDetectorParam(String crossId, Integer detectorNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for DetectorParam query");
        }
        if (detectorNo == null) {
            return serviceFactory.getDetectorService().findByCrossId(crossId);
        }
        return serviceFactory.getDetectorService().findByCrossIdAndDetectorNo(crossId, detectorNo);
    }

    private Object handleLaneParam(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for LaneParam query");
        }
        if (laneNo == null) {
            return serviceFactory.getLaneService().findByCrossId(crossId);
        }
        return serviceFactory.getLaneService().findByCrossIdAndLaneNo(crossId, laneNo);
    }

    private Object handlePedestrianParam(String crossId, Integer pedestrianNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for PedestrianParam query");
        }
        // 这里需要实现PedestrianService，暂时返回空列表
        logger.warn("PedestrianParam query not fully implemented for crossId: {}, pedestrianNo: {}", crossId, pedestrianNo);
        return java.util.Collections.emptyList();
    }

    private Object handleSignalGroupParam(String crossId, Integer signalGroupNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for SignalGroupParam query");
        }
        if (signalGroupNo == null) {
            return serviceFactory.getSignalGroupService().findByCrossId(crossId);
        }
        return serviceFactory.getSignalGroupService().findByCrossIdAndSignalGroupNo(crossId, signalGroupNo);
    }

    private Object handleStageParam(String crossId, Integer stageNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for StageParam query");
        }
        // 这里需要实现StageService，暂时返回空列表
        logger.warn("StageParam query not fully implemented for crossId: {}, stageNo: {}", crossId, stageNo);
        return java.util.Collections.emptyList();
    }

    private Object handlePlanParam(String crossId, Integer planNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for PlanParam query");
        }
        if (planNo == null) {
            return serviceFactory.getPlanService().findByCrossId(crossId);
        }
        return serviceFactory.getPlanService().findByCrossIdAndPlanNo(crossId, planNo);
    }

    private Object handleDayPlanParam(String crossId, Integer dayPlanNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for DayPlanParam query");
        }
        if (dayPlanNo == null) {
            throw new ValidationException("dayPlanNo", "Day plan number is required for DayPlanParam query");
        }
        return serviceFactory.getPlanService().getDayPlanParam(crossId, dayPlanNo);
    }

    private Object handleScheduleParam(String crossId, Integer scheduleNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for ScheduleParam query");
        }
        if (scheduleNo == null) {
            throw new ValidationException("scheduleNo", "Schedule number is required for ScheduleParam query");
        }
        return serviceFactory.getPlanService().getScheduleParam(crossId, scheduleNo);
    }

    // ==================== 运行信息处理方法 ====================

    private Object handleCrossState(String crossId) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for CrossState query");
        }
        return serviceFactory.getCrossService().getCrossState(crossId);
    }

    private Object handleCrossModePlan(String crossId) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for CrossModePlan query");
        }
        return serviceFactory.getPlanService().getCurrentModePlan(crossId);
    }

    private Object handleCrossCycle(String crossId) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for CrossCycle query");
        }
        return serviceFactory.getTrafficDataService().getCrossCycle(crossId);
    }

    private Object handleCrossStage(String crossId) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for CrossStage query");
        }
        return serviceFactory.getTrafficDataService().getCrossStage(crossId);
    }

    private Object handleCrossSignalGroupStatus(String crossId) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for CrossSignalGroupStatus query");
        }
        return serviceFactory.getSignalGroupService().getCrossSignalGroupStatus(crossId);
    }

    private Object handleCrossTrafficData(String crossId) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for CrossTrafficData query");
        }
        // 获取最新的交通流数据，使用当前时间和默认间隔
        return serviceFactory.getTrafficDataService().getCrossTrafficData(
                crossId, java.time.LocalDateTime.now(), 300);
    }

    private Object handleStageTrafficData(String crossId, Integer stageNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for StageTrafficData query");
        }
        if (stageNo == null) {
            throw new ValidationException("stageNo", "Stage number is required for StageTrafficData query");
        }
        // 获取当前阶段的交通流数据
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return serviceFactory.getTrafficDataService().getStageTrafficData(
                crossId, stageNo, now.minusMinutes(5), now);
    }

    private Object handleVarLaneStatus(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for VarLaneStatus query");
        }
        if (laneNo == null) {
            return serviceFactory.getLaneService().getVarLanes(crossId);
        }
        return serviceFactory.getLaneService().getVarLaneStatus(crossId, laneNo);
    }

    private Object handleRouteControlMode(String routeId) throws BusinessException {
        if (routeId == null) {
            throw new ValidationException("routeId", "Route ID is required for RouteControlMode query");
        }
        return serviceFactory.getRouteControlService().getRouteControlMode(routeId);
    }

    private Object handleRouteSpeed(String routeId) throws BusinessException {
        if (routeId == null) {
            throw new ValidationException("routeId", "Route ID is required for RouteSpeed query");
        }
        return serviceFactory.getRouteControlService().getRouteSpeed(routeId);
    }

    @Override
    public String getHandlerName() {
        return "TSCCommandHandler";
    }
}
