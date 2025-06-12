package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.dto.CrossQueryDto;
import com.traffic.gat1049.protocol.model.intersection.CrossParam;
import com.traffic.gat1049.protocol.model.runtime.CrossState;
import com.traffic.gat1049.model.enums.CrossFeature;
import com.traffic.gat1049.model.enums.CrossGrade;
import com.traffic.gat1049.model.enums.SystemState;
import com.traffic.gat1049.model.vo.CrossInfoVo;

import java.util.List;

/**
 * 路口服务接口
 */
public interface CrossService extends BaseService<CrossParam, String> {

    /**
     * 根据路口名称查询
     *
     * @param crossName 路口名称
     * @return 路口参数列表
     * @throws BusinessException 业务异常
     */
    List<CrossParam> findByName(String crossName) throws BusinessException;

    /**
     * 根据条件查询路口
     *
     * @param queryDto 查询条件
     * @return 路口信息列表
     * @throws BusinessException 业务异常
     */
    List<CrossInfoVo> findByCriteria(CrossQueryDto queryDto) throws BusinessException;

    /**
     * 根据形状查询路口
     *
     * @param feature 路口形状
     * @return 路口参数列表
     * @throws BusinessException 业务异常
     */
    List<CrossParam> findByFeature(CrossFeature feature) throws BusinessException;

    /**
     * 根据等级查询路口
     *
     * @param grade 路口等级
     * @return 路口参数列表
     * @throws BusinessException 业务异常
     */
    List<CrossParam> findByGrade(CrossGrade grade) throws BusinessException;

    /**
     * 根据信号机编号查询路口
     *
     * @param signalControllerId 信号机编号
     * @return 路口参数列表
     * @throws BusinessException 业务异常
     */
    List<CrossParam> findBySignalControllerId(String signalControllerId) throws BusinessException;

    /**
     * 获取路口状态
     *
     * @param crossId 路口编号
     * @return 路口状态
     * @throws BusinessException 业务异常
     */
    CrossState getCrossState(String crossId) throws BusinessException;

    /**
     * 更新路口状态
     *
     * @param crossId 路口编号
     * @param state 系统状态
     * @throws BusinessException 业务异常
     */
    void updateCrossState(String crossId, SystemState state) throws BusinessException;

    /**
     * 获取路口的车道序号列表
     *
     * @param crossId 路口编号
     * @return 车道序号列表
     * @throws BusinessException 业务异常
     */
    List<Integer> getLaneNos(String crossId) throws BusinessException;

    /**
     * 获取路口的信号组序号列表
     *
     * @param crossId 路口编号
     * @return 信号组序号列表
     * @throws BusinessException 业务异常
     */
    List<Integer> getSignalGroupNos(String crossId) throws BusinessException;

    /**
     * 获取路口的配时方案序号列表
     *
     * @param crossId 路口编号
     * @return 配时方案序号列表
     * @throws BusinessException 业务异常
     */
    List<Integer> getPlanNos(String crossId) throws BusinessException;
}
