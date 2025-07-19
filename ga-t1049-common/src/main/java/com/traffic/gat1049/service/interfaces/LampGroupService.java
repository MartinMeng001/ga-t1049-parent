package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;

import java.util.List;

/**
 * 信号灯组服务接口
 */
public interface LampGroupService extends BaseService<LampGroupParam, String> {

    /**
     * 根据路口编号获取信号灯组列表
     *
     * @param crossId 路口编号
     * @return 信号灯组参数列表
     * @throws BusinessException 业务异常
     */
    List<LampGroupParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号获取基础信号灯组列表
     *
     * @param crossId 路口编号
     * @return 信号灯组参数列表
     * @throws BusinessException 业务异常
     */
    List<LampGroupParam> findAllBasicByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和信号灯组序号获取信号灯组
     *
     * @param crossId 路口编号
     * @param lampGroupNo 信号灯组序号
     * @return 信号灯组参数
     * @throws BusinessException 业务异常
     */
    LampGroupParam findByCrossIdAndLampGroupNo(String crossId, Integer lampGroupNo) throws BusinessException;

    /**
     * 根据进口方向查询信号灯组
     *
     * @param direction 进口方向
     * @return 信号灯组参数列表
     * @throws BusinessException 业务异常
     */
    List<LampGroupParam> findByDirection(Direction direction) throws BusinessException;

    /**
     * 根据信号灯组类型查询信号灯组
     *
     * @param type 信号灯组类型
     * @return 信号灯组参数列表
     * @throws BusinessException 业务异常
     */
    List<LampGroupParam> findByType(LampGroupType type) throws BusinessException;

    /**
     * 根据路口编号和进口方向查询信号灯组
     *
     * @param crossId 路口编号
     * @param direction 进口方向
     * @return 信号灯组参数列表
     * @throws BusinessException 业务异常
     */
    List<LampGroupParam> findByCrossIdAndDirection(String crossId, Direction direction) throws BusinessException;

    /**
     * 根据路口编号和信号灯组类型查询信号灯组
     *
     * @param crossId 路口编号
     * @param type 信号灯组类型
     * @return 信号灯组参数列表
     * @throws BusinessException 业务异常
     */
    List<LampGroupParam> findByCrossIdAndType(String crossId, LampGroupType type) throws BusinessException;

    /**
     * 根据路口编号，方向和信号灯组类型查询信号灯组
     *
     * @param crossId 路口编号
     * @param direction 路口方向
     * @param type 信号灯组类型
     * @return 信号灯组参数
     * @throws BusinessException 业务异常
     */
    LampGroupParam findByCrossIdAndDirectionAndType(String crossId, Direction direction, LampGroupType type) throws BusinessException;
    /**
     * 删除指定路口的所有信号灯组
     *
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void deleteByCrossId(String crossId) throws BusinessException;

    /**
     * 删除指定路口的指定信号灯组
     *
     * @param crossId 路口编号
     * @param lampGroupNo 信号灯组序号
     * @throws BusinessException 业务异常
     */
    void deleteByCrossIdAndLampGroupNo(String crossId, Integer lampGroupNo) throws BusinessException;

    /**
     * 获取路口的信号灯组序号列表
     *
     * @param crossId 路口编号
     * @return 信号灯组序号列表
     * @throws BusinessException 业务异常
     */
    List<Integer> getLampGroupNos(String crossId) throws BusinessException;
}