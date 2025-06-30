package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.runtime.SCDoorStatus;
import com.traffic.gat1049.model.enums.DoorStatus;

import java.util.List;

/**
 * 信号机柜门状态服务接口
 */
public interface DoorStatusService {

    /**
     * 获取信号机柜门状态
     *
     * @param signalControllerId 信号机设备编号
     * @return 信号机柜门状态
     * @throws BusinessException 业务异常
     */
    SCDoorStatus getDoorStatus(String signalControllerId) throws BusinessException;

    /**
     * 获取所有信号机柜门状态
     *
     * @return 所有信号机柜门状态列表
     * @throws BusinessException 业务异常
     */
    List<SCDoorStatus> getAllDoorStatus() throws BusinessException;

    /**
     * 更新信号机柜门状态
     *
     * @param signalControllerId 信号机设备编号
     * @param doorNo 机柜门序号
     * @param status 门状态
     * @throws BusinessException 业务异常
     */
    void updateDoorStatus(String signalControllerId, Integer doorNo, DoorStatus status) throws BusinessException;

    /**
     * 批量更新信号机柜门状态
     *
     * @param scDoorStatus 信号机柜门状态对象
     * @throws BusinessException 业务异常
     */
    void updateDoorStatus(SCDoorStatus scDoorStatus) throws BusinessException;

    /**
     * 获取指定机柜门的状态
     *
     * @param signalControllerId 信号机设备编号
     * @param doorNo 机柜门序号
     * @return 门状态
     * @throws BusinessException 业务异常
     */
    DoorStatus getSingleDoorStatus(String signalControllerId, Integer doorNo) throws BusinessException;

    /**
     * 检查信号机是否存在
     *
     * @param signalControllerId 信号机设备编号
     * @return 是否存在
     * @throws BusinessException 业务异常
     */
    boolean isSignalControllerExists(String signalControllerId) throws BusinessException;

    /**
     * 获取信号机的机柜门数量
     *
     * @param signalControllerId 信号机设备编号
     * @return 机柜门数量
     * @throws BusinessException 业务异常
     */
    int getDoorCount(String signalControllerId) throws BusinessException;

    /**
     * 初始化信号机的机柜门状态
     *
     * @param signalControllerId 信号机设备编号
     * @param doorCount 机柜门数量
     * @throws BusinessException 业务异常
     */
    void initializeDoorStatus(String signalControllerId, int doorCount) throws BusinessException;

    /**
     * 删除信号机的机柜门状态
     *
     * @param signalControllerId 信号机设备编号
     * @throws BusinessException 业务异常
     */
    void deleteDoorStatus(String signalControllerId) throws BusinessException;
}