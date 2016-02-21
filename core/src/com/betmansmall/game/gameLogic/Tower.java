package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Timer;
import com.betmansmall.game.CollisionDetection;

import java.awt.Point;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    private int damage;
    private int radius;
    private float attackSpeed;
//    int hp;
//    int id;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTile tile;
    private Timer.Task timerForCreeps;

    private GridPoint2 position;

    public Tower(TiledMapTileLayer collisionLayer, TiledMapTile tile, GridPoint2 position){
        setCollisionLayer(collisionLayer);
        this.tile = tile;
        setPosition(position);
        setDamage(100);
        setRadius(1);
        setAttackSpeed(2);
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

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setPosition(GridPoint2 position) {
        this.position = position;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public void createTimerForTowers(){
        if(timerForCreeps == null) {
            timerForCreeps = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.log("Timer", "for Towerss!");

                    //GameField.attackCreep(getPosition());
                }
            }, 0, getAttackSpeed());
        }
    }

    public void stopTimerForTowers() {
        timerForCreeps.cancel();
        timerForCreeps = null;
    }

    public boolean deleteTower() {
        getCollisionLayer().setCell(getPosition().x, getPosition().y, null);
        return true;
    }
}
