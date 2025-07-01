package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.protocol.model.runtime.SignalControllerError;
import com.traffic.gat1049.service.interfaces.SignalControllerService;
import com.traffic.gat1049.model.enums.CommMode;
import com.traffic.gat1049.model.enums.ControllerErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号机服务实现
 * 更新以支持新的SignalController标准定义
 */
public class SignalControllerServiceImpl implements SignalControllerService {

    private static final Logger logger = LoggerFactory.getLogger(SignalControllerServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 信号机参数存储
    private final Map<String, SignalController> signalControllerStorage = new ConcurrentHashMap<>();
    // 信号机故障存储 - key: signalControllerID, value: 故障列表
    private final Map<String, List<SignalControllerError>> errorStorage = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Validator validator;

    public SignalControllerServiceImpl() throws BusinessException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        // 初始化一些示例数据可以在这里添加
        initializeSampleData();
    }

    private void initializeSampleData() {
        // 添加一些示例数据用于测试
        SignalController sample1 = new SignalController();
        sample1.setSignalControllerID("11010099001");
        sample1.setSupplier("海信网络科技");
        sample1.setType("HiSico-TCS100");
        sample1.setId("TSC001");
        sample1.setCommMode(CommMode.TCP_SERVER);
        sample1.setIp("192.168.1.100");
        sample1.setSubMask("255.255.255.0");
        sample1.setGateway("192.168.1.1");
        sample1.setPort(8080);
        sample1.setHasDoorStatus(1);
        sample1.setLongitude(116.397128);
        sample1.setLatitude(39.916527);
        sample1.setCrossIDList(Arrays.asList("11010001", "11010002"));

        signalControllerStorage.put(sample1.getSignalControllerID(), sample1);
    }

    @Override
    public SignalController findById(String signalControllerID) throws BusinessException {
        if (signalControllerID == null || signalControllerID.trim().isEmpty()) {
            throw new ValidationException("signalControllerID", "信号机编号不能为空");
        }

        SignalController signalController = signalControllerStorage.get(signalControllerID);
        if (signalController == null) {
            throw new DataNotFoundException("SignalController", signalControllerID);
        }

        return signalController;
    }

    @Override
    public List<SignalController> findBySignalControllerId(String signalControllerID) throws BusinessException {
        if (signalControllerID == null || signalControllerID.trim().isEmpty()) {
            throw new ValidationException("signalControllerID", "信号机编号不能为空");
        }
        return dataProvider.getSignalControllersById(signalControllerID);
    }

    @Override
    public List<SignalController> findAll() throws BusinessException {
        return dataProvider.getAllSignalControllers();
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

        signalControllerStorage.put(signalController.getSignalControllerID(), signalController);

        logger.info("保存信号机参数: signalControllerID={}, supplier={}, type={}, id={}",
                signalController.getSignalControllerID(),
                signalController.getSupplier(),
                signalController.getType(),
                signalController.getId());

        return signalController;
    }

    @Override
    public SignalController update(SignalController signalController) throws BusinessException {
        if (signalController == null) {
            throw new ValidationException("signalController", "信号机参数不能为空");
        }

        String signalControllerID = signalController.getSignalControllerID();
        if (!signalControllerStorage.containsKey(signalControllerID)) {
            throw new DataNotFoundException("SignalController", signalControllerID);
        }

        validateSignalController(signalController);

        signalControllerStorage.put(signalControllerID, signalController);

        logger.info("更新信号机参数: signalControllerID={}, supplier={}, type={}, id={}",
                signalController.getSignalControllerID(),
                signalController.getSupplier(),
                signalController.getType(),
                signalController.getId());

        return signalController;
    }

    @Override
    public void deleteById(String signalControllerID) throws BusinessException {
        if (signalControllerID == null || signalControllerID.trim().isEmpty()) {
            throw new ValidationException("signalControllerID", "信号机编号不能为空");
        }

        SignalController removed = signalControllerStorage.remove(signalControllerID);
        if (removed == null) {
            throw new DataNotFoundException("SignalController", signalControllerID);
        }

        // 同时删除故障记录
        errorStorage.remove(signalControllerID);

        logger.info("删除信号机参数: signalControllerID={}", signalControllerID);
    }

    @Override
    public boolean existsById(String signalControllerID) throws BusinessException {
        if (signalControllerID == null || signalControllerID.trim().isEmpty()) {
            return false;
        }
        return signalControllerStorage.containsKey(signalControllerID);
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
    public List<SignalController> findByIp(String ip) throws BusinessException {
        if (ip == null || ip.trim().isEmpty()) {
            throw new ValidationException("ip", "IP地址不能为空");
        }

        return signalControllerStorage.values().stream()
                .filter(controller -> ip.equals(controller.getIp()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SignalController> findByLocationRange(Double minLongitude, Double maxLongitude,
                                                      Double minLatitude, Double maxLatitude) throws BusinessException {
        if (minLongitude == null || maxLongitude == null || minLatitude == null || maxLatitude == null) {
            throw new ValidationException("location", "位置范围参数不能为空");
        }

        if (minLongitude > maxLongitude || minLatitude > maxLatitude) {
            throw new ValidationException("location", "位置范围参数无效");
        }

        return signalControllerStorage.values().stream()
                .filter(controller -> controller.getLongitude() != null && controller.getLatitude() != null)
                .filter(controller -> controller.getLongitude() >= minLongitude && controller.getLongitude() <= maxLongitude)
                .filter(controller -> controller.getLatitude() >= minLatitude && controller.getLatitude() <= maxLatitude)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getControlledCrosses(String signalControllerID) throws BusinessException {
        SignalController controller = findById(signalControllerID);
        return controller.getCrossIDList() != null ?
                new ArrayList<>(controller.getCrossIDList()) : new ArrayList<>();
    }

    @Override
    public void addControlledCross(String signalControllerID, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SignalController controller = findById(signalControllerID);
        if (controller.getCrossIDList() == null) {
            controller.setCrossIDList(new ArrayList<>());
        }

        if (!controller.getCrossIDList().contains(crossId)) {
            controller.getCrossIDList().add(crossId);
            signalControllerStorage.put(signalControllerID, controller);
            logger.info("添加路口到信号机控制列表: signalControllerID={}, crossId={}", signalControllerID, crossId);
        }
    }

    @Override
    public void removeControlledCross(String signalControllerID, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SignalController controller = findById(signalControllerID);
        if (controller.getCrossIDList() != null) {
            boolean removed = controller.getCrossIDList().remove(crossId);
            if (removed) {
                signalControllerStorage.put(signalControllerID, controller);
                logger.info("从信号机控制列表移除路口: signalControllerID={}, crossId={}", signalControllerID, crossId);
            }
        }
    }

    @Override
    public void setPrimaryCross(String signalControllerID, String primaryCrossId) throws BusinessException {
        if (primaryCrossId == null || primaryCrossId.trim().isEmpty()) {
            throw new ValidationException("primaryCrossId", "主路口编号不能为空");
        }

        SignalController controller = findById(signalControllerID);
        if (controller.getCrossIDList() == null) {
            controller.setCrossIDList(new ArrayList<>());
        }

        // 如果主路口不在列表中，先添加
        if (!controller.getCrossIDList().contains(primaryCrossId)) {
            controller.getCrossIDList().add(primaryCrossId);
        }

        // 将主路口移到列表首位
        controller.getCrossIDList().remove(primaryCrossId);
        controller.getCrossIDList().add(0, primaryCrossId);

        signalControllerStorage.put(signalControllerID, controller);
        logger.info("设置信号机主路口: signalControllerID={}, primaryCrossId={}", signalControllerID, primaryCrossId);
    }

    @Override
    public String getPrimaryCross(String signalControllerID) throws BusinessException {
        SignalController controller = findById(signalControllerID);
        if (controller.getCrossIDList() != null && !controller.getCrossIDList().isEmpty()) {
            return controller.getCrossIDList().get(0);
        }
        return null;
    }

    @Override
    public void updateNetworkConfig(String signalControllerID, String ip, String subMask,
                                    String gateway, Integer port) throws BusinessException {
        SignalController controller = findById(signalControllerID);

        controller.setIp(ip);
        controller.setSubMask(subMask);
        controller.setGateway(gateway);
        controller.setPort(port);

        validateSignalController(controller);
        signalControllerStorage.put(signalControllerID, controller);

        logger.info("更新信号机网络配置: signalControllerID={}, ip={}, port={}",
                signalControllerID, ip, port);
    }

    @Override
    public void updateLocation(String signalControllerID, Double longitude, Double latitude) throws BusinessException {
        if (longitude == null || latitude == null) {
            throw new ValidationException("location", "经纬度不能为空");
        }

        SignalController controller = findById(signalControllerID);
        controller.setLongitude(longitude);
        controller.setLatitude(latitude);

        validateSignalController(controller);
        signalControllerStorage.put(signalControllerID, controller);

        logger.info("更新信号机位置: signalControllerID={}, longitude={}, latitude={}",
                signalControllerID, longitude, latitude);
    }

    @Override
    public boolean hasDoorStatusDetection(String signalControllerID) throws BusinessException {
        SignalController controller = findById(signalControllerID);
        return controller.getHasDoorStatus() != null && controller.getHasDoorStatus() == 1;
    }

    @Override
    public void reportError(String signalControllerID, ControllerErrorType errorType, String errorDesc) throws BusinessException {
        if (errorType == null) {
            throw new ValidationException("errorType", "故障类型不能为空");
        }
        if (errorDesc == null || errorDesc.trim().isEmpty()) {
            throw new ValidationException("errorDesc", "故障描述不能为空");
        }

        // 确保信号机存在
        findById(signalControllerID);

        SignalControllerError error = new SignalControllerError();
        error.setSignalControllerId(signalControllerID);
        error.setErrorType(errorType);
        error.setErrorDesc(errorDesc);
        error.setOccurTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        errorStorage.computeIfAbsent(signalControllerID, k -> new ArrayList<>()).add(error);

        logger.warn("信号机故障报告: signalControllerID={}, errorType={}, desc={}",
                signalControllerID, errorType, errorDesc);
    }

    @Override
    public List<SignalControllerError> getErrors(String signalControllerID) throws BusinessException {
        // 确保信号机存在
        return dataProvider.getSignalControllerErrorsByControllerId(signalControllerID);
//        findById(signalControllerID);
//
//        return errorStorage.getOrDefault(signalControllerID, new ArrayList<>());
    }

    @Override
    public List<SignalControllerError> getAllErrors() throws BusinessException {
        return dataProvider.getAllSignalControllerErrors();
//        return errorStorage.values().stream()
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
    }

    @Override
    public void clearErrors(String signalControllerID) throws BusinessException {
        // 确保信号机存在
        findById(signalControllerID);

        errorStorage.remove(signalControllerID);
        logger.info("清除信号机故障记录: signalControllerID={}", signalControllerID);
    }

    @Override
    public boolean validateConfiguration(SignalController signalController) throws BusinessException {
        try {
            validateSignalController(signalController);
            return true;
        } catch (ValidationException e) {
            logger.warn("信号机配置验证失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean testConnection(String signalControllerID) throws BusinessException {
        SignalController controller = findById(signalControllerID);

        // 这里可以实现实际的连接测试逻辑
        // 目前只是模拟测试
        if (controller.getCommMode() == null) {
            return false;
        }

        if (controller.getCommMode() != CommMode.SERIAL &&
                (controller.getIp() == null || controller.getPort() == null)) {
            return false;
        }

        logger.info("测试信号机连接: signalControllerID={}, result=success", signalControllerID);
        return true;
    }

    /**
     * 验证信号机参数
     */
    private void validateSignalController(SignalController signalController) throws ValidationException {
        Set<ConstraintViolation<SignalController>> violations = validator.validate(signalController);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<SignalController> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new ValidationException("signalController", sb.toString());
        }

        // 额外的业务验证
        if (signalController.getCrossIDList() == null || signalController.getCrossIDList().isEmpty()) {
            throw new ValidationException("crossIDList", "控制路口列表不能为空");
        }

        // 验证网络配置的一致性
        if (signalController.getCommMode() != CommMode.SERIAL) {
            if (signalController.getIp() == null && signalController.getPort() != null) {
                throw new ValidationException("network", "指定端口时必须指定IP地址");
            }
        }
    }
}