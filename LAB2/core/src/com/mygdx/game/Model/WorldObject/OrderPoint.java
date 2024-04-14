package com.mygdx.game.Model.WorldObject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class OrderPoint extends MapObject{

    private final Sprite sprite;

    public OrderPoint(Integer x, Integer y){
        bounds.x = x * 32;
        bounds.y = y * 32;
        bounds.width = 78;
        sprite = new Sprite(new Texture("Textures/box.png"));
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

    public float getHeight() { return bounds.height;}
    public Sprite getSprite() {
        return sprite;
    }
}
