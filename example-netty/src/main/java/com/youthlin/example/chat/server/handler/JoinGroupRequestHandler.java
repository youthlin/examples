package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.JoinGroupRequestPacket;
import com.youthlin.example.chat.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 09:29
 */
@ChannelHandler.Sharable
public class JoinGroupRequestHandler extends SimpleChannelInboundHandler<JoinGroupRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinGroupRequestHandler.class);
    public static final JoinGroupRequestHandler INSTANCE = new JoinGroupRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinGroupRequestPacket msg) throws Exception {
        long groupId = msg.getGroupId();
        ChannelGroup group = SessionUtil.getChannelGroup(groupId);
        if (group == null) {
            LOGGER.error("群聊{}不存在", groupId);
        } else {
            Channel channel = ctx.channel();
            group.add(channel);//未处理已加入的情况
            User user = SessionUtil.getUser(channel);
            msg.setJoinUserId(user.getId());
            msg.setJoinUserName(user.getName());
            List<User> userList = new ArrayList<>();
            group.forEach(ch -> userList.add(SessionUtil.getUser(ch)));
            msg.setUserList(userList);
            group.writeAndFlush(msg);
        }
    }
}
