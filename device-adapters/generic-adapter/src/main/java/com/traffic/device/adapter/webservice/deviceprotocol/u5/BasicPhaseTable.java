package com.traffic.device.adapter.webservice.deviceprotocol.u5;

import com.fasterxml.jackson.databind.JsonNode;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
基本相位定义：
相位1 北直 | 相位2 北左 | 相位3 北右 | 相位4 北人 |
相位5 东直 | 相位6 东左 | 相位7 东右 | 相位8 东人 |
相位9 南直 | 相位10 南左| 相位11 南右| 相位12 南人|
相位13 西直| 相位14 西左| 相位15 西右| 相位16 西人|
 */
public class BasicPhaseTable {
    private List<LampGroupParam> allLamps = null;

    public void setAllLamps(List<LampGroupParam> allLamps) {
        this.allLamps = allLamps;
    }
    protected int getLightGroupNoByDirectionFlow(String direction, String flow) throws Exception {
        Direction direction5U = Direction.from5UDirection(direction);
        LampGroupType lampGroupType5U = LampGroupType.from5UType(flow);
        LampGroupParam lampGroupParam = findLampGroupByDirection(direction5U, lampGroupType5U);
        if(lampGroupParam==null) return 0;
        return lampGroupParam.getLampGroupNo();
    }
    public void parseDirection(String direction, Set<Integer> lamps, LightGroupItem data){
        try {
            parseFlow(direction, "Straight", lamps, data.getGo());
            parseFlow(direction, "TurnLeft", lamps, data.getLeft());
            parseFlow(direction, "TurnRight", lamps, data.getRight());
            parseFlow(direction, "Sidewalk", lamps, data.getPerson1());
            parseFlow(direction, "NonMotorized", lamps, data.getBicycle());
            parseFlow(direction, "TurnRound", lamps, data.getTurnback());
            parseFlow(direction, "Extend1", lamps, data.getPerson2());
            parseFlow(direction, "Extend2", lamps, data.getExtend());

        }catch (Exception e){e.printStackTrace();}
    }
    protected void parseFlow(String direction, String flow, Set<Integer> lamps, String data) throws Exception {
        if(!"Red".equals(data)){
            lamps.add(getLightGroupNoByDirectionFlow(direction, flow));
        }
    }
    protected LampGroupParam findLampGroupByDirection(Direction direction, LampGroupType flow) throws Exception {
        if (allLamps == null) {
            return null; // 或者抛出 IllegalArgumentException，取决于你的错误处理策略
        }

        Optional<LampGroupParam> result = allLamps.stream()
                .filter(lamp -> lamp.matchesDirectionAndType(direction, flow)) // 过滤出匹配的对象
                .findFirst(); // 获取第一个匹配的对象

        return result.orElse(null); // 如果找到了则返回对象，否则返回 null
    }
}
