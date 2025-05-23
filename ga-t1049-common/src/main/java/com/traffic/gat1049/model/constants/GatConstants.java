package com.traffic.gat1049.model.constants;

/**
 * GA/T 1049.2 协议常量定义
 */
public final class GatConstants {

    private GatConstants() {
        // 防止实例化
    }

    /**
     * 协议版本号
     */
    public static final String PROTOCOL_VERSION = "2.0";

    /**
     * 默认字符编码
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * XML 命名空间
     */
    public static final String XML_NAMESPACE = "http://tmri.cn/ticp/tsc/v1.0";

    /**
     * 时间格式常量
     */
    public static final class TimeFormat {
        public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME_PATTERN = "HH:mm";
        public static final String DATE_PATTERN = "yyyy-MM-dd";
        public static final String MONTH_DAY_PATTERN = "MM-dd";
    }

    /**
     * 数据验证常量
     */
    public static final class Validation {
        public static final int MIN_PLAN_NO = 0;
        public static final int MAX_PLAN_NO = 999;
        public static final int MIN_STAGE_NO = 1;
        public static final int MAX_STAGE_NO = 999;
        public static final int MIN_SIGNAL_GROUP_NO = 1;
        public static final int MAX_SIGNAL_GROUP_NO = 99;
        public static final int MIN_LANE_NO = 1;
        public static final int MAX_LANE_NO = 99;
        public static final int MIN_DETECTOR_NO = 1;
        public static final int MAX_DETECTOR_NO = 999;
        public static final int MIN_LAMP_GROUP_NO = 1;
        public static final int MAX_LAMP_GROUP_NO = 99;
        public static final int MIN_CYCLE_LEN = 1;
        public static final int MAX_CYCLE_LEN = 3600;
        public static final int MIN_AZIMUTH = 0;
        public static final int MAX_AZIMUTH = 359;
        public static final int MIN_OCCUPANCY = 0;
        public static final int MAX_OCCUPANCY = 100;
        public static final int MIN_WEEK_DAY = 1;
        public static final int MAX_WEEK_DAY = 7;
    }

    /**
     * 编号格式常量
     */
    public static final class IdFormat {
        public static final String REGION_ID_PATTERN = "\\d{9}";
        public static final String SUB_REGION_ID_PATTERN = "\\d{11}";
        public static final String SIGNAL_CONTROLLER_ID_PATTERN = "\\d{17}";
        public static final String MONTH_DAY_PATTERN = "\\d{2}-\\d{2}";
        public static final String DETECTOR_TARGET_PATTERN = "[01]{3}";
    }

    /**
     * 默认值常量
     */
    public static final class DefaultValue {
        public static final int DEFAULT_PRIORITY = 0;
        public static final int DEFAULT_WAITING_AREA = 0;
        public static final int DEFAULT_STAGE_ATTRIBUTE = 0;
        public static final boolean DEFAULT_ENABLED = true;
        public static final int DEFAULT_DISTANCE = 0;
        public static final int DEFAULT_TIMEOUT = 30;
        public static final String DEFAULT_TARGET = "000";
    }

    /**
     * 消息类型常量
     */
    public static final class MessageType {
        public static final String REQUEST = "REQUEST";
        public static final String RESPONSE = "RESPONSE";
        public static final String PUSH = "PUSH";
        public static final String ERROR = "ERROR";
    }

    /**
     * 操作名称常量
     */
    public static final class Operation {
        public static final String GET = "Get";
        public static final String SET = "Set";
        public static final String NOTIFY = "Notify";
    }

    /**
     * 对象名称常量
     */
    public static final class ObjectName {
        public static final String SYS_INFO = "SysInfo";
        public static final String REGION_PARAM = "RegionParam";
        public static final String SUB_REGION_PARAM = "SubRegionParam";
        public static final String ROUTE_PARAM = "RouteParam";
        public static final String CROSS_PARAM = "CrossParam";
        public static final String SIGNAL_CONTROLLER = "SignalController";
        public static final String LAMP_GROUP = "LampGroup";
        public static final String DETECTOR_PARAM = "DetectorParam";
        public static final String LANE_PARAM = "LaneParam";
        public static final String PEDESTRIAN_PARAM = "PedestrianParam";
        public static final String SIGNAL_GROUP_PARAM = "SignalGroupParam";
        public static final String STAGE_PARAM = "StageParam";
        public static final String PLAN_PARAM = "PlanParam";
        public static final String DAY_PLAN_PARAM = "DayPlanParam";
        public static final String SCHEDULE_PARAM = "ScheduleParam";
        public static final String SYS_STATE = "SysState";
        public static final String CROSS_STATE = "CrossState";
        public static final String SIGNAL_CONTROLLER_ERROR = "SignalControllerError";
        public static final String CROSS_MODE_PLAN = "CrossModePlan";
        public static final String CROSS_CYCLE = "CrossCycle";
        public static final String CROSS_STAGE = "CrossStage";
        public static final String CROSS_SIGNAL_GROUP_STATUS = "CrossSignalGroupStatus";
        public static final String CROSS_TRAFFIC_DATA = "CrossTrafficData";
        public static final String STAGE_TRAFFIC_DATA = "StageTrafficData";
        public static final String VAR_LANE_STATUS = "VarLaneStatus";
        public static final String ROUTE_CONTROL_MODE = "RouteControlMode";
        public static final String ROUTE_SPEED = "RouteSpeed";
    }

    /**
     * 错误代码常量
     */
    public static final class ErrorCode {
        public static final String SUCCESS = "0000";
        public static final String INVALID_PARAMETER = "1001";
        public static final String OBJECT_NOT_FOUND = "1002";
        public static final String OPERATION_FAILED = "1003";
        public static final String PERMISSION_DENIED = "1004";
        public static final String TIMEOUT = "1005";
        public static final String SYSTEM_ERROR = "9999";
    }

    /**
     * 错误消息常量
     */
    public static final class ErrorMessage {
        public static final String SUCCESS_MSG = "操作成功";
        public static final String INVALID_PARAMETER_MSG = "参数无效";
        public static final String OBJECT_NOT_FOUND_MSG = "对象未找到";
        public static final String OPERATION_FAILED_MSG = "操作失败";
        public static final String PERMISSION_DENIED_MSG = "权限不足";
        public static final String TIMEOUT_MSG = "操作超时";
        public static final String SYSTEM_ERROR_MSG = "系统错误";
    }

    /**
     * 系统地址常量
     */
    public static final class SystemAddress {
        public static final String TICP = "TICP";
        public static final String TSC = "TSC";
    }

    /**
     * 配置参数标识
     */
    public static final class ConfigParam {
        public static final String HEARTBEAT_INTERVAL = "heartbeat.interval";
        public static final String CONNECTION_TIMEOUT = "connection.timeout";
        public static final String MAX_RETRY_COUNT = "max.retry.count";
        public static final String BUFFER_SIZE = "buffer.size";
        public static final String THREAD_POOL_SIZE = "thread.pool.size";
    }

    /**
     * 单位常量
     */
    public static final class Unit {
        public static final String SECOND = "s";
        public static final String MINUTE = "min";
        public static final String HOUR = "h";
        public static final String METER = "m";
        public static final String KILOMETER = "km";
        public static final String KMH = "km/h";
        public static final String VEHICLE = "辆";
        public static final String PCU = "pcu";
        public static final String PERCENT = "%";
        public static final String DEGREE = "°";
        public static final String CENTIMETER = "cm";
    }

    /**
     * 日志级别常量
     */
    public static final class LogLevel {
        public static final String TRACE = "TRACE";
        public static final String DEBUG = "DEBUG";
        public static final String INFO = "INFO";
        public static final String WARN = "WARN";
        public static final String ERROR = "ERROR";
    }

    /**
     * 数据类型常量
     */
    public static final class DataType {
        public static final String STRING = "String";
        public static final String INTEGER = "Integer";
        public static final String FLOAT = "Float";
        public static final String BOOLEAN = "Boolean";
        public static final String DATETIME = "DateTime";
        public static final String ENUM = "Enum";
        public static final String LIST = "List";
        public static final String OBJECT = "Object";
    }

    /**
     * 状态值常量
     */
    public static final class StatusValue {
        public static final int INACTIVE = 0;
        public static final int ACTIVE = 1;
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
        public static final int NO = 0;
        public static final int YES = 1;
    }

    /**
     * 网络常量
     */
    public static final class Network {
        public static final int DEFAULT_TCP_PORT = 8080;
        public static final int DEFAULT_UDP_PORT = 8081;
        public static final String DEFAULT_HOST = "localhost";
        public static final int SOCKET_TIMEOUT = 30000;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int MAX_CONNECTIONS = 100;
    }

    /**
     * 文件常量
     */
    public static final class File {
        public static final String CONFIG_FILE = "gat1049.properties";
        public static final String LOG_FILE = "gat1049.log";
        public static final String XML_EXTENSION = ".xml";
        public static final String JSON_EXTENSION = ".json";
        public static final String PROPERTIES_EXTENSION = ".properties";
    }

    /**
     * 缓存常量
     */
    public static final class Cache {
        public static final String SYSTEM_INFO_CACHE = "systemInfo";
        public static final String CROSS_PARAM_CACHE = "crossParam";
        public static final String PLAN_PARAM_CACHE = "planParam";
        public static final String RUNTIME_DATA_CACHE = "runtimeData";
        public static final int DEFAULT_CACHE_SIZE = 1000;
        public static final int DEFAULT_EXPIRE_TIME = 3600;
    }

    /**
     * 线程池常量
     */
    public static final class ThreadPool {
        public static final int CORE_POOL_SIZE = 5;
        public static final int MAXIMUM_POOL_SIZE = 20;
        public static final long KEEP_ALIVE_TIME = 60L;
        public static final int QUEUE_CAPACITY = 100;
        public static final String THREAD_NAME_PREFIX = "GAT1049-";
    }

    /**
     * 数据库常量
     */
    public static final class Database {
        public static final String TABLE_PREFIX = "gat_";
        public static final String SEQUENCE_PREFIX = "seq_";
        public static final String INDEX_PREFIX = "idx_";
        public static final String FK_PREFIX = "fk_";
        public static final String UK_PREFIX = "uk_";
    }

    /**
     * 监控指标常量
     */
    public static final class Metrics {
        public static final String MESSAGE_COUNT = "message.count";
        public static final String MESSAGE_RATE = "message.rate";
        public static final String ERROR_COUNT = "error.count";
        public static final String ERROR_RATE = "error.rate";
        public static final String RESPONSE_TIME = "response.time";
        public static final String CONNECTION_COUNT = "connection.count";
        public static final String THROUGHPUT = "throughput";
        public static final String CPU_USAGE = "cpu.usage";
        public static final String MEMORY_USAGE = "memory.usage";
    }
}
