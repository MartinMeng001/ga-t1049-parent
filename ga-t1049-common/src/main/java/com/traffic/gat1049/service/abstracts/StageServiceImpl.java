package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.model.dto.StageQueryDto;
import com.traffic.gat1049.model.vo.StageInfoVo;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.service.interfaces.StageService;
import com.traffic.gat1049.model.enums.LampStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 阶段服务实现
 */
@Service
public class StageServiceImpl implements StageService {

    private static final Logger logger = LoggerFactory.getLogger(StageServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 阶段参数存储
    private final Map<String, StageParam> stageStorage = new ConcurrentHashMap<>();

    public StageServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        //initializeSampleData();
    }

    @Override
    public StageParam findById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "阶段ID不能为空");
        }

        StageParam stageParam = stageStorage.get(id);
        if (stageParam == null) {
            throw new DataNotFoundException("StageParam", id);
        }

        return stageParam;
    }

    @Override
    public List<StageParam> findAll() throws BusinessException {
        return dataPrider.getAllStages();
        //return new ArrayList<>(stageStorage.values());
    }

    @Override
    public List<StageParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<StageParam> allStages = findAll();

        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allStages.size());

        if (start >= allStages.size()) {
            return new ArrayList<>();
        }

        return allStages.subList(start, end);
    }

    @Override
    public StageParam save(StageParam stageParam) throws BusinessException {
        if (stageParam == null) {
            throw new ValidationException("stageParam", "阶段参数不能为空");
        }

        validateStageParam(stageParam);

//        stageParam.setCreateTime(LocalDateTime.now());
//        stageParam.setUpdateTime(LocalDateTime.now());

        String stageId = generateStageId(stageParam.getCrossId(), stageParam.getStageNo());
        stageStorage.put(stageId, stageParam);

        logger.info("保存阶段参数: crossId={}, stageNo={}, stageName={}",
                stageParam.getCrossId(), stageParam.getStageNo(), stageParam.getStageName());

        return stageParam;
    }

    @Override
    public StageParam update(StageParam stageParam) throws BusinessException {
        if (stageParam == null) {
            throw new ValidationException("stageParam", "阶段参数不能为空");
        }

        String stageId = generateStageId(stageParam.getCrossId(), stageParam.getStageNo());
        if (!stageStorage.containsKey(stageId)) {
            throw new DataNotFoundException("StageParam", stageId);
        }

        validateStageParam(stageParam);

        //stageParam.setUpdateTime(LocalDateTime.now());
        stageStorage.put(stageId, stageParam);

        logger.info("更新阶段参数: crossId={}, stageNo={}, stageName={}",
                stageParam.getCrossId(), stageParam.getStageNo(), stageParam.getStageName());

        return stageParam;
    }

    @Override
    public void deleteById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("id", "阶段ID不能为空");
        }

        StageParam removed = stageStorage.remove(id);
        if (removed == null) {
            throw new DataNotFoundException("StageParam", id);
        }

        logger.info("删除阶段参数: id={}", id);
    }

    @Override
    public boolean existsById(String id) throws BusinessException {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return stageStorage.containsKey(id);
    }

    @Override
    public long count() throws BusinessException {
        return stageStorage.size();
    }

    @Override
    public List<StageParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataPrider.getStagesByCrossId(crossId);
//        return stageStorage.values().stream()
//                .filter(stage -> crossId.equals(stage.getCrossId()))
//                .collect(Collectors.toList());
    }

    @Override
    public StageParam findByCrossIdAndStageNo(String crossId, Integer stageNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (stageNo == null) {
            throw new ValidationException("stageNo", "阶段号不能为空");
        }

        String stageId = generateStageId(crossId, stageNo);
        StageParam stageParam = dataPrider.getStageByCrossIdAndNo(crossId, stageNo.toString());//stageStorage.get(stageId);
        if (stageParam == null) {
            throw new DataNotFoundException("StageParam",
                    String.format("crossId=%s, stageNo=%d", crossId, stageNo));
        }

        return stageParam;
    }

    @Override
    public List<StageParam> findByName(String stageName) throws BusinessException {
        if (stageName == null || stageName.trim().isEmpty()) {
            throw new ValidationException("stageName", "阶段名称不能为空");
        }

        return stageStorage.values().stream()
                .filter(stage -> stage.getStageName() != null &&
                        stage.getStageName().contains(stageName))
                .collect(Collectors.toList());
    }

    @Override
    public List<StageInfoVo> findByCriteria(StageQueryDto queryDto) throws BusinessException {
        final StageQueryDto finalQueryDto = queryDto != null ? queryDto : new StageQueryDto();

        return stageStorage.values().stream()
                .filter(stage -> matchesCriteria(stage, finalQueryDto))
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public List<StageParam> findByAttribute(Integer attribute) throws BusinessException {
        if (attribute == null) {
            throw new ValidationException("attribute", "阶段特征不能为空");
        }

        return stageStorage.values().stream()
                .filter(stage -> attribute.equals(stage.getAttribute()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SignalGroupStatus> getSignalGroupStatusList(String crossId, Integer stageNo) throws BusinessException {
        StageParam stage = findByCrossIdAndStageNo(crossId, stageNo);
        return stage.getSignalGroupStatusList() != null ?
                stage.getSignalGroupStatusList() : new ArrayList<>();
    }

    @Override
    public void updateSignalGroupStatus(String crossId, Integer stageNo,
                                        List<SignalGroupStatus> signalGroupStatusList) throws BusinessException {
        StageParam stage = findByCrossIdAndStageNo(crossId, stageNo);

        if (signalGroupStatusList == null) {
            throw new ValidationException("signalGroupStatusList", "信号组状态列表不能为空");
        }

        stage.setSignalGroupStatusList(signalGroupStatusList);
        //stage.setUpdateTime(LocalDateTime.now());

        String stageId = generateStageId(crossId, stageNo);
        stageStorage.put(stageId, stage);

        logger.info("更新阶段信号组状态: crossId={}, stageNo={}, groups={}",
                crossId, stageNo, signalGroupStatusList.size());
    }

    @Override
    public List<Integer> getStageNos(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        return stageStorage.values().stream()
                .filter(stage -> crossId.equals(stage.getCrossId()))
                .map(StageParam::getStageNo)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public StageParam copyStage(String sourceCrossId, Integer sourceStageNo,
                                String targetCrossId, Integer targetStageNo) throws BusinessException {
        StageParam sourceStage = findByCrossIdAndStageNo(sourceCrossId, sourceStageNo);

        StageParam targetStage = new StageParam(targetCrossId, targetStageNo);
        targetStage.setStageName(sourceStage.getStageName());
        targetStage.setAttribute(sourceStage.getAttribute());

        // 深拷贝信号组状态列表
        if (sourceStage.getSignalGroupStatusList() != null) {
            List<SignalGroupStatus> copiedStatusList = sourceStage.getSignalGroupStatusList().stream()
                    .map(this::copySignalGroupStatus)
                    .collect(Collectors.toList());
            targetStage.setSignalGroupStatusList(copiedStatusList);
        }

        return save(targetStage);
    }

    @Override
    public boolean validateStageConfig(StageParam stageParam) throws BusinessException {
        if (stageParam == null) {
            return false;
        }

        try {
            validateStageParam(stageParam);

            // 验证信号组状态配置
            if (stageParam.getSignalGroupStatusList() != null) {
                for (SignalGroupStatus status : stageParam.getSignalGroupStatusList()) {
                    if (status.getSignalGroupNo() == null || status.getSignalGroupNo() <= 0) {
                        logger.warn("信号组编号无效: {}", status.getSignalGroupNo());
                        return false;
                    }
                    if (status.getLampStatus() == null) {
                        logger.warn("信号组灯态为空: signalGroupNo={}", status.getSignalGroupNo());
                        return false;
                    }
                }
            }

            return true;
        } catch (ValidationException e) {
            logger.warn("阶段配置验证失败: {}", e.getMessage());
            return false;
        }
    }

    // 私有方法

    private void validateStageParam(StageParam stageParam) throws BusinessException {
        if (stageParam.getCrossId() == null || stageParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (stageParam.getStageNo() == null || stageParam.getStageNo() <= 0) {
            throw new ValidationException("stageNo", "阶段号不能为空且必须大于0");
        }

        if (stageParam.getSignalGroupStatusList() == null || stageParam.getSignalGroupStatusList().isEmpty()) {
            throw new ValidationException("signalGroupStatusList", "信号组状态列表不能为空");
        }
    }

    private boolean matchesCriteria(StageParam stage, StageQueryDto query) {
        if (query.getCrossId() != null && !query.getCrossId().equals(stage.getCrossId())) {
            return false;
        }

        if (query.getStageNo() != null && !query.getStageNo().equals(stage.getStageNo())) {
            return false;
        }

        if (query.getStageName() != null &&
                (stage.getStageName() == null || !stage.getStageName().contains(query.getStageName()))) {
            return false;
        }

        if (query.getAttribute() != null && !query.getAttribute().equals(stage.getAttribute())) {
            return false;
        }

        return true;
    }

    private StageInfoVo convertToVo(StageParam stage) {
        StageInfoVo vo = new StageInfoVo();
        vo.setCrossId(stage.getCrossId());
        vo.setStageNo(stage.getStageNo());
        vo.setStageName(stage.getStageName());
        vo.setAttribute(stage.getAttribute());
        vo.setSignalGroupCount(stage.getSignalGroupStatusList() != null ?
                stage.getSignalGroupStatusList().size() : 0);
        //vo.setLastUpdateTime(stage.getUpdateTime());
        return vo;
    }

    private String generateStageId(String crossId, Integer stageNo) {
        return crossId + "_" + stageNo;
    }

    private SignalGroupStatus copySignalGroupStatus(SignalGroupStatus source) {
        SignalGroupStatus copy = new SignalGroupStatus();
        copy.setSignalGroupNo(source.getSignalGroupNo());
        copy.setLampStatus(source.getLampStatus());
        //copy.setCountDown(source.getCountDown());
        return copy;
    }

    private void initializeSampleData() {
//        try {
//            // 创建示例阶段数据 - 路口1的阶段
//            StageParam stage1 = new StageParam("11010000100001", 1);
//            stage1.setStageName("东西直行");
//            stage1.setAttribute(0); // 一般阶段
//
//            List<SignalGroupStatus> statusList1 = Arrays.asList(
//                    createSignalGroupStatus(1, LampStatus.GREEN, 30),
//                    createSignalGroupStatus(2, LampStatus.RED, 0),
//                    createSignalGroupStatus(3, LampStatus.GREEN, 30),
//                    createSignalGroupStatus(4, LampStatus.RED, 0)
//            );
//            stage1.setSignalGroupStatusList(statusList1);
//
//            StageParam stage2 = new StageParam("11010000100001", 2);
//            stage2.setStageName("东西左转");
//            stage2.setAttribute(1); // 感应阶段
//
//            List<SignalGroupStatus> statusList2 = Arrays.asList(
//                    createSignalGroupStatus(1, LampStatus.RED, 0),
//                    createSignalGroupStatus(2, LampStatus.GREEN, 25),
//                    createSignalGroupStatus(3, LampStatus.RED, 0),
//                    createSignalGroupStatus(4, LampStatus.GREEN, 25)
//            );
//            stage2.setSignalGroupStatusList(statusList2);
//
//            StageParam stage3 = new StageParam("11010000100001", 3);
//            stage3.setStageName("南北直行");
//            stage3.setAttribute(0);
//
//            List<SignalGroupStatus> statusList3 = Arrays.asList(
//                    createSignalGroupStatus(1, LampStatus.RED, 0),
//                    createSignalGroupStatus(2, LampStatus.RED, 0),
//                    createSignalGroupStatus(3, LampStatus.RED, 0),
//                    createSignalGroupStatus(4, LampStatus.RED, 0)
//            );
//            stage3.setSignalGroupStatusList(statusList3);
//
//            // 创建示例阶段数据 - 路口2的阶段
//            StageParam stage4 = new StageParam("11010000100002", 1);
//            stage4.setStageName("主路直行");
//            stage4.setAttribute(0);
//
//            List<SignalGroupStatus> statusList4 = Arrays.asList(
//                    createSignalGroupStatus(1, LampStatus.GREEN, 35),
//                    createSignalGroupStatus(2, LampStatus.RED, 0),
//                    createSignalGroupStatus(3, LampStatus.GREEN, 35)
//            );
//            stage4.setSignalGroupStatusList(statusList4);
//
//            save(stage1);
//            save(stage2);
//            save(stage3);
//            save(stage4);
//
//            logger.info("示例阶段数据初始化完成");
//        } catch (BusinessException e) {
//            logger.error("示例阶段数据初始化失败", e);
//        }
    }

    private SignalGroupStatus createSignalGroupStatus(Integer signalGroupNo, String lampStatus, Integer countDown) {
        SignalGroupStatus status = new SignalGroupStatus();
        status.setSignalGroupNo(signalGroupNo);
        status.setLampStatus(lampStatus);
        //status.setCountDown(countDown);
        return status;
    }
}