package com.youthlin.example.chat.client.console;

import com.youthlin.example.chat.protocol.request.QuitGroupRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:12
 */
public class QuitGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner in, Channel channel) {
        System.out.println("【退出群聊】输入群聊 ID:> ");
        String id = in.nextLine();
        long groupId = 0;
        try {
            groupId = Long.parseLong(id);
        } catch (Exception e) {
            System.err.println("群聊 ID 应该是正整数");
        }
        QuitGroupRequestPacket packet = new QuitGroupRequestPacket();
        packet.setGroupId(groupId);
        channel.writeAndFlush(packet);
    }
}
