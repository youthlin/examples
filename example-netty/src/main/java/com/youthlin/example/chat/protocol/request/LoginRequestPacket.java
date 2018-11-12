package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 15:14
 */
@Data
public class LoginRequestPacket extends Packet {
    private String username;
    private String password;

    @Override
    public byte command() {
        return Command.LOGIN_REQUEST;
    }
}
