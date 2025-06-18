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
 * 运行信息查询处理器
 */
public class RuntimeInfoHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public RuntimeInfoHandler(ServiceFactory serviceFactory) {
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

        // 支持的运行信息对象
        return GatConstants.ObjectName.CROSS_STATE.equals(objName) ||
                GatConstants.ObjectName.CROSS_MODE_PLAN.equals(objName) ||
                GatConstants.ObjectName.CROSS_CYCLE.equals(objName) ||
                GatConstants.ObjectName.CROSS_STAGE.equals(objName) ||
                GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS.equals(objName) ||
                GatConstants.ObjectName.CROSS_TRAFFIC_DATA.equals(objName) ||
                GatConstants.ObjectName.STAGE_TRAFFIC_DATA.equals(objName) ||
                GatConstants.ObjectName.VAR_LANE_STATUS.equals(objName) ||
                GatConstants.ObjectName.ROUTE_CONTROL_MODE.equals(objName) ||
                GatConstants.ObjectName.ROUTE_SPEED.equals(objName);
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        TSCCmd tscCmd = (TSCCmd) ProtocolUtils.getOperationData(message);

        try {
            validateTSCCmd(tscCmd);
            Object result = dispatchRuntimeQuery(tscCmd);
            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("Runtime info validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), "");
        } catch (BusinessException e) {
            logger.error("Runtime info business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
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

    private Object dispatchRuntimeQuery(TSCCmd tscCmd) throws BusinessException {
        String objName = tscCmd.getObjName();
        String id = tscCmd.getId();
        Integer no = tscCmd.getNo();

        switch (objName) {
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
                throw new ValidationException("objName", "Unsupported runtime object: " + objName);
        }
    }

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
        return serviceFactory.getControlModeService().getCurrentModePlan(crossId);
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
        return "RuntimeInfoHandler";
    }
}
