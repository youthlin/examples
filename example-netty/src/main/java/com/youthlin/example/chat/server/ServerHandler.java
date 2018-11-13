package com.youthlin.example.chat.server;

import com.youthlin.example.chat.LoginUtil;
import com.youthlin.example.chat.protocol.Packet;
import com.youthlin.example.chat.protocol.PacketCodec;
import com.youthlin.example.chat.protocol.request.LoginRequestPacket;
import com.youthlin.example.chat.protocol.request.MessageRequestPacket;
import com.youthlin.example.chat.protocol.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
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
        Channel channel = ctx.channel();
        ByteBuf buf = (ByteBuf) msg;
        Packet packet = PacketCodec.INSTANCE.decode(buf);
        if (packet instanceof LoginRequestPacket) {
            LOGGER.info("...客户端登录");
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
            LoginResponsePacket responsePacket = new LoginResponsePacket();
            if (valid(loginRequestPacket)) {
                responsePacket.setSuccess(true);
                LoginUtil.markAsLogin(channel);//登录成功打上标记 标记只在服务端可见 客户端需要收到登录成功的数据包后 自行打标
                LOGGER.info("登录成功");
            } else {
                responsePacket.setSuccess(false);
                responsePacket.setFailReason("用户名或密码错误");
                LOGGER.info("登录失败");
            }
            ByteBuf response = PacketCodec.INSTANCE.encode(ctx.alloc(), responsePacket);
            channel.writeAndFlush(response);
        } else {
            if (!LoginUtil.hasLogin(channel)) {//客户端发送的数据不是登录消息 且未登录 则关闭
                LOGGER.warn("未登录 关闭连接");
                channel.close();
                return;
            }
        }
        if (packet instanceof MessageRequestPacket) {
            MessageRequestPacket messageRequestPacket = (MessageRequestPacket) packet;
            LOGGER.info("收到消息 {}", messageRequestPacket);
            messageRequestPacket.setText("[echo]" + messageRequestPacket.getText());
            ByteBuf out = PacketCodec.INSTANCE.encode(messageRequestPacket);
            channel.writeAndFlush(out);
        }

    }

    private boolean valid(LoginRequestPacket packet) {
        return Objects.equals(packet.getUsername(), packet.getPassword());
    }

}
