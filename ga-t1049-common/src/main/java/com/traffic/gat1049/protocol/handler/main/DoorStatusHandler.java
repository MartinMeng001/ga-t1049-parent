package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.model.command.TSCCmd;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.runtime.SCDoorStatus;
import com.traffic.gat1049.protocol.model.sdo.SdoError;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.service.interfaces.DoorStatusService;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

import java.util.List;

/**
 * 信号机柜门状态处理器
 * 处理SCDoorStatus相关的查询和推送操作
 */
public class DoorStatusHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;
    private final DoorStatusService doorStatusService;

    public DoorStatusHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.doorStatusService = serviceFactory.getDoorStatusService();
    }

    @Override
    public boolean supports(Message message) {
        if (!isQueryRequest(message) && !isPushData(message)) {
            return false;
        }

        if (isQueryRequest(message)) {
            Object data = ProtocolUtils.getOperationData(message);
            if (!(data instanceof TSCCmd)) {
                return false;
            }

            TSCCmd tscCmd = (TSCCmd) data;
            return GatConstants.ObjectName.SC_DOOR_STATUS.equals(tscCmd.getObjName());
        }

        if (isPushData(message)) {
            Object data = ProtocolUtils.getOperationData(message);
            return data instanceof SCDoorStatus;
        }

        return false;
    }

    @Override
    public String getHandlerName() {
        return "";
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        try {
            if (isQueryRequest(message)) {
                return handleQuery(message);
            } else if (isPushData(message)) {
                return handlePush(message);
            }
            else {
                return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, GatConstants.ErrorMessage.INVALID_PARAMETER_MSG, "");
            }
        }catch(BusinessException e){
            logger.error("Door status business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
        }
    }

    /**
     * 处理查询请求
     */
    private Message handleQuery(Message message) throws GatProtocolException {
        TSCCmd tscCmd = (TSCCmd) ProtocolUtils.getOperationData(message);

        try {
            validateTSCCmd(tscCmd);
            Object result = dispatchDoorStatusQuery(tscCmd);
            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("Door status query validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), "");
        } catch (BusinessException e) {
            logger.error("Door status query business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
        }
    }

    /**
     * 处理推送数据
     */
    private Message handlePush(Message message) throws GatProtocolException {
        SCDoorStatus doorStatus = (SCDoorStatus) ProtocolUtils.getOperationData(message);

        try {
            // 处理推送的门状态数据
            handleDoorStatusPush(doorStatus);
            return createSuccessResponse(message, null);

        } catch (BusinessException e) {
            logger.error("Door status push handling failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
        }
    }

    /**
     * 分发门状态查询
     */
    private Object dispatchDoorStatusQuery(TSCCmd tscCmd) throws BusinessException {
        //String crossId = tscCmd.getCrossId();
        String signalControllerId = tscCmd.getId();

        if (signalControllerId != null && !signalControllerId.trim().isEmpty()) {
            // 查询指定信号机的门状态
            return doorStatusService.getDoorStatus(signalControllerId);
        } else {
            // 查询所有门状态
            return doorStatusService.getAllDoorStatus();
        }
    }

    /**
     * 根据路口编号获取门状态
     */
    private List<SCDoorStatus> getDoorStatusByCrossId(String crossId) throws BusinessException {
        // 获取所有信号机，然后筛选出控制指定路口的信号机
        List<String> controllerIds = getSignalControllersByCrossId(crossId);

        return controllerIds.stream()
                .map(controllerId -> {
                    try {
                        return doorStatusService.getDoorStatus(controllerId);
                    } catch (BusinessException e) {
                        logger.warn("获取信号机{}门状态失败: {}", controllerId, e.getMessage());
                        return null;
                    }
                })
                .filter(doorStatus -> doorStatus != null)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据路口编号获取相关的信号机编号列表
     */
    private List<String> getSignalControllersByCrossId(String crossId) throws BusinessException {
        // 获取所有信号机
        List<com.traffic.gat1049.protocol.model.intersection.SignalController> allControllers =
                serviceFactory.getSignalControllerService().findAll();

        // 筛选出控制指定路口的信号机
        return allControllers.stream()
                .filter(controller -> controller.getCrossIDList() != null &&
                        controller.getCrossIDList().contains(crossId))
                .map(controller -> controller.getSignalControllerID())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 处理门状态推送数据
     */
    private void handleDoorStatusPush(SCDoorStatus doorStatus) throws BusinessException {
        logger.info("Received door status push: signalControllerId={}, doorCount={}",
                doorStatus.getSignalControllerId(),
                doorStatus.getDoorStatusList().getDoorStatus().size());

        // 更新门状态
        doorStatusService.updateDoorStatus(doorStatus);

        // 检查是否有门处于打开状态，可能需要触发告警
        boolean hasOpenDoor = doorStatus.getDoorStatusList().getDoorStatus().stream()
                .anyMatch(door -> door.getStatus() == com.traffic.gat1049.model.enums.DoorStatus.OPEN);

        if (hasOpenDoor) {
            logger.warn("Signal controller {} has open cabinet doors", doorStatus.getSignalControllerId());
            // TODO: 触发安全告警机制
        }

        // 检查是否有未知状态的门
        boolean hasUnknownDoor = doorStatus.getDoorStatusList().getDoorStatus().stream()
                .anyMatch(door -> door.getStatus() == com.traffic.gat1049.model.enums.DoorStatus.UNKNOWN);

        if (hasUnknownDoor) {
            logger.warn("Signal controller {} has doors with unknown status", doorStatus.getSignalControllerId());
            // TODO: 触发状态异常告警
        }
    }

    /**
     * 验证TSCCmd参数
     */
    private void validateTSCCmd(TSCCmd tscCmd) throws ValidationException {
        if (tscCmd == null) {
            throw new ValidationException("tscCmd", "TSCCmd不能为空");
        }

        if (!GatConstants.ObjectName.SC_DOOR_STATUS.equals(tscCmd.getObjName())) {
            throw new ValidationException("objName", "对象名称必须为SCDoorStatus");
        }
    }

    /**
     * 判断是否为推送数据
     */
    private boolean isPushData(Message message) {
        return ProtocolUtils.isPush(message) &&
                GatConstants.Operation.NOTIFY.equals(ProtocolUtils.getOperationName(message));
    }

    /**
     * 判断是否为查询请求
     */
    protected boolean isQueryRequest(Message message) {
        return ProtocolUtils.isRequest(message) &&
                GatConstants.Operation.GET.equals(ProtocolUtils.getOperationName(message));
    }

    /**
     * 创建成功响应
     */
    protected Message createSuccessResponse(Message request, Object data) {
        return MessageBuilder.responseFor(request)
                .success(data)
                .build();
    }

    /**
     * 创建错误响应
     */
    protected Message createErrorResponse(Message request, String errorCode, String errorMessage, String detail) {
        SdoError sdoError = new SdoError();
        sdoError.setErrObj(detail != null ? detail : GatConstants.ObjectName.SC_DOOR_STATUS);
        sdoError.setErrDesc(errorMessage);
        sdoError.setErrType(errorCode);

        return MessageBuilder.createErrorResponse(request, sdoError);
    }
}