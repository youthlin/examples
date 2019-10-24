package com.youthlin.example.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : youthlin.chen @ 2019-10-24 21:05
 */
public class GameThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameThread.class);
    private static final double DEFAULT_FPS = 60;
    private final ILogic logic;
    private final double fps;
    private long pauseTime;
    private boolean exit;

    public GameThread(ILogic logic) {
        this(logic, DEFAULT_FPS);
    }

    public GameThread(ILogic logic, double fps) {
        this.logic = logic;
        this.fps = fps;
        setName("GameThread");
    }

    public void requestExit() {
        exit = true;
    }

    public void reStart() {
        LOGGER.debug("恢复");
        if (pauseTime > 0) {
            Thread.currentThread().interrupt();
        }
        pauseTime = 0;
    }

    public void pause() {
        LOGGER.debug("暂停");
        pauseTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long sleepTime = (long) (1000 / fps);
        while (!exit) {
            while (!exit && pauseTime > 0 && !Thread.interrupted()) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    LOGGER.debug("暂停中打断睡眠");
                    Thread.currentThread().interrupt();
                }
            }
            if (!exit) {
                logic.logic(fps);
            }
            if (!exit) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    LOGGER.debug("运行中打断睡眠");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
