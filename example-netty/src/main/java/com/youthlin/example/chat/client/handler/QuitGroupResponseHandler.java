package com.youthlin.example.chat.client.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.QuitGroupRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:18
 */
public class QuitGroupResponseHandler extends SimpleChannelInboundHandler<QuitGroupRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitGroupRequestPacket msg) throws Exception {
        User whoQuit = msg.getWhoQuit();
        long groupId = msg.getGroupId();
        List<User> userList = msg.getUserList();
        List<String> userNameList = userList.stream().map(User::getName).collect(Collectors.toList());
        System.out.println("[" + whoQuit.getName() + "]退出群聊[" + groupId + "]群里还有:" + userNameList);
    }
}
