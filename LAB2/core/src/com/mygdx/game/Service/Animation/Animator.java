package com.mygdx.game.Service.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Animator {
    private final Sprite sprite;
    private final ArrayList<ArrayList<Texture>> framesToState = new ArrayList<>();
    private final ArrayList<Integer> countOfFrames = new ArrayList<>();
    private int currentFrameIndex = 0;
    private boolean flipped = false;

    public Animator(String path) {
        Json json = new Json();
        AnimationConfig animationConfig = json.fromJson(AnimationConfig.class, Gdx.files.internal(path + "/animConfig.json"));
        ArrayList<String> nameAnimation = new ArrayList<>();
        for (AnimationItem animation : animationConfig.getAnimations()) {
            countOfFrames.add(animation.getCountFrames());
            nameAnimation.add(animation.getName());
        }

        for (int i = 0; i < countOfFrames.size(); i++) {
            framesToState.add(new ArrayList<>());
            for (int j = 0; j < countOfFrames.get(i); j++) {
                framesToState.get(i).add(new Texture(Gdx.files.internal(path + "/" + nameAnimation.get(i) + "/" + nameAnimation.get(i) + "_" + j + ".png")));
            }
        }

        sprite = new Sprite(framesToState.get(0).get(0));
    }

    public void flipSprite() {
        flipped = !flipped;
        sprite.flip(true, false);
    }

    public void setFrame(int textureIdx) {
        sprite.setTexture(framesToState.get(textureIdx).get(currentFrameIndex < countOfFrames.get(textureIdx) ? currentFrameIndex++ : (currentFrameIndex = 0)));
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void playAnimationInTimer(int textureIdx) {
        int initialDelay = 50;
        int period = 250;
        int countFrames = countOfFrames.get(textureIdx);
        final Integer[] currentFrameIndex = {0};
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                sprite.setTexture(framesToState.get(textureIdx)
                        .get(currentFrameIndex[0] < countOfFrames.get(textureIdx)
                                ? currentFrameIndex[0]++ : (currentFrameIndex[0] = 0)));
                if (currentFrameIndex[0] == countFrames - 1)
                    cancel();
            }
        };
        timer.schedule(task, initialDelay, period);
    }

    public boolean isFlipped() {
        return flipped;
    }
}
