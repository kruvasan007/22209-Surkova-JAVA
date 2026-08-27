package com.mygdx.game.Model.WorldObject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.objects.TextureMapObject;

public class Vehicle extends MapObject {
    private final Sprite sprite;
    private final int speed = 1500;
    private boolean isFlipped = false;

    public Vehicle(TextureMapObject object) {
        bounds.x = object.getX();
        bounds.y = object.getY();
        bounds.width = object.getProperties().get("width", Float.class);
        bounds.height = object.getProperties().get("height", Float.class);
        sprite = new Sprite(object.getTextureRegion());
        updateSprite();
    }

    public void moveVehicle(float x, float y) {
        bounds.x += x;
        bounds.y += y;
        updateDirection(x, y);
        updateSprite();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public float getX() {
        return bounds.x;
    }

    @Override
    public float getY() {
        return bounds.y;
    }

    @Override
    public float getWidth() {
        return bounds.width;
    }

    @Override
    public float getHeight() {
        return bounds.height;
    }

    private void updateDirection(float x, float y) {
        if (x < 0 && !isFlipped || x > 0 && isFlipped) {
            sprite.flip(true, false);
            isFlipped = !isFlipped;
        }
        if (Math.abs(y) > Math.abs(x)) {
            sprite.setScale(0.65f, 1f);
        } else if (Math.abs(x) > 0) {
            sprite.setScale(1f, 1f);
        }
    }

    private void updateSprite() {
        sprite.setSize(bounds.width, bounds.height);
        sprite.setOriginCenter();
        sprite.setPosition(bounds.x, bounds.y);
    }
}
