package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.betmansmall.maps.TmxMap;
import com.betmansmall.render.BasicRender;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;

public class MapEditorScreen extends AbstractScreen {
    public AutoTiler autoTiler;
    public TmxMap tmxMap;

    public MapEditorCameraController cameraController;
    public BatchTiledMapRenderer renderer;
    public BasicRender basicRender;
    public MapEditorInterface gameInterface;

    public MapEditorScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        autoTiler = new AutoTiler(Gdx.files.internal("maps/other/desert.tsx"));
        tmxMap = autoTiler.generateMap();

        gameInterface = new MapEditorInterface(this);
        gameInterface.setDebugUnderMouse(true);
        gameInterface.setDebugInvisible(true);
        gameInterface.setDebugParentUnderMouse(true);
        gameInterface.setDebugTableUnderMouse(true);

        cameraController = new MapEditorCameraController(this);

        if (tmxMap.isometric) {
            this.renderer = new IsometricTiledMapRenderer(tmxMap);
        } else {
            this.renderer = new OrthogonalTiledMapRenderer(tmxMap);
        }
        Logger.logDebug("renderer:" + renderer);
        this.basicRender = new BasicRender(cameraController);
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
//        autoTiler.dispose();
        tmxMap.dispose();
        cameraController.dispose();

        renderer.dispose();
        basicRender.dispose();

        gameInterface.dispose();
    }

    @Override
    public void show() {
        Logger.logFuncStart();
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
//        inputMultiplexer.addProcessor(new GestureDetector(new GestureDetector.GestureAdapter() {
//            @Override
//            public boolean longPress(float x, float y) {
//                Logger.logDebug("x:" + x, "y:" + y);
//                while (true) {
//                    if (autoTiler.generateMap(gameMaster.tileSetsFileHandles.random()) != null) {
//                        if (tmxMap.isometric) {
//                            renderer = new IsometricTiledMapRenderer(tmxMap);
//                        } else {
//                            renderer = new OrthogonalTiledMapRenderer(tmxMap);
//                        }
//                        break;
//                    }
//                }
//                return super.longPress(x, y);
//            }
//        }));
        inputMultiplexer.addProcessor(new GestureDetector(gameInterface));
        inputMultiplexer.addProcessor(gameInterface);
        inputMultiplexer.addProcessor(cameraController);
        inputMultiplexer.addProcessor(new GestureDetector(cameraController));
//        inputMultiplexer.addProcessor(new InputAdapter() {
//            @Override
//            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//                if (button == Input.Buttons.RIGHT) {
//                    autoTiler.generateMap();
//                }
////                Logger.logDebug(mapEditorCameraController.toString());
//                return true;
//            }
//            @Override
//            public boolean keyDown(int keycode) {
//                if (Input.Keys.PLUS == keycode) {
//                    autoTiler.setTimeSleep(true);
//                } else if (Input.Keys.MINUS == keycode) {
//                    autoTiler.setTimeSleep(false);
//                }
//                return super.keyDown(keycode);
//            }
//        });
        Gdx.input.setInputProcessor(inputMultiplexer);
//        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraController.update(delta);

        renderer.setView(cameraController.camera);
        renderer.render();
        basicRender.render();

        gameInterface.act();
        gameInterface.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.ESCAPE);");
            gameMaster.removeTopScreen();
        }

        cameraController.inputHandler(delta);
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (gameInterface != null) {
            gameInterface.resize(width, height);
        }
        if (cameraController != null) {
            cameraController.camera.viewportWidth = width;
            cameraController.camera.viewportHeight = height;
            cameraController.camera.update();
        }
        Logger.logDebug("New width:" + width + " height:" + height);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MapEditorScreen{");
        sb.append("tmxMap=").append(tmxMap);
        sb.append(", gameInterface=").append(gameInterface);
        sb.append(", cameraController=").append(cameraController);
        sb.append('}');
        return sb.toString();
    }
}
