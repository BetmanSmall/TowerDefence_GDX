package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameInterface.GameInterface;
import com.betmansmall.enums.GameType;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.util.logging.Logger;

import java.util.Random;

/**
 * Created by betma on 16.11.2018.
 */
public class CameraController extends AbstractCameraController {
    public ShapeRenderer shapeRenderer;
    public SpriteBatch spriteBatch;
    public BitmapFont bitmapFont;

    public GameScreen gameScreen;
    public GameField gameField;
    public GameInterface gameInterface;

//    public float cameraX = 800;
//    public float cameraY = 0;
    public OrthographicCamera camera;

    public int mapWidth, mapHeight;
//    public float viewportWidth = 0;
//    public float viewportHeight = 0;

    public int isDrawableGrid = 1;
    public int isDrawableUnits = 1;
    public int isDrawableTowers = 1;
    public int isDrawableBackground = 1;
    public int isDrawableGround = 1;
    public int isDrawableForeground = 1;
    public int isDrawableGridNav = 1;
    public int isDrawableRoutes = 1;
    public int drawOrder = 8;
    public boolean isDrawableFullField = false;

    public boolean flinging = false; // Что бы не пересикалось одно действие с другим действием (с) Андрей А
    public float initialScale = 2f;
    public float velX;
    public float velY;
//    public boolean lastCircleTouched = false;

    public float sizeCellX, sizeCellY;
    public float halfSizeCellX, halfSizeCellY;
    public float zoomMax = 5.0f;
    public float zoomMin = 0.1f;
//    public float zoom = 1;
    private float borderLeftX = 0.0f, borderRightX = 0.0f;
    private float borderUpY = 0.0f, borderDownY = 0.0f;

    public boolean panLeftMouseButton = true;
    public boolean panMidMouseButton = true;
    public boolean panRightMouseButton = true;
    public boolean paning = false;
    public int touchDownX, touchDownY;
    public int prevMouseX, prevMouseY;
    private Random random;

    public CameraController(GameScreen gameScreen) {
        Logger.logFuncStart("gameScreen:" + gameScreen);
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.bitmapFont = new BitmapFont();
        this.bitmapFont.getData().scale(Gdx.graphics.getHeight()*0.001f);

        this.gameScreen = gameScreen;
        this.gameField = gameScreen.gameField;
        this.gameInterface = gameScreen.gameInterface;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.mapWidth = gameField.tmxMap.width;
        this.mapHeight = gameField.tmxMap.height;
        this.sizeCellX = gameField.tmxMap.tileWidth;
        this.sizeCellY = gameField.tmxMap.tileHeight;
        this.halfSizeCellX = sizeCellX/2;
        this.halfSizeCellY = sizeCellY/2;
        random = new Random();
//        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void dispose() {
        Gdx.app.log("CameraController::dispose()", "--");
        shapeRenderer.dispose();
        spriteBatch.dispose();
        bitmapFont.dispose();

        gameScreen = null;
        gameField = null;
        gameInterface = null;
        camera = null;
    }

    void setBorders() {
        float borderLeftX  = (0 - (halfSizeCellX * mapHeight));
        float borderRightX = (0 + (halfSizeCellX * mapWidth));
        float borderUpY    = (0);
        float borderDownY  = (0 - (sizeCellY * (mapWidth>mapHeight ? mapWidth : mapHeight)));
        this.setBorders(borderLeftX, borderRightX, borderUpY, borderDownY);
    }

    void setBorders(float borderLeftX, float borderRightX, float borderUpY, float borderDownY) {
        this.borderLeftX  = borderLeftX;
        this.borderRightX = borderRightX;
        this.borderUpY    = borderUpY;
        this.borderDownY  = borderDownY;
    }

    @Override
    public boolean longPress(float x, float y) {
        super.longPress(x, y);
//        if (!gameInterface.interfaceTouched) {
            Vector3 touch = new Vector3(x, y, 0.0f);
            whichCell(touch, isDrawableTowers);
            if (random.nextBoolean()) {
                gameScreen.towerToggle((int) touch.x, (int) touch.y);
            } else {
                if (random.nextInt(5) == 0 && gameField.gameSettings.gameType == GameType.LittleGame) {
                    gameField.spawnHero((int) touch.x, (int) touch.y);
                } else {
                    gameField.spawnCompUnitToRandomExit((int) touch.x, (int) touch.y);
                }
            }
//        }
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        super.fling(velocityX, velocityY, button);
//        if (!gameInterface.interfaceTouched) {
            flinging = true;
            velX = camera.zoom * velocityX * 0.5f;
            velY = camera.zoom * velocityY * 0.5f;
//        }
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        super.zoom(initialDistance, distance);
        float ratio = initialDistance / distance;
        float newZoom = initialScale * ratio;
        if (newZoom < zoomMax && newZoom > zoomMin) {
            camera.zoom = newZoom;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);
        this.touchDownX = screenX;
        this.touchDownY = screenY;
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        selectTower(screenX, screenY);
        if ( ( (panLeftMouseButton && button == 0) ||
                (panRightMouseButton && button == 1) ||
                (panMidMouseButton && button == 2) ) ) {
//            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
//            Gdx.graphics.setCursor(Cursor.SystemCursor.Arrow);
//            setCursor(Qt::ClosedHandCursor);
            paning = true;
        }
        flinging = false;
        initialScale = camera.zoom;
        if (!gameInterface.interfaceTouched) {
            UnderConstruction underConstruction = gameField.getUnderConstruction();
            if (underConstruction != null) {
                if (button == 0) {
                    Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                    if (whichCell(touch, isDrawableTowers)) {
                        underConstruction.setStartCoors((int) touch.x, (int) touch.y);
                    }
//                } else if (button == 1) {
//                    gameField.cancelUnderConstruction();
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (paning) {
            if ( ( (panLeftMouseButton && button == 0) ||
                    (panRightMouseButton && button == 1) ||
                    (panMidMouseButton && button == 2) ) ) {
//                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
//                setCursor(Qt::ArrowCursor);
                paning = false;
            }
//            fling((float)(prevMouseX)-(prevMouseX), (float)(prevMouseY)-(prevMouseY), button);
        }
        if (!gameInterface.interfaceTouched) {
            Vector3 touch = new Vector3(screenX, screenY, 0.0f);
            if (gameField.getUnderConstruction() != null) {
                if (button == 0) {
                    if (whichCell(touch, isDrawableTowers)) {
                        gameField.buildTowersWithUnderConstruction((int) touch.x, (int) touch.y);
                    }
                } else if (button == 1) {
                    gameField.cancelUnderConstruction();
                }
            } else {
//            int tmpCellX = screenX;
//            int tmpCellY = screenY;
//            whichCell(tmpCellX, tmpCellY, 5);
                if ( (touchDownX == screenX && touchDownY == screenY) /*|| (prevCellX == tmpCellX && prevCellY == tmpCellY)*/ ) {
                    if (gameField.gameSettings.gameType == GameType.LittleGame) {
                        if (button == 0) {
                            if (whichCell(touch, isDrawableUnits)) {
                                gameField.rerouteHero((int) touch.x, (int) touch.y);
                            }
                        } else if (button == 1) {
                            if (whichCell(touch, isDrawableGround)) {
                                Cell cell = gameField.getCell((int) touch.x, (int) touch.y);
                                if (cell.isTerrain()) {
                                    cell.removeTerrain(random.nextBoolean());
                                    Gdx.app.log("CameraController::touchUp", "-- x:" + cell.cellX + " y:" + cell.cellY + " cell.isTerrain():" + cell.isTerrain());
                                } else if (cell.getTower() != null) {
                                    Tower tower = cell.getTower();
//                                    gameField.removeTowerWithGold(tower.cell.cellX, tower.cell.cellY);
                                    gameField.removeTower(tower.cell.cellX, tower.cell.cellY);
                                } else if (cell.isEmpty()) {
//                                gameField.towerActions(cell.cellX, cell.cellY);
                                    gameField.createTower(cell.cellX, cell.cellY, gameField.factionsManager.getRandomTemplateForTowerFromAllFaction());
                                    if (random.nextBoolean()) {
                                        int randNumber = (125 + random.nextInt(2));
                                        cell.setTerrain(gameField.tmxMap.getTileSets().getTileSet(0).getTile(randNumber), true, true);
                                    }
                                }
                            }
                        } else if (button == 2) {
                            if (whichCell(touch, isDrawableUnits)) {
                                if (random.nextInt(5) == 0) {
                                    gameField.spawnHero((int) touch.x, (int) touch.y);
                                } else {
                                    gameField.spawnCompUnitToRandomExit((int) touch.x, (int) touch.y);
                                }
                            }
                        }
                    } else if (gameField.gameSettings.gameType == GameType.TowerDefence) {
                        if (button == 0 || button == 1) {
                            if (whichCell(touch, isDrawableTowers)) {
                                gameScreen.towerToggle((int) touch.x, (int) touch.y);
                            }
                        } else if (button == 2) {
                            if (whichCell(touch, isDrawableUnits)) {
                                gameField.spawnCompUnitToRandomExit((int) touch.x, (int) touch.y);
                            }
                        } else if (button == 4) {
                            if (whichCell(touch, isDrawableUnits)) {
//                            gameField.setExitPoint((int) touch.x, (int) touch.y);
                            }
                        }
                    }
                }
            }
        }
        gameInterface.interfaceTouched = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);
        if (gameField != null) {
            UnderConstruction underConstruction = gameField.getUnderConstruction();
            if (underConstruction != null) {
                Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                if (whichCell(touch, isDrawableTowers)) {
                    underConstruction.setEndCoors((int) touch.x, (int) touch.y);
                }
            }

            if (underConstruction == null || Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                if (paning) {
                    float deltaX = this.prevMouseX - screenX;
                    float deltaY = this.prevMouseY - screenY;
                    prevMouseX = screenX;
                    prevMouseY = screenY;
                    float newCameraX = camera.position.x + (deltaX * camera.zoom);
                    float newCameraY = camera.position.y - (deltaY * camera.zoom);
                    if (borderLeftX != 0.0f || borderRightX != 0.0f || borderUpY != 0.0f || borderDownY != 0.0f) {
                        if (borderLeftX < newCameraX && newCameraX < borderRightX &&
                                borderUpY > newCameraY && newCameraY > borderDownY) {
                            camera.position.set(newCameraX, newCameraY, 0.0f);
                        }
                    } else {
                        camera.position.set(newCameraX, newCameraY, 0.0f);
                    }
                }
            }
        }
//        Gdx.app.log("CameraController::pan()", "-- camera.viewportWidth:" + camera.viewportWidth + " camera.viewportHeight:" + camera.viewportHeight);
//        float space = 50f;
//        float shiftCamera = 5f;
//        if (x < space) {
//            camera.position.add(-shiftCamera, 0.0f, 0.0f);
//        }
//        if (x > camera.viewportWidth - space) {
//            camera.position.add(shiftCamera, 0.0f, 0.0f);
//        }
//        if (y < space) {
//            camera.position.add(0.0f, shiftCamera, 0.0f);
//        }
//        if (y > camera.viewportHeight - space) {
//            camera.position.add(0.0f, -shiftCamera, 0.0f);
//        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        super.mouseMoved(screenX, screenY);
//        float deltaX = this.prevMouseX - screenX;
//        float deltaY = this.prevMouseY - screenY;
////        pan(screenX, screenY, deltaX, deltaY/*, buttons*/);
        this.prevMouseX = screenX;
        this.prevMouseY = screenY;
        if (gameField != null) {
            UnderConstruction underConstruction = gameField.getUnderConstruction();
            if (underConstruction != null) {
                Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                if (whichCell(touch, isDrawableTowers)) {
                    underConstruction.setEndCoors((int) touch.x, (int) touch.y);
                }
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        super.scrolled(amount);
//        if (gameInterface.scrolled(amount)) {
//            return false;
//        }
        if (amount > 0) {
            if (camera.zoom <= zoomMax)
                camera.zoom += 0.1f;
        } else if (amount < 0) {
            if (camera.zoom >= zoomMin)
                camera.zoom -= 0.1f;
        }
        camera.update();
        Gdx.app.log("CameraController::scrolled()", "-- camera.zoom:" + camera.zoom);
        return false;
    }

    public void update(float deltaTime) {
        try {
            if (gameField.getUnderConstruction() == null) {
                if (flinging) {
                    velX *= 0.98f;
                    velY *= 0.98f;
//                    float newCameraX = camera.position.x - velX;
//                    float newCameraY = camera.position.y + velY;
                    float newCameraX = camera.position.x - (velX * deltaTime);
                    float newCameraY = camera.position.y + (velY * deltaTime);
                    if (borderLeftX != 0.0f || borderRightX != 0.0f || borderUpY != 0.0f || borderDownY != 0.0f) {
                        if (borderLeftX < newCameraX && newCameraX < borderRightX &&
                                borderUpY > newCameraY && newCameraY > borderDownY) {
                            this.camera.position.x = newCameraX;
                            this.camera.position.y = newCameraY;
                        }
                    } else {
                        this.camera.position.x = newCameraX;
                        this.camera.position.y = newCameraY;
                    }
                    if (Math.abs(velX) < 0.01) velX = 0.0f;
                    if (Math.abs(velY) < 0.01) velY = 0.0f;
                    if (velX == 0.0 && velY == 0.0) {
                        flinging = false;
                    }
//                    Gdx.app.log("CameraController::update()", "-- velX:" + velX + " velY:" + velY);
//                    Gdx.app.log("CameraController::update()", "-- newCameraX:" + newCameraX + " newCameraY:" + newCameraY);
                }
            }
            camera.update();
        } catch (Exception exp) {
            Gdx.app.error("CameraController::update()", "-- Exception:" + exp);
        }
    }

//    void unproject(int &prevMouseX, int &prevMouseY) {
////    Gdx.app.log("CameraController::unproject()", "-- prevMouseX:" + prevMouseX + " prevMouseY:" + prevMouseY + " cameraX:" + cameraX + " cameraY:" + cameraY);
//        prevMouseX -= (cameraX*zoom);
//        prevMouseY -= (cameraY*zoom);
////    Gdx.app.log("CameraController::unproject()", "-- prevMouseX:" + prevMouseX + " prevMouseY:" + prevMouseY + " cameraX:" + cameraX + " cameraY:" + cameraY);
//    }

    /**
     * Selects tower in touched cell, if there is one.
     */
    private void selectTower(final int screenX, final int screenY) {
        Vector3 vector = new Vector3(screenX, screenY, 0);
        if (!whichCell(vector, 5)) return;
        Tower tower = gameField.getCell((int) vector.x, (int) vector.y).tower;
        if (tower != null) {
            gameField.towersManager.selectTower(gameScreen.playersManager.getLocalPlayer(), tower);
        }
    }

    /**
     * Calculates coordinates of touched cell, returning them in given vector.
     * Returns false, touch was out of map.
     */
    private boolean whichCell(Vector3 mouse, int map) {
//        Gdx.app.log("CameraController::whichCell()", "-wind- mouseX:" + mouse.x + " mouseY:" + mouse.y);
        camera.unproject(mouse);
//        Gdx.app.log("CameraController::whichCell()", "-grph- mouseX:" + mouse.x + " mouseY:" + mouse.y);
        float gameX = ((mouse.x / (halfSizeCellX)) + (mouse.y / (halfSizeCellY))) / 2;
        float gameY = ((mouse.y / (halfSizeCellY)) - (mouse.x / (halfSizeCellX))) / 2;
        if (!gameField.tmxMap.isometric) {
            gameX = (mouse.x / sizeCellX);
            gameY = (mouse.y / sizeCellY);
        }
//        Gdx.app.log("CameraController::whichCell()", "-graphics- mouseX:" + mouse.x + " mouseY:" + mouse.y + " map:" + map + " -new- gameX:" + gameX + " gameY:" + gameY);
        int cellX = Math.abs((int) gameX);
        int cellY = Math.abs((int) gameY);
        if(gameField.tmxMap.isometric && gameY < 0) {
            int tmpX = cellX;
            cellX = cellY;
            cellY = tmpX;
        } // Где то я накосячил. мб сделать подругому.
        // если это убирать то нужно будет править Cell::setGraphicCoordinates() для 3 и 4 карты-java // c++ ?? or ??
        mouse.x = cellX;
        mouse.y = cellY;
//        Gdx.app.log("CameraController::whichCell()", "-cell- cellX:" + cellX + " cellY:" + cellY);
        if (cellX < mapWidth && cellY < mapHeight) {
            if (map == 5) {
                return true;
            } else {
                if ( (map == 2 && gameX > 0 && gameY < 0)
                  || (map == 3 && gameX > 0 && gameY > 0) ) {
                    return true;
                } else if ( (map == 4 && gameX < 0 && gameY > 0)
                         || (map == 1 && gameX < 0 && gameY < 0) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getCorrectGraphicTowerCoord(Vector2 towerPos, int towerSize, int map) {
        if(map == 1) {
            if (!gameField.tmxMap.isometric) {
                towerPos.x += (-(halfSizeCellX * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))));
            } else {
                towerPos.x += (-(halfSizeCellX * towerSize) );
            }
            towerPos.y += (-(halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
        } else if(map == 2) {
            towerPos.x += (-(halfSizeCellX * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
            if (!gameField.tmxMap.isometric) {
                towerPos.y += (-(halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))));
            } else {
                towerPos.y += (-(halfSizeCellY * towerSize));
            }
        } else if(map == 3) {
            if (!gameField.tmxMap.isometric) {
                towerPos.x += (-(halfSizeCellX * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
            } else {
                towerPos.x += (-(halfSizeCellX * towerSize) );
            }
            towerPos.y += (-(halfSizeCellY * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
        } else if(map == 4) {
            towerPos.x += (-(halfSizeCellX * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
            if (!gameField.tmxMap.isometric) {
                towerPos.y += (-(halfSizeCellY * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
            } else {
                towerPos.y += (-(halfSizeCellY * towerSize));
            }
        } else {
            Gdx.app.log("GameField::getCorrectGraphicTowerCoord(" + towerPos + ", " + towerSize + ", " + map + ")", "-- Bad map[1-4] value:" + map);
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Camera[");
        sb.append("cameraX:" + camera.position.x);
        sb.append(",cameraY:" + camera.position.y);
        sb.append(",sizeCellX:" + sizeCellX);
        sb.append(",sizeCellY:" + sizeCellY);
        sb.append(",camera.zoom:" + camera.zoom);
//        sb.append(",zoomMax:" + zoomMax);
//        sb.append(",zoomMin:" + zoomMin);
//        sb.append(",borderLeftX:" + borderLeftX);
//        sb.append(",borderRightX:" + borderRightX);
//        sb.append(",borderUpY:" + borderUpY);
//        sb.append(",borderDownY:" + borderDownY);
        sb.append("]");
        return sb.toString();
    }
}
