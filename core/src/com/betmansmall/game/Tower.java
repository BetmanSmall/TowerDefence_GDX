package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower extends Sprite{
    int hp;
    boolean alive;
    int number;
    private TiledMapTileLayer collisionLayer;
    public Tower(Sprite sprite, TiledMapTileLayer collisionLayer){
        super(sprite);
        setCollisionLayer(collisionLayer);

    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void update(float delta){

    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }
    public Vector2 coordinatesConverter(int x, int y) {
        Vector2 point = new Vector2();
        point.add((x * getCollisionLayer().getTileWidth() /2.0f ) + (y * getCollisionLayer().getTileWidth() / 2.0f),
                - (x * getCollisionLayer().getTileHeight() / 2.0f) + (y * getCollisionLayer().getTileHeight() /2.0f));
        return point;
    }
}
