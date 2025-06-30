package com.traffic.gat1049.data.provider;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.system.SysInfo;
import com.traffic.gat1049.protocol.model.system.SysState;
import com.traffic.gat1049.protocol.model.system.RegionParam;
import com.traffic.gat1049.protocol.model.system.SubRegionParam;
import com.traffic.gat1049.protocol.model.system.RouteParam;
import com.traffic.gat1049.protocol.model.intersection.CrossParam;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.protocol.model.intersection.LampGroup;
import com.traffic.gat1049.protocol.model.intersection.DetectorParam;
import com.traffic.gat1049.protocol.model.intersection.LaneParam;
import com.traffic.gat1049.protocol.model.intersection.PedestrianParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;

import java.util.List;

/**
 * 全面的测试数据访问接口
 * 提供对所有测试数据的统一访问
 */
public interface ComprehensiveTestDataProvider {

    // ==================== 系统信息相关 ====================

    /**
     * 获取系统参数
     */
    SysInfo getSystemInfo() throws BusinessException;

    /**
     * 获取系统状态
     */
    SysState getSystemState() throws BusinessException;

    // ==================== 区域管理相关 ====================

    /**
     * 获取所有区域参数
     */
    List<RegionParam> getAllRegions() throws BusinessException;

    /**
     * 根据区域ID获取区域参数
     */
    RegionParam getRegionById(String regionId) throws BusinessException;

    /**
     * 获取所有子区参数
     */
    List<SubRegionParam> getAllSubRegions() throws BusinessException;

    /**
     * 根据子区ID获取子区参数
     */
    SubRegionParam getSubRegionById(String subRegionId) throws BusinessException;

    /**
     * 根据区域ID获取该区域下的所有子区
     */
    List<SubRegionParam> getSubRegionsByRegionId(String regionId) throws BusinessException;

    // ==================== 线路管理相关 ====================

    /**
     * 获取所有线路参数
     */
    List<RouteParam> getAllRoutes() throws BusinessException;

    /**
     * 根据线路ID获取线路参数
     */
    RouteParam getRouteById(String routeId) throws BusinessException;

    // ==================== 路口管理相关 ====================

    /**
     * 获取所有路口参数
     */
    List<CrossParam> getAllCrosses() throws BusinessException;

    /**
     * 根据路口ID获取路口参数
     */
    CrossParam getCrossById(String crossId) throws BusinessException;

    /**
     * 根据区域ID获取该区域下的所有路口
     */
    List<CrossParam> getCrossesByRegionId(String regionId) throws BusinessException;

    /**
     * 根据子区ID获取该子区下的所有路口
     */
    List<CrossParam> getCrossesBySubRegionId(String subRegionId) throws BusinessException;

    // ==================== 信号机管理相关 ====================

    /**
     * 获取所有信号机参数
     */
    List<SignalController> getAllSignalControllers() throws BusinessException;

    /**
     * 根据信号机ID获取信号机参数
     */
    SignalController getSignalControllerById(String signalControllerId) throws BusinessException;

    /**
     * 根据路口ID获取该路口的信号机
     */
    List<SignalController> getSignalControllersByCrossId(String crossId) throws BusinessException;

    // ==================== 灯组管理相关 ====================

    /**
     * 获取所有灯组
     */
    List<LampGroup> getAllLampGroups() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有灯组
     */
    List<LampGroup> getLampGroupsByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和灯组编号获取灯组
     */
    LampGroup getLampGroupByCrossIdAndNo(String crossId, String lampGroupNo) throws BusinessException;

    // ==================== 检测器管理相关 ====================

    /**
     * 获取所有检测器参数
     */
    List<DetectorParam> getAllDetectors() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有检测器
     */
    List<DetectorParam> getDetectorsByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和检测器编号获取检测器
     */
    DetectorParam getDetectorByCrossIdAndNo(String crossId, String detectorNo) throws BusinessException;

    // ==================== 车道管理相关 ====================

    /**
     * 获取所有车道参数
     */
    List<LaneParam> getAllLanes() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有车道
     */
    List<LaneParam> getLanesByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和车道编号获取车道
     */
    LaneParam getLaneByCrossIdAndNo(String crossId, String laneNo) throws BusinessException;

    // ==================== 行人管理相关 ====================

    /**
     * 获取所有行人参数
     */
    List<PedestrianParam> getAllPedestrians() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有行人参数
     */
    List<PedestrianParam> getPedestriansByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和行人编号获取行人参数
     */
    PedestrianParam getPedestrianByCrossIdAndNo(String crossId, String pedestrianNo) throws BusinessException;

    // ==================== 信号组管理相关 ====================

    /**
     * 获取所有信号组参数
     */
    List<SignalGroupParam> getAllSignalGroups() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有信号组
     */
    List<SignalGroupParam> getSignalGroupsByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和信号组编号获取信号组
     */
    SignalGroupParam getSignalGroupByCrossIdAndNo(String crossId, String signalGroupNo) throws BusinessException;

    // ==================== 阶段管理相关 ====================

    /**
     * 获取所有阶段参数
     */
    List<StageParam> getAllStages() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有阶段
     */
    List<StageParam> getStagesByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和阶段编号获取阶段
     */
    StageParam getStageByCrossIdAndNo(String crossId, String stageNo) throws BusinessException;

    // ==================== 配时方案管理相关 ====================

    /**
     * 获取所有配时方案参数
     */
    List<PlanParam> getAllPlans() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有配时方案
     */
    List<PlanParam> getPlansByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和方案编号获取配时方案
     */
    PlanParam getPlanByCrossIdAndNo(String crossId, String planNo) throws BusinessException;

    // ==================== 日计划管理相关 ====================

    /**
     * 获取所有日计划参数
     */
    List<DayPlanParam> getAllDayPlans() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有日计划
     */
    List<DayPlanParam> getDayPlansByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和日计划编号获取日计划
     */
    DayPlanParam getDayPlanByCrossIdAndNo(String crossId, String dayPlanNo) throws BusinessException;

    // ==================== 调度管理相关 ====================

    /**
     * 获取所有调度参数
     */
    List<ScheduleParam> getAllSchedules() throws BusinessException;

    /**
     * 根据路口ID获取该路口的所有调度
     */
    List<ScheduleParam> getSchedulesByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口ID和调度编号获取调度
     */
    ScheduleParam getScheduleByCrossIdAndNo(String crossId, String scheduleNo) throws BusinessException;

    // ==================== 运行状态相关 ====================

    /**
     * 获取所有路口状态
     */
    List<Object> getAllCrossStates() throws BusinessException;

    /**
     * 根据路口ID获取路口状态
     */
    Object getCrossStateById(String crossId) throws BusinessException;

    /**
     * 获取所有信号机故障信息
     */
    List<Object> getAllSignalTroubles() throws BusinessException;

    /**
     * 根据信号机ID获取故障信息
     */
    List<Object> getSignalTroublesByControllerId(String signalControllerId) throws BusinessException;

    /**
     * 获取所有路口控制模式和方案
     */
    List<Object> getAllCrossModePlans() throws BusinessException;

    /**
     * 根据路口ID获取控制模式和方案
     */
    Object getCrossModePlanById(String crossId) throws BusinessException;

    /**
     * 获取所有路口周期信息
     */
    List<Object> getAllCrossCycles() throws BusinessException;

    /**
     * 根据路口ID获取周期信息
     */
    Object getCrossCycleById(String crossId) throws BusinessException;

    /**
     * 获取所有路口阶段信息
     */
    List<Object> getAllCrossStages() throws BusinessException;

    /**
     * 根据路口ID获取当前阶段信息
     */
    Object getCrossStageById(String crossId) throws BusinessException;

    /**
     * 获取所有路口信号组状态
     */
    List<Object> getAllCrossSignalGroupStatus() throws BusinessException;

    /**
     * 根据路口ID获取信号组状态
     */
    Object getCrossSignalGroupStatusById(String crossId) throws BusinessException;

    // ==================== 交通数据相关 ====================

    /**
     * 获取所有路口交通流数据
     */
    List<Object> getAllCrossTrafficData() throws BusinessException;

    /**
     * 根据路口ID获取交通流数据
     */
    Object getCrossTrafficDataById(String crossId) throws BusinessException;

    /**
     * 获取所有阶段交通流数据
     */
    List<Object> getAllStageTrafficData() throws BusinessException;

    /**
     * 根据路口ID获取阶段交通流数据
     */
    Object getStageTrafficDataByCrossId(String crossId) throws BusinessException;

    // ==================== 可变车道和干线控制相关 ====================

    /**
     * 获取所有可变车道状态
     */
    List<Object> getAllVarLaneStatus() throws BusinessException;

    /**
     * 根据路口ID获取可变车道状态
     */
    Object getVarLaneStatusByCrossId(String crossId) throws BusinessException;

    /**
     * 获取所有干线控制模式
     */
    List<Object> getAllRouteControlModes() throws BusinessException;

    /**
     * 根据线路ID获取控制模式
     */
    Object getRouteControlModeById(String routeId) throws BusinessException;

    /**
     * 获取所有干线推荐车速
     */
    List<Object> getAllRouteSpeeds() throws BusinessException;

    /**
     * 根据线路ID获取推荐车速
     */
    Object getRouteSpeedById(String routeId) throws BusinessException;

    // ==================== 通用方法 ====================

    /**
     * 初始化测试数据
     */
    void initialize() throws BusinessException;

    /**
     * 检查数据是否可用
     */
    boolean isDataAvailable();

    /**
     * 重新加载测试数据
     */
    void reloadData() throws BusinessException;

    /**
     * 获取数据统计信息
     */
    String getDataStatistics();

    /**
     * 根据对象名称获取测试数据
     * @param objectName 对象名称（如 "CrossParam", "PlanParam" 等）
     * @return 对应的测试数据列表
     */
    List<Object> getTestDataByObjectName(String objectName) throws BusinessException;

    /**
     * 根据对象名称和ID获取特定测试数据
     * @param objectName 对象名称
     * @param id 对象ID
     * @return 对应的测试数据对象
     */
    Object getTestDataByObjectNameAndId(String objectName, String id) throws BusinessException;

    /**
     * 根据对象名称、ID和编号获取特定测试数据
     * @param objectName 对象名称
     * @param id 对象ID（如路口ID）
     * @param no 编号（如车道编号、检测器编号等）
     * @return 对应的测试数据对象
     */
    Object getTestDataByObjectNameIdAndNo(String objectName, String id, String no) throws BusinessException;
}