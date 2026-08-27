package com.mygdx.game.Controllers;

import com.mygdx.game.Model.Enum.PlayerAction;
import com.mygdx.game.Model.World;
import com.mygdx.game.Model.WorldObject.Hero;
import com.mygdx.game.Model.WorldObject.Vehicle;
import com.mygdx.game.Observers.Component;
import com.mygdx.game.Observers.ComponentObject;
import com.mygdx.game.Observers.ComponentObserver;
import com.mygdx.game.Service.Managers.CollisionManager;

public class Controller extends ComponentObject implements Component {
    private final World world;
    private final Hero hero;
    private final CollisionManager collisionManager;
    private final QuestController questController;
    private final MoveController moveController;

    public Controller() {
        world = new World();
        hero = world.getHero();
        questController = new QuestController();
        moveController = new MoveController(world);
        collisionManager = new CollisionManager(world);
    }

    public void renderIteration() {
        moveController.moveNPC();
        checkQuestAction();
    }

    public boolean isQuestPointAvailable() {
        return collisionManager.checkCollision(hero, world.getDistributionPoint())
                && questController.isTasksAvailable();
    }

    public void tryGetNewTask() {
        if (isQuestPointAvailable()) {
            notify("Get new task", ComponentObserver.ComponentEvent.GET_NEW_TASK);
        }
    }

    public void checkQuestAction() {
        if (questController.isTaskStarted()) {
            switch (questController.getCurrentTask().getQuestType()) {
                case DELIVERY -> {
                    if (collisionManager.checkCollision(
                            world.getOrderPoint(), hero)) {
                        hero.setPickable();
                        world.getOrderPoint().setInvisible();
                        notify("Pick order", ComponentObserver.ComponentEvent.MOVE_ORDER);
                    } else if (hero.isPickSomething() && collisionManager.checkCollision(
                            world.getDistributionPoint(), hero)) {
                        hero.setUnpickable();
                        questController.setTaskDone();
                    }
                }
                case KILL -> {
                    if (!world.getNpcList()
                            .get(questController.getCurrentTask().getQuestPoint().getX()).isAlive()) {
                        questController.setTaskDone();
                    }
                }
                default ->
                        throw new IllegalStateException("Unexpected value: " + questController.getCurrentTask().getQuestType());
            }
        }
    }

    public World getWorld() {
        return world;
    }


    public void movePlayer(PlayerAction action) {
        switch (action) {
            case Talk -> moveController.pressedTalk();
            case Wait -> moveController.pressedCancel();
            case Punch -> moveController.pressedPunch();
            case EnterVehicle -> moveController.switchVehicle();
            case Action -> pressedAction();
            case UpWalk -> moveController.pressedUp();
            case DownWalk -> moveController.pressedDown();
            case LeftWalk -> moveController.pressedLeft();
            case RightWalk -> moveController.pressedRight();
            case LeftRun -> moveController.pressedLeftRun();
            case RightRun -> moveController.pressedRightRun();
        }
    }

    private void pressedAction() {
        if (isDriving()) {
            return;
        }
        if (isQuestPointAvailable()) {
            tryGetNewTask();
        } else {
            moveController.pressedPunch();
        }
    }

    public QuestController getQuestController() {
        return questController;
    }

    public boolean isDriving() {
        return moveController.isDriving();
    }

    public Vehicle getNearestVehicle() {
        return moveController.getNearestVehicle();
    }

    @Override
    public void receiveMessage(String message) {
        //later...
    }
}
