package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class WaveManager {
    public Array<Wave> waves;

    public float intervalForSpawnCreeps = 1f;
    public float elapsedTimeForSpawn = 0f;

    WaveManager() {
        this.waves = new Array<Wave>();
    }

    public void addWave(Wave wave) {
        this.waves.add(wave);
    }

    public String getNextNameTemplateForUnitForSpawnCreep(float delta) {
        elapsedTimeForSpawn += delta;
        if (elapsedTimeForSpawn >= intervalForSpawnCreeps) {
            elapsedTimeForSpawn = 0f;
            if (waves.size != 0) {
                String templateName = waves.first().units.pollFirst();
                if (templateName == null) {
                    waves.removeIndex(0);
                    if (waves.size != 0) {
                        return waves.first().units.pollFirst();
                    } else {
                        return null;
                    }
                }
                intervalForSpawnCreeps = waves.first().delay.pollFirst();
                intervalForSpawnCreeps += waves.first().spawnInterval;
                return templateName;
            } else {
                return null;
            }
        }
        return null;
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
        }
        return null;
    }

    public int getNumberOfCreeps() {
        int creeps = 0;
        for (Wave wave : waves) {
            creeps += wave.units.size();
        }
        return creeps;
    }
}
