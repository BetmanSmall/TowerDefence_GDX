package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.badlogic.gdx.math.Rectangle; // AlexGor
import com.badlogic.gdx.math.Vector2;// AlexGor

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
    public Rectangle rect; // AlexGor
    public Vector2 oldPoint;
    public Vector2 newPoint; // AlexGor

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

            setAnimation("walk_");
            this.rect = new Rectangle(); // AlexGor
            this.oldPoint = new Vector2(oldPosition.getX(), oldPosition.getY());
            this.newPoint = new Vector2(newPosition.getX(), newPosition.getY());
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
        rect.set(this.graphicalCoordinateX+GameField.getSizeCellX()/3, this.graphicalCoordinateY+GameField.getSizeCellY()/2, 30f, 50f); // AlexGor
//        this.newPoint.set(x, y);
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

                int halfSizeCellX = GameField.getSizeCellX() / 2;
                int halfSizeCellY = GameField.getSizeCellY() / 2;
                float fVxNew = halfSizeCellX * (newY+1) + newX * halfSizeCellX; // По Y прибавляем еденицу хз почему бага наверное
                float fVyNew = halfSizeCellY * (newY+1) - newX * halfSizeCellY;
                float fVxOld = halfSizeCellX * oldY + oldX * halfSizeCellX;
                float fVyOld = halfSizeCellY * oldY - oldX * halfSizeCellY;
                this.oldPoint.set(fVxOld, fVyOld);
                this.newPoint.set(fVxNew, fVyNew);
                if(newX < oldX && newY > oldY) {
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
        return false;
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

    public Rectangle getRect() { return rect; } // AlexGor

    public float getDistanceofCreep() {
        return (float) Math.sqrt(oldPoint.dst2(newPoint));
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
