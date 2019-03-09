package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 20:17
 */
@Data
public class LogoutRequestPacket extends Packet {
    private static final long serialVersionUID = -7070352806872870755L;

    @Override
    public byte command() {
        return Command.LOGOUT_REQUEST;
    }
}
