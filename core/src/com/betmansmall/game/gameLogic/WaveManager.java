package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.WaveAlgorithm;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class WaveManager {
    public Array<Wave> waves;

    public float intervalForSpawnCreeps = 1f;
    public float elapsedTimeForSpawn = 0f;

    WaveManager() {
        this.waves = new Array<Wave>();

        Wave wave = new Wave(new GridPoint2(15, 15), new GridPoint2(45, 45));
        wave.addTemplateForUnit("unit1_grunt");
        wave.addTemplateForUnit("unit2_troll_axethrower");
        wave.addTemplateForUnit("unit3_footman");
        waves.add(wave);

        Wave wave2 = new Wave(new GridPoint2(13, 15), new GridPoint2(55, 55));
        wave2.addTemplateForUnit("unit3_footman");
        wave2.addTemplateForUnit("unit2_troll_axethrower");
        wave2.addTemplateForUnit("unit1_grunt");
        waves.add(wave2);
    }

    public void addWave(Wave wave) {
        this.waves.add(wave);
    }

    public String getNextNameTemplateForUnitForSpawnCreep(float delta) {
        elapsedTimeForSpawn += delta;
        if(elapsedTimeForSpawn >= intervalForSpawnCreeps) {
            elapsedTimeForSpawn = 0f;
            if(waves.size != 0) {
                String templateName = waves.first().units.pollFirst();
                if (templateName == null) {
                    waves.removeIndex(0);
                    if (waves.size != 0) {
                        return waves.first().units.pollFirst();
                    } else {
                        return null;
                    }
                }
                return templateName;
            } else {
                return null;
            }
        }
        return null;
    }

    public GridPoint2 getSpawnPoint() {
        if(waves.size != 0) {
            return waves.first().spawnPoint;
        }
        return null;
    }

    public GridPoint2 getExitPoint() {
        if(waves.size != 0) {
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
