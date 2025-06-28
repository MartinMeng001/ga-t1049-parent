package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.CrossCtrlInfo;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 路口控制方式方案处理器
 * 处理路口控制方式方案的指定命令
 * 对应文档5.3.2 指定控制方式方案
 */
public class CrossCtrlInfoHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public CrossCtrlInfoHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof CrossCtrlInfo;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        CrossCtrlInfo ctrlInfo = (CrossCtrlInfo) ProtocolUtils.getOperationData(message);

        try {
            // 验证参数
            validateCrossCtrlInfo(ctrlInfo);

            // 处理控制方式方案设置
            handleControlModeSet(ctrlInfo);

            // 创建成功响应
            return createSuccessResponse(message, ctrlInfo);

        } catch (ValidationException e) {
            logger.error("Cross control info validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), "");
        } catch (BusinessException e) {
            logger.error("Cross control info business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage(), "");
        }
    }

    /**
     * 验证CrossCtrlInfo参数
     */
    private void validateCrossCtrlInfo(CrossCtrlInfo ctrlInfo) throws ValidationException {
        if (ctrlInfo.getCrossId() == null || ctrlInfo.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (ctrlInfo.getControlMode() == null) {
            throw new ValidationException("controlMode", "控制方式不能为空");
        }

        if (ctrlInfo.getPlanNo() == null) {
            throw new ValidationException("planNo", "方案序号不能为空");
        }

        // 验证路口编号格式
        if (!ProtocolUtils.isValidCrossId(ctrlInfo.getCrossId())) {
            throw new ValidationException("crossId", "路口编号格式不正确: " + ctrlInfo.getCrossId());
        }

        // 验证方案序号范围
        if (ctrlInfo.getPlanNo() < 0 || ctrlInfo.getPlanNo() > 9999) {
            throw new ValidationException("planNo", "方案序号超出范围(0-9999): " + ctrlInfo.getPlanNo());
        }

        // 验证特殊控制方式的方案号
        validateSpecialControlMode(ctrlInfo);

        // 验证控制方式方案整体有效性
        if (!ctrlInfo.isValid()) {
            throw new ValidationException("ctrlInfo", "控制方式方案参数无效");
        }
    }

    /**
     * 验证特殊控制方式的方案号
     * 当控制方式为00、11、12、13时方案序号必须为0
     */
    private void validateSpecialControlMode(CrossCtrlInfo ctrlInfo) throws ValidationException {
        ControlMode mode = ctrlInfo.getControlMode();
        Integer planNo = ctrlInfo.getPlanNo();

        boolean isSpecialMode = mode == ControlMode.CANCEL ||
                mode == ControlMode.LIGHT_OFF ||
                mode == ControlMode.ALL_RED ||
                mode == ControlMode.ALL_YELLOW_FLASH;

        if (isSpecialMode && planNo != 0) {
            throw new ValidationException("planNo",
                    String.format("控制方式%s(%s)的方案序号必须为0，当前值：%d",
                            mode.getDescription(), mode.getCode(), planNo));
        }

        if (!isSpecialMode && planNo == 0) {
            throw new ValidationException("planNo",
                    String.format("控制方式%s(%s)需要有效的方案序号，不能为0",
                            mode.getDescription(), mode.getCode()));
        }
    }

    /**
     * 处理控制方式方案设置
     */
    private void handleControlModeSet(CrossCtrlInfo ctrlInfo) throws BusinessException {
        String crossId = ctrlInfo.getCrossId();
        ControlMode controlMode = ctrlInfo.getControlMode();
        Integer planNo = ctrlInfo.getPlanNo();

        logger.info("设置路口控制方式方案: crossId={}, controlMode={}({}), planNo={}",
                crossId, controlMode.getDescription(), controlMode.getCode(), planNo);

        try {
            // 调用控制模式服务设置控制方式
            serviceFactory.getControlModeService().setControlMode(crossId, controlMode, planNo);

            logger.info("路口控制方式方案设置成功: crossId={}, mode={}, plan={}",
                    crossId, controlMode.getDescription(), planNo);

        } catch (BusinessException e) {
            logger.error("设置路口控制方式方案失败: crossId={}, error={}", crossId, e.getMessage());
            throw e;
        }
    }

    /**
     * 创建成功响应结果
     */
    private Object createSuccessResult(CrossCtrlInfo ctrlInfo) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                return String.format("路口控制方式方案设置成功: %s -> %s(方案%d)",
                        ctrlInfo.getCrossId(),
                        ctrlInfo.getControlMode().getDescription(),
                        ctrlInfo.getPlanNo());
            }
            public String getCrossId() { return ctrlInfo.getCrossId(); }
            public String getControlMode() { return ctrlInfo.getControlMode().getCode(); }
            public String getControlModeDesc() { return ctrlInfo.getControlMode().getDescription(); }
            public Integer getPlanNo() { return ctrlInfo.getPlanNo(); }
            public String getTimestamp() {
                return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now());
            }
        };
    }

    @Override
    public String getHandlerName() {
        return "CrossCtrlInfoHandler";
    }
}