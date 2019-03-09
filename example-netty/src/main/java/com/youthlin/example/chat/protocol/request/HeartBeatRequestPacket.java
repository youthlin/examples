package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 11:50
 */
public class HeartBeatRequestPacket extends Packet {
    private static final long serialVersionUID = 166383924595842830L;

    @Override
    public byte command() {
        return Command.HEART_BEAT_REQUEST;
    }
}
