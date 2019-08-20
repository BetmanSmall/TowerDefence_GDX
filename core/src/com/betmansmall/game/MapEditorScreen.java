package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by betma on 09.05.2016.
 */
public class MapEditorScreen implements Screen, GestureDetector.GestureListener, InputProcessor {
    private WidgetController widgetController;
    private SpriteBatch spriteBatch;

    private float zoomMax = 5.0f;
    private float zoomMin = 0.1f;
    private float initialScale = 2f;
//    private float MAX_DESTINATION_X = 0f;
//    private float MAX_DESTINATION_Y = 0f;
//    private BitmapFont bitmapFont = new BitmapFont();

    private OrthographicCamera camera;

    private TiledMap map;
    private BatchTiledMapRenderer batchTiledMapRenderer;

    public MapEditorScreen(WidgetController widgetController, String mapPath) {
        Gdx.app.log("MapEditorScreen::MapEditorScreen()", "-- widgetController:" + widgetController + " mapPath:" + mapPath);
        this.widgetController = widgetController;
        this.spriteBatch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.map = new TmxMapLoader().load(mapPath);
        String orientation = map.getProperties().get("orientation", null);
        if (orientation != null) {
            if (orientation.equals("orthogonal")) {
                this.batchTiledMapRenderer = new OrthogonalTiledMapRenderer(map, spriteBatch);
            } else if (orientation.equals("isometric")) {
                this.batchTiledMapRenderer = new IsometricTiledMapRenderer(map, spriteBatch);
            } else {
                Gdx.app.error("MapEditorScreen::MapEditorScreen()", "-- orientation:" + orientation);
            }
        } else {
            Gdx.app.error("MapEditorScreen::MapEditorScreen()", "-- orientation:null");
        }
    }

    @Override
    public void show() {
        Gdx.app.log("MapEditorScreen::show()", "-- Start!");
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(new GestureDetector(this));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("MapEditorScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batchTiledMapRenderer.setView(camera);
        batchTiledMapRenderer.render();

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
        if (newZoom < zoomMax && newZoom > zoomMin) {
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
        Gdx.app.log("MapEditorScreen::pinchStop()", "--");
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("MapEditorScreen::keyDown()", "-- keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log("MapEditorScreen::keyUp()", "-- keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.log("MapEditorScreen::keyTyped()", "-- character:" + character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("MapEditorScreen::touchDown()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("MapEditorScreen::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Gdx.app.log("MapEditorScreen::touchDragged()", "-- screenX:" + screenX + " screenY:" + screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
//        Gdx.app.log("MapEditorScreen::mouseMoved()", "-- screenX:" + screenX + " screenY:" + screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        Gdx.app.log("MapEditorScreen::scrolled()", "-- amount:" + amount);
//        if (gameInterface.scrolled(amount)) {
//            return false;
//        }
        if (amount > 0) {
            if (camera.zoom <= zoomMax)
                camera.zoom += 0.1f;
        } else if (amount < 0) {
            if (camera.zoom >= zoomMin)
                camera.zoom -= 0.1f;
        }
        camera.update();
        Gdx.app.log("MapEditorScreen::scrolled()", "-- camera.zoom:" + camera.zoom);
        return false;
    }
}
