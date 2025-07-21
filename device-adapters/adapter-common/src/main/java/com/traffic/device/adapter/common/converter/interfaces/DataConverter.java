package com.traffic.device.adapter.common.converter.interfaces;

import com.traffic.gat1049.device.adapter.exception.DataConversionException;
import com.traffic.gat1049.device.adapter.model.*;
import java.util.Map;

/**
 * 通用数据转换器接口
 * 定义设备数据转换的标准方法
 */
public interface DataConverter {

    /**
     * 将设备原始数据转换为标准设备状态
     * @param rawData 设备原始数据
     * @param controllerId 控制器ID
     * @return 标准设备状态数据
     */
    DeviceStatusData convertToDeviceStatus(Object rawData, String controllerId) throws DataConversionException;

    /**
     * 将设备原始数据转换为实时数据
     * @param rawData 设备原始数据
     * @param controllerId 控制器ID
     * @return 设备实时数据
     */
    DeviceRuntimeData convertToRuntimeData(Object rawData, String controllerId) throws DataConversionException;

    /**
     * 将标准配置数据转换为设备特定格式
     * @param configData 标准配置数据
     * @return 设备特定配置数据
     */
    Object convertFromConfigData(DeviceConfigData configData);

    /**
     * 将设备响应转换为命令结果
     * @param response 设备响应
     * @param command 原始命令
     * @return 命令执行结果
     */
    CommandResult convertToCommandResult(Object response, DeviceCommand command) throws DataConversionException;

    /**
     * 验证数据完整性
     * @param data 待验证数据
     * @return 验证结果
     */
    boolean validateData(Object data);

    /**
     * 提取错误信息
     * @param errorData 错误数据
     * @return 错误信息
     */
    String extractErrorMessage(Object errorData);
}
