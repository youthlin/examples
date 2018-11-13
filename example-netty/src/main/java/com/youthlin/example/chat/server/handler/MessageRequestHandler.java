package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.protocol.request.MessageRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 14:49
 */
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket msg) throws Exception {
        LOGGER.info("收到消息 {}", msg);
        msg.setText("[echo]" + msg.getText());
        ctx.channel().writeAndFlush(msg);
    }

}
