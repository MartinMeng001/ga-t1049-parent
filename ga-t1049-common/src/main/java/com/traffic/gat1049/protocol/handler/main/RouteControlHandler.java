package com.traffic.gat1049.protocol.handler.main;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.runtime.RoadSectionSpeed;
import com.traffic.gat1049.protocol.model.runtime.RouteControlModeStatus;
import com.traffic.gat1049.protocol.model.runtime.RouteSpeed;
import com.traffic.gat1049.model.enums.RouteControlMode;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.service.interfaces.ServiceFactory;

/**
 * 干线控制处理器
 * 处理干线控制方式和推荐车速的设置
 */
public class RouteControlHandler extends AbstractProtocolHandler {

    private final ServiceFactory serviceFactory;

    public RouteControlHandler(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public boolean supports(Message message) {
        if (!isSetRequest(message)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        return data instanceof RouteControlModeStatus ||
                data instanceof RouteSpeed;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        Object data = ProtocolUtils.getOperationData(message);

        try {
            Object result = null;

            if (data instanceof RouteControlModeStatus) {
                result = handleRouteControlMode((RouteControlModeStatus) data);
            } else if (data instanceof RouteSpeed) {
                result = handleRouteSpeed((RouteSpeed) data);
            }

            return createSuccessResponse(message, result);

        } catch (ValidationException e) {
            logger.error("Route control validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (DataNotFoundException e) {
            logger.error("Route not found: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OBJECT_NOT_FOUND, e.getMessage());
        } catch (BusinessException e) {
            logger.error("Route control business error: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.OPERATION_FAILED, e.getMessage());
        }
    }

    private Object handleRouteControlMode(RouteControlModeStatus controlMode) throws BusinessException {
        // 验证参数
        validateRouteControlMode(controlMode);

        String routeId = controlMode.getRouteId();
        RouteControlMode mode = controlMode.getValue();

        logger.info("Setting route control mode: routeId={}, mode={}", routeId, mode);

        // 设置干线控制方式
        serviceFactory.getRouteControlService().setRouteControlMode(routeId, mode);

        // 根据控制方式执行相应操作
        switch (mode) {
            case TIMING_COORDINATED:
            case ACTUATED_COORDINATED:
            case ADAPTIVE_COORDINATED:
                // 启动协调控制
                serviceFactory.getRouteControlService().startCoordination(routeId);
                break;
            default:
                // 停止协调控制
                serviceFactory.getRouteControlService().stopCoordination(routeId);
                break;
        }

        return createControlModeResult(routeId, mode);
    }

    private Object handleRouteSpeed(RouteSpeed routeSpeed) throws BusinessException {
        // 验证参数
        validateRouteSpeed(routeSpeed);

        String routeId = routeSpeed.getRouteId();

        logger.info("Setting route speed: routeId={}, sections={}",
                routeId, routeSpeed.getRoadSectionSpeedList().size());

        // 设置干线路段推荐车速
        serviceFactory.getRouteControlService().setRouteSpeed(routeSpeed);

        return createRouteSpeedResult(routeSpeed);
    }

    private void validateRouteControlMode(RouteControlModeStatus controlMode) throws ValidationException {
        if (controlMode.getRouteId() == null || controlMode.getRouteId().trim().isEmpty()) {
            throw new ValidationException("routeId", "Route ID cannot be null or empty");
        }
        if (controlMode.getValue() == null) {
            throw new ValidationException("value", "Control mode value cannot be null");
        }

        // 验证线路编号格式（9位数字）
        if (!controlMode.getRouteId().matches("\\d{9}")) {
            throw new ValidationException("routeId", "Invalid route ID format: " + controlMode.getRouteId());
        }
    }

    private void validateRouteSpeed(RouteSpeed routeSpeed) throws ValidationException {
        if (routeSpeed.getRouteId() == null || routeSpeed.getRouteId().trim().isEmpty()) {
            throw new ValidationException("routeId", "Route ID cannot be null or empty");
        }
        if (routeSpeed.getRoadSectionSpeedList() == null || routeSpeed.getRoadSectionSpeedList().isEmpty()) {
            throw new ValidationException("roadSectionSpeedList", "Road section speed list cannot be null or empty");
        }

        // 验证线路编号格式
        if (!routeSpeed.getRouteId().matches("\\d{9}")) {
            throw new ValidationException("routeId", "Invalid route ID format: " + routeSpeed.getRouteId());
        }

        // 验证每个路段的推荐车速
        for (RoadSectionSpeed section : routeSpeed.getRoadSectionSpeedList()) {
            if (section.getUpCrossId() == null || section.getUpCrossId().trim().isEmpty()) {
                throw new ValidationException("upCrossId", "Upstream cross ID cannot be null or empty");
            }
            if (section.getDownCrossId() == null || section.getDownCrossId().trim().isEmpty()) {
                throw new ValidationException("downCrossId", "Downstream cross ID cannot be null or empty");
            }
            if (section.getRecommendSpeed() == null || section.getRecommendSpeed() < 0) {
                throw new ValidationException("recommendSpeed", "Recommend speed must be non-negative");
            }

            // 验证路口编号格式
            if (!ProtocolUtils.isValidCrossId(section.getUpCrossId())) {
                throw new ValidationException("upCrossId", "Invalid upstream cross ID format: " + section.getUpCrossId());
            }
            if (!ProtocolUtils.isValidCrossId(section.getDownCrossId())) {
                throw new ValidationException("downCrossId", "Invalid downstream cross ID format: " + section.getDownCrossId());
            }
        }
    }

    private Object createControlModeResult(String routeId, RouteControlMode mode) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                return String.format("Route control mode set successfully: %s", mode.getDescription());
            }
            public String getRouteId() { return routeId; }
            public String getControlMode() { return mode.getCode(); }
            public String getControlModeDesc() { return mode.getDescription(); }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
        };
    }

    private Object createRouteSpeedResult(RouteSpeed routeSpeed) {
        return new Object() {
            public String getCode() { return GatConstants.ErrorCode.SUCCESS; }
            public String getMessage() {
                return String.format("Route speed set successfully for %d sections",
                        routeSpeed.getRoadSectionSpeedList().size());
            }
            public String getRouteId() { return routeSpeed.getRouteId(); }
            public Integer getSectionCount() { return routeSpeed.getRoadSectionSpeedList().size(); }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
        };
    }

    @Override
    public String getHandlerName() {
        return "RouteControlHandler";
    }
}
