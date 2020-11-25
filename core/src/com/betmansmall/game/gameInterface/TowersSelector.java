package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.GameSettings;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.VisUI;

public class TowersSelector extends InterfaceSelector {
    public Array<TemplateForTower> templateForTowers;

    public TowersSelector(GameScreen gameScreen) {
        this.gameField = gameScreen.gameField;
        this.bitmapFont = gameScreen.gameInterface.bitmapFont;
        this.gameInterface = gameScreen.gameInterface;

        this.templateForTowers = gameField.factionsManager.getAllTemplateForTowers();
//        Logger.logDebug("templateForTowers:" + templateForTowers);
        this.setDebug(true);
        GameSettings gameSettings = gameScreen.gameMaster.sessionSettings.gameSettings;
        updateBorders(gameSettings.verticalSelector, gameSettings.topBottomLeftRightSelector, gameSettings.smoothFlingSelector);
        initButtons();
    }

    public void dispose() {
//        Gdx.app.log("TowersSelector::dispose()", "--");
    }

    @Override
    public void initButtons() {
        this.clear();
        for (int towerIndex = 0; towerIndex < templateForTowers.size; towerIndex++) {
            TemplateForTower templateForTower = templateForTowers.get(towerIndex);
            String nameTower = templateForTower.name;
            String attackTower = templateForTower.damage.toString();
            String radiusDetectionTower = templateForTower.radiusDetection.toString();
            String costTower = templateForTower.cost.toString();
            Label nameTowerLabel = new Label(nameTower, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label attackTowerLabel = new Label(attackTower, new Label.LabelStyle(bitmapFont, Color.RED));
            Label radiusDetectionTowerLabel = new Label(radiusDetectionTower, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costTowerLabel = new Label(costTower, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            nameTowerLabel.setName("nameTowerLabel");
            attackTowerLabel.setName("attackTowerLabel");
            radiusDetectionTowerLabel.setName("radiusDetectionTowerLabel");
            costTowerLabel.setName("costTowerLabel");

            Table towerTable = new Table();
            towerTable.add(nameTowerLabel).colspan(2).row();
            Image templateButton = new Image(templateForTower.idleTile.getTextureRegion());
            towerTable.add(templateButton).expand();

            Table tableWithCharacteristics = new Table();
            tableWithCharacteristics.add(attackTowerLabel).row();
            tableWithCharacteristics.add(radiusDetectionTowerLabel).row();
            tableWithCharacteristics.add(costTowerLabel).row();
            towerTable.add(tableWithCharacteristics).expandY().right();

            Button button = new Button(towerTable, VisUI.getSkin());
            button.setName(nameTower);
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
        if (index != null) {
            if (index >= 0 && index < templateForTowers.size) {
                return (gameField.createdUnderConstruction(templateForTowers.get(index)) != null);
            }
        }
        return false;
    }

    @Override
    public void selectorClosed() {
        gameField.cancelUnderConstruction();
    }
}
