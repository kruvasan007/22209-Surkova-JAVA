package com.mygdx.game.Model.WorldObject;

import com.badlogic.gdx.math.Rectangle;

public abstract class MapObject {
    protected boolean isVisible = true;
    protected final Rectangle bounds = new Rectangle();

    public abstract float getX();

    public abstract float getY();

    public boolean isVisible() {
        return isVisible;
    }

    public void setInvisible() {
        isVisible = false;
    }

    public void setVisible() {
        isVisible = true;
    }

    public abstract float getWidth();

    public abstract float getHeight();
}
