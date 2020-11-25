package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.GameMaster;
import com.betmansmall.game.gameInterface.MapEditorInterface;
import com.betmansmall.game.gameInterface.TestListView;
import com.betmansmall.game.gameLogic.MapEditorCameraController;
import com.betmansmall.maps.AutoTiler;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class MapEditorScreen extends AbstractScreen {
    public TmxMap tmxMap;
    public MapEditorInterface mapEditorInterface;
    public MapEditorCameraController mapEditorCameraController;

    public SpriteBatch spriteBatch;
    public BatchTiledMapRenderer renderer;

    public MapEditorScreen(GameMaster gameMaster, String mapPath) {
        super(gameMaster);
        Logger.logFuncStart("gameMaster:" + gameMaster + " mapPath:" + mapPath);
//        this.tmxMap = (TmxMap) new MapLoader().load(mapPath);
        this.tmxMap = new TmxMap(new TmxMapLoader().load(mapPath), mapPath);
        if (tmxMap.isometric) {
            this.renderer = new IsometricTiledMapRenderer(tmxMap, spriteBatch);
        } else {
            this.renderer = new OrthogonalTiledMapRenderer(tmxMap, spriteBatch);
        }

        mapEditorInterface = new MapEditorInterface(this);
//        mapEditorInterface.setDebugAll(true);
        mapEditorCameraController = new MapEditorCameraController(this);

        this.spriteBatch = new SpriteBatch();
        this.renderer = new IsometricTiledMapRenderer(tmxMap, spriteBatch);
    }

    @Override
    public void show() {
        Logger.logFuncStart();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(mapEditorInterface);
        inputMultiplexer.addProcessor(mapEditorCameraController);
        inputMultiplexer.addProcessor(new GestureDetector(mapEditorCameraController));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
//      Logger.logDebug("delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapEditorCameraController.update(delta);
        renderer.setView(mapEditorCameraController.camera);
        renderer.render();
        mapEditorInterface.act();
        mapEditorInterface.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.BACK || Input.Keys.ESCAPE);");
            gameMaster.removeTopScreen();
        }
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
}
