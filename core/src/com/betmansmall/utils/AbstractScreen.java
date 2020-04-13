package com.betmansmall.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.utils.logging.Logger;

/**
 * @author Crowni
 */
public class AbstractScreen implements Screen {
    private static final float WIDTH = 1080;
    private static final float HEIGHT = 1920;

    protected Stage stage;

    public GameMaster game;

    public AbstractScreen(GameMaster game) {
        Logger.logFuncStart();
        this.game = game;
    }

    @Override
    public void show() {
        Logger.logFuncStart();
    }

//    @Override
//    public void show() {
//        stage = new Stage(new ExtendViewport(WIDTH, HEIGHT));
////        Gdx.input.setInputProcessor(stage);
//    }

//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(24 / 255F, 168 / 255F, 173 / 255F, 0);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        stage.act(delta);
//        stage.draw();
//    }

//    @Override
//    public void resize(int width, int height) {
//        stage.getViewport().update(width, height);
//    }
//
//    public void resume() {
//    }
//
//    @Override
//    public void hide() {
//    }
//
//    @Override
//    public void pause() {
//    }
//
//    @Override
//    public void dispose() {
//        stage.dispose();
//    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void render(float delta) {
        Logger.logFuncStart();
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
