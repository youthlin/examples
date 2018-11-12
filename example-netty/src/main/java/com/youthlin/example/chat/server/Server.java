package com.youthlin.example.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 16:10
 */
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup woker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boos, woker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)//开启 TCP 心跳机制
                .childOption(ChannelOption.TCP_NODELAY, true)//高实时性有数据就马上发送
                .option(ChannelOption.SO_BACKLOG, 1024)//临时存放已完成三次握手的请求的队列的最大长度 连接建立频繁处理新连接较慢则增大
        ;
        bind(bootstrap, 1884);
    }

    private static void bind(ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                LOGGER.info("bind on port: {}", port);
            } else {
                bind(serverBootstrap, port + 1);
            }
        });
    }

}
