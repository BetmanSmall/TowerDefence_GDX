package com.betmansmall.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.GameSettings;
import com.betmansmall.game.gameInterface.GameInterface;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.Template;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.OrientationEnum;
import com.betmansmall.util.logging.Logger;

import java.util.List;

/**
 * @author Alexander on 18.11.2019.
 */
public abstract class Selector<T extends Template> extends ScrollPane {
    protected final GameSettings gameSettings;
    protected Container<Table> container;
    protected final Table table; // TODO replace with vertical group
    protected GameField gameField;
    protected BitmapFont bitmapFont;
    protected GameInterface gameInterface;
    protected OrientationEnum orientation;

    public Selector(GameScreen gameScreen, List<T> templates) {
        super(new Container<>());
        container = (Container<Table>) getActor();
        container.setActor(table = new Table());
        gameField = gameScreen.gameField;
        bitmapFont = gameScreen.gameInterface.bitmapFont;
        gameInterface = gameScreen.gameInterface;
        gameSettings = gameScreen.game.sessionSettings.gameSettings;
        Logger.logDebug("templates" + templates);
        initButtons(templates);
    }

    public void initButtons(List<T> templates) {
        Logger.logFuncStart();
    }

    public boolean buttonPressed(T template) {
        Logger.logFuncStart("Selected template:" + template);
        return false;
    }

    protected Label createLabel(String text, Color color) {
        Label label = new Label(text, gameInterface.skin);
        label.setColor(color);
        return label;
    }
}
