package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Андрей on 11.03.2016.
 */
public class Cell extends TiledMapTileLayer.Cell {

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
         return pathFinder == '.' && creeps.size == 0 && tower==null && !spawnPoint && !exitPoint;
    }

    public boolean isTerrain() {
        return pathFinder != '.';
    }

    public boolean isCreep() {
        return creeps.size != 0;
    }

    public boolean isTower() {
         return tower != null;
    }

}
