package com.guoshengkai.reverseapistudio.socks5;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.codec.socksx.v5.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiatian
 */
@Slf4j
public class NettySocks5ProxyServerController {

    private static final Socks5Config CONFIG = new Socks5Config();
    private static final Map<String, String> AUTHS = new ConcurrentHashMap<>();
    EventLoopGroup bossGroup = null;
    EventLoopGroup workerGroup = null;
    ChannelFuture f = null;

    public NettySocks5ProxyServerController(){
        try {
            updateState();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start SOCKS5 proxy server", e);
        }
    }
    private void updateState() throws Exception {
        synchronized (CONFIG) {
            if (f != null) {
                f.channel().close();
            }
            if (bossGroup != null) {
                bossGroup.shutdownGracefully().sync();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully().sync();
            }
            workerGroup = null;
            bossGroup = null;
            f = null;
        }
        if (CONFIG.isEnable()){
            int port = CONFIG.getPort();
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new SocksPortUnificationServerHandler());
                            p.addLast(Socks5ServerEncoder.DEFAULT);
                            p.addLast(new Socks5CommandRequestDecoder());
                            p.addLast(new Socks5ProxyServerHandler());
                        }
                    });

            f = b.bind(port).sync();
            System.out.println("SOCKS5 proxy (with auth) started on port " + port);
//            f.channel().closeFuture().sync();
        }else{
            System.out.println("SOCKS5 proxy server is disabled.");
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (f != null) {
                f.channel().close();
            }
            if (bossGroup != null) {
                bossGroup.shutdownGracefully().sync();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully().sync();
            }
        } catch (Exception ignored) {}
    }


    public void setState(int port, boolean enable) throws Exception {
        if (CONFIG.getPort() != port || CONFIG.isEnable() != enable) {
            CONFIG.setPort(port);
            CONFIG.setEnable(enable);
            updateState();
        }
    }

    public void updateAuth(Map<String, String> auths) {
        AUTHS.clear();
        if (auths != null && !auths.isEmpty()) {
            AUTHS.putAll(auths);
        }
    }

    // 业务处理Handler
    static class Socks5ProxyServerHandler extends SimpleChannelInboundHandler<Socks5Message> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Socks5Message msg) {
            if (msg instanceof Socks5InitialRequest) {
                // 不需要认证
                ctx.writeAndFlush(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
            } else if (msg instanceof Socks5CommandRequest) {
                Socks5CommandRequest request = (Socks5CommandRequest) msg;
                if (request.type() == Socks5CommandType.CONNECT) {
                    // 进行转发
                    Bootstrap b = new Bootstrap();
                    b.group(ctx.channel().eventLoop())
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) {
                                    ch.pipeline().addLast(new NettySocks5ProxyServer.RelayHandler(ctx.channel()));
                                }
                            });

                    b.connect(request.dstAddr(), request.dstPort()).addListener((ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            ctx.pipeline().addLast(new NettySocks5ProxyServer.RelayHandler(future.channel()));
                            ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                                    Socks5CommandStatus.SUCCESS, request.dstAddrType(),
                                    request.dstAddr(), request.dstPort()));
                        } else {
                            ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                                    Socks5CommandStatus.FAILURE, request.dstAddrType()));
                            ctx.close();
                        }
                    });
                } else {
                    ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                            Socks5CommandStatus.COMMAND_UNSUPPORTED, request.dstAddrType()));
                    ctx.close();
                }
            } else {
                ctx.close();
            }
        }
    }

    // 主要的认证和业务逻辑
    static class Socks5AuthHandler extends SimpleChannelInboundHandler<Socks5Message> {
        // 标记是否认证通过
        private boolean authenticated = false;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Socks5Message msg) {
            if (msg instanceof Socks5InitialRequest) {
                log.info("Received SOCKS5 initial request from {}", ctx.channel().remoteAddress());
                // 只支持用户名密码认证
                ctx.writeAndFlush(new DefaultSocks5InitialResponse(Socks5AuthMethod.PASSWORD));
                // 支持无认证
            }else if (msg instanceof Socks5PasswordAuthRequest) {
                log.info("Received SOCKS5 password auth request from {}", ctx.channel().remoteAddress());
                // 这里可以添加用户名密码认证逻辑
                Socks5PasswordAuthRequest req = (Socks5PasswordAuthRequest) msg;
                String pwd = AUTHS.get(req.username());
                if (pwd != null && pwd.equals(req.password())) {
                    authenticated = true;
                    ctx.writeAndFlush(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS));
                } else {
                    ctx.writeAndFlush(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE));
                    ctx.close();
                }
            }
            else if (msg instanceof Socks5CommandRequest) {
                log.info("Received SOCKS5 command request from {}", ctx.channel().remoteAddress());
                if (!authenticated) {
                    ctx.close();
                    return;
                }
                Socks5CommandRequest request = (Socks5CommandRequest) msg;
                if (request.type() == Socks5CommandType.CONNECT) {
                    // 进行转发
                    Bootstrap b = new Bootstrap();
                    b.group(ctx.channel().eventLoop())
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) {
                                    ch.pipeline().addLast(new RelayHandler(ctx.channel()));
                                }
                            });
                    log.info("Connecting to {}:{}", request.dstAddr(), request.dstPort());
                    b.connect(request.dstAddr(), request.dstPort()).addListener((ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            ctx.pipeline().addLast(new RelayHandler(future.channel()));
                            ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                                    Socks5CommandStatus.SUCCESS, request.dstAddrType(),
                                    request.dstAddr(), request.dstPort()));
                        } else {
                            ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                                    Socks5CommandStatus.FAILURE, request.dstAddrType()));
                            ctx.close();
                            log.error("Failed to connect to {}:{}", request.dstAddr(), request.dstPort(), future.cause());
                        }
                    });
                } else {
                    ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                            Socks5CommandStatus.COMMAND_UNSUPPORTED, request.dstAddrType()));
                    ctx.close();
                }
            } else {
                ctx.close();
            }
        }
    }

    // 数据转发Handler
    static class RelayHandler extends ChannelInboundHandlerAdapter {
        private final Channel relayChannel;
        public RelayHandler(Channel relayChannel) { this.relayChannel = relayChannel; }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            log.info("Relay connection from {}:{}", ctx.channel().remoteAddress(), msg);
            relayChannel.writeAndFlush(msg);
        }
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            log.info("Relay connection closed from {}", ctx.channel().remoteAddress());
            if (relayChannel.isActive()) {
                relayChannel.close();
            }
        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    }
}
