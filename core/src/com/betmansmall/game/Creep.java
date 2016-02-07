package com.betmansmall.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.awt.Point;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class  Creep {
    int hp;
    boolean alive;
    int number;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTile tile;
    private Vector2 velocity = new Vector2();

    private Point position;
    public Creep(TiledMapTileLayer collisionLayer, TiledMapTile tile){
        setCollisionLayer(collisionLayer);
        this.tile = tile;
        this.alive = true;
    }
    public Creep(TiledMapTileLayer collisionLayer, TiledMapTile tile, Point position){
        setCollisionLayer(collisionLayer);
        this.tile = tile;
        this.alive = true;
        this.position = position;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell((int) position.x, (int) position.y, cell);
        cell.setTile(tile);
    }

    public void moveTo(Point position) {
        getCollisionLayer().setCell((int) this.position.x, (int) this.position.y, null);
        this.position = position;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell((int) position.x, (int) position.y, cell);
        cell.setTile(this.tile);
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
