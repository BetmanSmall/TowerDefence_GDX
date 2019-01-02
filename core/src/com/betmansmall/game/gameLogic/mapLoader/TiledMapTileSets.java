package com.betmansmall.game.gameLogic.mapLoader;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class TiledMapTileSets implements Iterable<TiledMapTileSet> {

    private Array<TiledMapTileSet> tilesets;

    public TiledMapTileSets() {
        tilesets = new Array<TiledMapTileSet>();
    }

    public TiledMapTileSet getTileSet (int index) {
        return tilesets.get(index);
    }

    public TiledMapTileSet getTileSet (String name) {
        for (TiledMapTileSet tileset : tilesets) {
            if (name.equals(tileset.getName())) {
                return tileset;
            }
        }
        return null;
    }

    public void addTileSet (TiledMapTileSet tileset) {
        tilesets.add(tileset);
    }

    public void removeTileSet (int index) {
        tilesets.removeIndex(index);
    }

    public void removeTileSet (TiledMapTileSet tileset) {
        tilesets.removeValue(tileset, true);
    }

    public TiledMapTile getTile (int id) {
        for (int i = tilesets.size-1; i >= 0; i--) {
            TiledMapTileSet tileset = tilesets.get(i);
            TiledMapTile tiledMapTile = tileset.getTile(id);
            if (tiledMapTile != null) {
                return tiledMapTile;
            }
        }
        return null;
    }

    @Override
    public Iterator<TiledMapTileSet> iterator () {
        return tilesets.iterator();
    }
}

