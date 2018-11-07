package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by betma on 07.11.2018.
 */

public interface Tile {
    public enum BlendMode {
        NONE, ALPHA
    }

//    protected int id;
//    protected TextureRegion textureRegion;

    public int getId ();

    public void setId (int id);

    /** @return the {@link Tile.BlendMode} to use for rendering the tile */
    public Tile.BlendMode getBlendMode ();

    /** Sets the {@link Tile.BlendMode} to use for rendering the tile
     *
     * @param blendMode the blend mode to use for rendering the tile */
    public void setBlendMode (Tile.BlendMode blendMode);

    /** @return texture region used to render the tile */
    public TextureRegion getTextureRegion ();

    /** Sets the texture region used to render the tile */
    public void setTextureRegion(TextureRegion textureRegion);

    /** @return the amount to offset the x position when rendering the tile */
    public float getOffsetX();

    /** Set the amount to offset the x position when rendering the tile */
    public void setOffsetX(float offsetX);

    /** @return the amount to offset the y position when rendering the tile */
    public float getOffsetY();

    /** Set the amount to offset the y position when rendering the tile */
    public void setOffsetY(float offsetY);

    /** @return tile's properties set */
    public ObjectMap<String, String> getProperties ();
}
