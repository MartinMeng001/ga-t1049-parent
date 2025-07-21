package com.traffic.device.adapter.webservice.deviceprotocol.u5;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LightGroupItem {
    private String go;
    private String left;
    private String right;
    private String person1;
    private String bicycle;
    private String turnback;
    private String person2;
    private String extend;

    public void setLightGroupData(JsonNode obj){
        go = obj.get("Straight").asText();
        left = obj.get("TurnLeft").asText();
        right = obj.get("TurnRight").asText();
        person1 = obj.get("Sidewalk").asText();
        bicycle = obj.get("NonMotorized").asText();
        turnback = obj.get("TurnRound").asText();
        person2 = obj.get("Extend1").asText();
        extend = obj.get("Extend2").asText();
    }
    public JsonNode toSchemeParam(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ret = mapper.createObjectNode();

        ret.put("go", go);
        ret.put("left", left);
        ret.put("right", right);
        ret.put("person1", person1);
        ret.put("bicycle", bicycle);
        ret.put("turnback", turnback);
        ret.put("person2", person2);
        ret.put("extend", extend);
        return ret;
    }
}
