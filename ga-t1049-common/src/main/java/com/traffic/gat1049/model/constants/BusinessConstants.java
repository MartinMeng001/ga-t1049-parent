package com.traffic.gat1049.model.constants;

/**
 * 业务规则常量
 */
public final class BusinessConstants {

    private BusinessConstants() {
        // 防止实例化
    }

    /**
     * 交通信号控制相关常量
     */
    public static final class SignalControl {
        public static final int MIN_GREEN_TIME = 5;
        public static final int MAX_GREEN_TIME = 120;
        public static final int MIN_YELLOW_TIME = 3;
        public static final int MAX_YELLOW_TIME = 7;
        public static final int MIN_ALL_RED_TIME = 1;
        public static final int MAX_ALL_RED_TIME = 10;
        public static final int MIN_CYCLE_TIME = 30;
        public static final int MAX_CYCLE_TIME = 300;
        public static final int DEFAULT_GREEN_FLASH_TIME = 3;
        public static final int MAX_OFFSET = 300;
    }

    /**
     * 交通流检测相关常量
     */
    public static final class TrafficDetection {
        public static final int MIN_VOLUME = 0;
        public static final int MAX_VOLUME = 9999;
        public static final double MIN_SPEED = 0.0;
        public static final double MAX_SPEED = 200.0;
        public static final double MIN_OCCUPANCY = 0.0;
        public static final double MAX_OCCUPANCY = 100.0;
        public static final double MIN_HEADWAY = 0.0;
        public static final double MAX_HEADWAY = 60.0;
        public static final int DETECTION_INTERVAL = 60;
        public static final int STATISTICAL_PERIOD = 300;
    }

    /**
     * 设备管理相关常量
     */
    public static final class DeviceManagement {
        public static final int HEARTBEAT_INTERVAL = 30;
        public static final int OFFLINE_THRESHOLD = 90;
        public static final int MAX_RETRY_ATTEMPTS = 3;
        public static final int COMMAND_TIMEOUT = 10;
        public static final int STATUS_UPDATE_INTERVAL = 60;
        public static final int ERROR_REPORT_THRESHOLD = 5;
    }

    /**
     * 通信协议相关常量
     */
    public static final class Protocol {
        public static final int MAX_MESSAGE_SIZE = 1024 * 1024; // 1MB
        public static final int MIN_MESSAGE_SIZE = 10;
        public static final int HEADER_SIZE = 20;
        public static final int CHECKSUM_SIZE = 4;
        public static final String MESSAGE_DELIMITER = "\r\n";
        public static final String FIELD_SEPARATOR = ",";
    }

    /**
     * 数据质量相关常量
     */
    public static final class DataQuality {
        public static final double MIN_VALID_SPEED = 1.0;
        public static final double MAX_VALID_SPEED = 120.0;
        public static final int MIN_VALID_VOLUME = 0;
        public static final int MAX_VALID_VOLUME = 5000;
        public static final double MIN_VALID_OCCUPANCY = 0.0;
        public static final double MAX_VALID_OCCUPANCY = 100.0;
        public static final int DATA_VALIDITY_PERIOD = 300;
        public static final double OUTLIER_THRESHOLD = 3.0;
    }

    /**
     * 系统性能相关常量
     */
    public static final class Performance {
        public static final int MAX_CONCURRENT_REQUESTS = 1000;
        public static final int REQUEST_QUEUE_SIZE = 10000;
        public static final long MAX_PROCESSING_TIME = 5000L;
        public static final double CPU_THRESHOLD = 80.0;
        public static final double MEMORY_THRESHOLD = 85.0;
        public static final int GC_THRESHOLD = 10;
    }
}
