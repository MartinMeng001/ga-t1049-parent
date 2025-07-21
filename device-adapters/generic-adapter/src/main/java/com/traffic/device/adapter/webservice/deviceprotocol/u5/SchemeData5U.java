package com.traffic.device.adapter.webservice.deviceprotocol.u5;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
public class SchemeData5U {
    private String crossId;
    private int SIGID;
    private int SCHEMEID;
    private int CTRLMODE;
    private int cycle;
    private List<StageData5U> signalscheme = new ArrayList<>();
    private List<Integer> stageList = null;
    private boolean beUpdated=false;
    private final BasicPhaseTable basicPhaseTable;

    public SchemeData5U(BasicPhaseTable basicPhaseTable){
        this.basicPhaseTable = basicPhaseTable;
    }
    public void setSchemeInfo(JsonNode schemeobj, String crossId){
        if("ok".equals(schemeobj.get("success").asText())==false) return;
        this.crossId = crossId;
        beUpdated=false;
        cycle = schemeobj.get("cycle").asInt();
        JsonNode obj = schemeobj.get("resultA");
        ArrayNode stageArray = (ArrayNode) obj.get("PhaseList");
        SCHEMEID = obj.get("SchemeNo").asInt();
        CTRLMODE = obj.get("ControlMode").asInt();
        signalscheme.clear();
        for(int i = 0; i<stageArray.size(); i++){
            JsonNode stageObj = stageArray.get(i);
            StageData5U stage = new StageData5U(basicPhaseTable);
            stage.setStageData(i+1, stageObj);

            Set<Integer> phaseset = stage.getLightstatus().getStageSet();
            int stageno = BasicStageTable.getStageNo5U(phaseset);
            stage.setStageidStd(stageno);
            stage.setStageidZdk(100*SCHEMEID+i+1);

            signalscheme.add(stage);
        }
    }
    public void setSchemeInfoSensor(JsonNode schemeobj){
        if("ok".equals(schemeobj.get("success"))==false) return;
//        cycle = schemeobj.getInteger("cycle");
        JsonNode obj = schemeobj.get("resultA");
        ArrayNode stageArray = (ArrayNode)obj.get("PhaseList");
//        SCHEMEID = obj.getInteger("SchemeNo");
//        CTRLMODE = obj.getInteger("ControlMode");
//        signalscheme.clear();
        for(int i=0;i<stageArray.size();i++){
            JsonNode stageObj = stageArray.get(i);
            StageData5U stage5U = signalscheme.get(i);//
            stage5U.setStageDataSensor(i+1, stageObj);
            signalscheme.set(i, stage5U);
        }
    }
    public void setSchemeInfoSensorCommon(){
        for(int i=0;i<signalscheme.size();i++){
            StageData5U stage5U = signalscheme.get(i);
            stage5U.setStageDataSensorCommon(i+1);
            signalscheme.set(i,stage5U);
        }
    }
//    public int updateTemporaySchemeParam(SDOTemporaryControlPlan controlPlan){
//        int coordStage = controlPlan.getCoordStageNo();
//        int phasediff = controlPlan.getOffSet();
//        if(controlPlan.getStageNoList().size()!=signalscheme.size()) return 0;
//        for(int i=0;i<signalscheme.size();i++){
//            StageData5U stage = signalscheme.get(i);
//            SDOStageItem tempstage = controlPlan.getStageNoList().get(i);
//            stage.setGreentimeAllZDK2(tempstage.getStageTime());
//            if(coordStage == stage.getStageidZdk()){
//                stage.setIscoor(1);
//                stage.setCoortime(phasediff);
//            }else {
//                stage.setIscoor(0);
//                stage.setCoortime(0);
//            }
//            signalscheme.set(i, stage);
//        }
//        beUpdated=true;
//        return 1;
//    }
//    public int updateSchemeParam(SDOStageParam stage){
//        int stageNo = (stage.getStageNo()%100);
//        if(stageNo<=0) return 0;
//        //System.out.println("[DEBUG]stageNo="+stageNo);
//        StageData5U stage5U = signalscheme.get(stageNo-1);
//        if(stage.getGreen()==stage5U.getGreentimeAllZDK()) return 0;
//        //System.out.println("[DEBUG]greentime changed, from "+stage5U.getGreentimeAllZDK()+" to "+stage.getGreen());
//        stage5U.setGreentimeAllZDK(stage.getGreen());
//        signalscheme.set(stageNo-1, stage5U);
//        beUpdated=true;
//        return 1;
//    }
//    public int updateSchemeParam(SDOPlanParam plan){
//        int stageNo = (plan.getCoordStageNo()%100);
//        if(stageNo<=0) return 0;
//        StageData5U stage5U = signalscheme.get(stageNo-1);
//        stageList = plan.getStageNoChain();
//        if(stage5U.getIscoor()!=1){
//            for(int i=0;i<signalscheme.size();i++){
//                StageData5U stage = signalscheme.get(i);
//                if(stage.getStageid()==stageNo){
//                    stage.setIscoor(1);
//                    stage.setCoortime(plan.getOffSet());
//                    //System.out.println("[DEBUG]coordtime changed, from "+stage5U.getCoortime()+" to "+plan.getOffSet()+" stageNo="+stageNo);
//                }else stage.setIscoor(0);
//                signalscheme.set(i, stage);
//            }
//            beUpdated=true;
//            return 1;
//        }
//        if(stage5U.getCoortime()==plan.getOffSet()) return 0;
//        //System.out.println("[DEBUG]coordtime changed, from "+stage5U.getCoortime()+" to "+plan.getOffSet());
//        stage5U.setCoortime(plan.getOffSet());
//        signalscheme.set(stageNo-1, stage5U);
//        beUpdated=true;
//        return 1;
//    }
    public JsonNode toSchemeParam(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ArrayNode array = mapper.createArrayNode();
        if(stageList==null) {
            for (int i = 0; i < signalscheme.size(); i++) {
                array.add(signalscheme.get(i).toSchemeParam(CTRLMODE));
            }
        }else if(stageList.size() != signalscheme.size()){
            for (int i = 0; i < signalscheme.size(); i++) {
                array.add(signalscheme.get(i).toSchemeParam(CTRLMODE));
            }
        }else{
            for(int i=0;i<stageList.size();i++){
                int no = (stageList.get(i)%100);
                if(no<=0) return null;
                array.add(signalscheme.get(no-1).toSchemeParam(CTRLMODE));
            }
        }
        ret.put("signalscheme", array);
        ret.put("SCHEMEID", SCHEMEID);
        ret.put("CTRLMODE", CTRLMODE);
        ret.put("SIGID", SIGID);
        return ret;
    }
    public JsonNode toTempSchemeParam(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ArrayNode array = mapper.createArrayNode();
        if(stageList==null) {
            for (int i = 0; i < signalscheme.size(); i++) {
                array.add(signalscheme.get(i).toSchemeParam(CTRLMODE));
            }
        }else if(stageList.size() != signalscheme.size()){
            for (int i = 0; i < signalscheme.size(); i++) {
                array.add(signalscheme.get(i).toSchemeParam(CTRLMODE));
            }
        }else{
            for(int i=0;i<stageList.size();i++){
                int no = (stageList.get(i)%100);
                if(no<=0) return null;
                array.add(signalscheme.get(no-1).toSchemeParam(CTRLMODE));
            }
        }
        ret.put("signalscheme", array);
        ret.put("SCHEMEID", 17);
        ret.put("CTRLMODE", 3);
        ret.put("SIGID", SIGID);
        return ret;
    }
    public List<Integer> getStageList(){
        List<Integer> ret = new ArrayList<>();
        if(signalscheme==null) return ret;
        for(int i=0;i<signalscheme.size();i++){
            Set<Integer> phaseset = signalscheme.get(i).getLightstatus().getStageSet();
            int stageno = BasicStageTable.getStageNo5U(phaseset);
            if(stageno==0) return null;
            ret.add(stageno);
        }
        return ret;
    }

    public String getStageListGAT1049(){
        String ret = "";
        if(signalscheme==null) return ret;
        for(int i=0;i<signalscheme.size();i++){
            //Set<Integer> phaseset = signalscheme.get(i).getLightstatus().getStageSet();
            int stageno = signalscheme.get(i).getStageidStd();//BasicStageTable.getStageNo5U(phaseset);
            if(stageno==0) return null;
            if(i==(signalscheme.size()-1)) ret+=stageno;
            else ret+=stageno+"-";
        }
        return ret;
    }
    //
    public List<Integer> getStageListZDKGAT1049(){
        List<Integer> ret = new ArrayList<>();
        if(signalscheme==null) return ret;
        for(int i=0;i<signalscheme.size();i++){
            //Set<Integer> phaseset = signalscheme.get(i).getLightstatus().getStageSet();
            int stageno = signalscheme.get(i).getStageidZdk();
            if(stageno==0) return null;
            ret.add(stageno);
        }
        return ret;
    }
    public ArrayNode getStageDataListZDK1049(){
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode ret = mapper.createArrayNode();
        if(signalscheme==null) return ret;
        for(int i=0;i<signalscheme.size();i++){
            JsonNode obj = signalscheme.get(i).getStageItemGAT1049();
            ret.add(obj);
        }
        return ret;
    }
    public List<Integer> getStageTimeList(){
        List<Integer> ret = new ArrayList<>();
        if(signalscheme==null) return ret;
        for(int i=0;i<signalscheme.size();i++){
            int greentime = signalscheme.get(i).getGreentimeAllZDK();
            ret.add(greentime);
        }
        return ret;
    }
    public String getStageTimeListGAT1049(){
        String ret = "";
        if(signalscheme==null) return ret;
        for(int i=0;i<signalscheme.size();i++){
            int greentime = signalscheme.get(i).getGreentimeAll();
            if(i==(signalscheme.size()-1))ret+=greentime;
            else ret+=greentime+"-";
        }
        return ret;
    }
//    public int setStageListGAT1049(String stagelist){
//        String[] stageLst = stagelist.split("-");
//        if(stageLst==null) return 0;    // 0是没有下发，且缓存数据没有修改，可不重新读取方案，如小于0，则需要重新读取方案
//        if(stageLst.length==0) return 0;
//        for(int i=0;i<stageLst.length;i++){
//            int stage = Integer.parseInt(stageLst[i]);
//        }
//    }
//    public int setStageTimeListGAT1049(String stagetimelist){
//
//    }
//    public int setCoordStageGAT1049(int coordStage, int offset){
//
//    }
}
