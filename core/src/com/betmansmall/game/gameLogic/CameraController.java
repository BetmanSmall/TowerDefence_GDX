package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameInterface.GameInterface;
import com.betmansmall.enums.GameType;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.screens.GameAutoTileScreen;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.screens.menu.MapEditorScreen;
import com.betmansmall.utils.logging.Logger;
import org.lwjgl.input.Mouse;
import java.util.Random;

public class CameraController extends AbstractCameraController {
    public GameScreen gameScreen;
    public GameField gameField;
    public TmxMap tmxMap;
    public GameInterface gameInterface;

    public OrthographicCamera camera;
    public int mapWidth, mapHeight;

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

    public float zoomMax = 5.0f;
    public float zoomMin = 0.1f;
    public float sizeCellX, sizeCellY;
    public float halfSizeCellX, halfSizeCellY;
    protected float borderLeftX = 0.0f, borderRightX = 0.0f;
    protected float borderUpY = 0.0f, borderDownY = 0.0f;

    public boolean panLeftMouseButton = true;
    public boolean panMidMouseButton = true;
    public boolean panRightMouseButton = true;
    public boolean paning = false;
    public int touchDownX, touchDownY;
    public int prevMouseX, prevMouseY;
    protected Random random;

    float space = 50f;
    float shiftCamera = 5f;
    private float limitSpeedBorder = 15;

    public TemplateForTower templateForTower;

    public CameraController(Screen screen) {
        Logger.logFuncStart("screen:" + screen);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(185, -110, 0);
        camera.zoom = 0.5f;
        if (screen instanceof GameScreen) {
            this.gameScreen = (GameScreen) screen;
            this.gameField = gameScreen.gameField;
            this.tmxMap = gameField.tmxMap;
            this.gameInterface = gameScreen.gameInterface;
            this.mapWidth = tmxMap.width;
            this.mapHeight = tmxMap.height;
            this.sizeCellX = tmxMap.tileWidth;
            this.sizeCellY = tmxMap.tileHeight;
            this.halfSizeCellX = sizeCellX / 2;
            this.halfSizeCellY = sizeCellY / 2;
        } else if (screen instanceof MapEditorScreen) {
            tmxMap = ((MapEditorScreen) screen).tmxMap;
            gameInterface = ((MapEditorScreen) screen).mapEditorInterface;
        } else if (screen instanceof GameAutoTileScreen) {
            tmxMap = ((GameAutoTileScreen) screen).tmxMap;
            camera.position.set(515, 255, 0);
            camera.zoom = 1.05f;
            zoomMax = 10f;
        } else {
            Logger.logError("screen!=GameScreen");
        }
        random = new Random();
//        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void dispose() {
        Gdx.app.log("CameraController::dispose()", "--");

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
        if (camera != null) {
            this.touchDownX = screenX;
            this.touchDownY = screenY;
            this.prevMouseX = screenX;
            this.prevMouseY = screenY;
            if (((panLeftMouseButton && button == 0) ||
                    (panRightMouseButton && button == 1) ||
                    (panMidMouseButton && button == 2))) {
//                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
//                Gdx.graphics.setCursor(Cursor.SystemCursor.Arrow);
//                setCursor(Qt::ClosedHandCursor);
                paning = true;
            }
            flinging = false;
            initialScale = camera.zoom;
            selectObject(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (gameInterface != null && !gameInterface.interfaceTouched) {
            if (paning) {
                if (((panLeftMouseButton && button == 0) ||
                        (panRightMouseButton && button == 1) ||
                        (panMidMouseButton && button == 2))) {
                    paning = false;
                }
            }
            gameInterface.interfaceTouched = false;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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
        return super.touchDragged(screenX, screenY, pointer);
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
        if (camera != null) {
            if (amount > 0) {
                if (camera.zoom <= zoomMax)
                    camera.zoom += 0.1f;
            } else if (amount < 0) {
                if (camera.zoom >= zoomMin)
                    camera.zoom -= 0.1f;
            }
            camera.update();
        }
//        Logger.logFuncEnd("camera.zoom:" + camera.zoom);
        return false;
    }

    public void update(float deltaTime) {
        if (flinging) {
            velX *= 0.98f;
            velY *= 0.98f;
//            float newCameraX = camera.position.x - velX;
//            float newCameraY = camera.position.y + velY;
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
//            Logger.logDebug("velX:" + velX + " velY:" + velY);
//            Logger.logDebug("newCameraX:" + newCameraX + " newCameraY:" + newCameraY);
        }

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        if(shiftCamera < limitSpeedBorder){
            shiftCamera += deltaTime * 10f;
        }

        if (mouseX <= space) {
            camera.position.add(-shiftCamera, 0.0f, 0.0f);
        }
        if (mouseX >= camera.viewportWidth - space) {
            camera.position.add(shiftCamera, 0.0f, 0.0f);
        }
        if (mouseY <= space) {
            camera.position.add(0.0f, shiftCamera, 0.0f);
        }
        if (mouseY >= camera.viewportHeight - space) {
            camera.position.add(0.0f, -shiftCamera, 0.0f);
        }
        if (mouseX >= space && mouseX < camera.viewportWidth - space && mouseY >= space && mouseY < camera.viewportHeight - space) {
            shiftCamera = 0;
        }
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            if (!Mouse.isInsideWindow()) {
                shiftCamera = 0f;
            }
        }
        camera.update();
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
    private void selectObject(final int screenX, final int screenY) {
        Vector3 vector = new Vector3(screenX, screenY, 0);
        if (!whichCell(vector, 5)) return;
        if (gameField != null) {
            Tower tower = gameField.getCell((int) vector.x, (int) vector.y).tower;
            if (tower != null) {
                gameField.towersManager.selectTower(gameScreen.playersManager.getLocalPlayer(), tower);
            }
        }
    }

    /**
     * Calculates coordinates of touched cell, returning them in given vector.
     * Returns false, touch was out of map.
     */
    protected boolean whichCell(Vector3 mouse, int map) {
//        Logger.logDebug("-1- mouseX:" + mouse.x + " mouseY:" + mouse.y);
        if (camera != null && tmxMap != null) {
            camera.unproject(mouse);
//            Logger.logDebug("-2- mouseX:" + mouse.x + " mouseY:" + mouse.y);
            float gameX = ((mouse.x / (halfSizeCellX)) + (mouse.y / (halfSizeCellY))) / 2;
            float gameY = ((mouse.y / (halfSizeCellY)) - (mouse.x / (halfSizeCellX))) / 2;
//            Logger.logDebug("-3- gameX:" + gameX + " gameY:" + gameY);
//            Logger.logDebug("tmxMap:" + tmxMap);
            if (!tmxMap.isometric) {
                gameX = (mouse.x / sizeCellX);
                gameY = (mouse.y / sizeCellY);
            }
            int cellX = Math.abs((int) gameX);
            int cellY = Math.abs((int) gameY);
            if (tmxMap.isometric && gameY < 0) {
                int tmpX = cellX;
                cellX = cellY;
                cellY = tmpX;
            } // Где то я накосячил. мб сделать подругому.
            // если это убирать то нужно будет править Cell::setGraphicCoordinates() для 3 и 4 карты-java // c++ ?? or ??
            mouse.x = cellX;
            mouse.y = cellY;
            if (cellX < mapWidth && cellY < mapHeight) {
                if (map == 5) {
                    return true;
                } else {
                    if ((map == 2 && gameX > 0 && gameY < 0)
                            || (map == 3 && gameX > 0 && gameY > 0)) {
                        return true;
                    } else if ((map == 4 && gameX < 0 && gameY > 0)
                            || (map == 1 && gameX < 0 && gameY < 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean getCorrectGraphicTowerCoord(Vector2 towerPos, int towerSize, int map) {
        if(map == 1) {
            if (!tmxMap.isometric) {
                towerPos.x += (-(halfSizeCellX * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))));
            } else {
                towerPos.x += (-(halfSizeCellX * towerSize) );
            }
            towerPos.y += (-(halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
        } else if(map == 2) {
            towerPos.x += (-(halfSizeCellX * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
            if (!tmxMap.isometric) {
                towerPos.y += (-(halfSizeCellY * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))));
            } else {
                towerPos.y += (-(halfSizeCellY * towerSize));
            }
        } else if(map == 3) {
            if (!tmxMap.isometric) {
                towerPos.x += (-(halfSizeCellX * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
            } else {
                towerPos.x += (-(halfSizeCellX * towerSize) );
            }
            towerPos.y += (-(halfSizeCellY * ((towerSize % 2 != 0) ? towerSize : towerSize+1)) );
        } else if(map == 4) {
            towerPos.x += (-(halfSizeCellX * (towerSize - ((towerSize % 2 != 0) ? 0 : 1))) );
            if (!tmxMap.isometric) {
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
