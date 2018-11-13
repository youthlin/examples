package com.youthlin.example.chat.protocol;

import com.youthlin.example.chat.protocol.request.LoginRequestPacket;
import com.youthlin.example.chat.protocol.request.MessageRequestPacket;
import com.youthlin.example.chat.protocol.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

/**
 * +-------+---------+-----------+---------+--------+------+
 * | MAGIC | version | serialize | command | length | data |
 * +-------+---------+-----------+---------+--------+------+
 * |   4   |    1    |     1     |     1   |    4   |   n  |
 * +-------+---------+-----------+---------+--------+------+
 * 创建: youthlin.chen
 * 时间: 2018-11-12 15:41
 */
public class PacketCodec {
    private static final int MAGIC_NUMBER = 0xcee2018a;
    private static final byte VERSION = 1;
    private static final Map<Byte, Class<? extends Packet>> PACKET_TYPE_MAP;
    private static final Map<Byte, Serializer> SERIALIZER_MAP;
    public static final PacketCodec INSTANCE = new PacketCodec();

    static {
        PACKET_TYPE_MAP = new HashMap<>();
        SERIALIZER_MAP = new HashMap<>();
        PACKET_TYPE_MAP.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);
        PACKET_TYPE_MAP.put(Command.LOGIN_RESPONSE, LoginResponsePacket.class);
        PACKET_TYPE_MAP.put(Command.MESSAGE_REQUEST, MessageRequestPacket.class);
        SERIALIZER_MAP.put(Serializer.JSON_SERIALIZER, Serializer.DEFAULT);
    }

    public void encode(ByteBuf buf, Packet packet) {
        byte[] bytes = Serializer.DEFAULT.serialize(packet);
        buf.writeInt(MAGIC_NUMBER);                         // 4
        buf.writeByte(VERSION);                             // 1
        buf.writeByte(Serializer.DEFAULT.getAlgorithm());   // 1
        buf.writeByte(packet.command());                    // 1
        buf.writeInt(bytes.length);                         // 4
        buf.writeBytes(bytes);                              // n
    }

    public ByteBuf encode(ByteBufAllocator allocator, Packet packet) {
        ByteBuf buf = allocator.ioBuffer();
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        buf.writeInt(MAGIC_NUMBER);                         // 4
        buf.writeByte(VERSION);                             // 1
        buf.writeByte(Serializer.DEFAULT.getAlgorithm());   // 1
        buf.writeByte(packet.command());                    // 1
        buf.writeInt(bytes.length);                         // 4
        buf.writeBytes(bytes);                              // n
        return buf;
    }

    public Packet decode(ByteBuf byteBuf) {
        byteBuf.skipBytes(4);
        byteBuf.skipBytes(1);
        byte serializeAlgorithm = byteBuf.readByte();
        byte command = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Class<? extends Packet> dataType = getDataType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);
        if (serializer != null && dataType != null) {
            return serializer.deserialize(data, dataType);
        }
        return null;
    }

    private Serializer getSerializer(byte algorithm) {
        return SERIALIZER_MAP.get(algorithm);
    }

    private Class<? extends Packet> getDataType(byte command) {
        return PACKET_TYPE_MAP.get(command);
    }
}
