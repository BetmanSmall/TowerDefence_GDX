package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
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
//    public GridPoint2 cell;
    public Cell cell;
    public float elapsedReloadTime;
    public TemplateForTower templateForTower;

    public int player; // In Future need change to enumPlayers {Computer0, Player1, Player2} and etc
    public int capacity;
    public Array<Bullet> bullets;
    public Vector2 centerGraphicCoord;
    public Circle radiusDetectionCircle;
    public Circle radiusFlyShellCircle;

    public Tower(Cell cell, TemplateForTower templateForTower, int player) {
        Gdx.app.log("Tower::Tower()", "-- cell:" + cell + " templateForTower:" + templateForTower + " player:" + player);
        this.cell = cell;
        this.elapsedReloadTime = templateForTower.reloadTime;
        this.templateForTower = templateForTower;

        this.player = player;
        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
        this.bullets = new Array<Bullet>();
        this.centerGraphicCoord = new Vector2();
        this.radiusDetectionCircle = new Circle(0, 0, templateForTower.radiusDetection);
        this.radiusFlyShellCircle = new Circle(0, 0, templateForTower.radiusFlyShell);
    }

    public void dispose() {
        Gdx.app.log("Tower::dispose()", "--");
    }

    void updateCenterGraphicCoordinates(CameraController cameraController) {
//        if (centerGraphicCoord == null) {
            if (cameraController.isDrawableTowers == 1 || cameraController.isDrawableTowers == 5) {
                centerGraphicCoord.set(cell.graphicCoordinates1);
            } else if (cameraController.isDrawableTowers == 2) {
                centerGraphicCoord.set(cell.graphicCoordinates2);
            } else if (cameraController.isDrawableTowers == 3) {
                centerGraphicCoord.set(cell.graphicCoordinates3);
            } else if (cameraController.isDrawableTowers == 4) {
                centerGraphicCoord.set(cell.graphicCoordinates4);
            } else {
                centerGraphicCoord.setZero();
            }
//        }
        this.radiusDetectionCircle.setPosition(centerGraphicCoord);
        if (templateForTower.shellAttackType == ShellAttackType.FirstTarget && templateForTower.radiusFlyShell != 0.0 && templateForTower.radiusFlyShell >= templateForTower.radiusDetection) {
            this.radiusFlyShellCircle.setPosition(centerGraphicCoord);
        }
    }

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            return true;
        }
        return false;
    }

    public boolean shoot(Unit unit, CameraController cameraController) {
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
                bullets.add(new Bullet(centerGraphicCoord, templateForTower, unit, cameraController));
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

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tower[");
        sb.append("cell:" + cell);
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
