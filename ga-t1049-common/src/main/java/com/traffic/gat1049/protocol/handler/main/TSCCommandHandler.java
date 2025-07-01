package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.handler.base.TokenRequiredHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.TSCCmd;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.util.ResultHandlingUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;
import com.traffic.gat1049.application.session.SessionManager;

/**
 * TSCCmd命令处理器基类
 * 处理配置参数和运行信息的查询、通知命令
 */
public class TSCCommandHandler extends TokenRequiredHandler {

    protected final ServiceFactory serviceFactory;

    public TSCCommandHandler(ServiceFactory serviceFactory, SessionManager sessionManager) {
        super(sessionManager);
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
    protected Message doHandleWithSession(Message message, SessionManager.SessionInfo sessionInfo)
            throws GatProtocolException {

        logger.info("处理TSC查询请求: user={}, seq={}",
                sessionInfo.getUserName(), message.getSeq());

        try {
            // 1. 验证TSC命令（Handler内验证，简化版本）
            TSCCmd tscCmd = validateTSCCmd(message);

            // 2. 分发查询（按原设计思路）
            Object result = dispatchQuery(tscCmd, sessionInfo);

            // 3. 返回响应
            //return createSuccessResponse(message, result);
            return ResultHandlingUtils.createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("TSCCmd validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(),"");
        } catch (DataNotFoundException e) {
            logger.error("Data not found: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OBJECT_NOT_FOUND, e.getMessage(),"");
        } catch (BusinessException e) {
            logger.error("Business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(),"");
        }
    }

    /**
     * 验证TSCCmd参数
     */
    private TSCCmd validateTSCCmd(Message message) throws ValidationException {
        // 1. 获取原始数据
        Object operationData = ProtocolUtils.getOperationData(message);
        if (operationData == null) {
            throw new ValidationException("objName", "Object name cannot be null or empty");
        }

        // 2. 类型转换（这是必需的，因为ProtocolUtils返回Object）
        TSCCmd cmd;
        if (operationData instanceof TSCCmd) {
            cmd = (TSCCmd) operationData;
            // 3. 业务验证（只验证这个Handler关心的字段）
            try {
                messageValidator.validateObjectName(cmd.getObjName());
                messageValidator.validateId(cmd.getObjName(), cmd.getId());
            }catch (Exception e){
                e.printStackTrace();
                //throw new GatProtocolException("INVALID_PARAMETER", "查询参数错误");
            }
            logger.debug("TSC命令验证通过: objName={}, id={}, no={}",
                    cmd.getObjName(), cmd.getId(), cmd.getNo());
            return cmd;
        }

        throw new ValidationException("INVALID_PARAMETER", "查询参数无法识别");
    }

    /**
     * 根据对象名称分发查询请求
     */
    private Object dispatchQuery(TSCCmd tscCmd, SessionManager.SessionInfo sessionInfo) throws BusinessException {
        String objName = tscCmd.getObjName();
        String id = tscCmd.getId();
        Integer no = tscCmd.getNo();

        logger.debug("分发TSC查询: objName={}, id={}, no={}, user={}",
                objName, id, no, sessionInfo.getUserName());

        Object result = null;
        switch (objName) {
            case GatConstants.ObjectName.SYS_INFO:
                result = serviceFactory.getSystemService().getSystemInfo();
                break;

            case GatConstants.ObjectName.SYS_STATE:
                result = serviceFactory.getSystemService().getSystemState();
                break;

            case GatConstants.ObjectName.REGION_PARAM:
                result = handleRegionParam(id);
                break;
            case GatConstants.ObjectName.SUB_REGION_PARAM:
                result = handleSubRegionParam(id);
                break;
            case GatConstants.ObjectName.ROUTE_PARAM:
                result = handleRouteParam(id);
                break;
            case GatConstants.ObjectName.SIGNAL_CONTROLLER:
                result = handleSignalController(id);
                break;
            case GatConstants.ObjectName.LAMP_GROUP:
                result = handleLampGroup(id, no);
                break;
            case GatConstants.ObjectName.DETECTOR_PARAM:
                result = handleDetectorParam(id, no);
                break;
            case GatConstants.ObjectName.LANE_PARAM:
                result = handleLaneParam(id, no);
                break;
            case GatConstants.ObjectName.PEDESTRIAN_PARAM:
                result = handlePedestrianParam(id, no);
                break;
            case GatConstants.ObjectName.SIGNAL_GROUP_PARAM:
                result = handleSignalGroupParam(id, no);
                break;
            case GatConstants.ObjectName.STAGE_PARAM:
                result = handleStageParam(id, no);
                break;
            case GatConstants.ObjectName.DAY_PLAN_PARAM:
                result = handleDayPlanParam(id, no);
                break;
            case GatConstants.ObjectName.SCHEDULE_PARAM:
                result = handleScheduleParam(id, no);
                break;
            case GatConstants.ObjectName.CROSS_MODE_PLAN:
                result = handleCrossModePlan(id);
                break;
            case GatConstants.ObjectName.CROSS_CYCLE:
                result = handleCrossCycle(id);
                break;
            case GatConstants.ObjectName.CROSS_STAGE:
                result = handleCrossStage(id);
                break;
            case GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS:
                result = handleCrossSignalGroupStatus(id);
                break;
            case GatConstants.ObjectName.CROSS_TRAFFIC_DATA:
                result = handleCrossTrafficData(id);
                break;
            case GatConstants.ObjectName.STAGE_TRAFFIC_DATA:
                result = handleStageTrafficData(id, no);
                break;
            case GatConstants.ObjectName.VAR_LANE_STATUS:
                result = handleVarLaneStatus(id, no);
                break;
            case GatConstants.ObjectName.ROUTE_CONTROL_MODE:
                result = handleRouteControlMode(id);
                break;
            case GatConstants.ObjectName.ROUTE_SPEED:
                result = handleRouteSpeed(id);
                break;


            case GatConstants.ObjectName.CROSS_PARAM:
                result = handleCrossParam(id);
                break;

            case GatConstants.ObjectName.PLAN_PARAM:
                result = handlePlanParam(id, no);
                break;

            case GatConstants.ObjectName.CROSS_STATE:
                result = handleCrossState(id);
                break;
            case GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR:
                result = handleSignalControllerError(id);
                break;
            default:
                throw new ValidationException("objName", "Unsupported object name: " + objName);
        }
        logger.debug("TSC查询完成: objName={}, resultType={}, user={}",
                objName,
                result != null ? result.getClass().getSimpleName() : "null",
                sessionInfo.getUserName());

        return result;
    }

    // ==================== 配置参数处理方法 ====================

    private Object handleRegionParam(String regionId) throws BusinessException {
        if (regionId == null || "".equals(regionId)) {
            return serviceFactory.getRegionService().findAll();
        }
        return serviceFactory.getRegionService().findById(regionId);
    }

    private Object handleSubRegionParam(String subRegionId) throws BusinessException {
        if (subRegionId == null || "".equals(subRegionId)) {
            return serviceFactory.getSubRegionService().findAll();
        }
        return serviceFactory.getSubRegionService().findById(subRegionId);
    }

    private Object handleRouteParam(String routeId) throws BusinessException {
        if (routeId == null || "".equals(routeId)) {
            return serviceFactory.getRouteService().findAll();
        }
        return serviceFactory.getRouteService().findById(routeId);
    }

    private Object handleCrossParam(String crossId) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getCrossService().findAll();
        }
        return serviceFactory.getCrossService().findById(crossId);
    }

    private Object handleSignalController(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || "".equals(signalControllerId)) {
            return serviceFactory.getSignalControllerService().findAll();
        }
        return serviceFactory.getSignalControllerService().findBySignalControllerId(signalControllerId);
    }

    private Object handleLampGroup(String crossId, Integer lampGroupNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getLampGroupService().findAll();
        }
        if(lampGroupNo == null || lampGroupNo==0){
            return serviceFactory.getLampGroupService().findByCrossId(crossId);
        }
        return serviceFactory.getLampGroupService().findByCrossIdAndLampGroupNo(crossId, lampGroupNo);
    }

    private Object handleDetectorParam(String crossId, Integer detectorNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getDetectorService().findAll();
        }
        if (detectorNo == null || detectorNo == 0) {
            return serviceFactory.getDetectorService().findByCrossId(crossId);
        }
        return serviceFactory.getDetectorService().findByCrossIdAndDetectorNo(crossId, detectorNo);
    }

    private Object handleLaneParam(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getLaneService().findAll();
        }
        if (laneNo == null || laneNo==0) {
            return serviceFactory.getLaneService().findByCrossId(crossId);
        }
        return serviceFactory.getLaneService().findByCrossIdAndLaneNo(crossId, laneNo);
    }

    private Object handlePedestrianParam(String crossId, Integer pedestrianNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getPedestrianService().findAll();
        }
        if(pedestrianNo == null || pedestrianNo==0){
            return serviceFactory.getPedestrianService().findByCrossId(crossId);
        }
        return serviceFactory.getPedestrianService().findByCrossIdAndPedestrianNo(crossId, pedestrianNo);
    }

    private Object handleSignalGroupParam(String crossId, Integer signalGroupNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getSignalGroupService().findAll();
        }
        if (signalGroupNo == null || signalGroupNo == 0) {
            return serviceFactory.getSignalGroupService().findByCrossId(crossId);
        }
        return serviceFactory.getSignalGroupService().findByCrossIdAndSignalGroupNo(crossId, signalGroupNo);
    }

    private Object handleStageParam(String crossId, Integer stageNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getStageService().findAll();
        }
        if(stageNo == null || stageNo==0){
            return serviceFactory.getStageService().findByCrossId(crossId);
        }
        return serviceFactory.getStageService().findByCrossIdAndStageNo(crossId, stageNo);
    }

    private Object handlePlanParam(String crossId, Integer planNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getPlanService().findAllPlans();
        }
        if (planNo == null || planNo<=0) {
            return serviceFactory.getPlanService().findByCrossId(crossId);
        }
        return serviceFactory.getPlanService().findByCrossIdAndPlanNo(crossId, planNo);
    }

    private Object handleDayPlanParam(String crossId, Integer dayPlanNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getDayPlanService().findAllDayPlans();
        }
        if (dayPlanNo == null || dayPlanNo<=0) {
            return serviceFactory.getDayPlanService().findByCrossId(crossId);
        }
        return serviceFactory.getDayPlanService().findByCrossIdAndDayPlanNo(crossId, dayPlanNo);
    }

    private Object handleScheduleParam(String crossId, Integer scheduleNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getScheduleService().findAllSchedules();
        }
        if (scheduleNo == null || scheduleNo<=0) {
            return serviceFactory.getScheduleService().findByCrossId(crossId);
        }
        return serviceFactory.getScheduleService().findByCrossIdAndScheduleNo(crossId, scheduleNo);
    }

    // ==================== 运行信息处理方法 ====================

    private Object handleCrossState(String crossId) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {  // 返回所有CrossState
            //throw new ValidationException("crossId", "Cross ID is required for CrossState query");
            return serviceFactory.getCrossService().getAllCrossState();
        }
        return serviceFactory.getCrossService().getCrossState(crossId);
    }
    private Object handleSignalControllerError(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || "".equals(signalControllerId)) {  // 返回所有信号机故障
            return serviceFactory.getSignalControllerService().getAllErrors();
        }
        return serviceFactory.getSignalControllerService().getErrors(signalControllerId);
    }


    private Object handleCrossModePlan(String crossId) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getControlModeService().getAllControlModes();
        }
        return serviceFactory.getControlModeService().getCurrentModePlan(crossId);
    }

    private Object handleCrossCycle(String crossId) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getTrafficDataService().getAllCrossCycle();
        }
        return serviceFactory.getTrafficDataService().getCrossCycle(crossId);
    }

    private Object handleCrossStage(String crossId) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getTrafficDataService().getAllCrossStage();
        }
        return serviceFactory.getTrafficDataService().getCrossStage(crossId);
    }

    private Object handleCrossSignalGroupStatus(String crossId) throws BusinessException {
        if (crossId == null  || "".equals(crossId)) {
            return serviceFactory.getSignalGroupService().getAllCrossSignalGroupStatus();
        }
        return serviceFactory.getSignalGroupService().getCrossSignalGroupStatus(crossId);
    }

    private Object handleCrossTrafficData(String crossId) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getTrafficDataService().findAll();
        }
        return serviceFactory.getTrafficDataService().findById(crossId);
        // 获取最新的交通流数据，使用当前时间和默认间隔
//        return serviceFactory.getTrafficDataService().getCrossTrafficData(
//                crossId, java.time.LocalDateTime.now(), 300);
    }

    private Object handleStageTrafficData(String crossId, Integer stageNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getTrafficDataService().findAll();
        }
        if(stageNo==null || stageNo<=0){
            return serviceFactory.getTrafficDataService().getStageTrafficDataByCrossId(crossId);
        }

        // 获取当前阶段的交通流数据
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return serviceFactory.getTrafficDataService().getStageTrafficData(
                crossId, now.minusMinutes(5), now);
    }

    private Object handleVarLaneStatus(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null || "".equals(crossId)) {
            return serviceFactory.getLaneService().getVarLanes();
        }
        if(laneNo==null || laneNo==0){
            return serviceFactory.getLaneService().getVarLaneStatus(crossId);
        }
        return serviceFactory.getLaneService().getVarLaneStatus(crossId, laneNo);
    }

    private Object handleRouteControlMode(String routeId) throws BusinessException {
        if (routeId == null||"".equals(routeId)) {
            return serviceFactory.getRouteControlService().getAllRouteControlMode();
        }
        return serviceFactory.getRouteControlService().getRouteControlMode(routeId);
    }

    private Object handleRouteSpeed(String routeId) throws BusinessException {
        if (routeId == null || "".equals(routeId)) {
            return serviceFactory.getRouteControlService().getAllRouteSpeed();
        }
        return serviceFactory.getRouteControlService().getRouteSpeed(routeId);
    }

    @Override
    public String getHandlerName() {
        return "TSCCommandHandler";
    }
}
