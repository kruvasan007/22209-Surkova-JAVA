package com.mygdx.game.Model.WorldObject;

import java.util.Timer;
import java.util.TimerTask;

public class Label extends MapObject {

    private final String label;

    private final float bound = 24f;
    private float STEP = 2;
    private final float startY;

    public Label(String s, float x, float y) {
        label = s;
        bounds.x = x;
        bounds.y = y;
        startY = y;
        bounds.width = 64;
        bounds.height = 64;
        move();
    }

    public void move() {
        int initialDelay = 25;
        int period = 100;
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
            if (bounds.y + STEP >= bound + startY || bounds.y + STEP <= startY) {
                STEP = -STEP;
            }
            bounds.y += STEP;
            }
        };
        timer.schedule(task, initialDelay, period);
    }

    @Override
    public float getX() {
        return bounds.x;
    }

    @Override
    public float getY() {
        return bounds.y;
    }

    @Override
    public float getWidth() {
        return bounds.getWidth();
    }

    @Override
    public float getHeight() {
        return bounds.getHeight();
    }

    public String getLabel() {
        return label;
    }
}
