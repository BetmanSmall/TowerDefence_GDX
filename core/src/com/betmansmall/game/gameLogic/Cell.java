package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BetmanSmall on 11.03.2016.
 */
public class Cell {
    private Array<TiledMapTile> tiledMapTiles;
    private boolean empty;
    private boolean terrain;
    private Tower tower;
    private Array<Creep> creeps;

    public Cell() {
        this.tiledMapTiles = new Array<TiledMapTile>();
        this.empty = true;
        this.terrain = false;
        this.tower = null;
        this.creeps = null;
    }

    public void addTiledMapTile(TiledMapTile tiledMapTile) {
        tiledMapTiles.add(tiledMapTile);
    }

    public Array<TiledMapTile> getTiledMapTiles() {
        return tiledMapTiles;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isTerrain() {
        return terrain;
    }

    public boolean setTerrain() {
        if(empty) {
            terrain = true;
            empty = false;
            return true;
        }
        return false;
    }

    public boolean removeTerrain() {
        if(terrain) {
            terrain = false;
            empty = true;
            return true;
        }
        return false;
    }

    public Tower getTower() {
        return tower;
    }

    public boolean setTower(Tower tower) {
        if(empty) {
            this.tower = tower;
            empty = false;
            return true;
        }
        return false;
    }

    public boolean removeTower() {
        if(tower != null) {
            tower = null;
            empty = true;
            return true;
        }
        return false;
    }

    public Array<Creep> getCreeps() {
        return creeps;
    }

    public Creep getCreep() {
        if(creeps != null) {
            return creeps.first();
        }
        return null;
    }

    public boolean setCreep(Creep creep) {
        if(empty) {
            creeps = new Array<Creep>();
            creeps.add(creep);
            empty = false;
            return true;
        } else if(creeps != null) {
            creeps.add(creep);
            return true;
        }
        return false;
    }

    public int removeCreep(Creep creep) {
        if(creeps != null) {
            creeps.removeValue(creep, false);
            if(creeps.size == 0) {
                creeps = null;
                empty = true;
                return 0;
            }
            return creeps.size;
        }
        return -1;
    }

    public void dispose() {
        tiledMapTiles.clear();
        tiledMapTiles = null;
        tower = null;
        creeps.clear();
        creeps = null;
    }
}