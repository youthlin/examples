package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 20:17
 */
public class LogoutRequestPacket extends Packet {
    @Override
    public byte command() {
        return Command.LOGOUT_REQUEST;
    }
}
