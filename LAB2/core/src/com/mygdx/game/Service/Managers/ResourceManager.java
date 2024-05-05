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
    private final static String ITEMS_TEXTURE_ATLAS_PATH = "ui/xp/expee-ui.atlas";
    private final static String UI_SKIN_PATH = "ui/xp/expee-ui.json";
    private static final AssetManager assetManager = new AssetManager();
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
}