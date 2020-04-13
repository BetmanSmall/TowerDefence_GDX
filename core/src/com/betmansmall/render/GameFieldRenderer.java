package com.betmansmall.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.betmansmall.utils.logging.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Renders {@link GameField} by layers of objects.
 *
 * @author Alexander on 14.10.2019.
 */
public class GameFieldRenderer {
    private final GameField gameField;
    private final CameraController cameraController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;

    private Texture greenCheckmark;
    private Texture redCross;

    public GameFieldRenderer(final GameField gameField, final CameraController cameraController) {
        this.gameField = gameField;
        this.cameraController = cameraController;

        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.bitmapFont = new BitmapFont();

        this.greenCheckmark = new Texture(Gdx.files.internal("maps/textures/green_checkmark.png"));
        this.redCross = new Texture(Gdx.files.internal("maps/textures/red_cross.png"));
        if (greenCheckmark == null || redCross == null) {
            Logger.logError("-- Achtung! NOT FOUND 'maps/textures/green_checkmark.png' || 'maps/textures/red_cross.png'");
        }
    }

    public void dispose() {
//        this.gameField = null;
//        this.cameraController = null;

        this.shapeRenderer.dispose();
        this.spriteBatch.dispose();
        this.bitmapFont.dispose();

        this.greenCheckmark.dispose();
        this.redCross.dispose();
    }

    public void render() {
        spriteBatch.setProjectionMatrix(cameraController.camera.combined);
        spriteBatch.begin();
        shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (cameraController.isDrawableFullField) {
            drawFullField();
        }
        if(cameraController.isDrawableBackground > 0) {
            drawBackGrounds();
        }
        if(cameraController.isDrawableGround > 0 || cameraController.isDrawableUnits > 0 || cameraController.isDrawableTowers > 0) {
            drawGroundsWithUnitsAndTowers();
        }
        if (cameraController.isDrawableForeground > 0) {
            drawForeGrounds();
        }
        shapeRenderer.end();
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawUnitsBars();
        drawTowersBars();
        shapeRenderer.end();

        if (cameraController.isDrawableGrid > 0)
            drawGrid();
        if (cameraController.isDrawableGridNav > 0)
            drawGridNav();
        if (cameraController.isDrawableRoutes > 0) {
            drawRoutes();
//            drawWavesRoutes();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        spriteBatch.begin();
        drawBullets();
        drawTowersUnderConstruction();
        spriteBatch.end();
        shapeRenderer.end();

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(0f, 0f, 5);
        shapeRenderer.end();
    }

    void drawFullField() {
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
                    spriteBatch.draw(textureRegion, isometricSpaceX + sizeCellX / 2 - x * sizeCellX, isometricSpaceY - sizeCellY, sizeCellX, sizeCellY*2);
                }
                isometricSpaceY -= sizeCellY / 2;
                isometricSpaceX = isometricSpaceX != 0 ? 0 : sizeCellX / 2;
            }
        }
    }

    private void drawGrid() {
        TmxMap map = gameField.tmxMap;
        shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BROWN);
        if (!map.isometric) {
            float sizeCellX = cameraController.sizeCellX;
//            float sizeCellY = cameraController.sizeCellY;
            if (cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), -(sizeCellX*map.height));
                for (int y = 0; y < map.height+1; y++)
                    shapeRenderer.line(0, -(y*sizeCellX), -(sizeCellX*map.width), -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, -(sizeCellX*map.height));
                for (int y = 0; y < map.height+1; y++)
                    shapeRenderer.line(0, -(y*sizeCellX), sizeCellX*map.width, -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, sizeCellX*map.height);
                for (int y = 0; y < map.height+1; y++)
                    shapeRenderer.line(0, y*sizeCellX, sizeCellX*map.width, y*sizeCellX);
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < map.width+1; x++)
                    shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), sizeCellX*map.height);
                for (int y = 0; y < map.height+1; y++)
                    shapeRenderer.line(0, y*sizeCellX, -(sizeCellX*map.width), y*sizeCellX);
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
                    shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),-(widthForTop)+(halfSizeCellX*x),-(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= map.height; y++)
                    shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),(widthForBottom)-(halfSizeCellX*y),-(heightForBottom)-(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.width; x++)
                    shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),(widthForTop)+(halfSizeCellX*x),(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= map.height; y++)
                    shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),(widthForBottom)+(halfSizeCellX*y),-(heightForBottom)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                    shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),(widthForBottom)-(halfSizeCellX*x),(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                    shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),-(widthForTop)+(halfSizeCellX*y),(heightForTop)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= map.height; x++) // WHT??? map.height check groundDraw
                    shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),-(widthForBottom)-(halfSizeCellX*x),-(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= map.width; y++) // WHT??? map.width check groundDraw
                    shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),-(widthForTop)-(halfSizeCellX*y),(heightForTop)-(halfSizeCellY*y));
            }
        }
        shapeRenderer.end();
    }

    private void drawBackGrounds() {
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
                    spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableBackground != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableBackground));
                spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
    }

    private void drawGroundsWithUnitsAndTowers() {
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
                    spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableGround != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableGround));
                spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
        Array<Unit> units = cell.getUnits();
        if(units != null) {
            Color oldColorSB = spriteBatch.getColor();
            for (Unit unit : units) {
                drawUnit(unit);
            }
            spriteBatch.setColor(oldColorSB);
        }
        Tower tower = cell.getTower();
        if(tower != null) {
            drawTower(tower);
        }
    }

    private void drawUnit(Unit unit) { //TODO Need to refactor this
//        Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- Start!");
//        for (TowerShellEffect shellAttackType : unit.shellEffectTypes) {
//            if(shellAttackType.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
//                spriteBatch.setColor(0.0f, 0.0f, 1.0f, 0.9f);
//                // Gdx.app.log("GameField::drawUnit(" + unit + "," + spriteBatch + ")", "-- FreezeEffect!");
//            }
//            if(shellAttackType.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FireEffect) {
//                spriteBatch.setColor(1.0f, 0.0f, 0.0f, 0.9f);
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
                if (circle != null) {
                    fVx = circle.x - deltaX;
                    fVy = circle.y - deltaY;
                    spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
                }
            }
        } else if (cameraController.isDrawableUnits != 0) {
            Circle circle = unit.getCircle(cameraController.isDrawableUnits);
            if (circle != null) {
                fVx = circle.x - deltaX;
                fVy = circle.y - deltaY;
                spriteBatch.draw(currentFrame, fVx, fVy, sizeCellX, sizeCellY);
            }
        }
//        drawUnitBar(shapeRenderer, unit, currentFrame, fVx, fVy);
    }

    private void drawUnitsBars() {
        for (Unit unit : gameField.unitsManager.units) {
            if (unit.isAlive()) {
                if (cameraController.isDrawableUnits == 5) {
                    for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                        Circle circle = unit.getCircle(m);
                        drawUnitBar(unit, circle.x, circle.y);
                    }
                } else if (cameraController.isDrawableUnits != 0) {
                    Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                    drawUnitBar(unit, circle.x, circle.y);
                }
            }
        }
    }

    private void drawUnitBar(Unit unit, float fVx, float fVy) {
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

            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            shapeRenderer.setColor(Color.GREEN);

            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));

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
                        shapeRenderer.setColor(Color.RED);
                    } else if (towerShellEffect.shellEffectEnum == TowerShellEffect.ShellEffectEnum.FreezeEffect) {
                        shapeRenderer.setColor(Color.ROYAL);
                    }
                    float efWidth = effectBlockWidth - effectWidth * towerShellEffect.elapsedTime;
                    shapeRenderer.rect(efX, efY, efWidth, effectBarHeight);
                    efX += effectBlockWidth;
//                    Gdx.app.log("GameField::drawUnit()", "-- efX:" + efX + " efWidth:" + efWidth + ":" + effectIndex);
                }
            }
        }
    }

    private void drawTower(Tower tower) {
        Cell cell = tower.cell;
        int towerSize = tower.templateForTower.size;
//        Vector2 towerPos = new Vector2(cell.getGraphicCoordinates(isDrawableTowers));
//        shapeRenderer.circle(towerPos.x, towerPos.y, 3);
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
                spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
                if (burningFrame != null) {
                    spriteBatch.draw(burningFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
                }
//                shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius/2);
//                shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
            }
        } else if (cameraController.isDrawableTowers != 0) {
            towerPos.set(cell.getGraphicCoordinates(cameraController.isDrawableTowers));
            cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, cameraController.isDrawableTowers);
            spriteBatch.draw(currentFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
            if (burningFrame != null) {
                spriteBatch.draw(burningFrame, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
            }
//            shapeRenderer.circle(towerPos.x, towerPos.y, tower.radiusDetectionCircle.radius/2);
//            shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
        }
        // todo fix this shit 1 || this fix bug with transparent when select tower
        spriteBatch.end();
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Color oldColor = shapeRenderer.getColor();
        shapeRenderer.setColor(Color.WHITE);

        Player player = cameraController.gameField.gameScreen.playersManager.getLocalPlayer();
        if (player.playerID == tower.player.playerID && player.selectedTower == tower) {
            shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
        }

        shapeRenderer.setColor(oldColor);
        shapeRenderer.end();

        spriteBatch.begin();
        // todo fix this shit 2
//        towerPos = null; // delete towerPos;
    }

    private void drawForeGrounds() {
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
                    spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
                }
            } else if (cameraController.isDrawableForeground != 0) {
                cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableForeground));
                spriteBatch.draw(textureRegion, cellCoord.x-deltaX, cellCoord.y-deltaY, sizeCellX, sizeCellY);
            }
        }
    }

    private void drawTowersBars() {
        for (Tower tower : gameField.towersManager.towers) {
            if (tower.isNotDestroyed()) {
                if (cameraController.isDrawableTowers == 5) {
                    for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                        Circle circle = tower.getCircle(m);
                        drawTowerBar(tower, circle.x, circle.y);
                    }
                } else if (cameraController.isDrawableTowers != 0) {
                    Circle circle = tower.getCircle(cameraController.isDrawableTowers);
                    drawTowerBar(tower, circle.x, circle.y);
                }
            }
        }
    }

    private void drawTowerBar(Tower tower, float fVx, float fVy) {
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

            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(fVx + hpBarWidthIndent, fVy + currentFrameHeight - hpBarTopIndent, hpBarHPWidth, hpBarHeight);
            shapeRenderer.setColor(Color.GREEN);

            hpBarHPWidth = hpBarHPWidth / maxHP * hp;
            shapeRenderer.rect(fVx + hpBarWidthIndent + hpBarSpace, fVy + currentFrameHeight - hpBarTopIndent + hpBarSpace, hpBarHPWidth - (hpBarSpace * 2), hpBarHeight - (hpBarSpace * 2));
        }
    }

    private void drawBullets() {
        for (Tower tower : gameField.towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
                if (textureRegion != null) {
                    spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.currCircle.radius, bullet.currentPoint.y - bullet.currCircle.radius, bullet.currCircle.radius * 2, bullet.currCircle.radius * 2);
                }
            }
        }
        for (Unit unit : gameField.unitsManager.units) {
            for (UnitBullet bullet : unit.bullets) {
                TextureRegion textureRegion = bullet.textureRegion;
                if (textureRegion != null) {
                    spriteBatch.draw(textureRegion, bullet.currentPoint.x - bullet.currCircle.radius, bullet.currentPoint.y - bullet.currCircle.radius, bullet.currCircle.radius * 2, bullet.currCircle.radius * 2);
                }
            }
        }
    }

    private void drawGridNav() {
        shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        TmxMap map = gameField.tmxMap;
        Vector2 pos = new Vector2();
        float gridNavRadius = cameraController.sizeCellX/20f;
        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                Cell cell = gameField.getCellNoCheck(x, y);
                if (cell != null && !cell.isEmpty()) {
                    if (cell.isTerrain()) {
                        shapeRenderer.setColor(Color.RED);
                        if (cell.removableTerrain) {
                            shapeRenderer.getColor().set(255, 0, 0, 100);
                        }
                    } else if (cell.getUnit() != null) {
                        shapeRenderer.setColor(Color.GREEN);
                    } else if (cell.getTower() != null) {
                        shapeRenderer.setColor(Color.YELLOW);
                    }

                    if (cameraController.isDrawableGridNav == 5) {
                        for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                            pos.set(cell.getGraphicCoordinates(m));
                            shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                        }
                    } else if (cameraController.isDrawableGridNav != 0) {
                        pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                }
            }
        }

        Array<GridPoint2> spawnPoints = gameField.waveManager.getAllSpawnPoint();
        shapeRenderer.setColor(new Color(0f, 255f, 204f, 255f));
        if (!gameField.tmxMap.turnedMap) {
            for (GridPoint2 spawnPoint : spawnPoints) {
                Cell cell = gameField.getCell(spawnPoint.x, spawnPoint.y); // ArrayIndexOutOfBoundsException x==31 because turn x==16 && y==31 // turnedMap kostbIl
                if (cell != null) { // TODO need in turnAndFlip() convert. and this point and other
                    if (cameraController.isDrawableGridNav == 5) {
                        for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                            pos.set(cell.getGraphicCoordinates(m));
                            shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                        }
                    } else if (cameraController.isDrawableGridNav != 0) {
                        pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                } else {
                    Gdx.app.log("GameField::drawGridNav()", "-- cell:" + cell + " spawnPoint.x:" + spawnPoint.x + " spawnPoint.y:" + spawnPoint.y);
                }
            }
        }

        Array<GridPoint2> exitPoints = gameField.waveManager.getAllExitPoint();
        shapeRenderer.setColor(new Color(255f, 0f, 102f, 255f));
        if (!gameField.tmxMap.turnedMap) {
            for (GridPoint2 exitPoint : exitPoints) {
                Cell cell = gameField.getCell(exitPoint.x, exitPoint.y);
                if (cameraController.isDrawableGridNav == 5) {
                    for (int m = 1; m < cameraController.isDrawableGridNav; m++) {
                        pos.set(cell.getGraphicCoordinates(m));
                        shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                    }
                } else if (cameraController.isDrawableGridNav != 0) {
                    pos.set(cell.getGraphicCoordinates(cameraController.isDrawableGridNav));
                    shapeRenderer.circle(pos.x, pos.y, gridNavRadius);
                }
            }
        }

        shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : gameField.towersManager.towers) {
            for (Bullet bullet : tower.bullets) {
                shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, cameraController.sizeCellX/40f);
                if (null != bullet.currCircle) {
                    if (bullet.animation == null) {
                        shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }
        for (Unit unit : gameField.unitsManager.units) {
            for (UnitBullet bullet : unit.bullets) {
                shapeRenderer.rectLine(bullet.currentPoint.x, bullet.currentPoint.y, bullet.endPoint.x, bullet.endPoint.y, cameraController.sizeCellX/40f);
                if (null != bullet.currCircle) {
                    if (bullet.animation == null) {
                        shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        for (Tower tower : gameField.towersManager.towers) { // Draw Orange tower.bullets! -- bullet.currCircle
            for (Bullet bullet : tower.bullets) {
                if (null != bullet.currCircle) {
                    if (bullet.animation != null) {
                        shapeRenderer.circle(bullet.currCircle.x, bullet.currCircle.y, bullet.currCircle.radius);
                    }
                }
            }
        }

        shapeRenderer.setColor(Color.RED);
        for (Unit unit : gameField.unitsManager.units) {
            if (cameraController.isDrawableUnits == 5) {
                for (int m = 1; m < cameraController.isDrawableUnits; m++) {
                    Circle circle = unit.getCircle(m);
                    shapeRenderer.circle(circle.x, circle.y, circle.radius);
                }
            } else if (cameraController.isDrawableUnits != 0) {
                Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                shapeRenderer.circle(circle.x, circle.y, circle.radius);
            }
            if (unit.unitAttack != null && unit.unitAttack.circle != null) {
                shapeRenderer.circle(unit.unitAttack.circle.x, unit.unitAttack.circle.y, unit.unitAttack.circle.radius);
            }
        }

        shapeRenderer.setColor(Color.WHITE);
        for (Tower tower : gameField.towersManager.towers) { // Draw white towers radius! -- radiusDetectionCircle
            if (tower.radiusDetectionCircle != null) {
                if (cameraController.isDrawableGridNav == 5) {
                    if (cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m < cameraController.isDrawableTowers; m++) {
                            shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                        }
                    } else if (cameraController.isDrawableTowers != 0) {
                        shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if (cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        shapeRenderer.circle(tower.radiusDetectionCircle.x, tower.radiusDetectionCircle.y, tower.radiusDetectionCircle.radius);
                    }
                }
            }
        }

        shapeRenderer.setColor(Color.FIREBRICK);
        for (Tower tower : gameField.towersManager.towers) { // Draw FIREBRICK towers radius! -- radiusFlyShellCircle
            if (tower.radiusFlyShellCircle != null) {
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        shapeRenderer.circle(tower.radiusFlyShellCircle.x, tower.radiusFlyShellCircle.y, tower.radiusFlyShellCircle.radius);
                    }
                }
            }
        }

        shapeRenderer.setColor(Color.YELLOW);
        for (Tower tower : gameField.towersManager.towers) { // Draw YELLOW towers overlaps circle!
            if (tower.circles.size != 0) {
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            Circle towerCircle = tower.getCircle(m);
                            shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        Circle towerCircle = tower.getCircle(cameraController.isDrawableTowers);
                        shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        Circle towerCircle = tower.getCircle(cameraController.isDrawableGridNav);
                        shapeRenderer.circle(towerCircle.x, towerCircle.y, towerCircle.radius);
                    }
                }
            }
        }
        shapeRenderer.end();

        spriteBatch.begin();
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(0.5f);
        for (Unit unit : gameField.unitsManager.units) {
            if(cameraController.isDrawableGridNav == 5) {
                if(cameraController.isDrawableUnits == 5) {
                    for (int m = 1; m <= cameraController.isDrawableUnits; m++) {
                        Circle circle = unit.getCircle(m);
                        bitmapFont.draw(spriteBatch, String.valueOf(unit.hp), circle.x, circle.y+30);
                        bitmapFont.draw(spriteBatch, String.valueOf(unit.currentCell), circle.x, circle.y+20);
                        bitmapFont.draw(spriteBatch, String.valueOf(unit.nextCell), circle.x, circle.y+10);
                        bitmapFont.draw(spriteBatch, String.valueOf(unit.stepsInTime), circle.x, circle.y);
                    }
                } else if(cameraController.isDrawableUnits != 0) {
                    Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.hp), circle.x, circle.y+30);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.currentCell), circle.x, circle.y+20);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.nextCell), circle.x, circle.y+10);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.stepsInTime), circle.x, circle.y);
                }
            } else if(cameraController.isDrawableGridNav != 0) {
                if(cameraController.isDrawableGridNav == cameraController.isDrawableUnits) {
                    Circle circle = unit.getCircle(cameraController.isDrawableUnits);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.hp), circle.x, circle.y+30);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.currentCell), circle.x, circle.y+20);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.nextCell), circle.x, circle.y+10);
                    bitmapFont.draw(spriteBatch, String.valueOf(unit.stepsInTime), circle.x, circle.y);
                }
            }
        }

        for (Tower tower : gameField.towersManager.towers) { // Draw pit capacity value || players ID
            if (tower.templateForTower.towerAttackType == TowerAttackType.Pit) {
                bitmapFont.setColor(Color.YELLOW);
                bitmapFont.getData().setScale(0.5f);
                float halfSizeCellY = cameraController.halfSizeCellY/2;
                if(cameraController.isDrawableGridNav == 5) {
                    if(cameraController.isDrawableTowers  == 5) {
                        for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                            bitmapFont.draw(spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+halfSizeCellY);
                        }
                    } else if(cameraController.isDrawableTowers != 0) {
                        bitmapFont.draw(spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+halfSizeCellY);
                    }
                } else if(cameraController.isDrawableGridNav != 0) {
                    if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                        bitmapFont.draw(spriteBatch, String.valueOf(tower.capacity), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+halfSizeCellY);
                    }
                }
            }
            bitmapFont.getData().setScale(0.9f);
            if (tower.player == gameField.gameScreen.playersManager.getLocalServer()) {
                bitmapFont.setColor(Color.GRAY);
            } else if (tower.player == gameField.gameScreen.playersManager.getLocalPlayer()) {
                bitmapFont.setColor(Color.BLUE);
            } else {
                bitmapFont.setColor(Color.RED);
            }
            if(cameraController.isDrawableGridNav == 5) {
                if(cameraController.isDrawableTowers == 5) {
                    for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                        bitmapFont.draw(spriteBatch, String.valueOf(tower.player.playerID), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                    }
                } else if(cameraController.isDrawableTowers != 0) {
                    bitmapFont.draw(spriteBatch, String.valueOf(tower.player.playerID), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                }
            } else if(cameraController.isDrawableGridNav != 0) {
                if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                    bitmapFont.draw(spriteBatch, String.valueOf(tower.player.playerID), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y);
                }
            }
        }

        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(0.5f);
        for (Tower tower : gameField.towersManager.towers) { // Draw tower specific information for multiplayer
            if(cameraController.isDrawableGridNav == 5) {
                if(cameraController.isDrawableTowers == 5) {
                    for (int m = 1; m <= cameraController.isDrawableTowers; m++) {
                        bitmapFont.draw(spriteBatch, String.valueOf(tower.hp), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+30);
                        bitmapFont.draw(spriteBatch, "B:" +tower.bullets.size, tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+20);
                        bitmapFont.draw(spriteBatch, String.valueOf(tower.cell), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+10);
                    }
                } else if(cameraController.isDrawableTowers != 0) {
                    bitmapFont.draw(spriteBatch, String.valueOf(tower.hp), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+30);
                    bitmapFont.draw(spriteBatch, "B:" +tower.bullets.size, tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+20);
                    bitmapFont.draw(spriteBatch, String.valueOf(tower.cell), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+10);
                }
            } else if(cameraController.isDrawableGridNav != 0) {
                if(cameraController.isDrawableGridNav == cameraController.isDrawableTowers) {
                    bitmapFont.draw(spriteBatch, String.valueOf(tower.hp), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+30);
                    bitmapFont.draw(spriteBatch, "B:" +tower.bullets.size, tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+20);
                    bitmapFont.draw(spriteBatch, String.valueOf(tower.cell), tower.centerGraphicCoordinates.x, tower.centerGraphicCoordinates.y+10);
                }
            }
        }
        spriteBatch.end();
    }

    private void drawRoutes() {
        shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float gridNavRadius = cameraController.sizeCellX/22f;
        for (int u = 0; u < gameField.unitsManager.units.size; u++) {
            Unit unit = gameField.unitsManager.units.get(u);
//        for (Unit unit : gameField.unitsManager.units) {
            if (unit.player == gameField.gameScreen.playersManager.getLocalPlayer()) {
                shapeRenderer.setColor(Color.WHITE);
            } else {
                shapeRenderer.setColor(Color.BROWN); // (100, 60, 21, 1f);
            }
            ArrayDeque<Cell> unitRoute = unit.route;
            if (unitRoute != null && !unitRoute.isEmpty()) {
                for (Cell cell : unitRoute) {
                    if (cell != null) {
                        Vector2 cellCoord = new Vector2();
                            if (cameraController.isDrawableRoutes == 5) {
                            for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                                cellCoord.set(cell.getGraphicCoordinates(m));
                                shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius);
                            }
                        } else if (cameraController.isDrawableRoutes != 0) {
                            cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                            shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius);
                        }
                    }
                }
                shapeRenderer.setColor(0.756f, 0.329f, 0.756f, 1f);
                Cell cell = unitRoute.getLast();
//                Cell cell = getCell(destinationPoint.getX(), destinationPoint.getY());
                if (cell != null) {
                    Vector2 cellCoord = new Vector2();
                    if (cameraController.isDrawableRoutes == 5) {
                        for (int m = 1; m < cameraController.isDrawableRoutes; m++) {
                            cellCoord.set(cell.getGraphicCoordinates(m));
                            shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius * 0.7f);
                        }
                    } else if (cameraController.isDrawableRoutes != 0) {
                        cellCoord.set(cell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                        shapeRenderer.circle(cellCoord.x, cellCoord.y, gridNavRadius * 0.7f);
                    }
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawWavesRoutes() {
        shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BROWN);
        for (Wave wave : gameField.waveManager.waves) {
            drawWave(wave);
        }
        shapeRenderer.setColor(Color.BLUE);
        for (Wave wave : gameField.waveManager.wavesForUser) {
            drawWave(wave);
        }
        shapeRenderer.end();
    }

    private void drawWave(Wave wave) {
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
                        shapeRenderer.rectLine(startCellCoord, endCellCoord, linesWidth);
                    }
                } else if (cameraController.isDrawableRoutes != 0) {
                    startCellCoord.set(startCell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                    endCellCoord.set(endCell.getGraphicCoordinates(cameraController.isDrawableRoutes));
                    shapeRenderer.rectLine(startCellCoord, endCellCoord, linesWidth);
                }
                startNode = endNode;
            }
        }
    }

    private void drawTowersUnderConstruction() {
        UnderConstruction underConstruction = gameField.getUnderConstruction();
        if (underConstruction != null) {
            Player player = gameField.gameScreen.playersManager.getLocalPlayer();
            if (player != null) {
                int goldNeed = underConstruction.templateForTower.cost;
                boolean enoughGold = (player.gold >= goldNeed) ? true : false;
                if (underConstruction.state == 0) {
                    drawTowerUnderConstruction(underConstruction.endX, underConstruction.endY, underConstruction.templateForTower, enoughGold);
                } else if (underConstruction.state == 1) {
                    drawTowerUnderConstruction(underConstruction.startX, underConstruction.startY, underConstruction.templateForTower, enoughGold);
                    for (int k = 0; k < underConstruction.coorsX.size; k++) {
                        goldNeed += underConstruction.templateForTower.cost;
                        enoughGold = (gameField.gameScreen.playersManager.getLocalPlayer().gold >= goldNeed);// ? true : false;
                        drawTowerUnderConstruction(underConstruction.coorsX.get(k), underConstruction.coorsY.get(k), underConstruction.templateForTower, enoughGold);
                    }
                }
            }
        }
    }

    private void drawTowerUnderConstruction(int buildX, int buildY, TemplateForTower templateForTower, boolean enoughGold) {
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
//                Color oldColorSB = spriteBatch.getColor();
//                Color oldColorSR = shapeRenderer.getColor();
                if (enoughGold && canBuild) {
                    spriteBatch.setColor(0, 1f, 0, 0.55f);
                    shapeRenderer.setColor(0, 1f, 0, 0.55f);
                } else {
                    spriteBatch.setColor(1f, 0, 0, 0.55f);
                    shapeRenderer.setColor(1f, 0, 0, 0.55f);
                }
                if (cameraController.isDrawableTowers == 5) {
                    for (int map = 1; map < cameraController.isDrawableTowers; map++) {
                        drawTowerUnderConstructionAndMarks(map, templateForTower, mainCell, startDrawCell, finishDrawCell);
                    }
                } else if (cameraController.isDrawableTowers != 0) {
                    drawTowerUnderConstructionAndMarks(cameraController.isDrawableTowers, templateForTower, mainCell, startDrawCell, finishDrawCell);
                }
                spriteBatch.setColor(Color.WHITE);
                shapeRenderer.setColor(Color.WHITE);
            }
        }
    }

    private void drawTowerUnderConstructionAndMarks(int map, TemplateForTower templateForTower, Cell mainCell, GridPoint2 startDrawCell, GridPoint2 finishDrawCell) {
        float sizeCellX = cameraController.sizeCellX;
        float sizeCellY = cameraController.sizeCellY*2;
        if (!cameraController.gameField.tmxMap.isometric) {
            sizeCellY = cameraController.sizeCellY;
        }
        TextureRegion textureRegion = templateForTower.idleTile.getTextureRegion();
        int towerSize = templateForTower.size;
        Vector2 towerPos = new Vector2(mainCell.getGraphicCoordinates(map));
        if (templateForTower.radiusDetection != null) {
            shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection);
        }
        cameraController.getCorrectGraphicTowerCoord(towerPos, towerSize, map);
        spriteBatch.draw(textureRegion, towerPos.x, towerPos.y, sizeCellX * towerSize, sizeCellY * towerSize);
//        shapeRenderer.circle(towerPos.x, towerPos.y, templateForTower.radiusDetection/4);
        if (greenCheckmark != null && redCross != null) {
            Vector2 markPos = new Vector2();
            for (int x = startDrawCell.x; x <= finishDrawCell.x; x++) {
                for (int y = startDrawCell.y; y <= finishDrawCell.y; y++) {
                    Cell markCell = gameField.getCell(mainCell.cellX + x, mainCell.cellY + y);
                    if (markCell != null) {
                        markPos.set(markCell.getGraphicCoordinates(map));
                        markPos.add(-(cameraController.halfSizeCellX), -(cameraController.halfSizeCellY));
                        if(markCell.isEmpty()) {
                            spriteBatch.draw(greenCheckmark, markPos.x, markPos.y, sizeCellX, sizeCellY);
                        } else {
                            spriteBatch.draw(redCross, markPos.x, markPos.y, sizeCellX, sizeCellY);
                        }
                    }
                }
            }
            markPos = null; // delete markPos;
        }
        towerPos = null; // delete towerPos;
    }
}
