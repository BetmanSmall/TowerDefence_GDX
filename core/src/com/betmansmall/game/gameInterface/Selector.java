package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.GameSettings;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.playerTemplates.Template;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.logging.Logger;

import java.util.List;

/**
 * @author Alexander on 18.11.2019.
 */
public abstract class Selector<T extends Template> extends ScrollPane {
    protected final GameSettings gameSettings;
    protected final Table table; // TODO replace with vertical group
    protected GameField gameField;
    protected BitmapFont bitmapFont;
    protected GameInterface gameInterface;

    protected boolean vertical;

    public Selector(GameScreen gameScreen, List<T> templates) {
        super(new Table());
        table = (Table) getActor();
        gameField = gameScreen.gameField;
        bitmapFont = gameScreen.gameInterface.bitmapFont;
        gameInterface = gameScreen.gameInterface;
        gameSettings = gameScreen.game.sessionSettings.gameSettings;
        vertical = gameSettings.verticalSelector;
        Logger.logDebug("templates" + templates);
        initButtons(templates);
        setDebug(true);
    }

    public void initButtons(List<T> templates) {
        Logger.logFuncStart();
    }

    public boolean buttonPressed(T template) {
        Logger.logFuncStart("Selected template:" + template);
        return false;
    }

    public void selectorClosed() {
        Logger.logFuncStart();
    }

    protected Label createLabel(String text, Color color) {
        Label label = new Label(text, gameInterface.skin);
        label.setColor(color);
        return label;
    }
}
