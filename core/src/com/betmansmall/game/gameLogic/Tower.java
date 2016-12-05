package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
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
    public int capacity;

    public Array<Shell> shells;

    private Circle circle; //AlexGor
//    private Vector2 bulletSpawnPoint; //AlexGor

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        this.position = position;
        this.damage = templateForTower.damage;
        this.radius = templateForTower.radius;
        this.reloadTime = templateForTower.reloadTime;
        this.elapsedReloadTime = 0;

        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.idleTile;
        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
        this.shells = new Array<Shell>();
        this.circle = new Circle(getGraphCorX(), getGraphCorY(), templateForTower.radius); // AlexGor
//        this.bulletSpawnPoint = new Vector2(getGraphCorX(), getGraphCorY());
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
            shells.add(new Shell(new Vector2(getGraphCorX(), getGraphCorY()), new Vector2(creep.newPoint), creep, templateForTower)); // AlexGor
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }



    public void moveAllProjecTiles(float delta) {
        for(Shell shell : shells) {
            switch(shell.flightOfShell(delta)) {
                case 0:
//                    if(shell.creep.die(damage)) {
//                        GameField.gamerGold += shell.creep.getTemplateForUnit().bounty;
//                    }
//                    break;
                case -1:
                    shell.dispose();
                    shells.removeValue(shell, false);
            }
        }
    }

    //AlexGor
    public float getGraphCorX () {
        int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
        float pointX = halfSizeCellX * position.y + position.x * halfSizeCellX;
        return pointX + halfSizeCellX;
    }

    public float getGraphCorY () {
        int halfSizeCellY = GameField.getSizeCellY() / 2;
        float pointY = halfSizeCellY * position.y - position.x * halfSizeCellY;
        return pointY + halfSizeCellY*templateForTower.size;
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

    public Circle getCircle() {
        return circle;
    } //AlexGor

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
