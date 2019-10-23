package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;

public class PlayerInfoData implements NetworkPackage {
    public String name;
    public String factionName;

    public PlayerInfoData(Player player) {
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
        sb.append("name:" + name);
        if (full) {
            sb.append(",factionName:" + factionName);
        }
        sb.append("]");
        return sb.toString();
    }
}
