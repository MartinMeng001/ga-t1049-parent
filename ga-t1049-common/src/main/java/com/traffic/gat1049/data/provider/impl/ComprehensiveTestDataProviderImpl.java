package com.traffic.gat1049.data.provider.impl;

import com.traffic.gat1049.data.provider.ComprehensiveTestDataProvider;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.model.enums.*;
import com.traffic.gat1049.protocol.model.command.CrossCtrlInfo;
import com.traffic.gat1049.protocol.model.intersection.*;
import com.traffic.gat1049.protocol.model.system.*;
import com.traffic.gat1049.protocol.model.signal.*;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.LaneTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficFlowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 综合测试数据提供者实现 - 更新版
 * 从JSON测试数据文件中加载并提供所有类型的测试数据
 * 支持新的测试数据结构和运行状态数据
 */
public class ComprehensiveTestDataProviderImpl implements ComprehensiveTestDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveTestDataProviderImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode testDataRoot;
    private boolean initialized = false;

    // 缓存解析后的数据，提高访问性能
    private final Map<String, Object> dataCache = new ConcurrentHashMap<>();

    // 单例模式
    private static ComprehensiveTestDataProviderImpl instance;

    private ComprehensiveTestDataProviderImpl() {
        // 私有构造函数防止外部实例化
    }

    public static synchronized ComprehensiveTestDataProviderImpl getInstance() throws BusinessException {
        if (instance == null) {
            instance = new ComprehensiveTestDataProviderImpl();
            try {
                instance.initialize();
            } catch (BusinessException e) {
                instance = null;
                throw e;
            }
        }
        return instance;
    }

    @Override
    public void initialize() throws BusinessException {
        if (initialized) {
            return;
        }
        try {
            logger.info("初始化综合测试数据提供者...");
            loadTestDataFromJson();
            if (testDataRoot == null) {
                throw new BusinessException("INIT_ERROR", "无法加载测试数据");
            }
            initialized = true;
            preloadCommonData();
            logger.info("综合测试数据提供者初始化完成");
        } catch (Exception e) {
            logger.error("综合测试数据提供者初始化失败", e);
            initialized = false;
            throw new BusinessException("INIT_ERROR", "初始化失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isDataAvailable() {
        return initialized && testDataRoot != null;
    }

    @Override
    public void reloadData() throws BusinessException {
        logger.info("重新加载测试数据...");
        dataCache.clear();
        initialized = false;
        initialize();
    }

    @Override
    public String getDataStatistics() {
        return "";
    }

    @Override
    public List<Object> getTestDataByObjectName(String objectName) throws BusinessException {
        return List.of();
    }

    @Override
    public Object getTestDataByObjectNameAndId(String objectName, String id) throws BusinessException {
        return null;
    }

    @Override
    public Object getTestDataByObjectNameIdAndNo(String objectName, String id, String no) throws BusinessException {
        return null;
    }

    // ==================== 系统信息相关实现 ====================

    @Override
    public SysInfo getSystemInfo() throws BusinessException {
        ensureInitialized();

        String cacheKey = "SysInfo";
        if (dataCache.containsKey(cacheKey)) {
            return (SysInfo) dataCache.get(cacheKey);
        }

        try {
            JsonNode sysParamNode = testDataRoot.get("SysParam");
            if (sysParamNode == null) {
                throw new DataNotFoundException("测试数据中未找到SysParam节点");
            }

            SysInfo sysInfo = parseSystemInfo(sysParamNode);
            dataCache.put(cacheKey, sysInfo);

            return sysInfo;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析系统信息失败: " + e.getMessage());
        }
    }

    @Override
    public SysState getSystemState() throws BusinessException {
        ensureInitialized();

        String cacheKey = "SysState";
        if (dataCache.containsKey(cacheKey)) {
            return (SysState) dataCache.get(cacheKey);
        }

        try {
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode sysStateNode = runStatusNode.get("SysState");
                if (sysStateNode != null) {
                    SysState sysState = parseSysState(sysStateNode);
                    dataCache.put(cacheKey, sysState);
                    return sysState;
                }
            }

            // 如果没找到，返回默认状态
            SysState defaultState = new SysState();
            defaultState.setValue(SystemState.ONLINE);
            defaultState.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dataCache.put(cacheKey, defaultState);
            return defaultState;

        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析系统状态失败: " + e.getMessage());
        }
    }

    // ==================== 区域管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<RegionParam> getAllRegions() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllRegions";
        if (dataCache.containsKey(cacheKey)) {
            return (List<RegionParam>) dataCache.get(cacheKey);
        }

        try {
            List<RegionParam> regions = new ArrayList<>();
            JsonNode regionArray = testDataRoot.get("Region");

            if (regionArray != null && regionArray.isArray()) {
                for (JsonNode regionNode : regionArray) {
                    RegionParam region = parseRegionParam(regionNode);
                    regions.add(region);
                }
            }

            dataCache.put(cacheKey, regions);
            return regions;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析区域参数失败: " + e.getMessage());
        }
    }

    @Override
    public RegionParam getRegionById(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "区域ID不能为空");
        }

        List<RegionParam> regions = getAllRegions();
        return regions.stream()
                .filter(region -> regionId.equals(region.getRegionId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到区域ID: " + regionId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SubRegionParam> getAllSubRegions() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllSubRegions";
        if (dataCache.containsKey(cacheKey)) {
            return (List<SubRegionParam>) dataCache.get(cacheKey);
        }

        try {
            List<SubRegionParam> subRegions = new ArrayList<>();
            JsonNode subRegionArray = testDataRoot.get("SubRegion");

            if (subRegionArray != null && subRegionArray.isArray()) {
                for (JsonNode subRegionNode : subRegionArray) {
                    SubRegionParam subRegion = parseSubRegionParam(subRegionNode);
                    subRegions.add(subRegion);
                }
            }

            dataCache.put(cacheKey, subRegions);
            return subRegions;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析子区参数失败: " + e.getMessage());
        }
    }

    @Override
    public SubRegionParam getSubRegionById(String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "子区ID不能为空");
        }

        List<SubRegionParam> subRegions = getAllSubRegions();
        return subRegions.stream()
                .filter(subRegion -> subRegionId.equals(subRegion.getSubRegionId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到子区ID: " + subRegionId));
    }

    @Override
    public List<SubRegionParam> getSubRegionsByRegionId(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "区域ID不能为空");
        }

        RegionParam region = getRegionById(regionId);
        List<SubRegionParam> allSubRegions = getAllSubRegions();

        return allSubRegions.stream()
                .filter(subRegion -> region.getSubRegionIdList() != null &&
                        region.getSubRegionIdList().contains(subRegion.getSubRegionId()))
                .collect(Collectors.toList());
    }

    // ==================== 线路管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<RouteParam> getAllRoutes() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllRoutes";
        if (dataCache.containsKey(cacheKey)) {
            return (List<RouteParam>) dataCache.get(cacheKey);
        }

        try {
            List<RouteParam> routes = new ArrayList<>();
            JsonNode routeArray = testDataRoot.get("Route");

            if (routeArray != null && routeArray.isArray()) {
                for (JsonNode routeNode : routeArray) {
                    RouteParam route = parseRouteParam(routeNode);
                    routes.add(route);
                }
            }

            dataCache.put(cacheKey, routes);
            return routes;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析线路参数失败: " + e.getMessage());
        }
    }

    @Override
    public RouteParam getRouteById(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "线路ID不能为空");
        }

        List<RouteParam> routes = getAllRoutes();
        return routes.stream()
                .filter(route -> routeId.equals(route.getRouteId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到线路ID: " + routeId));
    }

    // ==================== 路口管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<CrossParam> getAllCrosses() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllCrosses";
        if (dataCache.containsKey(cacheKey)) {
            return (List<CrossParam>) dataCache.get(cacheKey);
        }

        try {
            List<CrossParam> crosses = new ArrayList<>();
            JsonNode crossArray = testDataRoot.get("CrossParam");

            if (crossArray != null && crossArray.isArray()) {
                for (JsonNode crossNode : crossArray) {
                    CrossParam cross = parseCrossParam(crossNode);
                    crosses.add(cross);
                }
            }

            dataCache.put(cacheKey, crosses);
            return crosses;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析路口参数失败: " + e.getMessage());
        }
    }

    @Override
    public CrossParam getCrossById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<CrossParam> crosses = getAllCrosses();
        return crosses.stream()
                .filter(cross -> crossId.equals(cross.getCrossId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到路口ID: " + crossId));
    }

    @Override
    public List<CrossParam> getCrossesByRegionId(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "区域ID不能为空");
        }

        RegionParam region = getRegionById(regionId);
        List<CrossParam> allCrosses = getAllCrosses();

        return allCrosses.stream()
                .filter(cross -> region.getCrossIdList() != null &&
                        region.getCrossIdList().contains(cross.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CrossParam> getCrossesBySubRegionId(String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "子区ID不能为空");
        }

        SubRegionParam subRegion = getSubRegionById(subRegionId);
        List<CrossParam> allCrosses = getAllCrosses();

        return allCrosses.stream()
                .filter(cross -> subRegion.getCrossIdList() != null &&
                        subRegion.getCrossIdList().contains(cross.getCrossId()))
                .collect(Collectors.toList());
    }

    // ==================== 信号机管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<SignalController> getAllSignalControllers() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllSignalControllers";
        if (dataCache.containsKey(cacheKey)) {
            return (List<SignalController>) dataCache.get(cacheKey);
        }

        try {
            List<SignalController> controllers = new ArrayList<>();
            JsonNode controllerArray = testDataRoot.get("SignalParam");

            if (controllerArray != null && controllerArray.isArray()) {
                for (JsonNode controllerNode : controllerArray) {
                    SignalController controller = parseSignalController(controllerNode);
                    controllers.add(controller);
                }
            }

            dataCache.put(cacheKey, controllers);
            return controllers;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析信号机参数失败: " + e.getMessage());
        }
    }

    @Override
    public SignalController getSignalControllerById(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "信号机ID不能为空");
        }

        List<SignalController> controllers = getAllSignalControllers();
        return controllers.stream()
                .filter(controller -> signalControllerId.equals(controller.getSignalControllerID()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到信号机ID: " + signalControllerId));
    }

    @Override
    public List<SignalController> getSignalControllersById(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "信号机ID不能为空");
        }

        List<SignalController> controllers = getAllSignalControllers();
        return controllers.stream()
                .filter(controller -> signalControllerId.equals(controller.getSignalControllerID()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SignalController> getSignalControllersByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<SignalController> allControllers = getAllSignalControllers();
        return allControllers.stream()
                .filter(controller -> controller.getCrossIDList() != null &&
                        controller.getCrossIDList().contains(crossId))
                .collect(Collectors.toList());
    }

    // ==================== 灯组管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<LampGroupParam> getAllLampGroups() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllLampGroups";
        if (dataCache.containsKey(cacheKey)) {
            return (List<LampGroupParam>) dataCache.get(cacheKey);
        }

        try {
            List<LampGroupParam> lampGroups = new ArrayList<>();
            JsonNode lampGroupArray = testDataRoot.get("LightGroup");

            if (lampGroupArray != null && lampGroupArray.isArray()) {
                for (JsonNode lampGroupNode : lampGroupArray) {
                    LampGroupParam lampGroup = parseLampGroup(lampGroupNode);
                    lampGroups.add(lampGroup);
                }
            }

            dataCache.put(cacheKey, lampGroups);
            return lampGroups;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析灯组参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<LampGroupParam> getLampGroupsByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<LampGroupParam> allLampGroups = getAllLampGroups();
        return allLampGroups.stream()
                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public LampGroupParam getLampGroupByCrossIdAndNo(String crossId, String lampGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (lampGroupNo == null || lampGroupNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "灯组编号不能为空");
        }

        List<LampGroupParam> lampGroups = getLampGroupsByCrossId(crossId);
        return lampGroups.stream()
                .filter(lampGroup -> lampGroupNo.equals(String.valueOf(lampGroup.getLampGroupNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的灯组%s", crossId, lampGroupNo)));
    }

    // ==================== 检测器管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<DetectorParam> getAllDetectors() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllDetectors";
        if (dataCache.containsKey(cacheKey)) {
            return (List<DetectorParam>) dataCache.get(cacheKey);
        }

        try {
            List<DetectorParam> detectors = new ArrayList<>();
            JsonNode detectorArray = testDataRoot.get("DetectorParam");

            if (detectorArray != null && detectorArray.isArray()) {
                for (JsonNode detectorNode : detectorArray) {
                    DetectorParam detector = parseDetectorParam(detectorNode);
                    detectors.add(detector);
                }
            }

            dataCache.put(cacheKey, detectors);
            return detectors;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析检测器参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<DetectorParam> getDetectorsByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<DetectorParam> allDetectors = getAllDetectors();
        return allDetectors.stream()
                .filter(detector -> crossId.equals(detector.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public DetectorParam getDetectorByCrossIdAndNo(String crossId, String detectorNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (detectorNo == null || detectorNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "检测器编号不能为空");
        }

        List<DetectorParam> detectors = getDetectorsByCrossId(crossId);
        return detectors.stream()
                .filter(detector -> detectorNo.equals(String.valueOf(detector.getDetectorNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的检测器%s", crossId, detectorNo)));
    }

    // ==================== 车道管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<LaneParam> getAllLanes() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllLanes";
        if (dataCache.containsKey(cacheKey)) {
            return (List<LaneParam>) dataCache.get(cacheKey);
        }

        try {
            List<LaneParam> lanes = new ArrayList<>();
            JsonNode laneArray = testDataRoot.get("LaneParam");

            if (laneArray != null && laneArray.isArray()) {
                for (JsonNode laneNode : laneArray) {
                    LaneParam lane = parseLaneParam(laneNode);
                    lanes.add(lane);
                }
            }

            dataCache.put(cacheKey, lanes);
            return lanes;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析车道参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<LaneParam> getLanesByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<LaneParam> allLanes = getAllLanes();
        return allLanes.stream()
                .filter(lane -> crossId.equals(lane.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public LaneParam getLaneByCrossIdAndNo(String crossId, String laneNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (laneNo == null || laneNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "车道编号不能为空");
        }

        List<LaneParam> lanes = getLanesByCrossId(crossId);
        return lanes.stream()
                .filter(lane -> laneNo.equals(String.valueOf(lane.getLaneNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的车道%s", crossId, laneNo)));
    }

    // ==================== 行人管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<PedestrianParam> getAllPedestrians() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllPedestrians";
        if (dataCache.containsKey(cacheKey)) {
            return (List<PedestrianParam>) dataCache.get(cacheKey);
        }

        try {
            List<PedestrianParam> pedestrians = new ArrayList<>();
            JsonNode pedestrianArray = testDataRoot.get("PedestrianParam");

            if (pedestrianArray != null && pedestrianArray.isArray()) {
                for (JsonNode pedestrianNode : pedestrianArray) {
                    PedestrianParam pedestrian = parsePedestrianParam(pedestrianNode);
                    pedestrians.add(pedestrian);
                }
            }

            dataCache.put(cacheKey, pedestrians);
            return pedestrians;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析行人参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<PedestrianParam> getPedestriansByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<PedestrianParam> allPedestrians = getAllPedestrians();
        return allPedestrians.stream()
                .filter(pedestrian -> crossId.equals(pedestrian.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public PedestrianParam getPedestrianByCrossIdAndNo(String crossId, String pedestrianNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (pedestrianNo == null || pedestrianNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "行人编号不能为空");
        }

        List<PedestrianParam> pedestrians = getPedestriansByCrossId(crossId);
        return pedestrians.stream()
                .filter(pedestrian -> pedestrianNo.equals(String.valueOf(pedestrian.getPedestrianNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的行人%s", crossId, pedestrianNo)));
    }

    // ==================== 信号组管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<SignalGroupParam> getAllSignalGroups() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllSignalGroups";
        if (dataCache.containsKey(cacheKey)) {
            return (List<SignalGroupParam>) dataCache.get(cacheKey);
        }

        try {
            List<SignalGroupParam> signalGroups = new ArrayList<>();
            JsonNode signalGroupArray = testDataRoot.get("SignalGroupParam");

            if (signalGroupArray != null && signalGroupArray.isArray()) {
                for (JsonNode signalGroupNode : signalGroupArray) {
                    SignalGroupParam signalGroup = parseSignalGroupParam(signalGroupNode);
                    signalGroups.add(signalGroup);
                }
            }

            dataCache.put(cacheKey, signalGroups);
            return signalGroups;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析信号组参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<SignalGroupParam> getSignalGroupsByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<SignalGroupParam> allSignalGroups = getAllSignalGroups();
        return allSignalGroups.stream()
                .filter(signalGroup -> crossId.equals(signalGroup.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public SignalGroupParam getSignalGroupByCrossIdAndNo(String crossId, String signalGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (signalGroupNo == null || signalGroupNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "信号组编号不能为空");
        }

        List<SignalGroupParam> signalGroups = getSignalGroupsByCrossId(crossId);
        return signalGroups.stream()
                .filter(signalGroup -> signalGroupNo.equals(String.valueOf(signalGroup.getSignalGroupNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的信号组%s", crossId, signalGroupNo)));
    }

    // ==================== 阶段管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<StageParam> getAllStages() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllStages";
        if (dataCache.containsKey(cacheKey)) {
            return (List<StageParam>) dataCache.get(cacheKey);
        }

        try {
            List<StageParam> stages = new ArrayList<>();
            JsonNode stageArray = testDataRoot.get("StageParam");

            if (stageArray != null && stageArray.isArray()) {
                for (JsonNode stageNode : stageArray) {
                    StageParam stage = parseStageParam(stageNode);
                    stages.add(stage);
                }
            }

            dataCache.put(cacheKey, stages);
            return stages;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析阶段参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<StageParam> getStagesByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<StageParam> allStages = getAllStages();
        return allStages.stream()
                .filter(stage -> crossId.equals(stage.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public StageParam getStageByCrossIdAndNo(String crossId, String stageNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (stageNo == null || stageNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "阶段编号不能为空");
        }

        List<StageParam> stages = getStagesByCrossId(crossId);
        return stages.stream()
                .filter(stage -> stageNo.equals(String.valueOf(stage.getStageNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的阶段%s", crossId, stageNo)));
    }

    // ==================== 配时方案管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<PlanParam> getAllPlans() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllPlans";
        if (dataCache.containsKey(cacheKey)) {
            return (List<PlanParam>) dataCache.get(cacheKey);
        }

        try {
            List<PlanParam> plans = new ArrayList<>();
            JsonNode planArray = testDataRoot.get("PlanParam");

            if (planArray != null && planArray.isArray()) {
                for (JsonNode planNode : planArray) {
                    PlanParam plan = parsePlanParam(planNode);
                    plans.add(plan);
                }
            }

            dataCache.put(cacheKey, plans);
            return plans;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析配时方案参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<PlanParam> getPlansByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<PlanParam> allPlans = getAllPlans();
        return allPlans.stream()
                .filter(plan -> crossId.equals(plan.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public PlanParam getPlanByCrossIdAndNo(String crossId, String planNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (planNo == null || planNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "配时方案编号不能为空");
        }

        List<PlanParam> plans = getPlansByCrossId(crossId);
        return plans.stream()
                .filter(plan -> planNo.equals(String.valueOf(plan.getPlanNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的配时方案%s", crossId, planNo)));
    }

    // ==================== 日计划管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<DayPlanParam> getAllDayPlans() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllDayPlans";
        if (dataCache.containsKey(cacheKey)) {
            return (List<DayPlanParam>) dataCache.get(cacheKey);
        }

        try {
            List<DayPlanParam> dayPlans = new ArrayList<>();
            JsonNode dayPlanArray = testDataRoot.get("DayPlan");

            if (dayPlanArray != null && dayPlanArray.isArray()) {
                for (JsonNode dayPlanNode : dayPlanArray) {
                    DayPlanParam dayPlan = parseDayPlanParam(dayPlanNode);
                    dayPlans.add(dayPlan);
                }
            }

            dataCache.put(cacheKey, dayPlans);
            return dayPlans;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析日计划参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<DayPlanParam> getDayPlansByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<DayPlanParam> allDayPlans = getAllDayPlans();
        return allDayPlans.stream()
                .filter(dayPlan -> crossId.equals(dayPlan.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public DayPlanParam getDayPlanByCrossIdAndNo(String crossId, String dayPlanNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (dayPlanNo == null || dayPlanNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "日计划编号不能为空");
        }

        List<DayPlanParam> dayPlans = getDayPlansByCrossId(crossId);
        return dayPlans.stream()
                .filter(dayPlan -> dayPlanNo.equals(String.valueOf(dayPlan.getDayPlanNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的日计划%s", crossId, dayPlanNo)));
    }

    // ==================== 调度管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<ScheduleParam> getAllSchedules() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllSchedules";
        if (dataCache.containsKey(cacheKey)) {
            return (List<ScheduleParam>) dataCache.get(cacheKey);
        }

        try {
            List<ScheduleParam> schedules = new ArrayList<>();
            JsonNode scheduleArray = testDataRoot.get("Schedule");

            if (scheduleArray != null && scheduleArray.isArray()) {
                for (JsonNode scheduleNode : scheduleArray) {
                    ScheduleParam schedule = parseScheduleParam(scheduleNode);
                    schedules.add(schedule);
                }
            }

            dataCache.put(cacheKey, schedules);
            return schedules;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析调度参数失败: " + e.getMessage());
        }
    }

    @Override
    public List<ScheduleParam> getSchedulesByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<ScheduleParam> allSchedules = getAllSchedules();
        return allSchedules.stream()
                .filter(schedule -> crossId.equals(schedule.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleParam getScheduleByCrossIdAndNo(String crossId, String scheduleNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (scheduleNo == null || scheduleNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "调度编号不能为空");
        }

        List<ScheduleParam> schedules = getSchedulesByCrossId(crossId);
        return schedules.stream()
                .filter(schedule -> scheduleNo.equals(String.valueOf(schedule.getScheduleNo())))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到路口%s的调度%s", crossId, scheduleNo)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CrossState> getAllCrossStates() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllCrossStates";
        if (dataCache.containsKey(cacheKey)) {
            return (List<CrossState>) dataCache.get(cacheKey);
        }

        try {
            List<CrossState> crossStates = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode crossStateArray = runStatusNode.get("CrossState");
                if (crossStateArray != null && crossStateArray.isArray()) {
                    for (JsonNode crossStateNode : crossStateArray) {
                        CrossState crossState = parseCrossState(crossStateNode);
                        crossStates.add(crossState);
                    }
                }
            }

            dataCache.put(cacheKey, crossStates);
            return crossStates;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析路口状态失败: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CrossState getCrossStateById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<CrossState> crossStates = getAllCrossStates();
        return crossStates.stream()
                .filter(crossState -> crossId.equals(crossState.getCrossId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到路口状态: " + crossId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SignalControllerError> getAllSignalControllerErrors() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllSignalControllerErrors";
        if (dataCache.containsKey(cacheKey)) {
            return (List<SignalControllerError>) dataCache.get(cacheKey);
        }

        try {
            List<SignalControllerError> errors = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode errorArray = runStatusNode.get("SignalControllerError");
                if (errorArray != null && errorArray.isArray()) {
                    for (JsonNode errorNode : errorArray) {
                        SignalControllerError error = parseSignalControllerError(errorNode);
                        errors.add(error);
                    }
                }
            }

            dataCache.put(cacheKey, errors);
            return errors;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析信号机故障信息失败: " + e.getMessage());
        }
    }

    @Override
    public List<SignalControllerError> getSignalControllerErrorsByControllerId(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "信号机ID不能为空");
        }

        List<SignalControllerError> allErrors = getAllSignalControllerErrors();
        return allErrors.stream()
                .filter(error -> signalControllerId.equals(error.getSignalControllerId()))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CrossCtrlInfo> getAllCrossCtrlInfos() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllCrossCtrlInfos";
        if (dataCache.containsKey(cacheKey)) {
            return (List<CrossCtrlInfo>) dataCache.get(cacheKey);
        }

        try {
            List<CrossCtrlInfo> ctrlInfos = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode ctrlInfoArray = runStatusNode.get("CrossCtrlInfo");
                if (ctrlInfoArray != null && ctrlInfoArray.isArray()) {
                    for (JsonNode ctrlInfoNode : ctrlInfoArray) {
                        CrossCtrlInfo ctrlInfo = parseCrossCtrlInfo(ctrlInfoNode);
                        ctrlInfos.add(ctrlInfo);
                    }
                }
            }

            dataCache.put(cacheKey, ctrlInfos);
            return ctrlInfos;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析路口控制信息失败: " + e.getMessage());
        }
    }

    @Override
    public CrossCtrlInfo getCrossCtrlInfoById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<CrossCtrlInfo> ctrlInfos = getAllCrossCtrlInfos();
        return ctrlInfos.stream()
                .filter(ctrlInfo -> crossId.equals(ctrlInfo.getCrossId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到路口控制信息: " + crossId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CrossCycle> getAllCrossCycles() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllCrossCycles";
        if (dataCache.containsKey(cacheKey)) {
            return (List<CrossCycle>) dataCache.get(cacheKey);
        }

        try {
            List<CrossCycle> cycles = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode cycleArray = runStatusNode.get("CrossCycle");
                if (cycleArray != null && cycleArray.isArray()) {
                    for (JsonNode cycleNode : cycleArray) {
                        CrossCycle cycle = parseCrossCycle(cycleNode);
                        cycles.add(cycle);
                    }
                }
            }

            dataCache.put(cacheKey, cycles);
            return cycles;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析路口周期信息失败: " + e.getMessage());
        }
    }

    @Override
    public CrossCycle getCrossCycleById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<CrossCycle> cycles = getAllCrossCycles();
        return cycles.stream()
                .filter(cycle -> crossId.equals(cycle.getCrossId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到路口周期信息: " + crossId));
    }

    @Override
    public List<CrossStage> getAllCrossStages() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllCrossStages";
        if (dataCache.containsKey(cacheKey)) {
            return (List<CrossStage>) dataCache.get(cacheKey);
        }

        try {
            List<CrossStage> stages = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode stageArray = runStatusNode.get("CrossStage");
                if (stageArray != null && stageArray.isArray()) {
                    for (JsonNode stageNode : stageArray) {
                        CrossStage stage = parseCrossStage(stageNode);
                        stages.add(stage);
                    }
                }
            }

            dataCache.put(cacheKey, stages);
            return stages;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析路口阶段信息失败: " + e.getMessage());
        }
    }

    @Override
    public CrossStage getCrossStageById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<CrossStage> stages = getAllCrossStages();
        return stages.stream()
                .filter(stage -> crossId.equals(stage.getCrossId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到路口阶段信息: " + crossId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CrossSignalGroupStatus> getAllCrossSignalGroupStatus() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllCrossSignalGroupStatus";
        if (dataCache.containsKey(cacheKey)) {
            return (List<CrossSignalGroupStatus>) dataCache.get(cacheKey);
        }

        try {
            List<CrossSignalGroupStatus> statusList = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode statusArray = runStatusNode.get("CrossSignalGroupStatus");
                if (statusArray != null && statusArray.isArray()) {
                    for (JsonNode statusNode : statusArray) {
                        CrossSignalGroupStatus status = parseCrossSignalGroupStatus(statusNode);
                        statusList.add(status);
                    }
                }
            }

            dataCache.put(cacheKey, statusList);
            return statusList;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析路口信号组状态失败: " + e.getMessage());
        }
    }

    @Override
    public CrossSignalGroupStatus getCrossSignalGroupStatusById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<CrossSignalGroupStatus> statusList = getAllCrossSignalGroupStatus();
        return statusList.stream()
                .filter(status -> crossId.equals(status.getCrossId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到路口信号组状态: " + crossId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CrossTrafficData> getAllCrossTrafficData() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllCrossTrafficData";
        if (dataCache.containsKey(cacheKey)) {
            return (List<CrossTrafficData>) dataCache.get(cacheKey);
        }

        try {
            List<CrossTrafficData> trafficDataList = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode trafficDataArray = runStatusNode.get("CrossTrafficData");
                if (trafficDataArray != null && trafficDataArray.isArray()) {
                    for (JsonNode trafficDataNode : trafficDataArray) {
                        CrossTrafficData trafficData = parseCrossTrafficData(trafficDataNode);
                        trafficDataList.add(trafficData);
                    }
                }
            }

            dataCache.put(cacheKey, trafficDataList);
            return trafficDataList;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析路口交通流数据失败: " + e.getMessage());
        }
    }

    @Override
    public CrossTrafficData getCrossTrafficDataById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<CrossTrafficData> trafficDataList = getAllCrossTrafficData();
        return trafficDataList.stream()
                .filter(trafficData -> crossId.equals(trafficData.getCrossId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到路口交通流数据: " + crossId));
    }

    @Override
    public List<CrossTrafficData> getCrossTrafficDataByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        List<CrossTrafficData> trafficDataList = getAllCrossTrafficData();
        return trafficDataList.stream()
                .filter(trafficData -> crossId.equals(trafficData.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StageTrafficData> getAllStageTrafficData() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllStageTrafficData";
        if (dataCache.containsKey(cacheKey)) {
            return (List<StageTrafficData>) dataCache.get(cacheKey);
        }

        try {
            List<StageTrafficData> stageTrafficDataList = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode stageTrafficDataArray = runStatusNode.get("StageTrafficData");
                if (stageTrafficDataArray != null && stageTrafficDataArray.isArray()) {
                    for (JsonNode stageTrafficDataNode : stageTrafficDataArray) {
                        StageTrafficData stageTrafficData = parseStageTrafficData(stageTrafficDataNode);
                        stageTrafficDataList.add(stageTrafficData);
                    }
                }
            }

            dataCache.put(cacheKey, stageTrafficDataList);
            return stageTrafficDataList;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析阶段交通流数据失败: " + e.getMessage());
        }
    }

    @Override
    public List<StageTrafficData> getStageTrafficDataByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<StageTrafficData> allStageTrafficData = getAllStageTrafficData();
        return allStageTrafficData.stream()
                .filter(stageTrafficData -> crossId.equals(stageTrafficData.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<VarLaneStatus> getAllVarLaneStatus() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllVarLaneStatus";
        if (dataCache.containsKey(cacheKey)) {
            return (List<VarLaneStatus>) dataCache.get(cacheKey);
        }

        try {
            List<VarLaneStatus> varLaneStatusList = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode varLaneStatusArray = runStatusNode.get("VarLaneStatus");
                if (varLaneStatusArray != null && varLaneStatusArray.isArray()) {
                    for (JsonNode varLaneStatusNode : varLaneStatusArray) {
                        VarLaneStatus varLaneStatus = parseVarLaneStatus(varLaneStatusNode);
                        varLaneStatusList.add(varLaneStatus);
                    }
                }
            }

            dataCache.put(cacheKey, varLaneStatusList);
            return varLaneStatusList;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析可变车道状态失败: " + e.getMessage());
        }
    }

    @Override
    public List<VarLaneStatus> getVarLaneStatusByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<VarLaneStatus> allVarLaneStatus = getAllVarLaneStatus();
        return allVarLaneStatus.stream()
                .filter(varLaneStatus -> crossId.equals(varLaneStatus.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public RouteCtrlInfo getRouteCtrlInfo() throws BusinessException {
        ensureInitialized();

        String cacheKey = "RouteCtrlInfo";
        if (dataCache.containsKey(cacheKey)) {
            return (RouteCtrlInfo) dataCache.get(cacheKey);
        }

        try {
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode routeCtrlInfoNode = runStatusNode.get("RouteCtrlInfo");
                if (routeCtrlInfoNode != null) {
                    RouteCtrlInfo routeCtrlInfo = parseRouteCtrlInfo(routeCtrlInfoNode);
                    dataCache.put(cacheKey, routeCtrlInfo);
                    return routeCtrlInfo;
                }
            }

            throw new DataNotFoundException("未找到干线控制信息");
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析干线控制信息失败: " + e.getMessage());
        }
    }

    @Override
    public RouteCtrlInfo getRouteCtrlInfoById(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "线路ID不能为空");
        }

        RouteCtrlInfo routeCtrlInfo = getRouteCtrlInfo();
        if (routeId.equals(routeCtrlInfo.getRouteId())) {
            return routeCtrlInfo;
        }

        throw new DataNotFoundException("未找到线路控制信息: " + routeId);
    }

    @Override
    public RouteSpeed getRouteSpeed() throws BusinessException {
        ensureInitialized();

        String cacheKey = "RouteSpeed";
        if (dataCache.containsKey(cacheKey)) {
            return (RouteSpeed) dataCache.get(cacheKey);
        }

        try {
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode routeSpeedNode = runStatusNode.get("RouteSpeed");
                if (routeSpeedNode != null) {
                    RouteSpeed routeSpeed = parseRouteSpeed(routeSpeedNode);
                    dataCache.put(cacheKey, routeSpeed);
                    return routeSpeed;
                }
            }

            throw new DataNotFoundException("未找到干线推荐车速信息");
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析干线推荐车速信息失败: " + e.getMessage());
        }
    }

    @Override
    public RouteSpeed getRouteSpeedById(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "线路ID不能为空");
        }

        RouteSpeed routeSpeed = getRouteSpeed();
        if (routeId.equals(routeSpeed.getRouteId())) {
            return routeSpeed;
        }

        throw new DataNotFoundException("未找到线路推荐车速: " + routeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SCDoorStatus> getAllSCDoorStatus() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllSCDoorStatus";
        if (dataCache.containsKey(cacheKey)) {
            return (List<SCDoorStatus>) dataCache.get(cacheKey);
        }

        try {
            List<SCDoorStatus> doorStatusList = new ArrayList<>();
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode doorStatusArray = runStatusNode.get("SCDoorStatus");
                if (doorStatusArray != null && doorStatusArray.isArray()) {
                    for (JsonNode doorStatusNode : doorStatusArray) {
                        SCDoorStatus doorStatus = parseSCDoorStatus(doorStatusNode);
                        doorStatusList.add(doorStatus);
                    }
                }
            }

            dataCache.put(cacheKey, doorStatusList);
            return doorStatusList;
        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析信号机柜门状态失败: " + e.getMessage());
        }
    }

    @Override
    public SCDoorStatus getSCDoorStatusByControllerId(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "信号机ID不能为空");
        }

        List<SCDoorStatus> doorStatusList = getAllSCDoorStatus();
        return doorStatusList.stream()
                .filter(doorStatus -> signalControllerId.equals(doorStatus.getSignalControllerId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到信号机柜门状态: " + signalControllerId));
    }
    // ==================== 通用方法实现 ====================

    // ==================== 私有辅助方法 ====================

    private void ensureInitialized() throws BusinessException {
        if (!initialized) {
            throw new BusinessException("NOT_INITIALIZED", "数据提供者未初始化");
        }
    }
    private void loadTestDataFromJson() throws BusinessException {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("testdata.json");

            if (inputStream != null) {
                testDataRoot = objectMapper.readTree(inputStream);
                inputStream.close();
                logger.info("从文件加载测试数据成功");
            } else {
//                String builtInData = getBuiltInTestData();
//                testDataRoot = objectMapper.readTree(builtInData);
                logger.info("使用内置测试数据");
            }
        } catch (Exception e) {
            throw new BusinessException("DATA_LOAD_ERROR", "加载测试数据失败: " + e.getMessage());
        }
    }

    private void preloadCommonData() throws BusinessException {
        getSystemInfo();
        getSystemState();
        getAllRegions();
        getAllSubRegions();
        getAllRoutes();
        getAllCrosses();
        getAllSignalControllers();
        logger.debug("预加载常用测试数据完成");
    }
    // ==================== 解析方法 ====================

    private SysInfo parseSystemInfo(JsonNode sysParamNode) {
        SysInfo sysInfo = new SysInfo();
        sysInfo.setSysName(sysParamNode.path("SysName").asText("测试交通信号控制系统"));
        sysInfo.setSysVersion(sysParamNode.path("SysVersion").asText("2.0"));
        sysInfo.setSupplier(sysParamNode.path("Supplier").asText("测试供应商"));
        sysInfo.setCrossIdList(parseStringList(sysParamNode, "CrossIDList"));
        sysInfo.setSubRegionIdList(parseStringList(sysParamNode, "SubRegionIDList"));
        sysInfo.setRouteIdList(parseStringList(sysParamNode, "RouteIDList"));
        sysInfo.setRegionIdList(parseStringList(sysParamNode, "RegionIDList"));
        sysInfo.setSignalControllerIdList(parseStringList(sysParamNode, "SignalControllerIDList"));
        return sysInfo;
    }

    private SysState parseSysState(JsonNode sysStateNode) {
        SysState sysState = new SysState();
        sysState.setValue(SystemState.fromCode(sysStateNode.path("Value").asText("Online")));
        sysState.setTime(sysStateNode.path("Time").asText(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        return sysState;
    }
    private RegionParam parseRegionParam(JsonNode regionNode) {
        RegionParam region = new RegionParam();
        region.setRegionId(regionNode.path("RegionID").asText());
        region.setRegionName(regionNode.path("RegionName").asText());
        region.setSubRegionIdList(parseStringList(regionNode, "SubRegionIDList"));
        region.setCrossIdList(parseStringList(regionNode, "CrossIDList"));
        return region;
    }

    private SubRegionParam parseSubRegionParam(JsonNode subRegionNode) {
        SubRegionParam subRegion = new SubRegionParam();
        subRegion.setSubRegionId(subRegionNode.path("SubRegionID").asText());
        subRegion.setSubRegionName(subRegionNode.path("SubRegionName").asText());
        subRegion.setCrossIdList(parseStringList(subRegionNode, "CrossIDList"));
        subRegion.setKeyCrossIdList(parseStringList(subRegionNode, "KeyCrossIDList"));
        return subRegion;
    }
    private RouteParam parseRouteParam(JsonNode routeNode) {
        RouteParam route = new RouteParam();
        route.setRouteId(routeNode.path("RouteID").asText());
        route.setRouteName(routeNode.path("RouteName").asText());
        route.setType(RouteType.fromCode(String.valueOf(routeNode.path("Type").asInt())));
        route.setSubRegionIdList(parseStringList(routeNode, "SubRegionIDList"));

        List<RouteCross> routeCrossList = new ArrayList<>();
        JsonNode routeCrossListNode = routeNode.path("RouteCrossList");
        if (routeCrossListNode != null && routeCrossListNode.isArray()) {
            for (JsonNode routeCrossNode : routeCrossListNode) {
                RouteCross routeCross = parseRouteCross(routeCrossNode);
                routeCrossList.add(routeCross);
            }
        }
        route.setRouteCrossList(routeCrossList);
        return route;
    }

    private RouteCross parseRouteCross(JsonNode routeCrossNode) {
        RouteCross routeCross = new RouteCross();
        routeCross.setCrossId(routeCrossNode.path("CrossID").asText());
        routeCross.setDistance(routeCrossNode.path("Distance").asInt(0));
        return routeCross;
    }
    private CrossParam parseCrossParam(JsonNode crossNode) {
        CrossParam cross = new CrossParam();
        cross.setCrossId(crossNode.path("CrossID").asText());
        cross.setCrossName(crossNode.path("CrossName").asText());

        String featureCode = String.valueOf(crossNode.path("Feature").asInt());
        if (!featureCode.isEmpty() && !featureCode.equals("null")) {
            try {
                cross.setFeature(CrossFeature.fromCode(featureCode));
            } catch (IllegalArgumentException e) {
                logger.warn("未知的路口形状代码: {}, 使用默认值", featureCode);
                cross.setFeature(CrossFeature.OTHER);
            }
        }

        String gradeCode = crossNode.path("Grade").asText();
        if (!gradeCode.isEmpty()) {
            try {
                cross.setGrade(CrossGrade.fromCode(gradeCode));
            } catch (IllegalArgumentException e) {
                logger.warn("未知的路口等级代码: {}, 使用默认值", gradeCode);
                cross.setGrade(CrossGrade.OTHER);
            }
        }

        cross.setDetNoList(parseIntList(crossNode, "DetNoList"));
        cross.setLaneNoList(parseIntList(crossNode, "LaneNoList"));
        cross.setPedestrianNoList(parseIntList(crossNode, "PedestrianNoList"));
        cross.setLampGroupNoList(parseIntList(crossNode, "LampGroupNoList"));
        cross.setSignalGroupNoList(parseStringList(crossNode, "SignalGroupNoList"));
        cross.setGreenConflictMatrix(crossNode.path("GreenConflictMatrix").asText());
        cross.setStageNoList(parseIntList(crossNode, "StageNoList"));
        cross.setPlanNoList(parseIntList(crossNode, "PlanNoList"));
        cross.setDayPlanNoList(parseIntList(crossNode, "DayPlanNoList"));
        cross.setScheduleNoList(parseIntList(crossNode, "ScheduleNoList"));

        if (crossNode.has("Longitude") && !crossNode.path("Longitude").isNull()) {
            cross.setLongitude(crossNode.path("Longitude").asDouble());
        }
        if (crossNode.has("Latitude") && !crossNode.path("Latitude").isNull()) {
            cross.setLatitude(crossNode.path("Latitude").asDouble());
        }
        if (crossNode.has("Altitude") && !crossNode.path("Altitude").isNull()) {
            cross.setAltitude(crossNode.path("Altitude").asInt());
        }

        return cross;
    }
    private SignalController parseSignalController(JsonNode controllerNode) {
        SignalController controller = new SignalController();
        controller.setSignalControllerID(controllerNode.path("SignalControllerID").asText());
        controller.setSupplier(controllerNode.path("Supplier").asText());
        controller.setType(controllerNode.path("Type").asText());
        controller.setId(controllerNode.path("ID").asText());
        controller.setCommMode(CommMode.fromCode(controllerNode.path("CommMode").asText()));
        controller.setIp(controllerNode.path("IP").asText());
        controller.setSubMask(controllerNode.path("SubMask").asText());
        controller.setGateway(controllerNode.path("Gateway").asText());
        controller.setPort(controllerNode.path("Port").asInt());
        controller.setHasDoorStatus(controllerNode.path("HasDoorStatus").asInt());

        if (controllerNode.has("Longitude") && !controllerNode.path("Longitude").isNull()) {
            controller.setLongitude(controllerNode.path("Longitude").asDouble());
        }
        if (controllerNode.has("Latitude") && !controllerNode.path("Latitude").isNull()) {
            controller.setLatitude(controllerNode.path("Latitude").asDouble());
        }

        controller.setCrossIDList(parseStringList(controllerNode, "CrossIDList"));
        return controller;
    }
    private LampGroupParam parseLampGroup(JsonNode lampGroupNode) {
        LampGroupParam lampGroup = new LampGroupParam();
        lampGroup.setCrossId(lampGroupNode.path("CrossID").asText());
        lampGroup.setLampGroupNo(lampGroupNode.path("LampGroupNo").asInt());
        lampGroup.setDirection(Direction.fromCode(lampGroupNode.path("Direction").asText()));
        lampGroup.setType(LampGroupType.fromCode(lampGroupNode.path("Type").asText()));
        return lampGroup;
    }
    private DetectorParam parseDetectorParam(JsonNode detectorNode) {
        DetectorParam detector = new DetectorParam();
        detector.setCrossId(detectorNode.path("CrossID").asText());
        detector.setDetectorNo(detectorNode.path("DetectorNo").asInt());
        detector.setType(DetectorType.fromCode(detectorNode.path("Type").asText()));
        detector.setPosition(DetectorPosition.fromCode(detectorNode.path("Position").asText()));
        detector.setTarget(detectorNode.path("Target").asText());
        detector.setDistance(detectorNode.path("Distance").asInt());
        detector.setLaneNoList(parseIntList(detectorNode, "LaneNoList"));
        detector.setPedestrianNoList(parseIntList(detectorNode, "PedestrianNoList"));
        return detector;
    }
    private LaneParam parseLaneParam(JsonNode laneNode) {
        LaneParam lane = new LaneParam();
        lane.setCrossId(laneNode.path("CrossID").asText());
        lane.setLaneNo(laneNode.path("LaneNo").asInt());
        lane.setDirection(Direction.fromCode(laneNode.path("Direction").asText()));
        lane.setAttribute(LaneAttribute.fromCode(laneNode.path("Attribute").asText()));
        lane.setMovement(LaneMovement.fromCode(String.valueOf(laneNode.path("Movement").asInt())));
        lane.setFeature(LaneFeature.fromCode(laneNode.path("Feature").asText()));
        lane.setAzimuth(laneNode.path("Azimuth").asInt());
        lane.setWaitingArea(laneNode.path("WaitingArea").asInt());
        lane.setVarMovementList(parseLaneMovementList(laneNode, "VarMovementList"));
        return lane;
    }
    private List<LaneMovement> parseLaneMovementList(JsonNode parentNode, String fieldName) {
        List<LaneMovement> result = new ArrayList<>();
        JsonNode listNode = parentNode.get(fieldName);

        if (listNode != null && listNode.isArray()) {
            listNode.forEach(node -> result.add(LaneMovement.fromCode(String.valueOf(node.asInt()))));
        }

        return result;
    }
    private PedestrianParam parsePedestrianParam(JsonNode pedestrianNode) {
        PedestrianParam pedestrian = new PedestrianParam();
        pedestrian.setCrossId(pedestrianNode.path("CrossID").asText());
        pedestrian.setPedestrianNo(pedestrianNode.path("PedestrianNo").asInt());
        pedestrian.setDirection(Direction.fromCode(pedestrianNode.path("Direction").asText()));
        pedestrian.setAttribute(PedestrianAttribute.fromCode(pedestrianNode.path("Attribute").asText()));
        return pedestrian;
    }
    private SignalGroupParam parseSignalGroupParam(JsonNode signalGroupNode) {
        SignalGroupParam signalGroup = new SignalGroupParam();
        signalGroup.setCrossId(signalGroupNode.path("CrossID").asText());
        signalGroup.setSignalGroupNo(signalGroupNode.path("SignalGroupNo").asInt());
        signalGroup.setName(signalGroupNode.path("Name").asText());
        signalGroup.setGreenFlashLen(signalGroupNode.path("GreenFlashLen").asInt());
        signalGroup.setMaxGreen(signalGroupNode.path("MaxGreen").asInt());
        signalGroup.setMinGreen(signalGroupNode.path("MinGreen").asInt());
        signalGroup.setLampGroupNoList(parseIntList(signalGroupNode, "LampGroupNoList"));
        return signalGroup;
    }
    private StageParam parseStageParam(JsonNode stageNode) {
        StageParam stage = new StageParam();
        stage.setCrossId(stageNode.path("CrossID").asText());
        stage.setStageNo(stageNode.path("StageNo").asInt());
        stage.setStageName(stageNode.path("StageName").asText());
        stage.setAttribute(stageNode.path("Attribute").asInt());

        List<SignalGroupStatus> signalGroupStatusList = new ArrayList<>();
        JsonNode statusListNode = stageNode.path("SignalGroupStatusList");
        if (statusListNode != null && statusListNode.isArray()) {
            for (JsonNode statusNode : statusListNode) {
                SignalGroupStatus status = new SignalGroupStatus();
                status.setSignalGroupNo(statusNode.path("SignalGroupNo").asInt());
                status.setLampStatus(statusNode.path("LampStatus").asText());
                signalGroupStatusList.add(status);
            }
        }
        stage.setSignalGroupStatusList(signalGroupStatusList);
        return stage;
    }
    private PlanParam parsePlanParam(JsonNode planNode) {
        PlanParam plan = new PlanParam();
        plan.setCrossId(planNode.path("CrossID").asText());
        plan.setPlanNo(planNode.path("PlanNo").asInt());
        plan.setPlanName(planNode.path("PlanName").asText());
        plan.setCycleLen(planNode.path("CycleLen").asInt());
        plan.setCoordStageNo(planNode.path("CoordStageNo").asInt());
        plan.setOffset(planNode.path("Offset").asInt());

        List<StageTiming> stageTimingList = new ArrayList<>();
        JsonNode timingListNode = planNode.path("StageTimingList");
        if (timingListNode != null && timingListNode.isArray()) {
            for (JsonNode timingNode : timingListNode) {
                StageTiming timing = parseStageTiming(timingNode);
                stageTimingList.add(timing);
            }
        }
        plan.setStageTimingList(stageTimingList);
        return plan;
    }
    private StageTiming parseStageTiming(JsonNode timingNode) {
        StageTiming timing = new StageTiming();
        timing.setStageNo(timingNode.path("StageNo").asInt());
        timing.setGreen(timingNode.path("Green").asInt());
        timing.setYellow(timingNode.path("Yellow").asInt());
        timing.setAllRed(timingNode.path("AllRed").asInt());
        timing.setMaxGreen(timingNode.path("MaxGreen").asInt());
        timing.setMinGreen(timingNode.path("MinGreen").asInt());

        List<Adjust> adjustList = new ArrayList<>();
        JsonNode adjustListNode = timingNode.path("AdjustList");
        if (adjustListNode != null && adjustListNode.isArray()) {
            for (JsonNode adjustNode : adjustListNode) {
                Adjust adjust = new Adjust();
                adjust.setSignalGroupNo(adjustNode.path("SignalGroupNo").asInt());
                adjust.setOper(AdjustOperation.fromCode(adjustNode.path("Oper").asText()));
                adjust.setLen(adjustNode.path("Len").asInt());
                adjustList.add(adjust);
            }
        }
        timing.setAdjustList(adjustList);
        return timing;
    }
    private DayPlanParam parseDayPlanParam(JsonNode dayPlanNode) {
        DayPlanParam dayPlan = new DayPlanParam();
        dayPlan.setCrossId(dayPlanNode.path("CrossID").asText());
        dayPlan.setDayPlanNo(dayPlanNode.path("DayPlanNo").asInt());
        dayPlan.setDayPlanName(dayPlanNode.path("DayPlanName").asText());

        List<Period> periodList = new ArrayList<>();
        JsonNode periodListNode = dayPlanNode.path("PeriodList");
        if (periodListNode != null && periodListNode.isArray()) {
            for (JsonNode periodNode : periodListNode) {
                Period period = new Period();
                period.setStartTime(periodNode.path("StartTime").asText());
                period.setPlanNo(periodNode.path("PlanNo").asInt());
                period.setCtrlMode(periodNode.path("CtrlMode").asText());
                periodList.add(period);
            }
        }
        dayPlan.setPeriodList(periodList);
        return dayPlan;
    }
    private ScheduleParam parseScheduleParam(JsonNode scheduleNode) {
        ScheduleParam schedule = new ScheduleParam();
        schedule.setCrossId(scheduleNode.path("CrossID").asText());
        schedule.setScheduleNo(scheduleNode.path("ScheduleNo").asInt());
        schedule.setScheduleName(scheduleNode.path("ScheduleName").asText());
        schedule.setType(ScheduleType.fromCode(scheduleNode.path("Type").asText()));
        schedule.setStartDay(scheduleNode.path("StartDay").asText());
        schedule.setEndDay(scheduleNode.path("EndDay").asText());
        schedule.setWeekDay(scheduleNode.path("WeekDay").asInt());
        schedule.setDayPlanNo(scheduleNode.path("DayPlanNo").asInt());
        return schedule;
    }
    private CrossState parseCrossState(JsonNode crossStateNode) {
        CrossState crossState = new CrossState();
        crossState.setCrossId(crossStateNode.path("CrossID").asText());
        crossState.setValue(SystemState.fromCode(crossStateNode.path("Value").asText()));
        return crossState;
    }
    private SignalControllerError parseSignalControllerError(JsonNode errorNode) {
        SignalControllerError error = new SignalControllerError();
        error.setSignalControllerId(errorNode.path("SignalControllerID").asText());
        error.setErrorType(ControllerErrorType.fromCode(errorNode.path("ErrorType").asText()));
        error.setErrorDesc(errorNode.path("ErrorDesc").asText());
        error.setOccurTime(errorNode.path("OccurTime").asText());
        return error;
    }
    private CrossCtrlInfo parseCrossCtrlInfo(JsonNode ctrlInfoNode) {
        CrossCtrlInfo ctrlInfo = new CrossCtrlInfo();
        ctrlInfo.setCrossId(ctrlInfoNode.path("CrossID").asText());
        ctrlInfo.setControlMode(ControlMode.fromCode(ctrlInfoNode.path("ControlMode").asText()));
        ctrlInfo.setPlanNo(ctrlInfoNode.path("PlanNo").asInt());
        ctrlInfo.setTime(ctrlInfoNode.path("Time").asText());
        return ctrlInfo;
    }
    private CrossCycle parseCrossCycle(JsonNode cycleNode) {
        CrossCycle cycle = new CrossCycle();
        cycle.setCrossId(cycleNode.path("CrossID").asText());
        cycle.setStartTime(cycleNode.path("StartTime").asText());
        cycle.setLastCycleLen(cycleNode.path("LastCycleLen").asInt());
        cycle.setAdjustFlag(cycleNode.path("AdjustFlag").asInt());
        return cycle;
    }
    private CrossStage parseCrossStage(JsonNode stageNode) {
        CrossStage stage = new CrossStage();
        stage.setCrossId(stageNode.path("CrossID").asText());
        stage.setLastStageNo(stageNode.path("LastStageNo").asInt());
        stage.setLastStageLen(stageNode.path("LastStageLen").asInt());
        stage.setCurStageNo(stageNode.path("CurStageNo").asInt());
        stage.setCurStageStartTime(stageNode.path("CurStageStartTime").asText());
        stage.setCurStageLen(stageNode.path("CurStageLen").asInt());
        return stage;
    }
    private CrossSignalGroupStatus parseCrossSignalGroupStatus(JsonNode statusNode) {
        CrossSignalGroupStatus status = new CrossSignalGroupStatus();
        status.setCrossId(statusNode.path("CrossID").asText());
        status.setLampStatusTime(statusNode.path("LampStatusTime").asText());

        List<SignalGroupStatus> statusList = new ArrayList<>();
        JsonNode statusListNode = statusNode.path("SignalGroupStatusList");
        if (statusListNode != null && statusListNode.isArray()) {
            for (JsonNode detailNode : statusListNode) {
                SignalGroupStatus detail = new SignalGroupStatus();
                detail.setSignalGroupNo(detailNode.path("SignalGroupNo").asInt());
                detail.setLampStatus(detailNode.path("LampStatus").asText());
                detail.setRemainTime(detailNode.path("RemainTime").asInt());
                statusList.add(detail);
            }
        }
        status.setSignalGroupStatusList(statusList);
        return status;
    }
    private CrossTrafficData parseCrossTrafficData(JsonNode trafficDataNode) {
        CrossTrafficData trafficData = new CrossTrafficData();
        trafficData.setCrossId(trafficDataNode.path("CrossID").asText());
        trafficData.setEndTime(trafficDataNode.path("EndTime").asText());
        trafficData.setInterval(trafficDataNode.path("Interval").asInt());

        List<LaneTrafficData> dataList = new ArrayList<>();
        JsonNode dataListNode = trafficDataNode.path("DataList");
        if (dataListNode != null && dataListNode.isArray()) {
            for (JsonNode dataNode : dataListNode) {
                LaneTrafficData laneData = parseLaneTrafficData(dataNode);
                dataList.add(laneData);
            }
        }
        trafficData.setDataList(dataList);
        return trafficData;
    }
    private LaneTrafficData parseLaneTrafficData(JsonNode dataNode) {
        LaneTrafficData laneData = new LaneTrafficData();
        laneData.setLaneNo(dataNode.path("LaneNo").asInt());
        laneData.setVolume(dataNode.path("Volume").asInt());
        laneData.setAvgVehLen(dataNode.path("AvgVehLen").decimalValue());
        laneData.setPcu(dataNode.path("Pcu").asInt());
        laneData.setHeadDistance(dataNode.path("HeadDistance").decimalValue());
        laneData.setHeadTime(dataNode.path("HeadTime").decimalValue());
        laneData.setSpeed(dataNode.path("Speed").decimalValue());
        laneData.setSaturation(dataNode.path("Saturation").decimalValue());
        laneData.setDensity(dataNode.path("Density").asInt());
        laneData.setQueueLength(dataNode.path("QueueLength").decimalValue());
        laneData.setMaxQueueLength(dataNode.path("MaxQueueLength").decimalValue());
        laneData.setOccupancy(dataNode.path("Occupancy").asInt());
        return laneData;
    }
    private StageTrafficData parseStageTrafficData(JsonNode stageDataNode) {
        StageTrafficData stageData = new StageTrafficData();
        stageData.setCrossId(stageDataNode.path("CrossID").asText());
        stageData.setStartTime(stageDataNode.path("StartTime").asText());
        stageData.setEndTime(stageDataNode.path("EndTime").asText());
        stageData.setStageNo(stageDataNode.path("StageNo").asInt());

        List<StageTrafficFlowData> dataList = new ArrayList<>();
        JsonNode dataListNode = stageDataNode.path("DataList");
        if (dataListNode != null && dataListNode.isArray()) {
            for (JsonNode dataNode : dataListNode) {
                StageTrafficFlowData laneData = parseStageLaneData(dataNode);
                dataList.add(laneData);
            }
        }
        stageData.setDataList(dataList);
        return stageData;
    }
    private StageTrafficFlowData parseStageLaneData(JsonNode dataNode) {
        StageTrafficFlowData laneData = new StageTrafficFlowData();
        laneData.setLaneNo(dataNode.path("LaneNo").asInt());
        laneData.setVehicleNum(dataNode.path("VehicleNum").asInt());
        laneData.setPcu(dataNode.path("Pcu").asInt());
        laneData.setHeadTime(dataNode.path("HeadTime").decimalValue());
        laneData.setSaturation(dataNode.path("Saturation").decimalValue());
        laneData.setQueueLength(dataNode.path("QueueLength").decimalValue());
        laneData.setOccupancy(dataNode.path("Occupancy").asInt());
        return laneData;
    }
    private VarLaneStatus parseVarLaneStatus(JsonNode varLaneNode) {
        VarLaneStatus varLane = new VarLaneStatus();
        varLane.setCrossId(varLaneNode.path("CrossID").asText());
        varLane.setLaneNo(varLaneNode.path("LaneNo").asInt());
        varLane.setCurMovement(LaneMovement.fromCode(varLaneNode.path("CurMovement").asText()));
        varLane.setCurMode(VarLaneMode.fromCode(varLaneNode.path("CurMode").asText()));
        return varLane;
    }
    private RouteCtrlInfo parseRouteCtrlInfo(JsonNode routeCtrlNode) {
        RouteCtrlInfo routeCtrl = new RouteCtrlInfo();
        routeCtrl.setRouteId(routeCtrlNode.path("RouteID").asText());
        routeCtrl.setValue(RouteControlMode.fromCode(routeCtrlNode.path("CtrlMode").asText()));
        return routeCtrl;
    }
    private RouteSpeed parseRouteSpeed(JsonNode routeSpeedNode) {
        RouteSpeed routeSpeed = new RouteSpeed();
        routeSpeed.setRouteId(routeSpeedNode.path("RouteID").asText());

        List<RoadSectionSpeed> speedList = new ArrayList<>();
        JsonNode speedListNode = routeSpeedNode.path("RoadSectionSpeedList");
        if (speedListNode != null && speedListNode.isArray()) {
            for (JsonNode speedNode : speedListNode) {
                RoadSectionSpeed sectionSpeed = new RoadSectionSpeed();
                sectionSpeed.setUpCrossId(speedNode.path("UpCrossID").asText());
                sectionSpeed.setDownCrossId(speedNode.path("DownCrossID").asText());
                sectionSpeed.setRecommendSpeed(speedNode.path("RecommendSpeed").asInt());
                speedList.add(sectionSpeed);
            }
        }
        routeSpeed.setRoadSectionSpeedList(speedList);
        return routeSpeed;
    }
    private SCDoorStatus parseSCDoorStatus(JsonNode doorStatusNode) {
        SCDoorStatus doorStatus = new SCDoorStatus();
        doorStatus.setSignalControllerId(doorStatusNode.path("SignalControllerID").asText());
        doorStatus.setTime(doorStatusNode.path("Time").asText());

        List<SCDoorStatus.DoorStatusItem> doorList = new ArrayList<>();
        JsonNode doorListNode = doorStatusNode.path("DoorStatusList");
        if (doorListNode != null && doorListNode.isArray()) {
            for (JsonNode doorNode : doorListNode) {
                SCDoorStatus.DoorStatusItem door = new SCDoorStatus.DoorStatusItem();
                door.setDoorNo(doorNode.path("DoorNo").asInt());
                door.setDoorName(doorNode.path("DoorName").asText());
                door.setStatus(DoorStatus.fromCode(doorNode.path("Status").asText()));
                doorList.add(door);
            }
        }
        doorStatus.setDoorStatusList(new SCDoorStatus.DoorStatusList(doorList));
        return doorStatus;
    }
    private List<String> parseStringList(JsonNode parentNode, String fieldName) {
        List<String> result = new ArrayList<>();
        JsonNode listNode = parentNode.get(fieldName);

        if (listNode != null && listNode.isArray()) {
            listNode.forEach(node -> result.add(node.asText()));
        }

        return result;
    }
    private List<Integer> parseIntList(JsonNode parentNode, String fieldName) {
        List<Integer> result = new ArrayList<>();
        JsonNode listNode = parentNode.get(fieldName);

        if (listNode != null && listNode.isArray()) {
            listNode.forEach(node -> result.add(node.asInt()));
        }

        return result;
    }
}