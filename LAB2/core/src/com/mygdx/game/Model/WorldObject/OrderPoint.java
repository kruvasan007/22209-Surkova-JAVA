package com.mygdx.game.Model.WorldObject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class OrderPoint extends MapObject{

    private final Sprite sprite;

    public OrderPoint(Integer x, Integer y, String texturePath){
        bounds.x = x * 32 + 12;
        bounds.y = y * 32 + 12;
        bounds.width = 40;
        bounds.height = 40;
        sprite = new Sprite(new Texture(texturePath));
        sprite.setSize(bounds.width, bounds.height);
        sprite.setPosition(bounds.x, bounds.y);
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
