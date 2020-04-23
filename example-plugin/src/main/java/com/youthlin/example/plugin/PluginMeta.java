package com.youthlin.example.plugin;

import lombok.Data;

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
}
