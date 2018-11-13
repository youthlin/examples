package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.util.LoginUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 15:49
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (LoginUtil.hasLogin(channel)) {
            LOGGER.info("已登录");
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        } else {
            channel.close();
            LOGGER.warn("未登录 关闭连接");
        }
    }
}
