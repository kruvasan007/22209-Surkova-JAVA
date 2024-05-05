package com.mygdx.game.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Service.Managers.ResourceManager;
import com.mygdx.game.View.Observers.ViewObject;

public class BaseScreen extends ViewObject implements Screen {
    protected ResourceManager resourceManager;
    protected Stage stage;

    public BaseScreen(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
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
}
