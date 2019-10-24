package com.youthlin.example.swing;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : youthlin.chen @ 2019-10-24 22:25
 */
public class GamePanel extends JPanel implements ILogic {
    private List<GameObject> gameObjectList = new LinkedList<>();
    private ScoreText score = new ScoreText();

    public GamePanel(int width, int height) {
        setSize(width, height);
        setBackground(Color.BLACK);
        addGameObject(score);
    }

    public void addGameObject(GameObject object) {
        gameObjectList.add(object);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.drawRect(10, 10, 490, 730);
        for (GameObject gameObject : gameObjectList) {
            gameObject.paint(g);
        }
    }

    @Override
    public void logic(double fps) {
        for (GameObject gameObject : gameObjectList) {
            gameObject.logic(fps);
        }
        repaint();
    }

}
