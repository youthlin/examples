package com.youthlin.example.plugin.api;

import java.util.Arrays;

public class PluginTest {
    public static void main(String[] args) {
        Plugins.addAction("start", param -> {
            System.out.println("on start 10 " + Arrays.toString(param));
        }, 10, 1);
        Plugins.addAction("start", param -> {
            System.out.println("on start 100 " + Arrays.toString(param));
        }, 100, 2);
        Plugins.addAction("start", param -> {
            System.out.println("on start -300 " + Arrays.toString(param));
        }, -300, 3);
        Plugins.addAction("start", param -> {
            System.out.println("on start 400 " + Arrays.toString(param));
        }, 400, 4);
        System.out.println(Arrays.toString(args));
        Plugins.doAction("start", (Object[]) args);

        Plugins.addFilter("test", (String input, Object... param) -> {
            return input + "(length=" + input.length() + ")";
        }, 10, 0);
        Plugins.addFilter("test", (String input, Object... param) -> {
            return input + "100";
        }, 100, 1);
        System.out.println(Plugins.applyFilter("test", "input"));
        Plugins.addFilter("test", (String input, Object... param) -> {
            return input + " ( length=" + input.length() + ")";
        }, 100, 0);
    }

}
