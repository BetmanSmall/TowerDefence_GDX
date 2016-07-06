package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Андрей on 11.03.2016.
 */
public class Cell {
    private boolean empty;
    private boolean terrain;
    private Tower tower;
    private Array<Creep> creeps;

//    private char pathFinder;
//    private boolean spawnPoint;
//    private boolean exitPoint;

    Cell() {
        this.empty = true;
        this.terrain = false;
        this.tower = null;
        this.creeps = null;
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

//    public int getCreep(Creep creep)

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

//    public char getPathFinder() {
//        return pathFinder;
//    }
//
//    public void setPathFinder(char pathFinder) {
//        this.pathFinder = pathFinder;
//    }
//
//    public boolean isSpawnPoint() {
//        return spawnPoint;
//    }
//
//    public void setSpawnPoint(boolean spawnPoint) {
//        this.spawnPoint = spawnPoint;
//    }
//
//    public boolean isExitPoint() {
//        return exitPoint;
//    }
//
//    public void setExitPoint(boolean exitPoint) {
//        this.exitPoint = exitPoint;
//    }
}