package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.logging.Logger;

import java.util.List;

public class UnitsSelector extends Selector<TemplateForUnit> {

    public UnitsSelector(GameScreen gameScreen) {
        super(gameScreen, gameScreen.gameField.factionsManager.getAllTemplateForUnits());
    }

    @Override
    public void initButtons(List<TemplateForUnit> templates) {
        this.clear();
        for (int index = 0; index < templates.size(); index++) {
            TemplateForUnit template = templates.get(index);
            Button button = new Button(createButtonTable(template), gameInterface.skin);
            button.setName(template.name);
            button.setUserObject(index);
            table.add(button).expand().fill();
            if(vertical) table.row();
        }
    }

    public Table createButtonTable(TemplateForUnit template) {
        Table table = new Table();
        table.add(createLabel(template.name, Color.WHITE)).colspan(2).row();
        table.add(new Image(template.animations.values().toArray().get(6).getTextureRegion())).expand();
        table.add(createCharacteristicsTable(template)).expandY().left();
        return table;
    }

    @Override
    public boolean buttonPressed(Integer index) {
        Logger.logFuncStart("index:" + index);
        if (index != null && index >= 0 && index < templates.size()) {
                return (gameField.spawnUnitFromUser(templates.get(index)) != null);
        }
        return false;
    }

    protected Table createCharacteristicsTable(TemplateForUnit template) {
        Table table = new Table();
        table.add(createLabel(template.healthPoints.toString(), Color.RED)).row();
        table.add(createLabel(template.speed.toString(), Color.GREEN)).row();
        table.add(createLabel(template.cost.toString(), Color.YELLOW)).row();
        return table;
    }
}
