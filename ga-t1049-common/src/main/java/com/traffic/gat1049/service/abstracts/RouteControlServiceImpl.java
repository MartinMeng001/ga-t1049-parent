package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.control.RouteControlParam;
import com.traffic.gat1049.service.interfaces.RouteControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 干线控制服务实现
 */
public class RouteControlServiceImpl implements RouteControlService {

    private static final Logger logger = LoggerFactory.getLogger(RouteControlServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    private final Map<String, RouteControlParam> routeControlStorage = new ConcurrentHashMap<>();

    public RouteControlServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public RouteControlParam findById(String routeControlId) throws BusinessException {
        if (routeControlId == null || routeControlId.trim().isEmpty()) {
            throw new ValidationException("routeControlId", "干线控制编号不能为空");
        }

        try {
            List<RouteControlParam> routeControls = dataProvider.getRouteControlParams();
            if (routeControls != null) {
                for (RouteControlParam routeControl : routeControls) {
                    if (routeControlId.equals(routeControl.getRouteControlId())) {
                        return routeControl;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取干线控制数据失败，使用本地存储", e);
        }

        RouteControlParam routeControl = routeControlStorage.get(routeControlId);
        if (routeControl == null) {
            throw new DataNotFoundException("RouteControlParam", routeControlId);
        }
        return routeControl;
    }

    @Override
    public List<RouteControlParam> findAll() throws BusinessException {
        try {
            List<RouteControlParam> routeControls = dataProvider.getRouteControlParams();
            if (routeControls != null && !routeControls.isEmpty()) {
                return routeControls;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取干线控制列表失败，使用本地存储", e);
        }
        return new ArrayList<>(routeControlStorage.values());
    }

    @Override
    public List<RouteControlParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<RouteControlParam> allRouteControls = findAll();
        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allLanes.size());
        if (startIndex >= allLanes.size()) {
            return new ArrayList<>();
        }
        return allLanes.subList(startIndex, endIndex);
    }

    @Override
    public LaneParam save(LaneParam laneParam) throws BusinessException {
        if (laneParam == null) {
            throw new ValidationException("laneParam", "车道参数不能为空");
        }
        laneParam.setUpdateTime(LocalDateTime.now());
        if (laneParam.getCreateTime() == null) {
            laneParam.setCreateTime(LocalDateTime.now());
        }
        laneStorage.put(laneParam.getLaneId(), laneParam);
        logger.info("保存车道参数: {}", laneParam.getLaneId());
        return laneParam;
    }

    @Override
    public void deleteById(String laneId) throws BusinessException {
        if (laneId == null || laneId.trim().isEmpty()) {
            throw new ValidationException("laneId", "车道编号不能为空");
        }
        LaneParam removed = laneStorage.remove(laneId);
        if (removed == null) {
            throw new DataNotFoundException("LaneParam", laneId);
        }
        logger.info("删除车道参数: {}", laneId);
    }

    @Override
    public List<LaneParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return findAll().stream()
                .filter(lane -> crossId.equals(lane.getCrossId()))
                .collect(Collectors.toList());
    }

    private void initializeSampleData() {
        try {
            LaneParam lane1 = new LaneParam();
            lane1.setLaneId("1101000010000101");
            lane1.setCrossId("11010000100001");
            lane1.setLaneNo(1);
            lane1.setCreateTime(LocalDateTime.now());
            laneStorage.put(lane1.getLaneId(), lane1);

            LaneParam lane2 = new LaneParam();
            lane2.setLaneId("1101000010000102");
            lane2.setCrossId("11010000100001");
            lane2.setLaneNo(2);
            lane2.setCreateTime(LocalDateTime.now());
            laneStorage.put(lane2.getLaneId(), lane2);

            logger.info("初始化车道示例数据完成，共 {} 条车道", laneStorage.size());
        } catch (Exception e) {
            logger.error("初始化车道示例数据失败", e);
        }
    }
}