package com.betmansmall.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

public class PlayersManager {
    public Array<Player> players;
    public Player localPlayer;

    public PlayersManager() {
        this.players = new Array<>();
        this.localPlayer = new Player();

        Player computer = new Player();
        computer.playerID = 0;
        computer.name = "Computer0";
        players.add(computer);
    }

    public void dispose() {
        this.players.clear();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayersManager[");
        sb.append("players.size:" + players.size);
        if (full) {
            for (Player player : players) {
                sb.append("," + player);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
