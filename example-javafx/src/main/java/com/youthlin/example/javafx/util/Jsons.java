package com.youthlin.example.javafx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author youthlin.chen
 * @date 2019-10-08 15:39
 */
@Slf4j
public class Jsons {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectWriter OBJECT_WRITER_PRETTY = new ObjectMapper().writerWithDefaultPrettyPrinter();

    public static String toJson(Object o) {
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            log.warn("to json error. {}", o, e);
            return "";
        }
    }

    public static String toJsonPretty(Object o) {
        try {
            return OBJECT_WRITER_PRETTY.writeValueAsString(o);
        } catch (Exception e) {
            log.warn("to json error. {}", o, e);
            return "";
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.warn("to json error. json={}, {}", json, clazz, e);
            return null;
        }
    }

}
