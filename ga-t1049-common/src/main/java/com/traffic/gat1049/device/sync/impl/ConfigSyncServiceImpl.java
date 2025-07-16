package com.traffic.gat1049.device.sync.impl;

import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.gat1049.repository.interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 配置同步服务实现
 * 专门处理配置相关的同步操作
 */
@Service("configSyncService")
@Transactional
public class ConfigSyncServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ConfigSyncServiceImpl.class);

    @Autowired
    private PlanParamRepository planParamRepository;

    @Autowired
    private StageParamRepository stageParamRepository;

    @Autowired
    private SignalGroupParamRepository signalGroupParamRepository;

    @Autowired
    private CrossParamRepository crossParamRepository;

    /**
     * 同步配时方案到数据库
     */
//    @Transactional
//    public SyncResult syncPlanParamsToDatabase(String controllerId, List<PlanParam> planParams) {
//        logger.info("同步配时方案到数据库: controllerId={}, planCount={}", controllerId, planParams.size());
//
//        SyncResult result = SyncResult.inProgress(controllerId, "PLAN_SYNC", "开始同步配时方案");
//        result.markStarted();
//
//        try {
//            // 1. 验证参数
//            if (controllerId == null || planParams == null || planParams.isEmpty()) {
//                result.markFailed("INVALID_PARAMS", "同步参数无效");
//                return result;
//            }
//
//            // 2. 删除旧数据
//            int deletedCount = planParamRepository.deleteByControllerId(controllerId);
//            logger.debug("删除旧配时方案: controllerId={}, count={}", controllerId, deletedCount);
//
//            // 3. 保存新数据
//            int savedCount = 0;
//            for (PlanParam planParam : planParams) {
//                try {
//                    PlanParamEntity entity = convertToPlanEntity(controllerId, planParam);
//                    planParamRepository.save(entity);
//                    savedCount++;
//
//                    result.addSuccessDetail("PLAN", planParam.getPlanNo().toString(),
//                            "配时方案保存成功");
//
//                } catch (Exception e) {
//                    logger.error("保存配时方案失败: controllerId={}, planNo={}",
//                            controllerId, planParam.getPlanNo(), e);
//                    result.addFailureDetail("PLAN", planParam.getPlanNo().toString(),
//                            "SAVE_ERROR", "保存失败: " + e.getMessage());
//                }
//            }
//
//            // 4. 更新结果
//            result.setAffectedRows(savedCount);
//            result.setSuccessCount(savedCount);
//            result.setFailureCount(planParams.size() - savedCount);
//
//            if (savedCount == planParams.size()) {
//                result.markCompleted();
//                result.setMessage("配时方案同步成功");
//            } else if (savedCount > 0) {
//                result.setSuccess(true);
//                result.setStatus(SyncStatus.PARTIAL_SUCCESS);
//                result.setMessage("配时方案部分同步成功");
//            } else {
//                result.markFailed("SYNC_FAILED", "配时方案同步失败");
//            }
//
//            logger.info("配时方案同步完成: controllerId={}, 成功={}, 失败={}",
//                    controllerId, savedCount, planParams.size() - savedCount);
//
//            return result;
//
//        } catch (Exception e) {
//            logger.error("配时方案同步异常: controllerId={}", controllerId, e);
//            result.markFailed("SYNC_EXCEPTION", "同步异常: " + e.getMessage());
//            return result;
//        }
//    }

    /**
     * 同步阶段参数到数据库
     */
//    @Transactional
//    public SyncResult syncStageParamsToDatabase(String controllerId, List<StageParam> stageParams) {
//        logger.info("同步阶段参数到数据库: controllerId={}, stageCount={}", controllerId, stageParams.size());
//
//        SyncResult result = SyncResult.inProgress(controllerId, "STAGE_SYNC", "开始同步阶段参数");
//        result.markStarted();
//
//        try {
//            // 删除旧数据
//            stageParamRepository.deleteByControllerId(controllerId);
//
//            // 保存新数据
//            int savedCount = 0;
//            for (StageParam stageParam : stageParams) {
//                try {
//                    StageParamEntity entity = convertToStageEntity(controllerId, stageParam);
//                    stageParamRepository.save(entity);
//                    savedCount++;
//
//                    result.addSuccessDetail("STAGE", stageParam.getStageNo().toString(),
//                            "阶段参数保存成功");
//
//                } catch (Exception e) {
//                    logger.error("保存阶段参数失败: controllerId={}, stageNo={}",
//                            controllerId, stageParam.getStageNo(), e);
//                    result.addFailureDetail("STAGE", stageParam.getStageNo().toString(),
//                            "SAVE_ERROR", "保存失败: " + e.getMessage());
//                }
//            }
//
//            // 更新结果
//            result.setAffectedRows(savedCount);
//            result.setSuccessCount(savedCount);
//            result.setFailureCount(stageParams.size() - savedCount);
//
//            if (savedCount == stageParams.size()) {
//                result.markCompleted();
//                result.setMessage("阶段参数同步成功");
//            } else if (savedCount > 0) {
//                result.setSuccess(true);
//                result.setStatus(SyncStatus.PARTIAL_SUCCESS);
//                result.setMessage("阶段参数部分同步成功");
//            } else {
//                result.markFailed("SYNC_FAILED", "阶段参数同步失败");
//            }
//
//            return result;
//
//        } catch (Exception e) {
//            logger.error("阶段参数同步异常: controllerId={}", controllerId, e);
//            result.markFailed("SYNC_EXCEPTION", "同步异常: " + e.getMessage());
//            return result;
//        }
//    }

//    /**
//     * 从数据库加载配时方案
//     */
//    public List<PlanParam> loadPlanParamsFromDatabase(String controllerId) {
//        try {
//            List<PlanParamEntity> entities = planParamRepository.findByControllerIdOrderByPlanNo(controllerId);
//
//            return entities.stream()
//                    .map(this::convertToPlanParam)
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            logger.error("加载配时方案失败: controllerId={}", controllerId, e);
//            return Collections.emptyList();
//        }
//    }

    /**
     * 从数据库加载阶段参数
     */
//    public List<StageParam> loadStageParamsFromDatabase(String controllerId) {
//        try {
//            List<StageParamEntity> entities = stageParamRepository.findByControllerIdOrderByStageNo(controllerId);
//
//            return entities.stream()
//                    .map(this::convertToStageParam)
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            logger.error("加载阶段参数失败: controllerId={}", controllerId, e);
//            return Collections.emptyList();
//        }
//    }

    // =================================================================
    // 转换方法
    // =================================================================

//    private PlanParamEntity convertToPlanEntity(String controllerId, PlanParam planParam) {
//        return PlanParamEntity.builder()
//                .controllerId(controllerId)
//                .planNo(planParam.getPlanNo())
//                .planName(planParam.getPlanName())
//                .cycleTime(planParam.getCycleTime())
//                .coordinationType(planParam.getCoordinationType())
//                .phaseOffset(planParam.getPhaseOffset())
//                .createTime(LocalDateTime.now())
//                .updateTime(LocalDateTime.now())
//                .build();
//    }

//    private StageParamEntity convertToStageEntity(String controllerId, StageParam stageParam) {
//        return StageParamEntity.builder()
//                .controllerId(controllerId)
//                .stageNo(stageParam.getStageNo())
//                .stageName(stageParam.getStageName())
//                .stageTime(stageParam.getStageTime())
//                .createTime(LocalDateTime.now())
//                .updateTime(LocalDateTime.now())
//                .build();
//    }
//
//    private PlanParam convertToPlanParam(PlanParamEntity entity) {
//        return PlanParam.builder()
//                .planNo(entity.getPlanNo())
//                .planName(entity.getPlanName())
//                .cycleTime(entity.getCycleTime())
//                .coordinationType(entity.getCoordinationType())
//                .phaseOffset(entity.getPhaseOffset())
//                .build();
//    }
//
//    private StageParam convertToStageParam(StageParamEntity entity) {
//        return StageParam.builder()
//                .stageNo(entity.getStageNo())
//                .stageName(entity.getStageName())
//                .stageTime(entity.getStageTime())
//                .build();
//    }
}
