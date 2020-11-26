package com.betmansmall.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.betmansmall.GameMaster;
import com.betmansmall.utils.logging.Logger;

public class AbstractScreen implements Screen {
    public GameMaster gameMaster;

    public AbstractScreen(GameMaster gameMaster) {
        Logger.logFuncStart();
        this.gameMaster = gameMaster;
    }

    @Override
    public void show() {
        Logger.logFuncStart();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            gameMaster.removeTopScreen();
        }
    }

    @Override
    public void resize(int width, int height) {
        Logger.logFuncStart("width:" + width + ", height:" + height);
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
    public void hide() {
        Logger.logFuncStart();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
    }
}
