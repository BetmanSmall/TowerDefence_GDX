package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.utils.AbstractScreen;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * Created by betma on 09.05.2016.
 */
public class MapEditorScreen extends AbstractScreen implements GestureDetector.GestureListener {
    private Stage stage;
    private SpriteBatch spriteBatch;

    private static final float MAX_ZOOM = 50f; //max size
    private static final float MIN_ZOOM = 0.2f; // 2x zoom
    private float initialScale = 2f;
//    private float MAX_DESTINATION_X = 0f;
//    private float MAX_DESTINATION_Y = 0f;
//    private BitmapFont bitmapFont = new BitmapFont();

    private OrthographicCamera camera;

    private TiledMap map;
    private IsometricTiledMapRenderer renderer;

    public MapEditorScreen(GameMaster gameMaster, String fileName) {
        super(gameMaster);
        Gdx.app.log("MapEditorScreen::MapEditorScreen()", "-- gameMaster:" + gameMaster + " fileName:" + fileName);
        this.stage = new Stage(new ScreenViewport());
        stage.setDebugAll(true);
        this.spriteBatch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.map = new TmxMapLoader().load(fileName);
        this.renderer = new IsometricTiledMapRenderer(map, spriteBatch);

        Table rootTable = new VisTable();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        VisSelectBox<String> selectBox = new VisSelectBox<>();
        Table elemTable = new VisTable();
        Array<String> sbList = new Array<>();
        sbList.add("maps/3dArena0.tmx");
        sbList.add("maps/arenaEmpty.tmx");
        sbList.add("maps/arena0.tmx");
        sbList.add("maps/randomMap.tmx");
        sbList.add("maps/island.tmx");
        sbList.add("maps/arena1.tmx");
        sbList.add("maps/arena2.tmx");
        sbList.add("maps/old/arena3.tmx");
        sbList.add("maps/arena4.tmx");
        sbList.add("maps/arena4_1.tmx");
        sbList.add("maps/sample.tmx");
        sbList.add("maps/desert.tmx");
        sbList.add("maps/summer.tmx");
        sbList.add("maps/winter.tmx");
        sbList.add("maps/old/NoNameMap.tmx");
        selectBox.setItems(sbList);
        selectBox.setSelected(sbList.first());

        rootTable.add(elemTable).expand().right().bottom();
        TextButton loadButton = new VisTextButton("Load Map");
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MapEditorScreen::loadButton::changed()", "-- backButton.isChecked():" + loadButton.isChecked());
                map = new TmxMapLoader().load(selectBox.getSelected());
                renderer = new IsometricTiledMapRenderer(map, spriteBatch);
            }
        });
        TextButton backButton = new VisTextButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MapEditorScreen::backButton::changed()", "-- backButton.isChecked():" + backButton.isChecked());
                gameMaster.removeTopScreen();
            }
        });
        elemTable.add(selectBox).row();
        elemTable.add(loadButton);
        elemTable.add(backButton);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new GestureDetector(this));
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void show() {
        Gdx.app.log("MapEditorScreen::show()", "-- Start!");
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("MapEditorScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        renderer.setView(camera);
        renderer.render();
        stage.act();
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.log("MapEditorScreen::render()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.ESCAPE);");
            game.removeTopScreen();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
        stage.getViewport().update(width, height, true);
        Gdx.app.log("MapEditorScreen::resize()", "-- New width:" + width + " height:" + height);
    }

    @Override
    public void pause() {
        Gdx.app.log("MapEditorScreen::pause()", "-- Start!");
    }

    @Override
    public void resume() {
        Gdx.app.log("MapEditorScreen::resume()", "-- Start!");
    }

    @Override
    public void hide() {
        Gdx.app.log("MapEditorScreen::hide()", "-- Start!");
    }

    @Override
    public void dispose() {
        Gdx.app.log("MapEditorScreen::dispose()", "-- Start!");
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Gdx.app.log("MapEditorScreen::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        initialScale = camera.zoom;
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("MapEditorScreen::touchDown()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        Gdx.app.log("MapEditorScreen::longPress()", "-- x:" + x + " y:" + y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("MapEditorScreen::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY + " button:" + button);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("MapEditorScreen::isPanning()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
//        if (camera.position.x + -deltaX * camera.zoom < MAX_DESTINATION_X && camera.position.x + -deltaX * camera.zoom > 0)
        camera.position.add(-deltaX * camera.zoom, 0, 0);
//        if (Math.abs(camera.position.y + deltaY * camera.zoom) < MAX_DESTINATION_Y)
        camera.position.add(0, deltaY * camera.zoom, 0);
        camera.update();
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("MapEditorScreen::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        Gdx.app.log("MapEditorScreen::zoom()", "-- initialDistance:" + initialDistance + " distance:" + distance);
        float ratio = initialDistance / distance;
        float newZoom = initialScale * ratio;
        if (newZoom < MAX_ZOOM && newZoom > MIN_ZOOM) {
            camera.zoom = newZoom;
        }
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        Gdx.app.log("MapEditorScreen::pinch()", "-- initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
