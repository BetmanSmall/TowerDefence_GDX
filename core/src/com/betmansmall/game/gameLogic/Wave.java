package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class Wave {
    public ArrayDeque<String> units;
    public GridPoint2 spawnPoint;
    public GridPoint2 exitPoint;

    public Wave(GridPoint2 spawnPoint, GridPoint2 exitPoint) {
        this.units = new ArrayDeque<String>();
        this.spawnPoint = spawnPoint;
        this.exitPoint = exitPoint;
    }

    public void addTemplateForUnit(String unit) {
        this.units.add(unit);
    }
}
