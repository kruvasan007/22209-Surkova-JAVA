package com.mygdx.game.Service.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.logging.Logger;

public class ResourceManager {
    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class.getName());
    private final static String ITEMS_TEXTURE_ATLAS_PATH = "ui/xp/expee-ui.atlas";
    private final static String UI_SKIN_PATH = "ui/xp/expee-ui.json";
    private static AssetManager assetManager = new AssetManager();
    public TextureAtlas atlas;
    public Pixmap cursor;
    public TextureRegion[][] button;
    public BitmapFont font;
    public Texture backgroundSheet;
    public TextureRegion[][] textField;

    public static Skin skin = new Skin(Gdx.files.internal(UI_SKIN_PATH));

    public ResourceManager() {
        assetManager.load(ITEMS_TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();
        atlas = assetManager.get(ITEMS_TEXTURE_ATLAS_PATH, TextureAtlas.class);
        cursor = new Pixmap(Gdx.files.internal("ui/cursor.png"));
        button = atlas.findRegion("button").split(18,18);

        textField = atlas.findRegion("textfield").split(4,4);
        backgroundSheet = new Texture(Gdx.files.internal("ui/xp/backdround.png"));

        font = new BitmapFont(Gdx.files.internal("ui/xp/font-export.fnt"), atlas.findRegion("font-export"), false);
        skin = new Skin(Gdx.files.internal("ui/xp/expee-ui.json"));
    }

    public static TiledMap getMapAsset(String mapFilenamePath) {
        TiledMap map = null;

        // once the asset manager is done loading
        if (assetManager.isLoaded(mapFilenamePath)) {
            map = assetManager.get(mapFilenamePath, TiledMap.class);
        } else {
            LOGGER.info("Load");
        }

        return map;
    }

    /*
    public static void loadMusicAsset(String musicFilenamePath) {
        if (musicFilenamePath == null || musicFilenamePath.isEmpty()) {
            return;
        }

        if (assetManager.isLoaded(musicFilenamePath)) {
            return;
        }

        //load asset
        if (filePathResolver.resolve(musicFilenamePath).exists()) {
            assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
            assetManager.load(musicFilenamePath, Music.class);
            //Until we add loading screen, just block until we load the map
            assetManager.finishLoadingAsset(musicFilenamePath);
            LOGGER.debug("Music loaded!: {}", musicFilenamePath);
        } else {
            LOGGER.debug("Music doesn't exist!: {}", musicFilenamePath);
        }
    }

    public static Music getMusicAsset(String musicFilenamePath) {
        Music music = null;

        // once the asset manager is done loading
        if (assetManager.isLoaded(musicFilenamePath)) {
            music = assetManager.get(musicFilenamePath, Music.class);
        } else {
            LOGGER.debug("Music is not loaded: {}", musicFilenamePath);
        }

        return music;
    }*/

    public void dispose() {
        assetManager.dispose();

        atlas.dispose();

        font.dispose();
    }
}