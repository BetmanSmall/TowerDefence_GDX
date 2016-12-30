package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.bcel.internal.generic.FLOAD;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class WaveManager {
    public Array<Wave> waves;

    public float intervalForSpawnCreeps = 0f;
    public float elapsedTimeForSpawn = 0f;

    WaveManager() {
        this.waves = new Array<Wave>();
    }

    public void addWave(Wave wave) {
        this.waves.add(wave);
    }

    /**
     *
     * @param delta
     * @return
     */
    public String getNextNameTemplateForUnitForSpawnCreep(float delta) {
        elapsedTimeForSpawn += delta;
        if (elapsedTimeForSpawn >= intervalForSpawnCreeps) {
            elapsedTimeForSpawn = 0f;
            if (waves.size != 0) {
                Wave wave = waves.first();
                String action = wave.actions.pollFirst();
                if(action == null) {
                    waves.removeIndex(0);
                    if (waves.size != 0) {
                        return wave.actions.pollFirst();
                    } else {
                        return null;
                    }
                } else if(action.contains("interval") || action.contains("delay") ) {
                    intervalForSpawnCreeps = Float.parseFloat(action.substring(action.indexOf("=")+1, action.length())) + wave.spawnInterval;
                    return action;
                } else {
                    intervalForSpawnCreeps = 0f;
                    return action;
                }
            } else {
                return null;
            }
        }
        return "delay:" + elapsedTimeForSpawn + " intervalForSpawnCreeps:" + intervalForSpawnCreeps;
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

    public int getNumberOfCreeps() { // not creeps | actions
        int creeps = 0;
        for (Wave wave : waves) {
            creeps += wave.actions.size();
        }
        return creeps;
    }
}
