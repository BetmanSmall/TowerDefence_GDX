package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    private GridPoint2 position;
    private int damage;
    private int radius;
    private float reloadTime;
    private float elapsedReloadTime;

    private TemplateForTower templateForTower;
    private TiledMapTile idleTile;

    public Array<ProjecTile> projecTiles;

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        this.position = position;
        this.damage = templateForTower.damage;
        this.radius = templateForTower.radius;
        this.reloadTime = templateForTower.reloadTime;
        this.elapsedReloadTime = 0;

        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.idleTile;
        this.projecTiles = new Array<ProjecTile>();
    }

    public void shot(Creep creep) {
        int halfSizeCellX = GameField.getSizeCellX()/2;
        int halfSizeCellY = GameField.getSizeCellY()/2;
//        float fVx = halfSizeCellX*newY + newX*halfSizeCellX;
//        float fVy = halfSizeCellY*newY - newX*halfSizeCellY;
        float coorX = halfSizeCellX*position.y + position.x*halfSizeCellX;
        float coorY = halfSizeCellY*position.y - position.x*halfSizeCellY;
        TextureRegion tmpTextureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
        coorX -= tmpTextureRegion.getRegionWidth()/2;
        coorY += tmpTextureRegion.getRegionHeight()/2;
        projecTiles.add(new ProjecTile(coorX, coorY, creep, templateForTower));
    }

    public void moveAllProjecTiles() {
        for(ProjecTile projecTile: projecTiles) {
            projecTile.move();
        }
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

    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }
    public float getReloadTime() {
        return reloadTime;
    }

    public void setElapsedReloadTime(float elapsedReloadTime) {
        this.elapsedReloadTime = elapsedReloadTime;
    }
    public float getElapsedReloadTime() {
        return elapsedReloadTime;
    }

    public TemplateForTower getTemplateForTower() {
        return templateForTower;
    }

    public TextureRegion getCurentFrame() {
        return idleTile.getTextureRegion();
    }

//    public void shot()
}
