package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.GameSettings;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.VisUI;

public class UnitsSelector extends InterfaceSelector {
    public Array<TemplateForUnit> templateForUnits;

    public UnitsSelector(GameScreen gameScreen) {
        this.gameField = gameScreen.gameField;
        this.bitmapFont = gameScreen.gameInterface.bitmapFont;
        this.gameInterface = gameScreen.gameInterface;

        this.templateForUnits = gameField.factionsManager.getAllTemplateForUnits();
        Logger.logDebug("templateForUnits:" + templateForUnits);
        this.setDebug(true);
        GameSettings gameSettings = gameScreen.gameMaster.sessionSettings.gameSettings;
        updateBorders(gameSettings.verticalSelector, !gameSettings.topBottomLeftRightSelector, gameSettings.smoothFlingSelector);
        initButtons();
    }

    public void dispose() {
        Logger.logFuncStart();
    }

    @Override
    public void initButtons() {
        this.clear();
        for (int unitIndex = 0; unitIndex < templateForUnits.size; unitIndex++) {
            TemplateForUnit templateForUnit = templateForUnits.get(unitIndex);
            String nameUnit = templateForUnit.name;
            String hpUnit = templateForUnit.healthPoints.toString();
            String speedUnit = templateForUnit.speed.toString();
            String costUnit = templateForUnit.cost.toString();
            Label nameUnitLabel = new Label(nameUnit, new Label.LabelStyle(bitmapFont, Color.WHITE));
            Label hpUnitLabel = new Label(hpUnit, new Label.LabelStyle(bitmapFont, Color.RED));
            Label speedUnitLabel = new Label(speedUnit, new Label.LabelStyle(bitmapFont, Color.GREEN));
            Label costUnitLabel = new Label(costUnit, new Label.LabelStyle(bitmapFont, Color.YELLOW));
            nameUnitLabel.setName("nameUnitLabel");
            hpUnitLabel.setName("hpUnitLabel");
            speedUnitLabel.setName("speedUnitLabel");
            costUnitLabel.setName("costUnitLabel");

            Table unitTable = new Table();

            unitTable.add(nameUnitLabel).colspan(2).row();
            Image templateButton = new Image(templateForUnit.animations.values().toArray().get(6).getTextureRegion());
            unitTable.add(templateButton).expand();

            Table verticalGroupHar = new Table();
            verticalGroupHar.add(hpUnitLabel).row();
            verticalGroupHar.add(speedUnitLabel).row();
            verticalGroupHar.add(costUnitLabel).row();
            unitTable.add(verticalGroupHar).expandY().left();

            Button button = new Button(unitTable, VisUI.getSkin());
            button.setName(nameUnit);
            button.setUserObject(unitIndex);
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
            if (index >= 0 && index < templateForUnits.size) {
                return (gameField.spawnUnitFromUser(templateForUnits.get(index)) != null);
            }
        }
        return false;
    }

    @Override
    public void selectorClosed() {
        Logger.logFuncStart();
    }
}
