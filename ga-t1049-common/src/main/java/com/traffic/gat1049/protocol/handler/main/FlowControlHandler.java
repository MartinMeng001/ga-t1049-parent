package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.handler.base.TokenRequiredHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.LockFlowDirection;
import com.traffic.gat1049.protocol.model.command.UnlockFlowDirection;
import com.traffic.gat1049.protocol.model.command.AdjustStage;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;
import com.traffic.gat1049.application.session.SessionManager;

/**
 * 交通流向控制命令处理器
 */
public class FlowControlHandler extends TokenRequiredHandler {

    private final ServiceFactory serviceFactory;

    public FlowControlHandler(ServiceFactory serviceFactory, SessionManager sessionManager) {
        super(sessionManager);
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof LockFlowDirection ||
                data instanceof UnlockFlowDirection ||
                data instanceof AdjustStage;
    }

//    @Override
//    protected Message doHandle(Message message) throws GatProtocolException {
//        Object data = ProtocolUtils.getOperationData(message);
//
//        try {
//            Object result = null;
//
//            if (data instanceof LockFlowDirection) {
//                result = handleLockFlowDirection((LockFlowDirection) data);
//            } else if (data instanceof UnlockFlowDirection) {
//                result = handleUnlockFlowDirection((UnlockFlowDirection) data);
//            } else if (data instanceof StageCtrl) {
//                result = handleStageCtrl((StageCtrl) data);
//            }
//
//            return createSuccessResponse(message, result);
//
//        } catch (ValidationException e) {
//            logger.error("Flow control validation failed: {}", e.getMessage());
//            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
//        } catch (BusinessException e) {
//            logger.error("Flow control business error: {}", e.getMessage());
//            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage());
//        }
//    }

    protected Message doHandleWithSession(Message message, SessionManager.SessionInfo sessionInfo)
            throws GatProtocolException {
        Object data = ProtocolUtils.getOperationData(message);
        try {
            Object result = null;

            if (data instanceof LockFlowDirection) {
                result = handleLockFlowDirection((LockFlowDirection) data);
            } else if (data instanceof UnlockFlowDirection) {
                result = handleUnlockFlowDirection((UnlockFlowDirection) data);
            } else if (data instanceof AdjustStage) {
                result = handleStageCtrl((AdjustStage) data);
            }
            // 3. 返回响应
            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("Flow control validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), "");
        } catch (BusinessException e) {
            logger.error("Flow control business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
        }
    }

    private Object handleLockFlowDirection(LockFlowDirection lockFlowDirection) throws BusinessException {
        // 验证必要参数
        if (lockFlowDirection.getCrossId() == null || lockFlowDirection.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "Cross ID cannot be null or empty");
        }
        if (lockFlowDirection.getType() == null) {
            throw new ValidationException("type", "Flow type cannot be null");
        }
        if (lockFlowDirection.getEntrance() == null) {
            throw new ValidationException("entrance", "Entrance direction cannot be null");
        }
        if (lockFlowDirection.getExit() == null) {
            throw new ValidationException("exit", "Exit direction cannot be null");
        }
        if (lockFlowDirection.getLockType() == null) {
            throw new ValidationException("lockType", "Lock type cannot be null");
        }
        if (lockFlowDirection.getDuration() == null || lockFlowDirection.getDuration() < 0) {
            throw new ValidationException("duration", "Duration must be non-negative");
        }

        logger.info("Locking flow direction: crossId={}, type={}, entrance={}, exit={}, lockType={}, duration={}",
                lockFlowDirection.getCrossId(),
                lockFlowDirection.getType(),
                lockFlowDirection.getEntrance(),
                lockFlowDirection.getExit(),
                lockFlowDirection.getLockType(),
                lockFlowDirection.getDuration());

        serviceFactory.getControlService().lockFlowDirection(
                lockFlowDirection.getCrossId(),
                lockFlowDirection.getType(),
                lockFlowDirection.getEntrance(),
                lockFlowDirection.getExit(),
                lockFlowDirection.getLockType(),
                lockFlowDirection.getLockStageNo(),
                lockFlowDirection.getDuration());

        return lockFlowDirection;
    }

    private Object handleUnlockFlowDirection(UnlockFlowDirection unlockFlowDirection) throws BusinessException {
        // 验证必要参数
        if (unlockFlowDirection.getCrossId() == null || unlockFlowDirection.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "Cross ID cannot be null or empty");
        }
        if (unlockFlowDirection.getType() == null) {
            throw new ValidationException("type", "Flow type cannot be null");
        }
        if (unlockFlowDirection.getEntrance() == null) {
            throw new ValidationException("entrance", "Entrance direction cannot be null");
        }
        if (unlockFlowDirection.getExit() == null) {
            throw new ValidationException("exit", "Exit direction cannot be null");
        }

        logger.info("Unlocking flow direction: crossId={}, type={}, entrance={}, exit={}",
                unlockFlowDirection.getCrossId(),
                unlockFlowDirection.getType(),
                unlockFlowDirection.getEntrance(),
                unlockFlowDirection.getExit());

        serviceFactory.getControlService().unlockFlowDirection(
                unlockFlowDirection.getCrossId(),
                unlockFlowDirection.getType(),
                unlockFlowDirection.getEntrance(),
                unlockFlowDirection.getExit());

        return unlockFlowDirection;
    }

    private Object handleStageCtrl(AdjustStage adjustStage) throws BusinessException {
        // 验证必要参数
        if (adjustStage.getCrossId() == null || adjustStage.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "Cross ID cannot be null or empty");
        }
        if (adjustStage.getStageNo() == null) {
            throw new ValidationException("stageNo", "Stage number cannot be null");
        }
        if (adjustStage.getType() == null) {
            throw new ValidationException("type", "Intervention type cannot be null");
        }
        if (adjustStage.getLen() == null || adjustStage.getLen() <= 0) {
            throw new ValidationException("len", "Intervention length must be positive");
        }

        logger.info("Stage intervention: crossId={}, stageNo={}, type={}, len={}",
                adjustStage.getCrossId(),
                adjustStage.getStageNo(),
                adjustStage.getType(),
                adjustStage.getLen());

        serviceFactory.getControlService().stageIntervention(
                adjustStage.getCrossId(),
                adjustStage.getStageNo(),
                adjustStage.getType(),
                adjustStage.getLen());

        return adjustStage;
    }

    private Object createSuccessResult(String message) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() { return message; }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
        };
    }

    @Override
    public String getHandlerName() {
        return "FlowControlHandler";
    }
}
