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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统服务实现
 */

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
        SysInfo info = dataPrider.getSystemInfo();//systemInfo.get();
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

//        sysInfo.setUpdateTime(LocalDateTime.now());
        systemInfo.set(sysInfo);

        logger.info("更新系统信息: 系统名称={}, 版本={}, 供应商={}",
                sysInfo.getSysName(), sysInfo.getSysVersion(), sysInfo.getSupplier());

        return sysInfo;
    }

    @Override
    public SysState getSystemState() throws BusinessException {
        SysState state = dataPrider.getSystemState();//systemState.get();
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

        SysState sysState = new SysState(state);
//        sysState.setStateTime(LocalDateTime.now());
        systemState.set(sysState);

        logger.info("更新系统状态: {}", state.getDescription());
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

    private void initializeDefaultSystemInfo() {
        SysInfo info = new SysInfo("GA/T 1049.2 交通信号控制系统", "2.0", "系统默认供应商");

        // 添加默认路口列表
        List<String> crossIds = new ArrayList<>();
        crossIds.add("11010000100001");
        crossIds.add("11010000100002");
        info.setCrossIdList(crossIds);

        // 添加默认信号机列表
        List<String> signalControllerIds = new ArrayList<>();
        signalControllerIds.add("11010000000000001");
        signalControllerIds.add("11010000000000002");
        info.setSignalControllerIdList(signalControllerIds);

//        info.setCreateTime(LocalDateTime.now());
//        info.setUpdateTime(LocalDateTime.now());

        systemInfo.set(info);
        logger.info("初始化默认系统信息完成");
    }

    private void initializeDefaultSystemState() {
        SysState state = new SysState(SystemState.ONLINE);
        systemState.set(state);
        logger.info("初始化默认系统状态完成");
    }

    private void validateSystemInfo(SysInfo sysInfo) throws BusinessException {
        if (sysInfo.getSysName() == null || sysInfo.getSysName().trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "系统名称不能为空");
        }

        if (sysInfo.getSysVersion() == null || sysInfo.getSysVersion().trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "版本号不能为空");
        }

        if (sysInfo.getSupplier() == null || sysInfo.getSupplier().trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "供应商不能为空");
        }

        if (sysInfo.getCrossIdList() == null || sysInfo.getCrossIdList().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "路口编号列表不能为空");
        }

        if (sysInfo.getSignalControllerIdList() == null || sysInfo.getSignalControllerIdList().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "信号机编号列表不能为空");
        }
    }
}
