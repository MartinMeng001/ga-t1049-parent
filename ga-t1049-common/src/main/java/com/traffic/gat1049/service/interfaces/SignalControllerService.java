package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.protocol.model.runtime.SignalControllerError;
import com.traffic.gat1049.model.enums.CommMode;
import com.traffic.gat1049.model.enums.ControllerErrorType;

import java.util.List;

/**
 * 信号机服务接口
 * 更新以支持新的SignalController标准定义
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
     * 根据识别码查询信号机
     *
     * @param id 识别码
     * @return 信号机列表
     * @throws BusinessException 业务异常
     */
    SignalController findById(String id) throws BusinessException;

    /**
     * 根据通信接口查询信号机
     *
     * @param commMode 通信接口
     * @return 信号机列表
     * @throws BusinessException 业务异常
     */
    List<SignalController> findByCommMode(CommMode commMode) throws BusinessException;

    /**
     * 根据IP地址查询信号机
     *
     * @param ip IP地址
     * @return 信号机列表
     * @throws BusinessException 业务异常
     */
    List<SignalController> findByIp(String ip) throws BusinessException;

    /**
     * 根据位置范围查询信号机
     *
     * @param minLongitude 最小经度
     * @param maxLongitude 最大经度
     * @param minLatitude 最小纬度
     * @param maxLatitude 最大纬度
     * @return 信号机列表
     * @throws BusinessException 业务异常
     */
    List<SignalController> findByLocationRange(Double minLongitude, Double maxLongitude,
                                               Double minLatitude, Double maxLatitude) throws BusinessException;

    /**
     * 获取信号机控制的路口列表
     *
     * @param signalControllerID 信号机编号
     * @return 路口编号列表
     * @throws BusinessException 业务异常
     */
    List<String> getControlledCrosses(String signalControllerID) throws BusinessException;

    /**
     * 添加路口到信号机控制列表
     *
     * @param signalControllerID 信号机编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void addControlledCross(String signalControllerID, String crossId) throws BusinessException;

    /**
     * 从信号机控制列表移除路口
     *
     * @param signalControllerID 信号机编号
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void removeControlledCross(String signalControllerID, String crossId) throws BusinessException;

    /**
     * 设置信号机主路口
     *
     * @param signalControllerID 信号机编号
     * @param primaryCrossId 主路口编号
     * @throws BusinessException 业务异常
     */
    void setPrimaryCross(String signalControllerID, String primaryCrossId) throws BusinessException;

    /**
     * 获取信号机主路口编号
     *
     * @param signalControllerID 信号机编号
     * @return 主路口编号
     * @throws BusinessException 业务异常
     */
    String getPrimaryCross(String signalControllerID) throws BusinessException;

    /**
     * 更新信号机网络配置
     *
     * @param signalControllerID 信号机编号
     * @param ip IP地址
     * @param subMask 子网掩码
     * @param gateway 网关
     * @param port 端口号
     * @throws BusinessException 业务异常
     */
    void updateNetworkConfig(String signalControllerID, String ip, String subMask,
                             String gateway, Integer port) throws BusinessException;

    /**
     * 更新信号机位置信息
     *
     * @param signalControllerID 信号机编号
     * @param longitude 经度
     * @param latitude 纬度
     * @throws BusinessException 业务异常
     */
    void updateLocation(String signalControllerID, Double longitude, Double latitude) throws BusinessException;

    /**
     * 检查信号机柜门状态
     *
     * @param signalControllerID 信号机编号
     * @return 是否有柜门状态检测功能
     * @throws BusinessException 业务异常
     */
    boolean hasDoorStatusDetection(String signalControllerID) throws BusinessException;

    /**
     * 报告信号机故障
     *
     * @param signalControllerID 信号机编号
     * @param errorType 故障类型
     * @param errorDesc 故障描述
     * @throws BusinessException 业务异常
     */
    void reportError(String signalControllerID, ControllerErrorType errorType, String errorDesc) throws BusinessException;

    /**
     * 获取信号机故障列表
     *
     * @param signalControllerID 信号机编号
     * @return 故障列表
     * @throws BusinessException 业务异常
     */
    List<SignalControllerError> getErrors(String signalControllerID) throws BusinessException;

    /**
     * 获取所有信号机故障列表
     *
     * @return 所有信号机的故障列表
     * @throws BusinessException 业务异常
     */
    List<SignalControllerError> getAllErrors() throws BusinessException;

    /**
     * 清除信号机故障
     *
     * @param signalControllerID 信号机编号
     * @throws BusinessException 业务异常
     */
    void clearErrors(String signalControllerID) throws BusinessException;

    /**
     * 验证信号机配置的完整性
     *
     * @param signalController 信号机对象
     * @return 验证结果
     * @throws BusinessException 业务异常
     */
    boolean validateConfiguration(SignalController signalController) throws BusinessException;

    /**
     * 测试信号机通信连接
     *
     * @param signalControllerID 信号机编号
     * @return 连接测试结果
     * @throws BusinessException 业务异常
     */
    boolean testConnection(String signalControllerID) throws BusinessException;
}