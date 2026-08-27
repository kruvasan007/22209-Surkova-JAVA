package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MiniMap {
    private final static int WIDTH = 150;
    private final static int HEIGHT = 72;
    private final static int PADDING = 12;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera uiCamera = new OrthographicCamera();
    private final int mapWidth;
    private final int mapHeight;
    private float playerX;
    private float playerY;

    public MiniMap(TiledMap map) {
        shapeRenderer = new ShapeRenderer();
        MapProperties prop = map.getProperties();
        mapWidth = prop.get("width", Integer.class) * prop.get("tilewidth", Integer.class);
        mapHeight = prop.get("height", Integer.class) * prop.get("tileheight", Integer.class);
    }

    public void update(float playerX, float playerY, float viewportWidth, float viewportHeight) {
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public void render() {
        float x = PADDING;
        float y = Gdx.graphics.getHeight() - HEIGHT - PADDING;
        float markerX = x + playerX / mapWidth * WIDTH;
        float markerY = y + HEIGHT - playerY / mapHeight * HEIGHT;

        uiCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
        shapeRenderer.rect(x, y, WIDTH, HEIGHT);
        shapeRenderer.setColor(0.18f, 0.35f, 0.18f, 0.85f);
        shapeRenderer.rect(x + 4, y + 4, WIDTH - 8, HEIGHT - 8);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(markerX, markerY, 4);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, WIDTH, HEIGHT);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - 1, y - 1, WIDTH + 2, HEIGHT + 2);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
