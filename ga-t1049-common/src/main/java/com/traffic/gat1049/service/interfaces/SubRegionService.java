package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.entity.system.SubRegionParam;

import java.util.List;

/**
 * 子区服务接口
 */
public interface SubRegionService extends BaseService<SubRegionParam, String> {

    /**
     * 根据子区名称查询
     *
     * @param subRegionName 子区名称
     * @return 子区参数列表
     * @throws BusinessException 业务异常
     */
    List<SubRegionParam> findByName(String subRegionName) throws BusinessException;

    /**
     * 根据区域编号查询子区
     *
     * @param regionId 区域编号
     * @return 子区列表
     * @throws BusinessException 业务异常
     */
    List<SubRegionParam> findByRegionId(String regionId) throws BusinessException;

    /**
     * 获取子区下的路口编号列表
     *
     * @param subRegionId 子区编号
     * @return 路口编号列表
     * @throws BusinessException 业务异常
     */
    List<String> getCrossIds(String subRegionId) throws BusinessException;

    /**
     * 获取关键路口编号列表
     *
     * @param subRegionId 子区编号
     * @return 关键路口编号列表
     * @throws BusinessException 业务异常
     */
    List<String> getKeyCrossIds(String subRegionId) throws BusinessException;

    /**
     * 添加路口到子区
     *
     * @param subRegionId 子区编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void addCross(String subRegionId, String crossId) throws BusinessException;

    /**
     * 设置关键路口
     *
     * @param subRegionId 子区编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void setKeyCross(String subRegionId, String crossId) throws BusinessException;
}
