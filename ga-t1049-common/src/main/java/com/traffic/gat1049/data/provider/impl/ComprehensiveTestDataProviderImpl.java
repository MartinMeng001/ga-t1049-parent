package com.traffic.gat1049.data.provider.impl;

import com.traffic.gat1049.data.provider.ComprehensiveTestDataProvider;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.enums.*;
import com.traffic.gat1049.protocol.model.intersection.*;
import com.traffic.gat1049.protocol.model.system.*;
import com.traffic.gat1049.protocol.model.signal.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 综合测试数据提供者实现
 * 从JSON测试数据文件中加载并提供所有类型的测试数据
 */
public class ComprehensiveTestDataProviderImpl implements ComprehensiveTestDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveTestDataProviderImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode testDataRoot;
    private boolean initialized = false;

    // 缓存解析后的数据，提高访问性能
    private final Map<String, Object> dataCache = new ConcurrentHashMap<>();

    // 1. Static instance of the class
    private static ComprehensiveTestDataProviderImpl instance;

    // 2. Private constructor
    private ComprehensiveTestDataProviderImpl() {
        // Prevent external instantiation
    }

    // 3. Static method to get the instance
    public static synchronized ComprehensiveTestDataProviderImpl getInstance() throws BusinessException {
        if (instance == null) {
            instance = new ComprehensiveTestDataProviderImpl();
            // 首次获取实例时进行初始化
            try {
                instance.initialize(); // 在这里确保只初始化一次
            } catch (BusinessException e) {
                // 如果初始化失败，需要处理或重新抛出异常
                instance = null; // 清除实例，以便下次尝试重新初始化
                throw e;
            }
        }
        return instance;
    }

    @Override
    public void initialize() throws BusinessException {
        if (initialized) {
            //logger.info("数据提供者已初始化，跳过重复初始化。");
            return;
        }
        try {
            logger.info("初始化综合测试数据提供者...");
            loadTestDataFromJson(); // 实际加载数据
            if (testDataRoot == null) {
                throw new BusinessException("INIT_ERROR", "无法加载测试数据");
            }
            preloadCommonData(); // 预加载和缓存
            initialized = true;
            logger.info("综合测试数据提供者初始化完成");
        } catch (Exception e) {
            logger.error("综合测试数据提供者初始化失败", e);
            // 初始化失败时，将 initialized 设为 false，确保后续调用会再次尝试初始化
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
                    String stateValue = sysStateNode.path("Value").asText("Online");
                    SystemState systemState = SystemState.fromCode(stateValue);

                    SysState sysState = new SysState(systemState);
//                    sysState.setStateTime(LocalDateTime.now());

                    dataCache.put(cacheKey, sysState);
                    return sysState;
                }
            }

            // 默认状态
            SysState defaultState = new SysState(SystemState.ONLINE);
//            defaultState.setStateTime(LocalDateTime.now());
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
                .filter(controller -> signalControllerId.equals(controller.getSignalControllerId()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("未找到信号机ID: " + signalControllerId));
    }

    @Override
    public List<SignalController> getSignalControllersByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<SignalController> allControllers = getAllSignalControllers();
        return allControllers.stream()
                .filter(controller -> controller.getCrossIdList() != null &&
                        controller.getCrossIdList().contains(crossId))
                .collect(Collectors.toList());
    }

    // ==================== 灯组管理相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<LampGroup> getAllLampGroups() throws BusinessException {
        ensureInitialized();

        String cacheKey = "AllLampGroups";
        if (dataCache.containsKey(cacheKey)) {
            return (List<LampGroup>) dataCache.get(cacheKey);
        }

        try {
            List<LampGroup> lampGroups = new ArrayList<>();
            JsonNode lampGroupArray = testDataRoot.get("LightGroup");

            if (lampGroupArray != null && lampGroupArray.isArray()) {
                for (JsonNode lampGroupNode : lampGroupArray) {
                    LampGroup lampGroup = parseLampGroup(lampGroupNode);
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
    public List<LampGroup> getLampGroupsByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }

        List<LampGroup> allLampGroups = getAllLampGroups();
        return allLampGroups.stream()
                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()))
                .collect(Collectors.toList());
    }

    @Override
    public LampGroup getLampGroupByCrossIdAndNo(String crossId, String lampGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口ID不能为空");
        }
        if (lampGroupNo == null || lampGroupNo.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "灯组编号不能为空");
        }

        List<LampGroup> lampGroups = getLampGroupsByCrossId(crossId);
        return lampGroups.stream()
                //.filter(lampGroup -> lampGroupNo.equals(lampGroup.getLampGroupNo()))
                .filter(lampGroup -> Integer.parseInt(lampGroupNo)==(lampGroup.getLampGroupNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到灯组: 路口ID=%s, 灯组编号=%s", crossId, lampGroupNo)));
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
                .filter(detector -> Integer.parseInt(detectorNo) == (detector.getDetectorNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到检测器: 路口ID=%s, 检测器编号=%s", crossId, detectorNo)));
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
                .filter(lane -> Integer.parseInt(laneNo)==(lane.getLaneNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到车道: 路口ID=%s, 车道编号=%s", crossId, laneNo)));
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
                .filter(pedestrian -> Integer.parseInt(pedestrianNo)==(pedestrian.getPedestrianNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到行人参数: 路口ID=%s, 行人编号=%s", crossId, pedestrianNo)));
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
            JsonNode signalGroupArray = testDataRoot.get("SignalGroup");

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
                .filter(signalGroup -> Integer.parseInt(signalGroupNo) == (signalGroup.getSignalGroupNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到信号组: 路口ID=%s, 信号组编号=%s", crossId, signalGroupNo)));
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
                .filter(stage -> Integer.parseInt(stageNo)==(stage.getStageNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到阶段: 路口ID=%s, 阶段编号=%s", crossId, stageNo)));
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
            throw new BusinessException("INVALID_PARAMETER", "方案编号不能为空");
        }

        List<PlanParam> plans = getPlansByCrossId(crossId);
        return plans.stream()
                .filter(plan -> Integer.parseInt(planNo)==(plan.getPlanNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到配时方案: 路口ID=%s, 方案编号=%s", crossId, planNo)));
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
            JsonNode dayPlanArray = testDataRoot.get("DayPlanParam");

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
                .filter(dayPlan -> Integer.parseInt(dayPlanNo) == (dayPlan.getDayPlanNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到日计划: 路口ID=%s, 日计划编号=%s", crossId, dayPlanNo)));
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
                .filter(schedule -> Integer.parseInt(scheduleNo)==(schedule.getScheduleNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("未找到调度: 路口ID=%s, 调度编号=%s", crossId, scheduleNo)));
    }

    // ==================== 运行状态相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllCrossStates() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("CrossState");
    }

    @Override
    public Object getCrossStateById(String crossId) throws BusinessException {
        List<Object> crossStates = getAllCrossStates();
        return findByIdInList(crossStates, "CrossID", crossId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllSignalTroubles() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("SignalTrouble");
    }

    @Override
    public List<Object> getSignalTroublesByControllerId(String signalControllerId) throws BusinessException {
        List<Object> troubles = getAllSignalTroubles();
        return filterListByField(troubles, "SignalControlerID", signalControllerId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllCrossModePlans() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("CrossModePlan");
    }

    @Override
    public Object getCrossModePlanById(String crossId) throws BusinessException {
        List<Object> modePlans = getAllCrossModePlans();
        return findByIdInList(modePlans, "CrossID", crossId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllCrossCycles() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("CrossCycle");
    }

    @Override
    public Object getCrossCycleById(String crossId) throws BusinessException {
        List<Object> cycles = getAllCrossCycles();
        return findByIdInList(cycles, "CrossID", crossId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllCrossStages() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("CrossStage");
    }

    @Override
    public Object getCrossStageById(String crossId) throws BusinessException {
        List<Object> stages = getAllCrossStages();
        return findByIdInList(stages, "CrossID", crossId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllCrossSignalGroupStatus() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("CrossSignalGroupStatus");
    }

    @Override
    public Object getCrossSignalGroupStatusById(String crossId) throws BusinessException {
        List<Object> statusList = getAllCrossSignalGroupStatus();
        return findByIdInList(statusList, "CrossID", crossId);
    }

    // ==================== 交通数据相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllCrossTrafficData() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("CrossTrafficData");
    }

    @Override
    public Object getCrossTrafficDataById(String crossId) throws BusinessException {
        List<Object> trafficData = getAllCrossTrafficData();
        return findByIdInList(trafficData, "CrossID", crossId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllStageTrafficData() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("StageTrafficData");
    }

    @Override
    public List<Object> getStageTrafficDataByCrossIdAndStageNo(String crossId, String stageNo) throws BusinessException {
        List<Object> stageData = getAllStageTrafficData();
        return stageData.stream()
                .filter(data -> {
                    if (data instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> dataMap = (Map<String, Object>) data;
                        return crossId.equals(dataMap.get("CrossID")) &&
                                stageNo.equals(dataMap.get("StageNo"));
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // ==================== 可变车道和干线控制相关实现 ====================

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllVarLaneStatus() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("VarLaneStatus");
    }

    @Override
    public List<Object> getVarLaneStatusByCrossId(String crossId) throws BusinessException {
        List<Object> varLanes = getAllVarLaneStatus();
        return filterListByField(varLanes, "CrossID", crossId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllRouteControlModes() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("RouteControlMode");
    }

    @Override
    public Object getRouteControlModeById(String routeId) throws BusinessException {
        List<Object> controlModes = getAllRouteControlModes();
        return findByIdInList(controlModes, "RouteID", routeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllRouteSpeeds() throws BusinessException {
        ensureInitialized();
        return (List<Object>) getRunStatusData("RouteSpeed");
    }

    @Override
    public Object getRouteSpeedById(String routeId) throws BusinessException {
        List<Object> routeSpeeds = getAllRouteSpeeds();
        return findByIdInList(routeSpeeds, "RouteID", routeId);
    }

    // ==================== 通用方法实现 ====================

    @Override
    public String getDataStatistics() {
        if (!initialized) {
            return "测试数据未初始化";
        }

        try {
            StringBuilder stats = new StringBuilder();
            stats.append("=== 测试数据统计 ===\n");

            // 系统信息统计
            stats.append(String.format("系统信息: %s\n", getSystemInfo().getSysName()));
            stats.append(String.format("系统状态: %s\n", getSystemState().getValue().getDescription()));

            // 各类数据统计
            stats.append(String.format("区域数量: %d\n", getAllRegions().size()));
            stats.append(String.format("子区数量: %d\n", getAllSubRegions().size()));
            stats.append(String.format("线路数量: %d\n", getAllRoutes().size()));
            stats.append(String.format("路口数量: %d\n", getAllCrosses().size()));
            stats.append(String.format("信号机数量: %d\n", getAllSignalControllers().size()));
            stats.append(String.format("灯组数量: %d\n", getAllLampGroups().size()));
            stats.append(String.format("检测器数量: %d\n", getAllDetectors().size()));
            stats.append(String.format("车道数量: %d\n", getAllLanes().size()));
            stats.append(String.format("行人参数数量: %d\n", getAllPedestrians().size()));
            stats.append(String.format("信号组数量: %d\n", getAllSignalGroups().size()));
            stats.append(String.format("阶段数量: %d\n", getAllStages().size()));
            stats.append(String.format("配时方案数量: %d\n", getAllPlans().size()));
            stats.append(String.format("日计划数量: %d\n", getAllDayPlans().size()));
            stats.append(String.format("调度数量: %d\n", getAllSchedules().size()));

            // 运行状态统计
            stats.append(String.format("路口状态数量: %d\n", getAllCrossStates().size()));
            stats.append(String.format("信号机故障数量: %d\n", getAllSignalTroubles().size()));
            stats.append(String.format("交通数据记录数量: %d\n", getAllCrossTrafficData().size()));

            stats.append(String.format("缓存数据项数量: %d\n", dataCache.size()));

            return stats.toString();
        } catch (Exception e) {
            return "获取数据统计失败: " + e.getMessage();
        }
    }

    @Override
    public List<Object> getTestDataByObjectName(String objectName) throws BusinessException {
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "对象名称不能为空");
        }

        ensureInitialized();

        switch (objectName.toLowerCase()) {
            case "regionparam":
                return new ArrayList<>(getAllRegions());
            case "subregionparam":
                return new ArrayList<>(getAllSubRegions());
            case "routeparam":
                return new ArrayList<>(getAllRoutes());
            case "crossparam":
                return new ArrayList<>(getAllCrosses());
            case "signalcontroller":
                return new ArrayList<>(getAllSignalControllers());
            case "lampgroup":
                return new ArrayList<>(getAllLampGroups());
            case "detectorparam":
                return new ArrayList<>(getAllDetectors());
            case "laneparam":
                return new ArrayList<>(getAllLanes());
            case "pedestrianparam":
                return new ArrayList<>(getAllPedestrians());
            case "signalgroupparam":
                return new ArrayList<>(getAllSignalGroups());
            case "stageparam":
                return new ArrayList<>(getAllStages());
            case "planparam":
                return new ArrayList<>(getAllPlans());
            case "dayplanparam":
                return new ArrayList<>(getAllDayPlans());
            case "scheduleparam":
                return new ArrayList<>(getAllSchedules());
            default:
                throw new DataNotFoundException("不支持的对象名称: " + objectName);
        }
    }

    @Override
    public Object getTestDataByObjectNameAndId(String objectName, String id) throws BusinessException {
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "对象名称不能为空");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "ID不能为空");
        }

        switch (objectName.toLowerCase()) {
            case "regionparam":
                return getRegionById(id);
            case "subregionparam":
                return getSubRegionById(id);
            case "routeparam":
                return getRouteById(id);
            case "crossparam":
                return getCrossById(id);
            case "signalcontroller":
                return getSignalControllerById(id);
            default:
                throw new DataNotFoundException("不支持通过ID查询的对象类型: " + objectName);
        }
    }

    @Override
    public Object getTestDataByObjectNameIdAndNo(String objectName, String id, String no) throws BusinessException {
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "对象名称不能为空");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "ID不能为空");
        }
        if (no == null || no.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "编号不能为空");
        }

        switch (objectName.toLowerCase()) {
            case "lampgroup":
                return getLampGroupByCrossIdAndNo(id, no);
            case "detectorparam":
                return getDetectorByCrossIdAndNo(id, no);
            case "laneparam":
                return getLaneByCrossIdAndNo(id, no);
            case "pedestrianparam":
                return getPedestrianByCrossIdAndNo(id, no);
            case "signalgroupparam":
                return getSignalGroupByCrossIdAndNo(id, no);
            case "stageparam":
                return getStageByCrossIdAndNo(id, no);
            case "planparam":
                return getPlanByCrossIdAndNo(id, no);
            case "dayplanparam":
                return getDayPlanByCrossIdAndNo(id, no);
            case "scheduleparam":
                return getScheduleByCrossIdAndNo(id, no);
            default:
                throw new DataNotFoundException("不支持通过ID和编号查询的对象类型: " + objectName);
        }
    }

    // ==================== 私有辅助方法 ====================

    private void ensureInitialized() throws BusinessException {
        if (!initialized) {
            // 如果在 getInstance() 之外调用，或者 getInstance() 初始化失败
            // 且后续又需要数据，这里可以抛出异常，或者根据业务逻辑选择再次尝试初始化 (不推荐)
            //throw new BusinessException("NOT_INITIALIZED", "数据提供者未初始化，请先调用 getInstance() 获取实例。");
        }
    }

    private void loadTestDataFromJson() throws BusinessException {
        try {
            // 先尝试从类路径加载JSON文件
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("testdata.json");

            if (inputStream != null) {
                testDataRoot = objectMapper.readTree(inputStream);
                inputStream.close();
                logger.info("从文件加载测试数据成功");
            } else {
                // 使用内置测试数据
                String builtInData = getBuiltInTestData();
                testDataRoot = objectMapper.readTree(builtInData);
                logger.info("使用内置测试数据");
            }
        } catch (Exception e) {
            throw new BusinessException("DATA_LOAD_ERROR", "加载测试数据失败: " + e.getMessage());
        }
    }

    private void preloadCommonData() throws BusinessException {
        // 预加载系统信息和状态
        getSystemInfo();
        getSystemState();

        // 预加载常用配置数据
        getAllRegions();
        getAllSubRegions();
        getAllRoutes();
        getAllCrosses();
        getAllSignalControllers();

        logger.debug("预加载常用测试数据完成");
    }

    private Object getRunStatusData(String dataType) throws BusinessException {
        String cacheKey = "RunStatus_" + dataType;
        if (dataCache.containsKey(cacheKey)) {
            return dataCache.get(cacheKey);
        }

        try {
            JsonNode runStatusNode = testDataRoot.get("RunStatus");
            if (runStatusNode != null) {
                JsonNode dataNode = runStatusNode.get(dataType);
                if (dataNode != null) {
                    Object data = objectMapper.convertValue(dataNode, Object.class);
                    dataCache.put(cacheKey, data);
                    return data;
                }
            }

            // 返回空列表而不是null
            List<Object> emptyList = new ArrayList<>();
            dataCache.put(cacheKey, emptyList);
            return emptyList;

        } catch (Exception e) {
            throw new BusinessException("PARSE_ERROR", "解析运行状态数据失败: " + dataType + " - " + e.getMessage());
        }
    }

    private Object findByIdInList(List<Object> list, String idField, String idValue) throws BusinessException {
        for (Object item : list) {
            if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) item;
                if (idValue.equals(itemMap.get(idField))) {
                    return item;
                }
            }
        }
        throw new DataNotFoundException(String.format("未找到%s=%s的记录", idField, idValue));
    }

    private List<Object> filterListByField(List<Object> list, String fieldName, String fieldValue) {
        return list.stream()
                .filter(item -> {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        return fieldValue.equals(itemMap.get(fieldName));
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // ==================== 解析方法 ====================

    private SysInfo parseSystemInfo(JsonNode sysParamNode) {
        SysInfo sysInfo = new SysInfo();
        sysInfo.setSysName(sysParamNode.path("SysName").asText("智能交通信号控制系统"));
        sysInfo.setSysVersion(String.valueOf(sysParamNode.path("SysVersion").asDouble(2.0)));
        sysInfo.setSupplier(sysParamNode.path("Supplier").asText("山东双百电子有限公司"));
        sysInfo.setCrossIdList(parseStringList(sysParamNode, "CrossIDList"));
        sysInfo.setRouteIdList(parseStringList(sysParamNode, "RouteIDList"));
        sysInfo.setRegionIdList(parseStringList(sysParamNode, "RegionIDList"));
        sysInfo.setSignalControllerIdList(parseStringList(sysParamNode, "SignalControlerIDList"));
//        sysInfo.setCreateTime(LocalDateTime.now());
//        sysInfo.setUpdateTime(LocalDateTime.now());
        return sysInfo;
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
        route.setType(RouteType.fromCode(routeNode.path("Type").asText()));
        route.setSubRegionIdList(parseStringList(routeNode, "SubRegionIDList"));

        // 解析RouteCrossList
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

    /**
     * 解析线路路口信息
     */
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
        cross.setFeature(CrossFeature.fromCode(crossNode.path("Feature").asText()));
        cross.setGrade(CrossGrade.fromCode(crossNode.path("Grade").asText()));
        cross.setDetNoList(parseIntList(crossNode, "DetNoList"));
        cross.setLaneNoList(parseIntList(crossNode, "LaneNoList"));
        cross.setLampGroupNoList(parseIntList(crossNode, "LampGroupNoList"));
        cross.setSignalGroupNoList(parseIntList(crossNode, "SignalGroupNoList"));
        cross.setStageNoList(parseIntList(crossNode, "StageNoList"));
        cross.setPlanNoList(parseIntList(crossNode, "PlanNoList"));
        cross.setDayPlanNoList(parseIntList(crossNode, "DayPlanNoList"));
        cross.setScheduleNoList(parseIntList(crossNode, "ScheduleNoList"));
        cross.setLongitude(crossNode.path("Longitude").asDouble());
        cross.setLatitude(crossNode.path("Latitude").asDouble());
        cross.setAltitude(crossNode.path("Altitude").asDouble());
        return cross;
    }

    private SignalController parseSignalController(JsonNode controllerNode) {
        SignalController controller = new SignalController();
        controller.setSignalControllerId(controllerNode.path("SignalControlerID").asText());
        controller.setSupplier(controllerNode.path("Supplier").asText());
        controller.setType(controllerNode.path("Type").asText());
        controller.setCommMode(CommMode.fromCode(controllerNode.path("CommMode").asText()));
        controller.setCrossIdList(parseStringList(controllerNode, "CrossIDList"));
        return controller;
    }

    private LampGroup parseLampGroup(JsonNode lampGroupNode) {
        LampGroup lampGroup = new LampGroup();
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
        lane.setMovement(LaneMovement.fromCode(laneNode.path("Movement").asText()));
        lane.setFeature(LaneFeature.fromCode(laneNode.path("Feature").asText()));
        lane.setAzimuth(laneNode.path("Azimuth").asInt());
        lane.setWaitingArea(laneNode.path("WaitingArea").asInt());
        lane.setVarMovementList(parseLaneMovementList(laneNode, "VarMovementList"));
        return lane;
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
        signalGroup.setGreenFlushLen(signalGroupNode.path("GreenFlushLen").asInt());
        signalGroup.setLampGroupNoList(parseIntList(signalGroupNode, "LampGroupNoList"));
        return signalGroup;
    }

    private StageParam parseStageParam(JsonNode stageNode) {
        StageParam stage = new StageParam();
        stage.setCrossId(stageNode.path("CrossID").asText());
        stage.setStageNo(stageNode.path("StageNo").asInt());
        stage.setStageName(stageNode.path("StageName").asText());
        stage.setAttribute(stageNode.path("Attribute").asInt());

        // 解析SignalGroupStatusList
        stage.setSignalGroupStatusList(parseSignalGroupStatusList(stageNode, "SignalGroupStatusList"));

        return stage;
    }

    /**
     * 解析信号组状态列表
     *
     * @param parentNode 父节点
     * @param fieldName 字段名称
     * @return 信号组状态列表
     */
    private List<SignalGroupStatus> parseSignalGroupStatusList(JsonNode parentNode, String fieldName) {
        List<SignalGroupStatus> statusList = new ArrayList<>();

        JsonNode statusArray = parentNode.path(fieldName);
        if (statusArray != null && statusArray.isArray()) {
            for (JsonNode statusNode : statusArray) {
                SignalGroupStatus status = parseSignalGroupStatus(statusNode);
                statusList.add(status);
            }
        }

        return statusList;
    }

    /**
     * 解析单个信号组状态
     *
     * @param statusNode 状态节点
     * @return 信号组状态对象
     */
    private SignalGroupStatus parseSignalGroupStatus(JsonNode statusNode) {
        SignalGroupStatus status = new SignalGroupStatus();

        // 解析信号组序号
        status.setSignalGroupNo(statusNode.path("SignalGroupNo").asInt());

        // 解析灯态状态，使用LampStatus枚举的fromCode方法
        String lampStatusCode = statusNode.path("LampStatus").asText();
        if (lampStatusCode != null && !lampStatusCode.trim().isEmpty()) {
            try {
                status.setLampStatus(LampStatus.fromCode(lampStatusCode));
            } catch (IllegalArgumentException e) {
                // 记录日志并设置默认值或抛出更友好的异常
                logger.warn("未知的灯态代码: {}, 使用默认值OFF", lampStatusCode);
                status.setLampStatus(LampStatus.OFF);
            }
        }

        return status;
    }

    private PlanParam parsePlanParam(JsonNode planNode) {
        PlanParam plan = new PlanParam();
        plan.setCrossId(planNode.path("CrossID").asText());
        plan.setPlanNo(planNode.path("PlanNo").asInt());
        plan.setPlanName(planNode.path("PlanName").asText());
        plan.setCycleLen(planNode.path("CycleLen").asInt());
        plan.setCoordStageNo(planNode.path("CoordStageNo").asInt());
        plan.setOffset(planNode.path("Offset").asInt());

        // 解析StageTimingList - 现在有完整实现
        plan.setStageTimingList(parseStageTimingList(planNode, "StageTimingList"));

        return plan;
    }

    /**
     * 解析阶段配时信息列表
     * @param parentNode 父JSON节点
     * @param fieldName 字段名称
     * @return 阶段配时信息列表
     */
    private List<StageTiming> parseStageTimingList(JsonNode parentNode, String fieldName) {
        List<StageTiming> result = new ArrayList<>();
        JsonNode listNode = parentNode.get(fieldName);

        if (listNode != null && listNode.isArray()) {
            for (JsonNode timingNode : listNode) {
                StageTiming timing = parseStageTiming(timingNode);
                result.add(timing);
            }
        }

        return result;
    }

    /**
     * 解析单个阶段配时信息
     * @param timingNode 阶段配时JSON节点
     * @return 阶段配时信息对象
     */
    private StageTiming parseStageTiming(JsonNode timingNode) {
        StageTiming timing = new StageTiming();

        // 设置基本配时信息
        timing.setStageNo(timingNode.path("StageNo").asInt());
        timing.setGreen(timingNode.path("Green").asInt());
        timing.setYellow(timingNode.path("Yellow").asInt());
        timing.setAllRed(timingNode.path("AllRed").asInt());

        // 设置可选的最大最小绿灯时间
        if (timingNode.has("MaxGreen") && !timingNode.path("MaxGreen").isNull()) {
            timing.setMaxGreen(timingNode.path("MaxGreen").asInt());
        }
        if (timingNode.has("MinGreen") && !timingNode.path("MinGreen").isNull()) {
            timing.setMinGreen(timingNode.path("MinGreen").asInt());
        }

        // 解析AdjustList（迟开早闭配置列表）
        timing.setAdjustList(parseAdjustList(timingNode, "AdjustList"));

        return timing;
    }

    /**
     * 解析迟开早闭配置列表
     * @param parentNode 父JSON节点
     * @param fieldName 字段名称
     * @return 迟开早闭配置列表
     */
    private List<Adjust> parseAdjustList(JsonNode parentNode, String fieldName) {
        List<Adjust> result = new ArrayList<>();
        JsonNode listNode = parentNode.get(fieldName);

        if (listNode != null && listNode.isArray()) {
            for (JsonNode adjustNode : listNode) {
                Adjust adjust = parseAdjust(adjustNode);
                result.add(adjust);
            }
        }

        return result;
    }

    /**
     * 解析单个迟开早闭配置
     * @param adjustNode 迟开早闭配置JSON节点
     * @return 迟开早闭配置对象
     */
    private Adjust parseAdjust(JsonNode adjustNode) {
        Adjust adjust = new Adjust();

        // 设置信号组序号
        adjust.setSignalGroupNo(adjustNode.path("SignalGroupNo").asInt());

        // 设置操作类型（迟开/早闭）
        String operCode = adjustNode.path("Oper").asText();
        if (operCode != null && !operCode.isEmpty()) {
            adjust.setOper(AdjustOperation.fromCode(operCode));
        }

        // 设置调整时间长度
        adjust.setLen(adjustNode.path("Len").asInt());

        return adjust;
    }

    /**
     * 解析DayPlanParam，包含完整的PeriodList解析
     */
    private DayPlanParam parseDayPlanParam(JsonNode dayPlanNode) {
        DayPlanParam dayPlan = new DayPlanParam();
        dayPlan.setCrossId(dayPlanNode.path("CrossID").asText());
        dayPlan.setDayPlanNo(dayPlanNode.path("DayPlanNo").asInt());

        // 解析PeriodList
        List<Period> periodList = parsePeriodList(dayPlanNode, "PeriodList");
        dayPlan.setPeriodList(periodList);

        return dayPlan;
    }

    /**
     * 解析时段信息列表
     * @param parentNode 父JSON节点
     * @param fieldName 字段名称
     * @return 时段信息列表
     */
    private List<Period> parsePeriodList(JsonNode parentNode, String fieldName) {
        List<Period> periodList = new ArrayList<>();
        JsonNode listNode = parentNode.get(fieldName);

        if (listNode != null && listNode.isArray()) {
            for (JsonNode periodNode : listNode) {
                Period period = parsePeriod(periodNode);
                periodList.add(period);
            }
        }

        return periodList;
    }

    /**
     * 解析单个时段信息
     * @param periodNode 时段JSON节点
     * @return 时段信息对象
     */
    private Period parsePeriod(JsonNode periodNode) {
        Period period = new Period();

        // 解析开始时间 - 直接作为字符串处理，不再转换为LocalTime
        String startTimeStr = periodNode.path("StartTime").asText();
        if (startTimeStr != null && !startTimeStr.isEmpty() && !"null".equals(startTimeStr)) {
            // 使用Period类的标准化方法处理时间格式
            period.setStartTime(Period.normalizeTimeFormat(startTimeStr));
        } else {
            // 设置默认时间
            period.setStartTime("00:00:00");
        }

        // 解析配时方案号 - 根据testdata.json，PlanNo可能是字符串形式的数字（如"001"）
        JsonNode planNoNode = periodNode.get("PlanNo");
        if (planNoNode != null && !planNoNode.isNull()) {
            if (planNoNode.isTextual()) {
                String planNoStr = planNoNode.asText();
                if (planNoStr != null && !planNoStr.isEmpty() && !"null".equals(planNoStr)) {
                    try {
                        // 去除前导零后转换为整数
                        period.setPlanNo(Integer.parseInt(planNoStr));
                    } catch (NumberFormatException e) {
                        logger.warn("解析配时方案号失败: {}, 使用默认值1", planNoStr);
                        period.setPlanNo(1);
                    }
                } else {
                    period.setPlanNo(1);
                }
            } else {
                period.setPlanNo(planNoNode.asInt(1));
            }
        } else {
            period.setPlanNo(1);
        }

        // 解析控制方式
        String ctrlMode = periodNode.path("CtrlMode").asText();
        if (ctrlMode != null && !ctrlMode.isEmpty() && !"null".equals(ctrlMode)) {
            period.setCtrlMode(ctrlMode);
        } else {
            // 根据协议文档，设置默认控制方式
            period.setCtrlMode("21"); // 假设21为默认的时间优化控制方式
        }

        return period;
    }

    /**
     * 验证时段信息列表 - 更新版本
     * 适配 startTime 为 String 类型的验证
     */
    private void validatePeriods(List<Period> periods) throws BusinessException {
        if (periods == null || periods.isEmpty()) {
            throw new ValidationException("periodList", "时段信息列表不能为空");
        }

        // 检查时段是否按时间顺序排列
        for (int i = 0; i < periods.size() - 1; i++) {
            Period current = periods.get(i);
            Period next = periods.get(i + 1);

            if (current.getStartTime() == null || next.getStartTime() == null) {
                throw new ValidationException("startTime", "时段开始时间不能为空");
            }

            // 使用Period类的时间比较方法
            if (Period.compareTime(current.getStartTime(), next.getStartTime()) >= 0) {
                throw new ValidationException("periodList", "时段必须按开始时间升序排列");
            }
        }

        // 验证每个时段的有效性
        for (Period period : periods) {
            validatePeriod(period);
        }
    }

    /**
     * 验证单个时段信息 - 更新版本
     */
    private void validatePeriod(Period period) throws BusinessException {
        if (period.getStartTime() == null || period.getStartTime().trim().isEmpty()) {
            throw new ValidationException("startTime", "时段开始时间不能为空");
        }

        if (period.getPlanNo() == null || period.getPlanNo() <= 0) {
            throw new ValidationException("planNo", "配时方案号必须大于0");
        }

        if (period.getCtrlMode() == null || period.getCtrlMode().trim().isEmpty()) {
            throw new ValidationException("ctrlMode", "控制方式不能为空");
        }

        // 使用Period类的验证方法检查时间格式
        if (!Period.isValidTimeFormat(period.getStartTime())) {
            throw new ValidationException("startTime", "时间格式必须为HH:MM:SS或HH:MM");
        }
    }


    /**
     * 使用示例和验证方法
     */
    public void validatePeriodListParsing() throws BusinessException {
        // 测试解析功能
        List<DayPlanParam> dayPlans = getAllDayPlans();

        for (DayPlanParam dayPlan : dayPlans) {
            logger.info("日计划: CrossID={}, DayPlanNo={}",
                    dayPlan.getCrossId(), dayPlan.getDayPlanNo());

            List<Period> periods = dayPlan.getPeriodList();
            if (periods != null && !periods.isEmpty()) {
                logger.info("包含{}个时段:", periods.size());
                for (Period period : periods) {
                    logger.info("  时段: 开始时间={}, 配时方案号={}, 控制方式={}",
                            period.getStartTime(), period.getPlanNo(), period.getCtrlMode());
                }
            } else {
                logger.warn("日计划{}没有时段信息", dayPlan.getDayPlanNo());
            }
        }
    }

    private ScheduleParam parseScheduleParam(JsonNode scheduleNode) {
        ScheduleParam schedule = new ScheduleParam();
        schedule.setCrossId(scheduleNode.path("CrossID").asText());
        schedule.setScheduleNo(scheduleNode.path("ScheduleNo").asInt());
        schedule.setType(ScheduleType.fromCode(scheduleNode.path("Type").asText()));
        schedule.setStartDay(scheduleNode.path("StartDay").asText());
        schedule.setEndDay(scheduleNode.path("EndDay").asText());
        schedule.setWeekDay(scheduleNode.path("WeekDay").asInt());
        schedule.setDayPlanNo(scheduleNode.path("DayPlanNo").asInt());
        return schedule;
    }
    private List<LaneMovement> parseLaneMovementList(JsonNode parentNode, String fieldName){
        List<LaneMovement> result = new ArrayList<>();
        JsonNode listNode = parentNode.get(fieldName);

        if (listNode != null && listNode.isArray()) {
            listNode.forEach(node -> result.add(LaneMovement.fromCode(node.asText())));
        }

        return result;
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

    private String getBuiltInTestData() {
        // 这里返回你提供的完整测试数据JSON
        return """
        {
          "SysParam": {
            "SysName": "智能交通信号控制系统",
            "SysVersion": 2.0,
            "Supplier": "山东双百电子有限公司",
            "CrossIDList": [
              "11010110001",
              "11010110002",
              "11010110003"
            ],
            "RouteIDList": [
              "110101001",
              "110101002"
            ],
            "RegionIDList": [
              "110101001",
              "110101002"
            ],
            "SignalControlerIDList": [
              "11010140000111001",
              "11010140000111002",
              "11010140000111003"
            ]
          },
          "RunStatus": {
            "SysState": {
              "Value": "Online"
            }
          }
        }
        """;
    }
}