package com.betmansmall.game;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Cell;

/**
 * Created by betma on 17.11.2018.
 */
public class GameSettings {
    public GameType gameType;
    public float difficultyLevel;
    public int enemyCount;
    public int towersCount;

    public boolean isometric = false;
    public Cell cellSpawnHero;
    public Cell cellExitHero;

    public int maxOfMissedUnitsForComputer0;
    public int missedUnitsForComputer0;
    public int maxOfMissedUnitsForPlayer1;
    public int missedUnitsForPlayer1;

    public GameSettings(String mapPath) {
        if (mapPath.contains("randomMap")) {
            gameType = GameType.LittleGame;
            this.difficultyLevel = 0f;
            this.enemyCount = 10;
            this.towersCount = 0;
        } else if (mapPath.contains("island")) {
            gameType = GameType.LittleGame;
            this.difficultyLevel = 1f;
            this.enemyCount = 10;
            this.towersCount = 0;
        } else {
            gameType = GameType.TowerDefence;
            this.difficultyLevel = 1f;
            this.enemyCount = 0;
            this.towersCount = 0;
        }
    }

    public GameSettings(GameType gameType) {
        this.gameType = gameType;
    }

    public GameSettings(GameType gameType, float difficultyLevel) {
        this.gameType = gameType;
        this.difficultyLevel = difficultyLevel;
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
