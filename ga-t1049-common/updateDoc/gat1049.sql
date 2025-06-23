-- ============================================================================
-- GA/T 1049.2 交通信号控制系统 MySQL数据库设计
-- 支持客户端、服务端、设备端统一数据访问
-- ============================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS gat1049_traffic
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gat1049_traffic;

-- ============================================================================
-- 1. 系统信息表
-- ============================================================================

-- 系统信息表 (SysInfo)
CREATE TABLE IF NOT EXISTS gat_sys_info (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            device_id VARCHAR(64) NOT NULL UNIQUE COMMENT '设备编号',
    device_name VARCHAR(128) COMMENT '设备名称',
    software_version VARCHAR(32) COMMENT '软件版本',
    hardware_version VARCHAR(32) COMMENT '硬件版本',
    manufacturer VARCHAR(128) COMMENT '制造商',
    model VARCHAR(64) COMMENT '设备型号',
    installation_date DATE COMMENT '安装日期',
    location VARCHAR(256) COMMENT '安装位置',
    contact_info VARCHAR(256) COMMENT '联系信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_device_id (device_id)
    ) ENGINE=InnoDB COMMENT='系统信息表';

-- 系统状态表 (SysState)
CREATE TABLE IF NOT EXISTS gat_sys_state (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             device_id VARCHAR(64) NOT NULL,
    current_time TIMESTAMP COMMENT '当前时间',
    running_state TINYINT COMMENT '运行状态: 0-停止,1-运行,2-故障',
    work_mode TINYINT COMMENT '工作模式: 0-手动,1-自动,2-维护',
    cpu_usage DECIMAL(5,2) COMMENT 'CPU使用率(%)',
    memory_usage DECIMAL(5,2) COMMENT '内存使用率(%)',
    disk_usage DECIMAL(5,2) COMMENT '磁盘使用率(%)',
    network_status TINYINT COMMENT '网络状态: 0-断开,1-连接',
    last_heartbeat TIMESTAMP COMMENT '最后心跳时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES gat_sys_info(device_id) ON DELETE CASCADE,
    INDEX idx_device_id (device_id),
    INDEX idx_current_time (current_time)
    ) ENGINE=InnoDB COMMENT='系统状态表';

-- ============================================================================
-- 2. 区域管理表
-- ============================================================================

-- 区域参数表 (RegionParam)
CREATE TABLE IF NOT EXISTS gat_region_param (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                region_id VARCHAR(64) NOT NULL UNIQUE COMMENT '区域编号',
    region_name VARCHAR(128) NOT NULL COMMENT '区域名称',
    parent_region_id VARCHAR(64) COMMENT '上级区域编号',
    region_level TINYINT COMMENT '区域级别: 1-省,2-市,3-区县,4-其他',
    description TEXT COMMENT '区域描述',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_region_id (region_id),
    INDEX idx_parent_region (parent_region_id)
    ) ENGINE=InnoDB COMMENT='区域参数表';

-- 子区参数表 (SubRegionParam)
CREATE TABLE IF NOT EXISTS gat_sub_region_param (
                                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                    sub_region_id VARCHAR(64) NOT NULL UNIQUE COMMENT '子区编号',
    sub_region_name VARCHAR(128) NOT NULL COMMENT '子区名称',
    region_id VARCHAR(64) NOT NULL COMMENT '所属区域编号',
    description TEXT COMMENT '子区描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (region_id) REFERENCES gat_region_param(region_id) ON DELETE CASCADE,
    INDEX idx_sub_region_id (sub_region_id),
    INDEX idx_region_id (region_id)
    ) ENGINE=InnoDB COMMENT='子区参数表';

-- 线路参数表 (RouteParam)
CREATE TABLE IF NOT EXISTS gat_route_param (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               route_id VARCHAR(64) NOT NULL UNIQUE COMMENT '线路编号',
    route_name VARCHAR(128) NOT NULL COMMENT '线路名称',
    region_id VARCHAR(64) COMMENT '所属区域编号',
    route_type TINYINT COMMENT '线路类型: 1-主干道,2-次干道,3-支路',
    length_km DECIMAL(8,3) COMMENT '线路长度(公里)',
    speed_limit INT COMMENT '限速(km/h)',
    description TEXT COMMENT '线路描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (region_id) REFERENCES gat_region_param(region_id) ON DELETE SET NULL,
    INDEX idx_route_id (route_id),
    INDEX idx_region_id (region_id)
    ) ENGINE=InnoDB COMMENT='线路参数表';

-- ============================================================================
-- 3. 路口管理表
-- ============================================================================

-- 路口参数表 (CrossParam)
CREATE TABLE IF NOT EXISTS gat_cross_param (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               cross_id VARCHAR(64) NOT NULL UNIQUE COMMENT '路口编号',
    cross_name VARCHAR(128) NOT NULL COMMENT '路口名称',
    region_id VARCHAR(64) COMMENT '所属区域编号',
    sub_region_id VARCHAR(64) COMMENT '所属子区编号',
    feature TINYINT COMMENT '路口特征: 0-一般,1-重要,2-关键',
    grade TINYINT COMMENT '路口等级: 1-一级,2-二级,3-三级',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    installation_date DATE COMMENT '安装日期',
    description TEXT COMMENT '路口描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (region_id) REFERENCES gat_region_param(region_id) ON DELETE SET NULL,
    FOREIGN KEY (sub_region_id) REFERENCES gat_sub_region_param(sub_region_id) ON DELETE SET NULL,
    INDEX idx_cross_id (cross_id),
    INDEX idx_region_id (region_id),
    INDEX idx_sub_region_id (sub_region_id)
    ) ENGINE=InnoDB COMMENT='路口参数表';

-- 信号机参数表 (SignalController)
CREATE TABLE IF NOT EXISTS gat_signal_controller (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     controller_id VARCHAR(64) NOT NULL UNIQUE COMMENT '信号机编号',
    controller_name VARCHAR(128) COMMENT '信号机名称',
    cross_id VARCHAR(64) NOT NULL COMMENT '所属路口编号',
    manufacturer VARCHAR(128) COMMENT '制造商',
    model VARCHAR(64) COMMENT '设备型号',
    software_version VARCHAR(32) COMMENT '软件版本',
    hardware_version VARCHAR(32) COMMENT '硬件版本',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    port INT COMMENT '端口号',
    communication_protocol VARCHAR(32) COMMENT '通信协议',
    installation_date DATE COMMENT '安装日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-停用,1-启用,2-故障',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_controller_id (controller_id),
    INDEX idx_cross_id (cross_id),
    INDEX idx_ip_address (ip_address)
    ) ENGINE=InnoDB COMMENT='信号机参数表';

-- ============================================================================
-- 4. 路口设备表
-- ============================================================================

-- 灯组参数表 (LampGroup)
CREATE TABLE IF NOT EXISTS gat_lamp_group (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    lamp_group_no INT NOT NULL COMMENT '灯组序号',
    direction TINYINT COMMENT '进口方向: 1-东,2-南,3-西,4-北',
    lamp_type TINYINT COMMENT '灯组类型: 1-机动车,2-行人,3-非机动车',
    lamp_count INT COMMENT '灯具数量',
    position_x DECIMAL(8,3) COMMENT 'X坐标',
    position_y DECIMAL(8,3) COMMENT 'Y坐标',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, lamp_group_no),
    INDEX idx_cross_id (cross_id)
    ) ENGINE=InnoDB COMMENT='灯组参数表';

-- 检测器参数表 (DetectorParam)
CREATE TABLE IF NOT EXISTS gat_detector_param (
                                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                  cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    detector_no INT NOT NULL COMMENT '检测器序号',
    detector_name VARCHAR(128) COMMENT '检测器名称',
    detector_type TINYINT COMMENT '检测器类型: 1-线圈,2-视频,3-雷达,4-微波',
    lane_no INT COMMENT '所属车道序号',
    direction TINYINT COMMENT '进口方向: 1-东,2-南,3-西,4-北',
    distance_to_stopline DECIMAL(6,2) COMMENT '距停车线距离(米)',
    detection_length DECIMAL(6,2) COMMENT '检测区长度(米)',
    detection_width DECIMAL(6,2) COMMENT '检测区宽度(米)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-停用,1-启用,2-故障',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, detector_no),
    INDEX idx_cross_id (cross_id),
    INDEX idx_lane_no (lane_no)
    ) ENGINE=InnoDB COMMENT='检测器参数表';

-- 车道参数表 (LaneParam)
CREATE TABLE IF NOT EXISTS gat_lane_param (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    lane_no INT NOT NULL COMMENT '车道序号',
    lane_name VARCHAR(128) COMMENT '车道名称',
    direction TINYINT COMMENT '进口方向: 1-东,2-南,3-西,4-北',
    lane_type TINYINT COMMENT '车道类型: 1-直行,2-左转,3-右转,4-直左,5-直右,6-左右',
    lane_width DECIMAL(4,2) COMMENT '车道宽度(米)',
    length DECIMAL(6,2) COMMENT '车道长度(米)',
    signal_group_no INT COMMENT '控制信号组序号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, lane_no),
    INDEX idx_cross_id (cross_id),
    INDEX idx_signal_group (signal_group_no)
    ) ENGINE=InnoDB COMMENT='车道参数表';

-- 行人参数表 (PedestrianParam)
CREATE TABLE IF NOT EXISTS gat_pedestrian_param (
                                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                    cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    pedestrian_no INT NOT NULL COMMENT '行人通道序号',
    pedestrian_name VARCHAR(128) COMMENT '行人通道名称',
    direction TINYINT COMMENT '方向: 1-东,2-南,3-西,4-北',
    width DECIMAL(4,2) COMMENT '通道宽度(米)',
    length DECIMAL(6,2) COMMENT '通道长度(米)',
    signal_group_no INT COMMENT '控制信号组序号',
    has_button TINYINT DEFAULT 0 COMMENT '是否有按钮: 0-无,1-有',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, pedestrian_no),
    INDEX idx_cross_id (cross_id)
    ) ENGINE=InnoDB COMMENT='行人参数表';

-- ============================================================================
-- 5. 信号控制表
-- ============================================================================

-- 信号组参数表 (SignalGroupParam)
CREATE TABLE IF NOT EXISTS gat_signal_group_param (
                                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                      cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    signal_group_no INT NOT NULL COMMENT '信号组序号',
    signal_group_name VARCHAR(128) COMMENT '信号组名称',
    green_flush_len INT COMMENT '绿闪时长(秒)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, signal_group_no),
    INDEX idx_cross_id (cross_id)
    ) ENGINE=InnoDB COMMENT='信号组参数表';

-- 信号组关联灯组表
CREATE TABLE IF NOT EXISTS gat_signal_group_lamp (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    signal_group_no INT NOT NULL COMMENT '信号组序号',
    lamp_group_no INT NOT NULL COMMENT '灯组序号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id, signal_group_no) REFERENCES gat_signal_group_param(cross_id, signal_group_no) ON DELETE CASCADE,
    FOREIGN KEY (cross_id, lamp_group_no) REFERENCES gat_lamp_group(cross_id, lamp_group_no) ON DELETE CASCADE,
    UNIQUE KEY uk_signal_lamp (cross_id, signal_group_no, lamp_group_no)
    ) ENGINE=InnoDB COMMENT='信号组关联灯组表';

-- 阶段参数表 (StageParam)
CREATE TABLE IF NOT EXISTS gat_stage_param (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    stage_no INT NOT NULL COMMENT '阶段序号',
    stage_name VARCHAR(128) COMMENT '阶段名称',
    attribute TINYINT DEFAULT 0 COMMENT '特征: 0-一般,1-感应',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, stage_no),
    INDEX idx_cross_id (cross_id)
    ) ENGINE=InnoDB COMMENT='阶段参数表';

-- 阶段信号组状态表
CREATE TABLE IF NOT EXISTS gat_stage_signal_status (
                                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                       cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    stage_no INT NOT NULL COMMENT '阶段序号',
    signal_group_no INT NOT NULL COMMENT '信号组序号',
    lamp_status TINYINT COMMENT '灯态: 1-红,2-黄,3-绿,4-红黄,5-绿闪,6-黄闪,7-全闪,8-熄灭',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id, stage_no) REFERENCES gat_stage_param(cross_id, stage_no) ON DELETE CASCADE,
    FOREIGN KEY (cross_id, signal_group_no) REFERENCES gat_signal_group_param(cross_id, signal_group_no) ON DELETE CASCADE,
    UNIQUE KEY uk_stage_signal (cross_id, stage_no, signal_group_no)
    ) ENGINE=InnoDB COMMENT='阶段信号组状态表';

-- 配时方案参数表 (PlanParam)
CREATE TABLE IF NOT EXISTS gat_plan_param (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    plan_no INT NOT NULL COMMENT '方案序号',
    plan_name VARCHAR(128) COMMENT '方案名称',
    cycle_len INT NOT NULL COMMENT '周期长度(秒)',
    coord_stage_no INT COMMENT '协调相位号',
    offset INT COMMENT '协调相位差(秒)',
    description TEXT COMMENT '方案描述',
    is_active TINYINT DEFAULT 0 COMMENT '是否激活: 0-否,1-是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, plan_no),
    INDEX idx_cross_id (cross_id),
    INDEX idx_is_active (is_active)
    ) ENGINE=InnoDB COMMENT='配时方案参数表';

-- 阶段配时表
CREATE TABLE IF NOT EXISTS gat_stage_timing (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    plan_no INT NOT NULL COMMENT '方案序号',
    stage_no INT NOT NULL COMMENT '阶段序号',
    min_green INT COMMENT '最小绿时(秒)',
    max_green INT COMMENT '最大绿时(秒)',
    yellow_time INT COMMENT '黄时(秒)',
    red_time INT COMMENT '红时(秒)',
    stage_order INT COMMENT '阶段顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id, plan_no) REFERENCES gat_plan_param(cross_id, plan_no) ON DELETE CASCADE,
    FOREIGN KEY (cross_id, stage_no) REFERENCES gat_stage_param(cross_id, stage_no) ON DELETE CASCADE,
    UNIQUE KEY uk_plan_stage (cross_id, plan_no, stage_no)
    ) ENGINE=InnoDB COMMENT='阶段配时表';

-- ============================================================================
-- 6. 调度管理表
-- ============================================================================

-- 日计划参数表 (DayPlanParam)
CREATE TABLE IF NOT EXISTS gat_day_plan_param (
                                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                  cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    day_plan_no INT NOT NULL COMMENT '日计划序号',
    day_plan_name VARCHAR(128) COMMENT '日计划名称',
    plan_date DATE COMMENT '计划日期',
    is_template TINYINT DEFAULT 0 COMMENT '是否模板: 0-否,1-是',
    description TEXT COMMENT '计划描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, day_plan_no),
    INDEX idx_cross_id (cross_id),
    INDEX idx_plan_date (plan_date)
    ) ENGINE=InnoDB COMMENT='日计划参数表';

-- 日计划时段表
CREATE TABLE IF NOT EXISTS gat_day_plan_period (
                                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                   cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    day_plan_no INT NOT NULL COMMENT '日计划序号',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    plan_no INT NOT NULL COMMENT '使用的配时方案序号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id, day_plan_no) REFERENCES gat_day_plan_param(cross_id, day_plan_no) ON DELETE CASCADE,
    FOREIGN KEY (cross_id, plan_no) REFERENCES gat_plan_param(cross_id, plan_no) ON DELETE CASCADE,
    INDEX idx_cross_day_plan (cross_id, day_plan_no),
    INDEX idx_time_range (start_time, end_time)
    ) ENGINE=InnoDB COMMENT='日计划时段表';

-- 调度参数表 (ScheduleParam)
CREATE TABLE IF NOT EXISTS gat_schedule_param (
                                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                  cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    schedule_no INT NOT NULL COMMENT '调度序号',
    schedule_name VARCHAR(128) COMMENT '调度名称',
    schedule_type TINYINT COMMENT '调度类型: 1-周调度,2-月调度,3-年调度',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    is_active TINYINT DEFAULT 0 COMMENT '是否激活: 0-否,1-是',
    description TEXT COMMENT '调度描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    PRIMARY KEY (cross_id, schedule_no),
    INDEX idx_cross_id (cross_id),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_is_active (is_active)
    ) ENGINE=InnoDB COMMENT='调度参数表';

-- 调度日计划关联表
CREATE TABLE IF NOT EXISTS gat_schedule_day_plan (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    schedule_no INT NOT NULL COMMENT '调度序号',
    weekday TINYINT NOT NULL COMMENT '星期几: 1-周一,7-周日',
    day_plan_no INT NOT NULL COMMENT '日计划序号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id, schedule_no) REFERENCES gat_schedule_param(cross_id, schedule_no) ON DELETE CASCADE,
    FOREIGN KEY (cross_id, day_plan_no) REFERENCES gat_day_plan_param(cross_id, day_plan_no) ON DELETE CASCADE,
    UNIQUE KEY uk_schedule_weekday (cross_id, schedule_no, weekday)
    ) ENGINE=InnoDB COMMENT='调度日计划关联表';

-- ============================================================================
-- 7. 运行时数据表
-- ============================================================================

-- 路口状态表 (CrossState)
CREATE TABLE IF NOT EXISTS gat_cross_state (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    current_time TIMESTAMP COMMENT '当前时间',
    control_mode TINYINT COMMENT '控制方式: 1-手动,2-自动,3-感应,4-协调',
    current_plan_no INT COMMENT '当前方案序号',
    current_stage_no INT COMMENT '当前阶段序号',
    stage_remaining_time INT COMMENT '阶段剩余时间(秒)',
    cycle_remaining_time INT COMMENT '周期剩余时间(秒)',
    fault_status INT DEFAULT 0 COMMENT '故障状态位',
    communication_status TINYINT DEFAULT 1 COMMENT '通信状态: 0-断开,1-正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_cross_id (cross_id),
    INDEX idx_current_time (current_time)
    ) ENGINE=InnoDB COMMENT='路口状态表';

-- 信号机故障表 (SignalControllerError)
CREATE TABLE IF NOT EXISTS gat_signal_controller_error (
                                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                           controller_id VARCHAR(64) NOT NULL COMMENT '信号机编号',
    cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    error_code INT NOT NULL COMMENT '故障代码',
    error_type TINYINT COMMENT '故障类型: 1-硬件,2-软件,3-通信,4-其他',
    error_level TINYINT COMMENT '故障级别: 1-轻微,2-一般,3-严重,4-致命',
    error_message TEXT COMMENT '故障描述',
    occurrence_time TIMESTAMP NOT NULL COMMENT '发生时间',
    recovery_time TIMESTAMP COMMENT '恢复时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-未处理,2-处理中,3-已处理',
    handled_by VARCHAR(128) COMMENT '处理人',
    handle_note TEXT COMMENT '处理说明',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    INDEX idx_controller_id (controller_id),
    INDEX idx_cross_id (cross_id),
    INDEX idx_occurrence_time (occurrence_time),
    INDEX idx_status (status)
    ) ENGINE=InnoDB COMMENT='信号机故障表';

-- 交通流数据表 (CrossTrafficData)
CREATE TABLE IF NOT EXISTS gat_cross_traffic_data (
                                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                      cross_id VARCHAR(64) NOT NULL COMMENT '路口编号',
    detector_no INT NOT NULL COMMENT '检测器序号',
    data_time TIMESTAMP NOT NULL COMMENT '数据时间',
    period_minutes INT DEFAULT 5 COMMENT '统计周期(分钟)',
    vehicle_count INT DEFAULT 0 COMMENT '车辆数(辆)',
    volume DECIMAL(8,2) DEFAULT 0 COMMENT '流量(辆/小时)',
    occupancy DECIMAL(5,2) DEFAULT 0 COMMENT '占有率(%)',
    avg_speed DECIMAL(5,1) DEFAULT 0 COMMENT '平均速度(km/h)',
    avg_headway DECIMAL(6,2) DEFAULT 0 COMMENT '平均车头时距(秒)',
    queue_length DECIMAL(6,1) DEFAULT 0 COMMENT '排队长度(米)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cross_id) REFERENCES gat_cross_param(cross_id) ON DELETE CASCADE,
    FOREIGN KEY (cross_id, detector_no) REFERENCES gat_detector_param(cross_id, detector_no) ON DELETE CASCADE,
    INDEX idx_cross_id (cross_id),
    INDEX idx_data_time (data_time),
    INDEX idx_cross_time (cross_id, data_time)
    ) ENGINE=InnoDB COMMENT='交通流数据表';

-- ============================================================================
-- 8. 系统日志表
-- ============================================================================

-- 操作日志表
CREATE TABLE IF NOT EXISTS gat_operation_log (
                                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                 user_id VARCHAR(64) COMMENT '用户ID',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型',
    operation_desc VARCHAR(256) COMMENT '操作描述',
    target_object VARCHAR(64) COMMENT '操作对象',
    target_id VARCHAR(64) COMMENT '对象ID',
    ip_address VARCHAR(45) COMMENT '操作IP',
    user_agent VARCHAR(256) COMMENT '用户代理',
    request_data TEXT COMMENT '请求数据',
    response_data TEXT COMMENT '响应数据',
    status TINYINT COMMENT '执行状态: 1-成功,0-失败',
    error_message TEXT COMMENT '错误信息',
    execution_time INT COMMENT '执行时间(毫秒)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
    ) ENGINE=InnoDB COMMENT='操作日志表';

-- 通信日志表
CREATE TABLE IF NOT EXISTS gat_communication_log (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     session_id VARCHAR(64) COMMENT '会话ID',
    message_type TINYINT COMMENT '消息类型: 1-REQUEST,2-RESPONSE,3-PUSH,4-ERROR',
    source_address VARCHAR(128) COMMENT '源地址',
    target_address VARCHAR(128) COMMENT '目标地址',
    operation_name VARCHAR(64) COMMENT '操作名称',
    object_name VARCHAR(64) COMMENT '对象名称',
    sequence_no VARCHAR(32) COMMENT '序列号',
    message_size INT COMMENT '消息大小(字节)',
    processing_time INT COMMENT '处理时间(毫秒)',
    status TINYINT COMMENT '状态: 1-成功,0-失败',
    error_code VARCHAR(16) COMMENT '错误代码',
    error_message TEXT COMMENT '错误信息',
    raw_message LONGTEXT COMMENT '原始消息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_message_type (message_type),
    INDEX idx_operation_name (operation_name),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
    ) ENGINE=InnoDB COMMENT='通信日志表';

-- ============================================================================
-- 9. 配置管理表
-- ============================================================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS gat_system_config (
                                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                 config_key VARCHAR(128) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(32) COMMENT '配置类型: string,number,boolean,json',
    category VARCHAR(64) COMMENT '配置分类',
    description VARCHAR(256) COMMENT '配置描述',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置: 0-否,1-是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key),
    INDEX idx_category (category)
    ) ENGINE=InnoDB COMMENT='系统配置表';

-- 用户会话表
CREATE TABLE IF NOT EXISTS gat_user_session (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                session_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    username VARCHAR(64) COMMENT '用户名',
    client_ip VARCHAR(45) COMMENT '客户端IP',
    client_type VARCHAR(32) COMMENT '客户端类型',
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后活动时间',
    expires_at TIMESTAMP COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-活跃,2-过期,3-注销',
    token VARCHAR(256) COMMENT '访问令牌',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_expires_at (expires_at)
    ) ENGINE=InnoDB COMMENT='用户会话表';

-- ============================================================================
-- 10. 数据初始化
-- ============================================================================

-- 插入默认系统配置
INSERT INTO gat_system_config (config_key, config_value, config_type, category, description, is_system) VALUES
                                                                                                            ('system.name', 'GA/T 1049交通信号控制系统', 'string', 'system', '系统名称', 1),
                                                                                                            ('system.version', '1.0.0', 'string', 'system', '系统版本', 1),
                                                                                                            ('protocol.version', '1.0', 'string', 'protocol', '协议版本', 1),
                                                                                                            ('communication.timeout', '30000', 'number', 'communication', '通信超时时间(毫秒)', 1),
                                                                                                            ('session.timeout', '3600', 'number', 'session', '会话超时时间(秒)', 1),
                                                                                                            ('heartbeat.interval', '30', 'number', 'heartbeat', '心跳间隔(秒)', 1),
                                                                                                            ('log.retention.days', '30', 'number', 'log', '日志保留天数', 1),
                                                                                                            ('traffic.data.interval', '300', 'number', 'traffic', '交通数据采集间隔(秒)', 1),
                                                                                                            ('max.concurrent.sessions', '1000', 'number', 'session', '最大并发会话数', 1),
                                                                                                            ('data.backup.enabled', 'true', 'boolean', 'backup', '是否启用数据备份', 1);

-- 插入示例区域数据
INSERT INTO gat_region_param (region_id, region_name, region_level, description) VALUES
                                                                                     ('110000', '北京市', 2, '直辖市'),
                                                                                     ('110100', '北京市市区', 3, '北京市中心城区'),
                                                                                     ('320000', '江苏省', 1, '省级行政区'),
                                                                                     ('320100', '南京市', 2, '江苏省省会'),
                                                                                     ('320101', '玄武区', 3, '南京市玄武区');

-- 插入示例子区数据
INSERT INTO gat_sub_region_param (sub_region_id, sub_region_name, region_id, description) VALUES
                                                                                              ('11010001', '东城片区', '110100', '北京市东城区域'),
                                                                                              ('11010002', '西城片区', '110100', '北京市西城区域'),
                                                                                              ('32010101', '新街口片区', '320101', '南京市玄武区新街口片区'),
                                                                                              ('32010102', '珠江路片区', '320101', '南京市玄武区珠江路片区');

-- 插入示例路线数据
INSERT INTO gat_route_param (route_id, route_name, region_id, route_type, length_km, speed_limit, description) VALUES
                                                                                                                   ('R001', '长安街', '110100', 1, 13.4, 60, '北京市长安街主干道'),
                                                                                                                   ('R002', '中山路', '320101', 1, 8.2, 50, '南京市中山路主干道'),
                                                                                                                   ('R003', '珠江路', '320101', 2, 5.6, 40, '南京市珠江路次干道');

-- 插入示例路口数据
INSERT INTO gat_cross_param (cross_id, cross_name, region_id, sub_region_id, feature, grade, longitude, latitude, description) VALUES
                                                                                                                                   ('C001001', '天安门东路口', '110100', '11010001', 2, 1, 116.397128, 39.916527, '重要路口'),
                                                                                                                                   ('C001002', '王府井路口', '110100', '11010001', 1, 1, 116.417592, 39.918837, '商业区路口'),
                                                                                                                                   ('C002001', '新街口路口', '320101', '32010101', 2, 1, 118.778074, 32.041544, '市中心路口'),
                                                                                                                                   ('C002002', '珠江路路口', '320101', '32010102', 1, 2, 118.792258, 32.041935, '科技园区路口');

-- 插入示例信号机数据
INSERT INTO gat_signal_controller (controller_id, controller_name, cross_id, manufacturer, model, software_version, ip_address, port) VALUES
                                                                                                                                          ('SC001001', '天安门东信号机', 'C001001', '海信网络科技', 'HiSmart-3000', 'V3.2.1', '192.168.1.101', 8080),
                                                                                                                                          ('SC001002', '王府井信号机', 'C001002', '海信网络科技', 'HiSmart-3000', 'V3.2.1', '192.168.1.102', 8080),
                                                                                                                                          ('SC002001', '新街口信号机', 'C002001', '易华录', 'EHR-2000', 'V2.1.3', '192.168.2.101', 8080),
                                                                                                                                          ('SC002002', '珠江路信号机', 'C002002', '易华录', 'EHR-2000', 'V2.1.3', '192.168.2.102', 8080);

-- ============================================================================
-- 11. 视图定义
-- ============================================================================

-- 路口完整信息视图
CREATE OR REPLACE VIEW v_cross_full_info AS
SELECT
    c.cross_id,
    c.cross_name,
    r.region_name,
    sr.sub_region_name,
    c.feature,
    c.grade,
    c.longitude,
    c.latitude,
    sc.controller_id,
    sc.controller_name,
    sc.ip_address,
    COUNT(DISTINCT lg.lamp_group_no) as lamp_group_count,
    COUNT(DISTINCT dp.detector_no) as detector_count,
    COUNT(DISTINCT lp.lane_no) as lane_count,
    COUNT(DISTINCT sgp.signal_group_no) as signal_group_count,
    COUNT(DISTINCT stp.stage_no) as stage_count,
    COUNT(DISTINCT pp.plan_no) as plan_count
FROM gat_cross_param c
         LEFT JOIN gat_region_param r ON c.region_id = r.region_id
         LEFT JOIN gat_sub_region_param sr ON c.sub_region_id = sr.sub_region_id
         LEFT JOIN gat_signal_controller sc ON c.cross_id = sc.cross_id
         LEFT JOIN gat_lamp_group lg ON c.cross_id = lg.cross_id
         LEFT JOIN gat_detector_param dp ON c.cross_id = dp.cross_id
         LEFT JOIN gat_lane_param lp ON c.cross_id = lp.cross_id
         LEFT JOIN gat_signal_group_param sgp ON c.cross_id = sgp.cross_id
         LEFT JOIN gat_stage_param stp ON c.cross_id = stp.cross_id
         LEFT JOIN gat_plan_param pp ON c.cross_id = pp.cross_id
GROUP BY c.cross_id, c.cross_name, r.region_name, sr.sub_region_name,
         c.feature, c.grade, c.longitude, c.latitude,
         sc.controller_id, sc.controller_name, sc.ip_address;

-- 路口当前状态视图
CREATE OR REPLACE VIEW v_cross_current_status AS
SELECT
    cs.cross_id,
    c.cross_name,
    cs.current_time,
    cs.control_mode,
    cs.current_plan_no,
    pp.plan_name,
    cs.current_stage_no,
    stp.stage_name,
    cs.stage_remaining_time,
    cs.cycle_remaining_time,
    cs.fault_status,
    cs.communication_status,
    sc.controller_id,
    sc.ip_address,
    CASE
        WHEN cs.communication_status = 1 AND cs.fault_status = 0 THEN '正常'
        WHEN cs.communication_status = 0 THEN '通信中断'
        WHEN cs.fault_status > 0 THEN '设备故障'
        ELSE '未知'
        END as status_desc
FROM gat_cross_state cs
         LEFT JOIN gat_cross_param c ON cs.cross_id = c.cross_id
         LEFT JOIN gat_signal_controller sc ON cs.cross_id = sc.cross_id
         LEFT JOIN gat_plan_param pp ON cs.cross_id = pp.cross_id AND cs.current_plan_no = pp.plan_no
         LEFT JOIN gat_stage_param stp ON cs.cross_id = stp.cross_id AND cs.current_stage_no = stp.stage_no;

-- 交通流统计视图
CREATE OR REPLACE VIEW v_traffic_statistics AS
SELECT
    ctd.cross_id,
    c.cross_name,
    DATE(ctd.data_time) as stat_date,
    HOUR(ctd.data_time) as stat_hour,
    COUNT(*) as record_count,
    SUM(ctd.vehicle_count) as total_vehicles,
    AVG(ctd.volume) as avg_volume,
    AVG(ctd.occupancy) as avg_occupancy,
    AVG(ctd.avg_speed) as avg_speed,
    MAX(ctd.queue_length) as max_queue_length
FROM gat_cross_traffic_data ctd
    LEFT JOIN gat_cross_param c ON ctd.cross_id = c.cross_id
GROUP BY ctd.cross_id, c.cross_name, DATE(ctd.data_time), HOUR(ctd.data_time);

-- ============================================================================
-- 12. 存储过程
-- ============================================================================

DELIMITER //

-- 获取路口配置信息存储过程
CREATE PROCEDURE sp_get_cross_config(IN p_cross_id VARCHAR(64))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

START TRANSACTION;

-- 路口基本信息
SELECT * FROM gat_cross_param WHERE cross_id = p_cross_id;

-- 信号机信息
SELECT * FROM gat_signal_controller WHERE cross_id = p_cross_id;

-- 灯组信息
SELECT * FROM gat_lamp_group WHERE cross_id = p_cross_id ORDER BY lamp_group_no;

-- 检测器信息
SELECT * FROM gat_detector_param WHERE cross_id = p_cross_id ORDER BY detector_no;

-- 车道信息
SELECT * FROM gat_lane_param WHERE cross_id = p_cross_id ORDER BY lane_no;

-- 信号组信息
SELECT sgp.*,
       GROUP_CONCAT(sgl.lamp_group_no ORDER BY sgl.lamp_group_no) as lamp_groups
FROM gat_signal_group_param sgp
         LEFT JOIN gat_signal_group_lamp sgl ON sgp.cross_id = sgl.cross_id
    AND sgp.signal_group_no = sgl.signal_group_no
WHERE sgp.cross_id = p_cross_id
GROUP BY sgp.cross_id, sgp.signal_group_no
ORDER BY sgp.signal_group_no;

-- 阶段信息
SELECT * FROM gat_stage_param WHERE cross_id = p_cross_id ORDER BY stage_no;

-- 配时方案信息
SELECT * FROM gat_plan_param WHERE cross_id = p_cross_id ORDER BY plan_no;

COMMIT;
END //

-- 更新路口状态存储过程
CREATE PROCEDURE sp_update_cross_state(
    IN p_cross_id VARCHAR(64),
    IN p_control_mode TINYINT,
    IN p_current_plan_no INT,
    IN p_current_stage_no INT,
    IN p_stage_remaining_time INT,
    IN p_cycle_remaining_time INT,
    IN p_fault_status INT,
    IN p_communication_status TINYINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

START TRANSACTION;

INSERT INTO gat_cross_state (
    cross_id, current_time, control_mode, current_plan_no,
    current_stage_no, stage_remaining_time, cycle_remaining_time,
    fault_status, communication_status
) VALUES (
             p_cross_id, NOW(), p_control_mode, p_current_plan_no,
             p_current_stage_no, p_stage_remaining_time, p_cycle_remaining_time,
             p_fault_status, p_communication_status
         )
    ON DUPLICATE KEY UPDATE
                         current_time = NOW(),
                         control_mode = p_control_mode,
                         current_plan_no = p_current_plan_no,
                         current_stage_no = p_current_stage_no,
                         stage_remaining_time = p_stage_remaining_time,
                         cycle_remaining_time = p_cycle_remaining_time,
                         fault_status = p_fault_status,
                         communication_status = p_communication_status;

COMMIT;
END //

-- 记录交通流数据存储过程
CREATE PROCEDURE sp_record_traffic_data(
    IN p_cross_id VARCHAR(64),
    IN p_detector_no INT,
    IN p_vehicle_count INT,
    IN p_volume DECIMAL(8,2),
    IN p_occupancy DECIMAL(5,2),
    IN p_avg_speed DECIMAL(5,1),
    IN p_avg_headway DECIMAL(6,2),
    IN p_queue_length DECIMAL(6,1)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

START TRANSACTION;

INSERT INTO gat_cross_traffic_data (
    cross_id, detector_no, data_time, vehicle_count, volume,
    occupancy, avg_speed, avg_headway, queue_length
) VALUES (
             p_cross_id, p_detector_no, NOW(), p_vehicle_count, p_volume,
             p_occupancy, p_avg_speed, p_avg_headway, p_queue_length
         );

COMMIT;
END //

DELIMITER ;

-- ============================================================================
-- 13. 触发器
-- ============================================================================

-- 路口参数更新触发器
DELIMITER //
CREATE TRIGGER tr_cross_param_update
    AFTER UPDATE ON gat_cross_param
    FOR EACH ROW
BEGIN
    INSERT INTO gat_operation_log (
        operation_type, operation_desc, target_object, target_id,
        request_data, status, created_at
    ) VALUES (
                 'UPDATE', '路口参数更新', 'CrossParam', NEW.cross_id,
                 CONCAT('从:', OLD.cross_name, ' 到:', NEW.cross_name), 1, NOW()
             );
END //

-- 信号机故障插入触发器
CREATE TRIGGER tr_signal_error_insert
    AFTER INSERT ON gat_signal_controller_error
    FOR EACH ROW
BEGIN
    -- 更新路口故障状态
    UPDATE gat_cross_state
    SET fault_status = fault_status | POW(2, NEW.error_type - 1)
    WHERE cross_id = NEW.cross_id;
END //

DELIMITER ;

-- ============================================================================
-- 14. 索引优化
-- ============================================================================

-- 为高频查询添加复合索引
CREATE INDEX idx_cross_state_time_status ON gat_cross_state(cross_id, current_time, communication_status);
CREATE INDEX idx_traffic_data_cross_time ON gat_cross_traffic_data(cross_id, data_time DESC);
CREATE INDEX idx_error_cross_time_status ON gat_signal_controller_error(cross_id, occurrence_time DESC, status);
CREATE INDEX idx_operation_log_time_type ON gat_operation_log(created_at DESC, operation_type);
CREATE INDEX idx_communication_log_time ON gat_communication_log(created_at DESC);

-- ============================================================================
-- 15. 数据库维护
-- ============================================================================

-- 清理过期数据事件
CREATE EVENT IF NOT EXISTS evt_cleanup_old_data
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- 清理30天前的交通流数据
DELETE FROM gat_cross_traffic_data
WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 清理7天前的通信日志
DELETE FROM gat_communication_log
WHERE created_at < DATE_SUB(NOW(), INTERVAL 7 DAY);

-- 清理90天前的操作日志
DELETE FROM gat_operation_log
WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- 清理过期会话
DELETE FROM gat_user_session
WHERE expires_at < NOW() OR status != 1;
END;

-- 启用事件调度器
SET GLOBAL event_scheduler = ON;

-- ============================================================================
-- 脚本执行完成
-- ============================================================================

SELECT 'GA/T 1049.2 MySQL数据库初始化完成!' as message;
SELECT COUNT(*) as table_count FROM information_schema.tables
WHERE table_schema = 'gat1049_traffic' AND table_type = 'BASE TABLE';

-- 显示创建的表
SELECT table_name, table_comment
FROM information_schema.tables
WHERE table_schema = 'gat1049_traffic' AND table_type = 'BASE TABLE'
ORDER BY table_name;