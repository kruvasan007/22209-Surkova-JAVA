package com.mygdx.game.Model.WorldObject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class OrderDistributionPoint extends MapObject {
    private final Sprite sprite;

    private Label label;

    public OrderDistributionPoint() {
        bounds.x = 10 * 32;
        bounds.y = 23 * 32;
        bounds.width = 78;
        bounds.height = 64;
        label = new Label("Issuing orders", bounds.x, bounds.y + bounds.height);
        sprite = new Sprite(new Texture("Textures/ice-cream-car.png"));
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

    public Label getLabel() {
        return label;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
