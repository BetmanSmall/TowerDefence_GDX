package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Timer;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    private GridPoint2 position;
//    private int id;
    private TemplateForTower templateForTower;
    private TiledMapTile idleTile;

//    private Timer.Task timerForCreeps;

    private TiledMapTileLayer layer;

    public Tower(GridPoint2 position, TiledMapTileLayer layer, TemplateForTower templateForTower){
        this.position = position;
        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.getIdleTile();

        this.layer = layer;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(idleTile);
        getLayer().setCell(position.x, position.y, cell);
    }

    public TiledMapTileLayer getLayer() {
        return layer;
    }

//    public int getDamage() {
//        return damage;
//    }
//
//    public void setDamage(int damage) {
//        this.damage = damage;
//    }
//
//    public int getRadius() {
//        return radius;
//    }
//
//    public void setRadius(int radius) {
//        this.radius = radius;
//    }

    public GridPoint2 getPosition() {
        return position;
    }

//    public void setPosition(GridPoint2 position) {
//        this.position = position;
//    }

//    public float getAttackSpeed() {
//        return attackSpeed;
//    }

//    public void setAttackSpeed(float attackSpeed) {
//        this.attackSpeed = attackSpeed;
//    }

//    public void createTimerForTowers(){
//        if(timerForCreeps == null) {
//            timerForCreeps = Timer.schedule(new Timer.Task() {
//                @Override
//                public void run() {
//                    Gdx.app.log("Timer", "for Towerss!");
//
////                    GameField.attackCreep(getPosition());
//                }
//            }, 0, getAttackSpeed());
//        }
//    }

//    public void stopTimerForTowers() {
//        timerForCreeps.cancel();
//        timerForCreeps = null;
//    }

    public boolean deleteTower() {
        getLayer().setCell(getPosition().x, getPosition().y, null);
        return true;
    }
}
