package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by betma on 09.05.2016.
 */
public class MapEditorScreen implements Screen, GestureDetector.GestureListener {
    private WidgetController widgetController;
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

    public MapEditorScreen(WidgetController widgetController, String fileName) {
        Gdx.app.log("MapEditorScreen::MapEditorScreen()", "-- widgetController:" + widgetController + " fileName:" + fileName);
        this.widgetController = widgetController;
        this.spriteBatch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.map = new TmxMapLoader().load(fileName);
        this.renderer = new IsometricTiledMapRenderer(map, spriteBatch);

        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void show() {
        Gdx.app.log("MapEditorScreen::show()", "-- Start!");
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("MapEditorScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        renderer.setView(camera);
        renderer.render();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("MapEditorScreen::render()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            WidgetController.getInstance().removeTopScreen();
        }
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
