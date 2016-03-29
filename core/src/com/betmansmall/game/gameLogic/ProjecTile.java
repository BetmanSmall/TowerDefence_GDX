package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class ProjecTile {
    public float x, y;
    public Creep creep;

    public TextureRegion textureRegion;

    ProjecTile(int x, int y, Creep creep, TextureRegion textureRegion) {
        this.x = x;
        this.y = y;
        this.creep = creep;
        this.textureRegion = textureRegion;
    }
}
