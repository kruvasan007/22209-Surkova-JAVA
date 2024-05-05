package com.mygdx.game.View.Observers;

import com.badlogic.gdx.utils.Array;

public class ViewObject {
    private final Array<ViewObserver> observers;

    public ViewObject() {
        observers = new Array<>();
    }

    public void addObserver(ViewObserver conversationObserver) {
        observers.add(conversationObserver);
    }

    protected void notify(final String value, ViewObserver.ViewEvent event) {
        for(ViewObserver observer: observers) {
            observer.onNotify(value, event);
        }
    }
}
