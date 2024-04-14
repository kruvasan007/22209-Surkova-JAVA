package com.mygdx.game.Service.Animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

abstract public class CommonHeroAnimator {

    public abstract void setNextFrame(float x, float y);

    public abstract Sprite getSprite();
}
