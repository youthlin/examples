package com.youthlin.example.plugin;

/**
 * @author youthlin.chen
 * @date 2020-04-15 18:23
 */
public interface IPlugin<T> {
    void onActive(T context);

    void onDisabled(T context);

}
