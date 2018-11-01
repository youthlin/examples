package com.youthlin.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;

/**
 * 创建: youthlin.chen
 * 时间: 2018-10-30 19:40
 */
public class NettyServer {

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap.group(boos, worker)             //1.指定线程模型 接受新连接、实际处理请求 分为两组
                .channel(NioServerSocketChannel.class)  //2. 指定IO模型        // NioServerSocketChannel 对应 ServerSocket
                .childHandler(                          //3. 定义每个连接的数据读写
                        new ChannelInitializer<NioSocketChannel>() {         // NioSocketChannel 对应 Socket
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new StringDecoder());
                                ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                            String msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                .handler(                               // handler 对应 服务端启动过程中(bind后)的逻辑 childHandler 用于处理新连接
                        new ChannelInitializer<NioServerSocketChannel>() {
                            @Override
                            protected void initChannel(NioServerSocketChannel ch) throws Exception {
                                System.out.println("Starting...");
                            }
                        })
                .attr(AttributeKey.newInstance("myKey"), "myValue")
                .childAttr(AttributeKey.newInstance("clientKey"), "clientValue")
                .childOption(ChannelOption.SO_KEEPALIVE, true)//开启 TCP 心跳机制
                .childOption(ChannelOption.TCP_NODELAY, true)//高实时性有数据就马上发送
                .option(ChannelOption.SO_BACKLOG, 1024)//临时存放已完成三次握手的请求的队列的最大长度 连接建立频繁处理新连接较慢则增大
        ;
        bind(serverBootstrap, 1000);

    }

    private static void bind(ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("bind on port: " + port);
            } else {
                bind(serverBootstrap, port + 1);
            }
        });
    }

}
