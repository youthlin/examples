package com.youthlin.example.chat.protocol.response;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 16:38
 */
@Data
public class LoginResponsePacket extends Packet {
    private boolean success;
    private String failReason;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}
