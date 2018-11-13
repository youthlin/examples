package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

import java.util.Set;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 19:19
 */
@Data
public class CreateGroupRequestPacket extends Packet {
    private Long roomId;
    private Long creatorUserId;
    private Set<Long> userIdList;
    private Set<String> userNameList;

    @Override
    public byte command() {
        return Command.CREATE_GROUP_REQUEST;
    }
}
