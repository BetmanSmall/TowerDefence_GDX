package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class TiledMap extends Map {
    private TiledMapTileSets tilesets;
    private Array<? extends Disposable> ownedResources;

    public String mapPath;
    public int width, height;
    public int tileWidth, tileHeight;

    public TiledMapTileSets getTileSets () {
        return tilesets;
    }

    public TiledMap () {
        tilesets = new TiledMapTileSets();
    }

    public TiledMap (String mapPath) {
        this.mapPath = mapPath;
        this.tilesets = new TiledMapTileSets();
    }

//    public MapLayer addNewLayer() {
//    int width = properties.value("width").toInt();
//    int height = properties.value("height").toInt();
//        int tileWidth = Integer.parseInt(properties.get("tilewidth"));
//        int tileHeight = Integer.parseInt(properties.get("tileheight"));
//        MapLayer* layer = new MapLayer(width, height, tileWidth, tileHeight);
//        mapLayers.add(layer);
//        return layer;
//    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

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
