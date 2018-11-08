package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ObjectMap;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTile;
import com.betmansmall.game.gameLogic.mapLoader.Tile;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.ShellAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by betmansmall on 29.03.2016.
 */
public class Bullet {
    public Unit unit;
    public float ammoExpSize;
    public float ammoSize;
    public float ammoSpeed;
    public TemplateForTower templateForTower;

    public TextureRegion textureRegion;
//    public ObjectMap<String, AnimatedTile> ammunitionPictures;

    boolean flying;
    int lastCellX, lastCellY;
    int currCellX, currCellY;
    public Vector2 currentPoint;
    public Circle circle;
    public Circle endPoint;
    public Vector2 velocity;

    Direction direction;
    Animation animation;

    Bullet(Vector2 currentPoint, TemplateForTower templateForTower, Unit unit) {
//        Gdx.app.log("Bullet", "Bullet(" + currentPoint + ", " + endPoint + ");");
        this.unit = unit;
        this.ammoExpSize = templateForTower.ammoSize;
        this.ammoSize = templateForTower.ammoSize;
        this.ammoSpeed = templateForTower.ammoSpeed;
        this.templateForTower = templateForTower;

        Tile tiledMapTile = templateForTower.animations.get("ammo_" + Direction.UP);
        this.textureRegion = tiledMapTile != null ? tiledMapTile.getTextureRegion() : templateForTower.idleTile.getTextureRegion();
//        this.ammunitionPictures = templateForTower.animations;

        this.currentPoint = new Vector2(currentPoint.x, currentPoint.y);
        this.circle = new Circle(currentPoint, ammoSize);
        if(templateForTower.shellAttackType == ShellAttackType.MultipleTarget || templateForTower.shellAttackType == ShellAttackType.FirstTarget) {
            this.endPoint = new Circle(unit.currentPoint.x + unit.displacement.x, unit.currentPoint.y + unit.displacement.y, 3f);
        } else if(templateForTower.shellAttackType == ShellAttackType.AutoTarget) {
            if(GameField.isDrawableUnits == 1 || GameField.isDrawableUnits == 5 || GameField.isDrawableUnits == 0)
                this.endPoint = unit.circle1;
            else if(GameField.isDrawableUnits == 2)
                this.endPoint = unit.circle1;
            else if(GameField.isDrawableUnits == 3)
                this.endPoint = unit.circle1;
            else if(GameField.isDrawableUnits == 4)
                this.endPoint = unit.circle1;
//            this.endPoint.setRadius(3f);
//            this.endPoint = unit.currentPoint // LOL break
        } else if (templateForTower.shellAttackType == ShellAttackType.FireBall) {
            Vector2 endPoint = new Vector2(unit.circle1.x, unit.circle1.y);
            Direction direction = unit.direction;
            float delta = GameField.sizeCellX;
            float del = 1.8f;
            if (direction == Direction.UP) {
                endPoint.add(0, delta);
            } else if (direction == Direction.UP_RIGHT) {
                endPoint.add(delta / del, delta / del);
            } else if (direction == Direction.RIGHT) {
                endPoint.add(delta, 0);
            } else if (direction == Direction.DOWN_RIGHT) {
                endPoint.add(delta / del, -(delta / del));
            } else if (direction == Direction.DOWN) {
                endPoint.add(0, -delta);
            } else if (direction == Direction.DOWN_LEFT) {
                endPoint.add(-(delta / del), -(delta / del));
            } else if (direction == Direction.LEFT) {
                endPoint.add(-delta, 0);
            } else if (direction == Direction.UP_LEFT) {
                endPoint.add(-(delta / del), delta / del);
            }
            this.endPoint = new Circle(endPoint, 3f);
        }
        velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
    }

    public Bullet(int currCellX, int currCellY, Direction direction, TemplateForTower templateForTower) {
        this.flying = true;
        this.currCellX = currCellX;
        this.currCellY = currCellY;
        this.direction = direction;
        this.templateForTower = templateForTower;
    }

    public void dispose() {
        unit = null;
        templateForTower = null;
//        ammunitionPictures = null;
    }

    void setAnimation(String action) {
        try {
            AnimatedTile animatedTiledMapTile = templateForTower.animations.get(action + direction);
            StaticTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            animation = new Animation(ammoSpeed / staticTiledMapTiles.length, textureRegions);
//        Gdx.app.log("Unit::setAnimation()", "-- ActionAndDirection:" + action+direction + " textureRegions:" + textureRegions[0]);
        } catch (Exception exp) {
            Gdx.app.log("Bullet::setAnimation(" + action + direction + ")", "-- templateForTowerName: " + templateForTower.name + " Exp: " + exp);
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
    public int flightOfShell(float delta) {
        if(unit.isAlive()) {
//            Gdx.app.log("Bullet", "flightOfShell(" + delta + "); -- " + currentPoint + ", " + endPoint + ", " + velocity);
            if(templateForTower.shellAttackType == ShellAttackType.MultipleTarget || templateForTower.shellAttackType == ShellAttackType.FirstTarget) {
                float displacementX = velocity.x * delta * ammoSpeed;
                float displacementY = velocity.y * delta * ammoSpeed;

                currentPoint.add(displacementX, displacementY);
                circle.setPosition(currentPoint);

                if (templateForTower.shellAttackType == ShellAttackType.MultipleTarget) {
                    if(Intersector.overlaps(circle, endPoint)) {
                        tryToHitUnits();
                        return 0;
                    }
                } else if(templateForTower.shellAttackType == ShellAttackType.FirstTarget) {
                    if(tryToHitUnits()) {
                        return 0;
                    }
                }
                return 1;
            } else if(templateForTower.shellAttackType == ShellAttackType.AutoTarget) {
                velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
                currentPoint.add(velocity.x * delta * ammoSpeed, velocity.y * delta * ammoSpeed);
                circle.setPosition(currentPoint);
                // endPoint2 == endPoint == unit.currentPoint ~= unit.circle1
                if (Intersector.overlaps(circle, unit.circle1)) {
                    if (unit.die(templateForTower.damage, templateForTower.shellEffectType)) {
                        GameField.gamerGold += unit.templateForUnit.bounty;
                    }
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    private boolean tryToHitUnits() {
        boolean hit = false;
        for (Unit unit : GameField.unitsManager.units) { // not good
            if (Intersector.overlaps(circle, unit.circle1)) {
                hit = true;
                if (unit.die(templateForTower.damage, templateForTower.shellEffectType)) {
                    GameField.gamerGold += unit.templateForUnit.bounty;
                }
            }
        }
        return hit;
    }
}
