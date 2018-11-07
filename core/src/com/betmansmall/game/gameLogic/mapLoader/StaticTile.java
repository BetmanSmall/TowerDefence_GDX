package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by betma on 07.11.2018.
 */

public class StaticTile implements Tile {

    private int id;

    private Tile.BlendMode blendMode = Tile.BlendMode.ALPHA;

    private ObjectMap<String, String> properties;

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
    public Tile.BlendMode getBlendMode () {
        return blendMode;
    }

    @Override
    public void setBlendMode (Tile.BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    @Override
    public ObjectMap<String, String> getProperties () {
        if (properties == null) {
            properties = new ObjectMap<String, String>();
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
    public StaticTile(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    /** Copy constructor
     *
     * @param copy the StaticTile to copy. */
//    public StaticTile(StaticTile copy) {
//        if (copy.properties != null) {
//            getProperties().putAll(copy.properties);
//        }
//        this.textureRegion = copy.textureRegion;
//        this.id = copy.id;
//    }
}
