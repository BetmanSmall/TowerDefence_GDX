package com.betmansmall.screens.menu;

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
import com.betmansmall.game.gameInterface.MapEditorInterface;
import com.betmansmall.game.gameLogic.MapEditorCameraController;
import com.betmansmall.maps.AutoTiler;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;

public class MapEditorScreen extends AbstractScreen {
    public AutoTiler autoTiler;
    public TmxMap tmxMap;
    public BatchTiledMapRenderer renderer;
    public MapEditorCameraController mapEditorCameraController;
    public MapEditorInterface mapEditorInterface;

    public MapEditorScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        autoTiler = new AutoTiler(Gdx.files.internal("maps/other/desert.tsx"));
        tmxMap = autoTiler.generateMap();
        if (tmxMap.isometric) {
            this.renderer = new IsometricTiledMapRenderer(tmxMap);
        } else {
            this.renderer = new OrthogonalTiledMapRenderer(tmxMap);
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
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
//        autoTiler.dispose();
        tmxMap.dispose();
        renderer.dispose();
        mapEditorCameraController.dispose();
        mapEditorInterface.dispose();
    }

    @Override
    public void show() {
        Logger.logFuncStart();
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
        inputMultiplexer.addProcessor(mapEditorInterface);
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
        mapEditorInterface.getViewport().update(width, height, true);
        Logger.logDebug("New width:" + width + " height:" + height);
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
