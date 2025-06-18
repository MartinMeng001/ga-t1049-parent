package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.model.dto.StageQueryDto;
import com.traffic.gat1049.model.vo.StageInfoVo;

import java.util.List;

/**
 * 阶段服务接口
 */
public interface StageService extends BaseService<StageParam, String> {

    /**
     * 根据路口编号查询阶段
     *
     * @param crossId 路口编号
     * @return 阶段参数列表
     * @throws BusinessException 业务异常
     */
    List<StageParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和阶段号查询阶段
     *
     * @param crossId 路口编号
     * @param stageNo 阶段号
     * @return 阶段参数
     * @throws BusinessException 业务异常
     */
    StageParam findByCrossIdAndStageNo(String crossId, Integer stageNo) throws BusinessException;

    /**
     * 根据阶段名称查询
     *
     * @param stageName 阶段名称
     * @return 阶段参数列表
     * @throws BusinessException 业务异常
     */
    List<StageParam> findByName(String stageName) throws BusinessException;

    /**
     * 根据条件查询阶段
     *
     * @param queryDto 查询条件
     * @return 阶段信息列表
     * @throws BusinessException 业务异常
     */
    List<StageInfoVo> findByCriteria(StageQueryDto queryDto) throws BusinessException;

    /**
     * 根据特征查询阶段
     *
     * @param attribute 特征（0：一般，1：感应）
     * @return 阶段参数列表
     * @throws BusinessException 业务异常
     */
    List<StageParam> findByAttribute(Integer attribute) throws BusinessException;

    /**
     * 获取阶段的信号组状态列表
     *
     * @param crossId 路口编号
     * @param stageNo 阶段号
     * @return 信号组状态列表
     * @throws BusinessException 业务异常
     */
    List<SignalGroupStatus> getSignalGroupStatusList(String crossId, Integer stageNo) throws BusinessException;

    /**
     * 更新阶段的信号组状态
     *
     * @param crossId 路口编号
     * @param stageNo 阶段号
     * @param signalGroupStatusList 信号组状态列表
     * @throws BusinessException 业务异常
     */
    void updateSignalGroupStatus(String crossId, Integer stageNo, List<SignalGroupStatus> signalGroupStatusList) throws BusinessException;

    /**
     * 获取路口的阶段号列表
     *
     * @param crossId 路口编号
     * @return 阶段号列表
     * @throws BusinessException 业务异常
     */
    List<Integer> getStageNos(String crossId) throws BusinessException;

    /**
     * 复制阶段参数
     *
     * @param sourceCrossId 源路口编号
     * @param sourceStageNo 源阶段号
     * @param targetCrossId 目标路口编号
     * @param targetStageNo 目标阶段号
     * @return 复制后的阶段参数
     * @throws BusinessException 业务异常
     */
    StageParam copyStage(String sourceCrossId, Integer sourceStageNo, String targetCrossId, Integer targetStageNo) throws BusinessException;

    /**
     * 验证阶段参数配置
     *
     * @param stageParam 阶段参数
     * @return 验证结果
     * @throws BusinessException 业务异常
     */
    boolean validateStageConfig(StageParam stageParam) throws BusinessException;
}