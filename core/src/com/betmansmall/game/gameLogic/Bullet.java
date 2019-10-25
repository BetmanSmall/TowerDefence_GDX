package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class Bullet {
    public TemplateForTower templateForTower;
    public Player player;
    public Unit unit;
    public float ammoExpSize;
    public float ammoSize;
    public float ammoSpeed;

    public TextureRegion textureRegion;

    public Vector2 currentPoint;
    public Circle currCircle;
    public Vector2 endPoint;
    public Circle endCircle;
    public Vector2 velocity;

    public Direction direction;
    public Animation animation;
    public float flyingTime;

    public Bullet(Vector2 currentPoint, Tower tower, Vector2 destPoint) {
//        Logger.logFuncStart("currentPoint:" + currentPoint, "tower:" + tower, "destPoint:" + destPoint);
        this.templateForTower = tower.templateForTower;
        this.player = tower.player;
        this.unit = null;
        this.ammoExpSize = templateForTower.ammoSize;
        this.ammoSize = templateForTower.ammoSize;
        this.ammoSpeed = templateForTower.ammoSpeed;
//        Logger.logDebug("ammoExpSize:" + ammoExpSize + ",ammoSize:" + ammoSize + ",ammoSpeed:" + ammoSpeed);

        this.currentPoint = new Vector2(currentPoint);
        this.currCircle = new Circle(currentPoint, ammoSize);
        this.endPoint = new Vector2(destPoint);
        this.endCircle = new Circle(destPoint, 3f);
//        Logger.logDebug("currentPoint:" + currentPoint + " currCircle:" + currCircle);
//        Logger.logDebug("endPoint:" + endPoint + " endCircle:" + endCircle);

        velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y);
        velocity.nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
//        Logger.logDebug("velocity:" + velocity);
        setAnimation("ammo_");
    }

    public Bullet(Vector2 currentPoint, Tower tower, Unit unit) {
//        Logger.logFuncStart("currentPoint:" + currentPoint, "tower:" + tower, "unit:" + unit);
        this.templateForTower = tower.templateForTower;
        this.player = tower.player;
        this.unit = unit;
        this.ammoExpSize = templateForTower.ammoSize;
        this.ammoSize = templateForTower.ammoSize;
        this.ammoSpeed = templateForTower.ammoSpeed;

        this.currentPoint = new Vector2(currentPoint);
        this.currCircle = new Circle(currentPoint, ammoSize);
        this.endPoint = new Vector2(unit.currentPoint.x + unit.displacement.x, unit.currentPoint.y + unit.displacement.y);
        this.endCircle = new Circle(0, 0, 3f);

        if(templateForTower.towerShellType == TowerShellType.MultipleTarget || templateForTower.towerShellType == TowerShellType.FirstTarget) {
            this.endCircle.setPosition(endPoint);
        } else if(templateForTower.towerShellType == TowerShellType.AutoTarget) {
            this.endCircle.setPosition(unit.currentPoint);
        }
//        setAnimation("ammo_");
        TiledMapTile tiledMapTile = templateForTower.animations.get("ammo_" + Direction.UP);
        this.textureRegion = tiledMapTile != null ? tiledMapTile.getTextureRegion() : templateForTower.idleTile.getTextureRegion();
//        Logger.logDebug("currentPoint:" + currentPoint + ", endCircle:" + endCircle);
//        Logger.logDebug("ammoSpeed:" + ammoSpeed);

        velocity = new Vector2(endCircle.x - currentPoint.x, endCircle.y - currentPoint.y);
        velocity.nor().scl(Math.min(currentPoint.dst(endCircle.x, endCircle.y), ammoSpeed));
//        Logger.logDebug("velocity:" + velocity);
//        setAnimation("ammo_");
    }

    public void dispose() {
        this.templateForTower = null;
        this.player = null;
        this.unit = null;

        this.textureRegion = null;

        this.currentPoint = null;
        this.currCircle = null;
        this.endPoint = null;
        this.endCircle = null;
        this.velocity = null;

        this.direction = null;
        this.animation = null;
    }

    void setAnimation(String action) {
        if (velocity.x > 0) {
            if (velocity.y > 0) {
                direction = Direction.UP_RIGHT;
            } else if (velocity.y == 0) {
                direction = Direction.RIGHT;
            } else if (velocity.y < 0) {
                direction = Direction.DOWN_RIGHT;
            }
        } else if (velocity.x == 0) {
            if (velocity.y > 0) {
                direction = Direction.UP;
            } else if (velocity.y == 0) {
//                direction = Direction.IDLE;
                Gdx.app.log("Bullet::setAnimation()", "-bad- velocity:" + velocity);
            } else if (velocity.y < 0) {
                direction = Direction.DOWN;
            }
        } else if (velocity.x < 0) {
            if (velocity.y > 0) {
                direction = Direction.UP_LEFT;
            } else if (velocity.y == 0) {
                direction = Direction.LEFT;
            } else if (velocity.y < 0) {
                direction = Direction.DOWN_LEFT;
            }
        }
//        Gdx.app.log("Bullet::setAnimation()", "-- action+direction:" + action+direction );
        AnimatedTiledMapTile animatedTiledMapTile = templateForTower.animations.get(action + direction);
        if (animatedTiledMapTile != null) {
            StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            animation = new Animation(ammoSpeed / staticTiledMapTiles.length, textureRegions);
//            Gdx.app.log("Bullet::setAnimation()", "-- animation:" + animation + " textureRegions:" + textureRegions[0]);
        } else {
            Gdx.app.log("Bullet::setAnimation(" + action + direction + ")", "-- TowerName: " + templateForTower.name + " animatedTiledMapTile: " + animatedTiledMapTile);
        }
    }

    /*
     * Говорит пуле постараться достигнуть криппа.
     *
     * @param delta пока не используется. (нужна для ожидания пули времени для перемещения)
     * @return -1 - Пуля не передвинулась. Крип мертв. Нужно убрать пулю из массива пуль.<br>
     * 0 - Пуля передвинулась и достигла крипа.<br>
     * 1 - Пуля передвинулась, но не достигла крипа.<br>
     */
    public int flightOfShell(float delta, CameraController cameraController) {
//        Gdx.app.log("Bullet::flightOfShell()", "-- delta:" + delta);
//        Gdx.app.log("Bullet::flightOfShell()", "-- currentPoint:" + currentPoint);
//        Gdx.app.log("Bullet::flightOfShell()", "-- endCircle:" + endCircle);
//        Gdx.app.log("Bullet::flightOfShell()", "-- velocity:" + velocity);
//        Gdx.app.log("Bullet::flightOfShell()", "-- towerShellType:" + templateForTower.towerShellType);
//        Gdx.app.log("Bullet::flightOfShell()", "-- animation:" + animation);
//        Gdx.app.log("Bullet::flightOfShell()", "-- textureRegion:" + textureRegion);

        float displacementX = velocity.x * delta * ammoSpeed;
        float displacementY = velocity.y * delta * ammoSpeed;
        currentPoint.add(displacementX, displacementY);
        currCircle.setPosition(currentPoint);
        if (animation != null) {
            flyingTime += (ammoSpeed*delta);
            if (flyingTime >= ammoSpeed) {
                flyingTime = 0f;
            }
            textureRegion = (TextureRegion)animation.getKeyFrame(flyingTime, true);
//            Gdx.app.log("Bullet::flightOfShell()", "-- flyingTime:" + flyingTime);
//            Gdx.app.log("Bullet::flightOfShell()", "-- textureRegion:" + textureRegion);
        }
        if (templateForTower.towerShellType == TowerShellType.FirstTarget) {
            return (tryToHitUnits(cameraController) == false) ? 1 : 0;
        } else if(templateForTower.towerShellType == TowerShellType.MultipleTarget || templateForTower.towerShellType == TowerShellType.FirstTarget) {
            if (templateForTower.towerShellType == TowerShellType.MultipleTarget) {
                if(currCircle.overlaps(endCircle)) {
                    tryToHitUnits(cameraController);
                    return 0;
                }
            } else if(templateForTower.towerShellType == TowerShellType.FirstTarget) {
                if(tryToHitUnits(cameraController)) {
                    return 0;
                }
            }
            return 1;
        } else if(templateForTower.towerShellType == TowerShellType.AutoTarget) {
            if(unit.isAlive()) {
                this.endCircle.setPosition(unit.currentPoint);
                velocity = new Vector2(endCircle.x - currentPoint.x, endCircle.y - currentPoint.y);
                velocity.nor().scl(Math.min(currentPoint.dst(endCircle.x, endCircle.y), ammoSpeed));
                currentPoint.add(velocity.x * delta * ammoSpeed, velocity.y * delta * ammoSpeed);
                currCircle.setPosition(currentPoint);
                // endPoint2 == endCircle == unit.currentPoint ~= unit.circle1
                Circle unitCircle = unit.getCircle(cameraController.isDrawableUnits);
                if (unitCircle != null) {
                    if (currCircle.overlaps(unitCircle)) {
                        if (unit.die(templateForTower.damage, templateForTower.towerShellEffect)) {
                            player.gold += unit.templateForUnit.bounty;
                        }
                        return 0;
                    }
                }
                return 1;
            }
        }
        return -1;
    }

    private boolean tryToHitUnits(CameraController cameraController) {
        boolean hit = false;
        for (Unit unit : cameraController.gameField.unitsManager.units) {
            Circle unitCircle = unit.getCircle(cameraController.isDrawableUnits);
            if (unitCircle != null) {
                if (currCircle.overlaps(unitCircle)) {
                    hit = true;
                    if (unit.die(templateForTower.damage, templateForTower.towerShellEffect)) {
                        player.gold += unit.templateForUnit.bounty;
                    }
                }
            }
        }
        return hit;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bullet[");
        sb.append("unit:" + unit);
        if (full) {
            sb.append(",templateForTower:" + templateForTower);
            sb.append(",player:" + player);

            sb.append(",ammoExpSize:" + ammoExpSize);
            sb.append(",ammoSize:" + ammoSize);
            sb.append(",ammoSpeed:" + ammoSpeed);
            sb.append(",textureRegion:" + textureRegion);
            sb.append(",currentPoint:" + currentPoint);
            sb.append(",currCircle:" + currCircle);
            sb.append(",endPoint:" + endPoint);
            sb.append(",endCircle:" + endCircle);
            sb.append(",velocity:" + velocity);
            sb.append(",direction:" + direction);
            sb.append(",animation:" + animation);
            sb.append(",flyingTime:" + flyingTime);
        }
        sb.append("]");
        return sb.toString();
    }
}
