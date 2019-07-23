package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Circle; // AlexGor
import com.badlogic.gdx.math.Vector2; //AlexGor


/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    public Cell cell;
    public float elapsedReloadTime;
    public TemplateForTower templateForTower;

    public int player; // In Future need change to enumPlayers {Computer0, Player1, Player2} and etc
    public int capacity;
    public Array<Bullet> bullets;
    public Vector2 centerGraphicCoord;
    public Circle radiusDetectionCircle;
    public Circle radiusFlyShellCircle;

    public float hp;
    public Array<Unit> whoAttackMe;

    public Tower(Cell cell, TemplateForTower templateForTower, int player) {
//        Gdx.app.log("Tower::Tower()", "-- cell:" + cell + " templateForTower:" + templateForTower + " player:" + player);
        this.cell = cell;
        this.elapsedReloadTime = templateForTower.reloadTime;
        this.templateForTower = templateForTower;

        this.player = player;
        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
        this.bullets = new Array<Bullet>();
        this.centerGraphicCoord = new Vector2();
        this.radiusDetectionCircle = new Circle(0, 0, templateForTower.radiusDetection);
        this.radiusFlyShellCircle = null;

        this.hp = templateForTower.healthPoints;
        this.whoAttackMe = new Array<Unit>();
    }

    public void dispose() {
        Gdx.app.log("Tower::dispose()", "--");
        cell = null;
//        elapsedReloadTime = 0;
        templateForTower = null;
//        player = 0;
//        capacity = 0;
        bullets.clear();
        centerGraphicCoord = null;
        radiusDetectionCircle = null;
        if (radiusFlyShellCircle != null) {
            radiusFlyShellCircle = null;
        }
//        hp = 0;
        whoAttackMe.clear();
    }

    void updateCenterGraphicCoordinates(CameraController cameraController) {
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
        this.radiusDetectionCircle.setPosition(centerGraphicCoord);
        if (templateForTower.towerShellType == TowerShellType.FirstTarget) {
            if (templateForTower.radiusFlyShell != 0.0 && templateForTower.radiusFlyShell >= templateForTower.radiusDetection) {
                if (radiusFlyShellCircle == null) {
                    this.radiusFlyShellCircle = new Circle(centerGraphicCoord, templateForTower.radiusFlyShell);
                } else {
                    this.radiusFlyShellCircle.setPosition(centerGraphicCoord);
                }
            }
        }
    }

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            return true;
        }
        return false;
    }

    public boolean shotFireBall(CameraController cameraController) {
        if (elapsedReloadTime >= templateForTower.reloadTime) {
            if (templateForTower.towerAttackType == TowerAttackType.FireBall) {
                elapsedReloadTime = 0f;
                int radius = Math.round(cameraController.gameField.gameSettings.difficultyLevel);
                if ( radius == 0 ) {
                    radius = Math.round(templateForTower.radiusDetection);
                }
                Cell towerCell = cell;
//                Gdx.app.log("Tower::shotFireBall()", "-- radius:" + radius + " towerCell:" + towerCell + " player:" + player);
                for (int tmpX = -radius; tmpX <= radius; tmpX++) {
                    for (int tmpY = -radius; tmpY <= radius; tmpY++) {
                        Cell cell = cameraController.gameField.getCell(tmpX + towerCell.cellX, tmpY + towerCell.cellY);
                        if (cell != null && cell != towerCell) {
                            bullets.add(new Bullet(centerGraphicCoord, templateForTower, cell.graphicCoordinates1, cameraController));
                        }
                    }
                }
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.UP));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.UP_RIGHT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.RIGHT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.DOWN_RIGHT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.DOWN));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.DOWN_LEFT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.LEFT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.UP_LEFT));
//                Gdx.app.log("Tower::shotFireBall()", "-- bullets:" + bullets + " templateForTower:" + templateForTower + " player:" + player);
                return true;
            }
        }
        return false;
    }

    public boolean shoot(Unit unit, CameraController cameraController) {
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            if (templateForTower.towerShellType == TowerShellType.MassAddEffect) {
                boolean effect = false;
                for (TowerShellEffect towerShellEffect : unit.shellEffectTypes) {
                    if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
                        effect = true;
                        break;
                    }
                }
                if (!effect) {
                    unit.shellEffectTypes.add(new TowerShellEffect(templateForTower.towerShellEffect));
                }
            } else {
                bullets.add(new Bullet(centerGraphicCoord, templateForTower, unit, cameraController));
            }
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public void moveAllShells(float delta, CameraController cameraController) {
        for(Bullet bullet : bullets) {
            if(radiusFlyShellCircle == null) {
                moveShell(delta, bullet, cameraController);
            } else if(radiusFlyShellCircle.overlaps(bullet.currCircle)) {
                moveShell(delta, bullet, cameraController);
            } else {
                bullets.removeValue(bullet, false);
//                bullet.dispose();
            }
        }
    }

    private void moveShell(float delta, Bullet bullet, CameraController cameraController) {
        switch (bullet.flightOfShell(delta, cameraController)) {
            case 1:
                break;
            case 0:
//                break;
            case -1:
                bullets.removeValue(bullet, false);
                break;
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
            sb.append(",elapsedReloadTime:" + elapsedReloadTime);
            sb.append(",templateForTower:" + templateForTower);
            sb.append(",player:" + player);
            sb.append(",capacity:" + capacity);
            sb.append(",bullets.size:" + bullets.size);
            sb.append(",radiusDetectionCircle:" + radiusDetectionCircle);
            sb.append(",radiusFlyShellCircle:" + radiusFlyShellCircle);
        }
        sb.append("]");
        return sb.toString();
    }
}
