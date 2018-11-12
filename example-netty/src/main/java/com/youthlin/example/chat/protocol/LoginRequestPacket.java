package com.youthlin.example.chat.protocol;

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
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}
