package com.traffic.server.test;

import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.repository.dto.SignalGroupWithLampGroupsDto;
import com.traffic.gat1049.repository.entity.SignalGroupLampGroupEntity;
import com.traffic.gat1049.repository.entity.SignalGroupParamEntity;
import com.traffic.gat1049.repository.interfaces.SignalGroupLampGroupRepository;
import com.traffic.gat1049.repository.interfaces.SignalGroupParamRepository;
import com.traffic.server.test.dto.CreateSignalGroupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 信号组参数测试服务类
 * 按照项目中的测试模式设计
 */
@Service
@Slf4j
class TestSignalGroupParamService {

    @Autowired
    private SignalGroupParamRepository signalGroupParamRepository;

    @Autowired
    private SignalGroupLampGroupRepository signalGroupLampGroupRepository;

    /**
     * 获取路口所有信号组（包含关联灯组）
     */
    public List<SignalGroupParam> getSignalGroupsWithLampGroups(String crossId) {
        log.info("查询路口所有信号组: crossId={}", crossId);

        List<SignalGroupWithLampGroupsDto> dtos = signalGroupParamRepository.findSignalGroupsWithLampGroups(crossId);

        log.info("找到{}个信号组", dtos.size());

        return dtos.stream()
                .map(this::convertDtoToProtocol)
                .collect(Collectors.toList());
    }
    /**
     * 方案1：直接传入SignalGroupParam对象（推荐）
     * 一次性创建包含关联灯组的完整信号组
     *
     * @param signalGroupParam 包含lampGroupNoList的完整信号组参数
     * @return 创建成功的信号组参数
     */
    @Transactional(rollbackFor = Exception.class)
    public SignalGroupParam createCompleteSignalGroup(SignalGroupParam signalGroupParam) {
        log.info("创建完整信号组: crossId={}, signalGroupNo={}, 灯组数量={}",
                signalGroupParam.getCrossId(),
                signalGroupParam.getSignalGroupNo(),
                signalGroupParam.getLampGroupNoList() != null ? signalGroupParam.getLampGroupNoList().size() : 0);

        try {
            // 1. 验证输入参数
            validateSignalGroupParam(signalGroupParam);

            // 2. 检查信号组是否已存在
            checkSignalGroupExists(signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

            // 3. 创建信号组主表记录
            SignalGroupParamEntity entity = createSignalGroupEntity(signalGroupParam);
            int insertResult = signalGroupParamRepository.insert(entity);

            if (insertResult <= 0) {
                throw new RuntimeException("信号组主表创建失败");
            }

            // 4. 创建信号组与灯组的关联关系
            if (signalGroupParam.getLampGroupNoList() != null && !signalGroupParam.getLampGroupNoList().isEmpty()) {
                createLampGroupAssociations(signalGroupParam);
            }

            log.info("信号组创建成功: crossId={}, signalGroupNo={}",
                    signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

            return signalGroupParam;

        } catch (Exception e) {
            log.error("创建信号组失败: crossId={}, signalGroupNo={}",
                    signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo(), e);
            throw new RuntimeException("创建信号组失败: " + e.getMessage(), e);
        }
    }

    /**
     * 方案2：使用CreateSignalGroupRequest对象
     * 更明确的创建请求对象，包含所有必要信息
     */
    @Transactional(rollbackFor = Exception.class)
    public SignalGroupParam createSignalGroupWithRequest(CreateSignalGroupRequest request) {
        log.info("使用请求对象创建信号组: {}", request);

        // 转换为SignalGroupParam对象
        SignalGroupParam signalGroupParam = convertRequestToParam(request);

        // 调用主创建方法
        return createCompleteSignalGroup(signalGroupParam);
    }

    /**
     * 方案3：分步创建但在同一事务中
     * 参数分别传入，但在同一个事务中完成所有操作
     */
    @Transactional(rollbackFor = Exception.class)
    public SignalGroupParam createSignalGroupStepByStep(
            String crossId,
            Integer signalGroupNo,
            String name,
            Integer type,
            Integer greenFlashLen,
            Integer maxGreen,
            Integer minGreen,
            List<Integer> lampGroupNoList) {

        log.info("分步创建信号组: crossId={}, signalGroupNo={}, name={}, 灯组列表={}",
                crossId, signalGroupNo, name, lampGroupNoList);

        // 构建SignalGroupParam对象
        SignalGroupParam signalGroupParam = new SignalGroupParam();
        signalGroupParam.setCrossId(crossId);
        signalGroupParam.setSignalGroupNo(signalGroupNo);
        signalGroupParam.setName(name);
        signalGroupParam.setGreenFlashLen(greenFlashLen);
        signalGroupParam.setMaxGreen(maxGreen);
        signalGroupParam.setMinGreen(minGreen);
        signalGroupParam.setLampGroupNoList(lampGroupNoList);

        // 调用主创建方法
        return createCompleteSignalGroup(signalGroupParam);
    }

    /**
     * 更新信号组（包含关联灯组）
     */
    @Transactional(rollbackFor = Exception.class)
    public SignalGroupParam updateCompleteSignalGroup(SignalGroupParam signalGroupParam) {
        log.info("更新完整信号组: crossId={}, signalGroupNo={}",
                signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

        try {
            // 1. 验证信号组是否存在
            SignalGroupParamEntity existingEntity = signalGroupParamRepository.findByCrossIdAndSignalGroupNo(
                    signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

            if (existingEntity == null) {
                throw new RuntimeException("信号组不存在: " + signalGroupParam.getCrossId() + ", " + signalGroupParam.getSignalGroupNo());
            }

            // 2. 更新主表
            updateSignalGroupEntity(existingEntity, signalGroupParam);
            int updateResult = signalGroupParamRepository.updateById(existingEntity);

            if (updateResult <= 0) {
                throw new RuntimeException("信号组主表更新失败");
            }

            // 3. 更新关联关系：先删除后创建
            signalGroupLampGroupRepository.deleteByCrossIdAndSignalGroupNo(
                    signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

            if (signalGroupParam.getLampGroupNoList() != null && !signalGroupParam.getLampGroupNoList().isEmpty()) {
                createLampGroupAssociations(signalGroupParam);
            }

            log.info("信号组更新成功: crossId={}, signalGroupNo={}",
                    signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

            return signalGroupParam;

        } catch (Exception e) {
            log.error("更新信号组失败: crossId={}, signalGroupNo={}",
                    signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo(), e);
            throw new RuntimeException("更新信号组失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除信号组（包含关联灯组）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompleteSignalGroup(String crossId, Integer signalGroupNo) {
        log.info("删除完整信号组: crossId={}, signalGroupNo={}", crossId, signalGroupNo);

        try {
            // 1. 验证信号组是否存在
            SignalGroupParamEntity existingEntity = signalGroupParamRepository.findByCrossIdAndSignalGroupNo(crossId, signalGroupNo);

            if (existingEntity == null) {
                throw new RuntimeException("信号组不存在: " + crossId + ", " + signalGroupNo);
            }

            // 2. 先删除关联关系
            int deletedAssociations = signalGroupLampGroupRepository.deleteByCrossIdAndSignalGroupNo(crossId, signalGroupNo);
            log.info("删除关联关系数量: {}", deletedAssociations);

            // 3. 再删除主表记录
            int deleteResult = signalGroupParamRepository.deleteById(existingEntity.getId());

            if (deleteResult <= 0) {
                throw new RuntimeException("信号组主表删除失败");
            }

            log.info("信号组删除成功: crossId={}, signalGroupNo={}", crossId, signalGroupNo);

        } catch (Exception e) {
            log.error("删除信号组失败: crossId={}, signalGroupNo={}", crossId, signalGroupNo, e);
            throw new RuntimeException("删除信号组失败: " + e.getMessage(), e);
        }
    }

    // ============ 私有辅助方法 ============

    /**
     * 验证信号组参数
     */
    private void validateSignalGroupParam(SignalGroupParam signalGroupParam) {
        if (signalGroupParam == null) {
            throw new IllegalArgumentException("信号组参数不能为空");
        }

        if (signalGroupParam.getCrossId() == null || signalGroupParam.getCrossId().trim().isEmpty()) {
            throw new IllegalArgumentException("路口编号不能为空");
        }

        if (signalGroupParam.getSignalGroupNo() == null) {
            throw new IllegalArgumentException("信号组序号不能为空");
        }

        if (signalGroupParam.getSignalGroupNo() < 1 || signalGroupParam.getSignalGroupNo() > 999) {
            throw new IllegalArgumentException("信号组序号必须在1-999之间");
        }

        if (signalGroupParam.getName() != null && signalGroupParam.getName().length() > 50) {
            throw new IllegalArgumentException("信号组名称最大长度为50");
        }

        // 验证灯组列表
        if (signalGroupParam.getLampGroupNoList() != null) {
            for (Integer lampGroupNo : signalGroupParam.getLampGroupNoList()) {
                if (lampGroupNo == null || lampGroupNo < 1) {
                    throw new IllegalArgumentException("灯组编号必须大于0");
                }
            }
        }
    }

    /**
     * 检查信号组是否已存在
     */
    private void checkSignalGroupExists(String crossId, Integer signalGroupNo) {
        SignalGroupParamEntity existing = signalGroupParamRepository.findByCrossIdAndSignalGroupNo(crossId, signalGroupNo);
        if (existing != null) {
            throw new RuntimeException("信号组已存在: " + crossId + ", " + signalGroupNo);
        }
    }

    /**
     * 创建信号组实体
     */
    private SignalGroupParamEntity createSignalGroupEntity(SignalGroupParam signalGroupParam) {
        SignalGroupParamEntity entity = new SignalGroupParamEntity();
        entity.setCrossId(signalGroupParam.getCrossId());
        entity.setSignalGroupNo(signalGroupParam.getSignalGroupNo());
        entity.setName(signalGroupParam.getName());
        entity.setGreenFlashLen(signalGroupParam.getGreenFlashLen());
        entity.setMaxGreen(signalGroupParam.getMaxGreen());
        entity.setMinGreen(signalGroupParam.getMinGreen());
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        return entity;
    }

    /**
     * 更新信号组实体
     */
    private void updateSignalGroupEntity(SignalGroupParamEntity entity, SignalGroupParam signalGroupParam) {
        entity.setName(signalGroupParam.getName());
        entity.setGreenFlashLen(signalGroupParam.getGreenFlashLen());
        entity.setMaxGreen(signalGroupParam.getMaxGreen());
        entity.setMinGreen(signalGroupParam.getMinGreen());
        entity.setUpdatedTime(LocalDateTime.now());
    }

    /**
     * 创建信号组与灯组的关联关系
     */
    private void createLampGroupAssociations(SignalGroupParam signalGroupParam) {
        List<SignalGroupLampGroupEntity> associations = new ArrayList<>();

        for (Integer lampGroupNo : signalGroupParam.getLampGroupNoList()) {
            SignalGroupLampGroupEntity association = new SignalGroupLampGroupEntity(
                    signalGroupParam.getCrossId(),
                    signalGroupParam.getSignalGroupNo(),
                    lampGroupNo);
            association.setCreatedTime(LocalDateTime.now());
            associations.add(association);
        }

        if (!associations.isEmpty()) {
            int batchResult = signalGroupLampGroupRepository.batchInsert(associations);
            log.info("批量创建灯组关联关系: 预期={}, 实际={}", associations.size(), batchResult);
        }
    }

    /**
     * 转换请求对象为参数对象
     */
    private SignalGroupParam convertRequestToParam(CreateSignalGroupRequest request) {
        SignalGroupParam param = new SignalGroupParam();
        param.setCrossId(request.getCrossId());
        param.setSignalGroupNo(request.getSignalGroupNo());
        param.setName(request.getName());
        param.setGreenFlashLen(request.getGreenFlashLen());
        param.setMaxGreen(request.getMaxGreen());
        param.setMinGreen(request.getMinGreen());
        param.setLampGroupNoList(request.getLampGroupNoList());

        return param;
    }
    /**
     * DTO转协议对象
     * 这是关键的转换逻辑
     */
    private SignalGroupParam convertDtoToProtocol(SignalGroupWithLampGroupsDto dto) {
        if (dto == null) {
            return null;
        }

        SignalGroupParam protocol = new SignalGroupParam();

        // 基础字段映射
        protocol.setCrossId(dto.getCrossId());
        protocol.setSignalGroupNo(dto.getSignalGroupNo());
        protocol.setName(dto.getName());
        protocol.setGreenFlashLen(dto.getGreenFlashLen());
        protocol.setMaxGreen(dto.getMaxGreen());
        protocol.setMinGreen(dto.getMinGreen());

        // 关联灯组数据映射
        protocol.setLampGroupNoList(dto.getLampGroupNoList());

        log.debug("转换完成: crossId={}, signalGroupNo={}, 关联灯组数量={}",
                dto.getCrossId(), dto.getSignalGroupNo(), dto.getLampGroupCount());

        return protocol;
    }
}
