package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.util.logging.Logger;

public class PlayerInfoData implements NetworkPackage {
    public Player.Type type;
    public String accountID;
    public Integer playerID;
    public String name;
    public String factionName;
    public Integer gold;

    public PlayerInfoData(Player player) {
        Logger.logFuncStart("player:" + player);
        this.type = player.type;
        this.accountID = player.accountID;
        this.playerID = player.playerID;
        this.name = player.name;
        this.factionName = player.faction.getName();
        this.gold = player.gold;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayerInfoData[");
        sb.append("type:" + type);
        sb.append(",accountID:" + accountID);
        sb.append(",playerID:" + playerID);
        sb.append(",name:" + name);
        if (full) {
            sb.append(",factionName:" + factionName);
            sb.append(",gold:" + gold);
        }
        sb.append("]");
        return sb.toString();
    }
}
