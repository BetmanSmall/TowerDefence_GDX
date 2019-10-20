package com.betmansmall.screens;

import com.badlogic.gdx.Screen;
import com.betmansmall.GameMaster;
import com.betmansmall.util.logging.Logger;

public abstract class AbstractScreen implements Screen {
    public GameMaster game;

    public AbstractScreen(GameMaster game) {
        Logger.logFuncStart();
        this.game = game;
    }

    @Override
    public void pause() {
        Logger.logFuncStart();
    }

    @Override
    public void resume() {
        Logger.logFuncStart();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
    }
}
