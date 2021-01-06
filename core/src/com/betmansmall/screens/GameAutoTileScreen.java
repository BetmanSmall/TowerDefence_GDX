package com.betmansmall.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.betmansmall.GameMaster;
import com.betmansmall.game.gameLogic.MapEditorCameraController;
import com.betmansmall.maps.AutoTiler;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;

public class GameAutoTileScreen extends AbstractScreen {
    public AutoTiler autoTiler;
    public TmxMap tmxMap;
    public BatchTiledMapRenderer renderer;
    public MapEditorCameraController mapEditorCameraController;

    public GameAutoTileScreen(GameMaster game) {
        super(game);

        autoTiler = new AutoTiler(Gdx.files.internal("maps/other/desert.tsx"));
        tmxMap = autoTiler.generateMap();
        if (tmxMap.isometric) {
            renderer = new IsometricTiledMapRenderer(tmxMap);
        } else {
            renderer = new OrthogonalTiledMapRenderer(tmxMap);
        }
        mapEditorCameraController = new MapEditorCameraController(this);
    }

    @Override
    public void dispose() {
        super.dispose();
//        autoTiler.dispose();
        tmxMap.dispose();
        renderer.dispose();
        mapEditorCameraController.dispose();
    }

    @Override
    public void show() {
        super.show();

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean longPress(float x, float y) {
                Logger.logDebug("x:" + x, "y:" + y);
                while (true) {
                    if (autoTiler.generateMap(gameMaster.tileSetsFileHandles.random()) != null) {
                        if (tmxMap.isometric) {
                            renderer = new IsometricTiledMapRenderer(tmxMap);
                        } else {
                            renderer = new OrthogonalTiledMapRenderer(tmxMap);
                        }
                        break;
                    }
                }
                return super.longPress(x, y);
            }
        }));
        inputMultiplexer.addProcessor(mapEditorCameraController);
        inputMultiplexer.addProcessor(new GestureDetector(mapEditorCameraController));
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.RIGHT) {
                    autoTiler.generateMap();
                }
                Logger.logDebug(mapEditorCameraController.toString());
                return true;
            }
            @Override
            public boolean keyDown(int keycode) {
                if (Input.Keys.PLUS == keycode) {
                    autoTiler.setTimeSleep(true);
                } else if (Input.Keys.MINUS == keycode) {
                    autoTiler.setTimeSleep(false);
                }
                return super.keyDown(keycode);
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapEditorCameraController.update(delta);
        renderer.setView(mapEditorCameraController.camera);
        renderer.render();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.ESCAPE);");
            gameMaster.removeTopScreen();
        }

        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mapEditorCameraController.camera.viewportHeight = height;
        mapEditorCameraController.camera.viewportWidth = width;
        mapEditorCameraController.camera.update();
        Logger.logDebug("New width:" + width + " height:" + height);
    }
}
