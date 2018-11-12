package com.youthlin.example.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 16:10
 */
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static final int MAX_RETRY = 3;

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)                          //1. 线程模型
                .channel(NioSocketChannel.class)        //2. IO 模型
                .handler(                               //3. 处理逻辑
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline().addLast(new ClientHandler());
                            }
                        })
                .attr(AttributeKey.newInstance("clientName"), "MyClient")
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)//连接超时时间
                .option(ChannelOption.SO_KEEPALIVE, true)//开启 TCP 心跳
                .option(ChannelOption.TCP_NODELAY, true)//高实时性
        ;
        //4. 建立连接
        connect(bootstrap, "127.0.0.1", 1884, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                LOGGER.info("connect success: {}:{}", host, port);
            } else if (retry <= 0) {
                LOGGER.warn("give up: max retry limit({})", MAX_RETRY);
            } else {
                int order = MAX_RETRY - retry + 1;
                int delay = 1 << order;
                LOGGER.error("connect fail");
                bootstrap.config()
                        .group()
                        .schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }

}
