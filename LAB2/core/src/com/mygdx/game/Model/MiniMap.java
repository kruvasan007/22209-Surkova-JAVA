package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MiniMap {
    private final SpriteBatch batch;
    private final Sprite sprite;
    private final OrthogonalTiledMapRenderer renderer;
    private final OrthographicCamera camera = new OrthographicCamera();

    public MiniMap(TiledMap map) {
        renderer = new OrthogonalTiledMapRenderer(map);
        camera.setToOrtho(false, 500, 500);
        camera.zoom = 13f;
        batch = new SpriteBatch();
        sprite = new Sprite();
        sprite.setTexture(new Texture(Gdx.files.internal("Textures/e1.png")));
    }

    public void update(float playerX, float playerY, float x, float y) {
        camera.position.x = playerX + x * 10;
        camera.position.y = playerY + y * 10;
        camera.update();

        renderer.setView(camera.combined, playerX - x, playerY - y, 1000, 1000);
        renderer.render();

        batch.begin();
        batch.draw(sprite, playerX, playerY);
        batch.setProjectionMatrix(camera.combined);
        batch.end();
    }

    public void render() {
        renderer.render();
    }
}
