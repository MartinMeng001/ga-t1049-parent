package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// 41. 数据上传控制表 (CrossReportCtrl)
@Data
@Accessors(chain = true)
@TableName("cross_report_ctrl")
@Entity
@Table(name = "cross_report_ctrl")
public class CrossReportCtrlEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("data_type")
    @Column(name = "data_type", length = 30, nullable = false)
    private String dataType;

    @TableField("cmd")
    @Enumerated(EnumType.STRING)
    @Column(name = "cmd", nullable = false)
    private ControlCommand cmd;

    @TableField("status")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ControlStatus status = ControlStatus.ACTIVE;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    public enum ControlCommand {
        Start, Stop
    }

    public enum ControlStatus {
        ACTIVE, INACTIVE
    }
    /**
     * 将crossId字符串转换为路口ID列表
     * 支持以逗号分隔的多个路口ID
     *
     * @return 路口ID列表，如果crossId为空则返回空列表
     */
    public List<String> getCrossList() {
        if (!StringUtils.hasText(crossId)) {
            return Collections.emptyList();
        }

        return Arrays.stream(crossId.split(","))
                .map(String::trim)                    // 去除空格
                .filter(StringUtils::hasText)         // 过滤空字符串
                .distinct()                           // 去重
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 设置路口ID列表
     * 将List<String>转换为逗号分隔的字符串设置到crossId字段
     *
     * @param crossList 路口ID列表
     */
    public void setCrossList(List<String> crossList) {
        if (crossList == null || crossList.isEmpty()) {
            this.crossId = "";
            return;
        }

        this.crossId = crossList.stream()
                .filter(StringUtils::hasText)         // 过滤空字符串
                .map(String::trim)                    // 去除空格
                .distinct()                           // 去重
                .collect(java.util.stream.Collectors.joining(","));
    }
}
