package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class TileSets implements Iterable<TileSet> {

    private Array<TileSet> tilesets;

    public TileSets () {
        tilesets = new Array<TileSet>();
    }

    public TileSet getTileSet (int index) {
        return tilesets.get(index);
    }

    public TileSet getTileSet (String name) {
        for (TileSet tileset : tilesets) {
            if (name.equals(tileset.name)) {
                return tileset;
            }
        }
        return null;
    }

    public void addTileSet (TileSet tileset) {
        tilesets.add(tileset);
    }

    public void removeTileSet (int index) {
        tilesets.removeIndex(index);
    }

    public void removeTileSet (TileSet tileset) {
        tilesets.removeValue(tileset, true);
    }

    public Tile getTile (int id) {
        for (int i = tilesets.size-1; i >= 0; i--) {
            TileSet tileset = tilesets.get(i);
            Tile tile = tileset.getTile(id);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

    @Override
    public Iterator<TileSet> iterator () {
        return tilesets.iterator();
    }
}

