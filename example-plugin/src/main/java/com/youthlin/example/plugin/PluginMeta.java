package com.youthlin.example.plugin;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * @author youthlin.chen
 * @date 2020-04-15 18:43
 */
@Data
public class PluginMeta {
    private final String namespace;
    private final String entrance;
    private final String name;
    private final String author;
    private final String version;
    private final String describe;
    private final Map<String, String> ext;

    @Data
    public static class View {
        private String namespace;
        private String entrance;
        private String name;
        private String author;
        private String version;
        private String describe;
        private Map<String, String> ext;

        public final PluginMeta toMeta() {
            return new PluginMeta(namespace, entrance, name, author, version, describe,
                    Collections.unmodifiableMap(ext)
            );
        }

    }

}
