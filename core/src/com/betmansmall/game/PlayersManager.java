package com.betmansmall.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.SessionType;
import com.betmansmall.game.gameLogic.playerTemplates.Faction;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.util.logging.Logger;

public class PlayersManager {
    private SessionType sessionType;
    private FactionsManager factionsManager;
    private Array<Player> players;
//    private Player localServer;
//    private Player localPlayer;

    public PlayersManager(SessionType sessionType, FactionsManager factionsManager, Player localPlayer) {
        Logger.logFuncStart("sessionType:" + sessionType, "localPlayer:" + localPlayer);
        this.sessionType = sessionType;
        this.factionsManager = factionsManager;
        this.players = new Array<>();

        if (sessionType == SessionType.SERVER_STANDALONE) {
            Player server = new Player(Player.Type.SERVER, 0, "ServerStandalone", factionsManager.getRandomFaction());
            server.gold = 999999;
            this.setServer(server);
//            this.setLocalPlayer(null);
        } else if (sessionType == SessionType.SERVER_AND_CLIENT) {
            Player server = new Player(Player.Type.SERVER, 0, "ServerByClient", factionsManager.getRandomFaction());
            server.gold = 999999;
            this.setServer(server);
            this.setLocalPlayer(localPlayer);
        } else if (sessionType == SessionType.CLIENT_ONLY) {
            this.setServer(null);
            this.setLocalPlayer(localPlayer);
        } else if (sessionType == SessionType.CLIENT_STANDALONE) {
            Player server = new Player(Player.Type.SERVER, 0, "Server_ClientStandalone", factionsManager.getRandomFaction());
            server.gold = 999999;
            this.setServer(server);

            Player player = new Player(Player.Type.CLIENT, 1, "ClientStandalone", factionsManager.getRandomFaction());
            player.gold = 9999;
            this.setLocalPlayer(player);
        }
    }

    public void dispose() {
        this.players.clear();
    }

    public Array<Player> getPlayers() {
        return players;
    }

    public void setServer(Player player) {
//        if (player.playerID == 0 && player.type == Player.Type.SERVER) {
            players.insert(0, player);
//        }
    }

    public void setLocalPlayer(Player player) {
//        if (player.type == Player.Type.CLIENT) {
            players.insert(1, player);
//        }
    }

    public boolean addPlayer(Player player) {
        if (!players.contains(player, false)) { // TODO or true?
            players.add(player);
            return true;
        }
        return false;
    }

    public Player addPlayerByServer(TcpConnection tcpConnection, PlayerInfoData playerInfoData) {
        Faction faction = factionsManager.getFactionByName(playerInfoData.factionName);
        Player player = new Player(tcpConnection, playerInfoData, faction);
        if (addPlayer(player)) {
            player.playerID = players.size-1;
            return player;
        }
        return null;
    }

    public Player addPlayerByClient(TcpConnection tcpConnection, PlayerInfoData playerInfoData) {
        Faction faction = factionsManager.getFactionByName(playerInfoData.factionName);
        Player player = new Player(tcpConnection, playerInfoData, faction);
        if (addPlayer(player)) {
            return player;
        }
        return null;
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

    public Player getPlayer(Integer playerID) {
        for (Player player : players) {
            if (player.playerID.equals(playerID)) {
                return player;
            }
        }
        return null;
    }

    public Player getLocalServer() {
        return players.get(0);
    }

    public Player getLocalPlayer() {
        return players.get(1);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayersManager[");
//        sb.append("localPlayer:" + localPlayer);
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
