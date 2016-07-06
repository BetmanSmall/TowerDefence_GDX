package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * Created by betma on 09.05.2016.
 */
public class MapEditorScreen implements Screen, GestureDetector.GestureListener {
    private TowerDefence towerDefence;
    private SpriteBatch spriteBatch;

    private static final float MAX_ZOOM = 50f; //max size
    private static final float MIN_ZOOM = 0.2f; // 2x zoom
    private float MAX_DESTINATION_X = 0f;
    private float MAX_DESTINATION_Y = 0f;
//    private BitmapFont bitmapFont = new BitmapFont();

    private OrthographicCamera camera;

    private TiledMap map;
    private IsometricTiledMapRenderer renderer;

    public MapEditorScreen(TowerDefence towerDefence, String fileName) {
        this.towerDefence = towerDefence;
        this.spriteBatch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.map = new TmxMapLoader().load(fileName);
        this.renderer = new IsometricTiledMapRenderer(map, spriteBatch);

        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("MapEditorScreen::render()", "-- delta:" + delta);
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
        Gdx.app.log("MapEditorScreen::resize()", "-- New width:" + width + " height:" + height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("CameraController::pan()", " -- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
//        if (camera.position.x + -deltaX * camera.zoom < MAX_DESTINATION_X && camera.position.x + -deltaX * camera.zoom > 0)
            camera.position.add(-deltaX * camera.zoom, 0, 0);
//        if (Math.abs(camera.position.y + deltaY * camera.zoom) < MAX_DESTINATION_Y)
            camera.position.add(0, deltaY * camera.zoom, 0);
        camera.update();
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
