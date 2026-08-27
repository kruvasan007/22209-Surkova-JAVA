package com.mygdx.game.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Model.World;
import com.mygdx.game.Model.WorldObject.Hero;
import com.mygdx.game.Model.WorldObject.NonPlayerCharacter;
import com.mygdx.game.Model.WorldObject.Vehicle;
import com.mygdx.game.Service.Managers.CollisionManager;

public class MoveController {
    private final int STEP = 16;
    private final CollisionManager collisionManager;
    private final World world;
    private final Hero hero;
    private Vehicle currentVehicle;

    public MoveController(World world) {
        this.world = world;
        this.hero = world.getHero();
        this.collisionManager = new CollisionManager(world);
    }

    public boolean isDriving() {
        return currentVehicle != null;
    }

    public Vehicle getNearestVehicle() {
        for (Vehicle vehicle : world.getVehicleList()) {
            if (collisionManager.checkCollision(vehicle, hero)) {
                return vehicle;
            }
        }
        return null;
    }

    public void switchVehicle() {
        if (isDriving()) {
            hero.setVisible();
            currentVehicle = null;
            return;
        }
        currentVehicle = getNearestVehicle();
        if (currentVehicle != null) {
            hero.setInvisible();
            syncHeroWithVehicle();
        }
    }

    public void moveNPC() {
        for (NonPlayerCharacter npc : world.getNpcList()) {
            if (npc.isAlive())
                npc.moveCharacter();
        }
    }

    public void pressedUp() {
        if (isDriving()) {
            moveVehicle(0, Gdx.graphics.getDeltaTime() * currentVehicle.getSpeed());
        } else if (hero.getY() + hero.getPlayerShiftY() <= world.getMapHeight()
                && collisionManager.getCollisionTilesFrom(
                (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) hero.getX(), (int) (hero.getY() + STEP / 2)))
            hero.moveHero(0, Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed());
    }

    public void pressedDown() {
        if (isDriving()) {
            moveVehicle(0, -Gdx.graphics.getDeltaTime() * currentVehicle.getSpeed());
        } else if (hero.getY() - hero.getPlayerShiftY()>= 0
                && collisionManager.getCollisionTilesFrom(
                        (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) hero.getX(), (int) (hero.getY() - STEP / 2)))
            hero.moveHero(0, -Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed());
    }

    public void pressedLeftRun() {
        if (isDriving()) {
            moveVehicle(-Gdx.graphics.getDeltaTime() * currentVehicle.getSpeed(), 0);
        } else if (hero.getX() - hero.getPlayerShiftX() >= 0 && collisionManager.getCollisionTilesFrom(
                (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) (hero.getX() - STEP + hero.getWidth() / 2), (int) hero.getY())) {
            hero.moveHero(-Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed() * 1.5f, 0);
        }
    }

    public void pressedRightRun() {
        if (isDriving()) {
            moveVehicle(Gdx.graphics.getDeltaTime() * currentVehicle.getSpeed(), 0);
        } else if (hero.getX() + hero.getPlayerShiftX() <= world.getMapWidth()
                && collisionManager.getCollisionTilesFrom((TiledMapTileLayer)
                        world.getTiledMap().getLayers().get(0),
                (int) (hero.getX() + STEP + hero.getWidth() / 2), (int) hero.getY()))
            hero.moveHero(Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed() * 1.5f, 0);
    }

    public void pressedLeft() {
        if (isDriving()) {
            moveVehicle(-Gdx.graphics.getDeltaTime() * currentVehicle.getSpeed(), 0);
        } else if (hero.getX() - hero.getPlayerShiftX() >= 0 && collisionManager.getCollisionTilesFrom(
                (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) (hero.getX() - STEP + hero.getWidth() / 2), (int) hero.getY()))
            hero.moveHero(-Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed(), 0);
    }
    public void pressedRight() {
        if (isDriving()) {
            moveVehicle(Gdx.graphics.getDeltaTime() * currentVehicle.getSpeed(), 0);
        } else if (hero.getX() + hero.getPlayerShiftX() <= world.getMapWidth()
                && collisionManager.getCollisionTilesFrom(
                        (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) (hero.getX() + STEP + hero.getWidth() / 2), (int) hero.getY()))
            hero.moveHero(Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed(), 0);
    }

    public void pressedCancel() {
        hero.moveHero(0, 0);
    }

    public void pressedTalk() {
        hero.startTalk();
    }

    public void pressedPunch() {
        if (isDriving()) {
            return;
        }
        for (int i = 0; i < world.getNpcList().size(); i++) {
            NonPlayerCharacter npc = world.getNpcList().get(i);
            if (collisionManager.checkCollision(npc, hero)) {
                npc.damage();
            }
        }
        hero.punch();
    }

    private void moveVehicle(float x, float y) {
        if (!canMoveVehicle(x, y)) {
            return;
        }
        currentVehicle.moveVehicle(x, y);
        syncHeroWithVehicle();
    }

    private boolean canMoveVehicle(float x, float y) {
        float nextX = currentVehicle.getX() + x;
        float nextY = currentVehicle.getY() + y;
        if (nextX < 0
                || nextX + currentVehicle.getWidth() > world.getMapWidth()
                || nextY < 0
                || nextY + currentVehicle.getHeight() > world.getMapHeight()) {
            return false;
        }

        TiledMapTileLayer layer = (TiledMapTileLayer) world.getTiledMap().getLayers().get(0);
        int checkY = (int) (y > 0 ? nextY + currentVehicle.getHeight() - STEP : nextY);
        int checkX = (int) (x > 0 ? nextX + currentVehicle.getWidth() - STEP : x < 0 ? nextX + STEP : nextX + currentVehicle.getWidth() / 2);

        if (x != 0) {
            return collisionManager.getCollisionTilesFrom(layer, checkX, (int) nextY)
                    && collisionManager.getCollisionTilesFrom(layer, checkX, (int) (nextY + currentVehicle.getHeight() / 2))
                    && collisionManager.getCollisionTilesFrom(layer, checkX, (int) (nextY + currentVehicle.getHeight() - STEP));
        }
        return collisionManager.getCollisionTilesFrom(layer, (int) (nextX + STEP), checkY)
                && collisionManager.getCollisionTilesFrom(layer, (int) (nextX + currentVehicle.getWidth() / 2), checkY)
                && collisionManager.getCollisionTilesFrom(layer, (int) (nextX + currentVehicle.getWidth() - STEP), checkY);
    }

    private void syncHeroWithVehicle() {
        hero.moveHero(
                currentVehicle.getX() + currentVehicle.getWidth() / 2 - hero.getWidth() / 2 - hero.getX(),
                currentVehicle.getY() + currentVehicle.getHeight() / 2 - hero.getHeight() / 2 - hero.getY()
        );
    }
}

