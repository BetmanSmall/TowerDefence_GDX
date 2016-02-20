package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public abstract class Roulette {

    /* tower roulette size */
    protected static final int ROULETTE_HEIGHT = 200;
    protected static final int ROULETTE_WIDTH = 200;

    /* model size */
    protected static final int MODEL_WIDTH = 1920;
    protected static final int MODEL_HEIGHT = 1080;

    /* ring size */
    protected static final int RING_HEIGHT = 700;
    protected static final int RING_WIDTH = 700;

    protected int getLocalWidth(int width) {
        return MODEL_WIDTH / Gdx.graphics.getWidth() * width;
    }

    protected int getLocalHeight(int height) {
        return MODEL_HEIGHT / Gdx.graphics.getHeight() * height;
    }

    public abstract Group getGroup();

}
