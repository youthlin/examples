package com.youthlin.example.chat.client.console;

import com.youthlin.example.chat.protocol.request.GroupMessageRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:30
 */
public class SendToGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner in, Channel channel) {
        System.out.println("【发送群聊消息】输入群 ID 空格 消息:> ");
        long groupId = in.nextLong();
        String text = in.nextLine();
        GroupMessageRequestPacket packet = new GroupMessageRequestPacket();
        packet.setGroupId(groupId);
        packet.setText(text);
        channel.writeAndFlush(packet);
    }
}
