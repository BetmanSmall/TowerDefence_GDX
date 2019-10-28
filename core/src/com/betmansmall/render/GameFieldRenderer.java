package com.betmansmall.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.Player;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.game.gameLogic.*;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TowerAttackType;
import com.betmansmall.game.gameLogic.playerTemplates.TowerShellEffect;
import com.betmansmall.util.logging.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Renders {@link GameField} by layers of objects.
 *
 * @author Alexander on 14.10.2019.
 */
public class GameFieldRenderer {
    private final CameraController cameraController;

    private Texture greenCheckmark;
    private Texture redCross;

    public GameFieldRenderer(CameraController cameraController) {
        this.cameraController = cameraController;

        this.greenCheckmark = new Texture(Gdx.files.internal("maps/textures/green_checkmark.png"));
        this.redCross = new Texture(Gdx.files.internal("maps/textures/red_cross.png"));
        if (greenCheckmark == null || redCross == null) {
            Logger.logDebug("-- Achtung! NOT FOUND 'maps/textures/green_checkmark.png' || 'maps/textures/red_cross.png'");
        }
    }

    public void dispose() {
//        this.cameraController = null;
        this.greenCheckmark.dispose();
        this.redCross.dispose();
    }

    public void render(GameField gameField) {
        cameraController.spriteBatch.setProjectionMatrix(cameraController.camera.combined);
        cameraController.spriteBatch.begin();
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (cameraController.isDrawableFullField) {
            drawFullField(cameraController);
        }
        if(cameraController.isDrawableBackground > 0) {
            drawBackGrounds(gameField, cameraController);
        }
        if(cameraController.isDrawableGround > 0 || cameraController.isDrawableUnits > 0 || cameraController.isDrawableTowers > 0) {
            drawGroundsWithUnitsAndTowers(gameField);
        }
        if (cameraController.isDrawableForeground > 0) {
            drawForeGrounds(gameField);
        }
        cameraController.shapeRenderer.end();
        cameraController.spriteBatch.end();

        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawUnitsBars(gameField);
        drawTowersBars(gameField);
        cameraController.shapeRenderer.end();

        if (cameraController.isDrawableGrid > 0)
            drawGrid(gameField);
        if (cameraController.isDrawableGridNav > 0)
            drawGridNav(gameField);
//            drawGridNavs(cameraController);
        if (cameraController.isDrawableRoutes > 0) {
            drawRoutes(gameField);
//            drawWavesRoutes(camera);
        }

        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.spriteBatch.begin();
        drawBullets(gameField);
        drawTowersUnderConstruction(gameField);
        cameraController.spriteBatch.end();
        cameraController.shapeRenderer.end();

        cameraController.shapeRenderer.setColor(Color.RED);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        cameraController.shapeRenderer.circle(0f, 0f, 5);
        cameraController.shapeRenderer.end();
    }

    void drawFullField(CameraController cameraController) {
        if (cameraController.gameField.tmxMap.isometric) {
            TextureRegion textureRegion = cameraController.gameField.tmxMap.getTileSets().getTile(85).getTextureRegion(); // draw water2
            float sizeCellX = cameraController.sizeCellX;
            float sizeCellY = cameraController.sizeCellY;
            int sizeX = cameraController.gameField.tmxMap.width*2;//30;//width()/sizeCellX)+1;
            int sizeY = cameraController.gameField.tmxMap.height*2;//30;//(height()/sizeCellY)*2+2;
            float isometricSpaceX = 0;
            float isometricSpaceY = +(cameraController.sizeCellY / 2) * (sizeY/2);
            for (int y = -sizeY/2; y <= sizeY; y++) {
                for (int x = -sizeX/2; x <= sizeX; x++) {
                    cameraController.spriteBatch.draw(textureRegion, isometricSpaceX + sizeCellX / 2 - x * sizeCellX, isometricSpaceY - sizeCellY, sizeCellX, sizeCellY*2);
                }
                isometricSpaceY -= sizeCellY / 2;
                isometricSpaceX = isometricSpaceX != 0 ? 0 : sizeCellX / 2;
            }
        }
    }

    private void drawGrid(GameField gameField) {
        TmxMap map = gameField.tmxMap;
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.shapeRenderer.setColor(Color.BROWN);
        if (!map.isometric) {
            float sizeCellX = cameraController.sizeCellX;
//            float sizeCellY = cameraController.sizeCellY;
            if (cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), -(sizeCellX*map.height));
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, -(y*sizeCellX), -(sizeCellX*map.width), -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, -(sizeCellX*map.height));
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, -(y*sizeCellX), sizeCellX*map.width, -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, sizeCellX*map.height);
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, y*sizeCellX, sizeCellX*map.width, y*sizeCellX);
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    cameraController.shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), sizeCellX*map.height);
                for (int y = 0; y < map.height+1; y++)
                    cameraController.shapeRenderer.line(0, y*sizeCellX, -(sizeCellX*map.width), y*sizeCellX);
            }
        } else {
            float halfSizeCellX = cameraController.halfSizeCellX;
            float halfSizeCellY = cameraController.halfSizeCellY;
            float widthForTop = map.height * halfSizeCellX; // A - B
            float heightForTop = map.height * halfSizeCellY; // B - Top
            float widthForBottom = map.width * halfSizeCellX; // A - C
            float heightForBottom = map.width * halfSizeCellY; // C - Bottom
            if (cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.width; x++)
                    cameraController.shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),-(widthForTop)+(halfSizeCellX*x),-(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= map.height; y++)
                    cameraController.shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),(widthForBottom)-(halfSizeCellX*y),-(heightForBottom)-(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.width; x++)
                    cameraController.shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),(widthForTop)+(halfSizeCellX*x),(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= map.height; y++)
                    cameraController.shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),(widthForBottom)+(halfSizeCellX*y),-(heightForBottom)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                    cameraController.shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),(widthForBottom)-(halfSizeCellX*x),(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                    cameraController.shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),-(widthForTop)+(halfSizeCellX*y),(heightForTop)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                    cameraController.shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),-(widthForBottom)-(halfSizeCellX*x),-(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                    cameraController.shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),-(widthForTop)-(halfSizeCellX*y),(heightForTop)-(halfSizeCellY*y));
            }
        }
        cameraController.shapeRenderer.end();
    }

    private void drawBackGrounds(GameField gameField, CameraController cameraController) {
        TmxMap map = gameField.tmxMap;
        switch (cameraController.drawOrder) {
            case 0:
                for (int y = 0; y < map.height; y++) {
                    for (int x = 0; x < map.width; x++) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 1:
                for (int x = 0; x < map.width; x++) {
                    for (int y = 0; y < map.height; y++) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 2:
                for (int y = map.height - 1; y >= 0; y--) {
                    for (int x = map.width - 1; x >= 0; x--) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 3:
                for (int x = map.width - 1; x >= 0; x--) {
                    for (int y = map.height - 1; y >= 0; y--) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 4:
                for (int y = map.height - 1; y >= 0; y--) {
                    for (int x = 0; x < map.width; x++) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 5:
                for (int x = 0; x < map.width; x++) {
                    for (int y = map.height - 1; y >= 0; y--) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 6:
                for (int y = 0; y < map.height; y++) {
                    for (int x = map.width - 1; x >= 0; x--) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 7:
                for (int x = map.width - 1; x >= 0; x--) {
                    for (int y = 0; y < map.height; y++) {
                        drawBackGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                break;
            case 8:
                int x = 0, y = 0;
                int length = Math.max(map.width, map.height);
                while (x < length) {
                    if (x < map.width && y < map.height) {
                        if (x == length - 1 && y == length - 1) {
                            drawBackGroundCell(gameField.getCellNoCheck(x, y));
                        } else {
                            drawBackGroundCell(gameField.getCellNoCheck(x, y));
                        }
                    }
                    if (x == length - 1) {
                        x = y + 1;
                        y = length - 1;
                    } else if (y == 0) {
                        y = x + 1;
                        x = 0;
                    } else {
                        x++;
                        y--;
                    }
                }
                break;
        }
    }

    private void drawBackGroundCell(Cell cell) {
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        if (!cameraController.gameField.tmxMap.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        Array<TiledMapTile> tiledMapTiles = cell.backgroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            Vector2 cellCoord = new Vector2();
            if (cameraController.isDrawableBackground == 5) {
                for (int m = 1; m < cameraController.isDrawableBackground; m++) {
                    cellCoord.set(cell.getGraphicCoordinates(m));
                    cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableBackground != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableBackground));
                cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
    }

    private void drawGroundsWithUnitsAndTowers(GameField gameField) {
        TmxMap map = gameField.tmxMap;
        if (cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 2) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 3) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 4) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 7) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = Math.max(map.width, map.height);
            while (x < length) {
                if (x < map.width && y < map.height) {
                    if (x == length - 1 && y == length - 1) {
                        drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                    } else {
                        drawGroundCellWithUnitsAndTower(gameField.getCellNoCheck(x, y));
                    }
                }
                if (x == length - 1) {
                    x = y + 1;
                    y = length - 1;
                } else if (y == 0) {
                    y = x + 1;
                    x = 0;
                } else {
                    x++;
                    y--;
                }
            }
        }
    }

    private void drawGroundCellWithUnitsAndTower(Cell cell) {
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        if (!cameraController.gameField.tmxMap.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        Array<TiledMapTile> tiledMapTiles = cell.groundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            Vector2 cellCoord = new Vector2();
            if (cameraController.isDrawableGround == 5) {
                for (int m = 1; m < cameraController.isDrawableGround; m++) {
                    cellCoord.set(cell.getGraphicCoordinates(m));
                    cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableGround != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableGround));
                cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
        Array<Unit> units = cell.getUnits();
        if(units != null) {
            Color oldColorSB = cameraController.spriteBatch.getColor();
            for (Unit unit : units) {
                drawUnit(unit);
            }
            cameraController.spriteBatch.setColor(oldColorSB);
        }
        Tower tower = cell.getTower();
        if(tower != null) {
            drawTower(cameraController, tower);
        }
    }

    private void drawUnit(Unit unit) { //TODO Need to refactor this
//        Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- Start!");
//        for (TowerShellEffect shellAttackType : unit.shellEffectTypes) {
//            if(shellAttackType.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
//                cameraController.spriteBatch.setColor(0.0f, 0.0f, 1.0f, 0.9f);
//                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FreezeEffect!");
//            }
//            if(shellAttackType.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FireEffect) {
//                cameraController.spriteBatch.setColor(1.0f, 0.0f, 0.0f, 0.9f);
//                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FireEffect!");
//            }
//        }
        TextureRegion currentFrame = null;
        if (unit.isAlive()) {
            currentFrame = unit.getCurrentAttackFrame();
            if (currentFrame == null) {
                currentFrame = unit.getCurrentFrame();
            }
        } else {
            currentFrame = unit.getCurrentDeathFrame();
        }

        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.sizeCellY;
        if (!cameraController.gameField.tmxMap.isometric) {
            sizeCellY = cameraController.sizeCellY;
            deltaY = cameraController.halfSizeCellY;
        }
        float fVx = 0f, fVy = 0f;
        if (cameraController.isDrawableUnits == 5) {
            for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                Circle circle = unit.getCircle(m);
                fVx = circle.x - deltaX;
                fVy = circle.y - deltaY;
                cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
            }
        } else if (cameraController.isDrawableUnits != 0) {
            Circle circle = unit.getCircle(cameraController.isDrawableUnits);
            fVx = circle.x - deltaX;
            fVy = circle.y - deltaY;
            cameraController.spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
        }
//        drawUnitBar(shapeRenderer, unit, currentFrame, fVx, fVy);
    }

    private void drawUnitsBars(GameField gameField) {
        for (Unit unit : gameField.unitsManager.units) {
            if (unit.isAlive()) {
                if (cameraController.isDrawableUnits == 5) {
                    for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                        Circle circle = unit.getCircle(m);
                        drawUnitBar(cameraController, unit, circle.x, circle.y);
                    }
                } else if (cameraController.isDrawableUnits != 0) {
                    Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                    drawUnitBar(cameraController, unit, circle.x, circle.y);
                }
            }
        }
    }

    private void drawUnitBar(CameraController cameraController, Unit unit, float fVx, float fVy) {
        float maxHP = unit.templateForUnit.healthPoints;
        float hp = unit.hp;
        if (maxHP != hp) {
            TextureRegion currentFrame = unit.getCurrentFrame();
            fVx -= cameraController.sizeCellX/2;
            fVy -= cameraController.sizeCellY;
            float currentFrameWidth = currentFrame.getRegionWidth();
            float currentFrameHeight = currentFrame.getRegionHeight();
            float hpBarSpace = 0.8f;
            float hpBarHPWidth = 30f;
            float hpBarHeight = 7f;
            float hpBarWidthIndent = (currentFrameWidth - hpBarHPWidth) / 2;
            float hpBarTopIndent = hpBarHeight;

            cameraController.shapeRenderer.setColor(Color.BLACK);
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            cameraController.shapeRenderer.setColor(Color.GREEN);

            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));

            float allTime = 0f;
            for (TowerShellEffect towerShellEffect : unit.shellEffectTypes) {
                allTime += towerShellEffect.time;
            }

            if (allTime != 0.0) {
                float effectBarWidthSpace = hpBarSpace * 2;
                float effectBarHeightSpace = hpBarSpace * 2;
                float effectBarWidth = hpBarHPWidth - effectBarWidthSpace * 2;
                float effectBarHeight = hpBarHeight - (effectBarHeightSpace * 2);
                float effectWidth = effectBarWidth / allTime;
                float efX = fVx + hpBarWidthIndent + effectBarWidthSpace;
                float efY = fVy + currentFrameHeight - hpBarTopIndent + effectBarHeightSpace;
                float effectBlockWidth = effectBarWidth / unit.shellEffectTypes.size;
                for (int effectIndex = 0; effectIndex < unit.shellEffectTypes.size; effectIndex++) {
                    TowerShellEffect towerShellEffect = unit.shellEffectTypes.get(effectIndex);
                    if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FireEffect) {
                        cameraController.shapeRenderer.setColor(Color.RED);
                    } else if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
                        cameraController.shapeRenderer.setColor(Color.ROYAL);
                    }
                    float efWidth = effectBlockWidth - effectWidth * towerShellEffect.elapsedTime;
                    cameraController.shapeRenderer.rect(efX, efY, efWidth, effectBarHeight);
                    efX += effectBlockWidth;
//                    Gdx.app.log("GameField::drawUnit()", "-- efX:" + efX + " efWidth:" + efWidth + ":" + effectIndex);
                }
            }
        }
    }

    private void drawTower(CameraController cameraController, Tower tower) {
        Cell cell = tower.cell;
        int towerSize = tower.templateForTower.size;
//        Vector2 towerPos = new Vector2(cell.getGraphicCoordinates(cameraController.isDrawableTowers));
//        cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, 3);
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;

        TextureRegion burningFrame = null;
        TextureRegion currentFrame = null;
        if (tower.isNotDestroyed()) {
            currentFrame = tower.templateForTower.idleTile.getTextureRegion();
            if (tower.burningAnimation != null) {
                burningFrame = tower.getCurrentBurningFrame();
            }
        } else {
            sizeCellY = cameraController.sizeCellY*1.5f;
            currentFrame = tower.getCurrentDestroyFrame();
        }
        if (!cameraController.gameField.tmxMap.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        Vector2 towerPos = new Vector2();
        if (cameraController.isDrawableTowers == 5) {
            for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                towerPos.set(cell.getGraphicCoordinates(m));
                cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, m);
                cameraController.spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
                if (burningFrame != null) {
                    cameraController.spriteBatch.draw(burningFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
                }
//                cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius/2);
//                cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
            }
        } else if (cameraController.isDrawableTowers != 0) {
            towerPos.set(cell.getGraphicCoordinates(cameraController.isDrawableTowers));
            cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, cameraController.isDrawableTowers);
            cameraController.spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
            if (burningFrame != null) {
                cameraController.spriteBatch.draw(burningFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
            }
//            cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius/2);
//            cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
        }
        // todo fix this shit 1 || this fix bug with transparent when select tower
        cameraController.spriteBatch.end();
        cameraController.shapeRenderer.end();
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        Color oldColor = cameraController.shapeRenderer.getColor();
        cameraController.shapeRenderer.setColor(Color.WHITE);
        Player player = cameraController.gameField.gameScreen.playersManager.localPlayer;
        if(player.playerID == tower.player.playerID && player.selectedTower == tower) {
            cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
        }
        cameraController.shapeRenderer.setColor(oldColor);

        cameraController.shapeRenderer.end();
        cameraController.spriteBatch.begin();
    }

    private void drawForeGrounds(GameField gameField) {
        TmxMap map = gameField.tmxMap;
        if (cameraController.drawOrder == 0) {
            for (int y = 0; y < map.height; y++) {
                for (int x = 0; x < map.width; x++) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 1) {
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 2) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 3) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 4) {
            for (int y = map.height - 1; y >= 0; y--) {
                for (int x = 0; x < map.width; x++) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 5) {
            for (int x = 0; x < map.width; x++) {
                for (int y = map.height - 1; y >= 0; y--) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 6) {
            for (int y = 0; y < map.height; y++) {
                for (int x = map.width - 1; x >= 0; x--) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 7) {
            for (int x = map.width - 1; x >= 0; x--) {
                for (int y = 0; y < map.height; y++) {
                    drawForeGroundCell(gameField.getCellNoCheck(x, y));
                }
            }
        } else if (cameraController.drawOrder == 8) {
            int x = 0, y = 0;
            int length = Math.max(map.width, map.height);
            while (x < length) {
                if (x < map.width && y < map.height) {
                    if (x == length - 1 && y == length - 1) {
                        drawForeGroundCell(gameField.getCellNoCheck(x, y));
                    } else {
                        drawForeGroundCell(gameField.getCellNoCheck(x, y));
                    }
                }
                if (x == length - 1) {
                    x = y + 1;
                    y = length - 1;
                } else if (y == 0) {
                    y = x + 1;
                    x = 0;
                } else {
                    x++;
                    y--;
                }
            }
        }
    }

    private void drawForeGroundCell(Cell cell) {
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        float deltaX = cameraController.halfSizeCellX;
        float deltaY = cameraController.halfSizeCellY;
        if (!cameraController.gameField.tmxMap.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        Array<TiledMapTile> tiledMapTiles = cell.foregroundTiles;
        for (TiledMapTile tiledMapTile : tiledMapTiles) {
            TextureRegion textureRegion = tiledMapTile.getTextureRegion();
            Vector2 cellCoord = new Vector2();
            if (cameraController.isDrawableForeground == 5) {
                for (int m = 1; m < cameraController.isDrawableForeground; m++) {
                    cellCoord.set(cell.getGraphicCoordinates(m));
                    cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableForeground != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableForeground));
                cameraController.spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
    }

    private void drawTowersBars(GameField gameField) {
        for (Tower tower : gameField.towersManager.towers) {
            if (tower.isNotDestroyed()) {
                if (cameraController.isDrawableTowers == 5) {
                    for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                        Circle circle = tower.getCircle(m);
                        drawTowerBar(cameraController, tower, circle.x, circle.y);
                    }
                } else if (cameraController.isDrawableTowers != 0) {
                    Circle circle = tower.getCircle(cameraController.isDrawableTowers);
                    drawTowerBar(cameraController, tower, circle.x, circle.y);
                }
            }
        }
    }

    private void drawTowerBar(CameraController cameraController, Tower tower, float fVx, float fVy) {
        float maxHP = tower.templateForTower.healthPoints;
        float hp = tower.hp;
        if (maxHP != hp) {
            TextureRegion currentFrame = tower.templateForTower.idleTile.getTextureRegion();
            fVx -= cameraController.sizeCellX/2;
            fVy -= cameraController.sizeCellY;
            float currentFrameWidth = currentFrame.getRegionWidth();
            float currentFrameHeight = currentFrame.getRegionHeight();
            float hpBarSpace = 0.8f;
            float hpBarHPWidth = 30f;
            float hpBarHeight = 7f;
            float hpBarWidthIndent = (currentFrameWidth - hpBarHPWidth) / 2;
            float hpBarTopIndent = hpBarHeight;

            cameraController.shapeRenderer.setColor(Color.BLACK);
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            cameraController.shapeRenderer.setColor(Color.GREEN);

            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            cameraController.shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));
        }
    }

    private void drawBullets(GameField gameField) {
        for (Tower tower : gameField.towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
                if (textureRegion != null) {
                    cameraController.spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.currCircle.radius, bullet.currentPoint.y - bullet.currCircle.radius, bullet.currCircle.radius * 2, bullet.currCircle.radius * 2);
                }
            }
        }
        for (Unit unit : gameField.unitsManager.units) {
            for (UnitBullet bullet : unit.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
                if (textureRegion != null) {
                    cameraController.spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.currCircle.radius, bullet.currentPoint.y - bullet.currCircle.radius, bullet.currCircle.radius * 2, bullet.currCircle.radius * 2);
                }
            }
        }
    }

    private void drawGridNav(GameField gameField) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        TmxMap map = gameField.tmxMap;
        Vector2 pos = new Vector2();
        float gridNavRadius = cameraController.sizeCellX/20f;
        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                Cell cell = gameField.getCellNoCheck(x, y);
                if (cell != null && !cell.isEmpty()) {
                    if (cell.isTerrain()) {
                        cameraController.shapeRenderer.setColor(Color.RED);
                        if (cell.removableTerrain) {
                            cameraController.shapeRenderer.getColor().set(255, 0, 0, 100);
                        }
                    } else if (cell.getUnit() != null) {
                        cameraController.shapeRenderer.setColor(Color.GREEN);
                    } else if (cell.getTower() != null) {
                        cameraController.shapeRenderer.setColor(Color.YELLOW);
                    }

                    if (cameraController.isDrawableGridNav == 5) {
                        for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                            pos.set(cell.getGraphicCoordinates(m));
                            cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                        }
                    } else if (cameraController.isDrawableGridNav != 0) {
                        pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                }
            }
        }

        Array<GridPoint2> spawnPoints = gameField.waveManager.getAllSpawnPoint();
        cameraController.shapeRenderer.setColor(new Color(0f, 255f, 204f, 255f));
        if (!gameField.turnedMap) {
            for (GridPoint2 spawnPoint : spawnPoints) {
                Cell cell = gameField.getCell(spawnPoint.x, spawnPoint.y); // ArrayIndexOutOfBoundsException x==31 because turn x==16 && y==31 // turnedMap kostbIl
                if (cell != null) { // TODO need in turnAndFlip() convert. and this point and other
                    if (cameraController.isDrawableGridNav == 5) {
                        for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                            pos.set(cell.getGraphicCoordinates(m));
                            cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                        }
                    } else if (cameraController.isDrawableGridNav != 0) {
                        pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                } else {
                    Gdx.app.log("GameField::drawGridNav()", "-- cell:" + cell + " spawnPoint.x:" + spawnPoint.x + " spawnPoint.y:" + spawnPoint.y);
                }
            }
        }

        Array<GridPoint2> exitPoints = gameField.waveManager.getAllExitPoint();
        cameraController.shapeRenderer.setColor(new Color(255f, 0f, 102f, 255f));
        if (!gameField.turnedMap) {
            for (GridPoint2 exitPoint : exitPoints) {
                Cell cell = gameField.getCell(exitPoint.x, exitPoint.y);
                if (cameraController.isDrawableGridNav == 5) {
                    for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                        pos.set(cell.getGraphicCoordinates(m));
                        cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                } else if (cameraController.isDrawableGridNav != 0) {
                    pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                    cameraController.shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : gameField.towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                cameraController.shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, cameraController.sizeCellX/40f);
                if (null != bullet.currCircle) {
                    if (bullet.animation == null) {
                        cameraController.shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }
        for (Unit unit : gameField.unitsManager.units) {
            for (UnitBullet bullet : unit.bullets) {
                cameraController.shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, cameraController.sizeCellX/40f);
                if (null != bullet.currCircle) {
                    if (bullet.animation == null) {
                        cameraController.shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }
        cameraController.shapeRenderer.end();

        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        cameraController.shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : gameField.towersManager.towers) { // Draw Orange tower.bullets! -- bullet.currCircle
            for (Bullet bullet : tower.bullets) {
                if (null != bullet.currCircle) {
                    if (bullet.animation != null) {
                        cameraController.shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.RED);
        for (Unit unit : gameField.unitsManager.units) {
            if (cameraController.isDrawableUnits == 5) {
                for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                    Circle circle = unit.getCircle(m);
                    cameraController.shapeRenderer.circle(circle.x, circle.y, circle.radius);
                }
            } else if (cameraController.isDrawableUnits != 0) {
                Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                cameraController.shapeRenderer.circle(circle.x, circle.y, circle.radius);
            }
            if (unit.unitAttack != null && unit.unitAttack.circle != null) {
                cameraController.shapeRenderer.circle(unit.unitAttack.circle.x, unit.unitAttack.circle.y, unit.unitAttack.circle.radius);
            }
        }

        cameraController.shapeRenderer.setColor(Color.WHITE);
        for (Tower tower : gameField.towersManager.towers) { // Draw white towers radius! -- radiusDetectionCircle
            if (tower.radiusDetectionCircle != null) {
                if (cameraController.isDrawableGridNav == 5) {
                    if (cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                            cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                        }
                    } else if (cameraController.isDrawableTowers != 0) {
                        cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if (cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        cameraController.shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                    }
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.FIREBRICK);
        for (Tower tower : gameField.towersManager.towers) { // Draw FIREBRICK towers radius! -- radiusFlyShellCircle
            if (tower.radiusFlyShellCircle != null) {
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            cameraController.shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        cameraController.shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        cameraController.shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                    }
                }
            }
        }

        cameraController.shapeRenderer.setColor(Color.YELLOW);
        for (Tower tower : gameField.towersManager.towers) { // Draw YELLOW towers overlaps circle!
            if (tower.circles.size != 0) {
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            Circle towerCircle = tower.getCircle(m);
                            cameraController.shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        Circle towerCircle = tower.getCircle(cameraController.isDrawableTowers);
                        cameraController.shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        Circle towerCircle = tower.getCircle(cameraController.isDrawableGridNav);
                        cameraController.shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                    }
                }
            }
        }
        cameraController.shapeRenderer.end();

        cameraController.spriteBatch.begin();
        for (Tower tower : gameField.towersManager.towers) { // Draw pit capacity value || players ID
            if (tower.templateForTower.towerAttackType == TowerAttackType.Pit) {
                cameraController.bitmapFont.setColor(Color.YELLOW);
                cameraController.bitmapFont.getData().setScale(0.7f);
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                    }
                }
            }
            cameraController.bitmapFont.getData().setScale(0.9f);
            if (tower.player == gameField.gameScreen.playersManager.localServer) {
                cameraController.bitmapFont.setColor(Color.RED);
            } else if (tower.player == gameField.gameScreen.playersManager.localPlayer) {
                cameraController.bitmapFont.setColor(Color.BLUE);
            }
            if(cameraController.isDrawableGridNav == 5) {
                if(cameraController.isDrawableTowers == 5) {
                    for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                        cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.player.playerID), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                    }
                } else if(cameraController.isDrawableTowers != 0) {
                    cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.player.playerID), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                }
            } else if(cameraController.isDrawableGridNav != 0) {
                if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                    cameraController.bitmapFont.draw(cameraController.spriteBatch, String.valueOf(tower.player.playerID), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                }
            }
        }
        cameraController.spriteBatch.end();
    }

    private void drawRoutes(GameField gameField) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float gridNavRadius = cameraController.sizeCellX/22f;
        for (Unit unit : gameField.unitsManager.units) {
            if (unit.player == gameField.gameScreen.playersManager.localPlayer) {
                cameraController.shapeRenderer.setColor(Color.WHITE);
            } else {
                cameraController.shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);
            }
            ArrayDeque<Cell> unitRoute = unit.route;
            if (unitRoute != null && !unitRoute.isEmpty()) {
                for (Cell cell : unitRoute) {
                    if (cell != null) {
                        Vector2 cellCoord = new Vector2();
                        if (cameraController.isDrawableRoutes == 5) {
                            for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                                cellCoord.set(cell.getGraphicCoordinates(m));
                                cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius);
                            }
                        } else if (cameraController.isDrawableRoutes != 0) {
                            cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                            cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius);
                        }
                    }
                }
                cameraController.shapeRenderer.setColor(0.756f, 0.329f, 0.756f, 1f);
                Cell cell = unitRoute.getLast();
//                Cell cell = getCell(destinationPoint.getX(), destinationPoint.getY());
                if (cell != null) {
                    Vector2 cellCoord = new Vector2();
                    if (cameraController.isDrawableRoutes == 5) {
                        for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                            cellCoord.set(cell.getGraphicCoordinates(m));
                            cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius * 0.7f);
                        }
                    } else if (cameraController.isDrawableRoutes != 0) {
                        cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                        cameraController.shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius * 0.7f);
                    }
                }
            }
        }
        cameraController.shapeRenderer.end();
    }

    private void drawWavesRoutes(GameField gameField) {
        cameraController.shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        cameraController.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        cameraController.shapeRenderer.setColor(Color.BROWN);
        for (Wave wave : gameField.waveManager.waves) {
            drawWave(gameField, wave);
        }
        cameraController.shapeRenderer.setColor(Color.BLUE);
        for (Wave wave : gameField.waveManager.wavesForUser) {
            drawWave(gameField, wave);
        }
        cameraController.shapeRenderer.end();
    }

    private void drawWave(GameField gameField, Wave wave) {
//        Gdx.app.log("GameField::drawWave(" + wave + ")", "--");
        float linesWidth = cameraController.sizeCellX/15f;
        ArrayDeque<Node> route = wave.route;
        if (route != null && !route.isEmpty()) {
            Iterator<Node> nodeIterator = route.iterator();
            Node startNode = nodeIterator.next();
            Node endNode = null;
            while (nodeIterator.hasNext()) {
                endNode = nodeIterator.next();
                Cell startCell = gameField.getCell(startNode.getX(), startNode.getY());
                Cell endCell = gameField.getCell(endNode.getX(), endNode.getY());
                Vector2 startCellCoord = new Vector2();
                Vector2 endCellCoord = new Vector2();
                if (cameraController.isDrawableRoutes == 5) {
                    for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                        startCellCoord.set(startCell.getGraphicCoordinates(m));
                        endCellCoord.set(endCell.getGraphicCoordinates(m));
                        cameraController.shapeRenderer.rectLine(startCellCoord, endCellCoord, linesWidth);
                    }
                } else if (cameraController.isDrawableRoutes != 0) {
                    startCellCoord.set(startCell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                    endCellCoord.set(endCell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                    cameraController.shapeRenderer.rectLine(startCellCoord, endCellCoord, linesWidth);
                }
                startNode = endNode;
            }
        }
    }

    private void drawTowersUnderConstruction(GameField gameField) {
        UnderConstruction underConstruction = gameField.getUnderConstruction();
        if (underConstruction != null) {
            int goldNeed = underConstruction.templateForTower.cost;
            boolean enoughGold = (gameField.gameScreen.playersManager.localPlayer.gold >= goldNeed) ? true : false;
            if (underConstruction.state == 0) {
                drawTowerUnderConstruction(gameField, underConstruction.endX, underConstruction.endY, underConstruction.templateForTower, enoughGold);
            } else if (underConstruction.state == 1) {
                drawTowerUnderConstruction(gameField, underConstruction.startX, underConstruction.startY, underConstruction.templateForTower, enoughGold);
                for (int k = 0; k < underConstruction.coorsX.size; k++) {
                    goldNeed += underConstruction.templateForTower.cost;
                    enoughGold = (gameField.gameScreen.playersManager.localPlayer.gold >= goldNeed) ? true : false;
                    drawTowerUnderConstruction(gameField, underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower, enoughGold);
                }
            }
        }
    }

    private void drawTowerUnderConstruction(GameField gameField, int buildX, int buildY, TemplateForTower templateForTower, boolean enoughGold) {
//        Gdx.app.log("GameField::drawTowerUnderConstruction()", "-- buildX:" + buildX + " buildY:" + buildY /*+ " templateForTower:" + templateForTower*/ + " enoughGold:" + enoughGold);
        boolean drawFull = true;
        boolean canBuild = true;
        int towerSize = templateForTower.size;
        int startX = 0, startY = 0, finishX = 0, finishY = 0;
        if (towerSize != 1) {
            // Нижняя карта
            if (towerSize % 2 == 0) {
                startX = -(towerSize / 2);
                startY = -(towerSize / 2);
                finishX = (towerSize / 2)-1;
                finishY = (towerSize / 2)-1;
            } else {
                startX = -(towerSize / 2);
                startY = -(towerSize / 2);
                finishX = (towerSize / 2);
                finishY = (towerSize / 2);
            }
        }
        GridPoint2 startDrawCell = new GridPoint2(startX, startY);
        GridPoint2 finishDrawCell = new GridPoint2(finishX, finishY);
        for (int x = startX; x <= finishX; x++) {
            for (int y = startY; y <= finishY; y++) {
                Cell cell = gameField.getCell(buildX + x, buildY + y);
                if(cell == null || !cell.isEmpty() && drawFull) {
                    canBuild = false;
                }
            }
        }
        if (drawFull) {
            Cell mainCell = gameField.getCell(buildX, buildY);
            if(mainCell != null) {
//                Color oldColorSB = cameraController.spriteBatch.getColor();
//                Color oldColorSR = cameraController.shapeRenderer.getColor();
                if (enoughGold && canBuild) {
                    cameraController.spriteBatch.setColor(0, 1f, 0, 0.55f);
                    cameraController.shapeRenderer.setColor(0, 1f, 0, 0.55f);
                } else {
                    cameraController.spriteBatch.setColor(1f, 0, 0, 0.55f);
                    cameraController.shapeRenderer.setColor(1f, 0, 0, 0.55f);
                }
                if (cameraController.isDrawableTowers == 5) {
                    for (int map = 1; map < cameraController.isDrawableTowers; map++) {
                        drawTowerUnderConstructionAndMarks(gameField, map, templateForTower, mainCell, startDrawCell, finishDrawCell);
                    }
                } else if (cameraController.isDrawableTowers != 0) {
                    drawTowerUnderConstructionAndMarks(gameField, cameraController.isDrawableTowers, templateForTower, mainCell, startDrawCell, finishDrawCell);
                }
                cameraController.spriteBatch.setColor(Color.WHITE);
                cameraController.shapeRenderer.setColor(Color.WHITE);
            }
        }
    }

    private void drawTowerUnderConstructionAndMarks(GameField gameField, int map, TemplateForTower templateForTower, Cell mainCell, GridPoint2 startDrawCell, GridPoint2 finishDrawCell) {
//        Gdx.app.log("GameField::drawTowerUnderConstructionAndMarks()", "-- spriteBatch:" + /*spriteBatch +*/ " shapeRenderer:" + /*shapeRenderer +*/ " map:" + map + " templateForTower:" + templateForTower + " mainCell:" + mainCell + " startDrawCell:" + startDrawCell + " finishDrawCell:" + finishDrawCell);
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        if (!cameraController.gameField.tmxMap.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();
        int towerSize = templateForTower.size;
        Vector2 towerPos = new Vector2(mainCell.getGraphicCoordinates(map));
        if (templateForTower.radiusDetection != null) {
            cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection);
        }
        cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, map);
        cameraController.spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
//        cameraController.shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection/4);
        if (greenCheckmark != null && redCross != null) {
            Vector2 markPos = new Vector2();
            for (int x = startDrawCell.x; x <= finishDrawCell.x; x++) {
                for (int y = startDrawCell.y; y <= finishDrawCell.y; y++) {
                    Cell markCell = gameField.getCell(mainCell.cellX + x, mainCell.cellY + y);
                    if (markCell != null) {
                        markPos.set(markCell.getGraphicCoordinates(map));
                        markPos.add(-(cameraController.halfSizeCellX), -(cameraController.halfSizeCellY));
                        if(markCell.isEmpty()) {
                            cameraController.spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY);
                        } else {
                            cameraController.spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY);
                        }
                    }
                }
            }
            markPos = null; // delete markPos;
        }
        towerPos = null; // delete towerPos;
    }
}
