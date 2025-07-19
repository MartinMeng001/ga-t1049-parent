package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.protocol.model.system.SysInfo;
import com.traffic.gat1049.protocol.model.system.SysState;
import com.traffic.gat1049.model.enums.SystemState;
import com.traffic.gat1049.service.interfaces.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统服务实现
 * 已更新以支持新的SysState定义（包含Time字段）
 */
@Service
public class SystemServiceImpl implements SystemService {

    private static final Logger logger = LoggerFactory.getLogger(SystemServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();

    private final AtomicReference<SysInfo> systemInfo = new AtomicReference<>();
    private final AtomicReference<SysState> systemState = new AtomicReference<>();

    public SystemServiceImpl() throws BusinessException {
        // 初始化默认系统信息
        initializeDefaultSystemInfo();
        initializeDefaultSystemState();
    }

    @Override
    public SysInfo getSystemInfo() throws BusinessException {
        SysInfo info = dataPrider.getSystemInfo();
        if (info == null) {
            throw new DataNotFoundException("系统信息未配置");
        }
        logger.debug("获取系统信息: {}", info.getSysName());
        return info;
    }

    @Override
    public SysInfo updateSystemInfo(SysInfo sysInfo) throws BusinessException {
        if (sysInfo == null) {
            throw new BusinessException("INVALID_PARAMETER", "系统信息不能为空");
        }

        validateSystemInfo(sysInfo);
        systemInfo.set(sysInfo);

        logger.info("更新系统信息: 系统名称={}, 版本={}, 供应商={}",
                sysInfo.getSysName(), sysInfo.getSysVersion(), sysInfo.getSupplier());

        return sysInfo;
    }

    @Override
    public SysState getSystemState() throws BusinessException {
        SysState state = dataPrider.getSystemState();
        if (state == null) {
            throw new DataNotFoundException("系统状态未初始化");
        }
        return state;
    }

    @Override
    public void updateSystemState(SystemState state) throws BusinessException {
        if (state == null) {
            throw new BusinessException("INVALID_PARAMETER", "系统状态不能为空");
        }

        // 创建新的SysState对象，包含时间信息
        SysState sysState = new SysState(state);
        systemState.set(sysState);

        logger.info("更新系统状态: {}, 时间: {}", state.getDescription(), sysState.getTime());
    }

    @Override
    public void updateSystemState(SysState sysState) throws BusinessException {
        if (sysState == null) {
            throw new BusinessException("INVALID_PARAMETER", "系统状态对象不能为空");
        }

        if (sysState.getValue() == null) {
            throw new BusinessException("INVALID_PARAMETER", "系统状态值不能为空");
        }

        // 如果没有时间信息，则设置当前时间
        if (sysState.getTime() == null || sysState.getTime().trim().isEmpty()) {
            sysState.setTimeFromLocalDateTime(LocalDateTime.now());
        }

        // 验证时间格式
        if (!sysState.isValidTimeFormat()) {
            throw new BusinessException("INVALID_PARAMETER", "时间格式不正确，应为yyyy-MM-dd HH:mm:ss");
        }

        systemState.set(sysState);

        logger.info("更新系统状态: {}, 时间: {}",
                sysState.getValue().getDescription(), sysState.getTime());
    }

    @Override
    public void initialize() throws BusinessException {
        logger.info("初始化系统服务...");

        try {
            // 初始化系统信息
            if (systemInfo.get() == null) {
                initializeDefaultSystemInfo();
            }

            // 初始化系统状态
            if (systemState.get() == null) {
                initializeDefaultSystemState();
            }

            logger.info("系统服务初始化完成");
        } catch (Exception e) {
            logger.error("系统服务初始化失败", e);
            throw new BusinessException("SYSTEM_INIT_ERROR", "系统初始化失败: " + e.getMessage());
        }
    }

    @Override
    public boolean healthCheck() throws BusinessException {
        try {
            // 检查系统信息
            SysInfo info = systemInfo.get();
            if (info == null || info.getSysName() == null) {
                return false;
            }

            // 检查系统状态
            SysState state = systemState.get();
            if (state == null || state.getValue() == null) {
                return false;
            }

            // 检查系统状态是否正常
            return SystemState.ONLINE.equals(state.getValue());

        } catch (Exception e) {
            logger.error("系统健康检查失败", e);
            return false;
        }
    }

    /**
     * 初始化默认系统信息
     */
    private void initializeDefaultSystemInfo() throws BusinessException {
        try {
            SysInfo defaultInfo = createDefaultSystemInfo();
            systemInfo.set(defaultInfo);
            logger.info("初始化默认系统信息完成");
        } catch (Exception e) {
            throw new BusinessException("SYSTEM_INIT_ERROR", "初始化默认系统信息失败", e);
        }
    }

    /**
     * 初始化默认系统状态
     */
    private void initializeDefaultSystemState() throws BusinessException {
        try {
            SysState defaultState = new SysState(SystemState.ONLINE);
            systemState.set(defaultState);
            logger.info("初始化默认系统状态: {}, 时间: {}",
                    defaultState.getValue().getDescription(), defaultState.getTime());
        } catch (Exception e) {
            throw new BusinessException("SYSTEM_INIT_ERROR", "初始化默认系统状态失败", e);
        }
    }

    /**
     * 创建默认系统信息
     */
    private SysInfo createDefaultSystemInfo() {
        SysInfo sysInfo = new SysInfo();
        sysInfo.setSysName("交通信号控制系统");
        sysInfo.setSysVersion("1.0.0");
        sysInfo.setSupplier("Traffic Control Systems Ltd.");
        return sysInfo;
    }

    /**
     * 验证系统信息
     */
    private void validateSystemInfo(SysInfo sysInfo) throws BusinessException {
        if (sysInfo.getSysName() == null || sysInfo.getSysName().trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "系统名称不能为空");
        }
        if (sysInfo.getSysVersion() == null || sysInfo.getSysVersion().trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "系统版本不能为空");
        }
        if (sysInfo.getSupplier() == null || sysInfo.getSupplier().trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "供应商不能为空");
        }
    }

    /**
     * 获取系统状态统计信息
     */
    public String getSystemStateInfo() throws BusinessException {
        SysState state = getSystemState();
        if (state == null) {
            return "系统状态未知";
        }

        StringBuilder info = new StringBuilder();
        info.append("系统状态: ").append(state.getValue().getDescription());
        info.append(", 更新时间: ").append(state.getTime());

        if (state.isOnline()) {
            info.append(" [正常运行]");
        } else if (state.isOffline()) {
            info.append(" [离线状态]");
        } else if (state.hasError()) {
            info.append(" [异常故障]");
        }

        return info.toString();
    }
}