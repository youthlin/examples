package com.youthlin.example.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.youthlin.utils.i18n.Translation.__;

/**
 * @author : youthlin.chen @ 2019-10-24 21:00
 */
public class MainWindow extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);
    private static final int WIDTH = 640;
    private static final int HEIGHT = 740;
    private final GameThread gameThread;

    public MainWindow() {
        setTitle("Hello");
        GamePanel panel = new GamePanel(WIDTH, HEIGHT);
        getContentPane().add(panel);
        setSize(WIDTH, HEIGHT + 30);
        setResizable(false);
        //这句会使得在屏幕上居中显示
        setLocationRelativeTo(null);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LOGGER.debug("new size={}", e.getComponent().getSize());
            }
        });
        gameThread = new GameThread(panel);
    }

    public void start() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(e.getWindow(), __("Are you sure to close"))
                        == JOptionPane.OK_OPTION) {
                    LOGGER.info("finish");
                    gameThread.requestExit();
                    dispose();
                }
            }

            @Override
            public void windowActivated(WindowEvent e) {
                gameThread.reStart();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                gameThread.pause();
            }
        });
        gameThread.start();
        setVisible(true);
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.start();
    }
}
