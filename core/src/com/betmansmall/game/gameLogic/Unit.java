package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.mapLoader.AnimatedTiledMapTile;
import com.betmansmall.game.gameLogic.mapLoader.StaticTiledMapTile;
import com.betmansmall.game.gameLogic.playerTemplates.Direction;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.badlogic.gdx.math.Circle;
import com.betmansmall.game.gameLogic.playerTemplates.UnitAttack;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class Unit {
    public ArrayDeque<Cell> route;
    public TemplateForUnit templateForUnit;
    public int player; // In Future need change to enumPlayers {Computer0, Player1, Player2} and etc

    public Cell exitCell;
    public Cell currentCell;
    public Cell nextCell;

    public float hp;
    public float speed;
    public float stepsInTime;
    public float deathElapsedTime;

    public Tower towerAttack;
    public UnitAttack unitAttack;
    public Vector2 currentPoint;
    public Vector2 backStepPoint;
    public Vector2 velocity;
    public Vector2 displacement;

//    public Circle circle1;
//    public Circle circle2;
//    public Circle circle3;
//    public Circle circle4;
    public Array<Circle> circles;

    public Array<TowerShellEffect> shellEffectTypes;
    public Array<UnitBullet> bullets;
    public Direction direction;
    private Animation animation;

    public Unit(ArrayDeque<Cell> route, TemplateForUnit templateForUnit, int player, Cell exitCell) {
        if(route != null) {
            this.route = route;
            this.templateForUnit = templateForUnit;
            this.player = player;

            this.exitCell = exitCell;
            this.currentCell = route.peekFirst();
            this.nextCell = route.pollFirst();

            this.hp = templateForUnit.healthPoints;
            this.speed = templateForUnit.speed;
            this.stepsInTime = 0f;//templateForUnit.speed; // need respawn animation
            this.deathElapsedTime = 0f;

            this.towerAttack = null;
            this.unitAttack = (templateForUnit.unitAttack != null) ? (new UnitAttack(templateForUnit.unitAttack)) : null; // in template need create simple UnitAttack!
            this.currentPoint = new Vector2();
            this.backStepPoint = new Vector2();
            this.velocity = new Vector2();
            this.displacement = new Vector2();

//            this.circle1 = new Circle(0, 0, 16f);
//            this.circle2 = new Circle(0, 0, 16f);
//            this.circle3 = new Circle(0, 0, 16f);
//            this.circle4 = new Circle(0, 0, 16f);
            this.circles = new Array<Circle>(4);
            this.circles.add(new Circle(0, 0, 16f));
            this.circles.add(new Circle(0, 0, 16f));
            this.circles.add(new Circle(0, 0, 16f));
            this.circles.add(new Circle(0, 0, 16f));

            this.shellEffectTypes = new Array<TowerShellEffect>();
            this.bullets = new Array<UnitBullet>();
            this.direction = Direction.UP;
            this.setAnimation("walk_");
        } else {
            Gdx.app.error("Unit::Unit()", "-- route == null");
        }
    }

    public void dispose() {
        if (route != null) {
//            route.clear(); // it is not need with Cell but mb need with Node in PathFinder!
            this.route = null;
        }
//        this.templateForUnit.dispose();
        this.templateForUnit = null;
//        this.player = 0;

//        this.exitCell.dispose();
        this.exitCell = null;
//        this.currentCell.dispose();
        this.currentCell = null;
//        this.nextCell.dispose();
        this.nextCell = null;

//        this.hp = 0;
//        this.speed = 0;
//        this.stepsInTime = 0;
//        this.deathElapsedTime = 0;

        this.towerAttack = null;
        this.unitAttack = null;
        this.currentPoint = null;
        this.backStepPoint = null;
        this.velocity = null;
        this.displacement = null;

//        this.circle1 = null;
//        this.circle2 = null;
//        this.circle3 = null;
//        this.circle4 = null;
        this.circles.clear();
        this.circles = null;

        this.shellEffectTypes.clear();
        this.shellEffectTypes = null;
        this.bullets.clear();
        this.bullets = null;
        this.direction = null;
        this.animation = null;
    }

    private void setAnimation(String action) { // Action transform to Enum
//        Gdx.app.log("Unit::setAnimation()", "-- action+direction:" + action+direction );
        AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get(action + direction);
        if (animatedTiledMapTile != null) {
            StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            if (action.equals("attack_")) { // check this how AnimationActions.type.Attack
                animation = new Animation(unitAttack.attackSpeed / staticTiledMapTiles.length, textureRegions);
            } else if (action.equals("walk_")){
                animation = new Animation(speed / staticTiledMapTiles.length, textureRegions);
            } else if (action.equals("death_")){
                animation = new Animation(templateForUnit.speed / staticTiledMapTiles.length, textureRegions); // speed change to speedToDIE;
            } else {
                animation = new Animation(speed / staticTiledMapTiles.length, textureRegions); // speed change to idleSpeed
            }
//            Gdx.app.log("Unit::setAnimation()", "-- animation:" + animation + " textureRegions:" + textureRegions[0]);
        } else {
            Gdx.app.log("Unit::setAnimation(" + action + direction + ")", "-- UnitName: " + templateForUnit.name + " animatedTiledMapTile: " + animatedTiledMapTile);
        }
    }

    public void shellEffectsMove(float delta) {
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
                    if (unitAttack != null) {
                        float smallSpeed2 = unitAttack.attackSpeed / 100f;
                        float percentSteps2 = unitAttack.elapsedTimeAttacked / smallSpeed2;
                        unitAttack.attackSpeed += towerShellEffect.speed;
                        smallSpeed2 = unitAttack.attackSpeed / 100f;
                        unitAttack.elapsedTimeAttacked = smallSpeed2 * percentSteps2;
                    }
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
                    if (unitAttack != null) {
                        float smallSpeed2 = unitAttack.attackSpeed / 100f;
                        float percentSteps2 = unitAttack.elapsedTimeAttacked / smallSpeed2;
                        unitAttack.attackSpeed = unitAttack.attackSpeed - towerShellEffect.speed;
                        smallSpeed2 = unitAttack.attackSpeed / 100f;
                        unitAttack.elapsedTimeAttacked = smallSpeed2 * percentSteps2;
                    }
                }
                shellEffectTypes.removeValue(towerShellEffect, true);
            }
        }
    }

    private void correct_fVc(Vector2 fVc, Direction direction, float sizeCellX, float sizeCellY, boolean isometric) {
        this.direction = direction;
        float fVx = fVc.x;
        float fVy = fVc.y;
        if (unitAttack == null || !unitAttack.attacked) {
            if (direction == Direction.UP) {
                fVy += ((sizeCellY) / speed) * (stepsInTime);
            } else if (direction == Direction.UP_RIGHT) {
                if (isometric) {
                    fVx += ((sizeCellX / 2) / speed) * (stepsInTime);
                    fVy += ((sizeCellY / 2) / speed) * (stepsInTime);
                } else {
                    fVx += ((sizeCellX) / speed) * (stepsInTime);
                    fVy += ((sizeCellY) / speed) * (stepsInTime);
                }
            } else if (direction == Direction.RIGHT) {
                fVx += (sizeCellX / speed) * (stepsInTime);
            } else if (direction == Direction.DOWN_RIGHT) {
                if (isometric) {
                    fVx += ((sizeCellX / 2) / speed) * (stepsInTime);
                    fVy -= ((sizeCellY / 2) / speed) * (stepsInTime);
                } else {
                    fVx += ((sizeCellX) / speed) * (stepsInTime);
                    fVy -= ((sizeCellY) / speed) * (stepsInTime);
                }
            } else if (direction == Direction.DOWN) {
                fVy -= ((sizeCellY) / speed) * (stepsInTime);
            } else if (direction == Direction.DOWN_LEFT) {
                if (isometric) {
                    fVx -= ((sizeCellX / 2) / speed) * (stepsInTime);
                    fVy -= ((sizeCellY / 2) / speed) * (stepsInTime);
                } else {
                    fVx -= ((sizeCellX) / speed) * (stepsInTime);
                    fVy -= ((sizeCellY) / speed) * (stepsInTime);
                }
            } else if (direction == Direction.LEFT) {
                fVx -= (sizeCellX / speed) * (stepsInTime);
            } else if (direction == Direction.UP_LEFT) {
                if (isometric) {
                    fVx -= ((sizeCellX / 2) / speed) * (stepsInTime);
                    fVy += ((sizeCellY / 2) / speed) * (stepsInTime);
                } else {
                    fVx -= ((sizeCellX) / speed) * (stepsInTime);
                    fVy += ((sizeCellY) / speed) * (stepsInTime);
                }
            }
        }
        fVc.set(fVx, fVy);
    }

// --- MANUAL ---
//    if (newX < oldX && newY > oldY) {
//    } else if (newX == oldX && newY > oldY) {
//    } else if (newX > oldX && newY > oldY) {
//    } else if (newX > oldX && newY == oldY) {
//    } else if (newX > oldX && newY < oldY) {
//    } else if (newX == oldX && newY < oldY) {
//    } else if (newX < oldX && newY < oldY) {
//    } else if (newX < oldX && newY == oldY) {
//    }
// --- MANUAL ---

    // что бы ефекты не стакались на крипах
    public Cell move(float delta, CameraController cameraController) {
//        Gdx.app.log("Unit", "move(); -- Unit status:" + this.toString());
        shellEffectsMove(delta);
//        stepsInTime += (speed*delta);
        stepsInTime += delta; // wtf? check Bullet::flightOfShell()
        if (stepsInTime >= speed) {
            if (route != null && !route.isEmpty()) {
                stepsInTime = 0f;
                if (unitAttack == null || (!unitAttack.attacked && towerAttack == null) ) {
                    currentCell = nextCell;
                    nextCell = route.pollFirst();
                }
//                if (nextCell == null) {
//                    nextCell = currentCell;
//                }
//                return nextCell;
            } else {
                direction = Direction.UP;
                setAnimation("idle_");
                return null;
            }
        }
        if (unitAttack == null || (!unitAttack.attacked && towerAttack == null) ) {
            Direction oldDirection = direction;
            int oldX = currentCell.cellX, oldY = currentCell.cellY;
            int newX = nextCell.cellX, newY = nextCell.cellY;

            calculateDirection(oldX, oldY, newX, newY, cameraController);

            velocity = new Vector2(backStepPoint.x - currentPoint.x, backStepPoint.y - currentPoint.y);
            velocity.nor().scl(Math.min(currentPoint.dst(backStepPoint.x, backStepPoint.y), speed));
            displacement = new Vector2(velocity.x * delta, velocity.y * delta);

//        Gdx.app.log("Unit::move()", "-- direction:" + direction + " oldDirection:" + oldDirection);
            if (!direction.equals(oldDirection)) {
                setAnimation("walk_");
            }
        }
        return currentCell;
    }

    private void calculateDirection(int currX, int currY, int nextX, int nextY, CameraController cameraController) {
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY;
        float halfSizeCellX = sizeCellX/2;
        float halfSizeCellY = sizeCellY/2;
        Vector2 fVc = new Vector2(); // fVc = floatVectorCoordinates
        int isDrawableUnits = cameraController.isDrawableUnits;
        if (isDrawableUnits == 4 || isDrawableUnits == 5) {
//            fVc.set(cameraController.gameField.getCell(currX, currY).getGraphicCoordinates(isDrawableUnits));
            float fVx = (-(halfSizeCellX * currY) - (currX * halfSizeCellX) ) - halfSizeCellX;
            float fVy = ( (halfSizeCellY * currY) - (currX * halfSizeCellY) );
            if (!cameraController.gameField.gameSettings.isometric) {
                fVx = (-(currX * sizeCellX) ) - halfSizeCellX;
                fVy = ( (currY * sizeCellY) ) + halfSizeCellY;
            }
            fVc.set(fVx, fVy);
            if (cameraController.gameField.gameSettings.isometric) {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            } else {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            }
            if (unitAttack == null || !unitAttack.attacked) {
                Circle circle4 = circles.get(3);
                circle4.setPosition(fVc);
//                this.circles.insert(3, circle4);
//                this.circle4.set(circle4);
            }
        }
        if(isDrawableUnits == 3 || isDrawableUnits == 5) {
//            fVc.set(cameraController.gameField.getCell(currX, currY).getGraphicCoordinates(isDrawableUnits));
            float fVx = (-(halfSizeCellX * currY) + (currX * halfSizeCellX) );
            float fVy = ( (halfSizeCellY * currY) + (currX * halfSizeCellY) ) + halfSizeCellY;
            if (!cameraController.gameField.gameSettings.isometric) {
                fVx = ( (currX * sizeCellX) ) + halfSizeCellX;
                fVy = ( (currY * sizeCellY) ) + halfSizeCellY;
            }
            fVc.set(fVx, fVy);
            if (cameraController.gameField.gameSettings.isometric) {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            } else {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            }
            if (unitAttack == null || !unitAttack.attacked) {
                Circle circle3 = circles.get(2);
                circle3.setPosition(fVc);
//                this.circles.insert(2, circle3);
//                this.circle3.set(circle3);
            }
        }
        if(isDrawableUnits == 2 || isDrawableUnits == 5) {
//            fVc.set(cameraController.gameField.getCell(currX, currY).getGraphicCoordinates(isDrawableUnits));
            float fVx = ( (halfSizeCellX * currY) + (currX * halfSizeCellX) ) + halfSizeCellX;
            float fVy = ( (halfSizeCellY * currY) - (currX * halfSizeCellY) );
            if (!cameraController.gameField.gameSettings.isometric) {
                fVx = ( (currX * sizeCellX) ) + halfSizeCellX;
                fVy = (-(currY * sizeCellY) ) - halfSizeCellY;
            }
            fVc.set(fVx, fVy);
            if (cameraController.gameField.gameSettings.isometric) {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            } else {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            }
            if (unitAttack == null || !unitAttack.attacked) {
                Circle circle2 = circles.get(1);
                circle2.setPosition(fVc);
//                this.circles.insert(1, circle2);
//                this.circle2.set(circle2);
            }
        }
        if(isDrawableUnits == 1 || isDrawableUnits == 5) {
//            fVc.set(cameraController.gameField.getCell(currX, currY).getGraphicCoordinates(cameraController.isDrawableUnits));
            float fVx = (-(halfSizeCellX * currY) + (currX * halfSizeCellX) );
            float fVy = (-(halfSizeCellY * currY) - (currX * halfSizeCellY) ) - halfSizeCellY;
            if (!cameraController.gameField.gameSettings.isometric) {
                fVx = (-(currX * sizeCellX)) - halfSizeCellX;
                fVy = (-(currY * sizeCellY)) - halfSizeCellY;
            }
            fVc.set(fVx, fVy);
            if (cameraController.gameField.gameSettings.isometric) {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            } else {
                if (nextX < currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY > currY) {
                    correct_fVc(fVc, Direction.DOWN_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY == currY) {
                    correct_fVc(fVc, Direction.LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX > currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP_LEFT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX == currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY < currY) {
                    correct_fVc(fVc, Direction.UP_RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                } else if (nextX < currX && nextY == currY) {
                    correct_fVc(fVc, Direction.RIGHT, sizeCellX, sizeCellY, cameraController.gameField.gameSettings.isometric);
                }
            }
            if (unitAttack == null || !unitAttack.attacked) {
                Circle circle1 = circles.get(0);
                circle1.setPosition(fVc);
//                this.circles.insert(0, circle1);
//                this.circle1.set(circle1);
            }
        }

        if (unitAttack == null || !unitAttack.attacked) {
//            circle4.setPosition(fVc);
//            circle3.setPosition(fVc);
//            circle2.setPosition(fVc);
//            circle1.setPosition(fVc);
            backStepPoint.set(currentPoint);
            currentPoint.set(fVc);
            if (unitAttack != null && unitAttack.circle != null) {
                unitAttack.circle.setPosition(fVc);
            }
        }
        fVc = null; // delete fVc;
    }

    public boolean tryAttackTower(float delta, final CameraController cameraController) {
        if (unitAttack != null) {
//            UnitAttack.AttackType attackType = unitAttack.attackType;
//            if (attackType == UnitAttack.AttackType.Melee) {
//                if ( (!unitAttack.stackInOneCell && this.equals(currentCell.getUnit())) || unitAttack.stackInOneCell) {
                    if (recharge(delta)) {
                        if (tryFoundTower(cameraController) != null) {
//                            return towerAttack(delta, cameraController);
                            if (!towerAttack(delta, cameraController)) {
                                return true;
                            }
                        }
                    }
//                    if (!unitAttack.attacked || towerAttack == null) {
//                        if (tryFoundTower(cameraController) != null) {
//                            return true;
//                        }
//                    } else {
//                        return true;
//                    }
//                }
//            } else if (attackType == UnitAttack.AttackType.Range) {
//                if (recharge(delta)) {
//                    towerAttack(delta, cameraController);
//                }
//                if (!unitAttack.attacked || towerAttack == null) {
//                    if (tryFoundTower(cameraController) != null) {
////                        continue;
//                    }
//                } else {
////                    continue;
//                }
//            }
        }
        return false;
    }

    public Tower tryFoundTower(final CameraController cameraController) {
        if (towerAttack == null) {
//            if ( (!unitAttack.stackInOneCell && this.equals(currentCell.getUnit())) || unitAttack.stackInOneCell) {
            Unit unit = currentCell.getUnit();
            if (unitAttack.stackInOneCell || (unit != null && unit.equals(this)) ) {
                int radius = Math.round(unitAttack.range);
                if (unitAttack.attackType == UnitAttack.AttackType.Melee) {
                    for (int tmpX = -radius; tmpX <= radius; tmpX++) {
                        for (int tmpY = -radius; tmpY <= radius; tmpY++) {
                            Cell cell = cameraController.gameField.getCell(tmpX + currentCell.cellX, tmpY + currentCell.cellY);
                            if (cell != null) {
                                Tower tower = cell.getTower();
                                if (tower != null) {
                                    if (tower.isNotDestroyed()) {
                                        towerAttackInit(tower, cameraController);
                                        return tower;
                                    }
                                }
                            }
                        }
                    }
                } else if (unitAttack.attackType == UnitAttack.AttackType.Range) {
                    for (Tower tower : cameraController.gameField.towersManager.towers) {
                        if (tower.templateForTower.towerAttackType != TowerAttackType.Pit) {
                            if (tower.isNotDestroyed()) {
                                Circle towerCircle = tower.getCircle(cameraController.isDrawableTowers);
                                if (towerCircle != null) {
                                    if (unitAttack.circle.overlaps(towerCircle)) {
                                        towerAttackInit(tower, cameraController);
                                        return tower;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Gdx.app.log("Unit::tryFoundTower()", "-- unitAttack.attackType:" + unitAttack.attackType);
                }
            }
            return null;
        } else {
            if (unitAttack.elapsedTimeRecharge >= unitAttack.reload) {
//                unitAttack.attacked = true;
//                towerAttackInit(towerAttack, cameraController);
                updateTowerAttack(cameraController);
            }
        }
        return towerAttack;
    }

    public boolean recharge(float deltaTime) {
//        Gdx.app.log("Unit::recharge()", " -- unitAttack.elapsedTimeRecharge:" + unitAttack.elapsedTimeRecharge);
//        Gdx.app.log("Unit::recharge()", " -- unitAttack.reload:" + unitAttack.reload);
//        if (towerAttack != null) {
//            if (!unitAttack.attacked) {
                unitAttack.elapsedTimeRecharge += deltaTime;
                if (unitAttack.elapsedTimeRecharge >= unitAttack.reload) {
                    return true;
                }
//            } else {
//                return true;
//            }
//        }
        return false;
    }

    public boolean towerAttack(float deltaTime, final CameraController cameraController) {
//        Gdx.app.log("Unit::towerAttack()", " -- unitAttack.elapsedTimeAttacked:" + unitAttack.elapsedTimeAttacked);
//        Gdx.app.log("Unit::towerAttack()", " -- unitAttack.attackSpeed:" + unitAttack.attackSpeed);
//        unitAttack.attacked = true;
        unitAttack.elapsedTimeAttacked += deltaTime;
        if (unitAttack.elapsedTimeAttacked >= unitAttack.attackSpeed) {
            unitAttack.elapsedTimeAttacked = 0;
            unitAttack.elapsedTimeRecharge = 0;
            if (unitAttack.attackType == UnitAttack.AttackType.Melee) {
                if (towerAttack.destroy(unitAttack.damage)) {
                    towerAttack = null;
                }
            } else if (unitAttack.attackType == UnitAttack.AttackType.Range) {
                Vector2 vector2 = new Vector2(currentPoint);
                vector2.add(0, cameraController.halfSizeCellY);
                bullets.add(new UnitBullet(vector2, templateForUnit, towerAttack));
            }
            if (!unitAttack.stayToDie) {
                towerAttack = null;
//                return false;
            } else {
                direction = Direction.UP;
                setAnimation("idle_");
            }
            unitAttack.attacked = false;
            return true;
        }
        return false;
    }

    public boolean towerAttackInit(Tower tower, CameraController cameraController) {
//        Gdx.app.log("Unit::towerAttackInit()", " -- unitAttack.elapsedTimeAttacked:" + unitAttack.elapsedTimeAttacked);
//        if (tower.isNotDestroyed()) {
            unitAttack.elapsedTimeRecharge = unitAttack.reload;
            unitAttack.elapsedTimeAttacked = 0;
            towerAttack = tower;
            towerAttack.whoAttackMe.add(this);

            updateTowerAttack(cameraController);
            return true;
//        }
//        return false;
    }

    private void updateTowerAttack(CameraController cameraController) {
        unitAttack.attacked = true;
        int oldX = currentCell.cellX, oldY = currentCell.cellY;
        int newX = towerAttack.cell.cellX, newY = towerAttack.cell.cellY;

        calculateDirection(oldX, oldY, newX, newY, cameraController);
        setAnimation("attack_");
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

    public void moveAllShells(float delta, CameraController cameraController) {
//        Logger.logDebug("-- bullets:" + bullets);
        for (UnitBullet bullet : bullets) {
            moveShell(delta, bullet, cameraController);
        }
    }

    private void moveShell(float delta, UnitBullet bullet, CameraController cameraController) {
        switch (bullet.flightOfShell(delta, cameraController)) {
            case 1:
                break;
            case 0:
//                break;
            case -1:
                bullets.removeValue(bullet, false);
                break;
        }
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
            if(deathElapsedTime >= templateForUnit.speed) { // need change to speedToDie
//                dispose();
                return false;
            } else {
                deathElapsedTime += delta;
            }
            return true;
        }
        return false;
    }

    public boolean isAlive() {
//        if(animation == null) { // TODO Не верно, нужно исправить.
//            return false;
//        }
        return hp > 0 ? true : false;
    }

    public TextureRegion getCurrentFrame() {
        return (TextureRegion)animation.getKeyFrame(stepsInTime, true);
    }

    public TextureRegion getCurrentDeathFrame() {
        if (animation != null) {
            return (TextureRegion) animation.getKeyFrame(deathElapsedTime, true);
        }
        return null;
    }

    public TextureRegion getCurrentAttackFrame() {
        if (unitAttack != null && unitAttack.attacked) {
            return (TextureRegion) animation.getKeyFrame(unitAttack.elapsedTimeAttacked, true);
        } else {
            return null;
        }
    }

    public Circle getCircle(int map) {
//        if(map == 1) {
//            return circle1;
//        } else if(map == 2) {
//            return circle2;
//        } else if(map == 3) {
//            return circle3;
//        } else if(map == 4) {
//            return circle4;
//        }

        map = (map == 5) ? 1 : map;
        if (map > 0 && map < 5) {
            return circles.get(map-1);
        }
        Gdx.app.log("Unit::getCircle(" + map + ")", "-- Bad map | return null!");
        return null;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unit[");
//        sb.append("route:" + route);
        sb.append("currentCell:" + currentCell);
        sb.append(",nextCell:" + nextCell);
        sb.append(",exitCell:" + (exitCell!=null) );
        sb.append(",hp:" + hp);
        sb.append(",speed:" + speed);
        if (full) {
            sb.append(",stepsInTime:" + stepsInTime);
            sb.append(",deathElapsedTime:" + deathElapsedTime);

            sb.append(",player:" + player);
            sb.append(",towerAttack:" + towerAttack);
            sb.append(",unitAttack:" + unitAttack);
            sb.append(",currentPoint:" + currentPoint);
            sb.append(",backStepPoint:" + backStepPoint);
            sb.append(",velocity:" + velocity);
            sb.append(",displacement:" + displacement);

//            sb.append(",circle1:" + circle1);
//            sb.append(",circle2:" + circle2);
//            sb.append(",circle3:" + circle3);
//            sb.append(",circle4:" + circle4);
            sb.append(",circles:" + circles);

            sb.append(",shellEffectTypes:" + shellEffectTypes);
            sb.append(",bullets:" + bullets);
            sb.append(",direction:" + direction);
            sb.append(",animation:" + animation);
        }
        sb.append(",templateForUnit:" + templateForUnit);
        sb.append("]");
        return sb.toString();
    }
}
