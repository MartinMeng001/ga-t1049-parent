package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.system.RegionParam;
import com.traffic.gat1049.protocol.model.system.SubRegionParam;
import com.traffic.gat1049.service.interfaces.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 区域服务实现
 */
public class RegionServiceImpl implements RegionService {

    private static final Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 内存存储，实际项目中应该从数据库获取
    private final Map<String, RegionParam> regionStorage = new ConcurrentHashMap<>();

    public RegionServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public RegionParam findById(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new ValidationException("regionId", "区域编号不能为空");
        }

        // 先从数据提供者获取
        try {
            List<RegionParam> regions = dataProvider.getRegionParams();
            if (regions != null) {
                for (RegionParam region : regions) {
                    if (regionId.equals(region.getRegionId())) {
                        return region;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取区域数据失败，使用本地存储", e);
        }

        RegionParam region = regionStorage.get(regionId);
        if (region == null) {
            throw new DataNotFoundException("RegionParam", regionId);
        }

        return region;
    }

    @Override
    public List<RegionParam> findAll() throws BusinessException {
        try {
            List<RegionParam> regions = dataProvider.getRegionParams();
            if (regions != null && !regions.isEmpty()) {
                return regions;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取区域列表失败，使用本地存储", e);
        }

        return new ArrayList<>(regionStorage.values());
    }

    @Override
    public List<RegionParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<RegionParam> allRegions = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allRegions.size());

        if (startIndex >= allRegions.size()) {
            return new ArrayList<>();
        }

        return allRegions.subList(startIndex, endIndex);
    }

    @Override
    public RegionParam save(RegionParam regionParam) throws BusinessException {
        if (regionParam == null) {
            throw new ValidationException("regionParam", "区域参数不能为空");
        }

        validateRegionParam(regionParam);

        regionParam.setUpdateTime(LocalDateTime.now());
        if (regionParam.getCreateTime() == null) {
            regionParam.setCreateTime(LocalDateTime.now());
        }

        regionStorage.put(regionParam.getRegionId(), regionParam);
        logger.info("保存区域参数: {}", regionParam.getRegionName());

        return regionParam;
    }

    @Override
    public void deleteById(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new ValidationException("regionId", "区域编号不能为空");
        }

        RegionParam removed = regionStorage.remove(regionId);
        if (removed == null) {
            throw new DataNotFoundException("RegionParam", regionId);
        }

        logger.info("删除区域参数: {}", regionId);
    }

    @Override
    public List<RegionParam> findByName(String regionName) throws BusinessException {
        if (regionName == null || regionName.trim().isEmpty()) {
            throw new ValidationException("regionName", "区域名称不能为空");
        }

        return findAll().stream()
                .filter(region -> region.getRegionName() != null &&
                        region.getRegionName().contains(regionName))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubRegionParam> getSubRegions(String regionId) throws BusinessException {
        RegionParam region = findById(regionId);

        if (region.getSubRegionIdList() == null || region.getSubRegionIdList().isEmpty()) {
            return new ArrayList<>();
        }

        // 这里需要调用SubRegionService来获取子区详细信息
        // 为了避免循环依赖，这里简化处理
        List<SubRegionParam> subRegions = new ArrayList<>();
        for (String subRegionId : region.getSubRegionIdList()) {
            SubRegionParam subRegion = new SubRegionParam();
            subRegion.setSubRegionId(subRegionId);
            subRegion.setSubRegionName("子区-" + subRegionId);
            subRegion.setCreateTime(LocalDateTime.now());
            subRegions.add(subRegion);
        }

        return subRegions;
    }

    @Override
    public List<String> getCrossIds(String regionId) throws BusinessException {
        RegionParam region = findById(regionId);
        return region.getCrossIdList() != null ? region.getCrossIdList() : new ArrayList<>();
    }

    @Override
    public void addCross(String regionId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        RegionParam region = findById(regionId);

        if (region.getCrossIdList() == null) {
            region.setCrossIdList(new ArrayList<>());
        }

        if (!region.getCrossIdList().contains(crossId)) {
            region.getCrossIdList().add(crossId);
            save(region);
            logger.info("向区域 {} 添加路口 {}", regionId, crossId);
        }
    }

    @Override
    public void removeCross(String regionId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        RegionParam region = findById(regionId);

        if (region.getCrossIdList() != null && region.getCrossIdList().remove(crossId)) {
            save(region);
            logger.info("从区域 {} 移除路口 {}", regionId, crossId);
        }
    }

    private void validateRegionParam(RegionParam regionParam) throws BusinessException {
        if (regionParam.getRegionId() == null || regionParam.getRegionId().trim().isEmpty()) {
            throw new ValidationException("regionId", "区域编号不能为空");
        }

        if (regionParam.getRegionName() == null || regionParam.getRegionName().trim().isEmpty()) {
            throw new ValidationException("regionName", "区域名称不能为空");
        }

        // 验证区域编号格式（9位数字）
        if (!regionParam.getRegionId().matches("\\d{9}")) {
            throw new ValidationException("regionId", "区域编号格式错误，应为9位数字");
        }
    }

    private void initializeSampleData() {
        try {
            // 初始化示例区域数据
            RegionParam region1 = new RegionParam("110100001", "北京市朝阳区");
            region1.setCrossIdList(Arrays.asList("11010000100001", "11010000100002"));
            region1.setSubRegionIdList(Arrays.asList("11010000100", "11010000101"));
            region1.setCreateTime(LocalDateTime.now());
            regionStorage.put(region1.getRegionId(), region1);

            RegionParam region2 = new RegionParam("110100002", "北京市海淀区");
            region2.setCrossIdList(Arrays.asList("11010000100003", "11010000100004"));
            region2.setSubRegionIdList(Arrays.asList("11010000102", "11010000103"));
            region2.setCreateTime(LocalDateTime.now());
            regionStorage.put(region2.getRegionId(), region2);

            logger.info("初始化区域示例数据完成，共 {} 个区域", regionStorage.size());
        } catch (Exception e) {
            logger.error("初始化区域示例数据失败", e);
        }
    }
}