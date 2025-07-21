package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.system.RouteParam;
import com.traffic.gat1049.protocol.model.system.RouteCross;
import com.traffic.gat1049.model.enums.RouteType;
import com.traffic.gat1049.service.interfaces.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 线路服务实现
 */
@Service
public class RouteServiceImpl implements RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 线路参数存储
    private final Map<String, RouteParam> routeStorage = new ConcurrentHashMap<>();

    public RouteServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        //initializeSampleData();
    }

    @Override
    public RouteParam findById(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        RouteParam routeParam = dataPrider.getRouteById(routeId);//routeStorage.get(routeId);
        if (routeParam == null) {
            throw new DataNotFoundException("RouteParam", routeId);
        }

        return routeParam;
    }

    @Override
    public List<RouteParam> findAll() throws BusinessException {
        return dataPrider.getAllRoutes();
        //return new ArrayList<>(routeStorage.values());
    }

    @Override
    public List<RouteParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<RouteParam> allRoutes = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allRoutes.size());

        if (start >= allRoutes.size()) {
            return new ArrayList<>();
        }

        return allRoutes.subList(start, end);
    }

    @Override
    public RouteParam save(RouteParam routeParam) throws BusinessException {
        if (routeParam == null) {
            throw new ValidationException("routeParam", "线路参数不能为空");
        }

        validateRouteParam(routeParam);

//        routeParam.setCreateTime(LocalDateTime.now());
//        routeParam.setUpdateTime(LocalDateTime.now());

        routeStorage.put(routeParam.getRouteId(), routeParam);

        logger.info("保存线路参数: routeId={}, routeName={}",
                routeParam.getRouteId(), routeParam.getRouteName());

        return routeParam;
    }

    @Override
    public RouteParam update(RouteParam routeParam) throws BusinessException {
        if (routeParam == null) {
            throw new ValidationException("routeParam", "线路参数不能为空");
        }

        String routeId = routeParam.getRouteId();
        if (!routeStorage.containsKey(routeId)) {
            throw new DataNotFoundException("RouteParam", routeId);
        }

        validateRouteParam(routeParam);

//        routeParam.setUpdateTime(LocalDateTime.now());
        routeStorage.put(routeId, routeParam);

        logger.info("更新线路参数: routeId={}, routeName={}",
                routeParam.getRouteId(), routeParam.getRouteName());

        return routeParam;
    }

    @Override
    public void deleteById(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        RouteParam removed = routeStorage.remove(routeId);
        if (removed == null) {
            throw new DataNotFoundException("RouteParam", routeId);
        }

        logger.info("删除线路参数: routeId={}", routeId);
    }

    @Override
    public boolean existsById(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            return false;
        }
        return routeStorage.containsKey(routeId);
    }

    @Override
    public long count() throws BusinessException {
        return routeStorage.size();
    }

    @Override
    public List<RouteParam> findByName(String routeName) throws BusinessException {
        if (routeName == null || routeName.trim().isEmpty()) {
            throw new ValidationException("routeName", "线路名称不能为空");
        }

        return routeStorage.values().stream()
                .filter(route -> route.getRouteName() != null &&
                        route.getRouteName().contains(routeName))
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteParam> findByType(RouteType routeType) throws BusinessException {
        if (routeType == null) {
            throw new ValidationException("routeType", "线路类型不能为空");
        }

        return routeStorage.values().stream()
                .filter(route -> routeType.equals(route.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteCross> getRouteCrosses(String routeId) throws BusinessException {
        RouteParam route = findById(routeId);
        return route.getRouteCrossList() != null ? route.getRouteCrossList() : new ArrayList<>();
    }

    @Override
    public void addCross(String routeId, String crossId, Integer distance) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (distance == null || distance < 0) {
            throw new ValidationException("distance", "距离不能为空且必须大于等于0");
        }

        RouteParam route = findById(routeId);
        List<RouteCross> routeCrossList = route.getRouteCrossList();

        // 检查路口是否已存在
        boolean exists = routeCrossList.stream()
                .anyMatch(rc -> crossId.equals(rc.getCrossId()));

        if (exists) {
            throw new ValidationException("crossId", "路口已存在于线路中: " + crossId);
        }

        // 添加新路口
        RouteCross routeCross = new RouteCross(crossId, distance);
        routeCrossList.add(routeCross);

//        route.setUpdateTime(LocalDateTime.now());
        routeStorage.put(routeId, route);

        logger.info("添加路口到线路: routeId={}, crossId={}, distance={}",
                routeId, crossId, distance);
    }

    @Override
    public void removeCross(String routeId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        RouteParam route = findById(routeId);
        List<RouteCross> routeCrossList = route.getRouteCrossList();

        boolean removed = routeCrossList.removeIf(rc -> crossId.equals(rc.getCrossId()));

        if (!removed) {
            throw new DataNotFoundException("RouteCross", crossId);
        }

//        route.setUpdateTime(LocalDateTime.now());
        routeStorage.put(routeId, route);

        logger.info("从线路移除路口: routeId={}, crossId={}", routeId, crossId);
    }

    @Override
    public void updateCrossDistance(String routeId, String crossId, Integer distance) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (distance == null || distance < 0) {
            throw new ValidationException("distance", "距离不能为空且必须大于等于0");
        }

        RouteParam route = findById(routeId);
        List<RouteCross> routeCrossList = route.getRouteCrossList();

        Optional<RouteCross> routeCrossOpt = routeCrossList.stream()
                .filter(rc -> crossId.equals(rc.getCrossId()))
                .findFirst();

        if (!routeCrossOpt.isPresent()) {
            throw new DataNotFoundException("RouteCross", crossId);
        }

        RouteCross routeCross = routeCrossOpt.get();
        routeCross.setDistance(distance);

//        route.setUpdateTime(LocalDateTime.now());
        routeStorage.put(routeId, route);

        logger.info("更新线路中路口距离: routeId={}, crossId={}, distance={}",
                routeId, crossId, distance);
    }

    private void validateRouteParam(RouteParam routeParam) throws BusinessException {
        if (routeParam.getRouteId() == null || routeParam.getRouteId().trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        if (routeParam.getRouteName() == null || routeParam.getRouteName().trim().isEmpty()) {
            throw new ValidationException("routeName", "线路名称不能为空");
        }

        if (routeParam.getType() == null) {
            throw new ValidationException("type", "线路类型不能为空");
        }

        if (routeParam.getRouteCrossList() == null || routeParam.getRouteCrossList().isEmpty()) {
            throw new ValidationException("routeCrossList", "线路路口列表不能为空");
        }

        // 验证路口列表中的路口编号不能重复
        Set<String> crossIds = new HashSet<>();
        for (RouteCross routeCross : routeParam.getRouteCrossList()) {
            if (routeCross.getCrossId() == null || routeCross.getCrossId().trim().isEmpty()) {
                throw new ValidationException("routeCross.crossId", "线路中的路口编号不能为空");
            }
            if (!crossIds.add(routeCross.getCrossId())) {
                throw new ValidationException("routeCross.crossId", "线路中存在重复的路口编号: " + routeCross.getCrossId());
            }
            if (routeCross.getDistance() == null || routeCross.getDistance() < 0) {
                throw new ValidationException("routeCross.distance", "路口距离不能为空且必须大于等于0");
            }
        }

        // 验证线路编号格式（9位数字）
        if (!routeParam.getRouteId().matches("\\d{9}")) {
            throw new ValidationException("routeId", "线路编号格式错误，应为9位数字");
        }
    }

    private void initializeSampleData() {
        // 创建示例线路数据
        RouteParam route1 = new RouteParam("110100001", "示例协调干线1", RouteType.COORDINATED);
        route1.setSubRegionIdList(Arrays.asList("110100001", "110100002"));

        List<RouteCross> routeCrossList1 = Arrays.asList(
                new RouteCross("11010000100001", 0),
                new RouteCross("11010000100002", 500),
                new RouteCross("11010000100003", 1200)
        );
        route1.setRouteCrossList(routeCrossList1);

        RouteParam route2 = new RouteParam("110100002", "示例大流量通道", RouteType.EXPRESSWAY);
        route2.setSubRegionIdList(Arrays.asList("110100002", "110100003"));

        List<RouteCross> routeCrossList2 = Arrays.asList(
                new RouteCross("11010000100004", 0),
                new RouteCross("11010000100005", 800),
                new RouteCross("11010000100006", 1500)
        );
        route2.setRouteCrossList(routeCrossList2);

        RouteParam route3 = new RouteParam("110100003", "示例公交优先线路", RouteType.BUS_PRIORITY);
        route3.setSubRegionIdList(Arrays.asList("110100001", "110100003"));

        List<RouteCross> routeCrossList3 = Arrays.asList(
                new RouteCross("11010000100001", 0),
                new RouteCross("11010000100007", 600),
                new RouteCross("11010000100008", 1100)
        );
        route3.setRouteCrossList(routeCrossList3);

        try {
            save(route1);
            save(route2);
            save(route3);
            logger.info("示例线路数据初始化完成");
        } catch (BusinessException e) {
            logger.error("示例线路数据初始化失败", e);
        }
    }
}