package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Андрей on 10.03.2016.
 */
public class CellManager {

    private int width;
    private int height;

    private Cell[][] cells;

    CellManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
    }


    public int getWidth () {
        return width;
    }

    public int getHeight () {
        return height;
    }

    public Cell getCell (int x, int y) {
        if (x < 0 || x >= width) return null;
        if (y < 0 || y >= height) return null;
        return cells[x][y];
    }

    public void setCell (int x, int y, Cell cell) {
        if (x < 0 || x >= width) return;
        if (y < 0 || y >= height) return;
        cells[x][y] = cell;
    }

    public char[][] getCharMatrix() {
        char[][] charMatrix = new char[width][height];
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                charMatrix[x][y] = getCell(x, y).getPathFinder();
            }
        }
        return charMatrix;
    }

    public static class Cell extends TiledMapTileLayer.Cell {

        private Tower tower;

        private Array<Creep> creeps;

        private char pathFinder;

        private boolean spawnPoint;

        private boolean exitPoint;

        Cell() {
            creeps = new Array<Creep>();
        }

        public Array<Creep> getCreeps() {
            return creeps;
        }

        public void addCreep(Creep creep) {
            creeps.add(creep);
        }

        public void removeCreep(Creep creep) {
            creeps.removeValue(creep, false);
        }

        public Tower getTower() {
            return tower;
        }

        public void setTower(Tower tower) {
            this.tower = tower;
        }

        public char getPathFinder() {
            return pathFinder;
        }

        public void setPathFinder(char pathFinder) {
            this.pathFinder = pathFinder;
        }

        public boolean isSpawnPoint() {
            return spawnPoint;
        }

        public void setSpawnPoint(boolean spawnPoint) {
            this.spawnPoint = spawnPoint;
        }

        public boolean isExitPoint() {
            return exitPoint;
        }

        public void setExitPoint(boolean exitPoint) {
            this.exitPoint = exitPoint;
        }
        public boolean isEmpty() {
            if (!isFlora() && !isCreep() && !isTower()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isFlora() {
            if(getPathFinder() == '.') {
                return false;
            }
            else {
                return true;
            }
        }

        public boolean isCreep() {
            if(creeps.size == 0) {
                return false;
            } else {
                return true;
            }
        }

        public boolean isTower() {
            if(tower != null) {
                return true;
            } else {
                return false;
            }
        }

    }
}
