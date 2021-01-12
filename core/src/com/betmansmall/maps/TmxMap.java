package com.betmansmall.maps;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.utils.logging.Logger;

public class TmxMap extends TiledMap { // how good? 1
//    private TiledMap tiledMap; // how good? 2

    public String mapPath;
    public int width, height;
    public int tileWidth, tileHeight;
    public float halfTileWidth, halfTileHeight;
    public boolean isometric;
    public boolean turnedMap = false;

    public TmxMap(TiledMap tiledMap, String mapPath) { // how good? 44444? // without MapLoader.loadTilemap()||loadTiledMap()
//    public void init(TiledMap tiledMap, String mapPath) {
        Logger.logFuncStart("tiledMap:" + tiledMap + ", mapPath:" + mapPath);
//        this.tiledMap = tiledMap;
        this.mapPath = mapPath;
        this.isometric = false;

        for (MapLayer mapLayer : tiledMap.getLayers()) {
            super.getLayers().add(mapLayer);
        }
        for (TiledMapTileSet tiledMapTileSet : tiledMap.getTileSets()) {
            super.getTileSets().addTileSet(tiledMapTileSet);
        }
//        super.setOwnedResources(getOwnedResources); // pizda libGDX? why?
        MapProperties mapProperties = this.getProperties();
        mapProperties.putAll(tiledMap.getProperties());
        this.width = mapProperties.get("width", 0, Integer.class);
        this.height = mapProperties.get("height", 0, Integer.class);
        this.tileWidth = mapProperties.get("tilewidth", 0, Integer.class);
        this.tileHeight = mapProperties.get("tileheight", 0, Integer.class);
        this.halfTileWidth = tileWidth / 2f;
        this.halfTileHeight = tileHeight / 2f;
        this.isometric = mapProperties.get("orientation", "", String.class).equals("isometric");
    }

    public TmxMap(String mapPath) {
        Logger.logFuncStart("mapPath:" + mapPath);
        this.mapPath = mapPath;
        this.isometric = false;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Array<String> getTiledMapTilesIds() {
        Array<String> tiledMapTileArray = new Array<>();
        for (TiledMapTileSet tiledMapTiles : getTileSets()) {
            for (TiledMapTile tiledMapTile : tiledMapTiles) {
                tiledMapTileArray.add(tiledMapTile.getId() + "");
            }
        }
        return tiledMapTileArray;
    }

    public Array<String> getMapLayersNames() {
        Array<String> mapLayersNames = new Array<>();
        for (MapLayer mapLayer : getLayers()) {
            mapLayersNames.add(mapLayer.getName());
        }
        return mapLayersNames;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("TmxMap[");
        sb.append("mapPath:" + mapPath);
        sb.append(",width:" + width);
        sb.append(",height:" + height);
        sb.append(",isometric:" + isometric);
        if (full) {
            sb.append(",turnedMap:" + turnedMap);
            sb.append(",tileWidth:" + tileWidth);
            sb.append(",tileHeight:" + tileHeight);
            sb.append(",halfTileWidth:" + halfTileWidth);
            sb.append(",halfTileHeight:" + halfTileHeight);
//            sb.append(",tiledMap:" + tiledMap);
        }
        sb.append("]");
        return sb.toString();
    }
}
