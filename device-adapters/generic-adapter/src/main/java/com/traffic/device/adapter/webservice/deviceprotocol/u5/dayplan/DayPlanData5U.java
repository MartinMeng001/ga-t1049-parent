package com.traffic.device.adapter.webservice.deviceprotocol.u5.dayplan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.protocol.model.signal.Period;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayPlanData5U {
    private String CrossID;
    private int SIGID;
    private int DAYPLANID;
    private List<TimeIntervalItem> periods = new ArrayList<>();
    private boolean beUpdated=false;
    public void setDayPlanInfo(JsonNode dayplanobj){
        if("ok".equals(dayplanobj.get("success").asText())==false) return;
        DAYPLANID = dayplanobj.get("dayplanid").asInt();
        JsonNode dayplan = dayplanobj.get("rows");
        if (dayplan != null && dayplan.isArray()) {
            ArrayNode dayplanArray = (ArrayNode) dayplan;
            periods.clear();
            beUpdated = false;
            for (int i = 0; i < dayplanArray.size(); i++) {
                JsonNode intervalobj = dayplanArray.get(i);
                TimeIntervalItem intervalitem = new TimeIntervalItem();
                intervalitem.setIntervalData(intervalobj);
                periods.add(intervalitem);
            }
        }
    }
    public void updateDayplanInfo(List<Period> TimeList){
        if(TimeList.size()!=periods.size()) {
            beUpdated=true;
            //System.out.println("[DEBUG]daylan update size changed");
            periods.clear();
            for (Period item : TimeList) {
                TimeIntervalItem intveralitem = new TimeIntervalItem();
                intveralitem.updateScheme(item.getPlanNo());
                intveralitem.updateControlMode(ControlMode.fromCode(item.getCtrlMode()));
                intveralitem.updateTimeItem(item.getStartTime());
                periods.add(intveralitem);
            }
        }else{
            for(int i=0;i<TimeList.size();i++){
                TimeIntervalItem intervalitem = periods.get(i);
                Period dayplanitem = TimeList.get(i);
                if(intervalitem.getSchemeno() != dayplanitem.getPlanNo()){
                    beUpdated=true;
                    //System.out.println("[DEBUG]daylan update planNO from "+intervalitem.getSchemeno()+" to "+dayplanitem.getPlanNo());
                    intervalitem.updateScheme(dayplanitem.getPlanNo());
                }
                if(intervalitem.getControlModeGAT1049().equals(dayplanitem.getCtrlMode())==false){
                    beUpdated=true;
                    //System.out.println("[DEBUG]daylan update ControlMode from "+intervalitem.getControlModeGAT1049()+" to "+dayplanitem.getControlMode());
                    intervalitem.updateControlMode(ControlMode.fromCode(dayplanitem.getCtrlMode()));
                }
                if(intervalitem.getTimeChainItemGAT1049().equals(dayplanitem.getStartTime())==false){
                    beUpdated=true;
                    //System.out.println("[DEBUG]daylan update Starttime from "+intervalitem.getTimeChainItemGAT1049()+" to "+dayplanitem.getStartTime());
                    intervalitem.updateTimeItem(dayplanitem.getStartTime());
                }
                if(beUpdated){
                    //System.out.println("[DEBUG]dayplanNo="+(i+1)+" time:"+intervalitem.getTimeChainItemGAT1049());
                    periods.set(i, intervalitem);
                }
            }
        }
    }
    public JsonNode toDayplanParam(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ret.put("SIGID", SIGID);
        ret.put("DAYPLANID", DAYPLANID);
        ArrayNode array = mapper.createArrayNode();
        for(TimeIntervalItem interval: periods){
            JsonNode obj = interval.toDayplanParam();
            array.add(obj);
        }
        ret.put("periods", array);
        return ret;
    }
    /*
    00:00-07:00-23:00
     */
    public DayPlanParam getDayplanParamGAT1049(){
        DayPlanParam dayPlanParam = new DayPlanParam();
        dayPlanParam.setCrossId(getCrossID());
        dayPlanParam.setDayPlanNo(getDAYPLANID());
        dayPlanParam.setDayPlanName("日计划"+getDAYPLANID());
        List<Period> periodList = new ArrayList<>();
        for(int i=0;i<periods.size();i++){
            TimeIntervalItem intervalobj = periods.get(i);
            periodList.add(intervalobj.getPeriodGAT1049());
        }
        dayPlanParam.setPeriodList(periodList);
        return dayPlanParam;
    }

}
