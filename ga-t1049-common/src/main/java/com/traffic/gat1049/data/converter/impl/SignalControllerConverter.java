package com.traffic.gat1049.data.converter.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.model.enums.CommMode;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.protocol.model.intersection.CommAddress;
import com.traffic.gat1049.protocol.model.intersection.CapabilitySet;
import com.traffic.gat1049.protocol.model.intersection.LampGroup;
import com.traffic.gat1049.protocol.model.intersection.Detector;
import com.traffic.gat1049.repository.entity.GatSignalControllerEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 完美匹配的信号控制器转换器实现
 * 实现GatSignalControllerEntity与SignalController之间的完美转换
 */
@Component
public class SignalControllerConverter extends AbstractEntityConverter<GatSignalControllerEntity, SignalController> {

    @Override
    public SignalController toProtocol(GatSignalControllerEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SignalController protocol = new SignalController();

            // ========================================================================
            // 直接匹配的字段
            // ========================================================================
            protocol.setSignalControllerID(entity.getSignalControllerId());
            protocol.setSupplier(entity.getSupplier());
            protocol.setType(entity.getDeviceType());
            protocol.setId(entity.getIdentifier());
            //protocol.setVersion(entity.getVersion());
            //protocol.setDescription(entity.getDescription());

            // ========================================================================
            // 通信配置字段
            // ========================================================================
            if (entity.getCommMode() != null) {
                protocol.setCommMode(CommMode.fromCode(String.valueOf(entity.getCommMode())));
            }
            protocol.setIp(entity.getIpAddress());
            protocol.setPort(entity.getPort());
            protocol.setSubMask(entity.getSubnetMask());
            protocol.setGateway(entity.getGateway());

            // ========================================================================
            // 位置信息
            // ========================================================================
            protocol.setLongitude(entity.getLongitude());
            protocol.setLatitude(entity.getLatitude());
            protocol.setHasDoorStatus(entity.getHasDoorStatus());

            // ========================================================================
            // 复杂对象字段转换
            // ========================================================================

            // 控制路口列表（必需字段）
            if (StringUtils.hasText(entity.getCrossIdList())) {
                List<String> crossIdList = deserializeFromJson(entity.getCrossIdList(),
                        new TypeReference<List<String>>() {});
                protocol.setCrossIDList(crossIdList);
            } else {
                // 如果为空，设置空列表而不是null
                protocol.setCrossIDList(new ArrayList<>());
            }

            // 灯组列表
            if (StringUtils.hasText(entity.getLampGroupList())) {
                List<LampGroup> lampGroupList = deserializeFromJson(entity.getLampGroupList(),
                        new TypeReference<List<LampGroup>>() {});
                protocol.setLampGroupList(lampGroupList);
            }

            // 检测器列表
            if (StringUtils.hasText(entity.getDetectorList())) {
                List<Detector> detectorList = deserializeFromJson(entity.getDetectorList(),
                        new TypeReference<List<Detector>>() {});
                protocol.setDetectorList(detectorList);
            }

            // 通信地址对象
            if (StringUtils.hasText(entity.getCommAddress())) {
                CommAddress commAddress = deserializeFromJson(entity.getCommAddress(), CommAddress.class);
                protocol.setCommAddress(commAddress);
            }

            // 设备能力集
            if (StringUtils.hasText(entity.getCapabilitySet())) {
                CapabilitySet capabilitySet = deserializeFromJson(entity.getCapabilitySet(), CapabilitySet.class);
                protocol.setCapabilitySet(capabilitySet);
            }

            // 自定义参数
            if (StringUtils.hasText(entity.getCustomParams())) {
                Map<String, Object> customParams = deserializeFromJson(entity.getCustomParams(),
                        new TypeReference<Map<String, Object>>() {});
                protocol.setCustomParams(customParams);
            }

            validateConversion(entity, protocol);

            logger.debug("实体转协议成功: {}", entity.getSignalControllerId());
            return protocol;

        } catch (Exception e) {
            logger.error("信号控制器实体转协议失败: {}", entity.getSignalControllerId(), e);
            throw new DataConversionException("信号控制器转换失败", e);
        }
    }

    @Override
    public GatSignalControllerEntity toEntity(SignalController protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            GatSignalControllerEntity entity = new GatSignalControllerEntity();

            // ========================================================================
            // 直接匹配的字段
            // ========================================================================
            entity.setSignalControllerId(protocol.getSignalControllerID());
            entity.setSupplier(protocol.getSupplier());
            entity.setDeviceType(protocol.getType());
            entity.setIdentifier(protocol.getId());
            entity.setVersion(protocol.getVersion());
            entity.setDescription(protocol.getDescription());

            // ========================================================================
            // 通信配置字段
            // ========================================================================
            if (protocol.getCommMode() != null) {
                entity.setCommMode(protocol.getCommMode().getValue());
            }
            entity.setIpAddress(protocol.getIp());
            entity.setPort(protocol.getPort());
            entity.setSubnetMask(protocol.getSubMask());
            entity.setGateway(protocol.getGateway());

            // ========================================================================
            // 位置信息
            // ========================================================================
            entity.setLongitude(protocol.getLongitude());
            entity.setLatitude(protocol.getLatitude());
            entity.setHasDoorStatus(protocol.getHasDoorStatus());

            // ========================================================================
            // 复杂对象字段转换
            // ========================================================================

            // 控制路口列表（必需字段）
            if (protocol.getCrossIDList() != null) {
                entity.setCrossIdList(serializeToJson(protocol.getCrossIDList()));
            } else {
                // 设置空数组而不是null
                entity.setCrossIdList("[]");
            }

            // 灯组列表
            if (protocol.getLampGroupList() != null) {
                entity.setLampGroupList(serializeToJson(protocol.getLampGroupList()));
            }

            // 检测器列表
            if (protocol.getDetectorList() != null) {
                entity.setDetectorList(serializeToJson(protocol.getDetectorList()));
            }

            // 通信地址对象
            if (protocol.getCommAddress() != null) {
                entity.setCommAddress(serializeToJson(protocol.getCommAddress()));
            }

            // 设备能力集
            if (protocol.getCapabilitySet() != null) {
                entity.setCapabilitySet(serializeToJson(protocol.getCapabilitySet()));
            }

            // 自定义参数
            if (protocol.getCustomParams() != null) {
                entity.setCustomParams(serializeToJson(protocol.getCustomParams()));
            }

            // ========================================================================
            // 设置管理字段的默认值
            // ========================================================================

            // 如果描述信息为空，使用信号机ID作为名称
            if (!StringUtils.hasText(entity.getControllerName())) {
                entity.setControllerName(protocol.getSignalControllerID());
            }

            // 设置默认状态
            entity.setDeviceStatus(0); // 默认离线
            entity.setConnectionStatus(0); // 默认未连接
            entity.setSyncStatus(0); // 默认未同步
            entity.setStatus(1); // 默认启用

            // 设置时间戳
            LocalDateTime now = LocalDateTime.now();
            if (entity.getId() == null) {
                entity.setCreatedAt(now);
            }
            entity.setUpdatedAt(now);

            validateConversion(entity, protocol);

            logger.debug("协议转实体成功: {}", protocol.getSignalControllerID());
            return entity;

        } catch (Exception e) {
            logger.error("信号控制器协议转实体失败: {}", protocol.getSignalControllerID(), e);
            throw new DataConversionException("信号控制器转换失败", e);
        }
    }

    @Override
    public void updateEntity(SignalController protocol, GatSignalControllerEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // ========================================================================
            // 基础字段更新（非空才更新）
            // ========================================================================
            if (StringUtils.hasText(protocol.getSupplier())) {
                entity.setSupplier(protocol.getSupplier());
            }
            if (StringUtils.hasText(protocol.getType())) {
                entity.setDeviceType(protocol.getType());
            }
            if (StringUtils.hasText(protocol.getId())) {
                entity.setIdentifier(protocol.getId());
            }
            if (StringUtils.hasText(protocol.getVersion())) {
                entity.setVersion(protocol.getVersion());
            }
            if (StringUtils.hasText(protocol.getDescription())) {
                entity.setDescription(protocol.getDescription());
            }

            // ========================================================================
            // 通信配置更新
            // ========================================================================
            if (protocol.getCommMode() != null) {
                entity.setCommMode(protocol.getCommMode().getValue());
            }
            if (StringUtils.hasText(protocol.getIp())) {
                entity.setIpAddress(protocol.getIp());
            }
            if (protocol.getPort() != null) {
                entity.setPort(protocol.getPort());
            }
            if (StringUtils.hasText(protocol.getSubMask())) {
                entity.setSubnetMask(protocol.getSubMask());
            }
            if (StringUtils.hasText(protocol.getGateway())) {
                entity.setGateway(protocol.getGateway());
            }

            // ========================================================================
            // 位置信息更新
            // ========================================================================
            if (protocol.getLongitude() != null) {
                entity.setLongitude(protocol.getLongitude());
            }
            if (protocol.getLatitude() != null) {
                entity.setLatitude(protocol.getLatitude());
            }
            if (protocol.getHasDoorStatus() != null) {
                entity.setHasDoorStatus(protocol.getHasDoorStatus());
            }

            // ========================================================================
            // 复杂对象更新
            // ========================================================================
            if (protocol.getCrossIDList() != null) {
                entity.setCrossIdList(serializeToJson(protocol.getCrossIDList()));
            }
            if (protocol.getLampGroupList() != null) {
                entity.setLampGroupList(serializeToJson(protocol.getLampGroupList()));
            }
            if (protocol.getDetectorList() != null) {
                entity.setDetectorList(serializeToJson(protocol.getDetectorList()));
            }
            if (protocol.getCommAddress() != null) {
                entity.setCommAddress(serializeToJson(protocol.getCommAddress()));
            }
            if (protocol.getCapabilitySet() != null) {
                entity.setCapabilitySet(serializeToJson(protocol.getCapabilitySet()));
            }
            if (protocol.getCustomParams() != null) {
                entity.setCustomParams(serializeToJson(protocol.getCustomParams()));
            }

            // 更新时间戳和配置版本
            entity.setUpdatedAt(LocalDateTime.now());
            // 配置版本会在@PreUpdate中自动增加

            logger.debug("实体部分更新成功: {}", entity.getSignalControllerId());

        } catch (Exception e) {
            logger.error("信号控制器实体更新失败: {}", entity.getSignalControllerId(), e);
            throw new DataConversionException("信号控制器更新失败", e);
        }
    }

    // ========================================================================
    // 辅助方法
    // ========================================================================

    /**
     * 安全的JSON反序列化（支持泛型）
     */
    protected <T> T deserializeFromJson(String json, TypeReference<T> typeRef) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            logger.error("JSON反序列化失败: {}", json, e);
            throw new DataConversionException("JSON反序列化失败", e);
        }
    }

    /**
     * 验证转换结果的业务逻辑
     */
    @Override
    protected void validateConversion(GatSignalControllerEntity entity, SignalController protocol) {
        super.validateConversion(entity, protocol);

        // 业务逻辑验证
        if (!StringUtils.hasText(protocol.getSignalControllerID())) {
            throw new DataConversionException("信号控制器ID不能为空");
        }

        if (!StringUtils.hasText(protocol.getSupplier())) {
            throw new DataConversionException("供应商信息不能为空");
        }

        if (!StringUtils.hasText(protocol.getType())) {
            throw new DataConversionException("设备型号不能为空");
        }

        if (!StringUtils.hasText(protocol.getId())) {
            throw new DataConversionException("识别码不能为空");
        }

        // 验证ID格式（GA/T 1049标准要求）
        if (!protocol.getSignalControllerID().matches("\\d{6}99\\d{4}")) {
            throw new DataConversionException("信号控制器ID格式不符合GA/T 1049标准");
        }

        // 验证控制路口列表（必需字段）
        if (protocol.getCrossIDList() == null || protocol.getCrossIDList().isEmpty()) {
            throw new DataConversionException("控制路口列表不能为空");
        }

        // 验证网络配置的一致性
        if (protocol.getCommMode() != null && protocol.getCommMode() != CommMode.SERIAL) {
            if (StringUtils.hasText(protocol.getIp()) && protocol.getPort() == null) {
                throw new DataConversionException("指定IP地址时必须指定端口号");
            }
            if (!StringUtils.hasText(protocol.getIp()) && protocol.getPort() != null) {
                throw new DataConversionException("指定端口号时必须指定IP地址");
            }
        }
    }

    /**
     * 创建示例SignalController对象（用于测试）
     */
    public SignalController createSampleProtocol() {
        SignalController sample = new SignalController();
        sample.setSignalControllerID("11010099001");
        sample.setSupplier("海信网络科技股份有限公司");
        sample.setType("HiSico-TCS100");
        sample.setId("TSC001");
        sample.setVersion("2.1.0");
        sample.setDescription("北京市朝阳区主要路口信号机");
        sample.setCommMode(CommMode.TCP_CLIENT);
        sample.setIp("192.168.1.100");
        sample.setPort(8080);
        sample.setSubMask("255.255.255.0");
        sample.setGateway("192.168.1.1");
        sample.setLongitude(116.397128);
        sample.setLatitude(39.916527);
        sample.setHasDoorStatus(1);

        List<String> crossIds = new ArrayList<>();
        crossIds.add("11010001");
        crossIds.add("11010002");
        sample.setCrossIDList(crossIds);

        return sample;
    }

    /**
     * 创建示例Entity对象（用于测试）
     */
    public GatSignalControllerEntity createSampleEntity() {
        GatSignalControllerEntity sample = new GatSignalControllerEntity();
        sample.setSignalControllerId("11010099001");
        sample.setSupplier("海信网络科技股份有限公司");
        sample.setDeviceType("HiSico-TCS100");
        sample.setIdentifier("TSC001");
        sample.setVersion("2.1.0");
        sample.setDescription("北京市朝阳区主要路口信号机");
        sample.setCommMode(1); // TCP_CLIENT
        sample.setIpAddress("192.168.1.100");
        sample.setPort(8080);
        sample.setSubnetMask("255.255.255.0");
        sample.setGateway("192.168.1.1");
        sample.setLongitude(116.397128);
        sample.setLatitude(39.916527);
        sample.setHasDoorStatus(1);
        sample.setCrossIdList("[\"11010001\",\"11010002\"]");
        sample.setControllerName("朝阳路-建国路交叉口");
        sample.setAdapterType("HISENSE");

        return sample;
    }
}