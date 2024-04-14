package com.mygdx.game.Observers;

public interface Component {

    String MESSAGE_TOKEN = ":::::";

    enum MESSAGE {
        START_TASK, GET_NEW_TASK, COMPLETE_TASK
    }

    void dispose();

    void receiveMessage(String message);
}
