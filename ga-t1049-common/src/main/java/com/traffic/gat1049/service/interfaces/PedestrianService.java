package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.intersection.PedestrianParam;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.PedestrianAttribute;

import java.util.List;

/**
 * 人行横道服务接口
 */
public interface PedestrianService extends BaseService<PedestrianParam, String> {

    /**
     * 根据路口编号查询人行横道
     *
     * @param crossId 路口编号
     * @return 人行横道参数列表
     * @throws BusinessException 业务异常
     */
    List<PedestrianParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和人行横道序号查询
     *
     * @param crossId 路口编号
     * @param pedestrianNo 人行横道序号
     * @return 人行横道参数
     * @throws BusinessException 业务异常
     */
    PedestrianParam findByCrossIdAndPedestrianNo(String crossId, Integer pedestrianNo) throws BusinessException;

    /**
     * 根据方向查询人行横道
     *
     * @param direction 方向
     * @return 人行横道参数列表
     * @throws BusinessException 业务异常
     */
    List<PedestrianParam> findByDirection(Direction direction) throws BusinessException;

    /**
     * 根据属性查询人行横道
     *
     * @param attribute 人行横道属性
     * @return 人行横道参数列表
     * @throws BusinessException 业务异常
     */
    List<PedestrianParam> findByAttribute(PedestrianAttribute attribute) throws BusinessException;

    /**
     * 根据路口编号和方向查询人行横道
     *
     * @param crossId 路口编号
     * @param direction 方向
     * @return 人行横道参数列表
     * @throws BusinessException 业务异常
     */
    List<PedestrianParam> findByCrossIdAndDirection(String crossId, Direction direction) throws BusinessException;

    /**
     * 获取路口的人行横道序号列表
     *
     * @param crossId 路口编号
     * @return 人行横道序号列表
     * @throws BusinessException 业务异常
     */
    List<Integer> getPedestrianNos(String crossId) throws BusinessException;

    /**
     * 删除路口的所有人行横道
     *
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void deleteByCrossId(String crossId) throws BusinessException;

    /**
     * 删除指定的人行横道
     *
     * @param crossId 路口编号
     * @param pedestrianNo 人行横道序号
     * @throws BusinessException 业务异常
     */
    void deleteByCrossIdAndPedestrianNo(String crossId, Integer pedestrianNo) throws BusinessException;
}