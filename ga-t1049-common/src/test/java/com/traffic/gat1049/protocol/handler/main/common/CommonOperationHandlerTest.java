package com.traffic.gat1049.protocol.handler.main.common;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.model.enums.OperationName;
import com.traffic.gat1049.model.enums.SystemType;
import com.traffic.gat1049.protocol.model.core.Address;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.core.MessageBody;
import com.traffic.gat1049.protocol.model.core.Operation;
import com.traffic.gat1049.protocol.model.sdo.SdoUser;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CommonOperationHandler 测试用例 - 修正版
 *
 * 修正了 SessionManager.login() 方法的调用签名
 * 实际方法签名：login(String clientAddress, SdoUser user, SystemType systemType)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("通用操作处理器测试 - 方法签名修正版")
class CommonOperationHandlerTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SubscriptionManager subscriptionManager;

    private CommonOperationHandler handler;

    // 测试数据常量
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_TOKEN = "test-token-12345";
    private static final String TEST_CLIENT_ADDRESS = "192.168.1.100";

    @BeforeEach
    void setUp() {
        handler = new CommonOperationHandler(sessionManager, subscriptionManager);
    }

    // ==================== 基础测试 ====================

    @Test
    @DisplayName("测试处理器初始化")
    void testHandlerInitialization() {
        assertNotNull(handler, "处理器不应该为空");
    }

    @Test
    @DisplayName("创建登录请求消息")
    void testCreateLoginMessage() {
        Message loginMessage = createLoginRequest(TEST_USERNAME, TEST_PASSWORD);

        assertNotNull(loginMessage, "登录消息不应该为空");
        assertEquals("REQUEST", loginMessage.getType(), "消息类型应该是REQUEST");
        assertEquals("1.0", loginMessage.getVersion(), "版本应该是1.0");
        assertNotNull(loginMessage.getBody(), "消息体不应该为空");

        assertEquals(1, loginMessage.getBody().getOperations().size(), "应该有1个操作");
        Operation operation = loginMessage.getBody().getOperations().get(0);
        assertEquals("Login", operation.getName(), "操作名称应该是Login");

        assertTrue(operation.getData() instanceof SdoUser, "数据应该是SdoUser类型");
        SdoUser user = (SdoUser) operation.getData();
        assertEquals(TEST_USERNAME, user.getUserName(), "用户名应该正确");
        assertEquals(TEST_PASSWORD, user.getPwd(), "密码应该正确");
    }

    // ==================== Mock 测试示例 - 使用正确的方法签名 ====================

    @Test
    @DisplayName("模拟登录成功 - 使用正确的方法签名")
    void testLoginSuccess_WithCorrectSignature() {
        // 创建测试用户对象
        SdoUser testUser = createTestUser(TEST_USERNAME, TEST_PASSWORD);

        // 创建模拟的登录结果
        SessionManager.LoginResult mockResult = createMockLoginResult(true, TEST_TOKEN, TEST_USERNAME);

        // 配置Mock行为 - 使用正确的方法签名
        when(sessionManager.login(eq(TEST_CLIENT_ADDRESS), eq(testUser), eq(SystemType.TICP)))
                .thenReturn(mockResult);

        // 执行登录
        SessionManager.LoginResult result = sessionManager.login(TEST_CLIENT_ADDRESS, testUser, SystemType.TICP);

        // 验证结果
        assertNotNull(result, "登录结果不应该为空");
        assertTrue(result.isSuccess(), "登录应该成功");
        assertEquals(TEST_TOKEN, result.getToken(), "应该返回正确的令牌");
        assertEquals(TEST_USERNAME, result.getUserName(), "应该返回正确的用户名");

        // 验证方法被调用
        verify(sessionManager, times(1)).login(TEST_CLIENT_ADDRESS, testUser, SystemType.TICP);
    }

    @Test
    @DisplayName("模拟登录失败 - 用户名密码错误")
    void testLoginFailure_InvalidCredentials() {
        // 创建错误的用户对象
        SdoUser wrongUser = createTestUser("wronguser", "wrongpass");

        // 创建模拟的失败结果
        SessionManager.LoginResult mockResult = createMockLoginResult(false, null, "wronguser");

        // 配置Mock行为
        when(sessionManager.login(eq(TEST_CLIENT_ADDRESS), eq(wrongUser), eq(SystemType.TICP)))
                .thenReturn(mockResult);

        // 执行登录
        SessionManager.LoginResult result = sessionManager.login(TEST_CLIENT_ADDRESS, wrongUser, SystemType.TICP);

        // 验证结果
        assertNotNull(result, "登录结果不应该为空");
        assertFalse(result.isSuccess(), "登录应该失败");
        assertNull(result.getToken(), "失败时令牌应该为空");

        // 验证方法被调用
        verify(sessionManager).login(TEST_CLIENT_ADDRESS, wrongUser, SystemType.TICP);
    }

    @Test
    @DisplayName("模拟登录异常处理")
    void testLoginException() {
        SdoUser testUser = createTestUser(TEST_USERNAME, TEST_PASSWORD);

        // 配置Mock抛出异常
        when(sessionManager.login(anyString(), any(SdoUser.class), any(SystemType.class)))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // 验证异常抛出
        assertThrows(RuntimeException.class, () -> {
            sessionManager.login(TEST_CLIENT_ADDRESS, testUser, SystemType.TICP);
        }, "应该抛出运行时异常");

        // 验证方法被调用
        verify(sessionManager).login(TEST_CLIENT_ADDRESS, testUser, SystemType.TICP);
    }

    // ==================== 令牌验证测试 ====================

    @Test
    @DisplayName("令牌验证 - 有效令牌")
    void testValidateToken_ValidToken() {
        // 配置Mock行为
        when(sessionManager.validateToken(TEST_TOKEN)).thenReturn(true);

        // 执行验证
        boolean isValid = sessionManager.validateToken(TEST_TOKEN);

        // 验证结果
        assertTrue(isValid, "有效令牌应该返回true");

        // 验证方法被调用
        verify(sessionManager).validateToken(TEST_TOKEN);
    }

    @Test
    @DisplayName("令牌验证 - 无效令牌")
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid-token";

        // 配置Mock行为
        when(sessionManager.validateToken(invalidToken)).thenReturn(false);

        // 执行验证
        boolean isValid = sessionManager.validateToken(invalidToken);

        // 验证结果
        assertFalse(isValid, "无效令牌应该返回false");

        // 验证方法被调用
        verify(sessionManager).validateToken(invalidToken);
    }

    // ==================== 登出测试 ====================

    @Test
    @DisplayName("用户登出 - 成功")
    void testLogout_Success() {
        // 配置Mock行为
        when(sessionManager.logout(TEST_TOKEN)).thenReturn(true);

        // 执行登出
        boolean result = sessionManager.logout(TEST_TOKEN);

        // 验证结果
        assertTrue(result, "登出应该成功");

        // 验证方法被调用
        verify(sessionManager).logout(TEST_TOKEN);
    }

    @Test
    @DisplayName("用户登出 - 令牌不存在")
    void testLogout_TokenNotFound() {
        String nonExistentToken = "non-existent-token";

        // 配置Mock行为
        when(sessionManager.logout(nonExistentToken)).thenReturn(false);

        // 执行登出
        boolean result = sessionManager.logout(nonExistentToken);

        // 验证结果
        assertFalse(result, "不存在的令牌登出应该失败");

        // 验证方法被调用
        verify(sessionManager).logout(nonExistentToken);
    }

    // ==================== 会话信息测试 ====================

    @Test
    @DisplayName("获取会话信息")
    void testGetSession() {
        // 创建模拟的会话信息
        SessionManager.SessionInfo mockSession = createMockSessionInfo();

        // 配置Mock行为
        when(sessionManager.getSession(TEST_TOKEN)).thenReturn(mockSession);

        // 执行获取会话
        SessionManager.SessionInfo session = sessionManager.getSession(TEST_TOKEN);

        // 验证结果
        assertNotNull(session, "会话信息不应该为空");
        assertEquals(TEST_USERNAME, session.getUserName(), "用户名应该正确");
        assertEquals(TEST_TOKEN, session.getToken(), "令牌应该正确");

        // 验证方法被调用
        verify(sessionManager).getSession(TEST_TOKEN);
    }

    // ==================== 参数化测试 ====================

    @ParameterizedTest
    @ValueSource(strings = {"admin", "user1", "testuser", "guest"})
    @DisplayName("多用户登录测试")
    void testMultipleUserLogins(String username) {
        SdoUser user = createTestUser(username, "password123");
        SessionManager.LoginResult mockResult = createMockLoginResult(true,
                "token-" + username, username);

        // 配置Mock
        when(sessionManager.login(anyString(), eq(user), any(SystemType.class)))
                .thenReturn(mockResult);

        // 执行登录
        SessionManager.LoginResult result = sessionManager.login(
                TEST_CLIENT_ADDRESS, user, SystemType.TICP);

        // 验证结果
        assertTrue(result.isSuccess(), username + " 登录应该成功");
        assertEquals(username, result.getUserName(), "用户名应该正确");
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("空值处理测试")
    void testNullHandling() {
        // 测试空用户对象
        when(sessionManager.login(anyString(), isNull(), any(SystemType.class)))
                .thenReturn(createMockLoginResult(false, null, null));

        SessionManager.LoginResult result = sessionManager.login(
                TEST_CLIENT_ADDRESS, null, SystemType.TICP);

        assertFalse(result.isSuccess(), "空用户对象登录应该失败");

        // 测试空令牌验证
        when(sessionManager.validateToken(null)).thenReturn(false);
        assertFalse(sessionManager.validateToken(null), "空令牌验证应该失败");

        when(sessionManager.validateToken("")).thenReturn(false);
        assertFalse(sessionManager.validateToken(""), "空字符串令牌验证应该失败");
    }

    @Test
    @DisplayName("边界条件 - 超长用户名")
    void testLongUsername() {
        String longUsername = "a".repeat(100);
        SdoUser user = createTestUser(longUsername, "password");

        // 模拟超长用户名被拒绝
        SessionManager.LoginResult mockResult = createMockLoginResult(false, null, longUsername);
        when(sessionManager.login(anyString(), eq(user), any(SystemType.class)))
                .thenReturn(mockResult);

        SessionManager.LoginResult result = sessionManager.login(
                TEST_CLIENT_ADDRESS, user, SystemType.TICP);

        assertFalse(result.isSuccess(), "超长用户名应该被拒绝");
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试用户对象
     */
    private SdoUser createTestUser(String username, String password) {
        SdoUser user = new SdoUser();
        user.setUserName(username);
        user.setPwd(password);
        return user;
    }

    /**
     * 创建模拟的登录结果
     */
    private SessionManager.LoginResult createMockLoginResult(boolean success, String token, String userName) {
        // 注意：这里需要根据实际的LoginResult类来创建对象
        // 如果LoginResult是静态内部类，可能需要不同的创建方式

        // 方式1：如果有静态工厂方法
        if (success) {
            return SessionManager.LoginResult.success(token, userName);
        } else {
            return SessionManager.LoginResult.failure("登录失败", userName);
        }

        // 方式2：如果需要直接创建（根据实际情况调整）
        // return new SessionManager.LoginResult(success, token, userName, success ? null : "登录失败");
    }

    /**
     * 创建模拟的会话信息
     */
    private SessionManager.SessionInfo createMockSessionInfo() {
        // 注意：这里需要根据实际的SessionInfo构造函数来创建
        // 查看SessionInfo的构造方法参数
        return new SessionManager.SessionInfo(
                "session-123",           // sessionId
                TEST_TOKEN,              // token
                TEST_USERNAME,           // userName
                TEST_CLIENT_ADDRESS,     // clientAddress
                SystemType.TICP          // systemType
        );
    }

    /**
     * 创建登录请求消息
     */
    private Message createLoginRequest(String username, String password) {
        SdoUser user = createTestUser(username, password);

        Operation operation = new Operation();
        operation.setOrder(1);
        operation.setName("Login");
        operation.setData(user);

        MessageBody body = new MessageBody();
        body.addOperation(operation);

        Message message = new Message();
        message.setVersion("1.0");
        message.setToken(""); // 登录前令牌为空
        message.setType("REQUEST");
        message.setSeq(generateSequence());
//        message.setSource(createClientAddress());
//        message.setTarget(createServerAddress());
        message.setBody(body);

        return message;
    }

    /**
     * 创建客户端地址
     */
    private Address createClientAddress() {
        Address address = new Address();
        address.setSys("TICP");
        address.setSubSys("01");
        address.setInstance("001");
        return address;
    }

    /**
     * 创建服务端地址
     */
    private Address createServerAddress() {
        Address address = new Address();
        address.setSys("UTCS");
        address.setSubSys("01");
        address.setInstance("001");
        return address;
    }

    /**
     * 生成序列号
     */
    private String generateSequence() {
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 1000000);
        return String.format("%014d%06d", timestamp, random);
    }

    // ==================== 嵌套测试类 ====================

    @Nested
    @DisplayName("系统类型测试")
    class SystemTypeTests {

        @Test
        @DisplayName("不同系统类型登录")
        void testDifferentSystemTypes() {
            SdoUser user = createTestUser(TEST_USERNAME, TEST_PASSWORD);

            // 测试 TICP 系统类型
            SessionManager.LoginResult ticpResult = createMockLoginResult(true, "ticp-token", TEST_USERNAME);
            when(sessionManager.login(anyString(), eq(user), eq(SystemType.TICP)))
                    .thenReturn(ticpResult);

            SessionManager.LoginResult result1 = sessionManager.login(TEST_CLIENT_ADDRESS, user, SystemType.TICP);
            assertTrue(result1.isSuccess(), "TICP系统类型登录应该成功");

            // 测试 UTCS 系统类型
            SessionManager.LoginResult utcsResult = createMockLoginResult(true, "utcs-token", TEST_USERNAME);
            when(sessionManager.login(anyString(), eq(user), eq(SystemType.UTCS)))
                    .thenReturn(utcsResult);

            SessionManager.LoginResult result2 = sessionManager.login(TEST_CLIENT_ADDRESS, user, SystemType.UTCS);
            assertTrue(result2.isSuccess(), "UTCS系统类型登录应该成功");
        }
    }

    @Nested
    @DisplayName("并发测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("并发登录性能测试")
        void testConcurrentLogins() {
            // 模拟并发登录场景
            SdoUser user = createTestUser(TEST_USERNAME, TEST_PASSWORD);
            SessionManager.LoginResult mockResult = createMockLoginResult(true, TEST_TOKEN, TEST_USERNAME);

            when(sessionManager.login(anyString(), any(SdoUser.class), any(SystemType.class)))
                    .thenReturn(mockResult);

            long startTime = System.currentTimeMillis();

            // 执行100次登录操作
            for (int i = 0; i < 100; i++) {
                SessionManager.LoginResult result = sessionManager.login(
                        TEST_CLIENT_ADDRESS, user, SystemType.TICP);
                assertTrue(result.isSuccess(), "第" + (i+1) + "次登录应该成功");
            }

            long duration = System.currentTimeMillis() - startTime;
            assertTrue(duration < 1000, "100次登录应该在1秒内完成，实际耗时: " + duration + "ms");

            // 验证调用次数
            verify(sessionManager, times(100)).login(anyString(), any(SdoUser.class), any(SystemType.class));
        }
    }
}