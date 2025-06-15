package com.traffic.gat1049.service.abstracts;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.DetectorParam;
import com.traffic.gat1049.service.interfaces.DetectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 检测器服务实现
 */
public class DetectorServiceImpl implements DetectorService {

    private static final Logger logger = LoggerFactory.getLogger(DetectorServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    private final Map<String, DetectorParam> detectorStorage = new ConcurrentHashMap<>();

    public DetectorServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public DetectorParam findById(String detectorId) throws BusinessException {
        if (detectorId == null || detectorId.trim().isEmpty()) {
            throw new ValidationException("detectorId", "检测器编号不能为空");
        }

        try {
            List<DetectorParam> detectors = dataProvider.getDetectorParams();
            if (detectors != null) {
                for (DetectorParam detector : detectors) {
                    if (detectorId.equals(detector.getDetectorId())) {
                        return detector;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取检测器数据失败，使用本地存储", e);
        }

        DetectorParam detector = detectorStorage.get(detectorId);
        if (detector == null) {
            throw new DataNotFoundException("DetectorParam", detectorId);
        }
        return detector;
    }

    @Override
    public List<DetectorParam> findAll() throws BusinessException {
        try {
            List<DetectorParam> detectors = dataProvider.getDetectorParams();
            if (detectors != null && !detectors.isEmpty()) {
                return detectors;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取检测器列表失败，使用本地存储", e);
        }
        return new ArrayList<>(detectorStorage.values());
    }

    @Override
    public List<DetectorParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<DetectorParam> allDetectors = findAll();
        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allDetectors.size());
        if (startIndex >= allDetectors.size()) {
            return new ArrayList<>();
        }
        return allDetectors.subList(startIndex, endIndex);
    }

    @Override
    public DetectorParam save(DetectorParam detectorParam) throws BusinessException {
        if (detectorParam == null) {
            throw new ValidationException("detectorParam", "检测器参数不能为空");
        }
        detectorParam.setUpdateTime(LocalDateTime.now());
        if (detectorParam.getCreateTime() == null) {
            detectorParam.setCreateTime(LocalDateTime.now());
        }
        detectorStorage.put(detectorParam.getDetectorId(), detectorParam);
        logger.info("保存检测器参数: {}", detectorParam.getDetectorId());
        return detectorParam;
    }

    @Override
    public void deleteById(String detectorId) throws BusinessException {
        if (detectorId == null || detectorId.trim().isEmpty()) {
            throw new ValidationException("detectorId", "检测器编号不能为空");
        }
        DetectorParam removed = detectorStorage.remove(detectorId);
        if (removed == null) {
            throw new DataNotFoundException("DetectorParam", detectorId);
        }
        logger.info("删除检测器参数: {}", detectorId);
    }

    @Override
    public List<DetectorParam> findByLaneId(String laneId) throws BusinessException {
        if (laneId == null || laneId.trim().isEmpty()) {
            throw new ValidationException("laneId", "车道编号不能为空");
        }
        return findAll().stream()
                .filter(detector -> laneId.equals(detector.getLaneId()))
                .collect(Collectors.toList());
    }

    private void initializeSampleData() {
        try {
            DetectorParam detector1 = new DetectorParam();
            detector1.setCrossId("11010000100001001");
            //detector1.setLaneId("1101000010000101");
            detector1.setDetectorNo(1);
            detector1.setCreateTime(LocalDateTime.now());
            detectorStorage.put(detector1.getDetectorId(), detector1);

            DetectorParam detector2 = new DetectorParam();
            detector2.setCrossId("11010000100001002");
            //detector2.setLaneId("1101000010000102");
            detector2.setDetectorNo(2);
            detector2.setCreateTime(LocalDateTime.now());
            detectorStorage.put(detector2.getCrossId(), detector2);

            logger.info("初始化检测器示例数据完成，共 {} 个检测器", detectorStorage.size());
        } catch (Exception e) {
            logger.error("初始化检测器示例数据失败", e);
        }
    }
}
