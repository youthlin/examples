package com.youthlin.example.plugin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youthlin.chen
 * @date 2020-04-15 17:43
 */
@Data
public class PluginInstance<T> {
    private final PluginMeta meta;
    private final ClassLoader classLoader;
    private final IPlugin<T> plugin;
    @EqualsAndHashCode.Exclude
    private State state = State.INSTALLED;
}
