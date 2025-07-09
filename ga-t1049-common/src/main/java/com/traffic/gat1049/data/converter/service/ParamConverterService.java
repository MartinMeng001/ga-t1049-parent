package com.traffic.gat1049.data.converter.service;

import com.traffic.gat1049.data.converter.impl.SignalGroupParamConverter;
import com.traffic.gat1049.data.converter.impl.StageParamConverter;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.repository.entity.SignalGroupLampGroupEntity;
import com.traffic.gat1049.repository.entity.SignalGroupParamEntity;
import com.traffic.gat1049.repository.entity.StageParamEntity;
import com.traffic.gat1049.repository.entity.StageSignalGroupStatusEntity;
import com.traffic.gat1049.repository.interfaces.SignalGroupLampGroupRepository;
import com.traffic.gat1049.repository.interfaces.StageSignalGroupStatusRepository;
import com.traffic.gat1049.repository.interfaces.SignalGroupParamRepository;
import com.traffic.gat1049.repository.interfaces.StageParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数转换器服务
 * 提供带有关联表处理的完整转换服务
 */
@Service
public class ParamConverterService {

    @Autowired
    private SignalGroupParamConverter signalGroupParamConverter;

    @Autowired
    private StageParamConverter stageParamConverter;

    @Autowired
    private SignalGroupParamRepository signalGroupParamRepository;

    @Autowired
    private StageParamRepository stageParamRepository;

    @Autowired
    private SignalGroupLampGroupRepository signalGroupLampGroupRepository;

    @Autowired
    private StageSignalGroupStatusRepository stageSignalGroupStatusRepository;

    /**
     * 完整保存信号组参数（包含关联表）
     */
    @Transactional
    public SignalGroupParam saveSignalGroupWithAssociations(SignalGroupParam protocol) {
        // 1. 保存主表
        SignalGroupParamEntity entity = signalGroupParamConverter.toEntity(protocol);
        signalGroupParamRepository.insert(entity);

        // 2. 保存关联表
        saveSignalGroupLampGroupAssociations(protocol);

        return protocol;
    }

    /**
     * 完整更新信号组参数（包含关联表）
     */
    @Transactional
    public SignalGroupParam updateSignalGroupWithAssociations(SignalGroupParam protocol) {
        // 1. 查询现有实体
        SignalGroupParamEntity entity = signalGroupParamRepository.findByCrossIdAndSignalGroupNo(
                protocol.getCrossId(), protocol.getSignalGroupNo());

        if (entity == null) {
            throw new IllegalArgumentException("信号组不存在: " + protocol.getCrossId() + ", " + protocol.getSignalGroupNo());
        }

        // 2. 更新主表
        signalGroupParamConverter.updateEntity(protocol, entity);
        signalGroupParamRepository.updateById(entity);

        // 3. 更新关联表
        updateSignalGroupLampGroupAssociations(protocol);

        return protocol;
    }

    /**
     * 完整保存阶段参数（包含关联表）
     */
    @Transactional
    public StageParam saveStageWithAssociations(StageParam protocol) {
        // 1. 保存主表
        StageParamEntity entity = stageParamConverter.toEntity(protocol);
        stageParamRepository.insert(entity);

        // 2. 保存关联表
        saveStageSignalGroupStatusAssociations(protocol);

        return protocol;
    }

    /**
     * 完整更新阶段参数（包含关联表）
     */
    @Transactional
    public StageParam updateStageWithAssociations(StageParam protocol) {
        // 1. 查询现有实体
        StageParamEntity entity = stageParamRepository.findByCrossIdAndStageNo(
                protocol.getCrossId(), protocol.getStageNo());

        if (entity == null) {
            throw new IllegalArgumentException("阶段不存在: " + protocol.getCrossId() + ", " + protocol.getStageNo());
        }

        // 2. 更新主表
        stageParamConverter.updateEntity(protocol, entity);
        stageParamRepository.updateById(entity);

        // 3. 更新关联表
        updateStageSignalGroupStatusAssociations(protocol);

        return protocol;
    }

    /**
     * 删除信号组及其关联数据
     */
    @Transactional
    public void deleteSignalGroupWithAssociations(String crossId, Integer signalGroupNo) {
        // 1. 删除关联表数据
        signalGroupLampGroupRepository.deleteByCrossIdAndSignalGroupNo(crossId, signalGroupNo);

        // 2. 删除主表数据
        SignalGroupParamEntity entity = signalGroupParamRepository.findByCrossIdAndSignalGroupNo(crossId, signalGroupNo);
        if (entity != null) {
            signalGroupParamRepository.deleteById(entity.getId());
        }
    }

    /**
     * 删除阶段及其关联数据
     */
    @Transactional
    public void deleteStageWithAssociations(String crossId, Integer stageNo) {
        // 1. 删除关联表数据
        stageSignalGroupStatusRepository.deleteByCrossIdAndStageNo(crossId, stageNo);

        // 2. 删除主表数据
        StageParamEntity entity = stageParamRepository.findByCrossIdAndStageNo(crossId, stageNo);
        if (entity != null) {
            stageParamRepository.deleteById(entity.getId());
        }
    }

    // ================================================================
    // 私有辅助方法
    // ================================================================

    /**
     * 保存信号组与灯组的关联关系
     */
    private void saveSignalGroupLampGroupAssociations(SignalGroupParam protocol) {
        if (protocol.getLampGroupNoList() == null || protocol.getLampGroupNoList().isEmpty()) {
            return;
        }

        List<SignalGroupLampGroupEntity> associations = new ArrayList<>();
        for (Integer lampGroupNo : protocol.getLampGroupNoList()) {
            SignalGroupLampGroupEntity association = new SignalGroupLampGroupEntity(
                    protocol.getCrossId(), protocol.getSignalGroupNo(), lampGroupNo);
            associations.add(association);
        }

        if (!associations.isEmpty()) {
            signalGroupLampGroupRepository.batchInsert(associations);
        }
    }

    /**
     * 更新信号组与灯组的关联关系
     */
    private void updateSignalGroupLampGroupAssociations(SignalGroupParam protocol) {
        // 1. 删除旧的关联关系
        signalGroupLampGroupRepository.deleteByCrossIdAndSignalGroupNo(
                protocol.getCrossId(), protocol.getSignalGroupNo());

        // 2. 保存新的关联关系
        saveSignalGroupLampGroupAssociations(protocol);
    }

    /**
     * 保存阶段信号组状态关联
     */
    private void saveStageSignalGroupStatusAssociations(StageParam protocol) {
        if (protocol.getSignalGroupStatusList() == null || protocol.getSignalGroupStatusList().isEmpty()) {
            return;
        }

        List<StageSignalGroupStatusEntity> statusEntities = new ArrayList<>();
        for (SignalGroupStatus status : protocol.getSignalGroupStatusList()) {
            StageSignalGroupStatusEntity statusEntity = new StageSignalGroupStatusEntity();
            statusEntity.setCrossId(protocol.getCrossId());
            statusEntity.setStageNo(protocol.getStageNo());
            statusEntity.setSignalGroupNo(status.getSignalGroupNo());

            // 需要确保 LampStatus 枚举有 getCode() 方法
            // statusEntity.setLampStatus(status.getLampStatus().getCode());

            // 临时处理，实际使用时需要根据具体的 LampStatus 枚举实现
            statusEntity.setLampStatus("G"); // 示例：G-绿灯, R-红灯, Y-黄灯

            statusEntities.add(statusEntity);
        }

        if (!statusEntities.isEmpty()) {
            stageSignalGroupStatusRepository.batchInsert(statusEntities);
        }
    }

    /**
     * 更新阶段信号组状态关联
     */
    private void updateStageSignalGroupStatusAssociations(StageParam protocol) {
        // 1. 删除旧的关联关系
        stageSignalGroupStatusRepository.deleteByCrossIdAndStageNo(
                protocol.getCrossId(), protocol.getStageNo());

        // 2. 保存新的关联关系
        saveStageSignalGroupStatusAssociations(protocol);
    }

    /**
     * 获取信号组的完整信息（包含关联数据）
     */
    public SignalGroupParam getSignalGroupWithAssociations(String crossId, Integer signalGroupNo) {
        // 1. 查询主表数据
        SignalGroupParamEntity entity = signalGroupParamRepository.findByCrossIdAndSignalGroupNo(crossId, signalGroupNo);
        if (entity == null) {
            return null;
        }

        // 2. 转换为协议对象（会自动查询关联数据）
        return signalGroupParamConverter.toProtocol(entity);
    }

    /**
     * 获取阶段的完整信息（包含关联数据）
     */
    public StageParam getStageWithAssociations(String crossId, Integer stageNo) {
        // 1. 查询主表数据
        StageParamEntity entity = stageParamRepository.findByCrossIdAndStageNo(crossId, stageNo);
        if (entity == null) {
            return null;
        }

        // 2. 转换为协议对象（会自动查询关联数据）
        return stageParamConverter.toProtocol(entity);
    }

    /**
     * 批量获取路口的所有信号组（包含关联数据）
     */
    public List<SignalGroupParam> getSignalGroupsByCrossId(String crossId) {
        List<SignalGroupParamEntity> entities = signalGroupParamRepository.findByCrossId(crossId);
        return signalGroupParamConverter.toProtocolList(entities);
    }

    /**
     * 批量获取路口的所有阶段（包含关联数据）
     */
    public List<StageParam> getStagesByCrossId(String crossId) {
        List<StageParamEntity> entities = stageParamRepository.findByCrossId(crossId);
        return stageParamConverter.toProtocolList(entities);
    }

    /**
     * 复制信号组参数到另一个路口
     */
    @Transactional
    public SignalGroupParam copySignalGroup(String sourceCrossId, Integer sourceSignalGroupNo,
                                            String targetCrossId, Integer targetSignalGroupNo) {
        // 1. 获取源信号组
        SignalGroupParam sourceProtocol = getSignalGroupWithAssociations(sourceCrossId, sourceSignalGroupNo);
        if (sourceProtocol == null) {
            throw new IllegalArgumentException("源信号组不存在");
        }

        // 2. 创建目标信号组
        SignalGroupParam targetProtocol = new SignalGroupParam();
        targetProtocol.setCrossId(targetCrossId);
        targetProtocol.setSignalGroupNo(targetSignalGroupNo);
        targetProtocol.setName(sourceProtocol.getName());
        targetProtocol.setGreenFlashLen(sourceProtocol.getGreenFlashLen());
        targetProtocol.setMaxGreen(sourceProtocol.getMaxGreen());
        targetProtocol.setMinGreen(sourceProtocol.getMinGreen());
        targetProtocol.setLampGroupNoList(new ArrayList<>(sourceProtocol.getLampGroupNoList()));

        // 3. 保存目标信号组
        return saveSignalGroupWithAssociations(targetProtocol);
    }

    /**
     * 复制阶段参数到另一个路口
     */
    @Transactional
    public StageParam copyStage(String sourceCrossId, Integer sourceStageNo,
                                String targetCrossId, Integer targetStageNo) {
        // 1. 获取源阶段
        StageParam sourceProtocol = getStageWithAssociations(sourceCrossId, sourceStageNo);
        if (sourceProtocol == null) {
            throw new IllegalArgumentException("源阶段不存在");
        }

        // 2. 创建目标阶段
        StageParam targetProtocol = new StageParam();
        targetProtocol.setCrossId(targetCrossId);
        targetProtocol.setStageNo(targetStageNo);
        targetProtocol.setStageName(sourceProtocol.getStageName());
        targetProtocol.setAttribute(sourceProtocol.getAttribute());

        // 复制信号组状态列表
        List<SignalGroupStatus> newStatusList = new ArrayList<>();
        for (SignalGroupStatus status : sourceProtocol.getSignalGroupStatusList()) {
            SignalGroupStatus newStatus = new SignalGroupStatus();
            newStatus.setSignalGroupNo(status.getSignalGroupNo());
            // newStatus.setLampStatus(status.getLampStatus());
            newStatusList.add(newStatus);
        }
        targetProtocol.setSignalGroupStatusList(newStatusList);

        // 3. 保存目标阶段
        return saveStageWithAssociations(targetProtocol);
    }
}

