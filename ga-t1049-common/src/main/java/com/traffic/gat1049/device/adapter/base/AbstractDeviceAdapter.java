package com.traffic.gat1049.device.adapter.base;

import com.traffic.gat1049.device.adapter.interfaces.DeviceAdapter;
import com.traffic.gat1049.device.adapter.registry.AdapterInfo;
import com.traffic.gat1049.device.adapter.model.AdapterStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象设备适配器基类
 * 提供适配器的基础功能实现
 */
public abstract class AbstractDeviceAdapter implements DeviceAdapter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    protected ApplicationEventPublisher eventPublisher;

    protected final AtomicBoolean initialized = new AtomicBoolean(false);
    protected final AtomicBoolean started = new AtomicBoolean(false);
    protected AdapterStatus currentStatus = AdapterStatus.CREATED;
    protected LocalDateTime startTime;
    protected AdapterInfo adapterInfo;

    @PostConstruct
    public void init() {
        try {
            this.adapterInfo = createAdapterInfo();
            logger.info("适配器初始化完成: {}", adapterInfo.getAdapterName());
        } catch (Exception e) {
            logger.error("适配器初始化失败", e);
        }
    }

    @Override
    public boolean initialize() {
        if (initialized.get()) {
            logger.warn("适配器已经初始化");
            return true;
        }

        try {
            logger.info("正在初始化适配器: {}", getAdapterInfo().getAdapterName());

            // 执行具体的初始化逻辑
            doInitialize();

            initialized.set(true);
            currentStatus = AdapterStatus.INITIALIZED;

            logger.info("适配器初始化成功: {}", getAdapterInfo().getAdapterName());
            return true;

        } catch (Exception e) {
            logger.error("适配器初始化失败", e);
            currentStatus = AdapterStatus.ERROR;
            return false;
        }
    }

    @Override
    public boolean start() {
        if (!initialized.get()) {
            logger.error("适配器未初始化，无法启动");
            return false;
        }

        if (started.get()) {
            logger.warn("适配器已经启动");
            return true;
        }

        try {
            logger.info("正在启动适配器: {}", getAdapterInfo().getAdapterName());

            // 执行具体的启动逻辑
            doStart();

            started.set(true);
            startTime = LocalDateTime.now();
            currentStatus = AdapterStatus.RUNNING;

            logger.info("适配器启动成功: {}", getAdapterInfo().getAdapterName());
            return true;

        } catch (Exception e) {
            logger.error("适配器启动失败", e);
            currentStatus = AdapterStatus.ERROR;
            return false;
        }
    }

    @Override
    public boolean stop() {
        if (!started.get()) {
            logger.warn("适配器未启动");
            return true;
        }

        try {
            logger.info("正在停止适配器: {}", getAdapterInfo().getAdapterName());

            // 执行具体的停止逻辑
            doStop();

            started.set(false);
            currentStatus = AdapterStatus.STOPPED;

            logger.info("适配器停止成功: {}", getAdapterInfo().getAdapterName());
            return true;

        } catch (Exception e) {
            logger.error("适配器停止失败", e);
            currentStatus = AdapterStatus.ERROR;
            return false;
        }
    }

    @PreDestroy
    @Override
    public void destroy() {
        try {
            logger.info("正在销毁适配器: {}", getAdapterInfo().getAdapterName());

            // 先停止适配器
            if (started.get()) {
                stop();
            }

            // 执行具体的清理逻辑
            doDestroy();

            initialized.set(false);
            currentStatus = AdapterStatus.DESTROYED;

            logger.info("适配器销毁完成: {}", getAdapterInfo().getAdapterName());

        } catch (Exception e) {
            logger.error("适配器销毁失败", e);
        }
    }

    @Override
    public AdapterStatus getStatus() {
        return currentStatus;
    }

    @Override
    public boolean healthCheck() {
        try {
            // 基础健康检查
            if (!initialized.get() || !started.get()) {
                return false;
            }

            // 执行具体的健康检查逻辑
            return doHealthCheck();

        } catch (Exception e) {
            logger.error("适配器健康检查失败", e);
            return false;
        }
    }

    @Override
    public AdapterInfo getAdapterInfo() {
        return adapterInfo;
    }

    // ========================= 抽象方法，由子类实现 =========================

    /**
     * 创建适配器信息
     * @return 适配器信息
     */
    protected abstract AdapterInfo createAdapterInfo();

    /**
     * 具体的初始化逻辑
     */
    protected abstract void doInitialize() throws Exception;

    /**
     * 具体的启动逻辑
     */
    protected abstract void doStart() throws Exception;

    /**
     * 具体的停止逻辑
     */
    protected abstract void doStop() throws Exception;

    /**
     * 具体的清理逻辑
     */
    protected abstract void doDestroy() throws Exception;

    /**
     * 具体的健康检查逻辑
     * @return 健康状态
     */
    protected abstract boolean doHealthCheck();

    // ========================= 工具方法 =========================

    /**
     * 发布事件
     */
    protected void publishEvent(Object event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
    }

    /**
     * 获取运行时间（秒）
     */
    protected long getUptimeSeconds() {
        if (startTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();
    }
}
