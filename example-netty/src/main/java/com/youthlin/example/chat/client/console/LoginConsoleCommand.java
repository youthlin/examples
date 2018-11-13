package com.youthlin.example.chat.client.console;

import com.youthlin.example.chat.protocol.request.LoginRequestPacket;
import com.youthlin.example.chat.util.LoginUtil;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:15
 */
public class LoginConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner in, Channel channel) {
        if (LoginUtil.hasLogin(channel)) {
            System.err.println("已登录 无需再次登录 请重新输入命令");
            return;
        }
        System.out.println("【登录】输入用户名:>");
        String username = in.nextLine();
        LoginRequestPacket packet = new LoginRequestPacket();
        packet.setUsername(username);
        packet.setPassword(username);
        channel.writeAndFlush(packet);
    }
}
