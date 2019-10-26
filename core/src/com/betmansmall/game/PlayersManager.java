package com.betmansmall.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.SessionType;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.util.logging.Logger;

public class PlayersManager {
    private Array<Player> players;
    public Player localServer;
    public Player localPlayer;

    public PlayersManager(SessionType sessionType, Player localPlayer) {
        Logger.logFuncStart("sessionType:" + sessionType, "localPlayer:" + localPlayer);
        this.players = new Array<>();

        if (sessionType == SessionType.SERVER_STANDALONE) {
            this.localServer = new Player(null, Player.Type.SERVER, 0);
            this.localServer.name = "ServerStandalone";
            this.localServer.gold = 999999;
            this.players.add(localServer);

            this.localPlayer = null;
        } else if (sessionType == SessionType.SERVER_AND_CLIENT) {
            this.localServer = new Player(null, Player.Type.SERVER, 0);
            this.localServer.name = "ServerByClient";
            this.localServer.gold = 999999;
            this.players.add(localServer);

            this.localPlayer = localPlayer;
            this.players.add(localPlayer);
        } else if (sessionType == SessionType.CLIENT_ONLY) {
            this.localServer = null;
            this.localPlayer = localPlayer;
        } else if (sessionType == SessionType.CLIENT_STANDALONE) {
            this.localServer = new Player(null, Player.Type.SERVER, 0);
            this.localServer.name = "Server_ClientStandalone";
            this.localServer.gold = 999999;
            this.players.add(localServer);

            this.localPlayer = new Player(null, Player.Type.CLIENT, 1);
            this.localPlayer.name = "ClientStandalone";
            this.localPlayer.gold = 9999;
//            this.localPlayer.faction;
            this.players.add(this.localPlayer);
        }
    }

    public void dispose() {
        this.players.clear();
    }

    public Array<Player> getPlayers() {
        return players;
    }

    public boolean addPlayer(Player player) {
        if (player.playerID == 0 && player.type == Player.Type.SERVER) {
            localServer = player;
        }
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
        sb.append("localPlayer:" + localPlayer);
        sb.append(",players.size:" + players.size);
        if (full) {
            for (Player player : players) {
                sb.append("," + player);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
