package com.youthlin.example.chat.codec;

import com.youthlin.example.chat.protocol.Packet;
import com.youthlin.example.chat.protocol.PacketCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-14 10:55
 */
@ChannelHandler.Sharable
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {
    public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) {
        ByteBuf buf = ctx.channel().alloc().ioBuffer();
        PacketCodec.INSTANCE.encode(buf, msg);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        out.add(PacketCodec.INSTANCE.decode(msg));
    }
}
