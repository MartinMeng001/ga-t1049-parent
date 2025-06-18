package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.system.SubRegionParam;
import com.traffic.gat1049.service.interfaces.SubRegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 子区服务实现
 */
public class SubRegionServiceImpl implements SubRegionService {

    private static final Logger logger = LoggerFactory.getLogger(SubRegionServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 子区参数存储
    private final Map<String, SubRegionParam> subRegionStorage = new ConcurrentHashMap<>();

    public SubRegionServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        initializeSampleData();
    }

    @Override
    public SubRegionParam findById(String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }

        SubRegionParam subRegionParam = dataPrider.getSubRegionById(subRegionId);//subRegionStorage.get(subRegionId);
        if (subRegionParam == null) {
            throw new DataNotFoundException("SubRegionParam", subRegionId);
        }

        return subRegionParam;
    }

    @Override
    public List<SubRegionParam> findAll() throws BusinessException {
        return dataPrider.getAllSubRegions();
        //return new ArrayList<>(subRegionStorage.values());
    }

    @Override
    public List<SubRegionParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<SubRegionParam> allSubRegions = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allSubRegions.size());

        if (start >= allSubRegions.size()) {
            return new ArrayList<>();
        }

        return allSubRegions.subList(start, end);
    }

    @Override
    public SubRegionParam save(SubRegionParam subRegionParam) throws BusinessException {
        if (subRegionParam == null) {
            throw new ValidationException("subRegionParam", "子区参数不能为空");
        }

        validateSubRegionParam(subRegionParam);

//        subRegionParam.setCreateTime(LocalDateTime.now());
//        subRegionParam.setUpdateTime(LocalDateTime.now());

        subRegionStorage.put(subRegionParam.getSubRegionId(), subRegionParam);

        logger.info("保存子区参数: subRegionId={}, subRegionName={}",
                subRegionParam.getSubRegionId(), subRegionParam.getSubRegionName());

        return subRegionParam;
    }

    @Override
    public SubRegionParam update(SubRegionParam subRegionParam) throws BusinessException {
        if (subRegionParam == null) {
            throw new ValidationException("subRegionParam", "子区参数不能为空");
        }

        String subRegionId = subRegionParam.getSubRegionId();
        if (!subRegionStorage.containsKey(subRegionId)) {
            throw new DataNotFoundException("SubRegionParam", subRegionId);
        }

        validateSubRegionParam(subRegionParam);

//        subRegionParam.setUpdateTime(LocalDateTime.now());
        subRegionStorage.put(subRegionId, subRegionParam);

        logger.info("更新子区参数: subRegionId={}, subRegionName={}",
                subRegionParam.getSubRegionId(), subRegionParam.getSubRegionName());

        return subRegionParam;
    }

    @Override
    public void deleteById(String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }

        SubRegionParam removed = subRegionStorage.remove(subRegionId);
        if (removed == null) {
            throw new DataNotFoundException("SubRegionParam", subRegionId);
        }

        logger.info("删除子区参数: subRegionId={}", subRegionId);
    }

    @Override
    public boolean existsById(String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            return false;
        }
        return subRegionStorage.containsKey(subRegionId);
    }

    @Override
    public long count() throws BusinessException {
        return subRegionStorage.size();
    }

    @Override
    public List<SubRegionParam> findByName(String subRegionName) throws BusinessException {
        if (subRegionName == null || subRegionName.trim().isEmpty()) {
            throw new ValidationException("subRegionName", "子区名称不能为空");
        }

        return subRegionStorage.values().stream()
                .filter(subRegion -> subRegion.getSubRegionName() != null &&
                        subRegion.getSubRegionName().contains(subRegionName))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubRegionParam> findByRegionId(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new ValidationException("regionId", "区域编号不能为空");
        }

        // 这里需要通过区域服务来查找子区，暂时返回空列表
        // 在实际实现中，应该注入 RegionService 并调用其方法
        logger.warn("findByRegionId 方法需要集成 RegionService 才能完全实现");
        return new ArrayList<>();
    }

    @Override
    public List<String> getCrossIds(String subRegionId) throws BusinessException {
        SubRegionParam subRegion = findById(subRegionId);
        return subRegion.getCrossIdList() != null ?
                new ArrayList<>(subRegion.getCrossIdList()) : new ArrayList<>();
    }

    @Override
    public List<String> getKeyCrossIds(String subRegionId) throws BusinessException {
        SubRegionParam subRegion = findById(subRegionId);
        return subRegion.getKeyCrossIdList() != null ?
                new ArrayList<>(subRegion.getKeyCrossIdList()) : new ArrayList<>();
    }

    @Override
    public void addCross(String subRegionId, String crossId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SubRegionParam subRegion = findById(subRegionId);

        if (subRegion.getCrossIdList() == null) {
            subRegion.setCrossIdList(new ArrayList<>());
        }

        if (!subRegion.getCrossIdList().contains(crossId)) {
            subRegion.getCrossIdList().add(crossId);
//            subRegion.setUpdateTime(LocalDateTime.now());
            subRegionStorage.put(subRegionId, subRegion);

            logger.info("添加路口到子区: subRegionId={}, crossId={}", subRegionId, crossId);
        } else {
            logger.warn("路口已存在于子区中: subRegionId={}, crossId={}", subRegionId, crossId);
        }
    }

    @Override
    public void setKeyCross(String subRegionId, String crossId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SubRegionParam subRegion = findById(subRegionId);

        // 确保路口在子区的路口列表中
        if (subRegion.getCrossIdList() == null || !subRegion.getCrossIdList().contains(crossId)) {
            throw new ValidationException("crossId", "路口不在子区的路口列表中");
        }

        if (subRegion.getKeyCrossIdList() == null) {
            subRegion.setKeyCrossIdList(new ArrayList<>());
        }

        if (!subRegion.getKeyCrossIdList().contains(crossId)) {
            subRegion.getKeyCrossIdList().add(crossId);
//            subRegion.setUpdateTime(LocalDateTime.now());
            subRegionStorage.put(subRegionId, subRegion);

            logger.info("设置关键路口: subRegionId={}, crossId={}", subRegionId, crossId);
        } else {
            logger.warn("路口已是关键路口: subRegionId={}, crossId={}", subRegionId, crossId);
        }
    }

    /**
     * 验证子区参数
     */
    private void validateSubRegionParam(SubRegionParam subRegionParam) throws BusinessException {
        if (subRegionParam.getSubRegionId() == null || subRegionParam.getSubRegionId().trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }

        if (subRegionParam.getSubRegionName() == null || subRegionParam.getSubRegionName().trim().isEmpty()) {
            throw new ValidationException("subRegionName", "子区名称不能为空");
        }

        // 验证子区编号格式（11位数字）
        if (!subRegionParam.getSubRegionId().matches("\\d{11}")) {
            throw new ValidationException("subRegionId", "子区编号格式错误，应为11位数字");
        }

        if (subRegionParam.getCrossIdList() == null || subRegionParam.getCrossIdList().isEmpty()) {
            throw new ValidationException("crossIdList", "路口编号列表不能为空");
        }
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        // 创建示例子区数据
        SubRegionParam subRegion1 = new SubRegionParam("11010000100", "示例子区1");
        List<String> crossIds1 = Arrays.asList("11010000100001", "11010000100002");
        List<String> keyCrossIds1 = Arrays.asList("11010000100001");
        subRegion1.setCrossIdList(crossIds1);
        subRegion1.setKeyCrossIdList(keyCrossIds1);

        SubRegionParam subRegion2 = new SubRegionParam("11010000200", "示例子区2");
        List<String> crossIds2 = Arrays.asList("11010000200001", "11010000200002", "11010000200003");
        List<String> keyCrossIds2 = Arrays.asList("11010000200001", "11010000200002");
        subRegion2.setCrossIdList(crossIds2);
        subRegion2.setKeyCrossIdList(keyCrossIds2);

        try {
            save(subRegion1);
            save(subRegion2);
            logger.info("示例子区数据初始化完成");
        } catch (BusinessException e) {
            logger.error("示例子区数据初始化失败", e);
        }
    }
}