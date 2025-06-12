package com.traffic.gat1049.protocol.handler.main;

import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.sdo.SdoUser;
import com.traffic.gat1049.model.enums.SystemType;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.protocol.handler.base.AbstractProtocolHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.application.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Login处理器 - 使用现有的SessionManager
 * 按照原有的 validateLoginRequest -> dispatchLogin 设计模式
 */
public class LoginHandler extends AbstractProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final SessionManager sessionManager;

    public LoginHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Message doHandle(Message message) throws GatProtocolException {
        logger.info("处理Login请求: seq={}, from={}",
                message.getSeq(),
                message.getFrom() != null ? message.getFrom().getSys() : "unknown");

        try {
            // 1. 验证Login请求并提取登录信息
            LoginRequest loginRequest = validateLoginRequest(message);

            // 2. 分发登录处理（使用现有的SessionManager）
            SessionManager.LoginResult loginResult = dispatchLogin(loginRequest);

            // 3. 创建响应消息
            Message response = createLoginResponse(message, loginResult);

            logger.info("Login处理完成: success={}, token={}",
                    loginResult.isSuccess(),
                    loginResult.isSuccess() ? loginResult.getToken().substring(0, 8) + "..." : "N/A");

            return response;

        } catch (Exception e) {
            logger.error("Login处理失败", e);
            return createErrorResponse(message, GatConstants.ErrorCode.SYSTEM_ERROR, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 验证Login请求
     * 提取并验证登录相关的参数
     */
    private LoginRequest validateLoginRequest(Message message) throws GatProtocolException {
        String operationName = ProtocolUtils.getOperationName(message);
        if (!"Login".equals(operationName)) {
            throw new GatProtocolException("INVALID_OPERATION", "期望Login操作，实际收到: " + operationName);
        }

        Object operationData = ProtocolUtils.getOperationData(message);
        if (operationData == null) {
            throw new GatProtocolException("INVALID_PARAMETER", "Login操作数据不能为空");
        }

        // 解析登录数据
        LoginRequest request = new LoginRequest();
        request.setClientAddress(getClientAddress(message));
        request.setUser(extractUserInfo(operationData));
        request.setSystemType(extractSystemType(operationData));

        // 验证必要参数
        if (request.getUser() == null ||
                request.getUser().getUserName() == null ||
                request.getUser().getUserName().trim().isEmpty()) {
            throw new GatProtocolException("INVALID_PARAMETER", "用户名不能为空");
        }

        if (request.getUser().getPwd() == null ||
                request.getUser().getPwd().trim().isEmpty()) {
            throw new GatProtocolException("INVALID_PARAMETER", "密码不能为空");
        }

        logger.debug("验证Login请求成功: userName={}, systemType={}",
                request.getUser().getUserName(), request.getSystemType());

        return request;
    }

    /**
     * 分发登录处理
     * 使用现有的SessionManager执行登录业务逻辑
     */
    private SessionManager.LoginResult dispatchLogin(LoginRequest request) throws Exception {
        logger.info("执行用户登录: userName={}, systemType={}, clientAddress={}",
                request.getUser().getUserName(),
                request.getSystemType(),
                request.getClientAddress());

        // 使用现有的SessionManager进行登录
        SessionManager.LoginResult result = sessionManager.login(
                request.getClientAddress(),
                request.getUser(),
                request.getSystemType()
        );

        if (result.isSuccess()) {
            logger.info("用户登录成功: userName={}, token={}",
                    request.getUser().getUserName(),
                    result.getToken().substring(0, 8) + "...");
        } else {
            logger.warn("用户登录失败: userName={}, error={}",
                    request.getUser().getUserName(),
                    result.getErrorMessage());
        }

        return result;
    }

    /**
     * 创建登录响应消息
     */
    private Message createLoginResponse(Message request, SessionManager.LoginResult loginResult)
            throws GatProtocolException {

        if (loginResult.isSuccess()) {
            // 登录成功 - 创建成功响应
            Message responseMessage = createLoginSuccessResponse(request, loginResult.getToken(), loginResult.getUserName());

            return responseMessage;
        } else {
            // 登录失败 - 创建错误响应
            return createErrorResponse(request,
                    GatConstants.ErrorCode.SDE_FAILURE,
                    loginResult.getErrorMessage());
        }
    }

    /**
     * 获取客户端地址
     */
    private String getClientAddress(Message message) {
        if (message.getFrom() != null) {
            StringBuilder addr = new StringBuilder();
            if (message.getFrom().getSys() != null) {
                addr.append(message.getFrom().getSys());
            }
            if (message.getFrom().getSubSys() != null) {
                addr.append("/").append(message.getFrom().getSubSys());
            }
            if (message.getFrom().getInstance() != null) {
                addr.append("/").append(message.getFrom().getInstance());
            }
            return addr.toString();
        }
        return "UNKNOWN";
    }

    /**
     * 从操作数据中提取用户信息
     */
    private SdoUser extractUserInfo(Object data) throws GatProtocolException {
        SdoUser user = new SdoUser();

        if (data instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) data;

            // 提取用户名
            Object userName = map.get("userName");
            if (userName == null) userName = map.get("UserName");
            if (userName == null) userName = map.get("username");
            if (userName != null) {
                user.setUserName(userName.toString().trim());
            }

            // 提取密码
            Object password = map.get("password");
            if (password == null) password = map.get("Password");
            if (password == null) password = map.get("pwd");
            if (password != null) {
                user.setPwd(password.toString());
            }

        } else if (data instanceof SdoUser) {
            user = (SdoUser) data;
        } else {
            // 尝试通过反射获取
            try {
                java.lang.reflect.Method getUserName = data.getClass().getMethod("getUserName");
                java.lang.reflect.Method getPassword = data.getClass().getMethod("getPassword");

                Object userName = getUserName.invoke(data);
                Object password = getPassword.invoke(data);

                if (userName != null) user.setUserName(userName.toString().trim());
                if (password != null) user.setPwd(password.toString());

            } catch (Exception e) {
                logger.warn("无法通过反射提取用户信息: {}", e.getMessage());
            }
        }

        return user;
    }

    /**
     * 从操作数据中提取系统类型
     */
    private SystemType extractSystemType(Object data) {
        if (data instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) data;

            Object systemType = map.get("systemType");
            if (systemType == null) systemType = map.get("SystemType");

            if (systemType != null) {
                try {
                    if (systemType instanceof SystemType) {
                        return (SystemType) systemType;
                    } else {
                        return SystemType.valueOf(systemType.toString().toUpperCase());
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("无法识别系统类型: {}", systemType);
                }
            }
        }

        // 默认返回TSC系统类型
        return SystemType.UTCS;
    }

    @Override
    public boolean supports(Message message) {
        // 只处理Login操作的请求消息
        return ProtocolUtils.isRequest(message) &&
                "Login".equals(ProtocolUtils.getOperationName(message));
    }

    @Override
    public String getHandlerName() {
        return "LoginHandler";
    }

    /**
     * 登录请求数据结构
     */
    private static class LoginRequest {
        private String clientAddress;
        private SdoUser user;
        private SystemType systemType;

        // Getters and Setters
        public String getClientAddress() { return clientAddress; }
        public void setClientAddress(String clientAddress) { this.clientAddress = clientAddress; }

        public SdoUser getUser() { return user; }
        public void setUser(SdoUser user) { this.user = user; }

        public SystemType getSystemType() { return systemType; }
        public void setSystemType(SystemType systemType) { this.systemType = systemType; }
    }
}