package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.screens.menu.MapEditorScreen;
import com.betmansmall.utils.FileChooserDialog;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import java.io.File;
import java.io.FileFilter;

public class MapEditorInterface extends GameInterface {
    private final MapEditorScreen mapEditorScreen;
    private final Stage stage;
    private final FileChooserDialog dialog;
    private VisLabel mapNameLabel;
    private VisTextButton chooiseMapButton;
    private VisTextButton exitButton;

    private VisCheckBox layerVisibleCheckBox;
    private VisSelectBox<String> selectMapsBox, selectTileBox, mapLayersBox;
    Array<String> arrName = new Array<String>();
    private TestListView testListView;

    public MapEditorInterface(MapEditorScreen _mapEditorScreen) {
        super();
        this.mapEditorScreen = _mapEditorScreen;
        this.stage = this;

        dialog = FileChooserDialog.createLoadDialog("LoadDialog", VisUI.getSkin(), Gdx.files.internal("maps/"));
//        dialog = FileChooserDialog.createSaveDialog("SaveDialog", VisUI.getSkin(), Gdx.files.internal("maps/"));
        dialog.setResultListener((success, result) -> {
            if (success) {
                Logger.logDebug("result.file().getPath():" + result.file().getPath());
//                    mapNaaeIsSelected = result.file().getName();
                mapNameLabel.setText("Map1:" + result.file().getName());
                Logger.logDebug("result.file().getName():" + result.file().getName());
                mapEditorScreen.gameMaster.addScreen(new MapEditorScreen(mapEditorScreen.gameMaster, result.file().getAbsolutePath()));
            }
            return true;
        });
        dialog.setFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getPath();
                return path.matches(".*(?:tmx)");
            }
        });

        Table rootTable = new VisTable();
        rootTable.setFillParent(true);
        addActor(rootTable);

        mapNameLabel = new VisLabel("Map: " + mapEditorScreen.tmxMap.mapPath);
        rootTable.add(mapNameLabel).align(Align.topRight).expand().row();

        chooiseMapButton = new VisTextButton("LoadMap");
        chooiseMapButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                dialog.show(stage);
            }
        });
        rootTable.add(chooiseMapButton).expandX().align(Align.right).row();

        exitButton = new VisTextButton("exit");
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                mapEditorScreen.gameMaster.removeTopScreen();
            }
        });
        rootTable.add(exitButton).expandX().align(Align.right).row();

        TextButton backButton = new VisTextButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logDebug("backButton");
                mapEditorScreen.gameMaster.removeTopScreen();
            }
        });
        rootTable.add(backButton).left().top().row();

        TextButton generateBtn = new VisTextButton("GEN");
        generateBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logDebug(generateBtn.isChecked()+"");
//                mapEditorScreen.tmxMap = (TmxMap) new AutoTiler(16, 16, Gdx.files.internal("utils/tileset.json")).generateMap();
//                mapEditorScreen.renderer = new IsometricTiledMapRenderer(tmxMap, spriteBatch);
//                mapEditorCameraController.camera.position.set((tmxMap.width* tmxMap.tileWidth)/2f, 0, 0f);
//                mapEditorCameraController.camera.update();
                updateTileList();
            }
        });
        rootTable.add(generateBtn).left().top().row();
        Table elemTable = new VisTable();
        rootTable.add(elemTable).expand().left().bottom();

        selectTileBox = new VisSelectBox<>();
        elemTable.add(selectTileBox).colspan(2).row();

        mapLayersBox = new VisSelectBox<>();
        mapLayersBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                layerVisibleCheckBox.setChecked(mapEditorScreen.tmxMap.getLayers().get(mapLayersBox.getSelected()).isVisible());
            }
        });
        elemTable.add(mapLayersBox).right();

        layerVisibleCheckBox = new VisCheckBox(":");
        layerVisibleCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapEditorScreen.tmxMap.getLayers().get(mapLayersBox.getSelected()).setVisible(layerVisibleCheckBox.isChecked());
            }
        });
        elemTable.add(layerVisibleCheckBox).left().row();

        selectMapsBox = new VisSelectBox<>();
        updateTileList();
        selectTileBox.setSelected(mapEditorScreen.tmxMap.mapPath);
        selectMapsBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logDebug(selectMapsBox.getSelected());
                mapEditorScreen.tmxMap = (TmxMap) new MapLoader().load(selectMapsBox.getSelected());
                mapEditorScreen.renderer = new IsometricTiledMapRenderer(mapEditorScreen.tmxMap, mapEditorScreen.spriteBatch);
                mapEditorScreen.mapEditorCameraController.camera.position.set((mapEditorScreen.tmxMap.width*mapEditorScreen.tmxMap.tileWidth)/2f, 0, 0f);
                mapEditorScreen.mapEditorCameraController.camera.update();
                updateTileList();
            }
        });
        elemTable.add(selectMapsBox).colspan(2);

        updateTileList();
    }

    public void updateTileList() {
        selectTileBox.setItems(mapEditorScreen.tmxMap.getTiledMapTilesIds());
        for (MapLayer mapLayer : mapEditorScreen.tmxMap.getLayers()) {
            Logger.logDebug("mapLayer.getName():" + mapLayer.getName());
            arrName.add(mapLayer.getName());
        }
        String selectedLayer = mapLayersBox.getSelected();
        Logger.logDebug("selectedLayer:" + selectedLayer);
        mapLayersBox.setItems(mapEditorScreen.tmxMap.getMapLayersNames());
        layerVisibleCheckBox.setChecked(mapEditorScreen.tmxMap.getLayers().get(mapLayersBox.getSelected()).isVisible());
        String selectedMap = selectMapsBox.getSelected();
        Logger.logDebug("selectedMap:" + selectedMap);
        selectMapsBox.setItems(mapEditorScreen.gameMaster.gameLevelMaps);
        if (mapEditorScreen.gameMaster.gameLevelMaps.contains(selectedMap, false)) {
            selectMapsBox.setSelected(selectedMap);
        }
        float testListViewPosX = -1, testListViewPosY = -1;
        if (testListView != null) {
            testListViewPosX = testListView.getX();
            testListViewPosY = testListView.getY();
            testListView.remove();
        }
        testListView = new TestListView(mapEditorScreen);
        if (testListViewPosX != -1 && testListViewPosY != -1) {
            testListView.setPosition(testListViewPosX, testListViewPosY);
        }
        addActor(testListView);
    }
}
