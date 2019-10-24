package com.youthlin.example.swing;

import java.awt.*;

/**
 * @author : youthlin.chen @ 2019-10-24 22:12
 */
public abstract class GameObject implements ILogic {
    @Override
    public abstract void logic(double fps);

    public abstract void paint(Graphics g);

}
