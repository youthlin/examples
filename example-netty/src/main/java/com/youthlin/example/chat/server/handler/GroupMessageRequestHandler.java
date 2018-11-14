package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.GroupMessageRequestPacket;
import com.youthlin.example.chat.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:34
 */
public class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupMessageRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMessageRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMessageRequestPacket msg) throws Exception {
        Channel channel = ctx.channel();
        long groupId = msg.getGroupId();
        ChannelGroup group = SessionUtil.getChannelGroup(groupId);
        if (group == null) {
            LOGGER.error("群聊{}不存在", groupId);
            return;
        }
        User fromUser = SessionUtil.getUser(channel);
        if (!group.contains(channel)) {
            LOGGER.warn("用户{}不在群聊{}中", fromUser.getName(), groupId);
            return;
        }
        msg.setFromUser(fromUser);
        group.writeAndFlush(msg);
    }
}
