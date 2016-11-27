package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectMap;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
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

    Shell(Vector2 currentPoint, Vector2 endPoint, Creep creep, TemplateForTower templateForTower) {
//        Gdx.app.log("Shell", "Shell(" + currentPoint + ", " + endPoint + ");");
        this.creep = creep;
        this.ammoExpSize = templateForTower.ammoDistance;
        this.ammoSize = templateForTower.ammoSize;
        this.ammoSpeed = templateForTower.ammoSpeed;
        this.templateForTower = templateForTower;

        this.textureRegion = templateForTower.ammunitionPictures.get("ammo_" + Direction.UP).getTextureRegion();
        this.ammunitionPictures = templateForTower.ammunitionPictures;

        this.currentPoint = currentPoint;
        this.endPoint = endPoint;
        circle = new Circle(currentPoint.x, currentPoint.y, ammoSize);
        velocity = new Vector2(endPoint.x - currentPoint.x, endPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(endPoint.x, endPoint.y), ammoSpeed));
    }

    public int flightOfShell(float delta) {
        if(creep.isAlive()) {
//            Gdx.app.log("Shell", "flightOfShell(" + delta + "); -- " + currentPoint + ", " + endPoint + ", " + velocity);
            currentPoint.add(velocity.x * delta, velocity.y * delta);
            circle.setPosition(currentPoint);
            boolean iIsAlive = true;
            if(Intersector.overlaps(new Circle(currentPoint, 2), new Circle(endPoint, 2))) {
                for (Creep creep : GameField.creepsManager.getAllCreeps()) { // not good
                    if (Intersector.overlaps(circle, creep.getRect())) {
                        iIsAlive = false;
                        Gdx.app.log("Shell", "flightOfShell(); -- overlaps(" + circle + ", " + creep.toString());
                        if (creep.die(templateForTower.damage)) {
                            GameField.gamerGold += creep.getTemplateForUnit().bounty;
                        }
                    }
                }
            }
            if(iIsAlive) {
                return 1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    /*
     * Говорит пуле постараться достигнуть криппа.
     *
     * @param delta пока не используется. (нужна для ожидания пули времени для перемещения)
     * @return -1 - Пуля не передвинулась. Крип мертв. Нужно убрать пулю из массива пуль.<br>
     * 0 - Пуля передвинулась и достигла крипа.<br>
     * 1 - Пуля передвинулась, но не достигла крипа.<br>
     */
    /*public int hasReached(float delta) {
        if(creep.isAlive()) {
            float creepCenterX = creep.graphicalCoordinateX + (creep.getCurentFrame().getRegionWidth() / 2);
            float creepCenterY = creep.graphicalCoordinateY + (creep.getCurentFrame().getRegionHeight() / 2);

//            ==================БЫДЛО КОД===============
            if (x == creepCenterX) {
                if (y < creepCenterY) {
                    y += ammoDistance;
                } else if (y > creepCenterY) {
                    y -= ammoDistance;
                }
            } else if (y == creepCenterY) {
                if (x < creepCenterX) {
                    x += ammoDistance;
                } else if (x > creepCenterX) {
                    x -= ammoDistance;
                }
            } else if (x < creepCenterX && y > creepCenterY) {
                x += ammoDistance / 2;
                y -= ammoDistance / 2;
            } else if (x > creepCenterX && y > creepCenterY) {
                x -= ammoDistance / 2;
                y -= ammoDistance / 2;
            } else if (x < creepCenterX && y < creepCenterY) {
                x += ammoDistance / 2;
                y += ammoDistance / 2;
            } else if (x > creepCenterX && y < creepCenterY) {
                x -= ammoDistance / 2;
                y += ammoDistance / 2;
            }
//            ==================БЫДЛО КОД===============

            float x1 = creepCenterX - (radius / 2);
            float y1 = creepCenterY - (radius / 2);
            float x2 = creepCenterX + (radius / 2);
            float y2 = creepCenterY + (radius / 2);

            if (x > x1 && x < x2) {
                if (y > y1 && y < y2) {
                    return 0;
                }
            }
            return 1;
        }
        return -1;
    }*/

    public void dispose() {
        creep = null;
        templateForTower = null;
        ammunitionPictures = null;
    }

}
