package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.WaveAlgorithm;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class WaveManager {
    public Array<GridPoint2> spawnPoints;
    public Array<GridPoint2> exitPoints;
    public Array<Wave> waves;

    public float intervalForSpawnCreeps = 2f;
    public float elapsedTimeForSpawn = 0f;

    WaveManager(int countTemplateForUnits) {
        this.spawnPoints = new Array<GridPoint2>();
        this.exitPoints = new Array<GridPoint2>();
        this.waves = new Array<Wave>();

        this.waves.add(new Wave(crateRandomIntArray(countTemplateForUnits, 25)));
    }

    public Array<Integer> crateRandomIntArray(int maxGeneratedNumber, int length) {
        Array<Integer> arr = new Array<Integer>(length);
        arr.add(maxGeneratedNumber-1);
        for(int k = 0; k < length; k++) {
            int num = MathUtils.random(0, maxGeneratedNumber-1);
            arr.add(num);
        }
        return arr;
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
