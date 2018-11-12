package com.youthlin.example.chat.server;

import com.youthlin.example.chat.protocol.Packet;
import com.youthlin.example.chat.protocol.PacketCodec;
import com.youthlin.example.chat.protocol.request.LoginRequestPacket;
import com.youthlin.example.chat.protocol.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 16:31
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("...客户端登录");
        ByteBuf buf = (ByteBuf) msg;
        Packet packet = PacketCodec.INSTANCE.decode(buf);
        if (packet instanceof LoginRequestPacket) {
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
            LoginResponsePacket responsePacket = new LoginResponsePacket();
            if (valid(loginRequestPacket)) {
                responsePacket.setSuccess(true);
            } else {
                responsePacket.setSuccess(false);
                responsePacket.setFailReason("用户名或密码错误");
            }
            ByteBuf response = PacketCodec.INSTANCE.encode(ctx.alloc(), responsePacket);
            ctx.channel().writeAndFlush(response);
        }

    }

    private boolean valid(LoginRequestPacket packet) {
        return Objects.equals(packet.getUsername(), packet.getPassword());
    }

}
