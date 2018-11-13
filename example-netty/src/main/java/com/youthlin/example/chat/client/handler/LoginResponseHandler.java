package com.youthlin.example.chat.client.handler;

import com.youthlin.example.chat.protocol.response.LoginResponsePacket;
import com.youthlin.example.chat.util.LoginUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 14:53
 */
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginResponseHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket msg) throws Exception {
        if (msg.isSuccess()) {
            LOGGER.info("客户端登录成功 {}", msg);
            //登录成功后标记 控制台线程就可以输入消息了 标记只在客户端可见 服务端标记要在服务端打
            LoginUtil.markAsLogin(ctx.channel());
        } else {
            LOGGER.warn("客户端登录失败:{}", msg.getFailReason());
        }
    }
}
