package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.model.enums.RouteType;
import com.traffic.gat1049.protocol.model.system.RouteParam;
import com.traffic.gat1049.protocol.model.system.RouteCross;
import com.traffic.gat1049.service.interfaces.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 线路服务实现
 */
public class RouteServiceImpl implements RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 内存存储，实际项目中应该从数据库获取
    private final Map<String, RouteParam> routeStorage = new ConcurrentHashMap<>();
    private final Map<String, List<RouteCross>> routeCrossStorage = new ConcurrentHashMap<>();

    public RouteServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public RouteParam findById(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        // 先从数据提供者获取
        try {
            List<RouteParam> routes = dataProvider.getRouteParams();
            if (routes != null) {
                for (RouteParam route : routes) {
                    if (routeId.equals(route.getRouteId())) {
                        return route;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取线路数据失败，使用本地存储", e);
        }

        RouteParam route = routeStorage.get(routeId);
        if (route == null) {
            throw new DataNotFoundException("RouteParam", routeId);
        }

        return route;
    }

    @Override
    public List<RouteParam> findAll() throws BusinessException {
        try {
            List<RouteParam> routes = dataProvider.getRouteParams();
            if (routes != null && !routes.isEmpty()) {
                return routes;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取线路列表失败，使用本地存储", e);
        }

        return new ArrayList<>(routeStorage.values());
    }

    @Override
    public List<RouteParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<RouteParam> allRoutes = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allRoutes.size());

        if (startIndex >= allRoutes.size()) {
            return new ArrayList<>();
        }

        return allRoutes.subList(startIndex, endIndex);
    }

    @Override
    public RouteParam save(RouteParam routeParam) throws BusinessException {
        if (routeParam == null) {
            throw new ValidationException("routeParam", "线路参数不能为空");
        }

        validateRouteParam(routeParam);

        routeParam.setUpdateTime(LocalDateTime.now());
        if (routeParam.getCreateTime() == null) {
            routeParam.setCreateTime(LocalDateTime.now());
        }

        routeStorage.put(routeParam.getRouteId(), routeParam);
        logger.info("保存线路参数: {}", routeParam.getRouteName());

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

        // 同时删除线路路口关系
        routeCrossStorage.remove(routeId);
        logger.info("删除线路参数: {}", routeId);
    }

    @Override
    public List<RouteParam> findByName(String routeName) throws BusinessException {
        if (routeName == null || routeName.trim().isEmpty()) {
            throw new ValidationException("routeName", "线路名称不能为空");
        }

        return findAll().stream()
                .filter(route -> route.getRouteName() != null &&
                        route.getRouteName().contains(routeName))
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteParam> findByType(RouteType routeType) throws BusinessException {
        if (routeType == null) {
            throw new ValidationException("routeType", "线路类型不能为空");
        }

        return findAll().stream()
                .filter(route -> routeType.equals(route.getRouteType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteCross> getRouteCrosses(String routeId) throws BusinessException {
        findById(routeId); // 验证线路是否存在

        List<RouteCross> routeCrosses = routeCrossStorage.get(routeId);
        return routeCrosses != null ? routeCrosses : new ArrayList<>();
    }

    @Override
    public void addCross(String routeId, String crossId, Integer distance) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (distance == null || distance < 0) {
            throw new ValidationException("distance", "距离不能为空且必须大于等于0");
        }

        findById(routeId); // 验证线路是否存在

        List<RouteCross> routeCrosses = routeCrossStorage.computeIfAbsent(routeId, k -> new ArrayList<>());

        // 检查路口是否已存在
        boolean exists = routeCrosses.stream()
                .anyMatch(rc -> crossId.equals(rc.getCrossId()));

        if (!exists) {
            RouteCross routeCross = new RouteCross();
            routeCross.setCrossId(crossId);
            routeCross.setDistance(distance);
            routeCross.setCreateTime(LocalDateTime.now());

            routeCrosses.add(routeCross);
            // 按距离排序
            routeCrosses.sort(Comparator.comparing(RouteCross::getDistance));

            logger.info("向线路 {} 添加路口 {}，距离 {}", routeId, crossId, distance);
        } else {
            throw new ValidationException("crossId", "路口已存在于线路中");
        }
    }

    @Override
    public void removeCross(String routeId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        findById(routeId); // 验证线路是否存在

        List<RouteCross> routeCrosses = routeCrossStorage.get(routeId);
        if (routeCrosses != null) {
            boolean removed = routeCrosses.removeIf(rc -> crossId.equals(rc.getCrossId()));
            if (removed) {
                logger.info("从线路 {} 移除路口 {}", routeId, crossId);
            } else {
                throw new DataNotFoundException("RouteCross", "routeId=" + routeId + ", crossId=" + crossId);
            }
        }
    }

    @Override
    public void updateCrossDistance(String routeId, String crossId, Integer distance) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (distance == null || distance < 0) {
            throw new ValidationException("distance", "距离不能为空且必须大于等于0");
        }

        findById(routeId); // 验证线路是否存在

        List<RouteCross> routeCrosses = routeCrossStorage.get(routeId);
        if (routeCrosses != null) {
            Optional<RouteCross> routeCrossOpt = routeCrosses.stream()
                    .filter(rc -> crossId.equals(rc.getCrossId()))
                    .findFirst();

            if (routeCrossOpt.isPresent()) {
                RouteCross routeCross = routeCrossOpt.get();
                routeCross.setDistance(distance);
                routeCross.setUpdateTime(LocalDateTime.now());

                // 重新排序
                routeCrosses.sort(Comparator.comparing(RouteCross::getDistance));

                logger.info("更新线路 {} 中路口 {} 的距离为 {}", routeId, crossId, distance);
            } else {
                throw new DataNotFoundException("RouteCross", "routeId=" + routeId + ", crossId=" + crossId);
            }
        } else {
            throw new DataNotFoundException("RouteCross", "routeId=" + routeId);
        }
    }

    private void validateRouteParam(RouteParam routeParam) throws BusinessException {
        if (routeParam.getRouteId() == null || routeParam.getRouteId().trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }

        if (routeParam.getRouteName() == null || routeParam.getRouteName().trim().isEmpty()) {
            throw new ValidationException("routeName", "线路名称不能为空");
        }

        // 验证线路编号格式（14位数字）
        if (!routeParam.getRouteId().matches("\\d{14}")) {
            throw new ValidationException("routeId", "线路编号格式错误，应为14位数字");
        }
    }

    private void initializeSampleData() {
        try {
            // 初始化示例线路数据
            RouteParam route1 = new RouteParam();
            route1.setRouteId("11010000000001");
            route1.setRouteName("长安街东西干线");
            route1.setRouteType(RouteType.MAIN_ROAD);
            route1.setCreateTime(LocalDateTime.now());
            routeStorage.put(route1.getRouteId(), route1);

            // 为线路1添加路口
            List<RouteCross> route1Crosses = new ArrayList<>();
            RouteCross rc1 = new RouteCross();
            rc1.setCrossId("11010000100001");
            rc1.setDistance(0);
            rc1.setCreateTime(LocalDateTime.now());
            route1Crosses.add(rc1);

            RouteCross rc2 = new RouteCross();
            rc2.setCrossId("11010000100002");
            rc2.setDistance(500);
            rc2.setCreateTime(LocalDateTime.now());
            route1Crosses.add(rc2);

            routeCrossStorage.put(route1.getRouteId(), route1Crosses);

            RouteParam route2 = new RouteParam();
            route2.setRouteId("11010000000002");
            route2.setRouteName("二环路");
            route2.setRouteType(RouteType.RING_ROAD);
            route2.setCreateTime(LocalDateTime.now());
            routeStorage.put(route2.getRouteId(), route2);

            // 为线路2添加路口
            List<RouteCross> route2Crosses = new ArrayList<>();
            RouteCross rc3 = new RouteCross();
            rc3.setCrossId("11010000100003");
            rc3.setDistance(0);
            rc3.setCreateTime(LocalDateTime.now());
            route2Crosses.add(rc3);

            RouteCross rc4 = new RouteCross();
            rc4.setCrossId("11010000100004");
            rc4.setDistance(800);
            rc4.setCreateTime(LocalDateTime.now());
            route2Crosses.add(rc4);

            routeCrossStorage.put(route2.getRouteId(), route2Crosses);

            logger.info("初始化线路示例数据完成，共 {} 条线路", routeStorage.size());
        } catch (Exception e) {
            logger.error("初始化线路示例数据失败", e);
        }
    }