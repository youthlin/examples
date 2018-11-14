package com.youthlin.example.chat.client.handler;

import com.youthlin.example.chat.protocol.request.HeartBeatRequestPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 11:47
 */
@ChannelHandler.Sharable
public class HeartBeatResponseHandler extends SimpleChannelInboundHandler<HeartBeatRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatResponseHandler.class);
    public static final HeartBeatResponseHandler INSTANCE = new HeartBeatResponseHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatRequestPacket msg) {
        LOGGER.debug("Client 收到心跳");
    }
}
