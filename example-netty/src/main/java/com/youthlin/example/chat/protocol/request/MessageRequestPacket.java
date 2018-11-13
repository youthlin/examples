package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 17:45
 */
@Data
public class MessageRequestPacket extends Packet {
    public static final byte TYPE_PLAIN_TEXT = 0;
    private byte msgType;
    private String text;

    public MessageRequestPacket() {
    }

    public MessageRequestPacket(String text) {
        this.text = text;
    }

    @Override
    public byte command() {
        return Command.MESSAGE_REQUEST;
    }
}
