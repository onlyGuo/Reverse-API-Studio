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

public class NettySocks5ProxyServer {
    public static void main(String[] args) throws Exception {
        int port = 1080;
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            // 兼容socks4和socks5
                            p.addLast(new SocksPortUnificationServerHandler());
                            // 只支持SOCKS5
                            p.addLast(Socks5ServerEncoder.DEFAULT);
                            p.addLast(new Socks5CommandRequestDecoder());
                            p.addLast(new Socks5ProxyServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            System.out.println("SOCKS5 proxy started on port " + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
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
                                    ch.pipeline().addLast(new RelayHandler(ctx.channel()));
                                }
                            });

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
            relayChannel.writeAndFlush(msg);
        }
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
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
