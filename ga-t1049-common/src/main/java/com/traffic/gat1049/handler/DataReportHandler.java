package com.traffic.gat1049.handler;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.model.entity.command.CrossReportCtrl;
import com.traffic.gat1049.model.enums.ReportCommand;
import com.traffic.gat1049.model.enums.ReportDataType;
import com.traffic.gat1049.protocol.handler.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

import java.util.List;

/**
 * 数据上报控制处理器
 * 处理路口周期、阶段、信号组灯态、交通流数据的上报控制
 */
public class DataReportHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public DataReportHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof CrossReportCtrl;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        CrossReportCtrl reportCtrl = (CrossReportCtrl) ProtocolUtils.getOperationData(message);

        try {
            // 验证参数
            validateReportCtrl(reportCtrl);

            // 处理数据上报控制
            handleReportControl(reportCtrl);

            // 创建成功响应
            return createSuccessResponse(message, createSuccessResult(reportCtrl));

        } catch (ValidationException e) {
            logger.error("Report control validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (BusinessException e) {
            logger.error("Report control business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage());
        }
    }

    private void validateReportCtrl(CrossReportCtrl reportCtrl) throws ValidationException {
        if (reportCtrl.getCmd() == null) {
            throw new ValidationException("cmd", "Command cannot be null");
        }
        if (reportCtrl.getType() == null) {
            throw new ValidationException("type", "Report data type cannot be null");
        }
        if (reportCtrl.getCrossIdList() == null || reportCtrl.getCrossIdList().isEmpty()) {
            throw new ValidationException("crossIdList", "Cross ID list cannot be null or empty");
        }

        // 验证路口编号格式
        for (String crossId : reportCtrl.getCrossIdList()) {
            if (!ProtocolUtils.isValidCrossId(crossId)) {
                throw new ValidationException("crossId", "Invalid cross ID format: " + crossId);
            }
        }
    }

    private void handleReportControl(CrossReportCtrl reportCtrl) throws BusinessException {
        ReportCommand command = reportCtrl.getCmd();
        ReportDataType dataType = reportCtrl.getType();
        List<String> crossIds = reportCtrl.getCrossIdList();

        logger.info("Processing report control: command={}, dataType={}, crossCount={}",
                command, dataType, crossIds.size());

        // 调用服务控制数据上报
        serviceFactory.getTrafficDataService().controlDataReport(
                command.getCode(),
                dataType.getCode(),
                crossIds
        );

        // 记录每个路口的设置
        for (String crossId : crossIds) {
            logger.debug("Set {} report for cross {} to {}",
                    dataType.getDescription(), crossId, command.getDescription());
        }
    }

    private Object createSuccessResult(CrossReportCtrl reportCtrl) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                return String.format("Report control set successfully: %s %s for %d crosses",
                        reportCtrl.getCmd().getDescription(),
                        reportCtrl.getType().getDescription(),
                        reportCtrl.getCrossIdList().size());
            }
            public String getCommand() { return reportCtrl.getCmd().getCode(); }
            public String getDataType() { return reportCtrl.getType().getCode(); }
            public Integer getCrossCount() { return reportCtrl.getCrossIdList().size(); }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
        };
    }

    @Override
    public String getHandlerName() {
        return "DataReportHandler";
    }
}
