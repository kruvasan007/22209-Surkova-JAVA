package com.mygdx.game.Service.Animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Animator {
    private final Sprite sprite;

    public Animator(Texture texture) {
        sprite = new Sprite(texture);
    }

    public void flipSprite() {
        sprite.flip(true, false);
    }

    public void setFrame(Texture texture) {
        sprite.setTexture(texture);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void playAnimationInTimer(ArrayList<Texture> frames, int countOfFrames) {
        int initialDelay = 50;
        int period = 250;
        final Integer[] currentFrameIndex = {0};
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                setFrame(frames.get(currentFrameIndex[0]++));
                if (currentFrameIndex[0] == countOfFrames - 1)
                    cancel();
            }
        };
        timer.schedule(task, initialDelay, period);
    }
}
