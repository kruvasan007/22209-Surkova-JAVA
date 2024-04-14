package com.mygdx.game.Model.Quest;

import java.util.List;

public class Quest {
    private String questTitle;
    private int questID;
    private int goldReward;
    private int xpReward;
    private boolean isQuestComplete;
    private List<QuestTask> questTasks;
    private int currentTaskId = 0;

    public String getQuestTitle() {
        return questTitle;
    }

    public boolean isQuestDone() {
        return isQuestComplete;
    }

    public QuestTask getCurrentTask() {
        if (currentTaskId == -1 || isQuestComplete) return null;
        return questTasks.get(currentTaskId);
    }


    public void setQuestTaskDone() {
        if (currentTaskId < questTasks.size()) {
            questTasks.get(currentTaskId).getTaskProperties().setTaskState(TaskProperties.TaskState.COMPLETE);
            currentTaskId++;
            if (currentTaskId == questTasks.size())
                isQuestComplete = true;
        }
    }
}
