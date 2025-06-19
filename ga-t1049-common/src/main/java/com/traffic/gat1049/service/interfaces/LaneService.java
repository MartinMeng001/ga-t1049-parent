package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.intersection.LaneParam;
import com.traffic.gat1049.protocol.model.runtime.VarLaneStatus;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LaneAttribute;
import com.traffic.gat1049.model.enums.LaneMovement;

import java.util.List;

/**
 * 车道服务接口
 */
public interface LaneService {

    /**
     * 根据路口编号获取车道列表
     *
     * @param crossId 路口编号
     * @return 车道参数列表
     * @throws BusinessException 业务异常
     */
    List<LaneParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和车道序号获取车道
     *
     * @param crossId 路口编号
     * @param laneNo 车道序号
     * @return 车道参数
     * @throws BusinessException 业务异常
     */
    LaneParam findByCrossIdAndLaneNo(String crossId, Integer laneNo) throws BusinessException;

    /**
     * 根据方向查询车道
     *
     * @param crossId 路口编号
     * @param direction 方向
     * @return 车道参数列表
     * @throws BusinessException 业务异常
     */
    List<LaneParam> findByDirection(String crossId, Direction direction) throws BusinessException;

    /**
     * 根据车道属性查询车道
     *
     * @param crossId 路口编号
     * @param attribute 车道属性
     * @return 车道参数列表
     * @throws BusinessException 业务异常
     */
    List<LaneParam> findByAttribute(String crossId, LaneAttribute attribute) throws BusinessException;

    /**
     * 根据转向查询车道
     *
     * @param crossId 路口编号
     * @param movement 转向
     * @return 车道参数列表
     * @throws BusinessException 业务异常
     */
    List<LaneParam> findByMovement(String crossId, LaneMovement movement) throws BusinessException;

    /**
     * 保存车道参数
     *
     * @param laneParam 车道参数
     * @return 保存后的车道参数
     * @throws BusinessException 业务异常
     */
    LaneParam save(LaneParam laneParam) throws BusinessException;

    /**
     * 更新车道参数
     *
     * @param laneParam 车道参数
     * @return 更新后的车道参数
     * @throws BusinessException 业务异常
     */
    LaneParam update(LaneParam laneParam) throws BusinessException;

    /**
     * 删除车道
     *
     * @param crossId 路口编号
     * @param laneNo 车道序号
     * @throws BusinessException 业务异常
     */
    void delete(String crossId, Integer laneNo) throws BusinessException;

    /**
     * 获取可变车道状态
     *
     * @param crossId 路口编号
     * @param laneNo 车道序号
     * @return 可变车道状态
     * @throws BusinessException 业务异常
     */
    VarLaneStatus getVarLaneStatus(String crossId, Integer laneNo) throws BusinessException;

    /**
     * 更新可变车道状态
     *
     * @param varLaneStatus 可变车道状态
     * @throws BusinessException 业务异常
     */
    void updateVarLaneStatus(VarLaneStatus varLaneStatus) throws BusinessException;

    /**
     * 获取路口的可变车道列表
     *
     * @return 可变车道状态列表
     * @throws BusinessException 业务异常
     */
    List<VarLaneStatus> getVarLanes() throws BusinessException;
}
