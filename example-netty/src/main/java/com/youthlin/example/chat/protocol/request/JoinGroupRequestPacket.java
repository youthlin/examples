package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

import java.util.List;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 09:22
 */
@Data
public class JoinGroupRequestPacket extends Packet {
    private static final long serialVersionUID = 6904991937992293117L;
    private long joinUserId;
    private String joinUserName;
    private long groupId;
    private List<User> userList;

    @Override
    public byte command() {
        return Command.JOIN_GROUP_REQUEST;
    }
}
