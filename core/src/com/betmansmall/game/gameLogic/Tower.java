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
    private TemplateForTower templateForTower;
    private TiledMapTile idleTile;
    private float attackSpeed;
    private float elapsedTime;
    private int damage;
    private int radius;

    private TiledMapTileLayer layer;

    public Tower(GridPoint2 position, TiledMapTileLayer layer, TemplateForTower templateForTower){
        this.position = position;
        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.getIdleTile();
        this.radius = templateForTower.getRadius();
        this.attackSpeed = templateForTower.getAttack();
        this.damage = templateForTower.getDamage();
        this.elapsedTime = 0;

        this.layer = layer;
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(idleTile);
        getLayer().setCell(position.x, position.y, cell);
    }

    public TiledMapTileLayer getLayer() {
        return layer;
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


    public boolean deleteTower() {
        getLayer().setCell(getPosition().x, getPosition().y, null);
        return true;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
