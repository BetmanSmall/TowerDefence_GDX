package com.betmansmall.game;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.playerTemplates.Faction;
import com.betmansmall.server.networking.TcpConnection;

public class Player {
    public TcpConnection connection;
    public int playerID;
    public String name;
    public Faction faction;
    public int gold = 1000;

    public Cell cellSpawnHero;
    public Cell cellExitHero;

    public int maxOfMissedUnits;
    public int missedUnits;

    public Player(TcpConnection connection) {
        this.connection = connection;
        if (connection != null) {
            this.connection.player = this;
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Player[");
        sb.append("connection:" + connection);
        sb.append(",playerID:" + playerID);
        sb.append(",name:" + name);
        sb.append(",faction:" + faction);
        sb.append(",gold:" + gold);
        if (full) {
            sb.append(",cellSpawnHero:" + cellSpawnHero);
            sb.append(",cellExitHero:" + cellExitHero);

            sb.append(",maxOfMissedUnits:" + maxOfMissedUnits);
            sb.append(",missedUnits:" + missedUnits);
        }
        sb.append("]");
        return sb.toString();
    }
}
