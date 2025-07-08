package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 关联关系表
// ================================

// 28. 区域子区关系表 (RegionSubRegion)
@Data
@Accessors(chain = true)
@TableName("region_sub_region")
@Entity
@Table(name = "region_sub_region")
public class RegionSubRegionEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("region_id")
    @Column(name = "region_id", length = 9, nullable = false)
    private String regionId;

    @TableField("sub_region_id")
    @Column(name = "sub_region_id", length = 11, nullable = false)
    private String subRegionId;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
