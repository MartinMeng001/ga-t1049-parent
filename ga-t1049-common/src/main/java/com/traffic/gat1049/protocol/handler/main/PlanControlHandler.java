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
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;
import com.traffic.gat1049.model.enums.OperationType;

import java.util.List;

/**
 * 配时方案控制命令处理器
 *
 * 更新说明：
 * - 支持 SetPlanParam 的 StageParamList 处理
 * - 新增配时方案时同步处理阶段参数
 * - 修改时可选择性更新阶段参数
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
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(),"");
        } catch (BusinessException e) {
            logger.error("Plan control business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(),"");
        }
    }

    /**
     * 处理设置配时方案参数命令
     * 支持 StageParamList 的处理
     */
    private Object handleSetPlanParam(SetPlanParam setPlanParam) throws BusinessException {
        // 基本参数验证
        if (setPlanParam.getOper() == null) {
            throw new ValidationException("oper", "Operation type cannot be null");
        }
        if (setPlanParam.getPlanParam() == null) {
            throw new ValidationException("planParam", "Plan parameter cannot be null");
        }

        // 获取操作类型和参数
        OperationType operation = setPlanParam.getOper();
        List<StageParam> stageParamList = setPlanParam.getStageParamList();
        boolean hasStageParams = stageParamList != null && !stageParamList.isEmpty();

        logger.info("Setting plan parameter: operation={}, crossId={}, planNo={}, hasStageParams={}",
                operation,
                setPlanParam.getPlanParam().getCrossId(),
                setPlanParam.getPlanParam().getPlanNo(),
                hasStageParams);

        // 根据操作类型处理
        switch (operation) {
            case ADD:
                return handleAddPlanParam(setPlanParam, stageParamList, hasStageParams);

            case MODIFY:
                return handleModifyPlanParam(setPlanParam, stageParamList, hasStageParams);

            case DELETE:
                return handleDeletePlanParam(setPlanParam, stageParamList, hasStageParams);

            default:
                throw new ValidationException("oper", "Unsupported operation type: " + operation);
        }
    }

    /**
     * 处理新增配时方案参数
     */
    private Object handleAddPlanParam(SetPlanParam setPlanParam, List<StageParam> stageParamList, boolean hasStageParams) throws BusinessException {
        // 新增配时方案
        Object planResult = serviceFactory.getPlanService().setPlanParam(
                setPlanParam.getOper(), setPlanParam.getPlanParam());

        // 如果同时提供了阶段参数，则新增阶段参数
        if (hasStageParams) {
            String crossId = setPlanParam.getPlanParam().getCrossId();

            logger.info("Adding {} stage parameters for new plan", stageParamList.size());

            for (StageParam stageParam : stageParamList) {
                // 确保阶段参数的路口编号与配时方案一致
                if (stageParam.getCrossId() == null) {
                    stageParam.setCrossId(crossId);
                } else if (!crossId.equals(stageParam.getCrossId())) {
                    logger.warn("Stage parameter crossId {} differs from plan crossId {}, using plan crossId",
                            stageParam.getCrossId(), crossId);
                    stageParam.setCrossId(crossId);
                }

                // 对于新增配时方案，如果阶段号为0，需要由系统分配
                if (stageParam.getStageNo() == null || stageParam.getStageNo() == 0) {
                    // 可以在这里实现自动分配阶段号的逻辑
                    logger.info("Stage number is 0 or null, will be assigned by system");
                }

                // 保存阶段参数
                try {
                    serviceFactory.getStageService().save(stageParam);
                    logger.info("Added stage parameter: crossId={}, stageNo={}",
                            stageParam.getCrossId(), stageParam.getStageNo());
                } catch (BusinessException e) {
                    logger.error("Failed to add stage parameter: crossId={}, stageNo={}, error={}",
                            stageParam.getCrossId(), stageParam.getStageNo(), e.getMessage());
                    // 根据业务需求决定是否继续或回滚
                    throw e;
                }
            }
        }

        return planResult;
    }

    /**
     * 处理修改配时方案参数
     */
    private Object handleModifyPlanParam(SetPlanParam setPlanParam, List<StageParam> stageParamList, boolean hasStageParams) throws BusinessException {
        // 修改配时方案
        Object planResult = serviceFactory.getPlanService().setPlanParam(
                setPlanParam.getOper(), setPlanParam.getPlanParam());

        // 如果提供了阶段参数列表，则更新相应的阶段参数
        if (hasStageParams) {
            String crossId = setPlanParam.getPlanParam().getCrossId();

            logger.info("Modifying {} stage parameters for existing plan", stageParamList.size());

            for (StageParam stageParam : stageParamList) {
                // 确保阶段参数的路口编号与配时方案一致
                if (stageParam.getCrossId() == null) {
                    stageParam.setCrossId(crossId);
                }

                // 更新阶段参数
                try {
                    serviceFactory.getStageService().update(stageParam);
                    logger.info("Modified stage parameter: crossId={}, stageNo={}",
                            stageParam.getCrossId(), stageParam.getStageNo());
                } catch (BusinessException e) {
                    logger.error("Failed to modify stage parameter: crossId={}, stageNo={}, error={}",
                            stageParam.getCrossId(), stageParam.getStageNo(), e.getMessage());
                    // 根据业务需求决定是否继续或回滚
                    throw e;
                }
            }
        }

        return planResult;
    }

    /**
     * 处理删除配时方案参数
     */
    private Object handleDeletePlanParam(SetPlanParam setPlanParam, List<StageParam> stageParamList, boolean hasStageParams) throws BusinessException {
        // 如果提供了阶段参数列表，先删除指定的阶段参数
        if (hasStageParams) {
            String crossId = setPlanParam.getPlanParam().getCrossId();

            logger.info("Deleting {} stage parameters before plan deletion", stageParamList.size());

            for (StageParam stageParam : stageParamList) {
                try {
                    String stageId = generateStageId(crossId, stageParam.getStageNo());
                    serviceFactory.getStageService().deleteById(stageId);
                    logger.info("Deleted stage parameter: crossId={}, stageNo={}",
                            crossId, stageParam.getStageNo());
                } catch (BusinessException e) {
                    logger.error("Failed to delete stage parameter: crossId={}, stageNo={}, error={}",
                            crossId, stageParam.getStageNo(), e.getMessage());
                    // 可以选择忽略不存在的阶段参数
                    if (!"Stage parameter not found".equals(e.getMessage())) {
                        throw e;
                    }
                }
            }
        }

        // 删除配时方案
        return serviceFactory.getPlanService().setPlanParam(
                setPlanParam.getOper(), setPlanParam.getPlanParam());
    }

    /**
     * 生成阶段ID
     */
    private String generateStageId(String crossId, Integer stageNo) {
        return crossId + "_" + stageNo;
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

        serviceFactory.getDayPlanService().setDayPlanParam(
                setDayPlanParam.getOper(), setDayPlanParam.getDayPlanParam());
        return setDayPlanParam;
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

        serviceFactory.getScheduleService().setScheduleParam(
                setScheduleParam.getOper(), setScheduleParam.getScheduleParam());
        return setScheduleParam;
    }

    private Object handleCenterPlan(CenterPlan centerPlan) throws BusinessException {
        // 验证必要字段
        validateCenterPlan(centerPlan);

        logger.info("Setting center plan: controlMode={}, maxRunTime={}, crossId={}",
                centerPlan.getControlMode(),
                centerPlan.getMaxRunTime(),
                centerPlan.getPlanParam().getCrossId());

        // 调用服务层处理
        Integer assignedPlanNo = serviceFactory.getPlanService().setCenterPlan(
                centerPlan.getControlMode(),
                centerPlan.getMaxRunTime(),
                centerPlan.getPlanParam());

        // 更新返回的方案号
        if (assignedPlanNo != null) {
            centerPlan.getPlanParam().setPlanNo(assignedPlanNo);
        }

        logger.info("Center plan set successfully: assignedPlanNo={}", assignedPlanNo);

        return centerPlan;
    }

    /**
     * 验证中心预案参数
     */
    private void validateCenterPlan(CenterPlan centerPlan) throws ValidationException {
        if (centerPlan == null) {
            throw new ValidationException("centerPlan", "Center plan cannot be null");
        }

        // 验证控制方式 - 支持新旧字段名
        if (centerPlan.getControlMode() == null) {
            // 尝试获取旧字段名的值（向后兼容）
            if (centerPlan.getCrossControlMode() != null) {
                centerPlan.setControlMode(centerPlan.getCrossControlMode());
            } else {
                throw new ValidationException("controlMode", "Control mode cannot be null");
            }
        }

        // 验证预案最大运行时长
        if (centerPlan.getMaxRunTime() == null) {
            // 如果未设置，使用默认值60分钟
            centerPlan.setMaxRunTime(60);
            logger.warn("MaxRunTime not set, using default value: 60 minutes");
        } else {
            Integer maxRunTime = centerPlan.getMaxRunTime();
            if (maxRunTime < 1 || maxRunTime > 1440) {
                throw new ValidationException("maxRunTime",
                        "MaxRunTime must be between 1 and 1440 minutes, got: " + maxRunTime);
            }
        }

        // 验证配时方案参数
        if (centerPlan.getPlanParam() == null) {
            throw new ValidationException("planParam", "Plan parameter cannot be null");
        }

        // 执行字段验证
        try {
            centerPlan.validate();
        } catch (IllegalArgumentException e) {
            throw new ValidationException("centerPlan", e.getMessage());
        }
    }

    @Override
    public String getHandlerName() {
        return "PlanControlHandler";
    }
}