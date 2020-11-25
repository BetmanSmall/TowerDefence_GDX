package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.playerTemplates.SimpleTemplate;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.badlogic.gdx.math.Circle; // AlexGor
import com.badlogic.gdx.math.Vector2; //AlexGor
import com.betmansmall.utils.logging.Logger;

/**
 * Created by Андрей on 24.01.2016.
 */
public class Tower {
    public Cell cell;
    public TemplateForTower templateForTower;
    public Player player;

    public Array<Unit> whoAttackMe;
    public Array<Bullet> bullets;
    public Array<Circle> circles;

    public Vector2 centerGraphicCoordinates;
    public Circle radiusDetectionCircle;
    public Circle radiusFlyShellCircle;

    public float destroyElapsedTime;
    public Animation animation;
    public float burningElapsedTime;
    public Animation burningAnimation;
    public Integer currBurningIndex;

    public float elapsedReloadTime;
    public float hp;
    public int capacity;

    public Tower(Cell cell, TemplateForTower templateForTower, Player player) {
//        Gdx.app.log("Tower::Tower()", "-- cell:" + cell + " templateForTower:" + templateForTower + " player:" + player);
        this.cell = cell;
        this.templateForTower = templateForTower;
        this.player = player;

        this.whoAttackMe = new Array<>();
        this.bullets = new Array<>();
        this.circles = new Array<>(4);
        this.circles.add(new Circle(0f, 0f, 16f));
        this.circles.add(new Circle(0f, 0f, 16f));
        this.circles.add(new Circle(0f, 0f, 16f));
        this.circles.add(new Circle(0f, 0f, 16f));

        this.centerGraphicCoordinates = new Vector2();
        this.radiusDetectionCircle = new Circle(0, 0, templateForTower.radiusDetection);
        this.radiusFlyShellCircle = null;

        this.destroyElapsedTime = 0f;
        this.animation = null;
        this.burningElapsedTime = 0f;
        this.burningAnimation = null;
        this.currBurningIndex = null;

        this.elapsedReloadTime = templateForTower.reloadTime;
        this.hp = templateForTower.healthPoints;
        this.capacity = (templateForTower.capacity != null) ? templateForTower.capacity : 0;
    }

    public void dispose() {
        Logger.logDebug("player:" + player, "cell:" + cell, "templateForTower:" + templateForTower);
        this.cell = null;
        this.templateForTower = null;
//        this.player = 0;

        this.whoAttackMe.clear();
        this.whoAttackMe = null;
        this.bullets.clear();
        this.bullets = null;
        this.circles.clear();
        this.circles = null;

        this.centerGraphicCoordinates = null;
        this.radiusDetectionCircle = null;
        this.radiusFlyShellCircle = null;

//        this.destroyElapsedTime = 0;
        this.animation = null;
//        this.burningElapsedTime = 0;
        this.burningAnimation = null;
        this.currBurningIndex = null;
//        this.elapsedReloadTime = 0;
//        this.hp = 0;
//        this.capacity = 0;
    }

    void updateCenterGraphicCoordinates(CameraController cameraController) {
//        if (cameraController.isDrawableTowers == 1) {
//            centerGraphicCoordinates.set(cell.graphicCoordinates1);
//            circles.get(0).setPosition(cell.graphicCoordinates1);
//        } else if (cameraController.isDrawableTowers == 2) {
//            centerGraphicCoordinates.set(cell.graphicCoordinates2);
//            circles.get(1).setPosition(cell.graphicCoordinates2);
//        } else if (cameraController.isDrawableTowers == 3) {
//            centerGraphicCoordinates.set(cell.graphicCoordinates3);
//            circles.get(2).setPosition(cell.graphicCoordinates3);
//        } else if (cameraController.isDrawableTowers == 4) {
//            centerGraphicCoordinates.set(cell.graphicCoordinates4);
//            circles.get(3).setPosition(cell.graphicCoordinates4);
//        } else if (cameraController.isDrawableTowers == 5) {
//            centerGraphicCoordinates.set(cell.graphicCoordinates1);
//            circles.get(0).setPosition(cell.graphicCoordinates1);
//        } else {
//            centerGraphicCoordinates.setZero();
//        }
        int isDrawableTowers = (cameraController.isDrawableTowers == 5) ? 1 : cameraController.isDrawableTowers;
        if (isDrawableTowers > 0 && isDrawableTowers < 5) {
            centerGraphicCoordinates.set(cell.getGraphicCoordinates(isDrawableTowers));

            Circle circle = circles.get(isDrawableTowers - 1);
            circle.setPosition(centerGraphicCoordinates);
//            circles.insertOrSet(isDrawableTowers, circle);

            this.radiusDetectionCircle.setPosition(centerGraphicCoordinates);
            if (templateForTower.towerShellType == TowerShellType.FirstTarget) {
                if (templateForTower.radiusFlyShell != 0.0 && templateForTower.radiusFlyShell >= templateForTower.radiusDetection) {
                    if (radiusFlyShellCircle == null) {
                        this.radiusFlyShellCircle = new Circle(centerGraphicCoordinates, templateForTower.radiusFlyShell);
                    } else {
                        this.radiusFlyShellCircle.setPosition(centerGraphicCoordinates);
                    }
                }
            }
        }
    }

    public void moveAnimations(float delta) {
        if (burningAnimation != null) {
            burningElapsedTime += delta;
            if (burningElapsedTime >= burningAnimation.getAnimationDuration()) {
                burningElapsedTime = 0f;
            }
        }
    }

    public boolean recharge(float delta) {
        elapsedReloadTime += delta;
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            return true;
        }
        return false;
    }

    public boolean shotFireBall(CameraController cameraController) {
        if (elapsedReloadTime >= templateForTower.reloadTime) {
            if (templateForTower.towerAttackType == TowerAttackType.FireBall) {
                elapsedReloadTime = 0f;
                int radius = Math.round(templateForTower.radiusDetection);
                if ( radius == 0 ) {
                    radius = Math.round(cameraController.gameField.gameSettings.difficultyLevel);
                }
                Cell towerCell = cell;
//                Gdx.app.log("Tower::shotFireBall()", "-- radius:" + radius + " towerCell:" + towerCell + " player:" + player);
                for (int tmpX = -radius; tmpX <= radius; tmpX++) {
                    for (int tmpY = -radius; tmpY <= radius; tmpY++) {
                        Cell cell = cameraController.gameField.getCell(tmpX + towerCell.cellX, tmpY + towerCell.cellY);
                        if (cell != null && cell != towerCell) {
                            Vector2 destPoint = cell.getGraphicCoordinates(cameraController.isDrawableTowers);
                            if (destPoint != null) {
//                                Circle towerCircle = this.getCircle(cameraController.isDrawableTowers);
//                                if (towerCircle != null) {
//                                    Vector2 towerPos = new Vector2(towerCircle.x, towerCircle.y);
//                                    cameraController.getCorrectGraphicTowerCoord(towerPos, this.templateForTower.size, cameraController.isDrawableTowers);
                                    bullets.add(new Bullet(centerGraphicCoordinates, this, destPoint));
//                                }
                            }
                        }
                    }
                }
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.UP));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.UP_RIGHT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.RIGHT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.DOWN_RIGHT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.DOWN));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.DOWN_LEFT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.LEFT));
//                bullets.add(new Bullet(centerGraphicCoord, templateForTower, cameraController, Direction.UP_LEFT));
//                Gdx.app.log("Tower::shotFireBall()", "-- bullets:" + bullets + " templateForTower:" + templateForTower + " player:" + player);
                return true;
            }
        }
        return false;
    }

    public boolean shoot(Unit unit, CameraController cameraController) {
        if(elapsedReloadTime >= templateForTower.reloadTime) {
            if (templateForTower.towerShellType == TowerShellType.MassAddEffect) {
                boolean effect = false;
                for (TowerShellEffect towerShellEffect : unit.shellEffectTypes) {
                    if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
                        effect = true;
                        break;
                    }
                }
                if (!effect) {
                    unit.shellEffectTypes.add(new TowerShellEffect(templateForTower.towerShellEffect));
                }
            } else {
//                Circle towerCircle = this.getCircle(cameraController.isDrawableTowers);
//                if (towerCircle != null) {
//                    Vector2 towerPos = new Vector2(towerCircle.x, towerCircle.y);
//                    cameraController.getCorrectGraphicTowerCoord(towerPos, this.templateForTower.size, cameraController.isDrawableTowers);
                    bullets.add(new Bullet(centerGraphicCoordinates, this, unit));
//                }
            }
            elapsedReloadTime = 0f;
            return true;
        }
        return false;
    }

    public void moveAllShells(float delta, CameraController cameraController) {
        for(Bullet bullet : bullets) {
            if(radiusFlyShellCircle == null) {
                moveShell(delta, bullet, cameraController);
            } else if(radiusFlyShellCircle.overlaps(bullet.currCircle)) {
                moveShell(delta, bullet, cameraController);
            } else {
                bullets.removeValue(bullet, false);
//                bullet.dispose();
            }
        }
    }

    private void moveShell(float delta, Bullet bullet, CameraController cameraController) {
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

    public boolean isNotDestroyed() {
//        if(animation == null) { // TODO Не верно, нужно исправить.
//            return false;
//        }
        if (circles == null) {
            return false;
        }
//        System.out.println("Tower::isNotDestroyed(); -- circle.size" + circles.size);
        return hp > 0 ? true : false;
    }

    public boolean destroy(float damage) {//, TowerShellEffect towerShellEffect) {
        if (hp > 0) {
            hp -= damage;
//            addEffect(towerShellEffect);
            if (hp <= 0) {
                destroyElapsedTime = 0;
                setAnimation("explosion_");
                return true;
            } else {
                for (int k = templateForTower.burningsTemplates.size-1; k >= 0; k--) {
                    if ( hp < (templateForTower.healthPoints - templateForTower.thresholdBurning*(k+1)) ) {
                        setBurningAnimation("burning_", k);
                        break;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void setAnimation(String action) { // Action transform to Enum
//        Gdx.app.log("Tower::setAnimation()", "-- action:" + action);
        AnimatedTiledMapTile animatedTiledMapTile = templateForTower.animations.get(action);
        if (animatedTiledMapTile != null) {
            StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
            TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
            for (int k = 0; k < staticTiledMapTiles.length; k++) {
                textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
            }
            if (action.equals("explosion_")) {
                animation = new Animation(templateForTower.destroyTime / staticTiledMapTiles.length, textureRegions);
            }
            Gdx.app.log("Tower::setAnimation()", "-- animation:" + animation + " textureRegions:" + textureRegions[0]);
        } else {
            Gdx.app.log("Tower::setAnimation(" + action + ")", "-- TowerName: " + templateForTower.name + " animatedTiledMapTile: " + animatedTiledMapTile);
        }
    }

    private void setBurningAnimation(String action, int index) {
//        Gdx.app.log("Tower::setBurningAnimation()", "-- action_index:" + action+index);
        if (currBurningIndex == null || currBurningIndex != index) {
            AnimatedTiledMapTile animatedTiledMapTile = templateForTower.animations.get(action + index);
            if (animatedTiledMapTile != null) {
                StaticTiledMapTile[] staticTiledMapTiles = animatedTiledMapTile.getFrameTiles();
                TextureRegion[] textureRegions = new TextureRegion[staticTiledMapTiles.length];
                for (int k = 0; k < staticTiledMapTiles.length; k++) {
                    textureRegions[k] = staticTiledMapTiles[k].getTextureRegion();
                }
                if (action.contains("burning_")) {
                    SimpleTemplate simpleTemplate = templateForTower.burningsTemplates.get(index);
                    float animationTime = Float.parseFloat(simpleTemplate.properties.get("animationTime"));
                    burningAnimation = new Animation(animationTime / staticTiledMapTiles.length, textureRegions);
                }
                Gdx.app.log("Tower::setBurningAnimation()", "-- animation:" + animation + " textureRegions:" + textureRegions[0]);
            } else {
                Gdx.app.log("Tower::setBurningAnimation(" + action + ")", "-- TowerName: " + templateForTower.name + " animatedTiledMapTile: " + animatedTiledMapTile);
            }
            currBurningIndex = index;
        }
    }

    public boolean changeDestroyFrame(float delta) {
        if(hp <= 0) {
            if(destroyElapsedTime >= templateForTower.destroyTime) { // need change to template.destroyTime
//                dispose();
                return false;
            } else {
                destroyElapsedTime += delta;
            }
            return true;
        }
        return false;
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

        if (circles != null) {
            map = (map == 5) ? 1 : map;
            if (map > 0 && map < 5) {
                return circles.get(map - 1);
            }
        }
        Gdx.app.log("Tower::getCircle(" + map + ")", "-- Bad map | return null!");
        return null;
    }

    public TextureRegion getCurrentBurningFrame() {
        if (burningAnimation != null) {
            return (TextureRegion) burningAnimation.getKeyFrame(burningElapsedTime, true);
        }
        return null;
    }

    public TextureRegion getCurrentDestroyFrame() {
        if (animation != null) {
            return (TextureRegion) animation.getKeyFrame(destroyElapsedTime, true);
        }
        return null;
    }

    public boolean upgrade() {
        if (templateForTower.nextTemplate != null) {
            TemplateForTower nexTemplateForTower = templateForTower.faction.getTemplateForTower(templateForTower.nextTemplate);
            if (nexTemplateForTower != null) {
                Logger.logDebug("nexTemplateForTower:" + nexTemplateForTower);
                this.templateForTower = nexTemplateForTower;
            }
        }
        Logger.logFuncEnd();
        return false;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tower[");
        sb.append("cell:" + cell);
        if (full) {
            sb.append(",elapsedReloadTime:" + elapsedReloadTime);
            sb.append(",templateForTower:" + templateForTower);
            sb.append(",player:" + player);
            sb.append(",capacity:" + capacity);
            sb.append(",bullets.size:" + bullets.size);
            sb.append(",radiusDetectionCircle:" + radiusDetectionCircle);
            sb.append(",radiusFlyShellCircle:" + radiusFlyShellCircle);
        }
        sb.append("]");
        return sb.toString();
    }
}
