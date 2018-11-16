package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by BetmanSmall on 11.03.2016.
 */
public class Cell {
    public class Tree {
        public TextureRegion textureRegion;
        public int width;
        public int height;

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
    public boolean empty;
    public boolean terrain;
    public boolean removableTerrain;

    public Tower tower;
    public Array<Unit> units;
    public boolean spawn;
    public boolean exit;

    public int cellX, cellY;
    public Vector2 graphicCoordinates1;
    public Vector2 graphicCoordinates2;
    public Vector2 graphicCoordinates3;
    public Vector2 graphicCoordinates4;

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

        graphicCoordinates1 = new Vector2();
        graphicCoordinates2 = new Vector2();
        graphicCoordinates3 = new Vector2();
        graphicCoordinates4 = new Vector2();
    }

    public void dispose() {
        backgroundTiles.clear();
        groundTiles.clear();
        foregroundTiles.clear();
        trees.clear();
        backgroundTiles = null;
        groundTiles = null;
        foregroundTiles = null;
        trees = null;

        tower = null;
        units.clear();
        units = null;
//        delete graphicCoordinates1,graphicCoordinates2,graphicCoordinates3, graphicCoordinates4;
    }

    public void setGraphicCoordinates(int cellX, int cellY, float halfSizeCellX, float halfSizeCellY) {
//        Gdx.app.log("Cell::setGraphicCoordinates(" + cellX + "," + cellY + "," + halfSizeCellX + ", " + halfSizeCellY + ")", "-- ");
        this.cellX = cellX;
        this.cellY = cellY;
//        if(map == 1) { // Нижняя карта-java // Верхняя карта-с++
        graphicCoordinates1.x = ( (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX) ) );
        graphicCoordinates1.y = ( (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY) ) - halfSizeCellY );
//        } else if(map == 2) { // Правая карта
        graphicCoordinates2.x = ( ( (halfSizeCellX * cellY) + (cellX * halfSizeCellX) ) + halfSizeCellX );
        graphicCoordinates2.y = ( ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY) ) );
//        } else if(map == 3) { // Верхняя карта-c++ // Нижняя карта-java
        graphicCoordinates3.x = ( (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX) ) );
        graphicCoordinates3.y = ( ( (halfSizeCellY * cellY) + (cellX * halfSizeCellY) ) + halfSizeCellY );
//        } else if(map == 4) {// Левая карта
        graphicCoordinates4.x = ( (-(halfSizeCellX * cellY) - (cellX * halfSizeCellX) ) - halfSizeCellX );
        graphicCoordinates4.y = ( ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY) ) );
//        }
    }

    public Vector2 getGraphicCoordinates(int map) {
        if(map == 1) {
            return graphicCoordinates1;
        } else if(map == 2) {
            return graphicCoordinates2;
        } else if(map == 3) {
            return graphicCoordinates3;
        } else if(map == 4) {
            return graphicCoordinates4;
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

    public boolean removeTerrain() {
        return removeTerrain(false);
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

    public int removeUnit() {
        return removeUnit(null);
    }

    public int removeUnit(Unit unit) {
        if (!empty && units != null) {
            if (unit == null) {
                units.clear();
            } else /*if (int num = containUnit(unit))*/ {
                if (units.contains(unit, false)) {
                    units.removeValue(unit, false);
                }
            }
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
        sb.append(",units:" + ((units!=null) ? units.size : false) );
        sb.append(",spawn:" + spawn);
        sb.append(",exit:" + exit);
        sb.append(",backgroundTiles:" + backgroundTiles.size);
        sb.append(",groundTiles:" + groundTiles.size);
        sb.append(",foregroundTiles:" + foregroundTiles.size);
//        sb.append(",graphicCoordinates1:" + graphicCoordinates1);
        sb.append("]");
        return sb.toString();
    }
}
