package com.mygdx.game.View;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Service.Managers.ResourceManager;

public class BaseScreen implements Screen {
    protected final Game gdxGame;
    protected OrthographicCamera gameCam;
    protected ResourceManager resourceManager;
    protected Stage stage;

    public BaseScreen(Game gdxGame, ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.gdxGame = gdxGame;
    }

    public void createButton(String buttonName, float posX, float posY, Table table) {
        TextureRegion[][] playButtons = resourceManager.button;
        BitmapFont font = resourceManager.font;
        TextureRegionDrawable imageUp = new TextureRegionDrawable(playButtons[0][0]);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(imageUp, imageUp, null, font);
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.pressedOffsetY = -2;
        TextButton button = new TextButton(buttonName, buttonStyle);
        button.pad(4f);
        button.setTransform(true);
        button.setScale(3f);
        table.add(button).padLeft(posX - button.getWidth()).padTop(posY);
        table.row();
    }

    public Table createTable() {
        Table table = new Table();
        table.setBounds(0, 0, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        return table;
    }

    @Override
    public void show() {
        // Nothing
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public OrthographicCamera getGameCam() {
        return gameCam;
    }

    public Stage getStage() {
        return stage;
    }
}
