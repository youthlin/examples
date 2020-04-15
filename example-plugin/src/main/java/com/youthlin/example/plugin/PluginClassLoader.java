package com.youthlin.example.plugin;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author youthlin.chen
 * @date 2020-04-15 18:47
 */
@Slf4j
public class PluginClassLoader extends URLClassLoader {
    public PluginClassLoader(URL[] urls) {
        super(urls);
        log.debug("urls={}", (Object) urls);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        log.debug("findClass: {}", name);
        return super.findClass(name);
    }
}
