package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 5. 信号机参数表 (SignalControllerParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("signal_controller")
@Entity
@Table(name = "signal_controller")
public class SignalControllerParamEntity {
    /**
     * 信号机设备编号
     * 机构代码前6位+99+4位数字，总长度18位
     */
    @TableId
    @Id
    @Column(name = "signal_controller_id", length = 18)  // ✅ 修正：长度改为18
    private String signalControllerId;

    /**
     * 供应商
     */
    @TableField("supplier")
    @Column(name = "supplier", length = 50, nullable = false)
    private String supplier;

    /**
     * 规格型号
     */
    @TableField("type")
    @Column(name = "type", length = 16, nullable = false)
    private String type;

    /**
     * 识别码
     */
    @TableField("id_code")
    @Column(name = "id_code", length = 16, nullable = false)
    private String idCode;

    /**
     * 通信接口
     * 10-以太网；11-TCP Client；12-TCP Server；13-UDP；20-串口；99-其他
     */
    @TableField("comm_mode")
    @Column(name = "comm_mode", length = 2, nullable = false)
    private String commMode;

    /**
     * 信号机通信IP地址
     */
    @TableField("ip")
    @Column(name = "ip", length = 15)
    private String ip;

    /**
     * 子网掩码
     */
    @TableField("sub_mask")
    @Column(name = "sub_mask", length = 15)
    private String subMask;

    /**
     * 网关
     */
    @TableField("gateway")
    @Column(name = "gateway", length = 15)
    private String gateway;

    /**
     * 端口号
     */
    @TableField("port")
    @Column(name = "port")
    private Integer port;

    /**
     * 是否有柜门状态检测
     * 1-是；0-否
     */
    @TableField("has_door_status")
    @Column(name = "has_door_status")
    private Integer hasDoorStatus;

    /**
     * 安装位置经度
     */
    @TableField("longitude")
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 安装位置纬度
     */
    @TableField("latitude")
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
