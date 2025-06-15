package com.traffic.gat1049.service.abstracts;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.dto.PageRequestDto;
import com.traffic.gat1049.protocol.model.intersection.SignalGroupParam;
import com.traffic.gat1049.service.interfaces.SignalGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号组服务实现
 */
public class SignalGroupServiceImpl implements SignalGroupService {

    private static final Logger logger = LoggerFactory.getLogger(SignalGroupServiceImpl.class);
    private final ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    private final Map<String, SignalGroupParam> signalGroupStorage = new ConcurrentHashMap<>();

    public SignalGroupServiceImpl() throws BusinessException {
        initializeSampleData();
    }

    @Override
    public SignalGroupParam findById(String signalGroupId) throws BusinessException {
        if (signalGroupId == null || signalGroupId.trim().isEmpty()) {
            throw new ValidationException("signalGroupId", "信号组编号不能为空");
        }

        try {
            List<SignalGroupParam> signalGroups = dataProvider.getSignalGroupParams();
            if (signalGroups != null) {
                for (SignalGroupParam signalGroup : signalGroups) {
                    if (signalGroupId.equals(signalGroup.getSignalGroupId())) {
                        return signalGroup;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取信号组数据失败，使用本地存储", e);
        }

        SignalGroupParam signalGroup = signalGroupStorage.get(signalGroupId);
        if (signalGroup == null) {
            throw new DataNotFoundException("SignalGroupParam", signalGroupId);
        }
        return signalGroup;
    }

    @Override
    public List<SignalGroupParam> findAll() throws BusinessException {
        try {
            List<SignalGroupParam> signalGroups = dataProvider.getSignalGroupParams();
            if (signalGroups != null && !signalGroups.isEmpty()) {
                return signalGroups;
            }
        } catch (Exception e) {
            logger.warn("从数据提供者获取信号组列表失败，使用本地存储", e);
        }
        return new ArrayList<>(signalGroupStorage.values());
    }

    @Override
    public List<SignalGroupParam> findPage(PageRequestDto pageRequest) throws BusinessException {
        List<SignalGroupParam> allSignalGroups = findAll();
        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allSignalGroups.size());
        if (startIndex >= allSignalGroups.size()) {
            return new ArrayList<>();
        }
        return allSignalGroups.subList(startIndex, endIndex);
    }

    @Override
    public SignalGroupParam save(SignalGroupParam signalGroupParam) throws BusinessException {
        if (signalGroupParam == null) {
            throw new ValidationException("signalGroupParam", "信号组参数不能为空");
        }
        signalGroupParam.setUpdateTime(LocalDateTime.now());
        if (signalGroupParam.getCreateTime() == null) {
            signalGroupParam.setCreateTime(LocalDateTime.now());
        }
        signalGroupStorage.put(signalGroupParam.getSignalGroupId(), signalGroupParam);
        logger.info("保存信号组参数: {}", signalGroupParam.getSignalGroupId());
        return signalGroupParam;
    }

    @Override
    public void deleteById(String signalGroupId) throws BusinessException {
        if (signalGroupId == null || signalGroupId.trim().isEmpty()) {
            throw new ValidationException("signalGroupId", "信号组编号不能为空");
        }
        SignalGroupParam removed = signalGroupStorage.remove(signalGroupId);
        if (removed == null) {
            throw new DataNotFoundException("SignalGroupParam", signalGroupId);
        }
        logger.info("删除信号组参数: {}", signalGroupId);
    }

    @Override
    public List<SignalGroupParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return findAll().stream()
                .filter(signalGroup -> crossId.equals(signalGroup.getCrossId()))
                .collect(Collectors.toList());
    }

    private void initializeSampleData() {
        try {
            SignalGroupParam signalGroup1 = new SignalGroupParam();
            signalGroup1.setSignalGroupId("1101000010000101");
            signalGroup1.setCrossId("11010000100001");
            signalGroup1.setSignalGroupNo(1);
            signalGroup1.setCreateTime(LocalDateTime.now());
            signalGroupStorage.put(signalGroup1.getSignalGroupId(), signalGroup1);

            SignalGroupParam signalGroup2 = new SignalGroupParam();
            signalGroup2.setSignalGroupId("1101000010000102");
            signalGroup2.setCrossId("11010000100001");
            signalGroup2.setSignalGroupNo(2);
            signalGroup2.setCreateTime(LocalDateTime.now());
            signalGroupStorage.put(signalGroup2.getSignalGroupId(), signalGroup2);

            logger.info("初始化信号组示例数据完成，共 {} 个信号组", signalGroupStorage.size());
        } catch (Exception e) {
            logger.error("初始化信号组示例数据失败", e);
        }
    }
}
