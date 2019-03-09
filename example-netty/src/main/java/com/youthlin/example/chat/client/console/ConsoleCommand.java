package com.youthlin.example.chat.client.console;

import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:14
 */
public interface ConsoleCommand {
    void exec(Scanner in, Channel channel);
}
