package com.mygdx.game.Controllers;

import com.mygdx.game.Model.Quest.Quest;
import com.mygdx.game.Model.Quest.QuestTask;
import com.mygdx.game.Observers.Component;
import com.mygdx.game.Observers.ComponentObject;
import com.mygdx.game.Observers.ComponentObserver;
import com.mygdx.game.Service.QuestBuilder;

public class QuestController extends ComponentObject implements Component {
    private final Quest currentQuest;
    private QuestTask currentTask;

    public QuestController() {
        QuestBuilder questBuilder = new QuestBuilder();
        currentQuest = questBuilder.createQuest(1); // пока в наличии один квест и 3 задания, поэтому final
        currentTask = currentQuest.getCurrentTask();
    }

    public boolean isTasksAvailable() {
        return !currentQuest.isQuestDone() && currentQuest.getCurrentTask().isTaskReady();
    }

    public QuestTask getCurrentTask() {
        return currentTask;
    }

    public boolean isTaskStarted() {
        if (currentTask != null) {
            return currentTask.isTaskStarted();
        }
        return false;
    }

    public void setTaskDone() {
        currentQuest.setQuestTaskDone();
        currentTask = currentQuest.getCurrentTask();
        notify("Complete task", ComponentObserver.ComponentEvent.COMPLETE_TASK);
    }

    @Override
    public void receiveMessage(String message) {
        String[] string = message.split(MESSAGE_TOKEN);

        if (string.length == 0) {
            return;
        }

        if (string[0].equalsIgnoreCase(MESSAGE.START_TASK.toString())) {
            currentTask.setTaskStart();
        }
    }
}
