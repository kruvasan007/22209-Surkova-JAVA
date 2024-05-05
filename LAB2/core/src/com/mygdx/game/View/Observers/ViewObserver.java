package com.mygdx.game.View.Observers;

public interface ViewObserver {

    enum ViewEvent {
        END_GAME, START_GAME
    }

    void onNotify(final String value, ViewObserver.ViewEvent event);
}
