package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 人行横道参数表 (PedestrianParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("pedestrian_param")
@Entity
@Table(name = "pedestrian_param")
public class PedestrianParamEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("pedestrian_no")
    @Column(name = "pedestrian_no", nullable = false)
    private Integer pedestrianNo;

    @TableField("direction")
    @Column(name = "direction", length = 1, nullable = false)
    private String direction;

    @TableField("attribute")
    @Column(name = "attribute", nullable = false)
    private Integer attribute;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 构造函数
     */
    public PedestrianParamEntity() {
        super();
    }

    public PedestrianParamEntity(String crossId, Integer pedestrianNo) {
        super();
        this.crossId = crossId;
        this.pedestrianNo = pedestrianNo;
    }

    public PedestrianParamEntity(String crossId, Integer pedestrianNo, String direction) {
        super();
        this.crossId = crossId;
        this.pedestrianNo = pedestrianNo;
        this.direction = direction;
    }

    public PedestrianParamEntity(String crossId, Integer pedestrianNo, String direction, Integer attribute) {
        super();
        this.crossId = crossId;
        this.pedestrianNo = pedestrianNo;
        this.direction = direction;
        this.attribute = attribute;
    }

    /**
     * 验证人行横道参数的有效性
     * @return 验证结果
     */
    public boolean isValid() {
        if (crossId == null || crossId.trim().isEmpty()) {
            return false;
        }
        if (pedestrianNo == null || pedestrianNo < 1 || pedestrianNo > 99) {
            return false;
        }
        if (direction == null || direction.trim().isEmpty()) {
            return false;
        }
        if (attribute == null) {
            return false;
        }
        // 验证方向值 (1-8对应N,NE,E,SE,S,SW,W,NW)
        if (!isValidDirection(direction)) {
            return false;
        }
        // 验证属性值 (1-一次过街；21-二次过街路口进口；22-二次过街路口出口)
        if (!isValidAttribute(attribute)) {
            return false;
        }
        return true;
    }

    /**
     * 验证方向是否有效
     * @param direction 方向
     * @return 是否有效
     */
    private boolean isValidDirection(String direction) {
        if (direction == null || direction.length() != 1) {
            return false;
        }
        char dir = direction.charAt(0);
        return dir >= '1' && dir <= '8';
    }

    /**
     * 验证属性是否有效
     * @param attribute 属性
     * @return 是否有效
     */
    private boolean isValidAttribute(Integer attribute) {
        return attribute != null &&
                (attribute == 1 || attribute == 21 || attribute == 22);
    }

    /**
     * 获取方向描述
     * @return 方向描述
     */
    public String getDirectionDescription() {
        if (direction == null) return "未知";
        switch (direction) {
            case "1": return "北";
            case "2": return "东北";
            case "3": return "东";
            case "4": return "东南";
            case "5": return "南";
            case "6": return "西南";
            case "7": return "西";
            case "8": return "西北";
            default: return "未知";
        }
    }

    /**
     * 获取属性描述
     * @return 属性描述
     */
    public String getAttributeDescription() {
        if (attribute == null) return "未知";
        switch (attribute) {
            case 1: return "一次过街";
            case 21: return "二次过街路口进口";
            case 22: return "二次过街路口出口";
            default: return "未知";
        }
    }

    @Override
    public String toString() {
        return "PedestrianParam{" +
                "id=" + id +
                ", crossId='" + crossId + '\'' +
                ", pedestrianNo=" + pedestrianNo +
                ", direction='" + direction + '\'' +
                ", attribute=" + attribute +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PedestrianParamEntity that = (PedestrianParamEntity) o;

        if (crossId != null ? !crossId.equals(that.crossId) : that.crossId != null) return false;
        return pedestrianNo != null ? pedestrianNo.equals(that.pedestrianNo) : that.pedestrianNo == null;
    }

    @Override
    public int hashCode() {
        int result = crossId != null ? crossId.hashCode() : 0;
        result = 31 * result + (pedestrianNo != null ? pedestrianNo.hashCode() : 0);
        return result;
    }

    /**
     * 人行横道属性枚举
     */
    public enum PedestrianAttribute {
        ONE_STAGE(1, "一次过街"),
        TWO_STAGE_ENTRANCE(21, "二次过街路口进口"),
        TWO_STAGE_EXIT(22, "二次过街路口出口");

        private final int code;
        private final String description;

        PedestrianAttribute(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static PedestrianAttribute fromCode(int code) {
            for (PedestrianAttribute attr : values()) {
                if (attr.code == code) {
                    return attr;
                }
            }
            throw new IllegalArgumentException("Unknown pedestrian attribute code: " + code);
        }
    }

    /**
     * 方向枚举
     */
    public enum Direction {
        NORTH("1", "北"),
        NORTHEAST("2", "东北"),
        EAST("3", "东"),
        SOUTHEAST("4", "东南"),
        SOUTH("5", "南"),
        SOUTHWEST("6", "西南"),
        WEST("7", "西"),
        NORTHWEST("8", "西北");

        private final String code;
        private final String description;

        Direction(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Direction fromCode(String code) {
            for (Direction dir : values()) {
                if (dir.code.equals(code)) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Unknown direction code: " + code);
        }
    }
}
