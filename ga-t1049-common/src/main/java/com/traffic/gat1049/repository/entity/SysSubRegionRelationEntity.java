package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 34. 系统子区关系表 (SysSubRegionRelation)
@Data
@Accessors(chain = true)
@TableName("sys_sub_region_relation")
@Entity
@Table(name = "sys_sub_region_relation")
public class SysSubRegionRelationEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("system_id")
    @Column(name = "system_id", length = 20, nullable = false)
    private String systemId;

    @TableField("sub_region_id")
    @Column(name = "sub_region_id", length = 11, nullable = false)
    private String subRegionId;

    @TableField("is_active")
    @Column(name = "is_active")
    private Boolean isActive = true;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
