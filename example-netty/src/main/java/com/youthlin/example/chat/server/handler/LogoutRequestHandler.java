package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.LogoutRequestPacket;
import com.youthlin.example.chat.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 20:18
 */
public class LogoutRequestHandler extends SimpleChannelInboundHandler<LogoutRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogoutRequestPacket msg) throws Exception {
        User user = SessionUtil.getUser(ctx.channel());
        SessionUtil.unBindUser(ctx.channel());
        LOGGER.info("登出 {}", user);
    }
}
