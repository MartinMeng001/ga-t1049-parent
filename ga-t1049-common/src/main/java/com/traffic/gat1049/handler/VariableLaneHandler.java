package com.traffic.gat1049.handler;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.model.entity.intersection.LaneParam;
import com.traffic.gat1049.model.entity.runtime.VarLaneStatus;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.VarLaneMode;
import com.traffic.gat1049.protocol.handler.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 可变车道控制处理器
 * 处理可变车道状态的设置和控制
 */
public class VariableLaneHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public VariableLaneHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof VarLaneStatus;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        VarLaneStatus varLaneStatus = (VarLaneStatus) ProtocolUtils.getOperationData(message);

        try {
            // 验证参数
            validateVarLaneStatus(varLaneStatus);

            // 检查车道是否存在且为可变车道
            validateVariableLane(varLaneStatus);

            // 处理可变车道控制
            handleVariableLaneControl(varLaneStatus);

            // 创建成功响应
            return createSuccessResponse(message, createSuccessResult(varLaneStatus));

        } catch (ValidationException e) {
            logger.error("Variable lane validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (DataNotFoundException e) {
            logger.error("Lane not found: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OBJECT_NOT_FOUND, e.getMessage());
        } catch (BusinessException e) {
            logger.error("Variable lane control error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage());
        }
    }

    private void validateVarLaneStatus(VarLaneStatus varLaneStatus) throws ValidationException {
        if (varLaneStatus.getCrossId() == null || varLaneStatus.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "Cross ID cannot be null or empty");
        }
        if (varLaneStatus.getLaneNo() == null) {
            throw new ValidationException("laneNo", "Lane number cannot be null");
        }
        if (varLaneStatus.getCurMovement() == null) {
            throw new ValidationException("curMovement", "Current movement cannot be null");
        }
        if (varLaneStatus.getCurMode() == null) {
            throw new ValidationException("curMode", "Current mode cannot be null");
        }

        // 验证路口编号格式
        if (!ProtocolUtils.isValidCrossId(varLaneStatus.getCrossId())) {
            throw new ValidationException("crossId",
                    "Invalid cross ID format: " + varLaneStatus.getCrossId());
        }

        // 验证车道序号范围
        if (varLaneStatus.getLaneNo() < 1 || varLaneStatus.getLaneNo() > 99) {
            throw new ValidationException("laneNo",
                    "Lane number must be between 1 and 99: " + varLaneStatus.getLaneNo());
        }
    }

    private void validateVariableLane(VarLaneStatus varLaneStatus) throws BusinessException {
        String crossId = varLaneStatus.getCrossId();
        Integer laneNo = varLaneStatus.getLaneNo();

        // 获取车道参数
        LaneParam laneParam = serviceFactory.getLaneService().findByCrossIdAndLaneNo(crossId, laneNo);

        if (laneParam == null) {
            throw new DataNotFoundException("Lane",
                    String.format("Lane not found: crossId=%s, laneNo=%d", crossId, laneNo));
        }

        // 检查是否为可变车道
        if (laneParam.getVarMovementList() == null || laneParam.getVarMovementList().isEmpty()) {
            throw new BusinessException("NOT_VARIABLE_LANE",
                    String.format("Lane %d at cross %s is not a variable lane", laneNo, crossId));
        }

        // 验证新的转向是否在可变转向列表中
        LaneMovement newMovement = varLaneStatus.getCurMovement();
        boolean isValidMovement = laneParam.getVarMovementList().contains(newMovement);

        if (!isValidMovement) {
            throw new ValidationException("curMovement",
                    String.format("Movement %s is not allowed for variable lane %d at cross %s",
                            newMovement.getDescription(), laneNo, crossId));
        }
    }

    private void handleVariableLaneControl(VarLaneStatus varLaneStatus) throws BusinessException {
        String crossId = varLaneStatus.getCrossId();
        Integer laneNo = varLaneStatus.getLaneNo();
        LaneMovement movement = varLaneStatus.getCurMovement();
        VarLaneMode mode = varLaneStatus.getCurMode();

        logger.info("Setting variable lane: crossId={}, laneNo={}, movement={}, mode={}",
                crossId, laneNo, movement, mode);

        // 更新可变车道状态
        serviceFactory.getLaneService().updateVarLaneStatus(varLaneStatus);

        // 如果是自适应模式，可能需要触发相关的自适应控制逻辑
        if (VarLaneMode.ADAPTIVE.equals(mode)) {
            logger.info("Variable lane {} at cross {} switched to adaptive mode", laneNo, crossId);
            // TODO: 触发自适应控制逻辑
        }

        // 记录状态变更
        logStateChange(crossId, laneNo, movement, mode);
    }

    private void logStateChange(String crossId, Integer laneNo, LaneMovement movement, VarLaneMode mode) {
        logger.info("Variable lane state changed - Cross: {}, Lane: {}, New Movement: {}, Mode: {}",
                crossId, laneNo, movement.getDescription(), mode.getDescription());
    }

    private Object createSuccessResult(VarLaneStatus varLaneStatus) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                return String.format("Variable lane set successfully: Lane %d at cross %s changed to %s (%s mode)",
                        varLaneStatus.getLaneNo(),
                        varLaneStatus.getCrossId(),
                        varLaneStatus.getCurMovement().getDescription(),
                        varLaneStatus.getCurMode().getDescription());
            }
            public String getCrossId() { return varLaneStatus.getCrossId(); }
            public Integer getLaneNo() { return varLaneStatus.getLaneNo(); }
            public String getMovement() { return varLaneStatus.getCurMovement().getCode(); }
            public String getMovementDesc() { return varLaneStatus.getCurMovement().getDescription(); }
            public String getMode() { return varLaneStatus.getCurMode().getCode(); }
            public String getModeDesc() { return varLaneStatus.getCurMode().getDescription(); }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
        };
    }

    @Override
    public String getHandlerName() {
        return "VariableLaneHandler";
    }
}
