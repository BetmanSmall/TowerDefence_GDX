package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.betmansmall.game.GameSettings;
import com.betmansmall.screens.menu.MapEditorScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisImageButton;

public class TileSelector extends InterfaceSelector {
    public TiledMapTileSet tiledMapTileSet;

    public TileSelector(MapEditorScreen mapEditorScreen) {
        this.gameInterface = mapEditorScreen.gameInterface;



        this.tiledMapTileSet = mapEditorScreen.autoTiler.tiledMapTiles;
        Logger.logDebug("tiledMapTileSet:" + tiledMapTileSet);
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
        for (int tileIndex = 0; tileIndex < tiledMapTileSet.size(); tileIndex++) {
            Cell<Button> cellButton = null;
            for (int k = 0; k < 3; k++, tileIndex++) {
                TiledMapTile tiledMapTile = tiledMapTileSet.getTile(tileIndex);
                if (tiledMapTile != null) {
                    String tiledId = String.valueOf(tiledMapTile.getId());
//                    Label tileIdLabel = new VisLabel(tiledId);
//                    tileIdLabel.setName("tileIdLabel");

//                    Table towerTable = new VisTable();
//                    towerTable.add(tileIdLabel).row();
//                    Image templateButton = new VisImage(tiledMapTile.getTextureRegion());
//                    templateButton.setFillParent(true);
//                    templateButton.scaleBy(1.5f);
//                    towerTable.add(templateButton).expand().fill();

//                    Table tableWithCharacteristics = new VisTable();
//                    towerTable.add(tableWithCharacteristics).expand().fill();

//                    VisImageTextButton.VisImageTextButtonStyle visImageTextButtonStyle = new VisImageTextButton.VisImageTextButtonStyle();
//                    visImageTextButtonStyle.imageUp = new TextureRegionDrawable(templateButton);
//                    Button button = new VisImageTextButton(tiledId, new TextureRegionDrawable(tiledMapTile.getTextureRegion()));
//                    VisImageTextButton.VisImageTextButtonStyle visImageTextButtonStyle = new VisImageTextButton.VisImageTextButtonStyle();
                    TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(tiledMapTile.getTextureRegion());
                    textureRegionDrawable.setMinSize(100f, 100f);
                    Button button = new VisImageButton(textureRegionDrawable);
                    button.pad(0.01f);
//                    button.setWidth(Gdx.graphics.getWidth()/10f);
//                    button.setHeight(Gdx.graphics.getWidth()/10f);
//                    button.sizeBy(2f);
//                    button.setSize(2f, 2f);
//                    button.setScale(2f);
//                    Button button = new Button(towerTable, VisUI.getSkin());
                    button.setName(tiledId);
                    button.setUserObject(tileIndex);
//                    button.setFillParent(true);
//                    Button button = new VisTextButton();
                    cellButton = this.add(button);//.expand().fill();//.width(Gdx.graphics.getWidth()/10f).height(Gdx.graphics.getHeight()/5f);
                }
            }
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
