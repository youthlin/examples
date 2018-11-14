package com.youthlin.example.chat.client.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.GroupMessageRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:36
 */
public class GroupMessageResponseHandler extends SimpleChannelInboundHandler<GroupMessageRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMessageRequestPacket msg) throws Exception {
        User fromUser = msg.getFromUser();
        System.out.println("[" + fromUser.getName() + "]向群聊[" + msg.getGroupId() + "]发送消息: " + msg.getText());
    }
}
