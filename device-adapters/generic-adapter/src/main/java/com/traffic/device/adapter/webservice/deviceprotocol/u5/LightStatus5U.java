package com.traffic.device.adapter.webservice.deviceprotocol.u5;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
public class LightStatus5U {
    private LightGroupItem north;
    private LightGroupItem east;
    private LightGroupItem south;
    private LightGroupItem west;
    private final BasicPhaseTable basicPhaseTable;
    public LightStatus5U(BasicPhaseTable basicPhaseTable) {
        this.basicPhaseTable = basicPhaseTable;
    }
    public void setLightStatus(JsonNode lightobj){
        if(north==null) north=new LightGroupItem();
        if(east==null) east=new LightGroupItem();
        if(south==null) south=new LightGroupItem();
        if(west==null) west=new LightGroupItem();
        north.setLightGroupData(lightobj.get("North"));
        east.setLightGroupData(lightobj.get("East"));
        south.setLightGroupData(lightobj.get("South"));
        west.setLightGroupData(lightobj.get("West"));
    }
    public JsonNode toSchemeParam(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();
        ret.put("north", north.toSchemeParam());
        ret.put("east", east.toSchemeParam());
        ret.put("south", south.toSchemeParam());
        ret.put("west", west.toSchemeParam());
        return ret;
    }
    public Set<Integer> getStageSet(){
        try {
            Set<Integer> ret = new HashSet<>();
            basicPhaseTable.parseDirection("North", ret, north);
            basicPhaseTable.parseDirection("East", ret, east);
            basicPhaseTable.parseDirection("South", ret, south);
            basicPhaseTable.parseDirection("West", ret, west);

            ret.remove(0);
            return ret;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
