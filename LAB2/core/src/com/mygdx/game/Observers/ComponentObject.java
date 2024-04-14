package com.mygdx.game.Observers;

import com.badlogic.gdx.utils.Array;

public class ComponentObject {
    private final Array<ComponentObserver> observers;

    public ComponentObject() {
        observers = new Array<>();
    }

    public void addObserver(ComponentObserver conversationObserver) {
        observers.add(conversationObserver);
    }

    public void removeObserver(ComponentObserver conversationObserver) {
        observers.removeValue(conversationObserver, true);
    }

    public void removeAllObservers() {
        for(ComponentObserver observer: observers) {
            observers.removeValue(observer, true);
        }
    }

    protected void notify(final String value, ComponentObserver.ComponentEvent event) {
        for(ComponentObserver observer: observers) {
            observer.onNotify(value, event);
        }
    }
}