package com.mygdx.game.Observers;

public interface ComponentObserver {
    enum ComponentEvent {
        SHOW_DIALOG, START_TASK, COMPLETE_TASK, END_GAME, GET_NEW_TASK, MOVE_ORDER
    }

    void onNotify(final String value, ComponentEvent event);
}
