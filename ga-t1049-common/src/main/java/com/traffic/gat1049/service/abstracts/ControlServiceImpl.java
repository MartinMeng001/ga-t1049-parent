package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.enums.*;
import com.traffic.gat1049.service.interfaces.ControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制服务实现
 */
@Service
public class ControlServiceImpl implements ControlService {

    private static final Logger logger = LoggerFactory.getLogger(ControlServiceImpl.class);

    // 流向锁定状态存储
    private final Map<String, FlowLockInfo> flowLockStorage = new ConcurrentHashMap<>();

    @Override
    public void lockFlowDirection(String crossId, FlowType flowType, Direction entrance, Direction exit,
                                  LockType lockType, Integer lockStageNo, Integer duration) throws BusinessException {
        validateLockParameters(crossId, flowType, entrance, exit, lockType, lockStageNo, duration);

        String lockKey = generateLockKey(crossId, flowType, entrance, exit);
        FlowLockInfo lockInfo = new FlowLockInfo(crossId, flowType, entrance, exit, lockType, lockStageNo, duration);
        lockInfo.setLockTime(LocalDateTime.now());

        flowLockStorage.put(lockKey, lockInfo);

        logger.info("锁定交通流向: crossId={}, flowType={}, entrance={}, exit={}, lockType={}, lockStageNo={}, duration={}",
                crossId, flowType.getDescription(), entrance.getDescription(),
                exit.getDescription(), lockType.getDescription(), lockStageNo, duration);
    }

    @Override
    public void unlockFlowDirection(String crossId, FlowType flowType, Direction entrance, Direction exit)
            throws BusinessException {
        validateUnlockParameters(crossId, flowType, entrance, exit);

        String lockKey = generateLockKey(crossId, flowType, entrance, exit);
        FlowLockInfo removed = flowLockStorage.remove(lockKey);

        if (removed == null) {
            logger.warn("尝试解锁未锁定的流向: crossId={}, flowType={}, entrance={}, exit={}",
                    crossId, flowType.getDescription(), entrance.getDescription(), exit.getDescription());
        } else {
            logger.info("解锁交通流向: crossId={}, flowType={}, entrance={}, exit={}",
                    crossId, flowType.getDescription(), entrance.getDescription(), exit.getDescription());
        }
    }

    @Override
    public void stageIntervention(String crossId, Integer stageNo, InterventionType interventionType, Integer len)
            throws BusinessException {
        validateStageInterventionParameters(crossId, stageNo, interventionType, len);

        logger.info("阶段干预: crossId={}, stageNo={}, interventionType={}, len={}",
                crossId, stageNo, interventionType.getDescription(), len);

        // 这里应该实际执行阶段干预逻辑
        // 由于是演示实现，这里只记录日志
    }

    @Override
    public boolean supportsOperation(String crossId, String operationType) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (operationType == null || operationType.trim().isEmpty()) {
            throw new ValidationException("operationType", "操作类型不能为空");
        }

        // 简单实现：假设所有路口都支持基本操作
        switch (operationType) {
            case "LOCK_FLOW":
            case "UNLOCK_FLOW":
            case "STAGE_INTERVENTION":
            case "EMERGENCY_CONTROL":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getLockStatus(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        StringBuilder status = new StringBuilder("路口锁定状态:\n");
        boolean hasLocks = false;

        for (Map.Entry<String, FlowLockInfo> entry : flowLockStorage.entrySet()) {
            FlowLockInfo lockInfo = entry.getValue();
            if (crossId.equals(lockInfo.getCrossId())) {
                hasLocks = true;
                status.append(String.format("- %s流向 %s->%s: %s锁定，持续时间%d秒\n",
                        lockInfo.getFlowType().getDescription(),
                        lockInfo.getEntrance().getDescription(),
                        lockInfo.getExit().getDescription(),
                        lockInfo.getLockType().getDescription(),
                        lockInfo.getDuration()));
            }
        }

        if (!hasLocks) {
            status.append("无锁定流向");
        }

        return status.toString();
    }

    @Override
    public void emergencyControl(String crossId, ControlMode controlMode) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (controlMode == null) {
            throw new ValidationException("controlMode", "控制方式不能为空");
        }

        logger.info("紧急控制: crossId={}, controlMode={}", crossId, controlMode.getDescription());

        // 这里应该实际执行紧急控制逻辑
        // 由于是演示实现，这里只记录日志
    }

    private void validateLockParameters(String crossId, FlowType flowType, Direction entrance, Direction exit,
                                        LockType lockType, Integer lockStageNo, Integer duration) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (flowType == null) {
            throw new ValidationException("flowType", "交通流类型不能为空");
        }
        if (entrance == null) {
            throw new ValidationException("entrance", "进口方向不能为空");
        }
        if (exit == null) {
            throw new ValidationException("exit", "出口方向不能为空");
        }
        if (lockType == null) {
            throw new ValidationException("lockType", "锁定类型不能为空");
        }
        if (lockStageNo == null) {
            throw new ValidationException("lockStageNo", "锁定阶段号不能为空");
        }
        if (duration == null || duration < 0 || duration > 3600) {
            throw new ValidationException("duration", "锁定持续时间必须在0-3600秒范围内");
        }

        // 验证锁定阶段号与锁定类型的一致性
        validateLockStageNoConsistency(lockType, lockStageNo);
    }

    private void validateLockStageNoConsistency(LockType lockType, Integer lockStageNo) throws ValidationException {
        if (lockType.usesDefaultStageNo() && lockStageNo != 0) {
            throw new ValidationException("lockStageNo",
                    String.format("锁定类型为%s时，锁定阶段号必须为0", lockType.getDescription()));
        }

        if (lockType.requiresStageNo() && lockStageNo <= 0) {
            throw new ValidationException("lockStageNo",
                    String.format("锁定类型为%s时，锁定阶段号必须大于0", lockType.getDescription()));
        }
    }

    private void validateUnlockParameters(String crossId, FlowType flowType, Direction entrance, Direction exit)
            throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (flowType == null) {
            throw new ValidationException("flowType", "交通流类型不能为空");
        }
        if (entrance == null) {
            throw new ValidationException("entrance", "进口方向不能为空");
        }
        if (exit == null) {
            throw new ValidationException("exit", "出口方向不能为空");
        }
    }

    private void validateStageInterventionParameters(String crossId, Integer stageNo,
                                                     InterventionType interventionType, Integer len)
            throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (stageNo == null) {
            throw new ValidationException("stageNo", "阶段号不能为空");
        }
        if (interventionType == null) {
            throw new ValidationException("interventionType", "干预类型不能为空");
        }
        if (len == null || len <= 0) {
            throw new ValidationException("len", "干预时长必须大于0");
        }
    }

    private String generateLockKey(String crossId, FlowType flowType, Direction entrance, Direction exit) {
        return String.format("%s_%s_%s_%s", crossId, flowType.getCode(), entrance.getCode(), exit.getCode());
    }

    /**
     * 流向锁定信息
     */
    private static class FlowLockInfo {
        private final String crossId;
        private final FlowType flowType;
        private final Direction entrance;
        private final Direction exit;
        private final LockType lockType;
        private final Integer duration;
        private final Integer lockStage;
        private LocalDateTime lockTime;

        public FlowLockInfo(String crossId, FlowType flowType, Direction entrance, Direction exit,
                            LockType lockType, Integer lockStage, Integer duration) {
            this.crossId = crossId;
            this.flowType = flowType;
            this.entrance = entrance;
            this.exit = exit;
            this.lockType = lockType;
            this.duration = duration;
            this.lockStage = lockStage;
        }

        // Getters
        public String getCrossId() { return crossId; }
        public FlowType getFlowType() { return flowType; }
        public Direction getEntrance() { return entrance; }
        public Direction getExit() { return exit; }
        public LockType getLockType() { return lockType; }
        public Integer getDuration() { return duration; }
        public LocalDateTime getLockTime() { return lockTime; }
        public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }
        public Integer getLockStage() { return lockStage; }
    }
}
