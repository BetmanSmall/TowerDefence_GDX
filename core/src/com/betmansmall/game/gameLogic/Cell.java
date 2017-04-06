package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BetmanSmall on 11.03.2016.
 */
public class Cell {
    public class Tree {
        TextureRegion textureRegion;
        int width, height;

        public Tree(TextureRegion textureRegion, int width, int height) {
            this.textureRegion = textureRegion;
            this.width = width;
            this.height = height;
        }
    }

    public Array<TiledMapTile> backgroundTiles;
    public Array<TiledMapTile> foregroundTiles;
    public Array<Tree> trees;
    private boolean empty;
    private boolean terrain;
    private Tower tower;
    private Array<Creep> creeps;
    public Vector2 graphicCoordinatesBottom, graphicCoordinatesRight, graphicCoordinatesTop, graphicCoordinatesLeft;

    public Cell() {
//        Gdx.app.log("Cell::Cell();", " -- ");
        this.backgroundTiles = new Array<TiledMapTile>();
        this.foregroundTiles = new Array<TiledMapTile>();
        this.trees = new Array<Tree>();
        this.empty = true;
        this.terrain = false;
        this.tower = null;
        this.creeps = null;
//        setGraphicCoordinates(cellX, cellY, halfSizeCellX, halfSizeCellY);
    }

    public void setGraphicCoordinates(int cellX, int cellY, float halfSizeCellX, float halfSizeCellY) {
//        Gdx.app.log("Cell::setGraphicCoordinates(" + cellX + "," + cellY + "," + halfSizeCellX + ", " + halfSizeCellY + ");", " -- ");
//        if(map == 1) { // Нижняя карта
        graphicCoordinatesBottom = new Vector2((-(halfSizeCellX * cellY) + (cellX * halfSizeCellX)), (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY)));
//        } else if(map == 2) { // Правая карта
        graphicCoordinatesRight = new Vector2(((halfSizeCellX * cellY) + (cellX * halfSizeCellX)) + halfSizeCellX, ((halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY);
//        } else if(map == 3) { // Верхняя карта
        graphicCoordinatesTop = new Vector2((-(halfSizeCellX * cellY) + (cellX * halfSizeCellX)), ((halfSizeCellY * cellY) + (cellX * halfSizeCellY)) + halfSizeCellY * 2);
//        } else if(map == 4) {// Левая карта
        graphicCoordinatesLeft = new Vector2((-(halfSizeCellX * cellY) - (cellX * halfSizeCellX)) - halfSizeCellX, ((halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY);
//        }
    }

    public Vector2 getGraphicCoordinates(int map) {
        if(map == 1) {
            return graphicCoordinatesBottom;
        } else if(map == 2) {
            return graphicCoordinatesRight;
        } else if(map == 3) {
            return graphicCoordinatesTop;
        } else if(map == 4) {
            return graphicCoordinatesLeft;
        }
        Gdx.app.log("Cell::getGraphicCoordinates(" + map + ");", " -- Bad map | return null!");
        return null;
    }

//    public void addTiledMapTile(TiledMapTile tiledMapTile) {
//        tiledMapTiles.add(tiledMapTile);
//    }
//
//    public Array<TiledMapTile> getTiledMapTiles() {
//        return tiledMapTiles;
//    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isTerrain() {
        return terrain;
    }

    public boolean setTerrain() {
        if (empty) {
            terrain = true;
            empty = false;
            return true;
        }
        return false;
    }

    public boolean removeTerrain() {
        if (terrain) {
            terrain = false;
            empty = true;
            return true;
        }
        return false;
    }

    public boolean isPassable() {
        if (empty || (!terrain && tower != null) || creeps != null) {
            return true;
        }
        return false;
    }

    public Tower getTower() {
        return tower;
    }

    public boolean setTower(Tower tower) {
        if (empty) {
            this.tower = tower;
            empty = false;
            return true;
        }
        return false;
    }

    public boolean removeTower() {
        if (tower != null) {
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
        if (creeps != null) {
            return creeps.first();
        }
        return null;
    }

    public boolean setCreep(Creep creep) {
        if (empty) {
            creeps = new Array<Creep>();
            creeps.add(creep);
            empty = false;
            return true;
        } else if (creeps != null) {
            creeps.add(creep);
            return true;
        }
        return false;
    }

    public int removeCreep(Creep creep) {
        if (creeps != null) {
            creeps.removeValue(creep, false);
            if (creeps.size == 0) {
                creeps = null;
                empty = true;
                return 0;
            }
            return creeps.size;
        }
        return -1;
    }

    public void dispose() {
        backgroundTiles.clear();
        foregroundTiles.clear();
        backgroundTiles = null;
        foregroundTiles = null;
        tower = null;
        creeps.clear();
        creeps = null;
    }
}
