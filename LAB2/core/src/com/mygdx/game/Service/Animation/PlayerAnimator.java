package com.mygdx.game.Service.Animation;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class PlayerAnimator implements CommonHeroAnimator {
    private enum PlayerState {
        wait, walk, talk, run, punch
    }

    private final Animator animator;

    public PlayerAnimator() {
        String path = "PlayerAnimation/";
        this.animator = new Animator(path);
    }

    public Sprite getSprite() {
        return animator.getSprite();
    }

    public void setNextFrame(float x, float y) {
        if (x < 0f && !animator.isFlipped() || x > 0f && animator.isFlipped())
            animator.flipSprite();
        if (x == 0 && y == 0) {
            animator.setFrame(PlayerState.wait.ordinal());
        } else if (x > 10 || x < -10) {
            animator.setFrame(PlayerState.run.ordinal());
        } else {
            animator.setFrame(PlayerState.walk.ordinal());
        }
    }

    public void setPunch() {
        animator.setFrame(PlayerState.punch.ordinal());
    }

    public void setTalk() {
        animator.setFrame(PlayerState.talk.ordinal());
    }
}
