package com.youthlin.example.chat.protocol;

import com.youthlin.example.chat.protocol.request.CreateGroupRequestPacket;
import com.youthlin.example.chat.protocol.request.JoinGroupRequestPacket;
import com.youthlin.example.chat.protocol.request.LoginRequestPacket;
import com.youthlin.example.chat.protocol.request.LogoutRequestPacket;
import com.youthlin.example.chat.protocol.request.MessageRequestPacket;
import com.youthlin.example.chat.protocol.request.QuitGroupRequestPacket;
import com.youthlin.example.chat.protocol.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;

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
    public static final int LENGTH_FIELD_OFFSET = 7;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int MAGIC_NUMBER = 0xcee2018a;
    private static final byte VERSION = 1;
    private static final Map<Byte, Class<? extends Packet>> PACKET_TYPE_MAP;
    private static final Map<Byte, Serializer> SERIALIZER_MAP;
    public static final PacketCodec INSTANCE = new PacketCodec();

    static {
        PACKET_TYPE_MAP = new HashMap<>();
        SERIALIZER_MAP = new HashMap<>();
        PACKET_TYPE_MAP.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);
        PACKET_TYPE_MAP.put(Command.LOGIN_RESPONSE, LoginResponsePacket.class);
        PACKET_TYPE_MAP.put(Command.LOGOUT_REQUEST, LogoutRequestPacket.class);
        PACKET_TYPE_MAP.put(Command.MESSAGE_REQUEST, MessageRequestPacket.class);
        PACKET_TYPE_MAP.put(Command.CREATE_GROUP_REQUEST, CreateGroupRequestPacket.class);
        PACKET_TYPE_MAP.put(Command.JOIN_GROUP_REQUEST, JoinGroupRequestPacket.class);
        PACKET_TYPE_MAP.put(Command.QUIT_GROUP_REQUEST, QuitGroupRequestPacket.class);
        SERIALIZER_MAP.put(Serializer.JSON_SERIALIZER, Serializer.DEFAULT);
    }

    public void encode(ByteBuf buf, Packet packet) {
        byte[] bytes = Serializer.DEFAULT.serialize(packet);//off len
        buf.writeInt(MAGIC_NUMBER);                         // 0  4
        buf.writeByte(VERSION);                             // 4  1
        buf.writeByte(Serializer.DEFAULT.getAlgorithm());   // 5  1
        buf.writeByte(packet.command());                    // 6  1
        buf.writeInt(bytes.length);                         // 7  4
        buf.writeBytes(bytes);                              // 11 n
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
