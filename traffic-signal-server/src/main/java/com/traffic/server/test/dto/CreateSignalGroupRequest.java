package com.traffic.server.test.dto;

import java.util.List;

/**
 * 创建信号组请求对象
 * 更明确的API设计
 */
public class CreateSignalGroupRequest {
    private String crossId;
    private Integer signalGroupNo;
    private String name;
    private Integer type;
    private Integer greenFlashLen;
    private Integer maxGreen;
    private Integer minGreen;
    private List<Integer> lampGroupNoList;

    // 构造函数
    public CreateSignalGroupRequest() {}

    public CreateSignalGroupRequest(String crossId, Integer signalGroupNo, String name, List<Integer> lampGroupNoList) {
        this.crossId = crossId;
        this.signalGroupNo = signalGroupNo;
        this.name = name;
        this.lampGroupNoList = lampGroupNoList;
    }

    // Getters and Setters
    public String getCrossId() { return crossId; }
    public void setCrossId(String crossId) { this.crossId = crossId; }

    public Integer getSignalGroupNo() { return signalGroupNo; }
    public void setSignalGroupNo(Integer signalGroupNo) { this.signalGroupNo = signalGroupNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Integer getGreenFlashLen() { return greenFlashLen; }
    public void setGreenFlashLen(Integer greenFlashLen) { this.greenFlashLen = greenFlashLen; }

    public Integer getMaxGreen() { return maxGreen; }
    public void setMaxGreen(Integer maxGreen) { this.maxGreen = maxGreen; }

    public Integer getMinGreen() { return minGreen; }
    public void setMinGreen(Integer minGreen) { this.minGreen = minGreen; }

    public List<Integer> getLampGroupNoList() { return lampGroupNoList; }
    public void setLampGroupNoList(List<Integer> lampGroupNoList) { this.lampGroupNoList = lampGroupNoList; }

    @Override
    public String toString() {
        return "CreateSignalGroupRequest{" +
                "crossId='" + crossId + '\'' +
                ", signalGroupNo=" + signalGroupNo +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", greenFlashLen=" + greenFlashLen +
                ", maxGreen=" + maxGreen +
                ", minGreen=" + minGreen +
                ", lampGroupNoList=" + lampGroupNoList +
                '}';
    }
}
