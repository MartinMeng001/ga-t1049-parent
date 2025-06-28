package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.CrossRunInfoRetrans;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * 重传运行信息处理器
 * 处理集指平台请求信控系统重新传输指定时间段内的运行信息
 */
public class RunInfoRetransHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    // 支持的数据对象名称
    private static final Set<String> SUPPORTED_OBJ_NAMES = new HashSet<>();

    static {
        // 初始化支持的数据对象名称
        SUPPORTED_OBJ_NAMES.add("SignalControllerError");  // 信号机故障信息
        SUPPORTED_OBJ_NAMES.add("CrossModePlan");          // 路口控制方式方案
        SUPPORTED_OBJ_NAMES.add("CrossTrafficData");       // 路口交通流数据
        SUPPORTED_OBJ_NAMES.add("CrossCycle");             // 路口周期数据
        SUPPORTED_OBJ_NAMES.add("CrossStage");             // 路口阶段数据
        SUPPORTED_OBJ_NAMES.add("CrossSignalGroupStatus"); // 信号组灯态数据
        SUPPORTED_OBJ_NAMES.add("StageTrafficData");       // 阶段交通流数据
    }

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public RunInfoRetransHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof CrossRunInfoRetrans;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        CrossRunInfoRetrans retransRequest = (CrossRunInfoRetrans) ProtocolUtils.getOperationData(message);

        try {
            // 验证参数
            validateRetransRequest(retransRequest);

            // 处理重传请求
            handleRetransRequest(retransRequest);

            // 创建成功响应
            return createSuccessResponse(message, retransRequest);

        } catch (ValidationException e) {
            logger.error("Run info retrans validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), "");
        } catch (BusinessException e) {
            logger.error("Run info retrans business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
        }
    }

    /**
     * 验证重传请求参数
     */
    private void validateRetransRequest(CrossRunInfoRetrans retransRequest) throws ValidationException {
        // 验证基本参数
        if (retransRequest.getStartTime() == null || retransRequest.getStartTime().trim().isEmpty()) {
            throw new ValidationException("startTime", "Start time cannot be null or empty");
        }

        if (retransRequest.getEndTime() == null || retransRequest.getEndTime().trim().isEmpty()) {
            throw new ValidationException("endTime", "End time cannot be null or empty");
        }

        if (retransRequest.getObjName() == null || retransRequest.getObjName().trim().isEmpty()) {
            throw new ValidationException("objName", "Object name cannot be null or empty");
        }

        if (retransRequest.getCrossIdList() == null || retransRequest.getCrossIdList().isEmpty()) {
            throw new ValidationException("crossIdList", "Cross ID list cannot be null or empty");
        }

        // 验证时间格式
        validateTimeFormat(retransRequest.getStartTime(), "startTime");
        validateTimeFormat(retransRequest.getEndTime(), "endTime");

        // 验证时间范围合理性
        validateTimeRange(retransRequest.getStartTime(), retransRequest.getEndTime());

        // 验证数据对象名称
        validateObjName(retransRequest.getObjName());

        // 验证路口编号格式
        for (String crossId : retransRequest.getCrossIdList()) {
            if (!ProtocolUtils.isValidCrossId(crossId)) {
                throw new ValidationException("crossId", "Invalid cross ID format: " + crossId);
            }
        }
    }

    /**
     * 验证时间格式
     */
    private void validateTimeFormat(String timeStr, String fieldName) throws ValidationException {
        try {
            LocalDateTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName,
                    "Invalid time format: " + timeStr + ", expected format: yyyyMMddHHmmss");
        }
    }

    /**
     * 验证时间范围
     */
    private void validateTimeRange(String startTimeStr, String endTimeStr) throws ValidationException {
        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, TIME_FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, TIME_FORMATTER);
            LocalDateTime now = LocalDateTime.now();

            // 结束时间必须大于开始时间
            if (!endTime.isAfter(startTime)) {
                throw new ValidationException("timeRange", "End time must be after start time");
            }

            // 时间段不能超过30天
            if (startTime.plusDays(30).isBefore(endTime)) {
                throw new ValidationException("timeRange", "Time range cannot exceed 30 days");
            }

            // 开始时间不能是未来时间
            if (startTime.isAfter(now)) {
                throw new ValidationException("startTime", "Start time cannot be in the future");
            }

        } catch (DateTimeParseException e) {
            throw new ValidationException("timeFormat", "Invalid time format");
        }
    }

    /**
     * 验证数据对象名称
     */
    private void validateObjName(String objName) throws ValidationException {
        if (!SUPPORTED_OBJ_NAMES.contains(objName)) {
            throw new ValidationException("objName",
                    "Unsupported object name: " + objName +
                            ", supported names: " + SUPPORTED_OBJ_NAMES);
        }
    }

    /**
     * 处理重传请求
     */
    private void handleRetransRequest(CrossRunInfoRetrans retransRequest) throws BusinessException {
        String startTime = retransRequest.getStartTime();
        String endTime = retransRequest.getEndTime();
        String objName = retransRequest.getObjName();
        List<String> crossIds = retransRequest.getCrossIdList();

        logger.info("Processing run info retrans request: objName={}, timeRange=[{} - {}], crossCount={}",
                objName, startTime, endTime, crossIds.size());

        try {
            // 调用服务执行重传
            serviceFactory.getRunInfoRetransService().requestRetransmission(
                    startTime, endTime, objName, crossIds
            );

            // 记录每个路口的重传请求
            for (String crossId : crossIds) {
                logger.debug("Requested retransmission for cross {} of {} data from {} to {}",
                        crossId, objName, startTime, endTime);
            }

            logger.info("Run info retrans request processed successfully for {} crosses", crossIds.size());

        } catch (Exception e) {
            logger.error("Failed to process run info retrans request", e);
            throw new BusinessException("Failed to request data retransmission: " + e.getMessage());
        }
    }

    /**
     * 创建成功响应结果
     */
    private Object createSuccessResult(CrossRunInfoRetrans retransRequest) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                return String.format("Run info retransmission requested successfully: %s data from %s to %s for %d crosses",
                        retransRequest.getObjName(),
                        retransRequest.getStartTime(),
                        retransRequest.getEndTime(),
                        retransRequest.getCrossCount());
            }
            public String getObjName() { return retransRequest.getObjName(); }
            public String getStartTime() { return retransRequest.getStartTime(); }
            public String getEndTime() { return retransRequest.getEndTime(); }
            public Integer getCrossCount() { return retransRequest.getCrossCount(); }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(LocalDateTime.now()); }
        };
    }

    @Override
    public String getHandlerName() {
        return "RunInfoRetransHandler";
    }
}