package com.youthlin.example.chat.client.console;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:48
 */
public class ConsoleCommandManager implements ConsoleCommand {
    private static final Map<String, ConsoleCommand> MAP = new HashMap<>();

    static {
        MAP.put("login", new LoginConsoleCommand());
        MAP.put("logout", new LogoutConsoleCommand());
        MAP.put("sendToUser", new SendToUserConsoleCommand());
        MAP.put("createGroup", new CreateGroupConsoleCommand());
    }

    @Override
    public void exec(Scanner in, Channel channel) {
        System.out.println("input command:> " + MAP.keySet() + "");
        String command = in.nextLine();
        ConsoleCommand consoleCommand = MAP.get(command);
        if (consoleCommand != null) {
            consoleCommand.exec(in, channel);
        } else {
            System.err.println("不能识别的命令,请重新输入.(" + MAP.keySet() + ")");
        }
    }
}
