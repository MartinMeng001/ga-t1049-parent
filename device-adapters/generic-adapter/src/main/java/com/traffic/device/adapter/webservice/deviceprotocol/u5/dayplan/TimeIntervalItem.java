package com.traffic.device.adapter.webservice.deviceprotocol.u5.dayplan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.protocol.model.signal.Period;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeIntervalItem {
    private int StartHour;
    private int StartMinute;
    private int iControlMode;
    private int schemeno;

    public void setIntervalData(JsonNode obj){
        StartHour = obj.get("StartHour").asInt();
        StartMinute = obj.get("StartMinute").asInt();
        iControlMode = obj.get("ControlMode").asInt();
        schemeno = obj.get("schemeNo").asInt();
    }
    public JsonNode toDayplanParam(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ret.put("StartHour", StartHour);
        ret.put("StartMinute", StartMinute);
        ret.put("ControlMode", iControlMode);
        if(iControlMode==11 || iControlMode==13){
            ret.put("schemeno", 0);
        }
        else ret.put("schemeno", schemeno);
        return ret;
    }
    public Period getPeriodGAT1049(){
        Period period = new Period();
        period.setCtrlMode(String.valueOf(iControlMode));
        String ret = String.format("%02d:%02d",StartHour, StartMinute);
        period.setStartTime(ret);
        period.setPlanNo(getSchemeno());
        return period;
    }
    public void updateTimeItem(String data){
        String[] timedata = data.split(":");
        if(timedata.length<2) return;
        StartHour = Integer.parseInt(timedata[0]);
        StartMinute = Integer.parseInt(timedata[1]);
        //System.out.println("[DEBUG] parseHour:"+StartHour+" ParseMinute:"+StartMinute);
    }
    public String getSchemeChainGAT1049(){
        String ret = String.format("%d", schemeno);
        return ret;
    }
    public void updateScheme(int scheme){
        schemeno = scheme;
    }
    public String getControlModeGAT1049(){
        String ret = mode2GAT1049(iControlMode);
        return ret;
    }
    public void updateControlMode(ControlMode mode){
        switch (mode){
            case SINGLE_TIMING: iControlMode=1; break;
            case SINGLE_ACTUATED: iControlMode=2; break;
            case LINE_COORDINATED: iControlMode=3; break;
            case SINGLE_ADAPTIVE: iControlMode=5; break;  // 不支持中电科
            case LIGHT_OFF: iControlMode=6; break;
            case ALL_YELLOW_FLASH: iControlMode=7; break;
            default: break;
        }
    }
    protected String mode2GAT1049(int mode){
        switch (mode){
            case 1: return ControlMode.SINGLE_TIMING.getCode();  // 定时
            case 2: return ControlMode.SINGLE_ACTUATED.getCode();  // 感应
            case 3: return ControlMode.LINE_COORDINATED.getCode();  // 绿波带
            case 5: return ControlMode.SINGLE_ADAPTIVE.getCode();  // 自适应
            case 6: return ControlMode.LIGHT_OFF.getCode();  // 灭灯
            case 7: return ControlMode.ALL_YELLOW_FLASH.getCode();  // 黄闪
            case 4: return ControlMode.AREA_COORDINATED.getCode();    // 区域协调
            case 8: return ControlMode.LINE_COORDINATED_ACTUATED.getCode(); // 线协调感应
        }
        return "";
    }
    protected int modeFromGAT1049(ControlMode mode){
        switch (mode){
            case SINGLE_TIMING: return 1;
            case SINGLE_ACTUATED: return 2;
            case LINE_COORDINATED: return 3;
            case SINGLE_ADAPTIVE: return 5;
            case LIGHT_OFF: return 6;
            case ALL_YELLOW_FLASH: return 7;
        }
        return 0;
    }
}
