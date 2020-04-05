package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.game.PlayersManager;

import java.util.ArrayList;

public class PlayersManagerData implements NetworkPackage {
    public ArrayList<PlayerInfoData> players;

    public PlayersManagerData(PlayersManager playersManager) {
        this.players = new ArrayList<>();
        for (Player player : playersManager.getPlayers()) {
            PlayerInfoData playerInfoData = new PlayerInfoData(player);
            this.players.add(playerInfoData);
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayersManagerData[");
        sb.append("players.size():" + players.size());
        if (full) {
            for (PlayerInfoData playerInfoData : players) {
                sb.append("," + playerInfoData);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
