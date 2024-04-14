package com.mygdx.game.View;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Service.Managers.ResourceManager;

public class MenuScreen extends BaseScreen {
    private Table menuTable;
    private Stage menuStage = new Stage();

    public MenuScreen(Game gdxGame, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);
        menuTable = createTable();
        createNewButton();
    }

    private void createNewButton() {
        createButton("New game", 0, menuTable.getHeight() / 10, menuTable);
        Actor newButton = menuTable.getCells().get(0).getActor();
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                gdxGame.setScreen(new GameScreen(gdxGame, resourceManager));
            }
        });
    }

    @Override
    public void show() {
        menuStage.addActor(menuTable);
        Gdx.input.setInputProcessor(menuStage);
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(resourceManager.cursor, 0, 0));
    }

    @Override
    public void render(float delta) {
        menuStage.act(delta);
        menuStage.getBatch().begin();
        menuStage.getBatch().draw(resourceManager.backgroundSheet, 0, 0);
        menuStage.getBatch().end();
        menuStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        menuTable.remove();
    }
}