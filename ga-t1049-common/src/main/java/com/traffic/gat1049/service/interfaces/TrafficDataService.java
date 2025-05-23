package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.dto.TrafficDataQueryDto;
import com.traffic.gat1049.model.entity.traffic.CrossTrafficData;
import com.traffic.gat1049.model.entity.traffic.StageTrafficData;
import com.traffic.gat1049.model.entity.runtime.CrossCycle;
import com.traffic.gat1049.model.entity.runtime.CrossStage;
import com.traffic.gat1049.model.vo.TrafficStatisticsVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交通数据服务接口
 */
public interface TrafficDataService {

    /**
     * 获取路口交通流数据
     *
     * @param crossId 路口编号
     * @param endTime 统计截止时间
     * @param interval 时间间隔
     * @return 路口交通流数据
     * @throws BusinessException 业务异常
     */
    CrossTrafficData getCrossTrafficData(String crossId, LocalDateTime endTime, Integer interval) throws BusinessException;

    /**
     * 根据查询条件获取交通流数据
     *
     * @param queryDto 查询条件
     * @return 交通统计数据列表
     * @throws BusinessException 业务异常
     */
    List<TrafficStatisticsVo> getTrafficStatistics(TrafficDataQueryDto queryDto) throws BusinessException;

    /**
     * 获取阶段交通流数据
     *
     * @param crossId 路口编号
     * @param stageNo 阶段号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 阶段交通流数据
     * @throws BusinessException 业务异常
     */
    StageTrafficData getStageTrafficData(String crossId, Integer stageNo, LocalDateTime startTime, LocalDateTime endTime) throws BusinessException;

    /**
     * 保存路口交通流数据
     *
     * @param crossTrafficData 路口交通流数据
     * @throws BusinessException 业务异常
     */
    void saveCrossTrafficData(CrossTrafficData crossTrafficData) throws BusinessException;

    /**
     * 保存阶段交通流数据
     *
     * @param stageTrafficData 阶段交通流数据
     * @throws BusinessException 业务异常
     */
    void saveStageTrafficData(StageTrafficData stageTrafficData) throws BusinessException;

    /**
     * 获取路口周期信息
     *
     * @param crossId 路口编号
     * @return 路口周期
     * @throws BusinessException 业务异常
     */
    CrossCycle getCrossCycle(String crossId) throws BusinessException;

    /**
     * 更新路口周期信息
     *
     * @param crossCycle 路口周期
     * @throws BusinessException 业务异常
     */
    void updateCrossCycle(CrossCycle crossCycle) throws BusinessException;

    /**
     * 获取路口阶段信息
     *
     * @param crossId 路口编号
     * @return 路口阶段
     * @throws BusinessException 业务异常
     */
    CrossStage getCrossStage(String crossId) throws BusinessException;

    /**
     * 更新路口阶段信息
     *
     * @param crossStage 路口阶段
     * @throws BusinessException 业务异常
     */
    void updateCrossStage(CrossStage crossStage) throws BusinessException;

    /**
     * 启动或停止数据上传
     *
     * @param command Start或Stop
     * @param dataType 数据类型
     * @param crossIds 路口编号列表
     * @throws BusinessException 业务异常
     */
    void controlDataReport(String command, String dataType, List<String> crossIds) throws BusinessException;

    /**
     * 清理历史数据
     *
     * @param beforeDate 清理此日期之前的数据
     * @throws BusinessException 业务异常
     */
    void cleanHistoryData(LocalDateTime beforeDate) throws BusinessException;
}
