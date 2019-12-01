package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.GameField;

public class GameFieldVariablesData implements NetworkPackage {
    public float timeOfGame;
    public float gameSpeed;
    public boolean gamePaused;
    public boolean unitsSpawn;

    public GameFieldVariablesData(GameField gameField) {
        this.timeOfGame = gameField.timeOfGame;
        this.gameSpeed = gameField.gameSpeed;
        this.gamePaused = gameField.gamePaused;
        this.unitsSpawn = gameField.unitsSpawn;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameFieldVariablesData[");
        sb.append("timeOfGame:" + timeOfGame);
        sb.append(",gameSpeed:" + gameSpeed);
        sb.append(",gamePaused:" + gamePaused);
        sb.append(",unitsSpawn:" + unitsSpawn);
        sb.append("]");
        return sb.toString();
    }
}
