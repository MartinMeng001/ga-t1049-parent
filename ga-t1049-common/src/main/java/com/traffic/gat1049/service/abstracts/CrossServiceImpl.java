package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.CrossQueryDto;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.CrossParam;
import com.traffic.gat1049.protocol.model.runtime.CrossState;
import com.traffic.gat1049.model.enums.CrossFeature;
import com.traffic.gat1049.model.enums.CrossGrade;
import com.traffic.gat1049.model.enums.SystemState;
import com.traffic.gat1049.model.vo.CrossInfoVo;
import com.traffic.gat1049.service.interfaces.CrossService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 路口服务实现
 */
@Service
public class CrossServiceImpl implements CrossService {

    private static final Logger logger = LoggerFactory.getLogger(CrossServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();

    // 路口参数存储
    private final Map<String, CrossParam> crossStorage = new ConcurrentHashMap<>();

    // 路口状态存储
    private final Map<String, CrossState> crossStateStorage = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public CrossServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        //initializeSampleData();
    }

    @Override
    public CrossParam findById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossParam crossParam = dataPrider.getCrossById(crossId);//crossStorage.get(crossId);
        if (crossParam == null) {
            throw new DataNotFoundException("CrossParam", crossId);
        }

        return crossParam;
    }

    @Override
    public List<CrossParam> findAll() throws BusinessException {
        return dataPrider.getAllCrosses();
        //return new ArrayList<>(crossStorage.values());
    }

    @Override
    public List<CrossParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<CrossParam> allCrosses = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allCrosses.size());

        if (start >= allCrosses.size()) {
            return new ArrayList<>();
        }

        return allCrosses.subList(start, end);
    }

    @Override
    public CrossParam save(CrossParam crossParam) throws BusinessException {
        if (crossParam == null) {
            throw new ValidationException("crossParam", "路口参数不能为空");
        }

        validateCrossParam(crossParam);

//        crossParam.setCreateTime(LocalDateTime.now());
//        crossParam.setUpdateTime(LocalDateTime.now());

        crossStorage.put(crossParam.getCrossId(), crossParam);

        // 同时创建路口状态
        CrossState crossState = new CrossState(crossParam.getCrossId(), SystemState.ONLINE);
        crossStateStorage.put(crossParam.getCrossId(), crossState);

        logger.info("保存路口参数: crossId={}, crossName={}",
                crossParam.getCrossId(), crossParam.getCrossName());

        return crossParam;
    }

    @Override
    public CrossParam update(CrossParam crossParam) throws BusinessException {
        if (crossParam == null) {
            throw new ValidationException("crossParam", "路口参数不能为空");
        }

        String crossId = crossParam.getCrossId();
        if (!crossStorage.containsKey(crossId)) {
            throw new DataNotFoundException("CrossParam", crossId);
        }

        validateCrossParam(crossParam);

//        crossParam.setUpdateTime(LocalDateTime.now());
        crossStorage.put(crossId, crossParam);

        logger.info("更新路口参数: crossId={}, crossName={}",
                crossParam.getCrossId(), crossParam.getCrossName());

        return crossParam;
    }

    @Override
    public void deleteById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossParam removed = crossStorage.remove(crossId);
        if (removed == null) {
            throw new DataNotFoundException("CrossParam", crossId);
        }

        // 同时删除路口状态
        crossStateStorage.remove(crossId);

        logger.info("删除路口参数: crossId={}", crossId);
    }

    @Override
    public boolean existsById(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            return false;
        }
        return crossStorage.containsKey(crossId);
    }

    @Override
    public long count() throws BusinessException {
        return crossStorage.size();
    }

    @Override
    public List<CrossParam> findByName(String crossName) throws BusinessException {
        if (crossName == null || crossName.trim().isEmpty()) {
            throw new ValidationException("crossName", "路口名称不能为空");
        }

        return crossStorage.values().stream()
                .filter(cross -> cross.getCrossName() != null &&
                        cross.getCrossName().contains(crossName))
                .collect(Collectors.toList());
    }

    @Override
    public List<CrossInfoVo> findByCriteria(CrossQueryDto queryDto) throws BusinessException {
        // 确保queryDto是final或effectively final
        final CrossQueryDto finalQueryDto = queryDto != null ? queryDto : new CrossQueryDto();

        return crossStorage.values().stream()
                .filter(cross -> matchesCriteria(cross, finalQueryDto))
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public List<CrossParam> findByFeature(CrossFeature feature) throws BusinessException {
        if (feature == null) {
            throw new ValidationException("feature", "路口形状不能为空");
        }

        return crossStorage.values().stream()
                .filter(cross -> feature.equals(cross.getFeature()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CrossParam> findByGrade(CrossGrade grade) throws BusinessException {
        if (grade == null) {
            throw new ValidationException("grade", "路口等级不能为空");
        }

        return crossStorage.values().stream()
                .filter(cross -> grade.equals(cross.getGrade()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CrossParam> findBySignalControllerId(String signalControllerId) throws BusinessException {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            throw new ValidationException("signalControllerId", "信号机编号不能为空");
        }

        // 这里需要查询信号机控制的路口，由于没有实现信号机服务，暂时返回空列表
        logger.warn("查询信号机控制的路口功能尚未完全实现: signalControllerId={}", signalControllerId);
        return new ArrayList<>();
    }

    @Override
    public CrossState getCrossState(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        Object obj = dataPrider.getCrossStateById(crossId);
        CrossState crossState = OBJECT_MAPPER.convertValue(obj, CrossState.class);//crossStateStorage.get(crossId);
        if (crossState == null) {
            // 如果路口存在但状态不存在，创建默认状态
            if (crossStorage.containsKey(crossId)) {
                crossState = new CrossState(crossId, SystemState.OFFLINE);
                crossStateStorage.put(crossId, crossState);
            } else {
                throw new DataNotFoundException("CrossState", crossId);
            }
        }

        return crossState;
    }

    @Override
    public List<CrossState> getAllCrossState() throws BusinessException {
        List<CrossState> objs = dataPrider.getAllCrossStates();
//        List<CrossState> allCrossState = new ArrayList<>();
//        for(Object obj : objs) {
//            CrossState crossState = OBJECT_MAPPER.convertValue(obj, CrossState.class);
//            allCrossState.add(crossState);
//        }
//        return allCrossState;
        return objs.stream()
                .map(obj -> {
                    try {
                        return OBJECT_MAPPER.convertValue(obj, CrossState.class);
                    } catch (IllegalArgumentException e) {
                        logger.warn("转换 CrossState 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCrossState(String crossId, SystemState state) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (state == null) {
            throw new ValidationException("state", "系统状态不能为空");
        }

        if (!crossStorage.containsKey(crossId)) {
            throw new DataNotFoundException("CrossParam", crossId);
        }

        CrossState crossState = new CrossState(crossId, state);
        //crossState.setStateTime(LocalDateTime.now());
        crossStateStorage.put(crossId, crossState);

        logger.info("更新路口状态: crossId={}, state={}", crossId, state.getDescription());
    }

    @Override
    public List<Integer> getLaneNos(String crossId) throws BusinessException {
        CrossParam cross = findById(crossId);
        return cross.getLaneNoList() != null ? cross.getLaneNoList() : new ArrayList<>();
    }

    @Override
    public List<String> getSignalGroupNos(String crossId) throws BusinessException {
        CrossParam cross = findById(crossId);
        return cross.getSignalGroupNoList() != null ? cross.getSignalGroupNoList() : new ArrayList<>();
    }

    @Override
    public List<Integer> getPlanNos(String crossId) throws BusinessException {
        CrossParam cross = findById(crossId);
        return cross.getPlanNoList() != null ? cross.getPlanNoList() : new ArrayList<>();
    }

    private void validateCrossParam(CrossParam crossParam) throws BusinessException {
        if (crossParam.getCrossId() == null || crossParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (crossParam.getCrossName() == null || crossParam.getCrossName().trim().isEmpty()) {
            throw new ValidationException("crossName", "路口名称不能为空");
        }

        if (crossParam.getLaneNoList() == null || crossParam.getLaneNoList().isEmpty()) {
            throw new ValidationException("laneNoList", "车道序号列表不能为空");
        }

        if (crossParam.getSignalGroupNoList() == null || crossParam.getSignalGroupNoList().isEmpty()) {
            throw new ValidationException("signalGroupNoList", "信号组序号列表不能为空");
        }

        if (crossParam.getPlanNoList() == null || crossParam.getPlanNoList().isEmpty()) {
            throw new ValidationException("planNoList", "配时方案序号列表不能为空");
        }
    }

    private boolean matchesCriteria(CrossParam cross, CrossQueryDto query) {
        if (query.getCrossId() != null && !query.getCrossId().equals(cross.getCrossId())) {
            return false;
        }

        if (query.getCrossName() != null &&
                (cross.getCrossName() == null || !cross.getCrossName().contains(query.getCrossName()))) {
            return false;
        }

        if (query.getState() != null) {
            CrossState state = crossStateStorage.get(cross.getCrossId());
            if (state == null || !query.getState().equals(state.getValue())) {
                return false;
            }
        }

        return true;
    }

    private CrossInfoVo convertToVo(CrossParam cross) {
        CrossInfoVo vo = new CrossInfoVo();
        vo.setCrossId(cross.getCrossId());
        vo.setCrossName(cross.getCrossName());
        vo.setFeature(cross.getFeature());
        vo.setGrade(cross.getGrade());
//        vo.setLongitude(cross.getLongitude());
//        vo.setLatitude(cross.getLatitude());
        vo.setLaneCount(cross.getLaneNoList() != null ? cross.getLaneNoList().size() : 0);
        vo.setSignalGroupCount(cross.getSignalGroupNoList() != null ? cross.getSignalGroupNoList().size() : 0);
//        vo.setLastUpdateTime(cross.getUpdateTime());

        // 设置状态
        CrossState state = crossStateStorage.get(cross.getCrossId());
        if (state != null) {
            vo.setState(state.getValue());
        } else {
            vo.setState(SystemState.OFFLINE);
        }

        return vo;
    }

    private void initializeSampleData() {
        // 创建示例路口数据
//        CrossParam cross1 = new CrossParam("11010000100001", "示例路口1");
//        cross1.setFeature(CrossFeature.CROSS);
//        cross1.setGrade(CrossGrade.LEVEL_1);
////        cross1.setLongitude(116.397128);
////        cross1.setLatitude(39.916527);
//
//        List<Integer> laneNos1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
//        List<Integer> signalGroupNos1 = Arrays.asList(1, 2, 3, 4);
//        List<Integer> planNos1 = Arrays.asList(1, 2, 3);
//
//        cross1.setLaneNoList(laneNos1);
//        cross1.setSignalGroupNoList(signalGroupNos1);
//        cross1.setPlanNoList(planNos1);
//
//        CrossParam cross2 = new CrossParam("11010000100002", "示例路口2");
//        cross2.setFeature(CrossFeature.T_SHAPE);
//        cross2.setGrade(CrossGrade.LEVEL_2);
////        cross2.setLongitude(116.407128);
////        cross2.setLatitude(39.926527);
//
//        List<Integer> laneNos2 = Arrays.asList(1, 2, 3, 4, 5, 6);
//        List<Integer> signalGroupNos2 = Arrays.asList(1, 2, 3);
//        List<Integer> planNos2 = Arrays.asList(1, 2);
//
//        cross2.setLaneNoList(laneNos2);
//        cross2.setSignalGroupNoList(signalGroupNos2);
//        cross2.setPlanNoList(planNos2);
//
//        try {
//            save(cross1);
//            save(cross2);
//            logger.info("示例路口数据初始化完成");
//        } catch (BusinessException e) {
//            logger.error("示例路口数据初始化失败", e);
//        }
    }
}