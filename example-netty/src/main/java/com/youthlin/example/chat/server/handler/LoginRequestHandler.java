package com.youthlin.example.chat.server.handler;

import com.youthlin.example.chat.model.User;
import com.youthlin.example.chat.protocol.request.LoginRequestPacket;
import com.youthlin.example.chat.protocol.response.LoginResponsePacket;
import com.youthlin.example.chat.util.LoginUtil;
import com.youthlin.example.chat.util.Randoms;
import com.youthlin.example.chat.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 14:45
 */
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket msg) {
        LOGGER.info("...客户端登录 {}", msg);
        Channel channel = ctx.channel();
        LoginResponsePacket responsePacket = new LoginResponsePacket();
        if (valid(msg)) {
            responsePacket.setSuccess(true);
            LoginUtil.markAsLogin(channel);//登录成功打上标记 标记只在服务端可见 客户端需要收到登录成功的数据包后 自行打标
            User user = new User();
            user.setId(Randoms.randomInt(10000, 99999));
            user.setName(msg.getUsername());
            responsePacket.setUser(user);
            LOGGER.info("{}登录成功,id={}", msg.getUsername(), user.getId());
            SessionUtil.bindUser(user, channel);
        } else {
            responsePacket.setSuccess(false);
            responsePacket.setFailReason("用户名或密码错误");
            LOGGER.info("登录失败");
        }
        channel.writeAndFlush(responsePacket);
    }

    private boolean valid(LoginRequestPacket packet) {
        return Objects.equals(packet.getUsername(), packet.getPassword());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unBindUser(ctx.channel());
        super.channelInactive(ctx);
    }
}
