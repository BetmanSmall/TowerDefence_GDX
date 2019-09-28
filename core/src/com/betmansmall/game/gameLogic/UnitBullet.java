package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTiledMapTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

public class UnitBullet {
//    public UnitsManager unitsManager;
    public Tower tower;
//    public float ammoExpSize;
    public float ammoSize;
    public float ammoSpeed;
    public TemplateForUnit templateForUnit;

    public TextureRegion textureRegion;

    public Vector2 currentPoint;
    public Circle currCircle;
    public Vector2 endPoint;
    public Circle endCircle;
    public Vector2 velocity;

    Direction direction;
    Animation animation;
    float flyingTime;

    public UnitBullet(Vector2 currentPoint, TemplateForUnit templateForUnit, Tower tower) {
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- currentPoint:" + currentPoint);
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- templateForUnit:" + templateForUnit.toString(true));
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- destPoint:" + destPoint);
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- cameraController:" + cameraController);
//        this.ammoExpSize = templateForUnit.weaponTemplate.properties.get("");
        this.ammoSize = templateForUnit.unitAttack.ammoSize;
        this.ammoSpeed = templateForUnit.unitAttack.ammoSpeed;
        this.templateForUnit = templateForUnit;
        this.tower = tower;
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- ammoExpSize:" + ammoExpSize);
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- ammoSize:" + ammoSize);
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- ammoSpeed:" + ammoSpeed);

        this.currentPoint = new Vector2(currentPoint);
        this.currCircle = new Circle(currentPoint, ammoSize);
        this.endPoint = new Vector2(tower.centerGraphicCoordinates);
        this.endCircle = new Circle(tower.centerGraphicCoordinates, 3f);
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- currentPoint:" + currentPoint + " currCircle:" + currCircle);
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- endPoint:" + endPoint + " endCircle:" + endCircle);

        velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y);
        velocity.nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
//        Gdx.app.log("UnitBullet::UnitBullet()", "-- velocity:" + velocity);
        setAnimation("weapon_");
    }

//    public UnitBullet(Vector2 currentPoint, TemplateForUnit templateForUnit, Tower tower, CameraController cameraController) {
////        Gdx.app.log("UnitBullet::UnitBullet()", "-- currentPoint:" + currentPoint);
////        Gdx.app.log("UnitBullet::UnitBullet()", "-- templateForUnit:" + templateForUnit);
////        Gdx.app.log("UnitBullet::UnitBullet()", "-- tower:" + tower);
////        Gdx.app.log("UnitBullet::UnitBullet()", "-- cameraController:" + cameraController);
////        this.ammoExpSize = templateForUnit.ammoSize;
//        this.ammoSize = templateForUnit.unitAttack.ammoSize;
//        this.ammoSpeed = templateForUnit.unitAttack.ammoSpeed;
//        this.templateForUnit = templateForUnit;
//        this.tower = tower;
//
//        this.currentPoint = new Vector2(currentPoint);
//        this.currCircle = new Circle(currentPoint, ammoSize);
//        this.endPoint = new Vector2(tower.centerGraphicCoord.x + tower.displacement.x, tower.centerGraphicCoord.y + tower.displacement.y);
//        this.endCircle = new Circle(0, 0, 3f);
//
//        if (templateForUnit.towerShellType == TowerShellType.MultipleTarget || templateForUnit.towerShellType == TowerShellType.FirstTarget) {
//            this.endCircle.setPosition(endPoint);
//        } else if (templateForUnit.towerShellType == TowerShellType.AutoTarget) {
//            this.endCircle.setPosition(tower.centerGraphicCoord);
//        }
////        setAnimation("ammo_");
//        TiledMapTile tiledMapTile = templateForUnit.animations.get("ammo_" + Direction.UP);
//        this.textureRegion = tiledMapTile != null ? tiledMapTile.getTextureRegion() : templateForUnit.idleTile.getTextureRegion();
//
////        Gdx.app.log("UnitBullet::UnitBullet()", "-- currentPoint:" + currentPoint + ", endCircle:" + endCircle);
////        Gdx.app.log("UnitBullet::UnitBullet()", "-- ammoSpeed:" + ammoSpeed);
//        velocity = new Vector2(endCircle.x - currentPoint.x, endCircle.y - currentPoint.y);
//        velocity.nor().scl(Math.min(currentPoint.dst(endCircle.x, endCircle.y), ammoSpeed));
////        Gdx.app.log("UnitBullet::UnitBullet()", "-- velocity:" + velocity);
////        setAnimation("ammo_");
//    }

    public void dispose() {
        tower = null;
        templateForUnit = null;
        textureRegion = null;

        currentPoint = null;
        currCircle = null;
        endPoint = null;
        endCircle = null;
        velocity = null;

        animation = null;
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
                Gdx.app.log("UnitBullet::setAnimation()", "-bad- velocity:" + velocity);
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
//        Gdx.app.log("UnitBullet::setAnimation()", "-- action+direction:" + action+direction );
        AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get(action + direction);
        if (animatedTiledMapTile != null) {
            StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            animation = new Animation((ammoSpeed / staticTiledMapTiles.length)/ammoSpeed, textureRegions);
//            Gdx.app.log("UnitBullet::setAnimation()", "-- animation:" + animation + " textureRegions:" + textureRegions[0]);
        } else {
            Gdx.app.log("UnitBullet::setAnimation(" + action + direction + ")", "-- UnitName: " + templateForUnit.name + " animatedTiledMapTile: " + animatedTiledMapTile);
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
//        Gdx.app.log("UnitBullet::flightOfShell()", "-- delta:" + delta);
//        Gdx.app.log("UnitBullet::flightOfShell()", "-- currentPoint:" + currentPoint);
//        Gdx.app.log("UnitBullet::flightOfShell()", "-- endCircle:" + endCircle);
//        Gdx.app.log("UnitBullet::flightOfShell()", "-- velocity:" + velocity);
//        Gdx.app.log("UnitBullet::flightOfShell()", "-- towerShellType:" + templateForUnit.towerShellType);
//        Gdx.app.log("UnitBullet::flightOfShell()", "-- animation:" + animation);
//        Gdx.app.log("UnitBullet::flightOfShell()", "-- textureRegion:" + textureRegion);

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
//            Gdx.app.log("UnitBullet::flightOfShell()", "-- flyingTime:" + flyingTime);
//            Gdx.app.log("UnitBullet::flightOfShell()", "-- textureRegion:" + textureRegion);
        }
        if (tower != null && tower.isNotDestroyed()) {
//            if (tower.circles != null && tower.circles.size != 0) {
                Circle towerCircle = tower.getCircle(cameraController.isDrawableTowers);
                if (towerCircle != null) {
                    if (currCircle.overlaps(towerCircle)) {
                        if (tower.destroy(templateForUnit.unitAttack.damage)) {//, templateForUnit.towerShellEffect)) {
                            cameraController.gameField.gamerGold += tower.templateForTower.cost * 0.5f;
                        }
                        return 0;
                    }
                }
//            }
            return 1;
        }
        return -1;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("UnitBullet[");
        sb.append("tower:" + tower);
        if (full) {
//            sb.append(",ammoExpSize:" + ammoExpSize);
            sb.append(",ammoSize:" + ammoSize);
            sb.append(",ammoSpeed:" + ammoSpeed);
            sb.append(",templateForUnit:" + templateForUnit);
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
