package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

import java.awt.Point;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class  Creep {
    private int hp;
    private boolean alive;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTile tile;

    private GridPoint2 position;

    public Creep() {

    }

    public Creep(TiledMapTileLayer collisionLayer, TiledMapTile tile, GridPoint2 position){
        setCollisionLayer(collisionLayer);
        this.tile = tile;
        setAlive(true);
        setHp(100);
        setPosition(position);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
        cell.setTile(tile);
    }

    public void moveTo(GridPoint2 position) {
        getCollisionLayer().setCell(this.position.x, this.position.y, null);
        this.position = position;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
        cell.setTile(this.tile);
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setPosition(GridPoint2 position) {
        this.position = position;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

}
