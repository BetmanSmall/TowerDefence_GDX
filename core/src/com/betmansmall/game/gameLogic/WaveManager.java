package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
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

    public Array<Wave> waves;

    public GridPoint2 lastExitPoint;

    WaveManager() {
        this.waves = new Array<Wave>();
    }

    public void addWave(Wave wave) {
        this.waves.add(wave);
    }

    public Array<TemplateNameAndPoints> getAllCreepsForSpawn(float delta) {
        Array<TemplateNameAndPoints> allCreepsForSpawn = new Array<TemplateNameAndPoints>();
        for (Wave wave : waves) {
            if(!wave.actions.isEmpty()) {
                String templateName = wave.getTemplateNameForSpawn(delta);
                if (templateName != null) {
                    allCreepsForSpawn.add(new TemplateNameAndPoints(templateName, wave.spawnPoint, wave.exitPoint));
                }
            } else {
                waves.removeValue(wave, true);
            }
        }
        return allCreepsForSpawn;
    }

    public Array<GridPoint2> getAllSpawnPoint() {
        Array<GridPoint2> points = new Array<GridPoint2>();
        for (Wave wave : waves) {
            points.add(wave.spawnPoint);
        }
        return points;
    }

    public Array<GridPoint2> getAllExitPoint() {
        Array<GridPoint2> points = new Array<GridPoint2>();
        for (Wave wave : waves) {
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
        return actions;
    }

//    public int getNumberOfCreeps() // need implement

    public void validationPoints(Cell[][] field) {
        if(field != null) {
            int sizeFieldX = field.length;
            int sizeFieldY = field[0].length;
            int wavesCount = waves.size;
            for (int w = 0; w < waves.size; w++) {
                Gdx.app.log("WaveManager::validationPoints(" + sizeFieldX + "," + sizeFieldY + ")", "-- wavesCount:(" + wavesCount + ":" + waves.size + ")");
                Wave wave = waves.get(w);
                GridPoint2 spawnPoint = wave.spawnPoint;
                GridPoint2 exitPoint = wave.exitPoint;
                Gdx.app.log("WaveManager::validationPoints()", "-- spawnPoint:" + spawnPoint + " exitPoint:" + exitPoint);
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
            Gdx.app.log("WaveManager::validationPoints(" + sizeFieldX + "," + sizeFieldY + ")", "-- wavesCount:(" + wavesCount + ":" + waves.size + ")");
        }
    }

    public void checkRoutes(PathFinder pathFinder) {
        Gdx.app.log("WaveManager::checkRoutes(" + pathFinder + ")", "--");
        if(pathFinder != null) {
            int wavesCount = waves.size;
            for (int w = 0; w < waves.size; w++) {
                Gdx.app.log("WaveManager::checkRoutes()", "-- wavesCount:(" + wavesCount + ":" + waves.size + ")");
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
            Gdx.app.log("WaveManager::checkRoutes()", "-- wavesCount:(" + wavesCount + ":" + waves.size + ")");
        } else {
            Gdx.app.log("WaveManager::checkRoutes()", "-- pathFinder == null");
        }
    }
}
