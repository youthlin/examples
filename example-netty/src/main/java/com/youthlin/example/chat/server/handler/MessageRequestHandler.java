package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.MessageRequestPacket;
import com.youthlin.example.chat.util.LoginUtil;
import com.youthlin.example.chat.util.SessionUtil;
import io.netty.channel.Channel;
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
        long toUser = msg.getToUser();
        User fromUser = SessionUtil.getUser(ctx.channel());
        long fromUserId = fromUser.getId();
        Channel to = SessionUtil.getChannel(toUser);
        if (to != null && LoginUtil.hasLogin(to)) {
            msg.setFromUser(fromUserId);
            to.writeAndFlush(msg);
        } else {
            msg.setText("[" + msg.getToUser() + "不在线,发送失败]");
            ctx.channel().writeAndFlush(msg);
        }
    }

}
