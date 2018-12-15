package com.betmansmall.game.gameLogic.mapLoader;

import java.util.Iterator;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class TileSet implements Iterable<Tile> {
    public String name;
    public IntMap<Tile> tiles;
    public ObjectMap<String, Object> properties;

    public TileSet () {
        tiles = new IntMap<Tile>();
        properties = new ObjectMap();
    }

    public Tile getTile (int id) {
        return tiles.get(id);
    }

    @Override
    public Iterator<Tile> iterator () {
        return tiles.values().iterator();
    }

    public void putTile (int id, Tile tile) {
        tiles.put(id, tile);
    }

    public void removeTile (int id) {
        tiles.remove(id);
    }

//    public int size() {
//        return tiles.size;
//    }
}

