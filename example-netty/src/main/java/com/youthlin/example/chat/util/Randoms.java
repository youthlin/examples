package com.youthlin.example.chat.util;

import java.util.Random;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 16:44
 */
public class Randoms {
    private static final Random random = new Random();
    private static final char[] symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private Randoms() {
    }

    public static String randomString(int len) {
        return randomString(len, symbols);
    }

    public static String randomString(int len, char[] symbols) {
        char[] arr = new char[len];

        for (int i = 0; i < len; ++i) {
            arr[i] = symbols[random.nextInt(symbols.length)];
        }

        return new String(arr);
    }

    public static int randomInt() {
        return random.nextInt();
    }

    public static int randomInt(int max) {
        return random.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }
}
