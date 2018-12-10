package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by betma on 19.11.2018.
 */

public class Map extends com.badlogic.gdx.maps.Map {
    public String mapPath;
    public TiledMapTileSets tilesets;
    private Array<? extends Disposable> ownedResources;
    public int width, height;
    public int tileWidth, tileHeight;
    public Array<TiledMapTile> terraintypes;

    /** @return collection of tilesets for this map. */
    public TiledMapTileSets getTileSets () {
        return tilesets;
    }

    /** Creates an empty TiledMap. */
    public Map (String mapPath) {
        this.mapPath = mapPath;
        tilesets = new TiledMapTileSets();
    }

    /** Used by loaders to set resources when loading the map directly, without {@link AssetManager}. To be disposed in
     * {@link #dispose()}.
     * @param resources */
    public void setOwnedResources (Array<? extends Disposable> resources) {
        this.ownedResources = resources;
    }

    @Override
    public void dispose () {
        if (ownedResources != null) {
            for (Disposable resource : ownedResources) {
                resource.dispose();
            }
        }
    }
}
