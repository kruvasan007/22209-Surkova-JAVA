package com.mygdx.game.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Model.World;
import com.mygdx.game.Model.WorldObject.Hero;
import com.mygdx.game.Model.WorldObject.NonPlayerCharacter;
import com.mygdx.game.Service.Managers.CollisionManager;

public class MoveController {
    private final int STEP = 16;
    private final CollisionManager collisionManager;
    private final World world;
    private final Hero hero;

    public MoveController(World world) {
        this.world = world;
        this.hero = world.getHero();
        this.collisionManager = new CollisionManager(world);
    }

    public void moveNPC() {
        for (NonPlayerCharacter npc : world.getNpcList()) {
            if (npc.isAlive())
                npc.moveCharacter();
        }
    }

    public void pressedUp() {
        if (hero.getY() + hero.getPlayerShiftY() <= world.getMapHeight()
                && collisionManager.getCollisionTilesFrom(
                (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) hero.getX(), (int) (hero.getY() + STEP / 2)))
            hero.moveHero(0, Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed());
    }

    public void pressedDown() {
        if (hero.getY() - hero.getPlayerShiftY()>= 0
                && collisionManager.getCollisionTilesFrom(
                        (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) hero.getX(), (int) (hero.getY() - STEP / 2)))
            hero.moveHero(0, -Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed());
    }

    public void pressedLeftRun() {
        if (hero.getX() - hero.getPlayerShiftX() >= 0 && collisionManager.getCollisionTilesFrom(
                (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) (hero.getX() - STEP + hero.getWidth() / 2), (int) hero.getY())) {
            hero.moveHero(-Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed() * 1.5f, 0);
        }
    }

    public void pressedRightRun() {
        if (hero.getX() + hero.getPlayerShiftX() <= world.getMapWidth()
                && collisionManager.getCollisionTilesFrom((TiledMapTileLayer)
                        world.getTiledMap().getLayers().get(0),
                (int) (hero.getX() + STEP + hero.getWidth() / 2), (int) hero.getY()))
            hero.moveHero(Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed() * 1.5f, 0);
    }

    public void pressedLeft() {
        if (hero.getX() - hero.getPlayerShiftX() >= 0 && collisionManager.getCollisionTilesFrom(
                (TiledMapTileLayer) world.getTiledMap().getLayers().get(0),
                (int) (hero.getX() - STEP + hero.getWidth() / 2), (int) hero.getY()))
            hero.moveHero(-Gdx.graphics.getDeltaTime() * hero.getPlayerSpeed(), 0);
    }
    public void pressedRight() {
        if (hero.getX() + hero.getPlayerShiftX() <= world.getMapWidth()
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
        for (int i = 0; i < world.getNpcList().size(); i++) {
            NonPlayerCharacter npc = world.getNpcList().get(i);
            if (collisionManager.checkCollision(npc, hero)) {
                npc.damage();
            }
        }
        hero.punch();
    }


}
