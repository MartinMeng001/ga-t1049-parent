package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.PedestrianParamEntity;
import com.traffic.gat1049.repository.interfaces.PedestrianParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 人行横道参数测试服务
 * 按照 com.traffic.server.test 模式实现
 */
@Service
@Transactional
@Slf4j
public class TestPedestrianParamService {

    @Autowired
    private PedestrianParamRepository pedestrianParamRepository;

    /**
     * 创建人行横道参数
     */
    public PedestrianParamEntity createPedestrianParam(String crossId, Integer pedestrianNo,
                                                       String direction, Integer attribute) {
        log.info("开始创建人行横道参数: crossId={}, pedestrianNo={}", crossId, pedestrianNo);

        // 检查是否已存在
        PedestrianParamEntity existing = pedestrianParamRepository.findByCrossIdAndPedestrianNo(
                crossId, pedestrianNo);
        if (existing != null) {
            throw new IllegalArgumentException("人行横道参数已存在: " + crossId + "-" + pedestrianNo);
        }

        // 创建新的实体
        PedestrianParamEntity entity = new PedestrianParamEntity();
        entity.setCrossId(crossId);
        entity.setPedestrianNo(pedestrianNo);
        entity.setDirection(direction);
        entity.setAttribute(attribute);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        // 使用MyBatis Plus的insert方法
        int result = pedestrianParamRepository.insert(entity);
        if (result <= 0) {
            throw new RuntimeException("插入人行横道参数失败");
        }

        log.info("人行横道参数创建成功: crossId={}, pedestrianNo={}", crossId, pedestrianNo);
        return entity;
    }

    /**
     * 根据路口ID查询所有人行横道
     */
    public List<PedestrianParamEntity> findByCrossId(String crossId) {
        log.info("查询路口人行横道: crossId={}", crossId);
        return pedestrianParamRepository.findByCrossId(crossId);
    }

    /**
     * 根据路口ID和人行横道号查询
     */
    public PedestrianParamEntity findByCrossIdAndPedestrianNo(String crossId, Integer pedestrianNo) {
        log.info("查询指定人行横道: crossId={}, pedestrianNo={}", crossId, pedestrianNo);

        PedestrianParamEntity entity = pedestrianParamRepository.findByCrossIdAndPedestrianNo(
                crossId, pedestrianNo);
        if (entity == null) {
            throw new IllegalArgumentException("人行横道参数不存在: " + crossId + "-" + pedestrianNo);
        }
        return entity;
    }

    /**
     * 根据方向查询人行横道
     */
    public List<PedestrianParamEntity> findByDirection(String crossId, String direction) {
        log.info("按方向查询人行横道: crossId={}, direction={}", crossId, direction);
        return pedestrianParamRepository.findByDirection(crossId, direction);
    }

    /**
     * 查询有按钮的人行横道
     */
    public List<PedestrianParamEntity> findWithButton(String crossId) {
        log.info("查询有按钮的人行横道: crossId={}", crossId);
        return pedestrianParamRepository.findWithButton(crossId);
    }

    /**
     * 查询有语音提示的人行横道
     */
    public List<PedestrianParamEntity> findWithVoice(String crossId) {
        log.info("查询有语音提示的人行横道: crossId={}", crossId);
        return pedestrianParamRepository.findWithVoice(crossId);
    }

    /**
     * 根据信号组查询关联人行横道
     */
    public List<PedestrianParamEntity> findBySignalGroup(String crossId, Integer signalGroupNo) {
        log.info("查询信号组关联人行横道: crossId={}, signalGroupNo={}", crossId, signalGroupNo);
        return pedestrianParamRepository.findBySignalGroup(crossId, signalGroupNo);
    }

    /**
     * 批量创建测试数据
     */
    public List<PedestrianParamEntity> createBatchTestData(String crossId, int count) {
        log.info("开始批量创建人行横道测试数据: crossId={}, count={}", crossId, count);

        List<PedestrianParamEntity> created = new ArrayList<>();
        String[] directions = {"E", "S", "W", "N"};
        Integer[] attributes = {1, 21, 22};

        for (int i = 1; i <= count; i++) {
            try {
                // 避免重复创建
                PedestrianParamEntity existing = pedestrianParamRepository.findByCrossIdAndPedestrianNo(
                        crossId, i);
                if (existing != null) {
                    log.warn("跳过已存在的人行横道: crossId={}, pedestrianNo={}", crossId, i);
                    continue;
                }

                PedestrianParamEntity entity = new PedestrianParamEntity();
                entity.setCrossId(crossId);
                entity.setPedestrianNo(i);
                entity.setDirection(directions[i % directions.length]);
                entity.setAttribute(attributes[i % attributes.length]);
                entity.setCreatedTime(LocalDateTime.now());
                entity.setUpdatedTime(LocalDateTime.now());

                int result = pedestrianParamRepository.insert(entity);
                if (result > 0) {
                    created.add(entity);
                    log.debug("创建人行横道成功: crossId={}, pedestrianNo={}", crossId, i);
                }

            } catch (Exception e) {
                log.error("创建人行横道失败: crossId={}, pedestrianNo={}", crossId, i, e);
            }
        }

        log.info("批量创建完成: 成功创建 {} 条记录", created.size());
        return created;
    }

    /**
     * 清理测试数据
     */
    public int cleanupTestData(String crossId) {
        log.info("开始清理测试数据: crossId={}", crossId);

        try {
            // 查询要删除的记录
            List<PedestrianParamEntity> toDelete = pedestrianParamRepository.findByCrossId(crossId);

            int deleteCount = 0;
            for (PedestrianParamEntity entity : toDelete) {
                int result = pedestrianParamRepository.deleteById(entity.getId());
                if (result > 0) {
                    deleteCount++;
                }
            }

            log.info("清理测试数据完成: 删除 {} 条记录", deleteCount);
            return deleteCount;

        } catch (Exception e) {
            log.error("清理测试数据失败: crossId={}", crossId, e);
            throw new RuntimeException("清理测试数据失败", e);
        }
    }

    /**
     * 更新人行横道参数
     */
    public PedestrianParamEntity updatePedestrianParam(String crossId, Integer pedestrianNo,
                                                       String direction, Integer attribute) {
        log.info("开始更新人行横道参数: crossId={}, pedestrianNo={}", crossId, pedestrianNo);

        // 查找现有记录
        PedestrianParamEntity entity = pedestrianParamRepository.findByCrossIdAndPedestrianNo(
                crossId, pedestrianNo);
        if (entity == null) {
            throw new IllegalArgumentException("人行横道参数不存在: " + crossId + "-" + pedestrianNo);
        }

        // 更新字段
        entity.setDirection(direction);
        entity.setAttribute(attribute);
        entity.setUpdatedTime(LocalDateTime.now());

        // 使用MyBatis Plus的updateById方法
        int result = pedestrianParamRepository.updateById(entity);
        if (result <= 0) {
            throw new RuntimeException("更新人行横道参数失败");
        }

        log.info("人行横道参数更新成功: crossId={}, pedestrianNo={}", crossId, pedestrianNo);
        return entity;
    }
}
