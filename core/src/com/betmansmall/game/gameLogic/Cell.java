package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

import com.betmansmall.game.gameLogic.mapLoader.Tile;

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
    public Array<TiledMapTile> groundTiles;
    public Array<TiledMapTile> foregroundTiles;
    public Array<Tree> trees;
    private boolean empty;
    private boolean terrain;
    private boolean removableTerrain;

    private Tower tower;
    private Array<Unit> units;
    public boolean spawn, exit;

    public int cellX, cellY;
    public Vector2 graphicCoordinatesBottom, graphicCoordinatesRight, graphicCoordinatesTop, graphicCoordinatesLeft;


    public Cell() {
//        Gdx.app.log("Cell::Cell()", "-- ");
        this.backgroundTiles = new Array<TiledMapTile>();
        this.groundTiles = new Array<TiledMapTile>();
        this.foregroundTiles = new Array<TiledMapTile>();
        this.trees = new Array<Tree>();
        this.empty = true;
        this.terrain = false;
        this.removableTerrain = true;

        this.tower = null;
        this.units = null;
        this.spawn = false;
        this.exit = false;

//        setGraphicCoordinates(cellX, cellY, halfSizeCellX, halfSizeCellY);
    }

    public void dispose() {
        backgroundTiles.clear();
        foregroundTiles.clear();
        trees.clear();
        backgroundTiles = null;
        foregroundTiles = null;
        trees = null;

        tower = null;
        units.clear();
        units = null;
//        delete graphicCoordinatesBottom,graphicCoordinatesRight,graphicCoordinatesTop, graphicCoordinatesLeft;
    }

    public void setGraphicCoordinates(int cellX, int cellY, float halfSizeCellX, float halfSizeCellY) {
//        Gdx.app.log("Cell::setGraphicCoordinates(" + cellX + "," + cellY + "," + halfSizeCellX + ", " + halfSizeCellY + ")", "-- ");
        this.cellX = cellX;
        this.cellY = cellY;
//        if(map == 1) { // Нижняя карта-java // Верхняя карта-с++
        graphicCoordinatesBottom = new Vector2((-(halfSizeCellX * cellY) + (cellX * halfSizeCellX)), (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY)));
//        } else if(map == 2) { // Правая карта
        graphicCoordinatesRight = new Vector2(((halfSizeCellX * cellY) + (cellX * halfSizeCellX)) + halfSizeCellX, ((halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY);
//        } else if(map == 3) { // Верхняя карта-c++ // Нижняя карта-java
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
        Gdx.app.log("Cell::getGraphicCoordinates(" + map + ")", "-- Bad map | return null!");
        return null;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isTerrain() {
        return terrain;
    }

    public boolean setTerrain(TiledMapTile tile) {
        return setTerrain(tile, true);
    }

    public boolean setTerrain(TiledMapTile tile, boolean removable) {
        if (tile != null) {
            groundTiles.add(tile);
        }
        if (empty && !spawn && !exit) {
            removableTerrain = removable;
            terrain = true;
            empty = false;
            return true;
        }
        return false;
    }

    public boolean removeTerrain(boolean force) {
        if (terrain && (removableTerrain || force) ) {
            terrain = false;
            empty = true;
            return true;
        }
        return false;
    }

    public boolean isPassable() {
        if (empty || (!terrain && tower != null) || units != null) {
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

    public Unit getHero() {
        for (Unit unit : units) {
            if (unit.player == 1) {
                return unit;
            }
        }
        return null;
    }

    public Array<Unit> getUnits() {
        return units;
    }

    public Unit getUnit() {
        if (units != null) {
            return units.first();
        }
        return null;
    }

    public boolean setUnit(Unit unit) {
        if (empty) {
            units = new Array<Unit>();
            units.add(unit);
            empty = false;
            return true;
        } else if (units != null) {
            units.add(unit);
            return true;
        }
        return false;
    }

    public int containUnit(Unit unit) {
        if(units != null) {
            int size = units.size;
            if(unit == null) {
                return size;
            } else {
                for(int k = 0; k < size; k++) {
                    if(units.get(k).equals(unit)) { // OR if (units.get(k) == unit)
                        return k+1;
                    }
                }
            }
        }
        return 0;
    }

    public int removeUnit(Unit unit) {
        if (units != null) {
            units.removeValue(unit, false);
            if (units.size == 0) {
                units = null;
                empty = true;
                return 0;
            }
            return units.size;
        }
        return -1;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cell[");
        sb.append("cellX:" + cellX);
        sb.append(",cellY:" + cellY);
        sb.append(",empty:" + empty);
        sb.append(",terrain:" + terrain);
        sb.append(",removableTerrain:" + removableTerrain);
        sb.append(",tower:" + tower);
        sb.append(",units:" + units);
        sb.append(",spawn:" + spawn);
        sb.append(",exit:" + exit);
        sb.append(",backgroundTiles:" + backgroundTiles.size);
        sb.append(",groundTiles:" + groundTiles.size);
        sb.append(",foregroundTiles:" + foregroundTiles.size);
//        sb.append(",graphicCoordinatesBottom:" + graphicCoordinatesBottom);
        sb.append("]");
        return sb.toString();
    }
}
