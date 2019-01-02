package com.betmansmall.game.gameLogic.mapLoader;

import java.util.Iterator;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.IntMap;

public class TiledMapTileSet implements Iterable<TiledMapTile> {
    private String name;
    private IntMap<TiledMapTile> tiles;
    private MapProperties properties;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public MapProperties getProperties () {
        return properties;
    }

    public TiledMapTileSet() {
        tiles = new IntMap<TiledMapTile>();
        properties = new MapProperties();
    }

    public TiledMapTile getTile (int id) {
        return tiles.get(id);
    }

    @Override
    public Iterator<TiledMapTile> iterator () {
        return tiles.values().iterator();
    }

    public void putTile (int id, TiledMapTile tiledMapTile) {
        tiles.put(id, tiledMapTile);
    }

    public void removeTile (int id) {
        tiles.remove(id);
    }

    public int size() {
        return tiles.size;
    }
}

