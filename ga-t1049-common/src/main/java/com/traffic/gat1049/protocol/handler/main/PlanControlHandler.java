package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.SetPlanParam;
import com.traffic.gat1049.protocol.model.command.SetDayPlanParam;
import com.traffic.gat1049.protocol.model.command.SetScheduleParam;
import com.traffic.gat1049.protocol.model.command.CenterPlan;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 配时方案控制命令处理器
 */
public class PlanControlHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public PlanControlHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof SetPlanParam ||
                data instanceof SetDayPlanParam ||
                data instanceof SetScheduleParam ||
                data instanceof CenterPlan;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        Object data = ProtocolUtils.getOperationData(message);

        try {
            Object result = null;

            if (data instanceof SetPlanParam) {
                result = handleSetPlanParam((SetPlanParam) data);
            } else if (data instanceof SetDayPlanParam) {
                result = handleSetDayPlanParam((SetDayPlanParam) data);
            } else if (data instanceof SetScheduleParam) {
                result = handleSetScheduleParam((SetScheduleParam) data);
            } else if (data instanceof CenterPlan) {
                result = handleCenterPlan((CenterPlan) data);
            }

            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("Plan control validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (BusinessException e) {
            logger.error("Plan control business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage());
        }
    }

    private Object handleSetPlanParam(SetPlanParam setPlanParam) throws BusinessException {
        if (setPlanParam.getOper() == null) {
            throw new ValidationException("oper", "Operation type cannot be null");
        }
        if (setPlanParam.getPlanParam() == null) {
            throw new ValidationException("planParam", "Plan parameter cannot be null");
        }

        logger.info("Setting plan parameter: operation={}, crossId={}, planNo={}",
                setPlanParam.getOper(),
                setPlanParam.getPlanParam().getCrossId(),
                setPlanParam.getPlanParam().getPlanNo());

        return serviceFactory.getPlanService().setPlanParam(
                setPlanParam.getOper(), setPlanParam.getPlanParam());
    }

    private Object handleSetDayPlanParam(SetDayPlanParam setDayPlanParam) throws BusinessException {
        if (setDayPlanParam.getOper() == null) {
            throw new ValidationException("oper", "Operation type cannot be null");
        }
        if (setDayPlanParam.getDayPlanParam() == null) {
            throw new ValidationException("dayPlanParam", "Day plan parameter cannot be null");
        }

        logger.info("Setting day plan parameter: operation={}, crossId={}, dayPlanNo={}",
                setDayPlanParam.getOper(),
                setDayPlanParam.getDayPlanParam().getCrossId(),
                setDayPlanParam.getDayPlanParam().getDayPlanNo());

        return serviceFactory.getPlanService().setDayPlanParam(
                setDayPlanParam.getOper(), setDayPlanParam.getDayPlanParam());
    }

    private Object handleSetScheduleParam(SetScheduleParam setScheduleParam) throws BusinessException {
        if (setScheduleParam.getOper() == null) {
            throw new ValidationException("oper", "Operation type cannot be null");
        }
        if (setScheduleParam.getScheduleParam() == null) {
            throw new ValidationException("scheduleParam", "Schedule parameter cannot be null");
        }

        logger.info("Setting schedule parameter: operation={}, crossId={}, scheduleNo={}",
                setScheduleParam.getOper(),
                setScheduleParam.getScheduleParam().getCrossId(),
                setScheduleParam.getScheduleParam().getScheduleNo());

        return serviceFactory.getPlanService().setScheduleParam(
                setScheduleParam.getOper(), setScheduleParam.getScheduleParam());
    }

    private Object handleCenterPlan(CenterPlan centerPlan) throws BusinessException {
        if (centerPlan.getCrossControlMode() == null) {
            throw new ValidationException("crossControlMode", "Control mode cannot be null");
        }
        if (centerPlan.getPlanParam() == null) {
            throw new ValidationException("planParam", "Plan parameter cannot be null");
        }

        logger.info("Setting center plan: controlMode={}, crossId={}",
                centerPlan.getCrossControlMode(),
                centerPlan.getPlanParam().getCrossId());

        Integer assignedPlanNo = serviceFactory.getPlanService().setCenterPlan(
                centerPlan.getCrossControlMode(), centerPlan.getPlanParam());

        // 返回分配的方案号
        return new Object() {
            public Integer getAssignedPlanNo() { return assignedPlanNo; }
            public String getMessage() { return "Center plan set successfully"; }
        };
    }

    @Override
    public String getHandlerName() {
        return "PlanControlHandler";
    }
}
