package com.mygdx.game.Controllers;

import com.mygdx.game.Model.Enum.PlayerAction;
import com.mygdx.game.Model.World;
import com.mygdx.game.Model.WorldObject.Hero;
import com.mygdx.game.Observers.Component;
import com.mygdx.game.Observers.ComponentObject;
import com.mygdx.game.Observers.ComponentObserver;
import com.mygdx.game.Quest.QuestController;
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
        checkQuestPointSenderCollision();
    }

    public void checkQuestPointSenderCollision() {
        if (collisionManager.checkCollision(hero, world.getDistributionPoint()) && questController.isTasksAvailable()){
            notify("Get new task", ComponentObserver.ComponentEvent.GET_NEW_TASK);
        }
    }

    public void checkQuestAction() {
        if (questController.isTaskStarted()) {
            switch (questController.getCurrentTask().getQuestType()) {
                case DELIVERY : {
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
                    break;
                }
                case KILL : {
                    if (!world.getNpcList()
                            .get(questController.getCurrentTask().getQuestPoint().getX()).isAlive()) {
                        notify("End game", ComponentObserver.ComponentEvent.END_GAME);
                    }
                    break;
                }
                default : throw new IllegalStateException("Unexpected value: " + questController.getCurrentTask().getQuestType());
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
            case UpWalk -> moveController.pressedUp();
            case DownWalk -> moveController.pressedDown();
            case LeftWalk -> moveController.pressedLeft();
            case RightWalk -> moveController.pressedRight();
            case LeftRun -> moveController.pressedLeftRun();
            case RightRun -> moveController.pressedRightRun();
        }
    }

    public QuestController getQuestController() {
        return questController;
    }

    @Override
    public void receiveMessage(String message) {
        String[] string = message.split(MESSAGE_TOKEN);

        if (string.length == 0) {
            return;
        }
    }

    @Override
    public void dispose() {

    }
}
