package com.traffic.device.adapter.webservice.deviceprotocol.u5;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
public class StageData5U {
    private int phaseno;
    private int stageid;
    private int stageidStd;
    private int stageidZdk;
    private int greentime;
    private int greenflashtime;
    private int mingreen;
    private int maxgreen;
    private int senseinterval;
    private int phaseAdjustStep;
    private int maxcount;
    private int isMain;
    private int yellowtime;
    private int yellowflashtime;
    private int redyellowHintTime;
    private int allredtime;
    private int pedgreen;
    private int pedgreenflash;
    private int pedearlyend;
    private int peddalay;
    private int noCarTimeInterval;
    private int personbutton;
    private int iscoor;
    private int coortime;
    private LightStatus5U lightstatus;

    public StageData5U(BasicPhaseTable basicPhaseTable){
        lightstatus = new LightStatus5U(basicPhaseTable);
    }
    public void setStageData(int phase, JsonNode obj){
        if(lightstatus==null) return;
        phaseno = phase;
        stageid = phase;
        greentime = obj.get("GreenTime").asInt();
        greenflashtime = obj.get("GreenFlashTime").asInt();
        //mingreen = obj.getInteger("");
        //maxgreen = obj.getInteger("");
        //senseinterval = obj.getInteger("");
        //phaseAdjustStep = obj.getInteger("");
        //maxcount = obj.getInteger("");
        //isMain = obj.getInteger("");
        yellowtime = obj.get("YellowTime").asInt();
        yellowflashtime = obj.get("YellowFlashTime").asInt();
        redyellowHintTime = obj.get("ExtinguishedYellow").asInt();
        allredtime = obj.get("RedTime").asInt();
        pedgreen = obj.get("PedestrianGreenTime").asInt();
        pedgreenflash = obj.get("PedestrianGreenFlashTime").asInt();
        pedearlyend = obj.get("PedestrianEarlyTime").asInt();
        peddalay = obj.get("PedestrianSlowTime").asInt();
        noCarTimeInterval = obj.get("NoCar_TimeInterval").asInt();
        personbutton = obj.get("PedestrianButtonActivated").asInt();
        iscoor = obj.get("CoordinationPhase").asInt();
        coortime = obj.get("PhaseDifference").asInt();
        JsonNode lanepassage = obj.get("LanePassage");
        if(lanepassage==null){
            System.out.println("[StageData5U]LanePassage is NULL:");
            //System.out.println(obj.toJSONString());
        }
        lightstatus.setLightStatus(lanepassage);
    }
    public void setStageDataSensor(int phase, JsonNode obj){
        //phaseno = phase;
        //stageid = phase;
        mingreen = obj.get("MinimumGreen").asInt();
        maxgreen = obj.get("MaximumGreen").asInt();
        senseinterval = obj.get("InductionInterval").asInt();
        phaseAdjustStep = obj.get("StepPhaseAdjustment").asInt();
        maxcount = obj.get("AccumulatedMaxFrequency").asInt();
        isMain = obj.get("IsMainPhase").asInt();
    }
    public void setStageDataSensorCommon(int phase){
        mingreen = 16;//CommonConfigFromXML.getSensorParamMinimumGreen();
        maxgreen = 100;//CommonConfigFromXML.getSensorParamMaximumGreen();
        senseinterval = 15;//CommonConfigFromXML.getSensorParamInductionInterval();
        phaseAdjustStep = 3;////CommonConfigFromXML.getSensorParamStepPhaseAdjustment();
        maxcount = 25;//CommonConfigFromXML.getSensorParamAccumulatedMaxFrequency();
        isMain = 0;//CommonConfigFromXML.getSensorParamIsMainPhase();
    }
    protected int getPedGreen(){
        int green = greentime + greenflashtime - peddalay - pedearlyend -pedgreenflash;
        if(green<0){ green=0; }
        return green;
    }
    protected int getPedGreenSensor(){
        int green = mingreen + greenflashtime - peddalay - pedearlyend -pedgreenflash;
        if(green<0){ green=0; }
        return green;
    }
    public JsonNode toSchemeParam(int mode){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ret.put("lightstatus", lightstatus.toSchemeParam());
        ret.put("phaseno", phaseno);
        ret.put("stageid", stageid);
        ret.put("greentime", greentime);
        ret.put("greenflashtime", greenflashtime);
        ret.put("mingreen", mingreen);
        ret.put("maxgreen", maxgreen);
        ret.put("senseinterval", senseinterval);
        ret.put("phaseAdjustStep", phaseAdjustStep);
        ret.put("maxcount", maxcount);
        ret.put("isMain", isMain);
        ret.put("yellowtime", yellowtime);
        ret.put("yellowflashtime", yellowflashtime);
        ret.put("redyellowHintTime", redyellowHintTime);
        ret.put("allredtime", allredtime);
        if(mode!=2)ret.put("pedgreen", getPedGreen());
        else ret.put("pedgreen", getPedGreenSensor());
        ret.put("pedgreenflash", pedgreenflash);
        ret.put("pedearlyend", pedearlyend);
        ret.put("peddalay", peddalay);
        ret.put("noCarTimeInterval", noCarTimeInterval);
        ret.put("personbutton", personbutton);
        ret.put("iscoor", iscoor);
        ret.put("coortime", coortime);
        return ret;
    }
    public int getGreentimeAll() {
        if(this.yellowtime != 0 && this.yellowflashtime!=0)
            return this.greentime + this.greenflashtime + this.yellowtime + this.allredtime
                    + this.redyellowHintTime%10;
        else if(this.yellowtime != 0)
            return this.greentime + this.greenflashtime + this.yellowtime + this.allredtime
                    + this.redyellowHintTime%10;
        else {
            return this.greentime + this.greenflashtime + this.yellowflashtime + this.allredtime
                    + this.redyellowHintTime%10;
        }
    }
    public int getGreentimeAllZDK() {
        return this.greentime + this.greenflashtime;
    }
    public void setGreentimeAllZDK(int greentimezdk){
        this.greentime = greentimezdk-this.greenflashtime;
    }
    public void setGreentimeAllZDK2(int greentimezdk){
        if(this.greenflashtime>0)
            this.greentime = greentimezdk-this.greenflashtime-this.greenflashtime;
        else
            this.greentime = greentimezdk-this.greenflashtime-this.yellowtime;
        this.pedgreen = this.greentime;
        this.pedgreenflash=this.greenflashtime;
    }
    public int setGreenTimeByAll(int greentimeall){
        if(this.yellowtime != 0 && this.yellowflashtime!=0) {
            greentime = greentimeall - this.greenflashtime - this.yellowtime - this.allredtime
                    - this.redyellowHintTime % 10;
        }else if(this.yellowtime != 0) {
            greentime = greentimeall - this.greenflashtime - this.yellowtime - this.allredtime
                    - this.redyellowHintTime % 10;
        }else {
            greentime = greentimeall - this.greenflashtime - this.yellowflashtime - this.allredtime
                    - this.redyellowHintTime % 10;
        }
        if(greentime<0) return -1;   //时间出错,这时不应该下发，可触发一遍读取，恢复数据
        return 1;
    }

    public StageParam getStageParamGAT1049(){
        StageParam ret = new StageParam();
        ret.setStageNo(stageidZdk);
    }
    public JsonNode getStageItemGAT1049(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ret.put("StageNo", stageidZdk);
        int stageno = BasicStageTable.getStageNo5U(lightstatus.getStageSet());
        if(stageno>10)ret.put("StageName", BasicStageTable.checkSimilarWithStd(lightstatus.getStageSet()));
        else ret.put("StageName", BasicStageTable.getStageName(stageno));
        ret.put("Attribute", 0);    // 0-一般，1-感应
        ret.put("PhaseNoList", mapper.valueToTree(lightstatus.getStageSet().toArray()));
        ret.put("Green", getGreentimeAllZDK());
        ret.put("RedYellow", this.redyellowHintTime % 10);
        ret.put("Yellow", this.yellowflashtime>0?this.yellowflashtime:this.yellowtime);
        ret.put("AllRed", this.allredtime);
        return ret;
    }
}
