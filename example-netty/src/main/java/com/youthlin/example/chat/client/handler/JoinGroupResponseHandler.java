package com.youthlin.example.chat.client.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.JoinGroupRequestPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 09:59
 */
@ChannelHandler.Sharable
public class JoinGroupResponseHandler extends SimpleChannelInboundHandler<JoinGroupRequestPacket> {
    public static final JoinGroupResponseHandler INSTANCE = new JoinGroupResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinGroupRequestPacket msg) throws Exception {
        String joinUserName = msg.getJoinUserName();
        List<User> userList = msg.getUserList();
        System.out.println("[" + joinUserName + "]加入群聊[" + msg.getGroupId() + "],群里有"
                + userList.stream().map(User::getName).collect(Collectors.toList())
        );
    }
}
