package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.enums.RouteControlMode;
import com.traffic.gat1049.protocol.model.runtime.RoadSectionSpeed;
import com.traffic.gat1049.protocol.model.runtime.RouteCtrlInfo;
import com.traffic.gat1049.protocol.model.runtime.RouteSpeed;
import com.traffic.gat1049.service.interfaces.RouteControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 干线控制服务实现
 * 参照 CrossServiceImpl 的实现模式
 */
@Service
public class RouteControlServiceImpl implements RouteControlService {

    private static final Logger logger = LoggerFactory.getLogger(RouteControlServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 干线控制方式存储
    private final Map<String, RouteCtrlInfo> routeControlModeStorage = new ConcurrentHashMap<>();

    // 干线路段推荐车速存储
    private final Map<String, RouteSpeed> routeSpeedStorage = new ConcurrentHashMap<>();

    // 路段推荐车速存储 (routeId_upCrossId_downCrossId -> RoadSectionSpeed)
    private final Map<String, RoadSectionSpeed> roadSectionSpeedStorage = new ConcurrentHashMap<>();

    // 干线协调控制状态存储
    private final Map<String, Boolean> coordinationStatusStorage = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public RouteControlServiceImpl() throws BusinessException {
        // 初始化示例数据
        //initializeSampleData();
    }

    @Override
    public RouteCtrlInfo getRouteControlMode(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        Object obj = dataPrider.getRouteCtrlInfoById(routeId);
        return OBJECT_MAPPER.convertValue(obj, RouteCtrlInfo.class);
    }

    @Override
    public List<RouteCtrlInfo> getAllRouteControlMode() throws BusinessException {
        List<Object> objs = new ArrayList<>();
        objs.add(dataPrider.getRouteCtrlInfo());
        return objs.stream()
                .map(obj -> {
                    try{
                        return OBJECT_MAPPER.convertValue(obj, RouteCtrlInfo.class);
                    }catch(IllegalArgumentException e){
                        logger.error("转换 RouteCtrlInfo 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void setRouteControlMode(String routeId, RouteControlMode controlMode) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        if (controlMode == null) {
            throw new ValidationException("controlMode", "控制方式不能为空");
        }

        // 验证线路编号格式（9位数字）
        if (!routeId.matches("\\d{9}")) {
            throw new ValidationException("routeId", "线路编号格式不正确: " + routeId);
        }

        RouteCtrlInfo status = new RouteCtrlInfo(routeId, controlMode);
//        status.setCreateTime(LocalDateTime.now());
//        status.setUpdateTime(LocalDateTime.now());

        routeControlModeStorage.put(routeId, status);

        logger.info("设置干线控制方式: routeId={}, controlMode={}", routeId, controlMode.getDescription());
    }

    @Override
    public RouteSpeed getRouteSpeed(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }
        Object obj = dataPrider.getRouteSpeedById(routeId);
        return OBJECT_MAPPER.convertValue(obj, RouteSpeed.class);
    }

    @Override
    public List<RouteSpeed> getAllRouteSpeed() throws BusinessException {
        List<Object> objs = new ArrayList<>();
        objs.add(dataPrider.getRouteSpeed());
        return objs.stream()
                .map(obj -> {
                    try{
                        return OBJECT_MAPPER.convertValue(obj, RouteSpeed.class);
                    }catch (IllegalArgumentException e){
                        logger.warn("转换 RouteSpeed 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void setRouteSpeed(RouteSpeed routeSpeed) throws BusinessException {
        if (routeSpeed == null) {
            throw new ValidationException("routeSpeed", "干线路段推荐车速不能为空");
        }

        validateRouteSpeed(routeSpeed);

        String routeId = routeSpeed.getRouteId();

//        routeSpeed.setCreateTime(LocalDateTime.now());
//        routeSpeed.setUpdateTime(LocalDateTime.now());

        // 保存整体路线车速信息
        routeSpeedStorage.put(routeId, routeSpeed);

        // 同时保存每个路段的车速信息到单独的存储中
        if (routeSpeed.getRoadSectionSpeedList() != null) {
            for (RoadSectionSpeed section : routeSpeed.getRoadSectionSpeedList()) {
                String key = buildRoadSectionKey(routeId, section.getUpCrossId(), section.getDownCrossId());
                roadSectionSpeedStorage.put(key, section);
            }
        }

        logger.info("设置干线路段推荐车速: routeId={}, sections={}",
                routeId, routeSpeed.getRoadSectionSpeedList().size());
    }

    @Override
    public void setRoadSectionSpeed(String routeId, String upCrossId, String downCrossId, Integer recommendSpeed) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        if (upCrossId == null || upCrossId.trim().isEmpty()) {
            throw new ValidationException("upCrossId", "上游路口编号不能为空");
        }

        if (downCrossId == null || downCrossId.trim().isEmpty()) {
            throw new ValidationException("downCrossId", "下游路口编号不能为空");
        }

        if (recommendSpeed == null || recommendSpeed < 0) {
            throw new ValidationException("recommendSpeed", "推荐车速不能为空且不能为负数");
        }

        // 验证路口编号格式（14位数字）
        if (!upCrossId.matches("\\d{14}")) {
            throw new ValidationException("upCrossId", "上游路口编号格式不正确: " + upCrossId);
        }

        if (!downCrossId.matches("\\d{14}")) {
            throw new ValidationException("downCrossId", "下游路口编号格式不正确: " + downCrossId);
        }

        RoadSectionSpeed sectionSpeed = new RoadSectionSpeed(upCrossId, downCrossId, recommendSpeed);
        String key = buildRoadSectionKey(routeId, upCrossId, downCrossId);
        roadSectionSpeedStorage.put(key, sectionSpeed);

        // 更新或创建整体路线车速信息
        updateRouteSpeedFromSections(routeId);

        logger.info("设置路段推荐车速: routeId={}, upCrossId={}, downCrossId={}, recommendSpeed={}",
                routeId, upCrossId, downCrossId, recommendSpeed);
    }

    @Override
    public RoadSectionSpeed getRoadSectionSpeed(String routeId, String upCrossId, String downCrossId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        if (upCrossId == null || upCrossId.trim().isEmpty()) {
            throw new ValidationException("upCrossId", "上游路口编号不能为空");
        }

        if (downCrossId == null || downCrossId.trim().isEmpty()) {
            throw new ValidationException("downCrossId", "下游路口编号不能为空");
        }

        String key = buildRoadSectionKey(routeId, upCrossId, downCrossId);
        RoadSectionSpeed sectionSpeed = roadSectionSpeedStorage.get(key);

        if (sectionSpeed == null) {
            throw new DataNotFoundException("RoadSectionSpeed", key);
        }

        return sectionSpeed;
    }

    @Override
    public void startCoordination(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        // 检查线路控制方式是否支持协调控制
        RouteCtrlInfo controlMode = getRouteControlMode(routeId);
        if (!isCoordinationSupportedMode(controlMode.getValue())) {
            throw new BusinessException("当前控制方式不支持协调控制: " + controlMode.getValue().getDescription());
        }

        coordinationStatusStorage.put(routeId, true);

        logger.info("启动干线协调控制: routeId={}", routeId);
    }

    @Override
    public void stopCoordination(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        coordinationStatusStorage.put(routeId, false);

        logger.info("停止干线协调控制: routeId={}", routeId);
    }

    @Override
    public boolean isCoordinating(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        return coordinationStatusStorage.getOrDefault(routeId, false);
    }

    // ========== 私有方法 ==========

    /**
     * 验证干线路段推荐车速参数
     */
    private void validateRouteSpeed(RouteSpeed routeSpeed) throws BusinessException {
        if (routeSpeed.getRouteId() == null || routeSpeed.getRouteId().trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        if (routeSpeed.getRoadSectionSpeedList() == null || routeSpeed.getRoadSectionSpeedList().isEmpty()) {
            throw new ValidationException("roadSectionSpeedList", "路段推荐车速列表不能为空");
        }

        // 验证线路编号格式
        if (!routeSpeed.getRouteId().matches("\\d{9}")) {
            throw new ValidationException("routeId", "线路编号格式不正确: " + routeSpeed.getRouteId());
        }

        // 验证每个路段的推荐车速
        for (RoadSectionSpeed section : routeSpeed.getRoadSectionSpeedList()) {
            validateRoadSectionSpeed(section);
        }
    }

    /**
     * 验证路段推荐车速参数
     */
    private void validateRoadSectionSpeed(RoadSectionSpeed section) throws BusinessException {
        if (section.getUpCrossId() == null || section.getUpCrossId().trim().isEmpty()) {
            throw new ValidationException("upCrossId", "上游路口编号不能为空");
        }

        if (section.getDownCrossId() == null || section.getDownCrossId().trim().isEmpty()) {
            throw new ValidationException("downCrossId", "下游路口编号不能为空");
        }

        if (section.getRecommendSpeed() == null || section.getRecommendSpeed() < 0) {
            throw new ValidationException("recommendSpeed", "推荐车速不能为空且不能为负数");
        }

        // 验证路口编号格式（14位数字）
        if (!section.getUpCrossId().matches("\\d{14}")) {
            throw new ValidationException("upCrossId", "上游路口编号格式不正确: " + section.getUpCrossId());
        }

        if (!section.getDownCrossId().matches("\\d{14}")) {
            throw new ValidationException("downCrossId", "下游路口编号格式不正确: " + section.getDownCrossId());
        }

        // 验证推荐车速范围（一般道路限速不超过120公里/小时）
        if (section.getRecommendSpeed() > 120) {
            throw new ValidationException("recommendSpeed", "推荐车速不能超过120公里/小时: " + section.getRecommendSpeed());
        }
    }

    /**
     * 构建路段存储key
     */
    private String buildRoadSectionKey(String routeId, String upCrossId, String downCrossId) {
        return routeId + "_" + upCrossId + "_" + downCrossId;
    }

    /**
     * 根据路段车速信息更新整体路线车速
     */
    private void updateRouteSpeedFromSections(String routeId) {
        // 查找该路线的所有路段车速
        List<RoadSectionSpeed> sections = roadSectionSpeedStorage.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(routeId + "_"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (!sections.isEmpty()) {
            RouteSpeed routeSpeed = routeSpeedStorage.getOrDefault(routeId, new RouteSpeed(routeId));
            routeSpeed.setRoadSectionSpeedList(sections);
//            routeSpeed.setUpdateTime(LocalDateTime.now());
            routeSpeedStorage.put(routeId, routeSpeed);
        }
    }

    /**
     * 检查控制方式是否支持协调控制
     */
    private boolean isCoordinationSupportedMode(RouteControlMode mode) {
        return mode == RouteControlMode.FIX_COORDINATED ||
                mode == RouteControlMode.ADAPTIVE_COORDINATED;
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        try {
            // 创建示例干线 1
            String routeId1 = "110100001";
            RouteControlMode mode1 = RouteControlMode.FIX_COORDINATED;
            setRouteControlMode(routeId1, mode1);

            // 创建示例路段车速信息
            RouteSpeed routeSpeed1 = new RouteSpeed(routeId1);
            List<RoadSectionSpeed> sections1 = Arrays.asList(
                    new RoadSectionSpeed("11010000100001", "11010000100002", 50),
                    new RoadSectionSpeed("11010000100002", "11010000100003", 60),
                    new RoadSectionSpeed("11010000100003", "11010000100004", 45)
            );
            routeSpeed1.setRoadSectionSpeedList(sections1);
            setRouteSpeed(routeSpeed1);

            // 创建示例干线 2
            String routeId2 = "110100002";
            RouteControlMode mode2 = RouteControlMode.ADAPTIVE_COORDINATED;
            setRouteControlMode(routeId2, mode2);

            // 创建示例路段车速信息
            RouteSpeed routeSpeed2 = new RouteSpeed(routeId2);
            List<RoadSectionSpeed> sections2 = Arrays.asList(
                    new RoadSectionSpeed("11010000100005", "11010000100006", 40),
                    new RoadSectionSpeed("11010000100006", "11010000100007", 55)
            );
            routeSpeed2.setRoadSectionSpeedList(sections2);
            setRouteSpeed(routeSpeed2);

            // 启动示例干线的协调控制
            startCoordination(routeId1);
            startCoordination(routeId2);

            logger.info("干线控制服务示例数据初始化完成");

        } catch (BusinessException e) {
            logger.error("干线控制服务示例数据初始化失败", e);
        }
    }
}