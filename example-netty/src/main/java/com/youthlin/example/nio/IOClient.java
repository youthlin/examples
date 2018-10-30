package com.youthlin.example.nio;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * 创建: youthlin.chen
 * 时间: 2018-10-30 19:31
 */
public class IOClient {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 1884);
                while (true) {
                    try {
                        socket.getOutputStream().write((new Date() + ": hello world").getBytes());
                        Thread.sleep(2000);
                    } catch (Exception ignore) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
