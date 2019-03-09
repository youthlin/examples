package com.youthlin.example.chat.client.console;

import com.youthlin.example.chat.protocol.request.CreateGroupRequestPacket;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:41
 */
public class CreateGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner in, Channel channel) {
        System.out.println("【拉人群聊】输入 userId 列表, userId 之间以英文逗号分隔:>");
        CreateGroupRequestPacket packet = new CreateGroupRequestPacket();
        String line = in.nextLine();
        Set<Long> userIdList = Arrays.stream(line.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        packet.setUserIdList(userIdList);
        channel.writeAndFlush(packet);
    }
}
