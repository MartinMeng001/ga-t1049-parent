package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 附加功能表
// ================================

// 供应商字典表 (SupplierDictionary)
@Data
@Accessors(chain = true)
@TableName("supplier_dictionary")
@Entity
@Table(name = "supplier_dictionary")
public class SupplierDictionaryEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("supplier_type")
    @Column(name = "supplier_type", length = 50, nullable = false)
    private String supplierType;

    @TableField("supplier_code")
    @Column(name = "supplier_code", length = 50, nullable = false)
    private String supplierCode;

    @TableField("supplier_name")
    @Column(name = "supplier_name", length = 100, nullable = false)
    private String supplierName;

    @TableField("supplier_value")
    @Column(name = "supplier_value", length = 200)
    private String supplierValue;

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
