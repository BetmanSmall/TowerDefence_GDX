package com.betmansmall.game;

import com.betmansmall.enums.SessionState;
import com.betmansmall.game.gameLogic.playerTemplates.Faction;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.client.ClientGameScreen;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.server.data.NetworkPackage;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.data.RemoveTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.ServerInfoData;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;

public class ClientSessionThread extends Thread implements TcpSocketListener {
    private ClientGameScreen clientGameScreen;
    private SessionSettings sessionSettings;
    private TcpConnection connection;
    public SessionState sessionState;

    public ClientSessionThread(ClientGameScreen clientGameScreen) {
        Logger.logFuncStart();
        this.clientGameScreen = clientGameScreen;
        this.sessionSettings = clientGameScreen.game.sessionSettings;
        this.connection = null;
        this.sessionState = SessionState.INITIALIZATION;
    }

    public void dispose() {
        Logger.logFuncStart();
        connection.disconnect();
        this.interrupt();
    }

    @Override
    public void run() {
        Logger.logInfo("Try connect to:" + sessionSettings.host + ":" + sessionSettings.port);
        try {
            new TcpConnection(this, sessionSettings.host, sessionSettings.port);
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            clientGameScreen.game.removeTopScreen(); // TODO mb not good!
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        this.connection = tcpConnection;
        sessionState = SessionState.CONNECTED;
        tcpConnection.sendObject(new SendObject(new PlayerInfoData(clientGameScreen.playersManager.getLocalPlayer())));
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        switch (sendObject.sendObjectEnum) {
            case SERVER_INFO_DATA: {
                for (NetworkPackage networkPackage : sendObject.networkPackages) {
                    if (networkPackage instanceof ServerInfoData) {
                        ServerInfoData serverInfoData = (ServerInfoData) networkPackage;
                        clientGameScreen.game.sessionSettings.gameSettings.mapPath = serverInfoData.mapPath;
                        clientGameScreen.game.sessionSettings.gameSettings.gameType = serverInfoData.gameType;
                        sessionState = SessionState.RECEIVED_SERVER_INFO_DATA;
                    } else if (networkPackage instanceof PlayerInfoData) {
                        PlayerInfoData playerInfoData = (PlayerInfoData) networkPackage;

                        Faction serverFaction = clientGameScreen.game.factionsManager.getServerFaction();
                        clientGameScreen.playersManager.setServer(new Player(playerInfoData, serverFaction));
                    }
                }
            }
        }
        for (NetworkPackage networkPackage : sendObject.networkPackages) {
            if (networkPackage instanceof PlayerInfoData) {
                PlayerInfoData playerInfoData = (PlayerInfoData) networkPackage;

                if (sendObject.sendObjectEnum == SendObject.SendObjectEnum.PLAYER_DISCONNECTED) {
                    clientGameScreen.playersManager.removePlayerByID(playerInfoData.playerID);
                } else if (sendObject.sendObjectEnum == SendObject.SendObjectEnum.UPDATE_PLAYER_INFO_DATA) {
                    clientGameScreen.playersManager.updatePlayerInfo(playerInfoData);
                } else if (sendObject.sendObjectEnum == SendObject.SendObjectEnum.PLAYER_INFO_DATA) {
                    clientGameScreen.playersManager.addPlayerByClient(playerInfoData);
//                    sessionState = SessionState.NEW_PLAYER_CONNECTED;
                }
            } else if (networkPackage instanceof BuildTowerData) {
                BuildTowerData buildTowerData = (BuildTowerData) networkPackage;

                Player player = clientGameScreen.playersManager.getPlayer(buildTowerData.playerID);
                TemplateForTower templateForTower = player.faction.getTemplateForTower(buildTowerData.templateName);
                clientGameScreen.gameField.createTowerWithGoldCheck(buildTowerData.buildX, buildTowerData.buildY, templateForTower, player);
            } else if (networkPackage instanceof RemoveTowerData) {
                RemoveTowerData removeTowerData = (RemoveTowerData) networkPackage;

                Player player = clientGameScreen.playersManager.getPlayer(removeTowerData.playerID);
                clientGameScreen.gameField.removeTowerWithGold(removeTowerData.removeX, removeTowerData.removeY, player);
            }
        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        clientGameScreen.game.removeTopScreen(); // TODO mb not good!
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

    public synchronized void sendObject(final SendObject sendObject) {
        this.connection.sendObject(sendObject);
    }
}
