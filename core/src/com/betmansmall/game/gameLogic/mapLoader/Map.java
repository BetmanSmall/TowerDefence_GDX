package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.utils.ObjectMap;

public class Map {
    public String mapPath;
    public TileSets tileSets;
    public MapLayers mapLayers;
    public ObjectMap<String, Object> mapProperties;
//    private Array<? extends Disposable> ownedResources;
    public int width, height;
    public int tileWidth, tileHeight;

    /** Creates an empty TiledMap. */
    public Map (String mapPath) {
        this.mapPath = mapPath;
        tileSets = new TileSets();
    }
}
