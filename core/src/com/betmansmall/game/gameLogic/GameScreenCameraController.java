package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.betmansmall.enums.GameType;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.utils.logging.Logger;

public class GameScreenCameraController extends CameraController {
    public GameScreenCameraController(GameScreen gameScreen) {
        super(gameScreen);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);
        if (camera != null) {
            if (!gameInterface.interfaceTouched) {
                UnderConstruction underConstruction = gameField.getUnderConstruction();
                if (underConstruction != null) {
                    if (button == 0) {
                        Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                        if (whichCell(touch, isDrawableTowers)) {
                            underConstruction.setStartCoors((int) touch.x, (int) touch.y);
                        }
                    } else if (button == 1) {
                        gameField.cancelUnderConstruction();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        super.touchDragged(screenX, screenY, pointer);
        if (gameField != null) {
            UnderConstruction underConstruction = gameField.getUnderConstruction();
            if (underConstruction != null) {
                Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                if (whichCell(touch, isDrawableTowers)) {
                    underConstruction.setEndCoors((int) touch.x, (int) touch.y);
                }
            }

            if (underConstruction == null || Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                super.touchDragged(screenX, screenY, pointer);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (gameInterface != null) {
            if (paning) {
                if (((panLeftMouseButton && button == 0) ||
                        (panRightMouseButton && button == 1) ||
                        (panMidMouseButton && button == 2))) {
                    paning = false;
                }
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
                    if ((touchDownX == screenX && touchDownY == screenY) /*|| (prevCellX == tmpCellX && prevCellY == tmpCellY)*/) {
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
                                            cell.setTerrain(tmxMap.getTileSets().getTileSet(0).getTile(randNumber), true, true);
                                        }
                                    }
                                }
                            } else if (button == 2) {
                                if (whichCell(touch, isDrawableUnits)) {
                                    if (random.nextInt(5) == 0) {
                                        gameField.spawnLocalHero((int) touch.x, (int) touch.y);
                                    } else {
                                        gameField.spawnServerUnitToRandomExit((int) touch.x, (int) touch.y);
                                    }
                                }
                            }
                        } else if (gameField.gameSettings.gameType == GameType.TowerDefence) {
                            if (button == 0 || button == 1) {
                                if (whichCell(touch, isDrawableTowers)) {
                                    gameScreen.createTower((int) touch.x, (int) touch.y);
                                }
                            } else if (button == 2) {
                                if (whichCell(touch, isDrawableUnits)) {
                                    gameField.spawnServerUnitToRandomExit((int) touch.x, (int) touch.y);
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
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        super.longPress(x, y);
//        if (!gameInterface.interfaceTouched) {
        Vector3 touch = new Vector3(x, y, 0.0f);
        whichCell(touch, isDrawableTowers);
        if (random.nextBoolean()) {
            gameScreen.createTower((int) touch.x, (int) touch.y);
        } else {
            if (random.nextInt(5) == 0 && gameField.gameSettings.gameType == GameType.LittleGame) {
                gameField.spawnLocalHero((int) touch.x, (int) touch.y);
            } else {
                gameField.spawnServerUnitToRandomExit((int) touch.x, (int) touch.y);
            }
        }
//        }
        return false;
    }

    @Override
    public void update(float deltaTime) {
        try {
            if (gameField.getUnderConstruction() == null) {
                super.update(deltaTime);
            }
            camera.update();
        } catch (Exception exp) {
            Logger.logError("exp:" + exp);
        }
    }
}
