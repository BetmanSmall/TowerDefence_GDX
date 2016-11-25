package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Circle; // AlexGor
import com.badlogic.gdx.math.Vector2; //AlexGor

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

    private Circle circle; //AlexGor
    private Vector2 endPoint, firstPoint; //AlexGor

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        this.position = position;
        this.damage = templateForTower.damage;
        this.radius = templateForTower.radius;
        this.reloadTime = templateForTower.reloadTime;
        this.elapsedReloadTime = 0;

        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.idleTile;
        this.projecTiles = new Array<ProjecTile>();
        this.circle = new Circle(getGraphCorX(), getGraphCorY(), radius * 30f); // AlexGor
        this.firstPoint = new Vector2(getGraphCorX() + getRegWidth(), getGraphCorY() + getRegHeight());
    }

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= reloadTime) {
            return true;
        }
        return false;
    }


    public boolean shoot(Creep creep) {
        if(elapsedReloadTime >= reloadTime) {
            projecTiles.add(new ProjecTile(this.firstPoint, creep.newPoint, creep, templateForTower)); // AlexGor
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }



    public void moveAllProjecTiles(float delta) {
        for(ProjecTile projecTile: projecTiles) {
            switch(projecTile.flightOfShell(delta)) {
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

    //AlexGor
    private float getGraphCorX () {
        int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
        float pointX = halfSizeCellX * position.y + position.x * halfSizeCellX;
        return pointX;
    }

    private float getGraphCorY () {
        int halfSizeCellY = GameField.getSizeCellY() / 2;
        float pointY = halfSizeCellY * position.y - position.x * halfSizeCellY;
        return pointY;
    }

    private float getRegWidth () {
        TextureRegion tmpTextureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
        return ((tmpTextureRegion.getRegionWidth()-(tmpTextureRegion.getRegionWidth()*templateForTower.ammoSize))/2);
    }

    private float getRegHeight () {
        TextureRegion tmpTextureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
        return ((tmpTextureRegion.getRegionHeight()-(tmpTextureRegion.getRegionHeight()*templateForTower.ammoSize))/2);
    }
    //AlexGor


    public GridPoint2 getPosition() {
        return position;
    }

    public Circle getCircle() { return circle; } //AlexGor

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
