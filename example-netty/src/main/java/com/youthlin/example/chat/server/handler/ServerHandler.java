package com.youthlin.example.chat.server.handler;

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
 * 时间: 2018-11-14 11:01
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    public static final ServerHandler INSTANCE = new ServerHandler();
    private static final Map<Byte, SimpleChannelInboundHandler<? extends Packet>> HANDLER_MAP = new HashMap<>();

    private ServerHandler() {
        HANDLER_MAP.put(Command.MESSAGE_REQUEST, MessageRequestHandler.INSTANCE);
        HANDLER_MAP.put(Command.CREATE_GROUP_REQUEST, CreateGroupRequestHandler.INSTANCE);
        HANDLER_MAP.put(Command.JOIN_GROUP_REQUEST, JoinGroupRequestHandler.INSTANCE);
        HANDLER_MAP.put(Command.QUIT_GROUP_REQUEST, QuitGroupRequestHandler.INSTANCE);
        HANDLER_MAP.put(Command.GROUP_MESSAGE_REQUEST, GroupMessageRequestHandler.INSTANCE);
        HANDLER_MAP.put(Command.LOGOUT_REQUEST, LogoutRequestHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        SimpleChannelInboundHandler<? extends Packet> handler = HANDLER_MAP.get(msg.command());
        if (handler != null) {
            handler.channelRead(ctx, msg);
        } else {
            LOGGER.error("找不到处理器 command={}", msg.command());
        }
    }
}
