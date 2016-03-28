package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    private GridPoint2 position;
    private int damage;
    private int radius;
    private float attackSpeed;
    private float elapsedTime;

    private TemplateForTower templateForTower;
    private TiledMapTile idleTile;

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        this.position = position;
        this.damage = templateForTower.damage;
        this.radius = templateForTower.radius;
        this.attackSpeed = templateForTower.attackSpeed;
        this.elapsedTime = 0;

        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.idleTile;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getDamage() {
        return damage;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    public int getRadius() {
        return radius;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public float getElapsedTime() {
        return elapsedTime;
    }

    public TemplateForTower getTemplateForTower() {
        return templateForTower;
    }

    public TextureRegion getCurentFrame() {
        return idleTile.getTextureRegion();
    }
}
