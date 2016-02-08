package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

import java.awt.Point;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    int hp;
    int id;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTile tile;

    private GridPoint2 position;

    public Tower(TiledMapTileLayer collisionLayer, TiledMapTile tile, GridPoint2 position){
        setCollisionLayer(collisionLayer);
        this.tile = tile;
        this.position = position;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        getCollisionLayer().setCell(position.x, position.y, cell);
        cell.setTile(tile);
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }
}
