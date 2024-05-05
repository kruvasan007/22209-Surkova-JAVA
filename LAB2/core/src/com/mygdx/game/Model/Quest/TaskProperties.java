package com.mygdx.game.Model.Quest;

public class TaskProperties {
    private TaskState TASK_STATE;
    private TargetType TARGET_TYPE;
    private TargetLocation TARGET_LOCATION;
    public TaskState getTaskState() {
        return TASK_STATE;
    }

    public void setTaskState(TaskState state) {
        TASK_STATE = state;
    }

    public TargetType getTargetType() {
        return TARGET_TYPE;
    }

    public TargetLocation getTargetLocation() {
        return TARGET_LOCATION;
    }

    public enum TargetType {
        BREAD,
        NPS
    }

    public enum TaskState {
        START,
        NONE,
        COMPLETE
    }

    public enum TargetLocation {
        MAIN_CAR
    }
}
