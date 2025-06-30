package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.enums.*;

/**
 * 控制服务接口
 */
public interface ControlService {

    /**
     * 锁定交通流向
     *
     * @param crossId 路口编号
     * @param flowType 交通流类型
     * @param entrance 进口方向
     * @param exit 出口方向
     * @param lockType 锁定类型
     * @param duration 锁定持续时间（秒）
     * @throws BusinessException 业务异常
     */
    public void lockFlowDirection(String crossId, FlowType flowType, Direction entrance, Direction exit,
                                  LockType lockType, Integer lockStageNo, Integer duration) throws BusinessException;

    /**
     * 解锁交通流向
     *
     * @param crossId 路口编号
     * @param flowType 交通流类型
     * @param entrance 进口方向
     * @param exit 出口方向
     * @throws BusinessException 业务异常
     */
    void unlockFlowDirection(String crossId, FlowType flowType, Direction entrance, Direction exit) throws BusinessException;

    /**
     * 阶段干预
     *
     * @param crossId 路口编号
     * @param stageNo 阶段号
     * @param interventionType 干预类型
     * @param len 干预时长
     * @throws BusinessException 业务异常
     */
    void stageIntervention(String crossId, Integer stageNo, InterventionType interventionType, Integer len) throws BusinessException;

    /**
     * 检查路口是否支持指定的控制操作
     *
     * @param crossId 路口编号
     * @param operationType 操作类型
     * @return 是否支持
     * @throws BusinessException 业务异常
     */
    boolean supportsOperation(String crossId, String operationType) throws BusinessException;

    /**
     * 获取路口当前锁定状态
     *
     * @param crossId 路口编号
     * @return 锁定状态信息
     * @throws BusinessException 业务异常
     */
    String getLockStatus(String crossId) throws BusinessException;

    /**
     * 紧急控制
     *
     * @param crossId 路口编号
     * @param controlMode 控制方式
     * @throws BusinessException 业务异常
     */
    void emergencyControl(String crossId, ControlMode controlMode) throws BusinessException;
}
