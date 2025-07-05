
/*
GA/T 1049.2 交通信号控制系统数据库创建完成

数据库特性：
1. 完全符合GA/T 1049.2协议标准
2. 支持多系统接入架构，每个系统独立管理状态
3. 提供完整的协议参数视图
4. 包含运行信息和控制命令数据表
5. 实现数据分区以提高性能
6. 包含完整的存储过程、触发器和视图
7. 提供数据字典和索引优化

核心视图：
- v_protocol_sys_info_complete: 系统完整信息
- v_region_param_complete: 区域参数完整信息
- v_route_param_complete: 线路参数完整信息
- v_sub_region_param_complete: 子区参数完整信息
- v_signal_controller_complete: 信号机参数完整信息
- v_cross_param_complete: 路口参数完整信息
- v_system_overview: 系统概览（包含系统状态）
- v_cross_system_mapping: 路口归属关系

系统状态管理：
- sys_state表关联system_id，支持多系统独立状态管理
- v_system_overview视图显示每个系统的最新状态
- 存储过程UpsertSystemState支持按系统更新状态

使用建议：
1. 定期执行清理历史数据存储过程
2. 根据实际需要添加月度分区
3. 监控索引性能并适时调整
4. 建立定期备份机制
5. 使用UpsertSystemState存储过程更新系统状态

查询示例：
-- 查询指定系统状态
SELECT * FROM v_system_overview WHERE system_id = 'SYS001';

-- 更新系统状态
CALL UpsertSystemState('SYS001', 'Online', NOW());

-- 查看所有系统状态
SELECT system_id, sys_name, sys_status, last_status_time
FROM v_system_overview
ORDER BY last_status_time DESC;
*/-- ================================
-- GA/T 1049.2 交通信号控制系统完整数据库
-- 基于《公安交通集成指挥平台通信协议 第2部分：交通信号控制系统》
-- 支持多系统接入架构
-- ================================

CREATE DATABASE IF NOT EXISTS traffic_signal_control
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE traffic_signal_control;

-- ================================
-- 配置参数数据表
-- ================================

-- 1. 系统参数表 (SysInfo) - 支持多系统
CREATE TABLE sys_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id VARCHAR(20) NOT NULL UNIQUE COMMENT '系统唯一标识',
    sys_name VARCHAR(50) NOT NULL COMMENT '系统名称',
    sys_version VARCHAR(10) NOT NULL COMMENT '系统版本号',
    supplier VARCHAR(50) NOT NULL COMMENT '供应商',
    is_active TINYINT(1) DEFAULT 1 COMMENT '系统是否激活：1-激活；0-停用',
    description TEXT COMMENT '系统描述',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_sys_info_system_id (system_id)
) COMMENT = '系统参数表';

-- 2. 区域参数表 (RegionParam)
CREATE TABLE region_param (
    region_id CHAR(9) PRIMARY KEY COMMENT '区域编号：6位行政区划代码+3位数字',
    region_name VARCHAR(50) NOT NULL COMMENT '区域名称',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT = '区域参数表';

-- 3. 线路参数表 (RouteParam)
CREATE TABLE route_param (
    route_id CHAR(11) PRIMARY KEY COMMENT '线路编号：6位行政区划代码+5位数字',
    route_name VARCHAR(50) NOT NULL COMMENT '线路名称',
    type TINYINT NOT NULL COMMENT '线路类型：1-协调干线；2-公交优先线路；3-特勤线路；4-有轨电车线路；5-快速路；9-其他',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT = '线路参数表';

-- 4. 线路路口关联表 (RouteCross)
CREATE TABLE route_cross (
    id INT PRIMARY KEY AUTO_INCREMENT,
    route_id CHAR(11) NOT NULL COMMENT '线路编号',
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    distance SMALLINT UNSIGNED DEFAULT 0 COMMENT '与上游路口距离(米)',
    order_seq TINYINT UNSIGNED NOT NULL COMMENT '路口在线路中的顺序',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES route_param(route_id) ON DELETE CASCADE,
    INDEX idx_route_id (route_id),
    INDEX idx_cross_id (cross_id)
) COMMENT = '线路路口关联表';

-- 5. 子区参数表 (SubRegionParam)
CREATE TABLE sub_region_param (
    sub_region_id CHAR(11) PRIMARY KEY COMMENT '子区编号：6位行政区划代码+5位数字',
    sub_region_name VARCHAR(50) NOT NULL COMMENT '子区名称',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT = '子区参数表';

-- 6. 路口参数表 (CrossParam)
CREATE TABLE cross_param (
    cross_id CHAR(14) PRIMARY KEY COMMENT '路口编号：机构代码前6位+80+4位路口代码',
    cross_name VARCHAR(50) NOT NULL COMMENT '路口名称',
    feature TINYINT NOT NULL COMMENT '路口形状：10-行人过街；12-2次行人过街；23-T形Y形；24-十字形；35-五岔；36-六岔；39-多岔；40-环形；50-匝道；51-匝道入口；52-匝道出口；61-快速路主路；90-其他',
    grade CHAR(2) NOT NULL COMMENT '路口等级：11-一级；12-二级；13-三级；21-四级；22-五级；31-六级；99-其他',
    green_conflict_matrix TEXT COMMENT '绿冲突矩阵',
    longitude DOUBLE COMMENT '路口中心位置经度',
    latitude DOUBLE COMMENT '路口中心位置纬度',
    altitude INT COMMENT '路口位置海拔高度(米)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_longitude_latitude (longitude, latitude)
) COMMENT = '路口参数表';

-- 7. 信号机参数表 (SignalController)
CREATE TABLE signal_controller (
    signal_controller_id CHAR(18) PRIMARY KEY COMMENT '信号机设备编号：机构代码前6位+99+4位数字',
    supplier VARCHAR(50) NOT NULL COMMENT '供应商',
    type CHAR(16) NOT NULL COMMENT '规格型号',
    id_code CHAR(16) NOT NULL COMMENT '识别码',
    comm_mode CHAR(2) NOT NULL COMMENT '通信接口：10-以太网；11-TCP Client；12-TCP Server；13-UDP；20-串口；99-其他',
    ip VARCHAR(15) COMMENT '信号机通信IP地址',
    sub_mask VARCHAR(15) COMMENT '子网掩码',
    gateway VARCHAR(15) COMMENT '网关',
    port SMALLINT UNSIGNED DEFAULT 0 COMMENT '端口号',
    has_door_status TINYINT(1) DEFAULT 0 COMMENT '是否有柜门状态检测：1-是；0-否',
    longitude DOUBLE COMMENT '安装位置经度',
    latitude DOUBLE COMMENT '安装位置纬度',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ip (ip),
    INDEX idx_longitude_latitude (longitude, latitude)
) COMMENT = '信号机参数表';

-- 8. 信号机控制路口关联表
CREATE TABLE signal_controller_cross (
    id INT PRIMARY KEY AUTO_INCREMENT,
    signal_controller_id CHAR(18) NOT NULL COMMENT '信号机设备编号',
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    is_main TINYINT(1) DEFAULT 0 COMMENT '是否为主路口：1-是；0-否',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (signal_controller_id) REFERENCES signal_controller(signal_controller_id) ON DELETE CASCADE,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_controller_cross (signal_controller_id, cross_id)
) COMMENT = '信号机控制路口关联表';

-- 9. 信号灯组参数表 (LampGroupParam)
CREATE TABLE lamp_group_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    lamp_group_no TINYINT UNSIGNED NOT NULL COMMENT '信号灯组序号(1-99)',
    direction CHAR(1) NOT NULL COMMENT '控制进口方向',
    type CHAR(2) NOT NULL COMMENT '灯组类型',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_lamp_group (cross_id, lamp_group_no)
) COMMENT = '信号灯组参数表';

-- 10. 检测器参数表 (DetectorParam)
CREATE TABLE detector_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    detector_no SMALLINT UNSIGNED NOT NULL COMMENT '检测器序号(1-999)',
    type TINYINT NOT NULL COMMENT '检测器类型：1-线圈；2-视频；3-地磁；4-微波；5-RFID；6-雷视一体；9-其他',
    position TINYINT NOT NULL COMMENT '检测位置：1-进口；2-出口；9-其他',
    target CHAR(3) NOT NULL COMMENT '检测对象：从左到右分别标记机动车、非机动车、行人(1-支持，0-不支持)',
    distance INT NOT NULL COMMENT '距停车线距离(厘米)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_detector (cross_id, detector_no)
) COMMENT = '检测器参数表';

-- 11. 车道参数表 (LaneParam)
CREATE TABLE lane_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    lane_no TINYINT UNSIGNED NOT NULL COMMENT '车道序号(1-99)',
    direction CHAR(1) NOT NULL COMMENT '车道所在进口方向',
    attribute TINYINT NOT NULL COMMENT '车道属性：0-路口进口；1-路口出口；2-匝道；3-路段车道；9-其他',
    movement CHAR(2) NOT NULL COMMENT '车道转向属性',
    feature TINYINT NOT NULL COMMENT '车道特性：1-机动车；2-非机动车；3-机非混合；4-行人便道；9-其他',
    azimuth SMALLINT UNSIGNED COMMENT '方位角(0-359度)',
    waiting_area TINYINT(1) DEFAULT 0 COMMENT '待行区：0-无；1-有',
    var_movement_list JSON COMMENT '可变车道功能列表',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_lane (cross_id, lane_no)
) COMMENT = '车道参数表';

-- 12. 人行横道参数表 (PedestrianParam)
CREATE TABLE pedestrian_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    pedestrian_no TINYINT UNSIGNED NOT NULL COMMENT '人行横道序号(1-99)',
    direction CHAR(1) NOT NULL COMMENT '所在进口方向',
    attribute TINYINT NOT NULL COMMENT '属性：1-一次过街；21-二次过街路口进口；22-二次过街路口出口',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_pedestrian (cross_id, pedestrian_no)
) COMMENT = '人行横道参数表';

-- 13. 信号组参数表 (SignalGroupParam)
CREATE TABLE signal_group_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    signal_group_no SMALLINT UNSIGNED NOT NULL COMMENT '信号组序号(1-999)',
    name VARCHAR(50) COMMENT '信号组名称',
    green_flash_len TINYINT UNSIGNED COMMENT '绿闪时长(秒)',
    max_green TINYINT UNSIGNED COMMENT '最大绿灯时长(秒)',
    min_green TINYINT UNSIGNED COMMENT '最小绿灯时长(秒)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_signal_group (cross_id, signal_group_no)
) COMMENT = '信号组参数表';

-- 14. 信号组灯组关联表
CREATE TABLE signal_group_lamp_group (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    signal_group_no SMALLINT UNSIGNED NOT NULL COMMENT '信号组序号',
    lamp_group_no TINYINT UNSIGNED NOT NULL COMMENT '信号灯组序号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_signal_group_lamp (cross_id, signal_group_no, lamp_group_no)
) COMMENT = '信号组灯组关联表';

-- 15. 阶段参数表 (StageParam)
CREATE TABLE stage_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    stage_no SMALLINT UNSIGNED NOT NULL COMMENT '阶段号',
    stage_name VARCHAR(50) COMMENT '阶段名称',
    attribute TINYINT DEFAULT 0 COMMENT '特征：0-一般；1-感应',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_stage (cross_id, stage_no)
) COMMENT = '阶段参数表';

-- 16. 阶段信号组状态表
CREATE TABLE stage_signal_group_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    stage_no SMALLINT UNSIGNED NOT NULL COMMENT '阶段号',
    signal_group_no SMALLINT UNSIGNED NOT NULL COMMENT '信号组序号',
    lamp_status CHAR(3) NOT NULL COMMENT '灯色状态',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_stage_signal_group (cross_id, stage_no, signal_group_no)
) COMMENT = '阶段信号组状态表';

-- 17. 配时方案参数表 (PlanParam)
CREATE TABLE plan_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    plan_no SMALLINT UNSIGNED NOT NULL COMMENT '方案序号(1-9999)',
    plan_name VARCHAR(50) COMMENT '方案名称',
    cycle_len TINYINT UNSIGNED NOT NULL COMMENT '周期长度(秒)',
    coord_stage_no TINYINT UNSIGNED DEFAULT 0 COMMENT '协调相位号',
    offset TINYINT UNSIGNED DEFAULT 0 COMMENT '协调相位差(秒)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_plan (cross_id, plan_no)
) COMMENT = '配时方案参数表';

-- 18. 阶段配时信息表 (StageTiming)
CREATE TABLE stage_timing (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    plan_no SMALLINT UNSIGNED NOT NULL COMMENT '方案序号',
    stage_no SMALLINT UNSIGNED NOT NULL COMMENT '阶段号',
    green TINYINT UNSIGNED NOT NULL COMMENT '绿灯时长(秒)',
    yellow TINYINT UNSIGNED NOT NULL COMMENT '黄灯时长(秒)',
    all_red TINYINT UNSIGNED NOT NULL COMMENT '全红时长(秒)',
    max_green TINYINT UNSIGNED COMMENT '感应/自适应控制最大绿灯时长(秒)',
    min_green TINYINT UNSIGNED COMMENT '感应/自适应控制最小绿灯时长(秒)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_plan_stage_timing (cross_id, plan_no, stage_no)
) COMMENT = '阶段配时信息表';

-- 19. 信号组迟开早闭调整表 (Adjust)
CREATE TABLE signal_group_adjust (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    plan_no SMALLINT UNSIGNED NOT NULL COMMENT '方案序号',
    stage_no SMALLINT UNSIGNED NOT NULL COMMENT '阶段号',
    signal_group_no SMALLINT UNSIGNED NOT NULL COMMENT '信号组序号',
    oper TINYINT NOT NULL COMMENT '调整方式：1-迟开；2-早闭',
    len TINYINT UNSIGNED NOT NULL COMMENT '调整时长(秒)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE
) COMMENT = '信号组迟开早闭调整表';

-- 20. 日计划参数表 (DayPlanParam)
CREATE TABLE day_plan_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    day_plan_no SMALLINT UNSIGNED NOT NULL COMMENT '日计划号(1-999)',
    day_plan_name VARCHAR(50) COMMENT '日计划名称',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_day_plan (cross_id, day_plan_no)
) COMMENT = '日计划参数表';

-- 21. 时段信息表 (Period)
CREATE TABLE period_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    day_plan_no SMALLINT UNSIGNED NOT NULL COMMENT '日计划号',
    start_time TIME NOT NULL COMMENT '开始时间',
    plan_no SMALLINT UNSIGNED NOT NULL COMMENT '配时方案序号',
    ctrl_mode CHAR(2) NOT NULL COMMENT '控制方式',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_day_plan_start_time (cross_id, day_plan_no, start_time)
) COMMENT = '时段信息表';

-- 22. 调度参数表 (ScheduleParam)
CREATE TABLE schedule_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    schedule_no SMALLINT UNSIGNED NOT NULL COMMENT '调度号(1-999)',
    schedule_name VARCHAR(50) COMMENT '调度名称',
    type TINYINT NOT NULL COMMENT '调度类型：1-特殊日调度；2-时间段周调度；3-周调度',
    start_day CHAR(5) NOT NULL COMMENT '开始月日(MM-DD)',
    end_day CHAR(5) NOT NULL COMMENT '结束月日(MM-DD)',
    week_day TINYINT COMMENT '周几(1-7分别代表周一至周日)',
    day_plan_no SMALLINT UNSIGNED NOT NULL COMMENT '日计划号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_schedule (cross_id, schedule_no)
) COMMENT = '调度参数表';

-- ================================
-- 多系统关联关系表
-- ================================

-- 23. 系统路口关联表
CREATE TABLE sys_cross_relation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id VARCHAR(20) NOT NULL COMMENT '系统标识',
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    is_primary TINYINT(1) DEFAULT 0 COMMENT '是否为主控系统：1-是；0-否',
    priority TINYINT DEFAULT 1 COMMENT '优先级：1-5，数字越小优先级越高',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '关联开始时间',
    end_time TIMESTAMP NULL COMMENT '关联结束时间，NULL表示永久有效',
    is_active TINYINT(1) DEFAULT 1 COMMENT '关联是否有效：1-有效；0-无效',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(200) COMMENT '备注',
    FOREIGN KEY (system_id) REFERENCES sys_info(system_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_sys_cross_active (system_id, cross_id, is_active),
    INDEX idx_system_id (system_id),
    INDEX idx_cross_id (cross_id),
    INDEX idx_active_time (is_active, start_time, end_time),
    INDEX idx_priority (priority)
) COMMENT = '系统路口关联表';

-- 24. 系统子区关联表
CREATE TABLE sys_sub_region_relation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id VARCHAR(20) NOT NULL COMMENT '系统标识',
    sub_region_id CHAR(11) NOT NULL COMMENT '子区编号',
    region_id CHAR(9) COMMENT '所属区域编号',
    is_active TINYINT(1) DEFAULT 1 COMMENT '关联是否有效：1-有效；0-无效',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (system_id) REFERENCES sys_info(system_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (sub_region_id) REFERENCES sub_region_param(sub_region_id) ON DELETE CASCADE,
    FOREIGN KEY (region_id) REFERENCES region_param(region_id) ON DELETE SET NULL,
    UNIQUE KEY uk_sys_sub_region (system_id, sub_region_id),
    INDEX idx_system_id (system_id),
    INDEX idx_sub_region_id (sub_region_id),
    INDEX idx_region_id (region_id)
) COMMENT = '系统子区关联表';

-- 25. 系统线路关联表
CREATE TABLE sys_route_relation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id VARCHAR(20) NOT NULL COMMENT '系统标识',
    route_id CHAR(11) NOT NULL COMMENT '线路编号',
    sub_region_id CHAR(11) COMMENT '所属子区编号',
    is_active TINYINT(1) DEFAULT 1 COMMENT '关联是否有效：1-有效；0-无效',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (system_id) REFERENCES sys_info(system_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (route_id) REFERENCES route_param(route_id) ON DELETE CASCADE,
    FOREIGN KEY (sub_region_id) REFERENCES sub_region_param(sub_region_id) ON DELETE SET NULL,
    UNIQUE KEY uk_sys_route (system_id, route_id),
    INDEX idx_system_id (system_id),
    INDEX idx_route_id (route_id),
    INDEX idx_sub_region_id (sub_region_id)
) COMMENT = '系统线路关联表';

-- 26. 系统区域关联表
CREATE TABLE sys_region_relation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id VARCHAR(20) NOT NULL COMMENT '系统标识',
    region_id CHAR(9) NOT NULL COMMENT '区域编号',
    is_active TINYINT(1) DEFAULT 1 COMMENT '关联是否有效：1-有效；0-无效',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (system_id) REFERENCES sys_info(system_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (region_id) REFERENCES region_param(region_id) ON DELETE CASCADE,
    UNIQUE KEY uk_sys_region (system_id, region_id),
    INDEX idx_system_id (system_id),
    INDEX idx_region_id (region_id)
) COMMENT = '系统区域关联表';

-- 27. 系统信号机关联表
CREATE TABLE sys_signal_controller_relation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id VARCHAR(20) NOT NULL COMMENT '系统标识',
    signal_controller_id CHAR(18) NOT NULL COMMENT '信号机设备编号',
    is_active TINYINT(1) DEFAULT 1 COMMENT '关联是否有效：1-有效；0-无效',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (system_id) REFERENCES sys_info(system_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (signal_controller_id) REFERENCES signal_controller(signal_controller_id) ON DELETE CASCADE,
    UNIQUE KEY uk_sys_controller (system_id, signal_controller_id),
    INDEX idx_system_id (system_id),
    INDEX idx_controller_id (signal_controller_id)
) COMMENT = '系统信号机关联表';

-- ================================
-- 关联关系表
-- ================================

-- 28. 区域子区关联表
CREATE TABLE region_sub_region (
    id INT PRIMARY KEY AUTO_INCREMENT,
    region_id CHAR(9) NOT NULL COMMENT '区域编号',
    sub_region_id CHAR(11) NOT NULL COMMENT '子区编号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (region_id) REFERENCES region_param(region_id) ON DELETE CASCADE,
    FOREIGN KEY (sub_region_id) REFERENCES sub_region_param(sub_region_id) ON DELETE CASCADE,
    UNIQUE KEY uk_region_sub_region (region_id, sub_region_id)
) COMMENT = '区域子区关联表';

-- 29. 区域路口关联表
CREATE TABLE region_cross (
    id INT PRIMARY KEY AUTO_INCREMENT,
    region_id CHAR(9) NOT NULL COMMENT '区域编号',
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (region_id) REFERENCES region_param(region_id) ON DELETE CASCADE,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_region_cross (region_id, cross_id)
) COMMENT = '区域路口关联表';

-- 30. 线路子区关联表
CREATE TABLE route_sub_region (
    id INT PRIMARY KEY AUTO_INCREMENT,
    route_id CHAR(11) NOT NULL COMMENT '线路编号',
    sub_region_id CHAR(11) NOT NULL COMMENT '子区编号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES route_param(route_id) ON DELETE CASCADE,
    FOREIGN KEY (sub_region_id) REFERENCES sub_region_param(sub_region_id) ON DELETE CASCADE,
    UNIQUE KEY uk_route_sub_region (route_id, sub_region_id)
) COMMENT = '线路子区关联表';

-- 31. 子区路口关联表
CREATE TABLE sub_region_cross (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sub_region_id CHAR(11) NOT NULL COMMENT '子区编号',
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    is_key_cross TINYINT(1) DEFAULT 0 COMMENT '是否为关键路口：1-是；0-否',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sub_region_id) REFERENCES sub_region_param(sub_region_id) ON DELETE CASCADE,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_sub_region_cross (sub_region_id, cross_id)
) COMMENT = '子区路口关联表';

-- 32. 检测器车道关联表
CREATE TABLE detector_lane (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    detector_no SMALLINT UNSIGNED NOT NULL COMMENT '检测器序号',
    lane_no TINYINT UNSIGNED NOT NULL COMMENT '车道序号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_detector_lane (cross_id, detector_no, lane_no)
) COMMENT = '检测器车道关联表';

-- 33. 检测器人行横道关联表
CREATE TABLE detector_pedestrian (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    detector_no SMALLINT UNSIGNED NOT NULL COMMENT '检测器序号',
    pedestrian_no TINYINT UNSIGNED NOT NULL COMMENT '人行横道序号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_detector_pedestrian (cross_id, detector_no, pedestrian_no)
) COMMENT = '检测器人行横道关联表';

-- ================================
-- 运行信息数据表
-- ================================

-- 34. 系统状态表 (SysState)
CREATE TABLE sys_state (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id VARCHAR(20) NOT NULL COMMENT '系统标识',
    value ENUM('Online', 'Offline', 'Error') NOT NULL COMMENT '系统运行状态',
    time TIMESTAMP NOT NULL COMMENT '系统当前时间',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (system_id) REFERENCES sys_info(system_id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_system_id_time (system_id, time),
    INDEX idx_time (time)
) COMMENT = '系统状态表';

-- 35. 路口状态表 (CrossState)
CREATE TABLE cross_state (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    value ENUM('Online', 'Offline', 'Error') NOT NULL COMMENT '路口运行状态',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_id_time (cross_id, created_time)
) COMMENT = '路口状态表';

-- 36. 信号机故障表 (SignalControllerError)
CREATE TABLE signal_controller_error (
    id INT PRIMARY KEY AUTO_INCREMENT,
    signal_controller_id CHAR(18) NOT NULL COMMENT '信号机设备编号',
    error_type VARCHAR(10) COMMENT '故障类型：1-灯输出故障；2-电源故障；3-时钟故障；4-运行故障；5-方案错误；9-其他错误',
    error_desc VARCHAR(200) COMMENT '故障描述',
    occur_time TIMESTAMP NOT NULL COMMENT '故障发生时间',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (signal_controller_id) REFERENCES signal_controller(signal_controller_id) ON DELETE CASCADE,
    INDEX idx_controller_occur_time (signal_controller_id, occur_time)
) COMMENT = '信号机故障表';

-- 37. 路口控制方式方案表 (CrossCtrlInfo)
CREATE TABLE cross_ctrl_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    control_mode CHAR(2) NOT NULL COMMENT '控制方式',
    plan_no SMALLINT UNSIGNED DEFAULT 0 COMMENT '方案序号',
    time TIMESTAMP NOT NULL COMMENT '路口本地时间',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_id_time (cross_id, time)
) COMMENT = '路口控制方式方案表';

-- 38. 路口周期表 (CrossCycle)
CREATE TABLE cross_cycle (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    start_time TIMESTAMP NOT NULL COMMENT '周期开始时间',
    last_cycle_len TINYINT UNSIGNED NOT NULL COMMENT '上周期长度(秒)',
    adjust_flag TINYINT(1) DEFAULT 0 COMMENT '过渡标志：0-否；1-是',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_start_time (cross_id, start_time)
) COMMENT = '路口周期表';

-- 39. 路口阶段表 (CrossStage)
CREATE TABLE cross_stage (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    last_stage_no TINYINT UNSIGNED COMMENT '上个阶段号',
    last_stage_len TINYINT UNSIGNED COMMENT '上个阶段执行时长(秒)',
    cur_stage_no TINYINT UNSIGNED NOT NULL COMMENT '当前阶段号',
    cur_stage_start_time TIMESTAMP NOT NULL COMMENT '当前阶段开始时间',
    cur_stage_len TINYINT UNSIGNED NOT NULL COMMENT '当前阶段已执行时长(秒)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_stage_time (cross_id, cur_stage_start_time)
) COMMENT = '路口阶段表';

-- 40. 路口信号组灯色状态表 (CrossSignalGroupStatus)
CREATE TABLE cross_signal_group_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    lamp_status_time TIMESTAMP(3) NOT NULL COMMENT '灯态开始时间(精确到毫秒)',
    signal_group_no SMALLINT UNSIGNED NOT NULL COMMENT '信号组序号',
    lamp_status CHAR(3) NOT NULL COMMENT '灯色状态',
    remain_time SMALLINT UNSIGNED DEFAULT 0 COMMENT '剩余时长(秒)：0-不确定；1-500-具体时长',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_lamp_time (cross_id, lamp_status_time),
    INDEX idx_cross_signal_group (cross_id, signal_group_no)
) COMMENT = '路口信号组灯色状态表';

-- 41. 路口交通流数据表 (CrossTrafficData) - 分区表
CREATE TABLE cross_traffic_data (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    end_time TIMESTAMP NOT NULL COMMENT '统计截止时间',
    interval_seconds SMALLINT UNSIGNED NOT NULL COMMENT '时间间隔(秒)',
    lane_no TINYINT UNSIGNED NOT NULL COMMENT '车道序号',
    volume SMALLINT UNSIGNED COMMENT '交通流量(辆/小时)',
    avg_veh_len SMALLINT UNSIGNED COMMENT '平均车长(厘米)',
    pcu SMALLINT UNSIGNED COMMENT '小客车当量(pcu/小时)',
    head_distance SMALLINT UNSIGNED COMMENT '平均车头间距(厘米/辆)',
    head_time SMALLINT UNSIGNED COMMENT '平均车头时距(秒/辆)',
    speed FLOAT COMMENT '平均速度(公里/小时)',
    saturation TINYINT UNSIGNED COMMENT '饱和度(0-100)',
    density SMALLINT UNSIGNED COMMENT '平均密度(辆/公里)',
    queue_length SMALLINT UNSIGNED COMMENT '平均排队长度(米)',
    max_queue_length SMALLINT UNSIGNED COMMENT '统计周期内最大排队长度(米)',
    occupancy TINYINT UNSIGNED COMMENT '占有率(0-100)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_end_time (cross_id, end_time),
    INDEX idx_cross_lane_end_time (cross_id, lane_no, end_time)
) COMMENT '路口交通流数据表';

-- 42. 阶段交通流数据表 (StageTrafficData)
CREATE TABLE stage_traffic_data (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    start_time TIMESTAMP NOT NULL COMMENT '阶段开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '阶段结束时间',
    stage_no TINYINT UNSIGNED NOT NULL COMMENT '阶段号',
    lane_no TINYINT UNSIGNED NOT NULL COMMENT '车道序号',
    vehicle_num SMALLINT UNSIGNED COMMENT '过车数量(辆)',
    pcu SMALLINT UNSIGNED COMMENT '小客车当量(pcu/小时)',
    head_time SMALLINT UNSIGNED COMMENT '平均车头时距(秒/辆)',
    saturation TINYINT UNSIGNED COMMENT '饱和度(0-100)',
    queue_length SMALLINT UNSIGNED COMMENT '阶段结束时排队长度(米)',
    occupancy TINYINT UNSIGNED COMMENT '占有率(0-100)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_stage_time (cross_id, stage_no, start_time),
    INDEX idx_cross_lane_stage_time (cross_id, lane_no, start_time)
)COMMENT '阶段交通流数据表';

-- 43. 可变导向车道状态表 (VarLaneStatus)
CREATE TABLE var_lane_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    lane_no TINYINT UNSIGNED NOT NULL COMMENT '车道序号',
    cur_movement CHAR(2) NOT NULL COMMENT '当前转向',
    cur_mode CHAR(2) NOT NULL COMMENT '可变导向车道控制方式：00-恢复信号机控制；11-信号机控制固定方案；12-信号机控制自适应；21-干预控制固定方案；22-干预控制自适应；99-其他',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_lane_var (cross_id, lane_no)
)COMMENT '可变导向车道状态表';

-- 44. 干线控制方式表 (RouteCtrlInfo)
CREATE TABLE route_ctrl_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    route_id CHAR(11) NOT NULL COMMENT '线路编号',
    ctrl_mode CHAR(2) NOT NULL COMMENT '干线控制方式：00-未进行干线协调控制；11-固定方案协调控制；12-自适应协调控制',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES route_param(route_id) ON DELETE CASCADE,
    INDEX idx_route_id_time (route_id, created_time)
)COMMENT '干线控制方式表';

-- 45. 干线路段推荐车速表 (RouteSpeed)
CREATE TABLE route_speed (
    id INT PRIMARY KEY AUTO_INCREMENT,
    route_id CHAR(11) NOT NULL COMMENT '线路编号',
    up_cross_id CHAR(14) NOT NULL COMMENT '上游路口编号',
    down_cross_id CHAR(14) NOT NULL COMMENT '下游路口编号',
    recommend_speed FLOAT NOT NULL COMMENT '推荐车速(公里/小时)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES route_param(route_id) ON DELETE CASCADE,
    UNIQUE KEY uk_route_cross_section (route_id, up_cross_id, down_cross_id)
)COMMENT '干线路段推荐车速表';

-- 46. 信号机柜门状态表 (SCDoorStatus)
CREATE TABLE sc_door_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    signal_controller_id CHAR(18) NOT NULL COMMENT '信号机设备编号',
    time TIMESTAMP NOT NULL COMMENT '时间',
    door_no TINYINT UNSIGNED NOT NULL COMMENT '机柜门序号(1-20)',
    door_name VARCHAR(50) COMMENT '机柜门名称',
    status CHAR(1) NOT NULL COMMENT '机柜门当前状态：0-关闭；1-打开；9-未知',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (signal_controller_id) REFERENCES signal_controller(signal_controller_id) ON DELETE CASCADE,
    INDEX idx_controller_door_time (signal_controller_id, door_no, time)
)COMMENT '信号机柜门状态表';

-- ================================
-- 控制命令数据表
-- ================================

-- 47. 控制命令日志表
CREATE TABLE control_command_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    command_type VARCHAR(50) NOT NULL COMMENT '命令类型',
    cross_id CHAR(14) COMMENT '路口编号',
    command_data JSON NOT NULL COMMENT '命令数据(JSON格式)',
    execute_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    result ENUM('SUCCESS', 'FAILED', 'PENDING') DEFAULT 'PENDING' COMMENT '执行结果',
    error_message TEXT COMMENT '错误信息',
    operator_id VARCHAR(50) COMMENT '操作员ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_command_type_time (command_type, execute_time),
    INDEX idx_cross_id_time (cross_id, execute_time)
)COMMENT '控制命令日志表';

-- 48. 锁定交通流向记录表 (LockFlowDirection)
CREATE TABLE lock_flow_direction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    type TINYINT NOT NULL COMMENT '交通流类型：0-行人；1-机动车；2-非机动车',
    entrance CHAR(1) NOT NULL COMMENT '进口方向',
    `exit` CHAR(1) NOT NULL COMMENT '出口方向',
    lock_type TINYINT NOT NULL COMMENT '锁定类型：1-匹配当前方案；2-单个进口方向放行；3-只放行此流向信号组；4-锁定指定阶段',
    lock_stage_no TINYINT UNSIGNED DEFAULT 0 COMMENT '锁定阶段号',
    duration SMALLINT UNSIGNED NOT NULL COMMENT '锁定持续时长(秒)：0-持续锁定；1-3600-具体时长',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '锁定开始时间',
    end_time TIMESTAMP NULL COMMENT '锁定结束时间',
    status ENUM('ACTIVE', 'EXPIRED', 'UNLOCKED') DEFAULT 'ACTIVE' COMMENT '锁定状态',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_status_time (cross_id, status, start_time)
)COMMENT '锁定交通流向记录表';

-- 49. 数据上传控制表 (CrossReportCtrl)
CREATE TABLE cross_report_ctrl (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    data_type VARCHAR(30) NOT NULL COMMENT '数据类型：CrossCycle-路口周期；CrossStage-路口阶段；CrossSignalGroupStatus-信号组灯色状态；CrossTrafficData-路口交通流数据；StageTrafficData-阶段交通流数据',
    cmd ENUM('Start', 'Stop') NOT NULL COMMENT '命令：Start-开始上传；Stop-停止上传',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE' COMMENT '状态',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cross_data_type (cross_id, data_type)
)COMMENT '数据上传控制表';

-- 50. 中心预案表 (CenterPlan)
CREATE TABLE center_plan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    control_mode CHAR(2) NOT NULL COMMENT '控制方式',
    max_run_time SMALLINT UNSIGNED NOT NULL COMMENT '预案最大运行时长(分钟)',
    plan_data JSON NOT NULL COMMENT '配时方案参数数据',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始执行时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    status ENUM('ACTIVE', 'EXPIRED', 'STOPPED') DEFAULT 'ACTIVE' COMMENT '预案状态',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_status_time (cross_id, status, start_time)
)COMMENT '中心预案表';

-- 51. 阶段干预记录表 (AdjustStage)
CREATE TABLE adjust_stage_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    stage_no TINYINT UNSIGNED COMMENT '干预的阶段号',
    type TINYINT NOT NULL COMMENT '干预类型：1-延长；2-缩短；3-切换到下阶段',
    len SMALLINT UNSIGNED COMMENT '干预时长(秒)',
    execute_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    result ENUM('SUCCESS', 'FAILED') COMMENT '执行结果',
    operator_id VARCHAR(50) COMMENT '操作员ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_execute_time (cross_id, execute_time)
)COMMENT '阶段干预记录表';

-- 52. 可变导向车道控制记录表 (CtrlVarLane)
CREATE TABLE ctrl_var_lane_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cross_id CHAR(14) NOT NULL COMMENT '路口编号',
    lane_no TINYINT UNSIGNED NOT NULL COMMENT '车道序号',
    movement CHAR(2) NOT NULL COMMENT '设置的功能(转向)',
    ctrl_mode CHAR(2) NOT NULL COMMENT '控制模式',
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    status ENUM('ACTIVE', 'EXPIRED', 'STOPPED') DEFAULT 'ACTIVE' COMMENT '状态',
    operator_id VARCHAR(50) COMMENT '操作员ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_lane_time (cross_id, lane_no, start_time)
)COMMENT '可变导向车道控制记录表';

-- ================================
-- 数据字典表
-- ================================

-- 53. 数据字典表
CREATE TABLE data_dictionary (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dict_type VARCHAR(50) NOT NULL COMMENT '字典类型',
    dict_code VARCHAR(10) NOT NULL COMMENT '字典代码',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_value VARCHAR(200) COMMENT '字典值',
    parent_code VARCHAR(10) COMMENT '父级代码',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否有效',
    remark TEXT COMMENT '备注',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dict_type_code (dict_type, dict_code),
    INDEX idx_dict_type (dict_type)
)COMMENT '数据字典表';

-- ================================
-- 协议参数完整信息视图
-- ================================

-- 1. SysInfo协议完整信息视图
CREATE VIEW v_protocol_sys_info_complete AS
SELECT
    si.system_id,
    si.sys_name,
    si.sys_version,
    si.supplier,
    si.is_active as system_active,
    si.description as system_description,

    CASE
        WHEN cross_list.cross_ids IS NOT NULL AND cross_list.cross_ids != ''
        THEN cross_list.cross_ids
        ELSE NULL
    END as cross_id_list,

    CASE
        WHEN sub_region_list.sub_region_ids IS NOT NULL AND sub_region_list.sub_region_ids != ''
        THEN sub_region_list.sub_region_ids
        ELSE NULL
    END as sub_region_id_list,

    CASE
        WHEN route_list.route_ids IS NOT NULL AND route_list.route_ids != ''
        THEN route_list.route_ids
        ELSE NULL
    END as route_id_list,

    CASE
        WHEN region_list.region_ids IS NOT NULL AND region_list.region_ids != ''
        THEN region_list.region_ids
        ELSE NULL
    END as region_id_list,

    CASE
        WHEN controller_list.controller_ids IS NOT NULL AND controller_list.controller_ids != ''
        THEN controller_list.controller_ids
        ELSE NULL
    END as signal_controller_id_list,

    COALESCE(cross_list.cross_count, 0) as cross_count,
    COALESCE(sub_region_list.sub_region_count, 0) as sub_region_count,
    COALESCE(route_list.route_count, 0) as route_count,
    COALESCE(region_list.region_count, 0) as region_count,
    COALESCE(controller_list.controller_count, 0) as controller_count,

    si.created_time as system_created_time,
    si.updated_time as system_updated_time,
    NOW() AS `current_time`

FROM sys_info si

LEFT JOIN (
    SELECT
        scr.system_id,
        GROUP_CONCAT(scr.cross_id ORDER BY scr.cross_id SEPARATOR ',') as cross_ids,
        COUNT(*) as cross_count
    FROM sys_cross_relation scr
    WHERE scr.is_active = 1
        AND (scr.end_time IS NULL OR scr.end_time > NOW())
    GROUP BY scr.system_id
) cross_list ON si.system_id = cross_list.system_id

LEFT JOIN (
    SELECT
        ssrr.system_id,
        GROUP_CONCAT(ssrr.sub_region_id ORDER BY ssrr.sub_region_id SEPARATOR ',') as sub_region_ids,
        COUNT(*) as sub_region_count
    FROM sys_sub_region_relation ssrr
    WHERE ssrr.is_active = 1
    GROUP BY ssrr.system_id
) sub_region_list ON si.system_id = sub_region_list.system_id

LEFT JOIN (
    SELECT
        srr.system_id,
        GROUP_CONCAT(srr.route_id ORDER BY srr.route_id SEPARATOR ',') as route_ids,
        COUNT(*) as route_count
    FROM sys_route_relation srr
    WHERE srr.is_active = 1
    GROUP BY srr.system_id
) route_list ON si.system_id = route_list.system_id

LEFT JOIN (
    SELECT
        srgr.system_id,
        GROUP_CONCAT(srgr.region_id ORDER BY srgr.region_id SEPARATOR ',') as region_ids,
        COUNT(*) as region_count
    FROM sys_region_relation srgr
    WHERE srgr.is_active = 1
    GROUP BY srgr.system_id
) region_list ON si.system_id = region_list.system_id

LEFT JOIN (
    SELECT
        sscr.system_id,
        GROUP_CONCAT(sscr.signal_controller_id ORDER BY sscr.signal_controller_id SEPARATOR ',') as controller_ids,
        COUNT(*) as controller_count
    FROM sys_signal_controller_relation sscr
    WHERE sscr.is_active = 1
    GROUP BY sscr.system_id
) controller_list ON si.system_id = controller_list.system_id

WHERE si.is_active = 1;

-- 2. 区域参数完整信息视图 (RegionParam)
CREATE VIEW v_region_param_complete AS
SELECT
    rp.region_id,
    rp.region_name,
    rp.created_time,
    rp.updated_time,

    CASE
        WHEN sub_region_list.sub_region_ids IS NOT NULL AND sub_region_list.sub_region_ids != ''
        THEN sub_region_list.sub_region_ids
        ELSE NULL
    END as sub_region_id_list,

    CASE
        WHEN cross_list.cross_ids IS NOT NULL AND cross_list.cross_ids != ''
        THEN cross_list.cross_ids
        ELSE NULL
    END as cross_id_list,

    COALESCE(sub_region_list.sub_region_count, 0) as sub_region_count,
    COALESCE(cross_list.cross_count, 0) as direct_cross_count

FROM region_param rp

LEFT JOIN (
    SELECT
        rsr.region_id,
        GROUP_CONCAT(rsr.sub_region_id ORDER BY rsr.sub_region_id SEPARATOR ',') as sub_region_ids,
        COUNT(*) as sub_region_count
    FROM region_sub_region rsr
    GROUP BY rsr.region_id
) sub_region_list ON rp.region_id = sub_region_list.region_id

LEFT JOIN (
    SELECT
        rc.region_id,
        GROUP_CONCAT(rc.cross_id ORDER BY rc.cross_id SEPARATOR ',') as cross_ids,
        COUNT(*) as cross_count
    FROM region_cross rc
    GROUP BY rc.region_id
) cross_list ON rp.region_id = cross_list.region_id;

-- 3. 线路参数完整信息视图 (RouteParam)
CREATE VIEW v_route_param_complete AS
SELECT
    rp.route_id,
    rp.route_name,
    rp.type as route_type,
    rp.created_time,
    rp.updated_time,

    route_cross_list.route_cross_data,

    CASE
        WHEN sub_region_list.sub_region_ids IS NOT NULL AND sub_region_list.sub_region_ids != ''
        THEN sub_region_list.sub_region_ids
        ELSE NULL
    END as sub_region_id_list,

    COALESCE(route_cross_list.cross_count, 0) as cross_count,
    COALESCE(sub_region_list.sub_region_count, 0) as sub_region_count

FROM route_param rp

LEFT JOIN (
    SELECT
        rc.route_id,
        CONCAT('[', GROUP_CONCAT(
            CONCAT('{"CrossID":"', rc.cross_id, '","Distance":', rc.distance, ',"OrderSeq":', rc.order_seq, '}')
            ORDER BY rc.order_seq SEPARATOR ','
        ), ']') as route_cross_data,
        COUNT(*) as cross_count
    FROM route_cross rc
    GROUP BY rc.route_id
) route_cross_list ON rp.route_id = route_cross_list.route_id

LEFT JOIN (
    SELECT
        rsr.route_id,
        GROUP_CONCAT(rsr.sub_region_id ORDER BY rsr.sub_region_id SEPARATOR ',') as sub_region_ids,
        COUNT(*) as sub_region_count
    FROM route_sub_region rsr
    GROUP BY rsr.route_id
    ) sub_region_list ON rp.route_id = sub_region_list.route_id;

-- 4. 子区参数完整信息视图 (SubRegionParam)
CREATE VIEW v_sub_region_param_complete AS
SELECT
    srp.sub_region_id,
    srp.sub_region_name,
    srp.created_time,
    srp.updated_time,

    CASE
        WHEN cross_list.cross_ids IS NOT NULL AND cross_list.cross_ids != ''
        THEN cross_list.cross_ids
        ELSE NULL
    END as cross_id_list,

    CASE
        WHEN key_cross_list.key_cross_ids IS NOT NULL AND key_cross_list.key_cross_ids != ''
        THEN key_cross_list.key_cross_ids
        ELSE NULL
    END as key_cross_id_list,

    COALESCE(cross_list.cross_count, 0) as cross_count,
    COALESCE(key_cross_list.key_cross_count, 0) as key_cross_count

FROM sub_region_param srp

LEFT JOIN (
    SELECT
        src.sub_region_id,
        GROUP_CONCAT(src.cross_id ORDER BY src.cross_id SEPARATOR ',') as cross_ids,
        COUNT(*) as cross_count
    FROM sub_region_cross src
    GROUP BY src.sub_region_id
) cross_list ON srp.sub_region_id = cross_list.sub_region_id

LEFT JOIN (
    SELECT
        src.sub_region_id,
        GROUP_CONCAT(src.cross_id ORDER BY src.cross_id SEPARATOR ',') as key_cross_ids,
        COUNT(*) as key_cross_count
    FROM sub_region_cross src
    WHERE src.is_key_cross = 1
    GROUP BY src.sub_region_id
) key_cross_list ON srp.sub_region_id = key_cross_list.sub_region_id;

-- 5. 信号机参数完整信息视图 (SignalController)
CREATE VIEW v_signal_controller_complete AS
SELECT
    sc.signal_controller_id,
    sc.supplier,
    sc.type,
    sc.id_code,
    sc.comm_mode,
    sc.ip,
    sc.sub_mask,
    sc.gateway,
    sc.port,
    sc.has_door_status,
    sc.longitude,
    sc.latitude,
    sc.created_time,
    sc.updated_time,

    CASE
        WHEN cross_list.cross_ids IS NOT NULL AND cross_list.cross_ids != ''
        THEN cross_list.cross_ids
        ELSE NULL
    END as cross_id_list,

    cross_list.main_cross_id,
    cross_list.main_cross_name,

    COALESCE(cross_list.cross_count, 0) as cross_count

FROM signal_controller sc

LEFT JOIN (
    SELECT
        scc.signal_controller_id,
        GROUP_CONCAT(scc.cross_id ORDER BY scc.is_main DESC, scc.cross_id SEPARATOR ',') as cross_ids,
        COUNT(*) as cross_count,
        MAX(CASE WHEN scc.is_main = 1 THEN scc.cross_id END) as main_cross_id,
        MAX(CASE WHEN scc.is_main = 1 THEN cp.cross_name END) as main_cross_name
    FROM signal_controller_cross scc
    LEFT JOIN cross_param cp ON scc.cross_id = cp.cross_id AND scc.is_main = 1
    GROUP BY scc.signal_controller_id
) cross_list ON sc.signal_controller_id = cross_list.signal_controller_id;

-- 6. 路口参数完整信息视图 (CrossParam)
CREATE VIEW v_cross_param_complete AS
SELECT
    cp.cross_id,
    cp.cross_name,
    cp.feature,
    cp.grade,
    cp.green_conflict_matrix,
    cp.longitude,
    cp.latitude,
    cp.altitude,
    cp.created_time,
    cp.updated_time,

    detector_list.detector_no_list,
    lane_list.lane_no_list,
    pedestrian_list.pedestrian_no_list,
    lamp_group_list.lamp_group_no_list,
    signal_group_list.signal_group_no_list,
    stage_list.stage_no_list,
    plan_list.plan_no_list,
    day_plan_list.day_plan_no_list,
    schedule_list.schedule_no_list,

    COALESCE(detector_list.detector_count, 0) as detector_count,
    COALESCE(lane_list.lane_count, 0) as lane_count,
    COALESCE(pedestrian_list.pedestrian_count, 0) as pedestrian_count,
    COALESCE(lamp_group_list.lamp_group_count, 0) as lamp_group_count,
    COALESCE(signal_group_list.signal_group_count, 0) as signal_group_count,
    COALESCE(stage_list.stage_count, 0) as stage_count,
    COALESCE(plan_list.plan_count, 0) as plan_count,
    COALESCE(day_plan_list.day_plan_count, 0) as day_plan_count,
    COALESCE(schedule_list.schedule_count, 0) as schedule_count

FROM cross_param cp

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(detector_no ORDER BY detector_no SEPARATOR ',') as detector_no_list,
        COUNT(*) as detector_count
    FROM detector_param
    GROUP BY cross_id
) detector_list ON cp.cross_id = detector_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(lane_no ORDER BY lane_no SEPARATOR ',') as lane_no_list,
        COUNT(*) as lane_count
    FROM lane_param
    GROUP BY cross_id
) lane_list ON cp.cross_id = lane_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(pedestrian_no ORDER BY pedestrian_no SEPARATOR ',') as pedestrian_no_list,
        COUNT(*) as pedestrian_count
    FROM pedestrian_param
    GROUP BY cross_id
) pedestrian_list ON cp.cross_id = pedestrian_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(lamp_group_no ORDER BY lamp_group_no SEPARATOR ',') as lamp_group_no_list,
        COUNT(*) as lamp_group_count
    FROM lamp_group_param
    GROUP BY cross_id
) lamp_group_list ON cp.cross_id = lamp_group_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(signal_group_no ORDER BY signal_group_no SEPARATOR ',') as signal_group_no_list,
        COUNT(*) as signal_group_count
    FROM signal_group_param
    GROUP BY cross_id
) signal_group_list ON cp.cross_id = signal_group_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(stage_no ORDER BY stage_no SEPARATOR ',') as stage_no_list,
        COUNT(*) as stage_count
    FROM stage_param
    GROUP BY cross_id
) stage_list ON cp.cross_id = stage_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(plan_no ORDER BY plan_no SEPARATOR ',') as plan_no_list,
        COUNT(*) as plan_count
    FROM plan_param
    GROUP BY cross_id
) plan_list ON cp.cross_id = plan_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(day_plan_no ORDER BY day_plan_no SEPARATOR ',') as day_plan_no_list,
        COUNT(*) as day_plan_count
    FROM day_plan_param
    GROUP BY cross_id
) day_plan_list ON cp.cross_id = day_plan_list.cross_id

LEFT JOIN (
    SELECT
        cross_id,
        GROUP_CONCAT(schedule_no ORDER BY schedule_no SEPARATOR ',') as schedule_no_list,
        COUNT(*) as schedule_count
    FROM schedule_param
    GROUP BY cross_id
) schedule_list ON cp.cross_id = schedule_list.cross_id;

-- ================================
-- 系统管理视图
-- ================================

-- 7. 系统概览视图
CREATE VIEW v_system_overview AS
SELECT
    si.system_id,
    si.sys_name,
    si.sys_version,
    si.supplier,
    si.is_active,
    si.description,

    -- 系统当前状态
    latest_sys_state.sys_status,
    latest_sys_state.last_status_time,

    COALESCE(stats.cross_count, 0) as total_crosses,
    COALESCE(stats.sub_region_count, 0) as total_sub_regions,
    COALESCE(stats.route_count, 0) as total_routes,
    COALESCE(stats.region_count, 0) as total_regions,
    COALESCE(stats.controller_count, 0) as total_controllers,

    COALESCE(active_stats.active_crosses, 0) as active_crosses,
    COALESCE(active_stats.active_controllers, 0) as active_controllers,

    si.created_time,
    si.updated_time

FROM sys_info si

-- 系统最新状态
LEFT JOIN (
    SELECT
        ss.system_id,
        ss.value as sys_status,
        ss.time as last_status_time,
        ROW_NUMBER() OVER (PARTITION BY ss.system_id ORDER BY ss.time DESC) as rn
    FROM sys_state ss
) latest_sys_state ON si.system_id = latest_sys_state.system_id AND latest_sys_state.rn = 1

LEFT JOIN (
    SELECT
        system_id,
        SUM(cross_count) as cross_count,
        SUM(sub_region_count) as sub_region_count,
        SUM(route_count) as route_count,
        SUM(region_count) as region_count,
        SUM(controller_count) as controller_count
    FROM (
        SELECT system_id, COUNT(*) as cross_count, 0 as sub_region_count, 0 as route_count, 0 as region_count, 0 as controller_count
        FROM sys_cross_relation WHERE is_active = 1 GROUP BY system_id
        UNION ALL
        SELECT system_id, 0, COUNT(*), 0, 0, 0 FROM sys_sub_region_relation WHERE is_active = 1 GROUP BY system_id
        UNION ALL
        SELECT system_id, 0, 0, COUNT(*), 0, 0 FROM sys_route_relation WHERE is_active = 1 GROUP BY system_id
        UNION ALL
        SELECT system_id, 0, 0, 0, COUNT(*), 0 FROM sys_region_relation WHERE is_active = 1 GROUP BY system_id
        UNION ALL
        SELECT system_id, 0, 0, 0, 0, COUNT(*) FROM sys_signal_controller_relation WHERE is_active = 1 GROUP BY system_id
    ) combined_stats
    GROUP BY system_id
) stats ON si.system_id = stats.system_id

LEFT JOIN (
    SELECT
        scr.system_id,
        COUNT(DISTINCT CASE WHEN cs.created_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) THEN scr.cross_id END) as active_crosses,
        COUNT(DISTINCT CASE WHEN sce.occur_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) THEN sscr.signal_controller_id END) as active_controllers
    FROM sys_cross_relation scr
    LEFT JOIN cross_state cs ON scr.cross_id = cs.cross_id
    LEFT JOIN sys_signal_controller_relation sscr ON scr.system_id = sscr.system_id
    LEFT JOIN signal_controller_error sce ON sscr.signal_controller_id = sce.signal_controller_id
    WHERE scr.is_active = 1
    GROUP BY scr.system_id
) active_stats ON si.system_id = active_stats.system_id;

-- 8. 路口归属查询视图
CREATE VIEW v_cross_system_mapping AS
SELECT
    cp.cross_id,
    cp.cross_name,
    scr.system_id,
    si.sys_name,
    scr.is_primary,
    scr.priority,
    scr.start_time,
    scr.end_time,
    scr.is_active,
    scr.remark,

    CASE
        WHEN scr.is_active = 0 THEN '已停用'
        WHEN scr.end_time IS NOT NULL AND scr.end_time <= NOW() THEN '已过期'
        ELSE '有效'
    END as status_desc

FROM cross_param cp
LEFT JOIN sys_cross_relation scr ON cp.cross_id = scr.cross_id
LEFT JOIN sys_info si ON scr.system_id = si.system_id
ORDER BY cp.cross_id, scr.priority;

-- ================================
-- 数据字典初始化
-- ================================

-- 路口形状字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('cross_feature', '10', '行人过街', '行人过街', 1),
('cross_feature', '12', '2次行人过街', '2次行人过街', 2),
('cross_feature', '23', 'T形、Y形', 'T形、Y形', 3),
('cross_feature', '24', '十字形', '十字形', 4),
('cross_feature', '35', '五岔路口', '五岔路口', 5),
('cross_feature', '36', '六岔路口', '六岔路口', 6),
('cross_feature', '39', '多岔路口', '多岔路口', 7),
('cross_feature', '40', '环形交叉口(环岛)', '环形交叉口(环岛)', 8),
('cross_feature', '50', '匝道', '匝道', 9),
('cross_feature', '51', '匝道-入口', '匝道-入口', 10),
('cross_feature', '52', '匝道-出口', '匝道-出口', 11),
('cross_feature', '61', '快速路主路路段(交汇区)', '快速路主路路段(交汇区)', 12),
('cross_feature', '90', '其他', '其他', 99);

-- 路口等级字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('cross_grade', '11', '一级', '主干路与主干路相交交叉口', 1),
('cross_grade', '12', '二级', '主干路与次干路相交交叉口', 2),
('cross_grade', '13', '三级', '主干路与支路相交交叉口', 3),
('cross_grade', '21', '四级', '次干路与次干路相交交叉口', 4),
('cross_grade', '22', '五级', '次干路与支路相交交叉口', 5),
('cross_grade', '31', '六级', '支路与支路相交交叉口', 6),
('cross_grade', '99', '其他', '其他', 99);

-- 方向字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('direction', '1', '北', '北', 1),
('direction', '2', '东北', '东北', 2),
('direction', '3', '东', '东', 3),
('direction', '4', '东南', '东南', 4),
('direction', '5', '南', '南', 5),
('direction', '6', '西南', '西南', 6),
('direction', '7', '西', '西', 7),
('direction', '8', '西北', '西北', 8),
('direction', '9', '其他', '其他', 99);

-- 控制方式字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('ctrl_mode', '00', '撤销或恢复自主', '撤销或恢复自主', 1),
('ctrl_mode', '01', '本地手动控制', '本地手动控制', 2),
('ctrl_mode', '11', '特殊控制-全部关灯', '特殊控制-全部关灯', 3),
('ctrl_mode', '12', '特殊控制-全红', '特殊控制-全红', 4),
('ctrl_mode', '13', '特殊控制-全部黄闪', '特殊控制-全部黄闪', 5),
('ctrl_mode', '21', '单点多时段定时控制', '单点多时段定时控制', 6),
('ctrl_mode', '22', '单点感应控制', '单点感应控制', 7),
('ctrl_mode', '23', '单点自适应控制', '单点自适应控制', 8),
('ctrl_mode', '31', '线协调定时控制', '线协调定时控制', 9),
('ctrl_mode', '32', '线协调感应控制', '线协调感应控制', 10),
('ctrl_mode', '33', '线协调自适应控制', '线协调自适应控制', 11),
('ctrl_mode', '41', '区域协调控制', '区域协调控制', 12),
('ctrl_mode', '51', '干预控制-手动控制', '干预控制-手动控制', 13),
('ctrl_mode', '52', '干预控制-锁定阶段', '干预控制-锁定阶段', 14),
('ctrl_mode', '53', '干预控制-指定方案', '干预控制-指定方案', 15);

-- 线路类型字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('route_type', '1', '协调干线', '协调干线', 1),
('route_type', '2', '公交优先线路', '公交优先线路', 2),
('route_type', '3', '特勤线路', '特勤线路', 3),
('route_type', '4', '有轨电车线路', '有轨电车线路', 4),
('route_type', '5', '快速路(沿线匝道路口)', '快速路(沿线匝道路口)', 5),
('route_type', '9', '其他', '其他', 99);

-- 检测器类型字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('detector_type', '1', '线圈', '线圈', 1),
('detector_type', '2', '视频', '视频', 2),
('detector_type', '3', '地磁', '地磁', 3),
('detector_type', '4', '微波', '微波', 4),
('detector_type', '5', '汽车电子标识(RFID)', '汽车电子标识(RFID)', 5),
('detector_type', '6', '雷视一体', '雷视一体', 6),
('detector_type', '9', '其他', '其他', 99);

-- 车道转向属性字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('lane_movement', '11', '直行', '直行', 1),
('lane_movement', '12', '左转', '左转', 2),
('lane_movement', '13', '右转', '右转', 3),
('lane_movement', '21', '直左混行', '直左混行', 4),
('lane_movement', '22', '直右混行', '直右混行', 5),
('lane_movement', '23', '左右混行', '左右混行', 6),
('lane_movement', '24', '直左右混行', '直左右混行', 7),
('lane_movement', '31', '掉头', '掉头', 8),
('lane_movement', '32', '掉头加左转', '掉头加左转', 9),
('lane_movement', '33', '掉头加直行', '掉头加直行', 10),
('lane_movement', '34', '掉头加右转', '掉头加右转', 11),
('lane_movement', '99', '其他', '其他', 99);

-- 信号机故障类型字典
INSERT INTO data_dictionary (dict_type, dict_code, dict_name, dict_value, sort_order) VALUES
('error_type', '1', '灯输出故障', '灯输出故障', 1),
('error_type', '2', '电源故障', '电源故障', 2),
('error_type', '3', '时钟故障', '时钟故障', 3),
('error_type', '4', '运行故障', '运行故障', 4),
('error_type', '5', '方案错误', '方案错误', 5),
('error_type', '9', '其他错误', '其他错误', 99);

-- ================================
-- 存储过程
-- ================================

DELIMITER //

-- 获取路口当前信号组状态的存储过程
CREATE PROCEDURE GetCrossCurrentSignalStatus(IN p_cross_id CHAR(14))
BEGIN
    SELECT
        csgs.cross_id,
        csgs.signal_group_no,
        csgs.lamp_status,
        csgs.remain_time,
        csgs.lamp_status_time,
        sgp.name AS signal_group_name
    FROM cross_signal_group_status csgs
    JOIN signal_group_param sgp ON csgs.cross_id = sgp.cross_id
        AND csgs.signal_group_no = sgp.signal_group_no
    WHERE csgs.cross_id = p_cross_id
        AND csgs.lamp_status_time = (
            SELECT MAX(lamp_status_time)
            FROM cross_signal_group_status
            WHERE cross_id = p_cross_id
        )
    ORDER BY csgs.signal_group_no;
END //

-- 获取路口指定时间段交通流数据的存储过程
CREATE PROCEDURE GetCrossTrafficDataByPeriod(
    IN p_cross_id CHAR(14),
    IN p_start_time TIMESTAMP,
    IN p_end_time TIMESTAMP
)
BEGIN
    SELECT
        ctd.cross_id,
        ctd.end_time,
        ctd.interval_seconds,
        ctd.lane_no,
        lp.direction,
        lp.movement,
        ctd.volume,
        ctd.speed,
        ctd.saturation,
        ctd.occupancy,
        ctd.queue_length,
        ctd.max_queue_length
    FROM cross_traffic_data ctd
    JOIN lane_param lp ON ctd.cross_id = lp.cross_id AND ctd.lane_no = lp.lane_no
    WHERE ctd.cross_id = p_cross_id
        AND ctd.end_time BETWEEN p_start_time AND p_end_time
    ORDER BY ctd.end_time, ctd.lane_no;
END //

-- 插入或更新系统状态的存储过程
CREATE PROCEDURE UpsertSystemState(
    IN p_system_id VARCHAR(20),
    IN p_value ENUM('Online', 'Offline', 'Error'),
    IN p_time TIMESTAMP
)
BEGIN
    INSERT INTO sys_state (system_id, value, time)
    VALUES (p_system_id, p_value, p_time);
END //

-- 清理历史数据的存储过程（保留最近3个月数据）
CREATE PROCEDURE CleanHistoryData()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_table_name VARCHAR(64);
    DECLARE v_sql TEXT;
    DECLARE cleanup_cursor CURSOR FOR
        SELECT TABLE_NAME
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME IN (
            'cross_traffic_data',
            'stage_traffic_data',
            'cross_signal_group_status',
            'cross_cycle',
            'cross_stage',
            'signal_controller_error',
            'control_command_log'
        );
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    SET @cleanup_date = DATE_SUB(CURDATE(), INTERVAL 3 MONTH);

    OPEN cleanup_cursor;
    read_loop: LOOP
        FETCH cleanup_cursor INTO v_table_name;
        IF done THEN
            LEAVE read_loop;
        END IF;

        CASE v_table_name
            WHEN 'cross_traffic_data' THEN
                SET v_sql = CONCAT('DELETE FROM ', v_table_name, ' WHERE end_time < ''', @cleanup_date, '''');
            WHEN 'stage_traffic_data' THEN
                SET v_sql = CONCAT('DELETE FROM ', v_table_name, ' WHERE end_time < ''', @cleanup_date, '''');
            WHEN 'cross_signal_group_status' THEN
                SET v_sql = CONCAT('DELETE FROM ', v_table_name, ' WHERE lamp_status_time < ''', @cleanup_date, '''');
            ELSE
                SET v_sql = CONCAT('DELETE FROM ', v_table_name, ' WHERE created_time < ''', @cleanup_date, '''');
        END CASE;

        SET @sql = v_sql;
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

    END LOOP;
    CLOSE cleanup_cursor;

    SELECT CONCAT('清理完成，删除了 ', @cleanup_date, ' 之前的历史数据') AS result;
END //

-- 添加新分区的存储过程
CREATE PROCEDURE AddMonthlyPartition(IN p_year INT, IN p_month INT)
BEGIN
    DECLARE v_partition_name VARCHAR(20);
    DECLARE v_partition_value INT;
    DECLARE v_sql TEXT;

    SET v_partition_name = CONCAT('p', p_year, LPAD(p_month, 2, '0'));
    SET v_partition_value = p_year * 100 + p_month + 1;

    SET v_sql = CONCAT(
        'ALTER TABLE cross_traffic_data ADD PARTITION (',
        'PARTITION ', v_partition_name, ' VALUES LESS THAN (', v_partition_value, ')',
        ')'
    );

    SET @sql = v_sql;
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SELECT CONCAT('已添加分区: ', v_partition_name) AS result;
END //

DELIMITER ;

-- ================================
-- 触发器
-- ================================

DELIMITER //

-- 确保每个路口至少有一个主控系统的触发器
CREATE TRIGGER tr_check_primary_system
BEFORE UPDATE ON sys_cross_relation
FOR EACH ROW
BEGIN
    IF OLD.is_primary = 1 AND NEW.is_primary = 0 THEN
        IF (SELECT COUNT(*) FROM sys_cross_relation
            WHERE cross_id = NEW.cross_id
              AND is_primary = 1
              AND is_active = 1
              AND system_id != NEW.system_id) = 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '路口必须至少有一个主控系统';
        END IF;
    END IF;
END //

-- 自动更新路口状态的触发器
CREATE TRIGGER tr_update_cross_state_after_ctrl_info
AFTER INSERT ON cross_ctrl_info
FOR EACH ROW
BEGIN
    INSERT INTO cross_state (cross_id, value)
    VALUES (NEW.cross_id, 'Online');
END //

DELIMITER ;

-- ================================
-- 索引优化
-- ================================

-- 为高频查询添加复合索引
CREATE INDEX idx_cross_traffic_data_composite ON cross_traffic_data(cross_id, end_time, lane_no);
CREATE INDEX idx_cross_signal_status_composite ON cross_signal_group_status(cross_id, lamp_status_time, signal_group_no);
CREATE INDEX idx_cross_ctrl_info_composite ON cross_ctrl_info(cross_id, time, control_mode);
CREATE INDEX idx_sys_cross_active_primary ON sys_cross_relation(system_id, is_active, is_primary);

-- ================================
-- 数据库创建完成
-- ================================
