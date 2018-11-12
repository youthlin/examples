package com.youthlin.example.chat.client;

import com.youthlin.example.chat.protocol.Packet;
import com.youthlin.example.chat.protocol.PacketCodec;
import com.youthlin.example.chat.protocol.request.LoginRequestPacket;
import com.youthlin.example.chat.protocol.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 16:27
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("连接建立 开始登录...");
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUsername("lin");
        loginRequestPacket.setPassword("lin");

        ByteBuf buf = PacketCodec.INSTANCE.encode(loginRequestPacket);

        ctx.channel().writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        Packet decode = PacketCodec.INSTANCE.decode(buf);
        if (decode instanceof LoginResponsePacket) {
            LoginResponsePacket packet = (LoginResponsePacket) decode;
            if (packet.isSuccess()) {
                LOGGER.info("客户端登录成功");
            } else {
                LOGGER.warn("客户端登录失败:{}", packet.getFailReason());
            }
        }
    }
}
