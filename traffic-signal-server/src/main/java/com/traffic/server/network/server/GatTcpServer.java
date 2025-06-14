package com.traffic.server.network.server;

import com.traffic.gat1049.application.connection.ConnectionManager;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.server.network.client.ServerToClientSender;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * GA/T 1049 TCP服务器
 * 处理客户端连接和消息通信
 */
@Component
public class GatTcpServer {

    private static final Logger logger = LoggerFactory.getLogger(GatTcpServer.class);

    @Value("${tcp.server.port:9999}")
    private int port;

    @Value("${tcp.server.boss-threads:1}")
    private int bossThreads;

    @Value("${tcp.server.worker-threads:4}")
    private int workerThreads;

    @Autowired
    private MessageProcessor messageProcessor;

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired(required = false)
    private ServerToClientSender clientSender;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    @PostConstruct
    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(bossThreads);
        workerGroup = new NioEventLoopGroup(workerThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
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

                        // 空闲状态处理器
                        pipeline.addLast("idleStateHandler",
                                new IdleStateHandler(
                                        GatConstants.Session.CONNECTION_TIMEOUT,
                                        0, 0, TimeUnit.SECONDS));

                        // 业务处理器 - 传入 clientSender
                        pipeline.addLast("handler", new GatServerHandler(messageProcessor, clientSender, connectionManager));
                    }
                });

        ChannelFuture future = bootstrap.bind(port).sync();
        serverChannel = future.channel();

        logger.info("GA/T 1049 TCP服务器启动成功，端口: {}", port);
    }

    @PreDestroy
    public void stop() {
        logger.info("正在关闭GA/T 1049 TCP服务器...");

        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
        } catch (InterruptedException e) {
            logger.error("关闭服务器通道时发生错误", e);
        } finally {
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
        }

        logger.info("GA/T 1049 TCP服务器已关闭");
    }

    /**
     * 服务器通道处理器
     */
    private static class GatServerHandler extends SimpleChannelInboundHandler<String> {

        private static final Logger logger = LoggerFactory.getLogger(GatServerHandler.class);
        private final MessageProcessor messageProcessor;
        private final ServerToClientSender clientSender; // 新增
        private final ConnectionManager connectionManager;
        private String clientId; // 新增

        public GatServerHandler(MessageProcessor messageProcessor, ServerToClientSender clientSender, ConnectionManager connectionManager) {
            this.messageProcessor = messageProcessor;
            this.clientSender = clientSender;
            this.connectionManager = connectionManager;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            String clientAddress = ctx.channel().remoteAddress().toString();
            logger.info("客户端连接: {}", clientAddress);

            // 生成或提取客户端ID（这里简化处理，实际应该从登录消息中获取）
            clientId = ctx.channel().id().asShortText();//"CLIENT_" + System.currentTimeMillis();

            // 注册客户端到网络发送器
            if (clientSender != null) {
                clientSender.registerClient(clientId, ctx);
                logger.info("客户端已注册到网络发送器: {}", clientId);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            String clientAddress = ctx.channel().remoteAddress().toString();
            logger.info("客户端断开: {}", clientAddress);

            // 从网络发送器中移除客户端
            if (clientSender != null && clientId != null) {
                clientSender.unregisterClient(clientId);
                logger.info("客户端已从网络发送器移除: {}", clientId);
                connectionManager.unregisterConnection(clientId);
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            logger.debug("收到消息: {}", msg);

            try {
                // 处理消息
                String response = messageProcessor.processMessage(msg);

                // 发送响应
                if (response != null && !response.trim().isEmpty()) {
                    ctx.writeAndFlush(response);
                    logger.debug("发送响应: {}", response);
                    if(messageProcessor.getTempToken().isEmpty()==false)
                        connectionManager.registerConnection(ctx.channel().id().asShortText(), messageProcessor.getTempToken());
                }

            } catch (Exception e) {
                logger.error("处理消息时发生错误", e);

                // 发送错误响应
                String errorResponse = createErrorResponse("消息处理失败: " + e.getMessage());
                ctx.writeAndFlush(errorResponse);
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof io.netty.handler.timeout.IdleStateEvent) {
                logger.warn("客户端连接空闲超时: {}", ctx.channel().remoteAddress());
                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("通道异常: " + ctx.channel().remoteAddress(), cause);
            ctx.close();
        }

        private String createErrorResponse(String errorMessage) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<Message>\n" +
                    "  <Version>2.0</Version>\n" +
                    "  <Token></Token>\n" +
                    "  <From><Sys>TICP</Sys></From>\n" +
                    "  <To><Sys>UTCS</Sys></To>\n" +
                    "  <Type>ERROR</Type>\n" +
                    "  <Seq>" + Message.generateSequence() + "</Seq>\n" +
                    "  <Body>\n" +
                    "    <Operation order=\"1\" name=\"Error\">\n" +
                    "      <SDO_Error>\n" +
                    "        <ErrObj></ErrObj>\n" +
                    "        <ErrType>SDE_Failure</ErrType>\n" +
                    "        <ErrDesc>" + errorMessage + "</ErrDesc>\n" +
                    "      </SDO_Error>\n" +
                    "    </Operation>\n" +
                    "  </Body>\n" +
                    "</Message>";
        }
    }
}