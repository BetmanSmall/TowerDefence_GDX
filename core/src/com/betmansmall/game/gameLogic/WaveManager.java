package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

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

    public int getNumberOfCreeps() { // not creeps | actions
        int creeps = 0;
        for (Wave wave : waves) {
            creeps += wave.actions.size();
        }
        return creeps;
    }

    public void validationPoints(Cell[][] field) {
        if(field != null) {
            int sizeFieldY = field.length;
            int sizeFieldX = field[0].length;
            int wavesCount = waves.size;
            for (int w = 0; w < waves.size; w++) {
                Gdx.app.log("WaveManager::validationPoints(" + sizeFieldX + "," + sizeFieldY + ");", " -- wavesCount:(" + wavesCount + ":" + waves.size + ")");
                Wave wave = waves.get(w);
                GridPoint2 spawnPoint = wave.spawnPoint;
                GridPoint2 exitPoint = wave.exitPoint;
                Gdx.app.log("WaveManager::validationPoints();", " -- spawnPoint:" + spawnPoint + " exitPoint:" + exitPoint);
                if (spawnPoint == null || spawnPoint.x < 0 || spawnPoint.x >= sizeFieldX || spawnPoint.y < 0 || spawnPoint.y >= sizeFieldY || !field[spawnPoint.x][spawnPoint.y].isPassable()) {
                    Gdx.app.log("GameField", "validationPoints(); -- SpawnPoint bad:" + spawnPoint + " wave:" + wave);
                    waves.removeValue(wave, true);
                    w--;
                } else if (exitPoint == null || exitPoint.x < 0 || exitPoint.x >= sizeFieldX || exitPoint.y < 0 || exitPoint.y >= sizeFieldY || !field[exitPoint.x][exitPoint.y].isPassable()) {
                    Gdx.app.log("GameField", "validationPoints(); -- ExitPoint bad:" + exitPoint + " wave:" + wave);
                    waves.removeValue(wave, true);
                    w--;
                }
            }
            Gdx.app.log("WaveManager::validationPoints(" + sizeFieldX + "," + sizeFieldY + ");", " -- wavesCount:(" + wavesCount + ":" + waves.size + ")");
        }
    }
}
