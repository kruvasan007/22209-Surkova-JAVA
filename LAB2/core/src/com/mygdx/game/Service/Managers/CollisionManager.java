package com.mygdx.game.Service.Managers;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Model.World;
import com.mygdx.game.Model.WorldObject.MapObject;

public class CollisionManager {
    private final World world;

    public CollisionManager(World world) {
        this.world = world;
    }

    public boolean checkCollision(MapObject object1, MapObject object2) {
        return Math.abs(object1.getX() - object2.getX()) <= (object1.getWidth() / 2 + object2.getWidth() / 2) / 2
                && Math.abs(object1.getY() - object2.getY()) <= (object1.getHeight() / 2 + object2.getHeight() / 2);
    }

    public boolean getCollisionTilesFrom(TiledMapTileLayer layer, Integer posX, Integer posY) {
        TiledMapTileLayer.Cell cell = layer.getCell(
                (posX) / world.getTileWidth(),
                (posY) / world.getTileHeight() + 1
        );
        if (cell != null) {
            if (cell.getTile() != null) {
                return !world.getAllowedTiles().contains(cell.getTile().getId());
            }
        }
        return false;
    }
}
