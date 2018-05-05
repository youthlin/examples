package com.youthlin.demo.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * 创建: youthlin.chen
 * 时间: 2018-05-05 06:48
 */
public class Test {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static CascadeClassifier faceDetector = new CascadeClassifier();
    private static CascadeClassifier eyeDetector = new CascadeClassifier();
    private static final String xmlPath;

    static {
        String lib = new File(Core.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        xmlPath = lib + "/data/haarcascades/";
        faceDetector.load(xmlPath + "haarcascade_frontalface_alt.xml");
        eyeDetector.load(xmlPath + "haarcascade_eye.xml");
    }

    private static boolean stop = false;
    private static JFrame window = new JFrame("OpenCV Test");
    private static JLabel label = new JLabel();
    private static JLabel text = new JLabel();

    public static void main(String[] args) {
        EventQueue.invokeLater(Test::createFrameAndShow);
        cap();
    }

    private static void createFrameAndShow() {
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton capBtn = new JButton("拍照");
        capBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stop = true;
                super.mouseClicked(e);
            }
        });
        window.setBounds(100, 100, 1280, 720);
        text.setBounds(30, 0, 100, 10);
        capBtn.setBounds(30, 10, 100, 30);
        window.getContentPane().add(text);
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
        final double fps = camera.get(Videoio.CV_CAP_PROP_FPS);
        int height = (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int width = (int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
        EventQueue.invokeLater(() -> {
            text.setText(String.format("%.2f", fps));
            window.setSize(width, height);
            label.setSize(width, height);
            window.setResizable(false);
        });
        VideoWriter writer = new VideoWriter();
        File out = new File("cap.mp4");
        if (out.exists()) {
            out.renameTo(new File("cap.renamedAt" + System.currentTimeMillis() + ".mp4"));
            out.delete();
        }
        writer.open(out.getAbsolutePath(), VideoWriter.fourcc('P', 'I', 'M', '1'), fps, new Size(width, height));
        if (!writer.isOpened()) {
            writer = null;
        }
        Mat pic = new Mat();
        while (!stop) {
            camera.read(pic);
            Mat face = detectFace(pic);
            label.setIcon(new ImageIcon(HighGui.toBufferedImage(face)));
            if (writer != null) {
                writer.write(face);
            }
            face.release();
            pic.release();
            try {
                Thread.sleep(100);//线程暂停100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        camera.release();
        if (writer != null) {
            writer.release();
        }
        System.out.println(pic.size());
    }

    private static Mat detectFace(Mat mat) {
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(mat, faces);
        for (Rect rect : faces.toArray()) {
            Imgproc.rectangle(mat,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(255, 0, 0));
        }
        eyeDetector.detectMultiScale(mat, faces);
        for (Rect rect : faces.toArray()) {
            Imgproc.circle(mat, new Point(rect.x + rect.width / 2, rect.y + rect.height / 2),
                    rect.width / 4 + rect.height / 4, new Scalar(0, 0, 255));
        }

        return flip(mat);
    }

    private static Mat flip(Mat mat) {
        Mat result = new Mat();
        Core.flip(mat, result, 1);//0上下 1左右 -1上下再左右
        return result;
    }

}
