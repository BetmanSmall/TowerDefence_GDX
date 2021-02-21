package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
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

    private VisLabel mapNameLabel;
    private VisTextButton chooseMapButton;
    private VisTextButton exitButton;

    private VisSelectBox<FileHandle> tileSetVisSelectBox;
    private VisCheckBox layerVisibleCheckBox;
    private VisSelectBox<String> selectMapsBox, selectTileBox, mapLayersBox;
    Array<String> arrName = new Array<String>();
    private TestListView testListView;

    private final FileChooserDialog dialog;

    public TileSelector tileSelector;

    public MapEditorInterface(MapEditorScreen _mapEditorScreen) {
        super();
        this.mapEditorScreen = _mapEditorScreen;
        this.mapEditorScreen.gameInterface = this; // wtf?
        this.stage = this;

        Table rootTable = new VisTable();
        rootTable.setFillParent(true);
        addActor(rootTable);

        mapNameLabel = new VisLabel("Map: " + mapEditorScreen.tmxMap.mapPath);
        rootTable.add(mapNameLabel).top().expand();

        tileSetVisSelectBox = new VisSelectBox<>();
        tileSetVisSelectBox.setItems(mapEditorScreen.gameMaster.tileSetsFileHandles);
        tileSetVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapEditorScreen.autoTiler.generateMap(tileSetVisSelectBox.getSelected());
            }
        });
        rootTable.add(tileSetVisSelectBox).top().right().expand().row();

        chooseMapButton = new VisTextButton("LoadMap");
        chooseMapButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                dialog.show(stage);
            }
        });
        rootTable.add(chooseMapButton).expandX().right().row();

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
//                updateTileList();
            }
        });
        rootTable.add(generateBtn).left().top().row();

        Table selectorsTable = new VisTable();
        selectorsTable.setFillParent(true);
        addActor(selectorsTable);

        tileSelector = new TileSelector(mapEditorScreen);
        selectorsTable.add(tileSelector).expand();

        Table elemTable = new VisTable();
        rootTable.add(elemTable).expand().left().top();

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

//        selectMapsBox = new VisSelectBox<>();
////        updateTileList();
//        selectMapsBox.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Logger.logDebug(selectMapsBox.getSelected());
//                mapEditorScreen.tmxMap = (TmxMap) new MapLoader().load(selectMapsBox.getSelected());
//                mapEditorScreen.renderer = new IsometricTiledMapRenderer(mapEditorScreen.tmxMap);
//                if (mapEditorScreen.mapEditorCameraController != null) {
//                    mapEditorScreen.mapEditorCameraController.camera.position.set((mapEditorScreen.tmxMap.width * mapEditorScreen.tmxMap.tileWidth) / 2f, 0, 0f);
//                    mapEditorScreen.mapEditorCameraController.camera.update();
//                }
//                updateTileList();
//            }
//        });
//        selectMapsBox.setSelected(mapEditorScreen.tmxMap.mapPath);
//        elemTable.add(selectMapsBox).colspan(2);
//        updateTileList();

        dialog = FileChooserDialog.createLoadDialog("LoadDialog", VisUI.getSkin(), Gdx.files.internal("maps/"));
//        dialog = FileChooserDialog.createSaveDialog("SaveDialog", VisUI.getSkin(), Gdx.files.internal("maps/"));
        dialog.setResultListener((success, result) -> {
            if (success) {
                Logger.logDebug("result.file().getPath():" + result.file().getPath());
//                    mapNaaeIsSelected = result.file().getName();
                mapNameLabel.setText("Map1:" + result.file().getName());
                Logger.logDebug("result.file().getName():" + result.file().getName());
                mapEditorScreen.gameMaster.addScreen(new MapEditorScreen(mapEditorScreen.gameMaster)); // result.file().getPath()
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
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

//    public void updateTileList() {
//        selectTileBox.setItems(mapEditorScreen.tmxMap.getTiledMapTilesIds());
//        for (MapLayer mapLayer : mapEditorScreen.tmxMap.getLayers()) {
//            arrName.add(mapLayer.getName());
//        }
//        mapLayersBox.setItems(mapEditorScreen.tmxMap.getMapLayersNames());
//        layerVisibleCheckBox.setChecked(mapEditorScreen.tmxMap.getLayers().get(mapLayersBox.getSelected()).isVisible());
//        String selectedMap = selectMapsBox.getSelected();
//        selectMapsBox.setItems(mapEditorScreen.gameMaster.gameLevelMaps);
//        if (mapEditorScreen.gameMaster.gameLevelMaps.contains(selectedMap, false)) {
//            selectMapsBox.setSelected(selectedMap);
//        }
//        float testListViewPosX = -1, testListViewPosY = -1;
//        if (testListView != null) {
//            testListViewPosX = testListView.getX();
//            testListViewPosY = testListView.getY();
//            testListView.remove();
//        }
//        testListView = new TestListView(mapEditorScreen);
//        if (testListViewPosX != -1 && testListViewPosY != -1) {
//            testListView.setPosition(testListViewPosX, testListViewPosY);
//        }
//        addActor(testListView);
//    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        if (tileSelector != null) {
            tileSelector.panStop(x, y, pointer, button);
        }
        return super.panStop(x, y, pointer, button);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (tileSelector != null) {
            if (tileSelector.scrolled(amountY)) {
                return true;
            }
        }
        return super.scrolled(amountX, amountY);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        if (tileSelector != null) {
            tileSelector.touchDown(screenX, screenY, pointer, button);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (tileSelector != null) {
            tileSelector.panStop(screenX, screenY, pointer, button);
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float deltaX = screenX - prevMouseX;
        float deltaY = screenY - prevMouseY;
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        boolean returnSuperTouchDragged = super.touchDragged(screenX, screenY, pointer);
        if (tileSelector != null) {
            if (tileSelector.pan(screenX, screenY, deltaX, deltaY)) {
                return true;
            }
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public void resize(int width, int height) {
        Logger.logFuncStart("width:" + width, "height:" + height);
        for (Actor actor : getActors()) {
            if (actor instanceof Table) {
                actor.setSize(width, height);
            }
        }
        super.getViewport().update(width, height, true);

        if (tileSelector != null) {
            tileSelector.resize(width, height);
        }
    }
}
