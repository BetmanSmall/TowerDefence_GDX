package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.betmansmall.enums.SessionState;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.server.ServerGameScreen;
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
import java.net.ServerSocket;

public class ServerSessionThread extends Thread implements TcpSocketListener {
    private ServerGameScreen serverGameScreen;
    private SessionSettings sessionSettings;
    private ServerSocket serverSocket;
    private Array<TcpConnection> connections;
    public SessionState sessionState;

    public ServerSessionThread(ServerGameScreen serverGameScreen) {
        Logger.logFuncStart();
        this.serverGameScreen = serverGameScreen;
        this.sessionSettings = serverGameScreen.game.sessionSettings;
        this.serverSocket = null;
        this.connections = new Array<TcpConnection>();
        this.sessionState = SessionState.INITIALIZATION;
        Logger.logFuncEnd();
    }

    public void dispose() {
        Logger.logFuncStart();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(TcpConnection socket : connections) {
            socket.disconnect();
        }
        this.interrupt();
    }

    @Override
    public void run() {
        Logger.logFuncStart();
//        try ( ServerSocket serverSocket = new ServerSocket(sessionSettings.port) ) {
//            this.serverSocket = serverSocket;
        try {
            this.serverSocket = new ServerSocket(sessionSettings.port);
            this.sessionState = SessionState.WAIT_CONNECTIONS;
            while (!this.isInterrupted()) {
                try {
                    new TcpConnection(this, serverSocket.accept());
                } catch (IOException exception) {
                    Logger.logError("exception:" + exception);
                }
            }
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            serverGameScreen.game.removeTopScreen(); // TODO mb not good!
            throw new RuntimeException(exception);
//        } finally {
//            if (serverSocket != null) {
//                serverSocket.close();
//            }
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);
        NetworkPackage serverInfoData = new ServerInfoData(sessionSettings.gameSettings);
        NetworkPackage serverPlayerInfoData = new PlayerInfoData(serverGameScreen.playersManager.getLocalServer());
        tcpConnection.sendObject(new SendObject(SendObject.SendObjectEnum.SERVER_INFO_DATA, serverInfoData, serverPlayerInfoData));

        for (Player player : serverGameScreen.playersManager.getPlayers()) {
            if (player.playerID != 0) {
                tcpConnection.sendObject(new SendObject(new PlayerInfoData(player)));
            }
        }
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        for (NetworkPackage networkPackage : sendObject.networkPackages) {
            if (networkPackage instanceof PlayerInfoData) {
                PlayerInfoData playerInfoData = (PlayerInfoData) networkPackage;

                Player player = serverGameScreen.playersManager.addPlayerByServer(tcpConnection, playerInfoData);
                sessionState = SessionState.PLAYER_CONNECTED;

                tcpConnection.sendObject(new SendObject(SendObject.SendObjectEnum.UPDATE_PLAYER_INFO_DATA, new PlayerInfoData(player)));
                sendObject(new SendObject(new PlayerInfoData(player)), tcpConnection);
            } else if (networkPackage instanceof BuildTowerData) {
                BuildTowerData buildTowerData = (BuildTowerData) networkPackage;

                Player player = serverGameScreen.playersManager.getPlayer(buildTowerData.playerID);
                TemplateForTower templateForTower = player.faction.getTemplateForTower(buildTowerData.templateName);
//                TemplateForTower templateForTower = tcpConnection.player.faction.getTemplateForTower(buildTowerData.templateName);
                Tower tower = serverGameScreen.gameField.createTowerWithGoldCheck(buildTowerData.buildX, buildTowerData.buildY, templateForTower, player);

                if (tower != null) {
                    this.sendObject(new SendObject(new BuildTowerData(tower)), tcpConnection);
                }
            } else if (networkPackage instanceof RemoveTowerData) {
                RemoveTowerData removeTowerData = (RemoveTowerData) networkPackage;

                Player player = serverGameScreen.playersManager.getPlayer(removeTowerData.playerID);
                serverGameScreen.gameField.removeTowerWithGold(removeTowerData.removeX, removeTowerData.removeY, player);

                this.sendObject(new SendObject(removeTowerData), tcpConnection);
//                 or
//                this.sendObject(new SendObject(new RemoveTowerData(removeTowerData.removeX, removeTowerData.removeY, player)), tcpConnection);
            }
        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        connections.removeValue(tcpConnection, true);
        Player player = serverGameScreen.playersManager.getPlayerByConnection(tcpConnection);
        for (TcpConnection connection : connections) {
            connection.sendObject(new SendObject(SendObject.SendObjectEnum.PLAYER_DISCONNECTED, new PlayerInfoData(player)));
        }
        serverGameScreen.playersManager.removePlayer(player);
//        gameServer.playerDisconnect(tcpConnection);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

    public synchronized void sendObject(final SendObject sendObject, TcpConnection tcpConnection) {
        for (TcpConnection connection : connections) {
            if (!connection.equals(tcpConnection) || connection != tcpConnection) {
                connection.sendObject(sendObject);
            }
        }
    }

    public synchronized void sendObject(final SendObject sendObject) {
        for (TcpConnection connection : connections) {
            connection.sendObject(sendObject);
        }
    }
}
