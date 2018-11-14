package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.CreateGroupRequestPacket;
import com.youthlin.example.chat.util.Randoms;
import com.youthlin.example.chat.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:56
 */
public class CreateGroupRequestHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateGroupRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket msg) throws Exception {
        msg.setRoomId((long) Randoms.randomInt(100000, 999999));
        Channel channel = ctx.channel();
        User fromUser = SessionUtil.getUser(channel);
        msg.setCreatorUserId(fromUser.getId());
        Set<Long> userIdList = msg.getUserIdList();
        Set<String> userNameList = new HashSet<>(userIdList.size() + 1);
        DefaultChannelGroup channelGroup = new DefaultChannelGroup(ctx.executor());
        for (Long userId : userIdList) {
            Channel ch = SessionUtil.getChannel(userId);
            if (ch != null) {
                channelGroup.add(ch);
                User user = SessionUtil.getUser(ch);
                userNameList.add(user.getName());
            } else {
                userIdList.remove(userId);
            }
        }
        userIdList.add(fromUser.getId());
        userNameList.add(fromUser.getName());
        msg.setUserNameList(userNameList);
        channelGroup.add(channel);
        channelGroup.writeAndFlush(msg);
        SessionUtil.bindChannelGroup(msg.getRoomId(), channelGroup);
        LOGGER.info("群聊创建成功 {}", msg);
    }
}
