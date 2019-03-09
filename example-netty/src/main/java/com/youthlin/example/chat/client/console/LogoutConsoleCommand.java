package com.youthlin.example.chat.client.console;

import com.youthlin.example.chat.attr.Attributes;
import com.youthlin.example.chat.protocol.request.LogoutRequestPacket;
import com.youthlin.example.chat.util.LoginUtil;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:17
 */
public class LogoutConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner in, Channel channel) {
        LogoutRequestPacket packet = new LogoutRequestPacket();
        channel.writeAndFlush(packet);
        LoginUtil.markAsLogout(channel);
    }
}
