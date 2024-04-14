package com.mygdx.game;

import com.mygdx.game.Controllers.Controller;
import com.mygdx.game.Observers.ComponentObserver;
import com.mygdx.game.Service.Managers.ResourceManager;
import com.mygdx.game.View.MenuScreen;

public class DeliveryGame extends com.badlogic.gdx.Game implements ComponentObserver {
    private ResourceManager resourceManager;
    private Controller controller;

    @Override
    public void create() {
        resourceManager = new ResourceManager();
        MenuScreen menuScreen = new MenuScreen(this, resourceManager);
        this.setScreen(menuScreen);
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

    @Override
    public void onNotify(String value, ComponentEvent event) {

    }
}