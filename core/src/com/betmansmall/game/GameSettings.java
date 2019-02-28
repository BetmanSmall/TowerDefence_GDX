package com.betmansmall.game;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Cell;

/**
 * Created by betma on 17.11.2018.
 */
public class GameSettings {
    public String mapPath;
    public GameType gameType;
    public float difficultyLevel;
    public int enemyCount;
    public int towersCount;
    public boolean panLeftMouseButton;
    public boolean panMidMouseButton;
    public boolean panRightMouseButton;

    public boolean isometric;
    public Cell cellSpawnHero;
    public Cell cellExitHero;

    public int maxOfMissedUnitsForComputer0;
    public int missedUnitsForComputer0;
    public int maxOfMissedUnitsForPlayer1;
    public int missedUnitsForPlayer1;

    public GameSettings() {
//        this.gameType = GameType.LittleGame;
        this.difficultyLevel = 1f;
        this.enemyCount = 20;
        this.towersCount = 10;

        panLeftMouseButton = true;
        panMidMouseButton = false;
        panRightMouseButton = false;

        isometric = false;
        cellSpawnHero = null;
        cellExitHero = null;
    }

    public void setGameTypeByMap(String mapPath) {
        this.mapPath = mapPath;
        if (mapPath.contains("arena0")) {
            gameType = GameType.TowerDefence;
        } else if (mapPath.contains("randomMap")) {
            gameType = GameType.LittleGame;
        } else if (mapPath.contains("island")) {
            gameType = GameType.LittleGame;
        } else {
            gameType = GameType.TowerDefence;
        }
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameSettings[");
        sb.append("gameType:" + gameType);
        sb.append(",difficultyLevel:" + difficultyLevel);
        sb.append(",enemyCount:" + enemyCount);
        sb.append(",towersCount:" + towersCount);
        if (full) {
            sb.append(",isometric:" + isometric);
            sb.append(",cellExitHero:" + cellExitHero);
            sb.append(",cellSpawnHero:" + cellSpawnHero);

            sb.append(",maxOfMissedUnitsForComputer0:" + maxOfMissedUnitsForComputer0);
            sb.append(",missedUnitsForComputer0:" + missedUnitsForComputer0);
            sb.append(",maxOfMissedUnitsForPlayer1:" + maxOfMissedUnitsForPlayer1);
            sb.append(",missedUnitsForPlayer1:" + missedUnitsForPlayer1);
        }
        sb.append("]");
        return sb.toString();
    }
}
