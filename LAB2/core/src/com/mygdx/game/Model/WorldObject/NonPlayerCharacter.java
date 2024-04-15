package com.mygdx.game.Model.WorldObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Model.Coords;
import com.mygdx.game.Service.Animation.NpsAnimator;

import java.util.concurrent.ThreadLocalRandom;

public class NonPlayerCharacter extends MapObject {
    private final NpsAnimator animator;
    private float deltaTime = 0;
    private int health = 100;
    private boolean IS_ALIVE = true;
    float FREQUENCY_FRAME_CHANGE = 0.1f;
    private final int SHIFT_X = 8;
    private final int SHIFT_Y = 24;
    private final Coords start;
    private final Coords end;
    private int speed = 16;

    public NonPlayerCharacter(Coords start, Coords end) {
        int disp = ThreadLocalRandom.current().nextInt(0, 15 + 1);
        bounds.x = SHIFT_X + 32 * (start.getX() + disp);
        bounds.y = SHIFT_Y + 32 * start.getY();
        this.end = end;
        this.start = start;
        bounds.width = 64;
        bounds.height = 64;
        double coef = ThreadLocalRandom.current().nextDouble(0.6f, 1.2f + 1);
        this.animator = new NpsAnimator();
        speed *= coef;
    }

    public void damage() {
        if (IS_ALIVE) {
            animator.setDamage();
            health -= 25;
            if (health <= 0) {
                death();
            }
        }
    }

    private void death() {
        animator.setDeath();
        IS_ALIVE = false;
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

    public void moveCharacter() {
        deltaTime += Gdx.graphics.getDeltaTime();
        if (deltaTime > FREQUENCY_FRAME_CHANGE && IS_ALIVE) {
            if (bounds.x > 32 * end.getX()){
                bounds.x -= 4;
                speed = -speed;
            } else if(bounds.x < 32 * start.getX()) {
                bounds.x += 4;
                speed = -speed;
            }
            float x = -deltaTime * speed * 1.5f;
            bounds.x += x;
            animator.setNextFrame(x, 0);
            deltaTime = 0;
        }
    }

    public boolean isAlive() {
        return IS_ALIVE;
    }
}
