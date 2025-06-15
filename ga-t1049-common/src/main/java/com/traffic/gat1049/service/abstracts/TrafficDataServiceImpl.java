package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.TrafficDataQueryDto;
import com.traffic.gat1049.model.vo.TrafficStatisticsVo;
import com.traffic.gat1049.protocol.model.runtime.CrossCycle;
import com.traffic.gat1049.protocol.model.runtime.CrossStage;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;
import com.traffic.gat1049.service.interfaces.TrafficDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 交通数据服务实现
 */
public class TrafficDataServiceImpl implements TrafficDataService {

    private static final Logger logger = LoggerFactory.getLogger(TrafficDataServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 内存存储，实际项目中应该从数据库获取
    private final Map<String, List<CrossTrafficData>> crossTrafficDataStorage = new ConcurrentHashMap<>();
    private final Map<String, List<StageTrafficData>> stageTrafficDataStorage = new ConcurrentHashMap<>();
    private final Map<String, CrossCycle> crossCycleStorage = new ConcurrentHashMap<>();

    public TrafficDataServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public CrossTrafficData getCrossTrafficData(String crossId, LocalDateTime endTime, Integer interval) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (endTime == null) {
            throw new ValidationException("endTime", "统计截止时间不能为空");
        }
        if (interval == null || interval <= 0) {
            throw new ValidationException("interval", "时间间隔必须大于0");
        }

        // 先从数据提供者获取
        try {
            CrossTrafficData trafficData = dataProvider.getCrossTrafficData(crossId);
            if (trafficData != null) {
                return trafficData;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取交通数据失败，使用本地存储", e);
        }

        List<CrossTrafficData> trafficDataList = crossTrafficDataStorage.get(crossId);
        if (trafficDataList == null || trafficDataList.isEmpty()) {
            throw new DataNotFoundException("CrossTrafficData", crossId);
        }

        // 查找最接近指定时间的数据
        CrossTrafficData result = trafficDataList.stream()
                .filter(data -> data.getEndTime() != null && !data.getEndTime().isAfter(endTime))
                .max(Comparator.comparing(CrossTrafficData::getEndTime))
                .orElse(null);

        if (result == null) {
            throw new DataNotFoundException("CrossTrafficData",
                    "crossId=" + crossId + ", endTime=" + endTime);
        }

        return result;
    }

    @Override
    public List<TrafficStatisticsVo> getTrafficStatistics(TrafficDataQueryDto queryDto) throws BusinessException {
        if (queryDto == null) {
            throw new ValidationException("queryDto", "查询条件不能为空");
        }

        List<TrafficStatisticsVo> statistics = new ArrayList<>();

        // 根据查询条件获取数据
        for (Map.Entry<String, List<CrossTrafficData>> entry : crossTrafficDataStorage.entrySet()) {
            String crossId = entry.getKey();

            // 如果指定了路口ID，则过滤
            if (queryDto.getCrossId() != null && !queryDto.getCrossId().equals(crossId)) {
                continue;
            }

            List<CrossTrafficData> dataList = entry.getValue();
            for (CrossTrafficData data : dataList) {
                // 时间范围过滤
                if (queryDto.getStartTime() != null && data.getEndTime().isBefore(queryDto.getStartTime())) {
                    continue;
                }
                if (queryDto.getEndTime() != null && data.getEndTime().isAfter(queryDto.getEndTime())) {
                    continue;
                }

                TrafficStatisticsVo vo = new TrafficStatisticsVo();
                vo.setCrossId(crossId);
                vo.setStatTime(data.getEndTime());
                vo.setTotalVolume(data.getVehicleFlow());
                vo.setAverageSpeed(data.getAverageSpeed());
                vo.setOccupancy(data.getOccupancy());
                statistics.add(vo);
            }
        }

        return statistics;
    }

    @Override
    public StageTrafficData getStageTrafficData(String crossId, Integer stageNo, LocalDateTime startTime, LocalDateTime endTime) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (stageNo == null) {
            throw new ValidationException("stageNo", "阶段号不能为空");
        }
        if (startTime == null) {
            throw new ValidationException("startTime", "开始时间不能为空");
        }
        if (endTime == null) {
            throw new ValidationException("endTime", "结束时间不能为空");
        }

        String key = crossId + "_" + stageNo;
        List<StageTrafficData> stageDataList = stageTrafficDataStorage.get(key);

        if (stageDataList == null || stageDataList.isEmpty()) {
            throw new DataNotFoundException("StageTrafficData", key);
        }

        // 查找时间范围内的数据
        StageTrafficData result = stageDataList.stream()
                .filter(data -> data.getStartTime() != null && data.getEndTime() != null &&
                        !data.getStartTime().isAfter(endTime) && !data.getEndTime().isBefore(startTime))
                .findFirst()
                .orElse(null);

        if (result == null) {
            throw new DataNotFoundException("StageTrafficData",
                    "crossId=" + crossId + ", stageNo=" + stageNo + ", timeRange=" + startTime + "~" + endTime);
        }

        return result;
    }

    @Override
    public void saveCrossTrafficData(CrossTrafficData crossTrafficData) throws BusinessException {
        if (crossTrafficData == null) {
            throw new ValidationException("crossTrafficData", "路口交通流数据不能为空");
        }
        if (crossTrafficData.getCrossId() == null || crossTrafficData.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        String crossId = crossTrafficData.getCrossId();
        List<CrossTrafficData> dataList = crossTrafficDataStorage.computeIfAbsent(crossId, k -> new ArrayList<>());

        crossTrafficData.setCreateTime(LocalDateTime.now());
        dataList.add(crossTrafficData);

        // 保持数据量在合理范围内（最多保留1000条）
        if (dataList.size() > 1000) {
            dataList.sort(Comparator.comparing(CrossTrafficData::getEndTime));
            dataList.subList(0, dataList.size() - 1000).clear();
        }

        logger.info("保存路口交通流数据: crossId={}, endTime={}", crossId, crossTrafficData.getEndTime());
    }

    @Override
    public void saveStageTrafficData(StageTrafficData stageTrafficData) throws BusinessException {
        if (stageTrafficData == null) {
            throw new ValidationException("stageTrafficData", "阶段交通流数据不能为空");
        }
        if (stageTrafficData.getCrossId() == null || stageTrafficData.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (stageTrafficData.getStageNo() == null) {
            throw new ValidationException("stageNo", "阶段号不能为空");
        }

        String key = stageTrafficData.getCrossId() + "_" + stageTrafficData.getStageNo();
        List<StageTrafficData> dataList = stageTrafficDataStorage.computeIfAbsent(key, k -> new ArrayList<>());

        stageTrafficData.setCreateTime(LocalDateTime.now());
        dataList.add(stageTrafficData);

        // 保持数据量在合理范围内（最多保留500条）
        if (dataList.size() > 500) {
            dataList.sort(Comparator.comparing(StageTrafficData::getStartTime));
            dataList.subList(0, dataList.size() - 500).clear();
        }

        logger.info("保存阶段交通流数据: crossId={}, stageNo={}, startTime={}",
                stageTrafficData.getCrossId(), stageTrafficData.getStageNo(), stageTrafficData.getStartTime());
    }

    @Override
    public CrossCycle getCrossCycle(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossCycle cycle = crossCycleStorage.get(crossId);
        if (cycle == null) {
            throw new DataNotFoundException("CrossCycle", crossId);
        }

        return cycle;
    }

    @Override
    public CrossStage getCrossStage(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        // 从数据提供者获取
        try {
            CrossStage stage = dataProvider.getCrossStage(crossId);
            if (stage != null) {
                return stage;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取路口阶段失败", e);
        }

        throw new DataNotFoundException("CrossStage", crossId);
    }

    private void initializeSampleData() {
        try {
            // 初始化示例交通数据
            LocalDateTime now = LocalDateTime.now();

            // 路口1的交通数据
            List<CrossTrafficData> cross1Data = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                CrossTrafficData data = new CrossTrafficData();
                data.setCrossId("11010000100001");
                data.setEndTime(now.minusHours(23 - i));
                data.setInterval(3600); // 1小时间隔
                data.setVehicleFlow(100 + (int)(Math.random() * 200)); // 100-300车次
                data.setAverageSpeed(30.0 + Math.random() * 20); // 30-50km/h
                data.setOccupancy(0.3 + Math.random() * 0.4); // 30%-70%占有率
                data.setCreateTime(LocalDateTime.now());
                cross1Data.add(data);
            }
            crossTrafficDataStorage.put("11010000100001", cross1Data);

            // 路口2的交通数据
            List<CrossTrafficData> cross2Data = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                CrossTrafficData data = new CrossTrafficData();
                data.setCrossId("11010000100002");
                data.setEndTime(now.minusHours(23 - i));
                data.setInterval(3600);
                data.setVehicleFlow(80 + (int)(Math.random() * 150));
                data.setAverageSpeed(25.0 + Math.random() * 25);
                data.setOccupancy(0.2 + Math.random() * 0.5);
                data.setCreateTime(LocalDateTime.now());
                cross2Data.add(data);
            }
            crossTrafficDataStorage.put("11010000100002", cross2Data);

            // 初始化周期数据
            CrossCycle cycle1 = new CrossCycle();
            cycle1.setCrossId("11010000100001");
            cycle1.setCycleTime(120); // 2分钟周期
            cycle1.setStartTime(now.minusMinutes(2));
            cycle1.setCreateTime(LocalDateTime.now());
            crossCycleStorage.put("11010000100001", cycle1);

            CrossCycle cycle2 = new CrossCycle();
            cycle2.setCrossId("11010000100002");
            cycle2.setCycleTime(150); // 2.5分钟周期
            cycle2.setStartTime(now.minusMinutes(3));
            cycle2.setCreateTime(LocalDateTime.now());
            crossCycleStorage.put("11010000100002", cycle2);

            logger.info("初始化交通数据示例完成，路口数据: {}, 周期数据: {}",
                    crossTrafficDataStorage.size(), crossCycleStorage.size());
        } catch (Exception e) {
            logger.error("初始化交通数据示例失败", e);
        }
    }
}