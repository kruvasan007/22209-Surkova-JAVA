package com.mygdx.game.View;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.DeliveryGame;
import com.mygdx.game.Service.Managers.ResourceManager;
import com.mygdx.game.View.Observers.ViewObserver;

public class EndGameScreen extends BaseScreen {
    private final Table menuTable;
    private final Stage menuStage = new Stage();

    public EndGameScreen(ResourceManager resourceManager) {
        super(resourceManager);
        menuTable = createTable();
        createRestartButton();
        createExitButton();
    }

    private void createRestartButton() {
        createButton("Restart", 0, menuTable.getHeight() / 10, menuTable);
        Actor newButton = menuTable.getCells().get(0).getActor();
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                EndGameScreen.this.notify("Restart game", ViewObserver.ViewEvent.START_GAME);
            }
        });
    }

    private void createExitButton() {
        createButton("Exit", 0, menuTable.getHeight() / 9, menuTable);
        Actor newButton = menuTable.getCells().get(1).getActor();
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                EndGameScreen.this.notify("End game", ViewObserver.ViewEvent.END_GAME);
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
