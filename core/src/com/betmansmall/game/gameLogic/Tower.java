package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.playerTemplates.ShellAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.ShellEffectType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Circle; // AlexGor
import com.badlogic.gdx.math.Vector2; //AlexGor

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    public GridPoint2 position;
    public float elapsedReloadTime;
    public TemplateForTower templateForTower;

    public int player; // In Future need change to enumPlayers {Computer0, Player1, Player2} and etc
    public int capacity;
    public Array<Bullet> bullets;
    public Circle radiusDetectionCircle;
    public Circle radiusFlyShellCircle;

    public Tower(GridPoint2 position, TemplateForTower templateForTower, int player) {
        Gdx.app.log("Tower::Tower()", "-- position:" + position + " templateForTower:" + templateForTower + " player:" + player);
        this.position = position;
        this.elapsedReloadTime = templateForTower.reloadTime;
        this.templateForTower = templateForTower;

        this.player = player;
        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
        this.bullets = new Array<Bullet>();
        this.radiusDetectionCircle = new Circle(getCenterGraphicCoord(1), (templateForTower.radiusDetection == null) ? 0f : templateForTower.radiusDetection); // AlexGor
        if(templateForTower.shellAttackType == ShellAttackType.FirstTarget && templateForTower.radiusFlyShell != null && templateForTower.radiusFlyShell >= templateForTower.radiusDetection) {
            this.radiusFlyShellCircle = new Circle(getCenterGraphicCoord(1), templateForTower.radiusFlyShell);
        }
    }

    public void dispose() {
        Gdx.app.log("Tower::dispose()", "--");
    }

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            return true;
        }
        return false;
    }

    public boolean shoot(Unit unit) {
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            if (templateForTower.shellAttackType == ShellAttackType.MassAddEffect) {
                boolean effect = false;
                for (ShellEffectType shellEffectType : unit.shellEffectTypes) {
                    if (shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                        effect = true;
                        break;
                    }
                }
                if (!effect) {
                    unit.shellEffectTypes.add(new ShellEffectType(templateForTower.shellEffectType));
                }
            } else if (templateForTower.shellAttackType == ShellAttackType.FireBall) {

            } else {
                bullets.add(new Bullet(getCenterGraphicCoord(), templateForTower, unit));
            }
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public void moveAllShells(float delta) {
        for(Bullet bullet : bullets) {
            if(radiusFlyShellCircle == null) {
                moveShell(delta, bullet);
            } else if(Intersector.overlaps(bullet.circle, radiusFlyShellCircle)) {
                moveShell(delta, bullet);
            } else {
                bullet.dispose();
                bullets.removeValue(bullet, false);
            }
        }
    }

    private void moveShell(float delta, Bullet bullet) {
        switch (bullet.flightOfShell(delta)) {
            case 0:
//                if(bullet.unit.die(damage)) {
//                    GameField.gamerGold += bullet.unit.getTemplateForUnit().bounty;
//                }
//                break;
            case -1:
                bullet.dispose();
                bullets.removeValue(bullet, false);
        }
    }

    public Vector2 getCenterGraphicCoord() {
        return getCenterGraphicCoord(GameField.isDrawableTowers);
    }

    public Vector2 getCenterGraphicCoord(int map) {
        return getCenterGraphicCoord(position.x, position.y, map);
    }

    public Vector2 getCenterGraphicCoord(int cellX, int cellY, int map) { // TODO need create 'getCenterGraphicCoord(int map)' func!
        int halfSizeCellX = GameField.sizeCellX / 2; // TODO ПЕРЕОСМЫСЛИТЬ!
        int halfSizeCellY = GameField.sizeCellY / 2;
        float pxlsX = 0f, pxlsY = 0f;
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
        } else if(map == 3) {
            pxlsX = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX));
            pxlsY = ( (halfSizeCellY * cellY) + (cellX * halfSizeCellY)) + halfSizeCellY*2;
        } else if(map == 4) {
            pxlsX = (-(halfSizeCellX * cellY) - (cellX * halfSizeCellX)) - halfSizeCellX;
            pxlsY = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY)) + halfSizeCellY;
        }
//        return new Vector2(pxlsX - halfSizeCellX, pxlsY + halfSizeCellY*templateForTower.size);
        return new Vector2(pxlsX, pxlsY);
    } // -------------------------------------------------------------- TODD It is analog GameField::getGraphicCoordinates() func!

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tower[");
        sb.append("position:" + position);
        if (full) {
            sb.append("elapsedReloadTime:" + elapsedReloadTime);
            sb.append("templateForTower:" + templateForTower);
            sb.append("player:" + player);
            sb.append("capacity:" + capacity);
            sb.append("bullets.size:" + bullets.size);
            sb.append("radiusDetectionCircle:" + radiusDetectionCircle);
            sb.append("radiusFlyShellCircle:" + radiusFlyShellCircle);
        }
        sb.append("]");
        return sb.toString();
    }
}
