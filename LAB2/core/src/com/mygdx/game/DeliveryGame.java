package com.mygdx.game;

import com.mygdx.game.Service.Managers.ResourceManager;
import com.mygdx.game.View.EndGameScreen;
import com.mygdx.game.View.GameScreen;
import com.mygdx.game.View.MenuScreen;
import com.mygdx.game.View.Observers.ViewObserver;

public class DeliveryGame extends com.badlogic.gdx.Game implements ViewObserver {
    private ResourceManager resourceManager;

    @Override
    public void create() {
        resourceManager = new ResourceManager();
        MenuScreen menuScreen = new MenuScreen(resourceManager);
        menuScreen.addObserver(this);
        this.setScreen(menuScreen);
    }

    @Override
    public void onNotify(String value, ViewEvent event) {
        switch (event) {
            case END_GAME -> {
                EndGameScreen endGameScreen = new EndGameScreen(resourceManager);
                this.setScreen(endGameScreen);
                endGameScreen.addObserver(this);
            }
            case START_GAME -> {
                GameScreen gameScreen = new GameScreen(resourceManager);
                this.setScreen(gameScreen);
                gameScreen.addObserver(this);
            }
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }
}