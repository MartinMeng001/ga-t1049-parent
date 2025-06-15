package com.traffic.gat1049.service.abstracts;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.LaneParam;
import com.traffic.gat1049.service.interfaces.LaneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 车道服务实现
 */
public class LaneServiceImpl implements LaneService {

    private static final Logger logger = LoggerFactory.getLogger(LaneServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    private final Map<String, LaneParam> laneStorage = new ConcurrentHashMap<>();

    public LaneServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public LaneParam findById(String laneId) throws BusinessException {
        if (laneId == null || laneId.trim().isEmpty()) {
            throw new ValidationException("laneId", "车道编号不能为空");
        }

        try {
            List<LaneParam> lanes = dataProvider.getLaneParams();
            if (lanes != null) {
                for (LaneParam lane : lanes) {
                    if (laneId.equals(lane.getLaneId())) {
                        return lane;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取车道数据失败，使用本地存储", e);
        }

        LaneParam lane = laneStorage.get(laneId);
        if (lane == null) {
            throw new DataNotFoundException("LaneParam", laneId);
        }
        return lane;
    }

    @Override
    public List<LaneParam> findAll() throws BusinessException {
        try {
            List<LaneParam> lanes = dataProvider.getLaneParams();
            if (lanes != null && !lanes.isEmpty()) {
                return lanes;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取车道列表失败，使用本地存储", e);
        }
        return new ArrayList<>(laneStorage.values());
    }

    @Override
    public List<LaneParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<LaneParam> allLanes = findAll();
        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int startIndex = (pageRequest.getPageNum() - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allRouteControls.size());
        if (startIndex >= allRouteControls.size()) {
            return new ArrayList<>();
        }
        return allRouteControls.subList(startIndex, endIndex);
    }

    @Override
    public RouteControlParam save(RouteControlParam routeControlParam) throws BusinessException {
        if (routeControlParam == null) {
            throw new ValidationException("routeControlParam", "干线控制参数不能为空");
        }
        routeControlParam.setUpdateTime(LocalDateTime.now());
        if (routeControlParam.getCreateTime() == null) {
            routeControlParam.setCreateTime(LocalDateTime.now());
        }
        routeControlStorage.put(routeControlParam.getRouteControlId(), routeControlParam);
        logger.info("保存干线控制参数: {}", routeControlParam.getRouteControlId());
        return routeControlParam;
    }

    @Override
    public void deleteById(String routeControlId) throws BusinessException {
        if (routeControlId == null || routeControlId.trim().isEmpty()) {
            throw new ValidationException("routeControlId", "干线控制编号不能为空");
        }
        RouteControlParam removed = routeControlStorage.remove(routeControlId);
        if (removed == null) {
            throw new DataNotFoundException("RouteControlParam", routeControlId);
        }
        logger.info("删除干线控制参数: {}", routeControlId);
    }

    @Override
    public List<RouteControlParam> findByRouteId(String routeId) throws BusinessException {
        if (routeId == null || routeId.trim().isEmpty()) {
            throw new ValidationException("routeId", "线路编号不能为空");
        }
        return findAll().stream()
                .filter(routeControl -> routeId.equals(routeControl.getRouteId()))
                .collect(Collectors.toList());
    }

    @Override
    public void enableRouteControl(String routeControlId) throws BusinessException {
        RouteControlParam routeControl = findById(routeControlId);
        routeControl.setEnabled(true);
        save(routeControl);
        logger.info("启用干线控制: {}", routeControlId);
    }

    @Override
    public void disableRouteControl(String routeControlId) throws BusinessException {
        RouteControlParam routeControl = findById(routeControlId);
        routeControl.setEnabled(false);
        save(routeControl);
        logger.info("禁用干线控制: {}", routeControlId);
    }

    private void initializeSampleData() {
        try {
            RouteControlParam routeControl1 = new RouteControlParam();
            routeControl1.setRouteControlId("11010000000001001");
            routeControl1.setRouteId("11010000000001");
            routeControl1.setControlName("长安街干线控制");
            routeControl1.setEnabled(true);
            routeControl1.setCreateTime(LocalDateTime.now());
            routeControlStorage.put(routeControl1.getRouteControlId(), routeControl1);

            RouteControlParam routeControl2 = new RouteControlParam();
            routeControl2.setRouteControlId("11010000000002001");
            routeControl2.setRouteId("11010000000002");
            routeControl2.setControlName("二环路干线控制");
            routeControl2.setEnabled(false);
            routeControl2.setCreateTime(LocalDateTime.now());
            routeControlStorage.put(routeControl2.getRouteControlId(), routeControl2);

            logger.info("初始化干线控制示例数据完成，共 {} 个控制策略", routeControlStorage.size());
        } catch (Exception e) {
            logger.error("初始化干线控制示例数据失败", e);
        }
    }
}

