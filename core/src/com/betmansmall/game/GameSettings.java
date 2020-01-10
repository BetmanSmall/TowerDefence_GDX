package com.betmansmall.game;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.GameType;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.server.data.GameSettingsData;

/**
 * Created by betma on 17.11.2018.
 */
public class GameSettings {
    public String mapPath;
    public GameType gameType;
    public float difficultyLevel;
    public int enemyCount;
    public int towersCount;
    public int landscapePercent;
    public boolean topBottomLeftRightSelector; // Top - false, Bottom - true, Left - false, Right - true;
    public boolean verticalSelector;
    public boolean smoothFlingSelector;
    public boolean panLeftMouseButton;
    public boolean panMidMouseButton;
    public boolean panRightMouseButton;

    public Cell cellSpawnHero;
    public Cell cellExitHero;

    public GameSettings() {
//        this.gameType = GameType.LittleGame;
        this.difficultyLevel = 1f;
        this.enemyCount = 20;
        this.towersCount = 10;
        this.landscapePercent = 30;

        this.topBottomLeftRightSelector = true;
        this.verticalSelector = true;
        this.smoothFlingSelector = true;
        this.panLeftMouseButton = true;
        this.panMidMouseButton = false;
        this.panRightMouseButton = false;

        this.cellSpawnHero = null;
        this.cellExitHero = null;
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

    public void updateGameSettings(GameSettingsData gameSettingsData) {
        this.mapPath = gameSettingsData.mapPath;
        this.gameType = gameSettingsData.gameType;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameSettings[");
        sb.append("mapPath:" + mapPath);
        sb.append(",gameType:" + gameType);
        sb.append(",difficultyLevel:" + difficultyLevel);
        sb.append(",enemyCount:" + enemyCount);
        sb.append(",towersCount:" + towersCount);
        if (full) {
            sb.append(",landscapePercent:" + landscapePercent);
            sb.append(",topBottomLeftRightSelector:" + topBottomLeftRightSelector);
            sb.append(",verticalSelector:" + verticalSelector);
            sb.append(",smoothFlingSelector:" + smoothFlingSelector);
            sb.append(",panLeftMouseButton:" + panLeftMouseButton);
            sb.append(",panMidMouseButton:" + panMidMouseButton);
            sb.append(",panRightMouseButton:" + panRightMouseButton);

            sb.append(",cellSpawnHero:" + cellSpawnHero);
            sb.append(",cellExitHero:" + cellExitHero);
        }
        sb.append("]");
        return sb.toString();
    }
}
