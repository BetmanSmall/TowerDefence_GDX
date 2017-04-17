package com.betmansmall.game.GameScreenInteface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.List;

/**
 * Created by Дима Цыкунов on 20.02.2016.
 */
public abstract class Roulette {

    /* scale parameters */
    private static float scale =  (((float) Gdx.graphics.getWidth()) / 1980);
    static int x =(int)( 200*scale);
    static int y =(int)( 350*scale);

    /* model size */
    protected static final int MODEL_WIDTH = 1920;
    protected static final int MODEL_HEIGHT = 1080;

    /* tower roulette size */
    protected static final int ROULETTE_RADIUS = x;

    /* ring size */
    protected static final int RING_RADIUS = y;

    protected float getLocalWidth(float width) {
        return MODEL_WIDTH / Gdx.graphics.getWidth() * width;
    }

    protected float getLocalHeight(float height) {
        return MODEL_HEIGHT / Gdx.graphics.getHeight() * height;
    }

    public abstract List<Group> getGroup();

}
