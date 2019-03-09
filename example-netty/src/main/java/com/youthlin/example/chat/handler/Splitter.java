package com.youthlin.example.chat.handler;

import com.youthlin.example.chat.protocol.PacketCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 15:23
 */
public class Splitter extends LengthFieldBasedFrameDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(Splitter.class);

    public Splitter() {
        super(Integer.MAX_VALUE, PacketCodec.LENGTH_FIELD_OFFSET, PacketCodec.LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int magic = in.getInt(in.readerIndex());
        if (magic != PacketCodec.MAGIC_NUMBER) {
            ctx.channel().close();
            LOGGER.warn("MAGIC NUMBER NOT MATCH. expected:{} got:{}", Integer.toHexString(PacketCodec.MAGIC_NUMBER), Integer.toHexString(magic));
            return null;
        }
        return super.decode(ctx, in);
    }
}
