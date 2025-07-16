package com.traffic.gat1049.repository.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 信号组参数DTO - 包含关联灯组信息
 * 用于Repository层返回组合查询结果
 */
@Data
@Accessors(chain = true)
public class SignalGroupWithLampGroupsDto {

    // 信号组基础信息
    private String crossId;
    private Integer signalGroupNo;
    private String name;
    private Integer type;
    private Integer greenFlashLen;
    private Integer maxGreen;
    private Integer minGreen;

    // 关联的灯组信息（来自JOIN查询）
    private String lampGroupNos; // GROUP_CONCAT结果：逗号分隔字符串 "1,2,3"

    // 可选的扩展字段
    private Integer lampGroupCount;
    private LocalDateTime lastUpdated;

    /**
     * 解析灯组编号字符串为列表
     */
    public List<Integer> getLampGroupNoList() {
        List<Integer> result = new ArrayList<>();
        if (lampGroupNos != null && !lampGroupNos.trim().isEmpty()) {
            String[] parts = lampGroupNos.split(",");
            for (String part : parts) {
                try {
                    result.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                    // 忽略无效数据
                }
            }
        }
        return result;
    }

    /**
     * 设置灯组编号列表（转换为字符串）
     */
    public void setLampGroupNoList(List<Integer> lampGroupNoList) {
        if (lampGroupNoList == null || lampGroupNoList.isEmpty()) {
            this.lampGroupNos = null;
        } else {
            this.lampGroupNos = String.join(",",
                    lampGroupNoList.stream()
                            .map(String::valueOf)
                            .toArray(String[]::new));
        }
    }

    /**
     * 获取信号组类型描述
     */
    public String getTypeDescription() {
        if (type == null) return "未知";

        switch (type) {
            case 0: return "行人信号组";
            case 1: return "机动车直行";
            case 21: return "机动车左转";
            case 22: return "机动车右转";
            case 23: return "机动车掉头";
            default: return "其他类型(" + type + ")";
        }
    }
}
