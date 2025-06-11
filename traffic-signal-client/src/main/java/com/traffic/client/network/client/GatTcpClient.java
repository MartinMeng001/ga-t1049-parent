package com.traffic.client.network.client;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.codec.MessageCodec;
import com.traffic.gat1049.protocol.model.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
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
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final long RECONNECT_DELAY = 5000; // 5秒

    public GatTcpClient(String host, int port, String clientId) throws Exception {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.responseHandler = new MessageResponseHandler();
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
                        pipeline.addLast("frameDecoder",
                                new LengthFieldBasedFrameDecoder(
                                        GatConstants.Network.MAX_MESSAGE_SIZE,
                                        0, 4, 0, 4));

                        // 添加长度字段编码器
                        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

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

        doConnect();
    }

    private void doConnect() throws Exception {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();
            connected = true;
            reconnectAttempts = 0;

            logger.info("Connected to GA/T 1049.2 server at {}:{}", host, port);

            // 连接成功后发送登录请求
            // sendLoginRequest();

        } catch (Exception e) {
            logger.error("Failed to connect to server at {}:{}", host, port, e);
            scheduleReconnect();
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

        connected = false;
        logger.info("Disconnecting from GA/T 1049.2 server...");

        try {
            // 取消重连任务
            if (reconnectTask != null) {
                reconnectTask.cancel(true);
            }

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
            com.traffic.gat1049.model.entity.sdo.SdoUser user =
                    new com.traffic.gat1049.model.entity.sdo.SdoUser(/*clientId*/"tsc_client", "tsc123");

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

        String seq = request.getSeq();
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
            Message heartbeat = MessageBuilder.createHeartbeatMessage(tocken);
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

    /**
     * 安排重连
     */
    private void scheduleReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            logger.error("Max reconnect attempts reached. Giving up.");
            return;
        }

        reconnectAttempts++;
        logger.info("Scheduling reconnect attempt {} in {} ms", reconnectAttempts, RECONNECT_DELAY);

        reconnectTask = reconnectExecutor.schedule(() -> {
            try {
                logger.info("Attempting to reconnect...");
                doConnect();
            } catch (Exception e) {
                logger.error("Reconnect failed", e);
                scheduleReconnect();
            }
        }, RECONNECT_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * 客户端通道处理器
     */
    private class GatClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            logger.info("Channel active");
            connected = true;
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            logger.info("Channel inactive");
            connected = false;

            // 触发重连
            if (!workerGroup.isShuttingDown()) {
                scheduleReconnect();
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
}
