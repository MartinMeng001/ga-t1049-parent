package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.protocol.model.runtime.CrossSignalGroupStatus;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;

import java.util.List;

/**
 * 信号组服务接口
 */
public interface SignalGroupService {

    /**
     * 根据路口编号获取信号组
     *
     * @param crossId 路口编号
     * @return 信号组参数列表
     * @throws BusinessException 业务异常
     */
    List<SignalGroupParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和信号组序号获取信号组
     *
     * @param crossId 路口编号
     * @param signalGroupNo 信号组序号
     * @return 信号组参数
     * @throws BusinessException 业务异常
     */
    SignalGroupParam findByCrossIdAndSignalGroupNo(String crossId, Integer signalGroupNo) throws BusinessException;

    /**
     * 保存信号组参数
     *
     * @param signalGroupParam 信号组参数
     * @return 保存后的信号组参数
     * @throws BusinessException 业务异常
     */
    SignalGroupParam save(SignalGroupParam signalGroupParam) throws BusinessException;

    /**
     * 更新信号组参数
     *
     * @param signalGroupParam 信号组参数
     * @return 更新后的信号组参数
     * @throws BusinessException 业务异常
     */
    SignalGroupParam update(SignalGroupParam signalGroupParam) throws BusinessException;

    /**
     * 删除信号组
     *
     * @param crossId 路口编号
     * @param signalGroupNo 信号组序号
     * @throws BusinessException 业务异常
     */
    void delete(String crossId, Integer signalGroupNo) throws BusinessException;

    /**
     * 获取路口信号组灯态
     *
     * @param crossId 路口编号
     * @return 路口信号组灯态
     * @throws BusinessException 业务异常
     */
    CrossSignalGroupStatus getCrossSignalGroupStatus(String crossId) throws BusinessException;

    /**
     * 获取路口信号组灯态列表
     *
     * @return 路口信号组灯态
     * @throws BusinessException 业务异常
     */
    List<CrossSignalGroupStatus> getAllCrossSignalGroupStatus() throws BusinessException;

    /**
     * 更新路口信号组灯态
     *
     * @param crossSignalGroupStatus 路口信号组灯态
     * @throws BusinessException 业务异常
     */
    void updateCrossSignalGroupStatus(CrossSignalGroupStatus crossSignalGroupStatus) throws BusinessException;

    /**
     * 获取单个信号组的当前灯态
     *
     * @param crossId 路口编号
     * @param signalGroupNo 信号组序号
     * @return 信号组灯态
     * @throws BusinessException 业务异常
     */
    SignalGroupStatus getSignalGroupStatus(String crossId, Integer signalGroupNo) throws BusinessException;
}
