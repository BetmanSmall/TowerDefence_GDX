package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.logging.Logger;

import java.util.List;

public class TowersSelector extends Selector<TemplateForTower> {

    public TowersSelector(GameScreen gameScreen) {
        super(gameScreen, gameScreen.gameField.factionsManager.getAllTemplateForTowers());
    }

    @Override
    public void initButtons(List<TemplateForTower> templates) {
        this.clear();
        for (int index = 0; index < templates.size(); index++) {
            TemplateForTower template = templates.get(index);
            Button button = new Button(createButtonTable(template), gameInterface.skin);
            button.setName(template.name);
            button.setUserObject(index);
            table.add(new Label("qwer", gameInterface.skin)).row();
            //            table.add(button).expand().fill();
//            if(vertical) table.row();
        }
    }

    public Table createButtonTable(TemplateForTower template) {
        Table table = new Table();
        table.add(createLabel(template.name, Color.WHITE)).colspan(2).row();
        table.add(new Image(template.idleTile.getTextureRegion())).expand();
        table.add(createCharacteristicsTable(template)).expandY().right();
        return table;
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

    protected Table createCharacteristicsTable(TemplateForTower template) {
        Table table = new Table();
        table.add(createLabel(template.damage.toString(), Color.RED)).row();
        table.add(createLabel(template.radiusDetection.toString(), Color.GREEN)).row();
        table.add(createLabel(template.cost.toString(), Color.YELLOW)).row();
        return table;
    }
}
