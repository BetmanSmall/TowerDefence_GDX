package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TiledMapImageLayer extends MapLayer {

    private TextureRegion region;

    private float x;
    private float y;

    public TiledMapImageLayer (TextureRegion region, float x, float y) {
        this.region = region;
        this.x = x;
        this.y = y;
    }

    public TextureRegion getTextureRegion () {
        return region;
    }

    public void setTextureRegion (TextureRegion region) {
        this.region = region;
    }

    public float getX () {
        return x;
    }

    public void setX (float x) {
        this.x = x;
    }

    public float getY () {
        return y;
    }

    public void setY (float y) {
        this.y = y;
    }

}
