package com.mygdx.game.Service.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class NpsAnimator implements CommonHeroAnimator {

    private enum NpsState {
        wait, walk, death, damage
    }

    private final Animator animator;

    public NpsAnimator() {
        int id = ThreadLocalRandom.current().nextInt(1, 2 + 1);
        String path = "NpsAnimation/Nps" + id + "/";
        this.animator = new Animator(path);
    }

    public void setDamage() {
        animator.setFrame(NpsState.damage.ordinal());
    }

    public void setNextFrame(float x, float y) {
        if (x < 0f && !animator.isFlipped() || x > 0f && animator.isFlipped())
            animator.flipSprite();
        animator.setFrame(NpsState.walk.ordinal());
    }

    public Sprite getSprite() {
        return animator.getSprite();
    }

    public void setDeath() {
        animator.playAnimationInTimer(NpsState.death.ordinal());
    }
}
