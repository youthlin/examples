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
    private static final long serialVersionUID = 1698034886900443942L;
    private String text;
    private long fromUser;
    private long toUser;

    @Override
    public byte command() {
        return Command.MESSAGE_REQUEST;
    }
}
