package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.GameScreenInteface.GameInterface;
import com.betmansmall.game.GameType;

/**
 * Created by betma on 16.11.2018.
 */

public class CameraController implements GestureDetector.GestureListener, InputProcessor {
    public ShapeRenderer shapeRenderer;
    public SpriteBatch spriteBatch;
    public BitmapFont bitmapFont;

    public GameField gameField;
    public GameInterface gameInterface;

    public OrthographicCamera camera;
//    public float viewportWidth = 0;
//    public float viewportHeight = 0;
    public int mapWidth, mapHeight;
//    public float cameraX = 800;
//    public float cameraY = 0;

    public int isDrawableGrid = 1;
    public int isDrawableUnits = 1;
    public int isDrawableTowers = 1;
    public int isDrawableBackground = 1;
    public int isDrawableGround = 1;
    public int isDrawableForeground = 1;
    public int isDrawableGridNav = 1;
    public int isDrawableRoutes = 1;
    public int drawOrder = 8;

//    public boolean flinging = false; // Что бы не пересикалось одно действие с другим действием (с) Андрей А
//    public float initialScale = 2f;
//    boolean lastCircleTouched = false;
//    public float velX;
//    public float velY;

    public float sizeCellX, sizeCellY;
    public float halfSizeCellX, halfSizeCellY;
    public float zoomMax = 50.0f;
    public float zoomMin = 0.2f;
    public float zoom = 1;
    Float borderLeftX, borderRightX;
    Float borderUpY, borderDownY;

//    public boolean panMidMouseButton = false;
//    public boolean paning = false;
//    public int prevMouseX, prevMouseY;
//    public int prevMouseCellX, prevMouseCellY;
//    public int prevGlobalMouseX, prevGlobalMouseY;

    public CameraController(GameField gameField, GameInterface gameInterface, OrthographicCamera camera) {
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.bitmapFont = new BitmapFont();
        this.gameField = gameField;
        this.gameInterface = gameInterface;
        this.camera = camera;
        this.mapWidth = gameField.map.width;
        this.mapHeight = gameField.map.height;
        this.sizeCellX = gameField.map.tileWidth;
        this.sizeCellY = gameField.map.tileHeight;
        this.halfSizeCellX = sizeCellX/2;
        this.halfSizeCellY = sizeCellY/2;
    }

    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        bitmapFont.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Gdx.app.log("CameraController::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
//            flinging = false;
//            initialScale = camera.zoom;
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("CameraController::tap()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
        if (gameInterface.tap(x, y, count, button)) {
            return false;
        }
        UnderConstruction underConstruction = gameField.getUnderConstruction();
        Vector3 touch = new Vector3(x, y, 0.0f);
        if (underConstruction == null) {
            if (gameField.gameSettings.gameType == GameType.LittleGame) {
                if (button == 0) {
                    if (whichCell(touch, isDrawableUnits)) {
                        gameField.updateHeroDestinationPoint((int) touch.x, (int) touch.y);
                    }
                } else if (button == 1) {
                    if (whichCell(touch, isDrawableGround)) {
                        Cell cell = gameField.getCell((int) touch.x, (int) touch.y);
                        if (cell.isTerrain()) {
                            cell.removeTerrain( ( ((int)(Math.random()*2))==0 ) ? true : false );
                            Gdx.app.log("CameraController::tap()", "-- x:" + cell.cellX + " y:" + cell.cellY + " cell.isTerrain():" + cell.isTerrain());
                        } else if (cell.getTower() != null) {
                            Tower tower = cell.getTower();
                            gameField.removeTower(tower.cell.cellX, tower.cell.cellY);
                        } else if (cell.isEmpty()) {
//                                gameField.towerActions(cell.cellX, cell.cellY);
                            gameField.createTower(cell.cellX, cell.cellY, gameField.factionsManager.getRandomTemplateForTowerFromAllFaction(), ( (int)(Math.random()*3) ) );
                            if ( ( ((int)(Math.random()*2))==0 ) ? true : false ) {
                                int randNumber = (125 + (int) (Math.random() * 2));
                                cell.setTerrain(gameField.map.getTileSets().getTileSet(0).getTile(randNumber), true, true);
                            }
//                            gameField.updateHeroDestinationPoint();
                        }
                    }
                } else if (button == 2) {
                    if (whichCell(touch, isDrawableUnits)) {
                        gameField.spawnHero((int) touch.x, (int) touch.y);
                    }
                }
            } else if (gameField.gameSettings.gameType == GameType.TowerDefence) {
                if (button == 0 || button == 1) {
                    if (whichCell(touch, isDrawableTowers)) {
                        gameField.towerActions((int) touch.x, (int) touch.y);
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
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        Gdx.app.log("CameraController::longPress()", "-- x:" + x + " y:" + y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("CameraController::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY + " button:" + button);
//        if (!lastCircleTouched) {
//            flinging = true;
//            velX = camera.zoom * velocityX * 0.5f;
//            velY = camera.zoom * velocityY * 0.5f;
//        }
//        Gdx.app.log("CameraController::fling()", "-- velX:" + velX + " velY:" + velY);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Gdx.app.log("CameraController::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
//        Vector3 touch = new Vector3(x, y, 0.0f);
//        camera.unproject(touch);
//            Gdx.app.log("CameraController::pan(1)", "-- x:" + camera.cell.x + " y:" + camera.cell.y);
//            Gdx.app.log("CameraController::pan(2)", "-- x:" + touch.x + " y:" + touch.y);
        if (gameInterface.pan(x, y, deltaX, deltaY)) {
//            lastCircleTouched = true;
            return true;
        }
//        lastCircleTouched = false;
        if (gameField.getUnderConstruction() == null || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            float newCameraX = camera.position.x + (-deltaX * camera.zoom);
            float newCameraY = camera.position.y + (deltaY * camera.zoom);
            if (borderLeftX != null && borderRightX != null && borderUpY != null && borderDownY != null) {
                Gdx.app.log("CameraController::pan()", "-- borderLeftX:" + borderLeftX + " borderRightX:" + borderRightX + " borderUpY:" + borderUpY + " borderDownY:" + borderDownY);
                if (borderLeftX < newCameraX && newCameraX < borderRightX &&
                        borderUpY > newCameraY && newCameraY > borderDownY) {
                    camera.position.set(newCameraX, newCameraY, 0.0f);
                }
            } else {
                camera.position.set(newCameraX, newCameraY, 0.0f);
            }
        } else {
            float space = 50f;
            float shiftCamera = 5f;
            if (x < space) {
                camera.position.add(-shiftCamera, 0.0f, 0.0f);
            }
            if (x > Gdx.graphics.getWidth() - space) {
                camera.position.add(shiftCamera, 0.0f, 0.0f);
            }
            if (y < space) {
                camera.position.add(0.0f, shiftCamera, 0.0f);
            }
            if (y > Gdx.graphics.getHeight() - space) {
                camera.position.add(0.0f, -shiftCamera, 0.0f);
            }
        }
        return false;
    }

//    boolean pan(float x, float y) {
////    Gdx.app.log("CameraController::pan()", "-- paning:" + paning;
////    Gdx.app.log("CameraController::pan()", "-- cameraX:" + cameraX + " cameraY:" + cameraY;
//        if (paning) {
//            float deltaX = x - prevMouseX;
//            float deltaY = y - prevMouseY;
////        Gdx.app.log("CameraController::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY;
//            float newCameraX = cameraX + (deltaX * (1/zoom) );
//            float newCameraY = cameraY + (deltaY * (1/zoom) );
////        if (borderLeftX != null && borderRightX != null && borderUpY != null && borderDownY != null) {
////            if (borderLeftX < newCameraX && newCameraX < borderRightX &&
////                    borderUpY > newCameraY && newCameraY > borderDownY) {
////            this.prevMouseX = x;
////            this.prevMouseY = y;
//            this.cameraX = newCameraX;
//            this.cameraY = newCameraY;
////        } else {
////        }
////        Gdx.app.log("CameraController::pan()", "-- newCameraX:" + newCameraX + " newCameraY:" + newCameraY;
//        }
//    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("CameraController::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        if(gameInterface.panStop(x, y, pointer, button)) {
//            return true;
        }
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        Gdx.app.log("CameraController::zoom()", "-- initialDistance:" + initialDistance + " distance:" + distance);
//        Gdx.app.log("CameraController::zoom()", "-- initialScale:" + initialScale);
        float ratio = initialDistance / distance;
        float newZoom = /*initialScale **/ ratio;
        if (newZoom < zoomMax && newZoom > zoomMin) {
            camera.zoom = newZoom;
        }
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        Gdx.app.log("CameraController::pinch()", "-- initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
        return false;
    }

    public void update(float deltaTime) {
//    Gdx.app.log("CameraController::update()", "-- deltaTime:" + deltaTime);
        try {
            if (gameField.getUnderConstruction() == null) {
//                if (flinging) {
//                    velX *= 0.98f;
//                    velY *= 0.98f;
//                    float newCameraX = camera.position.x + (velX * deltaTime);
//                    float newCameraY = camera.position.y + (velY * deltaTime);
////                    if (borderLeftX != null && borderRightX != null && borderUpY != null && borderDownY != null) {
////                        if (borderLeftX < newCameraX && newCameraX < borderRightX &&
////                                borderUpY > newCameraY && newCameraY > borderDownY) {
////                            this.cameraX = newCameraX;
////                            this.cameraY = newCameraY;
////                        }
////                    } else {
//                        this.camera.position.x = newCameraX;
//                        this.camera.position.y = newCameraY;
////                    }
//                    if (Math.abs(velX) < 0.01) velX = 0.0f;
//                    if (Math.abs(velY) < 0.01) velY = 0.0f;
////                    if (velX == 0.0 && velY == 0.0) {
////                        flinging = false;
////                    }
////                    Gdx.app.log("CameraController::update()", "-- newCameraX:" + newCameraX + " newCameraY:" + newCameraY);
//                }
            }
            camera.update();
        } catch (Exception exp) {
            Gdx.app.error("CameraController::update()", "-- Exception:" + exp);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("MyGestureDetector::keyDown()", "-- keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
//        Gdx.app.log("MyGestureDetector::keyUp()", "-- keycode:" + keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
//        Gdx.app.log("MyGestureDetector::keyTyped()", "-- character:" + character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("MyGestureDetector::touchDown()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
//        flinging = false;
//        initialScale = camera.zoom;

        if (!gameInterface.interfaceTouched) {
            UnderConstruction underConstruction = gameField.getUnderConstruction();
            Vector3 touch = new Vector3(screenX, screenY, 0.0f);
            if (underConstruction != null) {
                if (button == 0) {
                    if (whichCell(touch, isDrawableTowers)) {
                        underConstruction.setStartCoors((int) touch.x, (int) touch.y);
                    }
                } else if (button == 1) {
                    gameField.cancelUnderConstruction();
//                    gameField.removeTower((int) touch.x, (int) touch.y);
                }
            }
        }
        return false;
    }

//    boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        Gdx.app.log("CameraController::touchDown()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
//        this.prevMouseX = screenX;
//        this.prevMouseY = screenY;
//        this.paning = true;
//        this.flinging = false;
//        Gdx.app.log("CameraController::touchDown()", "-- cameraX:" + cameraX + " cameraY:" + cameraY);
//    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("MyGestureDetector::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
        if (!gameInterface.interfaceTouched) {
            Vector3 touch = new Vector3(screenX, screenY, 0.0f);
            if (gameField.getUnderConstruction() != null) {
                if (button == 0) {
                    if (whichCell(touch, isDrawableTowers)) {
                        gameField.buildTowersWithUnderConstruction((int) touch.x, (int) touch.y);
                    }
                }
            }

        }
        gameInterface.interfaceTouched = false;
        return false;
    }

//    boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        Gdx.app.log("CameraController::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
////    this.cameraX = screenX;
////    this.cameraY = screenY;
//        if (paning) {
//            this.paning = false;
////        fling((float)(screenX)-(prevMouseX), (float)(screenY)-(prevMouseY), button);
//        }
//        Gdx.app.log("CameraController::touchUp()", "-- cameraX:" + cameraX + " cameraY:" + cameraY);
//    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        Gdx.app.log("MyGestureDetector::touchDragged()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer);
        if (gameField != null && gameField.getUnderConstruction() != null) {
            Vector3 touch = new Vector3(screenX, screenY, 0.0f);
            if (whichCell(touch, isDrawableTowers)) {
                gameField.getUnderConstruction().setEndCoors((int)touch.x, (int)touch.y);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (gameField != null && gameField.getUnderConstruction() != null) {
            Vector3 touch = new Vector3(screenX, screenY, 0.0f);
            Gdx.app.log("GameScreen::mouseMoved()", "-window- screenX:" + screenX + " screenY:" + screenY);
            if (whichCell(touch, isDrawableTowers)) {
                gameField.getUnderConstruction().setEndCoors((int)touch.x, (int)touch.y);
                Gdx.app.log("GameScreen::mouseMoved()", "-cell- cellCoordinate.x:" + touch.x + " cellCoordinate.y:" + touch.y);
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        Gdx.app.log("MyGestureDetector::scrolled()", "-- amount:" + amount);
        if (gameInterface.scrolled(amount)) {
            return false;
        }
        if (amount == 1) {
            if (camera.zoom <= zoomMax)
                camera.zoom += 0.1f;
        } else if (amount == -1) {
            if (camera.zoom >= zoomMin)
                camera.zoom -= 0.1f;
        }
        camera.update();
        return false;
    }

//    boolean scrolled(int amount) {
//        Gdx.app.log("CameraController::scrolled()", "-- amount:" + amount);
//        if (amount > 0) {
//            if (zoom <= zoomMax) {
//                zoom += 0.1;
//            }
//        } else if (amount < 0) {
//            if (zoom >= zoomMin) {
//                zoom -= 0.1;
//            }
//        }
////    sizeCellX = defSizeCellX*zoom;
////    sizeCellY = defSizeCellY*zoom;
////    halfSizeCellX = sizeCellX/2;
////    halfSizeCellY = sizeCellY/2;
//        Gdx.app.log("CameraController::scrolled()", "-- zoom:" + zoom);
//        Gdx.app.log("CameraController::scrolled()", "-- sizeCellX:" + sizeCellX);
//        Gdx.app.log("CameraController::scrolled()", "-- sizeCellY:" + sizeCellY);
//        Gdx.app.log("CameraController::scrolled()", "-- halfSizeCellX:" + halfSizeCellX);
//        Gdx.app.log("CameraController::scrolled()", "-- halfSizeCellY:" + halfSizeCellY);
//    }

//    void unproject(int &screenX, int &screenY) {
////    Gdx.app.log("CameraController::unproject()", "-- screenX:" + screenX + " screenY:" + screenY + " cameraX:" + cameraX + " cameraY:" + cameraY);
//        screenX -= (cameraX*zoom);
//        screenY -= (cameraY*zoom);
////    Gdx.app.log("CameraController::unproject()", "-- screenX:" + screenX + " screenY:" + screenY + " cameraX:" + cameraX + " cameraY:" + cameraY);
//    }

//QPoint unproject(QPoint screenCoords) {
//    Gdx.app.log("CameraController::unproject()", "-- screenCoords:" + screenCoords);
//    return unproject(screenCoords, 0, 0, this.viewportWidth, this.viewportHeight);
//}

//QPoint unproject(QPoint screenCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
//    Gdx.app.log("CameraController::update()", "-- viewportX:" + viewportX + " viewportY:" + viewportY);
//    Gdx.app.log("CameraController::update()", "-- viewportWidth:" + viewportWidth + " viewportHeight:" + viewportHeight);
//    float x = screenCoords.x, y = screenCoords.y;
//    x = x - viewportX;
//    y = Gdx.graphics.getHeight() - y - 1;
//    y = y - viewportY;
//    screenCoords.x = (2 * x) / viewportWidth - 1;
//    screenCoords.y = (2 * y) / viewportHeight - 1;
//    screenCoords.z = 2 * screenCoords.z - 1;
//    screenCoords.prj(invProjectionView);
//    return screenCoords;
//}

//boolean whichCell(int &mouseX, int &mouseY) {
//    int mainCoorMapX = cameraX;
//    int mainCoorMapY = cameraY;
//    int gameX, gameY;
////    if(!field.isometric) {
////        gameX = ( (mouseX+sizeCell - mainCoorMapX) / sizeCell);
////        gameY = ( (mouseY+sizeCell - mainCoorMapY) / sizeCell);
////    } else {
//        int isometricCoorX = 0;//(sizeCellX/2) * mapHeight;
//        int isometricCoorY = 0;
//        int localMouseX = +mainCoorMapX + mouseX - isometricCoorX;
//        int localMouseY = +mainCoorMapY + mouseY + sizeCellY;
//        gameX = (localMouseX/2 + localMouseY) / (sizeCellX/2);
//        gameY = -(localMouseX/2 - localMouseY) / (sizeCellX/2);
////    }
//    if(gameX > 0 && gameX < mapWidth+1) {
//        if(gameY > 0 && gameY < mapHeight+1) {
//            Gdx.app.log("CameraController::whichCell(); -graphics- mouseX:" + mouseX + " mouseY:" + mouseY + " -new- gameX:" + gameX-1 + " gameY:" + gameY-1);
//            mouseX = gameX-1;
//            mouseY = gameY-1;
//            return true;
//        }
//    }
//    return false;
//}

    boolean whichCell(Vector3 mouse, int map) {
    Gdx.app.log("CameraController::whichCell()", "-wind- mouseX:" + mouse.x + " mouseY:" + mouse.y);
        camera.unproject(mouse);
    Gdx.app.log("CameraController::whichCell()", "-grph- mouseX:" + mouse.x + " mouseY:" + mouse.y);
        float gameX = ( (mouse.x / (halfSizeCellX)) + (mouse.y / (halfSizeCellY)) ) / 2;
        float gameY = ( (mouse.y / (halfSizeCellY)) - (mouse.x / (halfSizeCellX)) ) / 2;
    Gdx.app.log("CameraController::whichCell()", "-graphics- mouseX:" + mouse.x + " mouseY:" + mouse.y + " map:" + map + " -new- gameX:" + gameX + " gameY:" + gameY);
        int cellX = Math.abs((int) gameX);
        int cellY = Math.abs((int) gameY);
        if(gameY < 0) {
            int tmpX = cellX;
            cellX = cellY;
            cellY = tmpX;
        } // Где то я накосячил. мб сделать подругому.
        // если это убирать то нужно будет править Cell::setGraphicCoordinates() для 3 и 4 карты-java // c++ ?? or ??
        mouse.x = cellX;
        mouse.y = cellY;
    Gdx.app.log("CameraController::whichCell()", "-cell- cellX:" + cellX + " cellY:" + cellY);
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
            towerPos.x += (-(halfSizeCellX * towerSize) );
            towerPos.y += (-(halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))));
        } else if(map == 2) {
            towerPos.x += (-(halfSizeCellX * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
            towerPos.y += (-(halfSizeCellY * towerSize));
        } else if(map == 3) {
            towerPos.x += (-(halfSizeCellX * towerSize) );
            towerPos.y += (-(halfSizeCellY * ((towerSize % 2 != 0) ? towerSize : towerSize+1)));
        } else if(map == 4) {
            towerPos.x += (-(halfSizeCellX * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
            towerPos.y += (-(halfSizeCellY * towerSize));
        } else {
            Gdx.app.log("GameField::getCorrectGraphicTowerCoord(" + towerPos + ", " + towerSize + ", " + map + ")", "-- Bad map[1-4] value:" + map);
            return false;
        }
        return true;
    }

//    boolean getCorrectGraphicTowerCoord(Vector2 towerPos, int towerSize, int map) {
////    Gdx.app.log("CameraController::getCorrectGraphicTowerCoord()", "-- towerSize:" + towerSize + " qweqwe:" + (towerSize - ((towerSize % 2 != 0) ? 0 : 1));
//        if(map == 1) {
//            towerPos.x -= ( (halfSizeCellX * towerSize) );
//            towerPos.y -= ( (halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
//        } else if(map == 2) {
//            towerPos.x -= ( (halfSizeCellX * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
//            towerPos.y -= ( (halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
//        } else if(map == 3) {
//            towerPos.x -= ( (halfSizeCellX * towerSize) );
//            towerPos.y -= ( (halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
//        } else if(map == 4) {
//            towerPos.x -= ( (halfSizeCellX * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
//            towerPos.y -= ( (halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
//        } else {
//            Gdx.app.log("CameraController::getCorrectGraphicTowerCoord(" + towerPos + ", " + towerSize + ", " + map + ")", "-- Bad map[1-4] value:" + map);
//            return false;
//        }
//        return true;
//    }

//    public boolean getCenterGraphicCoord(int cellX, int cellY, int map, Vector2 vectorPos) {
//        if (vectorPos != null) {
////        float pxlsX = 0f, pxlsY = 0f;
////        float offsetX = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellX) : ( (templateForTower.size == 1) ? 0 : (templateForTower.size-1)*halfSizeCellX));
////        float offsetY = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellY) : ( (templateForTower.size == 1) ? 0 : (templateForTower.size-1)*halfSizeCellY));
//////        float offsetX = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellX) : (templateForTower.size-1)*halfSizeCellX);
//////        float offsetY = ((templateForTower.size%2 == 0) ? (templateForTower.size*halfSizeCellY) : (templateForTower.size-1)*halfSizeCellY);
//            if (map == 1) {
//                vectorPos.x = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX) );
//                vectorPos.y = (-(halfSizeCellY * cellY) - (cellX * halfSizeCellY) ) - halfSizeCellY;
//            } else if (map == 2) {
//                vectorPos.x = ( (halfSizeCellX * cellY) + (cellX * halfSizeCellX) ) + halfSizeCellX;
//                vectorPos.y = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY) );
//            } else if (map == 3) {
//                vectorPos.x = (-(halfSizeCellX * cellY) + (cellX * halfSizeCellX) );
//                vectorPos.y = ( (halfSizeCellY * cellY) + (cellX * halfSizeCellY) ) + halfSizeCellY;
//            } else if (map == 4) {
//                vectorPos.x = (-(halfSizeCellX * cellY) - (cellX * halfSizeCellX) ) - halfSizeCellX;
//                vectorPos.y = ( (halfSizeCellY * cellY) - (cellX * halfSizeCellY) );
//            } else {
//                Gdx.app.log("CameraController::getCenterGraphicCoord(" + cellX + ", " + cellY + ", " + vectorPos + ")", "-- Bad map[1-4] value:" + map);
//                return false;
//            }
//            return true;
//        }
//        Gdx.app.log("CameraController::getCenterGraphicCoord(" + cellX + ", " + cellY + ", " + vectorPos + ")", "-- Bad vectorPos:" + vectorPos);
//        return false;
////        return new Vector2(pxlsX - halfSizeCellX, pxlsY + halfSizeCellY*templateForTower.size);
//    } // -------------------------------------------------------------- TODD It is analog GameField::getGraphicCoordinates() func!

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Camera:[");
        sb.append("cameraX:" + camera.position.x);
        sb.append(",cameraY:" + camera.position.y);
        sb.append(",sizeCellX:" + sizeCellX);
        sb.append(",sizeCellY:" + sizeCellY);
        sb.append(",zoom:" + zoom);
        sb.append(",zoomMax:" + zoomMax);
        sb.append(",zoomMin:" + zoomMin);
//        sb.append(",borderLeftX:" + borderLeftX);
//        sb.append(",borderRightX:" + borderRightX);
//        sb.append(",borderUpY:" + borderUpY);
//        sb.append(",borderDownY:" + borderDownY);
        sb.append("]");
        return sb.toString();
    }
}
