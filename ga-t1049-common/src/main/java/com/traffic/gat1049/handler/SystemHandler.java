package com.traffic.gat1049.handler;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.model.entity.system.SysInfo;
import com.traffic.gat1049.model.entity.system.SysState;
import com.traffic.gat1049.protocol.handler.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 系统信息和状态处理器
 */
public class SystemHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public SystemHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isQueryRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        if (!(data instanceof com.traffic.gat1049.model.entity.command.TSCCmd)) {
            return false;
        }

        com.traffic.gat1049.model.entity.command.TSCCmd tscCmd =
                (com.traffic.gat1049.model.entity.command.TSCCmd) data;

        String objName = tscCmd.getObjName();
        return GatConstants.ObjectName.SYS_INFO.equals(objName) ||
                GatConstants.ObjectName.SYS_STATE.equals(objName);
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        com.traffic.gat1049.model.entity.command.TSCCmd tscCmd =
                (com.traffic.gat1049.model.entity.command.TSCCmd) ProtocolUtils.getOperationData(message);

        try {
            Object result = null;
            String objName = tscCmd.getObjName();

            switch (objName) {
                case GatConstants.ObjectName.SYS_INFO:
                    result = handleSystemInfo();
                    break;
                case GatConstants.ObjectName.SYS_STATE:
                    result = handleSystemState();
                    break;
                default:
                    throw new ValidationException("objName", "Unsupported object name: " + objName);
            }

            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("System handler validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (BusinessException e) {
            logger.error("System handler business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage());
        }
    }

    private SysInfo handleSystemInfo() throws BusinessException {
        logger.info("Querying system information");
        return serviceFactory.getSystemService().getSystemInfo();
    }

    private SysState handleSystemState() throws BusinessException {
        logger.info("Querying system state");
        return serviceFactory.getSystemService().getSystemState();
    }

    @Override
    public String getHandlerName() {
        return "SystemHandler";
    }
}
