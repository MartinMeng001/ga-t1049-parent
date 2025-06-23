package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.runtime.CrossModePlan;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.model.enums.ControlMode;

import java.util.List;

/**
 * 控制模式服务接口
 * 专门处理路口控制模式相关的业务逻辑
 */
public interface ControlModeService {

    /**
     * 获取路口当前控制模式方案
     *
     * @param crossId 路口编号
     * @return 路口控制模式方案
     * @throws BusinessException 业务异常
     */
    CrossModePlan getCurrentModePlan(String crossId) throws BusinessException;

    /**
     * 设置路口控制模式
     *
     * @param crossId 路口编号
     * @param controlMode 控制模式
     * @param planNo 方案号（特殊控制模式可为null）
     * @throws BusinessException 业务异常
     */
    void setControlMode(String crossId, ControlMode controlMode, Integer planNo) throws BusinessException;

    /**
     * 设置路口控制模式（使用CrossModePlan对象）
     *
     * @param modePlan 控制模式方案
     * @throws BusinessException 业务异常
     */
    void setControlMode(CrossModePlan modePlan) throws BusinessException;

    /**
     * 获取所有路口的控制模式
     *
     * @return 所有路口控制模式列表
     * @throws BusinessException 业务异常
     */
    List<CrossModePlan> getAllControlModes() throws BusinessException;

    /**
     * 根据控制模式类型查询路口列表
     *
     * @param controlMode 控制模式
     * @return 使用该控制模式的路口列表
     * @throws BusinessException 业务异常
     */
    List<CrossModePlan> findByControlMode(ControlMode controlMode) throws BusinessException;

    /**
     * 批量设置多个路口的控制模式
     *
     * @param crossIds 路口编号列表
     * @param controlMode 控制模式
     * @param planNo 方案号
     * @throws BusinessException 业务异常
     */
    void batchSetControlMode(List<String> crossIds, ControlMode controlMode, Integer planNo) throws BusinessException;

    /**
     * 检查控制模式是否为特殊控制模式（不需要方案号）
     *
     * @param controlMode 控制模式
     * @return 是否为特殊控制模式
     */
    boolean isSpecialControlMode(ControlMode controlMode);

    /**
     * 验证控制模式设置的有效性
     *
     * @param modePlan 控制模式方案
     * @throws BusinessException 验证异常
     */
    void validateControlMode(CrossModePlan modePlan) throws BusinessException;

    /**
     * 重置路口为默认控制模式
     *
     * @param crossId 路口编号
     * @throws BusinessException 业务异常
     */
    void resetToDefaultMode(String crossId) throws BusinessException;

    /**
     * 获取控制模式历史记录
     *
     * @param crossId 路口编号
     * @param limit 记录数量限制
     * @return 控制模式历史记录
     * @throws BusinessException 业务异常
     */
    List<CrossModePlan> getControlModeHistory(String crossId, int limit) throws BusinessException;

    /**
     * 检查控制模式切换的前置条件
     *
     * @param crossId 路口编号
     * @param targetMode 目标控制模式
     * @return 是否可以切换
     * @throws BusinessException 业务异常
     */
    boolean canSwitchToMode(String crossId, ControlMode targetMode) throws BusinessException;
}