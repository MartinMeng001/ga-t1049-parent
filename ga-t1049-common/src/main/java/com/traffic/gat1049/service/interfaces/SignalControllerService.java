package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.protocol.model.runtime.SignalControllerError;
import com.traffic.gat1049.model.enums.CommMode;
import com.traffic.gat1049.model.enums.ControllerErrorType;

import java.util.List;

/**
 * 信号机服务接口
 */
public interface SignalControllerService extends BaseService<SignalController, String> {

    /**
     * 根据供应商查询信号机
     *
     * @param supplier 供应商
     * @return 信号机列表
     * @throws BusinessException 业务异常
     */
    List<SignalController> findBySupplier(String supplier) throws BusinessException;

    /**
     * 根据规格型号查询信号机
     *
     * @param type 规格型号
     * @return 信号机列表
     * @throws BusinessException 业务异常
     */
    List<SignalController> findByType(String type) throws BusinessException;

    /**
     * 根据通信接口查询信号机
     *
     * @param commMode 通信接口
     * @return 信号机列表
     * @throws BusinessException 业务异常
     */
    List<SignalController> findByCommMode(CommMode commMode) throws BusinessException;

    /**
     * 获取信号机控制的路口列表
     *
     * @param signalControllerId 信号机编号
     * @return 路口编号列表
     * @throws BusinessException 业务异常
     */
    List<String> getControlledCrosses(String signalControllerId) throws BusinessException;

    /**
     * 添加路口到信号机控制列表
     *
     * @param signalControllerId 信号机编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void addControlledCross(String signalControllerId, String crossId) throws BusinessException;

    /**
     * 从信号机控制列表移除路口
     *
     * @param signalControllerId 信号机编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void removeControlledCross(String signalControllerId, String crossId) throws BusinessException;

    /**
     * 报告信号机故障
     *
     * @param signalControllerId 信号机编号
     * @param errorType 故障类型
     * @param errorDesc 故障描述
     * @throws BusinessException 业务异常
     */
    void reportError(String signalControllerId, ControllerErrorType errorType, String errorDesc) throws BusinessException;

    /**
     * 获取信号机故障列表
     *
     * @param signalControllerId 信号机编号
     * @return 故障列表
     * @throws BusinessException 业务异常
     */
    List<SignalControllerError> getErrors(String signalControllerId) throws BusinessException;

    /**
     * 清除信号机故障
     *
     * @param signalControllerId 信号机编号
     * @throws BusinessException 业务异常
     */
    void clearErrors(String signalControllerId) throws BusinessException;
}
