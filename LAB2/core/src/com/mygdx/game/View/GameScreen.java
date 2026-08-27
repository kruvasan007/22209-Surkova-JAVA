package com.mygdx.game.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.mygdx.game.Controllers.Controller;
import com.mygdx.game.Model.Enum.PlayerAction;
import com.mygdx.game.Model.Quest.QuestTask;
import com.mygdx.game.Model.World;
import com.mygdx.game.Model.WorldObject.Hero;
import com.mygdx.game.Model.WorldObject.NonPlayerCharacter;
import com.mygdx.game.Model.WorldObject.OrderDistributionPoint;
import com.mygdx.game.Model.WorldObject.Vehicle;
import com.mygdx.game.Observers.Component;
import com.mygdx.game.Observers.ComponentObserver;
import com.mygdx.game.Renderer.TextureMapObjectRenderer;
import com.mygdx.game.Service.Managers.ResourceManager;
import com.mygdx.game.View.Observers.ViewObserver;

public class GameScreen extends BaseScreen implements Screen, InputProcessor, ComponentObserver {
    private float deltaTime = 0;
    private final TextureMapObjectRenderer textureMapObjectRenderer;
    private final OrthographicCamera camera;
    private final World world;
    private final Hero hero;
    private final OrderDistributionPoint distributionPoint;
    private final ResourceManager resourceManager;
    private final OrthogonalTiledMapRenderer renderer;
    private final Controller controller;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private boolean isOrderVisible = false;
    private boolean isDialogOpen = false;
    private boolean isFPressed = false;

    public GameScreen(ResourceManager resourceManager) {
        super(resourceManager);
        this.resourceManager = resourceManager;
        controller = new Controller();
        this.world = controller.getWorld();
        this.hero = world.getHero();
        this.distributionPoint = world.getDistributionPoint();
        this.renderer = new OrthogonalTiledMapRenderer(world.getTiledMap());
        this.camera = new OrthographicCamera();

        batch = new SpriteBatch();
        stage = new Stage();
        cameraResize(280, 280);
        hero.moveHero(camera.viewportWidth / 2, camera.viewportHeight / 2);
        camera.position.x = hero.getX();
        camera.position.y = hero.getY();
        textureMapObjectRenderer = new TextureMapObjectRenderer(world.getTiledMap(), batch);
        shapeRenderer = new ShapeRenderer();

        controller.addObserver(this);
        controller.getQuestController().addObserver(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    public void sendMessage(Component.MESSAGE messageType, String... args) {
        String fullMessage = messageType.toString();
        for (String string : args) {
            fullMessage += Component.MESSAGE_TOKEN + string;
        }
        controller.receiveMessage(fullMessage);
        controller.getQuestController().receiveMessage(fullMessage);
    }

    @Override
    public void onNotify(String value, ComponentObserver.ComponentEvent event) {
        switch (event) {
            case GET_NEW_TASK -> createTaskDialog();
            case MOVE_ORDER -> isOrderVisible = world.getOrderPoint().isVisible();
            case START_TASK -> isOrderVisible = true;
            case COMPLETE_TASK -> isOrderVisible = false;
            case THANKS -> createThanksDialog(value, false);
            case END_GAME -> createThanksDialog(value, true);
            default -> {
            }
        }
    }

    public void createThanksDialog(String value, boolean isFinal) {
        if (!isDialogOpen) {
            Gdx.input.setInputProcessor(stage);
            Dialog dialog = new Dialog("Спасибо!", ResourceManager.skin, "dialog") {
                @Override
                protected void result(Object object) {
                    isDialogOpen = false;
                    Gdx.input.setInputProcessor(GameScreen.this);
                    if (isFinal) {
                        GameScreen.this.notify("End the game", ViewObserver.ViewEvent.END_GAME);
                    }
                }
            };
            dialog.text(isFinal ? "Все задания выполнены. Ты легенда доставки!" : getThanksText(value));
            dialog.button(isFinal ? "Завершить" : "Дальше", true);
            dialog.setScaleY(1.75f);
            dialog.setScaleX(1.15f);
            dialog.show(stage);
            isDialogOpen = true;
        }
    }

    private String getThanksText(String value) {
        return switch (value) {
            case "1" -> "Коробка спасена! Клиент счастлив, менеджер выдохнул.";
            case "2" -> "Потерянная посылка вернулась домой. Отличный розыск!";
            case "3" -> "Подозрительный тип больше не мешает маршрутам. Чистая работа!";
            default -> "Заказ закрыт красиво. Продолжаем в том же духе!";
        };
    }

    public void createTaskDialog() {
        QuestTask questTask = controller.getQuestController().getCurrentTask();
        if (!isDialogOpen) {
            Gdx.input.setInputProcessor(stage);
            Dialog dialog = new Dialog("Новое задание", ResourceManager.skin, "dialog") {
                @Override
                protected void result(Object object) {
                    if (object.toString().equals("false")) {
                        isDialogOpen = false;
                        Gdx.input.setInputProcessor(GameScreen.this);
                    } else if (object.toString().equals("true")) {
                        sendMessage(Component.MESSAGE.START_TASK, "Start");
                        if (questTask.getQuestType() == QuestTask.QuestType.DELIVERY) {
                            world.createObject(questTask.getQuestPoint(), questTask.getOrderTexturePath());
                            isOrderVisible = true;
                        }
                        isDialogOpen = false;
                        Gdx.input.setInputProcessor(GameScreen.this);
                    }
                }
            };
            dialog.text(questTask.getTaskPhrase());
            dialog.button("Нет", false);
            dialog.button("Да", true);
            dialog.setScaleY(1.75f);
            dialog.setScaleX(1.15f);
            dialog.show(stage);
            isDialogOpen = true;
        }
    }

    public void moveCamera() {
        if (camera.position.x - camera.viewportWidth / 2 >= 0 &&
                camera.position.x + camera.viewportWidth / 2 <= world.getMapWidth() &&
                camera.position.y + camera.viewportHeight / 2 <= world.getMapHeight() &&
                camera.position.y - camera.viewportHeight / 2 >= 0) {
            float lerp = 0.1f;
            Vector3 position = camera.position;
            float deltaTime = 0.5f;
            position.x += (hero.getX() - position.x + 32) * lerp * deltaTime;
            position.y += (hero.getY() - position.y + 32) * lerp * deltaTime;
            if (camera.position.x - camera.viewportWidth / 2 < 0) position.x = camera.viewportWidth / 2;
            if (camera.position.x + camera.viewportWidth / 2 > world.getMapWidth())
                position.x = world.getMapWidth() - camera.viewportWidth / 2;
            if (camera.position.y + camera.viewportHeight / 2 > world.getMapHeight())
                position.y = world.getMapHeight() - camera.viewportHeight / 2;
            if (camera.position.y - camera.viewportHeight / 2 < 0) position.y = camera.viewportHeight / 2;
        }
        camera.update();
    }


    private void cameraResize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.setView(camera);
        renderer.render();

        controller.renderIteration();
        renderTargetHighlight();

        batch.begin();
        renderMapObjectsBehindHero();
        renderVehicles();
        if (hero.isVisible()) {
            batch.draw(hero.getSprite(), hero.getX(), hero.getY());
        }
        Frustum camFrustum = camera.frustum;
        for (NonPlayerCharacter nps : world.getNpcList()) {
            if (isVisible(camFrustum, nps.getX(), nps.getY(), nps.getWidth(), nps.getHeight())) {
                batch.draw(nps.getSprite(), nps.getX(), nps.getY());
            }
        }

        if (isOrderVisible)
            world.getOrderPoint().getSprite().draw(batch);

        batch.draw(distributionPoint.getSprite(), distributionPoint.getX(), distributionPoint.getY());

        resourceManager.font.draw(batch, distributionPoint.getLabel().getLabel(), distributionPoint.getLabel().getX(), distributionPoint.getLabel().getY());

        renderMapObjectsInFrontOfHero();
        renderVehicleHint();
        renderQuestPointHint();
        renderControlHints();
        batch.setProjectionMatrix(camera.combined);
        batch.end();

        stage.act();
        stage.draw();

        world.getMiniMap().update(hero.getX(), hero.getY(), camera.viewportWidth, camera.viewportHeight);
        world.getMiniMap().render();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        moveCamera();
        checkInput();
    }

    private void renderMapObjectsBehindHero() {
        for (MapObject obj : world.getTiledMap().getLayers().get("objLayer").getObjects()) {
            if (obj.getProperties().get("y", Float.class) < hero.getY()) {
                textureMapObjectRenderer.renderObject(obj);
            }
        }
    }

    private void renderVehicles() {
        for (Vehicle vehicle : world.getVehicleList()) {
            vehicle.getSprite().draw(batch);
        }
    }

    private void renderVehicleHint() {
        Vehicle nearestVehicle = controller.getNearestVehicle();
        if (!controller.isDriving() && nearestVehicle != null) {
            resourceManager.font.draw(batch, "F: сесть", nearestVehicle.getX() + 12, nearestVehicle.getY() + nearestVehicle.getHeight() + 4);
        }
    }

    private void renderQuestPointHint() {
        if (controller.isQuestPointAvailable()) {
            resourceManager.font.draw(batch, "E: задание", distributionPoint.getX() - 4, distributionPoint.getY() + distributionPoint.getHeight() + 4);
        }
    }

    private void renderControlHints() {
        float x = camera.position.x + camera.viewportWidth / 2 - 86;
        float y = camera.position.y - camera.viewportHeight / 2 + 34;
        resourceManager.font.draw(batch, "E: удар", x, y);
        if (controller.isDriving()) {
            resourceManager.font.draw(batch, "F: выйти", x, y + 18);
        }
    }

    private void renderMapObjectsInFrontOfHero() {
        for (MapObject obj : world.getTiledMap().getLayers().get("objLayer").getObjects()) {
            if (obj.getProperties().get("y", Float.class) >= hero.getY()) {
                textureMapObjectRenderer.renderObject(obj);
            }
        }
    }

    private boolean isVisible(Frustum camFrustum, float x, float y, float width, float height) {
        return camFrustum.pointInFrustum(x, y, 0)
                || camFrustum.pointInFrustum(x + width, y, 0)
                || camFrustum.pointInFrustum(x + width, y + height, 0)
                || camFrustum.pointInFrustum(x, y + height, 0);
    }

    private void renderTargetHighlight() {
        QuestTask currentTask = controller.getQuestController().getCurrentTask();
        if (!controller.getQuestController().isTaskStarted()
                || currentTask.getQuestType() != QuestTask.QuestType.KILL) {
            return;
        }

        int targetIndex = currentTask.getQuestPoint().getX();
        if (targetIndex >= world.getNpcList().size()) {
            return;
        }

        NonPlayerCharacter target = world.getNpcList().get(targetIndex);
        if (!target.isAlive()) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2, 40);
        shapeRenderer.end();
    }

    private void checkInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            if (!isFPressed) {
                controller.movePlayer(PlayerAction.EnterVehicle);
                isFPressed = true;
            }
        } else {
            isFPressed = false;
        }

        deltaTime += Gdx.graphics.getDeltaTime();
        float FREQUENCY_FRAME_CHANGE = 0.1f;
        if (deltaTime > FREQUENCY_FRAME_CHANGE) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    controller.movePlayer(PlayerAction.LeftRun);
                else
                    controller.movePlayer(PlayerAction.LeftWalk);
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    controller.movePlayer(PlayerAction.RightRun);
                else
                    controller.movePlayer(PlayerAction.RightWalk);
            } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                controller.movePlayer(PlayerAction.UpWalk);
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                controller.movePlayer(PlayerAction.DownWalk);
            } else if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                controller.movePlayer(PlayerAction.Action);
            } else
                controller.movePlayer(PlayerAction.Wait);
            deltaTime = 0;
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
        cameraResize(width / 4, height / 4);
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        shapeRenderer.dispose();
        world.getMiniMap().dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char keycode) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
