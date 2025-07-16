package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.RouteParamEntity;
import com.traffic.gat1049.repository.interfaces.RouteParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 线路参数测试服务
 */
@Service
@Slf4j
public class TestRouteParamService {

    @Autowired
    private RouteParamRepository routeParamRepository;

    /**
     * 创建线路参数
     */
    public RouteParamEntity createRouteParam(String routeId, String routeName, Integer type) {
        RouteParamEntity entity = new RouteParamEntity();
        entity.setRouteId(routeId);
        entity.setRouteName(routeName);
        entity.setType(type);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        routeParamRepository.insert(entity);
        log.info("创建线路参数: routeId={}, routeName={}, type={}", routeId, routeName, type);

        return entity;
    }

    /**
     * 根据ID查询线路参数
     */
    public RouteParamEntity getRouteParam(String routeId) {
        RouteParamEntity entity = routeParamRepository.findByRouteId(routeId);

        if (entity == null) {
            throw new RuntimeException("未找到线路参数: " + routeId);
        }

        log.info("查询线路参数: routeId={}", routeId);
        return entity;
    }

    /**
     * 查询所有线路参数
     */
    public List<RouteParamEntity> getAllRoutes() {
        List<RouteParamEntity> entities = routeParamRepository.selectList(null);
        log.info("查询所有线路参数，共 {} 条", entities.size());
        return entities;
    }

    /**
     * 根据类型查询线路
     */
    public List<RouteParamEntity> getRoutesByType(Integer type) {
        List<RouteParamEntity> entities = routeParamRepository.findByType(type);
        log.info("根据类型查询线路: type={}, 共 {} 条", type, entities.size());
        return entities;
    }

    /**
     * 根据子区ID查询线路
     */
    public List<RouteParamEntity> getRoutesBySubRegion(String subRegionId) {
        List<RouteParamEntity> entities = routeParamRepository.findBySubRegionId(subRegionId);
        log.info("根据子区查询线路: subRegionId={}, 共 {} 条", subRegionId, entities.size());
        return entities;
    }

    /**
     * 根据线路名称模糊查询
     */
    public List<RouteParamEntity> searchRoutesByName(String routeName) {
        List<RouteParamEntity> entities = routeParamRepository.findByRouteNameLike(routeName);
        log.info("模糊查询线路: routeName={}, 共 {} 条", routeName, entities.size());
        return entities;
    }

    /**
     * 查询完整信息视图
     */
    public List<RouteParamEntity> getAllWithCompleteInfo() {
        List<RouteParamEntity> entities = routeParamRepository.findAllWithCompleteInfo();
        log.info("查询完整信息视图，共 {} 条", entities.size());
        return entities;
    }

    /**
     * 更新线路参数
     */
    public RouteParamEntity updateRouteParam(String routeId, String routeName, Integer type) {
        RouteParamEntity existing = getRouteParam(routeId);

        if (routeName != null && !routeName.trim().isEmpty()) {
            existing.setRouteName(routeName);
        }
        if (type != null) {
            existing.setType(type);
        }
        existing.setUpdatedTime(LocalDateTime.now());

        routeParamRepository.updateById(existing);
        log.info("更新线路参数: routeId={}", routeId);

        return existing;
    }

    /**
     * 删除线路参数
     */
    public void deleteRouteParam(String routeId) {
        RouteParamEntity existing = getRouteParam(routeId);
        routeParamRepository.deleteById(routeId);
        log.info("删除线路参数: routeId={}", routeId);
    }
}
