package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.PedestrianParam;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.PedestrianAttribute;
import com.traffic.gat1049.service.interfaces.PedestrianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 人行横道服务实现
 */
public class PedestrianServiceImpl implements PedestrianService {

    private static final Logger logger = LoggerFactory.getLogger(PedestrianServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 人行横道参数存储：使用crossId + pedestrianNo作为复合主键
    private final Map<String, PedestrianParam> pedestrianStorage = new ConcurrentHashMap<>();

    public PedestrianServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        //initializeSampleData();
    }

    @Override
    public PedestrianParam findById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "人行横道编号不能为空");
        }

        PedestrianParam pedestrianParam = pedestrianStorage.get(id);
        if (pedestrianParam == null) {
            throw new DataNotFoundException("PedestrianParam", id);
        }

        return pedestrianParam;
    }

    @Override
    public List<PedestrianParam> findAll() throws BusinessException {
        return new ArrayList<>(pedestrianStorage.values());
    }

    @Override
    public List<PedestrianParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<PedestrianParam> allPedestrians = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allPedestrians.size());

        if (start >= allPedestrians.size()) {
            return new ArrayList<>();
        }

        return allPedestrians.subList(start, end);
    }

    @Override
    public PedestrianParam save(PedestrianParam pedestrianParam) throws BusinessException {
        if (pedestrianParam == null) {
            throw new ValidationException("pedestrianParam", "人行横道参数不能为空");
        }

        validatePedestrianParam(pedestrianParam);

        pedestrianParam.setCreateTime(LocalDateTime.now());
        pedestrianParam.setUpdateTime(LocalDateTime.now());

        String key = generateKey(pedestrianParam.getCrossId(), pedestrianParam.getPedestrianNo());
        pedestrianStorage.put(key, pedestrianParam);

        logger.info("保存人行横道参数: crossId={}, pedestrianNo={}",
                pedestrianParam.getCrossId(), pedestrianParam.getPedestrianNo());

        return pedestrianParam;
    }

    @Override
    public PedestrianParam update(PedestrianParam pedestrianParam) throws BusinessException {
        if (pedestrianParam == null) {
            throw new ValidationException("pedestrianParam", "人行横道参数不能为空");
        }

        String key = generateKey(pedestrianParam.getCrossId(), pedestrianParam.getPedestrianNo());
        if (!pedestrianStorage.containsKey(key)) {
            throw new DataNotFoundException("PedestrianParam", key);
        }

        validatePedestrianParam(pedestrianParam);

        pedestrianParam.setUpdateTime(LocalDateTime.now());
        pedestrianStorage.put(key, pedestrianParam);

        logger.info("更新人行横道参数: crossId={}, pedestrianNo={}",
                pedestrianParam.getCrossId(), pedestrianParam.getPedestrianNo());

        return pedestrianParam;
    }

    @Override
    public void deleteById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "人行横道编号不能为空");
        }

        PedestrianParam removed = pedestrianStorage.remove(id);
        if (removed == null) {
            throw new DataNotFoundException("PedestrianParam", id);
        }

        logger.info("删除人行横道参数: id={}", id);
    }

    @Override
    public boolean existsById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return pedestrianStorage.containsKey(id);
    }

    @Override
    public long count() throws BusinessException {
        return pedestrianStorage.size();
    }

    @Override
    public List<PedestrianParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataPrider.getPedestriansByCrossId(crossId);
//        return pedestrianStorage.values().stream()
//                .filter(pedestrian -> crossId.equals(pedestrian.getCrossId()))
//                .collect(Collectors.toList());
    }

    @Override
    public PedestrianParam findByCrossIdAndPedestrianNo(String crossId, Integer pedestrianNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (pedestrianNo == null) {
            throw new ValidationException("pedestrianNo", "人行横道序号不能为空");
        }

        String key = generateKey(crossId, pedestrianNo);
        PedestrianParam pedestrianParam = dataPrider.getPedestrianByCrossIdAndNo(crossId, pedestrianNo.toString());//pedestrianStorage.get(key);
        if (pedestrianParam == null) {
            throw new DataNotFoundException("PedestrianParam", key);
        }

        return pedestrianParam;
    }

    @Override
    public List<PedestrianParam> findByDirection(Direction direction) throws BusinessException {
        if (direction == null) {
            throw new ValidationException("direction", "方向不能为空");
        }

        return pedestrianStorage.values().stream()
                .filter(pedestrian -> direction.equals(pedestrian.getDirection()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PedestrianParam> findByAttribute(PedestrianAttribute attribute) throws BusinessException {
        if (attribute == null) {
            throw new ValidationException("attribute", "人行横道属性不能为空");
        }

        return pedestrianStorage.values().stream()
                .filter(pedestrian -> attribute.equals(pedestrian.getAttribute()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PedestrianParam> findByCrossIdAndDirection(String crossId, Direction direction) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (direction == null) {
            throw new ValidationException("direction", "方向不能为空");
        }

        return pedestrianStorage.values().stream()
                .filter(pedestrian -> crossId.equals(pedestrian.getCrossId()) &&
                        direction.equals(pedestrian.getDirection()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getPedestrianNos(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        return pedestrianStorage.values().stream()
                .filter(pedestrian -> crossId.equals(pedestrian.getCrossId()))
                .map(PedestrianParam::getPedestrianNo)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        List<String> keysToRemove = pedestrianStorage.entrySet().stream()
                .filter(entry -> crossId.equals(entry.getValue().getCrossId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        keysToRemove.forEach(pedestrianStorage::remove);

        logger.info("删除路口{}的所有人行横道参数，共删除{}条", crossId, keysToRemove.size());
    }

    @Override
    public void deleteByCrossIdAndPedestrianNo(String crossId, Integer pedestrianNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (pedestrianNo == null) {
            throw new ValidationException("pedestrianNo", "人行横道序号不能为空");
        }

        String key = generateKey(crossId, pedestrianNo);
        PedestrianParam removed = pedestrianStorage.remove(key);
        if (removed == null) {
            throw new DataNotFoundException("PedestrianParam", key);
        }

        logger.info("删除人行横道参数: crossId={}, pedestrianNo={}", crossId, pedestrianNo);
    }

    /**
     * 验证人行横道参数
     */
    private void validatePedestrianParam(PedestrianParam pedestrianParam) throws BusinessException {
        if (pedestrianParam.getCrossId() == null || pedestrianParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (pedestrianParam.getPedestrianNo() == null) {
            throw new ValidationException("pedestrianNo", "人行横道序号不能为空");
        }

        if (pedestrianParam.getPedestrianNo() < 1 || pedestrianParam.getPedestrianNo() > 99) {
            throw new ValidationException("pedestrianNo", "人行横道序号必须在1-99之间");
        }

        if (pedestrianParam.getDirection() == null) {
            throw new ValidationException("direction", "人行横道方向不能为空");
        }

        if (pedestrianParam.getAttribute() == null) {
            throw new ValidationException("attribute", "人行横道属性不能为空");
        }
    }

    /**
     * 生成复合主键
     */
    private String generateKey(String crossId, Integer pedestrianNo) {
        return crossId + "-" + pedestrianNo;
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        try {
            // 为示例路口1创建人行横道
            PedestrianParam pedestrian1 = new PedestrianParam("11010000100001", 1, Direction.NORTH);
            pedestrian1.setAttribute(PedestrianAttribute.ONE_STAGE);

            PedestrianParam pedestrian2 = new PedestrianParam("11010000100001", 2, Direction.SOUTH);
            pedestrian2.setAttribute(PedestrianAttribute.ONE_STAGE);

            PedestrianParam pedestrian3 = new PedestrianParam("11010000100001", 3, Direction.EAST);
            pedestrian3.setAttribute(PedestrianAttribute.TWO_STAGE_ENTRANCE);

            PedestrianParam pedestrian4 = new PedestrianParam("11010000100001", 4, Direction.WEST);
            pedestrian4.setAttribute(PedestrianAttribute.TWO_STAGE_EXIT);

            // 为示例路口2创建人行横道
            PedestrianParam pedestrian5 = new PedestrianParam("11010000100002", 1, Direction.NORTH);
            pedestrian5.setAttribute(PedestrianAttribute.ONE_STAGE);

            PedestrianParam pedestrian6 = new PedestrianParam("11010000100002", 2, Direction.EAST);
            pedestrian6.setAttribute(PedestrianAttribute.ONE_STAGE);

            // 直接存储到map中，避免循环调用save方法
            String key1 = generateKey(pedestrian1.getCrossId(), pedestrian1.getPedestrianNo());
            pedestrian1.setCreateTime(LocalDateTime.now());
            pedestrian1.setUpdateTime(LocalDateTime.now());
            pedestrianStorage.put(key1, pedestrian1);

            String key2 = generateKey(pedestrian2.getCrossId(), pedestrian2.getPedestrianNo());
            pedestrian2.setCreateTime(LocalDateTime.now());
            pedestrian2.setUpdateTime(LocalDateTime.now());
            pedestrianStorage.put(key2, pedestrian2);

            String key3 = generateKey(pedestrian3.getCrossId(), pedestrian3.getPedestrianNo());
            pedestrian3.setCreateTime(LocalDateTime.now());
            pedestrian3.setUpdateTime(LocalDateTime.now());
            pedestrianStorage.put(key3, pedestrian3);

            String key4 = generateKey(pedestrian4.getCrossId(), pedestrian4.getPedestrianNo());
            pedestrian4.setCreateTime(LocalDateTime.now());
            pedestrian4.setUpdateTime(LocalDateTime.now());
            pedestrianStorage.put(key4, pedestrian4);

            String key5 = generateKey(pedestrian5.getCrossId(), pedestrian5.getPedestrianNo());
            pedestrian5.setCreateTime(LocalDateTime.now());
            pedestrian5.setUpdateTime(LocalDateTime.now());
            pedestrianStorage.put(key5, pedestrian5);

            String key6 = generateKey(pedestrian6.getCrossId(), pedestrian6.getPedestrianNo());
            pedestrian6.setCreateTime(LocalDateTime.now());
            pedestrian6.setUpdateTime(LocalDateTime.now());
            pedestrianStorage.put(key6, pedestrian6);

            logger.info("示例人行横道数据初始化完成");
        } catch (Exception e) {
            logger.error("示例人行横道数据初始化失败", e);
        }
    }
}