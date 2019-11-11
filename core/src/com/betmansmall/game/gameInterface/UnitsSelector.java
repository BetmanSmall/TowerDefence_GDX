package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.logging.Logger;

public class UnitsSelector extends InterfaceSelector<TemplateForUnit> {

    public UnitsSelector(GameScreen gameScreen) {
        super(gameScreen, gameScreen.gameField.factionsManager.getAllTemplateForUnits());
        updateBorders(gameSettings.verticalSelector, !gameSettings.topBottomLeftRightSelector, gameSettings.smoothFlingSelector);
    }

    @Override
    public void initButtons() {
        this.clear();
        for (int unitIndex = 0; unitIndex < templates.size(); unitIndex++) {
            TemplateForUnit templateForUnit = templates.get(unitIndex);
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

            Button button = new Button(unitTable, gameInterface.skin);
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
        if (index != null && index >= 0 && index < templates.size()) {
                return (gameField.spawnUnitFromUser(templates.get(index)) != null);
        }
        return false;
    }

    @Override
    public void selectorClosed() {
        Logger.logFuncStart();
    }
}
