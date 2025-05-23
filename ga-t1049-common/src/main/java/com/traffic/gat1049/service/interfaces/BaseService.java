package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.dto.PageRequestDto;

import java.util.List;

/**
 * 基础服务接口
 */
public interface BaseService<T, ID> {

    /**
     * 根据ID查询对象
     *
     * @param id 对象ID
     * @return 对象实例
     * @throws BusinessException 业务异常
     */
    T findById(ID id) throws BusinessException;

    /**
     * 查询所有对象
     *
     * @return 对象列表
     * @throws BusinessException 业务异常
     */
    List<T> findAll() throws BusinessException;

    /**
     * 分页查询
     *
     * @param pageRequest 分页请求
     * @return 分页结果
     * @throws BusinessException 业务异常
     */
    List<T> findPage(PageRequestDto pageRequest) throws BusinessException;

    /**
     * 保存对象
     *
     * @param entity 对象实例
     * @return 保存后的对象
     * @throws BusinessException 业务异常
     */
    T save(T entity) throws BusinessException;

    /**
     * 更新对象
     *
     * @param entity 对象实例
     * @return 更新后的对象
     * @throws BusinessException 业务异常
     */
    T update(T entity) throws BusinessException;

    /**
     * 删除对象
     *
     * @param id 对象ID
     * @throws BusinessException 业务异常
     */
    void deleteById(ID id) throws BusinessException;

    /**
     * 检查对象是否存在
     *
     * @param id 对象ID
     * @return 是否存在
     * @throws BusinessException 业务异常
     */
    boolean existsById(ID id) throws BusinessException;

    /**
     * 统计对象数量
     *
     * @return 对象总数
     * @throws BusinessException 业务异常
     */
    long count() throws BusinessException;
}
