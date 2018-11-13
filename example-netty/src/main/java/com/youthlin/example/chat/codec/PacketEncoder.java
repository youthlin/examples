package com.youthlin.example.chat.codec;

import com.youthlin.example.chat.protocol.Packet;
import com.youthlin.example.chat.protocol.PacketCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 14:41
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        PacketCodec.INSTANCE.encode(out, msg);
    }
}
