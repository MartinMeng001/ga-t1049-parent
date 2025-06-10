package com.traffic.gat1049.model.constants;
/**
 * GA/T 1049协议常量定义
 * 根据GA/T 1049.1-2013标准更新
 */
public final class GatConstants {

    private GatConstants() {
        // 防止实例化
    }

    /**
     * 协议版本号 - 符合GA/T 1049.1标准
     */
    public static final String PROTOCOL_VERSION = "2.0";

    /**
     * 默认字符编码 - XML版本1.0，使用UTF-8编码
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * XML命名空间
     */
    public static final String XML_NAMESPACE = "http://tmri.cn/ticp/general/v1.0";

    /**
     * 数据包长度限制 - 不超过100000个字符
     */
    public static final int MAX_MESSAGE_LENGTH = 100000;

    /**
     * 消息类型常量 - 对应GA/T 1049.1表1
     */
    public static final class MessageType {
        public static final String REQUEST = "REQUEST";      // 请求，要求通信对方应答
        public static final String RESPONSE = "RESPONSE";    // 应答，针对请求数据包的应答数据包
        public static final String PUSH = "PUSH";           // 主动推送，单向数据包不需要通信对方应答
        public static final String ERROR = "ERROR";         // 出错应答，REQUEST数据包发生错误时向通信对方应答该数据包
    }

    /**
     * 操作命令名称常量 - 对应GA/T 1049.1表A.3
     */
    public static final class Operation {
        public static final String LOGIN = "Login";           // 登录
        public static final String LOGOUT = "Logout";         // 登出
        public static final String SUBSCRIBE = "Subscribe";   // 订阅
        public static final String UNSUBSCRIBE = "Unsubscribe"; // 取消订阅
        public static final String GET = "Get";              // 查询，获取
        public static final String SET = "Set";              // 设置
        public static final String NOTIFY = "Notify";        // 通知
        public static final String OTHER = "Other";          // 其他
    }

    /**
     * 基础应用系统类型标识 - 对应GA/T 1049.1表A.2
     */
    public static final class SystemAddress {
        public static final String TICP = "TICP";  // 公安交通集成指挥平台
        public static final String UTCS = "UTCS";  // 交通信号控制系统 (注意：标准中使用UTCS而不是TSC)
        public static final String TVMS = "TVMS";  // 交通视频监视系统
        public static final String TICS = "TICS";  // 交通流信息采集系统
        public static final String TVMR = "TVMR";  // 交通违法监测记录系统
        public static final String TIPS = "TIPS";  // 交通信息发布系统
        public static final String PGPS = "PGPS";  // 警用车辆与单警定位系统
        public static final String TDMS = "TDMS";  // 交通设施管理系统
        public static final String TEDS = "TEDS";  // 交通事件采集系统
        public static final String VMKS = "VMKS";  // 机动车缉查布控系统
    }

    /**
     * 系统预定义数据对象名称 - 对应GA/T 1049.1表2
     */
    public static final class SystemObject {
        public static final String SDO_ERROR = "SDO_Error";         // 系统错误
        public static final String SDO_USER = "SDO_User";           // 系统用户
        public static final String SDO_MSG_ENTITY = "SDO_MsgEntity"; // 订阅对象
        public static final String SDO_HEART_BEAT = "SDO_HeartBeat"; // 心跳对象
        public static final String SDO_TIME_OUT = "SDO_TimeOut";     // 超时对象
        public static final String SDO_TIME_SERVER = "SDO_TimeServer"; // 对时服务器
    }

    /**
     * 系统预定义错误类型 - 对应GA/T 1049.1表A.5
     */
    public static final class ErrorCode {
        public static final String SDE_VERSION = "SDE_Version";     // 版本号错误
        public static final String SDE_TOKEN = "SDE_Token";         // 无效令牌，会话错误
        public static final String SDE_ADDRESS = "SDE_Address";     // 地址错误
        public static final String SDE_MSG_TYPE = "SDE_MsgType";    // 数据包类型错误
        public static final String SDE_OPER_NAME = "SDE_OperName";  // 操作命令错误
        public static final String SDE_USER_NAME = "SDE_UserName";  // 用户名错误
        public static final String SDE_PWD = "SDE_Pwd";             // 口令错误
        public static final String SDE_NOT_ALLOW = "SDE_NotAllow";  // 操作不允许
        public static final String SDE_FAILURE = "SDE_Failure";     // 操作失败
        public static final String SDE_UNKNOWN = "SDE_Unknown";     // 其他未知错误

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
     * 会话和连接常量
     */
    public static final class Session {
        public static final int DEFAULT_SESSION_TIMEOUT = 30;      // 默认会话超时时间（分钟）
        public static final int HEARTBEAT_INTERVAL = 30;           // 心跳间隔（秒）
        public static final int CONNECTION_TIMEOUT = 90;           // 连接超时时间（秒）
        public static final int MAX_RETRY_COUNT = 3;               // 最大重试次数
        public static final int RECONNECT_DELAY_MIN = 1;           // 重连延迟最小值（秒）
        public static final int RECONNECT_DELAY_MAX = 60;          // 重连延迟最大值（秒）
    }

    /**
     * 网络通信常量
     */
    public static final class Network {
        public static final int DEFAULT_TCP_PORT = 8080;
        public static final int DEFAULT_UTCS_PORT = 9999;          // 交通信号控制系统默认端口
        public static final String DEFAULT_HOST = "localhost";
        public static final int SOCKET_TIMEOUT = 30000;            // Socket超时时间（毫秒）
        public static final int CONNECTION_TIMEOUT = 10000;        // 连接超时时间（毫秒）
        public static final int MAX_CONNECTIONS = 100;             // 最大连接数
        public static final int MAX_MESSAGE_SIZE = 100000;         // 最大消息大小（字符）
    }

    /**
     * 时间格式常量
     */
    public static final class TimeFormat {
        public static final String DATETIME_PATTERN = "yyyyMMddHHmmss";  // 序列号中的日期时间格式
        public static final String DISPLAY_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss"; // 显示用日期时间格式
        public static final String TIME_PATTERN = "HH:mm:ss";
        public static final String DATE_PATTERN = "yyyy-MM-dd";
    }

    /**
     * 验证相关常量
     */

    public static final class Validation {
        public static final int MAX_SEQ_LENGTH = 20;               // 序列号最大长度
        public static final String VERSION_PATTERN = "\\d\\.\\d";  // 版本号格式
        public static final int MAX_USERNAME_LENGTH = 50;          // 用户名最大长度
        public static final int MAX_PASSWORD_LENGTH = 100;         // 密码最大长度
        public static final int MAX_ERROR_DESC_LENGTH = 500;       // 错误描述最大长度

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
     * 特殊字符常量 - XML中需要转义的字符
     */
    public static final class SpecialChars {
        public static final String AMP = "&amp;";      // &
        public static final String LT = "&lt;";        // <
        public static final String GT = "&gt;";        // >
        public static final String QUOT = "&quot;";    // "
        public static final String APOS = "&apos;";    // '
    }

    /**
     * 日志相关常量
     */
    public static final class Logging {
        public static final String LOG_PREFIX = "GAT1049";
        public static final String REQUEST_LOG = "REQ";
        public static final String RESPONSE_LOG = "RSP";
        public static final String PUSH_LOG = "PUSH";
        public static final String ERROR_LOG = "ERR";
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
