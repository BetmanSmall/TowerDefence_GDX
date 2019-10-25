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
//    public Vector2 graphicCoordinates1;
//    public Vector2 graphicCoordinates2;
//    public Vector2 graphicCoordinates3;
//    public Vector2 graphicCoordinates4;
    public Array<Vector2> graphicCoordinates;

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

//        this.graphicCoordinates1 = new Vector2();
//        this.graphicCoordinates2 = new Vector2();
//        this.graphicCoordinates3 = new Vector2();
//        this.graphicCoordinates4 = new Vector2();
        this.graphicCoordinates = new Array<Vector2>(4);
        this.graphicCoordinates.add(new Vector2());
        this.graphicCoordinates.add(new Vector2());
        this.graphicCoordinates.add(new Vector2());
        this.graphicCoordinates.add(new Vector2());
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
        if (units != null) {
            units.clear();
            units = null;
        }
//        delete graphicCoordinates1,graphicCoordinates2,graphicCoordinates3, graphicCoordinates4;
        graphicCoordinates.clear();
    }

    public void setGraphicCoordinates(int cellX, int cellY, float sizeCellX, float sizeCellY, boolean isometric) {
//        Gdx.app.log("Cell::setGraphicCoordinates(" + cellX + "," + cellY + "," + sizeCellX + ", " + sizeCellY + ", " + isometric + ")", "-- ");
        this.cellX = cellX;
        this.cellY = cellY;
        float halfSizeCellX = sizeCellX/2;
        float halfSizeCellY = sizeCellY/2;
        if (isometric) {
//        if(map == 1) { // Нижняя карта-java // Верхняя карта-с++
            Vector2 graphicCoordinates1 = graphicCoordinates.get(0);
            graphicCoordinates1.x = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX) );
            graphicCoordinates1.y = (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY) ) - halfSizeCellY;
//            this.graphicCoordinates.set(0, graphicCoordinates1);
//            this.graphicCoordinates1.set(graphicCoordinates1);
//        } else if(map == 2) { // Правая карта-(java && c++)
            Vector2 graphicCoordinates2 = graphicCoordinates.get(1);
            graphicCoordinates2.x = ( (halfSizeCellX * cellY) + (cellX * halfSizeCellX) ) + halfSizeCellX;
            graphicCoordinates2.y = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY) );
//            this.graphicCoordinates.set(1, graphicCoordinates2);
//            this.graphicCoordinates2.set(graphicCoordinates2);
//        } else if(map == 3) { // Верхняя карта-java // Нижняя карта-c++
            Vector2 graphicCoordinates3 = graphicCoordinates.get(2);
            graphicCoordinates3.x = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX) );
            graphicCoordinates3.y = ( (halfSizeCellY * cellY) + (cellX * halfSizeCellY) ) + halfSizeCellY;
//            this.graphicCoordinates.set(2, graphicCoordinates3);
//            this.graphicCoordinates3.set(graphicCoordinates3);
//        } else if(map == 4) {// Левая карта-(java && c++)
            Vector2 graphicCoordinates4 = graphicCoordinates.get(3);
            graphicCoordinates4.x = (-(halfSizeCellX * cellY) - (cellX * halfSizeCellX) ) - halfSizeCellX;
            graphicCoordinates4.y = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY) );
//            this.graphicCoordinates.set(3, graphicCoordinates4);
//            this.graphicCoordinates4.set(graphicCoordinates4);
//        }
        } else {
//        if(map == 1) { // НижняяЛевая карта-java // ВерхняяЛевая карта-с++
            Vector2 graphicCoordinates1 = graphicCoordinates.get(0);
            graphicCoordinates1.x = (-(cellX * sizeCellX) ) - halfSizeCellX;
            graphicCoordinates1.y = (-(cellY * sizeCellY) ) - halfSizeCellY;
//            this.graphicCoordinates.set(0, graphicCoordinates1);
//            this.graphicCoordinates1.set(graphicCoordinates1);
//        } else if(map == 2) { // НижняяПравая карта-java // ВерхняяПравая карта-с++
            Vector2 graphicCoordinates2 = graphicCoordinates.get(1);
            graphicCoordinates2.x = ( (cellX * sizeCellX) ) + halfSizeCellX;
            graphicCoordinates2.y = (-(cellY * sizeCellY) ) - halfSizeCellY;
//            this.graphicCoordinates.set(1, graphicCoordinates2);
//            this.graphicCoordinates2.set(graphicCoordinates2);
//        } else if(map == 3) { // ВерхняяПравая карта-java // НижняяПравая карта-с++
            Vector2 graphicCoordinates3 = graphicCoordinates.get(2);
            graphicCoordinates3.x = ( (cellX * sizeCellX) ) + halfSizeCellX;
            graphicCoordinates3.y = ( (cellY * sizeCellY) ) + halfSizeCellY;
//            this.graphicCoordinates.set(2, graphicCoordinates3);
//            this.graphicCoordinates3.set(graphicCoordinates3);
//        } else if(map == 4) {// ВерхняяЛевая карта-java // НижняяЛевая карта-c++
            Vector2 graphicCoordinates4 = graphicCoordinates.get(3);
            graphicCoordinates4.x = (-(cellX * sizeCellX) ) - halfSizeCellX;
            graphicCoordinates4.y = ( (cellY * sizeCellY) ) + halfSizeCellY;
//            this.graphicCoordinates.set(3, graphicCoordinates4);
//            this.graphicCoordinates4.set(graphicCoordinates4);
//        }
        }
    }

    public Vector2 getGraphicCoordinates(int map) {
//        if(map == 1) {
//            return graphicCoordinates1;
//        } else if(map == 2) {
//            return graphicCoordinates2;
//        } else if(map == 3) {
//            return graphicCoordinates3;
//        } else if(map == 4) {
//            return graphicCoordinates4;
//        }

//        map = (map == 5) ? 1 : map;
        if (map > 0 && map < 5) {
            return graphicCoordinates.get(map-1);
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
        return setTerrain(tile, true, true);
    }

    public boolean setTerrain(TiledMapTile tile, boolean removable, boolean withTower) {
        if (tile != null) {
            groundTiles.add(tile);
        }
        if ( (empty && !spawn && !exit) || (withTower && tower != null) ) {
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
//            groundTiles.clear();
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
            if (unit.player.playerID == 1) {
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
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Cell[");
        sb.append("cellX:" + cellX);
        sb.append(",cellY:" + cellY);
        if (full) {
            sb.append(",empty:" + empty);
            sb.append(",terrain:" + terrain);
            sb.append(",removableTerrain:" + removableTerrain);
            sb.append(",tower:" + (tower != null));
            sb.append(",units:" + ((units != null) ? units.size : false));
            sb.append(",spawn:" + spawn);
            sb.append(",exit:" + exit);
            sb.append(",backgroundTiles:" + backgroundTiles.size);
            sb.append(",groundTiles:" + groundTiles.size);
            sb.append(",foregroundTiles:" + foregroundTiles.size);
//        sb.append(",graphicCoordinates1:" + graphicCoordinates1);
        }
        sb.append("]");
        return sb.toString();
    }
}
