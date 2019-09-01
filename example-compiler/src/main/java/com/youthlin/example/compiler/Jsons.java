package com.youthlin.example.compiler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author : youthlin.chen @ 2019-09-01 14:01
 */
@Slf4j
public class Jsons {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("object toJson error:{}", object, e);
            return "";
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("json to object error:{},{}", clazz, json, e);
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            log.error("json to object error:{},{}", type, json, e);
            return null;
        }
    }

}
