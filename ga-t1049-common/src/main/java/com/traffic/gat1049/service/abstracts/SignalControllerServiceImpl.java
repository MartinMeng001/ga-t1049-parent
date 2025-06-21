package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.protocol.model.runtime.CrossState;
import com.traffic.gat1049.protocol.model.runtime.SignalControllerError;
import com.traffic.gat1049.service.interfaces.SignalControllerService;
import com.traffic.gat1049.model.enums.CommMode;
import com.traffic.gat1049.model.enums.ControllerErrorType;
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
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 信号机参数存储
    private final Map<String, SignalController> signalControllerStorage = new ConcurrentHashMap<>();

    // 信号机故障存储 - key: signalControllerId, value: 故障列表
    private final Map<String, List<SignalControllerError>> errorStorage = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public SignalControllerServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        //initializeSampleData();
    }

    @Override
    public SignalController findById(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }

        SignalController signalController = dataPrider.getSignalControllerById(signalControllerId);//signalControllerStorage.get(signalControllerId);
        if (signalController == null) {
            throw new DataNotFoundException("SignalController", signalControllerId);
        }

        return signalController;
    }

    @Override
    public List<SignalController> findAll() throws BusinessException {
        return dataPrider.getAllSignalControllers();
        //return new ArrayList<>(signalControllerStorage.values());
    }

    @Override
    public List<SignalController> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<SignalController> allControllers = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allControllers.size());

        if (start >= allControllers.size()) {
            return new ArrayList<>();
        }

        return allControllers.subList(start, end);
    }

    @Override
    public SignalController save(SignalController signalController) throws BusinessException {
        if (signalController == null) {
            throw new ValidationException("signalController", "信号机参数不能为空");
        }

        validateSignalController(signalController);

//        signalController.setCreateTime(LocalDateTime.now());
//        signalController.setUpdateTime(LocalDateTime.now());

        signalControllerStorage.put(signalController.getSignalControllerId(), signalController);

        logger.info("保存信号机参数: signalControllerId={}, supplier={}, type={}",
                signalController.getSignalControllerId(),
                signalController.getSupplier(),
                signalController.getType());

        return signalController;
    }

    @Override
    public SignalController update(SignalController signalController) throws BusinessException {
        if (signalController == null) {
            throw new ValidationException("signalController", "信号机参数不能为空");
        }

        String signalControllerId = signalController.getSignalControllerId();
        if (!signalControllerStorage.containsKey(signalControllerId)) {
            throw new DataNotFoundException("SignalController", signalControllerId);
        }

        validateSignalController(signalController);

//        signalController.setUpdateTime(LocalDateTime.now());
        signalControllerStorage.put(signalControllerId, signalController);

        logger.info("更新信号机参数: signalControllerId={}, supplier={}, type={}",
                signalController.getSignalControllerId(),
                signalController.getSupplier(),
                signalController.getType());

        return signalController;
    }

    @Override
    public void deleteById(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }

        SignalController removed = signalControllerStorage.remove(signalControllerId);
        if (removed == null) {
            throw new DataNotFoundException("SignalController", signalControllerId);
        }

        // 同时删除故障记录
        errorStorage.remove(signalControllerId);

        logger.info("删除信号机参数: signalControllerId={}", signalControllerId);
    }

    @Override
    public boolean existsById(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            return false;
        }
        return signalControllerStorage.containsKey(signalControllerId);
    }

    @Override
    public long count() throws BusinessException {
        return signalControllerStorage.size();
    }

    @Override
    public List<SignalController> findBySupplier(String supplier) throws BusinessException {
        if (supplier == null || supplier.trim().isEmpty()) {
            throw new ValidationException("supplier", "供应商不能为空");
        }

        return signalControllerStorage.values().stream()
                .filter(controller -> supplier.equals(controller.getSupplier()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SignalController> findByType(String type) throws BusinessException {
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("type", "规格型号不能为空");
        }

        return signalControllerStorage.values().stream()
                .filter(controller -> type.equals(controller.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SignalController> findByCommMode(CommMode commMode) throws BusinessException {
        if (commMode == null) {
            throw new ValidationException("commMode", "通信接口不能为空");
        }

        return signalControllerStorage.values().stream()
                .filter(controller -> commMode.equals(controller.getCommMode()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getControlledCrosses(String signalControllerId) throws BusinessException {
        SignalController controller = findById(signalControllerId);
        return controller.getCrossIdList() != null ?
                new ArrayList<>(controller.getCrossIdList()) : new ArrayList<>();
    }

    @Override
    public void addControlledCross(String signalControllerId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SignalController controller = findById(signalControllerId);

        if (controller.getCrossIdList() == null) {
            controller.setCrossIdList(new ArrayList<>());
        }

        if (!controller.getCrossIdList().contains(crossId)) {
            controller.getCrossIdList().add(crossId);
//            controller.setUpdateTime(LocalDateTime.now());

            logger.info("添加路口到信号机控制列表: signalControllerId={}, crossId={}",
                    signalControllerId, crossId);
        } else {
            logger.warn("路口已在信号机控制列表中: signalControllerId={}, crossId={}",
                    signalControllerId, crossId);
        }
    }

    @Override
    public void removeControlledCross(String signalControllerId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SignalController controller = findById(signalControllerId);

        if (controller.getCrossIdList() != null && controller.getCrossIdList().remove(crossId)) {
//            controller.setUpdateTime(LocalDateTime.now());

            logger.info("从信号机控制列表移除路口: signalControllerId={}, crossId={}",
                    signalControllerId, crossId);
        } else {
            logger.warn("路口不在信号机控制列表中: signalControllerId={}, crossId={}",
                    signalControllerId, crossId);
        }
    }

    @Override
    public void reportError(String signalControllerId, ControllerErrorType errorType, String errorDesc)
            throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }
        if (errorType == null) {
            throw new ValidationException("errorType", "故障类型不能为空");
        }

        // 验证信号机是否存在
        findById(signalControllerId);

        SignalControllerError error = new SignalControllerError();
        error.setSignalControllerId(signalControllerId);
        error.setErrorType(errorType);
        error.setErrorDesc(errorDesc != null ? errorDesc : "");
        error.setOccurTime(LocalDateTime.now());

        errorStorage.computeIfAbsent(signalControllerId, k -> new ArrayList<>()).add(error);

        logger.error("信号机故障报告: signalControllerId={}, errorType={}, errorDesc={}",
                signalControllerId, errorType, errorDesc);
    }

    @Override
    public List<SignalControllerError> getErrors(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }

        // 验证信号机是否存在
        findById(signalControllerId);
        List<Object> objs = dataPrider.getSignalTroublesByControllerId(signalControllerId);
        return objs.stream()
                .map(obj -> {
                    try {
                        return OBJECT_MAPPER.convertValue(obj, SignalControllerError.class);
                    } catch (IllegalArgumentException e) {
                        logger.warn("转换 SignalControllerError 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //return errorStorage.getOrDefault(signalControllerId, new ArrayList<>());
    }

    @Override
    public List<SignalControllerError> getAllErrors() throws BusinessException {
        List<Object> objs = dataPrider.getAllSignalTroubles();
        return objs.stream()
                .map(obj -> {
                    try {
                        return OBJECT_MAPPER.convertValue(obj, SignalControllerError.class);
                    } catch (IllegalArgumentException e) {
                        logger.warn("转换 SignalControllerError 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void clearErrors(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }

        // 验证信号机是否存在
        findById(signalControllerId);

        List<SignalControllerError> removed = errorStorage.remove(signalControllerId);
        int errorCount = removed != null ? removed.size() : 0;

        logger.info("清除信号机故障: signalControllerId={}, 清除故障数={}",
                signalControllerId, errorCount);
    }

    /**
     * 验证信号机参数
     */
    private void validateSignalController(SignalController signalController) throws BusinessException {
        if (signalController.getSignalControllerId() == null ||
                signalController.getSignalControllerId().trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }

        if (signalController.getSupplier() == null ||
                signalController.getSupplier().trim().isEmpty()) {
            throw new ValidationException("supplier", "供应商不能为空");
        }

        if (signalController.getType() == null ||
                signalController.getType().trim().isEmpty()) {
            throw new ValidationException("type", "规格型号不能为空");
        }

        if (signalController.getCommMode() == null) {
            throw new ValidationException("commMode", "通信接口不能为空");
        }

        // 验证信号机编号格式（根据注解定义应为17位数字）
        String signalControllerId = signalController.getSignalControllerId();
        if (!signalControllerId.matches("\\d{17}")) {
            throw new ValidationException("signalControllerId", "信号机编号格式不正确，应为17位数字");
        }
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        try {
            // 创建示例信号机1
            SignalController controller1 = new SignalController();
            controller1.setSignalControllerId("11010000100010001");
            controller1.setSupplier("海信网络科技");
            controller1.setType("HiSmart-TC400");
            controller1.setCommMode(CommMode.ETHERNET);

            List<String> crossIds1 = Arrays.asList("11010000100001", "11010000100002");
            controller1.setCrossIdList(crossIds1);

            // 创建示例信号机2
            SignalController controller2 = new SignalController();
            controller2.setSignalControllerId("11010000100020001");
            controller2.setSupplier("青岛易触科技");
            controller2.setType("YC-TSC300");
            controller2.setCommMode(CommMode.SERIAL);

            List<String> crossIds2 = Arrays.asList("11010000100003");
            controller2.setCrossIdList(crossIds2);

            // 创建示例信号机3
            SignalController controller3 = new SignalController();
            controller3.setSignalControllerId("11010000100030001");
            controller3.setSupplier("大华技术");
            controller3.setType("DH-TSC200");
            controller3.setCommMode(CommMode.ETHERNET);

            List<String> crossIds3 = Arrays.asList("11010000100004", "11010000100005");
            controller3.setCrossIdList(crossIds3);

            save(controller1);
            save(controller2);
            save(controller3);

            // 添加一些示例故障
            //reportError("11010000100010001", ControllerErrorType.COMM_ERROR, "网络通信超时");
            //reportError("11010000100020001", ControllerErrorType.HARDWARE_ERROR, "检测器故障");

            logger.info("示例信号机数据初始化完成，共{}台信号机", signalControllerStorage.size());
        } catch (BusinessException e) {
            logger.error("示例信号机数据初始化失败", e);
        }
    }
}