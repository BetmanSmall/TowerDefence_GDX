package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
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
        if(templateForTower.shellAttackType == ShellAttackType.MultipleTarget) {
            this.endPoint = new Vector2(creep.currentPoint);
            Direction direction = creep.direction;
            float delta = GameField.getSizeCellX();
            float del = 3f;
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
        } else if(templateForTower.shellAttackType == ShellAttackType.SingleTarget) {
            this.endPoint = creep.currentPoint;
        }
        velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
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
            Gdx.app.log("Shell", "flightOfShell(" + delta + "); -- " + currentPoint + ", " + endPoint + ", " + velocity);
            if(templateForTower.shellAttackType == ShellAttackType.MultipleTarget) {
                float displacementX = velocity.x * delta * ammoSpeed;
                float displacementY = velocity.y * delta * ammoSpeed;
                currentPoint.add(displacementX, displacementY);
                circle.setPosition(currentPoint);
//                Vector2 velo2 = velocity.rotate90(0);
//                Vector2 velo2 = new Vector2(endPoint.y, -endPoint.x);
//                Vector2 velo2 = new Vector2(endPoint).scl(velocity);
//                boolean b1 = false;
//                if (Intersector.overlaps(new Circle(currentPoint, 1), new Circle(endPoint, 1))) {
//                    b1 = true;
                if(currentPoint.epsilonEquals(endPoint.x, endPoint.y, (displacementX > displacementY) ? displacementX : displacementY)) {
//                if(currentPoint.isCollinearOpposite(velo2)) {
                    for (Creep creep : GameField.creepsManager.getAllCreeps()) { // not good
                        if (Intersector.overlaps(circle, creep.getCircle())) {
//                        Gdx.app.log("Shell", "flightOfShell(); -- overlaps(" + circle + ", " + creep.toString());
                            if (creep.die(templateForTower.damage, templateForTower.shellEffectType)) {
                                GameField.gamerGold += creep.getTemplateForUnit().bounty;
                            }
                        }
                    }
                    return 0;
                }
                return 1;
            } else if(templateForTower.shellAttackType == ShellAttackType.SingleTarget) {
                velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
                currentPoint.add(velocity.x * delta * ammoSpeed, velocity.y * delta * ammoSpeed);
                circle.setPosition(currentPoint);

                float x1 = creep.currentPoint.x - (templateForTower.ammoSize / 2);
                float y1 = creep.currentPoint.y - (templateForTower.ammoSize / 2);
                float x2 = creep.currentPoint.x + (templateForTower.ammoSize / 2);
                float y2 = creep.currentPoint.y + (templateForTower.ammoSize / 2);

                if (currentPoint.x > x1 && currentPoint.x < x2) {
                    if (currentPoint.y > y1 && currentPoint.y < y2) {
                        if (creep.die(templateForTower.damage, templateForTower.shellEffectType)) {
                            GameField.gamerGold += creep.getTemplateForUnit().bounty;
                        }
                        return 0;
                    }
                }
                return 1;
            }
        }
        return -1;
    }

    public void dispose() {
        creep = null;
        templateForTower = null;
        ammunitionPictures = null;
    }
}
