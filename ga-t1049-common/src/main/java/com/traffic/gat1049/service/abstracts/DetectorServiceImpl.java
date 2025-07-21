package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.intersection.DetectorParam;
import com.traffic.gat1049.model.enums.DetectorPosition;
import com.traffic.gat1049.model.enums.DetectorType;
import com.traffic.gat1049.service.interfaces.DetectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 检测器服务实现
 */
@Service
public class DetectorServiceImpl implements DetectorService {

    private static final Logger logger = LoggerFactory.getLogger(DetectorServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 检测器存储 - 使用复合键：crossId + detectorNo
    private final Map<String, DetectorParam> detectorStorage = new ConcurrentHashMap<>();

    // 检测器在线状态存储
    private final Map<String, Boolean> detectorStatusStorage = new ConcurrentHashMap<>();

    public DetectorServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        initializeSampleData();
    }

    @Override
    public List<DetectorParam> findAll() throws BusinessException {

        return dataPrider.getAllDetectors();
    }

    @Override
    public List<DetectorParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataPrider.getDetectorsByCrossId(crossId);
//        return detectorStorage.values().stream()
//                .filter(detector -> crossId.equals(detector.getCrossId()))
//                .collect(Collectors.toList());
    }

    @Override
    public DetectorParam findByCrossIdAndDetectorNo(String crossId, Integer detectorNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (detectorNo == null) {
            throw new ValidationException("detectorNo", "检测器序号不能为空");
        }

        String key = buildKey(crossId, detectorNo);
        DetectorParam detector = dataPrider.getDetectorByCrossIdAndNo(crossId, detectorNo.toString());//detectorStorage.get(key);
        if (detector == null) {
            throw new DataNotFoundException("DetectorParam",
                    String.format("crossId=%s, detectorNo=%d", crossId, detectorNo));
        }

        return detector;
    }

    @Override
    public List<DetectorParam> findByType(String crossId, DetectorType type) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (type == null) {
            throw new ValidationException("type", "检测器类型不能为空");
        }

        return detectorStorage.values().stream()
                .filter(detector -> crossId.equals(detector.getCrossId()) &&
                        type.equals(detector.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetectorParam> findByPosition(String crossId, DetectorPosition position) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (position == null) {
            throw new ValidationException("position", "检测位置不能为空");
        }

        return detectorStorage.values().stream()
                .filter(detector -> crossId.equals(detector.getCrossId()) &&
                        position.equals(detector.getPosition()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetectorParam> findByLaneNo(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (laneNo == null) {
            throw new ValidationException("laneNo", "车道序号不能为空");
        }

        return detectorStorage.values().stream()
                .filter(detector -> crossId.equals(detector.getCrossId()) &&
                        detector.getLaneNoList() != null &&
                        detector.getLaneNoList().contains(laneNo))
                .collect(Collectors.toList());
    }

    @Override
    public DetectorParam save(DetectorParam detectorParam) throws BusinessException {
        if (detectorParam == null) {
            throw new ValidationException("detectorParam", "检测器参数不能为空");
        }

        validateDetectorParam(detectorParam);

        String key = buildKey(detectorParam.getCrossId(), detectorParam.getDetectorNo());

        // 检查是否已存在
        if (detectorStorage.containsKey(key)) {
            throw new BusinessException("DETECTOR_ALREADY_EXISTS",
                    String.format("检测器已存在: crossId=%s, detectorNo=%d",
                            detectorParam.getCrossId(), detectorParam.getDetectorNo()));
        }

//        detectorParam.setCreateTime(LocalDateTime.now());
//        detectorParam.setUpdateTime(LocalDateTime.now());

        detectorStorage.put(key, detectorParam);

        // 设置检测器为在线状态
        detectorStatusStorage.put(key, true);

        logger.info("保存检测器参数: crossId={}, detectorNo={}, type={}",
                detectorParam.getCrossId(), detectorParam.getDetectorNo(), detectorParam.getType());

        return detectorParam;
    }

    @Override
    public DetectorParam update(DetectorParam detectorParam) throws BusinessException {
        if (detectorParam == null) {
            throw new ValidationException("detectorParam", "检测器参数不能为空");
        }

        validateDetectorParam(detectorParam);

        String key = buildKey(detectorParam.getCrossId(), detectorParam.getDetectorNo());

        if (!detectorStorage.containsKey(key)) {
            throw new DataNotFoundException("DetectorParam",
                    String.format("crossId=%s, detectorNo=%d",
                            detectorParam.getCrossId(), detectorParam.getDetectorNo()));
        }

//        detectorParam.setUpdateTime(LocalDateTime.now());
        detectorStorage.put(key, detectorParam);

        logger.info("更新检测器参数: crossId={}, detectorNo={}, type={}",
                detectorParam.getCrossId(), detectorParam.getDetectorNo(), detectorParam.getType());

        return detectorParam;
    }

    @Override
    public void delete(String crossId, Integer detectorNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (detectorNo == null) {
            throw new ValidationException("detectorNo", "检测器序号不能为空");
        }

        String key = buildKey(crossId, detectorNo);
        DetectorParam removed = detectorStorage.remove(key);
        if (removed == null) {
            throw new DataNotFoundException("DetectorParam",
                    String.format("crossId=%s, detectorNo=%d", crossId, detectorNo));
        }

        // 同时删除状态
        detectorStatusStorage.remove(key);

        logger.info("删除检测器参数: crossId={}, detectorNo={}", crossId, detectorNo);
    }

    @Override
    public boolean isOnline(String crossId, Integer detectorNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (detectorNo == null) {
            throw new ValidationException("detectorNo", "检测器序号不能为空");
        }

        String key = buildKey(crossId, detectorNo);

        // 检查检测器是否存在
        if (!detectorStorage.containsKey(key)) {
            throw new DataNotFoundException("DetectorParam",
                    String.format("crossId=%s, detectorNo=%d", crossId, detectorNo));
        }

        return detectorStatusStorage.getOrDefault(key, false);
    }

    /**
     * 设置检测器在线状态
     *
     * @param crossId 路口编号
     * @param detectorNo 检测器序号
     * @param online 是否在线
     * @throws BusinessException 业务异常
     */
    public void setOnlineStatus(String crossId, Integer detectorNo, boolean online) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (detectorNo == null) {
            throw new ValidationException("detectorNo", "检测器序号不能为空");
        }

        String key = buildKey(crossId, detectorNo);

        // 检查检测器是否存在
        if (!detectorStorage.containsKey(key)) {
            throw new DataNotFoundException("DetectorParam",
                    String.format("crossId=%s, detectorNo=%d", crossId, detectorNo));
        }

        detectorStatusStorage.put(key, online);

        logger.info("设置检测器状态: crossId={}, detectorNo={}, online={}",
                crossId, detectorNo, online);
    }

    /**
     * 获取路口下所有检测器的在线数量
     *
     * @param crossId 路口编号
     * @return 在线检测器数量
     * @throws BusinessException 业务异常
     */
    public long getOnlineCount(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        return detectorStorage.values().stream()
                .filter(detector -> crossId.equals(detector.getCrossId()))
                .mapToLong(detector -> {
                    String key = buildKey(detector.getCrossId(), detector.getDetectorNo());
                    return detectorStatusStorage.getOrDefault(key, false) ? 1 : 0;
                })
                .sum();
    }

    /**
     * 构建存储键
     */
    private String buildKey(String crossId, Integer detectorNo) {
        return crossId + "_" + detectorNo;
    }

    /**
     * 验证检测器参数
     */
    private void validateDetectorParam(DetectorParam detectorParam) throws BusinessException {
        if (detectorParam.getCrossId() == null || detectorParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (detectorParam.getDetectorNo() == null) {
            throw new ValidationException("detectorNo", "检测器序号不能为空");
        }

        if (detectorParam.getDetectorNo() < 1 || detectorParam.getDetectorNo() > 999) {
            throw new ValidationException("detectorNo", "检测器序号必须在1-999之间");
        }

        if (detectorParam.getType() == null) {
            throw new ValidationException("type", "检测器类型不能为空");
        }

        if (detectorParam.getPosition() == null) {
            throw new ValidationException("position", "检测位置不能为空");
        }

        if (detectorParam.getTarget() == null || detectorParam.getTarget().trim().isEmpty()) {
            throw new ValidationException("target", "检测对象不能为空");
        }

        // 验证检测对象格式
        if (!detectorParam.getTarget().matches("[01]{3}")) {
            throw new ValidationException("target", "检测对象格式错误，应为3位0或1的组合");
        }

        if (detectorParam.getDistance() != null && detectorParam.getDistance() < 0) {
            throw new ValidationException("distance", "距停车线距离不能为负数");
        }
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        try {
            // 为路口1创建检测器
            DetectorParam detector1 = new DetectorParam();
            detector1.setCrossId("11010000100001");
            detector1.setDetectorNo(1);
            detector1.setType(DetectorType.COIL);
            detector1.setPosition(DetectorPosition.ENTRANCE);
            detector1.setTarget("100"); // 只检测机动车
            detector1.setDistance(300); // 距停车线3米
            detector1.setLaneNoList(Arrays.asList(1, 2));
            detector1.setPedestrianNoList(new ArrayList<>());

            DetectorParam detector2 = new DetectorParam();
            detector2.setCrossId("11010000100001");
            detector2.setDetectorNo(2);
            detector2.setType(DetectorType.VIDEO);
            detector2.setPosition(DetectorPosition.ENTRANCE);
            detector2.setTarget("111"); // 检测机动车、非机动车、行人
            detector2.setDistance(500); // 距停车线5米
            detector2.setLaneNoList(Arrays.asList(3, 4));
            detector2.setPedestrianNoList(Arrays.asList(1));

            DetectorParam detector3 = new DetectorParam();
            detector3.setCrossId("11010000100001");
            detector3.setDetectorNo(3);
            detector3.setType(DetectorType.MAGNETIC);
            detector3.setPosition(DetectorPosition.EXIT);
            detector3.setTarget("100"); // 只检测机动车
            detector3.setDistance(200); // 距停车线2米
            detector3.setLaneNoList(Arrays.asList(5, 6));
            detector3.setPedestrianNoList(new ArrayList<>());

            // 为路口2创建检测器
            DetectorParam detector4 = new DetectorParam();
            detector4.setCrossId("11010000100002");
            detector4.setDetectorNo(1);
            detector4.setType(DetectorType.MICROWAVE);
            detector4.setPosition(DetectorPosition.ENTRANCE);
            detector4.setTarget("101"); // 检测机动车和行人
            detector4.setDistance(400); // 距停车线4米
            detector4.setLaneNoList(Arrays.asList(1));
            detector4.setPedestrianNoList(Arrays.asList(1, 2));

            DetectorParam detector5 = new DetectorParam();
            detector5.setCrossId("11010000100002");
            detector5.setDetectorNo(2);
            detector5.setType(DetectorType.RFID);
            detector5.setPosition(DetectorPosition.OTHER);
            detector5.setTarget("100"); // 只检测机动车
            detector5.setDistance(1000); // 距停车线10米
            detector5.setLaneNoList(Arrays.asList(2, 3));
            detector5.setPedestrianNoList(new ArrayList<>());

            save(detector1);
            save(detector2);
            save(detector3);
            save(detector4);
            save(detector5);

            logger.info("示例检测器数据初始化完成");
        } catch (BusinessException e) {
            logger.error("示例检测器数据初始化失败", e);
        }
    }
}