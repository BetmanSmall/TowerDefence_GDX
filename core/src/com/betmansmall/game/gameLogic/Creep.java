package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.ShellEffectType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.badlogic.gdx.math.Circle;

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
    private float stepsInTime;
    private float deathElapsedTime;

    public int player; // In Future need change to enumPlayers {Computer0, Player1, Player2} and etc
    public Vector2 currentPoint;
    public Vector2 backStepPoint;// AlexGor
    public Circle circle1;
    public Circle circle2;
    public Circle circle3;
    public Circle circle4;
    public Vector2 velocity;
    public Vector2 displacement;

    private TemplateForUnit templateForUnit;

    public Direction direction;
    private Animation animation;
    public Array<ShellEffectType> shellEffectTypes;

    public Creep(ArrayDeque<Node> route, TemplateForUnit templateForUnit, int player) {
        if(route != null) {
            this.route = route;
            this.oldPosition = route.peekFirst();
            this.newPosition = route.pollFirst();
            this.hp = templateForUnit.healthPoints;
            this.speed = templateForUnit.speed;
            this.stepsInTime = 0;//templateForUnit.speed; // need respawn animation
            this.deathElapsedTime = 0;

            this.player = player;
            this.currentPoint = new Vector2(newPosition.getX(), newPosition.getY());
            this.backStepPoint = new Vector2(oldPosition.getX(), oldPosition.getY());
            this.circle1 = new Circle();
            this.circle2 = new Circle();
            this.circle3 = new Circle();
            this.circle4 = new Circle();

            this.templateForUnit = templateForUnit;

            this.direction = Direction.UP;
            setAnimation("walk_");
            this.shellEffectTypes = new Array<ShellEffectType>();
        } else {
            Gdx.app.error("Creep::Creep()", "-- route == null");
        }
    }

    private void setAnimation(String action) {
        try {
            AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get(action + direction);
            StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            animation = new Animation(speed / staticTiledMapTiles.length, textureRegions);
//        Gdx.app.log("Creep::setAnimation()", "-- ActionAndDirection:" + action+direction + " textureRegions:" + textureRegions[0]);
        } catch (Exception exp) {
            Gdx.app.log("Creep::setAnimation(" + action + direction + ")", "-- CreepName: " + templateForUnit.name + " Exp: " + exp);
        }
    }

    public void dispose() {
        route = null;
        oldPosition = null;
        newPosition = null;
        templateForUnit = null;
        direction = null;
        animation = null;
    }

    public Node move(float delta) {
//        Gdx.app.log("Creep", "move(); -- Creep status:" + this.toString());
        if(route != null && !route.isEmpty()) {
            for(ShellEffectType shellEffectType : shellEffectTypes) {
                if(!shellEffectType.used) {
//                    Gdx.app.log("Creep", "move(); -- Active shellEffectType:" + shellEffectType);
                    shellEffectType.used = true;
                    if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                        float smallSpeed = speed/100f;
                        float percentSteps = stepsInTime/smallSpeed;
                        speed += shellEffectType.speed;
                        smallSpeed = speed/100f;
                        stepsInTime = smallSpeed*percentSteps;
                    } else if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                        hp -= shellEffectType.damage;
                    }
                } else {
                    if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                        hp -= shellEffectType.damage;
                    }
                }
                shellEffectType.elapsedTime += delta;
                if(shellEffectType.elapsedTime >= shellEffectType.time) {
//                    Gdx.app.log("Creep", "move(); -- Remove shellEffectType:" + shellEffectType);
                    if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                        float smallSpeed = speed/100f;
                        float percentSteps = stepsInTime/smallSpeed;
                        speed = templateForUnit.speed;
                        smallSpeed = speed/100f;
                        stepsInTime = smallSpeed*percentSteps;
                    }
                    shellEffectTypes.removeValue(shellEffectType, true);
                }
            }
            stepsInTime += delta;
            if (stepsInTime >= speed) {
                stepsInTime = 0f;
                oldPosition = newPosition;
                newPosition = route.pollFirst();
            }

            // Dead CODE | MANUAL
//                float fVxOld = halfSizeCellX * oldY + oldX * halfSizeCellX;
//                float fVyOld = halfSizeCellY * oldY - oldX * halfSizeCellY;
//                this.oldPoint.set(fVxOld, fVyOld);
//                this.currentPoint.set(fVx, fVy);
//                int oldX = oldPosition.getX(), oldY = oldPosition.getY();
//                int newX = newPosition.getX(), newY = newPosition.getY();
//                float fVx = halfSizeCellX * (newY+1) + newX * halfSizeCellX;
//                float fVy = halfSizeCellY * (newY+1) - newX * halfSizeCellY;
//                if(newX < oldX && newY > oldY) {
//                } else if (newX == oldX && newY > oldY) {
//                } else if (newX > oldX && newY > oldY) {
//                } else if (newX > oldX && newY == oldY) {
//                } else if (newX > oldX && newY < oldY) {
//                } else if (newX == oldX && newY < oldY) {
//                } else if (newX < oldX && newY < oldY) {
//                } else if (newX < oldX && newY == oldY) {
//                }
//            Gdx.app.log("Creep::move()", "-- fVx:" + fVx + " fVy:" + fVy);
            // --- MANUAL ---

            int oldX = oldPosition.getX(), oldY = oldPosition.getY();
            int newX = newPosition.getX(), newY = newPosition.getY();
            int sizeCellX = GameField.getSizeCellX();
            int sizeCellY = GameField.getSizeCellY();
            float halfSizeCellX = sizeCellX/2;
            float halfSizeCellY = sizeCellY/2;
            float fVx = 0, fVy = 0;
            Direction oldDirection = direction;
            int isDrawableCreeps = GameField.isDrawableCreeps;
            if(isDrawableCreeps == 4 || isDrawableCreeps == 5) {
                fVx = (-(halfSizeCellX * newY) - (newX * halfSizeCellX)) - halfSizeCellX;
                fVy = ( (halfSizeCellY * newY) - (newX * halfSizeCellY)) + halfSizeCellY;
                if (newX < oldX && newY > oldY) {
                    direction = Direction.UP;
                    fVy -= (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY == oldY) {
                    direction = Direction.UP_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY < oldY) {
                    direction = Direction.RIGHT;
                    fVx -= (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY < oldY) {
                    direction = Direction.DOWN_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY < oldY) {
                    direction = Direction.DOWN;
                    fVy += (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY == oldY) {
                    direction = Direction.DOWN_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY > oldY) {
                    direction = Direction.LEFT;
                    fVx += (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY > oldY) {
                    direction = Direction.UP_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                }
                currentPoint.set(fVx, fVy);
                circle4.set(fVx, fVy, 16f);
            }
            if(isDrawableCreeps == 3 || isDrawableCreeps == 5) {
                fVx = (-(halfSizeCellX * newY) + (newX * halfSizeCellX));
                fVy = ( (halfSizeCellY * newY) + (newX * halfSizeCellY)) + halfSizeCellY*2;
                if (newX < oldX && newY > oldY) {
                    direction = Direction.UP;
                    fVy -= (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY == oldY) {
                    direction = Direction.UP_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY < oldY) {
                    direction = Direction.RIGHT;
                    fVx -= (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY < oldY) {
                    direction = Direction.DOWN_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY < oldY) {
                    direction = Direction.DOWN;
                    fVy += (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY == oldY) {
                    direction = Direction.DOWN_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY > oldY) {
                    direction = Direction.LEFT;
                    fVx += (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY > oldY) {
                    direction = Direction.UP_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                }
                currentPoint.set(fVx, fVy);
                circle3.set(fVx, fVy, 16f);
            }
            if(isDrawableCreeps == 2 || isDrawableCreeps == 5) {
                fVx = (halfSizeCellX * newY) + (newX * halfSizeCellX) + halfSizeCellX;
                fVy = (halfSizeCellY * newY) - (newX * halfSizeCellY) + halfSizeCellY;
                if (newX < oldX && newY > oldY) {
                    direction = Direction.UP;
                    fVy -= (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY > oldY) {
                    direction = Direction.UP_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY > oldY) {
                    direction = Direction.RIGHT;
                    fVx -= (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY == oldY) {
                    direction = Direction.DOWN_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY < oldY) {
                    direction = Direction.DOWN;
                    fVy += (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY < oldY) {
                    direction = Direction.DOWN_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY < oldY) {
                    direction = Direction.LEFT;
                    fVx += (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY == oldY) {
                    direction = Direction.UP_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                }
                currentPoint.set(fVx, fVy);
                circle2.set(fVx, fVy, 16f);
            }
            if(isDrawableCreeps == 1 || isDrawableCreeps == 5) {
                fVx = (-(halfSizeCellX * newY) + (newX * halfSizeCellX));
                fVy = (-(halfSizeCellY * newY) - (newX * halfSizeCellY));
                if (newX < oldX && newY < oldY) {
                    direction = Direction.UP;
                    fVy -= (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY < oldY) {
                    direction = Direction.UP_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY < oldY) {
                    direction = Direction.RIGHT;
                    fVx -= (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY == oldY) {
                    direction = Direction.DOWN_RIGHT;
                    fVx -= (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX > oldX && newY > oldY) {
                    direction = Direction.DOWN;
                    fVy += (sizeCellY / speed) * (speed - stepsInTime);
                } else if (newX == oldX && newY > oldY) {
                    direction = Direction.DOWN_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy += (sizeCellY / 2 / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY > oldY) {
                    direction = Direction.LEFT;
                    fVx += (sizeCellX / speed) * (speed - stepsInTime);
                } else if (newX < oldX && newY == oldY) {
                    direction = Direction.UP_LEFT;
                    fVx += (sizeCellX / 2 / speed) * (speed - stepsInTime);
                    fVy -= (sizeCellY / 2 / speed) * (speed - stepsInTime);
                }
                currentPoint.set(fVx, fVy);
                circle1.set(fVx, fVy, 16f);
            }

            backStepPoint = currentPoint;
            currentPoint.set(fVx, fVy);

            velocity = new Vector2(backStepPoint.x - currentPoint.x,
                    backStepPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(backStepPoint.x,
                    backStepPoint.y), speed));
            displacement = new Vector2(velocity.x * delta, velocity.y * delta);

//            Gdx.app.log("Creep::move()", "-- direction:" + direction + " oldDirection:" + oldDirection);
            if(!direction.equals(oldDirection)) {
                setAnimation("walk_");
            }
            return newPosition;
        } else {
            dispose();
            return null;
        }
    }

    public boolean die(int damage, ShellEffectType shellEffectType) {
        if(hp > 0) {
            hp -= damage;
            addEffect(shellEffectType);
            if(hp <= 0) {
                deathElapsedTime = 0;
                setAnimation("death_");
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean addEffect(ShellEffectType shellEffectType) {
        if(shellEffectType != null){
            if(!shellEffectTypes.contains(shellEffectType, false)) {
                shellEffectTypes.add(new ShellEffectType(shellEffectType));
            }
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
        if(animation == null) { // TODO Не верно, нужно исправить.
            return false;
        }
        return hp > 0 ? true : false;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public float getSpeed() {
        return speed;
    }

    public void setStepsInTime(float stepsInTime) {
        this.stepsInTime = stepsInTime;
    }
    public float getStepsInTime() {
        return stepsInTime;
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
        return animation.getKeyFrame(stepsInTime, true);
    }

    public TextureRegion getCurrentDeathFrame() {
        return animation.getKeyFrame(deathElapsedTime, true);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creep[");
//        sb.append("route:" + route + ",");
        sb.append("oldPosition:" + oldPosition + ",");
        sb.append("newPosition:" + newPosition + ",");
        sb.append("hp:" + hp + ",");
        sb.append("speed:" + speed + ",");
        sb.append("stepsInTime:" + stepsInTime + ",");
        sb.append("deathElapsedTime:" + deathElapsedTime + ",");
        sb.append("circle1:" + circle1 + ",");
        sb.append("circle2:" + circle2 + ",");
        sb.append("templateForUnit:" + templateForUnit + ",");
        sb.append("direction:" + direction + ",");
        sb.append("animation:" + animation + ",");
        sb.append("shellEffectTypes:" + shellEffectTypes + ",");
        sb.append("]");
        return sb.toString();
    }
}
