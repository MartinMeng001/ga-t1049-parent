package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.enums.DoorStatus;
import com.traffic.gat1049.protocol.model.runtime.SCDoorStatus;
import com.traffic.gat1049.service.interfaces.DoorStatusService;
import com.traffic.gat1049.service.interfaces.SignalControllerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号机柜门状态服务实现
 */
@Service
public class DoorStatusServiceImpl implements DoorStatusService {

    private static final Logger logger = LoggerFactory.getLogger(DoorStatusServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 数据提供者
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 信号机服务，用于验证信号机是否存在
    private final SignalControllerService signalControllerService;

    // 机柜门状态存储 - key: signalControllerId, value: SCDoorStatus
    private final Map<String, SCDoorStatus> doorStatusStorage = new ConcurrentHashMap<>();

    public DoorStatusServiceImpl(SignalControllerService signalControllerService) throws BusinessException {
        this.signalControllerService = signalControllerService;
        initializeSampleData();
    }

    @Override
    public SCDoorStatus getDoorStatus(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机设备编号不能为空");
        }

        SCDoorStatus doorStatus = doorStatusStorage.get(signalControllerId);
        if (doorStatus == null) {
            // 如果信号机存在但门状态不存在，创建默认状态
            if (isSignalControllerExists(signalControllerId)) {
                initializeDoorStatus(signalControllerId, 4); // 默认4个门
                doorStatus = doorStatusStorage.get(signalControllerId);
            } else {
                throw new DataNotFoundException("SCDoorStatus", signalControllerId);
            }
        }

        return doorStatus;
    }

    @Override
    public List<SCDoorStatus> getAllDoorStatus() throws BusinessException {
        return new ArrayList<>(doorStatusStorage.values());
    }

    @Override
    public void updateDoorStatus(String signalControllerId, Integer doorNo, DoorStatus status) throws BusinessException {
        validateUpdateParameters(signalControllerId, doorNo, status);

        SCDoorStatus scDoorStatus = getDoorStatus(signalControllerId);

        // 查找并更新指定门的状态
        boolean doorFound = false;
        for (SCDoorStatus.DoorStatusItem item : scDoorStatus.getDoorStatusList().getDoorStatus()) {
            if (item.getDoorNo().equals(doorNo)) {
                item.setStatus(status);
                doorFound = true;
                break;
            }
        }

        if (!doorFound) {
            throw new DataNotFoundException("DoorNo", doorNo.toString());
        }

        // 更新时间
        scDoorStatus.setTime(LocalDateTime.now().format(TIME_FORMATTER));

        doorStatusStorage.put(signalControllerId, scDoorStatus);

        logger.info("更新机柜门状态: signalControllerId={}, doorNo={}, status={}",
                signalControllerId, doorNo, status.getDescription());
    }

    @Override
    public void updateDoorStatus(SCDoorStatus scDoorStatus) throws BusinessException {
        if (scDoorStatus == null) {
            throw new ValidationException("scDoorStatus", "机柜门状态对象不能为空");
        }

        validateSCDoorStatus(scDoorStatus);

        // 验证信号机是否存在
        if (!isSignalControllerExists(scDoorStatus.getSignalControllerId())) {
            throw new DataNotFoundException("SignalController", scDoorStatus.getSignalControllerId());
        }

        // 更新时间为当前时间
        scDoorStatus.setTime(LocalDateTime.now().format(TIME_FORMATTER));

        doorStatusStorage.put(scDoorStatus.getSignalControllerId(), scDoorStatus);

        logger.info("批量更新机柜门状态: signalControllerId={}, doorCount={}",
                scDoorStatus.getSignalControllerId(),
                scDoorStatus.getDoorStatusList().getDoorStatus().size());
    }

    @Override
    public DoorStatus getSingleDoorStatus(String signalControllerId, Integer doorNo) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机设备编号不能为空");
        }
        if (doorNo == null || doorNo < 1 || doorNo > 20) {
            throw new ValidationException("doorNo", "机柜门序号必须在1-20之间");
        }

        SCDoorStatus scDoorStatus = getDoorStatus(signalControllerId);

        for (SCDoorStatus.DoorStatusItem item : scDoorStatus.getDoorStatusList().getDoorStatus()) {
            if (item.getDoorNo().equals(doorNo)) {
                return item.getStatus();
            }
        }

        throw new DataNotFoundException("DoorNo", doorNo.toString());
    }

    @Override
    public boolean isSignalControllerExists(String signalControllerId) throws BusinessException {
        try {
            return signalControllerService.existsById(signalControllerId);
        } catch (BusinessException e) {
            logger.warn("检查信号机是否存在时发生异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int getDoorCount(String signalControllerId) throws BusinessException {
        SCDoorStatus doorStatus = getDoorStatus(signalControllerId);
        return doorStatus.getDoorStatusList().getDoorStatus().size();
    }

    @Override
    public void initializeDoorStatus(String signalControllerId, int doorCount) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机设备编号不能为空");
        }
        if (doorCount < 1 || doorCount > 20) {
            throw new ValidationException("doorCount", "机柜门数量必须在1-20之间");
        }

        List<SCDoorStatus.DoorStatusItem> doorItems = new ArrayList<>();
        for (int i = 1; i <= doorCount; i++) {
            SCDoorStatus.DoorStatusItem item = new SCDoorStatus.DoorStatusItem(
                    i,
                    "机柜门" + i,
                    DoorStatus.CLOSED  // 默认关闭状态
            );
            doorItems.add(item);
        }

        SCDoorStatus.DoorStatusList doorStatusList = new SCDoorStatus.DoorStatusList(doorItems);
        SCDoorStatus scDoorStatus = new SCDoorStatus(
                signalControllerId,
                LocalDateTime.now().format(TIME_FORMATTER),
                doorStatusList
        );

        doorStatusStorage.put(signalControllerId, scDoorStatus);

        logger.info("初始化机柜门状态: signalControllerId={}, doorCount={}", signalControllerId, doorCount);
    }

    @Override
    public void deleteDoorStatus(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机设备编号不能为空");
        }

        SCDoorStatus removed = doorStatusStorage.remove(signalControllerId);
        if (removed == null) {
            throw new DataNotFoundException("SCDoorStatus", signalControllerId);
        }

        logger.info("删除机柜门状态: signalControllerId={}", signalControllerId);
    }

    // 私有方法

    private void validateUpdateParameters(String signalControllerId, Integer doorNo, DoorStatus status)
            throws ValidationException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机设备编号不能为空");
        }
        if (doorNo == null || doorNo < 1 || doorNo > 20) {
            throw new ValidationException("doorNo", "机柜门序号必须在1-20之间");
        }
        if (status == null) {
            throw new ValidationException("status", "机柜门状态不能为空");
        }
    }

    private void validateSCDoorStatus(SCDoorStatus scDoorStatus) throws ValidationException {
        if (scDoorStatus.getSignalControllerId() == null || scDoorStatus.getSignalControllerId().trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机设备编号不能为空");
        }

        if (scDoorStatus.getDoorStatusList() == null ||
                scDoorStatus.getDoorStatusList().getDoorStatus() == null ||
                scDoorStatus.getDoorStatusList().getDoorStatus().isEmpty()) {
            throw new ValidationException("doorStatusList", "机柜门状态列表不能为空");
        }

        // 验证每个门状态项
        Set<Integer> doorNos = new HashSet<>();
        for (SCDoorStatus.DoorStatusItem item : scDoorStatus.getDoorStatusList().getDoorStatus()) {
            if (item.getDoorNo() == null || item.getDoorNo() < 1 || item.getDoorNo() > 20) {
                throw new ValidationException("doorNo", "机柜门序号必须在1-20之间");
            }

            if (doorNos.contains(item.getDoorNo())) {
                throw new ValidationException("doorNo", "机柜门序号不能重复: " + item.getDoorNo());
            }
            doorNos.add(item.getDoorNo());

            if (item.getDoorName() != null && item.getDoorName().length() > 50) {
                throw new ValidationException("doorName", "机柜门名称最大长度为50");
            }

            if (item.getStatus() == null) {
                throw new ValidationException("status", "机柜门状态不能为空");
            }
        }
    }

    private void initializeSampleData() throws BusinessException {
        // 初始化示例数据
        try {
            // 为示例信号机创建门状态
            String[] sampleControllerIds = {"11010140000111001", "11010140000111002"};

            for (String controllerId : sampleControllerIds) {
                if (isSignalControllerExists(controllerId)) {
                    initializeDoorStatus(controllerId, 4);

                    // 设置一些示例状态
                    updateDoorStatus(controllerId, 1, DoorStatus.CLOSED);
                    updateDoorStatus(controllerId, 2, DoorStatus.OPEN);
                    updateDoorStatus(controllerId, 3, DoorStatus.CLOSED);
                    updateDoorStatus(controllerId, 4, DoorStatus.UNKNOWN);
                }
            }
        } catch (Exception e) {
            logger.warn("初始化示例数据失败: {}", e.getMessage());
        }
    }
}