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
    public Cell currentCell;
    public Cell nextCell;
    public Cell exitCell;
    public float hp;
    public float speed;
    public float stepsInTime;
    public float deathElapsedTime;

    public UnitAttack unitAttack;
    public Tower towerAttack;
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

    public Unit(ArrayDeque<Cell> route, TemplateForUnit templateForUnit, int player, Cell exitCell) {
        if(route != null) {
            this.route = route;
            this.currentCell = route.peekFirst();
            this.nextCell = route.pollFirst();
            this.exitCell = exitCell;
            this.hp = templateForUnit.healthPoints;
            this.speed = templateForUnit.speed;
            this.stepsInTime = 0f;//templateForUnit.speed; // need respawn animation
            this.deathElapsedTime = 0f;

            this.unitAttack = (templateForUnit.unitAttack != null) ? (new UnitAttack(templateForUnit.unitAttack)) : null; // in template need create simple UnitAttack!
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
//            route.clear(); // it is not need with Cell but mb need with Node in PathFinder!
            route = null;
        }

//        currentCell.dispose();
        currentCell = null;
//        nextCell.dispose();
        nextCell = null;
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
//        Gdx.app.log("Unit::setAnimation()", "-- action+direction:" + action+direction );
        AnimatedTiledMapTile animatedTiledMapTile = templateForUnit.animations.get(action + direction);
        if (animatedTiledMapTile != null) {
            StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            animation = new Animation(speed / staticTiledMapTiles.length, textureRegions);
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

    private void correct_fVc(Vector2 fVc, Direction direction, float sizeCellX, float sizeCellY, boolean isometric) {
        this.direction = direction;
        float fVx = fVc.x;
        float fVy = fVc.y;
        if (unitAttack == null || unitAttack.elapsedTime <= 0) {
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
                currentCell = nextCell;
                nextCell = route.pollFirst();
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
        Direction oldDirection = direction;
        int oldX = currentCell.cellX, oldY = currentCell.cellY;
        int newX = nextCell.cellX, newY = nextCell.cellY;

        calculateDirection(oldX, oldY, newX, newY, cameraController);

        velocity = new Vector2(backStepPoint.x - currentPoint.x, backStepPoint.y - currentPoint.y);
        velocity.nor().scl(Math.min(currentPoint.dst(backStepPoint.x, backStepPoint.y), speed));
        displacement = new Vector2(velocity.x * delta, velocity.y * delta);

//        Gdx.app.log("Unit::move()", "-- direction:" + direction + " oldDirection:" + oldDirection);
        if(!direction.equals(oldDirection)) {
            setAnimation("walk_");
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
//                fVc = new Vector2(getCell(nextX, nextY).graphicsCoord4)
            float fVx = (-(halfSizeCellX * nextY) - (nextX * halfSizeCellX) ) - halfSizeCellX;
            float fVy = ( (halfSizeCellY * nextY) - (nextX * halfSizeCellY) );
            if (!cameraController.gameField.gameSettings.isometric) {
                fVx = (-(nextX * sizeCellX) ) - halfSizeCellX;
                fVy = ( (nextY * sizeCellY) ) + halfSizeCellY;
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
            circle4.setPosition(fVc);
        }
        if(isDrawableUnits == 3 || isDrawableUnits == 5) {
//                fVc = new Vector2(getCell(nextX, nextY).graphicsCoord3)
            float fVx = (-(halfSizeCellX * nextY) + (nextX * halfSizeCellX) );
            float fVy = ( (halfSizeCellY * nextY) + (nextX * halfSizeCellY) ) + halfSizeCellY;
            if (!cameraController.gameField.gameSettings.isometric) {
                fVx = ( (nextX * sizeCellX) ) + halfSizeCellX;
                fVy = ( (nextY * sizeCellY) ) + halfSizeCellY;
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
            circle3.setPosition(fVc);
        }
        if(isDrawableUnits == 2 || isDrawableUnits == 5) {
//                fVc = new Vector2(getCell(nextX, nextY).graphicsCoord2)
            float fVx = ( (halfSizeCellX * nextY) + (nextX * halfSizeCellX) ) + halfSizeCellX;
            float fVy = ( (halfSizeCellY * nextY) - (nextX * halfSizeCellY) );
            if (!cameraController.gameField.gameSettings.isometric) {
                fVx = ( (nextX * sizeCellX) ) + halfSizeCellX;
                fVy = (-(nextY * sizeCellY) ) - halfSizeCellY;
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
            circle2.setPosition(fVc);
        }
        if(isDrawableUnits == 1 || isDrawableUnits == 5) {
//                fVc = new Vector2(getCell(nextX, nextY).graphicsCoord1)
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
            circle1.setPosition(fVc);
        }

        backStepPoint.set(currentPoint);
        currentPoint.set(fVc);
        fVc = null; // delete fVc;
    }

    public boolean recharge(float deltaTime) {
        if (unitAttack.elapsedTime > 0) {
            stepsInTime += deltaTime;
            if (stepsInTime >= unitAttack.reload) {
//                unitAttack.elapsedTime = 0;
                return true;
            }
        }
        return false;
    }

    public boolean towerAttack(float deltaTime) {
        if (unitAttack.elapsedTime > 0) {
            unitAttack.elapsedTime += deltaTime;
            if (unitAttack.elapsedTime >= unitAttack.attackSpeed) {
                unitAttack.elapsedTime = 0;
                towerAttack.hp -= unitAttack.damage;
                return true;
            }
        }
        return false;
    }

    public void towerAttack(float deltaTime, Tower tower, CameraController cameraController) {
        unitAttack.elapsedTime += deltaTime;
        towerAttack = tower;

        int oldX = currentCell.cellX, oldY = currentCell.cellY;
        int newX = tower.cell.cellX, newY = tower.cell.cellY;

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
        if (unitAttack != null && unitAttack.elapsedTime > 0) {
            return (TextureRegion) animation.getKeyFrame(unitAttack.elapsedTime, true);
        } else {
            return null;
        }
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
            sb.append(",currentPoint:" + currentPoint);
            sb.append(",backStepPoint:" + backStepPoint);
            sb.append(",circle1:" + circle1);
            sb.append(",circle2:" + circle2);
            sb.append(",circle3:" + circle3);
            sb.append(",circle4:" + circle4);
            sb.append(",velocity:" + velocity);
            sb.append(",displacement:" + displacement);

            sb.append(",direction:" + direction);
            sb.append(",animation:" + animation);
            sb.append(",shellEffectTypes:" + shellEffectTypes);
        }
        sb.append(",templateForUnit:" + templateForUnit);
        sb.append("]");
        return sb.toString();
    }
}
