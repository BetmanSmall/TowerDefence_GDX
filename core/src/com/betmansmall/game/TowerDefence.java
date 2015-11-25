package com.betmansmall.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Vitaly on 13.10.2015.
 */
public class TowerDefence extends Game {
//    SpriteBatch batch;
//    BitmapFont font;

    @Override
    public void create() {
//        batch = new SpriteBatch();
//        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
//        batch.dispose();
//        font.dispose();
    }
}
