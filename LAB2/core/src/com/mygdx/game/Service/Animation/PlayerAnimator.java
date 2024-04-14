package com.mygdx.game.Service.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class PlayerAnimator extends CommonHeroAnimator {
    private enum PlayerState {
        wait, walk, talk, run, punch
    }

    private final ArrayList<ArrayList<Texture>> framesToState = new ArrayList<>();
    private final ArrayList<Integer> countOfFrames = new ArrayList<>();
    private int currentFrameIndex = 0;
    private final Animator animator;
    private boolean flipped = false;

    public PlayerAnimator() {
        String path = "PlayerAnimation/";
        int size = PlayerState.values().length;
        countOfFrames.add(6);
        countOfFrames.add(8);
        countOfFrames.add(7);
        countOfFrames.add(8);
        countOfFrames.add(6);
        for (int i = 0; i < size; i++) {
            framesToState.add(new ArrayList<>());
            for (int j = 0; j < countOfFrames.get(i); j++) {
                framesToState.get(i).add(new Texture(Gdx.files.internal(path + PlayerState.values()[i] + "/" + PlayerState.values()[i] + "_" + j + ".png")));
            }
        }
        this.animator = new Animator(framesToState.get(0).get(0));
    }

    public Sprite getSprite() {
        return animator.getSprite();
    }

    public void setNextFrame(float x, float y) {
        if (x < 0f && !flipped) {
            flipped = true;
            animator.flipSprite();
        }
        if (x > 0f && flipped) {
            flipped = false;
            animator.flipSprite();
        }
        if (x == 0 && y == 0) {
            animator.setFrame(framesToState.get(PlayerState.wait.ordinal()).get(currentFrameIndex < countOfFrames.get(PlayerState.wait.ordinal()) ? currentFrameIndex++ : (currentFrameIndex = 0)));
        } else if (x > 10 || x < -10) {
            animator.setFrame(framesToState.get(PlayerState.run.ordinal()).get(currentFrameIndex < countOfFrames.get(PlayerState.run.ordinal()) ? currentFrameIndex++ : (currentFrameIndex = 0)));
        } else {
            animator.setFrame(framesToState.get(PlayerState.walk.ordinal()).get(currentFrameIndex < countOfFrames.get(PlayerState.walk.ordinal()) ? currentFrameIndex++ : (currentFrameIndex = 0)));
        }
    }

    public void setPunch() {
        animator.setFrame(framesToState.get(PlayerState.punch.ordinal()).get(currentFrameIndex < countOfFrames.get(PlayerState.punch.ordinal()) ? currentFrameIndex++ : (currentFrameIndex = 0)));
    }


    public void setTalk() {
        animator.setFrame(framesToState.get(PlayerState.talk.ordinal()).get(currentFrameIndex < countOfFrames.get(PlayerState.talk.ordinal()) ? currentFrameIndex++ : (currentFrameIndex = 0)));
    }
}
