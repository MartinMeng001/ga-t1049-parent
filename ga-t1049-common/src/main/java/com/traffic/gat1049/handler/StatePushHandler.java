package com.traffic.gat1049.handler;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.model.entity.runtime.*;
import com.traffic.gat1049.model.entity.system.SysState;
import com.traffic.gat1049.model.entity.traffic.CrossTrafficData;
import com.traffic.gat1049.model.entity.traffic.LaneTrafficData;
import com.traffic.gat1049.model.entity.traffic.StageTrafficData;
import com.traffic.gat1049.protocol.handler.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 状态推送处理器
 * 处理系统主动推送的运行状态和数据
 */
public class StatePushHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public StatePushHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        // 支持推送类型的消息
        if (!ProtocolUtils.isPush(message) || !ProtocolUtils.isNotifyOperation(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return isSupportedDataType(data);
    }

    private boolean isSupportedDataType(Object data) {
        return data instanceof SysState ||
                data instanceof CrossState ||
                data instanceof SignalControllerError ||
                data instanceof CrossModePlan ||
                data instanceof CrossCycle ||
                data instanceof CrossStage ||
                data instanceof CrossSignalGroupStatus ||
                data instanceof CrossTrafficData ||
                data instanceof StageTrafficData ||
                data instanceof VarLaneStatus ||
                data instanceof RouteControlModeStatus ||
                data instanceof RouteSpeed;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        Object data = ProtocolUtils.getOperationData(message);

        try {
            // 处理推送的数据
            handlePushData(data);

            // 推送消息通常不需要响应
            return null;

        } catch (BusinessException e) {
            logger.error("Failed to handle push data: {}", e.getMessage());
            // 推送处理失败，记录日志但不返回错误响应
            return null;
        }
    }

    private void handlePushData(Object data) throws BusinessException {
        if (data instanceof SysState) {
            handleSystemState((SysState) data);
        } else if (data instanceof CrossState) {
            handleCrossState((CrossState) data);
        } else if (data instanceof SignalControllerError) {
            handleSignalControllerError((SignalControllerError) data);
        } else if (data instanceof CrossModePlan) {
            handleCrossModePlan((CrossModePlan) data);
        } else if (data instanceof CrossCycle) {
            handleCrossCycle((CrossCycle) data);
        } else if (data instanceof CrossStage) {
            handleCrossStage((CrossStage) data);
        } else if (data instanceof CrossSignalGroupStatus) {
            handleCrossSignalGroupStatus((CrossSignalGroupStatus) data);
        } else if (data instanceof CrossTrafficData) {
            handleCrossTrafficData((CrossTrafficData) data);
        } else if (data instanceof StageTrafficData) {
            handleStageTrafficData((StageTrafficData) data);
        } else if (data instanceof VarLaneStatus) {
            handleVarLaneStatus((VarLaneStatus) data);
        } else if (data instanceof RouteControlModeStatus) {
            handleRouteControlMode((RouteControlModeStatus) data);
        } else if (data instanceof RouteSpeed) {
            handleRouteSpeed((RouteSpeed) data);
        }
    }

    private void handleSystemState(SysState sysState) throws BusinessException {
        logger.info("Received system state push: state={}", sysState.getValue());

        // 更新系统状态
        serviceFactory.getSystemService().updateSystemState(sysState.getValue());

        // 触发系统状态变更事件
        // TODO: 实现事件通知机制
    }

    private void handleCrossState(CrossState crossState) throws BusinessException {
        logger.info("Received cross state push: crossId={}, state={}",
                crossState.getCrossId(), crossState.getValue());

        // 更新路口状态
        serviceFactory.getCrossService().updateCrossState(
                crossState.getCrossId(), crossState.getValue());

        // 如果路口离线或故障，可能需要触发告警
        if (!crossState.getValue().equals(com.traffic.gat1049.model.enums.SystemState.ONLINE)) {
            logger.warn("Cross {} is {}", crossState.getCrossId(), crossState.getValue());
            // TODO: 触发告警机制
        }
    }

    private void handleSignalControllerError(SignalControllerError error) throws BusinessException {
        logger.error("Received signal controller error: controllerId={}, errorType={}, desc={}",
                error.getSignalControllerId(), error.getErrorType(), error.getErrorDesc());

        // 记录信号机故障
        serviceFactory.getSignalControllerService().reportError(
                error.getSignalControllerId(),
                error.getErrorType(),
                error.getErrorDesc());

        // TODO: 触发故障告警
    }

    private void handleCrossModePlan(CrossModePlan modePlan) throws BusinessException {
        logger.info("Received cross mode plan push: crossId={}, mode={}, planNo={}",
                modePlan.getCrossId(), modePlan.getControlMode(), modePlan.getPlanNo());

        // 记录控制方式变更
        // TODO: 实现控制方式变更记录
    }

    private void handleCrossCycle(CrossCycle cycle) throws BusinessException {
        logger.debug("Received cross cycle push: crossId={}, startTime={}, cycleLen={}",
                cycle.getCrossId(), cycle.getStartTime(), cycle.getLastCycleLen());

        // 更新路口周期信息
        serviceFactory.getTrafficDataService().updateCrossCycle(cycle);
    }

    private void handleCrossStage(CrossStage stage) throws BusinessException {
        logger.debug("Received cross stage push: crossId={}, curStage={}, curLen={}",
                stage.getCrossId(), stage.getCurStageNo(), stage.getCurStageLen());

        // 更新路口阶段信息
        serviceFactory.getTrafficDataService().updateCrossStage(stage);
    }

    private void handleCrossSignalGroupStatus(CrossSignalGroupStatus status) throws BusinessException {
        logger.debug("Received signal group status push: crossId={}, groups={}",
                status.getCrossId(), status.getSignalGroupStatusList().size());

        // 更新信号组灯态
        serviceFactory.getSignalGroupService().updateCrossSignalGroupStatus(status);
    }

    private void handleCrossTrafficData(CrossTrafficData trafficData) throws BusinessException {
        logger.info("Received traffic data push: crossId={}, endTime={}, interval={}, lanes={}",
                trafficData.getCrossId(), trafficData.getEndTime(),
                trafficData.getInterval(), trafficData.getDataList().size());

        // 保存交通流数据
        serviceFactory.getTrafficDataService().saveCrossTrafficData(trafficData);

        // 分析交通流数据，检查是否需要触发优化
        analyzeTrafficData(trafficData);
    }

    private void handleStageTrafficData(StageTrafficData stageData) throws BusinessException {
        logger.info("Received stage traffic data push: crossId={}, stageNo={}, startTime={}, endTime={}",
                stageData.getCrossId(), stageData.getStageNo(),
                stageData.getStartTime(), stageData.getEndTime());

        // 保存阶段交通流数据
        serviceFactory.getTrafficDataService().saveStageTrafficData(stageData);
    }

    private void handleVarLaneStatus(VarLaneStatus varLaneStatus) throws BusinessException {
        logger.info("Received variable lane status push: crossId={}, laneNo={}, movement={}, mode={}",
                varLaneStatus.getCrossId(), varLaneStatus.getLaneNo(),
                varLaneStatus.getCurMovement(), varLaneStatus.getCurMode());

        // 更新可变车道状态
        serviceFactory.getLaneService().updateVarLaneStatus(varLaneStatus);
    }

    private void handleRouteControlMode(RouteControlModeStatus controlMode) throws BusinessException {
        logger.info("Received route control mode push: routeId={}, mode={}",
                controlMode.getRouteId(), controlMode.getValue());

        // 记录干线控制方式变更
        // TODO: 实现干线控制方式变更记录
    }

    private void handleRouteSpeed(RouteSpeed routeSpeed) throws BusinessException {
        logger.info("Received route speed push: routeId={}, sections={}",
                routeSpeed.getRouteId(), routeSpeed.getRoadSectionSpeedList().size());

        // 更新干线推荐车速
        serviceFactory.getRouteControlService().setRouteSpeed(routeSpeed);
    }

    private void analyzeTrafficData(CrossTrafficData trafficData) {
        // 分析交通流数据
        for (LaneTrafficData laneData : trafficData.getDataList()) {
            // 检查饱和度
            if (laneData.getSaturation() != null && laneData.getSaturation().doubleValue() > 0.85) {
                logger.warn("High saturation detected: crossId={}, laneNo={}, saturation={}%",
                        trafficData.getCrossId(), laneData.getLaneNo(), laneData.getSaturation());
                // TODO: 触发优化建议
            }

            // 检查排队长度
            if (laneData.getQueueLength() != null && laneData.getQueueLength().doubleValue() > 100) {
                logger.warn("Long queue detected: crossId={}, laneNo={}, queueLength={}m",
                        trafficData.getCrossId(), laneData.getLaneNo(), laneData.getQueueLength());
                // TODO: 触发排队溢出预警
            }
        }
    }

    @Override
    public String getHandlerName() {
        return "StatePushHandler";
    }
}