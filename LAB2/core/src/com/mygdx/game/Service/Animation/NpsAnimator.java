package com.mygdx.game.Service.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class NpsAnimator extends CommonHeroAnimator {

    private enum NpsState {
        wait, walk, death, damage
    }

    private String path;
    private final ArrayList<ArrayList<Texture>> framesToState = new ArrayList<>();
    private final ArrayList<Integer> countOfFrames = new ArrayList<>();
    private int currentFrameIndex = 0;
    private final Animator animator;
    private boolean flipped = false;


    public NpsAnimator() {
        int id = ThreadLocalRandom.current().nextInt(1, 2 + 1);
        path = "NpsAnimation/Nps" + id + "/";
        countOfFrames.add(6);
        countOfFrames.add(8);
        countOfFrames.add(7);
        countOfFrames.add(2);
        int size = NpsState.values().length;
        for (int i = 0; i < size; i++) {
            framesToState.add(new ArrayList<>());
            for (int j = 0; j < countOfFrames.get(i); j++) {
                framesToState.get(i).add(new Texture(Gdx.files.internal(path + NpsState.values()[i] + "/" + NpsState.values()[i] + "_" + j + ".png")));
            }
        }
        this.animator = new Animator(framesToState.get(0).get(0));
    }

    public void setDamage() {
        animator.setFrame(framesToState.get( NpsState.damage.ordinal())
                .get(currentFrameIndex < countOfFrames.get(NpsState.damage.ordinal())
                    ? currentFrameIndex++ : (currentFrameIndex = 0)));
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
        animator.setFrame(framesToState.get(NpsState.walk.ordinal()).get(currentFrameIndex < countOfFrames.get(NpsState.walk.ordinal()) ? currentFrameIndex++ : (currentFrameIndex = 0)));
    }

    public Sprite getSprite() {
        return animator.getSprite();
    }

    public void setDeath() {
        animator.playAnimationInTimer(framesToState.get(NpsState.death.ordinal()), countOfFrames.get(NpsState.death.ordinal()));
    }
}
