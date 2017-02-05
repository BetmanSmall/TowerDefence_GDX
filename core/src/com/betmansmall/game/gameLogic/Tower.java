package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.ShellAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Circle; // AlexGor
import com.badlogic.gdx.math.Vector2; //AlexGor

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    private GridPoint2 position;
//    private int damage;
//    private int radiusDetection;
//    private float radiusFlyShell;
//    private float reloadTime;
    private float elapsedReloadTime;

    private TemplateForTower templateForTower;
    private TiledMapTile idleTile;
    public int capacity;

    public Circle radiusDetectionСircle; //AlexGor
    public Circle radiusFlyShellСircle;
    public Array<Shell> shells;

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        Gdx.app.log("Tower", "Tower(" + position + ", " + templateForTower.toString() + ");");
        this.position = position;
//        this.damage = templateForTower.damage;
//        this.radiusDetection = templateForTower.radiusDetection;
//        this.radiusFlyShell = templateForTower.radiusFlyShell;
//        this.reloadTime = templateForTower.reloadTime;
        this.elapsedReloadTime = 0;

        this.templateForTower = templateForTower;
        this.idleTile = templateForTower.idleTile;
        this.shells = new Array<Shell>();

        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
        this.shells = new Array<Shell>();
        this.radiusDetectionСircle = new Circle(getGraphCorX(), getGraphCorY(), (templateForTower.radiusDetection == null) ? 0f : templateForTower.radiusDetection); // AlexGor
        if(templateForTower.shellAttackType == ShellAttackType.FirstTarget && templateForTower.radiusFlyShell != null ) {
            this.radiusFlyShellСircle = new Circle(getGraphCorX(), getGraphCorY(), templateForTower.radiusFlyShell);
        }
    }

    public void dispose() {
    }

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            return true;
        }
        return false;
    }

    public boolean shoot(Creep creep) {
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            shells.add(new Shell(templateForTower, creep, new Vector2(getGraphCorX(), getGraphCorY()))); // AlexGor
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public void moveAllShells(float delta) {
        for(Shell shell : shells) {
            if(radiusFlyShellСircle == null) {
                moveShell(delta, shell);
            } else if(Intersector.overlaps(shell.circle, radiusFlyShellСircle)) {
                moveShell(delta, shell);
            } else {
                shell.dispose();
                shells.removeValue(shell, false);
            }
        }
    }

    private void moveShell(float delta, Shell shell) {
        switch (shell.flightOfShell(delta)) {
            case 0:
//                if(shell.creep.die(damage)) {
//                    GameField.gamerGold += shell.creep.getTemplateForUnit().bounty;
//                }
//                break;
            case -1:
                shell.dispose();
                shells.removeValue(shell, false);
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

    public Circle getRadiusDetectionСircle() {
        return radiusDetectionСircle;
    } //AlexGor

//    public void setDamage(int damage) {
//        this.damage = damage;
//    }
    public int getDamage() {
        return templateForTower.damage;
    }

//    public void setRadiusDetection(int radiusDetection) {
//        this.radiusDetection = radiusDetection;
//    }
    public int getRadiusDetection() {
        return templateForTower.radiusDetection;
    }

//    public void setReloadTime(float reloadTime) {
//        this.reloadTime = reloadTime;
//    }
//    public float getReloadTime() {
//        return reloadTime;
//    }

    public TemplateForTower getTemplateForTower() {
        return templateForTower;
    }

    public TextureRegion getCurentFrame() {
        return idleTile.getTextureRegion();
    }
}
