package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.model.enums.OnlineStatus;
import com.traffic.gat1049.protocol.model.device.SignalControllerParam;
import com.traffic.gat1049.protocol.model.runtime.SignalControllerState;
import com.traffic.gat1049.service.interfaces.SignalControllerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号机服务实现
 */
public class SignalControllerServiceImpl implements SignalControllerService {

    private static final Logger logger = LoggerFactory.getLogger(SignalControllerServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 内存存储，实际项目中应该从数据库获取
    private final Map<String, SignalControllerParam> controllerStorage = new ConcurrentHashMap<>();
    private final Map<String, SignalControllerState> controllerStateStorage = new ConcurrentHashMap<>();

    public SignalControllerServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public SignalControllerParam findById(String controllerId) throws BusinessException {
        if (controllerId == null || controllerId.trim().isEmpty()) {
            throw new ValidationException("controllerId", "信号机编号不能为空");
        }

        // 先从数据提供者获取
        try {
            List<SignalControllerParam> controllers = dataProvider.getSignalControllerParams();
            if (controllers != null) {
                for (SignalControllerParam controller : controllers) {
                    if (controllerId.equals(controller.getSignalControllerId())) {
                        return controller;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取信号机数据失败，使用本地存储", e);
        }

        SignalControllerParam controller = controllerStorage.get(controllerId);
        if (controller == null) {
            throw new DataNotFoundException("SignalControllerParam", controllerId);
        }

        return controller;
    }

    @Override
    public List<SignalControllerParam> findAll() throws BusinessException {
        try {
            List<SignalControllerParam> controllers = dataProvider.getSignalControllerParams();
            if (controllers != null && !controllers.isEmpty()) {
                return controllers;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取信号机列表失败，使用本地存储", e);
        }

        return new ArrayList<>(controllerStorage.values());
    }

    @Override
    public List<SignalControllerParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<SignalControllerParam> allControllers = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allControllers.size());

        if (startIndex >= allControllers.size()) {
            return new ArrayList<>();
        }

        return allControllers.subList(startIndex, endIndex);
    }

    @Override
    public SignalControllerParam save(SignalControllerParam controllerParam) throws BusinessException {
        if (controllerParam == null) {
            throw new ValidationException("controllerParam", "信号机参数不能为空");
        }

        validateControllerParam(controllerParam);

        controllerParam.setUpdateTime(LocalDateTime.now());
        if (controllerParam.getCreateTime() == null) {
            controllerParam.setCreateTime(LocalDateTime.now());
        }

        controllerStorage.put(controllerParam.getSignalControllerId(), controllerParam);
        logger.info("保存信号机参数: {}", controllerParam.getSignalControllerId());

        return controllerParam;
    }

    @Override
    public void deleteById(String controllerId) throws BusinessException {
        if (controllerId == null || controllerId.trim().isEmpty()) {
            throw new ValidationException("controllerId", "信号机编号不能为空");
        }

        SignalControllerParam removed = controllerStorage.remove(controllerId);
        if (removed == null) {
            throw new DataNotFoundException("SignalControllerParam", controllerId);
        }

        // 同时删除状态信息
        controllerStateStorage.remove(controllerId);
        logger.info("删除信号机参数: {}", controllerId);
    }

    @Override
    public List<SignalControllerParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        return findAll().stream()
                .filter(controller -> crossId.equals(controller.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SignalControllerParam> findByOnlineStatus(OnlineStatus status) throws BusinessException {
        if (status == null) {
            throw new ValidationException("status", "在线状态不能为空");
        }

        return findAll().stream()
                .filter(controller -> {
                    SignalControllerState state = controllerStateStorage.get(controller.getSignalControllerId());
                    return state != null && status.equals(state.getOnlineStatus());
                })
                .collect(Collectors.toList());
    }

    @Override
    public SignalControllerState getControllerState(String controllerId) throws BusinessException {
        if (controllerId == null || controllerId.trim().isEmpty()) {
            throw new ValidationException("controllerId", "信号机编号不能为空");
        }

        // 先从数据提供者获取
        try {
            SignalControllerState state = dataProvider.getSignalControllerState(controllerId);
            if (state != null) {
                return state;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取信号机状态失败，使用本地存储", e);
        }

        SignalControllerState state = controllerStateStorage.get(controllerId);
        if (state == null) {
            throw new DataNotFoundException("SignalControllerState", controllerId);
        }

        return state;
    }

    @Override
    public void updateControllerState(String controllerId, SignalControllerState state) throws BusinessException {
        if (controllerId == null || controllerId.trim().isEmpty()) {
            throw new ValidationException("controllerId", "信号机编号不能为空");
        }
        if (state == null) {
            throw new ValidationException("state", "信号机状态不能为空");
        }

        // 验证信号机是否存在
        findById(controllerId);

        state.setSignalControllerId(controllerId);
        state.setStateTime(LocalDateTime.now());
        controllerStateStorage.put(controllerId, state);

        logger.info("更新信号机状态: controllerId={}, onlineStatus={}",
                controllerId, state.getOnlineStatus());
    }

    @Override
    public List<String> getControllerIds() throws BusinessException {
        return new ArrayList<>(controllerStorage.keySet());
    }

    @Override
    public boolean isOnline(String controllerId) throws BusinessException {
        try {
            SignalControllerState state = getControllerState(controllerId);
            return OnlineStatus.ONLINE.equals(state.getOnlineStatus());
        } catch (DataNotFoundException e) {
            return false;
        }
    }

    @Override
    public void setOnlineStatus(String controllerId, OnlineStatus status) throws BusinessException {
        if (controllerId == null || controllerId.trim().isEmpty()) {
            throw new ValidationException("controllerId", "信号机编号不能为空");
        }
        if (status == null) {
            throw new ValidationException("status", "在线状态不能为空");
        }

        SignalControllerState state = controllerStateStorage.get(controllerId);
        if (state == null) {
            state = new SignalControllerState();
            state.setSignalControllerId(controllerId);
        }

        state.setOnlineStatus(status);
        state.setStateTime(LocalDateTime.now());
        controllerStateStorage.put(controllerId, state);

        logger.info("设置信号机在线状态: controllerId={}, status={}", controllerId, status);
    }

    private void validateControllerParam(SignalControllerParam controllerParam) throws BusinessException {
        if (controllerParam.getSignalControllerId() == null ||
                controllerParam.getSignalControllerId().trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }

        if (controllerParam.getCrossId() == null || controllerParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        // 验证信号机编号格式（17位数字）
        if (!controllerParam.getSignalControllerId().matches("\\d{17}")) {
            throw new ValidationException("signalControllerId", "信号机编号格式错误，应为17位数字");
        }
    }

    private void initializeSampleData() {
        try {
            // 初始化示例信号机数据
            SignalControllerParam controller1 = new SignalControllerParam();
            controller1.setSignalControllerId("11010000000000001");
            controller1.setCrossId("11010000100001");
            controller1.setManufacturer("海信网络科技");
            controller1.setModel("HiCon-3000");
            controller1.setVersion("3.0.1");
            controller1.setCreateTime(LocalDateTime.now());
            controllerStorage.put(controller1.getSignalControllerId(), controller1);

            // 对应的状态
            SignalControllerState state1 = new SignalControllerState();
            state1.setSignalControllerId("11010000000000001");
            state1.setOnlineStatus(OnlineStatus.ONLINE);
            state1.setStateTime(LocalDateTime.now());
            controllerStateStorage.put("11010000000000001", state1);

            SignalControllerParam controller2 = new SignalControllerParam();
            controller2.setSignalControllerId("11010000000000002");
            controller2.setCrossId("11010000100002");
            controller2.setManufacturer("易华录");
            controller2.setModel("EHR-2000");
            controller2.setVersion("2.1.5");
            controller2.setCreateTime(LocalDateTime.now());
            controllerStorage.put(controller2.getSignalControllerId(), controller2);

            // 对应的状态
            SignalControllerState state2 = new SignalControllerState();
            state2.setSignalControllerId("11010000000000002");
            state2.setOnlineStatus(OnlineStatus.OFFLINE);
            state2.setStateTime(LocalDateTime.now().minusMinutes(5));
            controllerStateStorage.put("11010000000000002", state2);

            SignalControllerParam controller3 = new SignalControllerParam();
            controller3.setSignalControllerId("11010000000000003");
            controller3.setCrossId("11010000100003");
            controller3.setManufacturer("大华股份");
            controller3.setModel("DH-ITC");
            controller3.setVersion("1.8.2");
            controller3.setCreateTime(LocalDateTime.now());
            controllerStorage.put(controller3.getSignalControllerId(), controller3);

            // 对应的状态
            SignalControllerState state3 = new SignalControllerState();
            state3.setSignalControllerId("11010000000000003");
            state3.setOnlineStatus(OnlineStatus.ONLINE);
            state3.setStateTime(LocalDateTime.now());
            controllerStateStorage.put("11010000000000003", state3);

            logger.info("初始化信号机示例数据完成，共 {} 台信号机", controllerStorage.size());
        } catch (Exception e) {
            logger.error("初始化信号机示例数据失败", e);
        }
    }