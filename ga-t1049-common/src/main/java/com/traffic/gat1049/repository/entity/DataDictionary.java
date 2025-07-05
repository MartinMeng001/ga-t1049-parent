package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 附加功能表
// ================================

// 38. 数据字典表 (DataDictionary)
@Data
@Accessors(chain = true)
@TableName("data_dictionary")
@Entity
@Table(name = "data_dictionary")
public class DataDictionary {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("dict_type")
    @Column(name = "dict_type", length = 50, nullable = false)
    private String dictType;

    @TableField("dict_code")
    @Column(name = "dict_code", length = 50, nullable = false)
    private String dictCode;

    @TableField("dict_name")
    @Column(name = "dict_name", length = 100, nullable = false)
    private String dictName;

    @TableField("dict_value")
    @Column(name = "dict_value", length = 200)
    private String dictValue;

    @TableField("sort_order")
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @TableField("is_active")
    @Column(name = "is_active")
    private Boolean isActive = true;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
