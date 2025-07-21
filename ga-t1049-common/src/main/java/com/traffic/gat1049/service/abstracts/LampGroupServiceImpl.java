package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.converter.impl.LampGroupParamConverter;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;
import com.traffic.gat1049.repository.entity.LampGroupParamEntity;
import com.traffic.gat1049.repository.interfaces.LampGroupRepository;
import com.traffic.gat1049.service.interfaces.LampGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号灯组服务实现
 */
@Service
public class LampGroupServiceImpl implements LampGroupService {

    private static final Logger logger = LoggerFactory.getLogger(LampGroupServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 信号灯组存储 - 使用复合键：crossId + "-" + lampGroupNo
    private final Map<String, LampGroupParam> lampGroupStorage = new ConcurrentHashMap<>();
    @Autowired
    private LampGroupRepository lampGroupRepository;

    @Autowired
    private LampGroupParamConverter converter;

    public LampGroupServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        // initializeSampleData();
    }

    @Override
    public LampGroupParam findById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "信号灯组ID不能为空");
        }

        try {
            // 将字符串ID转换为整数（假设ID是数据库主键）
            Integer primaryKeyId = Integer.valueOf(id);

            // 使用MyBatis Plus的selectById方法
            LampGroupParamEntity entity = lampGroupRepository.selectById(primaryKeyId);

            if (entity == null) {
                throw new DataNotFoundException("LampGroupParam", id);
            }

            // 使用转换器将实体转换为协议对象
            LampGroupParam result = converter.toProtocol(entity);

            logger.debug("根据ID查询信号灯组成功: id={}, crossId={}, lampGroupNo={}",
                    id, result.getCrossId(), result.getLampGroupNo());

            return result;

        } catch (NumberFormatException e) {
            logger.error("ID格式错误: id={}", id, e);
            throw new ValidationException("id", "信号灯组ID格式不正确，必须是数字");
        } catch (Exception e) {
            logger.error("根据ID查询信号灯组失败: id={}", id, e);
            throw new BusinessException("QUERY_FAILED", "查询信号灯组失败: " + e.getMessage());
        }
    }

    @Override
    public List<LampGroupParam> findAll() throws BusinessException {
        return new ArrayList<>(lampGroupStorage.values());
    }

    @Override
    public List<LampGroupParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<LampGroupParam> allLampGroups = findAll();

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
    @Transactional
    public LampGroupParam save(LampGroupParam lampGroup) throws BusinessException {
        if (lampGroup == null) {
            throw new ValidationException("lampGroup", "信号灯组参数不能为空");
        }

        // 1. 参数验证
        validateLampGroup(lampGroup);

        // 2. 业务逻辑验证：检查是否已存在
        validateLampGroupUniqueness(lampGroup);

        try {
            // 3. 转换为数据库实体
            LampGroupParamEntity entity = converter.toEntity(lampGroup);

            // 4. 设置默认值
            setDefaultValues(entity);

            // 5. 保存到数据库
            int result = lampGroupRepository.insert(entity);
            if (result <= 0) {
                throw new BusinessException("SAVE_FAILED", "保存信号灯组失败，数据库操作返回0");
            }

            // 6. 转换并返回结果
            LampGroupParam savedLampGroup = converter.toProtocol(entity);

            logger.info("成功保存信号灯组: crossId={}, lampGroupNo={}, id={}",
                    savedLampGroup.getCrossId(),
                    savedLampGroup.getLampGroupNo(),
                    entity.getId());

            return savedLampGroup;

        } catch (DataAccessException e) {
            logger.error("数据库操作失败: crossId={}, lampGroupNo={}",
                    lampGroup.getCrossId(), lampGroup.getLampGroupNo(), e);
            throw new BusinessException("DATABASE_ERROR", "数据库操作失败: " + e.getMessage(), e);
        } catch (DataConversionException e) {
            logger.error("数据转换失败: crossId={}, lampGroupNo={}",
                    lampGroup.getCrossId(), lampGroup.getLampGroupNo(), e);
            throw new BusinessException("CONVERSION_ERROR", "数据转换失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("保存信号灯组时发生未知错误: crossId={}, lampGroupNo={}",
                    lampGroup.getCrossId(), lampGroup.getLampGroupNo(), e);
            throw new BusinessException("UNKNOWN_ERROR", "保存失败: " + e.getMessage(), e);
        }
    }

    @Override
    public LampGroupParam update(LampGroupParam lampGroup) throws BusinessException {
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

        LampGroupParam removed = lampGroupStorage.remove(id);
        if (removed == null) {
            throw new DataNotFoundException("LampGroupParam", id);
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
    public List<LampGroupParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        List<LampGroupParamEntity> entities = lampGroupRepository.findByCrossId(crossId);
        if (entities == null) {
            throw new DataNotFoundException("LampGroupParam");
        }

        // 使用转换器将实体转换为协议对象
        List<LampGroupParam> result = converter.toProtocolList(entities);
        return result;
    }

    @Override
    public List<LampGroupParam> findAllBasicByCrossId(String crossId) throws BusinessException {
        if(crossId == null || crossId.trim().isEmpty()){
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        List<LampGroupParamEntity> entities = lampGroupRepository.findAllBasicByCrossId(crossId);
        if(entities == null){
            throw new DataNotFoundException("LampGroupParam");
        }

        // 使用转换器将实体转换为协议对象
        List<LampGroupParam> result = converter.toProtocolList(entities);
        return result;
    }

    @Override
    public LampGroupParam findByCrossIdAndLampGroupNo(String crossId, Integer lampGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (lampGroupNo == null) {
            throw new ValidationException("lampGroupNo", "信号灯组序号不能为空");
        }

        String key = generateKey(crossId, lampGroupNo);
        LampGroupParam lampGroup = dataPrider.getLampGroupByCrossIdAndNo(crossId, lampGroupNo.toString());//lampGroupStorage.get(key);

        if (lampGroup == null) {
            throw new DataNotFoundException("LampGroup",
                    String.format("crossId=%s, lampGroupNo=%d", crossId, lampGroupNo));
        }

        return lampGroup;
    }

    @Override
    public List<LampGroupParam> findByDirection(Direction direction) throws BusinessException {
        if (direction == null) {
            throw new ValidationException("direction", "进口方向不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> direction.equals(lampGroup.getDirection()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LampGroupParam> findByType(LampGroupType type) throws BusinessException {
        if (type == null) {
            throw new ValidationException("type", "信号灯组类型不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> type.equals(lampGroup.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LampGroupParam> findByCrossIdAndDirection(String crossId, Direction direction) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (direction == null) {
            throw new ValidationException("direction", "进口方向不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()) &&
                        direction.equals(lampGroup.getDirection()))
                .sorted(Comparator.comparing(LampGroupParam::getLampGroupNo))
                .collect(Collectors.toList());
    }

    @Override
    public List<LampGroupParam> findByCrossIdAndType(String crossId, LampGroupType type) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (type == null) {
            throw new ValidationException("type", "信号灯组类型不能为空");
        }

        return lampGroupStorage.values().stream()
                .filter(lampGroup -> crossId.equals(lampGroup.getCrossId()) &&
                        type.equals(lampGroup.getType()))
                .sorted(Comparator.comparing(LampGroupParam::getLampGroupNo))
                .collect(Collectors.toList());
    }

    @Override
    public LampGroupParam findByCrossIdAndDirectionAndType(String crossId, Direction direction, LampGroupType type) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (type == null) {
            throw new ValidationException("type", "信号灯组类型不能为空");
        }
        if (direction == null) {
            throw new ValidationException("direction", "进口方向不能为空");
        }
        try {
            // 将字符串ID转换为整数（假设ID是数据库主键）
            // 使用MyBatis Plus的selectById方法
            LampGroupParamEntity entity = lampGroupRepository.findByCrossIdAndDirectionAndType(crossId, direction.getCode(), type.getCode());

            if (entity == null) {
                throw new DataNotFoundException("LampGroupParam");
            }

            // 使用转换器将实体转换为协议对象
            LampGroupParam result = converter.toProtocol(entity);

            logger.debug("根据ID查询信号灯组成功: crossId={}, lampGroupNo={}",
                    result.getCrossId(), result.getLampGroupNo());

            return result;

        } catch (Exception e) {
            logger.error("根据CrossID查询信号灯组失败: id={}", crossId, e);
            throw new BusinessException("QUERY_FAILED", "查询信号灯组失败: " + e.getMessage());
        }
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
        LampGroupParam removed = lampGroupStorage.remove(key);

        if (removed == null) {
            throw new DataNotFoundException("LampGroupParam",
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
                .map(LampGroupParam::getLampGroupNo)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 验证信号灯组参数
     */
    private void validateLampGroup(LampGroupParam lampGroup) throws BusinessException {
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
     * 验证信号灯组的唯一性
     */
    private void validateLampGroupUniqueness(LampGroupParam lampGroup) throws BusinessException {
        try {
            // 检查是否已存在相同的路口编号和信号灯组序号
            Boolean exists = lampGroupRepository.existsByCrossIdAndLampGroupNo(
                    lampGroup.getCrossId(),
                    lampGroup.getLampGroupNo());

            if (exists != null && exists) {
                throw new BusinessException("DUPLICATE_LAMP_GROUP",
                        String.format("信号灯组已存在: crossId=%s, lampGroupNo=%d",
                                lampGroup.getCrossId(), lampGroup.getLampGroupNo()));
            }

            logger.debug("信号灯组唯一性验证通过: crossId={}, lampGroupNo={}",
                    lampGroup.getCrossId(), lampGroup.getLampGroupNo());

        } catch (BusinessException e) {
            throw e; // 重新抛出业务异常
        } catch (Exception e) {
            logger.error("验证信号灯组唯一性时发生错误", e);
            throw new BusinessException("VALIDATION_ERROR", "唯一性验证失败: " + e.getMessage(), e);
        }
    }
    /**
     * 设置实体的默认值
     */
    private void setDefaultValues(LampGroupParamEntity entity) {
        // 设置有效标志为1（有效）
        if (entity.getValid() == null) {
            entity.setValid(1);
        }

        // 设置创建和更新时间（如果转换器没有设置的话）
        LocalDateTime now = LocalDateTime.now();
        if (entity.getCreatedTime() == null) {
            entity.setCreatedTime(now);
        }
        if (entity.getUpdatedTime() == null) {
            entity.setUpdatedTime(now);
        }

        logger.debug("设置默认值完成: valid={}, createdTime={}",
                entity.getValid(), entity.getCreatedTime());
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
            LampGroupParam eastMotor = new LampGroupParam(crossId1, 1, Direction.EAST, LampGroupType.MOTOR_VEHICLE);
            save(eastMotor);

            LampGroupParam eastLeft = new LampGroupParam(crossId1, 2, Direction.EAST, LampGroupType.MOTOR_LEFT);
            save(eastLeft);

            // 西进口信号灯组
            LampGroupParam westMotor = new LampGroupParam(crossId1, 3, Direction.WEST, LampGroupType.MOTOR_VEHICLE);
            save(westMotor);

            LampGroupParam westLeft = new LampGroupParam(crossId1, 4, Direction.WEST, LampGroupType.MOTOR_LEFT);
            save(westLeft);

            // 南进口信号灯组
            LampGroupParam southMotor = new LampGroupParam(crossId1, 5, Direction.SOUTH, LampGroupType.MOTOR_VEHICLE);
            save(southMotor);

            LampGroupParam southLeft = new LampGroupParam(crossId1, 6, Direction.SOUTH, LampGroupType.MOTOR_LEFT);
            save(southLeft);

            // 北进口信号灯组
            LampGroupParam northMotor = new LampGroupParam(crossId1, 7, Direction.NORTH, LampGroupType.MOTOR_VEHICLE);
            save(northMotor);

            LampGroupParam northLeft = new LampGroupParam(crossId1, 8, Direction.NORTH, LampGroupType.MOTOR_LEFT);
            save(northLeft);

            // 为示例路口2创建信号灯组
            String crossId2 = "11010000100002";

            LampGroupParam east2Motor = new LampGroupParam(crossId2, 1, Direction.EAST, LampGroupType.MOTOR_VEHICLE);
            save(east2Motor);

            LampGroupParam west2Motor = new LampGroupParam(crossId2, 2, Direction.WEST, LampGroupType.MOTOR_VEHICLE);
            save(west2Motor);

            LampGroupParam south2Motor = new LampGroupParam(crossId2, 3, Direction.SOUTH, LampGroupType.MOTOR_VEHICLE);
            save(south2Motor);

            logger.info("示例信号灯组数据初始化完成");
        } catch (BusinessException e) {
            logger.error("示例信号灯组数据初始化失败", e);
        }
    }
}