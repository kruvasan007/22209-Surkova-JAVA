package com.mygdx.game.Model.WorldObject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Service.Animation.PlayerAnimator;

public class Hero extends MapObject {

    private final PlayerAnimator animator;
    private final int PLAYER_SHIFT_X = 16;
    private final int PLAYER_SHIFT_Y = 24;
    private final int PLAYER_SPEED = 500;
    private boolean pickable = false;

    public Hero() {
        bounds.x = PLAYER_SHIFT_X + 32 * 11;
        bounds.y = PLAYER_SHIFT_Y + 32 * 16;
        bounds.width = 64;
        bounds.height = 64;
        animator = new PlayerAnimator();
    }

    public int getPlayerShiftX() {
        return PLAYER_SHIFT_X;
    }

    public int getPlayerShiftY() {
        return PLAYER_SHIFT_Y;
    }

    public Sprite getSprite() {
        return animator.getSprite();
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public void moveHero(float x, float y) {
        bounds.x += x;
        bounds.y += y;
        animator.setNextFrame(x, y);
    }

    public boolean isPickSomething() {
        return pickable;
    }

    public void setPickable(){
        pickable = true;
    }

    public void setUnpickable(){
        pickable = false;
    }

    public void startTalk() {
        animator.setTalk();
    }

    public void punch() {
        animator.setPunch();
    }

    public int getPlayerSpeed() {
        return PLAYER_SPEED;
    }
}
