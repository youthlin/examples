package com.youthlin.example.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

/**
 * @author youthlin.chen
 * @date 2019-06-03 19:19
 */
public class CompanySerializer implements Serializer<Company>, Deserializer<Company> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Company deserialize(String topic, byte[] data) {
        try {
            return OBJECT_MAPPER.readValue(data, Company.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public byte[] serialize(String topic, Company data) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public void close() {
    }

}
