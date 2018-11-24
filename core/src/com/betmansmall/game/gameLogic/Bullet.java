package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTile;
import com.betmansmall.game.gameLogic.mapLoader.Tile;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class Bullet {
//    public UnitsManager unitsManager;
    public Unit unit;
    public float ammoExpSize;
    public float ammoSize;
    public float ammoSpeed;
    public TemplateForTower templateForTower;

    public TextureRegion textureRegion;

    public Vector2 currentPoint;
    public Circle currCircle;
    public Vector2 endPoint;
    public Circle endCircle;
    public Vector2 velocity;

    Direction direction;
    Animation animation;
    float flyingTime;

    public Bullet(Vector2 currentPoint, TemplateForTower templateForTower, Vector2 destPoint, CameraController cameraController) {
        Gdx.app.log("Bullet::Bullet()", "-- currentPoint:" + currentPoint);
        Gdx.app.log("Bullet::Bullet()", "-- templateForTower:" + templateForTower);
        Gdx.app.log("Bullet::Bullet()", "-- destPoint:" + destPoint);
        Gdx.app.log("Bullet::Bullet()", "-- cameraController:" + cameraController);
        this.ammoExpSize = templateForTower.ammoSize;
        this.ammoSize = templateForTower.ammoSize;
        this.ammoSpeed = templateForTower.ammoSpeed;
        this.templateForTower = templateForTower;
        Gdx.app.log("Bullet::Bullet()", "-- ammoExpSize:" + ammoExpSize);
        Gdx.app.log("Bullet::Bullet()", "-- ammoSize:" + ammoSize);
        Gdx.app.log("Bullet::Bullet()", "-- ammoSpeed:" + ammoSpeed);

        this.currentPoint = new Vector2(currentPoint);
        this.currCircle = new Circle(currentPoint, ammoSize);
        this.endPoint = new Vector2(destPoint);
        this.endCircle = new Circle(destPoint, 3f);
        Gdx.app.log("Bullet::Bullet()", "-- currentPoint:" + currentPoint + " currCircle:" + currCircle);
        Gdx.app.log("Bullet::Bullet()", "-- endPoint:" + endPoint + " endCircle:" + endCircle);

        velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y);
        velocity.nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
        Gdx.app.log("Bullet::Bullet()", "-- velocity:" + velocity);

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
                Gdx.app.log("Bullet::Bullet()", "-bad- velocity:" + velocity);
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
        setAnimation("ammo_");
    }

    public Bullet(Vector2 currentPoint, TemplateForTower templateForTower, Unit unit, CameraController cameraController) {
        Gdx.app.log("Bullet::Bullet()", "-- currentPoint:" + currentPoint);
        Gdx.app.log("Bullet::Bullet()", "-- templateForTower:" + templateForTower);
        Gdx.app.log("Bullet::Bullet()", "-- unit:" + unit);
        Gdx.app.log("Bullet::Bullet()", "-- cameraController:" + cameraController);
        this.ammoExpSize = templateForTower.ammoSize;
        this.ammoSize = templateForTower.ammoSize;
        this.ammoSpeed = templateForTower.ammoSpeed;
        this.templateForTower = templateForTower;
        this.unit = unit;

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
        Tile tiledMapTile = templateForTower.animations.get("ammo_" + Direction.UP);
        this.textureRegion = tiledMapTile != null ? tiledMapTile.getTextureRegion() : templateForTower.idleTile.getTextureRegion();

        Gdx.app.log("Bullet::Bullet()", "-- currentPoint:" + currentPoint + ", endCircle:" + endCircle);
        Gdx.app.log("Bullet::Bullet()", "-- ammoSpeed:" + ammoSpeed);
        velocity = new Vector2(endCircle.x - currentPoint.x, endCircle.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endCircle.x, endCircle.y), ammoSpeed));
        Gdx.app.log("Bullet::Bullet()", "-- velocity:" + velocity);
    }

    public void dispose() {
        unit = null;
        templateForTower = null;
        textureRegion = null;

        currentPoint = null;
        currCircle = null;
        endPoint = null;
        endCircle = null;
        velocity = null;

        animation = null;
    }

    void setAnimation(String action) {
//        Gdx.app.log("Bullet::setAnimation()", "-- action+direction:" + action+direction );
        AnimatedTile animatedTiledMapTile = templateForTower.animations.get(action + direction);
        if (animatedTiledMapTile != null) {
            StaticTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
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
            textureRegion = animation.getKeyFrame(flyingTime, true);
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
                if (Intersector.overlaps(currCircle, unit.circle1)) {
                    if (unit.die(templateForTower.damage, templateForTower.towerShellEffect)) {
                        cameraController.gameField.gamerGold += unit.templateForUnit.bounty;
                    }
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    private boolean tryToHitUnits(CameraController cameraController) {
        boolean hit = false;
        for (Unit unit : cameraController.gameField.unitsManager.units) {
            if (currCircle.overlaps(unit.circle1)) {
                hit = true;
                if (unit.die(templateForTower.damage, templateForTower.towerShellEffect)) {
                    cameraController.gameField.gamerGold += unit.templateForUnit.bounty;
                }
            }
        }
        return hit;
    }
}
