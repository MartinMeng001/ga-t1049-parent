package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户表Repository
 */
@Repository
public interface GatUserRepository extends JpaRepository<GatUserEntity, Long> {

    /**
     * 根据用户ID查找
     */
    Optional<GatUserEntity> findByUserId(String userId);

    /**
     * 根据用户名查找
     */
    Optional<GatUserEntity> findByUsername(String username);

    /**
     * 根据邮箱查找
     */
    Optional<GatUserEntity> findByEmail(String email);

    /**
     * 根据手机号查找
     */
    Optional<GatUserEntity> findByPhone(String phone);

    /**
     * 根据用户类型查找
     */
    List<GatUserEntity> findByUserType(Integer userType);

    /**
     * 根据用户状态查找
     */
    List<GatUserEntity> findByStatus(Integer status);

    /**
     * 根据部门查找
     */
    List<GatUserEntity> findByDepartment(String department);

    /**
     * 查找启用的用户
     */
    @Query("SELECT u FROM GatUserEntity u WHERE u.status = 1")
    List<GatUserEntity> findEnabledUsers();

    /**
     * 查找管理员用户
     */
    @Query("SELECT u FROM GatUserEntity u WHERE u.userType IN (2, 3) AND u.status = 1")
    List<GatUserEntity> findAdminUsers();

    /**
     * 根据最后登录时间查找
     */
    @Query("SELECT u FROM GatUserEntity u WHERE u.lastLoginTime < :beforeTime")
    List<GatUserEntity> findByLastLoginTimeBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查找登录失败次数超过阈值的用户
     */
    List<GatUserEntity> findByFailedLoginCountGreaterThan(Integer failedLoginCount);

    /**
     * 根据真实姓名模糊查找
     */
    List<GatUserEntity> findByRealNameContaining(String realName);

    /**
     * 统计各用户类型数量
     */
    @Query("SELECT u.userType, COUNT(u) FROM GatUserEntity u GROUP BY u.userType")
    List<Object[]> countByUserType();

    /**
     * 统计各状态用户数量
     */
    @Query("SELECT u.status, COUNT(u) FROM GatUserEntity u GROUP BY u.status")
    List<Object[]> countByStatus();

    /**
     * 检查用户ID是否存在
     */
    boolean existsByUserId(String userId);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
}
