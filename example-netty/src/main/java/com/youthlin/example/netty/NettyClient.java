package com.youthlin.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 创建: youthlin.chen
 * 时间: 2018-10-30 19:44
 */
public class NettyClient {
    private static final int MAX_RETRY = 3;

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)                          //1. 线程模型
                .channel(NioSocketChannel.class)        //2. IO 模型
                .handler(                               //3. 处理逻辑
                        new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel channel) throws Exception {
                                channel.pipeline().addLast(new StringEncoder());
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
                System.out.println(String.format("[%1$tT] connect success", new Date()));
            } else if (retry <= 0) {
                System.out.println(String.format("[%1$tT] give up", new Date()));
            } else {
                int order = MAX_RETRY - retry + 1;
                int delay = 1 << order;
                System.err.println(String.format("[%1$tT] %2$d: connect fail", new Date(), order));
                bootstrap.config()
                        .group()
                        .schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }
}
