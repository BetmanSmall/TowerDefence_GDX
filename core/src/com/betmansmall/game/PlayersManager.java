package com.betmansmall.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.util.logging.Logger;

public class PlayersManager {
    private Array<Player> players;
    public Player localComputer;
    public Player localPlayer;

    public PlayersManager() {
        this.players = new Array<>();

        Player computer = new Player(null);
        computer.playerID = 0;
        computer.name = "Computer0";
        computer.gold = 999999;
        this.players.add(computer);

        this.localComputer = computer;
        this.localPlayer = new Player(null);
//        this.localPlayer.playerID = 1;
//        this.players.add(localPlayer); when receive server add localPlayer to players;
    }

    public void dispose() {
        this.players.clear();
    }

    public Array<Player> getPlayers() {
        return players;
    }

    public boolean addPlayer(Player player) {
        if (!players.contains(player, false)) { // TODO or true?
            players.add(player);
            return true;
        }
        return false;
    }

    public Player getPlayerByConnection(TcpConnection connection) {
        Logger.logFuncStart("connection:" + connection, "players:" + players);
        for (Player player : players) {
            if (player.connection != null && player.connection.equals(connection)) {
                return player;
            }
        }
        return null;
    }

    public boolean removePlayerByID(Integer playerID) {
        for (Player player : players) {
            if (player.playerID == playerID && players.removeValue(player, true)) {
                return true;
            }
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        return players.removeValue(player, true);
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
