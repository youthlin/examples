package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:31
 */
@Data
public class GroupMessageRequestPacket extends Packet {
    private static final long serialVersionUID = 7048631566224185671L;
    private long groupId;
    private String text;
    private User fromUser;

    @Override
    public byte command() {
        return Command.GROUP_MESSAGE_REQUEST;
    }
}
