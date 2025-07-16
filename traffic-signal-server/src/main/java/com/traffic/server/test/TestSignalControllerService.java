package com.traffic.server.test;

import com.traffic.gat1049.repository.entity.SignalControllerParamEntity;
import com.traffic.gat1049.repository.interfaces.SignalControllerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 信号机Repository测试服务
 *
 * 放置位置：traffic-signal-server/src/main/java/com/traffic/server/test/TestSignalControllerService.java
 */
@Service
@Slf4j
public class TestSignalControllerService {

    @Autowired
    private SignalControllerRepository signalControllerRepository;

    /**
     * 创建测试信号机（使用完整实体对象）
     */
    @Transactional
    public SignalControllerParamEntity createSignalControllerWithEntity(SignalControllerParamEntity entity) {
        // 验证必要字段
        if (entity.getSignalControllerId() == null || entity.getSignalControllerId().trim().isEmpty()) {
            throw new RuntimeException("信号机ID不能为空");
        }

        // 检查是否已存在
        SignalControllerParamEntity existing = signalControllerRepository.findBySignalControllerId(entity.getSignalControllerId());
        if (existing != null) {
            throw new RuntimeException("信号机ID已存在: " + entity.getSignalControllerId());
        }

        // 设置默认值
//        if (entity.getCrossIdList() == null) {
//            entity.setCrossIdList("[]");
//        }
//        if (entity.getSystemIdList() == null) {
//            entity.setSystemIdList("[]");
//        }
//        if (entity.getCrossCount() == null) {
//            entity.setCrossCount(0);
//        }
//        if (entity.getSystemCount() == null) {
//            entity.setSystemCount(0);
//        }
        if (entity.getCreatedTime() == null) {
            entity.setCreatedTime(LocalDateTime.now());
        }
        if (entity.getUpdatedTime() == null) {
            entity.setUpdatedTime(LocalDateTime.now());
        }

        signalControllerRepository.insert(entity);

        log.info("创建信号机成功: {}", entity.getSignalControllerId());
        return entity;
    }

    /**
     * 更新信号机（使用完整实体对象）
     */
    @Transactional
    public SignalControllerParamEntity updateSignalControllerWithEntity(SignalControllerParamEntity entity) {
        if (entity.getSignalControllerId() == null || entity.getSignalControllerId().trim().isEmpty()) {
            throw new RuntimeException("信号机ID不能为空");
        }

        SignalControllerParamEntity existing = signalControllerRepository.findBySignalControllerId(entity.getSignalControllerId());
        if (existing == null) {
            throw new RuntimeException("信号机不存在: " + entity.getSignalControllerId());
        }

        // 保留原有的ID和创建时间
        entity.setSignalControllerId(existing.getSignalControllerId());
        entity.setCreatedTime(existing.getCreatedTime());
        entity.setUpdatedTime(LocalDateTime.now());

        signalControllerRepository.updateById(entity);

        log.info("更新信号机成功: {}", entity.getSignalControllerId());
        return entity;
    }
    @Transactional
    public SignalControllerParamEntity createSignalController(String signalControllerId, String supplier,
                                                              String type, String ip, Integer port) {
        SignalControllerParamEntity entity = new SignalControllerParamEntity();

        entity.setSignalControllerId(signalControllerId);
        entity.setSupplier(supplier);
        entity.setType(type);
        entity.setIp(ip);
        entity.setPort(port);

        // 设置一些示例数据
//        entity.setCrossIdList("[\"CROSS001\", \"CROSS002\"]"); // JSON格式的路口ID列表
//        entity.setSystemIdList("[\"SYS001\"]"); // JSON格式的系统ID列表
//        entity.setCrossCount(2);
//        entity.setSystemCount(1);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        signalControllerRepository.insert(entity);

        log.info("创建测试信号机成功: {}", signalControllerId);
        return entity;
    }

    /**
     * 批量创建测试数据
     */
    @Transactional
    public List<SignalControllerParamEntity> createBatchTestData(int count) {
        List<SignalControllerParamEntity> created = new ArrayList<>();

        String[] suppliers = {"海信", "大华", "华为", "信号技术", "智能交通"};
        String[] types = {"1", "2", "3"};
        String[] ipBases = {"192.168.1.", "192.168.2.", "10.0.0."};

        for (int i = 0; i < count; i++) {
            String signalControllerId = "BATCH_TEST_" + System.currentTimeMillis() + "_" + i;
            String supplier = suppliers[i % suppliers.length];
            String type = types[i % types.length];
            String ip = ipBases[i % ipBases.length] + (100 + i);
            Integer port = 9000 + i;

            SignalControllerParamEntity entity = createSignalController(signalControllerId, supplier, type, ip, port);
            created.add(entity);

            // 避免时间戳重复
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("批量创建测试数据完成，数量: {}", created.size());
        return created;
    }

    /**
     * 更新信号机信息
     */
    @Transactional
    public SignalControllerParamEntity updateSignalController(String signalControllerId, String supplier,
                                                              String type, String ip, Integer port) {
        SignalControllerParamEntity entity = signalControllerRepository.findBySignalControllerId(signalControllerId);
        if (entity == null) {
            throw new RuntimeException("信号机不存在: " + signalControllerId);
        }

        entity.setSupplier(supplier);
        entity.setType(type);
        entity.setIp(ip);
        entity.setPort(port);
        entity.setUpdatedTime(LocalDateTime.now());

        signalControllerRepository.updateById(entity);

        log.info("更新信号机成功: {}", signalControllerId);
        return entity;
    }

    /**
     * 获取信号机信息
     */
    public SignalControllerParamEntity getSignalController(String signalControllerId) {
        return signalControllerRepository.findBySignalControllerId(signalControllerId);
    }

    /**
     * 删除信号机
     */
    @Transactional
    public void deleteSignalController(String signalControllerId) {
        SignalControllerParamEntity entity = signalControllerRepository.findBySignalControllerId(signalControllerId);
        if (entity != null) {
            signalControllerRepository.deleteById(entity.getSignalControllerId());
            log.info("删除信号机成功: {}", signalControllerId);
        } else {
            log.warn("要删除的信号机不存在: {}", signalControllerId);
        }
    }

    /**
     * 获取所有信号机
     */
    public List<SignalControllerParamEntity> getAllSignalControllers() {
        return signalControllerRepository.findAllWithCompleteInfo();
    }

    /**
     * 根据供应商获取信号机列表
     */
    public List<SignalControllerParamEntity> getSignalControllersBySupplier(String supplier) {
        return signalControllerRepository.findBySupplier(supplier);
    }

    /**
     * 根据类型获取信号机列表
     */
    public List<SignalControllerParamEntity> getSignalControllersByType(String type) {
        return signalControllerRepository.findByType(type);
    }

    /**
     * 根据路口ID获取信号机列表
     */
    public List<SignalControllerParamEntity> getSignalControllersByCrossId(String crossId) {
        return signalControllerRepository.findByCrossId(crossId);
    }

    /**
     * 根据系统ID获取信号机列表
     */
    public List<SignalControllerParamEntity> getSignalControllersBySystemId(String systemId) {
        return signalControllerRepository.findBySystemId(systemId);
    }

    /**
     * 根据IP地址获取信号机列表
     */
    public List<SignalControllerParamEntity> getSignalControllersByIp(String ip) {
        return signalControllerRepository.findByIp(ip);
    }

    /**
     * 验证信号机连接
     */
    public boolean testSignalControllerConnection(String signalControllerId) {
        SignalControllerParamEntity entity = signalControllerRepository.findBySignalControllerId(signalControllerId);
        if (entity == null) {
            log.warn("信号机不存在: {}", signalControllerId);
            return false;
        }

        // 这里可以实现实际的连接测试逻辑
        // 目前只是模拟测试
        if (entity.getIp() != null && entity.getPort() != null) {
            log.info("信号机连接测试成功: {} -> {}:{}", signalControllerId, entity.getIp(), entity.getPort());
            return true;
        } else {
            log.warn("信号机网络配置不完整: {}", signalControllerId);
            return false;
        }
    }

    /**
     * 清理测试数据
     */
    @Transactional
    public void cleanupTestData() {
        // 删除以测试前缀开头的数据
        List<SignalControllerParamEntity> allControllers = signalControllerRepository.findAllWithCompleteInfo();

        int deletedCount = 0;
        for (SignalControllerParamEntity controller : allControllers) {
            if (controller.getSignalControllerId().startsWith("TEST_") ||
                    controller.getSignalControllerId().startsWith("BATCH_TEST_") ||
                    controller.getSignalControllerId().startsWith("FULL_TEST_")) {
                signalControllerRepository.deleteById(controller.getSignalControllerId());
                deletedCount++;
            }
        }

        log.info("清理测试数据完成，删除数量: {}", deletedCount);
    }

    /**
     * 创建高级测试数据（使用完整实体对象）
     */
    @Transactional
    public List<SignalControllerParamEntity> createAdvancedTestDataWithEntities() {
        List<SignalControllerParamEntity> created = new ArrayList<>();

        // 创建海信信号机
//        SignalControllerParamEntity hisense = new SignalControllerParamEntity();
//        hisense.setSignalControllerId("ADV_HISENSE_ENTITY_001");
//        hisense.setSupplier("海信");
//        hisense.setType(1);
//        hisense.setIp("192.168.1.201");
//        hisense.setPort(9201);
//        hisense.setCrossIdList("[\"CROSS_MAIN_01\", \"CROSS_MAIN_02\"]");
//        hisense.setSystemIdList("[\"SYS_CENTRAL\"]");
//        hisense.setCrossCount(2);
//        hisense.setSystemCount(1);
//        hisense.setCreatedTime(LocalDateTime.now());
//        hisense.setUpdatedTime(LocalDateTime.now());
//        created.add(createSignalControllerWithEntity(hisense));
//
//        // 创建大华信号机
//        SignalControllerParamEntity dahua = new SignalControllerParamEntity();
//        dahua.setSignalControllerId("ADV_DAHUA_ENTITY_001");
//        dahua.setSupplier("大华");
//        dahua.setType(2);
//        dahua.setIp("192.168.1.202");
//        dahua.setPort(9202);
//        dahua.setCrossIdList("[\"CROSS_BRANCH_01\", \"CROSS_BRANCH_02\", \"CROSS_BRANCH_03\"]");
//        dahua.setSystemIdList("[\"SYS_BRANCH\", \"SYS_BACKUP\"]");
//        dahua.setCrossCount(3);
//        dahua.setSystemCount(2);
//        dahua.setCreatedTime(LocalDateTime.now());
//        dahua.setUpdatedTime(LocalDateTime.now());
//        created.add(createSignalControllerWithEntity(dahua));
//
//        // 创建华为信号机
//        SignalControllerParamEntity huawei = new SignalControllerParamEntity();
//        huawei.setSignalControllerId("ADV_HUAWEI_ENTITY_001");
//        huawei.setSupplier("华为");
//        huawei.setType(1);
//        huawei.setIp("192.168.1.203");
//        huawei.setPort(9203);
//        huawei.setCrossIdList("[\"CROSS_HIGHWAY_01\", \"CROSS_HIGHWAY_02\", \"CROSS_HIGHWAY_03\", \"CROSS_HIGHWAY_04\"]");
//        huawei.setSystemIdList("[\"SYS_HIGHWAY\", \"SYS_MONITOR\", \"SYS_CONTROL\"]");
//        huawei.setCrossCount(4);
//        huawei.setSystemCount(3);
//        huawei.setCreatedTime(LocalDateTime.now());
//        huawei.setUpdatedTime(LocalDateTime.now());
//        created.add(createSignalControllerWithEntity(huawei));

        log.info("创建高级测试数据（实体对象）完成，数量: {}", created.size());
        return created;
    }

    /**
     * 批量创建测试数据（使用完整实体对象）
     */
    @Transactional
    public List<SignalControllerParamEntity> createBatchTestDataWithEntities(int count) {
        List<SignalControllerParamEntity> created = new ArrayList<>();

//        String[] suppliers = {"海信", "大华", "华为", "信号技术", "智能交通"};
//        Integer[] types = {1, 2, 3};
//        String[] ipBases = {"192.168.1.", "192.168.2.", "10.0.0."};
//        String[][] crossGroups = {
//                {"CROSS_A01", "CROSS_A02"},
//                {"CROSS_B01", "CROSS_B02", "CROSS_B03"},
//                {"CROSS_C01"},
//                {"CROSS_D01", "CROSS_D02", "CROSS_D03", "CROSS_D04"},
//                {"CROSS_E01", "CROSS_E02"}
//        };
//        String[][] systemGroups = {
//                {"SYS_MAIN"},
//                {"SYS_BRANCH", "SYS_BACKUP"},
//                {"SYS_SINGLE"},
//                {"SYS_HIGHWAY", "SYS_MONITOR", "SYS_CONTROL"},
//                {"SYS_SMART", "SYS_AI"}
//        };
//
//        for (int i = 0; i < count; i++) {
//            SignalControllerParamEntity entity = new SignalControllerParamEntity();
//
//            entity.setSignalControllerId("BATCH_ENTITY_" + System.currentTimeMillis() + "_" + i);
//            entity.setSupplier(suppliers[i % suppliers.length]);
//            entity.setType(types[i % types.length]);
//            entity.setIp(ipBases[i % ipBases.length] + (200 + i));
//            entity.setPort(9200 + i);
//
//            // 设置路口列表
//            String[] crosses = crossGroups[i % crossGroups.length];
//            entity.setCrossIdList(Arrays.toString(crosses).replace(" ", ""));
//            entity.setCrossCount(crosses.length);
//
//            // 设置系统列表
//            String[] systems = systemGroups[i % systemGroups.length];
//            entity.setSystemIdList(Arrays.toString(systems).replace(" ", ""));
//            entity.setSystemCount(systems.length);
//
//            entity.setCreatedTime(LocalDateTime.now());
//            entity.setUpdatedTime(LocalDateTime.now());
//
//            created.add(createSignalControllerWithEntity(entity));
//
//            // 避免时间戳重复
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }

        log.info("批量创建测试数据（实体对象）完成，数量: {}", created.size());
        return created;
    }
}
