package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
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
    protected List<T> templates;

    protected boolean vertical;
    protected boolean topBottomLeftRight;
    protected boolean smoothFling;

    protected float parentWidth, parentHeight;
    private float selectorPrefWidth, selectorPrefHeight;
    protected float selectorBorderVertical;
    protected float selectorBorderHorizontal;

    protected boolean flinging;
    protected float flingVelocityX, flingVelocityY;

    private boolean open = true;
    protected float coordinateX = 0;
    protected float coordinateY = 0;

    private boolean isPanning;

    public Selector(GameScreen gameScreen, List<T> templates) {
        super(new Table());
        table = (Table) getActor();
        gameField = gameScreen.gameField;
        bitmapFont = gameScreen.gameInterface.bitmapFont;
        gameInterface = gameScreen.gameInterface;
        gameSettings = gameScreen.game.sessionSettings.gameSettings;
        Logger.logDebug("templates" + templates);
        initButtons(templates);
        setDebug(true);
    }

    public void initButtons(List<T> templates) {
        Logger.logFuncStart();
    }

    public boolean buttonPressed(Integer index) {
        Logger.logFuncStart("index:" + index);
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
