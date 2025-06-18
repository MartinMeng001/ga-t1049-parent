package com.traffic.gat1049.protocol.handler.main;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.TSCCmd;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 配置参数查询处理器
 */
public class ConfigParamHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public ConfigParamHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isQueryRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        if (!(data instanceof TSCCmd)) {
            return false;
        }

        TSCCmd tscCmd = (TSCCmd) data;
        String objName = tscCmd.getObjName();

        // 支持的配置参数对象
        return GatConstants.ObjectName.REGION_PARAM.equals(objName) ||
                GatConstants.ObjectName.SUB_REGION_PARAM.equals(objName) ||
                GatConstants.ObjectName.ROUTE_PARAM.equals(objName) ||
                GatConstants.ObjectName.CROSS_PARAM.equals(objName) ||
                GatConstants.ObjectName.SIGNAL_CONTROLLER.equals(objName) ||
                GatConstants.ObjectName.LAMP_GROUP.equals(objName) ||
                GatConstants.ObjectName.DETECTOR_PARAM.equals(objName) ||
                GatConstants.ObjectName.LANE_PARAM.equals(objName) ||
                GatConstants.ObjectName.PEDESTRIAN_PARAM.equals(objName) ||
                GatConstants.ObjectName.SIGNAL_GROUP_PARAM.equals(objName) ||
                GatConstants.ObjectName.STAGE_PARAM.equals(objName) ||
                GatConstants.ObjectName.PLAN_PARAM.equals(objName) ||
                GatConstants.ObjectName.DAY_PLAN_PARAM.equals(objName) ||
                GatConstants.ObjectName.SCHEDULE_PARAM.equals(objName);
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        TSCCmd tscCmd = (TSCCmd) ProtocolUtils.getOperationData(message);
        String objName = tscCmd.getObjName();
        try {
            validateTSCCmd(tscCmd);
            Object result = dispatchConfigQuery(tscCmd);
            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("Config param validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), objName);
        } catch (BusinessException e) {
            logger.error("Config param business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), objName);
        }
    }

    private void validateTSCCmd(TSCCmd tscCmd) throws ValidationException {
        if (tscCmd.getObjName() == null || tscCmd.getObjName().trim().isEmpty()) {
            throw new ValidationException("objName", "Object name cannot be null or empty");
        }
        try {
            messageValidator.validateObjectName(tscCmd.getObjName());
            messageValidator.validateId(tscCmd.getObjName(), tscCmd.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Object dispatchConfigQuery(TSCCmd tscCmd) throws BusinessException {
        String objName = tscCmd.getObjName();
        String id = tscCmd.getId();
        Integer no = tscCmd.getNo();

        switch (objName) {
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
            case GatConstants.ObjectName.DETECTOR_PARAM:
                return handleDetectorParam(id, no);
            case GatConstants.ObjectName.LANE_PARAM:
                return handleLaneParam(id, no);
            case GatConstants.ObjectName.SIGNAL_GROUP_PARAM:
                return handleSignalGroupParam(id, no);
            case GatConstants.ObjectName.PLAN_PARAM:
                return handlePlanParam(id, no);
            case GatConstants.ObjectName.DAY_PLAN_PARAM:
                return handleDayPlanParam(id, no);
            case GatConstants.ObjectName.SCHEDULE_PARAM:
                return handleScheduleParam(id, no);
            default:
                throw new ValidationException("objName", "Unsupported config object: " + objName);
        }
    }

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

    private Object handleSignalGroupParam(String crossId, Integer signalGroupNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for SignalGroupParam query");
        }
        if (signalGroupNo == null) {
            return serviceFactory.getSignalGroupService().findByCrossId(crossId);
        }
        return serviceFactory.getSignalGroupService().findByCrossIdAndSignalGroupNo(crossId, signalGroupNo);
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
            throw new ValidationException("dayPlanNo", "Day plan number is required");
        }
        return serviceFactory.getDayPlanService().getDayPlanParam(crossId, dayPlanNo);
    }

    private Object handleScheduleParam(String crossId, Integer scheduleNo) throws BusinessException {
        if (crossId == null) {
            throw new ValidationException("crossId", "Cross ID is required for ScheduleParam query");
        }
        if (scheduleNo == null) {
            throw new ValidationException("scheduleNo", "Schedule number is required");
        }
        return serviceFactory.getScheduleService().getScheduleParam(crossId, scheduleNo);
    }

    @Override
    public String getHandlerName() {
        return "ConfigParamHandler";
    }
}
