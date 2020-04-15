package com.youthlin.example.plugin.test;

import com.youthlin.example.plugin.PluginInstance;
import com.youthlin.example.plugin.PluginManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author youthlin.chen
 * @date 2020-04-15 19:18
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        PluginManager<AppContext> manager = new PluginManager<>();
        log.debug("start...");
        log.debug("{}", manager.getInstances());

        File file = new File("D:\\Projects\\examples\\example-plugin-hello\\target\\classes");
//        File file = new File("D:\\Projects\\examples\\example-plugin-hello\\target\\example-plugin-hello-1.0-SNAPSHOT.jar");
        PluginInstance<AppContext> instance = manager.install(file);
        log.debug("installed...");
        log.debug("{}", manager.getInstances());

        AppContext context = new AppContext();
        manager.active(instance, context);
        log.debug("active...");
        log.debug("{}", manager.getInstances());

        manager.disable(instance, context);
        log.debug("disable...");
        log.debug("{}", manager.getInstances());

        manager.uninstall(instance);
        log.debug("uninstall...");
        log.debug("{}", manager.getInstances());
    }
}
