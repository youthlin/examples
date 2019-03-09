package com.youthlin.example.chat.protocol.request;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import lombok.Data;

import java.util.List;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:10
 */
@Data
public class QuitGroupRequestPacket extends Packet {
    private static final long serialVersionUID = -8462923137864474577L;
    private long groupId;
    private User whoQuit;
    private List<User> userList;

    @Override

    public byte command() {
        return Command.QUIT_GROUP_REQUEST;
    }
}
