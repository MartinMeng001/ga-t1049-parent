package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.intersection.LaneParam;
import com.traffic.gat1049.protocol.model.runtime.VarLaneStatus;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LaneAttribute;
import com.traffic.gat1049.model.enums.LaneFeature;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.VarLaneMode;
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
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 车道参数存储 - key: crossId_laneNo
    private final Map<String, LaneParam> laneStorage = new ConcurrentHashMap<>();

    // 可变车道状态存储 - key: crossId_laneNo
    private final Map<String, VarLaneStatus> varLaneStatusStorage = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public LaneServiceImpl() throws BusinessException {
        // 初始化示例数据
        initializeSampleData();
    }

    @Override
    public List<LaneParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataPrider.getLanesByCrossId(crossId);
//        return laneStorage.values().stream()
//                .filter(lane -> crossId.equals(lane.getCrossId()))
//                .sorted(Comparator.comparing(LaneParam::getLaneNo))
//                .collect(Collectors.toList());
    }

    @Override
    public LaneParam findByCrossIdAndLaneNo(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (laneNo == null) {
            throw new ValidationException("laneNo", "车道序号不能为空");
        }

        String key = generateKey(crossId, laneNo);
        LaneParam laneParam = dataPrider.getLaneByCrossIdAndNo(crossId, laneNo.toString());//laneStorage.get(key);
        if (laneParam == null) {
            throw new DataNotFoundException("LaneParam",
                    String.format("crossId=%s, laneNo=%d", crossId, laneNo));
        }

        return laneParam;
    }

    @Override
    public List<LaneParam> findByDirection(String crossId, Direction direction) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (direction == null) {
            throw new ValidationException("direction", "方向不能为空");
        }

        return laneStorage.values().stream()
                .filter(lane -> crossId.equals(lane.getCrossId()) &&
                        direction.equals(lane.getDirection()))
                .sorted(Comparator.comparing(LaneParam::getLaneNo))
                .collect(Collectors.toList());
    }

    @Override
    public List<LaneParam> findByAttribute(String crossId, LaneAttribute attribute) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (attribute == null) {
            throw new ValidationException("attribute", "车道属性不能为空");
        }

        return laneStorage.values().stream()
                .filter(lane -> crossId.equals(lane.getCrossId()) &&
                        attribute.equals(lane.getAttribute()))
                .sorted(Comparator.comparing(LaneParam::getLaneNo))
                .collect(Collectors.toList());
    }

    @Override
    public List<LaneParam> findByMovement(String crossId, LaneMovement movement) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (movement == null) {
            throw new ValidationException("movement", "转向不能为空");
        }

        return laneStorage.values().stream()
                .filter(lane -> crossId.equals(lane.getCrossId()) &&
                        movement.equals(lane.getMovement()))
                .sorted(Comparator.comparing(LaneParam::getLaneNo))
                .collect(Collectors.toList());
    }

    @Override
    public LaneParam save(LaneParam laneParam) throws BusinessException {
        if (laneParam == null) {
            throw new ValidationException("laneParam", "车道参数不能为空");
        }

        validateLaneParam(laneParam);

        String key = generateKey(laneParam.getCrossId(), laneParam.getLaneNo());

        // 检查是否已存在
        if (laneStorage.containsKey(key)) {
            throw new BusinessException("LANE_ALREADY_EXISTS",
                    String.format("车道已存在: crossId=%s, laneNo=%d",
                            laneParam.getCrossId(), laneParam.getLaneNo()));
        }

//        laneParam.setCreateTime(LocalDateTime.now());
//        laneParam.setUpdateTime(LocalDateTime.now());

        laneStorage.put(key, laneParam);

        // 如果是可变车道，初始化可变车道状态
        if (isVariableLane(laneParam)) {
            initializeVarLaneStatus(laneParam);
        }

        logger.info("保存车道参数: crossId={}, laneNo={}, direction={}, movement={}",
                laneParam.getCrossId(), laneParam.getLaneNo(),
                laneParam.getDirection(), laneParam.getMovement());

        return laneParam;
    }

    @Override
    public LaneParam update(LaneParam laneParam) throws BusinessException {
        if (laneParam == null) {
            throw new ValidationException("laneParam", "车道参数不能为空");
        }

        validateLaneParam(laneParam);

        String key = generateKey(laneParam.getCrossId(), laneParam.getLaneNo());

        if (!laneStorage.containsKey(key)) {
            throw new DataNotFoundException("LaneParam",
                    String.format("crossId=%s, laneNo=%d",
                            laneParam.getCrossId(), laneParam.getLaneNo()));
        }

//        laneParam.setUpdateTime(LocalDateTime.now());
        laneStorage.put(key, laneParam);

        // 更新可变车道状态（如果适用）
        if (isVariableLane(laneParam)) {
            updateVarLaneStatusForLane(laneParam);
        } else {
            // 如果不再是可变车道，删除可变车道状态
            varLaneStatusStorage.remove(key);
        }

        logger.info("更新车道参数: crossId={}, laneNo={}, direction={}, movement={}",
                laneParam.getCrossId(), laneParam.getLaneNo(),
                laneParam.getDirection(), laneParam.getMovement());

        return laneParam;
    }

    @Override
    public void delete(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (laneNo == null) {
            throw new ValidationException("laneNo", "车道序号不能为空");
        }

        String key = generateKey(crossId, laneNo);

        LaneParam removed = laneStorage.remove(key);
        if (removed == null) {
            throw new DataNotFoundException("LaneParam",
                    String.format("crossId=%s, laneNo=%d", crossId, laneNo));
        }

        // 同时删除可变车道状态
        varLaneStatusStorage.remove(key);

        logger.info("删除车道参数: crossId={}, laneNo={}", crossId, laneNo);
    }

    @Override
    public VarLaneStatus getVarLaneStatus(String crossId, Integer laneNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (laneNo == null) {
            throw new ValidationException("laneNo", "车道序号不能为空");
        }

        Object obj = dataPrider.getVarLaneStatusByCrossId(crossId);
        return OBJECT_MAPPER.convertValue(obj, VarLaneStatus.class);
    }

    @Override
    public void updateVarLaneStatus(VarLaneStatus varLaneStatus) throws BusinessException {
        if (varLaneStatus == null) {
            throw new ValidationException("varLaneStatus", "可变车道状态不能为空");
        }

        validateVarLaneStatus(varLaneStatus);

        String crossId = varLaneStatus.getCrossId();
        Integer laneNo = varLaneStatus.getLaneNo();

        // 验证车道是否存在且为可变车道
        LaneParam laneParam = findByCrossIdAndLaneNo(crossId, laneNo);
        if (!isVariableLane(laneParam)) {
            throw new BusinessException("NOT_VARIABLE_LANE",
                    String.format("车道不是可变车道: crossId=%s, laneNo=%d", crossId, laneNo));
        }

        // 验证新的转向是否在允许的可变转向列表中
        LaneMovement newMovement = varLaneStatus.getCurMovement();
        if (!laneParam.getVarMovementList().contains(newMovement)) {
            throw new ValidationException("curMovement",
                    String.format("转向 %s 不在车道 %d 的可变转向列表中",
                            newMovement.getDescription(), laneNo));
        }

        //varLaneStatus.setUpdateTime(LocalDateTime.now());

        String key = generateKey(crossId, laneNo);
        varLaneStatusStorage.put(key, varLaneStatus);

        logger.info("更新可变车道状态: crossId={}, laneNo={}, movement={}, mode={}",
                crossId, laneNo, newMovement, varLaneStatus.getCurMode());
    }

    @Override
    public List<VarLaneStatus> getVarLanes() throws BusinessException {
        List<Object> objs = dataPrider.getAllVarLaneStatus();

        return objs.stream()
                .map(obj ->{
                    try{
                        return OBJECT_MAPPER.convertValue(obj, VarLaneStatus.class);
                    }catch (IllegalArgumentException e){
                        logger.warn("转换 VarLaneStatus 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 私有辅助方法

    private String generateKey(String crossId, Integer laneNo) {
        return crossId + "_" + laneNo;
    }

    private void validateLaneParam(LaneParam laneParam) throws BusinessException {
        if (laneParam.getCrossId() == null || laneParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (laneParam.getLaneNo() == null) {
            throw new ValidationException("laneNo", "车道序号不能为空");
        }

        if (laneParam.getLaneNo() < 1 || laneParam.getLaneNo() > 99) {
            throw new ValidationException("laneNo", "车道序号必须在1-99之间");
        }

        if (laneParam.getDirection() == null) {
            throw new ValidationException("direction", "方向不能为空");
        }

        if (laneParam.getAttribute() == null) {
            throw new ValidationException("attribute", "车道属性不能为空");
        }

        if (laneParam.getMovement() == null) {
            throw new ValidationException("movement", "转向不能为空");
        }

        if (laneParam.getFeature() == null) {
            throw new ValidationException("feature", "车道特性不能为空");
        }

        // 验证方位角范围
        if (laneParam.getAzimuth() != null &&
                (laneParam.getAzimuth() < 0 || laneParam.getAzimuth() > 359)) {
            throw new ValidationException("azimuth", "方位角必须在0-359度之间");
        }

        // 验证待行区值
        if (laneParam.getWaitingArea() != null &&
                (laneParam.getWaitingArea() != 0 && laneParam.getWaitingArea() != 1)) {
            throw new ValidationException("waitingArea", "待行区值必须为0或1");
        }
    }

    private void validateVarLaneStatus(VarLaneStatus varLaneStatus) throws BusinessException {
        if (varLaneStatus.getCrossId() == null || varLaneStatus.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (varLaneStatus.getLaneNo() == null) {
            throw new ValidationException("laneNo", "车道序号不能为空");
        }

        if (varLaneStatus.getCurMovement() == null) {
            throw new ValidationException("curMovement", "当前转向不能为空");
        }

        if (varLaneStatus.getCurMode() == null) {
            throw new ValidationException("curMode", "当前控制方式不能为空");
        }
    }

    private boolean isVariableLane(LaneParam laneParam) {
        return laneParam.getVarMovementList() != null &&
                !laneParam.getVarMovementList().isEmpty();
    }

    private void initializeVarLaneStatus(LaneParam laneParam) {
        VarLaneStatus varLaneStatus = new VarLaneStatus();
        varLaneStatus.setCrossId(laneParam.getCrossId());
        varLaneStatus.setLaneNo(laneParam.getLaneNo());
        varLaneStatus.setCurMovement(laneParam.getMovement()); // 使用默认转向
        varLaneStatus.setCurMode(VarLaneMode.FIXED); // 默认为固定模式
//        varLaneStatus.setCreateTime(LocalDateTime.now());
//        varLaneStatus.setUpdateTime(LocalDateTime.now());

        String key = generateKey(laneParam.getCrossId(), laneParam.getLaneNo());
        varLaneStatusStorage.put(key, varLaneStatus);

        logger.info("初始化可变车道状态: crossId={}, laneNo={}",
                laneParam.getCrossId(), laneParam.getLaneNo());
    }

    private void updateVarLaneStatusForLane(LaneParam laneParam) {
        String key = generateKey(laneParam.getCrossId(), laneParam.getLaneNo());
        VarLaneStatus existingStatus = varLaneStatusStorage.get(key);

        if (existingStatus == null) {
            // 如果之前没有可变车道状态，创建一个新的
            initializeVarLaneStatus(laneParam);
        } else {
            // 验证当前转向是否仍然在新的可变转向列表中
            if (!laneParam.getVarMovementList().contains(existingStatus.getCurMovement())) {
                // 如果当前转向不再允许，重置为默认转向
                existingStatus.setCurMovement(laneParam.getMovement());
//                existingStatus.setUpdateTime(LocalDateTime.now());
                varLaneStatusStorage.put(key, existingStatus);

                logger.info("重置可变车道状态转向: crossId={}, laneNo={}, newMovement={}",
                        laneParam.getCrossId(), laneParam.getLaneNo(), laneParam.getMovement());
            }
        }
    }

    private void initializeSampleData() {
//        try {
//            // 为示例路口1创建车道数据
//            String crossId1 = "11010000100001";
//
//            // 东进口车道
//            createSampleLane(crossId1, 1, Direction.EAST, LaneAttribute.ENTRY,
//                    LaneMovement.LEFT_TURN, LaneFeature.NORMAL, 90);
//            createSampleLane(crossId1, 2, Direction.EAST, LaneAttribute.ENTRY,
//                    LaneMovement.THROUGH, LaneFeature.NORMAL, 90);
//            createSampleLane(crossId1, 3, Direction.EAST, LaneAttribute.ENTRY,
//                    LaneMovement.RIGHT_TURN, LaneFeature.NORMAL, 90);
//
//            // 西进口车道
//            createSampleLane(crossId1, 4, Direction.WEST, LaneAttribute.ENTRY,
//                    LaneMovement.LEFT_TURN, LaneFeature.NORMAL, 270);
//            createSampleLane(crossId1, 5, Direction.WEST, LaneAttribute.ENTRY,
//                    LaneMovement.THROUGH, LaneFeature.NORMAL, 270);
//            createSampleLane(crossId1, 6, Direction.WEST, LaneAttribute.ENTRY,
//                    LaneMovement.RIGHT_TURN, LaneFeature.NORMAL, 270);
//
//            // 南进口车道（包含可变车道）
//            LaneParam varLane = createSampleLane(crossId1, 7, Direction.SOUTH, LaneAttribute.ENTRY,
//                    LaneMovement.THROUGH, LaneFeature.VARIABLE, 180);
//            varLane.setVarMovementList(Arrays.asList(LaneMovement.THROUGH, LaneMovement.LEFT_TURN));
//
//            createSampleLane(crossId1, 8, Direction.SOUTH, LaneAttribute.ENTRY,
//                    LaneMovement.RIGHT_TURN, LaneFeature.NORMAL, 180);
//
//            // 为示例路口2创建车道数据
//            String crossId2 = "11010000100002";
//
//            createSampleLane(crossId2, 1, Direction.EAST, LaneAttribute.ENTRY,
//                    LaneMovement.LEFT_TURN, LaneFeature.NORMAL, 90);
//            createSampleLane(crossId2, 2, Direction.EAST, LaneAttribute.ENTRY,
//                    LaneMovement.THROUGH, LaneFeature.NORMAL, 90);
//            createSampleLane(crossId2, 3, Direction.WEST, LaneAttribute.ENTRY,
//                    LaneMovement.THROUGH, LaneFeature.NORMAL, 270);
//            createSampleLane(crossId2, 4, Direction.WEST, LaneAttribute.ENTRY,
//                    LaneMovement.RIGHT_TURN, LaneFeature.NORMAL, 270);
//            createSampleLane(crossId2, 5, Direction.SOUTH, LaneAttribute.ENTRY,
//                    LaneMovement.LEFT_TURN, LaneFeature.NORMAL, 180);
//            createSampleLane(crossId2, 6, Direction.SOUTH, LaneAttribute.ENTRY,
//                    LaneMovement.RIGHT_TURN, LaneFeature.NORMAL, 180);

//            logger.info("示例车道数据初始化完成");
//        } catch (BusinessException e) {
//            logger.error("示例车道数据初始化失败", e);
//        }
    }

    private LaneParam createSampleLane(String crossId, Integer laneNo, Direction direction,
                                       LaneAttribute attribute, LaneMovement movement,
                                       LaneFeature feature, Integer azimuth) throws BusinessException {
        LaneParam laneParam = new LaneParam(crossId, laneNo, direction);
        laneParam.setAttribute(attribute);
        laneParam.setMovement(movement);
        laneParam.setFeature(feature);
        laneParam.setAzimuth(azimuth);
        laneParam.setWaitingArea(0); // 默认无待行区

        return save(laneParam);
    }
}