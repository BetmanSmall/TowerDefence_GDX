package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ObjectMap;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.ShellAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by betmansmall on 29.03.2016.
 */
public class Shell {
    public Creep creep;
    public float ammoExpSize;
    public float ammoSize;
    public float ammoSpeed;
    public TemplateForTower templateForTower;

    public TextureRegion textureRegion;
    public ObjectMap<String, TiledMapTile> ammunitionPictures;

    public Vector2 currentPoint;
    public Vector2 endPoint;
    public Circle circle = null;
    public Vector2 velocity;

    public Circle endPoint2;

    Shell(TemplateForTower templateForTower, Creep creep, Vector2 currentPoint) {
//        Gdx.app.log("Shell", "Shell(" + currentPoint + ", " + endPoint + ");");
        this.creep = creep;
        this.ammoExpSize = templateForTower.ammoSize;
        this.ammoSize = templateForTower.ammoSize;
        this.ammoSpeed = templateForTower.ammoSpeed;
        this.templateForTower = templateForTower;

        TiledMapTile tiledMapTile = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP);
        this.textureRegion = tiledMapTile != null ? tiledMapTile.getTextureRegion() : templateForTower.idleTile.getTextureRegion();
        this.ammunitionPictures = templateForTower.ammunitionPictures;

        this.currentPoint = currentPoint;
        circle = new Circle(currentPoint.x, currentPoint.y, ammoSize);
        if(templateForTower.shellAttackType == ShellAttackType.MultipleTarget || templateForTower.shellAttackType == ShellAttackType.FirstTarget) {
            this.endPoint = new Vector2(creep.circle1.x, creep.circle1.y);
            Direction direction = creep.direction;
            float delta = GameField.getSizeCellX();
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
        } else if(templateForTower.shellAttackType == ShellAttackType.AutoTarget) {
            this.endPoint = new Vector2(creep.circle1.x, creep.circle1.y); // LOL break
        }
        velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
        endPoint2 = new Circle(endPoint, 3f);
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
        if(creep.isAlive()) {
//            Gdx.app.log("Shell", "flightOfShell(" + delta + "); -- " + currentPoint + ", " + endPoint + ", " + velocity);
            if(templateForTower.shellAttackType == ShellAttackType.AutoTarget) {
                velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
                currentPoint.add(velocity.x * delta * ammoSpeed, velocity.y * delta * ammoSpeed);
                circle.setPosition(currentPoint);
                endPoint2.setPosition(new Vector2(endPoint));
                // endPoint2 == endPoint == creep.currentPoint ~= creep.circle1
                if(Intersector.overlaps(circle, creep.circle1)) {
                    if (creep.die(templateForTower.damage, templateForTower.shellEffectType)) {
                        GameField.gamerGold += creep.getTemplateForUnit().bounty;
                    }
                    return 0;
                }
                return 1;
            } else if(templateForTower.shellAttackType == ShellAttackType.MultipleTarget || templateForTower.shellAttackType == ShellAttackType.FirstTarget) {
                float displacementX = velocity.x * delta * ammoSpeed;
                float displacementY = velocity.y * delta * ammoSpeed;
                currentPoint.add(displacementX, displacementY);
                circle.setPosition(currentPoint);
                endPoint2.setPosition(new Vector2(endPoint).add(displacementX, displacementY));

                if (templateForTower.shellAttackType == ShellAttackType.MultipleTarget) {
                    if(Intersector.overlaps(circle, endPoint2)) {
                        tryToHitCreeps();
                        return 0;
                    }
                } else if(templateForTower.shellAttackType == ShellAttackType.FirstTarget) {
                    if(tryToHitCreeps()) {
                        return 0;
                    }
                }
                return 1;
            }
        }
        return -1;
    }

    private boolean tryToHitCreeps() {
        boolean hit = false;
        for (Creep creep : GameField.creepsManager.getAllCreeps()) { // not good
            if (Intersector.overlaps(circle, creep.getCircle1())) {
                hit = true;
                if (creep.die(templateForTower.damage, templateForTower.shellEffectType)) {
                    GameField.gamerGold += creep.getTemplateForUnit().bounty;
                }
            }
        }
        return hit;
    }

    public void dispose() {
        creep = null;
        templateForTower = null;
        ammunitionPictures = null;
    }
}
