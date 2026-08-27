package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Model.WorldObject.Hero;
import com.mygdx.game.Model.WorldObject.NonPlayerCharacter;
import com.mygdx.game.Model.WorldObject.OrderDistributionPoint;
import com.mygdx.game.Model.WorldObject.OrderPoint;
import com.mygdx.game.Model.WorldObject.Vehicle;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class World {
    private final Hero hero;

    private OrderPoint orderPoint;
    private final ArrayList<NonPlayerCharacter> npcList = new ArrayList<>();
    private final ArrayList<Vehicle> vehicleList = new ArrayList<>();
    private final OrderDistributionPoint distrPoint;
    private final ArrayList<Integer> allowedTiles = new ArrayList<>();
    private final TiledMap tiledMap;
    private final MiniMap miniMap;
    private int mapPixelWidth;
    private int tilePixelWidth;
    private int tilePixelHeight;
    private int mapPixelHeight;


    public World() {
        hero = new Hero();
        distrPoint = new OrderDistributionPoint();
        tiledMap = new TmxMapLoader().load("mapGame.tmx");
        Json json = new Json();
        SpawnPoint spawnPoint = json.fromJson(SpawnPoint.class, Gdx.files.internal("configs/spawn_points.json"));
        allowedTiles.add(1);
        for (int i = 0; i < 10; i++) {
            int id = ThreadLocalRandom.current().nextInt(0, spawnPoint.getPoints().size());
            npcList.add(new NonPlayerCharacter(spawnPoint.getPoints().get(id).getStart(),
                    spawnPoint.getPoints().get(id).getEnd()));
        }
        getMapProperties();
        createVehicles();

        miniMap = new MiniMap(tiledMap);
    }

    public ArrayList<Integer> getAllowedTiles() {
        return allowedTiles;
    }

    public ArrayList<NonPlayerCharacter> getNpcList() {
        return npcList;
    }

    public ArrayList<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public Hero getHero() {
        return hero;
    }

    public int getTileHeight() {
        return tilePixelHeight;
    }

    public int getTileWidth() {
        return tilePixelWidth;
    }

    public int getMapWidth() {
        return mapPixelWidth;
    }

    public int getMapHeight() {
        return mapPixelHeight;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    private void getMapProperties() {
        MapProperties prop = tiledMap.getProperties();
        int mapWidth = prop.get("width", Integer.class);
        int mapHeight = prop.get(
                "height", Integer.class);
        tilePixelWidth = prop.get("tilewidth", Integer.class);
        tilePixelHeight = prop.get("tileheight", Integer.class);
        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;
    }

    private void createVehicles() {
        ArrayList<MapObject> vehicles = new ArrayList<>();
        for (MapObject object : tiledMap.getLayers().get("objLayer").getObjects()) {
            Integer gid = object.getProperties().get("gid", Integer.class);
            if (object instanceof TextureMapObject && gid != null && (gid == 58 || gid == 59)) {
                vehicleList.add(new Vehicle((TextureMapObject) object));
                vehicles.add(object);
            }
        }
        for (MapObject vehicle : vehicles) {
            tiledMap.getLayers().get("objLayer").getObjects().remove(vehicle);
        }
    }

    public OrderDistributionPoint getDistributionPoint() {
        return distrPoint;
    }

    public void createObject(Coords coords, String texturePath) {
        orderPoint = new OrderPoint(coords.getX(), coords.getY(), texturePath);
    }

    public OrderPoint getOrderPoint() {
        return orderPoint;
    }

    public MiniMap getMiniMap() {
        return miniMap;
    }
}
