package com.betmansmall.game;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.playerTemplates.Faction;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.networking.TcpConnection;

public class Player {
    public enum Type {
        SERVER,
        CLIENT,
        SPECTATOR
    }
    public TcpConnection connection; // only for ServerSessionThread class
    public Player.Type type;
    public String accountID;
    public Integer playerID;
    public String name;
    public Faction faction;
    public int gold = 10000;

    public Cell cellSpawnHero;
    public Cell cellExitHero;

    public int maxOfMissedUnits = 100;
    public int missedUnits = 0;

    public Player(String serverName, Faction faction) {
        this.connection = null;
        this.type = Type.SERVER;
        this.accountID = "accID_SERVER";
        this.playerID = 0;
        this.name = serverName;
        this.faction = faction;
    }

    public Player(Player.Type type, UserAccount userAccount, Faction faction) {
        this.connection = null;
        this.type = type;
        this.accountID = userAccount.accountID;
        if (type == Type.SERVER) {
            this.playerID = 0;
        } else if (type == Type.CLIENT) {
            this.playerID = 1;
        }
        this.name = userAccount.loginName;
        this.faction = faction;
    }

    public Player(PlayerInfoData playerInfoData, Faction faction) {
        init(null, playerInfoData, faction);
    }

    public Player(TcpConnection tcpConnection, PlayerInfoData playerInfoData, Faction faction) { // only for ServerSessionThread class
        init(tcpConnection, playerInfoData, faction);
//        if (connection != null) {
//            this.connection.player = this;
//        }
    }

    public void init(TcpConnection tcpConnection, PlayerInfoData playerInfoData, Faction faction) {
        this.connection = tcpConnection;
        this.type = playerInfoData.type;
        this.accountID = playerInfoData.accountID;
        this.playerID = playerInfoData.playerID;
        this.name = playerInfoData.name;
        this.faction = faction;
        this.gold = playerInfoData.gold;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Player[");
        sb.append("type:" + type);
        sb.append(",accountID:" + accountID);
        sb.append(",playerID:" + playerID);
        sb.append(",name:" + name);
        sb.append(",faction:" + faction);
        sb.append(",gold:" + gold);
        if (full) {
//            sb.append(",connection:" + connection);
            sb.append(",cellSpawnHero:" + cellSpawnHero);
            sb.append(",cellExitHero:" + cellExitHero);

            sb.append(",maxOfMissedUnits:" + maxOfMissedUnits);
            sb.append(",missedUnits:" + missedUnits);
        }
        sb.append("]");
        return sb.toString();
    }
}
