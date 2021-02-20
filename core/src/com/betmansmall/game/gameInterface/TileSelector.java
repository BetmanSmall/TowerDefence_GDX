package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.GameSettings;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.screens.menu.MapEditorScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

public class TileSelector extends InterfaceSelector {
    public TiledMapTileSet tiledMapTileSet;

    public TileSelector(MapEditorScreen mapEditorScreen) {
        this.gameInterface = mapEditorScreen.mapEditorInterface;

        this.tiledMapTileSet = mapEditorScreen.autoTiler.tiledMapTiles;
//        Logger.logDebug("templateForTowers:" + templateForTowers);
        this.setDebug(true);
        GameSettings gameSettings = mapEditorScreen.gameMaster.sessionSettings.gameSettings;
        updateBorders(gameSettings.verticalSelector, gameSettings.topBottomLeftRightSelector, gameSettings.smoothFlingSelector);
        initButtons();
    }

    public void dispose() {
        Logger.logFuncStart();
    }

    @Override
    public void initButtons() {
        this.clear();
        for (int towerIndex = 0; towerIndex < tiledMapTileSet.size(); towerIndex++) {
            TiledMapTile tiledMapTile = tiledMapTileSet.getTile(towerIndex);
            String tiledId = String.valueOf(tiledMapTile.getId());
            Label tileIdLabel = new VisLabel(tiledId);
            tileIdLabel.setName("tileIdLabel");

            Table towerTable = new Table();
            towerTable.add(tileIdLabel).colspan(2).row();
            Image templateButton = new Image(tiledMapTile.getTextureRegion());
            towerTable.add(templateButton).expand();

            Table tableWithCharacteristics = new Table();
            towerTable.add(tableWithCharacteristics).expandY().right();

            Button button = new Button(towerTable, VisUI.getSkin());
            button.setName(tiledId);
            button.setUserObject(towerIndex);
            Cell<Button> cellButton = this.add(button).expand().fill();
            if (vertical) {
                cellButton.row();//.minHeight(Gdx.graphics.getHeight()*0.2f).row();
            }
        }
    }

    @Override
    public boolean buttonPressed(Integer index) {
        Logger.logFuncStart("index:" + index);
//        if (index != null) {
//            if (index >= 0 && index < templateForTowers.size) {
//                return (gameField.createdUnderConstruction(templateForTowers.get(index)) != null);
//            }
//        }
        return false;
    }

    @Override
    public void selectorClosed() {
        Logger.logFuncStart();
    }
}
