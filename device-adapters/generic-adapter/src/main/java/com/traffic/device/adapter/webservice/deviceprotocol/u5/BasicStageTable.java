package com.traffic.device.adapter.webservice.deviceprotocol.u5;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/*
阶段1：北向全放 相位1，相位2，相位16，相位3，相位7，相位11，相位15
阶段2：东向全放 相位5，相位6，相位4，相位3，相位7，相位11，相位15
阶段3：南向全放 相位9，相位10，相位8，相位3，相位7，相位11，相位15
阶段4：西向全放 相位13，相位14，相位12，相位3，相位7，相位11，相位15
阶段5：南北直行 相位1，相位9，相位16，相位8，相位3，相位7，相位11，相位15
阶段6：东西直行 相位5，相位13，相位4，相位12，相位3，相位7，相位11，相位15
阶段7：南北左转 相位2，相位10，相位3，相位7，相位11，相位15
阶段8：东西左转 相位6，相位14，相位3，相位7，相位11，相位15
阶段9，南北全放 相位1，相位2，相位16，相位9，相位10，相位8，相位3，相位7，相位11，相位15
阶段10，东西全放 相位5，相位6，相位4，相位13，相位14，相位12，相位3，相位7，相位11，相位15
 */
public class BasicStageTable {
    public static Set<Integer> NorthAll = new HashSet<>(Arrays.asList(1,2,16,3,7,11,15));
    public static Set<Integer> EastAll = new HashSet<>(Arrays.asList(5,6,4,3,7,11,15));
    public static Set<Integer> SouthAll = new HashSet<>(Arrays.asList(9,10,8,3,7,11,15));
    public static Set<Integer> WestAll = new HashSet<>(Arrays.asList(13,14,12,3,7,11,15));
    public static Set<Integer> SouthNorthGo = new HashSet<>(Arrays.asList(1,9,16,8,3,7,11,15));
    public static Set<Integer> EastWestGo = new HashSet<>(Arrays.asList(5,13,4,12,3,7,11,15));
    public static Set<Integer> SouthNorthLeft = new HashSet<>(Arrays.asList(2,10,3,7,11,15));
    public static Set<Integer> EastWestLeft = new HashSet<>(Arrays.asList(6,14,3,7,11,15));
    public static Set<Integer> SouthNorthAll = new HashSet<>(Arrays.asList(1,2,16,9,10,8,3,7,11,15));
    public static Set<Integer> EastWestAll = new HashSet<>(Arrays.asList(5,6,4,13,14,12,3,7,11,15));
    public static List<Set<Integer>> nonStdStageLst = new ArrayList<>();
    public static ArrayNode getStageParamsGAT1049(){
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.createArrayNode();
        array.add(getObjectItemGAT1049(1, NorthAll));
        array.add(getObjectItemGAT1049(2, EastAll));
        array.add(getObjectItemGAT1049(3, SouthAll));
        array.add(getObjectItemGAT1049(4, WestAll));
        array.add(getObjectItemGAT1049(5, SouthNorthGo));
        array.add(getObjectItemGAT1049(6, EastWestGo));
        array.add(getObjectItemGAT1049(7, SouthNorthLeft));
        array.add(getObjectItemGAT1049(8, EastWestLeft));
        array.add(getObjectItemGAT1049(9, SouthNorthAll));
        array.add(getObjectItemGAT1049(10, EastWestAll));
        for(int i=0;i<nonStdStageLst.size();i++){
            array.add(getObjectItemGAT1049(11+i, nonStdStageLst.get(i)));
        }
        return array;
    }
    public static String checkSimilarWithStd(Set<Integer> phases){
        if(NorthAll.containsAll(phases)) return "非标准"+getStageName(1);
        if(EastAll.containsAll(phases)) return "非标准"+getStageName(2);
        if(SouthAll.containsAll(phases)) return "非标准"+getStageName(3);
        if(WestAll.containsAll(phases)) return "非标准"+getStageName(4);
        if(SouthNorthGo.containsAll(phases)) return "非标准"+getStageName(5);
        if(EastWestGo.containsAll(phases)) return "非标准"+getStageName(6);
        if(SouthNorthLeft.containsAll(phases)) return "非标准"+getStageName(7);
        if(EastWestLeft.containsAll(phases)) return "非标准"+getStageName(8);
        if(SouthNorthAll.containsAll(phases)) return "非标准"+getStageName(9);
        if(EastWestAll.containsAll(phases)) return "非标准"+getStageName(10);
        return "未定义阶段";
    }
    protected static JsonNode getObjectItemGAT1049(int stageNo, Set<Integer> phases){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ret.put("StageNo", stageNo);
        if(stageNo>10)ret.put("StageName", checkSimilarWithStd(phases));
        else ret.put("StageName", getStageName(stageNo));
        ret.put("Attribute", 0);
        ret.put("PhaseNoList", mapper.valueToTree(phases));
        return ret;
    }
    public static int getStageNo5U(Set<Integer> stage){
        if(NorthAll.equals(stage)) return 1;
        if(EastAll.equals(stage)) return 2;
        if(SouthAll.equals(stage)) return 3;
        if(WestAll.equals(stage)) return 4;
        if(SouthNorthGo.equals(stage)) return 5;
        if(EastWestGo.equals(stage)) return 6;
        if(SouthNorthLeft.equals(stage)) return 7;
        if(EastWestLeft.equals(stage)) return 8;
        if(SouthNorthAll.equals(stage)) return 9;
        if(EastWestAll.equals(stage)) return 10;
        for(int i=0;i<nonStdStageLst.size();i++){
            if(nonStdStageLst.get(i).equals(stage)){
                return 11+i;
            }
        }
        System.out.println("[BasicStageTable] unknownStage << "+stage);
        nonStdStageLst.add(stage);
        return 0;
    }
    public static String getStageName(int stageNo){
        switch (stageNo){
            case 1: return "北向全放";
            case 2: return "东向全放";
            case 3: return "南向全放";
            case 4: return "西向全放";
            case 5: return "南北直行";
            case 6: return "东西直行";
            case 7: return "南北左转";
            case 8: return "东西左转";
            case 9: return "南北全放";
            case 10: return "东西全放";
        }
        return "未定义阶段";
    }
}
