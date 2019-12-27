package com.betmansmall.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.client.GameScreen;

import java.util.List;

public class TowersSelector extends Selector<TemplateForTower> {

    public TowersSelector(GameScreen gameScreen, boolean vertical) {
        super(gameScreen, vertical, gameScreen.gameField.factionsManager.getAllTemplateForTowers());
    }

    @Override
    public void initButtons(List<TemplateForTower> templates) {
        for (TemplateForTower template : templates) {
            Button button = createButtonTable(template);
            button.setName(template.name);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    buttonPressed(template);
                }
            });
            table.add(button).expand().fillX();
            if(vertical) table.row();
        }
        setDebug(true, true);
    }

    @Override
    public boolean buttonPressed(TemplateForTower template) {
        super.buttonPressed(template);
        return (gameField.createdUnderConstruction(template) != null);
    }

    //TODO change button layout for horizontal selectors
    public Button createButtonTable(TemplateForTower template) {
        Button button = new Button(gameInterface.skin);
        button.add(createLabel(template.name, Color.WHITE)).colspan(2).expandX().row();
        button.add(new Image(template.idleTile.getTextureRegion()));
        button.add(createCharacteristicsTable(template)).fillX().expand().right();
        return button;
    }

    protected Table createCharacteristicsTable(TemplateForTower template) {
        Table table = new Table();
        table.add(createLabel(template.damage.toString(), Color.RED)).row();
        table.add(createLabel(template.radiusDetection.toString(), Color.GREEN)).row();
        table.add(createLabel(template.cost.toString(), Color.YELLOW)).row();
        return table;
    }
}
