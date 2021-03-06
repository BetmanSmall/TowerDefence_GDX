package com.betmansmall.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.PlayerStatus;
import com.betmansmall.enums.SessionType;
import com.betmansmall.game.gameLogic.playerTemplates.Faction;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.data.PlayersManagerData;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.utils.logging.Logger;

public class PlayersManager {
    private SessionType sessionType;
    private FactionsManager factionsManager;
    private int connectedCount;
    private Array<Player> players;

    public PlayersManager(SessionType sessionType, FactionsManager factionsManager, UserAccount userAccount) {
        Logger.logFuncStart("sessionType:" + sessionType, "userAccount:" + userAccount);
        this.sessionType = sessionType;
        this.factionsManager = factionsManager;
        this.connectedCount = 0;
        this.players = new Array<>();

        if (sessionType == SessionType.SERVER_STANDALONE) {
            Player server = new Player("ServerStandalone", factionsManager.getServerFaction());
            server.playerStatus = PlayerStatus.LOCAL_SERVER;
            server.gold = 999999;
            this.setServer(server);
//            this.setLocalPlayer(null);
        } else if (sessionType == SessionType.SERVER_AND_CLIENT) {
            Player server = new Player("ServerByClient", factionsManager.getServerFaction());
            server.playerStatus = PlayerStatus.LOCAL_SERVER;
            server.gold = 999999;
            this.setServer(server);
            Player player = new Player(userAccount, factionsManager.getFactionByName(userAccount.factionName));
            player.playerStatus = PlayerStatus.LOCAL_SERVER;
            this.setLocalPlayer(player);
            this.connectedCount++;
        } else if (sessionType == SessionType.CLIENT_ONLY) {
            this.players.add(null);
//            this.setServer(null);
            Player player = new Player(userAccount, factionsManager.getFactionByName(userAccount.factionName));
            player.playerStatus = PlayerStatus.NOT_CONNECTED;
            this.setLocalPlayer(player);
        } else if (sessionType == SessionType.CLIENT_STANDALONE) {
            Player server = new Player("Server_ClientStandalone", factionsManager.getServerFaction());
            server.playerStatus = PlayerStatus.LOCAL_SERVER;
            server.gold = 999999;
            this.setServer(server);

            Player player = new Player(userAccount, factionsManager.getServerFaction());
            player.playerStatus = PlayerStatus.LOCAL_SERVER;
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

    public Player setServer(Player player) {
        if (player != null && player.playerID == 0 && player.type == PlayerType.SERVER) {
            if (players.size > 0) {
                players.set(0, player);
                return player;
            } else {
                players.add(player);
                return player;
            }
        }
        return null;
    }

    public Player setLocalPlayer(Player player) {
        if (player != null && player.type == PlayerType.CLIENT) { // && player.playerID > 0
            if (players.size > 1) {
                players.set(1, player);
                return player;
            } else {
                players.add(player);
                return player;
            }
        }
        return null;
    }

    private boolean addPlayer(Player player) {
        Logger.logFuncStart("connectedCount:" + connectedCount, "player:" + player);
        if (!players.contains(player, false)) { // TODO or true?
            players.add(player);
//            this.connectedCount++;
            return true;
        }
        return false;
    }

    private Player disconnectedPlayer(String accountID) {
        for (Player player : players) {
            Logger.logDebug("player:" + player);
            if (player.accountID.equals(accountID)) {
                if (player.playerStatus == PlayerStatus.DISCONNECTED) {
                    return player;
                }
            }
        }
        return null;
    }

    public Player addPlayerByServer(TcpConnection tcpConnection, PlayerInfoData playerInfoData) {
        Player player = disconnectedPlayer(playerInfoData.accountID);
        if (player == null) {
            Faction faction = factionsManager.getFactionByName(playerInfoData.factionName);
            player = new Player(tcpConnection, playerInfoData, faction);
            if (addPlayer(player)) {
                player.playerStatus = PlayerStatus.CONNECTED;
                player.playerID = ++connectedCount;
                return player;
            }
        } else { // user reconnect
//            if (addPlayer(player)) { // mb need not players.add() but players.insert()
//                return player;
//            }
            player.connection = tcpConnection;
            player.playerStatus = PlayerStatus.CONNECTED;
            return player;
        }
        return null;
    }

    public Player addPlayerByClient(PlayerInfoData playerInfoData) {
        Player player = disconnectedPlayer(playerInfoData.accountID);
        if (player == null) {
            Faction faction = factionsManager.getFactionByName(playerInfoData.factionName);
            player = new Player(playerInfoData, faction);
            if (addPlayer(player)) {
                return player;
            }
        } else {
            player.playerStatus = PlayerStatus.CONNECTED;
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

    public boolean updatePlayerInfoByAccID(PlayerInfoData playerInfoData) {
        Logger.logFuncStart("playerInfoData:" + playerInfoData);
        Logger.logDebug("players:" + players);
        for (Player player : players) {
            if (player.accountID.equals(playerInfoData.accountID)) {
                player.updateData(playerInfoData);
                return true;
            }
        }
        return false;
    }

    public void updatePlayers(PlayersManagerData playersManagerData) {
        for (PlayerInfoData playerInfoData : playersManagerData.players) {
            if (!updatePlayerInfoByAccID(playerInfoData)) {
                this.addPlayerByClient(playerInfoData);
            }
        }
    }

    public boolean removePlayerByID(Integer playerID) { // remove player on client
        for (Player player : players) {
            if (player.playerID == playerID) {
                if (playerDisconnect(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean playerDisconnect(Player player) { // disconnect player on server
        if (player != null && player.playerStatus == PlayerStatus.CONNECTED) {
            player.playerStatus = PlayerStatus.DISCONNECTED;
            return true;
        }
        return false;
    }

    public Player getPlayer(Integer playerID) {
        for (Player player : players) {
            if (player.playerID.equals(playerID)) {
                return player;
            }
        }
        Logger.logError("not found playerID:" + playerID + " | we have players:" + players);
        return getDisconnectedPlayer(playerID);
    }

    public Player getDisconnectedPlayer(Integer playerID) {
        for (Player player : players) {
            if (player.playerStatus == PlayerStatus.DISCONNECTED) {
                if (player.playerID.equals(playerID)) {
                    return player;
                }
            }
        }
        Logger.logError("not found playerID:" + playerID + " | we have players:" + players);
        return null;
    }

    public Player getLocalServer() {
        if (players.size > 0) {
            return players.get(0);
        }
        return null;
    }

    public Player getLocalPlayer() {
        if (players.size > 1) {
            return players.get(1);
        }
        return null;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayersManager[");
        sb.append("sessionType:" + sessionType);
//        sb.append(",factionsManager:" + factionsManager);
        sb.append(",connectedCount:" + connectedCount);
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
