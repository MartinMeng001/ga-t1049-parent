package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.runtime.CrossModePlan;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 控制方式处理器
 * 处理路口控制方式的设置
 */
public class ControlModeHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public ControlModeHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof CrossModePlan;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        CrossModePlan modePlan = (CrossModePlan) ProtocolUtils.getOperationData(message);
        String objName = GatConstants.ObjectName.CROSS_MODE_PLAN;
        try {
            // 验证参数
            validateCrossModePlan(modePlan);

            // 处理控制方式设置
            handleControlModeChange(modePlan);

            // 创建成功响应
            return createSuccessResponse(message, createSuccessResult(modePlan));

        } catch (ValidationException e) {
            logger.error("Control mode validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), objName);
        } catch (BusinessException e) {
            logger.error("Control mode business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), objName);
        }
    }

    private void validateCrossModePlan(CrossModePlan modePlan) throws ValidationException {
        if (modePlan.getCrossId() == null || modePlan.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "Cross ID cannot be null or empty");
        }
        if (modePlan.getControlMode() == null) {
            throw new ValidationException("controlMode", "Control mode cannot be null");
        }

        // 验证路口编号格式
        if (!ProtocolUtils.isValidCrossId(modePlan.getCrossId())) {
            throw new ValidationException("crossId",
                    "Invalid cross ID format: " + modePlan.getCrossId());
        }

        // 验证方案号的有效性
        ControlMode mode = modePlan.getControlMode();
        Integer planNo = modePlan.getPlanNo();

        // 特殊控制方式不需要方案号
        if (isSpecialControlMode(mode)) {
            if (planNo != null && planNo != 0) {
                logger.warn("Plan number {} ignored for special control mode {}", planNo, mode);
            }
        } else {
            // 非特殊控制方式需要有效的方案号
            if (planNo == null || planNo <= 0) {
                throw new ValidationException("planNo",
                        "Valid plan number required for control mode: " + mode.getDescription());
            }
        }
    }

    private boolean isSpecialControlMode(ControlMode mode) {
        return mode == ControlMode.CANCEL ||
                mode == ControlMode.LIGHT_OFF ||
                mode == ControlMode.ALL_RED ||
                mode == ControlMode.ALL_YELLOW_FLASH;
    }

    private void handleControlModeChange(CrossModePlan modePlan) throws BusinessException {
        String crossId = modePlan.getCrossId();
        ControlMode mode = modePlan.getControlMode();
        Integer planNo = modePlan.getPlanNo();

        logger.info("Setting control mode: crossId={}, mode={}, planNo={}",
                crossId, mode, planNo);

        // 检查路口是否存在
        if (!serviceFactory.getCrossService().existsById(crossId)) {
            throw new ValidationException("crossId", "Cross not found: " + crossId);
        }

        // 如果需要方案号，验证方案是否存在
        if (!isSpecialControlMode(mode) && planNo != null && planNo > 0) {
            PlanParam plan = serviceFactory.getPlanService().findByCrossIdAndPlanNo(crossId, planNo);
            if (plan == null) {
                throw new ValidationException("planNo",
                        String.format("Plan %d not found for cross %s", planNo, crossId));
            }
        }

        // 执行控制方式设置
        serviceFactory.getControlModeService().setControlMode(crossId, mode, planNo);

        // 根据不同的控制方式执行相应的操作
        handleSpecificControlMode(crossId, mode);
    }

    private void handleSpecificControlMode(String crossId, ControlMode mode) throws BusinessException {
        switch (mode) {
            case CANCEL:
                logger.info("Cancelling manual control for cross {}", crossId);
                // 恢复自主控制
                break;

            case LIGHT_OFF:
                logger.warn("Setting lights off for cross {}", crossId);
                // 关灯控制
                serviceFactory.getControlService().emergencyControl(crossId, mode);
                break;

            case ALL_RED:
                logger.warn("Setting all red for cross {}", crossId);
                // 全红控制
                serviceFactory.getControlService().emergencyControl(crossId, mode);
                break;

            case ALL_YELLOW_FLASH:
                logger.warn("Setting all yellow flash for cross {}", crossId);
                // 黄闪控制
                serviceFactory.getControlService().emergencyControl(crossId, mode);
                break;

            case MANUAL:
                logger.info("Setting manual control for cross {}", crossId);
                // 手动控制
                break;

            case STAGE_LOCK:
                logger.info("Setting stage lock control for cross {}", crossId);
                // 锁定阶段控制
                break;

            case PLAN_LOCK:
                logger.info("Setting plan lock control for cross {}", crossId);
                // 指定方案控制
                break;

            default:
                logger.info("Setting {} control for cross {}", mode.getDescription(), crossId);
                break;
        }
    }

    private Object createSuccessResult(CrossModePlan modePlan) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                if (isSpecialControlMode(modePlan.getControlMode())) {
                    return String.format("Control mode set successfully: %s",
                            modePlan.getControlMode().getDescription());
                } else {
                    return String.format("Control mode set successfully: %s with plan %d",
                            modePlan.getControlMode().getDescription(),
                            modePlan.getPlanNo());
                }
            }
            public String getCrossId() { return modePlan.getCrossId(); }
            public String getControlMode() { return modePlan.getControlMode().getCode(); }
            public String getControlModeDesc() { return modePlan.getControlMode().getDescription(); }
            public Integer getPlanNo() { return modePlan.getPlanNo(); }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
        };
    }

    @Override
    public String getHandlerName() {
        return "ControlModeHandler";
    }
}