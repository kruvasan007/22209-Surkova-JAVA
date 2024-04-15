package com.mygdx.game.Observers;

public interface Component {

    String MESSAGE_TOKEN = ":::::";

    enum MESSAGE {
        START_TASK
    }

    void receiveMessage(String message);
}
