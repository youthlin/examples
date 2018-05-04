package com.youthlin.demo.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 创建: youthlin.chen
 * 时间: 2018-05-05 06:48
 */
public class Test {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static boolean stop = false;
    private static boolean inited = false;
    private static JFrame window = new JFrame("OpenCV Test");
    private static JLabel label = new JLabel();

    public static void main(String[] args) {
        EventQueue.invokeLater(Test::createFrameAndShow);
        cap();
    }

    private static void createFrameAndShow() {
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton capBtn = new JButton("拍照");
        capBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                stop = true;
                super.mouseClicked(e);
            }
        });
        window.setBounds(100, 100, 1280, 720);
        capBtn.setBounds(33, 13, 113, 27);
        window.getContentPane().add(capBtn);
        label.setBounds(0, 0, 1280, 720);
        window.getContentPane().add(label);
        window.setVisible(true);
    }

    private static void cap() {
        VideoCapture camera = new VideoCapture();
        camera.open(0);
        if (!camera.isOpened()) {
            stop = true;
            label.setText("摄像头不可用");
            return;
        }
        Mat pic = new Mat();
        while (!stop) {
            camera.read(pic);
            label.setIcon(new ImageIcon(HighGui.toBufferedImage(pic)));
            if (!inited) {
                EventQueue.invokeLater(() -> {
                    window.setSize(pic.width(), pic.height());
                    label.setSize(pic.width(), pic.height());
                    window.setResizable(false);
                });
                inited = true;
            }
            try {
                Thread.sleep(100);//线程暂停100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(pic.size());
    }
}
