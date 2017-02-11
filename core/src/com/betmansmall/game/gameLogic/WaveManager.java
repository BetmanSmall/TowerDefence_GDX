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

    public GridPoint2 getSpawnPoint() {
        if (waves.size != 0) {
            return waves.first().spawnPoint;
        }
        return null;
    }

    public GridPoint2 getExitPoint() {
        if (waves.size != 0) {
            return waves.first().exitPoint;
        } else if (lastExitPoint != null) {
            return lastExitPoint;
        }
        return null;
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

    public void validationPoints(int sizeFieldX, int sizeFieldY) {
        for(Wave wave : waves) {
            if (wave.spawnPoint == null || wave.spawnPoint.x < 0 || wave.spawnPoint.x >= sizeFieldX || wave.spawnPoint.y < 0 || wave.spawnPoint.y >= sizeFieldY) {
                Gdx.app.error("GameField", "validationPoints(); -- SpawnPoint bad:" + wave.spawnPoint);
                waves.removeValue(wave, true);
            }
            if (wave.exitPoint == null || wave.exitPoint.x < 0 || wave.exitPoint.x >= sizeFieldX || wave.exitPoint.y < 0 || wave.exitPoint.y >= sizeFieldY) {
                Gdx.app.error("GameField", "validationPoints(); -- ExitPoint bad:" + wave.exitPoint);
                waves.removeValue(wave, true);
            }
        }
    }
}
