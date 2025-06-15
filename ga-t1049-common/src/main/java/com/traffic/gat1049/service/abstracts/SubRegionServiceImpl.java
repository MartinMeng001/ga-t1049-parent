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
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 内存存储，实际项目中应该从数据库获取
    private final Map<String, SubRegionParam> subRegionStorage = new ConcurrentHashMap<>();

    public SubRegionServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public SubRegionParam findById(String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }

        // 先从数据提供者获取
        try {
            List<SubRegionParam> subRegions = dataProvider.getSubRegionParams();
            if (subRegions != null) {
                for (SubRegionParam subRegion : subRegions) {
                    if (subRegionId.equals(subRegion.getSubRegionId())) {
                        return subRegion;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取子区数据失败，使用本地存储", e);
        }

        SubRegionParam subRegion = subRegionStorage.get(subRegionId);
        if (subRegion == null) {
            throw new DataNotFoundException("SubRegionParam", subRegionId);
        }

        return subRegion;
    }

    @Override
    public List<SubRegionParam> findAll() throws BusinessException {
        try {
            List<SubRegionParam> subRegions = dataProvider.getSubRegionParams();
            if (subRegions != null && !subRegions.isEmpty()) {
                return subRegions;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取子区列表失败，使用本地存储", e);
        }

        return new ArrayList<>(subRegionStorage.values());
    }

    @Override
    public List<SubRegionParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<SubRegionParam> allSubRegions = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allSubRegions.size());

        if (startIndex >= allSubRegions.size()) {
            return new ArrayList<>();
        }

        return allSubRegions.subList(startIndex, endIndex);
    }

    @Override
    public SubRegionParam save(SubRegionParam subRegionParam) throws BusinessException {
        if (subRegionParam == null) {
            throw new ValidationException("subRegionParam", "子区参数不能为空");
        }

        validateSubRegionParam(subRegionParam);

        subRegionParam.setUpdateTime(LocalDateTime.now());
        if (subRegionParam.getCreateTime() == null) {
            subRegionParam.setCreateTime(LocalDateTime.now());
        }

        subRegionStorage.put(subRegionParam.getSubRegionId(), subRegionParam);
        logger.info("保存子区参数: {}", subRegionParam.getSubRegionName());

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

        logger.info("删除子区参数: {}", subRegionId);
    }

    @Override
    public List<SubRegionParam> findByName(String subRegionName) throws BusinessException {
        if (subRegionName == null || subRegionName.trim().isEmpty()) {
            throw new ValidationException("subRegionName", "子区名称不能为空");
        }

        return findAll().stream()
                .filter(subRegion -> subRegion.getSubRegionName() != null &&
                        subRegion.getSubRegionName().contains(subRegionName))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubRegionParam> findByRegionId(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new ValidationException("regionId", "区域编号不能为空");
        }

        // 根据子区编号规则，子区编号前9位是区域编号
        return findAll().stream()
                .filter(subRegion -> subRegion.getSubRegionId() != null &&
                        subRegion.getSubRegionId().startsWith(regionId))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getCrossIds(String subRegionId) throws BusinessException {
        SubRegionParam subRegion = findById(subRegionId);
        return subRegion.getCrossIdList() != null ? subRegion.getCrossIdList() : new ArrayList<>();
    }

    @Override
    public List<String> getKeyCrossIds(String subRegionId) throws BusinessException {
        SubRegionParam subRegion = findById(subRegionId);
        return subRegion.getKeyCrossIdList() != null ? subRegion.getKeyCrossIdList() : new ArrayList<>();
    }

    @Override
    public void addCross(String subRegionId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SubRegionParam subRegion = findById(subRegionId);

        if (subRegion.getCrossIdList() == null) {
            subRegion.setCrossIdList(new ArrayList<>());
        }

        if (!subRegion.getCrossIdList().contains(crossId)) {
            subRegion.getCrossIdList().add(crossId);
            save(subRegion);
            logger.info("向子区 {} 添加路口 {}", subRegionId, crossId);
        }
    }

    @Override
    public void setKeyCross(String subRegionId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        SubRegionParam subRegion = findById(subRegionId);

        // 确保路口在普通路口列表中
        if (subRegion.getCrossIdList() == null || !subRegion.getCrossIdList().contains(crossId)) {
            throw new ValidationException("crossId", "路口必须先添加到子区的普通路口列表中");
        }

        if (subRegion.getKeyCrossIdList() == null) {
            subRegion.setKeyCrossIdList(new ArrayList<>());
        }

        if (!subRegion.getKeyCrossIdList().contains(crossId)) {
            subRegion.getKeyCrossIdList().add(crossId);
            save(subRegion);
            logger.info("设置子区 {} 的关键路口 {}", subRegionId, crossId);
        }
    }

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
    }

    private void initializeSampleData() {
        try {
            // 初始化示例子区数据
            SubRegionParam subRegion1 = new SubRegionParam("11010000100", "朝阳区东部子区");
            subRegion1.setCrossIdList(Arrays.asList("11010000100001", "11010000100002"));
            subRegion1.setKeyCrossIdList(Arrays.asList("11010000100001"));
            subRegion1.setCreateTime(LocalDateTime.now());
            subRegionStorage.put(subRegion1.getSubRegionId(), subRegion1);

            SubRegionParam subRegion2 = new SubRegionParam("11010000101", "朝阳区西部子区");
            subRegion2.setCrossIdList(Arrays.asList("11010000100003", "11010000100004"));
            subRegion2.setKeyCrossIdList(Arrays.asList("11010000100003"));
            subRegion2.setCreateTime(LocalDateTime.now());
            subRegionStorage.put(subRegion2.getSubRegionId(), subRegion2);

            SubRegionParam subRegion3 = new SubRegionParam("11010000102", "海淀区南部子区");
            subRegion3.setCrossIdList(Arrays.asList("11010000100005", "11010000100006"));
            subRegion3.setKeyCrossIdList(Arrays.asList("11010000100005"));
            subRegion3.setCreateTime(LocalDateTime.now());
            subRegionStorage.put(subRegion3.getSubRegionId(), subRegion3);

            logger.info("初始化子区示例数据完成，共 {} 个子区", subRegionStorage.size());
        } catch (Exception e) {
            logger.error("初始化子区示例数据失败", e);
        }
    }
}