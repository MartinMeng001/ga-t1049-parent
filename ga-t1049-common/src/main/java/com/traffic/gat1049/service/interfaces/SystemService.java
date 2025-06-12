package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.system.SysInfo;
import com.traffic.gat1049.protocol.model.system.SysState;
import com.traffic.gat1049.model.enums.SystemState;

/**
 * 系统服务接口
 */
public interface SystemService {

    /**
     * 获取系统信息
     *
     * @return 系统信息
     * @throws BusinessException 业务异常
     */
    SysInfo getSystemInfo() throws BusinessException;

    /**
     * 更新系统信息
     *
     * @param sysInfo 系统信息
     * @return 更新后的系统信息
     * @throws BusinessException 业务异常
     */
    SysInfo updateSystemInfo(SysInfo sysInfo) throws BusinessException;

    /**
     * 获取系统状态
     *
     * @return 系统状态
     * @throws BusinessException 业务异常
     */
    SysState getSystemState() throws BusinessException;

    /**
     * 更新系统状态
     *
     * @param state 系统状态
     * @throws BusinessException 业务异常
     */
    void updateSystemState(SystemState state) throws BusinessException;

    /**
     * 系统初始化
     *
     * @throws BusinessException 业务异常
     */
    void initialize() throws BusinessException;

    /**
     * 系统健康检查
     *
     * @return 是否健康
     * @throws BusinessException 业务异常
     */
    boolean healthCheck() throws BusinessException;
}
