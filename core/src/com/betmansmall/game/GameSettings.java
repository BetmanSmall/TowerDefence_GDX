package com.betmansmall.game;

import com.betmansmall.game.gameLogic.Cell;

/**
 * Created by betma on 17.11.2018.
 */

public class GameSettings {
    public GameType gameType;
    public float difficultyLevel;
    public int enemyCount;
    public int towersCount;

    public boolean isometric;
    public Cell cellSpawnHero;
    public Cell cellExitHero;

    public int maxOfMissedUnitsForComputer0;
    public int missedUnitsForComputer0;
    public int maxOfMissedUnitsForPlayer1;
    public int missedUnitsForPlayer1;

    public GameSettings(String mapPath) {
        if (mapPath.contains("randomMap")) {
            gameType = GameType.LittleGame;
            this.difficultyLevel = 1f;
            this.enemyCount = 10;
            this.towersCount = 5;
        } else if (mapPath.contains("island")) {
            gameType = GameType.LittleGame;
            this.difficultyLevel = 1f;
            this.enemyCount = 10;
            this.towersCount = 5;
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
}
