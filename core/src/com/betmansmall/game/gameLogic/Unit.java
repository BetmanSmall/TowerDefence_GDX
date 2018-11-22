package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTile;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;
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
    public Array<TowerShellEffect> shellEffectTypes;

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
            this.currentPoint = new Vector2();
            this.backStepPoint = new Vector2();
            this.circle1 = new Circle(0, 0, 16f);
            this.circle2 = new Circle(0, 0, 16f);
            this.circle3 = new Circle(0, 0, 16f);
            this.circle4 = new Circle(0, 0, 16f);

            this.templateForUnit = templateForUnit;

            this.direction = Direction.UP;
            setAnimation("walk_");
            this.shellEffectTypes = new Array<TowerShellEffect>();
        } else {
            Gdx.app.error("Unit::Unit()", "-- route == null");
        }
    }

    public void dispose() {
        if (route != null) {
            route.clear();
            route = null;
        }

        oldPosition.clear();
        oldPosition = null;
        newPosition.clear();
        newPosition = null;
        exitCell = null;

        currentPoint = null;
        backStepPoint = null;
        circle1 = null;
        circle2 = null;
        circle3 = null;
        circle4 = null;
        velocity = null;
        displacement = null;

        templateForUnit = null;

        direction = null;
        animation = null;
        shellEffectTypes.clear();
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

    void shellEffectsMove(float delta) {
        for (TowerShellEffect towerShellEffect : shellEffectTypes) {
//            Gdx.app.log("Unit::shellEffectsMove()", "-- towerShellEffect:" + towerShellEffect);
            if (!towerShellEffect.used) {
                towerShellEffect.used = true;
                if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
                    float smallSpeed = speed/100f;
                    float percentSteps = stepsInTime/smallSpeed;
                    speed += towerShellEffect.speed;
                    smallSpeed = speed/100f;
                    stepsInTime = smallSpeed*percentSteps;
                } else if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FireEffect) {
                    hp -= towerShellEffect.damage;
//                    if(die(towerShellEffect.damage, null)) {
//                        GameField.gamerGold += templateForUnit.bounty;
//                    }
                }
            } else {
                if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FireEffect) {
                    hp -= towerShellEffect.damage;
//                    if(die(towerShellEffect.damage, null)) {
//                        GameField.gamerGold += templateForUnit.bounty;
//                    }
                }
            }
            towerShellEffect.elapsedTime += delta;
            if (towerShellEffect.elapsedTime >= towerShellEffect.time) {
//                Gdx.app.log("Unit::shellEffectsMove()", "-- Remove towerShellEffect:" + towerShellEffect);
                if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
                    float smallSpeed = speed/100f;
                    float percentSteps = stepsInTime/smallSpeed;
                    speed = speed- towerShellEffect.speed;
                    smallSpeed = speed/100f;
                    stepsInTime = smallSpeed*percentSteps;
                }
                shellEffectTypes.removeValue(towerShellEffect, true);
            }
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

    // --- MANUAL ---
//        if(newX < oldX && newY > oldY) {
//        } else if (newX == oldX && newY > oldY) {
//        } else if (newX > oldX && newY > oldY) {
//        } else if (newX > oldX && newY == oldY) {
//        } else if (newX > oldX && newY < oldY) {
//        } else if (newX == oldX && newY < oldY) {
//        } else if (newX < oldX && newY < oldY) {
//        } else if (newX < oldX && newY == oldY) {
//        }
    // --- MANUAL ---

    // что бы ефекты не стакались на крипах
    public Node move(float delta, CameraController cameraController) {
//        Gdx.app.log("Unit", "move(); -- Unit status:" + this.toString());
        shellEffectsMove(delta);
//        stepsInTime += (speed*delta);
        stepsInTime += delta; // wtf? check Bullet::flightOfShell()
        if (stepsInTime >= speed) {
            if(route != null && !route.isEmpty()) {
                stepsInTime = 0f;
                oldPosition = newPosition;
                newPosition = route.pollFirst();
                if (newPosition == null) {
                    newPosition = oldPosition;
                }
            } else {
                direction = Direction.UP;
                setAnimation("idle_");
                return null;
            }
        }
        int oldX = oldPosition.getX(), oldY = oldPosition.getY();
        int newX = newPosition.getX(), newY = newPosition.getY();
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY;
        float halfSizeCellX = sizeCellX/2;
        float halfSizeCellY = sizeCellY/2;
        Vector2 fVc = new Vector2(); // fVc = floatVectorCoordinates
        Direction oldDirection = direction;
        int isDrawableUnits = cameraController.isDrawableUnits;
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
            circle1.setPosition(fVc);
        }

        backStepPoint.set(currentPoint);
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
    }

    public boolean die(float damage, TowerShellEffect towerShellEffect) {
        if(hp > 0) {
            hp -= damage;
            addEffect(towerShellEffect);
            if(hp <= 0) {
                deathElapsedTime = 0;
                setAnimation("death_");
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean addEffect(TowerShellEffect towerShellEffect) {
        if(towerShellEffect != null){
            if(!shellEffectTypes.contains(towerShellEffect, false)) {
                shellEffectTypes.add(new TowerShellEffect(towerShellEffect));
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
