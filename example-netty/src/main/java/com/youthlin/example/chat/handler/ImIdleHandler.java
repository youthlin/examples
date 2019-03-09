package com.youthlin.example.chat.handler;

import com.youthlin.example.chat.protocol.request.HeartBeatRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 11:43
 */
public class ImIdleHandler extends IdleStateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImIdleHandler.class);
    private static final int READER_IDLE_TIME = 15;
    private static final int HEART_BEAT_INTERVAL = 5;

    public ImIdleHandler() {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("ImIdleHandler Active");
        scheduleSendHeartBeat(ctx);
        super.channelActive(ctx);
    }

    private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                HeartBeatRequestPacket packet = new HeartBeatRequestPacket();
                LOGGER.info("发送心跳");
                ctx.channel().writeAndFlush(packet);
                scheduleSendHeartBeat(ctx);
            }
        }, HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn("连接关闭");
        super.channelInactive(ctx);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        LOGGER.debug("{}秒未读到数据 关闭连接", READER_IDLE_TIME);
        ctx.channel().close();
    }
}
