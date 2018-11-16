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
        this.radiusDetectionCircle = null;
        this.radiusFlyShellCircle = null;
    }

    public void dispose() {
        Gdx.app.log("Tower::dispose()", "--");
    }

    void updateGraphicCoordinates(CameraController cameraController) {
        if (radiusDetectionCircle != null) {
            radiusDetectionCircle = null; // delete radiusDetectionCircle;
        }
        this.radiusDetectionCircle = new Circle(cameraController.getCenterTowerGraphicCoord(position.x, position.y), templateForTower.radiusDetection);
        if (templateForTower.shellAttackType == ShellAttackType.FirstTarget && templateForTower.radiusFlyShell != 0.0 && templateForTower.radiusFlyShell >= templateForTower.radiusDetection) {
            if (radiusFlyShellCircle != null) {
                radiusFlyShellCircle = null; // delete radiusFlyShellCircle;
            }
            this.radiusFlyShellCircle = new Circle(cameraController.getCenterTowerGraphicCoord(position.x, position.y), templateForTower.radiusFlyShell);
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
                bullets.add(new Bullet(cameraController.getCenterTowerGraphicCoord(position.x, position.y), templateForTower, unit));
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
