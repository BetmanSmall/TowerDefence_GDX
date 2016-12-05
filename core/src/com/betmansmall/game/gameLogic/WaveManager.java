package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.WaveAlgorithm;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class WaveManager {
    public Array<GridPoint2> spawnPoints;
    public Array<GridPoint2> exitPoints;
    public Array<Wave> waves;

    public float intervalForSpawnCreeps = 1f;
    public float elapsedTimeForSpawn = 0f;

    WaveManager() {
        this.spawnPoints = new Array<GridPoint2>();
        this.exitPoints = new Array<GridPoint2>();
        this.waves = new Array<Wave>();

        this.waves.add(new Wave(new Integer[]{0,1,2,2,2,2,2,2,2,1,1,1,1,1,1,0,0,2,0,1,1,1,1,0,0,0,1,1,1,1,1,0,0,0,0,1,1,1,0}));
    }

    public Integer getNextIndexTemplateForUnitForSpawnCreep(float delta) {
        elapsedTimeForSpawn += delta;
        if(elapsedTimeForSpawn >= intervalForSpawnCreeps) {
            elapsedTimeForSpawn = 0f;
            return waves.first().units.pollFirst();
        }
        return null;
    }

    public int getNumberOfCreeps() {
        return waves.first().units.size();
    }
}
