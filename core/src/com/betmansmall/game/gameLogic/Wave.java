package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class Wave {
    public ArrayDeque<String> actions;
    public GridPoint2 spawnPoint;
    public GridPoint2 exitPoint;
    public float spawnInterval;

    public Wave(GridPoint2 spawnPoint, GridPoint2 exitPoint) {
        this.actions = new ArrayDeque<String>();
        this.spawnPoint = spawnPoint;
        this.exitPoint = exitPoint;
    }

    public void addAction(String action) {
        this.actions.add(action);
    }
}
