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

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= reloadTime) {
//            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public boolean shoot(Creep creep) {
        if(elapsedReloadTime >= reloadTime) {
            int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
            int halfSizeCellY = GameField.getSizeCellY() / 2;
//        float fVx = halfSizeCellX*newY + newX*halfSizeCellX;
//        float fVy = halfSizeCellY*newY - newX*halfSizeCellY;
            float coorX = halfSizeCellX * position.y + position.x * halfSizeCellX;
            float coorY = halfSizeCellY * position.y - position.x * halfSizeCellY;
            TextureRegion tmpTextureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
            coorX += (tmpTextureRegion.getRegionWidth()-(tmpTextureRegion.getRegionWidth()*templateForTower.ammoSize))/2;
            coorY += (tmpTextureRegion.getRegionHeight()-(tmpTextureRegion.getRegionHeight()*templateForTower.ammoSize))/2;
            projecTiles.add(new ProjecTile(coorX, coorY, creep, templateForTower));
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public void moveAllProjecTiles(float delta) {
        for(ProjecTile projecTile: projecTiles) {
            switch(projecTile.hasReached(delta)) {
                case 0:
                    if(projecTile.creep.die(damage)) {
                        GameField.gamerGold += projecTile.creep.getTemplateForUnit().bounty;
                    }
                case -1:
                    projecTile.dispose();
                    projecTiles.removeValue(projecTile, false);
            }
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

    public TemplateForTower getTemplateForTower() {
        return templateForTower;
    }

    public TextureRegion getCurentFrame() {
        return idleTile.getTextureRegion();
    }
}
