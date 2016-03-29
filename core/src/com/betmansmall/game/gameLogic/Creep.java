package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Vertex;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class  Creep {
    private ArrayDeque<Vertex> route;
    private Vertex oldPosition;
    private Vertex newPosition;
    private int hp;
    private float speed;
    private float elapsedTime;

    private TemplateForUnit templateForUnit;
//    private TextureRegion curentFrame;

    private Direction direction;
    private Animation animation;

    public Creep(ArrayDeque<Vertex> route, TemplateForUnit templateForUnit) {
        if(route != null) {
            this.route = route;
            this.oldPosition = route.peekFirst();
            this.newPosition = route.pollFirst();
            this.hp = templateForUnit.healthPoints;
            this.speed = templateForUnit.speed;
            this.elapsedTime = templateForUnit.speed;

            this.templateForUnit = templateForUnit;
//            this.curentFrame = templateForUnit.idle;

            this.direction = Direction.UP;
            setAnimation("idle_");
//            move(speed);
        } else {
            Gdx.app.error("Creep::Creep()", " -- route == null");
        }
    }

    public void dispose() {
        newPosition = null;
        route = null;
        templateForUnit = null;
//        curentFrame = null;
    }

    public Vertex move(float delta) {
        if(route != null && !route.isEmpty()) {
            elapsedTime += delta;
            if(elapsedTime >= speed) {
                elapsedTime = 0f;
                Direction oldDirection = direction;
                oldPosition = newPosition;
                newPosition = route.pollFirst();
                int oldX = oldPosition.getX(), oldY = oldPosition.getY();
                int newX = newPosition.getX(), newY = newPosition.getY();
                if(newX < oldX && newY > oldY) {
                    direction = Direction.UP;
                } else if(newX == oldX && newY > oldY) {
                    direction = Direction.UP_RIGHT;
                } else if(newX > oldX && newY > oldY) {
                    direction = Direction.RIGHT;
                } else if(newX > oldX && newY == oldY) {
                    direction = Direction.DOWN_RIGHT;
                } else if(newX > oldX && newY < oldY) {
                    direction = Direction.DOWN;
                } else if(newX == oldX && newY < oldY) {
                    direction = Direction.DOWN_LEFT;
                } else if(newX < oldX && newY < oldY) {
                    direction = Direction.LEFT;
                } else if(newX < oldX && newY == oldY) {
                    direction = Direction.UP_LEFT;
                }

//                Gdx.app.log("Creep::move()", " -- oldDirection:" + oldDirection + " newDirection:" + direction);
                if(!direction.equals(oldDirection)) {
                    setAnimation("walk_");
                }
            }
            return newPosition;
        } else {
            dispose();
            return null;
        }
    }

    private void setAnimation(String action) {
        AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get(action + direction);
        StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
        TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
        for (int k = 0; k < textureRegions.length; k++) {
            textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
        }
        animation = new Animation(speed/staticTiledMapTiles.length, textureRegions);
//        Gdx.app.log("Creep::setAnimation()", " -- Direction:" + direction + " textureRegions:" + textureRegions[0]);
    }

    public Vertex getOldPosition() {
        return oldPosition;
    }
    public Vertex getNewPosition() {
        return newPosition;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getHp() {
        return hp;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public float getSpeed() {
        return speed;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setRoute(ArrayDeque<Vertex> route) {
        this.route = route;
    }
    public ArrayDeque<Vertex> getRoute() {
        return route;
    }

    public TemplateForUnit getTemplateForUnit() {
        return templateForUnit;
    }

    public TextureRegion getCurentFrame() {
        return animation.getKeyFrame(elapsedTime, true);
    }
}
