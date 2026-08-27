package com.mygdx.game.Service.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class ResourceManager {
    private final static String ITEMS_TEXTURE_ATLAS_PATH = "ui/xp/expee-ui.atlas";
    private final static String UI_SKIN_PATH = "ui/xp/expee-ui.json";
    private final static String UI_FONT_PATH = "ui/xp/arial-unicode.ttf";
    private final static String CYRILLIC_CHARS = FreeTypeFontGenerator.DEFAULT_CHARS
            + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
            + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
            + "№«»—–…";
    private static final AssetManager assetManager = new AssetManager();
    public TextureAtlas atlas;
    public Pixmap cursor;
    public TextureRegion[][] button;
    public BitmapFont font;
    public Texture backgroundSheet;
    public TextureRegion[][] textField;
    public static Skin skin;

    public ResourceManager() {
        assetManager.load(ITEMS_TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();
        atlas = assetManager.get(ITEMS_TEXTURE_ATLAS_PATH, TextureAtlas.class);
        cursor = new Pixmap(Gdx.files.internal("ui/cursor.png"));
        button = atlas.findRegion("button").split(18,18);

        textField = atlas.findRegion("textfield").split(4,4);
        backgroundSheet = new Texture(Gdx.files.internal("ui/xp/backdround.png"));

        font = createCyrillicFont();
        skin = new Skin(Gdx.files.internal(UI_SKIN_PATH));
        applyFontToSkin();
    }

    private BitmapFont createCyrillicFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(UI_FONT_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        parameter.characters = CYRILLIC_CHARS;
        BitmapFont generatedFont = generator.generateFont(parameter);
        generator.dispose();
        return generatedFont;
    }

    private void applyFontToSkin() {
        skin.add("font", font, BitmapFont.class);
        skin.add("title", font, BitmapFont.class);

        setLabelFont("default");
        setLabelFont("title");
        setLabelFont("file");
        setLabelFont("folder");
        setLabelFont("drive");
        setLabelFont("address-bar");
        setLabelFont("white");

        setTextButtonFont("default");
        setTextButtonFont("back");
        setTextButtonFont("shutdown");
        setTextButtonFont("logoff");
        setTextButtonFont("programs");

        setCheckBoxFont("default");
        setCheckBoxFont("radio");
        setListFont("default");
        setSelectBoxFont("default");
        setTextFieldFont("default");
        setWindowFont("default");
        setWindowFont("loading");
        setWindowFont("dialog");
    }

    private void setLabelFont(String styleName) {
        skin.get(styleName, Label.LabelStyle.class).font = font;
    }

    private void setTextButtonFont(String styleName) {
        skin.get(styleName, TextButton.TextButtonStyle.class).font = font;
    }

    private void setCheckBoxFont(String styleName) {
        skin.get(styleName, CheckBox.CheckBoxStyle.class).font = font;
    }

    private void setListFont(String styleName) {
        List.ListStyle style = skin.get(styleName, List.ListStyle.class);
        style.font = font;
    }

    private void setSelectBoxFont(String styleName) {
        skin.get(styleName, SelectBox.SelectBoxStyle.class).font = font;
    }

    private void setTextFieldFont(String styleName) {
        TextField.TextFieldStyle style = skin.get(styleName, TextField.TextFieldStyle.class);
        style.font = font;
        style.messageFont = font;
    }

    private void setWindowFont(String styleName) {
        skin.get(styleName, Window.WindowStyle.class).titleFont = font;
    }
}