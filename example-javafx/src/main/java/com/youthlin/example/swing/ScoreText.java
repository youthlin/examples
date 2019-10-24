package com.youthlin.example.swing;

import javafx.beans.property.SimpleIntegerProperty;

import java.awt.*;

import static com.youthlin.utils.i18n.Translation._f;

/**
 * @author : youthlin.chen @ 2019-10-24 22:39
 */
public class ScoreText extends GameObject {
    private SimpleIntegerProperty score = new SimpleIntegerProperty();

    @Override
    public void logic(double fps) {
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString(_f("Score: {0}", score.get()), 510, 30);
    }
}
