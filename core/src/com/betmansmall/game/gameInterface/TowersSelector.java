package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.logging.Logger;

public class TowersSelector extends InterfaceSelector<TemplateForTower> {

    public TowersSelector(GameScreen gameScreen) {
        super(gameScreen, gameScreen.gameField.factionsManager.getAllTemplateForTowers());
        updateBorders(gameSettings.verticalSelector, gameSettings.topBottomLeftRightSelector, gameSettings.smoothFlingSelector);
    }

    @Override
    public void initButtons() {
        this.clear();
        for (int towerIndex = 0; towerIndex < templates.size(); towerIndex++) {
            TemplateForTower templateForTower = templates.get(towerIndex);
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

            Button button = new Button(towerTable, gameInterface.skin);
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
        if (index != null && index >= 0 && index < templates.size()) {
            return (gameField.createdUnderConstruction(templates.get(index)) != null);
        }
        return false;
    }

    @Override
    public void selectorClosed() {
        gameField.cancelUnderConstruction();
    }
}
