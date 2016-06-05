package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class Creep {
    private ArrayDeque<Node> route;
    private Node oldPosition;
    private Node newPosition;
    private int hp;
    private float speed;
    private float elapsedTime;
    private float deathElapsedTime;
    public float graphicalCoordinateX, graphicalCoordinateY;

    private TemplateForUnit templateForUnit;

    private Direction direction;
    private Animation animation;

    public Creep(ArrayDeque<Node> route, TemplateForUnit templateForUnit) {
        if(route != null) {
            this.route = route;
            this.oldPosition = route.peekFirst();
            this.newPosition = route.pollFirst();
            this.hp = templateForUnit.healthPoints;
            this.speed = templateForUnit.speed;
            this.elapsedTime = templateForUnit.speed;
            this.deathElapsedTime = 0;

            this.templateForUnit = templateForUnit;

            this.direction = Direction.UP;
            setAnimation("idle_");
        } else {
            Gdx.app.error("Creep::Creep()", " -- route == null");
        }
    }

    private void setAnimation(String action) {
        AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get(action + direction);
        StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
        TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
        for (int k = 0; k < staticTiledMapTiles.length; k++) {
            textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
        }
        animation = new Animation(speed/staticTiledMapTiles.length, textureRegions);
//        Gdx.app.log("Creep::setAnimation()", " -- ActionAndDirection:" + action+direction + " textureRegions:" + textureRegions[0]);
    }

    public void dispose() {
        route = null;
        oldPosition = null;
        newPosition = null;
        templateForUnit = null;
        direction = null;
        animation = null;
    }

    public void setGraphicalCoordinates(float x, float y) {
        this.graphicalCoordinateX = x;
        this.graphicalCoordinateY = y;
    }

    public Node move(float delta) {
        if(route != null && !route.isEmpty()) {
            elapsedTime += delta;
            if (elapsedTime >= speed) {
                elapsedTime = 0f;
                Direction oldDirection = direction;
                oldPosition = newPosition;
                newPosition = route.pollFirst();
                int oldX = oldPosition.getX(), oldY = oldPosition.getY();
                int newX = newPosition.getX(), newY = newPosition.getY();
                if (newX < oldX && newY > oldY) {
                    direction = Direction.UP;
                } else if (newX == oldX && newY > oldY) {
                    direction = Direction.UP_RIGHT;
                } else if (newX > oldX && newY > oldY) {
                    direction = Direction.RIGHT;
                } else if (newX > oldX && newY == oldY) {
                    direction = Direction.DOWN_RIGHT;
                } else if (newX > oldX && newY < oldY) {
                    direction = Direction.DOWN;
                } else if (newX == oldX && newY < oldY) {
                    direction = Direction.DOWN_LEFT;
                } else if (newX < oldX && newY < oldY) {
                    direction = Direction.LEFT;
                } else if (newX < oldX && newY == oldY) {
                    direction = Direction.UP_LEFT;
                }

//                Gdx.app.log("Creep::move()", " -- oldDirection:" + oldDirection + " newDirection:" + direction);
                if (!direction.equals(oldDirection)) {
                    setAnimation("walk_");
                }
            }
            return newPosition;
        } else {
            dispose();
            return null;
        }
    }

    public boolean die(int damage) {
        if(hp > 0) {
            hp -= damage;
            if(hp <= 0) {
                deathElapsedTime = 0;
                setAnimation("death_");
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean changeDeathFrame(float delta) {
        if(hp <= 0) {
            if(deathElapsedTime >= speed) {
                dispose();
                return false;
            } else {
                deathElapsedTime += delta;
            }
            return true;
        }
        return false;
    }

    public Node getOldPosition() {
        return oldPosition;
    }
    public Node getNewPosition() {
        return newPosition;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getHp() {
        return hp;
    }
    public boolean isAlive() {
        return hp > 0 ? true : false;
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

    public void setRoute(ArrayDeque<Node> route) {
        this.route = route;
    }
    public ArrayDeque<Node> getRoute() {
        return route;
    }

    public TemplateForUnit getTemplateForUnit() {
        return templateForUnit;
    }

    public TextureRegion getCurentFrame() {
        return animation.getKeyFrame(elapsedTime, true);
    }

    public TextureRegion getCurrentDeathFrame() {
        return animation.getKeyFrame(deathElapsedTime, true);
    }
}
