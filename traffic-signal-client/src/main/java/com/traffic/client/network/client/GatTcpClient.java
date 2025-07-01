package com.traffic.client.network.client;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.codec.MessageCodec;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoUser;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GA/T 1049.2 TCP客户端
 * 使用Netty实现高性能的TCP客户端
 */
public class GatTcpClient {

    private static final Logger logger = LoggerFactory.getLogger(GatTcpClient.class);
    public static String tocken = null;
    private final String host;
    private final int port;
    private final String clientId;
    private final MessageResponseHandler responseHandler;
    private final MessageCodec codec;
    private final MessageProcessor messageProcessor;

    private EventLoopGroup workerGroup;
    private Channel channel;
    private Bootstrap bootstrap;
    private volatile boolean connected = false;
    private final AtomicLong sequenceGenerator = new AtomicLong(0);

    // 用于同步请求响应
    private final ConcurrentHashMap<String, CompletableFuture<Message>> pendingRequests = new ConcurrentHashMap<>();

    // 重连控制
    private ScheduledExecutorService reconnectExecutor;
    private ScheduledFuture<?> reconnectTask;
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 10000;
    private static final long RECONNECT_DELAY = 10000; // 10秒
    private String username;
    private String password;
    // 添加重连状态控制
    private volatile ReconnectState reconnectState = ReconnectState.IDLE;
    private final Object reconnectLock = new Object();

    public GatTcpClient(String host, int port, String clientId, MessageProcessor messageProcessor) throws Exception {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.username = "";
        this.password = "";
        this.responseHandler = new MessageResponseHandler();
        this.messageProcessor = messageProcessor;
        this.codec = MessageCodec.getInstance();
        this.reconnectExecutor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "GatTcpClient-Reconnect"));
    }
    public GatTcpClient(String host, int port, String clientId, MessageProcessor messageProcessor, String username, String password) throws Exception {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.responseHandler = new MessageResponseHandler();
        this.messageProcessor = messageProcessor;
        this.codec = MessageCodec.getInstance();
        this.reconnectExecutor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "GatTcpClient-Reconnect"));
    }

    /**
     * 连接到服务器
     */
    public void connect() throws Exception {
        if (connected) {
            logger.warn("Client is already connected to {}:{}", host, port);
            return;
        }

        workerGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        // 添加长度字段解码器
//                        pipeline.addLast("frameDecoder",
//                                new LengthFieldBasedFrameDecoder(
//                                        GatConstants.Network.MAX_MESSAGE_SIZE,
//                                        0, 4, 0, 4));

                        // 添加长度字段编码器
                        //pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                        ByteBuf delimiter = Unpooled.copiedBuffer("</Message>".getBytes(CharsetUtil.UTF_8));
                        pipeline.addLast("frameDecoder",
                                new DelimiterBasedFrameDecoder(
                                        GatConstants.Network.MAX_MESSAGE_SIZE,
                                        false,  // stripDelimiter = false，保留分隔符（关键修改）
                                        delimiter
                                ));
                        // 字符串编解码器
                        pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                        // 心跳处理
                        pipeline.addLast("idleStateHandler",
                                new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS));

                        // 业务处理器
                        pipeline.addLast("handler", new GatClientHandler());
                    }
                });

        try {
            // 首次连接尝试
            doConnect();
        } catch (Exception e) {
            logger.warn("Initial connection failed, will start auto-reconnect: {}", e.getMessage());

            // 初始连接失败，启动重连机制
            synchronized (reconnectLock) {
                reconnectState = ReconnectState.IDLE;
                reconnectAttempts = 0; // 重置计数器，因为这是初始连接
            }

            // 启动重连
            scheduleReconnect();

            // 不抛出异常，让应用继续运行，依靠重连机制
            // throw e; // 注释掉这行
        }
    }

    /**
     * 修复后的doConnect方法
     */
    private void doConnect() throws Exception {
        try {
            // 如果已经连接，直接返回
            if (connected && channel != null && channel.isActive()) {
                logger.info("Already connected, skipping connection attempt");
                synchronized (reconnectLock) {
                    reconnectState = ReconnectState.IDLE;
                    reconnectAttempts = 0;
                }
                return;
            }

            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();
            connected = true;

            // 连接成功，重置重连状态
            synchronized (reconnectLock) {
                reconnectState = ReconnectState.IDLE;
                reconnectAttempts = 0;
            }

            logger.info("Successfully connected to GA/T 1049.2 server at {}:{} (attempt {})",
                    host, port, reconnectAttempts);

        } catch (Exception e) {
            logger.error("Failed to connect to server at {}:{} (attempt {}): {}",
                    host, port, reconnectAttempts, e.getMessage());
            throw e;
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (!connected) {
            return;
        }

        logger.info("Disconnecting from GA/T 1049.2 server...");

        try {
            // 停止所有重连活动
            synchronized (reconnectLock) {
                reconnectState = ReconnectState.STOPPED;
                reconnectAttempts = 0;

                if (reconnectTask != null) {
                    reconnectTask.cancel(true);
                    reconnectTask = null;
                }
            }

            connected = false;

            // 取消所有待处理的请求
            pendingRequests.forEach((seq, future) ->
                    future.completeExceptionally(new Exception("Client disconnected")));
            pendingRequests.clear();

            // 关闭通道
            if (channel != null) {
                channel.close().sync();
            }

            // 关闭线程组
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }

            // 关闭重连执行器
            if (reconnectExecutor != null) {
                reconnectExecutor.shutdown();
            }

            logger.info("Disconnected from GA/T 1049.2 server");
        } catch (Exception e) {
            logger.error("Error disconnecting from server", e);
        }
    }
    /**
     * 重新连接（公共方法，用于手动触发重连）
     */
    public void reconnect() {
        synchronized (reconnectLock) {
            if (reconnectState == ReconnectState.STOPPED) {
                logger.info("Reconnect was stopped, resetting state to allow reconnection");
                reconnectState = ReconnectState.IDLE;
                reconnectAttempts = 0;
            }

            if (reconnectState == ReconnectState.IDLE) {
                logger.info("Manual reconnect triggered");
                scheduleReconnect();
            } else {
                logger.info("Reconnect already in progress (state: {})", reconnectState);
            }
        }
    }
    /**
     * 停止重连
     */
    public void stopReconnect() {
        synchronized (reconnectLock) {
            logger.info("Stopping reconnect mechanism");
            reconnectState = ReconnectState.STOPPED;
            if (reconnectTask != null) {
                reconnectTask.cancel(true);
                reconnectTask = null;
            }
        }
    }
    /**
     * 获取重连状态信息
     */
    public String getReconnectStatus() {
        synchronized (reconnectLock) {
            return String.format("State: %s, Attempts: %d/%d",
                    reconnectState, reconnectAttempts, MAX_RECONNECT_ATTEMPTS);
        }
    }
    /**
     * 发送登录请求
     */
//    private void sendLoginRequest() {
//        try {
//            Message loginMessage = MessageBuilder.create()
//                    .request()
//                    .fromUtcs()
//                    .toTicp()
//                    .operation("Login", new Object() {
//                        public String getClientId() { return clientId; }
//                        public String getVersion() { return GatConstants.PROTOCOL_VERSION; }
//                    })
//                    .build();
//            logger.info(loginMessage.toString());
//            sendMessage(loginMessage);
//            logger.info("Login request sent");
//        } catch (Exception e) {
//            logger.error("Failed to send login request", e);
//        }
//    }
    private void sendLoginRequest() {
        try {
            // 使用具体的 SdoUser 对象
            SdoUser user =
                    new SdoUser(this.username, this.password);

            Message loginMessage = MessageBuilder.create()
                    .request()
                    .fromUtcs()
                    .toTicp()
                    .operation("Login", user)  // 使用具体对象而不是匿名内部类
                    .build();

            logger.info(loginMessage.toString());
            sendMessage(loginMessage);
            logger.info("Login request sent");
        } catch (Exception e) {
            logger.error("Failed to send login request", e);
        }
    }
    /**
     * 发送消息
     */
    public void sendMessage(Message message) throws Exception {
        if (!connected || channel == null || !channel.isActive()) {
            throw new Exception("Not connected to server");
        }

        String xml = codec.encode(message);
        channel.writeAndFlush(xml);
        logger.debug("Sent message: seq={}, type={}", message.getSeq(), message.getType());
    }

    /**
     * 发送请求并等待响应
     */
    public Message sendRequest(Message request, long timeout, TimeUnit unit) throws Exception {
        if (!connected) {
            throw new Exception("Not connected to server");
        }

        String seq = request.getSeq();//request.generateSequence();
        CompletableFuture<Message> future = new CompletableFuture<>();
        pendingRequests.put(seq, future);

        try {
            sendMessage(request);
            return future.get(timeout, unit);
        } catch (TimeoutException e) {
            pendingRequests.remove(seq);
            throw new Exception("Request timeout: " + seq);
        } catch (Exception e) {
            pendingRequests.remove(seq);
            throw e;
        }
    }

    /**
     * 发送心跳
     */
    private void sendHeartbeat() {
        try {
            Message heartbeat = MessageBuilder.createHeartbeatMessage(tocken, this.username);
            sendMessage(heartbeat);

            logger.debug("Heartbeat sent");
        } catch (Exception e) {
            logger.error("Failed to send heartbeat", e);
        }
    }

    /**
     * 生成序列号
     */
    private String generateSequence() {
        return clientId + "_" + sequenceGenerator.incrementAndGet();
    }

    /**
     * 是否已连接
     */
    public boolean isConnected() {
        return connected && channel != null && channel.isActive();
    }

    /**
     * 获取响应处理器
     */
    public MessageResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public String getUsername(){ return username; }
    public String getPassword(){ return password; }
    public String getHost() { return host; }

    /**
     * 完全重写的重连逻辑
     */
    private void scheduleReconnect() {
        synchronized (reconnectLock) {
            // 检查是否应该停止重连
            if (reconnectState == ReconnectState.STOPPED) {
                logger.info("Reconnect is stopped, will not schedule new reconnect");
                return;
            }

            // 检查是否已经在重连流程中
            if (reconnectState == ReconnectState.CONNECTING || reconnectState == ReconnectState.SCHEDULED) {
                logger.debug("Reconnect already in progress (state: {}), skipping...", reconnectState);
                return;
            }

            // 检查重连次数
            if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                logger.error("Max reconnect attempts ({}) reached. Giving up.", MAX_RECONNECT_ATTEMPTS);
                reconnectState = ReconnectState.STOPPED;
                return;
            }

            // 设置为已安排状态
            reconnectState = ReconnectState.SCHEDULED;
            reconnectAttempts++;

            logger.info("Scheduling reconnect attempt {} in {} ms", reconnectAttempts, RECONNECT_DELAY);

            // 取消之前的重连任务（如果存在）
            if (reconnectTask != null && !reconnectTask.isDone()) {
                reconnectTask.cancel(false);
            }

            reconnectTask = reconnectExecutor.schedule(() -> {
                synchronized (reconnectLock) {
                    // 再次检查状态
                    if (reconnectState == ReconnectState.STOPPED) {
                        logger.info("Reconnect was stopped, aborting reconnect attempt");
                        return;
                    }

                    // 设置为连接中状态
                    reconnectState = ReconnectState.CONNECTING;
                }

                try {
                    logger.info("Attempting to reconnect... (attempt {}/{})", reconnectAttempts, MAX_RECONNECT_ATTEMPTS);
                    doConnect();
                } catch (Exception e) {
                    logger.error("Reconnect attempt {} failed: {}", reconnectAttempts, e.getMessage());

                    synchronized (reconnectLock) {
                        // 连接失败，重置为空闲状态
                        reconnectState = ReconnectState.IDLE;

                        // 继续尝试重连（会在scheduleReconnect中检查次数限制）
                        scheduleReconnect();
                    }
                }
            }, RECONNECT_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 客户端通道处理器
     */
    private class GatClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            logger.info("Channel active");
            connected = true;

            // 通道激活，重置重连状态
            synchronized (reconnectLock) {
                reconnectState = ReconnectState.IDLE;
                reconnectAttempts = 0;
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            logger.info("Channel inactive");
            connected = false;

            // 只有在没有关闭且允许重连时才触发重连
            if (!workerGroup.isShuttingDown()) {
                synchronized (reconnectLock) {
                    // 只有在空闲状态下才触发重连
                    if (reconnectState == ReconnectState.IDLE) {
                        logger.info("Channel became inactive, triggering reconnect");
                        scheduleReconnect();
                    } else {
                        logger.debug("Channel inactive but reconnect state is {}, not triggering new reconnect", reconnectState);
                    }
                }
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            logger.debug("Received message: {}", msg);

            try {
                Message message = codec.decode(msg);

                // 检查是否是响应消息
                if ("RESPONSE".equals(message.getType()) || "ERROR".equals(message.getType())) {
                    CompletableFuture<Message> future = pendingRequests.remove(message.getSeq());
                    if (future != null) {
                        future.complete(message);
                    }
                }

                if("REQUEST".equals(message.getType())){
                    String response = messageProcessor.processMessage(msg);

                    // 发送响应
                    if (response != null && !response.trim().isEmpty()) {
                        ctx.writeAndFlush(response);
                        logger.debug("发送响应: {}", response);
                    }
                }
                // 交给响应处理器处理
                responseHandler.handleMessage(message);

            } catch (Exception e) {
                logger.error("Error processing received message", e);
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof io.netty.handler.timeout.IdleStateEvent) {
                // 发送心跳
                sendHeartbeat();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("Channel exception", cause);
            ctx.close();
        }
    }

    /**
     * 消息响应处理器
     */
    public static class MessageResponseHandler {

        private final ConcurrentHashMap<String, MessageListener> listeners = new ConcurrentHashMap<>();

        public void handleMessage(Message message) {
            // 处理推送消息
            if ("PUSH".equals(message.getType())) {
                handlePushMessage(message);
            }

            // 触发监听器
            listeners.values().forEach(listener -> {
                try {
                    listener.onMessage(message);
                } catch (Exception e) {
                    logger.error("Error in message listener", e);
                }
            });
        }

        private void handlePushMessage(Message message) {
            logger.info("Received push message: {}", message.getSeq());
            // TODO: 处理推送消息的具体逻辑
        }

        public void addListener(String name, MessageListener listener) {
            listeners.put(name, listener);
        }

        public void removeListener(String name) {
            listeners.remove(name);
        }
    }

    /**
     * 消息监听器接口
     */
    public interface MessageListener {
        void onMessage(Message message);
    }

    // 重连状态枚举
    private enum ReconnectState {
        IDLE,       // 空闲状态
        CONNECTING, // 正在连接
        SCHEDULED,  // 已安排重连
        STOPPED     // 已停止重连
    }
}
