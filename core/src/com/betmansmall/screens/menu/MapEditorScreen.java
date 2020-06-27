package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayer;
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
import com.betmansmall.game.gameInterface.TestListView;
import com.betmansmall.game.gameLogic.MapEditorCameraController;
import com.betmansmall.maps.AutoTiler;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.maps.TilesetConfig;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.ArrayList;

public class MapEditorScreen extends AbstractScreen {
    private MapEditorCameraController mapEditorCameraController;

    private Stage stage;
    private SpriteBatch spriteBatch;



    public TmxMap map;
    private IsometricTiledMapRenderer renderer;
    private VisCheckBox layerVisibleCheckBox;
    private VisSelectBox<String> selectMapsBox, selectTileBox, mapLayersBox;
    Array<String> arrName = new Array<String>();

    public MapEditorScreen(GameMaster gameMaster, String fileName) {
        super(gameMaster);
        Gdx.app.log("MapEditorScreen::MapEditorScreen()", "-- gameMaster:" + gameMaster + " fileName:" + fileName);
        this.stage = new Stage(new ScreenViewport());
        //stage.setDebugAll(true);
        this.spriteBatch = new SpriteBatch();
        mapEditorCameraController = new MapEditorCameraController();
        Gdx.input.setInputProcessor(new GestureDetector(mapEditorCameraController));


        Logger.logDebug("fileName:" + fileName);
        this.map = (TmxMap) new MapLoader().load(fileName);
        this.renderer = new IsometricTiledMapRenderer(map, spriteBatch);

        Table rootTable = new VisTable();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
        stage.addActor(new TestListView(this));

        TextButton backButton = new VisTextButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MapEditorScreen::backButton::changed()", "-- backButton.isChecked():" + backButton.isChecked());
                gameMaster.removeTopScreen();
            }
        });
        rootTable.add(backButton).left().top().row();

//        TextButton generateBtn = new VisTextButton("GEN");
//        generateBtn.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Logger.logDebug(generateBtn.isChecked()+"");
//                map = (TmxMap) new AutoTiler(16, 16, new TilesetConfig()).generateMap();
//                renderer = new IsometricTiledMapRenderer(map, spriteBatch);
//                camera.position.set((map.width*map.tileWidth)/2f, 0, 0f);
//                camera.update();
//                updateTileList();
//            }
//        });
//        rootTable.add(generateBtn).left().top().row();
        Table elemTable = new VisTable();
        rootTable.add(elemTable).expand().left().bottom();

        selectTileBox = new VisSelectBox<>();
        elemTable.add(selectTileBox).colspan(2).row();

        mapLayersBox = new VisSelectBox<>();
        mapLayersBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                layerVisibleCheckBox.setChecked(map.getLayers().get(mapLayersBox.getSelected()).isVisible());
            }
        });
        elemTable.add(mapLayersBox).right();

        layerVisibleCheckBox = new VisCheckBox(":");
        layerVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                map.getLayers().get(mapLayersBox.getSelected()).setVisible(layerVisibleCheckBox.isChecked());
            }
        });
        elemTable.add(layerVisibleCheckBox).left().row();

        selectMapsBox = new VisSelectBox<>();
        updateTileList();
        selectTileBox.setSelected(fileName);
        selectMapsBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logDebug(selectMapsBox.getSelected());
                map = (TmxMap) new MapLoader().load(selectMapsBox.getSelected());
                renderer = new IsometricTiledMapRenderer(map, spriteBatch);
                mapEditorCameraController.camera.position.set((map.width*map.tileWidth)/2f, 0, 0f);
                mapEditorCameraController.camera.update();
                updateTileList();
            }
        });
        elemTable.add(selectMapsBox).colspan(2);

        updateTileList();

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
//        inputMultiplexer.addProcessor(new GestureDetector(this));
//        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void updateTileList() {
        selectTileBox.setItems(map.getTiledMapTilesIds());
        for (MapLayer mapLayer : map.getLayers()) {
            Logger.logDebug("mapLayer.getName():" + mapLayer.getName());
            arrName.add(mapLayer.getName());
        }
        String selectedLayer = mapLayersBox.getSelected();
        Logger.logDebug("selectedLayer:" + selectedLayer);
        mapLayersBox.setItems(map.getMapLayersNames());
        layerVisibleCheckBox.setChecked(map.getLayers().get(mapLayersBox.getSelected()).isVisible());
        String selectedMap = selectMapsBox.getSelected();
        Logger.logDebug("selectedMap:" + selectedMap);
        selectMapsBox.setItems(game.gameLevelMaps);
        if (game.gameLevelMaps.contains(selectedMap, false)) {
            selectMapsBox.setSelected(selectedMap);
        }
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

        mapEditorCameraController.camera.update();
        renderer.setView(mapEditorCameraController.camera);
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
        mapEditorCameraController.camera.viewportHeight = height;
        mapEditorCameraController.camera.viewportWidth = width;
        mapEditorCameraController.camera.update();
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
}
