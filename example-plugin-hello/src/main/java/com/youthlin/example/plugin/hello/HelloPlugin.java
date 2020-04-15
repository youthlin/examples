package com.youthlin.example.plugin.hello;

import com.youthlin.example.plugin.IPlugin;
import lombok.extern.slf4j.Slf4j;

/**
 * @author youthlin.chen
 * @date 2020-04-15 19:10
 */
@Slf4j
public class HelloPlugin implements IPlugin<Object> {
    @Override
    public void onActive(Object context) {
        log.debug("onActive!! {} {}", context, this.getClass().getClassLoader());
    }

    @Override
    public void onDisabled(Object context) {
        log.debug("onDisabled!! {}", context);
    }

}
