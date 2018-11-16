package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTile;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.ShellEffectType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.badlogic.gdx.math.Circle;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class Unit {
    public ArrayDeque<Node> route;
    public Node oldPosition;
    public Node newPosition;
    public Cell exitCell;
    public float hp;
    public float speed;
    public float stepsInTime;
    public float deathElapsedTime;

    public int player; // In Future need change to enumPlayers {Computer0, Player1, Player2} and etc
    public Vector2 currentPoint;
    public Vector2 backStepPoint;
    public Circle circle1;
    public Circle circle2;
    public Circle circle3;
    public Circle circle4;
    public Vector2 velocity;
    public Vector2 displacement;

    public TemplateForUnit templateForUnit;

    public Direction direction;
    private Animation animation;
    public Array<ShellEffectType> shellEffectTypes;

//    public Unit(ArrayDeque<Node> route, TemplateForUnit templateForUnit, int player) {
//        Unit(route, templateForUnit, player, null);
//    }

    public Unit(ArrayDeque<Node> route, TemplateForUnit templateForUnit, int player, Cell exitCell) {
        if(route != null) {
            this.route = route;
            this.oldPosition = route.peekFirst();
            this.newPosition = route.pollFirst();
            this.exitCell = exitCell;
            this.hp = templateForUnit.healthPoints;
            this.speed = templateForUnit.speed;
            this.stepsInTime = 0f;//templateForUnit.speed; // need respawn animation
            this.deathElapsedTime = 0f;

            this.player = player;
            this.currentPoint = new Vector2(newPosition.getX(), newPosition.getY());
            this.backStepPoint = new Vector2(oldPosition.getX(), oldPosition.getY());
            this.circle1 = new Circle(0, 0, 16f);
            this.circle2 = new Circle(0, 0, 16f);
            this.circle3 = new Circle(0, 0, 16f);
            this.circle4 = new Circle(0, 0, 16f);

            this.templateForUnit = templateForUnit;

            this.direction = Direction.UP;
            setAnimation("walk_");
            this.shellEffectTypes = new Array<ShellEffectType>();
        } else {
            Gdx.app.error("Unit::Unit()", "-- route == null");
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

    private void setAnimation(String action) {
        try {
            AnimatedTile animatedTiledMapTile = templateForUnit.animations.get(action + direction);
            StaticTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            animation = new Animation(speed / staticTiledMapTiles.length, textureRegions);
//        Gdx.app.log("Unit::setAnimation()", "-- ActionAndDirection:" + action+direction + " textureRegions:" + textureRegions[0]);
        } catch (Exception exp) {
            Gdx.app.log("Unit::setAnimation(" + action + direction + ")", "-- UnitName: " + templateForUnit.name + " Exp: " + exp);
        }
    }

    void correct_fVc(Vector2 fVc, Direction direction, float sizeCellX) {
        this.direction = direction;
        float fVx = fVc.x;
        float fVy = fVc.y;
        if (direction == Direction.UP) {
            fVy -= ( (sizeCellX / 2) / speed ) * (speed - stepsInTime);
        } else if (direction == Direction.UP_RIGHT) {
            fVx -= ( (sizeCellX / 2) / speed ) * (speed - stepsInTime);
            fVy -= ( (sizeCellX / 4) / speed ) * (speed - stepsInTime);
        } else if (direction == Direction.RIGHT) {
            fVx -= ( sizeCellX / speed ) * (speed - stepsInTime);
        } else if (direction == Direction.DOWN_RIGHT) {
            fVx -= ( (sizeCellX / 2) / speed ) * (speed - stepsInTime);
            fVy += ( (sizeCellX / 4) / speed ) * (speed - stepsInTime);
        } else if (direction == Direction.DOWN) {
            fVy += ( (sizeCellX / 2) / speed ) * (speed - stepsInTime);
        } else if (direction == Direction.DOWN_LEFT) {
            fVx += ( (sizeCellX / 2) / speed ) * (speed - stepsInTime);
            fVy += ( (sizeCellX / 4) / speed ) * (speed - stepsInTime);
        } else if (direction == Direction.LEFT) {
            fVx += ( sizeCellX / speed ) * (speed - stepsInTime);
        } else if (direction == Direction.UP_LEFT) {
            fVx += ( (sizeCellX / 2) / speed ) * (speed - stepsInTime);
            fVy -= ( (sizeCellX / 4) / speed ) * (speed - stepsInTime);
        }
        fVc.set(fVx, fVy);
    }

    // что бы ефекты не стакались на крипах
    public Node move(float delta) {
//        Gdx.app.log("Unit", "move(); -- Unit status:" + this.toString());
        if(route != null && !route.isEmpty()) {
            for(ShellEffectType shellEffectType : shellEffectTypes) {
                if(!shellEffectType.used) {
//                    Gdx.app.log("Unit", "move(); -- Active shellEffectType:" + shellEffectType);
                    shellEffectType.used = true;
                    if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                        float smallSpeed = speed/100f;
                        float percentSteps = stepsInTime/smallSpeed;
                        speed += shellEffectType.speed;
                        smallSpeed = speed/100f;
                        stepsInTime = smallSpeed*percentSteps;
                    } else if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                        hp -= shellEffectType.damage;
//                        if(die(shellEffectType.damage, null)) {
//                            GameField.gamerGold += templateForUnit.bounty;
//                        }
                    }
                } else {
                    if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FireEffect) {
                        hp -= shellEffectType.damage;
//                        if(die(shellEffectType.damage, null)) {
//                            GameField.gamerGold += templateForUnit.bounty;
//                        }
                    }
                }
                shellEffectType.elapsedTime += delta;
                if(shellEffectType.elapsedTime >= shellEffectType.time) {
//                    Gdx.app.log("Unit", "move(); -- Remove shellEffectType:" + shellEffectType);
                    if(shellEffectType.shellEffectEnum == ShellEffectType.ShellEffectEnum.FreezeEffect) {
                        float smallSpeed = speed/100f;
                        float percentSteps = stepsInTime/smallSpeed;
                        speed = speed-shellEffectType.speed;
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
//            Gdx.app.log("Unit::move()", "-- fVx:" + fVx + " fVy:" + fVy);
            // --- MANUAL ---

            int oldX = oldPosition.getX(), oldY = oldPosition.getY();
            int newX = newPosition.getX(), newY = newPosition.getY();
            int sizeCellX = GameField.sizeCellX;
            int sizeCellY = GameField.sizeCellY;
            float halfSizeCellX = sizeCellX/2;
            float halfSizeCellY = sizeCellY/2;
            Vector2 fVc = new Vector2(); // fVc = floatVectorCoordinates
            Direction oldDirection = direction;
            int isDrawableUnits = GameField.isDrawableUnits;
            if(isDrawableUnits == 4 || isDrawableUnits == 5) {
//                fVc = new Vector2(getCell(newX, newY).graphicsCoord4)
                float fVx = (-(halfSizeCellX * newY) - (newX * halfSizeCellX)) - halfSizeCellX;
                float fVy = ( (halfSizeCellY * newY) - (newX * halfSizeCellY));
                fVc.set(fVx, fVy);
                if (newX < oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX);
                } else if (newX < oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX);
                } else if (newX < oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX);
                } else if (newX == oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX);
                } else if (newX > oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX);
                } else if (newX > oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX);
                } else if (newX > oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX);
                } else if (newX == oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX);
                }
//                currentPoint.set(fVc);
                circle4.setPosition(fVc);
            }
            if(isDrawableUnits == 3 || isDrawableUnits == 5) {
//                fVc = new Vector2(getCell(newX, newY).graphicsCoord3)
                float fVx = (-(halfSizeCellX * newY) + (newX * halfSizeCellX));
                float fVy = ( (halfSizeCellY * newY) + (newX * halfSizeCellY)) + halfSizeCellY;
                fVc.set(fVx, fVy);
                if (newX < oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX);
                } else if (newX > oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX);
                } else if (newX > oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX);
                } else if (newX == oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX);
                } else if (newX < oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX);
                } else if (newX < oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX);
                } else if (newX < oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX);
                } else if (newX == oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX);
                }
//                currentPoint.set(fVc);
                circle3.setPosition(fVc);
            }
            if(isDrawableUnits == 2 || isDrawableUnits == 5) {
//                fVc = new Vector2(getCell(newX, newY).graphicsCoord2)
                float fVx = (halfSizeCellX * newY) + (newX * halfSizeCellX) + halfSizeCellX;
                float fVy = (halfSizeCellY * newY) - (newX * halfSizeCellY);
                fVc.set(fVx, fVy);
                if (newX < oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX);
                } else if (newX == oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX);
                } else if (newX > oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX);
                } else if (newX > oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX);
                } else if (newX > oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX);
                } else if (newX == oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX);
                } else if (newX < oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX);
                } else if (newX < oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX);
                }
//                currentPoint.set(fVc);
                circle2.setPosition(fVc);
            }
            if(isDrawableUnits == 1 || isDrawableUnits == 5) {
//                fVc = new Vector2(getCell(newX, newY).graphicsCoord1)
                float fVx = (-(halfSizeCellX * newY) + (newX * halfSizeCellX));
                float fVy = (-(halfSizeCellY * newY) - (newX * halfSizeCellY)) - halfSizeCellY;
                fVc.set(fVx, fVy);
                if (newX < oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX);
                } else if (newX == oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX);
                } else if (newX > oldX && newY < oldY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX);
                } else if (newX > oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX);
                } else if (newX > oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX);
                } else if (newX == oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX);
                } else if (newX < oldX && newY > oldY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX);
                } else if (newX < oldX && newY == oldY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX);
                }
//                currentPoint.set(fVc);
                circle1.setPosition(fVc);
            }

            backStepPoint = currentPoint;
            currentPoint.set(fVc);
            fVc = null;

            velocity = new Vector2(backStepPoint.x - currentPoint.x,
                    backStepPoint.y - currentPoint.y).nor().scl(Math.min(currentPoint.dst(backStepPoint.x,
                    backStepPoint.y), speed));
            displacement = new Vector2(velocity.x * delta, velocity.y * delta);

//            Gdx.app.log("Unit::move()", "-- direction:" + direction + " oldDirection:" + oldDirection);
            if(!direction.equals(oldDirection)) {
                setAnimation("walk_");
            }
            return newPosition;
        } else {
//            dispose();
            return null;
        }
    }

    public boolean die(float damage, ShellEffectType shellEffectType) {
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
//                dispose();
                return false;
            } else {
                deathElapsedTime += delta;
            }
            return true;
        }
        return false;
    }

//    public Node getOldPosition() {
//        return oldPosition;
//    }
//    public Node getNewPosition() {
//        return newPosition;
//    }

//    public void setHp(int hp) {
//        this.hp = hp;
//    }
//    public int getHp() {
//        return (int)hp;
//    }
    public boolean isAlive() {
        if(animation == null) { // TODO Не верно, нужно исправить.
            return false;
        }
        return hp > 0 ? true : false;
    }

//    public void setSpeed(float speed) {
//        this.speed = speed;
//    }
//    public float getSpeed() {
//        return speed;
//    }

//    public void setStepsInTime(float stepsInTime) {
//        this.stepsInTime = stepsInTime;
//    }
//    public float getStepsInTime() {
//        return stepsInTime;
//    }

//    public void setRoute(ArrayDeque<Node> route) {
//        this.route = route;
//    }
//    public ArrayDeque<Node> getRoute() {
//        return route;
//    }

    public TextureRegion getCurrentFrame() {
        return animation.getKeyFrame(stepsInTime, true);
    }

    public TextureRegion getCurrentDeathFrame() {
        return animation.getKeyFrame(deathElapsedTime, true);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Unit[");
//        sb.append("route:" + route + ",");
        sb.append("oldPosition:" + oldPosition + ",");
        sb.append("newPosition:" + newPosition + ",");
        sb.append("exitCell:" + (exitCell!=null) + ",");
        sb.append("hp:" + hp + ",");
        sb.append("speed:" + speed + ",");
        sb.append("stepsInTime:" + stepsInTime + ",");
        sb.append("deathElapsedTime:" + deathElapsedTime + ",");

        sb.append("player:" + player + ",");
        sb.append("currentPoint:" + currentPoint + ",");
        sb.append("backStepPoint:" + backStepPoint + ",");
        sb.append("circle1:" + circle1 + ",");
        sb.append("circle2:" + circle2 + ",");
        sb.append("circle3:" + circle3 + ",");
        sb.append("circle4:" + circle4 + ",");
        sb.append("velocity:" + velocity + ",");
        sb.append("displacement:" + displacement + ",");

        sb.append("templateForUnit:" + templateForUnit + ",");
        sb.append("direction:" + direction + ",");
        sb.append("animation:" + animation + ",");
        sb.append("shellEffectTypes:" + shellEffectTypes + ",");
        sb.append("]");
        return sb.toString();
    }
}
