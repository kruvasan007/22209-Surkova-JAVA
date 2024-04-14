package com.mygdx.game.View;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.mygdx.game.Observers.Component;
import com.mygdx.game.Observers.ComponentObserver;
import com.mygdx.game.Renderer.TextureMapObjectRenderer;
import com.mygdx.game.Service.Managers.ResourceManager;

public class GameScreen extends BaseScreen implements Screen, InputProcessor, ComponentObserver {
    private float deltaTime = 0;
    private TextureMapObjectRenderer textureMapObjectRenderer;
    private final OrthographicCamera camera;
    private Dialog dialog;
    private final World world;
    private final Hero hero;
    private final OrderDistributionPoint distributionPoint;
    private final ResourceManager resourceManager;

    private final OrthogonalTiledMapRenderer renderer;
    private Controller controller;
    private SpriteBatch batch;
    private boolean isOrderVisible = false;
    private boolean isDialogOpen = false;

    public GameScreen(Game game, ResourceManager resourceManager) {
        super(game, resourceManager);
        this.resourceManager = resourceManager;
        controller = new Controller();
        this.world = controller.getWorld();
        this.hero = world.getHero();
        this.distributionPoint = world.getDistributionPoint();
        this.renderer = new OrthogonalTiledMapRenderer(world.getTiledMap());
        this.camera = new OrthographicCamera();

        batch = new SpriteBatch();
        stage = new Stage();
        cameraResize();
        hero.moveHero(camera.viewportWidth / 2, camera.viewportHeight / 2);
        textureMapObjectRenderer = new TextureMapObjectRenderer(world.getTiledMap(), batch);

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
            case END_GAME -> gdxGame.setScreen(new EndGameScreen(gdxGame, resourceManager));
            default -> {
            }
        }
    }

    public void createTaskDialog() {
        QuestTask questTask = controller.getQuestController().getCurrentTask();
        if (!isDialogOpen) {
            Gdx.input.setInputProcessor(stage);
            dialog = new Dialog("New order", ResourceManager.skin, "dialog") {
                @Override
                protected void result(Object object) {
                    if (object.toString().equals("false")) {
                        isDialogOpen = false;
                    } else if (object.toString().equals("true")) {
                        sendMessage(Component.MESSAGE.START_TASK, "Start");
                        world.createObject(questTask.getQuestPoint());
                        isOrderVisible = true;
                        isDialogOpen = false;
                    }
                }
            };
            dialog.text(questTask.getTaskPhrase());
            dialog.button("No", false).setWidth(300);
            dialog.button("Yes", true).setWidth(300);
            dialog.setPosition(camera.viewportWidth, camera.viewportHeight);
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


    private void cameraResize() {
        camera.setToOrtho(false, 280, 280);
        camera.update();
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(camera);
        renderer.render();
        world.getMiniMap().update(hero.getX(), hero.getY(), camera.viewportWidth, camera.viewportHeight);
        world.getMiniMap().render();

        controller.renderIteration();

        batch.begin();
        batch.draw(hero.getSprite(), hero.getX(), hero.getY());
        Frustum camFrustum = camera.frustum;
        for (NonPlayerCharacter nps : world.getNpcList()) {
            if (camFrustum.pointInFrustum(nps.getX(), nps.getY(), 0)
                    || camFrustum.pointInFrustum(nps.getX() + nps.getWidth(), nps.getY(), 0)
                    || camFrustum.pointInFrustum(nps.getX() + nps.getWidth(), nps.getY() + nps.getHeight(), 0)
                    || camFrustum.pointInFrustum(nps.getX(), nps.getY() + nps.getHeight(), 0)) {
                batch.draw(nps.getSprite(), nps.getX(), nps.getY());
            }
        }

        if (isOrderVisible)
            batch.draw(world.getOrderPoint().getSprite(), world.getOrderPoint().getX(), world.getOrderPoint().getY());

        batch.draw(distributionPoint.getSprite(), distributionPoint.getX(), distributionPoint.getY());

        resourceManager.font.draw(batch, distributionPoint.getLabel().getLabel(), distributionPoint.getLabel().getX(), distributionPoint.getLabel().getY());

        for (MapObject obj : world.getTiledMap().getLayers().get("objLayer").getObjects())
            textureMapObjectRenderer.renderObject(obj);
        batch.setProjectionMatrix(camera.combined);
        batch.end();

        stage.act();
        stage.draw();

        moveCamera();
        checkInput();
    }

    private void checkInput() {
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
                controller.movePlayer(PlayerAction.Punch);
            } else
                controller.movePlayer(PlayerAction.Wait);
            deltaTime = 0;
        }
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        renderer.dispose();
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
