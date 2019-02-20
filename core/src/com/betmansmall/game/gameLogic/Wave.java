package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder.Node;

import java.util.ArrayDeque;

/**
 * Created by betmansmall on 29.03.2016.
 */
public class Wave {
    public ArrayDeque<String> actions;
    public GridPoint2 spawnPoint;
    public GridPoint2 exitPoint;
    public ArrayDeque<Node> route;
    private float intervalForSpawn;
    private float elapsedTime;

    public Wave(GridPoint2 spawnPoint, GridPoint2 exitPoint, float startToMove) {
        this.actions = new ArrayDeque<String>();
        this.spawnPoint = spawnPoint;
        this.exitPoint = exitPoint;
        this.intervalForSpawn = startToMove;
        this.elapsedTime = 0f;
    }

    public Wave(GridPoint2 spawnPoint, GridPoint2 exitPoint) {
        this.actions = new ArrayDeque<String>();
        this.spawnPoint = spawnPoint;
        this.exitPoint = exitPoint;
        this.intervalForSpawn = 0f;
        this.elapsedTime = 0f;
    }

    public String getTemplateNameForSpawn(float delta) {
        elapsedTime += delta;
        if (elapsedTime >= intervalForSpawn) {
            elapsedTime = 0f;
            String action = actions.pollFirst();
            if (action == null) {
                return null;
            } else if (action.contains("delay")) {
                intervalForSpawn = Float.parseFloat(action.substring(action.indexOf("=") + 1, action.length())); // GOVNE GODE parseFloat1
//                Gdx.app.log("Wave::getNextNameTemplateForUnitForSpawnUnit()", "-- Delay after wave:" + intervalForSpawn + " sec.");
                return "wait=" + intervalForSpawn;
            } else if (action.contains("interval")) {
                intervalForSpawn = Float.parseFloat(action.substring(action.indexOf("=") + 1, action.length())); // GOVNE GODE parseFloat2
//                Gdx.app.log("Wave::getNextNameTemplateForUnitForSpawnUnit()", "-- Next unit spawn after:" + intervalForSpawn + " sec.");
                return "wait=" + intervalForSpawn;
            } else { // string contain templateName.
                intervalForSpawn = 0f;
                return action;
            }
        }
        return "wait=" + (intervalForSpawn-elapsedTime);
    }

    public void addAction(String action) {
        this.actions.add(action);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wave[");
        sb.append("spawnPoint:" + spawnPoint);
        sb.append(",exitPoint:" + exitPoint);
        sb.append(",elapsedTime:" + elapsedTime);
        sb.append(",intervalForSpawn:" + intervalForSpawn);
        sb.append(",actions:" + actions);
        sb.append(",route:" + ( (route != null)?route.size():"null") );
        sb.append("]");
        return sb.toString();
    }
}
