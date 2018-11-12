package com.youthlin.example.chat.protocol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 15:32
 */
public class JsonSerializer implements Serializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final byte[] empty = new byte[0];

    static {
        MAPPER.disable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public JsonSerializer() {
    }

    @Override
    public byte getAlgorithm() {
        return JSON_SERIALIZER;
    }

    @Override
    public byte[] serialize(Object object) {
        try {
            return MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("serialize_error", e);
        }
        return empty;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            return MAPPER.readValue(data, clazz);
        } catch (IOException e) {
            LOGGER.error("deserialize_error", e);
        }
        return null;
    }
}
