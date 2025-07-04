# GA/T 1049 品牌无关架构 - 完整包结构设计

## 📁 整体项目结构

```
gat1049-traffic-system/
├── ga-t1049-common/                    # 公共模块 (JAR包)
├── traffic-signal-server/              # 服务端应用
├── traffic-signal-client/              # 客户端应用
├── device-adapters/                    # 设备适配器模块
│   ├── hisense-adapter/               # 海信适配器
│   ├── ehualu-adapter/                # 易华录适配器
│   └── generic-adapter/               # 通用适配器
├── build.sh                          # 构建脚本
├── docker-compose.yml               # Docker编排
└── README.md                         # 项目说明
```

## 📦 GA-T1049-Common 核心包结构

### 🏗️ 根包结构
```
com.traffic.gat1049/
├── application/                       # 应用层
├── config/                           # 配置层
├── data/                             # 数据层
├── device/                           # 设备层 (新增)
├── exception/                        # 异常处理
├── model/                            # 数据模型
├── protocol/                         # 协议层
├── repository/                       # 数据仓库层 (新增)
├── service/                          # 服务层
└── util/                            # 工具类
```

### 📱 应用层 (application/)
```
com.traffic.gat1049.application/
├── session/                          # 会话管理
│   ├── SessionManager.java
│   ├── SessionInfo.java
│   └── interfaces/
│       └── SessionService.java
├── subscription/                     # 订阅管理
│   ├── SubscriptionManager.java
│   ├── SubscriptionResult.java
│   └── interfaces/
│       └── SubscriptionService.java
├── connection/                       # 连接管理
│   ├── ConnectionManager.java
│   ├── ConnectionInfo.java
│   └── interfaces/
│       └── ConnectionService.java
└── cache/                           # 缓存管理 (新增)
    ├── CacheManager.java
    ├── CacheConfig.java
    └── interfaces/
        └── CacheService.java
```

### ⚙️ 配置层 (config/)
```
com.traffic.gat1049.config/
├── DatabaseConfig.java              # 数据库配置
├── RedisConfig.java                 # Redis缓存配置
├── ProtocolConfig.java              # 协议配置
├── DeviceConfig.java                # 设备配置 (新增)
├── SyncConfig.java                  # 同步配置 (新增)
└── properties/                      # 配置属性
    ├── DatabaseProperties.java
    ├── DeviceProperties.java
    └── SyncProperties.java
```

### 💾 数据层 (data/)
```
com.traffic.gat1049.data/
├── provider/                        # 数据提供者
│   ├── interfaces/
│   │   ├── DataProvider.java
│   │   └── DeviceDataProvider.java # (新增)
│   └── impl/
│       ├── ComprehensiveTestDataProviderImpl.java
│       ├── DatabaseDataProviderImpl.java  # (新增)
│       └── DeviceDataProviderImpl.java    # (新增)
├── converter/                       # 数据转换器 (新增)
│   ├── interfaces/
│   │   ├── EntityConverter.java
│   │   └── DeviceDataConverter.java
│   └── impl/
│       ├── SignalControllerConverter.java
│       ├── SysInfoConverter.java
│       ├── SyncTaskConverter.java
│       └── DeviceConfigConverter.java
├── cache/                           # 数据缓存
│   ├── CacheKey.java
│   ├── CacheUtils.java
│   └── strategy/
│       ├── CacheStrategy.java
│       └── CacheEvictionPolicy.java
└── validation/                      # 数据验证 (新增)
    ├── DeviceDataValidator.java
    ├── ConfigValidator.java
    └── SyncDataValidator.java
```

### 🔌 设备层 (device/) - 核心新增
```
com.traffic.gat1049.device/
├── adapter/                         # 设备适配器
│   ├── interfaces/
│   │   ├── SignalControllerAdapter.java
│   │   ├── DeviceAdapter.java
│   │   └── AdapterFactory.java
│   ├── base/
│   │   ├── AbstractDeviceAdapter.java
│   │   └── BaseSignalControllerAdapter.java
│   ├── registry/
│   │   ├── AdapterRegistry.java
│   │   ├── AdapterInfo.java
│   │   └── AdapterMetadata.java
│   └── impl/                        # 实现类放在各自的适配器模块中
├── sync/                            # 数据同步
│   ├── interfaces/
│   │   ├── SyncService.java
│   │   ├── SyncTaskManager.java
│   │   └── SyncResultHandler.java
│   ├── impl/
│   │   ├── DataSyncServiceImpl.java
│   │   ├── ConfigSyncServiceImpl.java
│   │   └── StatusSyncServiceImpl.java
│   ├── task/
│   │   ├── SyncTask.java
│   │   ├── SyncTaskExecutor.java
│   │   └── SyncScheduler.java
│   └── model/
│       ├── SyncResult.java
│       ├── SyncStatus.java
│       └── SyncLog.java
├── management/                      # 设备管理
│   ├── DeviceManager.java
│   ├── DeviceDiscovery.java
│   ├── DeviceMonitor.java
│   └── DeviceHealthChecker.java
├── communication/                   # 设备通信
│   ├── DeviceConnector.java
│   ├── ProtocolHandler.java
│   └── MessageDispatcher.java
└── model/                          # 设备数据模型
    ├── DeviceInfo.java
    ├── DeviceStatus.java
    ├── DeviceCapabilities.java
    ├── DeviceConfigData.java
    ├── DeviceStatusData.java
    └── DeviceRuntimeData.java
```

### 🗃️ 数据仓库层 (repository/) - 新增
```
com.traffic.gat1049.repository/
├── entity/                          # JPA实体类
│   ├── GatSysInfoEntity.java
│   ├── GatSignalControllerEntity.java
│   ├── GatSyncTaskEntity.java
│   ├── GatDeviceAdapterEntity.java
│   ├── GatAlarmRecordEntity.java
│   └── ...                         # 对应gat1049.sql中的所有表
├── interfaces/                      # Repository接口
│   ├── GatSysInfoRepository.java
│   ├── GatSignalControllerRepository.java
│   ├── GatSyncTaskRepository.java
│   ├── GatDeviceAdapterRepository.java
│   └── ...
├── custom/                          # 自定义查询
│   ├── CustomSignalControllerRepository.java
│   ├── CustomSyncTaskRepository.java
│   └── impl/
│       ├── CustomSignalControllerRepositoryImpl.java
│       └── CustomSyncTaskRepositoryImpl.java
└── specification/                   # JPA规格查询
    ├── SignalControllerSpecification.java
    ├── SyncTaskSpecification.java
    └── DeviceAdapterSpecification.java
```

### 🏢 服务层 (service/) - 扩展
```
com.traffic.gat1049.service/
├── interfaces/                      # 服务接口
│   ├── BaseService.java            # (已有)
│   ├── SignalControllerService.java # (扩展)
│   ├── DeviceManagementService.java # (新增)
│   ├── SyncManagementService.java   # (新增)
│   ├── AdapterManagementService.java # (新增)
│   └── ...
├── abstracts/                       # 抽象实现
│   ├── SignalGroupServiceImpl.java  # (已有)
│   ├── DeviceManagementServiceImpl.java # (新增)
│   ├── SyncManagementServiceImpl.java   # (新增)
│   └── ...
├── database/                        # 数据库服务实现 (新增)
│   ├── DatabaseSignalControllerService.java
│   ├── DatabaseSysInfoService.java
│   ├── DatabaseSyncTaskService.java
│   └── ...
└── factory/                         # 服务工厂
    ├── ServiceFactory.java
    └── DefaultServiceFactory.java
```

### 📊 协议层 (protocol/) - 现有优化
```
com.traffic.gat1049.protocol/
├── model/                           # 协议模型 (已有)
├── handler/                         # 协议处理器 (已有)
├── codec/                           # 编解码器 (已有)
├── builder/                         # 消息构建器 (已有)
├── util/                           # 协议工具类 (已有)
└── constants/                       # 协议常量 (已有)
```

### 📋 数据模型层 (model/) - 扩展
```
com.traffic.gat1049.model/
├── dto/                            # 数据传输对象
│   ├── BaseResponseDto.java        # (已有)
│   ├── DataResponseDto.java        # (已有)
│   ├── DeviceConfigDto.java        # (新增)
│   ├── SyncTaskDto.java            # (新增)
│   └── AdapterConfigDto.java       # (新增)
├── enums/                          # 枚举类型 (已有)
├── vo/                             # 视图对象 (新增)
│   ├── DeviceStatusVo.java
│   ├── SyncStatisticsVo.java
│   └── AdapterInfoVo.java
└── domain/                         # 领域模型 (新增)
    ├── Device.java
    ├── SyncTask.java
    └── Adapter.java
```

## 🔧 设备适配器模块结构

### 📦 海信适配器 (device-adapters/hisense-adapter/)
```
com.traffic.device.adapter.hisense/
├── HisenseSignalControllerAdapter.java
├── HisenseProtocolHandler.java
├── HisenseDataConverter.java
├── HisenseConfigManager.java
├── config/
│   ├── HisenseAdapterConfig.java
│   └── HisenseProtocolConfig.java
├── protocol/
│   ├── HisenseMessage.java
│   ├── HisenseCommand.java
│   └── HisenseResponse.java
└── util/
    ├── HisenseProtocolUtils.java
    └── HisenseDataUtils.java
```

### 📦 易华录适配器 (device-adapters/ehualu-adapter/)
```
com.traffic.device.adapter.ehualu/
├── EhualuSignalControllerAdapter.java
├── EhualuProtocolHandler.java
├── EhualuDataConverter.java
├── EhualuConfigManager.java
├── config/
│   ├── EhualuAdapterConfig.java
│   └── EhualuProtocolConfig.java
├── protocol/
│   ├── EhualuMessage.java
│   ├── EhualuCommand.java
│   └── EhualuResponse.java
└── util/
    ├── EhualuProtocolUtils.java
    └── EhualuDataUtils.java
```

## 🎯 核心特性

### ✅ 分层明确
- **应用层**：会话、订阅、连接管理
- **配置层**：统一配置管理
- **数据层**：数据提供、转换、缓存
- **设备层**：适配器、同步、管理
- **仓库层**：数据持久化
- **服务层**：业务逻辑
- **协议层**：GA/T 1049协议处理

### ✅ 职责分离
- **Common模块**：提供核心框架和接口
- **Server模块**：实现服务端业务逻辑
- **Client模块**：实现客户端功能
- **Adapter模块**：各品牌设备适配

### ✅ 扩展性强
- **接口驱动**：所有核心功能都有接口定义
- **插件架构**：适配器可独立开发和部署
- **配置驱动**：通过配置支持不同场景
- **模块化**：各模块可独立升级

### ✅ 可维护性高
- **标准化命名**：遵循Java命名规范
- **包结构清晰**：按功能和层次组织
- **依赖关系简单**：避免循环依赖
- **接口抽象**：降低耦合度

## 🚀 实施建议

### 第一阶段：数据库集成
1. 实现 `repository/` 包下的所有接口
2. 创建 `data/converter/` 包下的转换器
3. 扩展 `service/database/` 包下的数据库服务

### 第二阶段：设备适配框架
1. 实现 `device/adapter/` 包下的核心接口
2. 创建 `device/management/` 包下的管理服务
3. 开发 `device/sync/` 包下的同步机制

### 第三阶段：品牌适配器
1. 基于框架开发海信适配器
2. 基于框架开发易华录适配器
3. 验证品牌无关架构的有效性

这个包结构设计充分考虑了现有框架的基础，同时为品牌无关架构预留了完整的扩展空间，确保了架构的可实施性和可维护性。