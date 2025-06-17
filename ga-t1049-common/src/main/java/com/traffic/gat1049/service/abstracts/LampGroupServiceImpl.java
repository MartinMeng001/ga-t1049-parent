package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.LampGroup;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;
import com.traffic.gat1049.service.interfaces.LampGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号灯组服务实现
 */
public class LampGroupServiceImpl implements LampGroupService {

    private static final Logger logger = LoggerFactory.getLogger(LampGroupServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 信号灯组存储 - 使用复合键：crossId + "-" + lampGroupNo
    private final Map<String, LampGroup> lampGroupStorage = new ConcurrentHashMap<>();

    public LampGroupServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        initializeSampleData();
    }

    @Override
    public LampGroup findById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "信号灯组ID不能为空");
        }

        LampGroup lampGroup = lampGroupStorage.get(id);
        if (lampGroup == null) {
            throw new DataNotFoundException("LampGroup", id);
        }

        return lampGroup;
    }

    @Override
    public List<LampGroup> findAll() throws BusinessException {
        return new ArrayList<>(lampGroupStorage.values());
    }

    @Override
    public List<LampGroup> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<LampGroup> allLampGroups = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allLampGroups.size());

        if (start >= allLampGroups.size()) {
            return new ArrayList<>();
        }

        return allLampGroups.subList(start, end);
    }

    @Override
    public LampGroup save(LampGroup lampGroup) throws BusinessException {
        if (lampGroup == null) {
            throw new ValidationException("lampGroup", "信号灯组参数不能为空");
        }

        validateLampGroup(lampGroup);

        String key = generateKey(lampGroup.getCrossId(), lampGroup.getLampGroupNo());

        // 检查是否已存在
        if (lampGroupStorage.containsKey(key)) {
            throw new BusinessException("DUPLICATE_LAMP_GROUP",
                    String.format("信号灯组已存在: crossId=%s, lampGroupNo=%d",
                            lampGroup.getCrossId(), lampGroup.getLampGroupNo()));
        }

        //lampGroup.setCreateTime(LocalDateTime.now());
        //lampGroup.setUpdateTime(LocalDateTime.now());

        lampGroupStorage.put(key, lampGroup);

        logger.info("保存信号灯组: crossId={}, lampGroupNo={}, direction={}, type={}",
                lampGroup.getCrossId(), lampGroup.getLampGroupNo(),
                lampGroup.getDirection(), lampGroup.getType());

        return lampGroup;
    }

    @Override
    public LampGroup update(LampGroup lampGroup) throws BusinessException {
        if (lampGroup == null) {
            throw new ValidationException("lampGroup", "信号灯组参数不能为空");
        }

        validateLampGroup(lampGroup);

        String key = generateKey(lampGroup.getCrossId(), lampGroup.getLampGroupNo());

        if (!lampGroupStorage.containsKey(key)) {
            throw new DataNotFoundException("LampGroup", key);
        }

        //lampGroup.setUpdateTime(LocalDateTime.now());
        lampGroupStorage.put(key, lampGroup);

        logger.info("更新信号灯组: crossId={}, lampGroupNo={}, direction={}, type={}",
                lampGroup.getCrossId(), lampGroup.getLampGroupNo(),
                lampGroup.getDirection(), lampGroup.getType());

        return lampGroup;
    }

    @Override
    public void deleteById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "信号灯组ID不能为空");
        }

        LampGroup removed = lampGroupStorage.remove(id);
        if (removed == null) {
            throw new DataNotFoundException("LampGroup", id);
        }

        logger.info("删除信号灯组: id={}", id);
    }

    @Override
    public boolean existsById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return lampGroupStorage.containsKey(id);
    }

    @Override
    public long count() throws BusinessException {
        return lampGroupStorage.size();
    }

    @Override
    public List<LampGroup> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataPrider.getLampGroupsByCrossId(crossId);
//        return lampGroupStorage.values().stream()
//                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()))
//                .sorted(Comparator.comparing(LampGroup::getLampGroupNo))
//                .collect(Collectors.toList());
    }

    @Override
    public LampGroup findByCrossIdAndLampGroupNo(String crossId, Integer lampGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (lampGroupNo == null) {
            throw new ValidationException("lampGroupNo", "信号灯组序号不能为空");
        }

        String key = generateKey(crossId, lampGroupNo);
        LampGroup lampGroup = dataPrider.getLampGroupByCrossIdAndNo(crossId, lampGroupNo.toString());//lampGroupStorage.get(key);

        if (lampGroup == null) {
            throw new DataNotFoundException("LampGroup",
                    String.format("crossId=%s, lampGroupNo=%d", crossId, lampGroupNo));
        }

        return lampGroup;
    }

    @Override
    public List<LampGroup> findByDirection(Direction direction) throws BusinessException {
        if (direction == null) {
            throw new ValidationException("direction", "进口方向不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> direction.equals(lampGroup.getDirection()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LampGroup> findByType(LampGroupType type) throws BusinessException {
        if (type == null) {
            throw new ValidationException("type", "信号灯组类型不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> type.equals(lampGroup.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LampGroup> findByCrossIdAndDirection(String crossId, Direction direction) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (direction == null) {
            throw new ValidationException("direction", "进口方向不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()) &&
                        direction.equals(lampGroup.getDirection()))
                .sorted(Comparator.comparing(LampGroup::getLampGroupNo))
                .collect(Collectors.toList());
    }

    @Override
    public List<LampGroup> findByCrossIdAndType(String crossId, LampGroupType type) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (type == null) {
            throw new ValidationException("type", "信号灯组类型不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()) &&
                        type.equals(lampGroup.getType()))
                .sorted(Comparator.comparing(LampGroup::getLampGroupNo))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        List<String> keysToRemove = lampGroupStorage.entrySet().stream()
                .filter(entry -> crossId.equals(entry.getValue().getCrossId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        keysToRemove.forEach(lampGroupStorage::remove);

        logger.info("删除路口的所有信号灯组: crossId={}, 删除数量={}", crossId, keysToRemove.size());
    }

    @Override
    public void deleteByCrossIdAndLampGroupNo(String crossId, Integer lampGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (lampGroupNo == null) {
            throw new ValidationException("lampGroupNo", "信号灯组序号不能为空");
        }

        String key = generateKey(crossId, lampGroupNo);
        LampGroup removed = lampGroupStorage.remove(key);

        if (removed == null) {
            throw new DataNotFoundException("LampGroup",
                    String.format("crossId=%s, lampGroupNo=%d", crossId, lampGroupNo));
        }

        logger.info("删除信号灯组: crossId={}, lampGroupNo={}", crossId, lampGroupNo);
    }

    @Override
    public List<Integer> getLampGroupNos(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()))
                .map(LampGroup::getLampGroupNo)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 验证信号灯组参数
     */
    private void validateLampGroup(LampGroup lampGroup) throws BusinessException {
        if (lampGroup.getCrossId() == null || lampGroup.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (lampGroup.getLampGroupNo() == null) {
            throw new ValidationException("lampGroupNo", "信号灯组序号不能为空");
        }

        if (lampGroup.getLampGroupNo() < 1 || lampGroup.getLampGroupNo() > 99) {
            throw new ValidationException("lampGroupNo", "信号灯组序号必须在1-99之间");
        }

        if (lampGroup.getDirection() == null) {
            throw new ValidationException("direction", "进口方向不能为空");
        }

        if (lampGroup.getType() == null) {
            throw new ValidationException("type", "信号灯组类型不能为空");
        }
    }

    /**
     * 生成存储键
     */
    private String generateKey(String crossId, Integer lampGroupNo) {
        return crossId + "-" + lampGroupNo;
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        // 为示例路口1创建信号灯组
        String crossId1 = "11010000100001";

        try {
            // 东进口信号灯组
            LampGroup eastMotor = new LampGroup(crossId1, 1, Direction.EAST, LampGroupType.MOTOR_VEHICLE);
            save(eastMotor);

            LampGroup eastLeft = new LampGroup(crossId1, 2, Direction.EAST, LampGroupType.MOTOR_LEFT);
            save(eastLeft);

            // 西进口信号灯组
            LampGroup westMotor = new LampGroup(crossId1, 3, Direction.WEST, LampGroupType.MOTOR_VEHICLE);
            save(westMotor);

            LampGroup westLeft = new LampGroup(crossId1, 4, Direction.WEST, LampGroupType.MOTOR_LEFT);
            save(westLeft);

            // 南进口信号灯组
            LampGroup southMotor = new LampGroup(crossId1, 5, Direction.SOUTH, LampGroupType.MOTOR_VEHICLE);
            save(southMotor);

            LampGroup southLeft = new LampGroup(crossId1, 6, Direction.SOUTH, LampGroupType.MOTOR_LEFT);
            save(southLeft);

            // 北进口信号灯组
            LampGroup northMotor = new LampGroup(crossId1, 7, Direction.NORTH, LampGroupType.MOTOR_VEHICLE);
            save(northMotor);

            LampGroup northLeft = new LampGroup(crossId1, 8, Direction.NORTH, LampGroupType.MOTOR_LEFT);
            save(northLeft);

            // 为示例路口2创建信号灯组
            String crossId2 = "11010000100002";

            LampGroup east2Motor = new LampGroup(crossId2, 1, Direction.EAST, LampGroupType.MOTOR_VEHICLE);
            save(east2Motor);

            LampGroup west2Motor = new LampGroup(crossId2, 2, Direction.WEST, LampGroupType.MOTOR_VEHICLE);
            save(west2Motor);

            LampGroup south2Motor = new LampGroup(crossId2, 3, Direction.SOUTH, LampGroupType.MOTOR_VEHICLE);
            save(south2Motor);

            logger.info("示例信号灯组数据初始化完成");
        } catch (BusinessException e) {
            logger.error("示例信号灯组数据初始化失败", e);
        }
    }
}