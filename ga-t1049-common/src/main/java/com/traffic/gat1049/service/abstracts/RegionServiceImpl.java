package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.system.RegionParam;
import com.traffic.gat1049.protocol.model.system.SubRegionParam;
import com.traffic.gat1049.service.interfaces.RegionService;
import com.traffic.gat1049.service.interfaces.SubRegionService;
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

    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 区域参数存储
    private final Map<String, RegionParam> regionStorage = new ConcurrentHashMap<>();

    // 子区服务引用（用于获取子区信息）
    private SubRegionService subRegionService;

    public RegionServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        initializeSampleData();
    }

    public RegionServiceImpl(SubRegionService subRegionService) throws BusinessException {
        this.subRegionService = subRegionService;
        initializeSampleData();
    }

    // Setter方法用于注入SubRegionService
    public void setSubRegionService(SubRegionService subRegionService) {
        this.subRegionService = subRegionService;
    }

    @Override
    public RegionParam findById(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            throw new ValidationException("regionId", "区域编号不能为空");
        }

        RegionParam regionParam = dataPrider.getRegionById(regionId);//regionStorage.get(regionId);
        if (regionParam == null) {
            throw new DataNotFoundException("RegionParam", regionId);
        }

        return regionParam;
    }

    @Override
    public List<RegionParam> findAll() throws BusinessException {
        return dataPrider.getAllRegions();
        //return new ArrayList<>(regionStorage.values());
    }

    @Override
    public List<RegionParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<RegionParam> allRegions = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allRegions.size());

        if (start >= allRegions.size()) {
            return new ArrayList<>();
        }

        return allRegions.subList(start, end);
    }

    @Override
    public RegionParam save(RegionParam regionParam) throws BusinessException {
        if (regionParam == null) {
            throw new ValidationException("regionParam", "区域参数不能为空");
        }

        validateRegionParam(regionParam);

//        regionParam.setCreateTime(LocalDateTime.now());
//        regionParam.setUpdateTime(LocalDateTime.now());

        regionStorage.put(regionParam.getRegionId(), regionParam);

        logger.info("保存区域参数: regionId={}, regionName={}",
                regionParam.getRegionId(), regionParam.getRegionName());

        return regionParam;
    }

    @Override
    public RegionParam update(RegionParam regionParam) throws BusinessException {
        if (regionParam == null) {
            throw new ValidationException("regionParam", "区域参数不能为空");
        }

        String regionId = regionParam.getRegionId();
        if (!regionStorage.containsKey(regionId)) {
            throw new DataNotFoundException("RegionParam", regionId);
        }

        validateRegionParam(regionParam);

//        regionParam.setUpdateTime(LocalDateTime.now());
        regionStorage.put(regionId, regionParam);

        logger.info("更新区域参数: regionId={}, regionName={}",
                regionParam.getRegionId(), regionParam.getRegionName());

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

        logger.info("删除区域参数: regionId={}", regionId);
    }

    @Override
    public boolean existsById(String regionId) throws BusinessException {
        if (regionId == null || regionId.trim().isEmpty()) {
            return false;
        }
        return regionStorage.containsKey(regionId);
    }

    @Override
    public long count() throws BusinessException {
        return regionStorage.size();
    }

    @Override
    public List<RegionParam> findByName(String regionName) throws BusinessException {
        if (regionName == null || regionName.trim().isEmpty()) {
            throw new ValidationException("regionName", "区域名称不能为空");
        }

        return regionStorage.values().stream()
                .filter(region -> region.getRegionName() != null &&
                        region.getRegionName().contains(regionName))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubRegionParam> getSubRegions(String regionId) throws BusinessException {
        RegionParam region = findById(regionId);

        List<SubRegionParam> subRegions = new ArrayList<>();

        if (region.getSubRegionIdList() != null && !region.getSubRegionIdList().isEmpty()) {
            if (subRegionService != null) {
                // 如果有子区服务，使用子区服务获取详细信息
                for (String subRegionId : region.getSubRegionIdList()) {
                    try {
                        SubRegionParam subRegion = subRegionService.findById(subRegionId);
                        subRegions.add(subRegion);
                    } catch (DataNotFoundException e) {
                        logger.warn("子区不存在: subRegionId={}", subRegionId);
                    }
                }
            } else {
                // 如果没有子区服务，创建简单的子区对象
                for (String subRegionId : region.getSubRegionIdList()) {
                    SubRegionParam subRegion = new SubRegionParam(subRegionId, "子区_" + subRegionId);
                    subRegions.add(subRegion);
                }
            }
        }

        return subRegions;
    }

    @Override
    public List<String> getCrossIds(String regionId) throws BusinessException {
        RegionParam region = findById(regionId);

        return region.getCrossIdList() != null ?
                new ArrayList<>(region.getCrossIdList()) : new ArrayList<>();
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
            //region.setUpdateTime(LocalDateTime.now());

            regionStorage.put(regionId, region);

            logger.info("向区域添加路口: regionId={}, crossId={}", regionId, crossId);
        } else {
            logger.warn("路口已存在于区域中: regionId={}, crossId={}", regionId, crossId);
        }
    }

    @Override
    public void removeCross(String regionId, String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        RegionParam region = findById(regionId);

        if (region.getCrossIdList() != null && region.getCrossIdList().remove(crossId)) {
            //region.setUpdateTime(LocalDateTime.now());
            regionStorage.put(regionId, region);

            logger.info("从区域移除路口: regionId={}, crossId={}", regionId, crossId);
        } else {
            logger.warn("路口不存在于区域中: regionId={}, crossId={}", regionId, crossId);
        }
    }

    /**
     * 添加子区到区域
     */
    public void addSubRegion(String regionId, String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }

        RegionParam region = findById(regionId);

        if (region.getSubRegionIdList() == null) {
            region.setSubRegionIdList(new ArrayList<>());
        }

        if (!region.getSubRegionIdList().contains(subRegionId)) {
            region.getSubRegionIdList().add(subRegionId);
            //region.setUpdateTime(LocalDateTime.now());

            regionStorage.put(regionId, region);

            logger.info("向区域添加子区: regionId={}, subRegionId={}", regionId, subRegionId);
        } else {
            logger.warn("子区已存在于区域中: regionId={}, subRegionId={}", regionId, subRegionId);
        }
    }

    /**
     * 从区域移除子区
     */
    public void removeSubRegion(String regionId, String subRegionId) throws BusinessException {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            throw new ValidationException("subRegionId", "子区编号不能为空");
        }

        RegionParam region = findById(regionId);

        if (region.getSubRegionIdList() != null && region.getSubRegionIdList().remove(subRegionId)) {
            //region.setUpdateTime(LocalDateTime.now());
            regionStorage.put(regionId, region);

            logger.info("从区域移除子区: regionId={}, subRegionId={}", regionId, subRegionId);
        } else {
            logger.warn("子区不存在于区域中: regionId={}, subRegionId={}", regionId, subRegionId);
        }
    }

    private void validateRegionParam(RegionParam regionParam) throws BusinessException {
        if (regionParam.getRegionId() == null || regionParam.getRegionId().trim().isEmpty()) {
            throw new ValidationException("regionId", "区域编号不能为空");
        }

        if (regionParam.getRegionName() == null || regionParam.getRegionName().trim().isEmpty()) {
            throw new ValidationException("regionName", "区域名称不能为空");
        }

        // 验证区域编号格式：9位数字（6位行政区划代码+3位数字）
        if (!regionParam.getRegionId().matches("\\d{9}")) {
            throw new ValidationException("regionId", "区域编号格式错误，应为9位数字");
        }

        if (regionParam.getCrossIdList() == null || regionParam.getCrossIdList().isEmpty()) {
            throw new ValidationException("crossIdList", "路口编号列表不能为空");
        }
    }

    private void initializeSampleData() {
        // 创建示例区域数据
        RegionParam region1 = new RegionParam("110100001", "北京市朝阳区1号区域");
        List<String> subRegionIds1 = Arrays.asList("11010000101", "11010000102");
        List<String> crossIds1 = Arrays.asList("11010000100001", "11010000100002");

        region1.setSubRegionIdList(subRegionIds1);
        region1.setCrossIdList(crossIds1);

        RegionParam region2 = new RegionParam("110100002", "北京市朝阳区2号区域");
        List<String> subRegionIds2 = Arrays.asList("11010000103", "11010000104");
        List<String> crossIds2 = Arrays.asList("11010000100003", "11010000100004");

        region2.setSubRegionIdList(subRegionIds2);
        region2.setCrossIdList(crossIds2);

        try {
            save(region1);
            save(region2);
            logger.info("示例区域数据初始化完成");
        } catch (BusinessException e) {
            logger.error("示例区域数据初始化失败", e);
        }
    }
}