package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

public class StaticTiledMapTile implements TiledMapTile {

    private int id;

    private BlendMode blendMode = BlendMode.ALPHA;

    private MapProperties properties;

    private TextureRegion textureRegion;

    private float offsetX;

    private float offsetY;

    @Override
    public int getId () {
        return id;
    }

    @Override
    public void setId (int id) {
        this.id = id;
    }

    @Override
    public BlendMode getBlendMode () {
        return blendMode;
    }

    @Override
    public void setBlendMode (BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    @Override
    public MapProperties getProperties () {
        if (properties == null) {
            properties = new MapProperties();
        }
        return properties;
    }

    @Override
    public TextureRegion getTextureRegion () {
        return textureRegion;
    }

    @Override
    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public float getOffsetX () {
        return offsetX;
    }

    @Override
    public void setOffsetX (float offsetX) {
        this.offsetX = offsetX;
    }

    @Override
    public float getOffsetY () {
        return offsetY;
    }

    @Override
    public void setOffsetY (float offsetY) {
        this.offsetY = offsetY;
    }

    /** Creates a static tile with the given region
     *
     * @param textureRegion the {@link TextureRegion} to use. */
    public StaticTiledMapTile(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    /** Copy constructor
     *
     * @param copy the StaticTiledMapTile to copy. */
    public StaticTiledMapTile(StaticTiledMapTile copy) {
        if (copy.properties != null) {
            getProperties().putAll(copy.properties);
        }
        this.textureRegion = copy.textureRegion;
        this.id = copy.id;
    }
}
