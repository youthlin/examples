package com.youthlin.example.chat.client.console;

import com.youthlin.example.chat.protocol.request.MessageRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:16
 */
public class SendToUserConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner in, Channel channel) {
        System.out.println("【发送消息】输入 UserId 后输入空格,再输入要发送的消息最后回车:>");
        long toUserId = in.nextLong();
        String text = in.nextLine();
        MessageRequestPacket packet = new MessageRequestPacket();
        packet.setToUser(toUserId);
        packet.setText(text.trim());
        channel.writeAndFlush(packet);
    }
}
