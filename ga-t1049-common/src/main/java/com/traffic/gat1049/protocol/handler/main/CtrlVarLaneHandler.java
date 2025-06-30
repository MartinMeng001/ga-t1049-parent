package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.CtrlVarLane;
import com.traffic.gat1049.protocol.model.intersection.LaneParam;
import com.traffic.gat1049.protocol.model.runtime.VarLaneStatus;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.VarLaneMode;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 可变导向车道控制处理器
 * 处理设置可变导向车道功能命令
 */
public class CtrlVarLaneHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CtrlVarLaneHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof CtrlVarLane;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        CtrlVarLane ctrlVarLane = (CtrlVarLane) ProtocolUtils.getOperationData(message);

        try {
            // 验证参数
            validateCtrlVarLane(ctrlVarLane);

            // 验证车道是否存在且为可变车道
            validateVariableLane(ctrlVarLane);

            // 验证时间参数（如果指定了控制模式需要时间）
            validateTimeParameters(ctrlVarLane);

            // 处理可变导向车道控制
            handleVariableLaneControl(ctrlVarLane);

            // 创建成功响应
            return createSuccessResponse(message, createSuccessResult(ctrlVarLane));

        } catch (ValidationException e) {
            logger.error("Variable lane control validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), "");
        } catch (BusinessException e) {
            logger.error("Variable lane control business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
        }
    }

    private void validateCtrlVarLane(CtrlVarLane ctrlVarLane) throws ValidationException {
        if (ctrlVarLane.getCrossId() == null || ctrlVarLane.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (ctrlVarLane.getLaneNo() == null) {
            throw new ValidationException("laneNo", "车道序号不能为空");
        }
        if (ctrlVarLane.getMovement() == null) {
            throw new ValidationException("movement", "车道转向不能为空");
        }
        if (ctrlVarLane.getCtrlMode() == null) {
            throw new ValidationException("ctrlMode", "控制方式不能为空");
        }

        // 验证路口编号格式
        if (!ProtocolUtils.isValidCrossId(ctrlVarLane.getCrossId())) {
            throw new ValidationException("crossId", "路口编号格式无效: " + ctrlVarLane.getCrossId());
        }

        // 验证车道序号范围
        if (ctrlVarLane.getLaneNo() < 1 || ctrlVarLane.getLaneNo() > 99) {
            throw new ValidationException("laneNo", "车道序号必须在1-99范围内");
        }
    }

    private void validateVariableLane(CtrlVarLane ctrlVarLane) throws BusinessException {
        String crossId = ctrlVarLane.getCrossId();
        Integer laneNo = ctrlVarLane.getLaneNo();

        // 获取车道参数
        LaneParam laneParam = serviceFactory.getLaneService().findByCrossIdAndLaneNo(crossId, laneNo);

        if (laneParam == null) {
            throw new DataNotFoundException("Lane",
                    String.format("车道不存在: crossId=%s, laneNo=%d", crossId, laneNo));
        }

        // 检查是否为可变车道
        if (laneParam.getVarMovementList() == null || laneParam.getVarMovementList().isEmpty()) {
            throw new BusinessException("NOT_VARIABLE_LANE",
                    String.format("车道%d在路口%s不是可变车道", laneNo, crossId));
        }

        // 验证新的转向是否在可变转向列表中
        LaneMovement newMovement = ctrlVarLane.getMovement();
        boolean isValidMovement = laneParam.getVarMovementList().contains(newMovement);

        if (!isValidMovement) {
            throw new ValidationException("movement",
                    String.format("转向%s不在车道%d的可变转向列表中，允许的转向: %s",
                            newMovement.getDescription(), laneNo,
                            laneParam.getVarMovementList().toString()));
        }
    }

    private void validateTimeParameters(CtrlVarLane ctrlVarLane) throws ValidationException {
        VarLaneMode mode = ctrlVarLane.getCtrlMode();

        // 恢复信号机控制模式时，时间参数无意义，可以为空
        if (mode == VarLaneMode.CONTROLLER) {
            return;
        }

        // 其他模式可能需要时间参数，这里根据具体需求验证
        String startTime = ctrlVarLane.getStartTime();
        String endTime = ctrlVarLane.getEndTime();

        if (startTime != null && !startTime.trim().isEmpty()) {
            try {
                LocalDateTime.parse(startTime, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new ValidationException("startTime", "开始时间格式错误，应为 yyyy-MM-dd HH:mm:ss");
            }
        }

        if (endTime != null && !endTime.trim().isEmpty()) {
            try {
                LocalDateTime endDateTime = LocalDateTime.parse(endTime, TIME_FORMATTER);
                if (startTime != null && !startTime.trim().isEmpty()) {
                    LocalDateTime startDateTime = LocalDateTime.parse(startTime, TIME_FORMATTER);
                    if (endDateTime.isBefore(startDateTime)) {
                        throw new ValidationException("endTime", "结束时间不能早于开始时间");
                    }
                }
            } catch (DateTimeParseException e) {
                throw new ValidationException("endTime", "结束时间格式错误，应为 yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    private void handleVariableLaneControl(CtrlVarLane ctrlVarLane) throws BusinessException {
        String crossId = ctrlVarLane.getCrossId();
        Integer laneNo = ctrlVarLane.getLaneNo();
        LaneMovement movement = ctrlVarLane.getMovement();
        VarLaneMode mode = ctrlVarLane.getCtrlMode();

        logger.info("设置可变导向车道: crossId={}, laneNo={}, movement={}, mode={}",
                crossId, laneNo, movement, mode);

        // 创建或更新可变车道状态
        VarLaneStatus varLaneStatus = new VarLaneStatus();
        varLaneStatus.setCrossId(crossId);
        varLaneStatus.setLaneNo(laneNo);
        varLaneStatus.setCurMovement(movement);
        varLaneStatus.setCurMode(mode);

        // 更新可变车道状态
        serviceFactory.getLaneService().updateVarLaneStatus(varLaneStatus);

        // 调用设备控制服务（如果需要）
//        if (serviceFactory.getDeviceControlService() != null) {
//            serviceFactory.getDeviceControlService().controlVariableLane(
//                    crossId, laneNo, movement, mode,
//                    ctrlVarLane.getStartTime(), ctrlVarLane.getEndTime());
//        }

        // 记录操作日志
        logger.info("可变导向车道设置成功: crossId={}, laneNo={}, movement={}, mode={}",
                crossId, laneNo, movement.getDescription(), mode.getDescription());
    }

    private Object createSuccessResult(CtrlVarLane ctrlVarLane) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                return String.format("可变导向车道设置成功: 路口%s 车道%d 转向%s 模式%s",
                        ctrlVarLane.getCrossId(),
                        ctrlVarLane.getLaneNo(),
                        ctrlVarLane.getMovement().getDescription(),
                        ctrlVarLane.getCtrlMode().getDescription());
            }
            public String getCrossId() { return ctrlVarLane.getCrossId(); }
            public Integer getLaneNo() { return ctrlVarLane.getLaneNo(); }
            public String getMovement() { return ctrlVarLane.getMovement().getCode(); }
            public String getCtrlMode() { return ctrlVarLane.getCtrlMode().getCode(); }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(LocalDateTime.now()); }
        };
    }

    @Override
    public String getHandlerName() {
        return "CtrlVarLaneHandler";
    }
}
