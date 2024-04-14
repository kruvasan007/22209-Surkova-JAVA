package com.mygdx.game.Model.Quest;

import com.mygdx.game.Model.Coords;
import com.mygdx.game.Model.WorldObject.OrderPoint;

public class QuestTask {

    public enum QuestType {
        DELIVERY,
        KILL
    }
    private TaskProperties taskProperties;
    private int id;
    private String taskPhrase;
    private QuestType questType;
    private Coords questPoint;

    public Coords getQuestPoint() {
        return questPoint;
    }

    public String getTaskPhrase() {
        return taskPhrase;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public boolean isTaskReady() {
        return taskProperties.getTaskState() == TaskProperties.TaskState.NONE;
    }

    public boolean isTaskStarted() {
        return taskProperties.getTaskState() == TaskProperties.TaskState.START;
    }
    public boolean isTaskDone() {
        return taskProperties.getTaskState() == TaskProperties.TaskState.COMPLETE;
    }

    public void setTaskDone() {
        taskProperties.setTaskState(TaskProperties.TaskState.COMPLETE);
    }

    public void setTaskStart() {
        taskProperties.setTaskState(TaskProperties.TaskState.START);
    }

    public TaskProperties getTaskProperties() {
        return taskProperties;
    }

}
