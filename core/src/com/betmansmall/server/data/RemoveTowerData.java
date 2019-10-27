package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;

public class RemoveTowerData implements NetworkPackage {
    public int removeX;
    public int removeY;
    public int playerID;

    public RemoveTowerData(int removeX, int removeY, Player player) {
        this.removeX = removeX;
        this.removeY = removeY;
        this.playerID = player.playerID;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("RemoveTowerData[");
        sb.append("removeX:" + removeX);
        sb.append(",removeY:" + removeY);
        sb.append(",playerID:" + playerID);
        sb.append("]");
        return sb.toString();
    }
}
