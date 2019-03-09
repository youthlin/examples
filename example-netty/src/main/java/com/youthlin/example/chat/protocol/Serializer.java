package com.youthlin.example.chat.protocol;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 15:17
 */
public interface Serializer {
    byte JSON_SERIALIZER = 1;
    Serializer DEFAULT = new JsonSerializer();

    byte getAlgorithm();

    byte[] serialize(Object object);

    <T> T deserialize(byte[] data, Class<T> clazz);

}
