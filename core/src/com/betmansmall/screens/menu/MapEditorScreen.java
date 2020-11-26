package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.betmansmall.GameMaster;
import com.betmansmall.game.gameInterface.MapEditorInterface;
import com.betmansmall.game.gameLogic.MapEditorCameraController;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;

public class MapEditorScreen extends AbstractScreen {
    public TmxMap tmxMap;

    public SpriteBatch spriteBatch;
    public BatchTiledMapRenderer renderer;

    public MapEditorCameraController mapEditorCameraController;
    public MapEditorInterface mapEditorInterface;

    public MapEditorScreen(GameMaster gameMaster, String mapPath) {
        super(gameMaster);
        Logger.logFuncStart("gameMaster:" + gameMaster + " mapPath:" + mapPath);
        this.tmxMap = (TmxMap) new MapLoader().load(mapPath);
        Logger.logDebug("tmxMap:" + tmxMap);

        this.spriteBatch = new SpriteBatch();
        if (tmxMap.isometric) {
            this.renderer = new IsometricTiledMapRenderer((TiledMap)tmxMap, spriteBatch);
        } else {
            this.renderer = new OrthogonalTiledMapRenderer(tmxMap, spriteBatch);
        }
        Logger.logDebug("renderer:" + renderer);

        mapEditorCameraController = new MapEditorCameraController(this);
        mapEditorInterface = new MapEditorInterface(this);
        mapEditorInterface.setDebugUnderMouse(true);
        mapEditorInterface.setDebugInvisible(true);
        mapEditorInterface.setDebugParentUnderMouse(true);
        mapEditorInterface.setDebugTableUnderMouse(true);
    }

    @Override
    public void show() {
        Logger.logFuncStart();
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(mapEditorInterface);
        inputMultiplexer.addProcessor(mapEditorCameraController);
        inputMultiplexer.addProcessor(new GestureDetector(mapEditorCameraController));
        Gdx.input.setInputProcessor(inputMultiplexer);
//        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapEditorCameraController.update(delta);
        renderer.setView(mapEditorCameraController.camera);
        renderer.render();
        mapEditorInterface.act();
        mapEditorInterface.draw();

        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        mapEditorCameraController.camera.viewportHeight = height;
        mapEditorCameraController.camera.viewportWidth = width;
        mapEditorCameraController.camera.update();
        mapEditorInterface.getViewport().update(width, height, true);
        Logger.logDebug("New width:" + width + " height:" + height);
    }

    @Override
    public void pause() {
        Logger.logDebug();
    }

    @Override
    public void resume() {
        Logger.logDebug();
    }

    @Override
    public void hide() {
        Logger.logDebug();
    }

    @Override
    public void dispose() {
        Logger.logDebug();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MapEditorScreen{");
        sb.append("tmxMap=").append(tmxMap);
        sb.append(", mapEditorInterface=").append(mapEditorInterface);
        sb.append(", mapEditorCameraController=").append(mapEditorCameraController);
        sb.append('}');
        return sb.toString();
    }
}
