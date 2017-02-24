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
    private float elapsedReloadTime;
    private TemplateForTower templateForTower;

    public int capacity;
    public Array<Shell> shells;
    public Circle radiusDetectionСircle;
    public Circle radiusFlyShellСircle;

    public Tower(GridPoint2 position, TemplateForTower templateForTower){
        Gdx.app.log("Tower", "Tower(" + position + ", " + templateForTower.toString() + ");");
        this.position = position;
        this.elapsedReloadTime = 0;
        this.templateForTower = templateForTower;

        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
        this.shells = new Array<Shell>();
        this.radiusDetectionСircle = new Circle(getTowerPosition(1), (templateForTower.radiusDetection == null) ? 0f : templateForTower.radiusDetection); // AlexGor
        if(templateForTower.shellAttackType == ShellAttackType.FirstTarget && templateForTower.radiusFlyShell != null && templateForTower.radiusFlyShell >= templateForTower.radiusDetection) {
            this.radiusFlyShellСircle = new Circle(getTowerPosition(1), templateForTower.radiusFlyShell);
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
            shells.add(new Shell(templateForTower, creep, getTowerPosition())); // AlexGor
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

    public Vector2 getTowerPosition() {
        return getTowerPosition(GameField.isDrawableTowers);
    }

    public Vector2 getTowerPosition(int map) {
        int halfSizeCellX = GameField.getSizeCellX() / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
        int halfSizeCellY = GameField.getSizeCellY() / 2;
        float pxlsX = 0f, pxlsY = 0f;
        int cellX = position.x;
        int cellY = position.y;
//        float offsetX = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellX) : ( (templateForTower.size == 1) ? 0 : (templateForTower.size-1)*halfSizeCellX));
//        float offsetY = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellY) : ( (templateForTower.size == 1) ? 0 : (templateForTower.size-1)*halfSizeCellY));
////        float offsetX = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellX) : (templateForTower.size-1)*halfSizeCellX);
////        float offsetY = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellY) : (templateForTower.size-1)*halfSizeCellY);
        if(map == 1) {
            pxlsX = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX));
            pxlsY = (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY));
        } else if(map == 2) {
            pxlsX = ( (halfSizeCellX * cellY) + (cellX * halfSizeCellX)) + halfSizeCellX;
            pxlsY = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY;
        } if(map == 3) {
            pxlsX = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX));
            pxlsY = ( (halfSizeCellY * cellY) + (cellX * halfSizeCellY)) + halfSizeCellY*2;
        } else if(map == 4) {
            pxlsX = (-(halfSizeCellX * cellY) - (cellX * halfSizeCellX)) - halfSizeCellX;
            pxlsY = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY;
        }
//        return new Vector2(pxlsX - halfSizeCellX, pxlsY + halfSizeCellY*templateForTower.size);
        return new Vector2(pxlsX, pxlsY);
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
        return templateForTower.idleTile.getTextureRegion();
    }
}
