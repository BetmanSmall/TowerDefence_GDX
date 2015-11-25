package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import javafx.application.Application;

/**
 * Created by Vitaly on 13.10.2015.
 */
public class MainMenuScreen implements Screen {
    final TowerDefence game;
    OrthographicCamera camera;
    SpriteBatch batch;
    BitmapFont font;

    public MainMenuScreen(final TowerDefence gam) {
        this.game = gam;

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "Welcome to TowerDefence!", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        font.draw(batch, "Tap anywhere to begin!", Gdx.graphics.getWidth()/2+2, Gdx.graphics.getHeight()/2-12);
        batch.end();

        if (Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
