package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.entity.system.RegionParam;
import com.traffic.gat1049.model.entity.system.SubRegionParam;

import java.util.List;

/**
 * 区域服务接口
 */
public interface RegionService extends BaseService<RegionParam, String> {

    /**
     * 根据区域名称查询
     *
     * @param regionName 区域名称
     * @return 区域参数列表
     * @throws BusinessException 业务异常
     */
    List<RegionParam> findByName(String regionName) throws BusinessException;

    /**
     * 获取区域下的子区列表
     *
     * @param regionId 区域编号
     * @return 子区列表
     * @throws BusinessException 业务异常
     */
    List<SubRegionParam> getSubRegions(String regionId) throws BusinessException;

    /**
     * 获取区域下的路口编号列表
     *
     * @param regionId 区域编号
     * @return 路口编号列表
     * @throws BusinessException 业务异常
     */
    List<String> getCrossIds(String regionId) throws BusinessException;

    /**
     * 添加路口到区域
     *
     * @param regionId 区域编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void addCross(String regionId, String crossId) throws BusinessException;

    /**
     * 从区域移除路口
     *
     * @param regionId 区域编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void removeCross(String regionId, String crossId) throws BusinessException;
}
