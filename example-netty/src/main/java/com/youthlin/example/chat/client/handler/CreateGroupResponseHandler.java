package com.youthlin.example.chat.client.handler;

import com.youthlin.example.chat.protocol.request.CreateGroupRequestPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 20:03
 */
@ChannelHandler.Sharable
public class CreateGroupResponseHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateGroupResponseHandler.class);
    public static final CreateGroupResponseHandler INSTANCE = new CreateGroupResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket msg) throws Exception {
        LOGGER.info("群聊创建成功 {}", msg);
    }
}
