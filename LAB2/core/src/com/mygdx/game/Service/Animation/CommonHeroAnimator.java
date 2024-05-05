package com.mygdx.game.Service.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public interface CommonHeroAnimator {

    void setNextFrame(float x, float y);

    Sprite getSprite();
}
