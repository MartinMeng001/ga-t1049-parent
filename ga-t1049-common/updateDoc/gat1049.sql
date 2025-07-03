-- ============================================================================
-- GA/T 1049.2 信号机品牌无关架构 MySQL数据库设计 - 修复版本
-- 支持海信、易华录等多品牌信号机统一接入
-- ============================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS gat1049_traffic
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gat1049_traffic;

-- ============================================================================
-- 1. 系统管理表
-- ============================================================================

-- 系统信息表
CREATE TABLE IF NOT EXISTS gat_sys_info (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            system_id VARCHAR(64) NOT NULL UNIQUE COMMENT '系统编号',
    system_name VARCHAR(128) NOT NULL COMMENT '系统名称',
    software_version VARCHAR(32) COMMENT '软件版本',
    hardware_version VARCHAR(32) COMMENT '硬件版本',
    manufacturer VARCHAR(128) COMMENT '制造商',
    installation_date DATE COMMENT '安装日期',
    location VARCHAR(256) COMMENT '安装位置',
    contact_info VARCHAR(256) COMMENT '联系信息',
    description TEXT COMMENT '系统描述',
    status TINYINT DEFAULT 1 COMMENT '系统状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_system_id (system_id),
    INDEX idx_status (status)
    ) ENGINE=InnoDB COMMENT='系统信息表';

-- 系统状态表
CREATE TABLE IF NOT EXISTS gat_sys_state (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             system_id VARCHAR(64) NOT NULL,
    `current_time` TIMESTAMP NOT NULL COMMENT '系统当前时间',
    running_state TINYINT COMMENT '运行状态：0-停止，1-运行，2-故障',
    work_mode TINYINT COMMENT '工作模式：0-手动，1-自动，2-维护',
    cpu_usage DECIMAL(5,2) COMMENT 'CPU使用率(%)',
    memory_usage DECIMAL(5,2) COMMENT '内存使用率(%)',
    disk_usage DECIMAL(5,2) COMMENT '磁盘使用率(%)',
    network_status TINYINT COMMENT '网络状态：0-断开，1-连接',
    last_heartbeat TIMESTAMP COMMENT '最后心跳时间',
    error_count INT DEFAULT 0 COMMENT '错误计数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (system_id) REFERENCES gat_sys_info(system_id) ON DELETE CASCADE,
    INDEX idx_system_id (system_id),
    INDEX idx_current_time (`current_time`),
    INDEX idx_running_state (running_state)
    ) ENGINE=InnoDB COMMENT='系统状态表';

-- ============================================================================
-- 2. 信号机设备管理表
-- ============================================================================

-- 信号机控制器表
CREATE TABLE IF NOT EXISTS gat_signal_controller (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     controller_id VARCHAR(12) NOT NULL UNIQUE COMMENT '信号机设备编号(6位机构代码+99+4位数字)',
    controller_name VARCHAR(128) COMMENT '信号机名称',
    supplier VARCHAR(64) NOT NULL COMMENT '供应商(海信/易华录/其他)',
    device_type VARCHAR(16) NOT NULL COMMENT '规格型号',
    identification VARCHAR(16) NOT NULL COMMENT '识别码',
    communication_mode TINYINT NOT NULL COMMENT '通信方式：1-有线，2-无线，3-光纤',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    port INT COMMENT '端口号',
    location VARCHAR(256) COMMENT '安装位置',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    installation_date DATE COMMENT '安装日期',
    maintenance_date DATE COMMENT '最后维护日期',
    warranty_expiry DATE COMMENT '保修期截止日期',
    device_status TINYINT DEFAULT 0 COMMENT '设备状态：0-离线，1-在线，2-故障，3-维护',
    connection_status TINYINT DEFAULT 0 COMMENT '连接状态：0-未连接，1-已连接',
    last_online TIMESTAMP COMMENT '最后在线时间',
    firmware_version VARCHAR(32) COMMENT '固件版本',
    protocol_version VARCHAR(16) COMMENT '协议版本',
    adapter_type VARCHAR(32) COMMENT '适配器类型',
    config_version BIGINT DEFAULT 1 COMMENT '配置版本号',
    sync_status TINYINT DEFAULT 0 COMMENT '同步状态：0-未同步，1-同步中，2-已同步，3-同步失败',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_controller_id (controller_id),
    INDEX idx_supplier (supplier),
    INDEX idx_device_status (device_status),
    INDEX idx_location (location),
    INDEX idx_ip_address (ip_address)
    ) ENGINE=InnoDB COMMENT='信号机控制器表';

-- 信号机状态表
CREATE TABLE IF NOT EXISTS gat_signal_controller_state (
                                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                           controller_id VARCHAR(12) NOT NULL,
    `current_time` TIMESTAMP NOT NULL COMMENT '当前时间',
    operation_state TINYINT COMMENT '运行状态：0-停止，1-正常，2-闪烁，3-全红，4-故障',
    control_mode TINYINT COMMENT '控制模式：0-关灯，1-闪烁，2-手动，3-本地，4-远程',
    current_plan_id VARCHAR(32) COMMENT '当前配时方案ID',
    current_phase_id VARCHAR(32) COMMENT '当前相位ID',
    remaining_time INT COMMENT '剩余时间(秒)',
    fault_code VARCHAR(16) COMMENT '故障代码',
    fault_description TEXT COMMENT '故障描述',
    voltage DECIMAL(5,2) COMMENT '电压(V)',
    current_val DECIMAL(5,2) COMMENT '电流(A)',
    temperature DECIMAL(4,1) COMMENT '温度(℃)',
    humidity DECIMAL(4,1) COMMENT '湿度(%)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    INDEX idx_controller_id (controller_id),
    INDEX idx_current_time (`current_time`),
    INDEX idx_operation_state (operation_state)
    ) ENGINE=InnoDB COMMENT='信号机状态表';

-- ============================================================================
-- 3. 路口配置管理表
-- ============================================================================

-- 路口基础信息表
CREATE TABLE IF NOT EXISTS gat_intersection (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                intersection_id VARCHAR(32) NOT NULL UNIQUE COMMENT '路口编号',
    intersection_name VARCHAR(128) NOT NULL COMMENT '路口名称',
    controller_id VARCHAR(12) COMMENT '关联的信号机控制器ID',
    road_names TEXT COMMENT '道路名称(JSON格式)',
    intersection_type TINYINT COMMENT '路口类型：1-十字，2-T字，3-Y字，4-环岛，5-其他',
    lanes_count TINYINT COMMENT '车道总数',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    region_code VARCHAR(12) COMMENT '行政区划代码',
    description TEXT COMMENT '路口描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE SET NULL,
    INDEX idx_intersection_id (intersection_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_location (longitude, latitude)
    ) ENGINE=InnoDB COMMENT='路口基础信息表';

-- 信号组表
CREATE TABLE IF NOT EXISTS gat_signal_group (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                signal_group_id VARCHAR(32) NOT NULL COMMENT '信号组编号',
    controller_id VARCHAR(12) NOT NULL COMMENT '信号机控制器ID',
    signal_group_name VARCHAR(64) COMMENT '信号组名称',
    signal_group_type TINYINT COMMENT '信号组类型：1-机动车，2-行人，3-非机动车',
    output_port TINYINT COMMENT '输出端口号',
    direction VARCHAR(16) COMMENT '方向：东西南北',
    lamp_types VARCHAR(32) COMMENT '灯组类型(红黄绿箭头等)',
    min_green_time INT COMMENT '最小绿灯时间(秒)',
    max_green_time INT COMMENT '最大绿灯时间(秒)',
    yellow_time INT COMMENT '黄灯时间(秒)',
    all_red_time INT COMMENT '全红时间(秒)',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    UNIQUE KEY uk_signal_group (controller_id, signal_group_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_signal_group_type (signal_group_type)
    ) ENGINE=InnoDB COMMENT='信号组表';

-- 相位表
CREATE TABLE IF NOT EXISTS gat_phase (
                                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         phase_id VARCHAR(32) NOT NULL COMMENT '相位编号',
    controller_id VARCHAR(12) NOT NULL COMMENT '信号机控制器ID',
    phase_name VARCHAR(64) COMMENT '相位名称',
    phase_type TINYINT COMMENT '相位类型：1-机动车，2-行人，3-混合',
    min_duration INT COMMENT '最小持续时间(秒)',
    max_duration INT COMMENT '最大持续时间(秒)',
    yellow_duration INT COMMENT '黄灯时间(秒)',
    all_red_duration INT COMMENT '全红时间(秒)',
    pedestrian_clear_time INT COMMENT '行人清空时间(秒)',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    UNIQUE KEY uk_phase (controller_id, phase_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_phase_type (phase_type)
    ) ENGINE=InnoDB COMMENT='相位表';

-- 相位-信号组关联表
CREATE TABLE IF NOT EXISTS gat_phase_signal_group (
                                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                      controller_id VARCHAR(12) NOT NULL,
    phase_id VARCHAR(32) NOT NULL,
    signal_group_id VARCHAR(32) NOT NULL,
    lamp_state TINYINT NOT NULL COMMENT '灯态：1-红，2-黄，3-绿，4-绿箭头，5-黄闪',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    INDEX idx_controller_id (controller_id),
    INDEX idx_phase_id (phase_id),
    INDEX idx_signal_group_id (signal_group_id),
    UNIQUE KEY uk_phase_signal_group (controller_id, phase_id, signal_group_id)
    ) ENGINE=InnoDB COMMENT='相位-信号组关联表';

-- ============================================================================
-- 4. 配时方案管理表
-- ============================================================================

-- 配时方案表
CREATE TABLE IF NOT EXISTS gat_timing_plan (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               plan_id VARCHAR(32) NOT NULL COMMENT '配时方案编号',
    controller_id VARCHAR(12) NOT NULL COMMENT '信号机控制器ID',
    plan_name VARCHAR(64) NOT NULL COMMENT '方案名称',
    plan_type TINYINT COMMENT '方案类型：1-工作日，2-周末，3-节假日，4-特殊',
    cycle_length INT NOT NULL COMMENT '周期长度(秒)',
    offset_time INT DEFAULT 0 COMMENT '偏移时间(秒)',
    start_time TIME COMMENT '启用开始时间',
    end_time TIME COMMENT '启用结束时间',
    effective_date DATE COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期',
    priority_level TINYINT DEFAULT 5 COMMENT '优先级(1-10，数字越大优先级越高)',
    is_active TINYINT DEFAULT 0 COMMENT '是否激活：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用，2-草稿',
    description TEXT COMMENT '方案描述',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    UNIQUE KEY uk_timing_plan (controller_id, plan_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_plan_type (plan_type),
    INDEX idx_is_active (is_active)
    ) ENGINE=InnoDB COMMENT='配时方案表';

-- 配时方案-相位表
CREATE TABLE IF NOT EXISTS gat_timing_plan_phase (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     controller_id VARCHAR(12) NOT NULL,
    plan_id VARCHAR(32) NOT NULL,
    phase_id VARCHAR(32) NOT NULL,
    phase_sequence TINYINT NOT NULL COMMENT '相位顺序',
    start_time INT NOT NULL COMMENT '相位开始时间(周期内秒数)',
    duration INT NOT NULL COMMENT '相位持续时间(秒)',
    green_time INT COMMENT '绿灯时间(秒)',
    yellow_time INT COMMENT '黄灯时间(秒)',
    all_red_time INT COMMENT '全红时间(秒)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    INDEX idx_controller_id (controller_id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_phase_sequence (phase_sequence),
    UNIQUE KEY uk_plan_phase_seq (controller_id, plan_id, phase_sequence)
    ) ENGINE=InnoDB COMMENT='配时方案-相位表';

-- ============================================================================
-- 5. 设备适配管理表
-- ============================================================================

-- 设备适配器配置表
CREATE TABLE IF NOT EXISTS gat_device_adapter (
                                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                  adapter_id VARCHAR(32) NOT NULL UNIQUE COMMENT '适配器编号',
    adapter_name VARCHAR(64) NOT NULL COMMENT '适配器名称',
    vendor VARCHAR(32) NOT NULL COMMENT '厂商名称(海信/易华录/其他)',
    adapter_type VARCHAR(32) NOT NULL COMMENT '适配器类型',
    adapter_version VARCHAR(16) COMMENT '适配器版本',
    protocol_type VARCHAR(32) COMMENT '通信协议类型',
    communication_params JSON COMMENT '通信参数配置(JSON格式)',
    config_template JSON COMMENT '配置模板(JSON格式)',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description TEXT COMMENT '适配器描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_adapter_id (adapter_id),
    INDEX idx_vendor (vendor),
    INDEX idx_adapter_type (adapter_type)
    ) ENGINE=InnoDB COMMENT='设备适配器配置表';

-- 设备适配器实例表
CREATE TABLE IF NOT EXISTS gat_device_adapter_instance (
                                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                           instance_id VARCHAR(32) NOT NULL UNIQUE COMMENT '适配器实例编号',
    adapter_id VARCHAR(32) NOT NULL COMMENT '适配器编号',
    controller_id VARCHAR(12) NOT NULL COMMENT '信号机控制器ID',
    instance_name VARCHAR(64) COMMENT '实例名称',
    config_params JSON COMMENT '实例配置参数',
    connection_params JSON COMMENT '连接参数',
    sync_interval INT DEFAULT 30 COMMENT '同步间隔(秒)',
    retry_count INT DEFAULT 3 COMMENT '重试次数',
    timeout_seconds INT DEFAULT 10 COMMENT '超时时间(秒)',
    last_sync_time TIMESTAMP COMMENT '最后同步时间',
    sync_status TINYINT DEFAULT 0 COMMENT '同步状态：0-未同步，1-同步中，2-同步成功，3-同步失败',
    error_message TEXT COMMENT '错误信息',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (adapter_id) REFERENCES gat_device_adapter(adapter_id) ON DELETE CASCADE,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    INDEX idx_instance_id (instance_id),
    INDEX idx_adapter_id (adapter_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_sync_status (sync_status)
    ) ENGINE=InnoDB COMMENT='设备适配器实例表';

-- ============================================================================
-- 6. 数据同步管理表
-- ============================================================================

-- 同步任务表
CREATE TABLE IF NOT EXISTS gat_sync_task (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             task_id VARCHAR(32) NOT NULL UNIQUE COMMENT '任务编号',
    task_name VARCHAR(64) NOT NULL COMMENT '任务名称',
    task_type TINYINT NOT NULL COMMENT '任务类型：1-配置下发，2-状态上报，3-数据采集',
    controller_id VARCHAR(12) NOT NULL COMMENT '信号机控制器ID',
    sync_direction TINYINT NOT NULL COMMENT '同步方向：1-数据库到设备，2-设备到数据库',
    data_type VARCHAR(32) COMMENT '数据类型',
    sync_content JSON COMMENT '同步内容',
    schedule_type TINYINT COMMENT '调度类型：1-立即执行，2-定时执行，3-周期执行',
    schedule_config JSON COMMENT '调度配置',
    priority_level TINYINT DEFAULT 5 COMMENT '优先级(1-10)',
    max_retry_count INT DEFAULT 3 COMMENT '最大重试次数',
    timeout_seconds INT DEFAULT 30 COMMENT '超时时间(秒)',
    status TINYINT DEFAULT 0 COMMENT '任务状态：0-待执行，1-执行中，2-成功，3-失败，4-取消',
    last_execute_time TIMESTAMP COMMENT '最后执行时间',
    next_execute_time TIMESTAMP COMMENT '下次执行时间',
    error_message TEXT COMMENT '错误信息',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_task_type (task_type),
    INDEX idx_status (status),
    INDEX idx_next_execute_time (next_execute_time)
    ) ENGINE=InnoDB COMMENT='同步任务表';

-- 同步日志表
CREATE TABLE IF NOT EXISTS gat_sync_log (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            log_id VARCHAR(32) NOT NULL UNIQUE COMMENT '日志编号',
    task_id VARCHAR(32) COMMENT '关联任务编号',
    controller_id VARCHAR(12) NOT NULL COMMENT '信号机控制器ID',
    sync_type TINYINT NOT NULL COMMENT '同步类型：1-配置下发，2-状态上报，3-数据采集',
    sync_direction TINYINT NOT NULL COMMENT '同步方向：1-数据库到设备，2-设备到数据库',
    data_type VARCHAR(32) COMMENT '数据类型',
    data_size INT COMMENT '数据大小(字节)',
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    duration_ms INT COMMENT '耗时(毫秒)',
    result TINYINT COMMENT '同步结果：0-失败，1-成功，2-部分成功',
    success_count INT DEFAULT 0 COMMENT '成功项数',
    failed_count INT DEFAULT 0 COMMENT '失败项数',
    error_code VARCHAR(16) COMMENT '错误代码',
    error_message TEXT COMMENT '错误信息',
    request_data JSON COMMENT '请求数据',
    response_data JSON COMMENT '响应数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES gat_sync_task(task_id) ON DELETE SET NULL,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    INDEX idx_log_id (log_id),
    INDEX idx_task_id (task_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_sync_type (sync_type),
    INDEX idx_start_time (start_time),
    INDEX idx_result (result)
    ) ENGINE=InnoDB COMMENT='同步日志表';

-- ============================================================================
-- 7. 检测器和传感器表
-- ============================================================================

-- 检测器表
CREATE TABLE IF NOT EXISTS gat_detector (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            detector_id VARCHAR(32) NOT NULL COMMENT '检测器编号',
    controller_id VARCHAR(12) NOT NULL COMMENT '关联信号机控制器ID',
    detector_name VARCHAR(64) COMMENT '检测器名称',
    detector_type TINYINT COMMENT '检测器类型：1-线圈，2-视频，3-雷达，4-红外',
    installation_position VARCHAR(64) COMMENT '安装位置',
    lane_id VARCHAR(32) COMMENT '关联车道编号',
    direction VARCHAR(16) COMMENT '检测方向',
    detection_zone JSON COMMENT '检测区域配置',
    sensitivity_level TINYINT COMMENT '灵敏度等级(1-10)',
    detection_params JSON COMMENT '检测参数配置',
    status TINYINT DEFAULT 1 COMMENT '状态：0-故障，1-正常，2-维护',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    UNIQUE KEY uk_detector (controller_id, detector_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_detector_type (detector_type)
    ) ENGINE=InnoDB COMMENT='检测器表';

-- 检测器数据表
CREATE TABLE IF NOT EXISTS gat_detector_data (
                                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                 controller_id VARCHAR(12) NOT NULL,
    detector_id VARCHAR(32) NOT NULL,
    detection_time TIMESTAMP NOT NULL COMMENT '检测时间',
    vehicle_count INT DEFAULT 0 COMMENT '车辆数量',
    occupancy DECIMAL(5,2) COMMENT '占有率(%)',
    average_speed DECIMAL(5,2) COMMENT '平均速度(km/h)',
    queue_length DECIMAL(6,2) COMMENT '排队长度(米)',
    headway DECIMAL(6,2) COMMENT '车头间距(秒)',
    volume_data JSON COMMENT '流量数据详情',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE CASCADE,
    INDEX idx_controller_detector (controller_id, detector_id),
    INDEX idx_detection_time (detection_time)
    ) ENGINE=InnoDB COMMENT='检测器数据表';

-- ============================================================================
-- 8. 事件和告警管理表
-- ============================================================================

-- 事件表
CREATE TABLE IF NOT EXISTS gat_event (
                                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         event_id VARCHAR(32) NOT NULL UNIQUE COMMENT '事件编号',
    controller_id VARCHAR(12) COMMENT '关联信号机控制器ID',
    event_type TINYINT NOT NULL COMMENT '事件类型：1-设备故障，2-通信异常，3-配置变更，4-系统告警',
    event_level TINYINT NOT NULL COMMENT '事件等级：1-信息，2-警告，3-错误，4-严重',
    event_source VARCHAR(32) COMMENT '事件源',
    event_title VARCHAR(128) NOT NULL COMMENT '事件标题',
    event_description TEXT COMMENT '事件描述',
    event_time TIMESTAMP NOT NULL COMMENT '事件发生时间',
    event_data JSON COMMENT '事件相关数据',
    is_handled TINYINT DEFAULT 0 COMMENT '是否已处理：0-未处理，1-已处理',
    handled_by VARCHAR(64) COMMENT '处理人',
    handled_time TIMESTAMP COMMENT '处理时间',
    handle_result TEXT COMMENT '处理结果',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE SET NULL,
    INDEX idx_event_id (event_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_event_type (event_type),
    INDEX idx_event_level (event_level),
    INDEX idx_event_time (event_time),
    INDEX idx_is_handled (is_handled)
    ) ENGINE=InnoDB COMMENT='事件表';

-- 告警规则表
CREATE TABLE IF NOT EXISTS gat_alarm_rule (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              rule_id VARCHAR(32) NOT NULL UNIQUE COMMENT '规则编号',
    rule_name VARCHAR(64) NOT NULL COMMENT '规则名称',
    rule_type TINYINT NOT NULL COMMENT '规则类型：1-设备状态，2-通信状态，3-性能指标',
    condition_expression JSON NOT NULL COMMENT '条件表达式',
    alarm_level TINYINT NOT NULL COMMENT '告警等级：1-信息，2-警告，3-错误，4-严重',
    notification_config JSON COMMENT '通知配置',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    description TEXT COMMENT '规则描述',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rule_id (rule_id),
    INDEX idx_rule_type (rule_type),
    INDEX idx_enabled (enabled)
    ) ENGINE=InnoDB COMMENT='告警规则表';

-- 告警记录表
CREATE TABLE IF NOT EXISTS gat_alarm_record (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                alarm_id VARCHAR(32) NOT NULL UNIQUE COMMENT '告警编号',
    rule_id VARCHAR(32) COMMENT '关联规则编号',
    controller_id VARCHAR(12) COMMENT '关联信号机控制器ID',
    alarm_type TINYINT NOT NULL COMMENT '告警类型：1-设备故障，2-通信异常，3-性能告警',
    alarm_level TINYINT NOT NULL COMMENT '告警等级：1-信息，2-警告，3-错误，4-严重',
    alarm_title VARCHAR(128) NOT NULL COMMENT '告警标题',
    alarm_description TEXT COMMENT '告警描述',
    alarm_time TIMESTAMP NOT NULL COMMENT '告警时间',
    trigger_data JSON COMMENT '触发数据',
    status TINYINT DEFAULT 0 COMMENT '告警状态：0-未处理，1-处理中，2-已处理，3-已忽略',
    acknowledged_by VARCHAR(64) COMMENT '确认人',
    acknowledged_time TIMESTAMP COMMENT '确认时间',
    resolved_by VARCHAR(64) COMMENT '解决人',
    resolved_time TIMESTAMP COMMENT '解决时间',
    resolution TEXT COMMENT '解决方案',
    notification_sent TINYINT DEFAULT 0 COMMENT '是否已发送通知：0-否，1-是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (rule_id) REFERENCES gat_alarm_rule(rule_id) ON DELETE SET NULL,
    FOREIGN KEY (controller_id) REFERENCES gat_signal_controller(controller_id) ON DELETE SET NULL,
    INDEX idx_alarm_id (alarm_id),
    INDEX idx_rule_id (rule_id),
    INDEX idx_controller_id (controller_id),
    INDEX idx_alarm_type (alarm_type),
    INDEX idx_alarm_level (alarm_level),
    INDEX idx_alarm_time (alarm_time),
    INDEX idx_status (status)
    ) ENGINE=InnoDB COMMENT='告警记录表';

-- ============================================================================
-- 9. 用户权限管理表
-- ============================================================================

-- 用户表
CREATE TABLE IF NOT EXISTS gat_user (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        user_id VARCHAR(32) NOT NULL UNIQUE COMMENT '用户编号',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(128) NOT NULL COMMENT '密码哈希',
    real_name VARCHAR(64) COMMENT '真实姓名',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(32) COMMENT '电话',
    department VARCHAR(64) COMMENT '部门',
    position VARCHAR(64) COMMENT '职位',
    user_type TINYINT DEFAULT 1 COMMENT '用户类型：1-普通用户，2-管理员，3-超级管理员',
    status TINYINT DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用，2-锁定',
    last_login_time TIMESTAMP COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) COMMENT '最后登录IP',
    password_update_time TIMESTAMP COMMENT '密码更新时间',
    failed_login_count INT DEFAULT 0 COMMENT '连续登录失败次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_status (status)
    ) ENGINE=InnoDB COMMENT='用户表';

-- 会话表
CREATE TABLE IF NOT EXISTS gat_session (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           session_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户编号',
    token VARCHAR(128) NOT NULL UNIQUE COMMENT '访问令牌',
    client_ip VARCHAR(45) COMMENT '客户端IP',
    user_agent TEXT COMMENT '用户代理',
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    last_access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后访问时间',
    expire_time TIMESTAMP NOT NULL COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '会话状态：0-无效，1-有效',
    FOREIGN KEY (user_id) REFERENCES gat_user(user_id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_token (token),
    INDEX idx_expire_time (expire_time)
    ) ENGINE=InnoDB COMMENT='会话表';

-- ============================================================================
-- 10. 操作日志表
-- ============================================================================

-- 操作日志表
CREATE TABLE IF NOT EXISTS gat_operation_log (
                                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                 log_id VARCHAR(32) NOT NULL UNIQUE COMMENT '日志编号',
    user_id VARCHAR(32) COMMENT '操作用户ID',
    username VARCHAR(64) COMMENT '用户名',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型',
    operation_name VARCHAR(64) NOT NULL COMMENT '操作名称',
    operation_description TEXT COMMENT '操作描述',
    target_type VARCHAR(32) COMMENT '目标对象类型',
    target_id VARCHAR(64) COMMENT '目标对象ID',
    request_params JSON COMMENT '请求参数',
    response_data JSON COMMENT '响应数据',
    operation_result TINYINT COMMENT '操作结果：0-失败，1-成功',
    error_message TEXT COMMENT '错误信息',
    client_ip VARCHAR(45) COMMENT '客户端IP',
    user_agent TEXT COMMENT '用户代理',
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    duration_ms INT COMMENT '操作耗时(毫秒)',
    FOREIGN KEY (user_id) REFERENCES gat_user(user_id) ON DELETE SET NULL,
    INDEX idx_log_id (log_id),
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_time (operation_time),
    INDEX idx_operation_result (operation_result)
    ) ENGINE=InnoDB COMMENT='操作日志表';

-- ============================================================================
-- 11. 数据字典表
-- ============================================================================

-- 数据字典表
CREATE TABLE IF NOT EXISTS gat_data_dictionary (
                                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                   dict_code VARCHAR(32) NOT NULL COMMENT '字典编码',
    dict_name VARCHAR(64) NOT NULL COMMENT '字典名称',
    dict_type VARCHAR(32) NOT NULL COMMENT '字典类型',
    dict_value VARCHAR(128) NOT NULL COMMENT '字典值',
    dict_label VARCHAR(128) NOT NULL COMMENT '字典标签',
    sort_order INT DEFAULT 0 COMMENT '排序',
    parent_code VARCHAR(32) COMMENT '父级编码',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description TEXT COMMENT '描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dict_code (dict_code),
    INDEX idx_dict_type (dict_type),
    INDEX idx_parent_code (parent_code),
    UNIQUE KEY uk_dict_type_value (dict_type, dict_value)
    ) ENGINE=InnoDB COMMENT='数据字典表';

-- ============================================================================
-- 12. 配置管理表
-- ============================================================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS gat_system_config (
                                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                 config_key VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(32) DEFAULT 'STRING' COMMENT '配置类型：STRING/NUMBER/BOOLEAN/JSON',
    config_group VARCHAR(32) COMMENT '配置分组',
    config_name VARCHAR(128) COMMENT '配置名称',
    description TEXT COMMENT '配置描述',
    is_encrypted TINYINT DEFAULT 0 COMMENT '是否加密：0-否，1-是',
    is_readonly TINYINT DEFAULT 0 COMMENT '是否只读：0-否，1-是',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group)
    ) ENGINE=InnoDB COMMENT='系统配置表';

-- ============================================================================
-- 13. 初始化数据
-- ============================================================================

-- 插入初始系统配置
INSERT INTO gat_system_config (config_key, config_value, config_type, config_group, config_name, description) VALUES
                                                                                                                  ('system.name', 'GA/T 1049信号机品牌无关架构系统', 'STRING', 'SYSTEM', '系统名称', '系统显示名称'),
                                                                                                                  ('system.version', '1.0.0', 'STRING', 'SYSTEM', '系统版本', '当前系统版本号'),
                                                                                                                  ('sync.default_interval', '30', 'NUMBER', 'SYNC', '默认同步间隔', '设备状态同步默认间隔(秒)'),
                                                                                                                  ('sync.max_retry_count', '3', 'NUMBER', 'SYNC', '最大重试次数', '同步失败时的最大重试次数'),
                                                                                                                  ('sync.timeout_seconds', '10', 'NUMBER', 'SYNC', '同步超时时间', '设备同步操作超时时间(秒)'),
                                                                                                                  ('session.expire_hours', '8', 'NUMBER', 'SECURITY', '会话过期时间', '用户会话过期时间(小时)'),
                                                                                                                  ('password.min_length', '8', 'NUMBER', 'SECURITY', '最小密码长度', '用户密码最小长度'),
                                                                                                                  ('log.retention_days', '90', 'NUMBER', 'LOG', '日志保留天数', '操作日志保留天数');

-- 插入数据字典
INSERT INTO gat_data_dictionary (dict_code, dict_name, dict_type, dict_value, dict_label, sort_order, description) VALUES
-- 设备状态
('DEVICE_STATUS_OFFLINE', '设备状态-离线', 'DEVICE_STATUS', '0', '离线', 1, '设备离线状态'),
('DEVICE_STATUS_ONLINE', '设备状态-在线', 'DEVICE_STATUS', '1', '在线', 2, '设备在线状态'),
('DEVICE_STATUS_FAULT', '设备状态-故障', 'DEVICE_STATUS', '2', '故障', 3, '设备故障状态'),
('DEVICE_STATUS_MAINTENANCE', '设备状态-维护', 'DEVICE_STATUS', '3', '维护', 4, '设备维护状态'),

-- 通信方式
('COMM_MODE_WIRED', '通信方式-有线', 'COMM_MODE', '1', '有线', 1, '有线通信'),
('COMM_MODE_WIRELESS', '通信方式-无线', 'COMM_MODE', '2', '无线', 2, '无线通信'),
('COMM_MODE_FIBER', '通信方式-光纤', 'COMM_MODE', '3', '光纤', 3, '光纤通信'),

-- 信号机供应商
('VENDOR_HISENSE', '供应商-海信', 'VENDOR', 'HISENSE', '海信', 1, '海信网络科技'),
('VENDOR_EHUALU', '供应商-易华录', 'VENDOR', 'EHUALU', '易华录', 2, '易华录信息技术'),
('VENDOR_DAHUA', '供应商-大华', 'VENDOR', 'DAHUA', '大华', 3, '大华技术'),
('VENDOR_OTHER', '供应商-其他', 'VENDOR', 'OTHER', '其他', 99, '其他供应商'),

-- 信号组类型
('SIGNAL_GROUP_VEHICLE', '信号组-机动车', 'SIGNAL_GROUP_TYPE', '1', '机动车', 1, '机动车信号组'),
('SIGNAL_GROUP_PEDESTRIAN', '信号组-行人', 'SIGNAL_GROUP_TYPE', '2', '行人', 2, '行人信号组'),
('SIGNAL_GROUP_BICYCLE', '信号组-非机动车', 'SIGNAL_GROUP_TYPE', '3', '非机动车', 3, '非机动车信号组'),

-- 灯态
('LAMP_STATE_RED', '灯态-红', 'LAMP_STATE', '1', '红', 1, '红灯'),
('LAMP_STATE_YELLOW', '灯态-黄', 'LAMP_STATE', '2', '黄', 2, '黄灯'),
('LAMP_STATE_GREEN', '灯态-绿', 'LAMP_STATE', '3', '绿', 3, '绿灯'),
('LAMP_STATE_GREEN_ARROW', '灯态-绿箭头', 'LAMP_STATE', '4', '绿箭头', 4, '绿色箭头灯'),
('LAMP_STATE_YELLOW_FLASH', '灯态-黄闪', 'LAMP_STATE', '5', '黄闪', 5, '黄闪灯');

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO gat_user (user_id, username, password_hash, real_name, user_type, status) VALUES
    ('admin', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVepjG', '系统管理员', 3, 1);

-- ============================================================================
-- 14. 创建视图
-- ============================================================================

-- 设备状态汇总视图
CREATE OR REPLACE VIEW v_device_status_summary AS
SELECT
    sc.controller_id,
    sc.controller_name,
    sc.supplier,
    sc.device_type,
    sc.device_status,
    sc.connection_status,
    sc.last_online,
    scs.operation_state,
    scs.control_mode,
    scs.current_plan_id,
    scs.fault_code,
    scs.fault_description,
    scs.created_at as status_update_time
FROM gat_signal_controller sc
         LEFT JOIN gat_signal_controller_state scs ON sc.controller_id = scs.controller_id
    AND scs.id = (SELECT MAX(id) FROM gat_signal_controller_state WHERE controller_id = sc.controller_id);

-- 告警统计视图
CREATE OR REPLACE VIEW v_alarm_statistics AS
SELECT
    DATE(alarm_time) as alarm_date,
    alarm_level,
    alarm_type,
    COUNT(*) as alarm_count,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as resolved_count,
    SUM(CASE WHEN status IN (0,1) THEN 1 ELSE 0 END) as pending_count
FROM gat_alarm_record
WHERE alarm_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(alarm_time), alarm_level, alarm_type;

-- 同步任务执行统计视图
CREATE OR REPLACE VIEW v_sync_task_statistics AS
SELECT
    controller_id,
    task_type,
    COUNT(*) as total_tasks,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as success_count,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) as failed_count,
    AVG(CASE WHEN status = 2 AND last_execute_time IS NOT NULL
                 THEN TIMESTAMPDIFF(SECOND, created_at, last_execute_time) END) as avg_duration_seconds
FROM gat_sync_task
GROUP BY controller_id, task_type;

-- ============================================================================
-- 15. 创建存储过程
-- ============================================================================

DELIMITER $

-- 清理过期数据的存储过程
CREATE PROCEDURE CleanExpiredData()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

START TRANSACTION;

-- 清理过期会话
DELETE FROM gat_session WHERE expire_time < NOW();

-- 清理90天前的检测器数据
DELETE FROM gat_detector_data WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- 清理90天前的操作日志
DELETE FROM gat_operation_log WHERE operation_time < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- 清理已处理的告警记录（保留30天）
DELETE FROM gat_alarm_record
WHERE status = 2 AND resolved_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 清理成功的同步日志（保留7天）
DELETE FROM gat_sync_log
WHERE result = 1 AND created_at < DATE_SUB(NOW(), INTERVAL 7 DAY);

COMMIT;
END$

-- 获取设备状态统计的存储过程
CREATE PROCEDURE GetDeviceStatistics()
BEGIN
SELECT
    'total' as category,
    COUNT(*) as count
FROM gat_signal_controller

UNION ALL

SELECT
    'online' as category,
    COUNT(*) as count
FROM gat_signal_controller
WHERE device_status = 1

UNION ALL

SELECT
    'offline' as category,
    COUNT(*) as count
FROM gat_signal_controller
WHERE device_status = 0

UNION ALL

SELECT
    'fault' as category,
    COUNT(*) as count
FROM gat_signal_controller
WHERE device_status = 2;
END$

DELIMITER ;

-- ============================================================================
-- 16. 性能优化索引
-- ============================================================================

-- 为经常查询的字段组合创建复合索引
CREATE INDEX idx_controller_status_time ON gat_signal_controller_state(controller_id, operation_state, created_at);
CREATE INDEX idx_sync_task_status_time ON gat_sync_task(status, next_execute_time);
CREATE INDEX idx_alarm_level_time ON gat_alarm_record(alarm_level, alarm_time);
CREATE INDEX idx_detector_time_type ON gat_detector_data(detection_time, controller_id, detector_id);

-- ============================================================================
-- 数据库设计完成
-- ============================================================================

-- 显示创建的表
SHOW TABLES;