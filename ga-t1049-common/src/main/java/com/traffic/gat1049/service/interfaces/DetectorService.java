package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.intersection.DetectorParam;
import com.traffic.gat1049.model.enums.DetectorPosition;
import com.traffic.gat1049.model.enums.DetectorType;

import java.util.List;

/**
 * 检测器服务接口
 */
public interface DetectorService {

    /**
     * 获取所有检测器列表
     *
     * @return 检测器参数列表
     * @throws BusinessException 业务异常
     */
    List<DetectorParam> findAll() throws BusinessException;
    /**
     * 根据路口编号获取检测器列表
     *
     * @param crossId 路口编号
     * @return 检测器参数列表
     * @throws BusinessException 业务异常
     */
    List<DetectorParam> findByCrossId(String crossId) throws BusinessException;

    /**
     * 根据路口编号和检测器序号获取检测器
     *
     * @param crossId 路口编号
     * @param detectorNo 检测器序号
     * @return 检测器参数
     * @throws BusinessException 业务异常
     */
    DetectorParam findByCrossIdAndDetectorNo(String crossId, Integer detectorNo) throws BusinessException;

    /**
     * 根据检测器类型查询
     *
     * @param crossId 路口编号
     * @param type 检测器类型
     * @return 检测器参数列表
     * @throws BusinessException 业务异常
     */
    List<DetectorParam> findByType(String crossId, DetectorType type) throws BusinessException;

    /**
     * 根据检测位置查询
     *
     * @param crossId 路口编号
     * @param position 检测位置
     * @return 检测器参数列表
     * @throws BusinessException 业务异常
     */
    List<DetectorParam> findByPosition(String crossId, DetectorPosition position) throws BusinessException;

    /**
     * 根据车道序号查询检测器
     *
     * @param crossId 路口编号
     * @param laneNo 车道序号
     * @return 检测器参数列表
     * @throws BusinessException 业务异常
     */
    List<DetectorParam> findByLaneNo(String crossId, Integer laneNo) throws BusinessException;

    /**
     * 保存检测器参数
     *
     * @param detectorParam 检测器参数
     * @return 保存后的检测器参数
     * @throws BusinessException 业务异常
     */
    DetectorParam save(DetectorParam detectorParam) throws BusinessException;

    /**
     * 更新检测器参数
     *
     * @param detectorParam 检测器参数
     * @return 更新后的检测器参数
     * @throws BusinessException 业务异常
     */
    DetectorParam update(DetectorParam detectorParam) throws BusinessException;

    /**
     * 删除检测器
     *
     * @param crossId 路口编号
     * @param detectorNo 检测器序号
     * @throws BusinessException 业务异常
     */
    void delete(String crossId, Integer detectorNo) throws BusinessException;

    /**
     * 检查检测器是否在线
     *
     * @param crossId 路口编号
     * @param detectorNo 检测器序号
     * @return 是否在线
     * @throws BusinessException 业务异常
     */
    boolean isOnline(String crossId, Integer detectorNo) throws BusinessException;
}
