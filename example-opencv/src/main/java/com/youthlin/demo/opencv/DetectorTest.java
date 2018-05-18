package com.youthlin.demo.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 创建: youthlin.chen
 * 时间: 2018-05-05 15:45
 */
public class DetectorTest {
    private static final String pic = "/home/lin/Pictures/美霖和勇涛.jpg";
    private static final String face = "/home/lin/Pictures/face.png";
    private static final double ratio = 0.7;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat pic = Imgcodecs.imread(DetectorTest.pic);
        Imgproc.resize(pic, pic, new Size(pic.width() / 5, pic.height() / 5));
        Mat face = Imgcodecs.imread(DetectorTest.face);

        MatOfKeyPoint picKeys = new MatOfKeyPoint();
        ORB orb = ORB.create();
        orb.detect(pic, picKeys);
        MatOfKeyPoint picDesc = new MatOfKeyPoint();
        orb.compute(pic, picKeys, picDesc);
        Features2d.drawKeypoints(pic, picKeys, pic, new Scalar(0, 0, 155), 0);
        HighGui.imshow("pic", pic);
        HighGui.waitKey();
        System.exit(0);
    }
}
