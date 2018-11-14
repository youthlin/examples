package com.youthlin.example.chat.client.handler;

import com.youthlin.example.chat.protocol.Command;
import com.youthlin.example.chat.protocol.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 11:20
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);
    public static final ClientHandler INSTANCE = new ClientHandler();
    private static final Map<Byte, SimpleChannelInboundHandler<? extends Packet>> MAP = new HashMap<>();

    private ClientHandler() {
        MAP.put(Command.LOGIN_RESPONSE, LoginResponseHandler.INSTANCE);
        MAP.put(Command.MESSAGE_REQUEST, MessageResponseHandler.INSTANCE);
        MAP.put(Command.CREATE_GROUP_REQUEST, CreateGroupResponseHandler.INSTANCE);
        MAP.put(Command.QUIT_GROUP_REQUEST, QuitGroupResponseHandler.INSTANCE);
        MAP.put(Command.GROUP_MESSAGE_REQUEST, GroupMessageResponseHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        SimpleChannelInboundHandler<? extends Packet> handler = MAP.get(msg.command());
        if (handler != null) {
            handler.channelRead(ctx, msg);
        } else {
            LOGGER.error("找不到处理器 command={}", msg.command());
        }
    }
}
