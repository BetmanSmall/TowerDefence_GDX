package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.game.PlayerType;
import com.betmansmall.util.logging.Logger;

public class PlayerInfoData implements NetworkPackage {
    public PlayerType type;
    public Integer playerID;
    public String name;
    public String factionName;

    public PlayerInfoData(Player player) {
        Logger.logFuncStart("player:" + player);
        this.type = player.type;
        this.playerID = player.playerID;
        this.name = player.name;
        this.factionName = player.faction.getName();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayerInfoData[");
        sb.append("type:" + type);
        sb.append(",playerID:" + playerID);
        sb.append(",name:" + name);
        if (full) {
            sb.append(",factionName:" + factionName);
        }
        sb.append("]");
        return sb.toString();
    }
}
