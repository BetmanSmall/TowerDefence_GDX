package com.betmansmall.game.screens;

import com.badlogic.gdx.Screen;
import com.betmansmall.game.WidgetController;

public abstract class AbstractScreen implements Screen {
    protected WidgetController widgetController;

    public AbstractScreen(WidgetController widgetController) {
        this.widgetController = widgetController;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
