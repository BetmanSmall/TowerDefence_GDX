package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class WaveManager {
    class TemplateNameAndPoints {
        public String templateName;
        public GridPoint2 spawnPoint;
        public GridPoint2 exitPoint;

        TemplateNameAndPoints(String templateName, GridPoint2 spawnPoint, GridPoint2 exitPoint) {
            this.templateName = templateName;
            this.spawnPoint = spawnPoint;
            this.exitPoint = exitPoint;
        }
    }

    public boolean allTogether;
    public Wave currentWave;
    public Array<Wave> waves;
    public Array<Wave> wavesForUser;
    public GridPoint2 lastExitPoint;
    public float waitForNextSpawnUnit;

    WaveManager() {
        Gdx.app.log("WaveManager::WaveManager()", "--");
//        this.allTogether = true;
        this.currentWave = null;
        this.waves = new Array<Wave>();
        this.wavesForUser = new Array<Wave>();
//        this.waitForNextSpawnUnit =
    }

    public void dispose() {
        Gdx.app.log("WaveManager::dispose()", "--");
    }

    public void addWave(Wave wave) {
        this.waves.add(wave);
    }

    public boolean updateCurrentWave() {
        if (waves.size != 0) {
            Wave newWave = waves.first();
            if (newWave != null) {
                waves.removeIndex(0);
                currentWave = newWave;
                return true;
            }
        }
        return false;
    }

    public TemplateNameAndPoints getUnitForSpawn(float delta) {
        waitForNextSpawnUnit -= delta;
        if (currentWave != null) {
            if (!currentWave.actions.isEmpty()) {
                String templateName = currentWave.getTemplateNameForSpawn(delta);
                if (templateName != null) {
                    if (templateName.contains("wait")) {
                        waitForNextSpawnUnit = Float.parseFloat(templateName.substring(templateName.indexOf("=") + 1, templateName.length()));// GOVNE GODE parseFloat3
                    } else {
                        return (new TemplateNameAndPoints(templateName, currentWave.spawnPoint, currentWave.exitPoint));
                    }
                }
            } else {
//                Gdx.app.log("WaveManager::getUnitForSpawn()", "waves.removeValue(currentWave, true):" + waves.removeValue(currentWave, true));
                currentWave = null;
            }
        }
        return null;
    }

    public Array<TemplateNameAndPoints> getAllUnitsForSpawn(float delta) {
        waitForNextSpawnUnit -= delta;
        Array<TemplateNameAndPoints> allUnitsForSpawn = new Array<TemplateNameAndPoints>();
        for (Wave wave : waves) {
            if(!wave.actions.isEmpty()) {
                String templateName = wave.getTemplateNameForSpawn(delta);
                if (templateName != null) {
                    if (templateName.contains("wait")) {
                        waitForNextSpawnUnit = Float.parseFloat(templateName.substring(templateName.indexOf("=") + 1, templateName.length()));// GOVNE GODE parseFloat3
                        // bitch naxyui =( || but work mb =)
                    } else {
                        allUnitsForSpawn.add(new TemplateNameAndPoints(templateName, wave.spawnPoint, wave.exitPoint));
                    }
                }
            } else {
                waves.removeValue(wave, true);
            }
        }
        return allUnitsForSpawn;
    }

    public Array<GridPoint2> getAllSpawnPoint() {
        Array<GridPoint2> points = new Array<GridPoint2>();
        for (Wave wave : waves) {
            points.add(wave.spawnPoint);
        }
        for (Wave wave : wavesForUser) {
            points.add(wave.spawnPoint);
        }
        return points;
    }

    public Array<GridPoint2> getAllExitPoint() {
        Array<GridPoint2> points = new Array<GridPoint2>();
        for (Wave wave : waves) {
            points.add(wave.exitPoint);
        }
        for (Wave wave : wavesForUser) {
            points.add(wave.exitPoint);
        }
        if (lastExitPoint != null) {
            points.add(lastExitPoint);
        }
        return points;
    }

    public boolean setExitPoint(GridPoint2 exitPoint) {
        this.lastExitPoint = exitPoint;
        if (waves.size != 0) {
            waves.first().exitPoint = exitPoint;
            return true;
        }
        return false;
    }

    public int getNumberOfActions() {
        int actions = 0;
        for (Wave wave : waves) {
            actions += wave.actions.size();
        }
        if (currentWave != null) {
            actions += currentWave.actions.size();
        }
        return actions;
    }

//    public int getNumberOfUnits() // need implement

    public void validationPoints(Cell[][] field, int sizeFieldX, int sizeFieldY) {
        Gdx.app.log("WaveManager::validationPoints(" + field + ")", "--");
        if(field != null) {
            int wavesSize = waves.size;
            if (wavesSize != 0) {
                Gdx.app.log("WaveManager::validationPoints()", "-- sizeField:(" + sizeFieldX + ", " + sizeFieldY + ") waves:(" + wavesSize + ":" + waves.size + ") - before");
                for (int w = 0; w < waves.size; w++) {
                    Wave wave = waves.get(w);
                    GridPoint2 spawnPoint = wave.spawnPoint;
                    GridPoint2 exitPoint = wave.exitPoint;
                    Gdx.app.log("WaveManager::validationPoints()", "-- spawnPoint:" + spawnPoint + " exitPoint:" + exitPoint + " wave:" + wave);
                    if (spawnPoint == null || spawnPoint.x < 0 || spawnPoint.x >= sizeFieldX || spawnPoint.y < 0 || spawnPoint.y >= sizeFieldY || !field[spawnPoint.x][spawnPoint.y].isPassable()) {
                        Gdx.app.log("WaveManager::validationPoints()", "-- SpawnPoint bad:" + spawnPoint + " wave:" + wave);
                        waves.removeValue(wave, true);
                        w--;
                    } else if (exitPoint == null || exitPoint.x < 0 || exitPoint.x >= sizeFieldX || exitPoint.y < 0 || exitPoint.y >= sizeFieldY || !field[exitPoint.x][exitPoint.y].isPassable()) {
                        Gdx.app.log("WaveManager::validationPoints()", "-- ExitPoint bad:" + exitPoint + " wave:" + wave);
                        waves.removeValue(wave, true);
                        w--;
                    }
                }
                Gdx.app.log("WaveManager::validationPoints()", "-- sizeField:(" + sizeFieldX + ", " + sizeFieldY + ") waves:(" + wavesSize + ":" + waves.size + ") - after");
            }
            int wavesForUserSize = wavesForUser.size;
            if (wavesForUserSize != 0) {
                Gdx.app.log("WaveManager::validationPoints()", "-- sizeField:(" + sizeFieldX + ", " + sizeFieldY + ") wavesForUser:(" + wavesForUserSize + ":" + wavesForUser.size + ") - before");
                for (int w = 0; w < wavesForUser.size; w++) {
                    Wave wave = wavesForUser.get(w);
                    GridPoint2 spawnPoint = wave.spawnPoint;
                    GridPoint2 exitPoint = wave.exitPoint;
                    Gdx.app.log("WaveManager::validationPoints()", "-- spawnPoint:" + spawnPoint + " exitPoint:" + exitPoint + " wave:" + wave);
                    if (spawnPoint == null || spawnPoint.x < 0 || spawnPoint.x >= sizeFieldX || spawnPoint.y < 0 || spawnPoint.y >= sizeFieldY || !field[spawnPoint.x][spawnPoint.y].isPassable()) {
                        Gdx.app.log("WaveManager::validationPoints()", "-- SpawnPoint bad:" + spawnPoint + " wave:" + wave);
                        wavesForUser.removeValue(wave, true);
                        w--;
                    } else if (exitPoint == null || exitPoint.x < 0 || exitPoint.x >= sizeFieldX || exitPoint.y < 0 || exitPoint.y >= sizeFieldY || !field[exitPoint.x][exitPoint.y].isPassable()) {
                        Gdx.app.log("WaveManager::validationPoints()", "-- ExitPoint bad:" + exitPoint + " wave:" + wave);
                        wavesForUser.removeValue(wave, true);
                        w--;
                    }
                }
                Gdx.app.log("WaveManager::validationPoints()", "-- sizeField:(" + sizeFieldX + ", " + sizeFieldY + ") wavesForUser:(" + wavesForUserSize + ":" + wavesForUser.size + ") - after");
            }
        }
    }

    public void checkRoutes(PathFinder pathFinder) {
        Gdx.app.log("WaveManager::checkRoutes(" + pathFinder + ")", "--");
        if(pathFinder != null) {
            int wavesSize = waves.size;
            Gdx.app.log("WaveManager::checkRoutes()", "-- waves:(" + wavesSize + ":" + waves.size + ") - before");
            for (int w = 0; w < waves.size; w++) {
                Wave wave = waves.get(w);
                GridPoint2 spawnPoint = wave.spawnPoint;
                GridPoint2 exitPoint = wave.exitPoint;
                Gdx.app.log("WaveManager::checkRoutes()", "-- spawnPoint:" + spawnPoint + " exitPoint:" + exitPoint);
                ArrayDeque<Node> route = pathFinder.route(spawnPoint.x, spawnPoint.y, exitPoint.x, exitPoint.y);
                if (route == null) {
                    Gdx.app.log("WaveManager::checkRoutes()", "-- Not found route for this points | Remove wave:" + wave);
                    waves.removeValue(wave, true);
                    w--;
                } else {
                    wave.route = route;
                }
            }
            Gdx.app.log("WaveManager::checkRoutes()", "-- waves:(" + wavesSize + ":" + waves.size + ") - after");
            int wavesForUserSize = wavesForUser.size;
            Gdx.app.log("WaveManager::checkRoutes()", "-- wavesForUser:(" + wavesForUserSize + ":" + wavesForUser.size + ") - before");
            for (int w = 0; w < wavesForUser.size; w++) {
                Wave wave = wavesForUser.get(w);
                GridPoint2 spawnPoint = wave.spawnPoint;
                GridPoint2 exitPoint = wave.exitPoint;
                Gdx.app.log("WaveManager::checkRoutes()", "-- spawnPoint:" + spawnPoint + " exitPoint:" + exitPoint);
                ArrayDeque<Node> route = pathFinder.route(spawnPoint.x, spawnPoint.y, exitPoint.x, exitPoint.y);
                if (route == null) {
                    Gdx.app.log("WaveManager::checkRoutes()", "-- Not found route for this points | Remove wave:" + wave);
                    wavesForUser.removeValue(wave, true);
                    w--;
                } else {
                    wave.route = route;
                }
            }
            Gdx.app.log("WaveManager::checkRoutes()", "-- wavesForUser:(" + wavesForUserSize + ":" + wavesForUser.size + ") - after");
        } else {
            Gdx.app.log("WaveManager::checkRoutes()", "-- pathFinder == null");
        }
    }

    // Need fix bag with this
//    public void turnRight() {
//        if(sizeFieldX == sizeFieldY) {
//            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
//            for(int y = 0; y < sizeFieldY; y++) {
//                for(int x = 0; x < sizeFieldX; x++) {
//                    newCells[sizeFieldX-y-1][x] = field[x][y];
//                    newCells[sizeFieldX-y-1][x].setGraphicCoordinates(sizeFieldX-y-1, x, halfSizeCellX, halfSizeCellY);
//                }
//            }
//            field = newCells;
//        } else {
//            Gdx.app.log("GameField::turnRight()", "-- Not work || Work, but mb not Good!");
//            int oldWidth = sizeFieldX;
//            int oldHeight = sizeFieldY;
//            sizeFieldX = sizeFieldY;
//            sizeFieldY = oldWidth;
//            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
//            for(int y = 0; y < oldHeight; y++) {
//                for(int x = 0; x < oldWidth; x++) {
//                    newCells[sizeFieldX-y-1][x] = field[x][y];
//                    newCells[sizeFieldX-y-1][x].setGraphicCoordinates(sizeFieldX-y-1, x, halfSizeCellX, halfSizeCellY);
//                }
//            }
//            field = newCells;
//        }
//    }
//
//    public void turnLeft() {
//        if(sizeFieldX == sizeFieldY) {
//            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
//            for(int y = 0; y < sizeFieldY; y++) {
//                for(int x = 0; x < sizeFieldX; x++) {
//                    newCells[y][sizeFieldY-x-1] = field[x][y];
//                    newCells[y][sizeFieldY-x-1].setGraphicCoordinates(y, sizeFieldY-x-1, halfSizeCellX, halfSizeCellY);
//                }
//            }
//            field = newCells;
//        } else {
//            Gdx.app.log("GameField::turnLeft()", "-- Not work || Work, but mb not Good!");
//            int oldWidth = sizeFieldX;
//            int oldHeight = sizeFieldY;
//            sizeFieldX = sizeFieldY;
//            sizeFieldY = oldWidth;
//            Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
//            for(int y = 0; y < oldHeight; y++) {
//                for(int x = 0; x < oldWidth; x++) {
//                    newCells[y][sizeFieldY-x-1] = field[x][y];
//                    newCells[y][sizeFieldY-x-1].setGraphicCoordinates(y, sizeFieldY-x-1, halfSizeCellX, halfSizeCellY);
//                }
//            }
//            field = newCells;
//        }
//    }
//
//    public void flipX() {
//        Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
//        for (int y = 0; y < sizeFieldY; y++) {
//            for (int x = 0; x < sizeFieldX; x++) {
//                newCells[sizeFieldX-x-1][y] = field[x][y];
//                newCells[sizeFieldX-x-1][y].setGraphicCoordinates(sizeFieldX-x-1, y, halfSizeCellX, halfSizeCellY);
//            }
//        }
//        field = newCells;
//    }
//
//    public void flipY() {
//        Cell[][] newCells = new Cell[sizeFieldX][sizeFieldY];
//        for(int y = 0; y < sizeFieldY; y++) {
//            for(int x = 0; x < sizeFieldX; x++) {
//                newCells[x][sizeFieldY-y-1] = field[x][y];
//                newCells[x][sizeFieldY-y-1].setGraphicCoordinates(x, sizeFieldY-y-1, halfSizeCellX, halfSizeCellY);
//            }
//        }
//        field = newCells;
//    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("WaveManager[");
        sb.append("waves.size:" + waves.size);
        if (full) {
            for (Wave wave : waves) {
                sb.append("," + wave);
            }
        }
        sb.append(",wavesForUser.size:" + wavesForUser.size);
        if (full) {
            for (Wave wave : wavesForUser) {
                sb.append("," + wave);
            }
        }
        sb.append(",lastExitPoint:" + lastExitPoint);
        sb.append(",waitForNextSpawnUnit:" + waitForNextSpawnUnit);
        sb.append("]");
        return sb.toString();
    }
}
