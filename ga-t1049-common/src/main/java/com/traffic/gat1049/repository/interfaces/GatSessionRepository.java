package com.traffic.gat1049.repository.interfaces;

import com.traffic.gat1049.repository.entity.GatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会话表Repository
 */
@Repository
public interface GatSessionRepository extends JpaRepository<GatSessionEntity, Long> {

    /**
     * 根据会话ID查找
     */
    Optional<GatSessionEntity> findBySessionId(String sessionId);

    /**
     * 根据令牌查找
     */
    Optional<GatSessionEntity> findByToken(String token);

    /**
     * 根据用户ID查找所有会话
     */
    List<GatSessionEntity> findByUserId(String userId);

    /**
     * 根据用户ID查找有效会话
     */
    @Query("SELECT s FROM GatSessionEntity s WHERE s.userId = :userId AND s.status = 1 AND s.expireTime > :now")
    List<GatSessionEntity> findValidSessionsByUserId(@Param("userId") String userId,
                                                     @Param("now") LocalDateTime now);

    /**
     * 根据客户端IP查找会话
     */
    List<GatSessionEntity> findByClientIp(String clientIp);

    /**
     * 根据会话状态查找
     */
    List<GatSessionEntity> findByStatus(Integer status);

    /**
     * 查找有效会话
     */
    @Query("SELECT s FROM GatSessionEntity s WHERE s.status = 1 AND s.expireTime > :now")
    List<GatSessionEntity> findValidSessions(@Param("now") LocalDateTime now);

    /**
     * 查找过期会话
     */
    @Query("SELECT s FROM GatSessionEntity s WHERE s.expireTime < :now")
    List<GatSessionEntity> findExpiredSessions(@Param("now") LocalDateTime now);

    /**
     * 根据最后访问时间查找
     */
    @Query("SELECT s FROM GatSessionEntity s WHERE s.lastAccessTime < :beforeTime")
    List<GatSessionEntity> findByLastAccessTimeBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 清理过期会话
     */
    void deleteByExpireTimeBefore(LocalDateTime expireTime);

    /**
     * 清理指定用户的所有会话
     */
    void deleteByUserId(String userId);

    /**
     * 统计活跃会话数量
     */
    @Query("SELECT COUNT(s) FROM GatSessionEntity s WHERE s.status = 1 AND s.expireTime > :now")
    Long countActiveSessions(@Param("now") LocalDateTime now);

    /**
     * 统计指定用户的活跃会话数量
     */
    @Query("SELECT COUNT(s) FROM GatSessionEntity s WHERE s.userId = :userId AND s.status = 1 AND s.expireTime > :now")
    Long countActiveSessionsByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * 检查会话ID是否存在
     */
    boolean existsBySessionId(String sessionId);

    /**
     * 检查令牌是否存在
     */
    boolean existsByToken(String token);
}
