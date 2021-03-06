package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.XmlReader;
import com.betmansmall.maps.MapLoader;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.PathFinder;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.utils.logging.Logger;

import java.util.ArrayDeque;
import java.util.Random;

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

    WaveManager(String filePath) {
        Logger.logFuncStart();
        this.allTogether = false;
        this.currentWave = null;
        this.waves = new Array<Wave>();
        this.wavesForUser = new Array<Wave>();
        this.waitForNextSpawnUnit = 0f;
        this.load(filePath);
    }

    private boolean load(String filePath) {
        XmlReader xml = new XmlReader();
        FileHandle tmxFile = Gdx.files.internal(filePath);
        XmlReader.Element root = xml.parse(tmxFile);

        XmlReader.Element waves = root.getChildByName("waves");
        if(waves != null) {
//            String type = waves.getAttribute("type", null);
            String source = waves.getAttribute("source", null);
            if (source != null) {
                FileHandle tsx = MapLoader.getRelativeFileHandle(tmxFile, source);
                XmlReader.Element rootwaves = xml.parse(tsx);
                wavesParser(rootwaves);
//            } else if(type != null/* && type == "empty"*/) { // LOL not WORK
//                System.out.println("type=" + type); // Хотел сделать пустую волну, не получилася=( мб как нить сделаем.
//                waveManager.addWave(new Wave(new GridPoint2(0, 0), new GridPoint2(0, 0), 10f));
            } else {
                wavesParser(waves);
            }
            return true;
        } else {
            Logger.logError("Not found waves block in tmxMap:" + tmxFile);
        }
        return false;
    }

    private void wavesParser(XmlReader.Element waves) {
        this.allTogether = waves.getBoolean("allTogether", false);
        Array<XmlReader.Element> waveElements = waves.getChildrenByName("wave");
        for (XmlReader.Element waveElement : waveElements) {
            int spawnPointX = waveElement.getIntAttribute("spawnPointX");
            int spawnPointY = waveElement.getIntAttribute("spawnPointY");
            int exitPointX = waveElement.getIntAttribute("exitPointX");
            int exitPointY = waveElement.getIntAttribute("exitPointY");
            float spawnInterval = waveElement.getFloat("spawnInterval", 0.0f);
            float startToMove = waveElement.getFloat("startToMove", 0.0f);
            Wave wave = new Wave(new GridPoint2(spawnPointX, spawnPointY), new GridPoint2(exitPointX, exitPointY), startToMove);
            int actionsCount = waveElement.getChildCount();
            for (int a = 0; a < actionsCount; a++) {
                XmlReader.Element action = waveElement.getChild(a);
                String sAction = action.getName();
                if (sAction.equals("unit")) { // mb bad?
                    float delay = action.getFloat("delay", 0.0f);
                    if (delay > 0f) {
                        wave.addAction("delay=" + delay);
                    }
                    String unitTemplateName = action.getAttribute("templateName");

                    float interval = action.getFloat("interval", 0.0f) + spawnInterval;
                    int amount = action.getInt("amount", 0);
                    for (int u = 0; u < amount; u++) {
                        if (interval > 0f) {
                            wave.addAction("interval=" + interval);
                        }
                        wave.addAction(unitTemplateName);
                    }
                }
            }
//            Array<Element> units = waveElement.getChildrenByName("unit");
//            for (Element unit : units) {
//                String unitTemplateName = unit.getAttribute("templateName");
//                int unitsAmount = unit.getIntAttribute("amount");
//                int delay = unit.getIntAttribute("delay", 0);
//                for (int k = 0; k < unitsAmount; k++) {
//                    wave.addTemplateForUnit(unitTemplateName);
//                    wave.addDelayForUnit(delay);
//                }
//            }
            this.addWave(wave);
        }
        Array<XmlReader.Element> waveForUserElements = waves.getChildrenByName("waveForUser");
        for (XmlReader.Element waveElement : waveForUserElements) {
            int spawnPointX = waveElement.getIntAttribute("spawnPointX");
            int spawnPointY = waveElement.getIntAttribute("spawnPointY");
            int exitPointX = waveElement.getIntAttribute("exitPointX");
            int exitPointY = waveElement.getIntAttribute("exitPointY");
            Wave wave = new Wave(new GridPoint2(spawnPointX, spawnPointY), new GridPoint2(exitPointX, exitPointY));
            this.wavesForUser.add(wave);
        }
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

    public void generateWave(GameField gameField, Cell[][] field, TmxMap tmxMap, Random random, PathFinder pathFinder) {
        validationPoints(field, tmxMap.width, tmxMap.height);
        int tryFoundIndex = 0;
        while (waves.size == 0 && tryFoundIndex < 5) {
            tryFoundIndex++;
            for (int w = 0; w < 10; w++) {
                GridPoint2 spawnPoint = new GridPoint2(random.nextInt(tmxMap.width), random.nextInt(tmxMap.height));
                GridPoint2 exitPoint = new GridPoint2(random.nextInt(tmxMap.width), random.nextInt(tmxMap.height));
                Cell spawnCell = gameField.getCell(spawnPoint.x, spawnPoint.y);
                Cell exitCell = gameField.getCell(exitPoint.x, exitPoint.y);
                if (spawnCell != null && spawnCell.isEmpty()) {
                    if (exitCell != null && exitCell.isEmpty()) {
                        Wave wave = new Wave(spawnPoint, exitPoint);
                        for (int t = 0; t < gameField.factionsManager.getServerFaction().getTemplateForUnits().size; t++) {
                            wave.addAction("interval=" + 1);
                            wave.addAction(gameField.factionsManager.getServerFaction().getTemplateForUnits().get(t).templateName);
                        }
                        addWave(wave);
                    }
                }
            }
            checkRoutes(pathFinder);
        }
        Logger.logFuncEnd("tryFoundIndex:" + tryFoundIndex);
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

//    public boolean setExitPoint(GridPoint2 exitPoint) {
//        this.lastExitPoint = exitPoint;
//        if (waves.size != 0) {
//            waves.first().exitPoint = exitPoint;
//            return true;
//        }
//        return false;
//    }

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
        if (pathFinder != null) {
            Logger.logDebug("waves.size:" + waves.size);
            int wavesSize = waves.size;
            Logger.logDebug("waves:(" + wavesSize + ":" + waves.size + ") - before");
            deleteBadWaves(pathFinder, waves);
            Logger.logDebug("waves:(" + wavesSize + ":" + waves.size + ") - after");
            int wavesForUserSize = wavesForUser.size;
            Logger.logDebug("wavesForUser:(" + wavesForUserSize + ":" + wavesForUser.size + ") - before");
            deleteBadWaves(pathFinder, wavesForUser);
            Logger.logDebug("wavesForUser:(" + wavesForUserSize + ":" + wavesForUser.size + ") - after");
        } else {
            Logger.logError("pathFinder == null");
        }
    }

    private void deleteBadWaves(PathFinder pathFinder, Array<Wave> wavesForUser) {
        for (int w = 0; w < wavesForUser.size; w++) {
            Wave wave = wavesForUser.get(w);
            GridPoint2 spawnPoint = wave.spawnPoint;
            GridPoint2 exitPoint = wave.exitPoint;
//                Gdx.app.log("WaveManager::checkRoutes()", "-- spawnPoint:" + spawnPoint + " exitPoint:" + exitPoint);
            ArrayDeque<Node> route = pathFinder.routeWithNode(spawnPoint.x, spawnPoint.y, exitPoint.x, exitPoint.y);
            if (route == null) {
//                    Gdx.app.log("WaveManager::checkRoutes()", "-- Not found route for this points | Remove wave:" + wave);
                wavesForUser.removeValue(wave, true);
                w--;
            } else {
                wave.route = route;
            }
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
