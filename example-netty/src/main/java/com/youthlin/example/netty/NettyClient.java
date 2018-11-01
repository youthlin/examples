package com.youthlin.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;
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
                log("connect success");
            } else if (retry <= 0) {
                log("give up");
            } else {
                int order = MAX_RETRY - retry + 1;
                int delay = 1 << order;
                err("connect fail");
                bootstrap.config()
                        .group()
                        .schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }

    private static class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override//连接建立成功之后调用
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log("客户端写出数据");
            ByteBuf buf = getByteBuf(ctx);
            ctx.channel().writeAndFlush(buf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            log("client read:" + buf.toString(Charset.forName("UTF-8")));
        }

        private ByteBuf getByteBuf(ChannelHandlerContext context) {
            ByteBuf buffer = context.alloc().buffer();
            buffer.writeBytes("Hello, 世界".getBytes(Charset.forName("UTF-8")));
            return buffer;
        }
    }

    private static void log(String msg) {
        System.out.println(String.format("[%1$tT] %2$s", new Date(), msg));
    }

    private static void err(String msg) {
        System.err.println(String.format("[%1$tT] %2$s", new Date(), msg));
    }

}
