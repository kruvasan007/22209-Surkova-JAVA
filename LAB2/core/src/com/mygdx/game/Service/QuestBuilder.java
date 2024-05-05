package com.mygdx.game.Service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Model.Quest.Quest;

public class QuestBuilder {

    public Quest createQuest(int id) {
        Json json = new Json();
        return json.fromJson(Quest.class, Gdx.files.internal("quests/quest" + id + ".json"));
    }
}
