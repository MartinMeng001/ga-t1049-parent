package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.TrafficDataQueryDto;
import com.traffic.gat1049.model.vo.LaneStatisticsVo;
import com.traffic.gat1049.model.vo.TrafficStatisticsVo;
import com.traffic.gat1049.protocol.model.runtime.CrossCycle;
import com.traffic.gat1049.protocol.model.runtime.CrossStage;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.LaneTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficFlowData;
import com.traffic.gat1049.model.enums.ReportDataType;
import com.traffic.gat1049.service.interfaces.TrafficDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 交通数据服务实现
 */
public class TrafficDataServiceImpl implements TrafficDataService {

    private static final Logger logger = LoggerFactory.getLogger(TrafficDataServiceImpl.class);

    // 路口交通流数据存储
    private final Map<String, List<CrossTrafficData>> crossTrafficDataStorage = new ConcurrentHashMap<>();

    // 阶段交通流数据存储
    private final Map<String, List<StageTrafficData>> stageTrafficDataStorage = new ConcurrentHashMap<>();

    // 路口周期存储
    private final Map<String, CrossCycle> crossCycleStorage = new ConcurrentHashMap<>();

    // 路口阶段存储
    private final Map<String, CrossStage> crossStageStorage = new ConcurrentHashMap<>();

    // 数据上传控制状态
    private final Map<String, Map<String, Boolean>> dataReportStatus = new ConcurrentHashMap<>();

    public TrafficDataServiceImpl() {
        // 初始化示例数据
        initializeSampleData();
    }

    @Override
    public CrossTrafficData getCrossTrafficData(String crossId, LocalDateTime endTime, Integer interval) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        if (interval == null) {
            interval = 300; // 默认5分钟
        }

        List<CrossTrafficData> dataList = crossTrafficDataStorage.get(crossId);
        if (dataList == null || dataList.isEmpty()) {
            throw new DataNotFoundException("CrossTrafficData", crossId);
        }

        // 查找最接近endTime的数据
        LocalDateTime finalEndTime = endTime;
        return dataList.stream()
                .filter(data -> data.getEndTime() != null)
                .min(Comparator.comparing(data ->
                        Math.abs(data.getEndTime().toEpochSecond(java.time.ZoneOffset.UTC) -
                                finalEndTime.toEpochSecond(java.time.ZoneOffset.UTC))))
                .orElseThrow(() -> new DataNotFoundException("CrossTrafficData", crossId));
    }

    @Override
    public List<TrafficStatisticsVo> getTrafficStatistics(TrafficDataQueryDto queryDto) throws BusinessException {
        if (queryDto == null) {
            throw new ValidationException("queryDto", "查询条件不能为空");
        }

        if (queryDto.getCrossId() == null || queryDto.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        List<CrossTrafficData> dataList = crossTrafficDataStorage.get(queryDto.getCrossId());
        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据时间范围过滤数据
        List<CrossTrafficData> filteredData = dataList.stream()
                .filter(data -> filterByTimeRange(data, queryDto.getStartTime(), queryDto.getEndTime()))
                .collect(Collectors.toList());

        // 转换为统计VO
        return filteredData.stream()
                .map(this::convertToStatisticsVo)
                .collect(Collectors.toList());
    }

    @Override
    public StageTrafficData getStageTrafficData(String crossId, Integer stageNo, LocalDateTime startTime, LocalDateTime endTime) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (stageNo == null) {
            throw new ValidationException("stageNo", "阶段号不能为空");
        }

        List<StageTrafficData> dataList = stageTrafficDataStorage.get(crossId);
        if (dataList == null || dataList.isEmpty()) {
            throw new DataNotFoundException("StageTrafficData", crossId + "-" + stageNo);
        }

        // 查找指定阶段和时间范围的数据
        return dataList.stream()
                .filter(data -> stageNo.equals(data.getStageNo()))
                .filter(data -> filterStageByTimeRange(data, startTime, endTime))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("StageTrafficData", crossId + "-" + stageNo));
    }

    @Override
    public void saveCrossTrafficData(CrossTrafficData crossTrafficData) throws BusinessException {
        if (crossTrafficData == null) {
            throw new ValidationException("crossTrafficData", "路口交通流数据不能为空");
        }

        validateCrossTrafficData(crossTrafficData);

        String crossId = crossTrafficData.getCrossId();
        crossTrafficDataStorage.computeIfAbsent(crossId, k -> new ArrayList<>()).add(crossTrafficData);

        logger.info("保存路口交通流数据: crossId={}, endTime={}, dataCount={}",
                crossId, crossTrafficData.getEndTime(), crossTrafficData.getDataList().size());
    }

    @Override
    public void saveStageTrafficData(StageTrafficData stageTrafficData) throws BusinessException {
        if (stageTrafficData == null) {
            throw new ValidationException("stageTrafficData", "阶段交通流数据不能为空");
        }

        validateStageTrafficData(stageTrafficData);

        String crossId = stageTrafficData.getCrossId();
        stageTrafficDataStorage.computeIfAbsent(crossId, k -> new ArrayList<>()).add(stageTrafficData);

        logger.info("保存阶段交通流数据: crossId={}, stageNo={}, startTime={}",
                crossId, stageTrafficData.getStageNo(), stageTrafficData.getStartTime());
    }

    @Override
    public CrossCycle getCrossCycle(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossCycle crossCycle = crossCycleStorage.get(crossId);
        if (crossCycle == null) {
            throw new DataNotFoundException("CrossCycle", crossId);
        }

        return crossCycle;
    }

    @Override
    public void updateCrossCycle(CrossCycle crossCycle) throws BusinessException {
        if (crossCycle == null) {
            throw new ValidationException("crossCycle", "路口周期不能为空");
        }

        if (crossCycle.getCrossId() == null || crossCycle.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        crossCycleStorage.put(crossCycle.getCrossId(), crossCycle);

        logger.info("更新路口周期: crossId={}, cycleLen={}",
                crossCycle.getCrossId(), crossCycle.getLastCycleLen());
    }

    @Override
    public CrossStage getCrossStage(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossStage crossStage = crossStageStorage.get(crossId);
        if (crossStage == null) {
            throw new DataNotFoundException("CrossStage", crossId);
        }

        return crossStage;
    }

    @Override
    public void updateCrossStage(CrossStage crossStage) throws BusinessException {
        if (crossStage == null) {
            throw new ValidationException("crossStage", "路口阶段不能为空");
        }

        if (crossStage.getCrossId() == null || crossStage.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        crossStageStorage.put(crossStage.getCrossId(), crossStage);

        logger.info("更新路口阶段: crossId={}, curStageNo={}, curStageLen={}",
                crossStage.getCrossId(), crossStage.getCurStageNo(), crossStage.getCurStageLen());
    }

    @Override
    public void controlDataReport(String command, String dataType, List<String> crossIds) throws BusinessException {
        if (command == null || command.trim().isEmpty()) {
            throw new ValidationException("command", "命令不能为空");
        }

        if (dataType == null || dataType.trim().isEmpty()) {
            throw new ValidationException("dataType", "数据类型不能为空");
        }

        if (crossIds == null || crossIds.isEmpty()) {
            throw new ValidationException("crossIds", "路口编号列表不能为空");
        }

        // 验证数据类型
        try {
            ReportDataType.fromCode(dataType);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("dataType", "不支持的数据类型: " + dataType);
        }

        boolean isStart = "Start".equalsIgnoreCase(command);

        for (String crossId : crossIds) {
            Map<String, Boolean> crossStatus = dataReportStatus.computeIfAbsent(crossId, k -> new ConcurrentHashMap<>());
            crossStatus.put(dataType, isStart);
        }

        logger.info("控制数据上传: command={}, dataType={}, crossIds={}",
                command, dataType, crossIds);
    }

    @Override
    public void cleanHistoryData(LocalDateTime beforeDate) throws BusinessException {
        if (beforeDate == null) {
            throw new ValidationException("beforeDate", "清理日期不能为空");
        }

        int cleanedCount = 0;

        // 清理路口交通流数据
        for (Map.Entry<String, List<CrossTrafficData>> entry : crossTrafficDataStorage.entrySet()) {
            List<CrossTrafficData> dataList = entry.getValue();
            int originalSize = dataList.size();
            dataList.removeIf(data -> data.getEndTime() != null && data.getEndTime().isBefore(beforeDate));
            cleanedCount += originalSize - dataList.size();
        }

        // 清理阶段交通流数据
        for (Map.Entry<String, List<StageTrafficData>> entry : stageTrafficDataStorage.entrySet()) {
            List<StageTrafficData> dataList = entry.getValue();
            int originalSize = dataList.size();
            dataList.removeIf(data -> data.getEndTime() != null && data.getEndTime().isBefore(beforeDate));
            cleanedCount += originalSize - dataList.size();
        }

        logger.info("清理历史数据完成: beforeDate={}, cleanedCount={}", beforeDate, cleanedCount);
    }

    // 私有辅助方法

    private void validateCrossTrafficData(CrossTrafficData crossTrafficData) throws BusinessException {
        if (crossTrafficData.getCrossId() == null || crossTrafficData.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (crossTrafficData.getEndTime() == null) {
            throw new ValidationException("endTime", "统计截止时间不能为空");
        }

        if (crossTrafficData.getDataList() == null || crossTrafficData.getDataList().isEmpty()) {
            throw new ValidationException("dataList", "车道交通流量数据列表不能为空");
        }
    }

    private void validateStageTrafficData(StageTrafficData stageTrafficData) throws BusinessException {
        if (stageTrafficData.getCrossId() == null || stageTrafficData.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (stageTrafficData.getStageNo() == null) {
            throw new ValidationException("stageNo", "阶段号不能为空");
        }

        if (stageTrafficData.getStartTime() == null) {
            throw new ValidationException("startTime", "阶段开始时间不能为空");
        }

        if (stageTrafficData.getDataList() == null || stageTrafficData.getDataList().isEmpty()) {
            throw new ValidationException("dataList", "车道交通流量数据列表不能为空");
        }
    }

    private boolean filterByTimeRange(CrossTrafficData data, LocalDateTime startTime, LocalDateTime endTime) {
        if (data.getEndTime() == null) {
            return false;
        }

        if (startTime != null && data.getEndTime().isBefore(startTime)) {
            return false;
        }

        if (endTime != null && data.getEndTime().isAfter(endTime)) {
            return false;
        }

        return true;
    }

    private boolean filterStageByTimeRange(StageTrafficData data, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && data.getEndTime() != null && data.getEndTime().isBefore(startTime)) {
            return false;
        }

        if (endTime != null && data.getStartTime() != null && data.getStartTime().isAfter(endTime)) {
            return false;
        }

        return true;
    }

    private TrafficStatisticsVo convertToStatisticsVo(CrossTrafficData crossTrafficData) {
        TrafficStatisticsVo vo = new TrafficStatisticsVo();
        vo.setCrossId(crossTrafficData.getCrossId());
        vo.setStatisticsTime(crossTrafficData.getEndTime());

        // 计算总流量和平均值
        List<LaneTrafficData> dataList = crossTrafficData.getDataList();
        if (dataList != null && !dataList.isEmpty()) {
            int totalVolume = dataList.stream().mapToInt(data -> data.getVolume() != null ? data.getVolume() : 0).sum();
            double avgSpeed = dataList.stream().mapToDouble(data -> data.getSpeed() != null ? data.getSpeed().doubleValue() : 0.0).average().orElse(0.0);
            double avgOccupancy = dataList.stream().mapToDouble(data -> data.getOccupancy() != null ? data.getOccupancy() : 0.0).average().orElse(0.0);
            double avgSaturation = dataList.stream().mapToDouble(data -> data.getSaturation() != null ? data.getSaturation().doubleValue() : 0.0).average().orElse(0.0);

            vo.setTotalVolume(totalVolume);
            vo.setAvgSpeed(avgSpeed);
            vo.setAvgOccupancy(avgOccupancy);
            vo.setAvgSaturation(avgSaturation);

            // 转换车道统计详情
            List<LaneStatisticsVo> laneStatistics = dataList.stream()
                    .map(this::convertToLaneStatisticsVo)
                    .collect(Collectors.toList());
            vo.setLaneStatistics(laneStatistics);
        }

        return vo;
    }

    private LaneStatisticsVo convertToLaneStatisticsVo(LaneTrafficData laneData) {
        LaneStatisticsVo laneVo = new LaneStatisticsVo();
        laneVo.setLaneNo(laneData.getLaneNo());
        laneVo.setVolume(laneData.getVolume());
//        laneVo.setSpeed(laneData.getSpeed());
//        laneVo.setOccupancy(laneData.getOccupancy());
//        laneVo.setSaturation(laneData.getSaturation());
//        laneVo.setQueueLength(laneData.getQueueLength());
//        laneVo.setPcu(laneData.getPcu());
//        laneVo.setDensity(laneData.getDensity());
//        laneVo.setHeadTime(laneData.getHeadTime());
//        laneVo.setHeadDistance(laneData.getHeadDistance());
//        laneVo.setAvgVehLen(laneData.getAvgVehLen());
        return laneVo;
    }

    private void initializeSampleData() {
//        try {
//            // 创建示例路口交通流数据
//            String crossId1 = "11010000100001";
//            CrossTrafficData trafficData1 = new CrossTrafficData(crossId1);
//            trafficData1.setEndTime(LocalDateTime.now());
//            trafficData1.setInterval(300);
//
//            // 添加车道数据
//            List<LaneTrafficData> laneDataList = new ArrayList<>();
//
//            LaneTrafficData lane1 = new LaneTrafficData("01");
//            lane1.setVolume(240);
//            lane1.setAvgVehLen(4.5);
//            lane1.setPcu(250);
//            lane1.setHeadDistance(8.2);
//            lane1.setHeadTime(2.8);
//            lane1.setSpeed(35.6);
//            lane1.setSaturation(0.65);
//            lane1.setDensity(28);
//            lane1.setQueueLength(15.8);
//            lane1.setOccupancy(25);
//
//            LaneTrafficData lane2 = new LaneTrafficData("02");
//            lane2.setVolume(180);
//            lane2.setAvgVehLen(4.8);
//            lane2.setPcu(190);
//            lane2.setHeadDistance(9.5);
//            lane2.setHeadTime(3.2);
//            lane2.setSpeed(32.4);
//            lane2.setSaturation(0.48);
//            lane2.setDensity(22);
//            lane2.setQueueLength(12.3);
//            lane2.setOccupancy(18);
//
//            laneDataList.add(lane1);
//            laneDataList.add(lane2);
//            trafficData1.setDataList(laneDataList);
//
//            saveCrossTrafficData(trafficData1);
//
//            // 创建示例阶段交通流数据
//            StageTrafficData stageData1 = new StageTrafficData(crossId1, 1);
//            stageData1.setStartTime(LocalDateTime.now().minusMinutes(5));
//            stageData1.setEndTime(LocalDateTime.now().minusMinutes(4));
//
//            List<StageTrafficFlowData> stageDataList = new ArrayList<>();
//            StageTrafficFlowData stageFlowData = new StageTrafficFlowData("01");
//            stageFlowData.setVehicleNum(18);
//            stageFlowData.setPcu(19);
//            stageFlowData.setHeadTime(2.8);
//            stageFlowData.setSaturation(0.68);
//            stageFlowData.setQueueLength(8.5);
//            stageFlowData.setOccupancy(32);
//            stageDataList.add(stageFlowData);
//            stageData1.setDataList(stageDataList);
//
//            saveStageTrafficData(stageData1);
//
//            // 创建示例周期数据
//            CrossCycle cycle1 = new CrossCycle(crossId1);
//            cycle1.setStartTime(LocalDateTime.now().minusMinutes(2));
//            cycle1.setLastCycleLen(120);
//            cycle1.setCurCycleLen(120);
//            updateCrossCycle(cycle1);
//
//            // 创建示例阶段数据
//            CrossStage stage1 = new CrossStage(crossId1);
//            stage1.setLastStageNo(4);
//            stage1.setLastStageLen(30);
//            stage1.setCurStageNo(1);
//            stage1.setCurStageLen(25);
//            updateCrossStage(stage1);
//
//            logger.info("交通数据示例数据初始化完成");
//
//        } catch (BusinessException e) {
//            logger.error("交通数据示例数据初始化失败", e);
//        }
    }
}